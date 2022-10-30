package org.chromium.content_public.browser;

import android.os.Parcelable;
import org.chromium.base.VisibleForTesting;

public interface WebContents extends Parcelable {
    void addMessageToDevToolsConsole(int i, String str);

    void addObserver(WebContentsObserver webContentsObserver);

    void adjustSelectionByCharacterOffset(int i, int i2);

    void copy();

    void cut();

    void destroy();

    @VisibleForTesting
    void evaluateJavaScript(String str, JavaScriptCallback javaScriptCallback);

    void exitFullscreen();

    int getBackgroundColor();

    String getLastCommittedUrl();

    NavigationController getNavigationController();

    int getThemeColor(int i);

    String getTitle();

    String getUrl();

    String getVisibleUrl();

    boolean hasAccessedInitialDocument();

    void insertCSS(String str);

    boolean isDestroyed();

    boolean isIncognito();

    boolean isLoading();

    boolean isLoadingToDifferentDocument();

    boolean isReady();

    boolean isShowingInterstitialPage();

    void onHide();

    void onShow();

    void paste();

    void releaseMediaPlayers();

    void removeObserver(WebContentsObserver webContentsObserver);

    void requestAccessibilitySnapshot(AccessibilitySnapshotCallback accessibilitySnapshotCallback, float f, float f2);

    void resumeLoadingCreatedWebContents();

    void resumeMediaSession();

    void scrollFocusedEditableNodeIntoView();

    void selectAll();

    void selectWordAroundCaret();

    void showImeIfNeeded();

    @VisibleForTesting
    void showInterstitialPage(String str, long j);

    void stop();

    void suspendMediaSession();

    void unselect();

    void updateTopControlsState(boolean z, boolean z2, boolean z3);
}
