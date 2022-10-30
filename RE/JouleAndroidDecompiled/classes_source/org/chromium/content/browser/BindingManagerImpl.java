package org.chromium.content.browser;

import android.annotation.TargetApi;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Handler;
import android.util.LruCache;
import android.util.SparseArray;
import java.util.Map.Entry;
import org.chromium.base.Log;
import org.chromium.base.SysUtils;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.metrics.RecordHistogram;

class BindingManagerImpl implements BindingManager {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final long DETACH_AS_ACTIVE_HIGH_END_DELAY_MILLIS = 1000;
    private static final long MODERATE_BINDING_POOL_CLEARER_DELAY_MILLIS = 10000;
    private static final long MODERATE_BINDING_POOL_CLEARER_DELAY_MILLIS_ON_TESTING = 100;
    private static final String TAG = "cr.BindingManager";
    private ManagedConnection mBoundForBackgroundPeriod;
    private final boolean mIsLowMemoryDevice;
    private ManagedConnection mLastInForeground;
    private final Object mLastInForegroundLock;
    private final SparseArray<ManagedConnection> mManagedConnections;
    private ModerateBindingPool mModerateBindingPool;
    private final Object mModerateBindingPoolLock;
    private final boolean mOnTesting;
    private final long mRemoveStrongBindingDelay;

    private class ManagedConnection {
        static final /* synthetic */ boolean $assertionsDisabled;
        private boolean mBoundForBackgroundPeriod;
        private ChildProcessConnection mConnection;
        private boolean mInForeground;
        private boolean mWasOomProtected;

        /* renamed from: org.chromium.content.browser.BindingManagerImpl.ManagedConnection.1 */
        class C03231 implements Runnable {
            final /* synthetic */ ChildProcessConnection val$connection;
            final /* synthetic */ boolean val$keepsAsModerate;

            C03231(ChildProcessConnection childProcessConnection, boolean z) {
                this.val$connection = childProcessConnection;
                this.val$keepsAsModerate = z;
            }

            public void run() {
                if (this.val$connection.isStrongBindingBound()) {
                    ModerateBindingPool moderateBindingPool;
                    this.val$connection.removeStrongBinding();
                    synchronized (BindingManagerImpl.this.mModerateBindingPoolLock) {
                        moderateBindingPool = BindingManagerImpl.this.mModerateBindingPool;
                    }
                    if (moderateBindingPool != null && !this.val$connection.isStrongBindingBound() && this.val$keepsAsModerate) {
                        moderateBindingPool.addConnection(ManagedConnection.this);
                    }
                }
            }
        }

        static {
            $assertionsDisabled = !BindingManagerImpl.class.desiredAssertionStatus() ? true : BindingManagerImpl.$assertionsDisabled;
        }

        private void removeInitialBinding() {
            if (this.mConnection != null && this.mConnection.isInitialBindingBound()) {
                this.mConnection.removeInitialBinding();
            }
        }

        private void addStrongBinding() {
            ChildProcessConnection connection = this.mConnection;
            if (connection != null) {
                ModerateBindingPool moderateBindingPool;
                connection.addStrongBinding();
                synchronized (BindingManagerImpl.this.mModerateBindingPoolLock) {
                    moderateBindingPool = BindingManagerImpl.this.mModerateBindingPool;
                }
                if (moderateBindingPool != null) {
                    moderateBindingPool.removeConnection(this);
                }
            }
        }

        private void removeStrongBinding(boolean keepsAsModerate) {
            ChildProcessConnection connection = this.mConnection;
            if (connection != null && connection.isStrongBindingBound()) {
                Runnable doUnbind = new C03231(connection, keepsAsModerate);
                if (BindingManagerImpl.this.mIsLowMemoryDevice) {
                    doUnbind.run();
                } else {
                    ThreadUtils.postOnUiThreadDelayed(doUnbind, BindingManagerImpl.this.mRemoveStrongBindingDelay);
                }
            }
        }

        private void removeModerateBinding() {
            if (this.mConnection != null && this.mConnection.isModerateBindingBound()) {
                this.mConnection.removeModerateBinding();
            }
        }

