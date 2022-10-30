package org.chromium.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("media")
class MediaPlayerListener implements OnPreparedListener, OnCompletionListener, OnBufferingUpdateListener, OnSeekCompleteListener, OnVideoSizeChangedListener, OnErrorListener {
    private static final int MEDIA_ERROR_DECODE = 1;
    private static final int MEDIA_ERROR_FORMAT = 0;
    private static final int MEDIA_ERROR_INVALID_CODE = 3;
    public static final int MEDIA_ERROR_MALFORMED = -1007;
    private static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 2;
    public static final int MEDIA_ERROR_TIMED_OUT = -110;
    private final Context mContext;
    private long mNativeMediaPlayerListener;

    private native void nativeOnBufferingUpdate(long j, int i);

    private native void nativeOnMediaError(long j, int i);

    private native void nativeOnMediaInterrupted(long j);

    private native void nativeOnMediaPrepared(long j);

    private native void nativeOnPlaybackComplete(long j);

    private native void nativeOnSeekComplete(long j);

    private native void nativeOnVideoSizeChanged(long j, int i, int i2);

    private MediaPlayerListener(long nativeMediaPlayerListener, Context context) {
        this.mNativeMediaPlayerListener = 0;
        this.mNativeMediaPlayerListener = nativeMediaPlayerListener;
        this.mContext = context;
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        int errorType;
        switch (what) {
            case MEDIA_ERROR_DECODE /*1*/:
                switch (extra) {
                    case MEDIA_ERROR_MALFORMED /*-1007*/:
                        errorType = MEDIA_ERROR_DECODE;
                        break;
                    case MEDIA_ERROR_TIMED_OUT /*-110*/:
                        errorType = MEDIA_ERROR_INVALID_CODE;
                        break;
                    default:
                        errorType = MEDIA_ERROR_FORMAT;
                        break;
                }
            case 100:
                errorType = MEDIA_ERROR_DECODE;
                break;
            case 200:
                errorType = MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK;
                break;
            default:
                errorType = MEDIA_ERROR_INVALID_CODE;
                break;
        }
        nativeOnMediaError(this.mNativeMediaPlayerListener, errorType);
        return true;
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        nativeOnVideoSizeChanged(this.mNativeMediaPlayerListener, width, height);
    }

    public void onSeekComplete(MediaPlayer mp) {
        nativeOnSeekComplete(this.mNativeMediaPlayerListener);
    }

    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        nativeOnBufferingUpdate(this.mNativeMediaPlayerListener, percent);
    }

    public void onCompletion(MediaPlayer mp) {
        nativeOnPlaybackComplete(this.mNativeMediaPlayerListener);
    }

    public void onPrepared(MediaPlayer mp) {
        nativeOnMediaPrepared(this.mNativeMediaPlayerListener);
    }

    @CalledByNative
    private static MediaPlayerListener create(long nativeMediaPlayerListener, Context context, MediaPlayerBridge mediaPlayerBridge) {
        MediaPlayerListener listener = new MediaPlayerListener(nativeMediaPlayerListener, context);
        if (mediaPlayerBridge != null) {
            mediaPlayerBridge.setOnBufferingUpdateListener(listener);
            mediaPlayerBridge.setOnCompletionListener(listener);
            mediaPlayerBridge.setOnErrorListener(listener);
            mediaPlayerBridge.setOnPreparedListener(listener);
            mediaPlayerBridge.setOnSeekCompleteListener(listener);
            mediaPlayerBridge.setOnVideoSizeChangedListener(listener);
        }
        return listener;
    }
}
