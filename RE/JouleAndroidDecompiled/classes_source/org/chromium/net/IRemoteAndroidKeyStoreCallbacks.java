package org.chromium.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IRemoteAndroidKeyStoreCallbacks extends IInterface {

    public static abstract class Stub extends Binder implements IRemoteAndroidKeyStoreCallbacks {
        private static final String DESCRIPTOR = "org.chromium.net.IRemoteAndroidKeyStoreCallbacks";
        static final int TRANSACTION_onDisabled = 1;
        static final int TRANSACTION_onInitComplete = 2;

        private static class Proxy implements IRemoteAndroidKeyStoreCallbacks {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void onDisabled() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onDisabled, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onInitComplete() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onInitComplete, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRemoteAndroidKeyStoreCallbacks asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IRemoteAndroidKeyStoreCallbacks)) {
                return new Proxy(obj);
            }
            return (IRemoteAndroidKeyStoreCallbacks) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case TRANSACTION_onDisabled /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    onDisabled();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_onInitComplete /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    onInitComplete();
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void onDisabled() throws RemoteException;

    void onInitComplete() throws RemoteException;
}