        private void addModerateBinding() {
            ChildProcessConnection connection = this.mConnection;
            if (connection != null) {
                connection.addModerateBinding();
            }
        }

        private void dropBindings() {
            if ($assertionsDisabled || BindingManagerImpl.this.mIsLowMemoryDevice) {
                ChildProcessConnection connection = this.mConnection;
                if (connection != null) {
                    connection.dropOomBindings();
                    return;
                }
                return;
            }
            throw new AssertionError();
        }

        ManagedConnection(ChildProcessConnection connection) {
            this.mConnection = connection;
        }

        void setInForeground(boolean nextInForeground) {
            if (!this.mInForeground && nextInForeground) {
                addStrongBinding();
            } else if (this.mInForeground && !nextInForeground) {
                removeStrongBinding(true);
            }
            this.mInForeground = nextInForeground;
        }

        void determinedVisibility() {
            removeInitialBinding();
        }

        void setBoundForBackgroundPeriod(boolean nextBound) {
            if (!this.mBoundForBackgroundPeriod && nextBound) {
                addStrongBinding();
            } else if (this.mBoundForBackgroundPeriod && !nextBound) {
                removeStrongBinding(BindingManagerImpl.$assertionsDisabled);
            }
            this.mBoundForBackgroundPeriod = nextBound;
        }

        boolean isOomProtected() {
            return this.mConnection != null ? this.mConnection.isOomProtectedOrWasWhenDied() : this.mWasOomProtected;
        }

        void clearConnection() {
            this.mWasOomProtected = this.mConnection.isOomProtectedOrWasWhenDied();
            synchronized (BindingManagerImpl.this.mModerateBindingPoolLock) {
                ModerateBindingPool moderateBindingPool = BindingManagerImpl.this.mModerateBindingPool;
            }
            if (moderateBindingPool != null) {
                moderateBindingPool.removeConnection(this);
            }
            this.mConnection = null;
        }

        @VisibleForTesting
        boolean isConnectionCleared() {
            return this.mConnection == null ? true : BindingManagerImpl.$assertionsDisabled;
        }
    }

    private static class ModerateBindingPool extends LruCache<Integer, ManagedConnection> implements ComponentCallbacks2 {
        private Runnable mDelayedClearer;
        private final Object mDelayedClearerLock;
        private final Handler mHandler;
        private final float mHighReduceRatio;
        private final float mLowReduceRatio;

        /* renamed from: org.chromium.content.browser.BindingManagerImpl.ModerateBindingPool.1 */
        class C03241 implements Runnable {
            final /* synthetic */ boolean val$onTesting;

            C03241(boolean z) {
                this.val$onTesting = z;
            }

            public void run() {
                synchronized (ModerateBindingPool.this.mDelayedClearerLock) {
                    if (ModerateBindingPool.this.mDelayedClearer == null) {
                        return;
                    }
                    ModerateBindingPool.this.mDelayedClearer = null;
                    Log.m33i(BindingManagerImpl.TAG, "Release moderate connections: %d", Integer.valueOf(ModerateBindingPool.this.size()));
                    if (!this.val$onTesting) {
                        RecordHistogram.recordCountHistogram("Android.ModerateBindingCount", ModerateBindingPool.this.size());
                    }
                    ModerateBindingPool.this.evictAll();
                }
            }
        }

        public ModerateBindingPool(int maxSize, float lowReduceRatio, float highReduceRatio) {
            super(maxSize);
            this.mDelayedClearerLock = new Object();
            this.mHandler = new Handler(ThreadUtils.getUiThreadLooper());
            this.mLowReduceRatio = lowReduceRatio;
            this.mHighReduceRatio = highReduceRatio;
        }

        public void onTrimMemory(int level) {
            Log.m33i(BindingManagerImpl.TAG, "onTrimMemory: level=%d, size=%d", Integer.valueOf(level), Integer.valueOf(size()));
            if (size() <= 0) {
                return;
            }
            if (level <= 5) {
                reduce(this.mLowReduceRatio);
            } else if (level <= 10) {
                reduce(this.mHighReduceRatio);
            } else if (level != 20) {
                evictAll();
            }
        }

