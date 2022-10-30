package org.apache.cordova.statusbar;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.Log;
import android.view.Window;
import com.adobe.phonegap.push.PushConstants;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.chromium.blink_public.web.WebInputEventModifier;
import org.chromium.ui.base.PageTransition;
import org.json.JSONException;

public class StatusBar extends CordovaPlugin {
    private static final String TAG = "StatusBar";

    /* renamed from: org.apache.cordova.statusbar.StatusBar.1 */
    class C02931 implements Runnable {
        final /* synthetic */ CordovaInterface val$cordova;

        C02931(CordovaInterface cordovaInterface) {
            this.val$cordova = cordovaInterface;
        }

        public void run() {
            this.val$cordova.getActivity().getWindow().clearFlags(WebInputEventModifier.IsLeft);
            StatusBar.this.setStatusBarBackgroundColor(StatusBar.this.preferences.getString("StatusBarBackgroundColor", "#000000"));
        }
    }

    /* renamed from: org.apache.cordova.statusbar.StatusBar.2 */
    class C02942 implements Runnable {
        final /* synthetic */ Window val$window;

        C02942(Window window) {
            this.val$window = window;
        }

        public void run() {
            this.val$window.clearFlags(WebInputEventModifier.NumLockOn);
        }
    }

    /* renamed from: org.apache.cordova.statusbar.StatusBar.3 */
    class C02953 implements Runnable {
        final /* synthetic */ Window val$window;

        C02953(Window window) {
            this.val$window = window;
        }

        public void run() {
            this.val$window.addFlags(WebInputEventModifier.NumLockOn);
        }
    }

    /* renamed from: org.apache.cordova.statusbar.StatusBar.4 */
    class C02964 implements Runnable {
        final /* synthetic */ CordovaArgs val$args;

        C02964(CordovaArgs cordovaArgs) {
            this.val$args = cordovaArgs;
        }

        public void run() {
            try {
                StatusBar.this.setStatusBarBackgroundColor(this.val$args.getString(0));
            } catch (JSONException e) {
                Log.e(StatusBar.TAG, "Invalid hexString argument, use f.i. '#777777'");
            }
        }
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.v(TAG, "StatusBar: initialization");
        super.initialize(cordova, webView);
        this.cordova.getActivity().runOnUiThread(new C02931(cordova));
    }

    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.v(TAG, "Executing action: " + action);
        Activity activity = this.cordova.getActivity();
        Window window = activity.getWindow();
        if ("_ready".equals(action)) {
            callbackContext.sendPluginResult(new PluginResult(Status.OK, (window.getAttributes().flags & WebInputEventModifier.NumLockOn) == 0));
        }
        if ("show".equals(action)) {
            this.cordova.getActivity().runOnUiThread(new C02942(window));
            return true;
        } else if ("hide".equals(action)) {
            this.cordova.getActivity().runOnUiThread(new C02953(window));
            return true;
        } else if ("backgroundColorByHexString".equals(action)) {
            this.cordova.getActivity().runOnUiThread(new C02964(args));
            return true;
        } else if (!"getHeightInPixels".equals(action)) {
            return false;
        } else {
            Resources r = activity.getWindow().getDecorView().getResources();
            int heightInPixels = 0;
            int resourceId = r.getIdentifier("status_bar_height", "dimen", PushConstants.ANDROID);
            if (resourceId > 0) {
                heightInPixels = (int) (((float) r.getDimensionPixelSize(resourceId)) / r.getDisplayMetrics().density);
            }
            callbackContext.sendPluginResult(new PluginResult(Status.OK, heightInPixels));
            return true;
        }
    }

    private void setStatusBarBackgroundColor(String colorPref) {
        if (VERSION.SDK_INT >= 21 && colorPref != null && !colorPref.isEmpty()) {
            Window window = this.cordova.getActivity().getWindow();
            window.clearFlags(PageTransition.HOME_PAGE);
            window.addFlags(ExploreByTouchHelper.INVALID_ID);
            try {
                window.getClass().getDeclaredMethod("setStatusBarColor", new Class[]{Integer.TYPE}).invoke(window, new Object[]{Integer.valueOf(Color.parseColor(colorPref))});
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Invalid hexString argument, use f.i. '#999999'");
            } catch (Exception e2) {
                Log.w(TAG, "Method window.setStatusBarColor not found for SDK level " + VERSION.SDK_INT);
            }
        }
    }
}
