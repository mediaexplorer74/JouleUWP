package de.appplant.cordova.plugin.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.support.v4.app.NotificationCompat.Builder;
import java.util.Date;
import org.chromium.ui.base.PageTransition;
import org.json.JSONException;
import org.json.JSONObject;

public class Notification {
    static final String PREF_KEY = "LocalNotification";
    private static Class<?> defaultReceiver;
    private final Builder builder;
    private final Context context;
    private final Options options;
    private Class<?> receiver;

    public enum Type {
        ALL,
        SCHEDULED,
        TRIGGERED
    }

    static {
        defaultReceiver = TriggerReceiver.class;
    }

    protected Notification(Context context, Options options, Builder builder, Class<?> receiver) {
        this.receiver = defaultReceiver;
        this.context = context;
        this.options = options;
        this.builder = builder;
        if (receiver == null) {
            receiver = defaultReceiver;
        }
        this.receiver = receiver;
    }

    public Context getContext() {
        return this.context;
    }

    public Options getOptions() {
        return this.options;
    }

    public int getId() {
        return this.options.getId().intValue();
    }

    public boolean isRepeating() {
        return getOptions().getRepeatInterval() > 0;
    }

    public boolean wasInThePast() {
        return new Date().after(this.options.getTriggerDate());
    }

    public boolean isScheduled() {
        return isRepeating() || !wasInThePast();
    }

    public boolean isTriggered() {
        return wasInThePast();
    }

    protected boolean isUpdate() {
        if (!this.options.getDict().has("updatedAt")) {
            return false;
        }
        long now = new Date().getTime();
        if (now - this.options.getDict().optLong("updatedAt", now) < 1000) {
            return true;
        }
        return false;
    }

    public Type getType() {
        return isTriggered() ? Type.TRIGGERED : Type.SCHEDULED;
    }

    public void schedule() {
        long triggerTime = this.options.getTriggerTime();
        persist();
        PendingIntent pi = PendingIntent.getBroadcast(this.context, 0, new Intent(this.context, this.receiver).setAction(this.options.getIdStr()).putExtra("NOTIFICATION_OPTIONS", this.options.toString()), PageTransition.CHAIN_START);
        if (isRepeating()) {
            getAlarmMgr().setRepeating(0, triggerTime, this.options.getRepeatInterval(), pi);
        } else {
            getAlarmMgr().set(0, triggerTime, pi);
        }
    }

    public void clear() {
        if (!isRepeating() && wasInThePast()) {
            unpersist();
        }
        if (!isRepeating()) {
            getNotMgr().cancel(getId());
        }
    }

    public void cancel() {
        getAlarmMgr().cancel(PendingIntent.getBroadcast(this.context, 0, new Intent(this.context, this.receiver).setAction(this.options.getIdStr()), 0));
        getNotMgr().cancel(this.options.getId().intValue());
        unpersist();
    }

    public void show() {
        showNotification();
    }

    private void showNotification() {
        int id = getOptions().getId().intValue();
        if (VERSION.SDK_INT <= 15) {
            getNotMgr().notify(id, this.builder.getNotification());
        } else {
            getNotMgr().notify(id, this.builder.build());
        }
    }

    public int getTriggerCountSinceSchedule() {
        long now = System.currentTimeMillis();
        long triggerTime = this.options.getTriggerTime();
        if (!wasInThePast()) {
            return 0;
        }
        if (isRepeating()) {
            return (int) ((now - triggerTime) / this.options.getRepeatInterval());
        }
        return 1;
    }

    public String toString() {
        JSONObject dict = this.options.getDict();
        JSONObject json = new JSONObject();
        try {
            json = new JSONObject(dict.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        json.remove("firstAt");
        json.remove("updatedAt");
        json.remove("soundUri");
        json.remove("iconUri");
        return json.toString();
    }

    private void persist() {
        Editor editor = getPrefs().edit();
        editor.putString(this.options.getIdStr(), this.options.toString());
        if (VERSION.SDK_INT < 9) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    private void unpersist() {
        Editor editor = getPrefs().edit();
        editor.remove(this.options.getIdStr());
        if (VERSION.SDK_INT < 9) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    private SharedPreferences getPrefs() {
        return this.context.getSharedPreferences(PREF_KEY, 0);
    }

    private NotificationManager getNotMgr() {
        return (NotificationManager) this.context.getSystemService("notification");
    }

    private AlarmManager getAlarmMgr() {
        return (AlarmManager) this.context.getSystemService(NotificationCompatApi21.CATEGORY_ALARM);
    }

    public static void setDefaultTriggerReceiver(Class<?> receiver) {
        defaultReceiver = receiver;
    }
}
