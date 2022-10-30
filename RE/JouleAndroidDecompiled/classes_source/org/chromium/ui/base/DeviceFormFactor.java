package org.chromium.ui.base;

import android.content.Context;
import org.chromium.base.CalledByNative;

public class DeviceFormFactor {
    private static final int MINIMUM_LARGE_TABLET_WIDTH_DP = 720;
    private static final int MINIMUM_TABLET_WIDTH_DP = 600;
    private static Boolean sIsLargeTablet;
    private static Boolean sIsTablet;

    static {
        sIsTablet = null;
        sIsLargeTablet = null;
    }

    @CalledByNative
    public static boolean isTablet(Context context) {
        if (sIsTablet == null) {
            sIsTablet = Boolean.valueOf(context.getResources().getConfiguration().smallestScreenWidthDp >= MINIMUM_TABLET_WIDTH_DP);
        }
        return sIsTablet.booleanValue();
    }

    public static boolean isLargeTablet(Context context) {
        if (sIsLargeTablet == null) {
            sIsLargeTablet = Boolean.valueOf(context.getResources().getConfiguration().smallestScreenWidthDp >= MINIMUM_LARGE_TABLET_WIDTH_DP);
        }
        return sIsLargeTablet.booleanValue();
    }
}
