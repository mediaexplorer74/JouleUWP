package org.xwalk.core.internal;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ConsoleMessage.MessageLevel;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import com.google.android.gms.common.ConnectionResult;
import java.security.Principal;
import javax.security.auth.x500.X500Principal;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.components.navigation_interception.InterceptNavigationDelegate;
import org.chromium.components.navigation_interception.NavigationParams;
import org.chromium.content.browser.ContentVideoViewClient;
import org.chromium.content.browser.ContentViewDownloadDelegate;
import org.chromium.content.browser.DownloadInfo;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.chromium.net.AndroidPrivateKey;
import org.chromium.net.DefaultAndroidKeyStore;
import org.xwalk.core.internal.ClientCertLookupTable.Cert;
import org.xwalk.core.internal.XWalkGeolocationPermissions.Callback;
import org.xwalk.core.internal.XWalkUIClientInternal.ConsoleMessageType;
import org.xwalk.core.internal.XWalkUIClientInternal.InitiateByInternal;
import org.xwalk.core.internal.XWalkUIClientInternal.JavascriptMessageTypeInternal;
import org.xwalk.core.internal.XWalkUIClientInternal.LoadStatusInternal;
import org.xwalk.core.internal.XWalkWebChromeClient.CustomViewCallback;

@JNINamespace("xwalk")
class XWalkContentsClientBridge extends XWalkContentsClient implements ContentViewDownloadDelegate {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int NEW_ICON_DOWNLOAD = 101;
    private static final int NEW_XWALKVIEW_CREATED = 100;
    private static final String TAG;
    private XWalkDownloadListenerInternal mDownloadListener;
    private Bitmap mFavicon;
    private InterceptNavigationDelegate mInterceptNavigationDelegate;
    private boolean mIsFullscreen;
    private LoadStatusInternal mLoadStatus;
    private String mLoadingUrl;
    protected long mNativeContentsClientBridge;
    private XWalkNavigationHandler mNavigationHandler;
    private XWalkNotificationService mNotificationService;
    private PageLoadListener mPageLoadListener;
    private float mPageScaleFactor;
    private Handler mUiThreadHandler;
    private XWalkClient mXWalkClient;
    private XWalkResourceClientInternal mXWalkResourceClient;
    private XWalkUIClientInternal mXWalkUIClient;
    private XWalkViewInternal mXWalkView;
    private XWalkWebChromeClient mXWalkWebChromeClient;

    /* renamed from: org.xwalk.core.internal.XWalkContentsClientBridge.1 */
    class C04491 extends Handler {
        C04491() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case XWalkContentsClientBridge.NEW_XWALKVIEW_CREATED /*100*/:
                    XWalkViewInternal newXWalkView = msg.obj;
                    if (newXWalkView == XWalkContentsClientBridge.this.mXWalkView) {
                        throw new IllegalArgumentException("Parent XWalkView cannot host it's own popup window");
                    } else if (newXWalkView == null || newXWalkView.getNavigationHistory().size() == 0) {
                        XWalkContentsClientBridge.this.mXWalkView.completeWindowCreation(newXWalkView);
                    } else {
                        throw new IllegalArgumentException("New WebView for popup window must not have been previously navigated.");
                    }
                case XWalkContentsClientBridge.NEW_ICON_DOWNLOAD /*101*/:
                    XWalkContentsClientBridge.this.nativeDownloadIcon(XWalkContentsClientBridge.this.mNativeContentsClientBridge, msg.obj);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkContentsClientBridge.2 */
    class C04502 implements ValueCallback<XWalkViewInternal> {
        C04502() {
        }

