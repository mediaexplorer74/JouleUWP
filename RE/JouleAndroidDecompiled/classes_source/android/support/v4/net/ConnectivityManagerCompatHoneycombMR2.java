package android.support.v4.net;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.gms.common.ConnectionResult;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.content.browser.ContentViewCore;

class ConnectivityManagerCompatHoneycombMR2 {
    ConnectivityManagerCompatHoneycombMR2() {
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager cm) {
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return true;
        }
        switch (info.getType()) {
            case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                return true;
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
            case ConnectionResult.NETWORK_ERROR /*7*/:
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return false;
            default:
                return true;
        }
    }
}
