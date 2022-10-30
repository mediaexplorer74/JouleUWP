package org.xwalk.core.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;
import com.google.android.gms.common.ConnectionResult;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.chromium.base.ApplicationStatus;
import org.chromium.base.ApplicationStatus.ActivityStateListener;
import org.chromium.base.ApplicationStatusManager;
import org.chromium.base.CommandLine;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.net.NetworkChangeNotifier;
import org.xwalk.core.internal.extension.BuiltinXWalkExtensions;

@XWalkAPI(createExternally = true, extendClass = FrameLayout.class)
public class XWalkViewInternal extends FrameLayout {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final String PATH_PREFIX = "file:";
    static final String PLAYSTORE_DETAIL_URI = "market://details?id=";
    @XWalkAPI
    public static final int RELOAD_IGNORE_CACHE = 1;
    @XWalkAPI
    public static final int RELOAD_NORMAL = 0;
    private static final String TAG;
    private static boolean sInitialized;
    private Activity mActivity;
    private XWalkActivityStateListener mActivityStateListener;
    private String mCameraPhotoPath;
    private XWalkContent mContent;
    private Context mContext;
    private ValueCallback<Uri> mFilePathCallback;
    private boolean mIsHidden;

    private class XWalkActivityStateListener implements ActivityStateListener {
        WeakReference<XWalkViewInternal> mXWalkViewRef;

        XWalkActivityStateListener(XWalkViewInternal view) {
            this.mXWalkViewRef = new WeakReference(view);
        }

        public void onActivityStateChange(Activity activity, int newState) {
            XWalkViewInternal view = (XWalkViewInternal) this.mXWalkViewRef.get();
            if (view != null) {
                view.onActivityStateChange(activity, newState);
            }
        }
    }

