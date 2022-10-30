package org.chromium.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import org.apache.cordova.camera.CameraLauncher;
import org.apache.cordova.networkinformation.NetworkManager;
import org.chromium.base.ApplicationStatus;
import org.chromium.base.ApplicationStatus.ApplicationStateListener;
import org.chromium.base.VisibleForTesting;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.chromium.ui.base.ime.TextInputType;

public class NetworkChangeNotifierAutoDetect extends BroadcastReceiver implements ApplicationStateListener {
    private static final String TAG = "NetworkChangeNotifierAutoDetect";
    private static final int UNKNOWN_LINK_SPEED = -1;
    private int mConnectionType;
    private ConnectivityManagerDelegate mConnectivityManagerDelegate;
    private final Context mContext;
    private final NetworkConnectivityIntentFilter mIntentFilter;
    private double mMaxBandwidthMbps;
    private final Observer mObserver;
    private boolean mRegistered;
    private WifiManagerDelegate mWifiManagerDelegate;
    private String mWifiSSID;

    static class ConnectivityManagerDelegate {
        private final ConnectivityManager mConnectivityManager;

        ConnectivityManagerDelegate(Context context) {
            this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        }

        ConnectivityManagerDelegate() {
            this.mConnectivityManager = null;
        }

        NetworkState getNetworkState() {
            NetworkInfo networkInfo = this.mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                return new NetworkState(false, NetworkChangeNotifierAutoDetect.UNKNOWN_LINK_SPEED, NetworkChangeNotifierAutoDetect.UNKNOWN_LINK_SPEED);
            }
            return new NetworkState(true, networkInfo.getType(), networkInfo.getSubtype());
        }
    }

    private static class NetworkConnectivityIntentFilter extends IntentFilter {
        NetworkConnectivityIntentFilter(boolean monitorRSSI) {
            addAction("android.net.conn.CONNECTIVITY_CHANGE");
            if (monitorRSSI) {
                addAction("android.net.wifi.RSSI_CHANGED");
            }
        }
    }

    static class NetworkState {
        private final boolean mConnected;
        private final int mSubtype;
        private final int mType;

        public NetworkState(boolean connected, int type, int subtype) {
            this.mConnected = connected;
            this.mType = type;
            this.mSubtype = subtype;
        }

        public boolean isConnected() {
            return this.mConnected;
        }

        public int getNetworkType() {
            return this.mType;
        }

        public int getNetworkSubType() {
            return this.mSubtype;
        }
    }

    public interface Observer {
        void onConnectionTypeChanged(int i);

        void onMaxBandwidthChanged(double d);
    }

    static class WifiManagerDelegate {
        private final Context mContext;
        private final boolean mHasWifiPermission;
        private final WifiManager mWifiManager;

        WifiManagerDelegate(Context context) {
            this.mContext = context;
            this.mHasWifiPermission = this.mContext.getPackageManager().checkPermission("android.permission.ACCESS_WIFI_STATE", this.mContext.getPackageName()) == 0;
            this.mWifiManager = this.mHasWifiPermission ? (WifiManager) this.mContext.getSystemService(NetworkManager.WIFI) : null;
        }

        WifiManagerDelegate() {
            this.mContext = null;
            this.mWifiManager = null;
            this.mHasWifiPermission = false;
        }

        String getWifiSSID() {
            Intent intent = this.mContext.registerReceiver(null, new IntentFilter("android.net.wifi.STATE_CHANGE"));
            if (intent != null) {
                WifiInfo wifiInfo = (WifiInfo) intent.getParcelableExtra("wifiInfo");
                if (wifiInfo != null) {
                    String ssid = wifiInfo.getSSID();
                    if (ssid != null) {
                        return ssid;
                    }
                }
            }
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }

        int getLinkSpeedInMbps() {
            if (!this.mHasWifiPermission || this.mWifiManager == null) {
                return NetworkChangeNotifierAutoDetect.UNKNOWN_LINK_SPEED;
            }
            WifiInfo wifiInfo = this.mWifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo.getLinkSpeed();
            }
            return NetworkChangeNotifierAutoDetect.UNKNOWN_LINK_SPEED;
        }

        boolean getHasWifiPermission() {
            return this.mHasWifiPermission;
        }
    }

    public NetworkChangeNotifierAutoDetect(Observer observer, Context context, boolean alwaysWatchForChanges) {
        this.mObserver = observer;
        this.mContext = context.getApplicationContext();
        this.mConnectivityManagerDelegate = new ConnectivityManagerDelegate(context);
        this.mWifiManagerDelegate = new WifiManagerDelegate(context);
        NetworkState networkState = this.mConnectivityManagerDelegate.getNetworkState();
        this.mConnectionType = getCurrentConnectionType(networkState);
        this.mWifiSSID = getCurrentWifiSSID(networkState);
        this.mMaxBandwidthMbps = getCurrentMaxBandwidthInMbps(networkState);
        this.mIntentFilter = new NetworkConnectivityIntentFilter(this.mWifiManagerDelegate.getHasWifiPermission());
        if (alwaysWatchForChanges) {
            registerReceiver();
            return;
        }
        ApplicationStatus.registerApplicationStateListener(this);
        onApplicationStateChange(getApplicationState());
    }

    void setConnectivityManagerDelegateForTests(ConnectivityManagerDelegate delegate) {
        this.mConnectivityManagerDelegate = delegate;
    }

    void setWifiManagerDelegateForTests(WifiManagerDelegate delegate) {
        this.mWifiManagerDelegate = delegate;
    }

    @VisibleForTesting
    int getApplicationState() {
        return ApplicationStatus.getStateForApplication();
    }

    @VisibleForTesting
    boolean isReceiverRegisteredForTesting() {
        return this.mRegistered;
    }

    public void destroy() {
        unregisterReceiver();
    }

    private void registerReceiver() {
        if (!this.mRegistered) {
            this.mRegistered = true;
            this.mContext.registerReceiver(this, this.mIntentFilter);
        }
    }

    private void unregisterReceiver() {
        if (this.mRegistered) {
            this.mRegistered = false;
            this.mContext.unregisterReceiver(this);
        }
    }

    public NetworkState getCurrentNetworkState() {
        return this.mConnectivityManagerDelegate.getNetworkState();
    }

    public int getCurrentConnectionType(NetworkState networkState) {
        if (!networkState.isConnected()) {
            return 6;
        }
        switch (networkState.getNetworkType()) {
            case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                switch (networkState.getNetworkSubType()) {
                    case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                    case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                    case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    case ConnectionResult.NETWORK_ERROR /*7*/:
                    case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                        return 3;
                    case ConnectionResult.SERVICE_DISABLED /*3*/:
                    case ConnectionResult.INVALID_ACCOUNT /*5*/:
                    case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                    case ConnectionResult.INTERNAL_ERROR /*8*/:
                    case ConnectionResult.SERVICE_INVALID /*9*/:
                    case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    case TextInputType.TIME /*12*/:
                    case ConnectionResult.TIMEOUT /*14*/:
                    case ConnectionResult.INTERRUPTED /*15*/:
                        return 4;
                    case ConnectionResult.CANCELED /*13*/:
                        return 5;
                    default:
                        return 0;
                }
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                return 2;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                return 5;
            case ConnectionResult.NETWORK_ERROR /*7*/:
                return 7;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return 1;
            default:
                return 0;
        }
    }

    public double getCurrentMaxBandwidthInMbps(NetworkState networkState) {
        if (getCurrentConnectionType(networkState) == 2) {
            int link_speed = this.mWifiManagerDelegate.getLinkSpeedInMbps();
            if (link_speed != UNKNOWN_LINK_SPEED) {
                return (double) link_speed;
            }
        }
        return NetworkChangeNotifier.getMaxBandwidthForConnectionSubtype(getCurrentConnectionSubtype(networkState));
    }

    private int getCurrentConnectionSubtype(NetworkState networkState) {
        if (!networkState.isConnected()) {
            return 31;
        }
        switch (networkState.getNetworkType()) {
            case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                switch (networkState.getNetworkSubType()) {
                    case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                        return 4;
                    case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                        return 5;
                    case ConnectionResult.SERVICE_DISABLED /*3*/:
                        return 6;
                    case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                        return 2;
                    case ConnectionResult.INVALID_ACCOUNT /*5*/:
                        return 7;
                    case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                        return 8;
                    case ConnectionResult.NETWORK_ERROR /*7*/:
                        return 3;
                    case ConnectionResult.INTERNAL_ERROR /*8*/:
                        return 11;
                    case ConnectionResult.SERVICE_INVALID /*9*/:
                        return 12;
                    case ConnectionResult.DEVELOPER_ERROR /*10*/:
                        return 9;
                    case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                        return 1;
                    case TextInputType.TIME /*12*/:
                        return 10;
                    case ConnectionResult.CANCELED /*13*/:
                        return 15;
                    case ConnectionResult.TIMEOUT /*14*/:
                        return 13;
                    case ConnectionResult.INTERRUPTED /*15*/:
                        return 14;
                    default:
                        return 30;
                }
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
            case ConnectionResult.NETWORK_ERROR /*7*/:
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return 30;
            default:
                return 30;
        }
    }

    private String getCurrentWifiSSID(NetworkState networkState) {
        if (getCurrentConnectionType(networkState) != 2) {
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
        return this.mWifiManagerDelegate.getWifiSSID();
    }

    public void onReceive(Context context, Intent intent) {
        NetworkState networkState = getCurrentNetworkState();
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            connectionTypeChanged(networkState);
            maxBandwidthChanged(networkState);
        } else if ("android.net.wifi.RSSI_CHANGED".equals(intent.getAction())) {
            maxBandwidthChanged(networkState);
        }
    }

    public void onApplicationStateChange(int newState) {
        NetworkState networkState = getCurrentNetworkState();
        if (newState == 1) {
            connectionTypeChanged(networkState);
            maxBandwidthChanged(networkState);
            registerReceiver();
        } else if (newState == 2) {
            unregisterReceiver();
        }
    }

    private void connectionTypeChanged(NetworkState networkState) {
        int newConnectionType = getCurrentConnectionType(networkState);
        String newWifiSSID = getCurrentWifiSSID(networkState);
        if (newConnectionType != this.mConnectionType || !newWifiSSID.equals(this.mWifiSSID)) {
            this.mConnectionType = newConnectionType;
            this.mWifiSSID = newWifiSSID;
            Log.d(TAG, "Network connectivity changed, type is: " + this.mConnectionType);
            this.mObserver.onConnectionTypeChanged(newConnectionType);
        }
    }

    private void maxBandwidthChanged(NetworkState networkState) {
        double newMaxBandwidthMbps = getCurrentMaxBandwidthInMbps(networkState);
        if (newMaxBandwidthMbps != this.mMaxBandwidthMbps) {
            this.mMaxBandwidthMbps = newMaxBandwidthMbps;
            this.mObserver.onMaxBandwidthChanged(newMaxBandwidthMbps);
        }
    }
}
