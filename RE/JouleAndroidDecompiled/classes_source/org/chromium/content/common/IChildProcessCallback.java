package org.chromium.content.common;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.Surface;

public interface IChildProcessCallback extends IInterface {

    public static abstract class Stub extends Binder implements IChildProcessCallback {
        private static final String DESCRIPTOR = "org.chromium.content.common.IChildProcessCallback";
        static final int TRANSACTION_establishSurfacePeer = 1;
        static final int TRANSACTION_getSurfaceTextureSurface = 5;
        static final int TRANSACTION_getViewSurface = 2;
        static final int TRANSACTION_registerSurfaceTextureSurface = 3;
        static final int TRANSACTION_unregisterSurfaceTextureSurface = 4;

        private static class Proxy implements IChildProcessCallback {
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

            public void establishSurfacePeer(int pid, Surface surface, int primaryID, int secondaryID) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pid);
                    if (surface != null) {
                        _data.writeInt(Stub.TRANSACTION_establishSurfacePeer);
                        surface.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(primaryID);
                    _data.writeInt(secondaryID);
                    this.mRemote.transact(Stub.TRANSACTION_establishSurfacePeer, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public SurfaceWrapper getViewSurface(int surfaceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    SurfaceWrapper _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(surfaceId);
                    this.mRemote.transact(Stub.TRANSACTION_getViewSurface, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (SurfaceWrapper) SurfaceWrapper.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerSurfaceTextureSurface(int surfaceTextureId, int clientId, Surface surface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(surfaceTextureId);
                    _data.writeInt(clientId);
                    if (surface != null) {
                        _data.writeInt(Stub.TRANSACTION_establishSurfacePeer);
                        surface.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_registerSurfaceTextureSurface, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterSurfaceTextureSurface(int surfaceTextureId, int clientId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(surfaceTextureId);
                    _data.writeInt(clientId);
                    this.mRemote.transact(Stub.TRANSACTION_unregisterSurfaceTextureSurface, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public SurfaceWrapper getSurfaceTextureSurface(int surfaceTextureId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    SurfaceWrapper _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(surfaceTextureId);
                    this.mRemote.transact(Stub.TRANSACTION_getSurfaceTextureSurface, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (SurfaceWrapper) SurfaceWrapper.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IChildProcessCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IChildProcessCallback)) {
                return new Proxy(obj);
            }
            return (IChildProcessCallback) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int _arg0;
            SurfaceWrapper _result;
            switch (code) {
                case TRANSACTION_establishSurfacePeer /*1*/:
                    Surface _arg1;
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg1 = (Surface) Surface.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    establishSurfacePeer(_arg0, _arg1, data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getViewSurface /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getViewSurface(data.readInt());
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_establishSurfacePeer);
                        _result.writeToParcel(reply, TRANSACTION_establishSurfacePeer);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_registerSurfaceTextureSurface /*3*/:
                    Surface _arg2;
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = data.readInt();
                    int _arg12 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg2 = (Surface) Surface.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    registerSurfaceTextureSurface(_arg0, _arg12, _arg2);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_unregisterSurfaceTextureSurface /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    unregisterSurfaceTextureSurface(data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getSurfaceTextureSurface /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getSurfaceTextureSurface(data.readInt());
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_establishSurfacePeer);
                        _result.writeToParcel(reply, TRANSACTION_establishSurfacePeer);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void establishSurfacePeer(int i, Surface surface, int i2, int i3) throws RemoteException;

    SurfaceWrapper getSurfaceTextureSurface(int i) throws RemoteException;

    SurfaceWrapper getViewSurface(int i) throws RemoteException;

    void registerSurfaceTextureSurface(int i, int i2, Surface surface) throws RemoteException;

    void unregisterSurfaceTextureSurface(int i, int i2) throws RemoteException;
}
