package de.appplant.cordova.plugin.localnotification;

import android.app.Activity;
import com.adobe.phonegap.push.PushConstants;
import de.appplant.cordova.plugin.notification.Manager;
import de.appplant.cordova.plugin.notification.Notification;
import de.appplant.cordova.plugin.notification.Notification.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocalNotification extends CordovaPlugin {
    private static Boolean deviceready;
    private static ArrayList<String> eventQueue;
    protected static Boolean isInBackground;
    private static CordovaWebView webView;

    /* renamed from: de.appplant.cordova.plugin.localnotification.LocalNotification.1 */
    class C02151 implements Runnable {
        final /* synthetic */ String val$action;
        final /* synthetic */ JSONArray val$args;
        final /* synthetic */ CallbackContext val$command;

        C02151(String str, JSONArray jSONArray, CallbackContext callbackContext) {
            this.val$action = str;
            this.val$args = jSONArray;
            this.val$command = callbackContext;
        }

        public void run() {
            if (this.val$action.equals("schedule")) {
                LocalNotification.this.schedule(this.val$args);
                this.val$command.success();
            } else if (this.val$action.equals("update")) {
                LocalNotification.this.update(this.val$args);
                this.val$command.success();
            } else if (this.val$action.equals("cancel")) {
                LocalNotification.this.cancel(this.val$args);
                this.val$command.success();
            } else if (this.val$action.equals("cancelAll")) {
                LocalNotification.this.cancelAll();
                this.val$command.success();
            } else if (this.val$action.equals("clear")) {
                LocalNotification.this.clear(this.val$args);
                this.val$command.success();
            } else if (this.val$action.equals("clearAll")) {
                LocalNotification.this.clearAll();
                this.val$command.success();
            } else if (this.val$action.equals("isPresent")) {
                LocalNotification.this.isPresent(this.val$args.optInt(0), this.val$command);
            } else if (this.val$action.equals("isScheduled")) {
                LocalNotification.this.isScheduled(this.val$args.optInt(0), this.val$command);
            } else if (this.val$action.equals("isTriggered")) {
                LocalNotification.this.isTriggered(this.val$args.optInt(0), this.val$command);
            } else if (this.val$action.equals("getAllIds")) {
                LocalNotification.this.getAllIds(this.val$command);
            } else if (this.val$action.equals("getScheduledIds")) {
                LocalNotification.this.getScheduledIds(this.val$command);
            } else if (this.val$action.equals("getTriggeredIds")) {
                LocalNotification.this.getTriggeredIds(this.val$command);
            } else if (this.val$action.equals("getSingle")) {
                LocalNotification.this.getSingle(this.val$args, this.val$command);
            } else if (this.val$action.equals("getSingleScheduled")) {
                LocalNotification.this.getSingleScheduled(this.val$args, this.val$command);
            } else if (this.val$action.equals("getSingleTriggered")) {
                LocalNotification.this.getSingleTriggered(this.val$args, this.val$command);
            } else if (this.val$action.equals("getAll")) {
                LocalNotification.this.getAll(this.val$args, this.val$command);
            } else if (this.val$action.equals("getScheduled")) {
                LocalNotification.this.getScheduled(this.val$args, this.val$command);
            } else if (this.val$action.equals("getTriggered")) {
                LocalNotification.this.getTriggered(this.val$args, this.val$command);
            } else if (this.val$action.equals("deviceready")) {
                LocalNotification.deviceready();
            }
        }
    }

    /* renamed from: de.appplant.cordova.plugin.localnotification.LocalNotification.2 */
    static class C02162 implements Runnable {
        final /* synthetic */ String val$js;

        C02162(String str) {
            this.val$js = str;
        }

        public void run() {
            LocalNotification.webView.loadUrl("javascript:" + this.val$js);
        }
    }

    static {
        webView = null;
        deviceready = Boolean.valueOf(false);
        isInBackground = Boolean.valueOf(true);
        eventQueue = new ArrayList();
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        webView = this.webView;
    }

    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        isInBackground = Boolean.valueOf(true);
    }

    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        isInBackground = Boolean.valueOf(false);
        deviceready();
    }

    public void onDestroy() {
        deviceready = Boolean.valueOf(false);
        isInBackground = Boolean.valueOf(true);
    }

    public boolean execute(String action, JSONArray args, CallbackContext command) throws JSONException {
        Notification.setDefaultTriggerReceiver(TriggerReceiver.class);
        this.cordova.getThreadPool().execute(new C02151(action, args, command));
        return true;
    }

    private void schedule(JSONArray notifications) {
        for (int i = 0; i < notifications.length(); i++) {
            fireEvent("schedule", getNotificationMgr().schedule(notifications.optJSONObject(i), TriggerReceiver.class));
        }
    }

    private void update(JSONArray updates) {
        for (int i = 0; i < updates.length(); i++) {
            JSONObject update = updates.optJSONObject(i);
            fireEvent("update", getNotificationMgr().update(update.optInt("id", 0), update, TriggerReceiver.class));
        }
    }

    private void cancel(JSONArray ids) {
        for (int i = 0; i < ids.length(); i++) {
            Notification notification = getNotificationMgr().cancel(ids.optInt(i, 0));
            if (notification != null) {
                fireEvent("cancel", notification);
            }
        }
    }

    private void cancelAll() {
        getNotificationMgr().cancelAll();
        fireEvent("cancelall");
    }

    private void clear(JSONArray ids) {
        for (int i = 0; i < ids.length(); i++) {
            Notification notification = getNotificationMgr().clear(ids.optInt(i, 0));
            if (notification != null) {
                fireEvent("clear", notification);
            }
        }
    }

    private void clearAll() {
        getNotificationMgr().clearAll();
        fireEvent("clearall");
    }

    private void isPresent(int id, CallbackContext command) {
        command.sendPluginResult(new PluginResult(Status.OK, getNotificationMgr().exist(id)));
    }

    private void isScheduled(int id, CallbackContext command) {
        command.sendPluginResult(new PluginResult(Status.OK, getNotificationMgr().exist(id, Type.SCHEDULED)));
    }

    private void isTriggered(int id, CallbackContext command) {
        command.sendPluginResult(new PluginResult(Status.OK, getNotificationMgr().exist(id, Type.TRIGGERED)));
    }

    private void getAllIds(CallbackContext command) {
        command.success(new JSONArray(getNotificationMgr().getIds()));
    }

    private void getScheduledIds(CallbackContext command) {
        command.success(new JSONArray(getNotificationMgr().getIdsByType(Type.SCHEDULED)));
    }

    private void getTriggeredIds(CallbackContext command) {
        command.success(new JSONArray(getNotificationMgr().getIdsByType(Type.TRIGGERED)));
    }

    private void getSingle(JSONArray ids, CallbackContext command) {
        getOptions(ids.optString(0), Type.ALL, command);
    }

    private void getSingleScheduled(JSONArray ids, CallbackContext command) {
        getOptions(ids.optString(0), Type.SCHEDULED, command);
    }

    private void getSingleTriggered(JSONArray ids, CallbackContext command) {
        getOptions(ids.optString(0), Type.TRIGGERED, command);
    }

    private void getAll(JSONArray ids, CallbackContext command) {
        getOptions(ids, Type.ALL, command);
    }

    private void getScheduled(JSONArray ids, CallbackContext command) {
        getOptions(ids, Type.SCHEDULED, command);
    }

    private void getTriggered(JSONArray ids, CallbackContext command) {
        getOptions(ids, Type.TRIGGERED, command);
    }

    private void getOptions(String id, Type type, CallbackContext command) {
        command.success((JSONObject) getNotificationMgr().getOptionsBy(type, toList(new JSONArray().put(id))).get(0));
    }

    private void getOptions(JSONArray ids, Type type, CallbackContext command) {
        List<JSONObject> options;
        if (ids.length() == 0) {
            options = getNotificationMgr().getOptionsByType(type);
        } else {
            options = getNotificationMgr().getOptionsBy(type, toList(ids));
        }
        command.success(new JSONArray(options));
    }

    private static synchronized void deviceready() {
        synchronized (LocalNotification.class) {
            isInBackground = Boolean.valueOf(false);
            deviceready = Boolean.valueOf(true);
            Iterator it = eventQueue.iterator();
            while (it.hasNext()) {
                sendJavascript((String) it.next());
            }
            eventQueue.clear();
        }
    }

    private void fireEvent(String event) {
        fireEvent(event, null);
    }

    static void fireEvent(String event, Notification notification) {
        String params = "\"" + getApplicationState() + "\"";
        if (notification != null) {
            params = notification.toString() + "," + params;
        }
        sendJavascript("cordova.plugins.notification.local.core.fireEvent(\"" + event + "\"," + params + ")");
    }

    private static synchronized void sendJavascript(String js) {
        synchronized (LocalNotification.class) {
            if (deviceready.booleanValue()) {
                Runnable jsLoader = new C02162(js);
                try {
                    webView.getClass().getMethod("post", new Class[]{Runnable.class}).invoke(webView, new Object[]{jsLoader});
                } catch (Exception e) {
                    ((Activity) webView.getContext()).runOnUiThread(jsLoader);
                }
            } else {
                eventQueue.add(js);
            }
        }
    }

    private List<Integer> toList(JSONArray ary) {
        ArrayList<Integer> list = new ArrayList();
        for (int i = 0; i < ary.length(); i++) {
            list.add(Integer.valueOf(ary.optInt(i)));
        }
        return list;
    }

    static String getApplicationState() {
        return isInBackground.booleanValue() ? "background" : PushConstants.FOREGROUND;
    }

    private Manager getNotificationMgr() {
        return Manager.getInstance(this.cordova.getActivity());
    }
}
