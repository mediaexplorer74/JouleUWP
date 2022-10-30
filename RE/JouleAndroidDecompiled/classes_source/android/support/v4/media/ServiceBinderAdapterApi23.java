package android.support.v4.media;

import android.media.browse.MediaBrowser.MediaItem;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.service.media.MediaBrowserService;
import android.support.v4.media.MediaBrowserServiceCompatApi23.ItemCallback;
import android.support.v4.media.MediaBrowserServiceCompatApi23.ServiceImplApi23;
import android.util.Log;

class ServiceBinderAdapterApi23 extends ServiceBinderAdapterApi21 {
    private static final String TAG = "IMediaBrowserServiceAdapterApi23";
    private static final int TRANSACTION_getMediaItem = 5;
    final ServiceImplApi23 mServiceImpl;

    /* renamed from: android.support.v4.media.ServiceBinderAdapterApi23.1 */
    class C05071 implements ItemCallback {
        final /* synthetic */ String val$KEY_MEDIA_ITEM;
        final /* synthetic */ ResultReceiver val$receiver;

        C05071(String str, ResultReceiver resultReceiver) {
            this.val$KEY_MEDIA_ITEM = str;
            this.val$receiver = resultReceiver;
        }

        public void onItemLoaded(int resultCode, Bundle resultData, Parcel itemParcel) {
            if (itemParcel != null) {
                itemParcel.setDataPosition(0);
                resultData.putParcelable(this.val$KEY_MEDIA_ITEM, (MediaItem) MediaItem.CREATOR.createFromParcel(itemParcel));
                itemParcel.recycle();
            }
            this.val$receiver.send(resultCode, resultData);
        }
    }

    public ServiceBinderAdapterApi23(ServiceImplApi23 serviceImpl) {
        super(serviceImpl);
        this.mServiceImpl = serviceImpl;
    }

    public IBinder asBinder() {
        return this;
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case TRANSACTION_getMediaItem /*5*/:
                ResultReceiver arg1;
                data.enforceInterface("android.service.media.IMediaBrowserService");
                String arg0 = data.readString();
                if (data.readInt() != 0) {
                    arg1 = (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(data);
                } else {
                    arg1 = null;
                }
                getMediaItem(arg0, arg1);
                return true;
            default:
                return super.onTransact(code, data, reply, flags);
        }
    }

    void getMediaItem(String mediaId, ResultReceiver receiver) {
        ReflectiveOperationException e;
        try {
            this.mServiceImpl.getMediaItem(mediaId, new C05071((String) MediaBrowserService.class.getDeclaredField("KEY_MEDIA_ITEM").get(null), receiver));
        } catch (IllegalAccessException e2) {
            e = e2;
            Log.i(TAG, "Failed to get KEY_MEDIA_ITEM via reflection", e);
        } catch (NoSuchFieldException e3) {
            e = e3;
            Log.i(TAG, "Failed to get KEY_MEDIA_ITEM via reflection", e);
        }
    }
}
