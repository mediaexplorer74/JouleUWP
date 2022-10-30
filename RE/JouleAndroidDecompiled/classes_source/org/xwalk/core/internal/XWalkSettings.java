package org.xwalk.core.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.Settings.System;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.ThreadUtils;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content_public.browser.WebContents;

@JNINamespace("xwalk")
public class XWalkSettings {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int MAXIMUM_FONT_SIZE = 72;
    private static final int MINIMUM_FONT_SIZE = 1;
    private static final String TAG = "XWalkSettings";
    private static boolean sAppCachePathIsSet;
    private static final Object sGlobalContentSettingsLock;
    private String mAcceptLanguages;
    private boolean mAllowContentUrlAccess;
    private boolean mAllowFileAccessFromFileURLs;
    private boolean mAllowFileUrlAccess;
    private boolean mAllowScriptsToCloseWindows;
    private boolean mAllowUniversalAccessFromFileURLs;
    private boolean mAppCacheEnabled;
    private boolean mAutoCompleteEnabled;
    private boolean mBlockNetworkLoads;
    private int mCacheMode;
    private final Context mContext;
    private double mDIPScale;
    private boolean mDatabaseEnabled;
    private String mDefaultVideoPosterURL;
    private boolean mDomStorageEnabled;
    private final EventHandler mEventHandler;
    private boolean mGeolocationEnabled;
    private boolean mImagesEnabled;
    private float mInitialPageScalePercent;
    private boolean mIsUpdateWebkitPrefsMessagePending;
    private boolean mJavaScriptCanOpenWindowsAutomatically;
    private boolean mJavaScriptEnabled;
    private boolean mLoadsImagesAutomatically;
    private boolean mMediaPlaybackRequiresUserGesture;
    private long mNativeXWalkSettings;
    private final boolean mPasswordEchoEnabled;
    private boolean mShouldFocusFirstNode;
    private boolean mSupportMultipleWindows;
    private boolean mUseWideViewport;
    private String mUserAgent;
    private final Object mXWalkSettingsLock;

    /* renamed from: org.xwalk.core.internal.XWalkSettings.1 */
    class C04691 implements Runnable {
        C04691() {
        }

