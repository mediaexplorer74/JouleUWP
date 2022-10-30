package org.chromium.base;

@JNINamespace("base::android")
public class EventLog {
    @CalledByNative
    public static void writeEvent(int tag, int value) {
        android.util.EventLog.writeEvent(tag, value);
    }
}
