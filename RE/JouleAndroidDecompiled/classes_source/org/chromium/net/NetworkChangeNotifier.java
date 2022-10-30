package org.chromium.net;

import android.content.Context;
import java.util.ArrayList;
import java.util.Iterator;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.NativeClassQualifiedName;
import org.chromium.base.ObserverList;
import org.chromium.net.NetworkChangeNotifierAutoDetect.Observer;

@JNINamespace("net")
public class NetworkChangeNotifier {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static NetworkChangeNotifier sInstance;
    private NetworkChangeNotifierAutoDetect mAutoDetector;
    private final ObserverList<ConnectionTypeObserver> mConnectionTypeObservers;
    private final Context mContext;
    private int mCurrentConnectionType;
    private double mCurrentMaxBandwidth;
    private final ArrayList<Long> mNativeChangeNotifiers;

    public interface ConnectionTypeObserver {
        void onConnectionTypeChanged(int i);
    }

    /* renamed from: org.chromium.net.NetworkChangeNotifier.1 */
    class C06391 implements Observer {
        C06391() {
        }

        public void onConnectionTypeChanged(int newConnectionType) {
            NetworkChangeNotifier.this.updateCurrentConnectionType(newConnectionType);
        }

        public void onMaxBandwidthChanged(double maxBandwidthMbps) {
            NetworkChangeNotifier.this.updateCurrentMaxBandwidth(maxBandwidthMbps);
        }
    }

    private static native double nativeGetMaxBandwidthForConnectionSubtype(int i);

    @NativeClassQualifiedName("NetworkChangeNotifierDelegateAndroid")
    private native void nativeNotifyConnectionTypeChanged(long j, int i);

    @NativeClassQualifiedName("NetworkChangeNotifierDelegateAndroid")
    private native void nativeNotifyMaxBandwidthChanged(long j, double d);

    static {
        $assertionsDisabled = !NetworkChangeNotifier.class.desiredAssertionStatus();
    }

    private NetworkChangeNotifier(Context context) {
        this.mCurrentConnectionType = 0;
        this.mCurrentMaxBandwidth = Double.POSITIVE_INFINITY;
        this.mContext = context.getApplicationContext();
        this.mNativeChangeNotifiers = new ArrayList();
        this.mConnectionTypeObservers = new ObserverList();
    }

