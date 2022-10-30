package org.xwalk.core.internal.extension.api.contacts;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.Build.VERSION;
import android.os.RemoteException;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import com.adobe.phonegap.push.PushConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.cordova.networkinformation.NetworkManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.api.contacts.ContactConstants.ContactMap;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

public class ContactSaver {
    private static final String TAG = "ContactSaver";
    private JSONObject mContact;
    private String mId;
    private boolean mIsUpdate;
    private ContactJson mJson;
    private ArrayList<ContentProviderOperation> mOps;
    private ContactUtils mUtils;

    public ContactSaver(ContentResolver resolver) {
        this.mUtils = new ContactUtils(resolver);
    }

    private Builder newUpdateBuilder(String mimeType) {
        Builder builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
        builder.withSelection("contact_id=? AND mimetype=?", new String[]{this.mId, mimeType});
        return builder;
    }

    private Builder newInsertBuilder(String mimeType) {
        Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
        builder.withValueBackReference("raw_contact_id", 0);
        builder.withValue("mimetype", mimeType);
        return builder;
    }

    private Builder newInsertFieldBuilder(String mimeType) {
        if (this.mUtils.getRawId(this.mId) == null) {
            Log.e(TAG, "Failed to create builder to insert field of " + this.mId);
            return null;
        }
        Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
        builder.withValue("raw_contact_id", this.mUtils.getRawId(this.mId));
        builder.withValue("mimetype", mimeType);
        return builder;
    }

    private Builder newInsertContactOrFieldBuilder(String mimeType) {
        return this.mIsUpdate ? newInsertFieldBuilder(mimeType) : newInsertBuilder(mimeType);
    }

    private Builder newBuilder(String mimeType) {
        return this.mIsUpdate ? newUpdateBuilder(mimeType) : newInsertBuilder(mimeType);
    }

