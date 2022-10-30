package org.chromium.content.browser;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import com.adobe.phonegap.push.PushConstants;
import org.chromium.base.Log;
import org.chromium.base.TraceEvent;
import org.chromium.base.VisibleForTesting;
import org.chromium.content.browser.ContentViewCore.InternalAccessDelegate;
import org.chromium.content.browser.ContentViewCore.SmartClipDataListener;

public class ContentView extends FrameLayout implements InternalAccessDelegate, SmartClipProvider {
    private static final String TAG = "cr.ContentView";
    protected final ContentViewCore mContentViewCore;

    /* renamed from: org.chromium.content.browser.ContentView.1 */
    class C05991 implements SmartClipDataListener {
        final /* synthetic */ Handler val$resultHandler;

        C05991(Handler handler) {
            this.val$resultHandler = handler;
        }

        public void onSmartClipDataExtracted(String text, String html, Rect clipRect) {
            Bundle bundle = new Bundle();
            bundle.putString("url", ContentView.this.mContentViewCore.getWebContents().getVisibleUrl());
            bundle.putString(PushConstants.TITLE, ContentView.this.mContentViewCore.getWebContents().getTitle());
            bundle.putParcelable("rect", clipRect);
            bundle.putString(PushConstants.STYLE_TEXT, text);
            bundle.putString("html", html);
            try {
                Message msg = Message.obtain(this.val$resultHandler, 0);
                msg.setData(bundle);
                msg.sendToTarget();
            } catch (Exception e) {
                Log.m32e(ContentView.TAG, "Error calling handler for smart clip data: ", e);
            }
        }
    }

    public static ContentView newInstance(Context context, ContentViewCore cvc) {
        if (VERSION.SDK_INT < 16) {
            return new ContentView(context, cvc);
        }
        return new JellyBeanContentView(context, cvc);
    }

    protected ContentView(Context context, ContentViewCore cvc) {
        super(context, null, 16842885);
        if (getScrollBarStyle() == 0) {
            setHorizontalScrollBarEnabled(false);
            setVerticalScrollBarEnabled(false);
        }
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.mContentViewCore = cvc;
    }

    @VisibleForTesting
    public void onProvideVirtualStructure(ViewStructure structure) {
        this.mContentViewCore.onProvideVirtualStructure(structure);
    }

    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    protected void onSizeChanged(int w, int h, int ow, int oh) {
        try {
            TraceEvent.begin("ContentView.onSizeChanged");
            super.onSizeChanged(w, h, ow, oh);
            this.mContentViewCore.onSizeChanged(w, h, ow, oh);
        } finally {
            TraceEvent.end("ContentView.onSizeChanged");
        }
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.mContentViewCore.onCreateInputConnection(outAttrs);
    }

    public boolean onCheckIsTextEditor() {
        return this.mContentViewCore.onCheckIsTextEditor();
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        try {
            TraceEvent.begin("ContentView.onFocusChanged");
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
            this.mContentViewCore.onFocusChanged(gainFocus);
        } finally {
            TraceEvent.end("ContentView.onFocusChanged");
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        this.mContentViewCore.onWindowFocusChanged(hasWindowFocus);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mContentViewCore.onKeyUp(keyCode, event);
    }

    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        return this.mContentViewCore.dispatchKeyEventPreIme(event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (isFocused()) {
            return this.mContentViewCore.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.mContentViewCore.onTouchEvent(event);
    }

    public boolean onHoverEvent(MotionEvent event) {
        boolean consumed = this.mContentViewCore.onHoverEvent(event);
        if (!this.mContentViewCore.isTouchExplorationEnabled()) {
            super.onHoverEvent(event);
        }
        return consumed;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return this.mContentViewCore.onGenericMotionEvent(event);
    }

    public boolean performLongClick() {
        return false;
    }

    protected void onConfigurationChanged(Configuration newConfig) {
        this.mContentViewCore.onConfigurationChanged(newConfig);
    }

    public void scrollBy(int x, int y) {
        this.mContentViewCore.scrollBy((float) x, (float) y);
    }

    public void scrollTo(int x, int y) {
        this.mContentViewCore.scrollTo((float) x, (float) y);
    }

    protected int computeHorizontalScrollExtent() {
        return this.mContentViewCore.computeHorizontalScrollExtent();
    }

    protected int computeHorizontalScrollOffset() {
        return this.mContentViewCore.computeHorizontalScrollOffset();
    }

    protected int computeHorizontalScrollRange() {
        return this.mContentViewCore.computeHorizontalScrollRange();
    }

    protected int computeVerticalScrollExtent() {
        return this.mContentViewCore.computeVerticalScrollExtent();
    }

    protected int computeVerticalScrollOffset() {
        return this.mContentViewCore.computeVerticalScrollOffset();
    }

    protected int computeVerticalScrollRange() {
        return this.mContentViewCore.computeVerticalScrollRange();
    }

    public boolean awakenScrollBars(int startDelay, boolean invalidate) {
        return this.mContentViewCore.awakenScrollBars(startDelay, invalidate);
    }

    public boolean awakenScrollBars() {
        return super.awakenScrollBars();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        this.mContentViewCore.onInitializeAccessibilityNodeInfo(info);
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        this.mContentViewCore.onInitializeAccessibilityEvent(event);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mContentViewCore.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mContentViewCore.onDetachedFromWindow();
    }

    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        this.mContentViewCore.onVisibilityChanged(changedView, visibility);
    }

    public void extractSmartClipData(int x, int y, int width, int height) {
        this.mContentViewCore.extractSmartClipData(x, y, width, height);
    }

    public void setSmartClipResultHandler(Handler resultHandler) {
        if (resultHandler == null) {
            this.mContentViewCore.setSmartClipDataListener(null);
        } else {
            this.mContentViewCore.setSmartClipDataListener(new C05991(resultHandler));
        }
    }

    public boolean super_onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    public boolean super_dispatchKeyEventPreIme(KeyEvent event) {
        return super.dispatchKeyEventPreIme(event);
    }

    public boolean super_dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    public boolean super_onGenericMotionEvent(MotionEvent event) {
        return super.onGenericMotionEvent(event);
    }

    public void super_onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public boolean super_awakenScrollBars(int startDelay, boolean invalidate) {
        return super.awakenScrollBars(startDelay, invalidate);
    }
}
