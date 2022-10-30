package org.chromium.base.library_loader;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.SystemClock;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import org.chromium.base.CalledByNative;
import org.chromium.base.CommandLine;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.base.PackageUtils;
import org.chromium.base.TraceEvent;
import org.chromium.base.metrics.RecordHistogram;

@JNINamespace("base::android")
public class LibraryLoader {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final boolean DEBUG = false;
    private static final String TAG = "cr.library_loader";
    private static volatile LibraryLoader sInstance;
    private static final Object sLock;
    private boolean mCommandLineSwitched;
    private volatile boolean mInitialized;
    private boolean mIsUsingBrowserSharedRelros;
    private long mLibraryLoadTimeMs;
    private final int mLibraryProcessType;
    private boolean mLibraryWasLoadedFromApk;
    private boolean mLoadAtFixedAddressFailed;
    private boolean mLoaded;
    private final AtomicBoolean mPrefetchLibraryHasBeenCalled;

    /* renamed from: org.chromium.base.library_loader.LibraryLoader.1 */
    class C03131 extends AsyncTask<Void, Void, Void> {
        C03131() {
        }

        protected Void doInBackground(Void... params) {
            TraceEvent.begin("LibraryLoader.asyncPrefetchLibrariesToMemory");
            boolean success = LibraryLoader.nativeForkAndPrefetchNativeLibrary();
            if (!success) {
                Log.m42w(LibraryLoader.TAG, "Forking a process to prefetch the native library failed.", new Object[0]);
            }
            RecordHistogram.recordBooleanHistogram("LibraryLoader.PrefetchStatus", success);
            TraceEvent.end("LibraryLoader.asyncPrefetchLibrariesToMemory");
            return null;
        }
    }

    private static native boolean nativeForkAndPrefetchNativeLibrary();

    private native String nativeGetVersionNumber();

    private native void nativeInitCommandLine(String[] strArr);

    private native boolean nativeLibraryLoaded();

    private native void nativeRecordChromiumAndroidLinkerBrowserHistogram(boolean z, boolean z2, int i, long j);

    private native void nativeRegisterChromiumAndroidLinkerRendererHistogram(boolean z, boolean z2, long j);

    static {
        $assertionsDisabled = !LibraryLoader.class.desiredAssertionStatus() ? true : DEBUG;
        sLock = new Object();
    }

