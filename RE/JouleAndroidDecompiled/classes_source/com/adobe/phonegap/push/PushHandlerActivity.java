package com.adobe.phonegap.push;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

public class PushHandlerActivity extends Activity implements PushConstants {
    private static String LOG_TAG;

    static {
        LOG_TAG = "PushPlugin_PushHandlerActivity";
    }

    public void onCreate(Bundle savedInstanceState) {
        new GCMIntentService().setNotification(getIntent().getIntExtra(PushConstants.NOT_ID, 0), CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");
        boolean isPushPluginActive = PushPlugin.isActive();
        processPushBundle(isPushPluginActive);
        finish();
        if (!isPushPluginActive) {
            forceMainActivityReload();
        }
    }

    private void processPushBundle(boolean isPushPluginActive) {
        boolean z = false;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Bundle originalExtras = extras.getBundle(PushConstants.PUSH_BUNDLE);
            originalExtras.putBoolean(PushConstants.FOREGROUND, false);
            String str = PushConstants.COLDSTART;
            if (!isPushPluginActive) {
                z = true;
            }
            originalExtras.putBoolean(str, z);
            originalExtras.putString(PushConstants.CALLBACK, extras.getString(PushConstants.CALLBACK));
            PushPlugin.sendExtras(originalExtras);
        }
    }

    private void forceMainActivityReload() {
        startActivity(getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName()));
    }

    protected void onResume() {
        super.onResume();
        ((NotificationManager) getSystemService("notification")).cancelAll();
    }
}
