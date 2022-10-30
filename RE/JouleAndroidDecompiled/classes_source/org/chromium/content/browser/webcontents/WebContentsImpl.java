package org.chromium.content.browser.webcontents;

import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable.Creator;
import android.support.v4.view.ViewCompat;
import java.util.UUID;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.VisibleForTesting;
import org.chromium.content_public.browser.AccessibilitySnapshotCallback;
import org.chromium.content_public.browser.AccessibilitySnapshotNode;
import org.chromium.content_public.browser.JavaScriptCallback;
import org.chromium.content_public.browser.NavigationController;
import org.chromium.content_public.browser.WebContents;
import org.chromium.content_public.browser.WebContentsObserver;

@JNINamespace("content")
class WebContentsImpl implements WebContents {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final Creator<WebContents> CREATOR;
    private static final long PARCELABLE_VERSION_ID = 0;
    private static final String PARCEL_PROCESS_GUARD_KEY = "processguard";
    private static final String PARCEL_VERSION_KEY = "version";
    private static final String PARCEL_WEBCONTENTS_KEY = "webcontents";
    private static UUID sParcelableUUID;
    private long mNativeWebContentsAndroid;
    private NavigationController mNavigationController;
    private WebContentsObserverProxy mObserverProxy;

    /* renamed from: org.chromium.content.browser.webcontents.WebContentsImpl.1 */
    static class C03701 implements Creator<WebContents> {
        C03701() {
        }

        public WebContents createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();
            if (bundle.getLong(WebContentsImpl.PARCEL_VERSION_KEY, -1) != WebContentsImpl.PARCELABLE_VERSION_ID) {
                return null;
            }
            if (WebContentsImpl.sParcelableUUID.compareTo(((ParcelUuid) bundle.getParcelable(WebContentsImpl.PARCEL_PROCESS_GUARD_KEY)).getUuid()) == 0) {
                return WebContentsImpl.nativeFromNativePtr(bundle.getLong(WebContentsImpl.PARCEL_WEBCONTENTS_KEY));
            }
            return null;
        }

