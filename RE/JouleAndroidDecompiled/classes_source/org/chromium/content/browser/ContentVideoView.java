package org.chromium.content.browser;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.content.C0317R;

@JNINamespace("content")
public class ContentVideoView extends FrameLayout implements Callback {
    private static final int MEDIA_BUFFERING_UPDATE = 3;
    private static final int MEDIA_ERROR = 100;
    public static final int MEDIA_ERROR_INVALID_CODE = 3;
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 2;
    private static final int MEDIA_INFO = 200;
    private static final int MEDIA_NOP = 0;
    private static final int MEDIA_PLAYBACK_COMPLETE = 2;
    private static final int MEDIA_PREPARED = 1;
    private static final int MEDIA_SEEK_COMPLETE = 4;
    private static final int MEDIA_SET_VIDEO_SIZE = 5;
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PAUSED = 2;
    private static final int STATE_PLAYBACK_COMPLETED = 3;
    private static final int STATE_PLAYING = 1;
    private static final String TAG = "cr.ContentVideoView";
    private final ContentVideoViewClient mClient;
    private final ContentViewCore mContentViewCore;
    private int mCurrentState;
    private String mErrorButton;
    private String mErrorTitle;
    private final Runnable mExitFullscreenRunnable;
    private boolean mInitialOrientation;
    private long mNativeContentVideoView;
    private long mOrientationChangedTime;
    private String mPlaybackErrorText;
    private long mPlaybackStartTime;
    private boolean mPossibleAccidentalChange;
    private View mProgressView;
    private SurfaceHolder mSurfaceHolder;
    private boolean mUmaRecorded;
    private String mUnknownErrorText;
    private int mVideoHeight;
    private String mVideoLoadingText;
    private VideoSurfaceView mVideoSurfaceView;
    private int mVideoWidth;

    /* renamed from: org.chromium.content.browser.ContentVideoView.1 */
    class C03321 implements Runnable {
        C03321() {
        }

        public void run() {
            ContentVideoView.this.exitFullscreen(true);
        }
    }

