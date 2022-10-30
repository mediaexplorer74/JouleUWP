package org.chromium.content.browser.accessibility;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.chromium.blink_public.web.WebInputEventModifier;
import org.chromium.blink_public.web.WebTextInputFlags;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content.browser.JavascriptInterface;
import org.json.JSONException;
import org.json.JSONObject;

@TargetApi(16)
class JellyBeanAccessibilityInjector extends AccessibilityInjector {
    private static final String ACCESSIBILITY_ANDROIDVOX_TEMPLATE = "cvox.AndroidVox.performAction('%1s')";
    private static final String ALIAS_TRAVERSAL_JS_INTERFACE = "accessibilityTraversal";
    private JSONObject mAccessibilityJSONObject;
    private CallbackHandler mCallback;

    private static class CallbackHandler {
        private static final String JAVASCRIPT_ACTION_TEMPLATE = "(function() {  retVal = false;  try {    retVal = %s;  } catch (e) {    retVal = false;  }  %s.onResult(%d, retVal);})()";
        private static final long RESULT_TIMEOUT = 5000;
        private final String mInterfaceName;
        private boolean mResult;
        private long mResultId;
        private final AtomicInteger mResultIdCounter;
        private final Object mResultLock;

        private CallbackHandler(String interfaceName) {
            this.mResultIdCounter = new AtomicInteger();
            this.mResultLock = new Object();
            this.mResult = false;
            this.mResultId = -1;
            this.mInterfaceName = interfaceName;
        }

        private boolean performAction(ContentViewCore contentView, String code) {
            int resultId = this.mResultIdCounter.getAndIncrement();
            contentView.getWebContents().evaluateJavaScript(String.format(Locale.US, JAVASCRIPT_ACTION_TEMPLATE, new Object[]{code, this.mInterfaceName, Integer.valueOf(resultId)}), null);
            return getResultAndClear(resultId);
        }

        private boolean getResultAndClear(int resultId) {
            boolean result;
            synchronized (this.mResultLock) {
                result = waitForResultTimedLocked(resultId) ? this.mResult : false;
                clearResultLocked();
            }
            return result;
        }

        private void clearResultLocked() {
            this.mResultId = -1;
            this.mResult = false;
        }

        private boolean waitForResultTimedLocked(int resultId) {
            long startTimeMillis = SystemClock.uptimeMillis();
            while (this.mResultId != ((long) resultId)) {
                try {
                    if (this.mResultId > ((long) resultId)) {
                        return false;
                    }
                    long waitTimeMillis = RESULT_TIMEOUT - (SystemClock.uptimeMillis() - startTimeMillis);
                    if (waitTimeMillis <= 0) {
                        return false;
                    }
                    this.mResultLock.wait(waitTimeMillis);
                } catch (InterruptedException e) {
                }
            }
            return true;
        }

        @JavascriptInterface
        public void onResult(String id, String result) {
            try {
                long resultId = Long.parseLong(id);
                synchronized (this.mResultLock) {
                    if (resultId > this.mResultId) {
                        this.mResult = Boolean.parseBoolean(result);
                        this.mResultId = resultId;
                    }
                    this.mResultLock.notifyAll();
                }
            } catch (NumberFormatException e) {
            }
        }
    }

    protected JellyBeanAccessibilityInjector(ContentViewCore view) {
        super(view);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        info.setMovementGranularities(31);
        info.addAction(WebTextInputFlags.AutocapitalizeWords);
        info.addAction(WebTextInputFlags.AutocapitalizeSentences);
        info.addAction(WebInputEventModifier.NumLockOn);
        info.addAction(WebInputEventModifier.IsLeft);
        info.addAction(16);
        info.setClickable(true);
    }

    public boolean supportsAccessibilityAction(int action) {
        if (action == WebTextInputFlags.AutocapitalizeWords || action == WebTextInputFlags.AutocapitalizeSentences || action == WebInputEventModifier.NumLockOn || action == WebInputEventModifier.IsLeft || action == 16) {
            return true;
        }
        return false;
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (!accessibilityIsAvailable() || !this.mContentViewCore.isAlive() || !this.mInjectedScriptEnabled || !this.mScriptInjected) {
            return false;
        }
        boolean actionSuccessful = sendActionToAndroidVox(action, arguments);
        if (!actionSuccessful) {
            return actionSuccessful;
        }
        this.mContentViewCore.getWebContents().showImeIfNeeded();
        return actionSuccessful;
    }

    protected void addAccessibilityApis() {
        super.addAccessibilityApis();
        if (this.mContentViewCore.getContext() != null && this.mCallback == null) {
            this.mCallback = new CallbackHandler(null);
            this.mContentViewCore.addJavascriptInterface(this.mCallback, ALIAS_TRAVERSAL_JS_INTERFACE);
        }
    }

    protected void removeAccessibilityApis() {
        super.removeAccessibilityApis();
        if (this.mCallback != null) {
            this.mContentViewCore.removeJavascriptInterface(ALIAS_TRAVERSAL_JS_INTERFACE);
            this.mCallback = null;
        }
    }

    private boolean sendActionToAndroidVox(int action, Bundle arguments) {
        if (this.mCallback == null) {
            return false;
        }
        if (this.mAccessibilityJSONObject == null) {
            this.mAccessibilityJSONObject = new JSONObject();
        } else {
            Iterator<?> keys = this.mAccessibilityJSONObject.keys();
            while (keys.hasNext()) {
                keys.next();
                keys.remove();
            }
        }
        try {
            this.mAccessibilityJSONObject.accumulate("action", Integer.valueOf(action));
            if (arguments != null) {
                if (action == WebTextInputFlags.AutocapitalizeWords || action == WebTextInputFlags.AutocapitalizeSentences) {
                    this.mAccessibilityJSONObject.accumulate("granularity", Integer.valueOf(arguments.getInt(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT)));
                } else if (action == WebInputEventModifier.NumLockOn || action == WebInputEventModifier.IsLeft) {
                    this.mAccessibilityJSONObject.accumulate("element", arguments.getString(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_HTML_ELEMENT_STRING));
                }
            }
            String jsonString = this.mAccessibilityJSONObject.toString();
            return this.mCallback.performAction(this.mContentViewCore, String.format(Locale.US, ACCESSIBILITY_ANDROIDVOX_TEMPLATE, new Object[]{jsonString}));
        } catch (JSONException e) {
            return false;
        }
    }
}
