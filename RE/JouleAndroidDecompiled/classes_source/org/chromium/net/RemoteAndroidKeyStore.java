package org.chromium.net;

import android.os.RemoteException;
import android.util.Log;

public class RemoteAndroidKeyStore implements AndroidKeyStore {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String TAG = "AndroidKeyStoreRemoteImpl";
    private final IRemoteAndroidKeyStore mRemoteManager;

    private static class RemotePrivateKey implements AndroidPrivateKey {
        final int mHandle;
        final RemoteAndroidKeyStore mStore;

        RemotePrivateKey(int handle, RemoteAndroidKeyStore store) {
            this.mHandle = handle;
            this.mStore = store;
        }

        public int getHandle() {
            return this.mHandle;
        }

        public AndroidKeyStore getKeyStore() {
            return this.mStore;
        }
    }

    static {
        $assertionsDisabled = !RemoteAndroidKeyStore.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public RemoteAndroidKeyStore(IRemoteAndroidKeyStore manager) {
        this.mRemoteManager = manager;
    }

    public byte[] getRSAKeyModulus(AndroidPrivateKey key) {
        RemotePrivateKey remoteKey = (RemotePrivateKey) key;
        try {
            Log.d(TAG, "getRSAKeyModulus");
            return this.mRemoteManager.getRSAKeyModulus(remoteKey.getHandle());
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getECKeyOrder(AndroidPrivateKey key) {
        RemotePrivateKey remoteKey = (RemotePrivateKey) key;
        try {
            Log.d(TAG, "getECKeyOrder");
            return this.mRemoteManager.getECKeyOrder(remoteKey.getHandle());
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] rawSignDigestWithPrivateKey(AndroidPrivateKey key, byte[] message) {
        RemotePrivateKey remoteKey = (RemotePrivateKey) key;
        try {
            Log.d(TAG, "rawSignDigestWithPrivateKey");
            return this.mRemoteManager.rawSignDigestWithPrivateKey(remoteKey.getHandle(), message);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getPrivateKeyType(AndroidPrivateKey key) {
        RemotePrivateKey remoteKey = (RemotePrivateKey) key;
        try {
            Log.d(TAG, "getPrivateKeyType");
            return this.mRemoteManager.getPrivateKeyType(remoteKey.getHandle());
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getOpenSSLHandleForPrivateKey(AndroidPrivateKey privateKey) {
        if ($assertionsDisabled) {
            return 0;
        }
        throw new AssertionError();
    }

    public Object getOpenSSLEngineForPrivateKey(AndroidPrivateKey privateKey) {
        if ($assertionsDisabled) {
            return null;
        }
        throw new AssertionError();
    }

    public AndroidPrivateKey createKey(String alias) {
        try {
            return new RemotePrivateKey(this.mRemoteManager.getPrivateKeyHandle(alias), this);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void releaseKey(AndroidPrivateKey key) {
        RemotePrivateKey remoteKey = (RemotePrivateKey) key;
        try {
            Log.d(TAG, "releaseKey");
            this.mRemoteManager.releaseKey(remoteKey.getHandle());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
