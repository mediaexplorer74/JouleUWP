package com.adobe.phonegap.push;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

public class RegistrationIntentService extends IntentService implements PushConstants {
    public static final String LOG_TAG = "PushPlugin_RegistrationIntentService";

    public RegistrationIntentService() {
        super(LOG_TAG);
    }

    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0);
        try {
            String token = InstanceID.getInstance(this).getToken(sharedPreferences.getString(PushConstants.SENDER_ID, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(LOG_TAG, "new GCM Registration Token: " + token);
            Editor editor = sharedPreferences.edit();
            editor.putString(PushConstants.REGISTRATION_ID, token);
            editor.commit();
        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed to complete token refresh", e);
        }
    }
}
