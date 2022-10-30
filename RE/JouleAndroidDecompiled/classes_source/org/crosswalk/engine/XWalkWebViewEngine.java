package org.crosswalk.engine;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.View;
import org.apache.cordova.CordovaBridge;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.CordovaWebViewEngine.Client;
import org.apache.cordova.ICordovaCookieManager;
import org.apache.cordova.NativeToJsMessageQueue;
import org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode;
import org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode.OnlineEventsBridgeModeDelegate;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.PluginManager;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.xwalk.core.XWalkActivityDelegate;
import org.xwalk.core.XWalkNavigationHistory.Direction;
import org.xwalk.core.XWalkView;

public class XWalkWebViewEngine implements CordovaWebViewEngine {
    public static final String TAG = "XWalkWebViewEngine";
    public static final String XWALK_USER_AGENT = "xwalkUserAgent";
    public static final String XWALK_Z_ORDER_ON_TOP = "xwalkZOrderOnTop";
    protected XWalkActivityDelegate activityDelegate;
    protected CordovaBridge bridge;
    protected Client client;
    protected XWalkCordovaCookieManager cookieManager;
    protected CordovaInterface cordova;
    protected NativeToJsMessageQueue nativeToJsMessageQueue;
    protected CordovaWebView parentWebView;
    protected PluginManager pluginManager;
    protected CordovaPreferences preferences;
    protected CordovaResourceApi resourceApi;
    protected String startUrl;
    protected final XWalkCordovaView webView;

    /* renamed from: org.crosswalk.engine.XWalkWebViewEngine.1 */
    class C04281 implements Runnable {
        C04281() {
        }

        public void run() {
            XWalkWebViewEngine.this.cordova.getActivity().finish();
        }
    }

    /* renamed from: org.crosswalk.engine.XWalkWebViewEngine.2 */
    class C04292 implements Runnable {
        C04292() {
        }

        public void run() {
            XWalkWebViewEngine.this.cookieManager = new XWalkCordovaCookieManager();
            XWalkWebViewEngine.this.initWebViewSettings();
            XWalkWebViewEngine.exposeJsInterface(XWalkWebViewEngine.this.webView, XWalkWebViewEngine.this.bridge);
            XWalkWebViewEngine.this.loadUrl(XWalkWebViewEngine.this.startUrl, true);
            if (XWalkWebViewEngine.this.pluginManager != null) {
                XWalkWebViewEngine.this.pluginManager.postMessage("onXWalkReady", this);
            }
        }
    }

    /* renamed from: org.crosswalk.engine.XWalkWebViewEngine.3 */
    class C06473 implements OnlineEventsBridgeModeDelegate {
        C06473() {
        }

        public void setNetworkAvailable(boolean value) {
            XWalkWebViewEngine.this.webView.setNetworkAvailable(value);
        }

        public void runOnUiThread(Runnable r) {
            XWalkWebViewEngine.this.cordova.getActivity().runOnUiThread(r);
        }
    }

    /* renamed from: org.crosswalk.engine.XWalkWebViewEngine.4 */
    class C06484 extends CordovaPlugin {
        C06484() {
        }

        public void onResume(boolean multitasking) {
            XWalkWebViewEngine.this.activityDelegate.onResume();
        }
    }

    public XWalkWebViewEngine(Context context, CordovaPreferences preferences) {
        this.preferences = preferences;
        this.activityDelegate = new XWalkActivityDelegate((Activity) context, new C04281(), new C04292());
        this.webView = new XWalkCordovaView(context, preferences);
    }

    public void init(CordovaWebView parentWebView, CordovaInterface cordova, Client client, CordovaResourceApi resourceApi, PluginManager pluginManager, NativeToJsMessageQueue nativeToJsMessageQueue) {
        if (this.cordova != null) {
            throw new IllegalStateException();
        }
        this.parentWebView = parentWebView;
        this.cordova = cordova;
        this.client = client;
        this.resourceApi = resourceApi;
        this.pluginManager = pluginManager;
        this.nativeToJsMessageQueue = nativeToJsMessageQueue;
        this.webView.init(this);
        nativeToJsMessageQueue.addBridgeMode(new OnlineEventsBridgeMode(new C06473()));
        this.bridge = new CordovaBridge(pluginManager, nativeToJsMessageQueue);
    }

    public CordovaWebView getCordovaWebView() {
        return this.parentWebView;
    }

    public View getView() {
        return this.webView;
    }

    private void initWebViewSettings() {
        boolean zOrderOnTop = false;
        this.webView.setVerticalScrollBarEnabled(false);
        if (this.preferences != null) {
            zOrderOnTop = this.preferences.getBoolean(XWALK_Z_ORDER_ON_TOP, false);
        }
        this.webView.setZOrderOnTop(zOrderOnTop);
        String xwalkUserAgent = this.preferences == null ? CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE : this.preferences.getString(XWALK_USER_AGENT, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
        if (!xwalkUserAgent.isEmpty()) {
            this.webView.setUserAgentString(xwalkUserAgent);
        }
        if (this.preferences.contains("BackgroundColor")) {
            this.webView.setBackgroundColor(this.preferences.getInteger("BackgroundColor", ViewCompat.MEASURED_STATE_MASK));
        }
    }

    private static void exposeJsInterface(XWalkView webView, CordovaBridge bridge) {
        webView.addJavascriptInterface(new XWalkExposedJsApi(bridge), "_cordovaNative");
    }

    public boolean canGoBack() {
        if (this.activityDelegate.isXWalkReady()) {
            return this.webView.getNavigationHistory().canGoBack();
        }
        return false;
    }

    public boolean goBack() {
        if (!this.webView.getNavigationHistory().canGoBack()) {
            return false;
        }
        this.webView.getNavigationHistory().navigate(Direction.BACKWARD, 1);
        return true;
    }

    public void setPaused(boolean value) {
        if (!this.activityDelegate.isXWalkReady()) {
            return;
        }
        if (value) {
            this.webView.pauseTimersForReal();
        } else {
            this.webView.resumeTimers();
        }
    }

    public void destroy() {
        if (this.activityDelegate.isXWalkReady()) {
            this.webView.onDestroy();
        }
    }

    public void clearHistory() {
        if (this.activityDelegate.isXWalkReady()) {
            this.webView.getNavigationHistory().clear();
        }
    }

    public void stopLoading() {
        if (this.activityDelegate.isXWalkReady()) {
            this.webView.stopLoading();
        }
    }

    public void clearCache() {
        if (this.activityDelegate.isXWalkReady()) {
            this.webView.clearCache(true);
        }
    }

    public String getUrl() {
        if (this.activityDelegate.isXWalkReady()) {
            return this.webView.getUrl();
        }
        return null;
    }

    public ICordovaCookieManager getCookieManager() {
        return this.cookieManager;
    }

    public void loadUrl(String url, boolean clearNavigationStack) {
        if (this.activityDelegate.isXWalkReady()) {
            this.webView.load(url, null);
            return;
        }
        this.startUrl = url;
        this.pluginManager.addService(new PluginEntry("XWalkInit", new C06484()));
    }

    public boolean isXWalkReady() {
        return this.activityDelegate.isXWalkReady();
    }
}
