package org.chromium.base.library_loader;

import android.os.Bundle;
import android.os.Parcel;
import com.google.android.gms.common.ConnectionResult;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.CalledByNative;
import org.chromium.base.Log;
import org.chromium.base.SysUtils;
import org.chromium.base.ThreadUtils;
import org.chromium.base.library_loader.Linker.LibInfo;
import org.chromium.base.library_loader.Linker.TestRunner;
import org.chromium.content.browser.ContentViewCore;

class LegacyLinker extends Linker {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String LINKER_JNI_LIBRARY = "chromium_android_linker";
    private static final String TAG = "cr.library_loader";
    private long mBaseLoadAddress;
    private boolean mBrowserUsesSharedRelro;
    private long mCurrentLoadAddress;
    private boolean mInBrowserProcess;
    private boolean mInitialized;
    protected HashMap<String, LibInfo> mLoadedLibraries;
    private boolean mPrepareLibraryLoadCalled;
    private boolean mRelroSharingSupported;
    private Bundle mSharedRelros;
    private boolean mWaitForSharedRelros;

    /* renamed from: org.chromium.base.library_loader.LegacyLinker.1 */
    static class C03121 implements Runnable {
        final /* synthetic */ long val$opaque;

        C03121(long j) {
            this.val$opaque = j;
        }

        public void run() {
            LegacyLinker.nativeRunCallbackOnUiThread(this.val$opaque);
        }
    }

    private static native boolean nativeCanUseSharedRelro();

    private static native boolean nativeCreateSharedRelro(String str, long j, LibInfo libInfo);

    private static native long nativeGetRandomBaseLoadAddress(long j);

    private static native boolean nativeLoadLibrary(String str, long j, LibInfo libInfo);

    private static native boolean nativeLoadLibraryInZipFile(String str, String str2, long j, LibInfo libInfo);

    private static native void nativeRunCallbackOnUiThread(long j);

    private static native boolean nativeUseSharedRelro(String str, LibInfo libInfo);

