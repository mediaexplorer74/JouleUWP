package org.chromium.content.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;

public class CleanupReference extends WeakReference<Object> {
    private static final int ADD_REF = 1;
    private static final boolean DEBUG = false;
    private static final int REMOVE_REF = 2;
    private static final String TAG = "cr.CleanupReference";
    private static Object sCleanupMonitor;
    private static ReferenceQueue<Object> sGcQueue;
    private static final Thread sReaperThread;
    private static Set<CleanupReference> sRefs;
    private Runnable mCleanupTask;

    /* renamed from: org.chromium.content.common.CleanupReference.1 */
    static class C03711 extends Thread {
        C03711(String x0) {
            super(x0);
        }

        public void run() {
            while (true) {
                try {
                    CleanupReference ref = (CleanupReference) CleanupReference.sGcQueue.remove();
                    synchronized (CleanupReference.sCleanupMonitor) {
                        Message.obtain(LazyHolder.sHandler, CleanupReference.REMOVE_REF, ref).sendToTarget();
                        CleanupReference.sCleanupMonitor.wait(500);
                    }
                } catch (Exception e) {
                    Object[] objArr = new Object[CleanupReference.ADD_REF];
                    objArr[0] = e;
                    Log.m32e(CleanupReference.TAG, "Queue remove exception:", objArr);
                }
            }
        }
    }

    private static class LazyHolder {
        static final Handler sHandler;

        /* renamed from: org.chromium.content.common.CleanupReference.LazyHolder.1 */
        static class C03721 extends Handler {
            C03721(Looper x0) {
                super(x0);
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void handleMessage(android.os.Message r8) {
                /*
                r7 = this;
                r2 = "CleanupReference.LazyHolder.handleMessage";
                org.chromium.base.TraceEvent.begin(r2);	 Catch:{ all -> 0x003b }
                r1 = r8.obj;	 Catch:{ all -> 0x003b }
                r1 = (org.chromium.content.common.CleanupReference) r1;	 Catch:{ all -> 0x003b }
                r2 = r8.what;	 Catch:{ all -> 0x003b }
                switch(r2) {
                    case 1: goto L_0x0042;
                    case 2: goto L_0x004a;
                    default: goto L_0x000e;
                };	 Catch:{ all -> 0x003b }
            L_0x000e:
                r2 = "cr.CleanupReference";
                r3 = "Bad message=%d";
                r4 = 1;
                r4 = new java.lang.Object[r4];	 Catch:{ all -> 0x003b }
                r5 = 0;
                r6 = r8.what;	 Catch:{ all -> 0x003b }
                r6 = java.lang.Integer.valueOf(r6);	 Catch:{ all -> 0x003b }
                r4[r5] = r6;	 Catch:{ all -> 0x003b }
                org.chromium.base.Log.m32e(r2, r3, r4);	 Catch:{ all -> 0x003b }
            L_0x0021:
                r3 = org.chromium.content.common.CleanupReference.sCleanupMonitor;	 Catch:{ all -> 0x003b }
                monitor-enter(r3);	 Catch:{ all -> 0x003b }
            L_0x0026:
                r2 = org.chromium.content.common.CleanupReference.sGcQueue;	 Catch:{ all -> 0x0038 }
                r2 = r2.poll();	 Catch:{ all -> 0x0038 }
                r0 = r2;
                r0 = (org.chromium.content.common.CleanupReference) r0;	 Catch:{ all -> 0x0038 }
                r1 = r0;
                if (r1 == 0) goto L_0x004e;
            L_0x0034:
                r1.runCleanupTaskInternal();	 Catch:{ all -> 0x0038 }
                goto L_0x0026;
            L_0x0038:
                r2 = move-exception;
                monitor-exit(r3);	 Catch:{ all -> 0x0038 }
                throw r2;	 Catch:{ all -> 0x003b }
            L_0x003b:
                r2 = move-exception;
                r3 = "CleanupReference.LazyHolder.handleMessage";
                org.chromium.base.TraceEvent.end(r3);
                throw r2;
            L_0x0042:
                r2 = org.chromium.content.common.CleanupReference.sRefs;	 Catch:{ all -> 0x003b }
                r2.add(r1);	 Catch:{ all -> 0x003b }
                goto L_0x0021;
            L_0x004a:
                r1.runCleanupTaskInternal();	 Catch:{ all -> 0x003b }
                goto L_0x0021;
            L_0x004e:
                r2 = org.chromium.content.common.CleanupReference.sCleanupMonitor;	 Catch:{ all -> 0x0038 }
                r2.notifyAll();	 Catch:{ all -> 0x0038 }
                monitor-exit(r3);	 Catch:{ all -> 0x0038 }
                r2 = "CleanupReference.LazyHolder.handleMessage";
                org.chromium.base.TraceEvent.end(r2);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.common.CleanupReference.LazyHolder.1.handleMessage(android.os.Message):void");
            }
        }

        private LazyHolder() {
        }

        static {
            sHandler = new C03721(ThreadUtils.getUiThreadLooper());
        }
    }

    static {
        sGcQueue = new ReferenceQueue();
        sCleanupMonitor = new Object();
        sReaperThread = new C03711(TAG);
        sReaperThread.setDaemon(true);
        sReaperThread.start();
        sRefs = new HashSet();
    }

    public CleanupReference(Object obj, Runnable cleanupTask) {
        super(obj, sGcQueue);
        this.mCleanupTask = cleanupTask;
        handleOnUiThread(ADD_REF);
    }

    public void cleanupNow() {
        handleOnUiThread(REMOVE_REF);
    }

    private void handleOnUiThread(int what) {
        Message msg = Message.obtain(LazyHolder.sHandler, what, this);
        if (Looper.myLooper() == msg.getTarget().getLooper()) {
            msg.getTarget().handleMessage(msg);
            msg.recycle();
            return;
        }
        msg.sendToTarget();
    }

    private void runCleanupTaskInternal() {
        sRefs.remove(this);
        if (this.mCleanupTask != null) {
            this.mCleanupTask.run();
            this.mCleanupTask = null;
        }
        clear();
    }
}
