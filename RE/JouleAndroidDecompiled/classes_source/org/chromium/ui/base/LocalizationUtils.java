package org.chromium.ui.base;

import java.util.Locale;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.ApplicationStatus;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("l10n_util")
public class LocalizationUtils {
    public static final int LEFT_TO_RIGHT = 2;
    public static final int RIGHT_TO_LEFT = 1;
    public static final int UNKNOWN_DIRECTION = 0;
    private static Boolean sIsLayoutRtl;

    private static native String nativeGetDurationString(long j);

    private static native int nativeGetFirstStrongCharacterDirection(String str);

    private LocalizationUtils() {
    }

    @CalledByNative
    private static Locale getJavaLocale(String language, String country, String variant) {
        return new Locale(language, country, variant);
    }

    @CalledByNative
    private static String getDisplayNameForLocale(Locale locale, Locale displayLocale) {
        return locale.getDisplayName(displayLocale);
    }

    @CalledByNative
    public static boolean isLayoutRtl() {
        boolean z = true;
        if (sIsLayoutRtl == null) {
            if (ApiCompatibilityUtils.getLayoutDirection(ApplicationStatus.getApplicationContext().getResources().getConfiguration()) != RIGHT_TO_LEFT) {
                z = false;
            }
            sIsLayoutRtl = Boolean.valueOf(z);
        }
        return sIsLayoutRtl.booleanValue();
    }

    public static int getFirstStrongCharacterDirection(String string) {
        return nativeGetFirstStrongCharacterDirection(string);
    }

    public static String getDurationString(long timeInMillis) {
        return nativeGetDurationString(timeInMillis);
    }
}