    @CalledByNative
    public static NetworkChangeNotifier init(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkChangeNotifier(context);
        }
        return sInstance;
    }

    public static boolean isInitialized() {
        return sInstance != null;
    }

    static void resetInstanceForTests(Context context) {
        sInstance = new NetworkChangeNotifier(context);
    }

    @CalledByNative
    public int getCurrentConnectionType() {
        return this.mCurrentConnectionType;
    }

    @CalledByNative
    public double getCurrentMaxBandwidthInMbps() {
        return this.mCurrentMaxBandwidth;
    }

    public static double getMaxBandwidthForConnectionSubtype(int subtype) {
        return nativeGetMaxBandwidthForConnectionSubtype(subtype);
    }

    @CalledByNative
    public void addNativeObserver(long nativeChangeNotifier) {
        this.mNativeChangeNotifiers.add(Long.valueOf(nativeChangeNotifier));
    }

    @CalledByNative
    public void removeNativeObserver(long nativeChangeNotifier) {
        this.mNativeChangeNotifiers.remove(Long.valueOf(nativeChangeNotifier));
    }

    public static NetworkChangeNotifier getInstance() {
        if ($assertionsDisabled || sInstance != null) {
            return sInstance;
        }
        throw new AssertionError();
    }

    public static void setAutoDetectConnectivityState(boolean shouldAutoDetect) {
        getInstance().setAutoDetectConnectivityStateInternal(shouldAutoDetect, false);
    }

    private void destroyAutoDetector() {
        if (this.mAutoDetector != null) {
            this.mAutoDetector.destroy();
            this.mAutoDetector = null;
        }
    }

    public static void registerToReceiveNotificationsAlways() {
        getInstance().setAutoDetectConnectivityStateInternal(true, true);
    }

    private void setAutoDetectConnectivityStateInternal(boolean shouldAutoDetect, boolean alwaysWatchForChanges) {
        if (!shouldAutoDetect) {
            destroyAutoDetector();
        } else if (this.mAutoDetector == null) {
            this.mAutoDetector = new NetworkChangeNotifierAutoDetect(new C06391(), this.mContext, alwaysWatchForChanges);
            NetworkState networkState = this.mAutoDetector.getCurrentNetworkState();
            updateCurrentConnectionType(this.mAutoDetector.getCurrentConnectionType(networkState));
            updateCurrentMaxBandwidth(this.mAutoDetector.getCurrentMaxBandwidthInMbps(networkState));
        }
    }

    @CalledByNative
    public static void forceConnectivityState(boolean networkAvailable) {
        setAutoDetectConnectivityState(false);
        getInstance().forceConnectivityStateInternal(networkAvailable);
    }

    private void forceConnectivityStateInternal(boolean forceOnline) {
        boolean connectionCurrentlyExists;
        int i = 0;
        if (this.mCurrentConnectionType != 6) {
            connectionCurrentlyExists = true;
        } else {
            connectionCurrentlyExists = false;
        }
        if (connectionCurrentlyExists != forceOnline) {
            if (!forceOnline) {
                i = 6;
            }
            updateCurrentConnectionType(i);
            updateCurrentMaxBandwidth(forceOnline ? Double.POSITIVE_INFINITY : 0.0d);
        }
    }

    private void updateCurrentConnectionType(int newConnectionType) {
        this.mCurrentConnectionType = newConnectionType;
        notifyObserversOfConnectionTypeChange(newConnectionType);
    }

    private void updateCurrentMaxBandwidth(double maxBandwidthMbps) {
        if (maxBandwidthMbps != this.mCurrentMaxBandwidth) {
            this.mCurrentMaxBandwidth = maxBandwidthMbps;
            notifyObserversOfMaxBandwidthChange(maxBandwidthMbps);
        }
    }

    void notifyObserversOfConnectionTypeChange(int newConnectionType) {
        Iterator i$ = this.mNativeChangeNotifiers.iterator();
        while (i$.hasNext()) {
            nativeNotifyConnectionTypeChanged(((Long) i$.next()).longValue(), newConnectionType);
        }
        i$ = this.mConnectionTypeObservers.iterator();
        while (i$.hasNext()) {
            ((ConnectionTypeObserver) i$.next()).onConnectionTypeChanged(newConnectionType);
        }
    }

    void notifyObserversOfMaxBandwidthChange(double maxBandwidthMbps) {
        Iterator i$ = this.mNativeChangeNotifiers.iterator();
        while (i$.hasNext()) {
            nativeNotifyMaxBandwidthChanged(((Long) i$.next()).longValue(), maxBandwidthMbps);
        }
    }

    public static void addConnectionTypeObserver(ConnectionTypeObserver observer) {
        getInstance().addConnectionTypeObserverInternal(observer);
    }

    private void addConnectionTypeObserverInternal(ConnectionTypeObserver observer) {
        this.mConnectionTypeObservers.addObserver(observer);
    }

    public static void removeConnectionTypeObserver(ConnectionTypeObserver observer) {
        getInstance().removeConnectionTypeObserverInternal(observer);
    }

    private void removeConnectionTypeObserverInternal(ConnectionTypeObserver observer) {
        this.mConnectionTypeObservers.removeObserver(observer);
    }

    public static NetworkChangeNotifierAutoDetect getAutoDetectorForTest() {
        return getInstance().mAutoDetector;
    }

    public static boolean isOnline() {
        int connectionType = getInstance().getCurrentConnectionType();
        return (connectionType == 0 || connectionType == 6) ? false : true;
    }
}