        public void run() {
            if (XWalkSettings.this.mNativeXWalkSettings != 0) {
                XWalkSettings.this.nativeUpdateUserAgent(XWalkSettings.this.mNativeXWalkSettings);
            }
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkSettings.2 */
    class C04702 implements Runnable {
        C04702() {
        }

        public void run() {
            if (XWalkSettings.this.mNativeXWalkSettings != 0) {
                XWalkSettings.this.nativeUpdateAcceptLanguages(XWalkSettings.this.mNativeXWalkSettings);
            }
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkSettings.3 */
    class C04713 implements Runnable {
        C04713() {
        }

        public void run() {
            if (XWalkSettings.this.mNativeXWalkSettings != 0) {
                XWalkSettings.this.nativeUpdateFormDataPreferences(XWalkSettings.this.mNativeXWalkSettings);
            }
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkSettings.4 */
    class C04724 implements Runnable {
        C04724() {
        }

        public void run() {
            if (XWalkSettings.this.mNativeXWalkSettings != 0) {
                XWalkSettings.this.nativeUpdateInitialPageScale(XWalkSettings.this.mNativeXWalkSettings);
            }
        }
    }

    private class EventHandler {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final int UPDATE_WEBKIT_PREFERENCES = 0;
        private Handler mHandler;

        /* renamed from: org.xwalk.core.internal.XWalkSettings.EventHandler.1 */
        class C04731 extends Handler {
            C04731(Looper x0) {
                super(x0);
            }

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        synchronized (XWalkSettings.this.mXWalkSettingsLock) {
                            XWalkSettings.this.updateWebkitPreferencesOnUiThread();
                            XWalkSettings.this.mIsUpdateWebkitPrefsMessagePending = XWalkSettings.$assertionsDisabled;
                            XWalkSettings.this.mXWalkSettingsLock.notifyAll();
                            break;
                        }
                    default:
                }
            }
        }

        static {
            $assertionsDisabled = !XWalkSettings.class.desiredAssertionStatus() ? true : XWalkSettings.$assertionsDisabled;
        }

        EventHandler() {
        }

        void bindUiThread() {
            if (this.mHandler == null) {
                this.mHandler = new C04731(ThreadUtils.getUiThreadLooper());
            }
        }

        void maybeRunOnUiThreadBlocking(Runnable r) {
            if (this.mHandler != null) {
                ThreadUtils.runOnUiThreadBlocking(r);
            }
        }

        private void updateWebkitPreferencesLocked() {
            if (!$assertionsDisabled && !Thread.holdsLock(XWalkSettings.this.mXWalkSettingsLock)) {
                throw new AssertionError();
            } else if (XWalkSettings.this.mNativeXWalkSettings != 0 && this.mHandler != null) {
                if (ThreadUtils.runningOnUiThread()) {
                    XWalkSettings.this.updateWebkitPreferencesOnUiThread();
                } else if (!XWalkSettings.this.mIsUpdateWebkitPrefsMessagePending) {
                    XWalkSettings.this.mIsUpdateWebkitPrefsMessagePending = true;
                    this.mHandler.sendMessage(Message.obtain(null, 0));
                    while (XWalkSettings.this.mIsUpdateWebkitPrefsMessagePending) {
                        try {
                            XWalkSettings.this.mXWalkSettingsLock.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            }
        }
    }

    static class LazyDefaultUserAgent {
        private static final String sInstance;

        LazyDefaultUserAgent() {
        }

        static {
            sInstance = XWalkSettings.nativeGetDefaultUserAgent();
        }
    }

    private native void nativeDestroy(long j);

    private static native String nativeGetDefaultUserAgent();

    private native long nativeInit(WebContents webContents);

    private native void nativeUpdateAcceptLanguages(long j);

    private native void nativeUpdateEverythingLocked(long j);

    private native void nativeUpdateFormDataPreferences(long j);

    private native void nativeUpdateInitialPageScale(long j);

    private native void nativeUpdateUserAgent(long j);

    private native void nativeUpdateWebkitPreferences(long j);

    static {
        boolean z;
        if (XWalkSettings.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        sGlobalContentSettingsLock = new Object();
        sAppCachePathIsSet = $assertionsDisabled;
    }

    public XWalkSettings(Context context, WebContents webContents, boolean isAccessFromFileURLsGrantedByDefault) {
        boolean z = true;
        this.mXWalkSettingsLock = new Object();
        this.mAllowScriptsToCloseWindows = true;
        this.mLoadsImagesAutomatically = true;
        this.mImagesEnabled = true;
        this.mJavaScriptEnabled = true;
        this.mAllowUniversalAccessFromFileURLs = $assertionsDisabled;
        this.mAllowFileAccessFromFileURLs = $assertionsDisabled;
        this.mJavaScriptCanOpenWindowsAutomatically = true;
        this.mCacheMode = -1;
        this.mSupportMultipleWindows = $assertionsDisabled;
        this.mAppCacheEnabled = true;
        this.mDomStorageEnabled = true;
        this.mDatabaseEnabled = true;
        this.mUseWideViewport = $assertionsDisabled;
        this.mMediaPlaybackRequiresUserGesture = $assertionsDisabled;
        this.mAllowContentUrlAccess = true;
        this.mAllowFileUrlAccess = true;
        this.mShouldFocusFirstNode = true;
        this.mGeolocationEnabled = true;
        this.mNativeXWalkSettings = 0;
        this.mIsUpdateWebkitPrefsMessagePending = $assertionsDisabled;
        this.mAutoCompleteEnabled = true;
        this.mInitialPageScalePercent = 0.0f;
        this.mDIPScale = 1.0d;
        ThreadUtils.assertOnUiThread();
        this.mContext = context;
        this.mBlockNetworkLoads = this.mContext.checkPermission("android.permission.INTERNET", Process.myPid(), Process.myUid()) != 0 ? true : $assertionsDisabled;
        if (isAccessFromFileURLsGrantedByDefault) {
            this.mAllowUniversalAccessFromFileURLs = true;
            this.mAllowFileAccessFromFileURLs = true;
        }
        this.mUserAgent = LazyDefaultUserAgent.sInstance;
        if (System.getInt(context.getContentResolver(), "show_password", MINIMUM_FONT_SIZE) != MINIMUM_FONT_SIZE) {
            z = $assertionsDisabled;
        }
        this.mPasswordEchoEnabled = z;
        this.mEventHandler = new EventHandler();
        setWebContents(webContents);
    }

    void setWebContents(WebContents webContents) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mNativeXWalkSettings != 0) {
                nativeDestroy(this.mNativeXWalkSettings);
                if (!($assertionsDisabled || this.mNativeXWalkSettings == 0)) {
                    throw new AssertionError();
                }
            }
            if (webContents != null) {
                this.mEventHandler.bindUiThread();
                this.mNativeXWalkSettings = nativeInit(webContents);
                nativeUpdateEverythingLocked(this.mNativeXWalkSettings);
            }
        }
    }

    @CalledByNative
    private void nativeXWalkSettingsGone(long nativeXWalkSettings) {
        if ($assertionsDisabled || (this.mNativeXWalkSettings != 0 && this.mNativeXWalkSettings == nativeXWalkSettings)) {
            this.mNativeXWalkSettings = 0;
            return;
        }
        throw new AssertionError();
    }

    public void setAllowScriptsToCloseWindows(boolean allow) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAllowScriptsToCloseWindows != allow) {
                this.mAllowScriptsToCloseWindows = allow;
            }
        }
    }

    public boolean getAllowScriptsToCloseWindows() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mAllowScriptsToCloseWindows;
        }
        return z;
    }