    static {
        $assertionsDisabled = !LegacyLinker.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    LegacyLinker() {
        this.mInitialized = $assertionsDisabled;
        this.mRelroSharingSupported = $assertionsDisabled;
        this.mInBrowserProcess = true;
        this.mWaitForSharedRelros = $assertionsDisabled;
        this.mBrowserUsesSharedRelro = $assertionsDisabled;
        this.mSharedRelros = null;
        this.mBaseLoadAddress = 0;
        this.mCurrentLoadAddress = 0;
        this.mPrepareLibraryLoadCalled = $assertionsDisabled;
        this.mLoadedLibraries = null;
    }

    private void ensureInitializedLocked() {
        if (!$assertionsDisabled && !Thread.holdsLock(this.mLock)) {
            throw new AssertionError();
        } else if (!this.mInitialized) {
            this.mRelroSharingSupported = $assertionsDisabled;
            if (NativeLibraries.sUseLinker) {
                try {
                    System.loadLibrary(LINKER_JNI_LIBRARY);
                } catch (UnsatisfiedLinkError e) {
                    Log.m42w(TAG, "Couldn't load libchromium_android_linker.so, trying libchromium_android_linker.cr.so", new Object[0]);
                    System.loadLibrary("chromium_android_linker.cr");
                }
                this.mRelroSharingSupported = nativeCanUseSharedRelro();
                if (!this.mRelroSharingSupported) {
                    Log.m42w(TAG, "This system cannot safely share RELRO sections", new Object[0]);
                }
                if (this.mMemoryDeviceConfig == 0) {
                    if (SysUtils.isLowEndDevice()) {
                        this.mMemoryDeviceConfig = 1;
                    } else {
                        this.mMemoryDeviceConfig = 2;
                    }
                }
                switch (1) {
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        this.mBrowserUsesSharedRelro = $assertionsDisabled;
                        break;
                    case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                        if (this.mMemoryDeviceConfig != 1) {
                            this.mBrowserUsesSharedRelro = $assertionsDisabled;
                            break;
                        }
                        this.mBrowserUsesSharedRelro = true;
                        Log.m42w(TAG, "Low-memory device: shared RELROs used in all processes", new Object[0]);
                        break;
                    case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                        Log.m42w(TAG, "Beware: shared RELROs used in all processes!", new Object[0]);
                        this.mBrowserUsesSharedRelro = true;
                        break;
                    default:
                        if (!$assertionsDisabled) {
                            throw new AssertionError("Unreached");
                        }
                        break;
                }
            }
            if (!this.mRelroSharingSupported) {
                this.mBrowserUsesSharedRelro = $assertionsDisabled;
                this.mWaitForSharedRelros = $assertionsDisabled;
            }
            this.mInitialized = true;
        }
    }

    public boolean isUsed() {
        if (!NativeLibraries.sUseLinker) {
            return $assertionsDisabled;
        }
        boolean z;
        synchronized (this.mLock) {
            ensureInitializedLocked();
            z = this.mRelroSharingSupported;
        }
        return z;
    }

    public boolean isUsingBrowserSharedRelros() {
        boolean z;
        synchronized (this.mLock) {
            ensureInitializedLocked();
            z = this.mBrowserUsesSharedRelro;
        }
        return z;
    }

    public boolean isInZipFile() {
        return NativeLibraries.sUseLibraryInZipFile;
    }

    public void prepareLibraryLoad() {
        synchronized (this.mLock) {
            this.mPrepareLibraryLoadCalled = true;
            if (this.mInBrowserProcess) {
                setupBaseLoadAddressLocked();
            }
        }
    }

    public void finishLibraryLoad() {
        synchronized (this.mLock) {
            if (this.mLoadedLibraries != null) {
                if (this.mInBrowserProcess) {
                    this.mSharedRelros = createBundleFromLibInfoMap(this.mLoadedLibraries);
                    if (this.mBrowserUsesSharedRelro) {
                        useSharedRelrosLocked(this.mSharedRelros);
                    }
                }
                if (this.mWaitForSharedRelros) {
                    if ($assertionsDisabled || !this.mInBrowserProcess) {
                        while (this.mSharedRelros == null) {
                            try {
                                this.mLock.wait();
                            } catch (InterruptedException e) {
                            }
                        }
                        useSharedRelrosLocked(this.mSharedRelros);
                        this.mSharedRelros.clear();
                        this.mSharedRelros = null;
                    } else {
                        throw new AssertionError();
                    }
                }
            }
            if (NativeLibraries.sEnableLinkerTests && this.mTestRunnerClassName != null) {
                TestRunner testRunner;
                try {
                    testRunner = (TestRunner) Class.forName(this.mTestRunnerClassName).newInstance();
                } catch (Exception e2) {
                    Log.m32e(TAG, "Could not extract test runner class name", e2);
                    testRunner = null;
                }
                if (testRunner != null) {
                    if (testRunner.runChecks(this.mMemoryDeviceConfig, this.mInBrowserProcess)) {
                        Log.m33i(TAG, "All linker tests passed!", new Object[0]);
                    } else {
                        Log.wtf(TAG, "Linker runtime tests failed in this process!!", new Object[0]);
                        if (!$assertionsDisabled) {
                            throw new AssertionError();
                        }
                    }
                }
            }
        }
    }

    public void useSharedRelros(Bundle bundle) {
        Bundle clonedBundle = null;
        if (bundle != null) {
            bundle.setClassLoader(LibInfo.class.getClassLoader());
            clonedBundle = new Bundle(LibInfo.class.getClassLoader());
            Parcel parcel = Parcel.obtain();
            bundle.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            clonedBundle.readFromParcel(parcel);
            parcel.recycle();
        }
        synchronized (this.mLock) {
            this.mSharedRelros = clonedBundle;
            this.mLock.notifyAll();
        }
    }

    public Bundle getSharedRelros() {
        Bundle bundle;
        synchronized (this.mLock) {
            if (this.mInBrowserProcess) {
                bundle = this.mSharedRelros;
            } else {
                bundle = null;
            }
        }
        return bundle;
    }

    public void disableSharedRelros() {
        synchronized (this.mLock) {
            this.mInBrowserProcess = $assertionsDisabled;
            this.mWaitForSharedRelros = $assertionsDisabled;
            this.mBrowserUsesSharedRelro = $assertionsDisabled;
        }
    }

    public void initServiceProcess(long baseLoadAddress) {
        synchronized (this.mLock) {
            ensureInitializedLocked();
            this.mInBrowserProcess = $assertionsDisabled;
            this.mBrowserUsesSharedRelro = $assertionsDisabled;
            if (this.mRelroSharingSupported) {
                this.mWaitForSharedRelros = true;
                this.mBaseLoadAddress = baseLoadAddress;
                this.mCurrentLoadAddress = baseLoadAddress;
            }
        }
    }

    public long getBaseLoadAddress() {
        long j;
        synchronized (this.mLock) {
            ensureInitializedLocked();
            if (this.mInBrowserProcess) {
                setupBaseLoadAddressLocked();
                j = this.mBaseLoadAddress;
            } else {
                Log.m42w(TAG, "Shared RELRO sections are disabled in this process!", new Object[0]);
                j = 0;
            }
        }
        return j;
    }

    private void setupBaseLoadAddressLocked() {
        if (!$assertionsDisabled && !Thread.holdsLock(this.mLock)) {
            throw new AssertionError();
        } else if (this.mBaseLoadAddress == 0) {
            long address = computeRandomBaseLoadAddress();
            this.mBaseLoadAddress = address;
            this.mCurrentLoadAddress = address;
            if (address == 0) {
                Log.m42w(TAG, "Disabling shared RELROs due address space pressure", new Object[0]);
                this.mBrowserUsesSharedRelro = $assertionsDisabled;
                this.mWaitForSharedRelros = $assertionsDisabled;
            }
        }
    }

    private long computeRandomBaseLoadAddress() {
        return nativeGetRandomBaseLoadAddress(201326592);
    }

    private void dumpBundle(Bundle bundle) {
    }

    private void useSharedRelrosLocked(Bundle bundle) {
        if (!$assertionsDisabled && !Thread.holdsLock(this.mLock)) {
            throw new AssertionError();
        } else if (bundle != null && this.mRelroSharingSupported && this.mLoadedLibraries != null) {
            HashMap<String, LibInfo> relroMap = createLibInfoMapFromBundle(bundle);
            for (Entry<String, LibInfo> entry : relroMap.entrySet()) {
                String libName = (String) entry.getKey();
                if (!nativeUseSharedRelro(libName, (LibInfo) entry.getValue())) {
                    Log.m42w(TAG, "Could not use shared RELRO section for " + libName, new Object[0]);
                }
            }
            if (!this.mInBrowserProcess) {
                closeLibInfoMap(relroMap);
            }
        }
    }

    public void loadLibrary(@Nullable String zipFilePath, String libFilePath) {
        synchronized (this.mLock) {
            ensureInitializedLocked();
            if ($assertionsDisabled || this.mPrepareLibraryLoadCalled) {
                if (this.mLoadedLibraries == null) {
                    this.mLoadedLibraries = new HashMap();
                }
                if (this.mLoadedLibraries.containsKey(libFilePath)) {
                    return;
                }
                LibInfo libInfo = new LibInfo();
                long loadAddress = 0;
                if ((this.mInBrowserProcess && this.mBrowserUsesSharedRelro) || this.mWaitForSharedRelros) {
                    loadAddress = this.mCurrentLoadAddress;
                }
                String sharedRelRoName = libFilePath;
                String errorMessage;
                if (zipFilePath != null) {
                    if (nativeLoadLibraryInZipFile(zipFilePath, libFilePath, loadAddress, libInfo)) {
                        sharedRelRoName = zipFilePath;
                    } else {
                        errorMessage = "Unable to load library: " + libFilePath + ", in: " + zipFilePath;
                        Log.m32e(TAG, errorMessage, new Object[0]);
                        throw new UnsatisfiedLinkError(errorMessage);
                    }
                } else if (!nativeLoadLibrary(libFilePath, loadAddress, libInfo)) {
                    errorMessage = "Unable to load library: " + libFilePath;
                    Log.m32e(TAG, errorMessage, new Object[0]);
                    throw new UnsatisfiedLinkError(errorMessage);
                }
                if (NativeLibraries.sEnableLinkerTests) {
                    String str = TAG;
                    Locale locale = Locale.US;
                    String str2 = "%s_LIBRARY_ADDRESS: %s %x";
                    Object[] objArr = new Object[3];
                    objArr[0] = this.mInBrowserProcess ? "BROWSER" : "RENDERER";
                    objArr[1] = libFilePath;
                    objArr[2] = Long.valueOf(libInfo.mLoadAddress);
                    Log.m33i(str, String.format(locale, str2, objArr), new Object[0]);
                }
                if (this.mInBrowserProcess && !nativeCreateSharedRelro(sharedRelRoName, this.mCurrentLoadAddress, libInfo)) {
                    Log.m42w(TAG, String.format(Locale.US, "Could not create shared RELRO for %s at %x", new Object[]{libFilePath, Long.valueOf(this.mCurrentLoadAddress)}), new Object[0]);
                }
                if (this.mCurrentLoadAddress != 0) {
                    this.mCurrentLoadAddress = libInfo.mLoadAddress + libInfo.mLoadSize;
                }
                this.mLoadedLibraries.put(sharedRelRoName, libInfo);
                return;
            }
            throw new AssertionError();
        }
    }

    public boolean isChromiumLinkerLibrary(String library) {
        return (library.equals(LINKER_JNI_LIBRARY) || library.equals("chromium_android_linker.cr")) ? true : $assertionsDisabled;
    }

    @CalledByNative
    public static void postCallbackOnMainThread(long opaque) {
        ThreadUtils.postOnUiThread(new C03121(opaque));
    }
}
