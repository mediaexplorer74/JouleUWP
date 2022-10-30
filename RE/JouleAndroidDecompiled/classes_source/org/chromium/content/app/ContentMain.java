package org.chromium.content.app;

import android.content.Context;
import org.chromium.base.JNINamespace;

@JNINamespace("content")
public class ContentMain {
    private static native void nativeInitApplicationContext(Context context);

    private static native int nativeStart();

    public static void initApplicationContext(Context context) {
        nativeInitApplicationContext(context);
    }

    public static int start() {
        return nativeStart();
    }
}
