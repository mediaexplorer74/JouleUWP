package org.chromium.content.browser;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.media.TransportMediator;
import com.google.android.gms.common.ConnectionResult;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.ApplicationStatus;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.content_public.common.ScreenOrientationConstants;
import org.chromium.ui.gfx.DeviceDisplayInfo;

@JNINamespace("content")
public class ScreenOrientationProvider {
    private static final String TAG = "cr.ScreenOrientation";

    /* renamed from: org.chromium.content.browser.ScreenOrientationProvider.1 */
    static class C03501 implements Runnable {
        C03501() {
        }

        public void run() {
            ScreenOrientationListener.getInstance().startAccurateListening();
        }
    }

    /* renamed from: org.chromium.content.browser.ScreenOrientationProvider.2 */
    static class C03512 implements Runnable {
        C03512() {
        }

        public void run() {
            ScreenOrientationListener.getInstance().stopAccurateListening();
        }
    }

    private static int getOrientationFromWebScreenOrientations(byte orientation, Activity activity) {
        switch (orientation) {
            case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                return -1;
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                return 1;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                return 9;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return 0;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                return 8;
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                return 10;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                return 6;
            case ConnectionResult.NETWORK_ERROR /*7*/:
                return 7;
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                DeviceDisplayInfo displayInfo = DeviceDisplayInfo.create(activity);
                int rotation = displayInfo.getRotationDegrees();
                if (rotation == 0 || rotation == 180) {
                    if (displayInfo.getDisplayHeight() >= displayInfo.getDisplayWidth()) {
                        return 1;
                    }
                    return 0;
                } else if (displayInfo.getDisplayHeight() < displayInfo.getDisplayWidth()) {
                    return 1;
                } else {
                    return 0;
                }
            default:
                Log.m42w(TAG, "Trying to lock to unsupported orientation!", new Object[0]);
                return -1;
        }
    }

    @CalledByNative
    static void lockOrientation(byte orientation) {
        lockOrientation(orientation, ApplicationStatus.getLastTrackedFocusedActivity());
    }

    public static void lockOrientation(byte webScreenOrientation, Activity activity) {
        if (activity != null) {
            int orientation = getOrientationFromWebScreenOrientations(webScreenOrientation, activity);
            if (orientation != -1) {
                activity.setRequestedOrientation(orientation);
            }
        }
    }

    @CalledByNative
    static void unlockOrientation() {
        Activity activity = ApplicationStatus.getLastTrackedFocusedActivity();
        if (activity != null) {
            int defaultOrientation = getOrientationFromWebScreenOrientations((byte) activity.getIntent().getIntExtra(ScreenOrientationConstants.EXTRA_ORIENTATION, 0), activity);
            if (defaultOrientation == -1) {
                try {
                    defaultOrientation = activity.getPackageManager().getActivityInfo(activity.getComponentName(), TransportMediator.FLAG_KEY_MEDIA_NEXT).screenOrientation;
                } catch (NameNotFoundException e) {
                    activity.setRequestedOrientation(defaultOrientation);
                    return;
                } catch (Throwable th) {
                    activity.setRequestedOrientation(defaultOrientation);
                }
            }
            activity.setRequestedOrientation(defaultOrientation);
        }
    }

    @CalledByNative
    static void startAccurateListening() {
        ThreadUtils.runOnUiThread(new C03501());
    }

    @CalledByNative
    static void stopAccurateListening() {
        ThreadUtils.runOnUiThread(new C03512());
    }

    private ScreenOrientationProvider() {
    }
}
