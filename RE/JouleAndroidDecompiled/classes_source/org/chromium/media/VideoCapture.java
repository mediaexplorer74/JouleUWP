package org.chromium.media;

import android.content.Context;
import android.view.WindowManager;
import com.google.android.gms.common.ConnectionResult;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("media")
public abstract class VideoCapture {
    protected int mCameraNativeOrientation;
    protected VideoCaptureFormat mCaptureFormat;
    protected final Context mContext;
    protected final int mId;
    protected boolean mInvertDeviceOrientationReadings;
    protected final long mNativeVideoCaptureDeviceAndroid;

    @CalledByNative
    public abstract boolean allocate(int i, int i2, int i3);

    @CalledByNative
    public abstract void deallocate();

    public native void nativeOnError(long j, String str);

    public native void nativeOnFrameAvailable(long j, byte[] bArr, int i, int i2);

    @CalledByNative
    public abstract boolean startCapture();

    @CalledByNative
    public abstract boolean stopCapture();

    VideoCapture(Context context, int id, long nativeVideoCaptureDeviceAndroid) {
        this.mCaptureFormat = null;
        this.mContext = context;
        this.mId = id;
        this.mNativeVideoCaptureDeviceAndroid = nativeVideoCaptureDeviceAndroid;
    }

    @CalledByNative
    public final int queryWidth() {
        return this.mCaptureFormat.mWidth;
    }

    @CalledByNative
    public final int queryHeight() {
        return this.mCaptureFormat.mHeight;
    }

    @CalledByNative
    public final int queryFrameRate() {
        return this.mCaptureFormat.mFramerate;
    }

    @CalledByNative
    public final int getColorspace() {
        switch (this.mCaptureFormat.mPixelFormat) {
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                return 17;
            case AndroidImageFormat.YUV_420_888 /*35*/:
                return 35;
            case AndroidImageFormat.YV12 /*842094169*/:
                return AndroidImageFormat.YV12;
            default:
                return 0;
        }
    }

    protected final int getCameraRotation() {
        return (this.mCameraNativeOrientation + (this.mInvertDeviceOrientationReadings ? 360 - getDeviceRotation() : getDeviceRotation())) % 360;
    }

    protected final int getDeviceRotation() {
        if (this.mContext == null) {
            return 0;
        }
        switch (((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRotation()) {
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                return 90;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                return 180;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return 270;
            default:
                return 0;
        }
    }
}
