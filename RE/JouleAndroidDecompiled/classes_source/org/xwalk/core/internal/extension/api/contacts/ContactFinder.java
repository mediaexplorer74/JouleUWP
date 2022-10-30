package org.xwalk.core.internal.extension.api.contacts;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

public class ContactFinder {
    private static final String TAG = "ContactFinder";
    private ContactUtils mUtils;

    private class ContactData {
        public JSONArray aAddresses;
        public JSONArray aCategories;
        public JSONArray aEmails;
        public JSONArray aImpp;
        public JSONArray aJobTitles;
        public JSONArray aNotes;
        public JSONArray aNumbers;
        public JSONArray aOrganizations;
        public JSONArray aPhotos;
        public JSONArray aUrls;
        public String anniversary;
        public String birthday;
        public String gender;
        public String lastUpdated;
        public JSONObject oName;

        private ContactData() {
        }

        public JSONObject ensurePut(long id) {
            JSONObject o = new JSONObject();
            try {
                o.put("id", id);
            } catch (JSONException e) {
                Log.e(ContactFinder.TAG, "ensurePut - Failed to build json data: " + e.toString());
            }
            ensurePut(o, "name", this.oName);
            ensurePut(o, "lastUpdated", this.lastUpdated);
            ensurePut(o, "emails", this.aEmails);
            ensurePut(o, "photos", this.aPhotos);
            ensurePut(o, "urls", this.aUrls);
            ensurePut(o, "categories", this.aCategories);
            ensurePut(o, "addresses", this.aAddresses);
            ensurePut(o, "phoneNumbers", this.aNumbers);
            ensurePut(o, "organizations", this.aOrganizations);
            ensurePut(o, "jobTitles", this.aJobTitles);
            ensurePut(o, "birthday", this.birthday);
            ensurePut(o, "notes", this.aNotes);
            ensurePut(o, "impp", this.aImpp);
            ensurePut(o, "anniversary", this.anniversary);
            ensurePut(o, "gender", this.gender);
            return o;
        }

        private <T> void ensurePut(JSONObject o, String jsonName, T t) {
            if (t != null) {
                try {
                    o.put(jsonName, t);
                } catch (JSONException e) {
                    Log.e(ContactFinder.TAG, "ensurePut - Failed to add json data: " + e.toString());
                }
            }
        }
    }

    public static class FindOption {
        public String mSortOrder;
        public String mWhere;
        public String[] mWhereArgs;

        public FindOption(String where, String[] whereArgs, String sortOrder) {
            this.mWhere = where;
            this.mWhereArgs = whereArgs;
            this.mSortOrder = sortOrder;
        }
    }

    public ContactFinder(ContentResolver resolver) {
        this.mUtils = new ContactUtils(resolver);
    }

    private JSONObject addString(JSONObject o, Cursor c, String jsonName, String dataName) {
        try {
            String value = c.getString(c.getColumnIndex(dataName));
            if (o == null) {
                o = new JSONObject();
            }
            if (value != null) {
                o.put(jsonName, value);
            }
        } catch (JSONException e) {
            Log.e(TAG, "addString - Failed to build json data: " + e.toString());
        }
        return o;
    }

    private JSONArray addString(JSONArray array, Cursor c, String dataName) {
        if (array == null) {
            array = new JSONArray();
        }
        String value = c.getString(c.getColumnIndex(dataName));
        if (value != null) {
            array.put(value);
        }
        return array;
    }

    private JSONObject addArrayTop(JSONObject o, Cursor c, String jsonName, String dataName, Map<String, Integer> typeValuesMap) {
        return ensureAddArrayTop(o, c, jsonName, (String) ContactUtils.getKeyFromValue(typeValuesMap, Integer.valueOf(c.getString(c.getColumnIndex(dataName)))));
    }

    private JSONObject addArrayTop(JSONObject o, Cursor c, String jsonName, String dataName) {
        return ensureAddArrayTop(o, c, jsonName, c.getString(c.getColumnIndex(dataName)));
    }

