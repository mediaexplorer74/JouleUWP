package org.chromium.content.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.view.Surface;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.base.annotations.SuppressFBWarnings;
import org.chromium.base.library_loader.Linker;
import org.chromium.content.browser.ChildProcessConnection;
import org.chromium.content.browser.FileDescriptorInfo;
import org.chromium.content.common.IChildProcessCallback;
import org.chromium.content.common.IChildProcessService.Stub;

@JNINamespace("content")
public class ChildProcessService extends Service {
    private static final String MAIN_THREAD_NAME = "ChildProcessMain";
    private static final String TAG = "cr.ChildProcessService";
    private static AtomicReference<Context> sContext;
    private final Semaphore mActivitySemaphore;
    private final Stub mBinder;
    private IChildProcessCallback mCallback;
    private String[] mCommandLineParams;
    private int mCpuCount;
    private long mCpuFeatures;
    private FileDescriptorInfo[] mFdInfos;
    private boolean mIsBound;
    private boolean mLibraryInitialized;
    private ChromiumLinkerParams mLinkerParams;
    private Thread mMainThread;

    /* renamed from: org.chromium.content.app.ChildProcessService.2 */
    class C03182 implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !ChildProcessService.class.desiredAssertionStatus();
        }

        C03182() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        @org.chromium.base.annotations.SuppressFBWarnings({"DM_EXIT"})
        public void run() {
            /*
            r18 = this;
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r3 = r2.mMainThread;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            monitor-enter(r3);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
        L_0x0009:
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ all -> 0x001f }
            r2 = r2.mCommandLineParams;	 Catch:{ all -> 0x001f }
            if (r2 != 0) goto L_0x0036;
        L_0x0013:
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ all -> 0x001f }
            r2 = r2.mMainThread;	 Catch:{ all -> 0x001f }
            r2.wait();	 Catch:{ all -> 0x001f }
            goto L_0x0009;
        L_0x001f:
            r2 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x001f }
            throw r2;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
        L_0x0022:
            r9 = move-exception;
            r2 = "cr.ChildProcessService";
            r3 = "%s startup failed: %s";
            r4 = 2;
            r4 = new java.lang.Object[r4];
            r5 = 0;
            r6 = "ChildProcessMain";
            r4[r5] = r6;
            r5 = 1;
            r4[r5] = r9;
            org.chromium.base.Log.m42w(r2, r3, r4);
        L_0x0035:
            return;
        L_0x0036:
            monitor-exit(r3);	 Catch:{ all -> 0x001f }
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.mCommandLineParams;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            org.chromium.base.CommandLine.init(r2);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r14 = org.chromium.base.library_loader.Linker.getInstance();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r17 = r14.isUsed();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r16 = 0;
            if (r17 == 0) goto L_0x00c1;
        L_0x004e:
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r3 = r2.mMainThread;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            monitor-enter(r3);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
        L_0x0057:
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ all -> 0x006d }
            r2 = r2.mIsBound;	 Catch:{ all -> 0x006d }
            if (r2 != 0) goto L_0x0084;
        L_0x0061:
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ all -> 0x006d }
            r2 = r2.mMainThread;	 Catch:{ all -> 0x006d }
            r2.wait();	 Catch:{ all -> 0x006d }
            goto L_0x0057;
        L_0x006d:
            r2 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x006d }
            throw r2;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
        L_0x0070:
            r9 = move-exception;
            r2 = "cr.ChildProcessService";
            r3 = "%s startup failed: %s";
            r4 = 2;
            r4 = new java.lang.Object[r4];
            r5 = 0;
            r6 = "ChildProcessMain";
            r4[r5] = r6;
            r5 = 1;
            r4[r5] = r9;
            org.chromium.base.Log.m42w(r2, r3, r4);
            goto L_0x0035;
        L_0x0084:
            monitor-exit(r3);	 Catch:{ all -> 0x006d }
            r2 = $assertionsDisabled;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            if (r2 != 0) goto L_0x0099;
        L_0x0089:
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.mLinkerParams;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            if (r2 != 0) goto L_0x0099;
        L_0x0093:
            r2 = new java.lang.AssertionError;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2.<init>();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            throw r2;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
        L_0x0099:
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.mLinkerParams;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.mWaitForSharedRelro;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            if (r2 == 0) goto L_0x0148;
        L_0x00a5:
            r16 = 1;
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.mLinkerParams;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.mBaseLoadAddress;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r14.initServiceProcess(r2);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
        L_0x00b4:
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.mLinkerParams;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.mTestRunnerClassName;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r14.setTestRunnerClassName(r2);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
        L_0x00c1:
            r12 = 0;
            r2 = org.chromium.base.CommandLine.getInstance();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r3 = "renderer-wait-for-java-debugger";
            r2 = r2.hasSwitch(r3);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            if (r2 == 0) goto L_0x00d1;
        L_0x00ce:
            android.os.Debug.waitForDebugger();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
        L_0x00d1:
            r15 = 0;
            r2 = 2;
            r2 = org.chromium.base.library_loader.LibraryLoader.get(r2);	 Catch:{ ProcessInitException -> 0x014d, InterruptedException -> 0x0022 }
            r0 = r18;
            r3 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ ProcessInitException -> 0x014d, InterruptedException -> 0x0022 }
            r3 = r3.getApplicationContext();	 Catch:{ ProcessInitException -> 0x014d, InterruptedException -> 0x0022 }
            r2.loadNow(r3);	 Catch:{ ProcessInitException -> 0x014d, InterruptedException -> 0x0022 }
            r12 = 1;
        L_0x00e3:
            if (r12 != 0) goto L_0x00fb;
        L_0x00e5:
            if (r16 == 0) goto L_0x00fb;
        L_0x00e7:
            r14.disableSharedRelros();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = 2;
            r2 = org.chromium.base.library_loader.LibraryLoader.get(r2);	 Catch:{ ProcessInitException -> 0x016b, InterruptedException -> 0x0022 }
            r0 = r18;
            r3 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ ProcessInitException -> 0x016b, InterruptedException -> 0x0022 }
            r3 = r3.getApplicationContext();	 Catch:{ ProcessInitException -> 0x016b, InterruptedException -> 0x0022 }
            r2.loadNow(r3);	 Catch:{ ProcessInitException -> 0x016b, InterruptedException -> 0x0022 }
            r12 = 1;
        L_0x00fb:
            if (r12 != 0) goto L_0x0101;
        L_0x00fd:
            r2 = -1;
            java.lang.System.exit(r2);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
        L_0x0101:
            r2 = 2;
            r2 = org.chromium.base.library_loader.LibraryLoader.get(r2);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r0 = r16;
            r2.registerRendererProcessHistogram(r0, r15);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = 2;
            r2 = org.chromium.base.library_loader.LibraryLoader.get(r2);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2.initialize();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r3 = r2.mMainThread;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            monitor-enter(r3);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ all -> 0x0145 }
            r4 = 1;
            r2.mLibraryInitialized = r4;	 Catch:{ all -> 0x0145 }
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ all -> 0x0145 }
            r2 = r2.mMainThread;	 Catch:{ all -> 0x0145 }
            r2.notifyAll();	 Catch:{ all -> 0x0145 }
        L_0x012f:
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ all -> 0x0145 }
            r2 = r2.mFdInfos;	 Catch:{ all -> 0x0145 }
            if (r2 != 0) goto L_0x017a;
        L_0x0139:
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ all -> 0x0145 }
            r2 = r2.mMainThread;	 Catch:{ all -> 0x0145 }
            r2.wait();	 Catch:{ all -> 0x0145 }
            goto L_0x012f;
        L_0x0145:
            r2 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x0145 }
            throw r2;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
        L_0x0148:
            r14.disableSharedRelros();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            goto L_0x00b4;
        L_0x014d:
            r9 = move-exception;
            if (r16 == 0) goto L_0x015c;
        L_0x0150:
            r2 = "cr.ChildProcessService";
            r3 = "Failed to load native library with shared RELRO, retrying without";
            r4 = 0;
            r4 = new java.lang.Object[r4];	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            org.chromium.base.Log.m42w(r2, r3, r4);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r15 = 1;
            goto L_0x00e3;
        L_0x015c:
            r2 = "cr.ChildProcessService";
            r3 = "Failed to load native library";
            r4 = 1;
            r4 = new java.lang.Object[r4];	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r5 = 0;
            r4[r5] = r9;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            org.chromium.base.Log.m32e(r2, r3, r4);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            goto L_0x00e3;
        L_0x016b:
            r9 = move-exception;
            r2 = "cr.ChildProcessService";
            r3 = "Failed to load native library on retry";
            r4 = 1;
            r4 = new java.lang.Object[r4];	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r5 = 0;
            r4[r5] = r9;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            org.chromium.base.Log.m32e(r2, r3, r4);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            goto L_0x00fb;
        L_0x017a:
            monitor-exit(r3);	 Catch:{ all -> 0x0145 }
            r2 = org.chromium.content.app.ChildProcessService.sContext;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.get();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = (android.content.Context) r2;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.getApplicationContext();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            org.chromium.content.app.ContentMain.initApplicationContext(r2);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r8 = r2.mFdInfos;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r13 = r8.length;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r11 = 0;
        L_0x0196:
            if (r11 >= r13) goto L_0x01ac;
        L_0x0198:
            r10 = r8[r11];	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r10.mId;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r3 = r10.mFd;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r3 = r3.detachFd();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r4 = r10.mOffset;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r6 = r10.mSize;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            org.chromium.content.app.ChildProcessService.nativeRegisterGlobalFileDescriptor(r2, r3, r4, r6);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r11 = r11 + 1;
            goto L_0x0196;
        L_0x01ac:
            r2 = org.chromium.content.app.ChildProcessService.sContext;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.get();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = (android.content.Context) r2;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.getApplicationContext();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r0 = r18;
            r3 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r0 = r18;
            r4 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r4 = r4.mCpuCount;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r0 = r18;
            r5 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r6 = r5.mCpuFeatures;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            org.chromium.content.app.ChildProcessService.nativeInitChildProcess(r2, r3, r4, r6);	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r0 = r18;
            r2 = org.chromium.content.app.ChildProcessService.this;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.mActivitySemaphore;	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            r2 = r2.tryAcquire();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            if (r2 == 0) goto L_0x0035;
        L_0x01df:
            org.chromium.content.app.ContentMain.start();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            org.chromium.content.app.ChildProcessService.nativeExitChildProcess();	 Catch:{ InterruptedException -> 0x0022, ProcessInitException -> 0x0070 }
            goto L_0x0035;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.app.ChildProcessService.2.run():void");
        }
    }

    /* renamed from: org.chromium.content.app.ChildProcessService.1 */
    class C06771 extends Stub {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !ChildProcessService.class.desiredAssertionStatus();
        }

        C06771() {
        }

        public int setupConnection(Bundle args, IChildProcessCallback callback) {
            ChildProcessService.this.mCallback = callback;
            args.setClassLoader(ChildProcessService.this.getClassLoader());
            synchronized (ChildProcessService.this.mMainThread) {
                if (ChildProcessService.this.mCommandLineParams == null) {
                    ChildProcessService.this.mCommandLineParams = args.getStringArray(ChildProcessConnection.EXTRA_COMMAND_LINE);
                }
                if ($assertionsDisabled || ChildProcessService.this.mCommandLineParams != null) {
                    ChildProcessService.this.mCpuCount = args.getInt(ChildProcessConnection.EXTRA_CPU_COUNT);
                    ChildProcessService.this.mCpuFeatures = args.getLong(ChildProcessConnection.EXTRA_CPU_FEATURES);
                    if ($assertionsDisabled || ChildProcessService.this.mCpuCount > 0) {
                        Parcelable[] fdInfosAsParcelable = args.getParcelableArray(ChildProcessConnection.EXTRA_FILES);
                        ChildProcessService.this.mFdInfos = new FileDescriptorInfo[fdInfosAsParcelable.length];
                        System.arraycopy(fdInfosAsParcelable, 0, ChildProcessService.this.mFdInfos, 0, fdInfosAsParcelable.length);
                        Bundle sharedRelros = args.getBundle(Linker.EXTRA_LINKER_SHARED_RELROS);
                        if (sharedRelros != null) {
                            Linker.getInstance().useSharedRelros(sharedRelros);
                        }
                        ChildProcessService.this.mMainThread.notifyAll();
                    } else {
                        throw new AssertionError();
                    }
                }
                throw new AssertionError();
            }
            return Process.myPid();
        }

        public void crashIntentionallyForTesting() {
            Process.killProcess(Process.myPid());
        }
    }

    private static native void nativeExitChildProcess();

    private static native void nativeInitChildProcess(Context context, ChildProcessService childProcessService, int i, long j);

    private static native void nativeRegisterGlobalFileDescriptor(int i, int i2, long j, long j2);

    private native void nativeShutdownMainThread();

    public ChildProcessService() {
        this.mLibraryInitialized = false;
        this.mIsBound = false;
        this.mActivitySemaphore = new Semaphore(1);
        this.mBinder = new C06771();
    }

    static {
        sContext = new AtomicReference(null);
    }

    static Context getContext() {
        return (Context) sContext.get();
    }

    public void onCreate() {
        Log.m33i(TAG, "Creating new ChildProcessService pid=%d", Integer.valueOf(Process.myPid()));
        if (sContext.get() != null) {
            throw new RuntimeException("Illegal child process reuse.");
        }
        sContext.set(this);
        super.onCreate();
        this.mMainThread = new Thread(new C03182(), MAIN_THREAD_NAME);
        this.mMainThread.start();
    }

    @SuppressFBWarnings({"DM_EXIT"})
    public void onDestroy() {
        Log.m33i(TAG, "Destroying ChildProcessService pid=%d", Integer.valueOf(Process.myPid()));
        super.onDestroy();
        if (this.mActivitySemaphore.tryAcquire()) {
            System.exit(0);
            return;
        }
        synchronized (this.mMainThread) {
            while (!this.mLibraryInitialized) {
                try {
                    this.mMainThread.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        nativeShutdownMainThread();
    }

    public IBinder onBind(Intent intent) {
        stopSelf();
        synchronized (this.mMainThread) {
            this.mCommandLineParams = intent.getStringArrayExtra(ChildProcessConnection.EXTRA_COMMAND_LINE);
            this.mLinkerParams = new ChromiumLinkerParams(intent);
            this.mIsBound = true;
            this.mMainThread.notifyAll();
        }
        return this.mBinder;
    }

    @CalledByNative
    private void establishSurfaceTexturePeer(int pid, Object surfaceObject, int primaryID, int secondaryID) {
        Surface surface;
        if (this.mCallback == null) {
            Log.m32e(TAG, "No callback interface has been provided.", new Object[0]);
            return;
        }
        boolean needRelease = false;
        if (surfaceObject instanceof Surface) {
            surface = (Surface) surfaceObject;
        } else if (surfaceObject instanceof SurfaceTexture) {
            surface = new Surface((SurfaceTexture) surfaceObject);
            needRelease = true;
        } else {
            Log.m32e(TAG, "Not a valid surfaceObject: %s", surfaceObject);
            return;
        }
        try {
            this.mCallback.establishSurfacePeer(pid, surface, primaryID, secondaryID);
            if (needRelease) {
                surface.release();
            }
        } catch (RemoteException e) {
            Log.m32e(TAG, "Unable to call establishSurfaceTexturePeer: %s", e);
            if (needRelease) {
                surface.release();
            }
        } catch (Throwable th) {
            if (needRelease) {
                surface.release();
            }
        }
    }

    @CalledByNative
    private Surface getViewSurface(int surfaceId) {
        Surface surface = null;
        if (this.mCallback == null) {
            Log.m32e(TAG, "No callback interface has been provided.", new Object[0]);
        } else {
            try {
                surface = this.mCallback.getViewSurface(surfaceId).getSurface();
            } catch (RemoteException e) {
                Log.m32e(TAG, "Unable to call establishSurfaceTexturePeer: %s", e);
            }
        }
        return surface;
    }

    @CalledByNative
    private void createSurfaceTextureSurface(int surfaceTextureId, int clientId, SurfaceTexture surfaceTexture) {
        if (this.mCallback == null) {
            Log.m32e(TAG, "No callback interface has been provided.", new Object[0]);
            return;
        }
        Surface surface = new Surface(surfaceTexture);
        try {
            this.mCallback.registerSurfaceTextureSurface(surfaceTextureId, clientId, surface);
        } catch (RemoteException e) {
            Log.m32e(TAG, "Unable to call registerSurfaceTextureSurface: %s", e);
        }
        surface.release();
    }

    @CalledByNative
    private void destroySurfaceTextureSurface(int surfaceTextureId, int clientId) {
        if (this.mCallback == null) {
            Log.m32e(TAG, "No callback interface has been provided.", new Object[0]);
            return;
        }
        try {
            this.mCallback.unregisterSurfaceTextureSurface(surfaceTextureId, clientId);
        } catch (RemoteException e) {
            Log.m32e(TAG, "Unable to call unregisterSurfaceTextureSurface: %s", e);
        }
    }

    @CalledByNative
    private Surface getSurfaceTextureSurface(int surfaceTextureId) {
        Surface surface = null;
        if (this.mCallback == null) {
            Log.m32e(TAG, "No callback interface has been provided.", new Object[0]);
        } else {
            try {
                surface = this.mCallback.getSurfaceTextureSurface(surfaceTextureId).getSurface();
            } catch (RemoteException e) {
                Log.m32e(TAG, "Unable to call getSurfaceTextureSurface: %s", e);
            }
        }
        return surface;
    }
}
