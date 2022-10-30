package org.xwalk.core.internal.extension.api.contacts;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import com.adobe.phonegap.push.PushConstants;
import com.google.android.gms.common.ConnectionResult;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.XWalkExtensionWithActivityStateListener;

public class Contacts extends XWalkExtensionWithActivityStateListener {
    public static final String JS_API_PATH = "jsapi/contacts_api.js";
    private static final String NAME = "xwalk.experimental.contacts";
    private static final String TAG = "Contacts";
    private final ContactEventListener mObserver;
    private final ContentResolver mResolver;

    public Contacts(String jsApiContent, Activity activity) {
        super(NAME, jsApiContent, activity);
        this.mResolver = activity.getContentResolver();
        this.mObserver = new ContactEventListener(new Handler(), this, this.mResolver);
        this.mResolver.registerContentObserver(android.provider.ContactsContract.Contacts.CONTENT_URI, true, this.mObserver);
    }

    public String onSyncMessage(int instanceID, String message) {
        return null;
    }

    public void onMessage(int instanceID, String message) {
        if (!message.isEmpty()) {
            try {
                JSONObject jsonInput = new JSONObject(message);
                String cmd = jsonInput.getString("cmd");
                if (cmd.equals("addEventListener")) {
                    this.mObserver.startListening();
                    return;
                }
                JSONObject jsonOutput = new JSONObject();
                jsonOutput.put("asyncCallId", jsonInput.getString("asyncCallId"));
                if (cmd.equals("save")) {
                    jsonOutput.put(PushConstants.PARSE_COM_DATA, new ContactSaver(this.mResolver).save(jsonInput.getString("contact")));
                } else if (cmd.equals("find")) {
                    jsonOutput.put(PushConstants.PARSE_COM_DATA, new ContactFinder(this.mResolver).find(jsonInput.has("options") ? jsonInput.getString("options") : null));
                } else if (cmd.equals("remove")) {
                    ArrayList<ContentProviderOperation> ops = new ArrayList();
                    ops.add(ContentProviderOperation.newDelete(RawContacts.CONTENT_URI).withSelection("contact_id=?", new String[]{jsonInput.getString("contactId")}).build());
                    this.mResolver.applyBatch("com.android.contacts", ops);
                } else if (cmd.equals("clear")) {
                    handleClear();
                } else {
                    Log.e(TAG, "Unexpected message received: " + message);
                    return;
                }
                postMessage(instanceID, jsonOutput.toString());
            } catch (Exception e) {
                if ((e instanceof RemoteException) || (e instanceof OperationApplicationException) || (e instanceof SecurityException)) {
                    Log.e(TAG, "onMessage - Failed to apply batch: " + e.toString());
                    return;
                }
                throw new RuntimeException(e);
            } catch (JSONException e2) {
                Log.e(TAG, e2.toString());
            }
        }
    }

    private void handleClear() {
        Cursor c = null;
        try {
            c = this.mResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (c.moveToNext()) {
                this.mResolver.delete(Uri.withAppendedPath(android.provider.ContactsContract.Contacts.CONTENT_LOOKUP_URI, c.getString(c.getColumnIndex("lookup"))), null, null);
            }
            if (c != null) {
                c.close();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "handleClear - failed to query: " + e.toString());
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    public void onActivityStateChange(Activity activity, int newState) {
        switch (newState) {
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                this.mObserver.onResume();
                this.mResolver.registerContentObserver(android.provider.ContactsContract.Contacts.CONTENT_URI, true, this.mObserver);
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                this.mResolver.unregisterContentObserver(this.mObserver);
            default:
        }
    }
}
