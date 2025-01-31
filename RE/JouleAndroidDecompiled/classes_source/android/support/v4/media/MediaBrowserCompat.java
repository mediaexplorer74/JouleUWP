package android.support.v4.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.MediaSessionCompat.Token;
import android.support.v4.os.ResultReceiver;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.apache.cordova.camera.CameraLauncher;

public final class MediaBrowserCompat {
    public static final String EXTRA_PAGE = "android.media.browse.extra.PAGE";
    public static final String EXTRA_PAGE_SIZE = "android.media.browse.extra.PAGE_SIZE";
    private static final String TAG = "MediaBrowserCompat";
    private final MediaBrowserImpl mImpl;

    private static class CallbackHandler extends Handler {
        private final MediaBrowserServiceCallbackImpl mCallbackImpl;
        private WeakReference<Messenger> mCallbacksMessengerRef;

        CallbackHandler(MediaBrowserServiceCallbackImpl callbackImpl) {
            this.mCallbackImpl = callbackImpl;
        }

        public void handleMessage(Message msg) {
            if (this.mCallbacksMessengerRef != null) {
                Bundle data = msg.getData();
                data.setClassLoader(MediaSessionCompat.class.getClassLoader());
                switch (msg.what) {
                    case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                        this.mCallbackImpl.onServiceConnected((Messenger) this.mCallbacksMessengerRef.get(), data.getString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID), (Token) data.getParcelable(MediaBrowserProtocol.DATA_MEDIA_SESSION_TOKEN), data.getBundle(MediaBrowserProtocol.DATA_ROOT_HINTS));
                    case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                        this.mCallbackImpl.onConnectionFailed((Messenger) this.mCallbacksMessengerRef.get());
                    case ConnectionResult.SERVICE_DISABLED /*3*/:
                        this.mCallbackImpl.onLoadChildren((Messenger) this.mCallbacksMessengerRef.get(), data.getString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID), data.getParcelableArrayList(MediaBrowserProtocol.DATA_MEDIA_ITEM_LIST), data.getBundle(MediaBrowserProtocol.DATA_OPTIONS));
                    default:
                        Log.w(MediaBrowserCompat.TAG, "Unhandled message: " + msg + "\n  Client version: " + 1 + "\n  Service version: " + msg.arg1);
                }
            }
        }

        void setCallbacksMessenger(Messenger callbacksMessenger) {
            this.mCallbacksMessengerRef = new WeakReference(callbacksMessenger);
        }
    }

    public static class ConnectionCallback {
        private ConnectionCallbackInternal mConnectionCallbackInternal;
        final Object mConnectionCallbackObj;

        interface ConnectionCallbackInternal {
            void onConnected();

            void onConnectionFailed();

            void onConnectionSuspended();
        }

        private class StubApi21 implements ConnectionCallback {
            private StubApi21() {
            }

            public void onConnected() {
                if (ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    ConnectionCallback.this.mConnectionCallbackInternal.onConnected();
                }
                ConnectionCallback.this.onConnected();
            }

            public void onConnectionSuspended() {
                if (ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    ConnectionCallback.this.mConnectionCallbackInternal.onConnectionSuspended();
                }
                ConnectionCallback.this.onConnectionSuspended();
            }

            public void onConnectionFailed() {
                if (ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    ConnectionCallback.this.mConnectionCallbackInternal.onConnectionFailed();
                }
                ConnectionCallback.this.onConnectionFailed();
            }
        }

        public ConnectionCallback() {
            if (VERSION.SDK_INT >= 21) {
                this.mConnectionCallbackObj = MediaBrowserCompatApi21.createConnectionCallback(new StubApi21());
            } else {
                this.mConnectionCallbackObj = null;
            }
        }

        public void onConnected() {
        }

        public void onConnectionSuspended() {
        }

        public void onConnectionFailed() {
        }

        void setInternalConnectionCallback(ConnectionCallbackInternal connectionCallbackInternal) {
            this.mConnectionCallbackInternal = connectionCallbackInternal;
        }
    }

    public static abstract class ItemCallback {
        final Object mItemCallbackObj;

        private class StubApi23 implements ItemCallback {
            private StubApi23() {
            }

            public void onItemLoaded(Parcel itemParcel) {
                itemParcel.setDataPosition(0);
                MediaItem item = (MediaItem) MediaItem.CREATOR.createFromParcel(itemParcel);
                itemParcel.recycle();
                ItemCallback.this.onItemLoaded(item);
            }

            public void onError(@NonNull String itemId) {
                ItemCallback.this.onError(itemId);
            }
        }

        public ItemCallback() {
            if (VERSION.SDK_INT >= 23) {
                this.mItemCallbackObj = MediaBrowserCompatApi23.createItemCallback(new StubApi23());
            } else {
                this.mItemCallbackObj = null;
            }
        }

        public void onItemLoaded(MediaItem item) {
        }

        public void onError(@NonNull String itemId) {
        }
    }

    interface MediaBrowserImpl {
        void connect();

        void disconnect();

        @Nullable
        Bundle getExtras();

        void getItem(@NonNull String str, @NonNull ItemCallback itemCallback);

        @NonNull
        String getRoot();

        ComponentName getServiceComponent();

        @NonNull
        Token getSessionToken();

        boolean isConnected();

        void subscribe(@NonNull String str, Bundle bundle, @NonNull SubscriptionCallback subscriptionCallback);

        void unsubscribe(@NonNull String str, Bundle bundle);
    }

    interface MediaBrowserServiceCallbackImpl {
        void onConnectionFailed(Messenger messenger);

        void onLoadChildren(Messenger messenger, String str, List list, Bundle bundle);

        void onServiceConnected(Messenger messenger, String str, Token token, Bundle bundle);
    }

    public static class MediaItem implements Parcelable {
        public static final Creator<MediaItem> CREATOR;
        public static final int FLAG_BROWSABLE = 1;
        public static final int FLAG_PLAYABLE = 2;
        private final MediaDescriptionCompat mDescription;
        private final int mFlags;

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaItem.1 */
        static class C00381 implements Creator<MediaItem> {
            C00381() {
            }

            public MediaItem createFromParcel(Parcel in) {
                return new MediaItem(null);
            }

            public MediaItem[] newArray(int size) {
                return new MediaItem[size];
            }
        }

        @Retention(RetentionPolicy.SOURCE)
        public @interface Flags {
        }

        public MediaItem(@NonNull MediaDescriptionCompat description, int flags) {
            if (description == null) {
                throw new IllegalArgumentException("description cannot be null");
            } else if (TextUtils.isEmpty(description.getMediaId())) {
                throw new IllegalArgumentException("description must have a non-empty media id");
            } else {
                this.mFlags = flags;
                this.mDescription = description;
            }
        }

        private MediaItem(Parcel in) {
            this.mFlags = in.readInt();
            this.mDescription = (MediaDescriptionCompat) MediaDescriptionCompat.CREATOR.createFromParcel(in);
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.mFlags);
            this.mDescription.writeToParcel(out, flags);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("MediaItem{");
            sb.append("mFlags=").append(this.mFlags);
            sb.append(", mDescription=").append(this.mDescription);
            sb.append('}');
            return sb.toString();
        }

        static {
            CREATOR = new C00381();
        }

        public int getFlags() {
            return this.mFlags;
        }

        public boolean isBrowsable() {
            return (this.mFlags & FLAG_BROWSABLE) != 0;
        }

        public boolean isPlayable() {
            return (this.mFlags & FLAG_PLAYABLE) != 0;
        }

        @NonNull
        public MediaDescriptionCompat getDescription() {
            return this.mDescription;
        }

        @NonNull
        public String getMediaId() {
            return this.mDescription.getMediaId();
        }
    }

    private static class ServiceBinderWrapper {
        private Messenger mMessenger;

        public ServiceBinderWrapper(IBinder target) {
            this.mMessenger = new Messenger(target);
        }

        void connect(Context context, Bundle rootHint, Messenger callbacksMessenger) throws RemoteException {
            Bundle data = new Bundle();
            data.putString(MediaBrowserProtocol.DATA_PACKAGE_NAME, context.getPackageName());
            data.putBundle(MediaBrowserProtocol.DATA_ROOT_HINTS, rootHint);
            sendRequest(1, data, callbacksMessenger);
        }

        void disconnect(Messenger callbacksMessenger) throws RemoteException {
            sendRequest(2, null, callbacksMessenger);
        }

        void addSubscription(String parentId, Bundle options, Messenger callbacksMessenger) throws RemoteException {
            Bundle data = new Bundle();
            data.putString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID, parentId);
            data.putBundle(MediaBrowserProtocol.DATA_OPTIONS, options);
            sendRequest(3, data, callbacksMessenger);
        }

        void removeSubscription(String parentId, Bundle options, Messenger callbacksMessenger) throws RemoteException {
            Bundle data = new Bundle();
            data.putString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID, parentId);
            data.putBundle(MediaBrowserProtocol.DATA_OPTIONS, options);
            sendRequest(4, data, callbacksMessenger);
        }

        void getMediaItem(String mediaId, ResultReceiver receiver) throws RemoteException {
            Bundle data = new Bundle();
            data.putString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID, mediaId);
            data.putParcelable(MediaBrowserProtocol.DATA_RESULT_RECEIVER, receiver);
            sendRequest(5, data, null);
        }

        void registerCallbackMessenger(Messenger callbackMessenger) throws RemoteException {
            sendRequest(6, null, callbackMessenger);
        }

        private void sendRequest(int what, Bundle data, Messenger cbMessenger) throws RemoteException {
            Message msg = Message.obtain();
            msg.what = what;
            msg.arg1 = 1;
            msg.setData(data);
            msg.replyTo = cbMessenger;
            this.mMessenger.send(msg);
        }
    }

    private static class Subscription {
        private final List<SubscriptionCallback> mCallbacks;
        private final List<Bundle> mOptionsList;

        public Subscription() {
            this.mCallbacks = new ArrayList();
            this.mOptionsList = new ArrayList();
        }

        public boolean isEmpty() {
            return this.mCallbacks.isEmpty();
        }

        public List<Bundle> getOptionsList() {
            return this.mOptionsList;
        }

        public List<SubscriptionCallback> getCallbacks() {
            return this.mCallbacks;
        }

        public SubscriptionCallback getCallback(Bundle options) {
            for (int i = 0; i < this.mOptionsList.size(); i++) {
                if (MediaBrowserCompatUtils.areSameOptions((Bundle) this.mOptionsList.get(i), options)) {
                    return (SubscriptionCallback) this.mCallbacks.get(i);
                }
            }
            return null;
        }

        public void putCallback(Bundle options, SubscriptionCallback callback) {
            for (int i = 0; i < this.mOptionsList.size(); i++) {
                if (MediaBrowserCompatUtils.areSameOptions((Bundle) this.mOptionsList.get(i), options)) {
                    this.mCallbacks.set(i, callback);
                    return;
                }
            }
            this.mCallbacks.add(callback);
            this.mOptionsList.add(options);
        }

        public boolean removeCallback(Bundle options) {
            for (int i = 0; i < this.mOptionsList.size(); i++) {
                if (MediaBrowserCompatUtils.areSameOptions((Bundle) this.mOptionsList.get(i), options)) {
                    this.mCallbacks.remove(i);
                    this.mOptionsList.remove(i);
                    return true;
                }
            }
            return false;
        }
    }

    public static abstract class SubscriptionCallback {
        public void onChildrenLoaded(@NonNull String parentId, List<MediaItem> list) {
        }

        public void onChildrenLoaded(@NonNull String parentId, List<MediaItem> list, @NonNull Bundle options) {
        }

        public void onError(@NonNull String parentId) {
        }

        public void onError(@NonNull String parentId, @NonNull Bundle options) {
        }
    }

    private static class ItemReceiver extends ResultReceiver {
        private final ItemCallback mCallback;
        private final String mMediaId;

        ItemReceiver(String mediaId, ItemCallback callback, Handler handler) {
            super(handler);
            this.mMediaId = mediaId;
            this.mCallback = callback;
        }

        protected void onReceiveResult(int resultCode, Bundle resultData) {
            resultData.setClassLoader(MediaBrowserCompat.class.getClassLoader());
            if (resultCode == 0 && resultData != null && resultData.containsKey(MediaBrowserServiceCompat.KEY_MEDIA_ITEM)) {
                Parcelable item = resultData.getParcelable(MediaBrowserServiceCompat.KEY_MEDIA_ITEM);
                if (item instanceof MediaItem) {
                    this.mCallback.onItemLoaded((MediaItem) item);
                    return;
                } else {
                    this.mCallback.onError(this.mMediaId);
                    return;
                }
            }
            this.mCallback.onError(this.mMediaId);
        }
    }

    static class MediaBrowserImplApi21 implements MediaBrowserImpl, MediaBrowserServiceCallbackImpl, ConnectionCallbackInternal {
        private static final boolean DBG = false;
        protected Object mBrowserObj;
        private Messenger mCallbacksMessenger;
        private final CallbackHandler mHandler;
        private ServiceBinderWrapper mServiceBinderWrapper;
        private final ComponentName mServiceComponent;
        private final ArrayMap<String, Subscription> mSubscriptions;

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplApi21.1 */
        class C00301 implements Runnable {
            final /* synthetic */ ItemCallback val$cb;
            final /* synthetic */ String val$mediaId;

            C00301(ItemCallback itemCallback, String str) {
                this.val$cb = itemCallback;
                this.val$mediaId = str;
            }

            public void run() {
                this.val$cb.onError(this.val$mediaId);
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplApi21.2 */
        class C00312 implements Runnable {
            final /* synthetic */ ItemCallback val$cb;

            C00312(ItemCallback itemCallback) {
                this.val$cb = itemCallback;
            }

            public void run() {
                this.val$cb.onItemLoaded(null);
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserImplApi21.3 */
        class C00323 implements Runnable {
            final /* synthetic */ ItemCallback val$cb;
            final /* synthetic */ String val$mediaId;

            C00323(ItemCallback itemCallback, String str) {
                this.val$cb = itemCallback;
                this.val$mediaId = str;
            }

            public void run() {
                this.val$cb.onError(this.val$mediaId);
            }
        }

        public MediaBrowserImplApi21(Context context, ComponentName serviceComponent, ConnectionCallback callback, Bundle rootHints) {
            this.mHandler = new CallbackHandler(this);
            this.mSubscriptions = new ArrayMap();
            this.mServiceComponent = serviceComponent;
            callback.setInternalConnectionCallback(this);
            this.mBrowserObj = MediaBrowserCompatApi21.createBrowser(context, serviceComponent, callback.mConnectionCallbackObj, rootHints);
        }

        public void connect() {
            MediaBrowserCompatApi21.connect(this.mBrowserObj);
        }

        public void disconnect() {
            MediaBrowserCompatApi21.disconnect(this.mBrowserObj);
        }

        public boolean isConnected() {
            return MediaBrowserCompatApi21.isConnected(this.mBrowserObj);
        }

        public ComponentName getServiceComponent() {
            return MediaBrowserCompatApi21.getServiceComponent(this.mBrowserObj);
        }

        @NonNull
        public String getRoot() {
            return MediaBrowserCompatApi21.getRoot(this.mBrowserObj);
        }

        @Nullable
        public Bundle getExtras() {
            return MediaBrowserCompatApi21.getExtras(this.mBrowserObj);
        }

        @NonNull
        public Token getSessionToken() {
            return Token.fromToken(MediaBrowserCompatApi21.getSessionToken(this.mBrowserObj));
        }

        public void subscribe(@NonNull String parentId, Bundle options, @NonNull SubscriptionCallback callback) {
            SubscriptionCallbackApi21 cb21 = new SubscriptionCallbackApi21(callback, options);
            Subscription sub = (Subscription) this.mSubscriptions.get(parentId);
            if (sub == null) {
                sub = new Subscription();
                this.mSubscriptions.put(parentId, sub);
            }
            sub.putCallback(options, cb21);
            if (!MediaBrowserCompatApi21.isConnected(this.mBrowserObj)) {
                return;
            }
            if (options == null || this.mServiceBinderWrapper == null) {
                MediaBrowserCompatApi21.subscribe(this.mBrowserObj, parentId, cb21.mSubscriptionCallbackObj);
                return;
            }
            try {
                this.mServiceBinderWrapper.addSubscription(parentId, options, this.mCallbacksMessenger);
            } catch (RemoteException e) {
                Log.i(MediaBrowserCompat.TAG, "Remote error subscribing media item: " + parentId);
            }
        }

        public void unsubscribe(@NonNull String parentId, Bundle options) {
            if (TextUtils.isEmpty(parentId)) {
                throw new IllegalArgumentException("parentId is empty.");
            }
            Subscription sub = (Subscription) this.mSubscriptions.get(parentId);
            if (sub != null && sub.removeCallback(options)) {
                if (options == null || this.mServiceBinderWrapper == null) {
                    if (this.mServiceBinderWrapper != null || sub.isEmpty()) {
                        MediaBrowserCompatApi21.unsubscribe(this.mBrowserObj, parentId);
                    }
                } else if (this.mServiceBinderWrapper == null) {
                    try {
                        this.mServiceBinderWrapper.removeSubscription(parentId, options, this.mCallbacksMessenger);
                    } catch (RemoteException e) {
                        Log.d(MediaBrowserCompat.TAG, "removeSubscription failed with RemoteException parentId=" + parentId);
                    }
                }
            }
            if (sub != null && sub.isEmpty()) {
                this.mSubscriptions.remove(parentId);
            }
        }

        public void getItem(@NonNull String mediaId, @NonNull ItemCallback cb) {
            if (TextUtils.isEmpty(mediaId)) {
                throw new IllegalArgumentException("mediaId is empty.");
            } else if (cb == null) {
                throw new IllegalArgumentException("cb is null.");
            } else if (!MediaBrowserCompatApi21.isConnected(this.mBrowserObj)) {
                Log.i(MediaBrowserCompat.TAG, "Not connected, unable to retrieve the MediaItem.");
                this.mHandler.post(new C00301(cb, mediaId));
            } else if (this.mServiceBinderWrapper == null) {
                this.mHandler.post(new C00312(cb));
            } else {
                try {
                    this.mServiceBinderWrapper.getMediaItem(mediaId, new ItemReceiver(mediaId, cb, this.mHandler));
                } catch (RemoteException e) {
                    Log.i(MediaBrowserCompat.TAG, "Remote error getting media item: " + mediaId);
                    this.mHandler.post(new C00323(cb, mediaId));
                }
            }
        }

        public void onConnected() {
            Bundle extras = MediaBrowserCompatApi21.getExtras(this.mBrowserObj);
            if (extras != null) {
                IBinder serviceBinder = BundleCompat.getBinder(extras, MediaBrowserProtocol.EXTRA_MESSENGER_BINDER);
                if (serviceBinder != null) {
                    this.mServiceBinderWrapper = new ServiceBinderWrapper(serviceBinder);
                    this.mCallbacksMessenger = new Messenger(this.mHandler);
                    this.mHandler.setCallbacksMessenger(this.mCallbacksMessenger);
                    try {
                        this.mServiceBinderWrapper.registerCallbackMessenger(this.mCallbacksMessenger);
                    } catch (RemoteException e) {
                        Log.i(MediaBrowserCompat.TAG, "Remote error registering client messenger.");
                    }
                    onServiceConnected(this.mCallbacksMessenger, null, null, null);
                }
            }
        }

        public void onConnectionSuspended() {
            this.mServiceBinderWrapper = null;
            this.mCallbacksMessenger = null;
        }

        public void onConnectionFailed() {
        }

        public void onServiceConnected(Messenger callback, String root, Token session, Bundle extra) {
            for (Entry<String, Subscription> subscriptionEntry : this.mSubscriptions.entrySet()) {
                String id = (String) subscriptionEntry.getKey();
                Subscription sub = (Subscription) subscriptionEntry.getValue();
                List<Bundle> optionsList = sub.getOptionsList();
                List<SubscriptionCallback> callbackList = sub.getCallbacks();
                for (int i = 0; i < optionsList.size(); i++) {
                    if (optionsList.get(i) == null) {
                        MediaBrowserCompatApi21.subscribe(this.mBrowserObj, id, ((SubscriptionCallbackApi21) callbackList.get(i)).mSubscriptionCallbackObj);
                    } else {
                        try {
                            this.mServiceBinderWrapper.addSubscription(id, (Bundle) optionsList.get(i), this.mCallbacksMessenger);
                        } catch (RemoteException e) {
                            Log.d(MediaBrowserCompat.TAG, "addSubscription failed with RemoteException parentId=" + id);
                        }
                    }
                }
            }
        }

        public void onConnectionFailed(Messenger callback) {
        }

        public void onLoadChildren(Messenger callback, String parentId, List list, @NonNull Bundle options) {
            if (this.mCallbacksMessenger == callback) {
                List<MediaItem> data = list;
                Subscription subscription = (Subscription) this.mSubscriptions.get(parentId);
                if (subscription != null) {
                    subscription.getCallback(options).onChildrenLoaded(parentId, data, options);
                }
            }
        }
    }

    static class MediaBrowserServiceImplBase implements MediaBrowserImpl, MediaBrowserServiceCallbackImpl {
        private static final int CONNECT_STATE_CONNECTED = 2;
        private static final int CONNECT_STATE_CONNECTING = 1;
        private static final int CONNECT_STATE_DISCONNECTED = 0;
        private static final int CONNECT_STATE_SUSPENDED = 3;
        private static final boolean DBG = false;
        private final ConnectionCallback mCallback;
        private Messenger mCallbacksMessenger;
        private final Context mContext;
        private Bundle mExtras;
        private final CallbackHandler mHandler;
        private Token mMediaSessionToken;
        private final Bundle mRootHints;
        private String mRootId;
        private ServiceBinderWrapper mServiceBinderWrapper;
        private final ComponentName mServiceComponent;
        private MediaServiceConnection mServiceConnection;
        private int mState;
        private final ArrayMap<String, Subscription> mSubscriptions;

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserServiceImplBase.1 */
        class C00331 implements Runnable {
            final /* synthetic */ ServiceConnection val$thisConnection;

            C00331(ServiceConnection serviceConnection) {
                this.val$thisConnection = serviceConnection;
            }

            public void run() {
                if (this.val$thisConnection == MediaBrowserServiceImplBase.this.mServiceConnection) {
                    MediaBrowserServiceImplBase.this.forceCloseConnection();
                    MediaBrowserServiceImplBase.this.mCallback.onConnectionFailed();
                }
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserServiceImplBase.2 */
        class C00342 implements Runnable {
            final /* synthetic */ ItemCallback val$cb;
            final /* synthetic */ String val$mediaId;

            C00342(ItemCallback itemCallback, String str) {
                this.val$cb = itemCallback;
                this.val$mediaId = str;
            }

            public void run() {
                this.val$cb.onError(this.val$mediaId);
            }
        }

        /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserServiceImplBase.3 */
        class C00353 implements Runnable {
            final /* synthetic */ ItemCallback val$cb;
            final /* synthetic */ String val$mediaId;

            C00353(ItemCallback itemCallback, String str) {
                this.val$cb = itemCallback;
                this.val$mediaId = str;
            }

            public void run() {
                this.val$cb.onError(this.val$mediaId);
            }
        }

        private class MediaServiceConnection implements ServiceConnection {

            /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserServiceImplBase.MediaServiceConnection.1 */
            class C00361 implements Runnable {
                final /* synthetic */ IBinder val$binder;
                final /* synthetic */ ComponentName val$name;

                C00361(ComponentName componentName, IBinder iBinder) {
                    this.val$name = componentName;
                    this.val$binder = iBinder;
                }

                public void run() {
                    if (MediaServiceConnection.this.isCurrent("onServiceConnected")) {
                        MediaBrowserServiceImplBase.this.mServiceBinderWrapper = new ServiceBinderWrapper(this.val$binder);
                        MediaBrowserServiceImplBase.this.mCallbacksMessenger = new Messenger(MediaBrowserServiceImplBase.this.mHandler);
                        MediaBrowserServiceImplBase.this.mHandler.setCallbacksMessenger(MediaBrowserServiceImplBase.this.mCallbacksMessenger);
                        MediaBrowserServiceImplBase.this.mState = MediaBrowserServiceImplBase.CONNECT_STATE_CONNECTING;
                        try {
                            MediaBrowserServiceImplBase.this.mServiceBinderWrapper.connect(MediaBrowserServiceImplBase.this.mContext, MediaBrowserServiceImplBase.this.mRootHints, MediaBrowserServiceImplBase.this.mCallbacksMessenger);
                        } catch (RemoteException e) {
                            Log.w(MediaBrowserCompat.TAG, "RemoteException during connect for " + MediaBrowserServiceImplBase.this.mServiceComponent);
                        }
                    }
                }
            }

            /* renamed from: android.support.v4.media.MediaBrowserCompat.MediaBrowserServiceImplBase.MediaServiceConnection.2 */
            class C00372 implements Runnable {
                final /* synthetic */ ComponentName val$name;

                C00372(ComponentName componentName) {
                    this.val$name = componentName;
                }

                public void run() {
                    if (MediaServiceConnection.this.isCurrent("onServiceDisconnected")) {
                        MediaBrowserServiceImplBase.this.mServiceBinderWrapper = null;
                        MediaBrowserServiceImplBase.this.mCallbacksMessenger = null;
                        MediaBrowserServiceImplBase.this.mHandler.setCallbacksMessenger(null);
                        MediaBrowserServiceImplBase.this.mState = MediaBrowserServiceImplBase.CONNECT_STATE_SUSPENDED;
                        MediaBrowserServiceImplBase.this.mCallback.onConnectionSuspended();
                    }
                }
            }

            private MediaServiceConnection() {
            }

            public void onServiceConnected(ComponentName name, IBinder binder) {
                postOrRun(new C00361(name, binder));
            }

            public void onServiceDisconnected(ComponentName name) {
                postOrRun(new C00372(name));
            }

            private void postOrRun(Runnable r) {
                if (Thread.currentThread() == MediaBrowserServiceImplBase.this.mHandler.getLooper().getThread()) {
                    r.run();
                } else {
                    MediaBrowserServiceImplBase.this.mHandler.post(r);
                }
            }

            private boolean isCurrent(String funcName) {
                if (MediaBrowserServiceImplBase.this.mServiceConnection == this) {
                    return true;
                }
                if (MediaBrowserServiceImplBase.this.mState != 0) {
                    Log.i(MediaBrowserCompat.TAG, funcName + " for " + MediaBrowserServiceImplBase.this.mServiceComponent + " with mServiceConnection=" + MediaBrowserServiceImplBase.this.mServiceConnection + " this=" + this);
                }
                return false;
            }
        }

        public MediaBrowserServiceImplBase(Context context, ComponentName serviceComponent, ConnectionCallback callback, Bundle rootHints) {
            this.mHandler = new CallbackHandler(this);
            this.mSubscriptions = new ArrayMap();
            this.mState = CONNECT_STATE_DISCONNECTED;
            if (context == null) {
                throw new IllegalArgumentException("context must not be null");
            } else if (serviceComponent == null) {
                throw new IllegalArgumentException("service component must not be null");
            } else if (callback == null) {
                throw new IllegalArgumentException("connection callback must not be null");
            } else {
                this.mContext = context;
                this.mServiceComponent = serviceComponent;
                this.mCallback = callback;
                this.mRootHints = rootHints;
            }
        }

        public void connect() {
            if (this.mState != 0) {
                throw new IllegalStateException("connect() called while not disconnected (state=" + getStateLabel(this.mState) + ")");
            } else if (this.mServiceBinderWrapper != null) {
                throw new RuntimeException("mServiceBinderWrapper should be null. Instead it is " + this.mServiceBinderWrapper);
            } else if (this.mCallbacksMessenger != null) {
                throw new RuntimeException("mCallbacksMessenger should be null. Instead it is " + this.mCallbacksMessenger);
            } else {
                this.mState = CONNECT_STATE_CONNECTING;
                Intent intent = new Intent(MediaBrowserServiceCompat.SERVICE_INTERFACE);
                intent.setComponent(this.mServiceComponent);
                ServiceConnection thisConnection = new MediaServiceConnection();
                this.mServiceConnection = thisConnection;
                boolean bound = false;
                try {
                    bound = this.mContext.bindService(intent, this.mServiceConnection, CONNECT_STATE_CONNECTING);
                } catch (Exception e) {
                    Log.e(MediaBrowserCompat.TAG, "Failed binding to service " + this.mServiceComponent);
                }
                if (!bound) {
                    this.mHandler.post(new C00331(thisConnection));
                }
            }
        }

        public void disconnect() {
            if (this.mCallbacksMessenger != null) {
                try {
                    this.mServiceBinderWrapper.disconnect(this.mCallbacksMessenger);
                } catch (RemoteException e) {
                    Log.w(MediaBrowserCompat.TAG, "RemoteException during connect for " + this.mServiceComponent);
                }
            }
            forceCloseConnection();
        }

        private void forceCloseConnection() {
            if (this.mServiceConnection != null) {
                this.mContext.unbindService(this.mServiceConnection);
            }
            this.mState = CONNECT_STATE_DISCONNECTED;
            this.mServiceConnection = null;
            this.mServiceBinderWrapper = null;
            this.mCallbacksMessenger = null;
            this.mRootId = null;
            this.mMediaSessionToken = null;
        }

        public boolean isConnected() {
            return this.mState == CONNECT_STATE_CONNECTED;
        }

        @NonNull
        public ComponentName getServiceComponent() {
            if (isConnected()) {
                return this.mServiceComponent;
            }
            throw new IllegalStateException("getServiceComponent() called while not connected (state=" + this.mState + ")");
        }

        @NonNull
        public String getRoot() {
            if (isConnected()) {
                return this.mRootId;
            }
            throw new IllegalStateException("getRoot() called while not connected(state=" + getStateLabel(this.mState) + ")");
        }

        @Nullable
        public Bundle getExtras() {
            if (isConnected()) {
                return this.mExtras;
            }
            throw new IllegalStateException("getExtras() called while not connected (state=" + getStateLabel(this.mState) + ")");
        }

        @NonNull
        public Token getSessionToken() {
            if (isConnected()) {
                return this.mMediaSessionToken;
            }
            throw new IllegalStateException("getSessionToken() called while not connected(state=" + this.mState + ")");
        }

        public void subscribe(@NonNull String parentId, Bundle options, @NonNull SubscriptionCallback callback) {
            if (TextUtils.isEmpty(parentId)) {
                throw new IllegalArgumentException("parentId is empty.");
            } else if (callback == null) {
                throw new IllegalArgumentException("callback is null");
            } else {
                Subscription sub = (Subscription) this.mSubscriptions.get(parentId);
                if (sub == null) {
                    sub = new Subscription();
                    this.mSubscriptions.put(parentId, sub);
                }
                sub.putCallback(options, callback);
                if (this.mState == CONNECT_STATE_CONNECTED) {
                    try {
                        this.mServiceBinderWrapper.addSubscription(parentId, options, this.mCallbacksMessenger);
                    } catch (RemoteException e) {
                        Log.d(MediaBrowserCompat.TAG, "addSubscription failed with RemoteException parentId=" + parentId);
                    }
                }
            }
        }

        public void unsubscribe(@NonNull String parentId, Bundle options) {
            if (TextUtils.isEmpty(parentId)) {
                throw new IllegalArgumentException("parentId is empty.");
            }
            Subscription sub = (Subscription) this.mSubscriptions.get(parentId);
            if (sub != null && sub.removeCallback(options) && this.mState == CONNECT_STATE_CONNECTED) {
                try {
                    this.mServiceBinderWrapper.removeSubscription(parentId, options, this.mCallbacksMessenger);
                } catch (RemoteException e) {
                    Log.d(MediaBrowserCompat.TAG, "removeSubscription failed with RemoteException parentId=" + parentId);
                }
            }
            if (sub != null && sub.isEmpty()) {
                this.mSubscriptions.remove(parentId);
            }
        }

        public void getItem(@NonNull String mediaId, @NonNull ItemCallback cb) {
            if (TextUtils.isEmpty(mediaId)) {
                throw new IllegalArgumentException("mediaId is empty.");
            } else if (cb == null) {
                throw new IllegalArgumentException("cb is null.");
            } else if (this.mState != CONNECT_STATE_CONNECTED) {
                Log.i(MediaBrowserCompat.TAG, "Not connected, unable to retrieve the MediaItem.");
                this.mHandler.post(new C00342(cb, mediaId));
            } else {
                try {
                    this.mServiceBinderWrapper.getMediaItem(mediaId, new ItemReceiver(mediaId, cb, this.mHandler));
                } catch (RemoteException e) {
                    Log.i(MediaBrowserCompat.TAG, "Remote error getting media item.");
                    this.mHandler.post(new C00353(cb, mediaId));
                }
            }
        }

        public void onServiceConnected(Messenger callback, String root, Token session, Bundle extra) {
            if (!isCurrent(callback, "onConnect")) {
                return;
            }
            if (this.mState != CONNECT_STATE_CONNECTING) {
                Log.w(MediaBrowserCompat.TAG, "onConnect from service while mState=" + getStateLabel(this.mState) + "... ignoring");
                return;
            }
            this.mRootId = root;
            this.mMediaSessionToken = session;
            this.mExtras = extra;
            this.mState = CONNECT_STATE_CONNECTED;
            this.mCallback.onConnected();
            try {
                for (Entry<String, Subscription> subscriptionEntry : this.mSubscriptions.entrySet()) {
                    String id = (String) subscriptionEntry.getKey();
                    for (Bundle options : ((Subscription) subscriptionEntry.getValue()).getOptionsList()) {
                        this.mServiceBinderWrapper.addSubscription(id, options, this.mCallbacksMessenger);
                    }
                }
            } catch (RemoteException e) {
                Log.d(MediaBrowserCompat.TAG, "addSubscription failed with RemoteException.");
            }
        }

        public void onConnectionFailed(Messenger callback) {
            Log.e(MediaBrowserCompat.TAG, "onConnectFailed for " + this.mServiceComponent);
            if (!isCurrent(callback, "onConnectFailed")) {
                return;
            }
            if (this.mState != CONNECT_STATE_CONNECTING) {
                Log.w(MediaBrowserCompat.TAG, "onConnect from service while mState=" + getStateLabel(this.mState) + "... ignoring");
                return;
            }
            forceCloseConnection();
            this.mCallback.onConnectionFailed();
        }

        public void onLoadChildren(Messenger callback, String parentId, List list, Bundle options) {
            if (isCurrent(callback, "onLoadChildren")) {
                List<MediaItem> data = list;
                Subscription subscription = (Subscription) this.mSubscriptions.get(parentId);
                if (subscription != null) {
                    SubscriptionCallback subscriptionCallback = subscription.getCallback(options);
                    if (subscriptionCallback == null) {
                        return;
                    }
                    if (options == null) {
                        subscriptionCallback.onChildrenLoaded(parentId, data);
                    } else {
                        subscriptionCallback.onChildrenLoaded(parentId, data, options);
                    }
                }
            }
        }

        private static String getStateLabel(int state) {
            switch (state) {
                case CONNECT_STATE_DISCONNECTED /*0*/:
                    return "CONNECT_STATE_DISCONNECTED";
                case CONNECT_STATE_CONNECTING /*1*/:
                    return "CONNECT_STATE_CONNECTING";
                case CONNECT_STATE_CONNECTED /*2*/:
                    return "CONNECT_STATE_CONNECTED";
                case CONNECT_STATE_SUSPENDED /*3*/:
                    return "CONNECT_STATE_SUSPENDED";
                default:
                    return "UNKNOWN/" + state;
            }
        }

        private boolean isCurrent(Messenger callback, String funcName) {
            if (this.mCallbacksMessenger == callback) {
                return true;
            }
            if (this.mState != 0) {
                Log.i(MediaBrowserCompat.TAG, funcName + " for " + this.mServiceComponent + " with mCallbacksMessenger=" + this.mCallbacksMessenger + " this=" + this);
            }
            return false;
        }

        void dump() {
            Log.d(MediaBrowserCompat.TAG, "MediaBrowserCompat...");
            Log.d(MediaBrowserCompat.TAG, "  mServiceComponent=" + this.mServiceComponent);
            Log.d(MediaBrowserCompat.TAG, "  mCallback=" + this.mCallback);
            Log.d(MediaBrowserCompat.TAG, "  mRootHints=" + this.mRootHints);
            Log.d(MediaBrowserCompat.TAG, "  mState=" + getStateLabel(this.mState));
            Log.d(MediaBrowserCompat.TAG, "  mServiceConnection=" + this.mServiceConnection);
            Log.d(MediaBrowserCompat.TAG, "  mServiceBinderWrapper=" + this.mServiceBinderWrapper);
            Log.d(MediaBrowserCompat.TAG, "  mCallbacksMessenger=" + this.mCallbacksMessenger);
            Log.d(MediaBrowserCompat.TAG, "  mRootId=" + this.mRootId);
            Log.d(MediaBrowserCompat.TAG, "  mMediaSessionToken=" + this.mMediaSessionToken);
        }
    }

    static class SubscriptionCallbackApi21 extends SubscriptionCallback {
        private Bundle mOptions;
        SubscriptionCallback mSubscriptionCallback;
        private final Object mSubscriptionCallbackObj;

        private class StubApi21 implements SubscriptionCallback {
            private StubApi21() {
            }

            public void onChildrenLoaded(@NonNull String parentId, List<Parcel> children) {
                List<MediaItem> mediaItems = parcelListToItemList(children);
                if (SubscriptionCallbackApi21.this.mOptions != null) {
                    SubscriptionCallbackApi21.this.onChildrenLoaded(parentId, MediaBrowserCompatUtils.applyOptions(mediaItems, SubscriptionCallbackApi21.this.mOptions), SubscriptionCallbackApi21.this.mOptions);
                } else {
                    SubscriptionCallbackApi21.this.onChildrenLoaded(parentId, mediaItems);
                }
            }

            public void onError(@NonNull String parentId) {
                if (SubscriptionCallbackApi21.this.mOptions != null) {
                    SubscriptionCallbackApi21.this.onError(parentId, SubscriptionCallbackApi21.this.mOptions);
                } else {
                    SubscriptionCallbackApi21.this.onError(parentId);
                }
            }

            List<MediaItem> parcelListToItemList(List<Parcel> parcelList) {
                if (parcelList == null) {
                    return null;
                }
                List<MediaItem> items = new ArrayList();
                for (Parcel parcel : parcelList) {
                    parcel.setDataPosition(0);
                    items.add(MediaItem.CREATOR.createFromParcel(parcel));
                    parcel.recycle();
                }
                return items;
            }
        }

        private class StubApi24 extends StubApi21 implements SubscriptionCallback {
            private StubApi24() {
                super(null);
            }

            public void onChildrenLoaded(@NonNull String parentId, @NonNull List<Parcel> children, @NonNull Bundle options) {
                SubscriptionCallbackApi21.this.onChildrenLoaded(parentId, parcelListToItemList(children), SubscriptionCallbackApi21.this.mOptions);
            }

            public void onError(@NonNull String parentId, @NonNull Bundle options) {
                SubscriptionCallbackApi21.this.onError(parentId, SubscriptionCallbackApi21.this.mOptions);
            }
        }

        public SubscriptionCallbackApi21(SubscriptionCallback callback, Bundle options) {
            this.mSubscriptionCallback = callback;
            this.mOptions = options;
            if (VERSION.SDK_INT >= 24) {
                this.mSubscriptionCallbackObj = MediaBrowserCompatApi24.createSubscriptionCallback(new StubApi24());
            } else {
                this.mSubscriptionCallbackObj = MediaBrowserCompatApi21.createSubscriptionCallback(new StubApi21());
            }
        }

        public void onChildrenLoaded(@NonNull String parentId, List<MediaItem> children) {
            this.mSubscriptionCallback.onChildrenLoaded(parentId, children);
        }

        public void onChildrenLoaded(@NonNull String parentId, List<MediaItem> children, @NonNull Bundle options) {
            this.mSubscriptionCallback.onChildrenLoaded(parentId, children, options);
        }

        public void onError(@NonNull String parentId) {
            this.mSubscriptionCallback.onError(parentId);
        }

        public void onError(@NonNull String parentId, @NonNull Bundle options) {
            this.mSubscriptionCallback.onError(parentId, options);
        }
    }

    static class MediaBrowserImplApi23 extends MediaBrowserImplApi21 {
        public MediaBrowserImplApi23(Context context, ComponentName serviceComponent, ConnectionCallback callback, Bundle rootHints) {
            super(context, serviceComponent, callback, rootHints);
        }

        public void getItem(@NonNull String mediaId, @NonNull ItemCallback cb) {
            MediaBrowserCompatApi23.getItem(this.mBrowserObj, mediaId, cb.mItemCallbackObj);
        }
    }

    static class MediaBrowserImplApi24 extends MediaBrowserImplApi23 {
        public MediaBrowserImplApi24(Context context, ComponentName serviceComponent, ConnectionCallback callback, Bundle rootHints) {
            super(context, serviceComponent, callback, rootHints);
        }

        public void subscribe(@NonNull String parentId, @NonNull Bundle options, @NonNull SubscriptionCallback callback) {
            SubscriptionCallbackApi21 cb21 = new SubscriptionCallbackApi21(callback, options);
            if (options == null) {
                MediaBrowserCompatApi21.subscribe(this.mBrowserObj, parentId, cb21.mSubscriptionCallbackObj);
            } else {
                MediaBrowserCompatApi24.subscribe(this.mBrowserObj, parentId, options, cb21.mSubscriptionCallbackObj);
            }
        }

        public void unsubscribe(@NonNull String parentId, Bundle options) {
            if (options == null) {
                MediaBrowserCompatApi21.unsubscribe(this.mBrowserObj, parentId);
            } else {
                MediaBrowserCompatApi24.unsubscribe(this.mBrowserObj, parentId, options);
            }
        }
    }

    public MediaBrowserCompat(Context context, ComponentName serviceComponent, ConnectionCallback callback, Bundle rootHints) {
        if (VERSION.SDK_INT >= 24) {
            this.mImpl = new MediaBrowserImplApi24(context, serviceComponent, callback, rootHints);
        } else if (VERSION.SDK_INT >= 23) {
            this.mImpl = new MediaBrowserImplApi23(context, serviceComponent, callback, rootHints);
        } else if (VERSION.SDK_INT >= 21) {
            this.mImpl = new MediaBrowserImplApi21(context, serviceComponent, callback, rootHints);
        } else {
            this.mImpl = new MediaBrowserServiceImplBase(context, serviceComponent, callback, rootHints);
        }
    }

    public void connect() {
        this.mImpl.connect();
    }

    public void disconnect() {
        this.mImpl.disconnect();
    }

    public boolean isConnected() {
        return this.mImpl.isConnected();
    }

    @NonNull
    public ComponentName getServiceComponent() {
        return this.mImpl.getServiceComponent();
    }

    @NonNull
    public String getRoot() {
        return this.mImpl.getRoot();
    }

    @Nullable
    public Bundle getExtras() {
        return this.mImpl.getExtras();
    }

    @NonNull
    public Token getSessionToken() {
        return this.mImpl.getSessionToken();
    }

    public void subscribe(@NonNull String parentId, @NonNull SubscriptionCallback callback) {
        this.mImpl.subscribe(parentId, null, callback);
    }

    public void subscribe(@NonNull String parentId, @NonNull Bundle options, @NonNull SubscriptionCallback callback) {
        if (options == null) {
            throw new IllegalArgumentException("options are null");
        }
        this.mImpl.subscribe(parentId, options, callback);
    }

    public void unsubscribe(@NonNull String parentId) {
        this.mImpl.unsubscribe(parentId, null);
    }

    public void unsubscribe(@NonNull String parentId, @NonNull Bundle options) {
        if (options == null) {
            throw new IllegalArgumentException("options are null");
        }
        this.mImpl.unsubscribe(parentId, options);
    }

    public void getItem(@NonNull String mediaId, @NonNull ItemCallback cb) {
        this.mImpl.getItem(mediaId, cb);
    }
}
