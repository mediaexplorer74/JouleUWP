package org.apache.cordova;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import com.adobe.phonegap.push.PushConstants;
import java.util.HashMap;
import org.apache.cordova.PluginResult.Status;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class CoreAndroid extends CordovaPlugin {
    public static final String PLUGIN_NAME = "CoreAndroid";
    protected static final String TAG = "CordovaApp";
    private CallbackContext messageChannel;
    private BroadcastReceiver telephonyReceiver;

    /* renamed from: org.apache.cordova.CoreAndroid.1 */
    class C02391 implements Runnable {
        C02391() {
        }

        public void run() {
            CoreAndroid.this.webView.getPluginManager().postMessage("spinner", "stop");
        }
    }

    /* renamed from: org.apache.cordova.CoreAndroid.2 */
    class C02402 implements Runnable {
        C02402() {
        }

        public void run() {
            CoreAndroid.this.webView.clearCache(true);
        }
    }

    /* renamed from: org.apache.cordova.CoreAndroid.3 */
    class C02413 implements Runnable {
        C02413() {
        }

        public void run() {
            CoreAndroid.this.webView.clearHistory();
        }
    }

    /* renamed from: org.apache.cordova.CoreAndroid.4 */
    class C02424 implements Runnable {
        C02424() {
        }

        public void run() {
            CoreAndroid.this.webView.backHistory();
        }
    }

    /* renamed from: org.apache.cordova.CoreAndroid.5 */
    class C02435 extends BroadcastReceiver {
        C02435() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("android.intent.action.PHONE_STATE") && intent.hasExtra("state")) {
                String extraData = intent.getStringExtra("state");
                if (extraData.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    LOG.m15i(CoreAndroid.TAG, "Telephone RINGING");
                    CoreAndroid.this.webView.getPluginManager().postMessage("telephone", "ringing");
                } else if (extraData.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    LOG.m15i(CoreAndroid.TAG, "Telephone OFFHOOK");
                    CoreAndroid.this.webView.getPluginManager().postMessage("telephone", "offhook");
                } else if (extraData.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    LOG.m15i(CoreAndroid.TAG, "Telephone IDLE");
                    CoreAndroid.this.webView.getPluginManager().postMessage("telephone", "idle");
                }
            }
        }
    }

    CoreAndroid() {
    }

    public void fireJavascriptEvent(String action) {
        sendEventMessage(action);
    }

    public void pluginInitialize() {
        initTelephonyReceiver();
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Status status = Status.OK;
        String result = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        try {
            if (action.equals("clearCache")) {
                clearCache();
            } else if (action.equals("show")) {
                this.cordova.getActivity().runOnUiThread(new C02391());
            } else if (action.equals("loadUrl")) {
                loadUrl(args.getString(0), args.optJSONObject(1));
            } else if (!action.equals("cancelLoadUrl")) {
                if (action.equals("clearHistory")) {
                    clearHistory();
                } else if (action.equals("backHistory")) {
                    backHistory();
                } else if (action.equals("overrideButton")) {
                    overrideButton(args.getString(0), args.getBoolean(1));
                } else if (action.equals("overrideBackbutton")) {
                    overrideBackbutton(args.getBoolean(0));
                } else if (action.equals("exitApp")) {
                    exitApp();
                } else if (action.equals("messageChannel")) {
                    this.messageChannel = callbackContext;
                    return true;
                }
            }
            callbackContext.sendPluginResult(new PluginResult(status, result));
            return true;
        } catch (JSONException e) {
            callbackContext.sendPluginResult(new PluginResult(Status.JSON_EXCEPTION));
            return false;
        }
    }

    public void clearCache() {
        this.cordova.getActivity().runOnUiThread(new C02402());
    }

    public void loadUrl(String url, JSONObject props) throws JSONException {
        LOG.m9d("App", "App.loadUrl(" + url + "," + props + ")");
        int wait = 0;
        boolean openExternal = false;
        boolean clearHistory = false;
        HashMap<String, Object> params = new HashMap();
        if (props != null) {
            JSONArray keys = props.names();
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.getString(i);
                if (key.equals("wait")) {
                    wait = props.getInt(key);
                } else if (key.equalsIgnoreCase("openexternal")) {
                    openExternal = props.getBoolean(key);
                } else if (key.equalsIgnoreCase("clearhistory")) {
                    clearHistory = props.getBoolean(key);
                } else {
                    Object value = props.get(key);
                    if (value != null) {
                        if (value.getClass().equals(String.class)) {
                            params.put(key, (String) value);
                        } else if (value.getClass().equals(Boolean.class)) {
                            params.put(key, (Boolean) value);
                        } else if (value.getClass().equals(Integer.class)) {
                            params.put(key, (Integer) value);
                        }
                    }
                }
            }
        }
        if (wait > 0) {
            try {
                synchronized (this) {
                    wait((long) wait);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.webView.showWebPage(url, openExternal, clearHistory, params);
    }

    public void clearHistory() {
        this.cordova.getActivity().runOnUiThread(new C02413());
    }

    public void backHistory() {
        this.cordova.getActivity().runOnUiThread(new C02424());
    }

    public void overrideBackbutton(boolean override) {
        LOG.m15i("App", "WARNING: Back Button Default Behavior will be overridden.  The backbutton event will be fired!");
        this.webView.setButtonPlumbedToJs(4, override);
    }

    public void overrideButton(String button, boolean override) {
        LOG.m15i("App", "WARNING: Volume Button Default Behavior will be overridden.  The volume event will be fired!");
        if (button.equals("volumeup")) {
            this.webView.setButtonPlumbedToJs(24, override);
        } else if (button.equals("volumedown")) {
            this.webView.setButtonPlumbedToJs(25, override);
        }
    }

    public boolean isBackbuttonOverridden() {
        return this.webView.isButtonPlumbedToJs(4);
    }

    public void exitApp() {
        this.webView.getPluginManager().postMessage(PushConstants.EXIT, null);
    }

    private void initTelephonyReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        this.telephonyReceiver = new C02435();
        this.webView.getContext().registerReceiver(this.telephonyReceiver, intentFilter);
    }

    private void sendEventMessage(String action) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", action);
        } catch (Throwable e) {
            LOG.m13e(TAG, "Failed to create event message", e);
        }
        PluginResult pluginResult = new PluginResult(Status.OK, obj);
        pluginResult.setKeepCallback(true);
        if (this.messageChannel != null) {
            this.messageChannel.sendPluginResult(pluginResult);
        }
    }

    public void onDestroy() {
        this.webView.getContext().unregisterReceiver(this.telephonyReceiver);
    }
}
