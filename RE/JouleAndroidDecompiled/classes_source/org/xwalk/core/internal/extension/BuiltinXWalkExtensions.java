package org.xwalk.core.internal.extension;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.xwalk.core.internal.XWalkExtensionInternal;
import org.xwalk.core.internal.extension.api.contacts.Contacts;
import org.xwalk.core.internal.extension.api.device_capabilities.DeviceCapabilities;
import org.xwalk.core.internal.extension.api.launchscreen.LaunchScreenExtension;
import org.xwalk.core.internal.extension.api.messaging.Messaging;
import org.xwalk.core.internal.extension.api.presentation.PresentationExtension;

public class BuiltinXWalkExtensions {
    private static final String TAG = "BuiltinXWalkExtension";
    private static HashMap<String, XWalkExtensionInternal> sBuiltinExtensions;

    static {
        sBuiltinExtensions = new HashMap();
    }

    public static void load(Context context, Activity activity) {
        String jsApiContent = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        try {
            sBuiltinExtensions.put(PresentationExtension.JS_API_PATH, new PresentationExtension(getExtensionJSFileContent(context, PresentationExtension.JS_API_PATH, true), activity));
        } catch (IOException e) {
            Log.w(TAG, "Failed to read JS API file: jsapi/presentation_api.js");
        }
        jsApiContent = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        try {
            sBuiltinExtensions.put(LaunchScreenExtension.JS_API_PATH, new LaunchScreenExtension(getExtensionJSFileContent(context, LaunchScreenExtension.JS_API_PATH, true), activity));
        } catch (IOException e2) {
            Log.w(TAG, "Failed to read JS API file: jsapi/launch_screen_api.js");
        }
        jsApiContent = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        try {
            sBuiltinExtensions.put(Contacts.JS_API_PATH, new Contacts(getExtensionJSFileContent(context, Contacts.JS_API_PATH, true), activity));
        } catch (IOException e3) {
            Log.w(TAG, "Failed to read JS API file: jsapi/contacts_api.js");
        }
        jsApiContent = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        try {
            sBuiltinExtensions.put(DeviceCapabilities.JS_API_PATH, new DeviceCapabilities(getExtensionJSFileContent(context, DeviceCapabilities.JS_API_PATH, true), activity));
        } catch (IOException e4) {
            Log.w(TAG, "Failed to read JS API file: jsapi/device_capabilities_api.js");
        }
        jsApiContent = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        try {
            sBuiltinExtensions.put(Messaging.JS_API_PATH, new Messaging(getExtensionJSFileContent(context, Messaging.JS_API_PATH, true), activity));
        } catch (IOException e5) {
            Log.w(TAG, "Failed to read JS API file: jsapi/messaging_api.js");
        }
    }

    private static String getExtensionJSFileContent(Context context, String fileName, boolean fromRaw) throws IOException {
        String result = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        InputStream inputStream = null;
        if (fromRaw) {
            try {
                Resources resource = context.getResources();
                String resName = new File(fileName).getName().split("\\.")[0];
                int resId = resource.getIdentifier(resName, "raw", context.getPackageName());
                if (resId > 0) {
                    inputStream = resource.openRawResource(resId);
                }
            } catch (NotFoundException e) {
                Log.w(TAG, "Inputstream failed to open for R.raw." + resName + ", try to find it in assets");
            } catch (Throwable th) {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        if (inputStream == null) {
            inputStream = context.getAssets().open(fileName);
        }
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        result = new String(buffer);
        if (inputStream != null) {
            inputStream.close();
        }
        return result;
    }
}
