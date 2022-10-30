package android.support.v4.media;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserServiceCompatApi24.ServiceCallbacksImplApi24;
import android.support.v4.media.MediaBrowserServiceCompatApi24.ServiceImplApi24;

class ServiceBinderAdapterApi24 extends ServiceBinderAdapterApi23 {
    private static final int TRANSACTION_addSubscriptionWithOptions = 6;
    private static final int TRANSACTION_removeSubscriptionWithOptions = 7;
    final ServiceImplApi24 mServiceImpl;

    public ServiceBinderAdapterApi24(ServiceImplApi24 serviceImpl) {
        super(serviceImpl);
        this.mServiceImpl = serviceImpl;
    }

    public IBinder asBinder() {
        return this;
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        Bundle arg1 = null;
        String arg0;
        switch (code) {
            case TRANSACTION_addSubscriptionWithOptions /*6*/:
                data.enforceInterface("android.service.media.IMediaBrowserService");
                arg0 = data.readString();
                if (data.readInt() != 0) {
                    arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                }
                addSubscription(arg0, arg1, Stub.asInterface(data.readStrongBinder()));
                return true;
            case TRANSACTION_removeSubscriptionWithOptions /*7*/:
                data.enforceInterface("android.service.media.IMediaBrowserService");
                arg0 = data.readString();
                if (data.readInt() != 0) {
                    arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                }
                removeSubscription(arg0, arg1, Stub.asInterface(data.readStrongBinder()));
                return true;
            default:
                return super.onTransact(code, data, reply, flags);
        }
    }

    void connect(String pkg, Bundle rootHints, Object callbacks) {
        this.mServiceImpl.connect(pkg, rootHints, new ServiceCallbacksImplApi24(callbacks));
    }

    void addSubscription(String id, Bundle options, Object callbacks) {
        this.mServiceImpl.addSubscription(id, options, new ServiceCallbacksImplApi24(callbacks));
    }

    void removeSubscription(String id, Bundle options, Object callbacks) {
        this.mServiceImpl.removeSubscription(id, options, new ServiceCallbacksImplApi24(callbacks));
    }
}
