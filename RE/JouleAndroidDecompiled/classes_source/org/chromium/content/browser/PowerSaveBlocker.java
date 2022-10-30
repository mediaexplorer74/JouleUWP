package org.chromium.content.browser;

import android.view.View;
import java.lang.ref.WeakReference;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.ui.base.ViewAndroidDelegate;

@JNINamespace("content")
class PowerSaveBlocker {
    static final /* synthetic */ boolean $assertionsDisabled;
    private WeakReference<View> mKeepScreenOnView;

    static {
        $assertionsDisabled = !PowerSaveBlocker.class.desiredAssertionStatus();
    }

    @CalledByNative
    private static PowerSaveBlocker create() {
        return new PowerSaveBlocker();
    }

    private PowerSaveBlocker() {
    }

    @CalledByNative
    private void applyBlock(ContentViewCore contentViewCore) {
        if ($assertionsDisabled || this.mKeepScreenOnView == null) {
            ViewAndroidDelegate delegate = contentViewCore.getViewAndroidDelegate();
            View anchorView = delegate.acquireAnchorView();
            this.mKeepScreenOnView = new WeakReference(anchorView);
            delegate.setAnchorViewPosition(anchorView, 0.0f, 0.0f, 0.0f, 0.0f);
            anchorView.setKeepScreenOn(true);
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private void removeBlock(ContentViewCore contentViewCore) {
        if ($assertionsDisabled || this.mKeepScreenOnView != null) {
            View anchorView = (View) this.mKeepScreenOnView.get();
            this.mKeepScreenOnView = null;
            if (anchorView != null) {
                ViewAndroidDelegate delegate = contentViewCore.getViewAndroidDelegate();
                anchorView.setKeepScreenOn(false);
                delegate.releaseAnchorView(anchorView);
                return;
            }
            return;
        }
        throw new AssertionError();
    }
}
