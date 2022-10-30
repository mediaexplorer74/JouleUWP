package org.xwalk.core.internal.extension.api.contacts;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import com.adobe.phonegap.push.PushConstants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

public class ContactEventListener extends ContentObserver {
    private static final String TAG = "ContactsEventListener";
    private HashSet<String> mContactIDs;
    private final Contacts mContacts;
    private boolean mIsListening;
    private HashMap<String, String> mRawID2ContactIDMaps;
    private HashMap<String, String> mRawID2VersionMaps;
    private final ContentResolver mResolver;

    public ContactEventListener(Handler handler, Contacts instance, ContentResolver resolver) {
        super(handler);
        this.mIsListening = false;
        this.mContacts = instance;
        this.mResolver = resolver;
    }

    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        if (this.mIsListening) {
            notifyChanges(false);
        }
    }

    protected void startListening() {
        if (!this.mIsListening) {
            this.mIsListening = true;
            this.mContactIDs = getAllContactIDs();
            readAllRawContactInfo();
        }
    }

    protected void onResume() {
        if (this.mIsListening) {
            notifyChanges(true);
        }
    }

    private void notifyChanges(boolean bResume) {
        try {
            JSONObject jsonOutput = new JSONObject();
            HashSet<String> contactIDs = getAllContactIDs();
            if (bResume || contactIDs.size() > this.mContactIDs.size()) {
                HashSet<String> addedIDs = getDiffSet(contactIDs, this.mContactIDs);
                if (!bResume || addedIDs.size() > 0) {
                    jsonOutput.put("added", convertSet2JSONArray(addedIDs));
                }
            } else if (bResume || contactIDs.size() < this.mContactIDs.size()) {
                HashSet<String> removedIDs = getDiffSet(this.mContactIDs, contactIDs);
                if (!bResume || contactIDs.size() < 0) {
                    jsonOutput.put("removed", convertSet2JSONArray(removedIDs));
                }
            } else {
                HashSet<String> commonIDs;
                if (bResume) {
                    commonIDs = getIntersectSet(this.mContactIDs, contactIDs);
                } else {
                    commonIDs = contactIDs;
                }
                HashSet<String> modifiedIDs = compareAllRawContactInfo(commonIDs);
                if (modifiedIDs.size() != 0) {
                    jsonOutput.put("modified", convertSet2JSONArray(modifiedIDs));
                }
            }
            notifyContactChanged(jsonOutput);
            this.mContactIDs = contactIDs;
            readAllRawContactInfo();
        } catch (JSONException e) {
            Log.e(TAG, "notifyChanges: " + e.toString());
        }
    }

    private void notifyContactChanged(JSONObject outObject) {
        if (outObject != null && outObject.length() != 0) {
            try {
                JSONObject jsonOutput = new JSONObject();
                jsonOutput.put("reply", "contactschange");
                jsonOutput.put(PushConstants.PARSE_COM_DATA, outObject);
                this.mContacts.broadcastMessage(jsonOutput.toString());
            } catch (JSONException e) {
                Log.e(TAG, "notifyContactChanged: " + e.toString());
            }
        }
    }

    private JSONArray convertSet2JSONArray(HashSet<String> set) {
        JSONArray jsonArray = new JSONArray();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            jsonArray.put(iterator.next());
        }
        return jsonArray;
    }

    private HashSet<String> getAllContactIDs() {
        Cursor c = null;
        try {
            c = this.mResolver.query(Contacts.CONTENT_URI, null, null, null, null);
            HashSet<String> hashSet = new HashSet();
            while (c.moveToNext()) {
                hashSet.add(c.getString(c.getColumnIndex(MessagingSmsConsts.ID)));
            }
            if (c == null) {
                return hashSet;
            }
            c.close();
            return hashSet;
        } catch (SecurityException e) {
            Log.e(TAG, "getAllContactIDs: " + e.toString());
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private HashSet<String> getIntersectSet(HashSet<String> setA, HashSet<String> setB) {
        HashSet<String> resultSet = new HashSet();
        resultSet.addAll(setA);
        resultSet.retainAll(setB);
        return resultSet;
    }

    private HashSet<String> getDiffSet(HashSet<String> setA, HashSet<String> setB) {
        HashSet<String> resultSet = new HashSet();
        resultSet.addAll(setA);
        resultSet.removeAll(setB);
        return resultSet;
    }

    private void readAllRawContactInfo() {
        Cursor c = null;
        try {
            c = this.mResolver.query(RawContacts.CONTENT_URI, null, null, null, null);
            this.mRawID2ContactIDMaps = new HashMap();
            this.mRawID2VersionMaps = new HashMap();
            while (c.moveToNext()) {
                String contactID = c.getString(c.getColumnIndex("contact_id"));
                String rawContactID = c.getString(c.getColumnIndex(MessagingSmsConsts.ID));
                String version = c.getString(c.getColumnIndex("version"));
                this.mRawID2ContactIDMaps.put(rawContactID, contactID);
                this.mRawID2VersionMaps.put(rawContactID, version);
            }
            if (c != null) {
                c.close();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "readAllRawContactInfo: " + e.toString());
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private HashSet<String> compareAllRawContactInfo(HashSet<String> commonSet) {
        HashSet<String> contactIDs;
        SecurityException e;
        HashMap<String, String> hashMap;
        HashSet<String> hashSet;
        Throwable th;
        Cursor c = null;
        try {
            c = this.mResolver.query(RawContacts.CONTENT_URI, null, null, null, null);
            contactIDs = new HashSet();
            try {
                HashMap<String, String> compareMaps = new HashMap();
                while (c.moveToNext()) {
                    try {
                        compareMaps.put(c.getString(c.getColumnIndex(MessagingSmsConsts.ID)), c.getString(c.getColumnIndex("version")));
                    } catch (SecurityException e2) {
                        e = e2;
                        hashMap = compareMaps;
                        hashSet = contactIDs;
                    } catch (Throwable th2) {
                        th = th2;
                        hashMap = compareMaps;
                        hashSet = contactIDs;
                    }
                }
                if (c != null) {
                    c.close();
                }
                for (String rawContactID : compareMaps.keySet()) {
                    String newVersion = (String) compareMaps.get(rawContactID);
                    String oldVersion = (String) this.mRawID2VersionMaps.get(rawContactID);
                    if (oldVersion == null || !newVersion.equals(oldVersion)) {
                        String contactID = (String) this.mRawID2ContactIDMaps.get(rawContactID);
                        if (contactID != null && commonSet.contains(contactID)) {
                            contactIDs.add(contactID);
                        }
                    }
                }
                hashSet = contactIDs;
            } catch (SecurityException e3) {
                e = e3;
                hashSet = contactIDs;
                try {
                    Log.e(TAG, "compareAllRawContactInfo: " + e.toString());
                    contactIDs = null;
                    if (c != null) {
                        c.close();
                    }
                    return contactIDs;
                } catch (Throwable th3) {
                    th = th3;
                    if (c != null) {
                        c.close();
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                hashSet = contactIDs;
                if (c != null) {
                    c.close();
                }
                throw th;
            }
        } catch (SecurityException e4) {
            e = e4;
            Log.e(TAG, "compareAllRawContactInfo: " + e.toString());
            contactIDs = null;
            if (c != null) {
                c.close();
            }
            return contactIDs;
        }
        return contactIDs;
    }
}
