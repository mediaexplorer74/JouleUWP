package org.xwalk.core.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ValueCallback;

public class XWalkViewBridge extends XWalkViewInternal {
    private ReflectMethod addJavascriptInterfaceObjectStringMethod;
    private ReflectMethod canZoomInMethod;
    private ReflectMethod canZoomOutMethod;
    private ReflectMethod clearCacheForSingleFileStringMethod;
    private ReflectMethod clearCachebooleanMethod;
    private ReflectMethod clearFormDataMethod;
    private XWalkCoreBridge coreBridge;
    private ReflectMethod evaluateJavascriptStringValueCallbackMethod;
    private ReflectMethod getAPIVersionMethod;
    private ReflectMethod getNavigationHistoryMethod;
    private ReflectMethod getOriginalUrlMethod;
    private ReflectMethod getRemoteDebuggingUrlMethod;
    private ReflectMethod getTitleMethod;
    private ReflectMethod getUrlMethod;
    private ReflectMethod getUserAgentStringMethod;
    private ReflectMethod getXWalkVersionMethod;
    private ReflectMethod hasEnteredFullscreenMethod;
    private ReflectMethod leaveFullscreenMethod;
    private ReflectMethod loadAppFromManifestStringStringMethod;
    private ReflectMethod loadStringStringMethod;
    private ReflectMethod onActivityResultintintIntentMethod;
    private ReflectMethod onCreateInputConnectionEditorInfoMethod;
    private ReflectMethod onDestroyMethod;
    private ReflectMethod onFocusChangedDelegatebooleanintRectMethod;
    private ReflectMethod onHideMethod;
    private ReflectMethod onNewIntentIntentMethod;
    private ReflectMethod onScrollChangedDelegateintintintintMethod;
    private ReflectMethod onShowMethod;
    private ReflectMethod onTouchEventDelegateMotionEventMethod;
    private ReflectMethod onTouchEventMotionEventMethod;
    private ReflectMethod pauseTimersMethod;
    private ReflectMethod performLongClickDelegateMethod;
    private ReflectMethod reloadintMethod;
    private ReflectMethod restoreStateBundleMethod;
    private ReflectMethod resumeTimersMethod;
    private ReflectMethod saveStateBundleMethod;
    private ReflectMethod setAcceptLanguagesStringMethod;
    private ReflectMethod setBackgroundColorintMethod;
    private ReflectMethod setDownloadListenerXWalkDownloadListenerInternalMethod;
    private ReflectMethod setInitialScaleintMethod;
    private ReflectMethod setLayerTypeintPaintMethod;
    private ReflectMethod setNetworkAvailablebooleanMethod;
    private ReflectMethod setResourceClientXWalkResourceClientInternalMethod;
    private ReflectMethod setSurfaceViewVisibilityintMethod;
    private ReflectMethod setUIClientXWalkUIClientInternalMethod;
    private ReflectMethod setUserAgentStringStringMethod;
    private ReflectMethod setVisibilityintMethod;
    private ReflectMethod setZOrderOnTopbooleanMethod;
    private ReflectMethod stopLoadingMethod;
    private Object wrapper;
    private ReflectMethod zoomByfloatMethod;
    private ReflectMethod zoomInMethod;
    private ReflectMethod zoomOutMethod;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkViewBridge(Context context, Object wrapper) {
        super(context);
        this.loadStringStringMethod = new ReflectMethod(null, "load", new Class[0]);
        this.loadAppFromManifestStringStringMethod = new ReflectMethod(null, "loadAppFromManifest", new Class[0]);
        this.reloadintMethod = new ReflectMethod(null, "reload", new Class[0]);
        this.stopLoadingMethod = new ReflectMethod(null, "stopLoading", new Class[0]);
        this.getUrlMethod = new ReflectMethod(null, "getUrl", new Class[0]);
        this.getTitleMethod = new ReflectMethod(null, "getTitle", new Class[0]);
        this.getOriginalUrlMethod = new ReflectMethod(null, "getOriginalUrl", new Class[0]);
        this.getNavigationHistoryMethod = new ReflectMethod(null, "getNavigationHistory", new Class[0]);
        this.addJavascriptInterfaceObjectStringMethod = new ReflectMethod(null, "addJavascriptInterface", new Class[0]);
        this.evaluateJavascriptStringValueCallbackMethod = new ReflectMethod(null, "evaluateJavascript", new Class[0]);
        this.clearCachebooleanMethod = new ReflectMethod(null, "clearCache", new Class[0]);
        this.clearCacheForSingleFileStringMethod = new ReflectMethod(null, "clearCacheForSingleFile", new Class[0]);
        this.hasEnteredFullscreenMethod = new ReflectMethod(null, "hasEnteredFullscreen", new Class[0]);
        this.leaveFullscreenMethod = new ReflectMethod(null, "leaveFullscreen", new Class[0]);
        this.pauseTimersMethod = new ReflectMethod(null, "pauseTimers", new Class[0]);
        this.resumeTimersMethod = new ReflectMethod(null, "resumeTimers", new Class[0]);
        this.onHideMethod = new ReflectMethod(null, "onHide", new Class[0]);
        this.onShowMethod = new ReflectMethod(null, "onShow", new Class[0]);
        this.onDestroyMethod = new ReflectMethod(null, "onDestroy", new Class[0]);
        this.onActivityResultintintIntentMethod = new ReflectMethod(null, "onActivityResult", new Class[0]);
        this.onNewIntentIntentMethod = new ReflectMethod(null, "onNewIntent", new Class[0]);
        this.saveStateBundleMethod = new ReflectMethod(null, "saveState", new Class[0]);
        this.restoreStateBundleMethod = new ReflectMethod(null, "restoreState", new Class[0]);
        this.getAPIVersionMethod = new ReflectMethod(null, "getAPIVersion", new Class[0]);
        this.getXWalkVersionMethod = new ReflectMethod(null, "getXWalkVersion", new Class[0]);
        this.setUIClientXWalkUIClientInternalMethod = new ReflectMethod(null, "setUIClient", new Class[0]);
        this.setResourceClientXWalkResourceClientInternalMethod = new ReflectMethod(null, "setResourceClient", new Class[0]);
        this.setBackgroundColorintMethod = new ReflectMethod(null, "setBackgroundColor", new Class[0]);
        this.setLayerTypeintPaintMethod = new ReflectMethod(null, "setLayerType", new Class[0]);
        this.setUserAgentStringStringMethod = new ReflectMethod(null, "setUserAgentString", new Class[0]);
        this.getUserAgentStringMethod = new ReflectMethod(null, "getUserAgentString", new Class[0]);
        this.setAcceptLanguagesStringMethod = new ReflectMethod(null, "setAcceptLanguages", new Class[0]);
        this.setNetworkAvailablebooleanMethod = new ReflectMethod(null, "setNetworkAvailable", new Class[0]);
        this.getRemoteDebuggingUrlMethod = new ReflectMethod(null, "getRemoteDebuggingUrl", new Class[0]);
        this.zoomInMethod = new ReflectMethod(null, "zoomIn", new Class[0]);
        this.zoomOutMethod = new ReflectMethod(null, "zoomOut", new Class[0]);
        this.zoomByfloatMethod = new ReflectMethod(null, "zoomBy", new Class[0]);
        this.canZoomInMethod = new ReflectMethod(null, "canZoomIn", new Class[0]);
        this.canZoomOutMethod = new ReflectMethod(null, "canZoomOut", new Class[0]);
        this.onCreateInputConnectionEditorInfoMethod = new ReflectMethod(null, "onCreateInputConnection", new Class[0]);
        this.setInitialScaleintMethod = new ReflectMethod(null, "setInitialScale", new Class[0]);
        this.setZOrderOnTopbooleanMethod = new ReflectMethod(null, "setZOrderOnTop", new Class[0]);
        this.clearFormDataMethod = new ReflectMethod(null, "clearFormData", new Class[0]);
        this.setVisibilityintMethod = new ReflectMethod(null, "setVisibility", new Class[0]);
        this.setSurfaceViewVisibilityintMethod = new ReflectMethod(null, "setSurfaceViewVisibility", new Class[0]);
        this.setDownloadListenerXWalkDownloadListenerInternalMethod = new ReflectMethod(null, "setDownloadListener", new Class[0]);
        this.performLongClickDelegateMethod = new ReflectMethod(null, "performLongClickDelegate", new Class[0]);
        this.onTouchEventDelegateMotionEventMethod = new ReflectMethod(null, "onTouchEventDelegate", new Class[0]);
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        this.onScrollChangedDelegateintintintintMethod = new ReflectMethod(null, "onScrollChangedDelegate", new Class[0]);
        this.onFocusChangedDelegatebooleanintRectMethod = new ReflectMethod(null, "onFocusChangedDelegate", new Class[0]);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public XWalkViewBridge(Context context, AttributeSet attrs, Object wrapper) {
        super(context, attrs);
        this.loadStringStringMethod = new ReflectMethod(null, "load", new Class[0]);
        this.loadAppFromManifestStringStringMethod = new ReflectMethod(null, "loadAppFromManifest", new Class[0]);
        this.reloadintMethod = new ReflectMethod(null, "reload", new Class[0]);
        this.stopLoadingMethod = new ReflectMethod(null, "stopLoading", new Class[0]);
        this.getUrlMethod = new ReflectMethod(null, "getUrl", new Class[0]);
        this.getTitleMethod = new ReflectMethod(null, "getTitle", new Class[0]);
        this.getOriginalUrlMethod = new ReflectMethod(null, "getOriginalUrl", new Class[0]);
        this.getNavigationHistoryMethod = new ReflectMethod(null, "getNavigationHistory", new Class[0]);
        this.addJavascriptInterfaceObjectStringMethod = new ReflectMethod(null, "addJavascriptInterface", new Class[0]);
        this.evaluateJavascriptStringValueCallbackMethod = new ReflectMethod(null, "evaluateJavascript", new Class[0]);
        this.clearCachebooleanMethod = new ReflectMethod(null, "clearCache", new Class[0]);
        this.clearCacheForSingleFileStringMethod = new ReflectMethod(null, "clearCacheForSingleFile", new Class[0]);
        this.hasEnteredFullscreenMethod = new ReflectMethod(null, "hasEnteredFullscreen", new Class[0]);
        this.leaveFullscreenMethod = new ReflectMethod(null, "leaveFullscreen", new Class[0]);
        this.pauseTimersMethod = new ReflectMethod(null, "pauseTimers", new Class[0]);
        this.resumeTimersMethod = new ReflectMethod(null, "resumeTimers", new Class[0]);
        this.onHideMethod = new ReflectMethod(null, "onHide", new Class[0]);
        this.onShowMethod = new ReflectMethod(null, "onShow", new Class[0]);
        this.onDestroyMethod = new ReflectMethod(null, "onDestroy", new Class[0]);
        this.onActivityResultintintIntentMethod = new ReflectMethod(null, "onActivityResult", new Class[0]);
        this.onNewIntentIntentMethod = new ReflectMethod(null, "onNewIntent", new Class[0]);
        this.saveStateBundleMethod = new ReflectMethod(null, "saveState", new Class[0]);
        this.restoreStateBundleMethod = new ReflectMethod(null, "restoreState", new Class[0]);
        this.getAPIVersionMethod = new ReflectMethod(null, "getAPIVersion", new Class[0]);
        this.getXWalkVersionMethod = new ReflectMethod(null, "getXWalkVersion", new Class[0]);
        this.setUIClientXWalkUIClientInternalMethod = new ReflectMethod(null, "setUIClient", new Class[0]);
        this.setResourceClientXWalkResourceClientInternalMethod = new ReflectMethod(null, "setResourceClient", new Class[0]);
        this.setBackgroundColorintMethod = new ReflectMethod(null, "setBackgroundColor", new Class[0]);
        this.setLayerTypeintPaintMethod = new ReflectMethod(null, "setLayerType", new Class[0]);
        this.setUserAgentStringStringMethod = new ReflectMethod(null, "setUserAgentString", new Class[0]);
        this.getUserAgentStringMethod = new ReflectMethod(null, "getUserAgentString", new Class[0]);
        this.setAcceptLanguagesStringMethod = new ReflectMethod(null, "setAcceptLanguages", new Class[0]);
        this.setNetworkAvailablebooleanMethod = new ReflectMethod(null, "setNetworkAvailable", new Class[0]);
        this.getRemoteDebuggingUrlMethod = new ReflectMethod(null, "getRemoteDebuggingUrl", new Class[0]);
        this.zoomInMethod = new ReflectMethod(null, "zoomIn", new Class[0]);
        this.zoomOutMethod = new ReflectMethod(null, "zoomOut", new Class[0]);
        this.zoomByfloatMethod = new ReflectMethod(null, "zoomBy", new Class[0]);
        this.canZoomInMethod = new ReflectMethod(null, "canZoomIn", new Class[0]);
        this.canZoomOutMethod = new ReflectMethod(null, "canZoomOut", new Class[0]);
        this.onCreateInputConnectionEditorInfoMethod = new ReflectMethod(null, "onCreateInputConnection", new Class[0]);
        this.setInitialScaleintMethod = new ReflectMethod(null, "setInitialScale", new Class[0]);
        this.setZOrderOnTopbooleanMethod = new ReflectMethod(null, "setZOrderOnTop", new Class[0]);
        this.clearFormDataMethod = new ReflectMethod(null, "clearFormData", new Class[0]);
        this.setVisibilityintMethod = new ReflectMethod(null, "setVisibility", new Class[0]);
        this.setSurfaceViewVisibilityintMethod = new ReflectMethod(null, "setSurfaceViewVisibility", new Class[0]);
        this.setDownloadListenerXWalkDownloadListenerInternalMethod = new ReflectMethod(null, "setDownloadListener", new Class[0]);
        this.performLongClickDelegateMethod = new ReflectMethod(null, "performLongClickDelegate", new Class[0]);
        this.onTouchEventDelegateMotionEventMethod = new ReflectMethod(null, "onTouchEventDelegate", new Class[0]);
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        this.onScrollChangedDelegateintintintintMethod = new ReflectMethod(null, "onScrollChangedDelegate", new Class[0]);
        this.onFocusChangedDelegatebooleanintRectMethod = new ReflectMethod(null, "onFocusChangedDelegate", new Class[0]);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public XWalkViewBridge(Context context, Activity activity, Object wrapper) {
        super(context, activity);
        this.loadStringStringMethod = new ReflectMethod(null, "load", new Class[0]);
        this.loadAppFromManifestStringStringMethod = new ReflectMethod(null, "loadAppFromManifest", new Class[0]);
        this.reloadintMethod = new ReflectMethod(null, "reload", new Class[0]);
        this.stopLoadingMethod = new ReflectMethod(null, "stopLoading", new Class[0]);
        this.getUrlMethod = new ReflectMethod(null, "getUrl", new Class[0]);
        this.getTitleMethod = new ReflectMethod(null, "getTitle", new Class[0]);
        this.getOriginalUrlMethod = new ReflectMethod(null, "getOriginalUrl", new Class[0]);
        this.getNavigationHistoryMethod = new ReflectMethod(null, "getNavigationHistory", new Class[0]);
        this.addJavascriptInterfaceObjectStringMethod = new ReflectMethod(null, "addJavascriptInterface", new Class[0]);
        this.evaluateJavascriptStringValueCallbackMethod = new ReflectMethod(null, "evaluateJavascript", new Class[0]);
        this.clearCachebooleanMethod = new ReflectMethod(null, "clearCache", new Class[0]);
        this.clearCacheForSingleFileStringMethod = new ReflectMethod(null, "clearCacheForSingleFile", new Class[0]);
        this.hasEnteredFullscreenMethod = new ReflectMethod(null, "hasEnteredFullscreen", new Class[0]);
        this.leaveFullscreenMethod = new ReflectMethod(null, "leaveFullscreen", new Class[0]);
        this.pauseTimersMethod = new ReflectMethod(null, "pauseTimers", new Class[0]);
        this.resumeTimersMethod = new ReflectMethod(null, "resumeTimers", new Class[0]);
        this.onHideMethod = new ReflectMethod(null, "onHide", new Class[0]);
        this.onShowMethod = new ReflectMethod(null, "onShow", new Class[0]);
        this.onDestroyMethod = new ReflectMethod(null, "onDestroy", new Class[0]);
        this.onActivityResultintintIntentMethod = new ReflectMethod(null, "onActivityResult", new Class[0]);
        this.onNewIntentIntentMethod = new ReflectMethod(null, "onNewIntent", new Class[0]);
        this.saveStateBundleMethod = new ReflectMethod(null, "saveState", new Class[0]);
        this.restoreStateBundleMethod = new ReflectMethod(null, "restoreState", new Class[0]);
        this.getAPIVersionMethod = new ReflectMethod(null, "getAPIVersion", new Class[0]);
        this.getXWalkVersionMethod = new ReflectMethod(null, "getXWalkVersion", new Class[0]);
        this.setUIClientXWalkUIClientInternalMethod = new ReflectMethod(null, "setUIClient", new Class[0]);
        this.setResourceClientXWalkResourceClientInternalMethod = new ReflectMethod(null, "setResourceClient", new Class[0]);
        this.setBackgroundColorintMethod = new ReflectMethod(null, "setBackgroundColor", new Class[0]);
        this.setLayerTypeintPaintMethod = new ReflectMethod(null, "setLayerType", new Class[0]);
        this.setUserAgentStringStringMethod = new ReflectMethod(null, "setUserAgentString", new Class[0]);
        this.getUserAgentStringMethod = new ReflectMethod(null, "getUserAgentString", new Class[0]);
        this.setAcceptLanguagesStringMethod = new ReflectMethod(null, "setAcceptLanguages", new Class[0]);
        this.setNetworkAvailablebooleanMethod = new ReflectMethod(null, "setNetworkAvailable", new Class[0]);
        this.getRemoteDebuggingUrlMethod = new ReflectMethod(null, "getRemoteDebuggingUrl", new Class[0]);
        this.zoomInMethod = new ReflectMethod(null, "zoomIn", new Class[0]);
        this.zoomOutMethod = new ReflectMethod(null, "zoomOut", new Class[0]);
        this.zoomByfloatMethod = new ReflectMethod(null, "zoomBy", new Class[0]);
        this.canZoomInMethod = new ReflectMethod(null, "canZoomIn", new Class[0]);
        this.canZoomOutMethod = new ReflectMethod(null, "canZoomOut", new Class[0]);
        this.onCreateInputConnectionEditorInfoMethod = new ReflectMethod(null, "onCreateInputConnection", new Class[0]);
        this.setInitialScaleintMethod = new ReflectMethod(null, "setInitialScale", new Class[0]);
        this.setZOrderOnTopbooleanMethod = new ReflectMethod(null, "setZOrderOnTop", new Class[0]);
        this.clearFormDataMethod = new ReflectMethod(null, "clearFormData", new Class[0]);
        this.setVisibilityintMethod = new ReflectMethod(null, "setVisibility", new Class[0]);
        this.setSurfaceViewVisibilityintMethod = new ReflectMethod(null, "setSurfaceViewVisibility", new Class[0]);
        this.setDownloadListenerXWalkDownloadListenerInternalMethod = new ReflectMethod(null, "setDownloadListener", new Class[0]);
        this.performLongClickDelegateMethod = new ReflectMethod(null, "performLongClickDelegate", new Class[0]);
        this.onTouchEventDelegateMotionEventMethod = new ReflectMethod(null, "onTouchEventDelegate", new Class[0]);
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        this.onScrollChangedDelegateintintintintMethod = new ReflectMethod(null, "onScrollChangedDelegate", new Class[0]);
        this.onFocusChangedDelegatebooleanintRectMethod = new ReflectMethod(null, "onFocusChangedDelegate", new Class[0]);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public void load(String url, String content) {
        if (this.loadStringStringMethod.isNull()) {
            loadSuper(url, content);
            return;
        }
        this.loadStringStringMethod.invoke(url, content);
    }

    public void loadSuper(String url, String content) {
        super.load(url, content);
    }

    public void loadAppFromManifest(String url, String content) {
        if (this.loadAppFromManifestStringStringMethod.isNull()) {
            loadAppFromManifestSuper(url, content);
            return;
        }
        this.loadAppFromManifestStringStringMethod.invoke(url, content);
    }

    public void loadAppFromManifestSuper(String url, String content) {
        super.loadAppFromManifest(url, content);
    }

    public void reload(int mode) {
        if (this.reloadintMethod.isNull()) {
            reloadSuper(mode);
            return;
        }
        this.reloadintMethod.invoke(Integer.valueOf(mode));
    }

    public void reloadSuper(int mode) {
        super.reload(mode);
    }

    public void stopLoading() {
        if (this.stopLoadingMethod.isNull()) {
            stopLoadingSuper();
        } else {
            this.stopLoadingMethod.invoke(new Object[0]);
        }
    }

    public void stopLoadingSuper() {
        super.stopLoading();
    }

    public String getUrl() {
        if (this.getUrlMethod.isNull()) {
            return getUrlSuper();
        }
        return (String) this.getUrlMethod.invoke(new Object[0]);
    }

    public String getUrlSuper() {
        String ret = super.getUrl();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public String getTitle() {
        if (this.getTitleMethod.isNull()) {
            return getTitleSuper();
        }
        return (String) this.getTitleMethod.invoke(new Object[0]);
    }

    public String getTitleSuper() {
        String ret = super.getTitle();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public String getOriginalUrl() {
        if (this.getOriginalUrlMethod.isNull()) {
            return getOriginalUrlSuper();
        }
        return (String) this.getOriginalUrlMethod.invoke(new Object[0]);
    }

    public String getOriginalUrlSuper() {
        String ret = super.getOriginalUrl();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public XWalkNavigationHistoryInternal getNavigationHistory() {
        if (this.getNavigationHistoryMethod.isNull()) {
            return getNavigationHistorySuper();
        }
        return (XWalkNavigationHistoryBridge) this.coreBridge.getBridgeObject(this.getNavigationHistoryMethod.invoke(new Object[0]));
    }

    public XWalkNavigationHistoryBridge getNavigationHistorySuper() {
        XWalkNavigationHistoryInternal ret = super.getNavigationHistory();
        if (ret == null) {
            return null;
        }
        return ret instanceof XWalkNavigationHistoryBridge ? (XWalkNavigationHistoryBridge) ret : new XWalkNavigationHistoryBridge(ret);
    }

    public void addJavascriptInterface(Object object, String name) {
        if (this.addJavascriptInterfaceObjectStringMethod.isNull()) {
            addJavascriptInterfaceSuper(object, name);
            return;
        }
        this.addJavascriptInterfaceObjectStringMethod.invoke(object, name);
    }

    public void addJavascriptInterfaceSuper(Object object, String name) {
        super.addJavascriptInterface(object, name);
    }

    public void evaluateJavascript(String script, ValueCallback<String> callback) {
        if (this.evaluateJavascriptStringValueCallbackMethod.isNull()) {
            evaluateJavascriptSuper(script, callback);
            return;
        }
        this.evaluateJavascriptStringValueCallbackMethod.invoke(script, callback);
    }

    public void evaluateJavascriptSuper(String script, ValueCallback<String> callback) {
        super.evaluateJavascript(script, callback);
    }

    public void clearCache(boolean includeDiskFiles) {
        if (this.clearCachebooleanMethod.isNull()) {
            clearCacheSuper(includeDiskFiles);
            return;
        }
        this.clearCachebooleanMethod.invoke(Boolean.valueOf(includeDiskFiles));
    }

    public void clearCacheSuper(boolean includeDiskFiles) {
        super.clearCache(includeDiskFiles);
    }

    public void clearCacheForSingleFile(String url) {
        if (this.clearCacheForSingleFileStringMethod.isNull()) {
            clearCacheForSingleFileSuper(url);
            return;
        }
        this.clearCacheForSingleFileStringMethod.invoke(url);
    }

    public void clearCacheForSingleFileSuper(String url) {
        super.clearCacheForSingleFile(url);
    }

    public boolean hasEnteredFullscreen() {
        if (this.hasEnteredFullscreenMethod.isNull()) {
            return hasEnteredFullscreenSuper();
        }
        return ((Boolean) this.hasEnteredFullscreenMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean hasEnteredFullscreenSuper() {
        return super.hasEnteredFullscreen();
    }

    public void leaveFullscreen() {
        if (this.leaveFullscreenMethod.isNull()) {
            leaveFullscreenSuper();
        } else {
            this.leaveFullscreenMethod.invoke(new Object[0]);
        }
    }

    public void leaveFullscreenSuper() {
        super.leaveFullscreen();
    }

    public void pauseTimers() {
        if (this.pauseTimersMethod.isNull()) {
            pauseTimersSuper();
        } else {
            this.pauseTimersMethod.invoke(new Object[0]);
        }
    }

    public void pauseTimersSuper() {
        super.pauseTimers();
    }

    public void resumeTimers() {
        if (this.resumeTimersMethod.isNull()) {
            resumeTimersSuper();
        } else {
            this.resumeTimersMethod.invoke(new Object[0]);
        }
    }

    public void resumeTimersSuper() {
        super.resumeTimers();
    }

    public void onHide() {
        if (this.onHideMethod.isNull()) {
            onHideSuper();
        } else {
            this.onHideMethod.invoke(new Object[0]);
        }
    }

    public void onHideSuper() {
        super.onHide();
    }

    public void onShow() {
        if (this.onShowMethod.isNull()) {
            onShowSuper();
        } else {
            this.onShowMethod.invoke(new Object[0]);
        }
    }

    public void onShowSuper() {
        super.onShow();
    }

    public void onDestroy() {
        if (this.onDestroyMethod.isNull()) {
            onDestroySuper();
        } else {
            this.onDestroyMethod.invoke(new Object[0]);
        }
    }

    public void onDestroySuper() {
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.onActivityResultintintIntentMethod.isNull()) {
            onActivityResultSuper(requestCode, resultCode, data);
            return;
        }
        this.onActivityResultintintIntentMethod.invoke(Integer.valueOf(requestCode), Integer.valueOf(resultCode), data);
    }

    public void onActivityResultSuper(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onNewIntent(Intent intent) {
        if (this.onNewIntentIntentMethod.isNull()) {
            return onNewIntentSuper(intent);
        }
        return ((Boolean) this.onNewIntentIntentMethod.invoke(intent)).booleanValue();
    }

    public boolean onNewIntentSuper(Intent intent) {
        return super.onNewIntent(intent);
    }

    public boolean saveState(Bundle outState) {
        if (this.saveStateBundleMethod.isNull()) {
            return saveStateSuper(outState);
        }
        return ((Boolean) this.saveStateBundleMethod.invoke(outState)).booleanValue();
    }

    public boolean saveStateSuper(Bundle outState) {
        return super.saveState(outState);
    }

    public boolean restoreState(Bundle inState) {
        if (this.restoreStateBundleMethod.isNull()) {
            return restoreStateSuper(inState);
        }
        return ((Boolean) this.restoreStateBundleMethod.invoke(inState)).booleanValue();
    }

    public boolean restoreStateSuper(Bundle inState) {
        return super.restoreState(inState);
    }

    public String getAPIVersion() {
        if (this.getAPIVersionMethod.isNull()) {
            return getAPIVersionSuper();
        }
        return (String) this.getAPIVersionMethod.invoke(new Object[0]);
    }

    public String getAPIVersionSuper() {
        String ret = super.getAPIVersion();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public String getXWalkVersion() {
        if (this.getXWalkVersionMethod.isNull()) {
            return getXWalkVersionSuper();
        }
        return (String) this.getXWalkVersionMethod.invoke(new Object[0]);
    }

    public String getXWalkVersionSuper() {
        String ret = super.getXWalkVersion();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setUIClient(XWalkUIClientInternal client) {
        if (client instanceof XWalkUIClientBridge) {
            setUIClient((XWalkUIClientBridge) client);
        } else {
            super.setUIClient(client);
        }
    }

    public void setUIClient(XWalkUIClientBridge client) {
        if (this.setUIClientXWalkUIClientInternalMethod.isNull()) {
            setUIClientSuper(client);
            return;
        }
        this.setUIClientXWalkUIClientInternalMethod.invoke(client.getWrapper());
    }

    public void setUIClientSuper(XWalkUIClientBridge client) {
        super.setUIClient(client);
    }

    public void setResourceClient(XWalkResourceClientInternal client) {
        if (client instanceof XWalkResourceClientBridge) {
            setResourceClient((XWalkResourceClientBridge) client);
        } else {
            super.setResourceClient(client);
        }
    }

    public void setResourceClient(XWalkResourceClientBridge client) {
        if (this.setResourceClientXWalkResourceClientInternalMethod.isNull()) {
            setResourceClientSuper(client);
            return;
        }
        this.setResourceClientXWalkResourceClientInternalMethod.invoke(client.getWrapper());
    }

    public void setResourceClientSuper(XWalkResourceClientBridge client) {
        super.setResourceClient(client);
    }

    public void setBackgroundColor(int color) {
        if (this.setBackgroundColorintMethod.isNull()) {
            setBackgroundColorSuper(color);
            return;
        }
        this.setBackgroundColorintMethod.invoke(Integer.valueOf(color));
    }

    public void setBackgroundColorSuper(int color) {
        super.setBackgroundColor(color);
    }

    public void setLayerType(int layerType, Paint paint) {
        if (this.setLayerTypeintPaintMethod.isNull()) {
            setLayerTypeSuper(layerType, paint);
            return;
        }
        this.setLayerTypeintPaintMethod.invoke(Integer.valueOf(layerType), paint);
    }

    public void setLayerTypeSuper(int layerType, Paint paint) {
        super.setLayerType(layerType, paint);
    }

    public void setUserAgentString(String userAgent) {
        if (this.setUserAgentStringStringMethod.isNull()) {
            setUserAgentStringSuper(userAgent);
            return;
        }
        this.setUserAgentStringStringMethod.invoke(userAgent);
    }

    public void setUserAgentStringSuper(String userAgent) {
        super.setUserAgentString(userAgent);
    }

    public String getUserAgentString() {
        if (this.getUserAgentStringMethod.isNull()) {
            return getUserAgentStringSuper();
        }
        return (String) this.getUserAgentStringMethod.invoke(new Object[0]);
    }

    public String getUserAgentStringSuper() {
        String ret = super.getUserAgentString();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setAcceptLanguages(String acceptLanguages) {
        if (this.setAcceptLanguagesStringMethod.isNull()) {
            setAcceptLanguagesSuper(acceptLanguages);
            return;
        }
        this.setAcceptLanguagesStringMethod.invoke(acceptLanguages);
    }

    public void setAcceptLanguagesSuper(String acceptLanguages) {
        super.setAcceptLanguages(acceptLanguages);
    }

    public void setNetworkAvailable(boolean networkUp) {
        if (this.setNetworkAvailablebooleanMethod.isNull()) {
            setNetworkAvailableSuper(networkUp);
            return;
        }
        this.setNetworkAvailablebooleanMethod.invoke(Boolean.valueOf(networkUp));
    }

    public void setNetworkAvailableSuper(boolean networkUp) {
        super.setNetworkAvailable(networkUp);
    }

    public Uri getRemoteDebuggingUrl() {
        if (this.getRemoteDebuggingUrlMethod.isNull()) {
            return getRemoteDebuggingUrlSuper();
        }
        return (Uri) this.getRemoteDebuggingUrlMethod.invoke(new Object[0]);
    }

    public Uri getRemoteDebuggingUrlSuper() {
        Uri ret = super.getRemoteDebuggingUrl();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public boolean zoomIn() {
        if (this.zoomInMethod.isNull()) {
            return zoomInSuper();
        }
        return ((Boolean) this.zoomInMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean zoomInSuper() {
        return super.zoomIn();
    }

    public boolean zoomOut() {
        if (this.zoomOutMethod.isNull()) {
            return zoomOutSuper();
        }
        return ((Boolean) this.zoomOutMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean zoomOutSuper() {
        return super.zoomOut();
    }

    public void zoomBy(float factor) {
        if (this.zoomByfloatMethod.isNull()) {
            zoomBySuper(factor);
            return;
        }
        this.zoomByfloatMethod.invoke(Float.valueOf(factor));
    }

    public void zoomBySuper(float factor) {
        super.zoomBy(factor);
    }

    public boolean canZoomIn() {
        if (this.canZoomInMethod.isNull()) {
            return canZoomInSuper();
        }
        return ((Boolean) this.canZoomInMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean canZoomInSuper() {
        return super.canZoomIn();
    }

    public boolean canZoomOut() {
        if (this.canZoomOutMethod.isNull()) {
            return canZoomOutSuper();
        }
        return ((Boolean) this.canZoomOutMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean canZoomOutSuper() {
        return super.canZoomOut();
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (this.onCreateInputConnectionEditorInfoMethod.isNull()) {
            return onCreateInputConnectionSuper(outAttrs);
        }
        return (InputConnection) this.onCreateInputConnectionEditorInfoMethod.invoke(outAttrs);
    }

    public InputConnection onCreateInputConnectionSuper(EditorInfo outAttrs) {
        InputConnection ret = super.onCreateInputConnection(outAttrs);
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setInitialScale(int scaleInPercent) {
        if (this.setInitialScaleintMethod.isNull()) {
            setInitialScaleSuper(scaleInPercent);
            return;
        }
        this.setInitialScaleintMethod.invoke(Integer.valueOf(scaleInPercent));
    }

    public void setInitialScaleSuper(int scaleInPercent) {
        super.setInitialScale(scaleInPercent);
    }

    public void setZOrderOnTop(boolean onTop) {
        if (this.setZOrderOnTopbooleanMethod.isNull()) {
            setZOrderOnTopSuper(onTop);
            return;
        }
        this.setZOrderOnTopbooleanMethod.invoke(Boolean.valueOf(onTop));
    }

    public void setZOrderOnTopSuper(boolean onTop) {
        super.setZOrderOnTop(onTop);
    }

    public void clearFormData() {
        if (this.clearFormDataMethod.isNull()) {
            clearFormDataSuper();
        } else {
            this.clearFormDataMethod.invoke(new Object[0]);
        }
    }

    public void clearFormDataSuper() {
        super.clearFormData();
    }

    public void setVisibility(int visibility) {
        if (this.setVisibilityintMethod.isNull()) {
            setVisibilitySuper(visibility);
            return;
        }
        this.setVisibilityintMethod.invoke(Integer.valueOf(visibility));
    }

    public void setVisibilitySuper(int visibility) {
        super.setVisibility(visibility);
    }

    public void setSurfaceViewVisibility(int visibility) {
        if (this.setSurfaceViewVisibilityintMethod.isNull()) {
            setSurfaceViewVisibilitySuper(visibility);
            return;
        }
        this.setSurfaceViewVisibilityintMethod.invoke(Integer.valueOf(visibility));
    }

    public void setSurfaceViewVisibilitySuper(int visibility) {
        super.setSurfaceViewVisibility(visibility);
    }

    public void setDownloadListener(XWalkDownloadListenerInternal listener) {
        if (listener instanceof XWalkDownloadListenerBridge) {
            setDownloadListener((XWalkDownloadListenerBridge) listener);
        } else {
            super.setDownloadListener(listener);
        }
    }

    public void setDownloadListener(XWalkDownloadListenerBridge listener) {
        if (this.setDownloadListenerXWalkDownloadListenerInternalMethod.isNull()) {
            setDownloadListenerSuper(listener);
            return;
        }
        this.setDownloadListenerXWalkDownloadListenerInternalMethod.invoke(listener.getWrapper());
    }

    public void setDownloadListenerSuper(XWalkDownloadListenerBridge listener) {
        super.setDownloadListener(listener);
    }

    public boolean performLongClickDelegate() {
        if (this.performLongClickDelegateMethod.isNull()) {
            return performLongClickDelegateSuper();
        }
        return ((Boolean) this.performLongClickDelegateMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean performLongClickDelegateSuper() {
        return super.performLongClickDelegate();
    }

    public boolean onTouchEventDelegate(MotionEvent event) {
        if (this.onTouchEventDelegateMotionEventMethod.isNull()) {
            return onTouchEventDelegateSuper(event);
        }
        return ((Boolean) this.onTouchEventDelegateMotionEventMethod.invoke(event)).booleanValue();
    }

    public boolean onTouchEventDelegateSuper(MotionEvent event) {
        return super.onTouchEventDelegate(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.onTouchEventMotionEventMethod.isNull()) {
            return onTouchEventSuper(event);
        }
        return ((Boolean) this.onTouchEventMotionEventMethod.invoke(event)).booleanValue();
    }

    public boolean onTouchEventSuper(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void onScrollChangedDelegate(int l, int t, int oldl, int oldt) {
        if (this.onScrollChangedDelegateintintintintMethod.isNull()) {
            onScrollChangedDelegateSuper(l, t, oldl, oldt);
            return;
        }
        this.onScrollChangedDelegateintintintintMethod.invoke(Integer.valueOf(l), Integer.valueOf(t), Integer.valueOf(oldl), Integer.valueOf(oldt));
    }

    public void onScrollChangedDelegateSuper(int l, int t, int oldl, int oldt) {
        super.onScrollChangedDelegate(l, t, oldl, oldt);
    }

    public void onFocusChangedDelegate(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (this.onFocusChangedDelegatebooleanintRectMethod.isNull()) {
            onFocusChangedDelegateSuper(gainFocus, direction, previouslyFocusedRect);
            return;
        }
        this.onFocusChangedDelegatebooleanintRectMethod.invoke(Boolean.valueOf(gainFocus), Integer.valueOf(direction), previouslyFocusedRect);
    }

    public void onFocusChangedDelegateSuper(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChangedDelegate(gainFocus, direction, previouslyFocusedRect);
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.loadStringStringMethod.init(this.wrapper, null, "load", String.class, String.class);
            this.loadAppFromManifestStringStringMethod.init(this.wrapper, null, "loadAppFromManifest", String.class, String.class);
            this.reloadintMethod.init(this.wrapper, null, "reload", Integer.TYPE);
            this.stopLoadingMethod.init(this.wrapper, null, "stopLoading", new Class[0]);
            this.getUrlMethod.init(this.wrapper, null, "getUrl", new Class[0]);
            this.getTitleMethod.init(this.wrapper, null, "getTitle", new Class[0]);
            this.getOriginalUrlMethod.init(this.wrapper, null, "getOriginalUrl", new Class[0]);
            this.getNavigationHistoryMethod.init(this.wrapper, null, "getNavigationHistory", new Class[0]);
            this.addJavascriptInterfaceObjectStringMethod.init(this.wrapper, null, "addJavascriptInterface", Object.class, String.class);
            this.evaluateJavascriptStringValueCallbackMethod.init(this.wrapper, null, "evaluateJavascript", String.class, ValueCallback.class);
            this.clearCachebooleanMethod.init(this.wrapper, null, "clearCache", Boolean.TYPE);
            this.clearCacheForSingleFileStringMethod.init(this.wrapper, null, "clearCacheForSingleFile", String.class);
            this.hasEnteredFullscreenMethod.init(this.wrapper, null, "hasEnteredFullscreen", new Class[0]);
            this.leaveFullscreenMethod.init(this.wrapper, null, "leaveFullscreen", new Class[0]);
            this.pauseTimersMethod.init(this.wrapper, null, "pauseTimers", new Class[0]);
            this.resumeTimersMethod.init(this.wrapper, null, "resumeTimers", new Class[0]);
            this.onHideMethod.init(this.wrapper, null, "onHide", new Class[0]);
            this.onShowMethod.init(this.wrapper, null, "onShow", new Class[0]);
            this.onDestroyMethod.init(this.wrapper, null, "onDestroy", new Class[0]);
            this.onActivityResultintintIntentMethod.init(this.wrapper, null, "onActivityResult", Integer.TYPE, Integer.TYPE, Intent.class);
            this.onNewIntentIntentMethod.init(this.wrapper, null, "onNewIntent", Intent.class);
            this.saveStateBundleMethod.init(this.wrapper, null, "saveState", Bundle.class);
            this.restoreStateBundleMethod.init(this.wrapper, null, "restoreState", Bundle.class);
            this.getAPIVersionMethod.init(this.wrapper, null, "getAPIVersion", new Class[0]);
            this.getXWalkVersionMethod.init(this.wrapper, null, "getXWalkVersion", new Class[0]);
            this.setUIClientXWalkUIClientInternalMethod.init(this.wrapper, null, "setUIClient", this.coreBridge.getWrapperClass("XWalkUIClient"));
            this.setResourceClientXWalkResourceClientInternalMethod.init(this.wrapper, null, "setResourceClient", this.coreBridge.getWrapperClass("XWalkResourceClient"));
            this.setBackgroundColorintMethod.init(this.wrapper, null, "setBackgroundColor", Integer.TYPE);
            this.setLayerTypeintPaintMethod.init(this.wrapper, null, "setLayerType", Integer.TYPE, Paint.class);
            this.setUserAgentStringStringMethod.init(this.wrapper, null, "setUserAgentString", String.class);
            this.getUserAgentStringMethod.init(this.wrapper, null, "getUserAgentString", new Class[0]);
            this.setAcceptLanguagesStringMethod.init(this.wrapper, null, "setAcceptLanguages", String.class);
            this.setNetworkAvailablebooleanMethod.init(this.wrapper, null, "setNetworkAvailable", Boolean.TYPE);
            this.getRemoteDebuggingUrlMethod.init(this.wrapper, null, "getRemoteDebuggingUrl", new Class[0]);
            this.zoomInMethod.init(this.wrapper, null, "zoomIn", new Class[0]);
            this.zoomOutMethod.init(this.wrapper, null, "zoomOut", new Class[0]);
            this.zoomByfloatMethod.init(this.wrapper, null, "zoomBy", Float.TYPE);
            this.canZoomInMethod.init(this.wrapper, null, "canZoomIn", new Class[0]);
            this.canZoomOutMethod.init(this.wrapper, null, "canZoomOut", new Class[0]);
            this.onCreateInputConnectionEditorInfoMethod.init(this.wrapper, null, "onCreateInputConnection", EditorInfo.class);
            this.setInitialScaleintMethod.init(this.wrapper, null, "setInitialScale", Integer.TYPE);
            this.setZOrderOnTopbooleanMethod.init(this.wrapper, null, "setZOrderOnTop", Boolean.TYPE);
            this.clearFormDataMethod.init(this.wrapper, null, "clearFormData", new Class[0]);
            this.setVisibilityintMethod.init(this.wrapper, null, "setVisibility", Integer.TYPE);
            this.setSurfaceViewVisibilityintMethod.init(this.wrapper, null, "setSurfaceViewVisibility", Integer.TYPE);
            this.setDownloadListenerXWalkDownloadListenerInternalMethod.init(this.wrapper, null, "setDownloadListener", this.coreBridge.getWrapperClass("XWalkDownloadListener"));
            this.performLongClickDelegateMethod.init(this.wrapper, null, "performLongClickDelegate", new Class[0]);
            this.onTouchEventDelegateMotionEventMethod.init(this.wrapper, null, "onTouchEventDelegate", MotionEvent.class);
            this.onTouchEventMotionEventMethod.init(this.wrapper, null, "onTouchEvent", MotionEvent.class);
            this.onScrollChangedDelegateintintintintMethod.init(this.wrapper, null, "onScrollChangedDelegate", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            this.onFocusChangedDelegatebooleanintRectMethod.init(this.wrapper, null, "onFocusChangedDelegate", Boolean.TYPE, Integer.TYPE, Rect.class);
        }
    }
}
