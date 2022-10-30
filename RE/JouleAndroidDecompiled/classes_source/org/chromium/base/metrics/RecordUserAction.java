package org.chromium.base.metrics;

import org.chromium.base.JNINamespace;
import org.chromium.base.ThreadUtils;

@JNINamespace("base::android")
public class RecordUserAction {

    /* renamed from: org.chromium.base.metrics.RecordUserAction.1 */
    static class C03151 implements Runnable {
        final /* synthetic */ String val$action;

        C03151(String str) {
            this.val$action = str;
        }

        public void run() {
            RecordUserAction.nativeRecordUserAction(this.val$action);
        }
    }

    private static native void nativeRecordUserAction(String str);

    public static void record(String action) {
        if (ThreadUtils.runningOnUiThread()) {
            nativeRecordUserAction(action);
        } else {
            ThreadUtils.runOnUiThread(new C03151(action));
        }
    }
}
