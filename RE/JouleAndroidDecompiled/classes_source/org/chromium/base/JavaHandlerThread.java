package org.chromium.base;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;

@JNINamespace("base::android")
class JavaHandlerThread {
    final HandlerThread mThread;

    /* renamed from: org.chromium.base.JavaHandlerThread.1 */
    class C03031 implements Runnable {
        final /* synthetic */ long val$nativeEvent;
        final /* synthetic */ long val$nativeThread;

        C03031(long j, long j2) {
            this.val$nativeThread = j;
            this.val$nativeEvent = j2;
        }

        public void run() {
            JavaHandlerThread.this.nativeInitializeThread(this.val$nativeThread, this.val$nativeEvent);
        }
    }

    /* renamed from: org.chromium.base.JavaHandlerThread.2 */
    class C03042 implements Runnable {
        final /* synthetic */ long val$nativeEvent;
        final /* synthetic */ long val$nativeThread;
        final /* synthetic */ boolean val$quitSafely;

        C03042(long j, long j2, boolean z) {
            this.val$nativeThread = j;
            this.val$nativeEvent = j2;
            this.val$quitSafely = z;
        }

        public void run() {
            JavaHandlerThread.this.nativeStopThread(this.val$nativeThread, this.val$nativeEvent);
            if (!this.val$quitSafely) {
                JavaHandlerThread.this.mThread.quit();
            }
        }
    }

    private native void nativeInitializeThread(long j, long j2);

    private native void nativeStopThread(long j, long j2);

    private JavaHandlerThread(String name) {
        this.mThread = new HandlerThread(name);
    }

    @CalledByNative
    private static JavaHandlerThread create(String name) {
        return new JavaHandlerThread(name);
    }

    @CalledByNative
    private void start(long nativeThread, long nativeEvent) {
        this.mThread.start();
        new Handler(this.mThread.getLooper()).post(new C03031(nativeThread, nativeEvent));
    }

    @TargetApi(18)
    @CalledByNative
    private void stop(long nativeThread, long nativeEvent) {
        boolean quitSafely = VERSION.SDK_INT >= 18;
        new Handler(this.mThread.getLooper()).post(new C03042(nativeThread, nativeEvent, quitSafely));
        if (quitSafely) {
            this.mThread.quitSafely();
        }
    }
}
