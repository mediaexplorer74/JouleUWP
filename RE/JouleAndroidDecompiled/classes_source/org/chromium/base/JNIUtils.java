package org.chromium.base;

public class JNIUtils {
    @CalledByNative
    public static Object getClassLoader() {
        return JNIUtils.class.getClassLoader();
    }
}
