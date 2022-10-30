package org.xwalk.core;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import java.util.ArrayList;

public class XWalkUIClient {
    static final /* synthetic */ boolean $assertionsDisabled;
    private Object bridge;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod enumConsoleMessageTypeClassValueOfMethod;
    private ReflectMethod enumInitiateByClassValueOfMethod;
    private ReflectMethod enumJavascriptMessageTypeClassValueOfMethod;
    private ReflectMethod enumLoadStatusClassValueOfMethod;
    private ReflectMethod f4xde6ca526;
    private ReflectMethod f5xb5cc0caa;
    private ReflectMethod onFullscreenToggledXWalkViewInternalbooleanMethod;
    private ReflectMethod onIconAvailableXWalkViewInternalStringMessageMethod;
    private ReflectMethod onJavascriptCloseWindowXWalkViewInternalMethod;
    private ReflectMethod f6x125f119f;
    private ReflectMethod onPageLoadStartedXWalkViewInternalStringMethod;
    private ReflectMethod onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod;
    private ReflectMethod onReceivedIconXWalkViewInternalStringBitmapMethod;
    private ReflectMethod onReceivedTitleXWalkViewInternalStringMethod;
    private ReflectMethod onRequestFocusXWalkViewInternalMethod;
    private ReflectMethod onScaleChangedXWalkViewInternalfloatfloatMethod;
    private ReflectMethod onUnhandledKeyEventXWalkViewInternalKeyEventMethod;
    private ReflectMethod openFileChooserXWalkViewInternalValueCallbackStringStringMethod;
    private ReflectMethod postWrapperMethod;
    private ReflectMethod shouldOverrideKeyEventXWalkViewInternalKeyEventMethod;

    public enum ConsoleMessageType {
        DEBUG,
        ERROR,
        LOG,
        INFO,
        WARNING
    }

    public enum InitiateBy {
        BY_USER_GESTURE,
        BY_JAVASCRIPT
    }

    public enum JavascriptMessageType {
        JAVASCRIPT_ALERT,
        JAVASCRIPT_CONFIRM,
        JAVASCRIPT_PROMPT,
        JAVASCRIPT_BEFOREUNLOAD
    }

    public enum LoadStatus {
        FINISHED,
        FAILED,
        CANCELLED
    }

    static {
        $assertionsDisabled = !XWalkUIClient.class.desiredAssertionStatus();
    }

