package org.chromium.content.browser;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import java.io.IOException;
import org.chromium.base.CpuFeatures;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.TraceEvent;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.library_loader.Linker;
import org.chromium.content.app.ChildProcessService;
import org.chromium.content.app.ChromiumLinkerParams;
import org.chromium.content.browser.ChildProcessConnection.ConnectionCallback;
import org.chromium.content.browser.ChildProcessConnection.DeathCallback;
import org.chromium.content.common.IChildProcessCallback;
import org.chromium.content.common.IChildProcessService;
import org.chromium.content.common.IChildProcessService.Stub;

public class ChildProcessConnectionImpl implements ChildProcessConnection {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String TAG = "cr.ChildProcessConnect";
    private final boolean mAlwaysInForeground;
    private ConnectionCallback mConnectionCallback;
    private ConnectionParams mConnectionParams;
    private final Context mContext;
    private final DeathCallback mDeathCallback;
    private final boolean mInSandbox;
    private ChildServiceConnection mInitialBinding;
    private ChromiumLinkerParams mLinkerParams;
    private final Object mLock;
    private ChildServiceConnection mModerateBinding;
    private int mPid;
    private IChildProcessService mService;
    private final Class<? extends ChildProcessService> mServiceClass;
    private boolean mServiceConnectComplete;
    private boolean mServiceDisconnected;
    private final int mServiceNumber;
    private ChildServiceConnection mStrongBinding;
    private int mStrongBindingCount;
    private ChildServiceConnection mWaivedBinding;
    private boolean mWasOomProtected;

    private class ChildServiceConnection implements ServiceConnection {
        private final int mBindFlags;
        private boolean mBound;

        private Intent createServiceBindIntent() {
            Intent intent = new Intent();
            intent.setClassName(ChildProcessConnectionImpl.this.mContext, ChildProcessConnectionImpl.this.mServiceClass.getName() + ChildProcessConnectionImpl.this.mServiceNumber);
            intent.setPackage(ChildProcessConnectionImpl.this.mContext.getPackageName());
            return intent;
        }

        public ChildServiceConnection(int bindFlags) {
            this.mBound = ChildProcessConnectionImpl.$assertionsDisabled;
            this.mBindFlags = bindFlags;
        }

        boolean bind(String[] commandLine) {
            if (!this.mBound) {
                try {
                    TraceEvent.begin("ChildProcessConnectionImpl.ChildServiceConnection.bind");
                    Intent intent = createServiceBindIntent();
                    if (commandLine != null) {
                        intent.putExtra(ChildProcessConnection.EXTRA_COMMAND_LINE, commandLine);
                    }
                    if (ChildProcessConnectionImpl.this.mLinkerParams != null) {
                        ChildProcessConnectionImpl.this.mLinkerParams.addIntentExtras(intent);
                    }
                    this.mBound = ChildProcessConnectionImpl.this.mContext.bindService(intent, this, this.mBindFlags);
                } finally {
                    TraceEvent.end("ChildProcessConnectionImpl.ChildServiceConnection.bind");
                }
            }
            return this.mBound;
        }

        void unbind() {
            if (this.mBound) {
                ChildProcessConnectionImpl.this.mContext.unbindService(this);
                this.mBound = ChildProcessConnectionImpl.$assertionsDisabled;
            }
        }

        boolean isBound() {
            return this.mBound;
        }

