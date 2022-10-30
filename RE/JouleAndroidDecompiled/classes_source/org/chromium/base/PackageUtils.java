package org.chromium.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtils {
    public static PackageInfo getOwnPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0);
        } catch (NameNotFoundException e) {
            throw new AssertionError("Failed to retrieve own package info");
        }
    }

    public static int getPackageVersion(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo != null) {
                return packageInfo.versionCode;
            }
            return -1;
        } catch (NameNotFoundException e) {
            return -1;
        }
    }

    private PackageUtils() {
    }
}
