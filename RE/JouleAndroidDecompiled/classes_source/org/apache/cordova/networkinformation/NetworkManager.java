package org.apache.cordova.networkinformation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.util.Locale;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

public class NetworkManager extends CordovaPlugin {
    public static final String CDMA = "cdma";
    public static final String CELLULAR = "cellular";
    public static final String EDGE = "edge";
    public static final String EHRPD = "ehrpd";
    public static final String GPRS = "gprs";
    public static final String GSM = "gsm";
    public static final String HSDPA = "hsdpa";
    public static final String HSPA = "hspa";
    public static final String HSPA_PLUS = "hspa+";
    public static final String HSUPA = "hsupa";
    private static final String LOG_TAG = "NetworkManager";
    public static final String LTE = "lte";
    public static final String MOBILE = "mobile";
    public static int NOT_REACHABLE = 0;
    public static final String ONEXRTT = "1xrtt";
    public static int REACHABLE_VIA_CARRIER_DATA_NETWORK = 0;
    public static int REACHABLE_VIA_WIFI_NETWORK = 0;
    public static final String TYPE_2G = "2g";
    public static final String TYPE_3G = "3g";
    public static final String TYPE_4G = "4g";
    public static final String TYPE_ETHERNET = "ethernet";
    public static final String TYPE_NONE = "none";
    public static final String TYPE_UNKNOWN = "unknown";
    public static final String TYPE_WIFI = "wifi";
    public static final String UMB = "umb";
    public static final String UMTS = "umts";
    public static final String WIFI = "wifi";
    public static final String WIMAX = "wimax";
    private CallbackContext connectionCallbackContext;
    private JSONObject lastInfo;
    BroadcastReceiver receiver;
    ConnectivityManager sockMan;

    /* renamed from: org.apache.cordova.networkinformation.NetworkManager.1 */
    class C02831 extends BroadcastReceiver {
        C02831() {
        }

        public void onReceive(Context context, Intent intent) {
            if (NetworkManager.this.webView != null) {
                NetworkManager.this.updateConnectionInfo(NetworkManager.this.sockMan.getActiveNetworkInfo());
            }
        }
    }

    public NetworkManager() {
        this.lastInfo = null;
    }

    static {
        NOT_REACHABLE = 0;
        REACHABLE_VIA_CARRIER_DATA_NETWORK = 1;
        REACHABLE_VIA_WIFI_NETWORK = 2;
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.sockMan = (ConnectivityManager) cordova.getActivity().getSystemService("connectivity");
        this.connectionCallbackContext = null;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        if (this.receiver == null) {
            this.receiver = new C02831();
            webView.getContext().registerReceiver(this.receiver, intentFilter);
        }
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (!action.equals("getConnectionInfo")) {
            return false;
        }
        this.connectionCallbackContext = callbackContext;
        NetworkInfo info = this.sockMan.getActiveNetworkInfo();
        String connectionType = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        try {
            connectionType = getConnectionInfo(info).get(MessagingSmsConsts.TYPE).toString();
        } catch (JSONException e) {
        }
        PluginResult pluginResult = new PluginResult(Status.OK, connectionType);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
        return true;
    }

    public void onDestroy() {
        if (this.receiver != null) {
            try {
                this.webView.getContext().unregisterReceiver(this.receiver);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error unregistering network receiver: " + e.getMessage(), e);
            } finally {
                this.receiver = null;
            }
        }
    }

    private void updateConnectionInfo(NetworkInfo info) {
        JSONObject thisInfo = getConnectionInfo(info);
        if (!thisInfo.equals(this.lastInfo)) {
            String connectionType = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
            try {
                connectionType = thisInfo.get(MessagingSmsConsts.TYPE).toString();
            } catch (JSONException e) {
            }
            sendUpdate(connectionType);
            this.lastInfo = thisInfo;
        }
    }

    private JSONObject getConnectionInfo(NetworkInfo info) {
        String type = TYPE_NONE;
        String extraInfo = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        if (info != null) {
            if (info.isConnected()) {
                type = getType(info);
            } else {
                type = TYPE_NONE;
            }
            extraInfo = info.getExtraInfo();
        }
        Log.d("CordovaNetworkManager", "Connection Type: " + type);
        Log.d("CordovaNetworkManager", "Connection Extra Info: " + extraInfo);
        JSONObject connectionInfo = new JSONObject();
        try {
            connectionInfo.put(MessagingSmsConsts.TYPE, type);
            connectionInfo.put("extraInfo", extraInfo);
        } catch (JSONException e) {
        }
        return connectionInfo;
    }

    private void sendUpdate(String type) {
        if (this.connectionCallbackContext != null) {
            PluginResult result = new PluginResult(Status.OK, type);
            result.setKeepCallback(true);
            this.connectionCallbackContext.sendPluginResult(result);
        }
        this.webView.postMessage("networkconnection", type);
    }

    private String getType(NetworkInfo info) {
        if (info == null) {
            return TYPE_NONE;
        }
        String type = info.getTypeName().toLowerCase(Locale.US);
        Log.d("CordovaNetworkManager", "toLower : " + type.toLowerCase());
        Log.d("CordovaNetworkManager", "wifi : wifi");
        if (type.equals(WIFI)) {
            return WIFI;
        }
        if (type.toLowerCase().equals(TYPE_ETHERNET)) {
            return TYPE_ETHERNET;
        }
        if (type.equals(MOBILE) || type.equals(CELLULAR)) {
            type = info.getSubtypeName();
            if (type.equals(GSM) || type.equals(GPRS) || type.equals(EDGE)) {
                return TYPE_2G;
            }
            if (type.startsWith(CDMA) || type.equals(UMTS) || type.equals(ONEXRTT) || type.equals(EHRPD) || type.equals(HSUPA) || type.equals(HSDPA) || type.equals(HSPA)) {
                return TYPE_3G;
            }
            if (type.equals(LTE) || type.equals(UMB) || type.equals(HSPA_PLUS)) {
                return TYPE_4G;
            }
        }
        return TYPE_UNKNOWN;
    }
}