        public WebContents[] newArray(int size) {
            return new WebContents[size];
        }
    }

    private native void nativeAddMessageToDevToolsConsole(long j, int i, String str);

    private native void nativeAdjustSelectionByCharacterOffset(long j, int i, int i2);

    private native void nativeCopy(long j);

    private native void nativeCut(long j);

    private static native void nativeDestroyWebContents(long j);

    private native void nativeEvaluateJavaScript(long j, String str, JavaScriptCallback javaScriptCallback);

    private native void nativeExitFullscreen(long j);

    private static native WebContents nativeFromNativePtr(long j);

    private native int nativeGetBackgroundColor(long j);

    private native String nativeGetLastCommittedURL(long j);

    private native int nativeGetThemeColor(long j);

    private native String nativeGetTitle(long j);

    private native String nativeGetURL(long j);

    private native String nativeGetVisibleURL(long j);

    private native boolean nativeHasAccessedInitialDocument(long j);

    private native void nativeInsertCSS(long j, String str);

    private native boolean nativeIsIncognito(long j);

    private native boolean nativeIsLoading(long j);

    private native boolean nativeIsLoadingToDifferentDocument(long j);

    private native boolean nativeIsRenderWidgetHostViewReady(long j);

    private native boolean nativeIsShowingInterstitialPage(long j);

    private native void nativeOnHide(long j);

    private native void nativeOnShow(long j);

    private native void nativePaste(long j);

    private native void nativeReleaseMediaPlayers(long j);

    private native void nativeRequestAccessibilitySnapshot(long j, AccessibilitySnapshotCallback accessibilitySnapshotCallback, float f, float f2);

    private native void nativeResumeLoadingCreatedWebContents(long j);

    private native void nativeResumeMediaSession(long j);

    private native void nativeScrollFocusedEditableNodeIntoView(long j);

    private native void nativeSelectAll(long j);

    private native void nativeSelectWordAroundCaret(long j);

    private native void nativeShowImeIfNeeded(long j);

    private native void nativeShowInterstitialPage(long j, String str, long j2);

    private native void nativeStop(long j);

    private native void nativeSuspendMediaSession(long j);

    private native void nativeUnselect(long j);

    private native void nativeUpdateTopControlsState(long j, boolean z, boolean z2, boolean z3);

    static {
        $assertionsDisabled = !WebContentsImpl.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        sParcelableUUID = UUID.randomUUID();
        CREATOR = new C03701();
    }

    @VisibleForTesting
    public static void invalidateSerializedWebContentsForTesting() {
        sParcelableUUID = UUID.randomUUID();
    }

    private WebContentsImpl(long nativeWebContentsAndroid, NavigationController navigationController) {
        this.mNativeWebContentsAndroid = nativeWebContentsAndroid;
        this.mNavigationController = navigationController;
    }

    @CalledByNative
    private static WebContentsImpl create(long nativeWebContentsAndroid, NavigationController navigationController) {
        return new WebContentsImpl(nativeWebContentsAndroid, navigationController);
    }

    @CalledByNative
    private void clearNativePtr() {
        this.mNativeWebContentsAndroid = PARCELABLE_VERSION_ID;
        this.mNavigationController = null;
        if (this.mObserverProxy != null) {
            this.mObserverProxy.destroy();
            this.mObserverProxy = null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Bundle data = new Bundle();
        data.putLong(PARCEL_VERSION_KEY, PARCELABLE_VERSION_ID);
        data.putParcelable(PARCEL_PROCESS_GUARD_KEY, new ParcelUuid(sParcelableUUID));
        data.putLong(PARCEL_WEBCONTENTS_KEY, this.mNativeWebContentsAndroid);
        dest.writeBundle(data);
    }

    @CalledByNative
    private long getNativePointer() {
        return this.mNativeWebContentsAndroid;
    }

    public void destroy() {
        if (this.mNativeWebContentsAndroid != PARCELABLE_VERSION_ID) {
            nativeDestroyWebContents(this.mNativeWebContentsAndroid);
        }
    }

    public boolean isDestroyed() {
        return this.mNativeWebContentsAndroid == PARCELABLE_VERSION_ID ? true : $assertionsDisabled;
    }

    public NavigationController getNavigationController() {
        return this.mNavigationController;
    }

    public String getTitle() {
        return nativeGetTitle(this.mNativeWebContentsAndroid);
    }

    public String getVisibleUrl() {
        return nativeGetVisibleURL(this.mNativeWebContentsAndroid);
    }

    public boolean isLoading() {
        return nativeIsLoading(this.mNativeWebContentsAndroid);
    }

    public boolean isLoadingToDifferentDocument() {
        return nativeIsLoadingToDifferentDocument(this.mNativeWebContentsAndroid);
    }

    public void stop() {
        nativeStop(this.mNativeWebContentsAndroid);
    }

    public void cut() {
        nativeCut(this.mNativeWebContentsAndroid);
    }

    public void copy() {
        nativeCopy(this.mNativeWebContentsAndroid);
    }

    public void paste() {
        nativePaste(this.mNativeWebContentsAndroid);
    }

    public void selectAll() {
        nativeSelectAll(this.mNativeWebContentsAndroid);
    }

    public void unselect() {
        if (this.mNativeWebContentsAndroid != PARCELABLE_VERSION_ID) {
            nativeUnselect(this.mNativeWebContentsAndroid);
        }
    }

    public void insertCSS(String css) {
        if (this.mNativeWebContentsAndroid != PARCELABLE_VERSION_ID) {
            nativeInsertCSS(this.mNativeWebContentsAndroid, css);
        }
    }

    public void onHide() {
        nativeOnHide(this.mNativeWebContentsAndroid);
    }

    public void onShow() {
        nativeOnShow(this.mNativeWebContentsAndroid);
    }

    public void releaseMediaPlayers() {
        nativeReleaseMediaPlayers(this.mNativeWebContentsAndroid);
    }

    public int getBackgroundColor() {
        return nativeGetBackgroundColor(this.mNativeWebContentsAndroid);
    }

    public void showInterstitialPage(String url, long interstitialPageDelegateAndroid) {
        nativeShowInterstitialPage(this.mNativeWebContentsAndroid, url, interstitialPageDelegateAndroid);
    }

    public boolean isShowingInterstitialPage() {
        return nativeIsShowingInterstitialPage(this.mNativeWebContentsAndroid);
    }

    public boolean isReady() {
        return nativeIsRenderWidgetHostViewReady(this.mNativeWebContentsAndroid);
    }

    public void exitFullscreen() {
        nativeExitFullscreen(this.mNativeWebContentsAndroid);
    }

    public void updateTopControlsState(boolean enableHiding, boolean enableShowing, boolean animate) {
        nativeUpdateTopControlsState(this.mNativeWebContentsAndroid, enableHiding, enableShowing, animate);
    }

    public void showImeIfNeeded() {
        nativeShowImeIfNeeded(this.mNativeWebContentsAndroid);
    }

    public void scrollFocusedEditableNodeIntoView() {
        nativeScrollFocusedEditableNodeIntoView(this.mNativeWebContentsAndroid);
    }

    public void selectWordAroundCaret() {
        nativeSelectWordAroundCaret(this.mNativeWebContentsAndroid);
    }

    public void adjustSelectionByCharacterOffset(int startAdjust, int endAdjust) {
        nativeAdjustSelectionByCharacterOffset(this.mNativeWebContentsAndroid, startAdjust, endAdjust);
    }

    public String getUrl() {
        return nativeGetURL(this.mNativeWebContentsAndroid);
    }

    public String getLastCommittedUrl() {
        return nativeGetLastCommittedURL(this.mNativeWebContentsAndroid);
    }

    public boolean isIncognito() {
        return nativeIsIncognito(this.mNativeWebContentsAndroid);
    }

    public void resumeLoadingCreatedWebContents() {
        nativeResumeLoadingCreatedWebContents(this.mNativeWebContentsAndroid);
    }

    @VisibleForTesting
    public void evaluateJavaScript(String script, JavaScriptCallback callback) {
        nativeEvaluateJavaScript(this.mNativeWebContentsAndroid, script, callback);
    }

    public void addMessageToDevToolsConsole(int level, String message) {
        nativeAddMessageToDevToolsConsole(this.mNativeWebContentsAndroid, level, message);
    }

    public boolean hasAccessedInitialDocument() {
        return nativeHasAccessedInitialDocument(this.mNativeWebContentsAndroid);
    }

    @CalledByNative
    private static void onEvaluateJavaScriptResult(String jsonResult, JavaScriptCallback callback) {
        callback.handleJavaScriptResult(jsonResult);
    }

    public int getThemeColor(int defaultColor) {
        int color = nativeGetThemeColor(this.mNativeWebContentsAndroid);
        return color == 0 ? defaultColor : color | ViewCompat.MEASURED_STATE_MASK;
    }

    public void requestAccessibilitySnapshot(AccessibilitySnapshotCallback callback, float offsetY, float scrollX) {
        nativeRequestAccessibilitySnapshot(this.mNativeWebContentsAndroid, callback, offsetY, scrollX);
    }

    public void resumeMediaSession() {
        nativeResumeMediaSession(this.mNativeWebContentsAndroid);
    }

    public void suspendMediaSession() {
        nativeSuspendMediaSession(this.mNativeWebContentsAndroid);
    }

    @CalledByNative
    private static void onAccessibilitySnapshot(AccessibilitySnapshotNode root, AccessibilitySnapshotCallback callback) {
        callback.onAccessibilitySnapshot(root);
    }

    @CalledByNative
    private static void addAccessibilityNodeAsChild(AccessibilitySnapshotNode parent, AccessibilitySnapshotNode child) {
        parent.addChild(child);
    }

    @CalledByNative
    private static AccessibilitySnapshotNode createAccessibilitySnapshotNode(int x, int y, int scrollX, int scrollY, int width, int height, String text, int color, int bgcolor, float size, int textStyle, String className) {
        AccessibilitySnapshotNode node = new AccessibilitySnapshotNode(x, y, scrollX, scrollY, width, height, text, className);
        if (((double) size) >= 0.0d) {
            node.setStyle(color, bgcolor, size, (textStyle & 1) > 0 ? true : $assertionsDisabled, (textStyle & 2) > 0 ? true : $assertionsDisabled, (textStyle & 4) > 0 ? true : $assertionsDisabled, (textStyle & 8) > 0 ? true : $assertionsDisabled);
        }
        return node;
    }

    public void addObserver(WebContentsObserver observer) {
        if ($assertionsDisabled || this.mNativeWebContentsAndroid != PARCELABLE_VERSION_ID) {
            if (this.mObserverProxy == null) {
                this.mObserverProxy = new WebContentsObserverProxy(this);
            }
            this.mObserverProxy.addObserver(observer);
            return;
        }
        throw new AssertionError();
    }

    public void removeObserver(WebContentsObserver observer) {
        if (this.mObserverProxy != null) {
            this.mObserverProxy.removeObserver(observer);
        }
    }
}
