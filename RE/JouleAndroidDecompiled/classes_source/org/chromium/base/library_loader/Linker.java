package org.chromium.base.library_loader;

import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.chromium.base.Log;
import org.chromium.base.annotations.AccessedByNative;

public abstract class Linker {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int BROWSER_SHARED_RELRO_CONFIG = 1;
    public static final int BROWSER_SHARED_RELRO_CONFIG_ALWAYS = 2;
    public static final int BROWSER_SHARED_RELRO_CONFIG_LOW_RAM_ONLY = 1;
    public static final int BROWSER_SHARED_RELRO_CONFIG_NEVER = 0;
    protected static final boolean DEBUG = false;
    public static final String EXTRA_LINKER_SHARED_RELROS = "org.chromium.base.android.linker.shared_relros";
    public static final int MEMORY_DEVICE_CONFIG_INIT = 0;
    public static final int MEMORY_DEVICE_CONFIG_LOW = 1;
    public static final int MEMORY_DEVICE_CONFIG_NORMAL = 2;
    private static final String TAG = "cr.library_loader";
    private static Linker sSingleton;
    private static Object sSingletonLock;
    protected final Object mLock;
    protected int mMemoryDeviceConfig;
    String mTestRunnerClassName;

    public static class LibInfo implements Parcelable {
        public static final Creator<LibInfo> CREATOR;
        @AccessedByNative
        public long mLoadAddress;
        @AccessedByNative
        public long mLoadSize;
        @AccessedByNative
        public int mRelroFd;
        @AccessedByNative
        public long mRelroSize;
        @AccessedByNative
        public long mRelroStart;

        /* renamed from: org.chromium.base.library_loader.Linker.LibInfo.1 */
        static class C03141 implements Creator<LibInfo> {
            C03141() {
            }

            public LibInfo createFromParcel(Parcel in) {
                return new LibInfo(in);
            }

            public LibInfo[] newArray(int size) {
                return new LibInfo[size];
            }
        }

        public LibInfo() {
            this.mLoadAddress = 0;
            this.mLoadSize = 0;
            this.mRelroStart = 0;
            this.mRelroSize = 0;
            this.mRelroFd = -1;
        }

        public void close() {
            if (this.mRelroFd >= 0) {
                try {
                    ParcelFileDescriptor.adoptFd(this.mRelroFd).close();
                } catch (IOException e) {
                }
                this.mRelroFd = -1;
            }
        }

        public LibInfo(Parcel in) {
            this.mLoadAddress = in.readLong();
            this.mLoadSize = in.readLong();
            this.mRelroStart = in.readLong();
            this.mRelroSize = in.readLong();
            ParcelFileDescriptor fd = (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(in);
            this.mRelroFd = fd == null ? -1 : fd.detachFd();
        }

        public void writeToParcel(Parcel out, int flags) {
            if (this.mRelroFd >= 0) {
                out.writeLong(this.mLoadAddress);
                out.writeLong(this.mLoadSize);
                out.writeLong(this.mRelroStart);
                out.writeLong(this.mRelroSize);
                try {
                    ParcelFileDescriptor fd = ParcelFileDescriptor.fromFd(this.mRelroFd);
                    fd.writeToParcel(out, Linker.MEMORY_DEVICE_CONFIG_INIT);
                    fd.close();
                } catch (IOException e) {
                    Object[] objArr = new Object[Linker.MEMORY_DEVICE_CONFIG_LOW];
                    objArr[Linker.MEMORY_DEVICE_CONFIG_INIT] = e;
                    Log.m32e(Linker.TAG, "Cant' write LibInfo file descriptor to parcel", objArr);
                }
            }
        }

        public int describeContents() {
            return Linker.MEMORY_DEVICE_CONFIG_LOW;
        }

        static {
            CREATOR = new C03141();
        }

        public String toString() {
            return String.format(Locale.US, "[load=0x%x-0x%x relro=0x%x-0x%x fd=%d]", new Object[]{Long.valueOf(this.mLoadAddress), Long.valueOf(this.mLoadAddress + this.mLoadSize), Long.valueOf(this.mRelroStart), Long.valueOf(this.mRelroStart + this.mRelroSize), Integer.valueOf(this.mRelroFd)});
        }
    }

    public interface TestRunner {
        boolean runChecks(int i, boolean z);
    }

    public abstract void disableSharedRelros();

    public abstract void finishLibraryLoad();

    public abstract long getBaseLoadAddress();

    public abstract Bundle getSharedRelros();

    public abstract void initServiceProcess(long j);

    public abstract boolean isChromiumLinkerLibrary(String str);

    public abstract boolean isInZipFile();

    public abstract boolean isUsed();

    public abstract boolean isUsingBrowserSharedRelros();

    public abstract void loadLibrary(@Nullable String str, String str2);

    public abstract void prepareLibraryLoad();

    public abstract void useSharedRelros(Bundle bundle);

    static {
        boolean z;
        if (Linker.class.desiredAssertionStatus()) {
            z = DEBUG;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        sSingleton = null;
        sSingletonLock = new Object();
    }

    protected Linker() {
        this.mLock = new Object();
        this.mMemoryDeviceConfig = MEMORY_DEVICE_CONFIG_INIT;
        this.mTestRunnerClassName = null;
    }

    public static final Linker getInstance() {
        Linker linker;
        synchronized (sSingletonLock) {
            if (sSingleton == null) {
                sSingleton = new LegacyLinker();
            }
            linker = sSingleton;
        }
        return linker;
    }

    public void setTestRunnerClassName(String testRunnerClassName) {
        if (NativeLibraries.sEnableLinkerTests) {
            synchronized (this.mLock) {
                if ($assertionsDisabled || this.mTestRunnerClassName == null) {
                    this.mTestRunnerClassName = testRunnerClassName;
                } else {
                    throw new AssertionError();
                }
            }
        }
    }

    public String getTestRunnerClassName() {
        String str;
        synchronized (this.mLock) {
            str = this.mTestRunnerClassName;
        }
        return str;
    }

    public void setMemoryDeviceConfig(int memoryDeviceConfig) {
        if ($assertionsDisabled || NativeLibraries.sEnableLinkerTests) {
            synchronized (this.mLock) {
                if (!$assertionsDisabled && this.mMemoryDeviceConfig != 0) {
                    throw new AssertionError();
                } else if ($assertionsDisabled || memoryDeviceConfig == MEMORY_DEVICE_CONFIG_LOW || memoryDeviceConfig == MEMORY_DEVICE_CONFIG_NORMAL) {
                    this.mMemoryDeviceConfig = memoryDeviceConfig;
                } else {
                    throw new AssertionError();
                }
            }
            return;
        }
        throw new AssertionError();
    }

    protected Bundle createBundleFromLibInfoMap(HashMap<String, LibInfo> map) {
        Bundle bundle = new Bundle(map.size());
        for (Entry<String, LibInfo> entry : map.entrySet()) {
            bundle.putParcelable((String) entry.getKey(), (Parcelable) entry.getValue());
        }
        return bundle;
    }

    protected HashMap<String, LibInfo> createLibInfoMapFromBundle(Bundle bundle) {
        HashMap<String, LibInfo> map = new HashMap();
        for (String library : bundle.keySet()) {
            map.put(library, (LibInfo) bundle.getParcelable(library));
        }
        return map;
    }

    protected void closeLibInfoMap(HashMap<String, LibInfo> map) {
        for (Entry<String, LibInfo> entry : map.entrySet()) {
            ((LibInfo) entry.getValue()).close();
        }
    }
}
