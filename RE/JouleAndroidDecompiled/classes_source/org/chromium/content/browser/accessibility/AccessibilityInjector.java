package org.chromium.content.browser.accessibility;

import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.HashMap;
import java.util.Iterator;
import org.chromium.base.CommandLine;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content.browser.JavascriptInterface;
import org.chromium.content.common.ContentSwitches;
import org.json.JSONException;
import org.json.JSONObject;

public class AccessibilityInjector {
    private static final String ACCESSIBILITY_SCREEN_READER_JAVASCRIPT_TEMPLATE = "(function() {    var chooser = document.createElement('script');    chooser.type = 'text/javascript';    chooser.src = '%1s';    document.getElementsByTagName('head')[0].appendChild(chooser);  })();";
    private static final int ACCESSIBILITY_SCRIPT_INJECTION_OPTED_OUT = 0;
    private static final int ACCESSIBILITY_SCRIPT_INJECTION_PROVIDED = 1;
    private static final int ACCESSIBILITY_SCRIPT_INJECTION_UNDEFINED = -1;
    private static final String ALIAS_ACCESSIBILITY_JS_INTERFACE = "accessibility";
    private static final String ALIAS_ACCESSIBILITY_JS_INTERFACE_2 = "accessibility2";
    private static final String DEFAULT_ACCESSIBILITY_SCREEN_READER_URL = "https://ssl.gstatic.com/accessibility/javascript/android/chromeandroidvox.js";
    private static final int FEEDBACK_BRAILLE = 32;
    private static final String TOGGLE_CHROME_VOX_JAVASCRIPT = "(function() {    if (typeof cvox !== 'undefined') {        cvox.ChromeVox.host.activateOrDeactivateChromeVox(%1s);    }  })();";
    private AccessibilityManager mAccessibilityManager;
    private final String mAccessibilityScreenReaderUrl;
    protected ContentViewCore mContentViewCore;
    private final boolean mHasVibratePermission;
    protected boolean mInjectedScriptEnabled;
    protected boolean mScriptInjected;
    private TextToSpeechWrapper mTextToSpeech;
    private VibratorWrapper mVibrator;

    protected static class TextToSpeechWrapper {
        protected final TextToSpeech mTextToSpeech;
        private final View mView;

        protected TextToSpeechWrapper(View view, Context context) {
            this.mView = view;
            this.mTextToSpeech = new TextToSpeech(context, null, null);
        }

        @JavascriptInterface
        public boolean isSpeaking() {
            return this.mTextToSpeech.isSpeaking();
        }

        @JavascriptInterface
        public int speak(String text, int queueMode, String jsonParams) {
            HashMap<String, String> params = null;
            if (jsonParams != null) {
                try {
                    HashMap<String, String> params2 = new HashMap();
                    try {
                        JSONObject json = new JSONObject(jsonParams);
                        Iterator<String> keyIt = json.keys();
                        while (keyIt.hasNext()) {
                            String key = (String) keyIt.next();
                            if (json.optJSONObject(key) == null && json.optJSONArray(key) == null) {
                                params2.put(key, json.getString(key));
                            }
                        }
                        params = params2;
                    } catch (JSONException e) {
                        params = params2;
                        params = null;
                        return this.mTextToSpeech.speak(text, queueMode, params);
                    }
                } catch (JSONException e2) {
                    params = null;
                    return this.mTextToSpeech.speak(text, queueMode, params);
                }
            }
            return this.mTextToSpeech.speak(text, queueMode, params);
        }

        @JavascriptInterface
        public int stop() {
            return this.mTextToSpeech.stop();
        }

        @JavascriptInterface
        public void braille(String jsonString) {
        }

        protected void shutdownInternal() {
            this.mTextToSpeech.shutdown();
        }
    }

    private static class VibratorWrapper {
        private static final long MAX_VIBRATE_DURATION_MS = 5000;
        private final Vibrator mVibrator;

        public VibratorWrapper(Context context) {
            this.mVibrator = (Vibrator) context.getSystemService("vibrator");
        }

        @JavascriptInterface
        public boolean hasVibrator() {
            return this.mVibrator.hasVibrator();
        }

        @JavascriptInterface
        public void vibrate(long milliseconds) {
            this.mVibrator.vibrate(Math.min(milliseconds, MAX_VIBRATE_DURATION_MS));
        }

        @JavascriptInterface
        public void vibrate(long[] pattern, int repeat) {
            for (int i = AccessibilityInjector.ACCESSIBILITY_SCRIPT_INJECTION_OPTED_OUT; i < pattern.length; i += AccessibilityInjector.ACCESSIBILITY_SCRIPT_INJECTION_PROVIDED) {
                pattern[i] = Math.min(pattern[i], MAX_VIBRATE_DURATION_MS);
            }
            this.mVibrator.vibrate(pattern, AccessibilityInjector.ACCESSIBILITY_SCRIPT_INJECTION_UNDEFINED);
        }

        @JavascriptInterface
        public void cancel() {
            this.mVibrator.cancel();
        }
    }

    public static AccessibilityInjector newInstance(ContentViewCore view) {
        if (VERSION.SDK_INT >= 21) {
            return new LollipopAccessibilityInjector(view);
        }
        if (VERSION.SDK_INT >= 16) {
            return new JellyBeanAccessibilityInjector(view);
        }
        return new AccessibilityInjector(view);
    }

    protected AccessibilityInjector(ContentViewCore view) {
        this.mContentViewCore = view;
        this.mAccessibilityScreenReaderUrl = CommandLine.getInstance().getSwitchValue(ContentSwitches.ACCESSIBILITY_JAVASCRIPT_URL, DEFAULT_ACCESSIBILITY_SCREEN_READER_URL);
        this.mHasVibratePermission = this.mContentViewCore.getContext().checkCallingOrSelfPermission("android.permission.VIBRATE") == 0;
    }

