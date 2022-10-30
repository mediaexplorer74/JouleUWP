package com.ohh2ahh.appavailability;

import android.content.pm.PackageManager.NameNotFoundException;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.json.JSONArray;
import org.json.JSONException;

public class AppAvailability extends CordovaPlugin {
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (!action.equals("checkAvailability")) {
            return false;
        }
        checkAvailability(args.getString(0), callbackContext);
        return true;
    }

    public boolean appInstalled(String uri) {
        try {
            this.cordova.getActivity().getApplicationContext().getPackageManager().getPackageInfo(uri, 1);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    private void checkAvailability(String uri, CallbackContext callbackContext) {
        if (appInstalled(uri)) {
            callbackContext.success();
        } else {
            callbackContext.error(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
        }
    }
}
