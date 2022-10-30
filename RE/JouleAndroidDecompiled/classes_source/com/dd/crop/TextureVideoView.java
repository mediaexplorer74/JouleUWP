package com.dd.crop;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import com.google.android.gms.common.ConnectionResult;
import java.io.IOException;
import org.apache.cordova.camera.CameraLauncher;

public class TextureVideoView extends TextureView implements SurfaceTextureListener {
    public static final boolean LOG_ON = true;
    private static final String TAG;
    private boolean mIsDataSourceSet;
    private boolean mIsPlayCalled;
    private boolean mIsVideoPrepared;
    private boolean mIsViewAvailable;
    private MediaPlayerListener mListener;
    private MediaPlayer mMediaPlayer;
    private ScaleType mScaleType;
    private State mState;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private float mVideoHeight;
    private float mVideoWidth;

    /* renamed from: com.dd.crop.TextureVideoView.1 */
    class C01321 implements OnVideoSizeChangedListener {
        C01321() {
        }

        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            TextureVideoView.this.mVideoWidth = (float) width;
            TextureVideoView.this.mVideoHeight = (float) height;
            TextureVideoView.log("Video size changed, updating texture view size");
            TextureVideoView.this.updateTextureViewSize();
        }
    }

    /* renamed from: com.dd.crop.TextureVideoView.2 */
    class C01332 implements OnCompletionListener {
        C01332() {
        }

        public void onCompletion(MediaPlayer mp) {
            TextureVideoView.this.mState = State.END;
            TextureVideoView.log("Video has ended.");
            if (TextureVideoView.this.mListener != null) {
                TextureVideoView.this.mListener.onVideoEnd();
            }
        }
    }

    /* renamed from: com.dd.crop.TextureVideoView.3 */
    class C01343 implements OnPreparedListener {
        C01343() {
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
            TextureVideoView.this.mIsVideoPrepared = TextureVideoView.LOG_ON;
            TextureVideoView.log("Player is prepared");
            if (TextureVideoView.this.mIsPlayCalled && TextureVideoView.this.mIsViewAvailable) {
                TextureVideoView.log("Player is prepared and play() was called.");
                TextureVideoView.this.play();
            }
            if (TextureVideoView.this.mListener != null) {
                TextureVideoView.this.mListener.onVideoPrepared();
            }
        }
    }

    /* renamed from: com.dd.crop.TextureVideoView.4 */
    static /* synthetic */ class C01354 {
        static final /* synthetic */ int[] $SwitchMap$com$dd$crop$TextureVideoView$ScaleType;

        static {
            $SwitchMap$com$dd$crop$TextureVideoView$ScaleType = new int[ScaleType.values().length];
            try {
                $SwitchMap$com$dd$crop$TextureVideoView$ScaleType[ScaleType.TOP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$dd$crop$TextureVideoView$ScaleType[ScaleType.BOTTOM.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$dd$crop$TextureVideoView$ScaleType[ScaleType.CENTER_CROP.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public interface MediaPlayerListener {
        void onVideoEnd();

        void onVideoPrepared();
    }

    public enum ScaleType {
        CENTER_CROP,
        TOP,
        BOTTOM
    }

    public enum State {
        UNINITIALIZED,
        PLAY,
        STOP,
        PAUSE,
        END
    }

    static {
        TAG = TextureVideoView.class.getName();
    }

    public TextureVideoView(Context context) {
        super(context);
        initView();
    }

    public TextureVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TextureVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        initPlayer();
        setScaleType(ScaleType.CENTER_CROP);
        setSurfaceTextureListener(this);
    }

    public void setScaleType(ScaleType scaleType) {
        this.mScaleType = scaleType;
    }

    private void updateTextureViewSize() {
        int pivotPointX;
        int pivotPointY;
        float viewWidth = (float) getWidth();
        float viewHeight = (float) getHeight();
        float scaleY = (viewWidth * (this.mVideoHeight / this.mVideoWidth)) / viewHeight;
        switch (C01354.$SwitchMap$com$dd$crop$TextureVideoView$ScaleType[this.mScaleType.ordinal()]) {
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                pivotPointX = 0;
                pivotPointY = 0;
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                pivotPointX = (int) viewWidth;
                pivotPointY = (int) viewHeight;
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                pivotPointX = (int) (viewWidth / 2.0f);
                pivotPointY = (int) (viewHeight / 2.0f);
                break;
            default:
                pivotPointX = (int) (viewWidth / 2.0f);
                pivotPointY = (int) (viewHeight / 2.0f);
                break;
        }
        log("Texture view size is (" + viewWidth + ", " + viewHeight + ")");
        log("Video size is (" + this.mVideoWidth + ", " + this.mVideoHeight + ")");
        log("Texture view scale is (" + 1.0f + ", " + scaleY + ")");
        log("Scaling view to (" + ((int) (viewWidth * 1.0f)) + ", " + ((int) (viewHeight * scaleY)) + ")");
        log("Pivot point (" + pivotPointX + ", " + pivotPointY + ")");
        Matrix matrix = new Matrix();
        matrix.setScale(1.0f, scaleY, (float) pivotPointX, (float) pivotPointY);
        setTransform(matrix);
    }

    private void initPlayer() {
        if (this.mMediaPlayer == null) {
            this.mMediaPlayer = new MediaPlayer();
        } else {
            this.mMediaPlayer.reset();
        }
        this.mMediaPlayer.setVolume(0.0f, 0.0f);
        this.mIsVideoPrepared = false;
        this.mIsPlayCalled = false;
        this.mState = State.UNINITIALIZED;
    }

    public void setDataSource(String path) {
        initPlayer();
        try {
            this.mMediaPlayer.setDataSource(path);
            this.mMediaPlayer.setVideoScalingMode(2);
            this.mIsDataSourceSet = LOG_ON;
            prepare();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void setDataSource(Context context, Uri uri) {
        initPlayer();
        try {
            this.mMediaPlayer.setDataSource(context, uri);
            this.mIsDataSourceSet = LOG_ON;
            prepare();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void setDataSource(AssetFileDescriptor afd) {
        initPlayer();
        try {
            this.mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            this.mIsDataSourceSet = LOG_ON;
            prepare();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void prepare() {
        try {
            this.mMediaPlayer.setOnVideoSizeChangedListener(new C01321());
            this.mMediaPlayer.setOnCompletionListener(new C01332());
            this.mMediaPlayer.prepareAsync();
            this.mMediaPlayer.setOnPreparedListener(new C01343());
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        } catch (SecurityException e2) {
            Log.d(TAG, e2.getMessage());
        } catch (IllegalStateException e3) {
            Log.d(TAG, e3.toString());
        }
    }

    public void play() {
        if (this.mIsDataSourceSet) {
            this.mIsPlayCalled = LOG_ON;
            if (!this.mIsVideoPrepared) {
                log("play() was called but video is not prepared yet, waiting.");
                return;
            } else if (!this.mIsViewAvailable) {
                log("play() was called but view is not available yet, waiting.");
                return;
            } else if (this.mState == State.PLAY) {
                log("play() was called but video is already playing.");
                return;
            } else if (this.mState == State.PAUSE) {
                log("play() was called but video is paused, resuming.");
                this.mState = State.PLAY;
                this.mMediaPlayer.start();
                return;
            } else if (this.mState == State.END || this.mState == State.STOP) {
                log("play() was called but video already ended, starting over.");
                this.mState = State.PLAY;
                this.mMediaPlayer.seekTo(0);
                this.mMediaPlayer.start();
                return;
            } else {
                this.mState = State.PLAY;
                this.mMediaPlayer.start();
                return;
            }
        }
        log("play() was called but data source was not set.");
    }

    public void pause() {
        if (this.mState == State.PAUSE) {
            log("pause() was called but video already paused.");
        } else if (this.mState == State.STOP) {
            log("pause() was called but video already stopped.");
        } else if (this.mState == State.END) {
            log("pause() was called but video already ended.");
        } else {
            this.mState = State.PAUSE;
            if (this.mMediaPlayer.isPlaying()) {
                this.mMediaPlayer.pause();
            }
        }
    }

    public void stop() {
        if (this.mState == State.STOP) {
            log("stop() was called but video already stopped.");
        } else if (this.mState == State.END) {
            log("stop() was called but video already ended.");
        } else {
            this.mState = State.STOP;
            if (this.mMediaPlayer.isPlaying()) {
                this.mMediaPlayer.pause();
                this.mMediaPlayer.seekTo(0);
            }
        }
    }

    public void setLooping(boolean looping) {
        this.mMediaPlayer.setLooping(looping);
    }

    public void seekTo(int milliseconds) {
        this.mMediaPlayer.seekTo(milliseconds);
    }

    public int getDuration() {
        return this.mMediaPlayer.getDuration();
    }

    static void log(String message) {
        Log.d(TAG, message);
    }

    public void setListener(MediaPlayerListener listener) {
        this.mListener = listener;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        this.mSurfaceTexture = surfaceTexture;
        this.mSurface = new Surface(surfaceTexture);
        this.mMediaPlayer.setSurface(this.mSurface);
        this.mIsViewAvailable = LOG_ON;
        if (this.mIsDataSourceSet && this.mIsPlayCalled && this.mIsVideoPrepared) {
            log("View is available and play() was called.");
            play();
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
}
