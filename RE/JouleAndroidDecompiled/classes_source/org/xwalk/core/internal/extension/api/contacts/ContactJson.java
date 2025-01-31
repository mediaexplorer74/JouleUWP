package org.xwalk.core.internal.extension.api.contacts;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactJson {
    private static final String TAG = "ContactJson";
    private JSONObject mObject;

    public ContactJson(JSONObject o) {
        this.mObject = o;
    }

    public ContactJson(String init) {
        if (init != null) {
            try {
                this.mObject = new JSONObject(init);
            } catch (JSONException e) {
                Log.e(TAG, "Init JSON by " + init + " failed: " + e.toString());
            }
        }
    }

    public List<String> getStringArray(String name) {
        List<String> list = new ArrayList();
        if (this.mObject != null && this.mObject.has(name)) {
            try {
                JSONArray jsonArray = this.mObject.getJSONArray(name);
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                Log.e(TAG, "getStringArray(" + name + "): Failed to parse json data: " + e.toString());
            }
        }
        return list;
    }

    public String getFirstValue(String name) {
        String value = null;
        if (this.mObject != null && this.mObject.has(name)) {
            try {
                value = this.mObject.getJSONArray(name).getString(0);
            } catch (JSONException e) {
                Log.e(TAG, "getArrayTop(" + name + "): Failed to parse json data: " + e.toString());
            }
        }
        return value;
    }

    public String getString(String name) {
        String value = null;
        if (this.mObject != null && this.mObject.has(name)) {
            try {
                value = this.mObject.getString(name);
            } catch (JSONException e) {
                Log.e(TAG, "getString(" + name + "): Failed to parse json data: " + e.toString());
            }
        }
        return value;
    }

    public boolean getBoolean(String name) {
        boolean value = false;
        if (this.mObject != null && this.mObject.has(name)) {
            try {
                value = this.mObject.getBoolean(name);
            } catch (JSONException e) {
                Log.e(TAG, "getBoolean(" + name + "): Failed to parse json data: " + e.toString());
            }
        }
        return value;
    }

    public JSONObject getObject(String name) {
        JSONObject o = null;
        if (this.mObject != null && this.mObject.has(name)) {
            try {
                o = this.mObject.getJSONObject(name);
            } catch (JSONException e) {
                Log.e(TAG, "getObject(" + name + "): Failed to parse json data: " + e.toString());
            }
        }
        return o;
    }
}
