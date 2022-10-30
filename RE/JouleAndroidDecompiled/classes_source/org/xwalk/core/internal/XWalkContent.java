package org.xwalk.core.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.widget.FrameLayout.LayoutParams;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.ThreadUtils;
import org.chromium.blink_public.web.WebInputEventModifier;
import org.chromium.components.navigation_interception.InterceptNavigationDelegate;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content.browser.ContentViewRenderView;
import org.chromium.content.browser.ContentViewRenderView.CompositingSurfaceType;
import org.chromium.content.browser.ContentViewStatics;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.chromium.content.common.CleanupReference;
import org.chromium.content_public.browser.JavaScriptCallback;
import org.chromium.content_public.browser.LoadUrlParams;
import org.chromium.content_public.browser.NavigationController;
import org.chromium.content_public.browser.NavigationHistory;
import org.chromium.content_public.browser.WebContents;
import org.chromium.media.MediaPlayerBridge;
import org.chromium.ui.base.ActivityWindowAndroid;
import org.chromium.ui.base.PageTransition;
import org.chromium.ui.gfx.DeviceDisplayInfo;
import org.xwalk.core.internal.XWalkDevToolsServer.Security;
import org.xwalk.core.internal.XWalkGeolocationPermissions.Callback;

@JNINamespace("xwalk")
class XWalkContent implements KeyValueChangeListener {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final String SAVE_RESTORE_STATE_KEY = "XWALKVIEW_STATE";
    private static String TAG;
    private static Class<? extends Annotation> javascriptInterfaceClass;
    private static boolean timerPaused;
    private CleanupReference mCleanupReference;
    private XWalkContentView mContentView;
    private ContentViewCore mContentViewCore;
    private ContentViewRenderView mContentViewRenderView;
    private XWalkContentsClientBridge mContentsClientBridge;
    private double mDIPScale;
    private XWalkDevToolsServer mDevToolsServer;
    private XWalkGeolocationPermissions mGeolocationPermissions;
    private XWalkContentsIoThreadClient mIoThreadClient;
    private boolean mIsLoaded;
    private XWalkLaunchScreenManager mLaunchScreenManager;
    long mNativeContent;
    long mNativeWebContents;
    private NavigationController mNavigationController;
    private XWalkSettings mSettings;
    private Context mViewContext;
    private WebContents mWebContents;
    private ActivityWindowAndroid mWindow;
    private XWalkAutofillClient mXWalkAutofillClient;
    private XWalkWebContentsDelegateAdapter mXWalkContentsDelegateAdapter;
    private XWalkViewInternal mXWalkView;

    /* renamed from: org.xwalk.core.internal.XWalkContent.3 */
    class C04453 implements Runnable {
        final /* synthetic */ String val$url;

        C04453(String str) {
            this.val$url = str;
        }

