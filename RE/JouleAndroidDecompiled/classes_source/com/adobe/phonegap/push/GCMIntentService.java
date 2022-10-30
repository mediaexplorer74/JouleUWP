package com.adobe.phonegap.push;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.System;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.text.Html;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.chromium.ui.base.PageTransition;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

@SuppressLint({"NewApi"})
public class GCMIntentService extends GcmListenerService implements PushConstants {
    private static final String LOG_TAG = "PushPlugin_GCMIntentService";
    private static HashMap<Integer, ArrayList<String>> messageMap;

    static {
        messageMap = new HashMap();
    }

    public void setNotification(int notId, String message) {
        ArrayList<String> messageList = (ArrayList) messageMap.get(Integer.valueOf(notId));
        if (messageList == null) {
            messageList = new ArrayList();
            messageMap.put(Integer.valueOf(notId), messageList);
        }
        if (message.isEmpty()) {
            messageList.clear();
        } else {
            messageList.add(message);
        }
    }

    public void onMessageReceived(String from, Bundle extras) {
        Log.d(LOG_TAG, "onMessage - from: " + from);
        if (extras != null) {
            boolean forceShow = getApplicationContext().getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0).getBoolean(PushConstants.FORCE_SHOW, false);
            extras = normalizeExtras(extras);
            if (!forceShow && PushPlugin.isInForeground()) {
                Log.d(LOG_TAG, PushConstants.FOREGROUND);
                extras.putBoolean(PushConstants.FOREGROUND, true);
                PushPlugin.sendExtras(extras);
            } else if (forceShow && PushPlugin.isInForeground()) {
                Log.d(LOG_TAG, "foreground force");
                extras.putBoolean(PushConstants.FOREGROUND, true);
                showNotificationIfPossible(getApplicationContext(), extras);
            } else {
                Log.d(LOG_TAG, "background");
                extras.putBoolean(PushConstants.FOREGROUND, false);
                showNotificationIfPossible(getApplicationContext(), extras);
            }
        }
    }

    private void replaceKey(String oldKey, String newKey, Bundle extras, Bundle newExtras) {
        Object value = extras.get(oldKey);
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            newExtras.putString(newKey, (String) value);
        } else if (value instanceof Boolean) {
            newExtras.putBoolean(newKey, ((Boolean) value).booleanValue());
        } else if (value instanceof Number) {
            newExtras.putDouble(newKey, ((Number) value).doubleValue());
        } else {
            newExtras.putString(newKey, String.valueOf(value));
        }
    }

    private String normalizeKey(String key) {
        if (key.equals(MessagingSmsConsts.BODY) || key.equals(PushConstants.ALERT) || key.equals(PushConstants.GCM_NOTIFICATION_BODY)) {
            return PushConstants.MESSAGE;
        }
        if (key.equals(PushConstants.MSGCNT) || key.equals(PushConstants.BADGE)) {
            return PushConstants.COUNT;
        }
        if (key.equals(PushConstants.SOUNDNAME)) {
            return PushConstants.SOUND;
        }
        if (key.startsWith(PushConstants.GCM_NOTIFICATION)) {
            return key.substring(PushConstants.GCM_NOTIFICATION.length() + 1, key.length());
        }
        if (key.startsWith(PushConstants.GCM_N)) {
            return key.substring(PushConstants.GCM_N.length() + 1, key.length());
        }
        return key.startsWith(PushConstants.UA_PREFIX) ? key.substring(PushConstants.UA_PREFIX.length() + 1, key.length()).toLowerCase() : key;
    }

    private Bundle normalizeExtras(Bundle extras) {
        Log.d(LOG_TAG, "normalize extras");
        Bundle newExtras = new Bundle();
        for (String key : extras.keySet()) {
            String newKey;
            Log.d(LOG_TAG, "key = " + key);
            if (key.equals(PushConstants.PARSE_COM_DATA) || key.equals(PushConstants.MESSAGE)) {
                Object json = extras.get(key);
                if ((json instanceof String) && ((String) json).startsWith("{")) {
                    Log.d(LOG_TAG, "extracting nested message data from key = " + key);
                    try {
                        JSONObject data = new JSONObject((String) json);
                        if (data.has(PushConstants.ALERT) || data.has(PushConstants.MESSAGE) || data.has(MessagingSmsConsts.BODY) || data.has(PushConstants.TITLE)) {
                            Iterator<String> jsonIter = data.keys();
                            while (jsonIter.hasNext()) {
                                String jsonKey = (String) jsonIter.next();
                                Log.d(LOG_TAG, "key = data/" + jsonKey);
                                newExtras.putString(normalizeKey(jsonKey), data.getString(jsonKey));
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "normalizeExtras: JSON exception");
                    }
                }
            } else if (key.equals("notification")) {
                Bundle value = extras.getBundle(key);
                for (String notifkey : value.keySet()) {
                    Log.d(LOG_TAG, "notifkey = " + notifkey);
                    newKey = normalizeKey(notifkey);
                    Log.d(LOG_TAG, "replace key " + notifkey + " with " + newKey);
                    newExtras.putString(newKey, value.getString(notifkey));
                }
            }
            newKey = normalizeKey(key);
            Log.d(LOG_TAG, "replace key " + key + " with " + newKey);
            replaceKey(key, newKey, extras, newExtras);
        }
        return newExtras;
    }

    private void showNotificationIfPossible(Context context, Bundle extras) {
        String message = extras.getString(PushConstants.MESSAGE);
        String title = extras.getString(PushConstants.TITLE);
        String contentAvailable = extras.getString(PushConstants.CONTENT_AVAILABLE);
        Log.d(LOG_TAG, "message =[" + message + "]");
        Log.d(LOG_TAG, "title =[" + title + "]");
        Log.d(LOG_TAG, "contentAvailable =[" + contentAvailable + "]");
        if (!((message == null || message.length() == 0) && (title == null || title.length() == 0))) {
            Log.d(LOG_TAG, "create notification");
            createNotification(context, extras);
        }
        if ("1".equals(contentAvailable)) {
            Log.d(LOG_TAG, "send notification event");
            PushPlugin.sendExtras(extras);
        }
    }

    public void createNotification(Context context, Bundle extras) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService("notification");
        String appName = getAppName(this);
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        int notId = parseInt(PushConstants.NOT_ID, extras);
        Intent intent = new Intent(this, PushHandlerActivity.class);
        intent.addFlags(603979776);
        intent.putExtra(PushConstants.PUSH_BUNDLE, extras);
        intent.putExtra(PushConstants.NOT_ID, notId);
        Builder mBuilder = new Builder(context).setWhen(System.currentTimeMillis()).setContentTitle(extras.getString(PushConstants.TITLE)).setTicker(extras.getString(PushConstants.TITLE)).setContentIntent(PendingIntent.getActivity(this, new Random().nextInt(), intent, PageTransition.FROM_API)).setAutoCancel(true);
        SharedPreferences prefs = context.getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0);
        String localIcon = prefs.getString(PushConstants.ICON, null);
        String localIconColor = prefs.getString(PushConstants.ICON_COLOR, null);
        boolean soundOption = prefs.getBoolean(PushConstants.SOUND, true);
        boolean vibrateOption = prefs.getBoolean(PushConstants.VIBRATE, true);
        Log.d(LOG_TAG, "stored icon=" + localIcon);
        Log.d(LOG_TAG, "stored iconColor=" + localIconColor);
        Log.d(LOG_TAG, "stored sound=" + soundOption);
        Log.d(LOG_TAG, "stored vibrate=" + vibrateOption);
        setNotificationVibration(extras, Boolean.valueOf(vibrateOption), mBuilder);
        setNotificationIconColor(extras.getString("color"), mBuilder, localIconColor);
        setNotificationSmallIcon(context, extras, packageName, resources, mBuilder, localIcon);
        setNotificationLargeIcon(extras, packageName, resources, mBuilder);
        if (soundOption) {
            setNotificationSound(context, extras, mBuilder);
        }
        setNotificationLedColor(extras, mBuilder);
        setNotificationPriority(extras, mBuilder);
        setNotificationMessage(notId, extras, mBuilder);
        setNotificationCount(extras, mBuilder);
        createActions(extras, mBuilder, resources, packageName);
        mNotificationManager.notify(appName, notId, mBuilder.build());
    }

    private void createActions(Bundle extras, Builder mBuilder, Resources resources, String packageName) {
        Log.d(LOG_TAG, "create actions");
        String actions = extras.getString(PushConstants.ACTIONS);
        if (actions != null) {
            try {
                JSONArray actionsArray = new JSONArray(actions);
                for (int i = 0; i < actionsArray.length(); i++) {
                    Log.d(LOG_TAG, "adding action");
                    JSONObject action = actionsArray.getJSONObject(i);
                    Log.d(LOG_TAG, "adding callback = " + action.getString(PushConstants.CALLBACK));
                    Intent intent = new Intent(this, PushHandlerActivity.class);
                    intent.putExtra(PushConstants.CALLBACK, action.getString(PushConstants.CALLBACK));
                    intent.putExtra(PushConstants.PUSH_BUNDLE, extras);
                    mBuilder.addAction(resources.getIdentifier(action.getString(PushConstants.ICON), PushConstants.DRAWABLE, packageName), action.getString(PushConstants.TITLE), PendingIntent.getActivity(this, i, intent, PageTransition.FROM_API));
                }
            } catch (JSONException e) {
            }
        }
    }

    private void setNotificationCount(Bundle extras, Builder mBuilder) {
        String msgcnt = extras.getString(PushConstants.MSGCNT);
        if (msgcnt == null) {
            msgcnt = extras.getString(PushConstants.BADGE);
        }
        if (msgcnt != null) {
            mBuilder.setNumber(Integer.parseInt(msgcnt));
        }
    }

    private void setNotificationVibration(Bundle extras, Boolean vibrateOption, Builder mBuilder) {
        String vibrationPattern = extras.getString(PushConstants.VIBRATION_PATTERN);
        if (vibrationPattern != null) {
            String[] items = vibrationPattern.replaceAll("\\[", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE).replaceAll("\\]", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE).split(",");
            long[] results = new long[items.length];
            for (int i = 0; i < items.length; i++) {
                try {
                    results[i] = Long.parseLong(items[i].trim());
                } catch (NumberFormatException e) {
                }
            }
            mBuilder.setVibrate(results);
        } else if (vibrateOption.booleanValue()) {
            mBuilder.setDefaults(2);
        }
    }

    private void setNotificationMessage(int notId, Bundle extras, Builder mBuilder) {
        String message = extras.getString(PushConstants.MESSAGE);
        String style = extras.getString(PushConstants.STYLE, PushConstants.STYLE_TEXT);
        BigTextStyle bigText;
        if (PushConstants.STYLE_INBOX.equals(style)) {
            setNotification(notId, message);
            mBuilder.setContentText(message);
            ArrayList<String> messageList = (ArrayList) messageMap.get(Integer.valueOf(notId));
            Integer sizeList = Integer.valueOf(messageList.size());
            if (sizeList.intValue() > 1) {
                String sizeListMessage = sizeList.toString();
                String stacking = sizeList + " more";
                if (extras.getString(PushConstants.SUMMARY_TEXT) != null) {
                    stacking = extras.getString(PushConstants.SUMMARY_TEXT).replace("%n%", sizeListMessage);
                }
                InboxStyle notificationInbox = new InboxStyle().setBigContentTitle(extras.getString(PushConstants.TITLE)).setSummaryText(stacking);
                for (int i = messageList.size() - 1; i >= 0; i--) {
                    notificationInbox.addLine(Html.fromHtml((String) messageList.get(i)));
                }
                mBuilder.setStyle(notificationInbox);
                return;
            }
            bigText = new BigTextStyle();
            if (message != null) {
                bigText.bigText(message);
                bigText.setBigContentTitle(extras.getString(PushConstants.TITLE));
                mBuilder.setStyle(bigText);
            }
        } else if (PushConstants.STYLE_PICTURE.equals(style)) {
            setNotification(notId, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
            BigPictureStyle bigPicture = new BigPictureStyle();
            bigPicture.bigPicture(getBitmapFromURL(extras.getString(PushConstants.STYLE_PICTURE)));
            bigPicture.setBigContentTitle(extras.getString(PushConstants.TITLE));
            bigPicture.setSummaryText(extras.getString(PushConstants.SUMMARY_TEXT));
            mBuilder.setContentTitle(extras.getString(PushConstants.TITLE));
            mBuilder.setContentText(message);
            mBuilder.setStyle(bigPicture);
        } else {
            setNotification(notId, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
            bigText = new BigTextStyle();
            if (message != null) {
                mBuilder.setContentText(Html.fromHtml(message));
                bigText.bigText(message);
                bigText.setBigContentTitle(extras.getString(PushConstants.TITLE));
                String summaryText = extras.getString(PushConstants.SUMMARY_TEXT);
                if (summaryText != null) {
                    bigText.setSummaryText(summaryText);
                }
                mBuilder.setStyle(bigText);
            }
        }
    }

    private void setNotificationSound(Context context, Bundle extras, Builder mBuilder) {
        String soundname = extras.getString(PushConstants.SOUNDNAME);
        if (soundname == null) {
            soundname = extras.getString(PushConstants.SOUND);
        }
        if (PushConstants.SOUND_RINGTONE.equals(soundname)) {
            mBuilder.setSound(System.DEFAULT_RINGTONE_URI);
        } else if (soundname == null || soundname.contentEquals(PushConstants.SOUND_DEFAULT)) {
            mBuilder.setSound(System.DEFAULT_NOTIFICATION_URI);
        } else {
            Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + soundname);
            Log.d(LOG_TAG, sound.toString());
            mBuilder.setSound(sound);
        }
    }

    private void setNotificationLedColor(Bundle extras, Builder mBuilder) {
        String ledColor = extras.getString(PushConstants.LED_COLOR);
        if (ledColor != null) {
            String[] items = ledColor.replaceAll("\\[", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE).replaceAll("\\]", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE).split(",");
            int[] results = new int[items.length];
            for (int i = 0; i < items.length; i++) {
                try {
                    results[i] = Integer.parseInt(items[i].trim());
                } catch (NumberFormatException e) {
                }
            }
            if (results.length == 4) {
                mBuilder.setLights(Color.argb(results[0], results[1], results[2], results[3]), 500, 500);
            } else {
                Log.e(LOG_TAG, "ledColor parameter must be an array of length == 4 (ARGB)");
            }
        }
    }

    private void setNotificationPriority(Bundle extras, Builder mBuilder) {
        String priorityStr = extras.getString(PushConstants.PRIORITY);
        if (priorityStr != null) {
            try {
                Integer priority = Integer.valueOf(Integer.parseInt(priorityStr));
                if (priority.intValue() < -2 || priority.intValue() > 2) {
                    Log.e(LOG_TAG, "Priority parameter must be between -2 and 2");
                } else {
                    mBuilder.setPriority(priority.intValue());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void setNotificationLargeIcon(Bundle extras, String packageName, Resources resources, Builder mBuilder) {
        String gcmLargeIcon = extras.getString(PushConstants.IMAGE);
        if (gcmLargeIcon == null) {
            return;
        }
        if (gcmLargeIcon.startsWith("http://") || gcmLargeIcon.startsWith("https://")) {
            mBuilder.setLargeIcon(getBitmapFromURL(gcmLargeIcon));
            Log.d(LOG_TAG, "using remote large-icon from gcm");
            return;
        }
        try {
            mBuilder.setLargeIcon(BitmapFactory.decodeStream(getAssets().open(gcmLargeIcon)));
            Log.d(LOG_TAG, "using assets large-icon from gcm");
        } catch (IOException e) {
            int largeIconId = resources.getIdentifier(gcmLargeIcon, PushConstants.DRAWABLE, packageName);
            if (largeIconId != 0) {
                mBuilder.setLargeIcon(BitmapFactory.decodeResource(resources, largeIconId));
                Log.d(LOG_TAG, "using resources large-icon from gcm");
                return;
            }
            Log.d(LOG_TAG, "Not setting large icon");
        }
    }

    private void setNotificationSmallIcon(Context context, Bundle extras, String packageName, Resources resources, Builder mBuilder, String localIcon) {
        int iconId = 0;
        String icon = extras.getString(PushConstants.ICON);
        if (icon != null) {
            iconId = resources.getIdentifier(icon, PushConstants.DRAWABLE, packageName);
            Log.d(LOG_TAG, "using icon from plugin options");
        } else if (localIcon != null) {
            iconId = resources.getIdentifier(localIcon, PushConstants.DRAWABLE, packageName);
            Log.d(LOG_TAG, "using icon from plugin options");
        }
        if (iconId == 0) {
            Log.d(LOG_TAG, "no icon resource found - using application icon");
            iconId = context.getApplicationInfo().icon;
        }
        mBuilder.setSmallIcon(iconId);
    }

    private void setNotificationIconColor(String color, Builder mBuilder, String localIconColor) {
        int iconColor = 0;
        if (color != null) {
            try {
                iconColor = Color.parseColor(color);
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, "couldn't parse color from android options");
            }
        } else if (localIconColor != null) {
            try {
                iconColor = Color.parseColor(localIconColor);
            } catch (IllegalArgumentException e2) {
                Log.e(LOG_TAG, "couldn't parse color from android options");
            }
        }
        if (iconColor != 0) {
            mBuilder.setColor(iconColor);
        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(strURL).openConnection();
            connection.setDoInput(true);
            connection.connect();
            return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getAppName(Context context) {
        return (String) context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
    }

    private int parseInt(String value, Bundle extras) {
        int retval = 0;
        try {
            retval = Integer.parseInt(extras.getString(value));
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Number format exception - Error parsing " + value + ": " + e.getMessage());
        } catch (Exception e2) {
            Log.e(LOG_TAG, "Number format exception - Error parsing " + value + ": " + e2.getMessage());
        }
        return retval;
    }
}