        public void onServiceConnected(ComponentName className, IBinder service) {
            synchronized (ChildProcessConnectionImpl.this.mLock) {
                if (ChildProcessConnectionImpl.this.mServiceConnectComplete) {
                    return;
                }
                try {
                    TraceEvent.begin("ChildProcessConnectionImpl.ChildServiceConnection.onServiceConnected");
                    ChildProcessConnectionImpl.this.mServiceConnectComplete = true;
                    ChildProcessConnectionImpl.this.mService = Stub.asInterface(service);
                    if (ChildProcessConnectionImpl.this.mConnectionParams != null) {
                        ChildProcessConnectionImpl.this.doConnectionSetupLocked();
                    }
                } finally {
                    TraceEvent.end("ChildProcessConnectionImpl.ChildServiceConnection.onServiceConnected");
                }
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            synchronized (ChildProcessConnectionImpl.this.mLock) {
                if (ChildProcessConnectionImpl.this.mServiceDisconnected) {
                    return;
                }
                ChildProcessConnectionImpl.this.mWasOomProtected = ChildProcessConnectionImpl.this.isCurrentlyOomProtected();
                ChildProcessConnectionImpl.this.mServiceDisconnected = true;
                Log.m42w(ChildProcessConnectionImpl.TAG, "onServiceDisconnected (crash or killed by oom): pid=%d", Integer.valueOf(ChildProcessConnectionImpl.this.mPid));
                ChildProcessConnectionImpl.this.stop();
                ChildProcessConnectionImpl.this.mDeathCallback.onChildProcessDied(ChildProcessConnectionImpl.this);
                if (ChildProcessConnectionImpl.this.mConnectionCallback != null) {
                    ChildProcessConnectionImpl.this.mConnectionCallback.onConnected(0);
                }
                ChildProcessConnectionImpl.this.mConnectionCallback = null;
            }
        }
    }

    private static class ConnectionParams {
        final IChildProcessCallback mCallback;
        final String[] mCommandLine;
        final FileDescriptorInfo[] mFilesToBeMapped;
        final Bundle mSharedRelros;

        ConnectionParams(String[] commandLine, FileDescriptorInfo[] filesToBeMapped, IChildProcessCallback callback, Bundle sharedRelros) {
            this.mCommandLine = commandLine;
            this.mFilesToBeMapped = filesToBeMapped;
            this.mCallback = callback;
            this.mSharedRelros = sharedRelros;
        }
    }