    private Object ConvertJavascriptMessageType(JavascriptMessageType type) {
        return this.enumJavascriptMessageTypeClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertConsoleMessageType(ConsoleMessageType type) {
        return this.enumConsoleMessageTypeClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertInitiateBy(InitiateBy type) {
        return this.enumInitiateByClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertLoadStatus(LoadStatus type) {
        return this.enumLoadStatusClassValueOfMethod.invoke(type.toString());
    }

    Object getBridge() {
        return this.bridge;
    }

    public XWalkUIClient(XWalkView view) {
        this.enumJavascriptMessageTypeClassValueOfMethod = new ReflectMethod();
        this.enumConsoleMessageTypeClassValueOfMethod = new ReflectMethod();
        this.enumInitiateByClassValueOfMethod = new ReflectMethod();
        this.enumLoadStatusClassValueOfMethod = new ReflectMethod();
        this.f5xb5cc0caa = new ReflectMethod(null, "onCreateWindowRequested", new Class[0]);
        this.onIconAvailableXWalkViewInternalStringMessageMethod = new ReflectMethod(null, "onIconAvailable", new Class[0]);
        this.onReceivedIconXWalkViewInternalStringBitmapMethod = new ReflectMethod(null, "onReceivedIcon", new Class[0]);
        this.onRequestFocusXWalkViewInternalMethod = new ReflectMethod(null, "onRequestFocus", new Class[0]);
        this.onJavascriptCloseWindowXWalkViewInternalMethod = new ReflectMethod(null, "onJavascriptCloseWindow", new Class[0]);
        this.f6x125f119f = new ReflectMethod(null, "onJavascriptModalDialog", new Class[0]);
        this.onFullscreenToggledXWalkViewInternalbooleanMethod = new ReflectMethod(null, "onFullscreenToggled", new Class[0]);
        this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod = new ReflectMethod(null, "openFileChooser", new Class[0]);
        this.onScaleChangedXWalkViewInternalfloatfloatMethod = new ReflectMethod(null, "onScaleChanged", new Class[0]);
        this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod = new ReflectMethod(null, "shouldOverrideKeyEvent", new Class[0]);
        this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod = new ReflectMethod(null, "onUnhandledKeyEvent", new Class[0]);
        this.f4xde6ca526 = new ReflectMethod(null, "onConsoleMessage", new Class[0]);
        this.onReceivedTitleXWalkViewInternalStringMethod = new ReflectMethod(null, "onReceivedTitle", new Class[0]);
        this.onPageLoadStartedXWalkViewInternalStringMethod = new ReflectMethod(null, "onPageLoadStarted", new Class[0]);
        this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod = new ReflectMethod(null, "onPageLoadStopped", new Class[0]);
        this.constructorTypes = new ArrayList();
        this.constructorTypes.add("XWalkViewBridge");
        this.constructorParams = new ArrayList();
        this.constructorParams.add(view);
        reflectionInit();
    }

    public boolean onCreateWindowRequested(XWalkView view, InitiateBy initiator, ValueCallback<XWalkView> callback) {
        return ((Boolean) this.f5xb5cc0caa.invoke(view.getBridge(), ConvertInitiateBy(initiator), callback)).booleanValue();
    }

    public void onIconAvailable(XWalkView view, String url, Message startDownload) {
        this.onIconAvailableXWalkViewInternalStringMessageMethod.invoke(view.getBridge(), url, startDownload);
    }

    public void onReceivedIcon(XWalkView view, String url, Bitmap icon) {
        this.onReceivedIconXWalkViewInternalStringBitmapMethod.invoke(view.getBridge(), url, icon);
    }

    public void onRequestFocus(XWalkView view) {
        this.onRequestFocusXWalkViewInternalMethod.invoke(view.getBridge());
    }

    public void onJavascriptCloseWindow(XWalkView view) {
        this.onJavascriptCloseWindowXWalkViewInternalMethod.invoke(view.getBridge());
    }

    public boolean onJavascriptModalDialog(XWalkView view, JavascriptMessageType type, String url, String message, String defaultValue, XWalkJavascriptResult result) {
        return ((Boolean) this.f6x125f119f.invoke(view.getBridge(), ConvertJavascriptMessageType(type), url, message, defaultValue, ((XWalkJavascriptResultHandler) result).getBridge())).booleanValue();
    }

    public void onFullscreenToggled(XWalkView view, boolean enterFullscreen) {
        this.onFullscreenToggledXWalkViewInternalbooleanMethod.invoke(view.getBridge(), Boolean.valueOf(enterFullscreen));
    }

    public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod.invoke(view.getBridge(), uploadFile, acceptType, capture);
    }

    public void onScaleChanged(XWalkView view, float oldScale, float newScale) {
        this.onScaleChangedXWalkViewInternalfloatfloatMethod.invoke(view.getBridge(), Float.valueOf(oldScale), Float.valueOf(newScale));
    }

    public boolean shouldOverrideKeyEvent(XWalkView view, KeyEvent event) {
        return ((Boolean) this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod.invoke(view.getBridge(), event)).booleanValue();
    }

    public void onUnhandledKeyEvent(XWalkView view, KeyEvent event) {
        this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod.invoke(view.getBridge(), event);
    }

    public boolean onConsoleMessage(XWalkView view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
        return ((Boolean) this.f4xde6ca526.invoke(view.getBridge(), message, Integer.valueOf(lineNumber), sourceId, ConvertConsoleMessageType(messageType))).booleanValue();
    }

    public void onReceivedTitle(XWalkView view, String title) {
        this.onReceivedTitleXWalkViewInternalStringMethod.invoke(view.getBridge(), title);
    }

    public void onPageLoadStarted(XWalkView view, String url) {
        this.onPageLoadStartedXWalkViewInternalStringMethod.invoke(view.getBridge(), url);
    }

    public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
        this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod.invoke(view.getBridge(), url, ConvertLoadStatus(status));
    }

    void reflectionInit() {
        XWalkCoreWrapper.initEmbeddedMode();
        this.coreWrapper = XWalkCoreWrapper.getInstance();
        if (this.coreWrapper == null) {
            XWalkCoreWrapper.reserveReflectObject(this);
            return;
        }
        int length = this.constructorTypes.size();
        Class<?>[] paramTypes = new Class[(length + 1)];
        for (int i = 0; i < length; i++) {
            Object type = this.constructorTypes.get(i);
            if (type instanceof String) {
                paramTypes[i] = this.coreWrapper.getBridgeClass((String) type);
                this.constructorParams.set(i, this.coreWrapper.getBridgeObject(this.constructorParams.get(i)));
            } else if (type instanceof Class) {
                paramTypes[i] = (Class) type;
            } else if (!$assertionsDisabled) {
                throw new AssertionError();
            }
        }
        paramTypes[length] = Object.class;
        this.constructorParams.add(this);
        this.bridge = new ReflectConstructor(this.coreWrapper.getBridgeClass("XWalkUIClientBridge"), paramTypes).newInstance(this.constructorParams.toArray());
        if (this.postWrapperMethod != null) {
            this.postWrapperMethod.invoke(new Object[0]);
        }
        this.enumJavascriptMessageTypeClassValueOfMethod.init(null, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$JavascriptMessageTypeInternal"), "valueOf", String.class);
        this.enumConsoleMessageTypeClassValueOfMethod.init(null, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$ConsoleMessageType"), "valueOf", String.class);
        this.enumInitiateByClassValueOfMethod.init(null, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$InitiateByInternal"), "valueOf", String.class);
        this.enumLoadStatusClassValueOfMethod.init(null, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$LoadStatusInternal"), "valueOf", String.class);
        this.f5xb5cc0caa.init(this.bridge, null, "onCreateWindowRequestedSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), this.coreWrapper.getBridgeClass("XWalkUIClientInternal$InitiateByInternal"), ValueCallback.class);
        this.onIconAvailableXWalkViewInternalStringMessageMethod.init(this.bridge, null, "onIconAvailableSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, Message.class);
        this.onReceivedIconXWalkViewInternalStringBitmapMethod.init(this.bridge, null, "onReceivedIconSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, Bitmap.class);
        this.onRequestFocusXWalkViewInternalMethod.init(this.bridge, null, "onRequestFocusSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"));
        this.onJavascriptCloseWindowXWalkViewInternalMethod.init(this.bridge, null, "onJavascriptCloseWindowSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"));
        this.f6x125f119f.init(this.bridge, null, "onJavascriptModalDialogSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), this.coreWrapper.getBridgeClass("XWalkUIClientInternal$JavascriptMessageTypeInternal"), String.class, String.class, String.class, this.coreWrapper.getBridgeClass("XWalkJavascriptResultHandlerBridge"));
        this.onFullscreenToggledXWalkViewInternalbooleanMethod.init(this.bridge, null, "onFullscreenToggledSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), Boolean.TYPE);
        this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod.init(this.bridge, null, "openFileChooserSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), ValueCallback.class, String.class, String.class);
        this.onScaleChangedXWalkViewInternalfloatfloatMethod.init(this.bridge, null, "onScaleChangedSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), Float.TYPE, Float.TYPE);
        this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod.init(this.bridge, null, "shouldOverrideKeyEventSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), KeyEvent.class);
        this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod.init(this.bridge, null, "onUnhandledKeyEventSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), KeyEvent.class);
        this.f4xde6ca526.init(this.bridge, null, "onConsoleMessageSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, Integer.TYPE, String.class, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$ConsoleMessageType"));
        this.onReceivedTitleXWalkViewInternalStringMethod.init(this.bridge, null, "onReceivedTitleSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class);
        this.onPageLoadStartedXWalkViewInternalStringMethod.init(this.bridge, null, "onPageLoadStartedSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class);
        this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod.init(this.bridge, null, "onPageLoadStoppedSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$LoadStatusInternal"));
    }
}
