package android.support.v4.media;

import android.content.Intent;
import android.media.MediaDescription.Builder;
import android.media.browse.MediaBrowser.MediaItem;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

class MediaBrowserServiceCompatApi21 {
    private static Object sNullParceledListSliceObj;

    static class MediaBrowserServiceAdaptorApi21 {
        Binder mBinder;

        MediaBrowserServiceAdaptorApi21() {
        }

        public void onCreate(ServiceImplApi21 serviceImpl) {
            this.mBinder = createServiceBinder(serviceImpl);
        }

        public IBinder onBind(Intent intent) {
            if (MediaBrowserServiceCompat.SERVICE_INTERFACE.equals(intent.getAction())) {
                return this.mBinder;
            }
            return null;
        }

        protected Binder createServiceBinder(ServiceImplApi21 serviceImpl) {
            return new ServiceBinderAdapterApi21(serviceImpl);
        }
    }

    public interface ServiceCallbacksApi21 {
        IBinder asBinder();

        void onConnect(String str, Object obj, Bundle bundle) throws RemoteException;

        void onConnectFailed() throws RemoteException;

        void onLoadChildren(String str, List<Parcel> list) throws RemoteException;
    }

    public interface ServiceImplApi21 {
        void addSubscription(String str, ServiceCallbacksApi21 serviceCallbacksApi21);

        void connect(String str, Bundle bundle, ServiceCallbacksApi21 serviceCallbacksApi21);

        void disconnect(ServiceCallbacksApi21 serviceCallbacksApi21);

        void removeSubscription(String str, ServiceCallbacksApi21 serviceCallbacksApi21);
    }

    public static class ServiceCallbacksImplApi21 implements ServiceCallbacksApi21 {
        final ServiceCallbacksAdapterApi21 mCallbacks;

        ServiceCallbacksImplApi21(Object callbacksObj) {
            this.mCallbacks = createCallbacks(callbacksObj);
        }

        public IBinder asBinder() {
            return this.mCallbacks.asBinder();
        }

        public void onConnect(String root, Object session, Bundle extras) throws RemoteException {
            this.mCallbacks.onConnect(root, session, extras);
        }

        public void onConnectFailed() throws RemoteException {
            this.mCallbacks.onConnectFailed();
        }

        public void onLoadChildren(String mediaId, List<Parcel> list) throws RemoteException {
            this.mCallbacks.onLoadChildren(mediaId, MediaBrowserServiceCompatApi21.parcelListToParceledListSliceObject(list));
        }

        ServiceCallbacksAdapterApi21 createCallbacks(Object callbacksObj) {
            return new ServiceCallbacksAdapterApi21(callbacksObj);
        }
    }

    MediaBrowserServiceCompatApi21() {
    }

    static {
        MediaItem nullMediaItem = new MediaItem(new Builder().setMediaId("android.support.v4.media.MediaBrowserCompat.NULL_MEDIA_ITEM").build(), 0);
        List<MediaItem> nullMediaItemList = new ArrayList();
        nullMediaItemList.add(nullMediaItem);
        sNullParceledListSliceObj = ParceledListSliceAdapterApi21.newInstance(nullMediaItemList);
    }

    public static Object createService() {
        return new MediaBrowserServiceAdaptorApi21();
    }

    public static void onCreate(Object serviceObj, ServiceImplApi21 serviceImpl) {
        ((MediaBrowserServiceAdaptorApi21) serviceObj).onCreate(serviceImpl);
    }

    public static IBinder onBind(Object serviceObj, Intent intent) {
        return ((MediaBrowserServiceAdaptorApi21) serviceObj).onBind(intent);
    }

    public static Object parcelListToParceledListSliceObject(List<Parcel> list) {
        if (list != null) {
            List<MediaItem> itemList = new ArrayList();
            for (Parcel parcel : list) {
                parcel.setDataPosition(0);
                itemList.add(MediaItem.CREATOR.createFromParcel(parcel));
                parcel.recycle();
            }
            return ParceledListSliceAdapterApi21.newInstance(itemList);
        } else if (VERSION.SDK_INT <= 23) {
            return sNullParceledListSliceObj;
        } else {
            return null;
        }
    }
}
