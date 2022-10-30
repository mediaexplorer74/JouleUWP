package org.chromium.content.browser;

import android.view.ActionMode;
import org.chromium.base.Log;

public class SelectActionMode {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String TAG = "cr.SelectActionMode";
    protected final ActionMode mActionMode;

    static {
        $assertionsDisabled = !SelectActionMode.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public SelectActionMode(ActionMode actionMode) {
        if ($assertionsDisabled || actionMode != null) {
            this.mActionMode = actionMode;
            return;
        }
        throw new AssertionError();
    }

    public void finish() {
        this.mActionMode.finish();
    }

    public void invalidate() {
        try {
            this.mActionMode.invalidate();
        } catch (NullPointerException e) {
            Log.m42w(TAG, "Ignoring NPE from ActionMode.invalidate() as workaround for L", e);
        }
    }

    public void invalidateContentRect() {
    }
}
