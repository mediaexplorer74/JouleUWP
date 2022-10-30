package org.xwalk.core.internal;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import org.xwalk.core.internal.XWalkUIClientInternal.ConsoleMessageType;
import org.xwalk.core.internal.XWalkUIClientInternal.InitiateByInternal;
import org.xwalk.core.internal.XWalkUIClientInternal.JavascriptMessageTypeInternal;
import org.xwalk.core.internal.XWalkUIClientInternal.LoadStatusInternal;

public class XWalkUIClientBridge extends XWalkUIClientInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod enumConsoleMessageTypeClassValueOfMethod;
    private ReflectMethod enumInitiateByClassValueOfMethod;
    private ReflectMethod enumJavascriptMessageTypeClassValueOfMethod;
    private ReflectMethod enumLoadStatusClassValueOfMethod;
    private ReflectMethod f24xde6ca526;
    private ReflectMethod f25xb5cc0caa;
    private ReflectMethod onFullscreenToggledXWalkViewInternalbooleanMethod;
    private ReflectMethod onIconAvailableXWalkViewInternalStringMessageMethod;
    private ReflectMethod onJavascriptCloseWindowXWalkViewInternalMethod;
    private ReflectMethod f26x125f119f;
    private ReflectMethod onPageLoadStartedXWalkViewInternalStringMethod;
    private ReflectMethod onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod;
    private ReflectMethod onReceivedIconXWalkViewInternalStringBitmapMethod;
    private ReflectMethod onReceivedTitleXWalkViewInternalStringMethod;
    private ReflectMethod onRequestFocusXWalkViewInternalMethod;
    private ReflectMethod onScaleChangedXWalkViewInternalfloatfloatMethod;
    private ReflectMethod onUnhandledKeyEventXWalkViewInternalKeyEventMethod;
    private ReflectMethod openFileChooserXWalkViewInternalValueCallbackStringStringMethod;
    private ReflectMethod shouldOverrideKeyEventXWalkViewInternalKeyEventMethod;
    private Object wrapper;

    /* renamed from: org.xwalk.core.internal.XWalkUIClientBridge.1 */
    class C04741 implements ValueCallback<Object> {
        final /* synthetic */ ValueCallback val$callbackFinal;

        C04741(ValueCallback valueCallback) {
            this.val$callbackFinal = valueCallback;
        }

        public void onReceiveValue(Object value) {
            this.val$callbackFinal.onReceiveValue((XWalkViewBridge) XWalkUIClientBridge.this.coreBridge.getBridgeObject(value));
        }
    }

    public Object getWrapper() {
        return this.wrapper;
    }

    private Object ConvertJavascriptMessageTypeInternal(JavascriptMessageTypeInternal type) {
        return this.enumJavascriptMessageTypeClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertConsoleMessageType(ConsoleMessageType type) {
        return this.enumConsoleMessageTypeClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertInitiateByInternal(InitiateByInternal type) {
        return this.enumInitiateByClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertLoadStatusInternal(LoadStatusInternal type) {
        return this.enumLoadStatusClassValueOfMethod.invoke(type.toString());
    }

    public XWalkUIClientBridge(XWalkViewBridge view, Object wrapper) {
        super(view);
        this.enumJavascriptMessageTypeClassValueOfMethod = new ReflectMethod();
        this.enumConsoleMessageTypeClassValueOfMethod = new ReflectMethod();
        this.enumInitiateByClassValueOfMethod = new ReflectMethod();
        this.enumLoadStatusClassValueOfMethod = new ReflectMethod();
        this.f25xb5cc0caa = new ReflectMethod(null, "onCreateWindowRequested", new Class[0]);
        this.onIconAvailableXWalkViewInternalStringMessageMethod = new ReflectMethod(null, "onIconAvailable", new Class[0]);
        this.onReceivedIconXWalkViewInternalStringBitmapMethod = new ReflectMethod(null, "onReceivedIcon", new Class[0]);
        this.onRequestFocusXWalkViewInternalMethod = new ReflectMethod(null, "onRequestFocus", new Class[0]);
        this.onJavascriptCloseWindowXWalkViewInternalMethod = new ReflectMethod(null, "onJavascriptCloseWindow", new Class[0]);
        this.f26x125f119f = new ReflectMethod(null, "onJavascriptModalDialog", new Class[0]);
        this.onFullscreenToggledXWalkViewInternalbooleanMethod = new ReflectMethod(null, "onFullscreenToggled", new Class[0]);
        this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod = new ReflectMethod(null, "openFileChooser", new Class[0]);
        this.onScaleChangedXWalkViewInternalfloatfloatMethod = new ReflectMethod(null, "onScaleChanged", new Class[0]);
        this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod = new ReflectMethod(null, "shouldOverrideKeyEvent", new Class[0]);
        this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod = new ReflectMethod(null, "onUnhandledKeyEvent", new Class[0]);
        this.f24xde6ca526 = new ReflectMethod(null, "onConsoleMessage", new Class[0]);
        this.onReceivedTitleXWalkViewInternalStringMethod = new ReflectMethod(null, "onReceivedTitle", new Class[0]);
        this.onPageLoadStartedXWalkViewInternalStringMethod = new ReflectMethod(null, "onPageLoadStarted", new Class[0]);
        this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod = new ReflectMethod(null, "onPageLoadStopped", new Class[0]);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public boolean onCreateWindowRequested(XWalkViewInternal view, InitiateByInternal initiator, ValueCallback<XWalkViewInternal> callback) {
        if (view instanceof XWalkViewBridge) {
            return onCreateWindowRequested((XWalkViewBridge) view, initiator, (ValueCallback) callback);
        }
        return super.onCreateWindowRequested(view, initiator, callback);
    }

    public boolean onCreateWindowRequested(XWalkViewBridge view, InitiateByInternal initiator, ValueCallback<XWalkViewInternal> callback) {
        if (this.f25xb5cc0caa.isNull()) {
            return onCreateWindowRequestedSuper(view, initiator, callback);
        }
        ValueCallback<XWalkViewInternal> callbackFinal = callback;
        return ((Boolean) this.f25xb5cc0caa.invoke(view.getWrapper(), ConvertInitiateByInternal(initiator), new C04741(callbackFinal))).booleanValue();
    }

    public boolean onCreateWindowRequestedSuper(XWalkViewBridge view, InitiateByInternal initiator, ValueCallback<XWalkViewInternal> callback) {
        return super.onCreateWindowRequested(view, initiator, callback);
    }

    public void onIconAvailable(XWalkViewInternal view, String url, Message startDownload) {
        if (view instanceof XWalkViewBridge) {
            onIconAvailable((XWalkViewBridge) view, url, startDownload);
        } else {
            super.onIconAvailable(view, url, startDownload);
        }
    }

    public void onIconAvailable(XWalkViewBridge view, String url, Message startDownload) {
        if (this.onIconAvailableXWalkViewInternalStringMessageMethod.isNull()) {
            onIconAvailableSuper(view, url, startDownload);
            return;
        }
        this.onIconAvailableXWalkViewInternalStringMessageMethod.invoke(view.getWrapper(), url, startDownload);
    }

    public void onIconAvailableSuper(XWalkViewBridge view, String url, Message startDownload) {
        super.onIconAvailable(view, url, startDownload);
    }

    public void onReceivedIcon(XWalkViewInternal view, String url, Bitmap icon) {
        if (view instanceof XWalkViewBridge) {
            onReceivedIcon((XWalkViewBridge) view, url, icon);
        } else {
            super.onReceivedIcon(view, url, icon);
        }
    }

    public void onReceivedIcon(XWalkViewBridge view, String url, Bitmap icon) {
        if (this.onReceivedIconXWalkViewInternalStringBitmapMethod.isNull()) {
            onReceivedIconSuper(view, url, icon);
            return;
        }
        this.onReceivedIconXWalkViewInternalStringBitmapMethod.invoke(view.getWrapper(), url, icon);
    }

    public void onReceivedIconSuper(XWalkViewBridge view, String url, Bitmap icon) {
        super.onReceivedIcon(view, url, icon);
    }

    public void onRequestFocus(XWalkViewInternal view) {
        if (view instanceof XWalkViewBridge) {
            onRequestFocus((XWalkViewBridge) view);
        } else {
            super.onRequestFocus(view);
        }
    }

    public void onRequestFocus(XWalkViewBridge view) {
        if (this.onRequestFocusXWalkViewInternalMethod.isNull()) {
            onRequestFocusSuper(view);
            return;
        }
        this.onRequestFocusXWalkViewInternalMethod.invoke(view.getWrapper());
    }

    public void onRequestFocusSuper(XWalkViewBridge view) {
        super.onRequestFocus(view);
    }

    public void onJavascriptCloseWindow(XWalkViewInternal view) {
        if (view instanceof XWalkViewBridge) {
            onJavascriptCloseWindow((XWalkViewBridge) view);
        } else {
            super.onJavascriptCloseWindow(view);
        }
    }

    public void onJavascriptCloseWindow(XWalkViewBridge view) {
        if (this.onJavascriptCloseWindowXWalkViewInternalMethod.isNull()) {
            onJavascriptCloseWindowSuper(view);
            return;
        }
        this.onJavascriptCloseWindowXWalkViewInternalMethod.invoke(view.getWrapper());
    }

    public void onJavascriptCloseWindowSuper(XWalkViewBridge view) {
        super.onJavascriptCloseWindow(view);
    }

    public boolean onJavascriptModalDialog(XWalkViewInternal view, JavascriptMessageTypeInternal type, String url, String message, String defaultValue, XWalkJavascriptResultInternal result) {
        if (!(view instanceof XWalkViewBridge)) {
            return super.onJavascriptModalDialog(view, type, url, message, defaultValue, result);
        }
        return onJavascriptModalDialog((XWalkViewBridge) view, type, url, message, defaultValue, result instanceof XWalkJavascriptResultHandlerBridge ? (XWalkJavascriptResultHandlerBridge) result : new XWalkJavascriptResultHandlerBridge((XWalkJavascriptResultHandlerInternal) result));
    }

    public boolean onJavascriptModalDialog(XWalkViewBridge view, JavascriptMessageTypeInternal type, String url, String message, String defaultValue, XWalkJavascriptResultHandlerBridge result) {
        if (this.f26x125f119f.isNull()) {
            return onJavascriptModalDialogSuper(view, type, url, message, defaultValue, result);
        }
        ReflectMethod reflectMethod = this.f26x125f119f;
        Object[] objArr = new Object[6];
        objArr[0] = view.getWrapper();
        objArr[1] = ConvertJavascriptMessageTypeInternal(type);
        objArr[2] = url;
        objArr[3] = message;
        objArr[4] = defaultValue;
        if (!(result instanceof XWalkJavascriptResultHandlerBridge)) {
            result = new XWalkJavascriptResultHandlerBridge(result);
        }
        objArr[5] = result.getWrapper();
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public boolean onJavascriptModalDialogSuper(XWalkViewBridge view, JavascriptMessageTypeInternal type, String url, String message, String defaultValue, XWalkJavascriptResultHandlerBridge result) {
        return super.onJavascriptModalDialog(view, type, url, message, defaultValue, result);
    }

    public void onFullscreenToggled(XWalkViewInternal view, boolean enterFullscreen) {
        if (view instanceof XWalkViewBridge) {
            onFullscreenToggled((XWalkViewBridge) view, enterFullscreen);
        } else {
            super.onFullscreenToggled(view, enterFullscreen);
        }
    }

    public void onFullscreenToggled(XWalkViewBridge view, boolean enterFullscreen) {
        if (this.onFullscreenToggledXWalkViewInternalbooleanMethod.isNull()) {
            onFullscreenToggledSuper(view, enterFullscreen);
            return;
        }
        this.onFullscreenToggledXWalkViewInternalbooleanMethod.invoke(view.getWrapper(), Boolean.valueOf(enterFullscreen));
    }

    public void onFullscreenToggledSuper(XWalkViewBridge view, boolean enterFullscreen) {
        super.onFullscreenToggled(view, enterFullscreen);
    }

    public void openFileChooser(XWalkViewInternal view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        if (view instanceof XWalkViewBridge) {
            openFileChooser((XWalkViewBridge) view, (ValueCallback) uploadFile, acceptType, capture);
        } else {
            super.openFileChooser(view, uploadFile, acceptType, capture);
        }
    }

    public void openFileChooser(XWalkViewBridge view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        if (this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod.isNull()) {
            openFileChooserSuper(view, uploadFile, acceptType, capture);
            return;
        }
        this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod.invoke(view.getWrapper(), uploadFile, acceptType, capture);
    }

    public void openFileChooserSuper(XWalkViewBridge view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        super.openFileChooser(view, uploadFile, acceptType, capture);
    }

    public void onScaleChanged(XWalkViewInternal view, float oldScale, float newScale) {
        if (view instanceof XWalkViewBridge) {
            onScaleChanged((XWalkViewBridge) view, oldScale, newScale);
        } else {
            super.onScaleChanged(view, oldScale, newScale);
        }
    }

    public void onScaleChanged(XWalkViewBridge view, float oldScale, float newScale) {
        if (this.onScaleChangedXWalkViewInternalfloatfloatMethod.isNull()) {
            onScaleChangedSuper(view, oldScale, newScale);
            return;
        }
        this.onScaleChangedXWalkViewInternalfloatfloatMethod.invoke(view.getWrapper(), Float.valueOf(oldScale), Float.valueOf(newScale));
    }

    public void onScaleChangedSuper(XWalkViewBridge view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
    }

    public boolean shouldOverrideKeyEvent(XWalkViewInternal view, KeyEvent event) {
        if (view instanceof XWalkViewBridge) {
            return shouldOverrideKeyEvent((XWalkViewBridge) view, event);
        }
        return super.shouldOverrideKeyEvent(view, event);
    }

    public boolean shouldOverrideKeyEvent(XWalkViewBridge view, KeyEvent event) {
        if (this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod.isNull()) {
            return shouldOverrideKeyEventSuper(view, event);
        }
        return ((Boolean) this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod.invoke(view.getWrapper(), event)).booleanValue();
    }

    public boolean shouldOverrideKeyEventSuper(XWalkViewBridge view, KeyEvent event) {
        return super.shouldOverrideKeyEvent(view, event);
    }

    public void onUnhandledKeyEvent(XWalkViewInternal view, KeyEvent event) {
        if (view instanceof XWalkViewBridge) {
            onUnhandledKeyEvent((XWalkViewBridge) view, event);
        } else {
            super.onUnhandledKeyEvent(view, event);
        }
    }

    public void onUnhandledKeyEvent(XWalkViewBridge view, KeyEvent event) {
        if (this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod.isNull()) {
            onUnhandledKeyEventSuper(view, event);
            return;
        }
        this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod.invoke(view.getWrapper(), event);
    }

    public void onUnhandledKeyEventSuper(XWalkViewBridge view, KeyEvent event) {
        super.onUnhandledKeyEvent(view, event);
    }

    public boolean onConsoleMessage(XWalkViewInternal view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
        if (view instanceof XWalkViewBridge) {
            return onConsoleMessage((XWalkViewBridge) view, message, lineNumber, sourceId, messageType);
        }
        return super.onConsoleMessage(view, message, lineNumber, sourceId, messageType);
    }

    public boolean onConsoleMessage(XWalkViewBridge view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
        if (this.f24xde6ca526.isNull()) {
            return onConsoleMessageSuper(view, message, lineNumber, sourceId, messageType);
        }
        return ((Boolean) this.f24xde6ca526.invoke(view.getWrapper(), message, Integer.valueOf(lineNumber), sourceId, ConvertConsoleMessageType(messageType))).booleanValue();
    }

    public boolean onConsoleMessageSuper(XWalkViewBridge view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
        return super.onConsoleMessage(view, message, lineNumber, sourceId, messageType);
    }

    public void onReceivedTitle(XWalkViewInternal view, String title) {
        if (view instanceof XWalkViewBridge) {
            onReceivedTitle((XWalkViewBridge) view, title);
        } else {
            super.onReceivedTitle(view, title);
        }
    }

    public void onReceivedTitle(XWalkViewBridge view, String title) {
        if (this.onReceivedTitleXWalkViewInternalStringMethod.isNull()) {
            onReceivedTitleSuper(view, title);
            return;
        }
        this.onReceivedTitleXWalkViewInternalStringMethod.invoke(view.getWrapper(), title);
    }

    public void onReceivedTitleSuper(XWalkViewBridge view, String title) {
        super.onReceivedTitle(view, title);
    }

    public void onPageLoadStarted(XWalkViewInternal view, String url) {
        if (view instanceof XWalkViewBridge) {
            onPageLoadStarted((XWalkViewBridge) view, url);
        } else {
            super.onPageLoadStarted(view, url);
        }
    }

    public void onPageLoadStarted(XWalkViewBridge view, String url) {
        if (this.onPageLoadStartedXWalkViewInternalStringMethod.isNull()) {
            onPageLoadStartedSuper(view, url);
            return;
        }
        this.onPageLoadStartedXWalkViewInternalStringMethod.invoke(view.getWrapper(), url);
    }

    public void onPageLoadStartedSuper(XWalkViewBridge view, String url) {
        super.onPageLoadStarted(view, url);
    }

    public void onPageLoadStopped(XWalkViewInternal view, String url, LoadStatusInternal status) {
        if (view instanceof XWalkViewBridge) {
            onPageLoadStopped((XWalkViewBridge) view, url, status);
        } else {
            super.onPageLoadStopped(view, url, status);
        }
    }

    public void onPageLoadStopped(XWalkViewBridge view, String url, LoadStatusInternal status) {
        if (this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod.isNull()) {
            onPageLoadStoppedSuper(view, url, status);
            return;
        }
        this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod.invoke(view.getWrapper(), url, ConvertLoadStatusInternal(status));
    }

    public void onPageLoadStoppedSuper(XWalkViewBridge view, String url, LoadStatusInternal status) {
        super.onPageLoadStopped(view, url, status);
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.enumJavascriptMessageTypeClassValueOfMethod.init(null, this.coreBridge.getWrapperClass("XWalkUIClient$JavascriptMessageType"), "valueOf", String.class);
            this.enumConsoleMessageTypeClassValueOfMethod.init(null, this.coreBridge.getWrapperClass("XWalkUIClient$ConsoleMessageType"), "valueOf", String.class);
            this.enumInitiateByClassValueOfMethod.init(null, this.coreBridge.getWrapperClass("XWalkUIClient$InitiateBy"), "valueOf", String.class);
            this.enumLoadStatusClassValueOfMethod.init(null, this.coreBridge.getWrapperClass("XWalkUIClient$LoadStatus"), "valueOf", String.class);
            this.f25xb5cc0caa.init(this.wrapper, null, "onCreateWindowRequested", this.coreBridge.getWrapperClass("XWalkView"), this.coreBridge.getWrapperClass("XWalkUIClient$InitiateBy"), ValueCallback.class);
            this.onIconAvailableXWalkViewInternalStringMessageMethod.init(this.wrapper, null, "onIconAvailable", this.coreBridge.getWrapperClass("XWalkView"), String.class, Message.class);
            this.onReceivedIconXWalkViewInternalStringBitmapMethod.init(this.wrapper, null, "onReceivedIcon", this.coreBridge.getWrapperClass("XWalkView"), String.class, Bitmap.class);
            this.onRequestFocusXWalkViewInternalMethod.init(this.wrapper, null, "onRequestFocus", this.coreBridge.getWrapperClass("XWalkView"));
            this.onJavascriptCloseWindowXWalkViewInternalMethod.init(this.wrapper, null, "onJavascriptCloseWindow", this.coreBridge.getWrapperClass("XWalkView"));
            this.f26x125f119f.init(this.wrapper, null, "onJavascriptModalDialog", this.coreBridge.getWrapperClass("XWalkView"), this.coreBridge.getWrapperClass("XWalkUIClient$JavascriptMessageType"), String.class, String.class, String.class, this.coreBridge.getWrapperClass("XWalkJavascriptResult"));
            this.onFullscreenToggledXWalkViewInternalbooleanMethod.init(this.wrapper, null, "onFullscreenToggled", this.coreBridge.getWrapperClass("XWalkView"), Boolean.TYPE);
            this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod.init(this.wrapper, null, "openFileChooser", this.coreBridge.getWrapperClass("XWalkView"), ValueCallback.class, String.class, String.class);
            this.onScaleChangedXWalkViewInternalfloatfloatMethod.init(this.wrapper, null, "onScaleChanged", this.coreBridge.getWrapperClass("XWalkView"), Float.TYPE, Float.TYPE);
            this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod.init(this.wrapper, null, "shouldOverrideKeyEvent", this.coreBridge.getWrapperClass("XWalkView"), KeyEvent.class);
            this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod.init(this.wrapper, null, "onUnhandledKeyEvent", this.coreBridge.getWrapperClass("XWalkView"), KeyEvent.class);
            this.f24xde6ca526.init(this.wrapper, null, "onConsoleMessage", this.coreBridge.getWrapperClass("XWalkView"), String.class, Integer.TYPE, String.class, this.coreBridge.getWrapperClass("XWalkUIClient$ConsoleMessageType"));
            this.onReceivedTitleXWalkViewInternalStringMethod.init(this.wrapper, null, "onReceivedTitle", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.onPageLoadStartedXWalkViewInternalStringMethod.init(this.wrapper, null, "onPageLoadStarted", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod.init(this.wrapper, null, "onPageLoadStopped", this.coreBridge.getWrapperClass("XWalkView"), String.class, this.coreBridge.getWrapperClass("XWalkUIClient$LoadStatus"));
        }
    }
}