    private JSONObject ensureAddArrayTop(JSONObject o, Cursor c, String jsonName, String nameString) {
        if (o == null) {
            try {
                o = new JSONObject();
            } catch (JSONException e) {
                Log.e(TAG, "ensureAddArrayTop - Failed to build json data: " + e.toString());
            }
        }
        if (nameString != null) {
            JSONArray nameArray = new JSONArray();
            nameArray.put(nameString);
            o.put(jsonName, nameArray);
        }
        return o;
    }

    private JSONArray addTypeArray(JSONArray array, Cursor c, String data, Map<String, String> typeMap, Map<String, Integer> typeValuesMap) {
        if (array == null) {
            try {
                array = new JSONArray();
            } catch (JSONException e) {
                Log.e(TAG, "addTypeArray - Failed to build json data: " + e.toString());
            }
        }
        String preferred = c.getString(c.getColumnIndex((String) typeMap.get("isSuperPrimary"))).equals("1") ? "true" : "false";
        JSONObject o = new JSONObject();
        o.put("preferred", preferred);
        addArrayTop(o, c, "types", (String) typeMap.get(MessagingSmsConsts.TYPE), typeValuesMap);
        String value = c.getString(c.getColumnIndex(data));
        if (c.getString(c.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/im")) {
            value = ((String) ContactUtils.getKeyFromValue(ContactConstants.imProtocolMap, Integer.valueOf(c.getInt(c.getColumnIndex("data5"))))) + ':' + value;
        }
        o.put("value", value);
        array.put(o);
        return array;
    }

    private Set<String> getContactIds(FindOption findOption) {
        SecurityException e;
        Set<String> set;
        Throwable th;
        Cursor c = null;
        try {
            c = this.mUtils.mResolver.query(Data.CONTENT_URI, null, findOption.mWhere, findOption.mWhereArgs, findOption.mSortOrder);
            Set<String> ids = new HashSet();
            while (c.moveToNext()) {
                try {
                    ids.add(String.valueOf(c.getLong(c.getColumnIndex("contact_id"))));
                } catch (SecurityException e2) {
                    e = e2;
                    set = ids;
                } catch (Throwable th2) {
                    th = th2;
                    set = ids;
                }
            }
            if (c != null) {
                c.close();
            }
            set = ids;
            return ids;
        } catch (SecurityException e3) {
            e = e3;
            try {
                Log.e(TAG, "getContactIds: " + e.toString());
                if (c != null) {
                    c.close();
                }
                return null;
            } catch (Throwable th3) {
                th = th3;
                if (c != null) {
                    c.close();
                }
                throw th;
            }
        }
    }

    private String getSortOrder(List<String> sortBy, String sortOrder) {
        if (sortOrder == null) {
            return null;
        }
        String suffix = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        if (sortOrder.equals("ascending")) {
            suffix = " ASC";
        } else if (sortOrder.equals("descending")) {
            suffix = " DESC";
        }
        String order = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        for (String s : sortBy) {
            Pair<String, String> fields = (Pair) ContactConstants.contactDataMap.get(s);
            if (fields != null) {
                order = order + ((String) fields.first) + suffix + ",";
            }
        }
        return order != CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE ? order.substring(0, order.length() - 1) : null;
    }

    private JSONArray getContacts(Set<String> contactIds, String sortOrder, String sortByMimeType, Long resultsLimit) {
        Throwable e;
        Map<Long, ContactData> map;
        Throwable th;
        JSONArray returnArray = new JSONArray();
        if (contactIds.size() != 0) {
            Cursor cursor = null;
            try {
                long id;
                ContactFinder contactFinder;
                cursor = this.mUtils.mResolver.query(Data.CONTENT_URI, null, "contact_id in (" + ContactUtils.makeQuestionMarkList(contactIds) + ")", (String[]) contactIds.toArray(new String[contactIds.size()]), sortOrder);
                Map<Long, ContactData> dataMap = new LinkedHashMap();
                if (sortOrder != null) {
                    while (cursor.moveToNext()) {
                        try {
                            if (cursor.getString(cursor.getColumnIndex("mimetype")).equals(sortByMimeType)) {
                                id = cursor.getLong(cursor.getColumnIndex("contact_id"));
                                if (!dataMap.containsKey(Long.valueOf(id))) {
                                    contactFinder = this;
                                    dataMap.put(Long.valueOf(id), new ContactData());
                                }
                            }
                        } catch (Exception e2) {
                            e = e2;
                            map = dataMap;
                        } catch (Throwable th2) {
                            th = th2;
                            map = dataMap;
                        }
                    }
                    cursor.moveToFirst();
                }
                while (cursor.moveToNext()) {
                    id = cursor.getLong(cursor.getColumnIndex("contact_id"));
                    if (!dataMap.containsKey(Long.valueOf(id))) {
                        contactFinder = this;
                        dataMap.put(Long.valueOf(id), new ContactData());
                    }
                    ContactData d = (ContactData) dataMap.get(Long.valueOf(id));
                    if (d.lastUpdated == null && VERSION.SDK_INT >= 18) {
                        d.lastUpdated = this.mUtils.getLastUpdated(id);
                    }
                    String mime = cursor.getString(cursor.getColumnIndex("mimetype"));
                    if (mime.equals("vnd.android.cursor.item/name")) {
                        d.oName = addString(d.oName, cursor, "displayName", "data1");
                        d.oName = addArrayTop(d.oName, cursor, "honorificPrefixes", "data4");
                        d.oName = addArrayTop(d.oName, cursor, "givenNames", "data2");
                        d.oName = addArrayTop(d.oName, cursor, "additionalNames", "data5");
                        d.oName = addArrayTop(d.oName, cursor, "familyNames", "data3");
                        d.oName = addArrayTop(d.oName, cursor, "honorificSuffixes", "data6");
                    } else {
                        if (mime.equals("vnd.android.cursor.item/nickname")) {
                            d.oName = addArrayTop(d.oName, cursor, "nicknames", "data1");
                        } else {
                            if (mime.equals("vnd.android.cursor.item/email_v2")) {
                                d.aEmails = addTypeArray(d.aEmails, cursor, "data1", ContactConstants.emailTypeMap, ContactConstants.emailTypeValuesMap);
                            } else {
                                if (mime.equals("vnd.android.cursor.item/photo")) {
                                    d.aPhotos = addString(d.aPhotos, cursor, "data15");
                                } else {
                                    if (mime.equals("vnd.android.cursor.item/website")) {
                                        d.aUrls = addTypeArray(d.aUrls, cursor, "data1", ContactConstants.websiteTypeMap, ContactConstants.websiteTypeValuesMap);
                                    } else {
                                        if (mime.equals("vnd.android.cursor.item/group_membership")) {
                                            String title = this.mUtils.getGroupTitle(cursor.getString(cursor.getColumnIndex("data1")));
                                            if (title != null) {
                                                if (d.aCategories == null) {
                                                    d.aCategories = new JSONArray();
                                                }
                                                d.aCategories.put(title);
                                            }
                                        } else {
                                            if (mime.equals("vnd.android.cursor.item/postal-address_v2")) {
                                                d.aAddresses = addTypeArray(d.aAddresses, cursor, "data1", ContactConstants.addressTypeMap, ContactConstants.addressTypeValuesMap);
                                            } else {
                                                if (mime.equals("vnd.android.cursor.item/phone_v2")) {
                                                    d.aNumbers = addTypeArray(d.aNumbers, cursor, "data1", ContactConstants.phoneTypeMap, ContactConstants.phoneTypeValuesMap);
                                                } else {
                                                    if (mime.equals("vnd.android.cursor.item/organization")) {
                                                        d.aOrganizations = addString(d.aOrganizations, cursor, "data1");
                                                    } else {
                                                        if (mime.equals("vnd.android.cursor.item/organization")) {
                                                            d.aJobTitles = addString(d.aJobTitles, cursor, "data4");
                                                        } else {
                                                            if (mime.equals("vnd.android.cursor.item/contact_event")) {
                                                                int type = Integer.valueOf(cursor.getString(cursor.getColumnIndex("data2"))).intValue();
                                                                if (type == 3) {
                                                                    d.birthday = cursor.getString(cursor.getColumnIndex("data1"));
                                                                } else if (type == 1) {
                                                                    d.anniversary = cursor.getString(cursor.getColumnIndex("data1"));
                                                                }
                                                            } else {
                                                                if (mime.equals("vnd.android.cursor.item/note")) {
                                                                    d.aNotes = addString(d.aNotes, cursor, "data1");
                                                                } else {
                                                                    if (mime.equals("vnd.android.cursor.item/im")) {
                                                                        d.aImpp = addTypeArray(d.aImpp, cursor, "data1", ContactConstants.imTypeMap, ContactConstants.imTypeValuesMap);
                                                                    } else {
                                                                        if (mime.equals(ContactConstants.CUSTOM_MIMETYPE_GENDER)) {
                                                                            d.gender = cursor.getString(cursor.getColumnIndex("data1"));
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                int i = 0;
                for (Entry<Long, ContactData> entry : dataMap.entrySet()) {
                    if (resultsLimit != null) {
                        i++;
                        if (((long) i) > resultsLimit.longValue()) {
                            break;
                        }
                    }
                    returnArray.put(((ContactData) entry.getValue()).ensurePut(((Long) entry.getKey()).longValue()));
                }
            } catch (Exception e3) {
                e = e3;
                try {
                    if ((e instanceof NumberFormatException) || (e instanceof SecurityException)) {
                        Log.e(TAG, "getContacts: " + e.toString());
                        returnArray = new JSONArray();
                        if (cursor != null) {
                            cursor.close();
                        }
                        return returnArray;
                    }
                    throw new RuntimeException(e);
                } catch (Throwable th3) {
                    th = th3;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
        }
        return returnArray;
    }

    private FindOption createFindIDOption(String findString) {
        ContactJson findJson = new ContactJson(findString);
        String value = findString != null ? findJson.getString("value") : null;
        if (!(value == null || value.equals(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE))) {
            if (!findString.equals(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE)) {
                List<String> args = new ArrayList();
                List<String> fields = findJson.getStringArray("fields");
                String operator = findJson.getString("operator");
                if (operator == null) {
                    return new FindOption(null, null, null);
                }
                if (operator.equals("is")) {
                    operator = " = ";
                } else if (operator.equals("contains")) {
                    operator = " LIKE ";
                    value = "%" + value + "%";
                } else {
                    Log.e(TAG, "find - Wrong Operator: [" + operator + "], should be 'is' or 'contains'");
                    return null;
                }
                String where = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
                for (String field : fields) {
                    String column = (String) ContactConstants.findFieldMap.get(field);
                    if (column != null) {
                        Pair<String, String> name = (Pair) ContactConstants.contactDataMap.get(column);
                        where = where + ((String) name.first) + operator + " ? AND " + "mimetype" + " = ? or ";
                        args.add(value);
                        args.add(name.second);
                    }
                }
                if (where == CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE) {
                    return new FindOption(null, null, null);
                }
                return new FindOption(where.substring(0, where.length() - 3), (String[]) args.toArray(new String[args.size()]), null);
            }
        }
        return new FindOption(null, null, null);
    }

    public JSONArray find(String findString) {
        Set<String> ids = getContactIds(createFindIDOption(findString));
        if (ids == null) {
            return new JSONArray();
        }
        ContactJson findJson = new ContactJson(findString);
        List<String> sortBy = findJson.getStringArray("sortBy");
        String order = getSortOrder(sortBy, findJson.getString("sortOrder"));
        String orderMimeType = order == null ? null : (String) ((Pair) ContactConstants.contactDataMap.get(sortBy.get(0))).second;
        String resultsLimit = findJson.getString("resultsLimit");
        return getContacts(ids, order, orderMimeType, resultsLimit == null ? null : Long.valueOf(resultsLimit));
    }
}
