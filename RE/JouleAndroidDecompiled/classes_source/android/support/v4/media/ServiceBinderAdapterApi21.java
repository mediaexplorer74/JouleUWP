package android.support.v4.media;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserServiceCompatApi21.ServiceCallbacksImplApi21;
import android.support.v4.media.MediaBrowserServiceCompatApi21.ServiceImplApi21;

class ServiceBinderAdapterApi21 extends Binder implements IInterface {
    static final String DESCRIPTOR = "android.service.media.IMediaBrowserService";
    private static final int TRANSACTION_addSubscription = 3;
    private static final int TRANSACTION_connect = 1;
    private static final int TRANSACTION_disconnect = 2;
    private static final int TRANSACTION_removeSubscription = 4;
    final ServiceImplApi21 mServiceImpl;

    public ServiceBinderAdapterApi21(ServiceImplApi21 serviceImpl) {
        this.mServiceImpl = serviceImpl;
        attachInterface(this, DESCRIPTOR);
    }

    public IBinder asBinder() {
        return this;
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case TRANSACTION_connect /*1*/:
                Bundle arg1;
                data.enforceInterface(DESCRIPTOR);
                String arg0 = data.readString();
                if (data.readInt() != 0) {
                    arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                } else {
                    arg1 = null;
                }
                connect(arg0, arg1, Stub.asInterface(data.readStrongBinder()));
                return true;
            case TRANSACTION_disconnect /*2*/:
                data.enforceInterface(DESCRIPTOR);
                disconnect(Stub.asInterface(data.readStrongBinder()));
                return true;
            case TRANSACTION_addSubscription /*3*/:
                data.enforceInterface(DESCRIPTOR);
                addSubscription(data.readString(), Stub.asInterface(data.readStrongBinder()));
                return true;
            case TRANSACTION_removeSubscription /*4*/:
                data.enforceInterface(DESCRIPTOR);
                removeSubscription(data.readString(), Stub.asInterface(data.readStrongBinder()));
                return true;
            case 1598968902:
                reply.writeString(DESCRIPTOR);
                return true;
            default:
                return super.onTransact(code, data, reply, flags);
        }
    }

    void connect(String pkg, Bundle rootHints, Object callbacks) {
        this.mServiceImpl.connect(pkg, rootHints, new ServiceCallbacksImplApi21(callbacks));
    }

    void disconnect(Object callbacks) {
        this.mServiceImpl.disconnect(new ServiceCallbacksImplApi21(callbacks));
    }

    void addSubscription(String id, Object callbacks) {
        this.mServiceImpl.addSubscription(id, new ServiceCallbacksImplApi21(callbacks));
    }

    void removeSubscription(String id, Object callbacks) {
        this.mServiceImpl.removeSubscription(id, new ServiceCallbacksImplApi21(callbacks));
    }
}
