package android.support.v4.media;

import android.media.browse.MediaBrowser;
import android.media.browse.MediaBrowser.MediaItem;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import java.lang.reflect.Method;
import java.util.List;

class MediaBrowserCompatApi24 {
    private static Method sSubscribeMethod;
    private static Method sUnsubscribeMethod;

    interface SubscriptionCallback extends SubscriptionCallback {
        void onChildrenLoaded(@NonNull String str, List<Parcel> list, @NonNull Bundle bundle);

        void onError(@NonNull String str, @NonNull Bundle bundle);
    }

    static class SubscriptionCallbackProxy<T extends SubscriptionCallback> extends SubscriptionCallbackProxy<T> {
        public SubscriptionCallbackProxy(T callback) {
            super(callback);
        }

        public void onChildrenLoaded(@NonNull String parentId, List<MediaItem> children, @NonNull Bundle options) {
            ((SubscriptionCallback) this.mSubscriptionCallback).onChildrenLoaded(parentId, SubscriptionCallbackProxy.itemListToParcelList(children), options);
        }

        public void onError(@NonNull String parentId, @NonNull Bundle options) {
            ((SubscriptionCallback) this.mSubscriptionCallback).onError(parentId, options);
        }
    }

    MediaBrowserCompatApi24() {
    }

    static {
        try {
            sSubscribeMethod = MediaBrowser.class.getMethod("subscribe", new Class[]{String.class, Bundle.class, android.media.browse.MediaBrowser.SubscriptionCallback.class});
            sUnsubscribeMethod = MediaBrowser.class.getMethod("unsubscribe", new Class[]{String.class, Bundle.class});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static Object createSubscriptionCallback(SubscriptionCallback callback) {
        return new SubscriptionCallbackProxy(callback);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void subscribe(java.lang.Object r4, java.lang.String r5, android.os.Bundle r6, java.lang.Object r7) {
        /*
        r1 = sSubscribeMethod;	 Catch:{ IllegalAccessException -> 0x0012, InvocationTargetException -> 0x0017 }
        r2 = 3;
        r2 = new java.lang.Object[r2];	 Catch:{ IllegalAccessException -> 0x0012, InvocationTargetException -> 0x0017 }
        r3 = 0;
        r2[r3] = r5;	 Catch:{ IllegalAccessException -> 0x0012, InvocationTargetException -> 0x0017 }
        r3 = 1;
        r2[r3] = r6;	 Catch:{ IllegalAccessException -> 0x0012, InvocationTargetException -> 0x0017 }
        r3 = 2;
        r2[r3] = r7;	 Catch:{ IllegalAccessException -> 0x0012, InvocationTargetException -> 0x0017 }
        r1.invoke(r4, r2);	 Catch:{ IllegalAccessException -> 0x0012, InvocationTargetException -> 0x0017 }
    L_0x0011:
        return;
    L_0x0012:
        r0 = move-exception;
    L_0x0013:
        r0.printStackTrace();
        goto L_0x0011;
    L_0x0017:
        r0 = move-exception;
        goto L_0x0013;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.MediaBrowserCompatApi24.subscribe(java.lang.Object, java.lang.String, android.os.Bundle, java.lang.Object):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void unsubscribe(java.lang.Object r4, java.lang.String r5, android.os.Bundle r6) {
        /*
        r1 = sUnsubscribeMethod;	 Catch:{ IllegalAccessException -> 0x000f, InvocationTargetException -> 0x0014 }
        r2 = 2;
        r2 = new java.lang.Object[r2];	 Catch:{ IllegalAccessException -> 0x000f, InvocationTargetException -> 0x0014 }
        r3 = 0;
        r2[r3] = r5;	 Catch:{ IllegalAccessException -> 0x000f, InvocationTargetException -> 0x0014 }
        r3 = 1;
        r2[r3] = r6;	 Catch:{ IllegalAccessException -> 0x000f, InvocationTargetException -> 0x0014 }
        r1.invoke(r4, r2);	 Catch:{ IllegalAccessException -> 0x000f, InvocationTargetException -> 0x0014 }
    L_0x000e:
        return;
    L_0x000f:
        r0 = move-exception;
    L_0x0010:
        r0.printStackTrace();
        goto L_0x000e;
    L_0x0014:
        r0 = move-exception;
        goto L_0x0010;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.media.MediaBrowserCompatApi24.unsubscribe(java.lang.Object, java.lang.String, android.os.Bundle):void");
    }
}