        public void onReceiveValue(XWalkViewInternal newXWalkView) {
            XWalkContentsClientBridge.this.mUiThreadHandler.obtainMessage(XWalkContentsClientBridge.NEW_XWALKVIEW_CREATED, newXWalkView).sendToTarget();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkContentsClientBridge.4 */
    class C04514 implements ValueCallback<Boolean> {
        final /* synthetic */ int val$id;

        C04514(int i) {
            this.val$id = i;
        }

        public void onReceiveValue(Boolean value) {
            XWalkContentsClientBridge.this.proceedSslError(value.booleanValue(), this.val$id);
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkContentsClientBridge.5 */
    static /* synthetic */ class C04525 {
        static final /* synthetic */ int[] $SwitchMap$android$webkit$ConsoleMessage$MessageLevel;

        static {
            $SwitchMap$android$webkit$ConsoleMessage$MessageLevel = new int[MessageLevel.values().length];
            try {
                $SwitchMap$android$webkit$ConsoleMessage$MessageLevel[MessageLevel.TIP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$webkit$ConsoleMessage$MessageLevel[MessageLevel.LOG.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$webkit$ConsoleMessage$MessageLevel[MessageLevel.WARNING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$webkit$ConsoleMessage$MessageLevel[MessageLevel.ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkContentsClientBridge.3 */
    class C06523 extends AnonymousClass1UriCallback {
        boolean completed;
        final /* synthetic */ int val$modeFlags;
        final /* synthetic */ int val$processId;
        final /* synthetic */ int val$renderId;

        C06523(int i, int i2, int i3) {
            this.val$processId = i;
            this.val$renderId = i2;
            this.val$modeFlags = i3;
            new ValueCallback<Uri>() {
                boolean syncCallFinished;
                boolean syncNullReceived;

                {
                    this.syncNullReceived = XWalkContentsClientBridge.$assertionsDisabled;
                    this.syncCallFinished = XWalkContentsClientBridge.$assertionsDisabled;
                }

                protected String resolveFileName(Uri uri, ContentResolver contentResolver) {
                    String string;
                    if (contentResolver == null || uri == null) {
                        return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
                    }
                    Cursor cursor = null;
                    try {
                        cursor = contentResolver.query(uri, null, null, null, null);
                        if (cursor != null && cursor.getCount() >= 1) {
                            cursor.moveToFirst();
                            int index = cursor.getColumnIndex("_display_name");
                            if (index > -1) {
                                string = cursor.getString(index);
                                if (cursor == null) {
                                    return string;
                                }
                                cursor.close();
                                return string;
                            }
                        }
                        if (cursor != null) {
                            cursor.close();
                        }
                        return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
                    } catch (NullPointerException e) {
                        string = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
                        if (cursor == null) {
                            return string;
                        }
                        cursor.close();
                        return string;
                    } catch (Throwable th) {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            };
            this.completed = XWalkContentsClientBridge.$assertionsDisabled;
        }

        public void onReceiveValue(Uri value) {
            if (this.completed) {
                throw new IllegalStateException("Duplicate openFileChooser result");
            } else if (value != null || this.syncCallFinished) {
                this.completed = true;
                if (value == null) {
                    XWalkContentsClientBridge.this.nativeOnFilesNotSelected(XWalkContentsClientBridge.this.mNativeContentsClientBridge, this.val$processId, this.val$renderId, this.val$modeFlags);
                    return;
                }
                String displayName;
                String result = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
                if (AndroidProtocolHandler.FILE_SCHEME.equals(value.getScheme())) {
                    result = value.getSchemeSpecificPart();
                    displayName = value.getLastPathSegment();
                } else if ("content".equals(value.getScheme())) {
                    result = value.toString();
                    displayName = resolveFileName(value, XWalkContentsClientBridge.this.mXWalkView.getActivity().getContentResolver());
                } else {
                    result = value.getPath();
                    displayName = value.getLastPathSegment();
                }
                if (displayName == null || displayName.isEmpty()) {
                    displayName = result;
                }
                XWalkContentsClientBridge.this.nativeOnFilesSelected(XWalkContentsClientBridge.this.mNativeContentsClientBridge, this.val$processId, this.val$renderId, this.val$modeFlags, result, displayName);
            } else {
                this.syncNullReceived = true;
            }
        }
    }

    private class InterceptNavigationDelegateImpl implements InterceptNavigationDelegate {
        private XWalkContentsClient mContentsClient;

        public InterceptNavigationDelegateImpl(XWalkContentsClient client) {
            this.mContentsClient = client;
        }

        public boolean shouldIgnoreNavigation(NavigationParams navigationParams) {
            String url = navigationParams.url;
            boolean ignoreNavigation = (XWalkContentsClientBridge.this.shouldOverrideUrlLoading(url) || (XWalkContentsClientBridge.this.mNavigationHandler != null && XWalkContentsClientBridge.this.mNavigationHandler.handleNavigation(navigationParams))) ? true : XWalkContentsClientBridge.$assertionsDisabled;
            if (!ignoreNavigation) {
                this.mContentsClient.getCallbackHelper().postOnPageStarted(url);
            }
            return ignoreNavigation;
        }
    }

    private native void nativeCancelJsResult(long j, int i);

    private native void nativeConfirmJsResult(long j, int i, String str);

    private native void nativeDownloadIcon(long j, String str);

    private native void nativeExitFullscreen(long j, long j2);

    private native void nativeNotificationClicked(long j, int i);

    private native void nativeNotificationClosed(long j, int i, boolean z);

    private native void nativeNotificationDisplayed(long j, int i);

    private native void nativeOnFilesNotSelected(long j, int i, int i2, int i3);

    private native void nativeOnFilesSelected(long j, int i, int i2, int i3, String str, String str2);

    private native void nativeProceedSslError(long j, boolean z, int i);

    private native void nativeProvideClientCertificateResponse(long j, int i, byte[][] bArr, AndroidPrivateKey androidPrivateKey);

    static {
        $assertionsDisabled = !XWalkContentsClientBridge.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        TAG = XWalkContentsClientBridge.class.getName();
    }

    public XWalkContentsClientBridge(XWalkViewInternal xwView) {
        this.mIsFullscreen = $assertionsDisabled;
        this.mLoadStatus = LoadStatusInternal.FINISHED;
        this.mLoadingUrl = null;
        this.mXWalkView = xwView;
        this.mLocalKeyStore = new DefaultAndroidKeyStore();
        this.mLookupTable = new ClientCertLookupTable();
        this.mInterceptNavigationDelegate = new InterceptNavigationDelegateImpl(this);
        this.mUiThreadHandler = new C04491();
    }

    public void setUIClient(XWalkUIClientInternal client) {
        if (client != null) {
            this.mXWalkUIClient = client;
        } else {
            this.mXWalkUIClient = new XWalkUIClientInternal(this.mXWalkView);
        }
    }

    public void setResourceClient(XWalkResourceClientInternal client) {
        if (client != null) {
            this.mXWalkResourceClient = client;
        } else {
            this.mXWalkResourceClient = new XWalkResourceClientInternal(this.mXWalkView);
        }
    }

    public void setXWalkWebChromeClient(XWalkWebChromeClient client) {
        if (client != null) {
            client.setContentsClient(this);
            this.mXWalkWebChromeClient = client;
        }
    }

    public XWalkWebChromeClient getXWalkWebChromeClient() {
        return this.mXWalkWebChromeClient;
    }

    public void setXWalkClient(XWalkClient client) {
        this.mXWalkClient = client;
    }

    public void setNavigationHandler(XWalkNavigationHandler handler) {
        this.mNavigationHandler = handler;
    }

    void registerPageLoadListener(PageLoadListener listener) {
        this.mPageLoadListener = listener;
    }

    public void setNotificationService(XWalkNotificationService service) {
        if (this.mNotificationService != null) {
            this.mNotificationService.shutdown();
        }
        this.mNotificationService = service;
        if (this.mNotificationService != null) {
            this.mNotificationService.setBridge(this);
        }
    }

    public boolean onNewIntent(Intent intent) {
        return this.mNotificationService.maybeHandleIntent(intent);
    }

    public InterceptNavigationDelegate getInterceptNavigationDelegate() {
        return this.mInterceptNavigationDelegate;
    }

    private boolean isOwnerActivityRunning() {
        if (this.mXWalkView == null || !this.mXWalkView.isOwnerActivityRunning()) {
            return $assertionsDisabled;
        }
        return true;
    }

    public boolean shouldOverrideUrlLoading(String url) {
        if (this.mXWalkResourceClient == null || this.mXWalkView == null) {
            return $assertionsDisabled;
        }
        return this.mXWalkResourceClient.shouldOverrideUrlLoading(this.mXWalkView, url);
    }

    public boolean shouldOverrideKeyEvent(KeyEvent event) {
        boolean overridden = $assertionsDisabled;
        if (!(this.mXWalkUIClient == null || this.mXWalkView == null)) {
            overridden = this.mXWalkUIClient.shouldOverrideKeyEvent(this.mXWalkView, event);
        }
        if (overridden) {
            return overridden;
        }
        return super.shouldOverrideKeyEvent(event);
    }

    public void onUnhandledKeyEvent(KeyEvent event) {
        if (this.mXWalkUIClient != null && this.mXWalkView != null) {
            this.mXWalkUIClient.onUnhandledKeyEvent(this.mXWalkView, event);
        }
    }

    public void getVisitedHistory(ValueCallback<String[]> valueCallback) {
    }

    public void doUpdateVisitedHistory(String url, boolean isReload) {
        this.mXWalkResourceClient.doUpdateVisitedHistory(this.mXWalkView, url, isReload);
    }

    public void onProgressChanged(int progress) {
        if (isOwnerActivityRunning()) {
            this.mXWalkResourceClient.onProgressChanged(this.mXWalkView, progress);
        }
    }

    public WebResourceResponse shouldInterceptRequest(String url) {
        if (isOwnerActivityRunning()) {
            return this.mXWalkResourceClient.shouldInterceptLoadRequest(this.mXWalkView, url);
        }
        return null;
    }

    public void onDidChangeThemeColor(int color) {
        if (isOwnerActivityRunning()) {
            this.mXWalkUIClient.onDidChangeThemeColor(this.mXWalkView, color);
        }
    }

    public void onDocumentLoadedInFrame(long frameId) {
        if (isOwnerActivityRunning()) {
            this.mXWalkResourceClient.onDocumentLoadedInFrame(this.mXWalkView, frameId);
        }
    }

    public void onResourceLoadStarted(String url) {
        if (isOwnerActivityRunning()) {
            this.mXWalkResourceClient.onLoadStarted(this.mXWalkView, url);
        }
    }

    public void onResourceLoadFinished(String url) {
        if (isOwnerActivityRunning()) {
            this.mXWalkResourceClient.onLoadFinished(this.mXWalkView, url);
        }
    }

    public void onLoadResource(String url) {
        if (this.mXWalkClient != null && isOwnerActivityRunning()) {
            this.mXWalkClient.onLoadResource(this.mXWalkView, url);
        }
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (this.mXWalkClient == null || this.mXWalkView == null) {
            return $assertionsDisabled;
        }
        ConsoleMessageType consoleMessageType = ConsoleMessageType.DEBUG;
        switch (C04525.$SwitchMap$android$webkit$ConsoleMessage$MessageLevel[consoleMessage.messageLevel().ordinal()]) {
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                consoleMessageType = ConsoleMessageType.INFO;
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                consoleMessageType = ConsoleMessageType.LOG;
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                consoleMessageType = ConsoleMessageType.WARNING;
                break;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                consoleMessageType = ConsoleMessageType.ERROR;
                break;
            default:
                Log.w(TAG, "Unknown message level, defaulting to DEBUG");
                break;
        }
        return this.mXWalkUIClient.onConsoleMessage(this.mXWalkView, consoleMessage.message(), consoleMessage.lineNumber(), consoleMessage.sourceId(), consoleMessageType);
    }

    @CalledByNative
    public void onReceivedHttpAuthRequest(XWalkHttpAuthHandlerInternal handler, String host, String realm) {
        if (this.mXWalkResourceClient != null && isOwnerActivityRunning()) {
            this.mXWalkResourceClient.onReceivedHttpAuthRequest(this.mXWalkView, handler, host, realm);
        }
    }

    public void onReceivedSslError(ValueCallback<Boolean> callback, SslError error) {
        if (this.mXWalkResourceClient != null && isOwnerActivityRunning()) {
            this.mXWalkResourceClient.onReceivedSslError(this.mXWalkView, callback, error);
        }
    }

    public void onReceivedLoginRequest(String realm, String account, String args) {
    }

    public void onReceivedClientCertRequest(ClientCertRequestInternal handler) {
        if (this.mXWalkResourceClient != null && isOwnerActivityRunning()) {
            this.mXWalkResourceClient.onReceivedClientCertRequest(this.mXWalkView, handler);
        }
    }

    public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
        if (this.mXWalkWebChromeClient != null && isOwnerActivityRunning()) {
            this.mXWalkWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    public void onGeolocationPermissionsHidePrompt() {
        if (this.mXWalkWebChromeClient != null && isOwnerActivityRunning()) {
            this.mXWalkWebChromeClient.onGeolocationPermissionsHidePrompt();
        }
    }

    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
    }

    public void onNewPicture(Picture picture) {
    }

    public void onPageStarted(String url) {
        if (this.mXWalkUIClient != null && isOwnerActivityRunning()) {
            this.mLoadingUrl = url;
            this.mLoadStatus = LoadStatusInternal.FINISHED;
            this.mXWalkUIClient.onPageLoadStarted(this.mXWalkView, url);
        }
    }

    public void onPageFinished(String url) {
        if (isOwnerActivityRunning()) {
            if (this.mPageLoadListener != null) {
                this.mPageLoadListener.onPageFinished(url);
            }
            if (this.mXWalkUIClient != null) {
                if (this.mLoadStatus != LoadStatusInternal.CANCELLED || this.mLoadingUrl == null) {
                    this.mXWalkUIClient.onPageLoadStopped(this.mXWalkView, url, this.mLoadStatus);
                } else {
                    this.mXWalkUIClient.onPageLoadStopped(this.mXWalkView, this.mLoadingUrl, this.mLoadStatus);
                }
                this.mLoadingUrl = null;
            }
            onResourceLoadFinished(url);
        }
    }

    protected void onStopLoading() {
        this.mLoadStatus = LoadStatusInternal.CANCELLED;
    }

    public void onReceivedError(int errorCode, String description, String failingUrl) {
        if (isOwnerActivityRunning()) {
            if (this.mLoadingUrl != null && this.mLoadingUrl.equals(failingUrl)) {
                this.mLoadStatus = LoadStatusInternal.FAILED;
            }
            this.mXWalkResourceClient.onReceivedLoadError(this.mXWalkView, errorCode, description, failingUrl);
        }
    }

    public void onRendererUnresponsive() {
        if (this.mXWalkClient != null && isOwnerActivityRunning()) {
            this.mXWalkClient.onRendererUnresponsive(this.mXWalkView);
        }
    }

    public void onRendererResponsive() {
        if (this.mXWalkClient != null && isOwnerActivityRunning()) {
            this.mXWalkClient.onRendererResponsive(this.mXWalkView);
        }
    }

    public void onFormResubmission(Message dontResend, Message resend) {
        dontResend.sendToTarget();
    }

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        if (this.mDownloadListener != null) {
            this.mDownloadListener.onDownloadStart(url, userAgent, contentDisposition, mimeType, contentLength);
        }
    }

    public boolean onCreateWindow(boolean isDialog, boolean isUserGesture) {
        if (isDialog) {
            return $assertionsDisabled;
        }
        InitiateByInternal initiator = InitiateByInternal.BY_JAVASCRIPT;
        if (isUserGesture) {
            initiator = InitiateByInternal.BY_USER_GESTURE;
        }
        return this.mXWalkUIClient.onCreateWindowRequested(this.mXWalkView, initiator, new C04502());
    }

    public void onRequestFocus() {
        if (isOwnerActivityRunning()) {
            this.mXWalkUIClient.onRequestFocus(this.mXWalkView);
        }
    }

    public void onCloseWindow() {
        if (isOwnerActivityRunning()) {
            this.mXWalkUIClient.onJavascriptCloseWindow(this.mXWalkView);
        }
    }

    public void onReceivedIcon(Bitmap bitmap) {
        if (!(this.mXWalkWebChromeClient == null || this.mXWalkView == null)) {
            this.mXWalkWebChromeClient.onReceivedIcon(this.mXWalkView, bitmap);
        }
        this.mFavicon = bitmap;
    }

    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (this.mXWalkWebChromeClient != null && isOwnerActivityRunning()) {
            this.mXWalkWebChromeClient.onShowCustomView(view, callback);
        }
    }

    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        if (this.mXWalkWebChromeClient != null && isOwnerActivityRunning()) {
            this.mXWalkWebChromeClient.onShowCustomView(view, requestedOrientation, callback);
        }
    }

    public void onHideCustomView() {
        if (this.mXWalkWebChromeClient != null && isOwnerActivityRunning()) {
            this.mXWalkWebChromeClient.onHideCustomView();
        }
    }

    public void onScaleChangedScaled(float oldScale, float newScale) {
        if (isOwnerActivityRunning()) {
            this.mXWalkUIClient.onScaleChanged(this.mXWalkView, oldScale, newScale);
        }
    }

    public void didFinishLoad(String url) {
    }

    public void onTitleChanged(String title) {
        if (this.mXWalkUIClient != null && isOwnerActivityRunning()) {
            this.mXWalkUIClient.onReceivedTitle(this.mXWalkView, title);
        }
    }

    public void onToggleFullscreen(boolean enterFullscreen) {
        if (isOwnerActivityRunning()) {
            this.mIsFullscreen = enterFullscreen;
            this.mXWalkUIClient.onFullscreenToggled(this.mXWalkView, enterFullscreen);
        }
    }

    public boolean hasEnteredFullscreen() {
        return this.mIsFullscreen;
    }

    public boolean shouldCreateWebContents(String contentUrl) {
        return true;
    }

    public boolean shouldOverrideRunFileChooser(int processId, int renderId, int modeFlags, String acceptTypes, boolean capture) {
        boolean z = true;
        if (!isOwnerActivityRunning()) {
            return $assertionsDisabled;
        }
        AnonymousClass1UriCallback uploadFile = new C06523(processId, renderId, modeFlags);
        this.mXWalkUIClient.openFileChooser(this.mXWalkView, uploadFile, acceptTypes, Boolean.toString(capture));
        uploadFile.syncCallFinished = true;
        if (uploadFile.syncNullReceived) {
            return this.mXWalkView.showFileChooser(uploadFile, acceptTypes, Boolean.toString(capture));
        }
        if (uploadFile.syncNullReceived) {
            z = $assertionsDisabled;
        }
        return z;
    }

    public ContentVideoViewClient getContentVideoViewClient() {
        return new XWalkContentVideoViewClient(this, this.mXWalkView.getActivity(), this.mXWalkView);
    }

    public void provideClientCertificateResponse(int id, byte[][] certChain, AndroidPrivateKey androidKey) {
        nativeProvideClientCertificateResponse(this.mNativeContentsClientBridge, id, certChain, androidKey);
    }

    @CalledByNative
    private void setNativeContentsClientBridge(long nativeContentsClientBridge) {
        this.mNativeContentsClientBridge = nativeContentsClientBridge;
    }

    @CalledByNative
    private boolean allowCertificateError(int certError, byte[] derBytes, String url, int id) {
        SslCertificate cert = SslUtil.getCertificateFromDerBytes(derBytes);
        if (cert == null) {
            return $assertionsDisabled;
        }
        onReceivedSslError(new C04514(id), SslUtil.sslErrorFromNetErrorCode(certError, cert, url));
        return true;
    }

    @CalledByNative
    private void selectClientCertificate(int id, String[] keyTypes, byte[][] encodedPrincipals, String host, int port) {
        if (this.mXWalkResourceClient != null && isOwnerActivityRunning()) {
            if ($assertionsDisabled || this.mNativeContentsClientBridge != 0) {
                Cert cert = this.mLookupTable.getCertData(host, port);
                if (this.mLookupTable.isDenied(host, port)) {
                    nativeProvideClientCertificateResponse(this.mNativeContentsClientBridge, id, (byte[][]) null, null);
                    return;
                } else if (cert != null) {
                    nativeProvideClientCertificateResponse(this.mNativeContentsClientBridge, id, cert.certChain, cert.privateKey);
                    return;
                } else {
                    if (encodedPrincipals.length > 0) {
                        Principal[] principals = new X500Principal[encodedPrincipals.length];
                        int n = 0;
                        while (n < encodedPrincipals.length) {
                            try {
                                principals[n] = new X500Principal(encodedPrincipals[n]);
                                n++;
                            } catch (IllegalArgumentException e) {
                                Log.w(TAG, "Exception while decoding issuers list: " + e);
                                nativeProvideClientCertificateResponse(this.mNativeContentsClientBridge, id, (byte[][]) null, null);
                                return;
                            }
                        }
                    }
                    onReceivedClientCertRequest(new ClientCertRequestHandlerInternal(this, id, host, port));
                    return;
                }
            }
            throw new AssertionError();
        }
    }

    private void proceedSslError(boolean proceed, int id) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeProceedSslError(this.mNativeContentsClientBridge, proceed, id);
        }
    }

    @CalledByNative
    private void handleJsAlert(String url, String message, int id) {
        if (isOwnerActivityRunning()) {
            String str = url;
            String str2 = message;
            this.mXWalkUIClient.onJavascriptModalDialog(this.mXWalkView, JavascriptMessageTypeInternal.JAVASCRIPT_ALERT, str, str2, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE, new XWalkJavascriptResultHandlerInternal(this, id));
        }
    }

    @CalledByNative
    private void handleJsConfirm(String url, String message, int id) {
        if (isOwnerActivityRunning()) {
            String str = url;
            String str2 = message;
            this.mXWalkUIClient.onJavascriptModalDialog(this.mXWalkView, JavascriptMessageTypeInternal.JAVASCRIPT_CONFIRM, str, str2, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE, new XWalkJavascriptResultHandlerInternal(this, id));
        }
    }

    @CalledByNative
    private void handleJsPrompt(String url, String message, String defaultValue, int id) {
        if (isOwnerActivityRunning()) {
            this.mXWalkUIClient.onJavascriptModalDialog(this.mXWalkView, JavascriptMessageTypeInternal.JAVASCRIPT_PROMPT, url, message, defaultValue, new XWalkJavascriptResultHandlerInternal(this, id));
        }
    }

    @CalledByNative
    private void handleJsBeforeUnload(String url, String message, int id) {
        if (isOwnerActivityRunning()) {
            String str = url;
            String str2 = message;
            this.mXWalkUIClient.onJavascriptModalDialog(this.mXWalkView, JavascriptMessageTypeInternal.JAVASCRIPT_BEFOREUNLOAD, str, str2, CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE, new XWalkJavascriptResultHandlerInternal(this, id));
        }
    }

    @CalledByNative
    private void showNotification(String title, String message, String replaceId, Bitmap icon, int notificationId) {
        this.mNotificationService.showNotification(title, message, replaceId, icon, notificationId);
    }

    @CalledByNative
    private void cancelNotification(int notificationId) {
        this.mNotificationService.cancelNotification(notificationId);
    }

    void confirmJsResult(int id, String prompt) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeConfirmJsResult(this.mNativeContentsClientBridge, id, prompt);
        }
    }

    void cancelJsResult(int id) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeCancelJsResult(this.mNativeContentsClientBridge, id);
        }
    }

    void exitFullscreen(long nativeWebContents) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeExitFullscreen(this.mNativeContentsClientBridge, nativeWebContents);
        }
    }

    public void notificationDisplayed(int id) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeNotificationDisplayed(this.mNativeContentsClientBridge, id);
        }
    }

