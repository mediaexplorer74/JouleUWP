package org.chromium.media;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import org.chromium.base.BuildInfo;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;

@JNINamespace("media")
class VideoCaptureFactory {

    static class ChromiumCameraInfo {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final String[][] SPECIAL_DEVICE_LIST;
        private static final String TAG = "cr.media";
        private static int sNumberOfSystemCameras;

        static {
            $assertionsDisabled = !VideoCaptureFactory.class.desiredAssertionStatus() ? true : $assertionsDisabled;
            String[][] strArr = new String[1][];
            strArr[0] = new String[]{"Peanut", "peanut"};
            SPECIAL_DEVICE_LIST = strArr;
            sNumberOfSystemCameras = -1;
        }

        ChromiumCameraInfo() {
        }

        private static boolean isSpecialDevice() {
            for (String[] device : SPECIAL_DEVICE_LIST) {
                if (device[0].contentEquals(Build.MODEL) && device[1].contentEquals(Build.DEVICE)) {
                    return true;
                }
            }
            return $assertionsDisabled;
        }

        private static boolean isSpecialCamera(int id) {
            return id >= sNumberOfSystemCameras ? true : $assertionsDisabled;
        }

        private static int toSpecialCameraId(int id) {
            if ($assertionsDisabled || isSpecialCamera(id)) {
                return id - sNumberOfSystemCameras;
            }
            throw new AssertionError();
        }

        private static int getNumberOfCameras(Context appContext) {
            if (sNumberOfSystemCameras == -1) {
                if (!BuildInfo.isMncOrLater() && appContext.getPackageManager().checkPermission("android.permission.CAMERA", appContext.getPackageName()) != 0) {
                    sNumberOfSystemCameras = 0;
                    Log.m42w(TAG, "Missing android.permission.CAMERA permission, no system camera available.", new Object[0]);
                } else if (VideoCaptureFactory.isLReleaseOrLater()) {
                    sNumberOfSystemCameras = VideoCaptureCamera2.getNumberOfCameras(appContext);
                } else {
                    sNumberOfSystemCameras = VideoCaptureAndroid.getNumberOfCameras();
                    if (isSpecialDevice()) {
                        Log.m25d(TAG, "Special device: %s", Build.MODEL);
                        sNumberOfSystemCameras += VideoCaptureTango.numberOfCameras();
                    }
                }
            }
            return sNumberOfSystemCameras;
        }
    }

    VideoCaptureFactory() {
    }

    private static boolean isLReleaseOrLater() {
        return VERSION.SDK_INT >= 21;
    }

    @CalledByNative
    static VideoCapture createVideoCapture(Context context, int id, long nativeVideoCaptureDeviceAndroid) {
        if (isLReleaseOrLater() && !VideoCaptureCamera2.isLegacyDevice(context, id)) {
            return new VideoCaptureCamera2(context, id, nativeVideoCaptureDeviceAndroid);
        }
        if (ChromiumCameraInfo.isSpecialCamera(id)) {
            return new VideoCaptureTango(context, ChromiumCameraInfo.toSpecialCameraId(id), nativeVideoCaptureDeviceAndroid);
        }
        return new VideoCaptureAndroid(context, id, nativeVideoCaptureDeviceAndroid);
    }

    @CalledByNative
    static int getNumberOfCameras(Context appContext) {
        return ChromiumCameraInfo.getNumberOfCameras(appContext);
    }

    @CalledByNative
    static int getCaptureApiType(int id, Context appContext) {
        if (isLReleaseOrLater()) {
            return VideoCaptureCamera2.getCaptureApiType(id, appContext);
        }
        if (ChromiumCameraInfo.isSpecialCamera(id)) {
            return VideoCaptureTango.getCaptureApiType(ChromiumCameraInfo.toSpecialCameraId(id));
        }
        return VideoCaptureAndroid.getCaptureApiType(id);
    }

    @CalledByNative
    static String getDeviceName(int id, Context appContext) {
        if (!isLReleaseOrLater() || VideoCaptureCamera2.isLegacyDevice(appContext, id)) {
            return ChromiumCameraInfo.isSpecialCamera(id) ? VideoCaptureTango.getName(ChromiumCameraInfo.toSpecialCameraId(id)) : VideoCaptureAndroid.getName(id);
        } else {
            return VideoCaptureCamera2.getName(id, appContext);
        }
    }

    @CalledByNative
    static VideoCaptureFormat[] getDeviceSupportedFormats(Context appContext, int id) {
        if (!isLReleaseOrLater() || VideoCaptureCamera2.isLegacyDevice(appContext, id)) {
            return ChromiumCameraInfo.isSpecialCamera(id) ? VideoCaptureTango.getDeviceSupportedFormats(ChromiumCameraInfo.toSpecialCameraId(id)) : VideoCaptureAndroid.getDeviceSupportedFormats(id);
        } else {
            return VideoCaptureCamera2.getDeviceSupportedFormats(appContext, id);
        }
    }

    @CalledByNative
    static int getCaptureFormatWidth(VideoCaptureFormat format) {
        return format.getWidth();
    }

    @CalledByNative
    static int getCaptureFormatHeight(VideoCaptureFormat format) {
        return format.getHeight();
    }

    @CalledByNative
    static int getCaptureFormatFramerate(VideoCaptureFormat format) {
        return format.getFramerate();
    }

    @CalledByNative
    static int getCaptureFormatPixelFormat(VideoCaptureFormat format) {
        return format.getPixelFormat();
    }
}