    static {
        boolean z;
        if (XWalkViewInternal.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        TAG = XWalkViewInternal.class.getSimpleName();
        sInitialized = $assertionsDisabled;
    }

    @XWalkAPI(postWrapperLines = {"        addView((FrameLayout)bridge, new FrameLayout.LayoutParams(", "                FrameLayout.LayoutParams.MATCH_PARENT,", "                FrameLayout.LayoutParams.MATCH_PARENT));", "        removeViewAt(0);"}, preWrapperLines = {"        super(${param1}, null);", "        SurfaceView surfaceView = new SurfaceView(${param1});", "        surfaceView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));", "        addView(surfaceView);"})
    public XWalkViewInternal(Context context) {
        super(context, null);
        checkThreadSafety();
        this.mActivity = (Activity) context;
        this.mContext = getContext();
        init(getContext(), getActivity());
        initXWalkContent(this.mContext, null);
    }

    @XWalkAPI(postWrapperLines = {"        addView((FrameLayout)bridge, new FrameLayout.LayoutParams(", "                FrameLayout.LayoutParams.MATCH_PARENT,", "                FrameLayout.LayoutParams.MATCH_PARENT));", "        removeViewAt(0);"}, preWrapperLines = {"        super(${param1}, ${param2});", "        SurfaceView surfaceView = new SurfaceView(${param1});", "        surfaceView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));", "        addView(surfaceView);"})
    public XWalkViewInternal(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkThreadSafety();
        this.mActivity = (Activity) context;
        this.mContext = getContext();
        init(getContext(), getActivity());
        initXWalkContent(this.mContext, attrs);
    }

    @XWalkAPI(postWrapperLines = {"        addView((FrameLayout)bridge, new FrameLayout.LayoutParams(", "                FrameLayout.LayoutParams.MATCH_PARENT,", "                FrameLayout.LayoutParams.MATCH_PARENT));", "        removeViewAt(0);"}, preWrapperLines = {"        super(${param1}, null);", "        SurfaceView surfaceView = new SurfaceView(${param1});", "        surfaceView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));", "        addView(surfaceView);"})
    public XWalkViewInternal(Context context, Activity activity) {
        super(context, null);
        checkThreadSafety();
        this.mActivity = activity;
        this.mContext = getContext();
        init(getContext(), getActivity());
        initXWalkContent(this.mContext, null);
    }

    private static void init(Context context, Activity activity) {
        if (!sInitialized) {
            XWalkViewDelegate.init(null, activity);
            ApplicationStatusManager.init(activity.getApplication());
            NetworkChangeNotifier.init(activity);
            NetworkChangeNotifier.setAutoDetectConnectivityState(true);
            ApplicationStatusManager.informActivityStarted(activity);
            sInitialized = true;
        }
    }

    public Activity getActivity() {
        if (this.mActivity != null) {
            return this.mActivity;
        }
        if (getContext() instanceof Activity) {
            return (Activity) getContext();
        }
        if ($assertionsDisabled) {
            return null;
        }
        throw new AssertionError();
    }

    public Context getViewContext() {
        return this.mContext;
    }

    public void completeWindowCreation(XWalkViewInternal newXWalkView) {
        this.mContent.supplyContentsForPopup(newXWalkView == null ? null : newXWalkView.mContent);
    }

    private void initXWalkContent(Context context, AttributeSet attrs) {
        this.mActivityStateListener = new XWalkActivityStateListener(this);
        ApplicationStatus.registerStateListenerForActivity(this.mActivityStateListener, getActivity());
        this.mIsHidden = $assertionsDisabled;
        this.mContent = new XWalkContent(context, attrs, this);
        this.mContent.resumeTimers();
        setXWalkClient(new XWalkClient(this));
        setXWalkWebChromeClient(new XWalkWebChromeClient(this));
        setUIClient(new XWalkUIClientInternal(this));
        setResourceClient(new XWalkResourceClientInternal(this));
        setDownloadListener(new XWalkDownloadListenerImpl(context));
        setNavigationHandler(new XWalkNavigationHandlerImpl(context));
        setNotificationService(new XWalkNotificationServiceImpl(context, this));
        if (CommandLine.getInstance().hasSwitch("disable-xwalk-extensions")) {
            XWalkPreferencesInternal.setValue("enable-extensions", (boolean) $assertionsDisabled);
        } else {
            BuiltinXWalkExtensions.load(context, getActivity());
        }
        XWalkPathHelper.initialize();
        XWalkPathHelper.setCacheDirectory(this.mContext.getApplicationContext().getCacheDir().getPath());
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state) || "mounted_ro".equals(state)) {
            File extCacheDir = this.mContext.getApplicationContext().getExternalCacheDir();
            if (extCacheDir != null) {
                XWalkPathHelper.setExternalCacheDirectory(extCacheDir.getPath());
            }
        }
    }

    @XWalkAPI
    public void load(String url, String content) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.loadUrl(url, content);
        }
    }

    @XWalkAPI
    public void loadAppFromManifest(String url, String content) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.loadAppFromManifest(url, content);
        }
    }

    @XWalkAPI
    public void reload(int mode) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.reload(mode);
        }
    }

    @XWalkAPI
    public void stopLoading() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.stopLoading();
        }
    }

    @XWalkAPI
    public String getUrl() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getUrl();
    }

    @XWalkAPI
    public String getTitle() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getTitle();
    }

    @XWalkAPI
    public String getOriginalUrl() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getOriginalUrl();
    }

    @XWalkAPI
    public XWalkNavigationHistoryInternal getNavigationHistory() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getNavigationHistory();
    }

    @XWalkAPI
    public void addJavascriptInterface(Object object, String name) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.addJavascriptInterface(object, name);
        }
    }

    @XWalkAPI
    public void evaluateJavascript(String script, ValueCallback<String> callback) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.evaluateJavascript(script, callback);
        }
    }

    @XWalkAPI
    public void clearCache(boolean includeDiskFiles) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.clearCache(includeDiskFiles);
        }
    }

    @XWalkAPI
    public void clearCacheForSingleFile(String url) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.clearCacheForSingleFile(url);
        }
    }

    @XWalkAPI
    public boolean hasEnteredFullscreen() {
        if (this.mContent == null) {
            return $assertionsDisabled;
        }
        checkThreadSafety();
        return this.mContent.hasEnteredFullscreen();
    }

    @XWalkAPI
    public void leaveFullscreen() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.exitFullscreen();
        }
    }

    @XWalkAPI
    public void pauseTimers() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.pauseTimers();
        }
    }

    @XWalkAPI
    public void resumeTimers() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.resumeTimers();
        }
    }

    @XWalkAPI
    public void onHide() {
        if (this.mContent != null && !this.mIsHidden) {
            this.mContent.onPause();
            this.mIsHidden = true;
        }
    }

    @XWalkAPI
    public void onShow() {
        if (this.mContent != null && this.mIsHidden) {
            this.mContent.onResume();
            this.mIsHidden = $assertionsDisabled;
        }
    }

    @XWalkAPI
    public void onDestroy() {
        destroy();
    }

    @XWalkAPI
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.mContent != null) {
            if (requestCode != RELOAD_IGNORE_CACHE || this.mFilePathCallback == null) {
                this.mContent.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri results = null;
            if (-1 == resultCode) {
                if (data != null && (data.getAction() != null || data.getData() != null)) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = Uri.parse(dataString);
                    }
                    deleteImageFile();
                } else if (this.mCameraPhotoPath != null) {
                    results = Uri.parse(this.mCameraPhotoPath);
                }
            } else if (resultCode == 0) {
                deleteImageFile();
            }
            this.mFilePathCallback.onReceiveValue(results);
            this.mFilePathCallback = null;
        }
    }

    @XWalkAPI
    public boolean onNewIntent(Intent intent) {
        if (this.mContent == null) {
            return $assertionsDisabled;
        }
        return this.mContent.onNewIntent(intent);
    }

    @XWalkAPI
    public boolean saveState(Bundle outState) {
        if (this.mContent == null) {
            return $assertionsDisabled;
        }
        this.mContent.saveState(outState);
        return true;
    }

    @XWalkAPI
    public boolean restoreState(Bundle inState) {
        if (this.mContent == null || this.mContent.restoreState(inState) == null) {
            return $assertionsDisabled;
        }
        return true;
    }

    @XWalkAPI
    public String getAPIVersion() {
        return "5.0";
    }

    @XWalkAPI
    public String getXWalkVersion() {
        if (this.mContent == null) {
            return null;
        }
        return this.mContent.getXWalkVersion();
    }

    @XWalkAPI(reservable = true)
    public void setUIClient(XWalkUIClientInternal client) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setUIClient(client);
        }
    }

    @XWalkAPI(reservable = true)
    public void setResourceClient(XWalkResourceClientInternal client) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setResourceClient(client);
        }
    }

    @XWalkAPI
    public void setBackgroundColor(int color) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setBackgroundColor(color);
        }
    }

    @XWalkAPI
    public void setLayerType(int layerType, Paint paint) {
        if (layerType != RELOAD_IGNORE_CACHE) {
            super.setLayerType(layerType, paint);
        } else {
            Log.w(TAG, "LAYER_TYPE_SOFTWARE is not supported by XwalkView");
        }
    }

    @XWalkAPI
    public void setUserAgentString(String userAgent) {
        XWalkSettings settings = getSettings();
        if (settings != null) {
            checkThreadSafety();
            settings.setUserAgentString(userAgent);
        }
    }

    @XWalkAPI
    public String getUserAgentString() {
        XWalkSettings settings = getSettings();
        if (settings == null) {
            return null;
        }
        checkThreadSafety();
        return settings.getUserAgentString();
    }

    @XWalkAPI
    public void setAcceptLanguages(String acceptLanguages) {
        XWalkSettings settings = getSettings();
        if (settings != null) {
            checkThreadSafety();
            settings.setAcceptLanguages(acceptLanguages);
        }
    }

    public XWalkSettings getSettings() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getSettings();
    }

    @XWalkAPI
    public void setNetworkAvailable(boolean networkUp) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setNetworkAvailable(networkUp);
        }
    }

    public void enableRemoteDebugging() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.enableRemoteDebugging();
        }
    }

    @XWalkAPI
    public Uri getRemoteDebuggingUrl() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        String wsUrl = this.mContent.getRemoteDebuggingUrl();
        if (wsUrl == null || wsUrl.isEmpty()) {
            return null;
        }
        return Uri.parse(wsUrl);
    }

    @XWalkAPI
    public boolean zoomIn() {
        if (this.mContent == null) {
            return $assertionsDisabled;
        }
        checkThreadSafety();
        return this.mContent.zoomIn();
    }

    @XWalkAPI
    public boolean zoomOut() {
        if (this.mContent == null) {
            return $assertionsDisabled;
        }
        checkThreadSafety();
        return this.mContent.zoomOut();
    }

    @XWalkAPI
    public void zoomBy(float factor) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.zoomBy(factor);
        }
    }

    @XWalkAPI
    public boolean canZoomIn() {
        if (this.mContent == null) {
            return $assertionsDisabled;
        }
        checkThreadSafety();
        return this.mContent.canZoomIn();
    }

    @XWalkAPI
    public boolean canZoomOut() {
        if (this.mContent == null) {
            return $assertionsDisabled;
        }
        checkThreadSafety();
        return this.mContent.canZoomOut();
    }

    @XWalkAPI
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.mContent.onCreateInputConnection(outAttrs);
    }

    @XWalkAPI
    public void setInitialScale(int scaleInPercent) {
        checkThreadSafety();
        XWalkSettings settings = getSettings();
        if (settings != null) {
            settings.setInitialPageScale((float) scaleInPercent);
        }
    }

    public int getContentID() {
        if (this.mContent == null) {
            return -1;
        }
        return this.mContent.getRoutingID();
    }

    boolean canGoBack() {
        if (this.mContent == null) {
            return $assertionsDisabled;
        }
        checkThreadSafety();
        return this.mContent.canGoBack();
    }

    void goBack() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.goBack();
        }
    }

    boolean canGoForward() {
        if (this.mContent == null) {
            return $assertionsDisabled;
        }
        checkThreadSafety();
        return this.mContent.canGoForward();
    }

    void goForward() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.goForward();
        }
    }

    void clearHistory() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.clearHistory();
        }
    }

    void destroy() {
        if (this.mContent != null) {
            ApplicationStatus.unregisterActivityStateListener(this.mActivityStateListener);
            this.mActivityStateListener = null;
            this.mContent.destroy();
            disableRemoteDebugging();
        }
    }

    void disableRemoteDebugging() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.disableRemoteDebugging();
        }
    }

    private static void checkThreadSafety() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException(new Throwable("Warning: A XWalkViewInternal method was called on thread '" + Thread.currentThread().getName() + "'. " + "All XWalkViewInternal methods must be called on the UI thread. "));
        }
    }

    boolean isOwnerActivityRunning() {
        if (ApplicationStatus.getStateForActivity(getActivity()) == 6) {
            return $assertionsDisabled;
        }
        return true;
    }

    void navigateTo(int offset) {
        if (this.mContent != null) {
            this.mContent.navigateTo(offset);
        }
    }

    void setOverlayVideoMode(boolean enabled) {
        this.mContent.setOverlayVideoMode(enabled);
    }

    @XWalkAPI
    public void setZOrderOnTop(boolean onTop) {
        if (this.mContent != null) {
            this.mContent.setZOrderOnTop(onTop);
        }
    }

    @XWalkAPI
    public void clearFormData() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.hideAutofillPopup();
        }
    }

    @XWalkAPI(disableReflectMethod = true, preWrapperLines = {"        if (visibility == View.INVISIBLE) visibility = View.GONE;", "        super.setVisibility(visibility);", "        setSurfaceViewVisibility(visibility);"})
    public void setVisibility(int visibility) {
    }

    @XWalkAPI(reservable = true)
    public void setSurfaceViewVisibility(int visibility) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setVisibility(visibility);
        }
    }

    public void setXWalkClient(XWalkClient client) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setXWalkClient(client);
        }
    }

    public void setXWalkWebChromeClient(XWalkWebChromeClient client) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setXWalkWebChromeClient(client);
        }
    }

    @XWalkAPI
    public void setDownloadListener(XWalkDownloadListenerInternal listener) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setDownloadListener(listener);
        }
    }

    public void setNavigationHandler(XWalkNavigationHandler handler) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setNavigationHandler(handler);
        }
    }

    public void setNotificationService(XWalkNotificationService service) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setNotificationService(service);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == RELOAD_IGNORE_CACHE && event.getKeyCode() == 4) {
            if (hasEnteredFullscreen()) {
                leaveFullscreen();
                return true;
            } else if (canGoBack()) {
                goBack();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void onActivityStateChange(Activity activity, int newState) {
        if ($assertionsDisabled || getActivity() == activity) {
            switch (newState) {
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                    onShow();
                    return;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    resumeTimers();
                    return;
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    pauseTimers();
                    return;
                case ConnectionResult.INVALID_ACCOUNT /*5*/:
                    onHide();
                    return;
                case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                    onDestroy();
                    return;
                default:
                    return;
            }
        }
        throw new AssertionError();
    }

    public boolean showFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        this.mFilePathCallback = uploadFile;
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                this.mCameraPhotoPath = PATH_PREFIX + photoFile.getAbsolutePath();
                takePictureIntent.putExtra("PhotoPath", this.mCameraPhotoPath);
                takePictureIntent.putExtra("output", Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }
        Intent contentSelectionIntent = new Intent("android.intent.action.GET_CONTENT");
        contentSelectionIntent.addCategory("android.intent.category.OPENABLE");
        contentSelectionIntent.setType("*/*");
        Intent camcorder = new Intent("android.media.action.VIDEO_CAPTURE");
        Intent soundRecorder = new Intent("android.provider.MediaStore.RECORD_SOUND");
        ArrayList<Intent> extraIntents = new ArrayList();
        if (takePictureIntent != null) {
            extraIntents.add(takePictureIntent);
        }
        extraIntents.add(camcorder);
        extraIntents.add(soundRecorder);
        Intent chooserIntent = new Intent("android.intent.action.CHOOSER");
        chooserIntent.putExtra("android.intent.extra.INTENT", contentSelectionIntent);
        chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) extraIntents.toArray(new Intent[0]));
        getActivity().startActivityForResult(chooserIntent, RELOAD_IGNORE_CACHE);
        return true;
    }

    private File createImageFile() {
        File file = null;
        if (Environment.getExternalStorageState().equals("mounted")) {
            String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            try {
                file = File.createTempFile(imageFileName, ".jpg", storageDir);
            } catch (IOException ex) {
                Log.e(TAG, "Unable to create Image File", ex);
            }
        } else {
            Log.e(TAG, "External storage is not mounted.");
        }
        return file;
    }

    private boolean deleteImageFile() {
        if (this.mCameraPhotoPath == null || !this.mCameraPhotoPath.contains(PATH_PREFIX)) {
            return $assertionsDisabled;
        }
        return new File(this.mCameraPhotoPath.split(PATH_PREFIX)[RELOAD_IGNORE_CACHE]).delete();
    }

    public ContentViewCore getXWalkContentForTest() {
        return this.mContent.getContentViewCoreForTest();
    }

    @XWalkAPI(delegate = true, preWrapperLines = {"return performLongClick();"})
    public boolean performLongClickDelegate() {
        return $assertionsDisabled;
    }

    @XWalkAPI(delegate = true, preWrapperLines = {"return onTouchEvent(event);"})
    public boolean onTouchEventDelegate(MotionEvent event) {
        return $assertionsDisabled;
    }

    @XWalkAPI
    public boolean onTouchEvent(MotionEvent event) {
        return this.mContent.onTouchEvent(event);
    }

    @XWalkAPI(delegate = true, preWrapperLines = {"onScrollChanged(l, t, oldl, oldt);"})
    public void onScrollChangedDelegate(int l, int t, int oldl, int oldt) {
    }

    @XWalkAPI(delegate = true, preWrapperLines = {"onFocusChanged(gainFocus, direction, previouslyFocusedRect);"})
    public void onFocusChangedDelegate(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
    }
}
