package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import com.google.android.gms.C0143R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.internal.zzmu;
import org.apache.cordova.camera.CameraLauncher;

public final class zzg {
    public static String zzc(Context context, int i, String str) {
        Resources resources = context.getResources();
        switch (i) {
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                if (zzmu.zzb(resources)) {
                    return resources.getString(C0143R.string.common_google_play_services_install_text_tablet, new Object[]{str});
                }
                return resources.getString(C0143R.string.common_google_play_services_install_text_phone, new Object[]{str});
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                return resources.getString(C0143R.string.common_google_play_services_update_text, new Object[]{str});
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return resources.getString(C0143R.string.common_google_play_services_enable_text, new Object[]{str});
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                return resources.getString(C0143R.string.common_google_play_services_invalid_account_text);
            case ConnectionResult.NETWORK_ERROR /*7*/:
                return resources.getString(C0143R.string.common_google_play_services_network_error_text);
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return resources.getString(C0143R.string.common_google_play_services_unsupported_text, new Object[]{str});
            case ConnectionResult.API_UNAVAILABLE /*16*/:
                return resources.getString(C0143R.string.common_google_play_services_api_unavailable_text, new Object[]{str});
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                return resources.getString(C0143R.string.common_google_play_services_sign_in_failed_text);
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                return resources.getString(C0143R.string.common_google_play_services_updating_text, new Object[]{str});
            case CameraLauncher.PERMISSION_DENIED_ERROR /*20*/:
                return resources.getString(C0143R.string.common_google_play_services_restricted_profile_text);
            case MotionEventCompat.AXIS_GENERIC_11 /*42*/:
                return resources.getString(C0143R.string.common_google_play_services_wear_update_text);
            default:
                return resources.getString(C0143R.string.common_google_play_services_unknown_issue, new Object[]{str});
        }
    }

    @Nullable
    public static final String zzg(Context context, int i) {
        Resources resources = context.getResources();
        switch (i) {
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                return resources.getString(C0143R.string.common_google_play_services_install_title);
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
            case MotionEventCompat.AXIS_GENERIC_11 /*42*/:
                return resources.getString(C0143R.string.common_google_play_services_update_title);
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return resources.getString(C0143R.string.common_google_play_services_enable_title);
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                return null;
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                Log.e("GoogleApiAvailability", "An invalid account was specified when connecting. Please provide a valid account.");
                return resources.getString(C0143R.string.common_google_play_services_invalid_account_title);
            case ConnectionResult.NETWORK_ERROR /*7*/:
                Log.e("GoogleApiAvailability", "Network error occurred. Please retry request later.");
                return resources.getString(C0143R.string.common_google_play_services_network_error_title);
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                Log.e("GoogleApiAvailability", "Internal error occurred. Please see logs for detailed information");
                return null;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                Log.e("GoogleApiAvailability", "Google Play services is invalid. Cannot recover.");
                return resources.getString(C0143R.string.common_google_play_services_unsupported_title);
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                Log.e("GoogleApiAvailability", "Developer error occurred. Please see logs for detailed information");
                return null;
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                Log.e("GoogleApiAvailability", "The application is not licensed to the user.");
                return null;
            case ConnectionResult.API_UNAVAILABLE /*16*/:
                Log.e("GoogleApiAvailability", "One of the API components you attempted to connect to is not available.");
                return null;
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                Log.e("GoogleApiAvailability", "The specified account could not be signed in.");
                return resources.getString(C0143R.string.common_google_play_services_sign_in_failed_title);
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                return resources.getString(C0143R.string.common_google_play_services_updating_title);
            case CameraLauncher.PERMISSION_DENIED_ERROR /*20*/:
                Log.e("GoogleApiAvailability", "The current user profile is restricted and could not use authenticated features.");
                return resources.getString(C0143R.string.common_google_play_services_restricted_profile_title);
            default:
                Log.e("GoogleApiAvailability", "Unexpected error code " + i);
                return null;
        }
    }

    public static String zzh(Context context, int i) {
        Resources resources = context.getResources();
        switch (i) {
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                return resources.getString(C0143R.string.common_google_play_services_install_button);
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                return resources.getString(C0143R.string.common_google_play_services_update_button);
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return resources.getString(C0143R.string.common_google_play_services_enable_button);
            default:
                return resources.getString(17039370);
        }
    }
}
