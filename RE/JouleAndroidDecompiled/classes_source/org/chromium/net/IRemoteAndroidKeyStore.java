package org.chromium.net;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IRemoteAndroidKeyStore extends IInterface {

    public static abstract class Stub extends Binder implements IRemoteAndroidKeyStore {
        private static final String DESCRIPTOR = "org.chromium.net.IRemoteAndroidKeyStore";
        static final int TRANSACTION_getClientCertificateAlias = 1;
        static final int TRANSACTION_getDSAKeyParamQ = 7;
        static final int TRANSACTION_getECKeyOrder = 8;
        static final int TRANSACTION_getEncodedCertificateChain = 2;
        static final int TRANSACTION_getPrivateKeyEncodedBytes = 6;
        static final int TRANSACTION_getPrivateKeyHandle = 3;
        static final int TRANSACTION_getPrivateKeyType = 10;
        static final int TRANSACTION_getRSAKeyModulus = 5;
        static final int TRANSACTION_rawSignDigestWithPrivateKey = 9;
        static final int TRANSACTION_releaseKey = 11;
        static final int TRANSACTION_setClientCallbacks = 4;

        private static class Proxy implements IRemoteAndroidKeyStore {
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

            public String getClientCertificateAlias() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getClientCertificateAlias, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte[] getEncodedCertificateChain(String alias) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    this.mRemote.transact(Stub.TRANSACTION_getEncodedCertificateChain, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getPrivateKeyHandle(String alias) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    this.mRemote.transact(Stub.TRANSACTION_getPrivateKeyHandle, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setClientCallbacks(IRemoteAndroidKeyStoreCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callbacks != null ? callbacks.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_setClientCallbacks, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte[] getRSAKeyModulus(int handle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(handle);
                    this.mRemote.transact(Stub.TRANSACTION_getRSAKeyModulus, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte[] getPrivateKeyEncodedBytes(int handle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(handle);
                    this.mRemote.transact(Stub.TRANSACTION_getPrivateKeyEncodedBytes, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte[] getDSAKeyParamQ(int handle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(handle);
                    this.mRemote.transact(Stub.TRANSACTION_getDSAKeyParamQ, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte[] getECKeyOrder(int handle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(handle);
                    this.mRemote.transact(Stub.TRANSACTION_getECKeyOrder, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public byte[] rawSignDigestWithPrivateKey(int handle, byte[] message) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(handle);
                    _data.writeByteArray(message);
                    this.mRemote.transact(Stub.TRANSACTION_rawSignDigestWithPrivateKey, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getPrivateKeyType(int handle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(handle);
                    this.mRemote.transact(Stub.TRANSACTION_getPrivateKeyType, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void releaseKey(int handle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(handle);
                    this.mRemote.transact(Stub.TRANSACTION_releaseKey, _data, _reply, 0);
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

        public static IRemoteAndroidKeyStore asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IRemoteAndroidKeyStore)) {
                return new Proxy(obj);
            }
            return (IRemoteAndroidKeyStore) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            byte[] _result;
            int _result2;
            switch (code) {
                case TRANSACTION_getClientCertificateAlias /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    String _result3 = getClientCertificateAlias();
                    reply.writeNoException();
                    reply.writeString(_result3);
                    return true;
                case TRANSACTION_getEncodedCertificateChain /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getEncodedCertificateChain(data.readString());
                    reply.writeNoException();
                    reply.writeByteArray(_result);
                    return true;
                case TRANSACTION_getPrivateKeyHandle /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getPrivateKeyHandle(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_setClientCallbacks /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    setClientCallbacks(org.chromium.net.IRemoteAndroidKeyStoreCallbacks.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getRSAKeyModulus /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getRSAKeyModulus(data.readInt());
                    reply.writeNoException();
                    reply.writeByteArray(_result);
                    return true;
                case TRANSACTION_getPrivateKeyEncodedBytes /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getPrivateKeyEncodedBytes(data.readInt());
                    reply.writeNoException();
                    reply.writeByteArray(_result);
                    return true;
                case TRANSACTION_getDSAKeyParamQ /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getDSAKeyParamQ(data.readInt());
                    reply.writeNoException();
                    reply.writeByteArray(_result);
                    return true;
                case TRANSACTION_getECKeyOrder /*8*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getECKeyOrder(data.readInt());
                    reply.writeNoException();
                    reply.writeByteArray(_result);
                    return true;
                case TRANSACTION_rawSignDigestWithPrivateKey /*9*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = rawSignDigestWithPrivateKey(data.readInt(), data.createByteArray());
                    reply.writeNoException();
                    reply.writeByteArray(_result);
                    return true;
                case TRANSACTION_getPrivateKeyType /*10*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getPrivateKeyType(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_releaseKey /*11*/:
                    data.enforceInterface(DESCRIPTOR);
                    releaseKey(data.readInt());
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

    String getClientCertificateAlias() throws RemoteException;

    byte[] getDSAKeyParamQ(int i) throws RemoteException;

    byte[] getECKeyOrder(int i) throws RemoteException;

    byte[] getEncodedCertificateChain(String str) throws RemoteException;

    byte[] getPrivateKeyEncodedBytes(int i) throws RemoteException;

    int getPrivateKeyHandle(String str) throws RemoteException;

    int getPrivateKeyType(int i) throws RemoteException;

    byte[] getRSAKeyModulus(int i) throws RemoteException;

    byte[] rawSignDigestWithPrivateKey(int i, byte[] bArr) throws RemoteException;

    void releaseKey(int i) throws RemoteException;

    void setClientCallbacks(IRemoteAndroidKeyStoreCallbacks iRemoteAndroidKeyStoreCallbacks) throws RemoteException;
}
