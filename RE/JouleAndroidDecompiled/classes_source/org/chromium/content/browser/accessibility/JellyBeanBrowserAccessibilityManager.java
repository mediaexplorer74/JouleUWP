package org.chromium.content.browser.accessibility;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import java.util.List;
import org.chromium.base.JNINamespace;
import org.chromium.content.browser.ContentViewCore;

@JNINamespace("content")
@TargetApi(16)
public class JellyBeanBrowserAccessibilityManager extends BrowserAccessibilityManager {
    private AccessibilityNodeProvider mAccessibilityNodeProvider;

    /* renamed from: org.chromium.content.browser.accessibility.JellyBeanBrowserAccessibilityManager.1 */
    class C03551 extends AccessibilityNodeProvider {
        final /* synthetic */ BrowserAccessibilityManager val$delegate;

        C03551(BrowserAccessibilityManager browserAccessibilityManager) {
            this.val$delegate = browserAccessibilityManager;
        }

        public AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
            return this.val$delegate.createAccessibilityNodeInfo(virtualViewId);
        }

        public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String text, int virtualViewId) {
            return this.val$delegate.findAccessibilityNodeInfosByText(text, virtualViewId);
        }

        public boolean performAction(int virtualViewId, int action, Bundle arguments) {
            return this.val$delegate.performAction(virtualViewId, action, arguments);
        }
    }

    JellyBeanBrowserAccessibilityManager(long nativeBrowserAccessibilityManagerAndroid, ContentViewCore contentViewCore) {
        super(nativeBrowserAccessibilityManagerAndroid, contentViewCore);
        this.mAccessibilityNodeProvider = new C03551(this);
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        return this.mAccessibilityNodeProvider;
    }
}
