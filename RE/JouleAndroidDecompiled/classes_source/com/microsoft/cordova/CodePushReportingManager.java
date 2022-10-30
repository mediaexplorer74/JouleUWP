package com.microsoft.cordova;

import android.app.Activity;
import java.util.Locale;
import org.apache.cordova.CordovaWebView;

public class CodePushReportingManager {
    private CodePushPreferences codePushPreferences;
    private Activity cordovaActivity;

    /* renamed from: com.microsoft.cordova.CodePushReportingManager.1 */
    class C02141 implements Runnable {
        final /* synthetic */ String val$script;
        final /* synthetic */ CordovaWebView val$webView;

        C02141(CordovaWebView cordovaWebView, String str) {
            this.val$webView = cordovaWebView;
            this.val$script = str;
        }

        public void run() {
            this.val$webView.loadUrl(this.val$script);
        }
    }

    public enum Status {
        STORE_VERSION(0),
        UPDATE_CONFIRMED(1),
        UPDATE_ROLLED_BACK(2);
        
        private int value;

        private Status(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    public CodePushReportingManager(Activity cordovaActivity, CodePushPreferences codePushPreferences) {
        this.cordovaActivity = cordovaActivity;
        this.codePushPreferences = codePushPreferences;
    }

    public void reportStatus(Status status, String label, String appVersion, String deploymentKey, CordovaWebView webView) {
        if (deploymentKey != null && !deploymentKey.isEmpty()) {
            String script = String.format(Locale.US, "javascript:document.addEventListener(\"deviceready\", function () { window.codePush.reportStatus(%d, %s, %s, %s, %s, %s); });", new Object[]{Integer.valueOf(status.getValue()), convertStringParameter(label), convertStringParameter(appVersion), convertStringParameter(deploymentKey), convertStringParameter(this.codePushPreferences.getLastVersionLabelOrAppVersion()), convertStringParameter(this.codePushPreferences.getLastVersionDeploymentKey())});
            if (status == Status.STORE_VERSION || status == Status.UPDATE_CONFIRMED) {
                CodePushPreferences codePushPreferences = this.codePushPreferences;
                if (label != null) {
                    appVersion = label;
                }
                codePushPreferences.saveLastVersion(appVersion, deploymentKey);
            }
            this.cordovaActivity.runOnUiThread(new C02141(webView, script));
        }
    }

    private String convertStringParameter(String input) {
        if (input == null) {
            return "undefined";
        }
        return "'" + input + "'";
    }
}