        public void onLowMemory() {
            Log.m33i(BindingManagerImpl.TAG, "onLowMemory: evict %d bindings", Integer.valueOf(size()));
            evictAll();
        }

        public void onConfigurationChanged(Configuration configuration) {
        }

        @TargetApi(17)
        private void reduce(float reduceRatio) {
            int oldSize = size();
            int newSize = (int) (((float) oldSize) * (1.0f - reduceRatio));
            Log.m33i(BindingManagerImpl.TAG, "Reduce connections from %d to %d", Integer.valueOf(oldSize), Integer.valueOf(newSize));
            if (newSize == 0) {
                evictAll();
            } else if (VERSION.SDK_INT >= 17) {
                trimToSize(newSize);
            } else {
                int count = 0;
                for (Entry<Integer, ManagedConnection> entry : snapshot().entrySet()) {
                    remove(entry.getKey());
                    count++;
                    if (count == oldSize - newSize) {
                        return;
                    }
                }
            }
        }

        void addConnection(ManagedConnection managedConnection) {
            ChildProcessConnection connection = managedConnection.mConnection;
            if (connection != null && connection.isInSandbox()) {
                managedConnection.addModerateBinding();
                if (connection.isModerateBindingBound()) {
                    put(Integer.valueOf(connection.getServiceNumber()), managedConnection);
                } else {
                    remove(Integer.valueOf(connection.getServiceNumber()));
                }
            }
        }

        void removeConnection(ManagedConnection managedConnection) {
            ChildProcessConnection connection = managedConnection.mConnection;
            if (connection != null && connection.isInSandbox()) {
                remove(Integer.valueOf(connection.getServiceNumber()));
            }
        }

        protected void entryRemoved(boolean evicted, Integer key, ManagedConnection oldValue, ManagedConnection newValue) {
            if (oldValue != newValue) {
                oldValue.removeModerateBinding();
            }
        }

        void onSentToBackground(boolean onTesting) {
            if (size() != 0) {
                synchronized (this.mDelayedClearerLock) {
                    this.mDelayedClearer = new C03241(onTesting);
                    this.mHandler.postDelayed(this.mDelayedClearer, onTesting ? BindingManagerImpl.MODERATE_BINDING_POOL_CLEARER_DELAY_MILLIS_ON_TESTING : BindingManagerImpl.MODERATE_BINDING_POOL_CLEARER_DELAY_MILLIS);
                }
            }
        }

        void onBroughtToForeground() {
            synchronized (this.mDelayedClearerLock) {
                if (this.mDelayedClearer == null) {
                    return;
                }
                this.mHandler.removeCallbacks(this.mDelayedClearer);
                this.mDelayedClearer = null;
            }
        }
    }

