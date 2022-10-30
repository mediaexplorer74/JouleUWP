package org.xwalk.core.internal.extension.api.messaging;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.adobe.phonegap.push.PushConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessagingManager {
    private static final String TAG = "MessagingManager";
    private final Activity mMainActivity;
    private final Messaging mMessagingHandler;

    MessagingManager(Activity activity, Messaging messaging) {
        this.mMainActivity = activity;
        this.mMessagingHandler = messaging;
    }

    public void onMsgFindMessages(int instanceID, JSONObject jsonMsg) {
        queryMessage(instanceID, jsonMsg);
    }

    public void onMsgGetMessage(int instanceID, JSONObject jsonMsg) {
        queryMessage(instanceID, jsonMsg);
    }

    public void onMsgDeleteMessage(int instanceID, JSONObject jsonMsg) {
        operation(instanceID, jsonMsg);
    }

    public void onMsgDeleteConversation(int instanceID, JSONObject jsonMsg) {
        operation(instanceID, jsonMsg);
    }

    public void onMsgMarkMessageRead(int instanceID, JSONObject jsonMsg) {
        operation(instanceID, jsonMsg);
    }

    public void onMsgMarkConversationRead(int instanceID, JSONObject jsonMsg) {
        operation(instanceID, jsonMsg);
    }

    private Uri getUri(String type) {
        if (type.equals("mms")) {
            return Uri.parse("content://mms");
        }
        return Uri.parse("content://sms");
    }

    private void queryMessage(int instanceID, JSONObject jsonMsg) {
        JSONException e;
        JSONObject jSONObject;
        String messageID = null;
        JSONObject filter = null;
        JSONObject filterOption = null;
        try {
            String msgType;
            String sqlString;
            String[] sqlArgs;
            String asyncCallId = jsonMsg.getString("asyncCallId");
            String cmd = jsonMsg.getString("cmd");
            JSONObject eventBody = jsonMsg.getJSONObject(PushConstants.PARSE_COM_DATA);
            if (eventBody.has("messageID")) {
                messageID = eventBody.getString("messageID");
            }
            if (eventBody.has("filter")) {
                filter = eventBody.getJSONObject("filter");
            }
            if (eventBody.has("options")) {
                filterOption = eventBody.getJSONObject("options");
            }
            if (filter != null) {
                msgType = filter.getString(MessagingSmsConsts.TYPE);
            } else {
                msgType = eventBody.getString(MessagingSmsConsts.TYPE);
            }
            if (!msgType.equals("sms")) {
                if (!msgType.equals("mms")) {
                    Log.e(TAG, "Invalidate message type: " + msgType);
                    return;
                }
            }
            ContentResolver cr = this.mMainActivity.getContentResolver();
            Uri contentUri = getUri(msgType);
            String sqlOption = null;
            if (cmd.equals("msg_findMessages")) {
                Object[] retValue = MessagingHelpers.buildSqlFilterString(filter);
                sqlString = retValue[0];
                sqlArgs = (String[]) retValue[1];
                sqlOption = MessagingHelpers.buildSqlFilterOptionString(filterOption);
            } else {
                sqlString = String.format("%s = ?", new Object[]{MessagingSmsConsts.ID});
                sqlArgs = new String[]{messageID};
            }
            Cursor cursor = cr.query(contentUri, null, sqlString, sqlArgs, sqlOption);
            try {
                JSONArray results;
                JSONObject jsonMsgRet = new JSONObject();
                try {
                    results = new JSONArray();
                } catch (JSONException e2) {
                    e = e2;
                    jSONObject = jsonMsgRet;
                    e.printStackTrace();
                }
                try {
                    jsonMsgRet.put("asyncCallId", asyncCallId);
                    jsonMsgRet.put("cmd", cmd + "_ret");
                    JSONObject jsData = new JSONObject();
                    jsonMsgRet.put(PushConstants.PARSE_COM_DATA, jsData);
                    jsData.put("error", false);
                    JSONObject jsBody = new JSONObject();
                    jsData.put(MessagingSmsConsts.BODY, jsBody);
                    jsBody.put("results", results);
                    try {
                        if (!msgType.equals("mms")) {
                            if (cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    JSONObject jsonSmsObj = MessagingHelpers.SmsMessageCursor2Json(cursor);
                                    if (jsonSmsObj != null) {
                                        results.put(jsonSmsObj);
                                    }
                                }
                            }
                        }
                        cursor.close();
                        this.mMessagingHandler.postMessage(instanceID, jsonMsgRet.toString());
                    } catch (Throwable th) {
                        cursor.close();
                    }
                } catch (JSONException e3) {
                    e = e3;
                    JSONArray jSONArray = results;
                    jSONObject = jsonMsgRet;
                    e.printStackTrace();
                }
            } catch (JSONException e4) {
                e = e4;
                e.printStackTrace();
            }
        } catch (JSONException e5) {
            e5.printStackTrace();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void operation(int r24, org.json.JSONObject r25) {
        /*
        r23 = this;
        r8 = 0;
        r3 = 0;
        r15 = 0;
        r9 = 0;
        r4 = 0;
        r10 = 0;
        r19 = "asyncCallId";
        r0 = r25;
        r1 = r19;
        r3 = r0.getString(r1);	 Catch:{ JSONException -> 0x0124 }
        r19 = "data";
        r0 = r25;
        r1 = r19;
        r8 = r0.getJSONObject(r1);	 Catch:{ JSONException -> 0x0124 }
        r19 = "messageID";
        r0 = r19;
        r19 = r8.has(r0);	 Catch:{ JSONException -> 0x0124 }
        if (r19 == 0) goto L_0x011a;
    L_0x0024:
        r19 = "messageID";
        r0 = r19;
        r9 = r8.getString(r0);	 Catch:{ JSONException -> 0x0124 }
    L_0x002c:
        r19 = "cmd";
        r0 = r25;
        r1 = r19;
        r4 = r0.getString(r1);	 Catch:{ JSONException -> 0x0124 }
        r19 = "value";
        r0 = r19;
        r19 = r8.has(r0);	 Catch:{ JSONException -> 0x0124 }
        if (r19 == 0) goto L_0x0048;
    L_0x0040:
        r19 = "value";
        r0 = r19;
        r10 = r8.getBoolean(r0);	 Catch:{ JSONException -> 0x0124 }
    L_0x0048:
        r19 = "type";
        r0 = r19;
        r15 = r8.getString(r0);	 Catch:{ JSONException -> 0x0124 }
        r17 = 0;
        r19 = "messageID";
        r0 = r19;
        r19 = r8.has(r0);
        if (r19 == 0) goto L_0x0129;
    L_0x005c:
        r19 = "%s = ?";
        r20 = 1;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r21 = 0;
        r22 = "_id";
        r20[r21] = r22;
        r17 = java.lang.String.format(r19, r20);
    L_0x0070:
        r19 = 1;
        r0 = r19;
        r0 = new java.lang.String[r0];
        r16 = r0;
        r19 = 0;
        r16[r19] = r9;
        r0 = r23;
        r0 = r0.mMainActivity;
        r19 = r0;
        r6 = r19.getContentResolver();
        r0 = r23;
        r5 = r0.getUri(r15);
        r19 = "msg_deleteMessage";
        r0 = r19;
        r19 = r4.equals(r0);
        if (r19 != 0) goto L_0x00a0;
    L_0x0096:
        r19 = "msg_deleteConversation";
        r0 = r19;
        r19 = r4.equals(r0);
        if (r19 == 0) goto L_0x013f;
    L_0x00a0:
        r0 = r17;
        r1 = r16;
        r6.delete(r5, r0, r1);
    L_0x00a7:
        r13 = 0;
        r14 = new org.json.JSONObject;	 Catch:{ JSONException -> 0x0184 }
        r14.<init>();	 Catch:{ JSONException -> 0x0184 }
        r19 = "asyncCallId";
        r0 = r19;
        r14.put(r0, r3);	 Catch:{ JSONException -> 0x017e }
        r12 = new org.json.JSONObject;	 Catch:{ JSONException -> 0x017e }
        r12.<init>();	 Catch:{ JSONException -> 0x017e }
        r19 = "data";
        r0 = r19;
        r14.put(r0, r12);	 Catch:{ JSONException -> 0x017e }
        r19 = "error";
        r20 = 0;
        r0 = r19;
        r1 = r20;
        r12.put(r0, r1);	 Catch:{ JSONException -> 0x017e }
        r11 = new org.json.JSONObject;	 Catch:{ JSONException -> 0x017e }
        r11.<init>();	 Catch:{ JSONException -> 0x017e }
        r19 = "body";
        r0 = r19;
        r12.put(r0, r11);	 Catch:{ JSONException -> 0x017e }
        r19 = "messageID";
        r0 = r19;
        r19 = r8.has(r0);	 Catch:{ JSONException -> 0x017e }
        if (r19 == 0) goto L_0x0175;
    L_0x00e1:
        r19 = "messageID";
        r0 = r19;
        r11.put(r0, r9);	 Catch:{ JSONException -> 0x017e }
    L_0x00e8:
        r19 = "cmd";
        r20 = new java.lang.StringBuilder;	 Catch:{ JSONException -> 0x017e }
        r20.<init>();	 Catch:{ JSONException -> 0x017e }
        r0 = r20;
        r20 = r0.append(r4);	 Catch:{ JSONException -> 0x017e }
        r21 = "_ret";
        r20 = r20.append(r21);	 Catch:{ JSONException -> 0x017e }
        r20 = r20.toString();	 Catch:{ JSONException -> 0x017e }
        r0 = r19;
        r1 = r20;
        r14.put(r0, r1);	 Catch:{ JSONException -> 0x017e }
        r0 = r23;
        r0 = r0.mMessagingHandler;
        r19 = r0;
        r20 = r14.toString();
        r0 = r19;
        r1 = r24;
        r2 = r20;
        r0.postMessage(r1, r2);
    L_0x0119:
        return;
    L_0x011a:
        r19 = "conversationID";
        r0 = r19;
        r9 = r8.getString(r0);	 Catch:{ JSONException -> 0x0124 }
        goto L_0x002c;
    L_0x0124:
        r7 = move-exception;
        r7.printStackTrace();
        goto L_0x0119;
    L_0x0129:
        r19 = "%s = ?";
        r20 = 1;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r21 = 0;
        r22 = "thread_id";
        r20[r21] = r22;
        r17 = java.lang.String.format(r19, r20);
        goto L_0x0070;
    L_0x013f:
        r19 = "msg_markMessageRead";
        r0 = r19;
        r19 = r4.equals(r0);
        if (r19 != 0) goto L_0x0153;
    L_0x0149:
        r19 = "msg_markConversationRead";
        r0 = r19;
        r19 = r4.equals(r0);
        if (r19 == 0) goto L_0x00a7;
    L_0x0153:
        r18 = new android.content.ContentValues;
        r18.<init>();
        r20 = "read";
        if (r10 == 0) goto L_0x0172;
    L_0x015c:
        r19 = "1";
    L_0x015e:
        r0 = r18;
        r1 = r20;
        r2 = r19;
        r0.put(r1, r2);
        r0 = r18;
        r1 = r17;
        r2 = r16;
        r6.update(r5, r0, r1, r2);
        goto L_0x00a7;
    L_0x0172:
        r19 = "0";
        goto L_0x015e;
    L_0x0175:
        r19 = "conversationID";
        r0 = r19;
        r11.put(r0, r9);	 Catch:{ JSONException -> 0x017e }
        goto L_0x00e8;
    L_0x017e:
        r7 = move-exception;
        r13 = r14;
    L_0x0180:
        r7.printStackTrace();
        goto L_0x0119;
    L_0x0184:
        r7 = move-exception;
        goto L_0x0180;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xwalk.core.internal.extension.api.messaging.MessagingManager.operation(int, org.json.JSONObject):void");
    }
}
