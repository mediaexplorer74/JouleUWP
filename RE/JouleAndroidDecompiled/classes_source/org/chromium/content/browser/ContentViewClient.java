package org.chromium.content.browser;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.View;
import org.chromium.base.Log;
import org.chromium.content.browser.SelectActionModeCallback.ActionHandler;

public class ContentViewClient {
    private static final String TAG = "cr.ContentViewClient";

    public void onUpdateTitle(String title) {
    }

    public void onBackgroundColorChanged(int color) {
    }

    public void onOffsetsForFullscreenChanged(float topControlsOffsetYPix, float contentOffsetYPix, float overdrawBottomHeightPix) {
    }

    public boolean shouldOverrideKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (!shouldPropagateKey(keyCode)) {
            return true;
        }
        if (event.isCtrlPressed() && (keyCode == 61 || keyCode == 51 || keyCode == 134)) {
            return true;
        }
        return false;
    }

    public void onImeEvent() {
    }

    public void onFocusedNodeEditabilityChanged(boolean editable) {
    }

    public SelectActionMode startActionMode(View view, ActionHandler actionHandler, boolean floating) {
        if (floating) {
            return null;
        }
        ActionMode actionMode = view.startActionMode(new SelectActionModeCallback(view.getContext(), actionHandler));
        if (actionMode != null) {
            return new SelectActionMode(actionMode);
        }
        return null;
    }

    public boolean supportsFloatingActionMode() {
        return false;
    }

    public void onContextualActionBarShown() {
    }

    public void onContextualActionBarHidden() {
    }

    public void performWebSearch(String searchQuery) {
    }

    public boolean doesPerformWebSearch() {
        return false;
    }

    public void onStartContentIntent(Context context, String intentUrl) {
        try {
            try {
                context.startActivity(Intent.parseUri(intentUrl, 1));
            } catch (ActivityNotFoundException e) {
                Log.m42w(TAG, "No application can handle %s", intentUrl);
            }
        } catch (Exception ex) {
            Log.m42w(TAG, "Bad URI %s", intentUrl, ex);
        }
    }

    public ContentVideoViewClient getContentVideoViewClient() {
        return null;
    }

    public boolean shouldBlockMediaRequest(String url) {
        return false;
    }

    public boolean isJavascriptEnabled() {
        return true;
    }

    public boolean isExternalScrollActive() {
        return false;
    }

    public static boolean shouldPropagateKey(int keyCode) {
        if (keyCode == 82 || keyCode == 3 || keyCode == 4 || keyCode == 5 || keyCode == 6 || keyCode == 26 || keyCode == 79 || keyCode == 27 || keyCode == 80 || keyCode == 25 || keyCode == 164 || keyCode == 24) {
            return false;
        }
        return true;
    }
}