    static {
        $assertionsDisabled = !BindingManagerImpl.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    private BindingManagerImpl(boolean isLowMemoryDevice, long removeStrongBindingDelay, boolean onTesting) {
        this.mModerateBindingPoolLock = new Object();
        this.mManagedConnections = new SparseArray();
        this.mLastInForegroundLock = new Object();
        this.mIsLowMemoryDevice = isLowMemoryDevice;
        this.mRemoveStrongBindingDelay = removeStrongBindingDelay;
        this.mOnTesting = onTesting;
    }

    public static BindingManagerImpl createBindingManager() {
        return new BindingManagerImpl(SysUtils.isLowEndDevice(), DETACH_AS_ACTIVE_HIGH_END_DELAY_MILLIS, $assertionsDisabled);
    }

    public static BindingManagerImpl createBindingManagerForTesting(boolean isLowEndDevice) {
        return new BindingManagerImpl(isLowEndDevice, 0, true);
    }

    public void addNewConnection(int pid, ChildProcessConnection connection) {
        synchronized (this.mManagedConnections) {
            this.mManagedConnections.put(pid, new ManagedConnection(connection));
        }
    }

    public void setInForeground(int pid, boolean inForeground) {
        synchronized (this.mManagedConnections) {
            ManagedConnection managedConnection = (ManagedConnection) this.mManagedConnections.get(pid);
        }
        if (managedConnection == null) {
            Log.m42w(TAG, "Cannot setInForeground() - never saw a connection for the pid: %d", Integer.valueOf(pid));
            return;
        }
        synchronized (this.mLastInForegroundLock) {
            if (inForeground) {
                if (!(!this.mIsLowMemoryDevice || this.mLastInForeground == null || this.mLastInForeground == managedConnection)) {
                    this.mLastInForeground.dropBindings();
                }
            }
            managedConnection.setInForeground(inForeground);
            if (inForeground) {
                this.mLastInForeground = managedConnection;
            }
        }
    }

    public void determinedVisibility(int pid) {
        synchronized (this.mManagedConnections) {
            ManagedConnection managedConnection = (ManagedConnection) this.mManagedConnections.get(pid);
        }
        if (managedConnection == null) {
            Log.m42w(TAG, "Cannot call determinedVisibility() - never saw a connection for the pid: %d", Integer.valueOf(pid));
            return;
        }
        managedConnection.determinedVisibility();
    }

    public void onSentToBackground() {
        if ($assertionsDisabled || this.mBoundForBackgroundPeriod == null) {
            ModerateBindingPool moderateBindingPool;
            synchronized (this.mLastInForegroundLock) {
                if (this.mLastInForeground != null) {
                    this.mLastInForeground.setBoundForBackgroundPeriod(true);
                    this.mBoundForBackgroundPeriod = this.mLastInForeground;
                }
            }
            synchronized (this.mModerateBindingPoolLock) {
                moderateBindingPool = this.mModerateBindingPool;
            }
            if (moderateBindingPool != null) {
                moderateBindingPool.onSentToBackground(this.mOnTesting);
                return;
            }
            return;
        }
        throw new AssertionError();
    }

    public void onBroughtToForeground() {
        if (this.mBoundForBackgroundPeriod != null) {
            this.mBoundForBackgroundPeriod.setBoundForBackgroundPeriod($assertionsDisabled);
            this.mBoundForBackgroundPeriod = null;
        }
        synchronized (this.mModerateBindingPoolLock) {
            ModerateBindingPool moderateBindingPool = this.mModerateBindingPool;
        }
        if (moderateBindingPool != null) {
            moderateBindingPool.onBroughtToForeground();
        }
    }

    public boolean isOomProtected(int pid) {
        ManagedConnection managedConnection;
        synchronized (this.mManagedConnections) {
            managedConnection = (ManagedConnection) this.mManagedConnections.get(pid);
        }
        return managedConnection != null ? managedConnection.isOomProtected() : $assertionsDisabled;
    }

    public void clearConnection(int pid) {
        synchronized (this.mManagedConnections) {
            ManagedConnection managedConnection = (ManagedConnection) this.mManagedConnections.get(pid);
        }
        if (managedConnection != null) {
            managedConnection.clearConnection();
        }
    }

    @VisibleForTesting
    public boolean isConnectionCleared(int pid) {
        boolean isConnectionCleared;
        synchronized (this.mManagedConnections) {
            isConnectionCleared = ((ManagedConnection) this.mManagedConnections.get(pid)).isConnectionCleared();
        }
        return isConnectionCleared;
    }

    public void startModerateBindingManagement(Context context, int maxSize, float lowReduceRatio, float highReduceRatio) {
        synchronized (this.mModerateBindingPoolLock) {
            if (this.mIsLowMemoryDevice || this.mModerateBindingPool != null) {
                return;
            }
            Log.m33i(TAG, "Moderate binding enabled: maxSize=%d lowReduceRatio=%f highReduceRatio=%f", Integer.valueOf(maxSize), Float.valueOf(lowReduceRatio), Float.valueOf(highReduceRatio));
            this.mModerateBindingPool = new ModerateBindingPool(maxSize, lowReduceRatio, highReduceRatio);
            if (context != null) {
                context.registerComponentCallbacks(this.mModerateBindingPool);
            }
        }
    }

    public void releaseAllModerateBindings() {
        synchronized (this.mModerateBindingPoolLock) {
            ModerateBindingPool moderateBindingPool = this.mModerateBindingPool;
        }
        if (moderateBindingPool != null) {
            Log.m33i(TAG, "Release all moderate bindings: %d", Integer.valueOf(moderateBindingPool.size()));
            moderateBindingPool.evictAll();
        }
    }
}
