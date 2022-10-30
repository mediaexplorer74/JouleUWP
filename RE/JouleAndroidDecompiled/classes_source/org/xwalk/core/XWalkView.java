package org.xwalk.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;
import java.util.ArrayList;

public class XWalkView extends FrameLayout {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int RELOAD_IGNORE_CACHE = 1;
    public static final int RELOAD_NORMAL = 0;
    private ReflectMethod addJavascriptInterfaceObjectStringMethod;
    private Object bridge;
    private ReflectMethod canZoomInMethod;
    private ReflectMethod canZoomOutMethod;
    private ReflectMethod clearCacheForSingleFileStringMethod;
    private ReflectMethod clearCachebooleanMethod;
    private ReflectMethod clearFormDataMethod;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
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
    private ReflectMethod onHideMethod;
    private ReflectMethod onNewIntentIntentMethod;
    private ReflectMethod onShowMethod;
    private ReflectMethod onTouchEventMotionEventMethod;
    private ReflectMethod pauseTimersMethod;
    private ReflectMethod postWrapperMethod;
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
    private ReflectMethod zoomByfloatMethod;
    private ReflectMethod zoomInMethod;
    private ReflectMethod zoomOutMethod;

    static {
        $assertionsDisabled = !XWalkView.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    Object getBridge() {
        return this.bridge;
    }

    public XWalkView(Context context) {
        super(context, null);
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
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        SurfaceView surfaceView = new SurfaceView(context);
        surfaceView.setLayoutParams(new LayoutParams(0, 0));
        addView(surfaceView);
        this.constructorTypes = new ArrayList();
        this.constructorTypes.add(Context.class);
        this.constructorParams = new ArrayList();
        this.constructorParams.add(context);
        this.postWrapperMethod = new ReflectMethod((Object) this, "postXWalkViewInternalContextConstructor", new Class[0]);
        reflectionInit();
    }

    public void postXWalkViewInternalContextConstructor() {
        addView((FrameLayout) this.bridge, new FrameLayout.LayoutParams(-1, -1));
        removeViewAt(0);
    }

    public XWalkView(Context context, AttributeSet attrs) {
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
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        SurfaceView surfaceView = new SurfaceView(context);
        surfaceView.setLayoutParams(new LayoutParams(0, 0));
        addView(surfaceView);
        this.constructorTypes = new ArrayList();
        this.constructorTypes.add(Context.class);
        this.constructorTypes.add(AttributeSet.class);
        this.constructorParams = new ArrayList();
        this.constructorParams.add(context);
        this.constructorParams.add(attrs);
        this.postWrapperMethod = new ReflectMethod((Object) this, "postXWalkViewInternalContextAttributeSetConstructor", new Class[0]);
        reflectionInit();
    }

    public void postXWalkViewInternalContextAttributeSetConstructor() {
        addView((FrameLayout) this.bridge, new FrameLayout.LayoutParams(-1, -1));
        removeViewAt(0);
    }

    public XWalkView(Context context, Activity activity) {
        super(context, null);
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
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        SurfaceView surfaceView = new SurfaceView(context);
        surfaceView.setLayoutParams(new LayoutParams(0, 0));
        addView(surfaceView);
        this.constructorTypes = new ArrayList();
        this.constructorTypes.add(Context.class);
        this.constructorTypes.add(Activity.class);
        this.constructorParams = new ArrayList();
        this.constructorParams.add(context);
        this.constructorParams.add(activity);
        this.postWrapperMethod = new ReflectMethod((Object) this, "postXWalkViewInternalContextActivityConstructor", new Class[0]);
        reflectionInit();
    }

    public void postXWalkViewInternalContextActivityConstructor() {
        addView((FrameLayout) this.bridge, new FrameLayout.LayoutParams(-1, -1));
        removeViewAt(0);
    }

    public void load(String url, String content) {
        this.loadStringStringMethod.invoke(url, content);
    }

    public void loadAppFromManifest(String url, String content) {
        this.loadAppFromManifestStringStringMethod.invoke(url, content);
    }

    public void reload(int mode) {
        ReflectMethod reflectMethod = this.reloadintMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = Integer.valueOf(mode);
        reflectMethod.invoke(objArr);
    }

    public void stopLoading() {
        this.stopLoadingMethod.invoke(new Object[0]);
    }

    public String getUrl() {
        return (String) this.getUrlMethod.invoke(new Object[0]);
    }

    public String getTitle() {
        return (String) this.getTitleMethod.invoke(new Object[0]);
    }

    public String getOriginalUrl() {
        return (String) this.getOriginalUrlMethod.invoke(new Object[0]);
    }

    public XWalkNavigationHistory getNavigationHistory() {
        return (XWalkNavigationHistory) this.coreWrapper.getWrapperObject(this.getNavigationHistoryMethod.invoke(new Object[0]));
    }

    public void addJavascriptInterface(Object object, String name) {
        this.addJavascriptInterfaceObjectStringMethod.invoke(object, name);
    }

    public void evaluateJavascript(String script, ValueCallback<String> callback) {
        this.evaluateJavascriptStringValueCallbackMethod.invoke(script, callback);
    }

    public void clearCache(boolean includeDiskFiles) {
        ReflectMethod reflectMethod = this.clearCachebooleanMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = Boolean.valueOf(includeDiskFiles);
        reflectMethod.invoke(objArr);
    }

    public void clearCacheForSingleFile(String url) {
        ReflectMethod reflectMethod = this.clearCacheForSingleFileStringMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = url;
        reflectMethod.invoke(objArr);
    }

    public boolean hasEnteredFullscreen() {
        return ((Boolean) this.hasEnteredFullscreenMethod.invoke(new Object[0])).booleanValue();
    }

    public void leaveFullscreen() {
        this.leaveFullscreenMethod.invoke(new Object[0]);
    }

    public void pauseTimers() {
        this.pauseTimersMethod.invoke(new Object[0]);
    }

    public void resumeTimers() {
        this.resumeTimersMethod.invoke(new Object[0]);
    }

    public void onHide() {
        this.onHideMethod.invoke(new Object[0]);
    }

    public void onShow() {
        this.onShowMethod.invoke(new Object[0]);
    }

    public void onDestroy() {
        this.onDestroyMethod.invoke(new Object[0]);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.onActivityResultintintIntentMethod.invoke(Integer.valueOf(requestCode), Integer.valueOf(resultCode), data);
    }

    public boolean onNewIntent(Intent intent) {
        ReflectMethod reflectMethod = this.onNewIntentIntentMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = intent;
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public boolean saveState(Bundle outState) {
        ReflectMethod reflectMethod = this.saveStateBundleMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = outState;
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public boolean restoreState(Bundle inState) {
        ReflectMethod reflectMethod = this.restoreStateBundleMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = inState;
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public String getAPIVersion() {
        return (String) this.getAPIVersionMethod.invoke(new Object[0]);
    }

    public String getXWalkVersion() {
        return (String) this.getXWalkVersionMethod.invoke(new Object[0]);
    }

    public void setUIClient(XWalkUIClient client) {
        if (this.setUIClientXWalkUIClientInternalMethod.isNull()) {
            ReflectMethod reflectMethod = this.setUIClientXWalkUIClientInternalMethod;
            Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
            objArr[0] = new ReflectMethod((Object) client, "getBridge", new Class[0]);
            reflectMethod.setArguments(objArr);
            XWalkCoreWrapper.reserveReflectMethod(this.setUIClientXWalkUIClientInternalMethod);
            return;
        }
        reflectMethod = this.setUIClientXWalkUIClientInternalMethod;
        objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = client.getBridge();
        reflectMethod.invoke(objArr);
    }

    public void setResourceClient(XWalkResourceClient client) {
        if (this.setResourceClientXWalkResourceClientInternalMethod.isNull()) {
            ReflectMethod reflectMethod = this.setResourceClientXWalkResourceClientInternalMethod;
            Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
            objArr[0] = new ReflectMethod((Object) client, "getBridge", new Class[0]);
            reflectMethod.setArguments(objArr);
            XWalkCoreWrapper.reserveReflectMethod(this.setResourceClientXWalkResourceClientInternalMethod);
            return;
        }
        reflectMethod = this.setResourceClientXWalkResourceClientInternalMethod;
        objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = client.getBridge();
        reflectMethod.invoke(objArr);
    }

    public void setBackgroundColor(int color) {
        ReflectMethod reflectMethod = this.setBackgroundColorintMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = Integer.valueOf(color);
        reflectMethod.invoke(objArr);
    }

    public void setLayerType(int layerType, Paint paint) {
        this.setLayerTypeintPaintMethod.invoke(Integer.valueOf(layerType), paint);
    }

    public void setUserAgentString(String userAgent) {
        ReflectMethod reflectMethod = this.setUserAgentStringStringMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = userAgent;
        reflectMethod.invoke(objArr);
    }

    public String getUserAgentString() {
        return (String) this.getUserAgentStringMethod.invoke(new Object[0]);
    }

    public void setAcceptLanguages(String acceptLanguages) {
        ReflectMethod reflectMethod = this.setAcceptLanguagesStringMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = acceptLanguages;
        reflectMethod.invoke(objArr);
    }

    public void setNetworkAvailable(boolean networkUp) {
        ReflectMethod reflectMethod = this.setNetworkAvailablebooleanMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = Boolean.valueOf(networkUp);
        reflectMethod.invoke(objArr);
    }

    public Uri getRemoteDebuggingUrl() {
        return (Uri) this.getRemoteDebuggingUrlMethod.invoke(new Object[0]);
    }

    public boolean zoomIn() {
        return ((Boolean) this.zoomInMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean zoomOut() {
        return ((Boolean) this.zoomOutMethod.invoke(new Object[0])).booleanValue();
    }

    public void zoomBy(float factor) {
        ReflectMethod reflectMethod = this.zoomByfloatMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = Float.valueOf(factor);
        reflectMethod.invoke(objArr);
    }

    public boolean canZoomIn() {
        return ((Boolean) this.canZoomInMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean canZoomOut() {
        return ((Boolean) this.canZoomOutMethod.invoke(new Object[0])).booleanValue();
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        ReflectMethod reflectMethod = this.onCreateInputConnectionEditorInfoMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = outAttrs;
        return (InputConnection) reflectMethod.invoke(objArr);
    }

    public void setInitialScale(int scaleInPercent) {
        ReflectMethod reflectMethod = this.setInitialScaleintMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = Integer.valueOf(scaleInPercent);
        reflectMethod.invoke(objArr);
    }

    public void setZOrderOnTop(boolean onTop) {
        ReflectMethod reflectMethod = this.setZOrderOnTopbooleanMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = Boolean.valueOf(onTop);
        reflectMethod.invoke(objArr);
    }

    public void clearFormData() {
        this.clearFormDataMethod.invoke(new Object[0]);
    }

    public void setVisibility(int visibility) {
        if (visibility == 4) {
            visibility = 8;
        }
        super.setVisibility(visibility);
        setSurfaceViewVisibility(visibility);
    }

    public void setSurfaceViewVisibility(int visibility) {
        if (this.setSurfaceViewVisibilityintMethod.isNull()) {
            ReflectMethod reflectMethod = this.setSurfaceViewVisibilityintMethod;
            Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
            objArr[0] = Integer.valueOf(visibility);
            reflectMethod.setArguments(objArr);
            XWalkCoreWrapper.reserveReflectMethod(this.setSurfaceViewVisibilityintMethod);
            return;
        }
        reflectMethod = this.setSurfaceViewVisibilityintMethod;
        objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = Integer.valueOf(visibility);
        reflectMethod.invoke(objArr);
    }

    public void setDownloadListener(XWalkDownloadListener listener) {
        ReflectMethod reflectMethod = this.setDownloadListenerXWalkDownloadListenerInternalMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = listener.getBridge();
        reflectMethod.invoke(objArr);
    }

    private boolean performLongClickDelegate() {
        return performLongClick();
    }

    private boolean onTouchEventDelegate(MotionEvent event) {
        return onTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        ReflectMethod reflectMethod = this.onTouchEventMotionEventMethod;
        Object[] objArr = new Object[RELOAD_IGNORE_CACHE];
        objArr[0] = event;
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    private void onScrollChangedDelegate(int l, int t, int oldl, int oldt) {
        onScrollChanged(l, t, oldl, oldt);
    }

    private void onFocusChangedDelegate(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    void reflectionInit() {
        XWalkCoreWrapper.initEmbeddedMode();
        this.coreWrapper = XWalkCoreWrapper.getInstance();
        if (this.coreWrapper == null) {
            XWalkCoreWrapper.reserveReflectObject(this);
            return;
        }
        int length = this.constructorTypes.size();
        Class<?>[] paramTypes = new Class[(length + RELOAD_IGNORE_CACHE)];
        for (int i = 0; i < length; i += RELOAD_IGNORE_CACHE) {
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
        this.bridge = new ReflectConstructor(this.coreWrapper.getBridgeClass("XWalkViewBridge"), paramTypes).newInstance(this.constructorParams.toArray());
        if (this.postWrapperMethod != null) {
            this.postWrapperMethod.invoke(new Object[0]);
        }
        this.loadStringStringMethod.init(this.bridge, null, "loadSuper", String.class, String.class);
        this.loadAppFromManifestStringStringMethod.init(this.bridge, null, "loadAppFromManifestSuper", String.class, String.class);
        Class[] clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Integer.TYPE;
        this.reloadintMethod.init(this.bridge, null, "reloadSuper", clsArr);
        this.stopLoadingMethod.init(this.bridge, null, "stopLoadingSuper", new Class[0]);
        this.getUrlMethod.init(this.bridge, null, "getUrlSuper", new Class[0]);
        this.getTitleMethod.init(this.bridge, null, "getTitleSuper", new Class[0]);
        this.getOriginalUrlMethod.init(this.bridge, null, "getOriginalUrlSuper", new Class[0]);
        this.getNavigationHistoryMethod.init(this.bridge, null, "getNavigationHistorySuper", new Class[0]);
        this.addJavascriptInterfaceObjectStringMethod.init(this.bridge, null, "addJavascriptInterfaceSuper", Object.class, String.class);
        this.evaluateJavascriptStringValueCallbackMethod.init(this.bridge, null, "evaluateJavascriptSuper", String.class, ValueCallback.class);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Boolean.TYPE;
        this.clearCachebooleanMethod.init(this.bridge, null, "clearCacheSuper", clsArr);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = String.class;
        this.clearCacheForSingleFileStringMethod.init(this.bridge, null, "clearCacheForSingleFileSuper", clsArr);
        this.hasEnteredFullscreenMethod.init(this.bridge, null, "hasEnteredFullscreenSuper", new Class[0]);
        this.leaveFullscreenMethod.init(this.bridge, null, "leaveFullscreenSuper", new Class[0]);
        this.pauseTimersMethod.init(this.bridge, null, "pauseTimersSuper", new Class[0]);
        this.resumeTimersMethod.init(this.bridge, null, "resumeTimersSuper", new Class[0]);
        this.onHideMethod.init(this.bridge, null, "onHideSuper", new Class[0]);
        this.onShowMethod.init(this.bridge, null, "onShowSuper", new Class[0]);
        this.onDestroyMethod.init(this.bridge, null, "onDestroySuper", new Class[0]);
        this.onActivityResultintintIntentMethod.init(this.bridge, null, "onActivityResultSuper", Integer.TYPE, Integer.TYPE, Intent.class);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Intent.class;
        this.onNewIntentIntentMethod.init(this.bridge, null, "onNewIntentSuper", clsArr);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Bundle.class;
        this.saveStateBundleMethod.init(this.bridge, null, "saveStateSuper", clsArr);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Bundle.class;
        this.restoreStateBundleMethod.init(this.bridge, null, "restoreStateSuper", clsArr);
        this.getAPIVersionMethod.init(this.bridge, null, "getAPIVersionSuper", new Class[0]);
        this.getXWalkVersionMethod.init(this.bridge, null, "getXWalkVersionSuper", new Class[0]);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = this.coreWrapper.getBridgeClass("XWalkUIClientBridge");
        this.setUIClientXWalkUIClientInternalMethod.init(this.bridge, null, "setUIClientSuper", clsArr);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = this.coreWrapper.getBridgeClass("XWalkResourceClientBridge");
        this.setResourceClientXWalkResourceClientInternalMethod.init(this.bridge, null, "setResourceClientSuper", clsArr);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Integer.TYPE;
        this.setBackgroundColorintMethod.init(this.bridge, null, "setBackgroundColorSuper", clsArr);
        this.setLayerTypeintPaintMethod.init(this.bridge, null, "setLayerTypeSuper", Integer.TYPE, Paint.class);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = String.class;
        this.setUserAgentStringStringMethod.init(this.bridge, null, "setUserAgentStringSuper", clsArr);
        this.getUserAgentStringMethod.init(this.bridge, null, "getUserAgentStringSuper", new Class[0]);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = String.class;
        this.setAcceptLanguagesStringMethod.init(this.bridge, null, "setAcceptLanguagesSuper", clsArr);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Boolean.TYPE;
        this.setNetworkAvailablebooleanMethod.init(this.bridge, null, "setNetworkAvailableSuper", clsArr);
        this.getRemoteDebuggingUrlMethod.init(this.bridge, null, "getRemoteDebuggingUrlSuper", new Class[0]);
        this.zoomInMethod.init(this.bridge, null, "zoomInSuper", new Class[0]);
        this.zoomOutMethod.init(this.bridge, null, "zoomOutSuper", new Class[0]);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Float.TYPE;
        this.zoomByfloatMethod.init(this.bridge, null, "zoomBySuper", clsArr);
        this.canZoomInMethod.init(this.bridge, null, "canZoomInSuper", new Class[0]);
        this.canZoomOutMethod.init(this.bridge, null, "canZoomOutSuper", new Class[0]);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = EditorInfo.class;
        this.onCreateInputConnectionEditorInfoMethod.init(this.bridge, null, "onCreateInputConnectionSuper", clsArr);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Integer.TYPE;
        this.setInitialScaleintMethod.init(this.bridge, null, "setInitialScaleSuper", clsArr);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Boolean.TYPE;
        this.setZOrderOnTopbooleanMethod.init(this.bridge, null, "setZOrderOnTopSuper", clsArr);
        this.clearFormDataMethod.init(this.bridge, null, "clearFormDataSuper", new Class[0]);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Integer.TYPE;
        this.setVisibilityintMethod.init(this.bridge, null, "setVisibilitySuper", clsArr);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = Integer.TYPE;
        this.setSurfaceViewVisibilityintMethod.init(this.bridge, null, "setSurfaceViewVisibilitySuper", clsArr);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = this.coreWrapper.getBridgeClass("XWalkDownloadListenerBridge");
        this.setDownloadListenerXWalkDownloadListenerInternalMethod.init(this.bridge, null, "setDownloadListenerSuper", clsArr);
        clsArr = new Class[RELOAD_IGNORE_CACHE];
        clsArr[0] = MotionEvent.class;
        this.onTouchEventMotionEventMethod.init(this.bridge, null, "onTouchEventSuper", clsArr);
    }
}
