package org.xwalk.core.internal;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import org.chromium.content.browser.ContentView;
import org.chromium.content.browser.ContentViewCore;

public class XWalkContentView extends ContentView {
    private static final String TAG = "XWalkContentView";
    private XWalkViewInternal mXWalkView;

    XWalkContentView(Context context, ContentViewCore cvc, XWalkViewInternal xwView) {
        super(context, cvc);
        this.mXWalkView = xwView;
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.mXWalkView.onCreateInputConnection(outAttrs);
    }

    public InputConnection onCreateInputConnectionSuper(EditorInfo outAttrs) {
        return super.onCreateInputConnection(outAttrs);
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (VERSION.SDK_INT < 16) {
            return super.performAccessibilityAction(action, arguments);
        }
        if (this.mContentViewCore.supportsAccessibilityAction(action)) {
            return this.mContentViewCore.performAccessibilityAction(action, arguments);
        }
        return super.performAccessibilityAction(action, arguments);
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (VERSION.SDK_INT < 16) {
            return super.getAccessibilityNodeProvider();
        }
        AccessibilityNodeProvider provider = this.mContentViewCore.getAccessibilityNodeProvider();
        return provider == null ? super.getAccessibilityNodeProvider() : provider;
    }

    public boolean performLongClick() {
        return this.mXWalkView.performLongClickDelegate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mXWalkView.onTouchEventDelegate(event)) {
            return true;
        }
        return this.mContentViewCore.onTouchEvent(event);
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        this.mXWalkView.onScrollChangedDelegate(l, t, oldl, oldt);
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        this.mXWalkView.onFocusChangedDelegate(gainFocus, direction, previouslyFocusedRect);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }
}