    public void injectAccessibilityScriptIntoPage() {
        if (accessibilityIsAvailable() && getAxsUrlParameterValue() == ACCESSIBILITY_SCRIPT_INJECTION_UNDEFINED) {
            String js = getScreenReaderInjectingJs();
            if (this.mContentViewCore.isDeviceAccessibilityScriptInjectionEnabled() && js != null && this.mContentViewCore.isAlive()) {
                addOrRemoveAccessibilityApisIfNecessary();
                this.mContentViewCore.getWebContents().evaluateJavaScript(js, null);
                this.mInjectedScriptEnabled = true;
                this.mScriptInjected = true;
            }
        }
    }

    public void addOrRemoveAccessibilityApisIfNecessary() {
        if (accessibilityIsAvailable()) {
            addAccessibilityApis();
        } else {
            removeAccessibilityApis();
        }
    }

    public boolean accessibilityIsAvailable() {
        if (!getAccessibilityManager().isEnabled() || !this.mContentViewCore.getContentViewClient().isJavascriptEnabled()) {
            return false;
        }
        try {
            if (getAccessibilityManager().getEnabledAccessibilityServiceList(33).size() > 0) {
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void setScriptEnabled(boolean enabled) {
        if (enabled && !this.mScriptInjected) {
            injectAccessibilityScriptIntoPage();
        }
        if (accessibilityIsAvailable() && this.mInjectedScriptEnabled != enabled) {
            this.mInjectedScriptEnabled = enabled;
            if (this.mContentViewCore.isAlive()) {
                String str = TOGGLE_CHROME_VOX_JAVASCRIPT;
                Object[] objArr = new Object[ACCESSIBILITY_SCRIPT_INJECTION_PROVIDED];
                objArr[ACCESSIBILITY_SCRIPT_INJECTION_OPTED_OUT] = Boolean.toString(this.mInjectedScriptEnabled);
                this.mContentViewCore.getWebContents().evaluateJavaScript(String.format(str, objArr), null);
                if (!this.mInjectedScriptEnabled) {
                    onPageLostFocus();
                }
            }
        }
    }

    public void onPageLoadStarted() {
        this.mScriptInjected = false;
    }

    public void onPageLoadStopped() {
        injectAccessibilityScriptIntoPage();
    }

    public void onPageLostFocus() {
        if (this.mContentViewCore.isAlive()) {
            if (this.mTextToSpeech != null) {
                this.mTextToSpeech.stop();
            }
            if (this.mVibrator != null) {
                this.mVibrator.cancel();
            }
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
    }

    public boolean supportsAccessibilityAction(int action) {
        return false;
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        return false;
    }

    protected void addAccessibilityApis() {
        Context context = this.mContentViewCore.getContext();
        if (context != null) {
            if (this.mTextToSpeech == null) {
                this.mTextToSpeech = createTextToSpeechWrapper(this.mContentViewCore.getContainerView(), context);
                this.mContentViewCore.addJavascriptInterface(this.mTextToSpeech, ALIAS_ACCESSIBILITY_JS_INTERFACE);
            }
            if (this.mVibrator == null && this.mHasVibratePermission) {
                this.mVibrator = new VibratorWrapper(context);
                this.mContentViewCore.addJavascriptInterface(this.mVibrator, ALIAS_ACCESSIBILITY_JS_INTERFACE_2);
            }
        }
    }

    protected void removeAccessibilityApis() {
        if (this.mTextToSpeech != null) {
            this.mContentViewCore.removeJavascriptInterface(ALIAS_ACCESSIBILITY_JS_INTERFACE);
            this.mTextToSpeech.stop();
            this.mTextToSpeech.shutdownInternal();
            this.mTextToSpeech = null;
        }
        if (this.mVibrator != null) {
            this.mContentViewCore.removeJavascriptInterface(ALIAS_ACCESSIBILITY_JS_INTERFACE_2);
            this.mVibrator.cancel();
            this.mVibrator = null;
        }
    }

    private int getAxsUrlParameterValue() {
        int i = ACCESSIBILITY_SCRIPT_INJECTION_UNDEFINED;
        if (this.mContentViewCore.getWebContents().getUrl() != null) {
            try {
                String axs = Uri.parse(this.mContentViewCore.getWebContents().getUrl()).getQueryParameter("axs");
                if (axs != null) {
                    i = Integer.parseInt(axs);
                }
            } catch (NumberFormatException e) {
            } catch (IllegalArgumentException e2) {
            }
        }
        return i;
    }

    private String getScreenReaderInjectingJs() {
        String str = ACCESSIBILITY_SCREEN_READER_JAVASCRIPT_TEMPLATE;
        Object[] objArr = new Object[ACCESSIBILITY_SCRIPT_INJECTION_PROVIDED];
        objArr[ACCESSIBILITY_SCRIPT_INJECTION_OPTED_OUT] = this.mAccessibilityScreenReaderUrl;
        return String.format(str, objArr);
    }

    private AccessibilityManager getAccessibilityManager() {
        if (this.mAccessibilityManager == null) {
            this.mAccessibilityManager = (AccessibilityManager) this.mContentViewCore.getContext().getSystemService(ALIAS_ACCESSIBILITY_JS_INTERFACE);
        }
        return this.mAccessibilityManager;
    }

    protected TextToSpeechWrapper createTextToSpeechWrapper(View view, Context context) {
        return new TextToSpeechWrapper(view, context);
    }
}
