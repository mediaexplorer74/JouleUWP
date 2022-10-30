package org.xwalk.core;

import android.net.http.SslError;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import java.util.ArrayList;

public class XWalkResourceClient {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int ERROR_AUTHENTICATION = -4;
    public static final int ERROR_BAD_URL = -12;
    public static final int ERROR_CONNECT = -6;
    public static final int ERROR_FAILED_SSL_HANDSHAKE = -11;
    public static final int ERROR_FILE = -13;
    public static final int ERROR_FILE_NOT_FOUND = -14;
    public static final int ERROR_HOST_LOOKUP = -2;
    public static final int ERROR_IO = -7;
    public static final int ERROR_OK = 0;
    public static final int ERROR_PROXY_AUTHENTICATION = -5;
    public static final int ERROR_REDIRECT_LOOP = -9;
    public static final int ERROR_TIMEOUT = -8;
    public static final int ERROR_TOO_MANY_REQUESTS = -15;
    public static final int ERROR_UNKNOWN = -1;
    public static final int ERROR_UNSUPPORTED_AUTH_SCHEME = -3;
    public static final int ERROR_UNSUPPORTED_SCHEME = -10;
    private Object bridge;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod;
    private ReflectMethod onDocumentLoadedInFrameXWalkViewInternallongMethod;
    private ReflectMethod onLoadFinishedXWalkViewInternalStringMethod;
    private ReflectMethod onLoadStartedXWalkViewInternalStringMethod;
    private ReflectMethod onProgressChangedXWalkViewInternalintMethod;
    private ReflectMethod f2x4c27f13b;
    private ReflectMethod f3xafae7cfd;
    private ReflectMethod onReceivedLoadErrorXWalkViewInternalintStringStringMethod;
    private ReflectMethod onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod;
    private ReflectMethod postWrapperMethod;
    private ReflectMethod shouldInterceptLoadRequestXWalkViewInternalStringMethod;
    private ReflectMethod shouldOverrideUrlLoadingXWalkViewInternalStringMethod;

    static {
        $assertionsDisabled = !XWalkResourceClient.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    Object getBridge() {
        return this.bridge;
    }

    public XWalkResourceClient(XWalkView view) {
        this.onDocumentLoadedInFrameXWalkViewInternallongMethod = new ReflectMethod(null, "onDocumentLoadedInFrame", new Class[ERROR_OK]);
        this.onLoadStartedXWalkViewInternalStringMethod = new ReflectMethod(null, "onLoadStarted", new Class[ERROR_OK]);
        this.onLoadFinishedXWalkViewInternalStringMethod = new ReflectMethod(null, "onLoadFinished", new Class[ERROR_OK]);
        this.onProgressChangedXWalkViewInternalintMethod = new ReflectMethod(null, "onProgressChanged", new Class[ERROR_OK]);
        this.shouldInterceptLoadRequestXWalkViewInternalStringMethod = new ReflectMethod(null, "shouldInterceptLoadRequest", new Class[ERROR_OK]);
        this.onReceivedLoadErrorXWalkViewInternalintStringStringMethod = new ReflectMethod(null, "onReceivedLoadError", new Class[ERROR_OK]);
        this.shouldOverrideUrlLoadingXWalkViewInternalStringMethod = new ReflectMethod(null, "shouldOverrideUrlLoading", new Class[ERROR_OK]);
        this.onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod = new ReflectMethod(null, "onReceivedSslError", new Class[ERROR_OK]);
        this.f2x4c27f13b = new ReflectMethod(null, "onReceivedClientCertRequest", new Class[ERROR_OK]);
        this.doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod = new ReflectMethod(null, "doUpdateVisitedHistory", new Class[ERROR_OK]);
        this.f3xafae7cfd = new ReflectMethod(null, "onReceivedHttpAuthRequest", new Class[ERROR_OK]);
        this.constructorTypes = new ArrayList();
        this.constructorTypes.add("XWalkViewBridge");
        this.constructorParams = new ArrayList();
        this.constructorParams.add(view);
        reflectionInit();
    }

    public void onDocumentLoadedInFrame(XWalkView view, long frameId) {
        this.onDocumentLoadedInFrameXWalkViewInternallongMethod.invoke(view.getBridge(), Long.valueOf(frameId));
    }

    public void onLoadStarted(XWalkView view, String url) {
        this.onLoadStartedXWalkViewInternalStringMethod.invoke(view.getBridge(), url);
    }

    public void onLoadFinished(XWalkView view, String url) {
        this.onLoadFinishedXWalkViewInternalStringMethod.invoke(view.getBridge(), url);
    }

    public void onProgressChanged(XWalkView view, int progressInPercent) {
        this.onProgressChangedXWalkViewInternalintMethod.invoke(view.getBridge(), Integer.valueOf(progressInPercent));
    }

    public WebResourceResponse shouldInterceptLoadRequest(XWalkView view, String url) {
        return (WebResourceResponse) this.shouldInterceptLoadRequestXWalkViewInternalStringMethod.invoke(view.getBridge(), url);
    }

    public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
        this.onReceivedLoadErrorXWalkViewInternalintStringStringMethod.invoke(view.getBridge(), Integer.valueOf(errorCode), description, failingUrl);
    }

