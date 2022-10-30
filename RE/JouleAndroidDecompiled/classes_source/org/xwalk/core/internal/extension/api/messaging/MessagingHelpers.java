package org.xwalk.core.internal.extension.api.messaging;

import android.database.Cursor;
import com.adobe.phonegap.push.PushConstants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.json.JSONException;
import org.json.JSONObject;

public class MessagingHelpers {
    private static String buildSqlClause(boolean hasAnd, String condition, String column) {
        return (hasAnd ? " AND " : CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE) + String.format(condition, new Object[]{column});
    }

    public static String convertJsDateString2Long(String date) {
        long time = 0;
        try {
            time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date.replace('T', ' ').replace('Z', ' ')).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(time);
    }

    public static String convertDateLong2String(long time) {
        if (time <= 0) {
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }

    public static Object[] buildSqlFilterString(JSONObject filter) {
        String filterString = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        ArrayList<String> argsStringList = new ArrayList();
        boolean hasAnd = false;
        try {
            if (filter.has("startDate")) {
                filterString = filterString + buildSqlClause(false, "%s >= ?", MessagingSmsConsts.DATE);
                argsStringList.add(convertJsDateString2Long(filter.getString("startDate")));
                hasAnd = true;
            }
            if (filter.has("endDate")) {
                filterString = filterString + buildSqlClause(hasAnd, "%s <= ?", MessagingSmsConsts.DATE);
                argsStringList.add(convertJsDateString2Long(filter.getString("endDate")));
                hasAnd = true;
            }
            if (filter.has(PushConstants.FROM)) {
                filterString = filterString + buildSqlClause(hasAnd, "%s = ?", MessagingSmsConsts.ADDRESS);
                argsStringList.add(filter.getString(PushConstants.FROM));
                hasAnd = true;
            }
            String msgType = "sms";
            if (filter.has(MessagingSmsConsts.TYPE)) {
                msgType = filter.getString(MessagingSmsConsts.TYPE);
            }
            if (filter.has("state") && msgType.equals("sms")) {
                filterString = filterString + buildSqlClause(hasAnd, "%s = ?", MessagingSmsConsts.TYPE);
                argsStringList.add(String.valueOf((Integer) MessagingSmsConstMaps.smsStateDictS2I.get(filter.getString("state"))));
                hasAnd = true;
            }
            if (filter.has(MessagingSmsConsts.READ)) {
                filterString = filterString + buildSqlClause(hasAnd, "%s = ?", MessagingSmsConsts.READ);
                argsStringList.add(filter.getBoolean(MessagingSmsConsts.READ) ? "1" : "0");
            }
            return new Object[]{filterString, argsStringList.toArray(new String[argsStringList.size()])};
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String buildSqlFilterOptionString(JSONObject filterOption) {
        String filterOptionString = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        try {
            if (filterOption.has("sortBy")) {
                filterOptionString = filterOptionString + " " + ((String) MessagingSmsConstMaps.smsTableColumnDict.get(filterOption.getString("sortBy")));
            }
            if (filterOption.has("sortOrder")) {
                filterOptionString = filterOptionString + " " + ((String) MessagingSmsConstMaps.sortOrderDict.get(filterOption.getString("sortOrder")));
            }
            if (filterOption.has("limit")) {
                filterOptionString = filterOptionString + " LIMIT " + filterOption.getString("limit");
            }
            return filterOptionString;
        } catch (JSONException e) {
            e.printStackTrace();
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
    }

    public static JSONObject SmsMessageCursor2Json(Cursor c) {
        JSONException e;
        try {
            JSONObject jsonMsg = new JSONObject();
            JSONObject jSONObject;
            try {
                jsonMsg.put("messageID", c.getString(c.getColumnIndex(MessagingSmsConsts.ID)));
                jsonMsg.put("conversationID", c.getString(c.getColumnIndex(MessagingSmsConsts.THREAD_ID)));
                jsonMsg.put(MessagingSmsConsts.TYPE, "sms");
                jsonMsg.put("serviceID", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
                jsonMsg.put(PushConstants.FROM, c.getString(c.getColumnIndex(MessagingSmsConsts.ADDRESS)));
                jsonMsg.put("timestamp", convertDateLong2String(c.getLong(c.getColumnIndex(MessagingSmsConsts.DATE))));
                jsonMsg.put(MessagingSmsConsts.READ, c.getString(c.getColumnIndex(MessagingSmsConsts.READ)));
                jsonMsg.put("to", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
                jsonMsg.put(MessagingSmsConsts.BODY, c.getString(c.getColumnIndex(MessagingSmsConsts.BODY)));
                jsonMsg.put("state", MessagingSmsConstMaps.smsStateDictI2S.get(Integer.valueOf(c.getInt(c.getColumnIndex(MessagingSmsConsts.TYPE)))));
                jsonMsg.put("deliveryStatus", MessagingSmsConstMaps.smsDiliveryStatusDictI2S.get(Integer.valueOf(c.getInt(c.getColumnIndex(MessagingSmsConsts.STATUS)))));
                jsonMsg.put("deliveryTimestamp", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
                jsonMsg.put("messageClass", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
                jSONObject = jsonMsg;
                return jsonMsg;
            } catch (JSONException e2) {
                e = e2;
                jSONObject = jsonMsg;
                e.printStackTrace();
                return null;
            }
        } catch (JSONException e3) {
            e = e3;
            e.printStackTrace();
            return null;
        }
    }
}
