package android.support.v4.media;

import android.os.Binder;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.media.MediaBrowserServiceCompatApi21.ServiceImplApi21;

class MediaBrowserServiceCompatApi23 extends MediaBrowserServiceCompatApi21 {

    public interface ItemCallback {
        void onItemLoaded(int i, Bundle bundle, Parcel parcel);
    }

    static class MediaBrowserServiceAdaptorApi23 extends MediaBrowserServiceAdaptorApi21 {
        MediaBrowserServiceAdaptorApi23() {
        }

        protected Binder createServiceBinder(ServiceImplApi21 serviceImpl) {
            return new ServiceBinderAdapterApi23((ServiceImplApi23) serviceImpl);
        }
    }

    public interface ServiceImplApi23 extends ServiceImplApi21 {
        void getMediaItem(String str, ItemCallback itemCallback);
    }

    MediaBrowserServiceCompatApi23() {
    }

    public static Object createService() {
        return new MediaBrowserServiceAdaptorApi23();
    }
}