    /* renamed from: org.chromium.content.browser.ContentVideoView.2 */
    class C03332 implements OnClickListener {
        C03332() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            ContentVideoView.this.onCompletion();
        }
    }

    private static class ProgressView extends LinearLayout {
        private final ProgressBar mProgressBar;
        private final TextView mTextView;

        public ProgressView(Context context, String videoLoadingText) {
            super(context);
            setOrientation(ContentVideoView.STATE_PLAYING);
            setLayoutParams(new LayoutParams(-2, -2));
            this.mProgressBar = new ProgressBar(context, null, 16842874);
            this.mTextView = new TextView(context);
            this.mTextView.setText(videoLoadingText);
            addView(this.mProgressBar);
            addView(this.mTextView);
        }
    }

    private class VideoSurfaceView extends SurfaceView {
        public VideoSurfaceView(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = ContentVideoView.STATE_PLAYING;
            int height = ContentVideoView.STATE_PLAYING;
            if (ContentVideoView.this.mVideoWidth > 0 && ContentVideoView.this.mVideoHeight > 0) {
                width = getDefaultSize(ContentVideoView.this.mVideoWidth, widthMeasureSpec);
                height = getDefaultSize(ContentVideoView.this.mVideoHeight, heightMeasureSpec);
                if (ContentVideoView.this.mVideoWidth * height > ContentVideoView.this.mVideoHeight * width) {
                    height = (ContentVideoView.this.mVideoHeight * width) / ContentVideoView.this.mVideoWidth;
                } else if (ContentVideoView.this.mVideoWidth * height < ContentVideoView.this.mVideoHeight * width) {
                    width = (ContentVideoView.this.mVideoWidth * height) / ContentVideoView.this.mVideoHeight;
                }
            }
            if (ContentVideoView.this.mUmaRecorded) {
                if (ContentVideoView.this.mPlaybackStartTime == ContentVideoView.this.mOrientationChangedTime) {
                    if (ContentVideoView.this.isOrientationPortrait() != ContentVideoView.this.mInitialOrientation) {
                        ContentVideoView.this.mOrientationChangedTime = System.currentTimeMillis();
                    }
                } else if (!ContentVideoView.this.mPossibleAccidentalChange && ContentVideoView.this.isOrientationPortrait() == ContentVideoView.this.mInitialOrientation && System.currentTimeMillis() - ContentVideoView.this.mOrientationChangedTime < 5000) {
                    ContentVideoView.this.mPossibleAccidentalChange = true;
                }
            }
            setMeasuredDimension(width, height);
        }
    }

    private native void nativeExitFullscreen(long j, boolean z);

    private static native ContentVideoView nativeGetSingletonJavaContentVideoView();

    private native boolean nativeIsPlaying(long j);

    private native void nativeRecordExitFullscreenPlayback(long j, boolean z, long j2, long j3);

    private native void nativeRecordFullscreenPlayback(long j, boolean z, boolean z2);

    private native void nativeRequestMediaMetadata(long j);

    private native void nativeSetSurface(long j, Surface surface);

    private ContentVideoView(Context context, ContentViewCore contentViewCore, long nativeContentVideoView) {
        super(context);
        this.mCurrentState = STATE_IDLE;
        this.mExitFullscreenRunnable = new C03321();
        this.mNativeContentVideoView = nativeContentVideoView;
        this.mContentViewCore = contentViewCore;
        this.mClient = this.mContentViewCore.getContentVideoViewClient();
        this.mUmaRecorded = false;
        this.mPossibleAccidentalChange = false;
        initResources(context);
        this.mVideoSurfaceView = new VideoSurfaceView(context);
        showContentVideoView();
        setVisibility(STATE_IDLE);
    }

    private ContentVideoViewClient getContentVideoViewClient() {
        return this.mClient;
    }

    private void initResources(Context context) {
        if (this.mPlaybackErrorText == null) {
            this.mPlaybackErrorText = context.getString(C0317R.string.media_player_error_text_invalid_progressive_playback);
            this.mUnknownErrorText = context.getString(C0317R.string.media_player_error_text_unknown);
            this.mErrorButton = context.getString(C0317R.string.media_player_error_button);
            this.mErrorTitle = context.getString(C0317R.string.media_player_error_title);
            this.mVideoLoadingText = context.getString(C0317R.string.media_player_loading_video);
        }
    }

    private void showContentVideoView() {
        this.mVideoSurfaceView.getHolder().addCallback(this);
        addView(this.mVideoSurfaceView, new FrameLayout.LayoutParams(-2, -2, 17));
        this.mProgressView = this.mClient.getVideoLoadingProgressView();
        if (this.mProgressView == null) {
            this.mProgressView = new ProgressView(getContext(), this.mVideoLoadingText);
        }
        addView(this.mProgressView, new FrameLayout.LayoutParams(-2, -2, 17));
    }

    private SurfaceView getSurfaceView() {
        return this.mVideoSurfaceView;
    }

    @CalledByNative
    public void onMediaPlayerError(int errorType) {
        Log.m25d(TAG, "OnMediaPlayerError: %d", Integer.valueOf(errorType));
        if (this.mCurrentState != STATE_ERROR && this.mCurrentState != STATE_PLAYBACK_COMPLETED && errorType != STATE_PLAYBACK_COMPLETED) {
            this.mCurrentState = STATE_ERROR;
            if (ContentViewCore.activityFromContext(getContext()) == null) {
                Log.m42w(TAG, "Unable to show alert dialog because it requires an activity context", new Object[STATE_IDLE]);
            } else if (getWindowToken() != null) {
                String message;
                if (errorType == STATE_PAUSED) {
                    message = this.mPlaybackErrorText;
                } else {
                    message = this.mUnknownErrorText;
                }
                try {
                    new Builder(getContext()).setTitle(this.mErrorTitle).setMessage(message).setPositiveButton(this.mErrorButton, new C03332()).setCancelable(false).show();
                } catch (RuntimeException e) {
                    Object[] objArr = new Object[STATE_PAUSED];
                    objArr[STATE_IDLE] = message;
                    objArr[STATE_PLAYING] = e;
                    Log.m32e(TAG, "Cannot show the alert dialog, error message: %s", objArr);
                }
            }
        }
    }

    @CalledByNative
    private void onVideoSizeChanged(int width, int height) {
        this.mVideoWidth = width;
        this.mVideoHeight = height;
        this.mVideoSurfaceView.getHolder().setFixedSize(this.mVideoWidth, this.mVideoHeight);
    }

    @CalledByNative
    private void onBufferingUpdate(int percent) {
    }

    @CalledByNative
    private void onPlaybackComplete() {
        onCompletion();
    }

    @CalledByNative
    private void onUpdateMediaMetadata(int videoWidth, int videoHeight, int duration, boolean canPause, boolean canSeekBack, boolean canSeekForward) {
        boolean z = true;
        this.mProgressView.setVisibility(8);
        this.mCurrentState = isPlaying() ? STATE_PLAYING : STATE_PAUSED;
        onVideoSizeChanged(videoWidth, videoHeight);
        if (!this.mUmaRecorded) {
            try {
                if (System.getInt(getContext().getContentResolver(), "accelerometer_rotation") != 0) {
                    this.mInitialOrientation = isOrientationPortrait();
                    this.mUmaRecorded = true;
                    this.mPlaybackStartTime = System.currentTimeMillis();
                    this.mOrientationChangedTime = this.mPlaybackStartTime;
                    long j = this.mNativeContentVideoView;
                    if (videoHeight <= videoWidth) {
                        z = false;
                    }
                    nativeRecordFullscreenPlayback(j, z, this.mInitialOrientation);
                }
            } catch (SettingNotFoundException e) {
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mSurfaceHolder = holder;
        openVideo();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.mNativeContentVideoView != 0) {
            nativeSetSurface(this.mNativeContentVideoView, null);
        }
        this.mSurfaceHolder = null;
        post(this.mExitFullscreenRunnable);
    }

    @CalledByNative
    private void openVideo() {
        if (this.mSurfaceHolder != null) {
            this.mCurrentState = STATE_IDLE;
            if (this.mNativeContentVideoView != 0) {
                nativeRequestMediaMetadata(this.mNativeContentVideoView);
                nativeSetSurface(this.mNativeContentVideoView, this.mSurfaceHolder.getSurface());
            }
        }
    }

    private void onCompletion() {
        this.mCurrentState = STATE_PLAYBACK_COMPLETED;
    }

    public boolean isPlaying() {
        return this.mNativeContentVideoView != 0 && nativeIsPlaying(this.mNativeContentVideoView);
    }

    @CalledByNative
    private static ContentVideoView createContentVideoView(ContentViewCore contentViewCore, long nativeContentVideoView) {
        ThreadUtils.assertOnUiThread();
        Context context = contentViewCore.getContext();
        ContentVideoViewClient client = contentViewCore.getContentVideoViewClient();
        ContentVideoView videoView = new ContentVideoView(context, contentViewCore, nativeContentVideoView);
        client.enterFullscreenVideo(videoView);
        contentViewCore.updateDoubleTapSupport(false);
        contentViewCore.updateMultiTouchZoomSupport(false);
        return videoView;
    }

    public void removeSurfaceView() {
        removeView(this.mVideoSurfaceView);
        removeView(this.mProgressView);
        this.mVideoSurfaceView = null;
        this.mProgressView = null;
    }

    public void exitFullscreen(boolean relaseMediaPlayer) {
        if (this.mNativeContentVideoView != 0) {
            destroyContentVideoView(false);
            if (this.mUmaRecorded && !this.mPossibleAccidentalChange) {
                long timeBeforeOrientationChange = this.mOrientationChangedTime - this.mPlaybackStartTime;
                long timeAfterOrientationChange = System.currentTimeMillis() - this.mOrientationChangedTime;
                if (timeBeforeOrientationChange == 0) {
                    timeBeforeOrientationChange = timeAfterOrientationChange;
                    timeAfterOrientationChange = 0;
                }
                nativeRecordExitFullscreenPlayback(this.mNativeContentVideoView, this.mInitialOrientation, timeBeforeOrientationChange, timeAfterOrientationChange);
            }
            nativeExitFullscreen(this.mNativeContentVideoView, relaseMediaPlayer);
            this.mNativeContentVideoView = 0;
            this.mContentViewCore.updateDoubleTapSupport(true);
            this.mContentViewCore.updateMultiTouchZoomSupport(true);
        }
    }

    public void onFullscreenWindowFocused() {
        this.mClient.setSystemUiVisibility(true);
    }

    @CalledByNative
    private void onExitFullscreen() {
        exitFullscreen(false);
    }

    @CalledByNative
    private void destroyContentVideoView(boolean nativeViewDestroyed) {
        if (this.mVideoSurfaceView != null) {
            removeSurfaceView();
            setVisibility(8);
            this.mClient.exitFullscreenVideo();
        }
        if (nativeViewDestroyed) {
            this.mNativeContentVideoView = 0;
        }
    }

    public static ContentVideoView getContentVideoView() {
        return nativeGetSingletonJavaContentVideoView();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != MEDIA_SEEK_COMPLETE) {
            return super.onKeyUp(keyCode, event);
        }
        exitFullscreen(false);
        return true;
    }

    private boolean isOrientationPortrait() {
        Display display = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay();
        Point outputSize = new Point(STATE_IDLE, STATE_IDLE);
        display.getSize(outputSize);
        if (outputSize.x <= outputSize.y) {
            return true;
        }
        return false;
    }
}