        public void run() {
            XWalkContent.this.clearCacheForSingleFile(this.val$url);
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkContent.4 */
    class C04464 implements Runnable {
        final /* synthetic */ int val$color;

        C04464(int i) {
            this.val$color = i;
        }

        public void run() {
            XWalkContent.this.setBackgroundColor(this.val$color);
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkContent.5 */
    class C04475 implements Runnable {
        C04475() {
        }

        public void run() {
            XWalkContent.this.hideAutofillPopup();
        }
    }

    private static final class DestroyRunnable implements Runnable {
        private final long mNativeContent;

        private DestroyRunnable(long nativeXWalkContent) {
            this.mNativeContent = nativeXWalkContent;
        }

        public void run() {
            XWalkContent.nativeDestroy(this.mNativeContent);
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkContent.1 */
    class C06501 extends ContentViewRenderView {
        C06501(Context x0, CompositingSurfaceType x1) {
            super(x0, x1);
        }

        protected void onReadyToRender() {
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkContent.2 */
    class C06512 implements JavaScriptCallback {
        final /* synthetic */ ValueCallback val$fCallback;

        C06512(ValueCallback valueCallback) {
            this.val$fCallback = valueCallback;
        }

        public void handleJavaScriptResult(String jsonResult) {
            this.val$fCallback.onReceiveValue(jsonResult);
        }
    }

    private class XWalkGeolocationCallback implements Callback {

        /* renamed from: org.xwalk.core.internal.XWalkContent.XWalkGeolocationCallback.1 */
        class C04481 implements Runnable {
            final /* synthetic */ boolean val$allow;
            final /* synthetic */ String val$origin;
            final /* synthetic */ boolean val$retain;

            C04481(boolean z, boolean z2, String str) {
                this.val$retain = z;
                this.val$allow = z2;
                this.val$origin = str;
            }

            public void run() {
                if (this.val$retain) {
                    if (this.val$allow) {
                        XWalkContent.this.mGeolocationPermissions.allow(this.val$origin);
                    } else {
                        XWalkContent.this.mGeolocationPermissions.deny(this.val$origin);
                    }
                }
                XWalkContent.this.nativeInvokeGeolocationCallback(XWalkContent.this.mNativeContent, this.val$allow, this.val$origin);
            }
        }

        private XWalkGeolocationCallback() {
        }

        public void invoke(String origin, boolean allow, boolean retain) {
            ThreadUtils.runOnUiThread(new C04481(retain, allow, origin));
        }
    }

    private class XWalkIoThreadClientImpl implements XWalkContentsIoThreadClient {
        private XWalkIoThreadClientImpl() {
        }

        public int getCacheMode() {
            return XWalkContent.this.mSettings.getCacheMode();
        }

        public InterceptedRequestData shouldInterceptRequest(String url, boolean isMainFrame) {
            XWalkContent.this.mContentsClientBridge.getCallbackHelper().postOnResourceLoadStarted(url);
            WebResourceResponse webResourceResponse = XWalkContent.this.mContentsClientBridge.shouldInterceptRequest(url);
            if (webResourceResponse == null) {
                XWalkContent.this.mContentsClientBridge.getCallbackHelper().postOnLoadResource(url);
                return null;
            }
            if (isMainFrame && webResourceResponse.getData() == null) {
                XWalkContent.this.mContentsClientBridge.getCallbackHelper().postOnReceivedError(-1, null, url);
            }
            return new InterceptedRequestData(webResourceResponse.getMimeType(), webResourceResponse.getEncoding(), webResourceResponse.getData());
        }

        public boolean shouldBlockContentUrls() {
            return !XWalkContent.this.mSettings.getAllowContentAccess() ? true : XWalkContent.$assertionsDisabled;
        }

        public boolean shouldBlockFileUrls() {
            return !XWalkContent.this.mSettings.getAllowFileAccess() ? true : XWalkContent.$assertionsDisabled;
        }

        public boolean shouldBlockNetworkLoads() {
            return XWalkContent.this.mSettings.getBlockNetworkLoads();
        }

        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            XWalkContent.this.mContentsClientBridge.getCallbackHelper().postOnDownloadStart(url, userAgent, contentDisposition, mimeType, contentLength);
        }

        public void newLoginRequest(String realm, String account, String args) {
            XWalkContent.this.mContentsClientBridge.getCallbackHelper().postOnReceivedLoginRequest(realm, account, args);
        }
    }

    private native void nativeClearCache(long j, boolean z);

    private native void nativeClearCacheForSingleFile(long j, String str);

    private static native void nativeDestroy(long j);

    private native String nativeDevToolsAgentId(long j);

    private native int nativeGetRoutingID(long j);

    private native byte[] nativeGetState(long j);

    private native String nativeGetVersion(long j);

    private native WebContents nativeGetWebContents(long j);

    private native long nativeInit();

    private native void nativeInvokeGeolocationCallback(long j, boolean z, String str);

    private native long nativeReleasePopupXWalkContent(long j);

    private native void nativeSetBackgroundColor(long j, int i);

    private native void nativeSetJavaPeers(long j, XWalkContent xWalkContent, XWalkWebContentsDelegateAdapter xWalkWebContentsDelegateAdapter, XWalkContentsClientBridge xWalkContentsClientBridge, XWalkContentsIoThreadClient xWalkContentsIoThreadClient, InterceptNavigationDelegate interceptNavigationDelegate);

    private native void nativeSetJsOnlineProperty(long j, boolean z);

    private native boolean nativeSetManifest(long j, String str, String str2);

    private native boolean nativeSetState(long j, byte[] bArr);

    static {
        $assertionsDisabled = !XWalkContent.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        TAG = "XWalkContent";
        javascriptInterfaceClass = null;
        timerPaused = $assertionsDisabled;
    }

    static void setJavascriptInterfaceClass(Class<? extends Annotation> clazz) {
        if ($assertionsDisabled || javascriptInterfaceClass == null) {
            javascriptInterfaceClass = clazz;
            return;
        }
        throw new AssertionError();
    }

    public XWalkContent(Context context, AttributeSet attrs, XWalkViewInternal xwView) {
        this.mIsLoaded = $assertionsDisabled;
        this.mXWalkView = xwView;
        this.mViewContext = this.mXWalkView.getContext();
        this.mContentsClientBridge = new XWalkContentsClientBridge(this.mXWalkView);
        this.mXWalkContentsDelegateAdapter = new XWalkWebContentsDelegateAdapter(this.mContentsClientBridge);
        this.mIoThreadClient = new XWalkIoThreadClientImpl();
        this.mWindow = new ActivityWindowAndroid(xwView.getActivity());
        this.mGeolocationPermissions = new XWalkGeolocationPermissions(new InMemorySharedPreferences());
        MediaPlayerBridge.setResourceLoadingFilter(new XWalkMediaPlayerResourceLoadingFilter());
        setNativeContent(nativeInit());
        XWalkPreferencesInternal.load(this);
    }

    private void setNativeContent(long newNativeContent) {
        if (this.mNativeContent != 0) {
            destroy();
            this.mContentViewCore = null;
        }
        if ($assertionsDisabled || (this.mNativeContent == 0 && this.mCleanupReference == null && this.mContentViewCore == null)) {
            this.mContentViewRenderView = new C06501(this.mViewContext, XWalkPreferencesInternal.getValue(XWalkPreferencesInternal.ANIMATABLE_XWALK_VIEW) ? CompositingSurfaceType.TEXTURE_VIEW : CompositingSurfaceType.SURFACE_VIEW);
            this.mContentViewRenderView.onNativeLibraryLoaded(this.mWindow);
            this.mLaunchScreenManager = new XWalkLaunchScreenManager(this.mViewContext, this.mXWalkView);
            this.mContentViewRenderView.registerFirstRenderedFrameListener(this.mLaunchScreenManager);
            this.mXWalkView.addView(this.mContentViewRenderView, new LayoutParams(-1, -1));
            this.mNativeContent = newNativeContent;
            this.mCleanupReference = new CleanupReference(this, new DestroyRunnable(null));
            WebContents webContents = nativeGetWebContents(this.mNativeContent);
            this.mContentViewCore = new ContentViewCore(this.mViewContext);
            this.mContentView = new XWalkContentView(this.mViewContext, this.mContentViewCore, this.mXWalkView);
            this.mContentViewCore.initialize(this.mContentView, this.mContentView, webContents, this.mWindow);
            this.mWebContents = this.mContentViewCore.getWebContents();
            this.mNavigationController = this.mWebContents.getNavigationController();
            this.mXWalkView.addView(this.mContentView, new LayoutParams(-1, -1));
            this.mContentViewCore.setContentViewClient(this.mContentsClientBridge);
            this.mContentViewRenderView.setCurrentContentViewCore(this.mContentViewCore);
            this.mContentsClientBridge.installWebContentsObserver(this.mWebContents);
            this.mSettings = new XWalkSettings(this.mViewContext, webContents, $assertionsDisabled);
            this.mSettings.setAllowFileAccessFromFileURLs(true);
            this.mDIPScale = DeviceDisplayInfo.create(this.mViewContext).getDIPScale();
            this.mContentsClientBridge.setDIPScale(this.mDIPScale);
            this.mSettings.setDIPScale(this.mDIPScale);
            this.mContentViewCore.setDownloadDelegate(this.mContentsClientBridge);
            String language = Locale.getDefault().toString().replaceAll("_", "-").toLowerCase();
            if (language.isEmpty()) {
                language = "en";
            }
            this.mSettings.setAcceptLanguages(language);
            nativeSetJavaPeers(this.mNativeContent, this, this.mXWalkContentsDelegateAdapter, this.mContentsClientBridge, this.mIoThreadClient, this.mContentsClientBridge.getInterceptNavigationDelegate());
            return;
        }
        throw new AssertionError();
    }

    public void supplyContentsForPopup(XWalkContent newContents) {
        if (this.mNativeContent != 0) {
            long popupNativeXWalkContent = nativeReleasePopupXWalkContent(this.mNativeContent);
            if (popupNativeXWalkContent == 0) {
                Log.w(TAG, "Popup XWalkView bind failed: no pending content.");
                if (newContents != null) {
                    newContents.destroy();
                }
            } else if (newContents == null) {
                nativeDestroy(popupNativeXWalkContent);
            } else {
                newContents.receivePopupContents(popupNativeXWalkContent);
            }
        }
    }

    private void receivePopupContents(long popupNativeXWalkContents) {
        setNativeContent(popupNativeXWalkContents);
        this.mContentViewCore.onShow();
    }

    void doLoadUrl(String url, String content) {
        if (url == null || url.isEmpty() || !TextUtils.equals(url, this.mWebContents.getUrl())) {
            LoadUrlParams params;
            if (content == null || content.isEmpty()) {
                params = new LoadUrlParams(url);
            } else {
                try {
                    params = LoadUrlParams.createLoadDataParamsWithBaseUrl(Base64.encodeToString(content.getBytes("utf-8"), 0), "text/html", true, url, null, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.w(TAG, "Unable to load data string " + content, e);
                    return;
                }
            }
            params.setOverrideUserAgent(2);
            this.mNavigationController.loadUrl(params);
        } else {
            this.mNavigationController.reload(true);
        }
        this.mContentView.requestFocus();
    }

    public void loadUrl(String url, String data) {
        if (this.mNativeContent != 0) {
            if ((url != null && !url.isEmpty()) || (data != null && !data.isEmpty())) {
                doLoadUrl(url, data);
                this.mIsLoaded = true;
            }
        }
    }

    public void reload(int mode) {
        if (this.mNativeContent != 0) {
            switch (mode) {
                case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                    this.mNavigationController.reloadIgnoringCache(true);
                    break;
                default:
                    this.mNavigationController.reload(true);
                    break;
            }
            this.mIsLoaded = true;
        }
    }

    public String getUrl() {
        if (this.mNativeContent == 0) {
            return null;
        }
        String url = this.mWebContents.getUrl();
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        return url;
    }

    public String getTitle() {
        if (this.mNativeContent == 0) {
            return null;
        }
        String title = this.mWebContents.getTitle().trim();
        if (title == null) {
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
        return title;
    }

    public void addJavascriptInterface(Object object, String name) {
        if (this.mNativeContent != 0) {
            this.mContentViewCore.addPossiblyUnsafeJavascriptInterface(object, name, javascriptInterfaceClass);
        }
    }

    public void evaluateJavascript(String script, ValueCallback<String> callback) {
        if (this.mNativeContent != 0) {
            ValueCallback<String> fCallback = callback;
            JavaScriptCallback coreCallback = null;
            if (fCallback != null) {
                coreCallback = new C06512(fCallback);
            }
            this.mContentViewCore.getWebContents().evaluateJavaScript(script, coreCallback);
        }
    }

    public void setUIClient(XWalkUIClientInternal client) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setUIClient(client);
        }
    }

    public void setResourceClient(XWalkResourceClientInternal client) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setResourceClient(client);
        }
    }

    public void setXWalkWebChromeClient(XWalkWebChromeClient client) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setXWalkWebChromeClient(client);
        }
    }

    public XWalkWebChromeClient getXWalkWebChromeClient() {
        if (this.mNativeContent == 0) {
            return null;
        }
        return this.mContentsClientBridge.getXWalkWebChromeClient();
    }

    public void setXWalkClient(XWalkClient client) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setXWalkClient(client);
        }
    }

