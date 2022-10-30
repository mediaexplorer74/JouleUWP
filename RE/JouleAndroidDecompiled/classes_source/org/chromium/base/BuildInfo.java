package org.chromium.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

public class BuildInfo {
    private static final int MAX_FINGERPRINT_LENGTH = 128;
    private static final String TAG = "BuildInfo";

    private BuildInfo() {
    }

    @CalledByNative
    public static String getDevice() {
        return Build.DEVICE;
    }

    @CalledByNative
    public static String getBrand() {
        return Build.BRAND;
    }

    @CalledByNative
    public static String getAndroidBuildId() {
        return Build.ID;
    }

    @CalledByNative
    public static String getAndroidBuildFingerprint() {
        return Build.FINGERPRINT.substring(0, Math.min(Build.FINGERPRINT.length(), MAX_FINGERPRINT_LENGTH));
    }

    @CalledByNative
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    @CalledByNative
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    @CalledByNative
    public static String getPackageVersionCode(Context context) {
        String msg = "versionCode not available.";
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            msg = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
            if (pi.versionCode > 0) {
                msg = Integer.toString(pi.versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.d(TAG, msg);
        }
        return msg;
    }

    @CalledByNative
    public static String getPackageVersionName(Context context) {
        String msg = "versionName not available";
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Log.d(TAG, msg);
            return msg;
        }
    }

    @CalledByNative
    public static String getPackageLabel(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            CharSequence label = packageManager.getApplicationLabel(packageManager.getApplicationInfo(context.getPackageName(), MAX_FINGERPRINT_LENGTH));
            return label != null ? label.toString() : CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        } catch (NameNotFoundException e) {
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
    }

    @CalledByNative
    public static String getPackageName(Context context) {
        String packageName = context != null ? context.getPackageName() : null;
        return packageName != null ? packageName : CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
    }

    @CalledByNative
    public static String getBuildType() {
        return Build.TYPE;
    }

    @CalledByNative
    public static int getSdkInt() {
        return VERSION.SDK_INT;
    }

    public static boolean isMncOrLater() {
        return VERSION.SDK_INT > 22 || TextUtils.equals("MNC", VERSION.CODENAME);
    }

    private static boolean isLanguageSplit(String splitName) {
        return splitName.length() == 9 && splitName.startsWith("config.");
    }

    @TargetApi(21)
    @CalledByNative
    public static boolean hasLanguageApkSplits(Context context) {
        if (VERSION.SDK_INT < 21) {
            return false;
        }
        PackageInfo packageInfo = PackageUtils.getOwnPackageInfo(context);
        if (packageInfo.splitNames == null) {
            return false;
        }
        for (String isLanguageSplit : packageInfo.splitNames) {
            if (isLanguageSplit(isLanguageSplit)) {
                return true;
            }
        }
        return false;
    }
}