    static {
        $assertionsDisabled = !ChildProcessConnectionImpl.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    ChildProcessConnectionImpl(Context context, int number, boolean inSandbox, DeathCallback deathCallback, Class<? extends ChildProcessService> serviceClass, ChromiumLinkerParams chromiumLinkerParams, boolean alwaysInForeground) {
        this.mLock = new Object();
        this.mService = null;
        this.mServiceConnectComplete = $assertionsDisabled;
        this.mServiceDisconnected = $assertionsDisabled;
        this.mWasOomProtected = $assertionsDisabled;
        this.mPid = 0;
        this.mInitialBinding = null;
        this.mStrongBinding = null;
        this.mWaivedBinding = null;
        this.mStrongBindingCount = 0;
        this.mModerateBinding = null;
        this.mLinkerParams = null;
        this.mContext = context;
        this.mServiceNumber = number;
        this.mInSandbox = inSandbox;
        this.mDeathCallback = deathCallback;
        this.mServiceClass = serviceClass;
        this.mLinkerParams = chromiumLinkerParams;
        this.mAlwaysInForeground = alwaysInForeground;
        int initialFlags = 1;
        if (this.mAlwaysInForeground) {
            initialFlags = 1 | 64;
        }
        this.mInitialBinding = new ChildServiceConnection(initialFlags);
        this.mStrongBinding = new ChildServiceConnection(65);
        this.mWaivedBinding = new ChildServiceConnection(33);
        this.mModerateBinding = new ChildServiceConnection(1);
    }

    public int getServiceNumber() {
        return this.mServiceNumber;
    }

    public boolean isInSandbox() {
        return this.mInSandbox;
    }

    public IChildProcessService getService() {
        IChildProcessService iChildProcessService;
        synchronized (this.mLock) {
            iChildProcessService = this.mService;
        }
        return iChildProcessService;
    }

    public int getPid() {
        int i;
        synchronized (this.mLock) {
            i = this.mPid;
        }
        return i;
    }

    public void start(String[] commandLine) {
        try {
            TraceEvent.begin("ChildProcessConnectionImpl.start");
            synchronized (this.mLock) {
                if (!$assertionsDisabled && ThreadUtils.runningOnUiThread()) {
                    throw new AssertionError();
                } else if ($assertionsDisabled || this.mConnectionParams == null) {
                    if (this.mInitialBinding.bind(commandLine)) {
                        this.mWaivedBinding.bind(null);
                    } else {
                        Log.m32e(TAG, "Failed to establish the service connection.", new Object[0]);
                        this.mDeathCallback.onChildProcessDied(this);
                    }
                } else {
                    throw new AssertionError("setupConnection() called before start() in ChildProcessConnectionImpl.");
                }
            }
        } finally {
            TraceEvent.end("ChildProcessConnectionImpl.start");
        }
    }

    public void setupConnection(String[] commandLine, FileDescriptorInfo[] filesToBeMapped, IChildProcessCallback processCallback, ConnectionCallback connectionCallback, Bundle sharedRelros) {
        synchronized (this.mLock) {
            if (!$assertionsDisabled && this.mConnectionParams != null) {
                throw new AssertionError();
            } else if (this.mServiceDisconnected) {
                Log.m42w(TAG, "Tried to setup a connection that already disconnected.", new Object[0]);
                connectionCallback.onConnected(0);
            } else {
                try {
                    TraceEvent.begin("ChildProcessConnectionImpl.setupConnection");
                    this.mConnectionCallback = connectionCallback;
                    this.mConnectionParams = new ConnectionParams(commandLine, filesToBeMapped, processCallback, sharedRelros);
                    if (this.mServiceConnectComplete) {
                        doConnectionSetupLocked();
                    }
                } finally {
                    TraceEvent.end("ChildProcessConnectionImpl.setupConnection");
                }
            }
        }
    }

    public void stop() {
        synchronized (this.mLock) {
            this.mInitialBinding.unbind();
            this.mStrongBinding.unbind();
            this.mWaivedBinding.unbind();
            this.mModerateBinding.unbind();
            this.mStrongBindingCount = 0;
            if (this.mService != null) {
                this.mService = null;
            }
            this.mConnectionParams = null;
        }
    }

    private void doConnectionSetupLocked() {
        try {
            TraceEvent.begin("ChildProcessConnectionImpl.doConnectionSetupLocked");
            if (!$assertionsDisabled && (!this.mServiceConnectComplete || this.mService == null)) {
                throw new AssertionError();
            } else if ($assertionsDisabled || this.mConnectionParams != null) {
                Bundle bundle = new Bundle();
                bundle.putStringArray(ChildProcessConnection.EXTRA_COMMAND_LINE, this.mConnectionParams.mCommandLine);
                bundle.putParcelableArray(ChildProcessConnection.EXTRA_FILES, this.mConnectionParams.mFilesToBeMapped);
                bundle.putInt(ChildProcessConnection.EXTRA_CPU_COUNT, CpuFeatures.getCount());
                bundle.putLong(ChildProcessConnection.EXTRA_CPU_FEATURES, CpuFeatures.getMask());
                bundle.putBundle(Linker.EXTRA_LINKER_SHARED_RELROS, this.mConnectionParams.mSharedRelros);
                this.mPid = this.mService.setupConnection(bundle, this.mConnectionParams.mCallback);
                if (!$assertionsDisabled && this.mPid == 0) {
                    throw new AssertionError("Child service claims to be run by a process of pid=0.");
                }
                try {
                    for (FileDescriptorInfo fileInfo : this.mConnectionParams.mFilesToBeMapped) {
                        fileInfo.mFd.close();
                    }
                } catch (IOException ioe) {
                    Log.m42w(TAG, "Failed to close FD.", ioe);
                }
                this.mConnectionParams = null;
                if (this.mConnectionCallback != null) {
                    this.mConnectionCallback.onConnected(this.mPid);
                }
                this.mConnectionCallback = null;
                TraceEvent.end("ChildProcessConnectionImpl.doConnectionSetupLocked");
            } else {
                throw new AssertionError();
            }
        } catch (RemoteException re) {
            Log.m32e(TAG, "Failed to setup connection.", re);
        } catch (Throwable th) {
            TraceEvent.end("ChildProcessConnectionImpl.doConnectionSetupLocked");
        }
    }

    public boolean isInitialBindingBound() {
        boolean isBound;
        synchronized (this.mLock) {
            isBound = this.mInitialBinding.isBound();
        }
        return isBound;
    }

    public boolean isStrongBindingBound() {
        boolean isBound;
        synchronized (this.mLock) {
            isBound = this.mStrongBinding.isBound();
        }
        return isBound;
    }

    public void removeInitialBinding() {
        synchronized (this.mLock) {
            if ($assertionsDisabled || !this.mAlwaysInForeground) {
                this.mInitialBinding.unbind();
            } else {
                throw new AssertionError();
            }
        }
    }

    public boolean isOomProtectedOrWasWhenDied() {
        boolean z;
        synchronized (this.mLock) {
            if (this.mServiceDisconnected) {
                z = this.mWasOomProtected;
            } else {
                z = isCurrentlyOomProtected();
            }
        }
        return z;
    }

    private boolean isCurrentlyOomProtected() {
        boolean isApplicationInForeground;
        synchronized (this.mLock) {
            if (!$assertionsDisabled && this.mServiceDisconnected) {
                throw new AssertionError();
            } else if (this.mAlwaysInForeground) {
                isApplicationInForeground = ChildProcessLauncher.isApplicationInForeground();
            } else {
                isApplicationInForeground = (this.mInitialBinding.isBound() || this.mStrongBinding.isBound()) ? true : $assertionsDisabled;
            }
        }
        return isApplicationInForeground;
    }

    public void dropOomBindings() {
        synchronized (this.mLock) {
            if ($assertionsDisabled || !this.mAlwaysInForeground) {
                this.mInitialBinding.unbind();
                this.mStrongBindingCount = 0;
                this.mStrongBinding.unbind();
                this.mModerateBinding.unbind();
            } else {
                throw new AssertionError();
            }
        }
    }

    public void addStrongBinding() {
        synchronized (this.mLock) {
            if (this.mService == null) {
                Log.m42w(TAG, "The connection is not bound for %d", Integer.valueOf(this.mPid));
                return;
            }
            if (this.mStrongBindingCount == 0) {
                this.mStrongBinding.bind(null);
            }
            this.mStrongBindingCount++;
        }
    }

    public void removeStrongBinding() {
        synchronized (this.mLock) {
            if (this.mService == null) {
                Log.m42w(TAG, "The connection is not bound for %d", Integer.valueOf(this.mPid));
            } else if ($assertionsDisabled || this.mStrongBindingCount > 0) {
                this.mStrongBindingCount--;
                if (this.mStrongBindingCount == 0) {
                    this.mStrongBinding.unbind();
                }
            } else {
                throw new AssertionError();
            }
        }
    }

    public boolean isModerateBindingBound() {
        boolean isBound;
        synchronized (this.mLock) {
            isBound = this.mModerateBinding.isBound();
        }
        return isBound;
    }

    public void addModerateBinding() {
        synchronized (this.mLock) {
            if (this.mService == null) {
                Log.m42w(TAG, "The connection is not bound for %d", Integer.valueOf(this.mPid));
                return;
            }
            this.mModerateBinding.bind(null);
        }
    }

    public void removeModerateBinding() {
        synchronized (this.mLock) {
            if (this.mService == null) {
                Log.m42w(TAG, "The connection is not bound for %d", Integer.valueOf(this.mPid));
                return;
            }
            this.mModerateBinding.unbind();
        }
    }

    @VisibleForTesting
    public boolean crashServiceForTesting() throws RemoteException {
        try {
            this.mService.crashIntentionallyForTesting();
            return $assertionsDisabled;
        } catch (DeadObjectException e) {
            return true;
        }
    }

    @VisibleForTesting
    public boolean isConnected() {
        return this.mService != null ? true : $assertionsDisabled;
    }
}
