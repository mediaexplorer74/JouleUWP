package org.xwalk.core.internal;

import android.net.http.SslError;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;

public class XWalkResourceClientBridge extends XWalkResourceClientInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod;
    private ReflectMethod onDocumentLoadedInFrameXWalkViewInternallongMethod;
    private ReflectMethod onLoadFinishedXWalkViewInternalStringMethod;
    private ReflectMethod onLoadStartedXWalkViewInternalStringMethod;
    private ReflectMethod onProgressChangedXWalkViewInternalintMethod;
    private ReflectMethod f22x4c27f13b;
    private ReflectMethod f23xafae7cfd;
    private ReflectMethod onReceivedLoadErrorXWalkViewInternalintStringStringMethod;
    private ReflectMethod onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod;
    private ReflectMethod shouldInterceptLoadRequestXWalkViewInternalStringMethod;
    private ReflectMethod shouldOverrideUrlLoadingXWalkViewInternalStringMethod;
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkResourceClientBridge(XWalkViewBridge view, Object wrapper) {
        super(view);
        this.onDocumentLoadedInFrameXWalkViewInternallongMethod = new ReflectMethod(null, "onDocumentLoadedInFrame", new Class[0]);
        this.onLoadStartedXWalkViewInternalStringMethod = new ReflectMethod(null, "onLoadStarted", new Class[0]);
        this.onLoadFinishedXWalkViewInternalStringMethod = new ReflectMethod(null, "onLoadFinished", new Class[0]);
        this.onProgressChangedXWalkViewInternalintMethod = new ReflectMethod(null, "onProgressChanged", new Class[0]);
        this.shouldInterceptLoadRequestXWalkViewInternalStringMethod = new ReflectMethod(null, "shouldInterceptLoadRequest", new Class[0]);
        this.onReceivedLoadErrorXWalkViewInternalintStringStringMethod = new ReflectMethod(null, "onReceivedLoadError", new Class[0]);
        this.shouldOverrideUrlLoadingXWalkViewInternalStringMethod = new ReflectMethod(null, "shouldOverrideUrlLoading", new Class[0]);
        this.onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod = new ReflectMethod(null, "onReceivedSslError", new Class[0]);
        this.f22x4c27f13b = new ReflectMethod(null, "onReceivedClientCertRequest", new Class[0]);
        this.doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod = new ReflectMethod(null, "doUpdateVisitedHistory", new Class[0]);
        this.f23xafae7cfd = new ReflectMethod(null, "onReceivedHttpAuthRequest", new Class[0]);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public void onDocumentLoadedInFrame(XWalkViewInternal view, long frameId) {
        if (view instanceof XWalkViewBridge) {
            onDocumentLoadedInFrame((XWalkViewBridge) view, frameId);
        } else {
            super.onDocumentLoadedInFrame(view, frameId);
        }
    }

    public void onDocumentLoadedInFrame(XWalkViewBridge view, long frameId) {
        if (this.onDocumentLoadedInFrameXWalkViewInternallongMethod.isNull()) {
            onDocumentLoadedInFrameSuper(view, frameId);
            return;
        }
        this.onDocumentLoadedInFrameXWalkViewInternallongMethod.invoke(view.getWrapper(), Long.valueOf(frameId));
    }

    public void onDocumentLoadedInFrameSuper(XWalkViewBridge view, long frameId) {
        super.onDocumentLoadedInFrame(view, frameId);
    }

    public void onLoadStarted(XWalkViewInternal view, String url) {
        if (view instanceof XWalkViewBridge) {
            onLoadStarted((XWalkViewBridge) view, url);
        } else {
            super.onLoadStarted(view, url);
        }
    }

    public void onLoadStarted(XWalkViewBridge view, String url) {
        if (this.onLoadStartedXWalkViewInternalStringMethod.isNull()) {
            onLoadStartedSuper(view, url);
            return;
        }
        this.onLoadStartedXWalkViewInternalStringMethod.invoke(view.getWrapper(), url);
    }

    public void onLoadStartedSuper(XWalkViewBridge view, String url) {
        super.onLoadStarted(view, url);
    }

    public void onLoadFinished(XWalkViewInternal view, String url) {
        if (view instanceof XWalkViewBridge) {
            onLoadFinished((XWalkViewBridge) view, url);
        } else {
            super.onLoadFinished(view, url);
        }
    }

    public void onLoadFinished(XWalkViewBridge view, String url) {
        if (this.onLoadFinishedXWalkViewInternalStringMethod.isNull()) {
            onLoadFinishedSuper(view, url);
            return;
        }
        this.onLoadFinishedXWalkViewInternalStringMethod.invoke(view.getWrapper(), url);
    }

    public void onLoadFinishedSuper(XWalkViewBridge view, String url) {
        super.onLoadFinished(view, url);
    }

    public void onProgressChanged(XWalkViewInternal view, int progressInPercent) {
        if (view instanceof XWalkViewBridge) {
            onProgressChanged((XWalkViewBridge) view, progressInPercent);
        } else {
            super.onProgressChanged(view, progressInPercent);
        }
    }

    public void onProgressChanged(XWalkViewBridge view, int progressInPercent) {
        if (this.onProgressChangedXWalkViewInternalintMethod.isNull()) {
            onProgressChangedSuper(view, progressInPercent);
            return;
        }
        this.onProgressChangedXWalkViewInternalintMethod.invoke(view.getWrapper(), Integer.valueOf(progressInPercent));
    }

    public void onProgressChangedSuper(XWalkViewBridge view, int progressInPercent) {
        super.onProgressChanged(view, progressInPercent);
    }

    public WebResourceResponse shouldInterceptLoadRequest(XWalkViewInternal view, String url) {
        if (view instanceof XWalkViewBridge) {
            return shouldInterceptLoadRequest((XWalkViewBridge) view, url);
        }
        return super.shouldInterceptLoadRequest(view, url);
    }

    public WebResourceResponse shouldInterceptLoadRequest(XWalkViewBridge view, String url) {
        if (this.shouldInterceptLoadRequestXWalkViewInternalStringMethod.isNull()) {
            return shouldInterceptLoadRequestSuper(view, url);
        }
        return (WebResourceResponse) this.shouldInterceptLoadRequestXWalkViewInternalStringMethod.invoke(view.getWrapper(), url);
    }

    public WebResourceResponse shouldInterceptLoadRequestSuper(XWalkViewBridge view, String url) {
        WebResourceResponse ret = super.shouldInterceptLoadRequest(view, url);
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void onReceivedLoadError(XWalkViewInternal view, int errorCode, String description, String failingUrl) {
        if (view instanceof XWalkViewBridge) {
            onReceivedLoadError((XWalkViewBridge) view, errorCode, description, failingUrl);
        } else {
            super.onReceivedLoadError(view, errorCode, description, failingUrl);
        }
    }

    public void onReceivedLoadError(XWalkViewBridge view, int errorCode, String description, String failingUrl) {
        if (this.onReceivedLoadErrorXWalkViewInternalintStringStringMethod.isNull()) {
            onReceivedLoadErrorSuper(view, errorCode, description, failingUrl);
            return;
        }
        this.onReceivedLoadErrorXWalkViewInternalintStringStringMethod.invoke(view.getWrapper(), Integer.valueOf(errorCode), description, failingUrl);
    }

    public void onReceivedLoadErrorSuper(XWalkViewBridge view, int errorCode, String description, String failingUrl) {
        super.onReceivedLoadError(view, errorCode, description, failingUrl);
    }

    public boolean shouldOverrideUrlLoading(XWalkViewInternal view, String url) {
        if (view instanceof XWalkViewBridge) {
            return shouldOverrideUrlLoading((XWalkViewBridge) view, url);
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    public boolean shouldOverrideUrlLoading(XWalkViewBridge view, String url) {
        if (this.shouldOverrideUrlLoadingXWalkViewInternalStringMethod.isNull()) {
            return shouldOverrideUrlLoadingSuper(view, url);
        }
        return ((Boolean) this.shouldOverrideUrlLoadingXWalkViewInternalStringMethod.invoke(view.getWrapper(), url)).booleanValue();
    }

    public boolean shouldOverrideUrlLoadingSuper(XWalkViewBridge view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }

    public void onReceivedSslError(XWalkViewInternal view, ValueCallback<Boolean> callback, SslError error) {
        if (view instanceof XWalkViewBridge) {
            onReceivedSslError((XWalkViewBridge) view, (ValueCallback) callback, error);
        } else {
            super.onReceivedSslError(view, callback, error);
        }
    }

    public void onReceivedSslError(XWalkViewBridge view, ValueCallback<Boolean> callback, SslError error) {
        if (this.onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod.isNull()) {
            onReceivedSslErrorSuper(view, callback, error);
            return;
        }
        this.onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod.invoke(view.getWrapper(), callback, error);
    }

    public void onReceivedSslErrorSuper(XWalkViewBridge view, ValueCallback<Boolean> callback, SslError error) {
        super.onReceivedSslError(view, callback, error);
    }

    public void onReceivedClientCertRequest(XWalkViewInternal view, ClientCertRequestInternal handler) {
        if (view instanceof XWalkViewBridge) {
            onReceivedClientCertRequest((XWalkViewBridge) view, handler instanceof ClientCertRequestHandlerBridge ? (ClientCertRequestHandlerBridge) handler : new ClientCertRequestHandlerBridge((ClientCertRequestHandlerInternal) handler));
        } else {
            super.onReceivedClientCertRequest(view, handler);
        }
    }

    public void onReceivedClientCertRequest(XWalkViewBridge view, ClientCertRequestHandlerBridge handler) {
        if (this.f22x4c27f13b.isNull()) {
            onReceivedClientCertRequestSuper(view, handler);
            return;
        }
        ReflectMethod reflectMethod = this.f22x4c27f13b;
        Object[] objArr = new Object[2];
        objArr[0] = view.getWrapper();
        if (!(handler instanceof ClientCertRequestHandlerBridge)) {
            handler = new ClientCertRequestHandlerBridge(handler);
        }
        objArr[1] = handler.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void onReceivedClientCertRequestSuper(XWalkViewBridge view, ClientCertRequestHandlerBridge handler) {
        super.onReceivedClientCertRequest(view, handler);
    }

    public void doUpdateVisitedHistory(XWalkViewInternal view, String url, boolean isReload) {
        if (view instanceof XWalkViewBridge) {
            doUpdateVisitedHistory((XWalkViewBridge) view, url, isReload);
        } else {
            super.doUpdateVisitedHistory(view, url, isReload);
        }
    }

    public void doUpdateVisitedHistory(XWalkViewBridge view, String url, boolean isReload) {
        if (this.doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod.isNull()) {
            doUpdateVisitedHistorySuper(view, url, isReload);
            return;
        }
        this.doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod.invoke(view.getWrapper(), url, Boolean.valueOf(isReload));
    }

    public void doUpdateVisitedHistorySuper(XWalkViewBridge view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    public void onReceivedHttpAuthRequest(XWalkViewInternal view, XWalkHttpAuthHandlerInternal handler, String host, String realm) {
        if ((view instanceof XWalkViewBridge) && (handler instanceof XWalkHttpAuthHandlerBridge)) {
            onReceivedHttpAuthRequest((XWalkViewBridge) view, (XWalkHttpAuthHandlerBridge) handler, host, realm);
        } else {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }

    public void onReceivedHttpAuthRequest(XWalkViewBridge view, XWalkHttpAuthHandlerBridge handler, String host, String realm) {
        if (this.f23xafae7cfd.isNull()) {
            onReceivedHttpAuthRequestSuper(view, handler, host, realm);
            return;
        }
        this.f23xafae7cfd.invoke(view.getWrapper(), handler.getWrapper(), host, realm);
    }

    public void onReceivedHttpAuthRequestSuper(XWalkViewBridge view, XWalkHttpAuthHandlerBridge handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.onDocumentLoadedInFrameXWalkViewInternallongMethod.init(this.wrapper, null, "onDocumentLoadedInFrame", this.coreBridge.getWrapperClass("XWalkView"), Long.TYPE);
            this.onLoadStartedXWalkViewInternalStringMethod.init(this.wrapper, null, "onLoadStarted", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.onLoadFinishedXWalkViewInternalStringMethod.init(this.wrapper, null, "onLoadFinished", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.onProgressChangedXWalkViewInternalintMethod.init(this.wrapper, null, "onProgressChanged", this.coreBridge.getWrapperClass("XWalkView"), Integer.TYPE);
            this.shouldInterceptLoadRequestXWalkViewInternalStringMethod.init(this.wrapper, null, "shouldInterceptLoadRequest", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.onReceivedLoadErrorXWalkViewInternalintStringStringMethod.init(this.wrapper, null, "onReceivedLoadError", this.coreBridge.getWrapperClass("XWalkView"), Integer.TYPE, String.class, String.class);
            this.shouldOverrideUrlLoadingXWalkViewInternalStringMethod.init(this.wrapper, null, "shouldOverrideUrlLoading", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod.init(this.wrapper, null, "onReceivedSslError", this.coreBridge.getWrapperClass("XWalkView"), ValueCallback.class, SslError.class);
            this.f22x4c27f13b.init(this.wrapper, null, "onReceivedClientCertRequest", this.coreBridge.getWrapperClass("XWalkView"), this.coreBridge.getWrapperClass("ClientCertRequest"));
            this.doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod.init(this.wrapper, null, "doUpdateVisitedHistory", this.coreBridge.getWrapperClass("XWalkView"), String.class, Boolean.TYPE);
            this.f23xafae7cfd.init(this.wrapper, null, "onReceivedHttpAuthRequest", this.coreBridge.getWrapperClass("XWalkView"), this.coreBridge.getWrapperClass("XWalkHttpAuthHandler"), String.class, String.class);
        }
    }
}