    public static LibraryLoader get(int libraryProcessType) throws ProcessInitException {
        LibraryLoader libraryLoader;
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new LibraryLoader(libraryProcessType);
                libraryLoader = sInstance;
            } else if (sInstance.mLibraryProcessType == libraryProcessType) {
                libraryLoader = sInstance;
            } else {
                throw new ProcessInitException(2);
            }
        }
        return libraryLoader;
    }

    private LibraryLoader(int libraryProcessType) {
        this.mLibraryProcessType = libraryProcessType;
        this.mPrefetchLibraryHasBeenCalled = new AtomicBoolean();
    }

    public void ensureInitialized(Context context) throws ProcessInitException {
        synchronized (sLock) {
            if (this.mInitialized) {
                return;
            }
            loadAlreadyLocked(context);
            initializeAlreadyLocked();
        }
    }

    public static boolean isInitialized() {
        return (sInstance == null || !sInstance.mInitialized) ? DEBUG : true;
    }

    public void loadNow(Context context) throws ProcessInitException {
        synchronized (sLock) {
            loadAlreadyLocked(context);
        }
    }

    public void initialize() throws ProcessInitException {
        synchronized (sLock) {
            initializeAlreadyLocked();
        }
    }

    public void asyncPrefetchLibrariesToMemory() {
        if (this.mPrefetchLibraryHasBeenCalled.compareAndSet(DEBUG, true)) {
            new C03131().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    private void loadAlreadyLocked(Context context) throws ProcessInitException {
        try {
            if (!this.mLoaded) {
                if ($assertionsDisabled || !this.mInitialized) {
                    long startTime = SystemClock.uptimeMillis();
                    Linker linker = Linker.getInstance();
                    if (linker.isUsed()) {
                        String apkFilePath = getLibraryApkPath(context);
                        linker.prepareLibraryLoad();
                        for (String library : NativeLibraries.LIBRARIES) {
                            if (!linker.isChromiumLinkerLibrary(library)) {
                                String zipFilePath = null;
                                String libFilePath = System.mapLibraryName(library);
                                if (linker.isInZipFile()) {
                                    zipFilePath = apkFilePath;
                                    Log.m33i(TAG, "Loading " + library + " directly from within " + apkFilePath, new Object[0]);
                                } else {
                                    Log.m33i(TAG, "Loading " + library, new Object[0]);
                                }
                                boolean isLoaded = DEBUG;
                                if (linker.isUsingBrowserSharedRelros()) {
                                    this.mIsUsingBrowserSharedRelros = true;
                                    try {
                                        loadLibrary(zipFilePath, libFilePath);
                                        isLoaded = true;
                                    } catch (UnsatisfiedLinkError e) {
                                        Log.m42w(TAG, "Failed to load native library with shared RELRO, retrying without", new Object[0]);
                                        linker.disableSharedRelros();
                                        this.mLoadAtFixedAddressFailed = true;
                                    }
                                }
                                if (isLoaded) {
                                    continue;
                                } else {
                                    loadLibrary(zipFilePath, libFilePath);
                                }
                            }
                        }
                        linker.finishLibraryLoad();
                    } else {
                        for (String library2 : NativeLibraries.LIBRARIES) {
                            System.loadLibrary(library2);
                        }
                    }
                    long stopTime = SystemClock.uptimeMillis();
                    this.mLibraryLoadTimeMs = stopTime - startTime;
                    String str = TAG;
                    r21 = new Object[3];
                    r21[0] = Long.valueOf(this.mLibraryLoadTimeMs);
                    r21[1] = Long.valueOf(startTime % 10000);
                    r21[2] = Long.valueOf(stopTime % 10000);
                    Log.m33i(str, String.format("Time to load native libraries: %d ms (timestamps %d-%d)", r21), new Object[0]);
                    this.mLoaded = true;
                } else {
                    throw new AssertionError();
                }
            }
            Log.m33i(TAG, String.format("Expected native library version number \"%s\", actual native library version number \"%s\"", new Object[]{NativeLibraries.sVersionNumber, nativeGetVersionNumber()}), new Object[0]);
            if (!NativeLibraries.sVersionNumber.equals(nativeGetVersionNumber())) {
                throw new ProcessInitException(3);
            }
        } catch (UnsatisfiedLinkError e2) {
            throw new ProcessInitException(2, e2);
        }
    }

    private static boolean isAbiSplit(String splitName) {
        return splitName.startsWith("abi_");
    }

    @TargetApi(21)
    private static String getLibraryApkPath(Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        if (VERSION.SDK_INT < 21) {
            return appInfo.sourceDir;
        }
        PackageInfo packageInfo = PackageUtils.getOwnPackageInfo(context);
        if (packageInfo.splitNames != null) {
            for (int i = 0; i < packageInfo.splitNames.length; i++) {
                if (isAbiSplit(packageInfo.splitNames[i])) {
                    return appInfo.splitSourceDirs[i];
                }
            }
        }
        return appInfo.sourceDir;
    }

    private void loadLibrary(@Nullable String zipFilePath, String libFilePath) {
        Linker.getInstance().loadLibrary(zipFilePath, libFilePath);
        if (zipFilePath != null) {
            this.mLibraryWasLoadedFromApk = true;
        }
    }

    public void switchCommandLineForWebView() {
        synchronized (sLock) {
            ensureCommandLineSwitchedAlreadyLocked();
        }
    }

    private void ensureCommandLineSwitchedAlreadyLocked() {
        if (!$assertionsDisabled && !this.mLoaded) {
            throw new AssertionError();
        } else if (!this.mCommandLineSwitched) {
            nativeInitCommandLine(CommandLine.getJavaSwitchesOrNull());
            CommandLine.enableNativeProxy();
            this.mCommandLineSwitched = true;
        }
    }

    private void initializeAlreadyLocked() throws ProcessInitException {
        if (!this.mInitialized) {
            if (!this.mCommandLineSwitched) {
                nativeInitCommandLine(CommandLine.getJavaSwitchesOrNull());
            }
            if (nativeLibraryLoaded()) {
                if (!this.mCommandLineSwitched) {
                    CommandLine.enableNativeProxy();
                    this.mCommandLineSwitched = true;
                }
                TraceEvent.registerNativeEnabledObserver();
                this.mInitialized = true;
                return;
            }
            Log.m32e(TAG, "error calling nativeLibraryLoaded", new Object[0]);
            throw new ProcessInitException(1);
        }
    }

    public void onNativeInitializationComplete(Context context) {
        recordBrowserProcessHistogram(context);
    }

    private void recordBrowserProcessHistogram(Context context) {
        if (Linker.getInstance().isUsed()) {
            nativeRecordChromiumAndroidLinkerBrowserHistogram(this.mIsUsingBrowserSharedRelros, this.mLoadAtFixedAddressFailed, getLibraryLoadFromApkStatus(context), this.mLibraryLoadTimeMs);
        }
    }

    private int getLibraryLoadFromApkStatus(Context context) {
        if (!$assertionsDisabled && !Linker.getInstance().isUsed()) {
            throw new AssertionError();
        } else if (this.mLibraryWasLoadedFromApk) {
            return 3;
        } else {
            return 0;
        }
    }

    public void registerRendererProcessHistogram(boolean requestedSharedRelro, boolean loadAtFixedAddressFailed) {
        if (Linker.getInstance().isUsed()) {
            nativeRegisterChromiumAndroidLinkerRendererHistogram(requestedSharedRelro, loadAtFixedAddressFailed, this.mLibraryLoadTimeMs);
        }
    }

    @CalledByNative
    public static int getLibraryProcessType() {
        if (sInstance == null) {
            return 0;
        }
        return sInstance.mLibraryProcessType;
    }
}