    private void buildByArray(ContactMap contactMap) {
        if (this.mContact.has(contactMap.mName)) {
            if (this.mIsUpdate) {
                this.mUtils.cleanByMimeType(this.mId, contactMap.mMimeType);
            }
            try {
                JSONArray fields = this.mContact.getJSONArray(contactMap.mName);
                for (int i = 0; i < fields.length(); i++) {
                    ContactJson json = new ContactJson(fields.getJSONObject(i));
                    List<String> typeList = json.getStringArray("types");
                    if (!(typeList == null || typeList.isEmpty())) {
                        Integer iType = (Integer) contactMap.mTypeValueMap.get((String) typeList.get(0));
                        Builder builder = newInsertContactOrFieldBuilder(contactMap.mMimeType);
                        if (builder != null) {
                            if (json.getBoolean("preferred")) {
                                builder.withValue((String) contactMap.mTypeMap.get("isPrimary"), Integer.valueOf(1));
                                builder.withValue((String) contactMap.mTypeMap.get("isSuperPrimary"), Integer.valueOf(1));
                            }
                            if (iType != null) {
                                builder.withValue((String) contactMap.mTypeMap.get(MessagingSmsConsts.TYPE), iType);
                            }
                            for (Entry<String, String> entry : contactMap.mDataMap.entrySet()) {
                                String value = json.getString((String) entry.getValue());
                                if (contactMap.mName.equals("impp")) {
                                    int colonIdx = value.indexOf(58);
                                    if (-1 != colonIdx) {
                                        builder.withValue("data5", ContactConstants.imProtocolMap.get(value.substring(0, colonIdx)));
                                        value = value.substring(colonIdx + 1);
                                    }
                                }
                                builder.withValue((String) entry.getKey(), value);
                            }
                            this.mOps.add(builder.build());
                        } else {
                            return;
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse json data of " + contactMap.mName + ": " + e.toString());
            }
        }
    }

    private void buildByArray(String mimeType, String data, List<String> dataEntries) {
        if (this.mIsUpdate) {
            this.mUtils.cleanByMimeType(this.mId, mimeType);
        }
        for (String entry : dataEntries) {
            Builder builder = newInsertContactOrFieldBuilder(mimeType);
            if (builder != null) {
                builder.withValue(data, entry);
                this.mOps.add(builder.build());
            } else {
                return;
            }
        }
    }

    private void buildByArray(ContactMap contactMap, String data, List<String> dataEntries) {
        if (this.mContact.has(contactMap.mName)) {
            buildByArray(contactMap.mMimeType, data, (List) dataEntries);
        }
    }

    private void buildByDate(String name, String mimeType, String data, String type, int dateType) {
        if (this.mContact.has(name)) {
            String dateString = this.mUtils.dateTrim(this.mJson.getString(name));
            Builder builder = newBuilder(mimeType);
            builder.withValue(data, dateString);
            if (type != null) {
                builder.withValue(type, Integer.valueOf(dateType));
            }
            this.mOps.add(builder.build());
        }
    }

    private void buildByEvent(String eventName, int eventType) {
        buildByDate(eventName, "vnd.android.cursor.item/contact_event", "data1", "data2", eventType);
    }

    private void buildByContactMapList() {
        for (ContactMap contactMap : ContactConstants.contactMapList) {
            if (contactMap.mTypeMap != null) {
                buildByArray(contactMap);
            } else {
                buildByArray(contactMap, (String) contactMap.mDataMap.get(PushConstants.PARSE_COM_DATA), this.mJson.getStringArray(contactMap.mName));
            }
        }
    }

    private void PutToContact(String name, String value) {
        if (name != null) {
            try {
                this.mContact.put(name, value);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to set " + name + " = " + value + " for contact" + e.toString());
            }
        }
    }

    public JSONObject save(String saveString) {
        this.mOps = new ArrayList();
        try {
            Builder builder;
            this.mContact = new JSONObject(saveString);
            this.mJson = new ContactJson(this.mContact);
            this.mId = this.mJson.getString("id");
            this.mIsUpdate = this.mUtils.hasID(this.mId);
            Set<String> oldRawIds = null;
            if (!this.mIsUpdate) {
                oldRawIds = this.mUtils.getCurrentRawIds();
                this.mId = null;
                builder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
                builder.withValue("account_type", null);
                builder.withValue("account_name", null);
                this.mOps.add(builder.build());
            }
            if (this.mContact.has("name")) {
                JSONObject name = this.mJson.getObject("name");
                ContactJson nameJson = new ContactJson(name);
                builder = newBuilder("vnd.android.cursor.item/name");
                builder.withValue("data1", nameJson.getString("displayName"));
                builder.withValue("data3", nameJson.getFirstValue("familyNames"));
                builder.withValue("data2", nameJson.getFirstValue("givenNames"));
                builder.withValue("data5", nameJson.getFirstValue("additionalNames"));
                builder.withValue("data4", nameJson.getFirstValue("honorificPrefixes"));
                builder.withValue("data6", nameJson.getFirstValue("honorificSuffixes"));
                this.mOps.add(builder.build());
                if (name.has("nicknames")) {
                    builder = newBuilder("vnd.android.cursor.item/nickname");
                    builder.withValue("data1", nameJson.getFirstValue("nicknames"));
                    this.mOps.add(builder.build());
                }
            }
            if (this.mContact.has("categories")) {
                List groupIds = new ArrayList();
                for (String groupTitle : this.mJson.getStringArray("categories")) {
                    groupIds.add(this.mUtils.getEnsuredGroupId(groupTitle));
                }
                buildByArray("vnd.android.cursor.item/group_membership", "data1", groupIds);
            }
            if (this.mContact.has("gender")) {
                String gender = this.mJson.getString("gender");
                if (Arrays.asList(new String[]{"male", "female", "other", NetworkManager.TYPE_NONE, NetworkManager.TYPE_UNKNOWN}).contains(gender)) {
                    builder = newBuilder(ContactConstants.CUSTOM_MIMETYPE_GENDER);
                    builder.withValue("data1", gender);
                    this.mOps.add(builder.build());
                }
            }
            buildByEvent("birthday", 3);
            buildByEvent("anniversary", 1);
            buildByContactMapList();
            try {
                this.mUtils.mResolver.applyBatch("com.android.contacts", this.mOps);
                if (!this.mIsUpdate) {
                    Set<String> newRawIds = this.mUtils.getCurrentRawIds();
                    if (newRawIds == null) {
                        return new JSONObject();
                    }
                    newRawIds.removeAll(oldRawIds);
                    if (newRawIds.size() != 1) {
                        Log.e(TAG, "Something wrong after batch applied, new raw ids are: " + newRawIds.toString());
                        return this.mContact;
                    }
                    this.mId = this.mUtils.getId((String) newRawIds.iterator().next());
                    PutToContact("id", this.mId);
                }
                if (VERSION.SDK_INT >= 18) {
                    PutToContact("lastUpdated", String.valueOf(this.mUtils.getLastUpdated(Long.valueOf(this.mId).longValue())));
                }
                return this.mContact;
            } catch (Exception e) {
                if ((e instanceof RemoteException) || (e instanceof OperationApplicationException) || (e instanceof SecurityException)) {
                    Log.e(TAG, "Failed to apply batch: " + e.toString());
                    return new JSONObject();
                }
                throw new RuntimeException(e);
            }
        } catch (JSONException e2) {
            Log.e(TAG, "Failed to parse json data: " + e2.toString());
            return new JSONObject();
        }
    }
}
