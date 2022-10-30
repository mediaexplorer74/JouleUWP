package com.adobe.phonegap.push;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
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

public class PushPlugin extends CordovaPlugin implements PushConstants {
    public static final String LOG_TAG = "PushPlugin";
    private static Bundle gCachedExtras;
    private static boolean gForeground;
    private static CordovaWebView gWebView;
    private static CallbackContext pushContext;

    /* renamed from: com.adobe.phonegap.push.PushPlugin.1 */
    class C01241 implements Runnable {
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ JSONArray val$data;

        C01241(CallbackContext callbackContext, JSONArray jSONArray) {
            this.val$callbackContext = callbackContext;
            this.val$data = jSONArray;
        }

        public void run() {
            PushPlugin.pushContext = this.val$callbackContext;
            JSONObject jo = null;
            Log.v(PushPlugin.LOG_TAG, "execute: data=" + this.val$data.toString());
            SharedPreferences sharedPref = PushPlugin.this.getApplicationContext().getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0);
            String token = null;
            String senderID = null;
            try {
                jo = this.val$data.getJSONObject(0).getJSONObject(PushConstants.ANDROID);
                Log.v(PushPlugin.LOG_TAG, "execute: jo=" + jo.toString());
                senderID = jo.getString(PushConstants.SENDER_ID);
                Log.v(PushPlugin.LOG_TAG, "execute: senderID=" + senderID);
                String savedSenderID = sharedPref.getString(PushConstants.SENDER_ID, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
                if (CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE.equals(sharedPref.getString(PushConstants.REGISTRATION_ID, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE))) {
                    token = InstanceID.getInstance(PushPlugin.this.getApplicationContext()).getToken(senderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                } else if (savedSenderID.equals(senderID)) {
                    token = sharedPref.getString(PushConstants.REGISTRATION_ID, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
                } else {
                    token = InstanceID.getInstance(PushPlugin.this.getApplicationContext()).getToken(senderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                }
                if (CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE.equals(token)) {
                    this.val$callbackContext.error("Empty registration ID received from GCM");
                    return;
                }
                JSONObject json = new JSONObject().put(PushConstants.REGISTRATION_ID, token);
                Log.v(PushPlugin.LOG_TAG, "onRegistered: " + json.toString());
                PushPlugin.this.subscribeToTopics(jo.optJSONArray(PushConstants.TOPICS), token);
                PushPlugin.sendEvent(json);
                if (jo != null) {
                    Editor editor = sharedPref.edit();
                    try {
                        editor.putString(PushConstants.ICON, jo.getString(PushConstants.ICON));
                    } catch (JSONException e) {
                        Log.d(PushPlugin.LOG_TAG, "no icon option");
                    }
                    try {
                        editor.putString(PushConstants.ICON_COLOR, jo.getString(PushConstants.ICON_COLOR));
                    } catch (JSONException e2) {
                        Log.d(PushPlugin.LOG_TAG, "no iconColor option");
                    }
                    editor.putBoolean(PushConstants.SOUND, jo.optBoolean(PushConstants.SOUND, true));
                    editor.putBoolean(PushConstants.VIBRATE, jo.optBoolean(PushConstants.VIBRATE, true));
                    editor.putBoolean(PushConstants.CLEAR_NOTIFICATIONS, jo.optBoolean(PushConstants.CLEAR_NOTIFICATIONS, true));
                    editor.putBoolean(PushConstants.FORCE_SHOW, jo.optBoolean(PushConstants.FORCE_SHOW, false));
                    editor.putString(PushConstants.SENDER_ID, senderID);
                    editor.putString(PushConstants.REGISTRATION_ID, token);
                    editor.commit();
                }
                if (PushPlugin.gCachedExtras != null) {
                    Log.v(PushPlugin.LOG_TAG, "sending cached extras");
                    PushPlugin.sendExtras(PushPlugin.gCachedExtras);
                    PushPlugin.gCachedExtras = null;
                }
            } catch (JSONException e3) {
                Log.e(PushPlugin.LOG_TAG, "execute: Got JSON Exception " + e3.getMessage());
                this.val$callbackContext.error(e3.getMessage());
            } catch (IOException e4) {
                Log.e(PushPlugin.LOG_TAG, "execute: Got JSON Exception " + e4.getMessage());
                this.val$callbackContext.error(e4.getMessage());
            }
        }
    }

    /* renamed from: com.adobe.phonegap.push.PushPlugin.2 */
    class C01252 implements Runnable {
        final /* synthetic */ CallbackContext val$callbackContext;
        final /* synthetic */ JSONArray val$data;

        C01252(JSONArray jSONArray, CallbackContext callbackContext) {
            this.val$data = jSONArray;
            this.val$callbackContext = callbackContext;
        }

        public void run() {
            try {
                SharedPreferences sharedPref = PushPlugin.this.getApplicationContext().getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0);
                String token = sharedPref.getString(PushConstants.REGISTRATION_ID, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
                JSONArray topics = this.val$data.optJSONArray(0);
                if (topics == null || CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE.equals(token)) {
                    InstanceID.getInstance(PushPlugin.this.getApplicationContext()).deleteInstanceID();
                    Log.v(PushPlugin.LOG_TAG, "UNREGISTER");
                    Editor editor = sharedPref.edit();
                    editor.remove(PushConstants.SOUND);
                    editor.remove(PushConstants.VIBRATE);
                    editor.remove(PushConstants.CLEAR_NOTIFICATIONS);
                    editor.remove(PushConstants.FORCE_SHOW);
                    editor.remove(PushConstants.SENDER_ID);
                    editor.remove(PushConstants.REGISTRATION_ID);
                    editor.commit();
                } else {
                    PushPlugin.this.unsubscribeFromTopics(topics, token);
                }
                this.val$callbackContext.success();
            } catch (IOException e) {
                Log.e(PushPlugin.LOG_TAG, "execute: Got JSON Exception " + e.getMessage());
                this.val$callbackContext.error(e.getMessage());
            }
        }
    }

    /* renamed from: com.adobe.phonegap.push.PushPlugin.3 */
    class C01263 implements Runnable {
        final /* synthetic */ CallbackContext val$callbackContext;

        C01263(CallbackContext callbackContext) {
            this.val$callbackContext = callbackContext;
        }

        public void run() {
            JSONObject jo = new JSONObject();
            try {
                jo.put("isEnabled", PermissionUtils.hasPermission(PushPlugin.this.getApplicationContext(), "OP_POST_NOTIFICATION"));
                PluginResult pluginResult = new PluginResult(Status.OK, jo);
                pluginResult.setKeepCallback(true);
                this.val$callbackContext.sendPluginResult(pluginResult);
            } catch (UnknownError e) {
                this.val$callbackContext.error(e.getMessage());
            } catch (JSONException e2) {
                this.val$callbackContext.error(e2.getMessage());
            }
        }
    }

    static {
        gCachedExtras = null;
        gForeground = false;
    }

    private Context getApplicationContext() {
        return this.cordova.getActivity().getApplicationContext();
    }

    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        Log.v(LOG_TAG, "execute: action=" + action);
        gWebView = this.webView;
        if (PushConstants.INITIALIZE.equals(action)) {
            this.cordova.getThreadPool().execute(new C01241(callbackContext, data));
        } else if (PushConstants.UNREGISTER.equals(action)) {
            this.cordova.getThreadPool().execute(new C01252(data, callbackContext));
        } else if (PushConstants.FINISH.equals(action)) {
            callbackContext.success();
        } else if (PushConstants.HAS_PERMISSION.equals(action)) {
            this.cordova.getThreadPool().execute(new C01263(callbackContext));
        } else {
            Log.e(LOG_TAG, "Invalid action : " + action);
            callbackContext.sendPluginResult(new PluginResult(Status.INVALID_ACTION));
            return false;
        }
        return true;
    }

    public static void sendEvent(JSONObject _json) {
        PluginResult pluginResult = new PluginResult(Status.OK, _json);
        pluginResult.setKeepCallback(true);
        if (pushContext != null) {
            pushContext.sendPluginResult(pluginResult);
        }
    }

    public static void sendError(String message) {
        PluginResult pluginResult = new PluginResult(Status.ERROR, message);
        pluginResult.setKeepCallback(true);
        if (pushContext != null) {
            pushContext.sendPluginResult(pluginResult);
        }
    }

    public static void sendExtras(Bundle extras) {
        if (extras == null) {
            return;
        }
        if (gWebView != null) {
            sendEvent(convertBundleToJson(extras));
            return;
        }
        Log.v(LOG_TAG, "sendExtras: caching extras to send at a later time.");
        gCachedExtras = extras;
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        gForeground = true;
    }

    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        gForeground = false;
        if (getApplicationContext().getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0).getBoolean(PushConstants.CLEAR_NOTIFICATIONS, true)) {
            ((NotificationManager) this.cordova.getActivity().getSystemService("notification")).cancelAll();
        }
    }

    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        gForeground = true;
    }

    public void onDestroy() {
        super.onDestroy();
        gForeground = false;
        gWebView = null;
    }

    private void subscribeToTopics(JSONArray topics, String registrationToken) {
        if (topics != null) {
            String topic = null;
            for (int i = 0; i < topics.length(); i++) {
                try {
                    topic = topics.optString(i, null);
                    if (topic != null) {
                        Log.d(LOG_TAG, "Subscribing to topic: " + topic);
                        GcmPubSub.getInstance(getApplicationContext()).subscribe(registrationToken, "/topics/" + topic, null);
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Failed to subscribe to topic: " + topic, e);
                }
            }
        }
    }

    private void unsubscribeFromTopics(JSONArray topics, String registrationToken) {
        if (topics != null) {
            String topic = null;
            for (int i = 0; i < topics.length(); i++) {
                try {
                    topic = topics.optString(i, null);
                    if (topic != null) {
                        Log.d(LOG_TAG, "Unsubscribing to topic: " + topic);
                        GcmPubSub.getInstance(getApplicationContext()).unsubscribe(registrationToken, "/topics/" + topic);
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Failed to unsubscribe to topic: " + topic, e);
                }
            }
        }
    }

    private static JSONObject convertBundleToJson(Bundle extras) {
        Log.d(LOG_TAG, "convert extras to json");
        JSONObject json = new JSONObject();
        JSONObject additionalData = new JSONObject();
        HashSet<String> jsonKeySet = new HashSet();
        Collections.addAll(jsonKeySet, new String[]{PushConstants.TITLE, PushConstants.MESSAGE, PushConstants.COUNT, PushConstants.SOUND, PushConstants.IMAGE});
        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            Log.d(LOG_TAG, "key = " + key);
            if (jsonKeySet.contains(key)) {
                json.put(key, value);
            } else if (key.equals(PushConstants.COLDSTART)) {
                additionalData.put(key, extras.getBoolean(PushConstants.COLDSTART));
            } else if (key.equals(PushConstants.FOREGROUND)) {
                additionalData.put(key, extras.getBoolean(PushConstants.FOREGROUND));
            } else if (value instanceof String) {
                String strValue = (String) value;
                try {
                    if (strValue.startsWith("{")) {
                        additionalData.put(key, new JSONObject(strValue));
                    } else if (strValue.startsWith("[")) {
                        additionalData.put(key, new JSONArray(strValue));
                    } else {
                        additionalData.put(key, value);
                    }
                } catch (Exception e) {
                    try {
                        additionalData.put(key, value);
                    } catch (JSONException e2) {
                        Log.e(LOG_TAG, "extrasToJSON: JSON exception");
                        return null;
                    }
                }
            } else {
                continue;
            }
        }
        json.put(PushConstants.ADDITIONAL_DATA, additionalData);
        Log.v(LOG_TAG, "extrasToJSON: " + json.toString());
        return json;
    }

    public static boolean isInForeground() {
        return gForeground;
    }

    public static boolean isActive() {
        return gWebView != null;
    }
}
