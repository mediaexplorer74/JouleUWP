package com.adobe.phonegap.push;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

public class PushInstanceIDListenerService extends InstanceIDListenerService implements PushConstants {
    public static final String LOG_TAG = "PushPlugin_PushInstanceIDListenerService";

    public void onTokenRefresh() {
        if (!CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE.equals(getApplicationContext().getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0).getString(PushConstants.SENDER_ID, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE))) {
            startService(new Intent(this, RegistrationIntentService.class));
        }
    }
}