    public void setCacheMode(int mode) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mCacheMode != mode) {
                this.mCacheMode = mode;
            }
        }
    }

    public int getCacheMode() {
        int i;
        synchronized (this.mXWalkSettingsLock) {
            i = this.mCacheMode;
        }
        return i;
    }

    public void setBlockNetworkLoads(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (!flag) {
                if (this.mContext.checkPermission("android.permission.INTERNET", Process.myPid(), Process.myUid()) != 0) {
                    throw new SecurityException("Permission denied - application missing INTERNET permission");
                }
            }
            this.mBlockNetworkLoads = flag;
        }
    }

    public boolean getBlockNetworkLoads() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mBlockNetworkLoads;
        }
        return z;
    }

    public void setAllowFileAccess(boolean allow) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAllowFileUrlAccess != allow) {
                this.mAllowFileUrlAccess = allow;
            }
        }
    }

    public boolean getAllowFileAccess() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mAllowFileUrlAccess;
        }
        return z;
    }

    public void setAllowContentAccess(boolean allow) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAllowContentUrlAccess != allow) {
                this.mAllowContentUrlAccess = allow;
            }
        }
    }

    public boolean getAllowContentAccess() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mAllowContentUrlAccess;
        }
        return z;
    }

    public void setGeolocationEnabled(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mGeolocationEnabled != flag) {
                this.mGeolocationEnabled = flag;
            }
        }
    }

    boolean getGeolocationEnabled() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mGeolocationEnabled;
        }
        return z;
    }

    public void setJavaScriptEnabled(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mJavaScriptEnabled != flag) {
                this.mJavaScriptEnabled = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public void setAllowUniversalAccessFromFileURLs(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAllowUniversalAccessFromFileURLs != flag) {
                this.mAllowUniversalAccessFromFileURLs = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public void setAllowFileAccessFromFileURLs(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAllowFileAccessFromFileURLs != flag) {
                this.mAllowFileAccessFromFileURLs = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public void setLoadsImagesAutomatically(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mLoadsImagesAutomatically != flag) {
                this.mLoadsImagesAutomatically = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public boolean getLoadsImagesAutomatically() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mLoadsImagesAutomatically;
        }
        return z;
    }

    public void setImagesEnabled(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mImagesEnabled != flag) {
                this.mImagesEnabled = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public boolean getImagesEnabled() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mImagesEnabled;
        }
        return z;
    }

    public boolean getJavaScriptEnabled() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mJavaScriptEnabled;
        }
        return z;
    }

    public boolean getAllowUniversalAccessFromFileURLs() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mAllowUniversalAccessFromFileURLs;
        }
        return z;
    }

    public boolean getAllowFileAccessFromFileURLs() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mAllowFileAccessFromFileURLs;
        }
        return z;
    }

    public void setJavaScriptCanOpenWindowsAutomatically(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mJavaScriptCanOpenWindowsAutomatically != flag) {
                this.mJavaScriptCanOpenWindowsAutomatically = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public boolean getJavaScriptCanOpenWindowsAutomatically() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mJavaScriptCanOpenWindowsAutomatically;
        }
        return z;
    }

    public void setSupportMultipleWindows(boolean support) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mSupportMultipleWindows != support) {
                this.mSupportMultipleWindows = support;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public boolean supportMultipleWindows() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mSupportMultipleWindows;
        }
        return z;
    }

    public void setUseWideViewPort(boolean use) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mUseWideViewport != use) {
                this.mUseWideViewport = use;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public boolean getUseWideViewPort() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mUseWideViewport;
        }
        return z;
    }

    public void setAppCacheEnabled(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAppCacheEnabled != flag) {
                this.mAppCacheEnabled = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public void setAppCachePath(String path) {
        boolean needToSync = $assertionsDisabled;
        synchronized (sGlobalContentSettingsLock) {
            if (!(sAppCachePathIsSet || path == null || path.isEmpty())) {
                sAppCachePathIsSet = true;
                needToSync = true;
            }
        }
        if (needToSync) {
            synchronized (this.mXWalkSettingsLock) {
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @CalledByNative
    private boolean getAppCacheEnabled() {
        return this.mAppCacheEnabled;
    }

    public void setDomStorageEnabled(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mDomStorageEnabled != flag) {
                this.mDomStorageEnabled = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public boolean getDomStorageEnabled() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mDomStorageEnabled;
        }
        return z;
    }

    public void setDatabaseEnabled(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mDatabaseEnabled != flag) {
                this.mDatabaseEnabled = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public boolean getDatabaseEnabled() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mDatabaseEnabled;
        }
        return z;
    }

    public void setMediaPlaybackRequiresUserGesture(boolean require) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mMediaPlaybackRequiresUserGesture != require) {
                this.mMediaPlaybackRequiresUserGesture = require;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public boolean getMediaPlaybackRequiresUserGesture() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mMediaPlaybackRequiresUserGesture;
        }
        return z;
    }

    public void setDefaultVideoPosterURL(String url) {
        synchronized (this.mXWalkSettingsLock) {
            if (!(this.mDefaultVideoPosterURL == null || this.mDefaultVideoPosterURL.equals(url)) || (this.mDefaultVideoPosterURL == null && url != null)) {
                this.mDefaultVideoPosterURL = url;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public static String getDefaultUserAgent() {
        return LazyDefaultUserAgent.sInstance;
    }

    public void setUserAgentString(String ua) {
        synchronized (this.mXWalkSettingsLock) {
            String oldUserAgent = this.mUserAgent;
            if (ua == null || ua.length() == 0) {
                this.mUserAgent = LazyDefaultUserAgent.sInstance;
            } else {
                this.mUserAgent = ua;
            }
            if (!oldUserAgent.equals(this.mUserAgent)) {
                this.mEventHandler.maybeRunOnUiThreadBlocking(new C04691());
            }
        }
    }

    public String getUserAgentString() {
        String str;
        synchronized (this.mXWalkSettingsLock) {
            str = this.mUserAgent;
        }
        return str;
    }

    @CalledByNative
    private String getUserAgentLocked() {
        return this.mUserAgent;
    }

    public String getDefaultVideoPosterURL() {
        String str;
        synchronized (this.mXWalkSettingsLock) {
            str = this.mDefaultVideoPosterURL;
        }
        return str;
    }

    @CalledByNative
    private void updateEverything() {
        synchronized (this.mXWalkSettingsLock) {
            nativeUpdateEverythingLocked(this.mNativeXWalkSettings);
        }
    }

    private void updateWebkitPreferencesOnUiThread() {
        if (this.mNativeXWalkSettings != 0) {
            ThreadUtils.assertOnUiThread();
            nativeUpdateWebkitPreferences(this.mNativeXWalkSettings);
        }
    }

    public void setAcceptLanguages(String acceptLanguages) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAcceptLanguages == acceptLanguages) {
                return;
            }
            this.mAcceptLanguages = acceptLanguages;
            this.mEventHandler.maybeRunOnUiThreadBlocking(new C04702());
        }
    }

    public String getAcceptLanguages() {
        String str;
        synchronized (this.mXWalkSettingsLock) {
            str = this.mAcceptLanguages;
        }
        return str;
    }

    public void setSaveFormData(boolean enable) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAutoCompleteEnabled == enable) {
                return;
            }
            this.mAutoCompleteEnabled = enable;
            this.mEventHandler.maybeRunOnUiThreadBlocking(new C04713());
        }
    }

    public boolean getSaveFormData() {
        boolean saveFormDataLocked;
        synchronized (this.mXWalkSettingsLock) {
            saveFormDataLocked = getSaveFormDataLocked();
        }
        return saveFormDataLocked;
    }

    @CalledByNative
    private String getAcceptLanguagesLocked() {
        return this.mAcceptLanguages;
    }

    @CalledByNative
    private boolean getSaveFormDataLocked() {
        return this.mAutoCompleteEnabled;
    }

    void setDIPScale(double dipScale) {
        synchronized (this.mXWalkSettingsLock) {
            this.mDIPScale = dipScale;
        }
    }

    public void setInitialPageScale(float scaleInPercent) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mInitialPageScalePercent == scaleInPercent) {
                return;
            }
            this.mInitialPageScalePercent = scaleInPercent;
            this.mEventHandler.maybeRunOnUiThreadBlocking(new C04724());
        }
    }

    @CalledByNative
    private float getInitialPageScalePercentLocked() {
        if ($assertionsDisabled || Thread.holdsLock(this.mXWalkSettingsLock)) {
            return this.mInitialPageScalePercent;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private double getDIPScaleLocked() {
        if ($assertionsDisabled || Thread.holdsLock(this.mXWalkSettingsLock)) {
            return this.mDIPScale;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private boolean getPasswordEchoEnabledLocked() {
        if ($assertionsDisabled || Thread.holdsLock(this.mXWalkSettingsLock)) {
            return this.mPasswordEchoEnabled;
        }
        throw new AssertionError();
    }
}