    public void notificationClicked(int id) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeNotificationClicked(this.mNativeContentsClientBridge, id);
        }
    }

    public void notificationClosed(int id, boolean byUser) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeNotificationClosed(this.mNativeContentsClientBridge, id, byUser);
        }
    }

    void setDownloadListener(XWalkDownloadListenerInternal listener) {
        this.mDownloadListener = listener;
    }

    public void requestHttpGetDownload(DownloadInfo downloadInfo) {
        if (this.mDownloadListener != null) {
            this.mDownloadListener.onDownloadStart(downloadInfo.getUrl(), downloadInfo.getUserAgent(), downloadInfo.getContentDisposition(), downloadInfo.getMimeType(), downloadInfo.getContentLength());
        }
    }

    public void onDownloadStarted(String filename, String mimeType) {
    }

    public void onDangerousDownload(String filename, int downloadId) {
    }

    @CalledByNative
    public void onWebLayoutPageScaleFactorChanged(float pageScaleFactor) {
        if (this.mPageScaleFactor != pageScaleFactor) {
            float oldPageScaleFactor = this.mPageScaleFactor;
            this.mPageScaleFactor = pageScaleFactor;
            onScaleChanged(oldPageScaleFactor, this.mPageScaleFactor);
        }
    }

    @CalledByNative
    public void onIconAvailable(String url) {
        this.mXWalkUIClient.onIconAvailable(this.mXWalkView, url, this.mUiThreadHandler.obtainMessage(NEW_ICON_DOWNLOAD, url));
    }

    @CalledByNative
    public void onReceivedIcon(String url, Bitmap icon) {
        this.mXWalkUIClient.onReceivedIcon(this.mXWalkView, url, icon);
    }
}
