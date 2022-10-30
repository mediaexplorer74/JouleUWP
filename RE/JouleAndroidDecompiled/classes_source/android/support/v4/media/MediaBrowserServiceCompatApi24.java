package android.support.v4.media;

import android.os.Binder;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserServiceCompatApi21.ServiceCallbacksApi21;
import android.support.v4.media.MediaBrowserServiceCompatApi21.ServiceCallbacksImplApi21;
import android.support.v4.media.MediaBrowserServiceCompatApi21.ServiceImplApi21;
import android.support.v4.media.MediaBrowserServiceCompatApi23.ServiceImplApi23;
import java.util.List;

class MediaBrowserServiceCompatApi24 extends MediaBrowserServiceCompatApi23 {

    public interface ServiceCallbacksApi24 extends ServiceCallbacksApi21 {
        void onLoadChildren(String str, List<Parcel> list, Bundle bundle) throws RemoteException;
    }

    static class MediaBrowserServiceAdaptorApi24 extends MediaBrowserServiceAdaptorApi23 {
        MediaBrowserServiceAdaptorApi24() {
        }

        protected Binder createServiceBinder(ServiceImplApi21 serviceImpl) {
            return new ServiceBinderAdapterApi24((ServiceImplApi24) serviceImpl);
        }
    }

    public static class ServiceCallbacksImplApi24 extends ServiceCallbacksImplApi21 implements ServiceCallbacksApi24 {
        ServiceCallbacksImplApi24(Object callbacksObj) {
            super(callbacksObj);
        }

        public void onLoadChildren(String mediaId, List<Parcel> list, Bundle options) throws RemoteException {
            ((ServiceCallbacksAdapterApi24) this.mCallbacks).onLoadChildrenWithOptions(mediaId, MediaBrowserServiceCompatApi21.parcelListToParceledListSliceObject(list), options);
        }

        ServiceCallbacksAdapterApi24 createCallbacks(Object callbacksObj) {
            return new ServiceCallbacksAdapterApi24(callbacksObj);
        }
    }

    public interface ServiceImplApi24 extends ServiceImplApi23 {
        void addSubscription(String str, Bundle bundle, ServiceCallbacksApi24 serviceCallbacksApi24);

        void connect(String str, Bundle bundle, ServiceCallbacksApi24 serviceCallbacksApi24);

        void removeSubscription(String str, Bundle bundle, ServiceCallbacksApi24 serviceCallbacksApi24);
    }

    MediaBrowserServiceCompatApi24() {
    }

    public static Object createService() {
        return new MediaBrowserServiceAdaptorApi24();
    }
}
