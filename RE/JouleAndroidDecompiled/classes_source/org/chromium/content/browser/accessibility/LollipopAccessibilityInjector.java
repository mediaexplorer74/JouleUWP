package org.chromium.content.browser.accessibility;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import java.util.Iterator;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content.browser.JavascriptInterface;
import org.json.JSONException;
import org.json.JSONObject;

@TargetApi(21)
class LollipopAccessibilityInjector extends JellyBeanAccessibilityInjector {

    protected static class LTextToSpeechWrapper extends TextToSpeechWrapper {
        private LTextToSpeechWrapper(View view, Context context) {
            super(view, context);
        }

        @JavascriptInterface
        public int speak(String text, int queueMode, String jsonParams) {
            Bundle bundle = null;
            if (jsonParams != null) {
                try {
                    Bundle bundle2 = new Bundle();
                    try {
                        JSONObject json = new JSONObject(jsonParams);
                        Iterator<String> keyIt = json.keys();
                        while (keyIt.hasNext()) {
                            String key = (String) keyIt.next();
                            if (json.optJSONObject(key) == null && json.optJSONArray(key) == null) {
                                bundle2.putCharSequence(key, json.getString(key));
                            }
                        }
                        bundle = bundle2;
                    } catch (JSONException e) {
                        bundle = bundle2;
                        bundle = null;
                        return this.mTextToSpeech.speak(text, queueMode, bundle, null);
                    }
                } catch (JSONException e2) {
                    bundle = null;
                    return this.mTextToSpeech.speak(text, queueMode, bundle, null);
                }
            }
            return this.mTextToSpeech.speak(text, queueMode, bundle, null);
        }
    }

    protected LollipopAccessibilityInjector(ContentViewCore view) {
        super(view);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        info.setMovementGranularities(31);
        info.addAction(AccessibilityAction.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
        info.addAction(AccessibilityAction.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY);
        info.addAction(AccessibilityAction.ACTION_NEXT_HTML_ELEMENT);
        info.addAction(AccessibilityAction.ACTION_PREVIOUS_HTML_ELEMENT);
        info.addAction(AccessibilityAction.ACTION_CLICK);
        info.setClickable(true);
    }

    protected TextToSpeechWrapper createTextToSpeechWrapper(View view, Context context) {
        return new LTextToSpeechWrapper(context, null);
    }
}
