package org.chromium.content.browser.webcontents;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.ObserverList;
import org.chromium.base.ObserverList.RewindableIterator;
import org.chromium.base.ThreadUtils;
import org.chromium.content_public.browser.WebContentsObserver;

@JNINamespace("content")
class WebContentsObserverProxy extends WebContentsObserver {
    static final /* synthetic */ boolean $assertionsDisabled;
    private long mNativeWebContentsObserverProxy;
    private final ObserverList<WebContentsObserver> mObservers;
    private final RewindableIterator<WebContentsObserver> mObserversIterator;

    private native void nativeDestroy(long j);

    private native long nativeInit(WebContentsImpl webContentsImpl);

    static {
        $assertionsDisabled = !WebContentsObserverProxy.class.desiredAssertionStatus();
    }

    public WebContentsObserverProxy(WebContentsImpl webContents) {
        ThreadUtils.assertOnUiThread();
        this.mNativeWebContentsObserverProxy = nativeInit(webContents);
        this.mObservers = new ObserverList();
        this.mObserversIterator = this.mObservers.rewindableIterator();
    }

    void addObserver(WebContentsObserver observer) {
        if ($assertionsDisabled || this.mNativeWebContentsObserverProxy != 0) {
            this.mObservers.addObserver(observer);
            return;
        }
        throw new AssertionError();
    }

    void removeObserver(WebContentsObserver observer) {
        this.mObservers.removeObserver(observer);
    }

    boolean hasObservers() {
        return !this.mObservers.isEmpty();
    }

    @CalledByNative
    public void renderViewReady() {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).renderViewReady();
        }
    }

    @CalledByNative
    public void renderProcessGone(boolean wasOomProtected) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).renderProcessGone(wasOomProtected);
        }
    }

    @CalledByNative
    public void didStartLoading(String url) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didStartLoading(url);
        }
    }

    @CalledByNative
    public void didStopLoading(String url) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didStopLoading(url);
        }
    }

    @CalledByNative
    public void didFailLoad(boolean isProvisionalLoad, boolean isMainFrame, int errorCode, String description, String failingUrl, boolean wasIgnoredByHandler) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didFailLoad(isProvisionalLoad, isMainFrame, errorCode, description, failingUrl, wasIgnoredByHandler);
        }
    }

    @CalledByNative
    public void didNavigateMainFrame(String url, String baseUrl, boolean isNavigationToDifferentPage, boolean isFragmentNavigation, int statusCode) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didNavigateMainFrame(url, baseUrl, isNavigationToDifferentPage, isFragmentNavigation, statusCode);
        }
    }

    @CalledByNative
    public void didFirstVisuallyNonEmptyPaint() {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didFirstVisuallyNonEmptyPaint();
        }
    }

    @CalledByNative
    public void didNavigateAnyFrame(String url, String baseUrl, boolean isReload) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didNavigateAnyFrame(url, baseUrl, isReload);
        }
    }

    @CalledByNative
    public void documentAvailableInMainFrame() {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).documentAvailableInMainFrame();
        }
    }

    @CalledByNative
    public void didStartProvisionalLoadForFrame(long frameId, long parentFrameId, boolean isMainFrame, String validatedUrl, boolean isErrorPage, boolean isIframeSrcdoc) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didStartProvisionalLoadForFrame(frameId, parentFrameId, isMainFrame, validatedUrl, isErrorPage, isIframeSrcdoc);
        }
    }

    @CalledByNative
    public void didCommitProvisionalLoadForFrame(long frameId, boolean isMainFrame, String url, int transitionType) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didCommitProvisionalLoadForFrame(frameId, isMainFrame, url, transitionType);
        }
    }

    @CalledByNative
    public void didFinishLoad(long frameId, String validatedUrl, boolean isMainFrame) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didFinishLoad(frameId, validatedUrl, isMainFrame);
        }
    }

    @CalledByNative
    public void documentLoadedInFrame(long frameId) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).documentLoadedInFrame(frameId);
        }
    }

    @CalledByNative
    public void navigationEntryCommitted() {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).navigationEntryCommitted();
        }
    }

    @CalledByNative
    public void didAttachInterstitialPage() {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didAttachInterstitialPage();
        }
    }

    @CalledByNative
    public void didDetachInterstitialPage() {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didDetachInterstitialPage();
        }
    }

    @CalledByNative
    public void didChangeThemeColor(int color) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didChangeThemeColor(color);
        }
    }

    @CalledByNative
    public void didStartNavigationToPendingEntry(String url) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).didStartNavigationToPendingEntry(url);
        }
    }

    @CalledByNative
    public void mediaSessionStateChanged(boolean isControllable, boolean isSuspended) {
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).mediaSessionStateChanged(isControllable, isSuspended);
        }
    }

    @CalledByNative
    public void destroy() {
        ThreadUtils.assertOnUiThread();
        this.mObserversIterator.rewind();
        while (this.mObserversIterator.hasNext()) {
            ((WebContentsObserver) this.mObserversIterator.next()).destroy();
        }
        if ($assertionsDisabled || this.mObservers.isEmpty()) {
            this.mObservers.clear();
            if (this.mNativeWebContentsObserverProxy != 0) {
                nativeDestroy(this.mNativeWebContentsObserverProxy);
                this.mNativeWebContentsObserverProxy = 0;
                return;
            }
            return;
        }
        throw new AssertionError();
    }
}
