package org.chromium.base;

public class FieldTrialList {
    private static native String nativeFindFullName(String str);

    private static native boolean nativeTrialExists(String str);

    private FieldTrialList() {
    }

    public static String findFullName(String trialName) {
        return nativeFindFullName(trialName);
    }

    public static boolean trialExists(String trialName) {
        return nativeTrialExists(trialName);
    }
}
