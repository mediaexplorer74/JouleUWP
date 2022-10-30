package org.chromium.content.browser;

import android.content.Context;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeProvider;

class JellyBeanContentView extends ContentView {
    JellyBeanContentView(Context context, ContentViewCore cvc) {
        super(context, cvc);
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (this.mContentViewCore.supportsAccessibilityAction(action)) {
            return this.mContentViewCore.performAccessibilityAction(action, arguments);
        }
        return super.performAccessibilityAction(action, arguments);
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        AccessibilityNodeProvider provider = this.mContentViewCore.getAccessibilityNodeProvider();
        return provider != null ? provider : super.getAccessibilityNodeProvider();
    }
}