    public void setDownloadListener(XWalkDownloadListenerInternal listener) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setDownloadListener(listener);
        }
    }

    public void setNavigationHandler(XWalkNavigationHandler handler) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setNavigationHandler(handler);
        }
    }

    public void setNotificationService(XWalkNotificationService service) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setNotificationService(service);
        }
    }

    public void onPause() {
        if (this.mNativeContent != 0) {
            this.mContentViewCore.onHide();
        }
    }

    public void onResume() {
        if (this.mNativeContent != 0) {
            this.mContentViewCore.onShow();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.mNativeContent != 0) {
            this.mWindow.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean onNewIntent(Intent intent) {
        if (this.mNativeContent == 0) {
            return $assertionsDisabled;
        }
        return this.mContentsClientBridge.onNewIntent(intent);
    }

    public void clearCache(boolean includeDiskFiles) {
        if (this.mNativeContent != 0) {
            nativeClearCache(this.mNativeContent, includeDiskFiles);
        }
    }

    public void clearCacheForSingleFile(String url) {
        if (this.mNativeContent != 0) {
            if (this.mIsLoaded) {
                nativeClearCacheForSingleFile(this.mNativeContent, url);
            } else {
                this.mXWalkView.post(new C04453(url));
            }
        }
    }

    public void clearHistory() {
        if (this.mNativeContent != 0) {
            this.mNavigationController.clearHistory();
        }
    }

    public boolean canGoBack() {
        return this.mNativeContent == 0 ? $assertionsDisabled : this.mNavigationController.canGoBack();
    }

    public void goBack() {
        if (this.mNativeContent != 0) {
            this.mNavigationController.goBack();
        }
    }

    public boolean canGoForward() {
        return this.mNativeContent == 0 ? $assertionsDisabled : this.mNavigationController.canGoForward();
    }

    public void goForward() {
        if (this.mNativeContent != 0) {
            this.mNavigationController.goForward();
        }
    }

    void navigateTo(int offset) {
        this.mNavigationController.goToOffset(offset);
    }

    public void stopLoading() {
        if (this.mNativeContent != 0) {
            this.mWebContents.stop();
            this.mContentsClientBridge.onStopLoading();
        }
    }

    public void pauseTimers() {
        if (!timerPaused && this.mNativeContent != 0) {
            ContentViewStatics.setWebKitSharedTimersSuspended(true);
            timerPaused = true;
        }
    }

    public void resumeTimers() {
        if (timerPaused && this.mNativeContent != 0) {
            ContentViewStatics.setWebKitSharedTimersSuspended($assertionsDisabled);
            timerPaused = $assertionsDisabled;
        }
    }

    public String getOriginalUrl() {
        if (this.mNativeContent == 0) {
            return null;
        }
        NavigationHistory history = this.mNavigationController.getNavigationHistory();
        int currentIndex = history.getCurrentEntryIndex();
        if (currentIndex < 0 || currentIndex >= history.getEntryCount()) {
            return null;
        }
        return history.getEntryAtIndex(currentIndex).getOriginalUrl();
    }

    public String getXWalkVersion() {
        if (this.mNativeContent == 0) {
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
        return nativeGetVersion(this.mNativeContent);
    }

    private boolean isOpaque(int color) {
        return ((color >> 24) & PageTransition.CORE_MASK) == PageTransition.CORE_MASK ? true : $assertionsDisabled;
    }

    @CalledByNative
    public void setBackgroundColor(int color) {
        if (this.mNativeContent != 0) {
            if (this.mIsLoaded) {
                if (!isOpaque(color)) {
                    setOverlayVideoMode(true);
                    this.mContentViewRenderView.setSurfaceViewBackgroundColor(color);
                    this.mContentViewCore.setBackgroundOpaque($assertionsDisabled);
                }
                nativeSetBackgroundColor(this.mNativeContent, color);
                return;
            }
            this.mXWalkView.post(new C04464(color));
        }
    }

    public void setNetworkAvailable(boolean networkUp) {
        if (this.mNativeContent != 0) {
            nativeSetJsOnlineProperty(this.mNativeContent, networkUp);
        }
    }

    public ContentViewCore getContentViewCoreForTest() {
        return this.mContentViewCore;
    }

    public void installWebContentsObserverForTest(XWalkContentsClient contentClient) {
        if (this.mNativeContent != 0) {
            contentClient.installWebContentsObserver(this.mContentViewCore.getWebContents());
        }
    }

    public String devToolsAgentId() {
        if (this.mNativeContent == 0) {
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
        return nativeDevToolsAgentId(this.mNativeContent);
    }

    public XWalkSettings getSettings() {
        return this.mSettings;
    }

    public void loadAppFromManifest(String url, String data) {
        if (this.mNativeContent == 0) {
            return;
        }
        if ((url != null && !url.isEmpty()) || (data != null && !data.isEmpty())) {
            String content = data;
            if (data == null || data.isEmpty()) {
                try {
                    content = AndroidProtocolHandler.getUrlContent(this.mXWalkView.getActivity(), url);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read the manifest: " + url);
                }
            }
            String baseUrl = url;
            int position = url.lastIndexOf("/");
            if (position != -1) {
                baseUrl = url.substring(0, position + 1);
            } else {
                Log.w(TAG, "The url of manifest.json is probably not set correctly.");
            }
            if (nativeSetManifest(this.mNativeContent, baseUrl, content)) {
                this.mIsLoaded = true;
                return;
            }
            throw new RuntimeException("Failed to parse the manifest file: " + url);
        }
    }

    public XWalkNavigationHistoryInternal getNavigationHistory() {
        if (this.mNativeContent == 0) {
            return null;
        }
        return new XWalkNavigationHistoryInternal(this.mXWalkView, this.mNavigationController.getNavigationHistory());
    }

    public XWalkNavigationHistoryInternal saveState(Bundle outState) {
        if (this.mNativeContent == 0 || outState == null) {
            return null;
        }
        byte[] state = nativeGetState(this.mNativeContent);
        if (state == null) {
            return null;
        }
        outState.putByteArray(SAVE_RESTORE_STATE_KEY, state);
        return getNavigationHistory();
    }

    public XWalkNavigationHistoryInternal restoreState(Bundle inState) {
        if (this.mNativeContent == 0 || inState == null) {
            return null;
        }
        byte[] state = inState.getByteArray(SAVE_RESTORE_STATE_KEY);
        if (state == null) {
            return null;
        }
        boolean result = nativeSetState(this.mNativeContent, state);
        if (result) {
            this.mContentsClientBridge.onUpdateTitle(this.mWebContents.getTitle());
        }
        if (result) {
            return getNavigationHistory();
        }
        return null;
    }

    boolean hasEnteredFullscreen() {
        return this.mContentsClientBridge.hasEnteredFullscreen();
    }

    void exitFullscreen() {
        if (hasEnteredFullscreen()) {
            this.mContentsClientBridge.exitFullscreen(this.mNativeWebContents);
        }
    }

    @CalledByNative
    public void onGetUrlFromManifest(String url) {
        if (url != null && !url.isEmpty()) {
            loadUrl(url, null);
        }
    }

    @CalledByNative
    public void onGetUrlAndLaunchScreenFromManifest(String url, String readyWhen, String imageBorder) {
        if (url != null && !url.isEmpty()) {
            this.mLaunchScreenManager.displayLaunchScreen(readyWhen, imageBorder);
            this.mContentsClientBridge.registerPageLoadListener(this.mLaunchScreenManager);
            loadUrl(url, null);
        }
    }

    @CalledByNative
    public void onGetFullscreenFlagFromManifest(boolean enterFullscreen) {
        if (!enterFullscreen) {
            return;
        }
        if (VERSION.SDK_INT >= 19) {
            this.mXWalkView.getActivity().getWindow().getDecorView().setSystemUiVisibility(5894);
        } else {
            this.mXWalkView.getActivity().getWindow().addFlags(WebInputEventModifier.NumLockOn);
        }
    }

    public void destroy() {
        if (this.mNativeContent != 0) {
            XWalkPreferencesInternal.unload(this);
            setNotificationService(null);
            this.mXWalkView.removeView(this.mContentView);
            this.mXWalkView.removeView(this.mContentViewRenderView);
            this.mContentViewRenderView.setCurrentContentViewCore(null);
            this.mContentViewRenderView.destroy();
            this.mContentViewCore.destroy();
            this.mCleanupReference.cleanupNow();
            this.mCleanupReference = null;
            this.mNativeContent = 0;
        }
    }

    public int getRoutingID() {
        return nativeGetRoutingID(this.mNativeContent);
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.mContentView.onCreateInputConnectionSuper(outAttrs);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.mContentViewCore.onTouchEvent(event);
    }

    @CalledByNative
    private void onGeolocationPermissionsShowPrompt(String origin) {
        if (this.mNativeContent != 0) {
            if (!this.mSettings.getGeolocationEnabled()) {
                nativeInvokeGeolocationCallback(this.mNativeContent, $assertionsDisabled, origin);
            } else if (this.mGeolocationPermissions.hasOrigin(origin)) {
                nativeInvokeGeolocationCallback(this.mNativeContent, this.mGeolocationPermissions.isOriginAllowed(origin), origin);
            } else {
                this.mContentsClientBridge.onGeolocationPermissionsShowPrompt(origin, new XWalkGeolocationCallback());
            }
        }
    }

    @CalledByNative
    public void onGeolocationPermissionsHidePrompt() {
        this.mContentsClientBridge.onGeolocationPermissionsHidePrompt();
    }

    public void enableRemoteDebugging() {
        String socketName = this.mViewContext.getApplicationContext().getPackageName() + "_devtools_remote";
        if (this.mDevToolsServer == null) {
            this.mDevToolsServer = new XWalkDevToolsServer(socketName);
            this.mDevToolsServer.setRemoteDebuggingEnabled(true, Security.ALLOW_SOCKET_ACCESS);
        }
    }

    void disableRemoteDebugging() {
        if (this.mDevToolsServer != null) {
            if (this.mDevToolsServer.isRemoteDebuggingEnabled()) {
                this.mDevToolsServer.setRemoteDebuggingEnabled($assertionsDisabled);
            }
            this.mDevToolsServer.destroy();
            this.mDevToolsServer = null;
        }
    }

    public String getRemoteDebuggingUrl() {
        if (this.mDevToolsServer == null) {
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
        return "ws://" + this.mDevToolsServer.getSocketName() + "/devtools/page/" + devToolsAgentId();
    }

    public void onKeyValueChanged(String key, PreferenceValue value) {
        if (key != null) {
            if (key.equals(XWalkPreferencesInternal.REMOTE_DEBUGGING)) {
                if (value.getBooleanValue()) {
                    enableRemoteDebugging();
                } else {
                    disableRemoteDebugging();
                }
            } else if (key.equals("enable-javascript")) {
                if (this.mSettings != null) {
                    this.mSettings.setJavaScriptEnabled(value.getBooleanValue());
                }
            } else if (key.equals(XWalkPreferencesInternal.JAVASCRIPT_CAN_OPEN_WINDOW)) {
                if (this.mSettings != null) {
                    this.mSettings.setJavaScriptCanOpenWindowsAutomatically(value.getBooleanValue());
                }
            } else if (key.equals(XWalkPreferencesInternal.ALLOW_UNIVERSAL_ACCESS_FROM_FILE)) {
                if (this.mSettings != null) {
                    this.mSettings.setAllowUniversalAccessFromFileURLs(value.getBooleanValue());
                }
            } else if (key.equals(XWalkPreferencesInternal.SUPPORT_MULTIPLE_WINDOWS) && this.mSettings != null) {
                this.mSettings.setSupportMultipleWindows(value.getBooleanValue());
            }
        }
    }

    public void setOverlayVideoMode(boolean enabled) {
        if (this.mContentViewRenderView != null) {
            this.mContentViewRenderView.setOverlayVideoMode(enabled);
        }
    }

    public void setZOrderOnTop(boolean onTop) {
        if (this.mContentViewRenderView != null) {
            this.mContentViewRenderView.setZOrderOnTop(onTop);
        }
    }

    public boolean zoomIn() {
        if (this.mNativeContent == 0) {
            return $assertionsDisabled;
        }
        return this.mContentViewCore.zoomIn();
    }

    public boolean zoomOut() {
        if (this.mNativeContent == 0) {
            return $assertionsDisabled;
        }
        return this.mContentViewCore.zoomOut();
    }

    public void zoomBy(float delta) {
        if (this.mNativeContent != 0) {
            if (delta < 0.01f || delta > 100.0f) {
                throw new IllegalStateException("zoom delta value outside [0.01, 100] range.");
            }
            this.mContentViewCore.pinchByDelta(delta);
        }
    }

    public boolean canZoomIn() {
        if (this.mNativeContent == 0) {
            return $assertionsDisabled;
        }
        return this.mContentViewCore.canZoomIn();
    }

    public boolean canZoomOut() {
        if (this.mNativeContent == 0) {
            return $assertionsDisabled;
        }
        return this.mContentViewCore.canZoomOut();
    }

    public void hideAutofillPopup() {
        if (this.mNativeContent != 0) {
            if (!this.mIsLoaded) {
                this.mXWalkView.post(new C04475());
            } else if (this.mXWalkAutofillClient != null) {
                this.mXWalkAutofillClient.hideAutofillPopup();
            }
        }
    }

    public void setVisibility(int visibility) {
        SurfaceView surfaceView = this.mContentViewRenderView.getSurfaceView();
        if (surfaceView != null) {
            surfaceView.setVisibility(visibility);
        }
    }

    @CalledByNative
    private void setXWalkAutofillClient(XWalkAutofillClient client) {
        this.mXWalkAutofillClient = client;
        client.init(this.mContentViewCore);
    }
}