    public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
        return ((Boolean) this.shouldOverrideUrlLoadingXWalkViewInternalStringMethod.invoke(view.getBridge(), url)).booleanValue();
    }

    public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {
        this.onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod.invoke(view.getBridge(), callback, error);
    }

    public void onReceivedClientCertRequest(XWalkView view, ClientCertRequest handler) {
        this.f2x4c27f13b.invoke(view.getBridge(), ((ClientCertRequestHandler) handler).getBridge());
    }

    public void doUpdateVisitedHistory(XWalkView view, String url, boolean isReload) {
        this.doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod.invoke(view.getBridge(), url, Boolean.valueOf(isReload));
    }

    public void onReceivedHttpAuthRequest(XWalkView view, XWalkHttpAuthHandler handler, String host, String realm) {
        this.f3xafae7cfd.invoke(view.getBridge(), handler.getBridge(), host, realm);
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
        for (int i = ERROR_OK; i < length; i++) {
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
        this.bridge = new ReflectConstructor(this.coreWrapper.getBridgeClass("XWalkResourceClientBridge"), paramTypes).newInstance(this.constructorParams.toArray());
        if (this.postWrapperMethod != null) {
            this.postWrapperMethod.invoke(new Object[ERROR_OK]);
        }
        this.onDocumentLoadedInFrameXWalkViewInternallongMethod.init(this.bridge, null, "onDocumentLoadedInFrameSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), Long.TYPE);
        this.onLoadStartedXWalkViewInternalStringMethod.init(this.bridge, null, "onLoadStartedSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class);
        this.onLoadFinishedXWalkViewInternalStringMethod.init(this.bridge, null, "onLoadFinishedSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class);
        this.onProgressChangedXWalkViewInternalintMethod.init(this.bridge, null, "onProgressChangedSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), Integer.TYPE);
        this.shouldInterceptLoadRequestXWalkViewInternalStringMethod.init(this.bridge, null, "shouldInterceptLoadRequestSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class);
        this.onReceivedLoadErrorXWalkViewInternalintStringStringMethod.init(this.bridge, null, "onReceivedLoadErrorSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), Integer.TYPE, String.class, String.class);
        this.shouldOverrideUrlLoadingXWalkViewInternalStringMethod.init(this.bridge, null, "shouldOverrideUrlLoadingSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class);
        this.onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod.init(this.bridge, null, "onReceivedSslErrorSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), ValueCallback.class, SslError.class);
        this.f2x4c27f13b.init(this.bridge, null, "onReceivedClientCertRequestSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), this.coreWrapper.getBridgeClass("ClientCertRequestHandlerBridge"));
        this.doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod.init(this.bridge, null, "doUpdateVisitedHistorySuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, Boolean.TYPE);
        this.f3xafae7cfd.init(this.bridge, null, "onReceivedHttpAuthRequestSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), this.coreWrapper.getBridgeClass("XWalkHttpAuthHandlerBridge"), String.class, String.class);
    }
}
