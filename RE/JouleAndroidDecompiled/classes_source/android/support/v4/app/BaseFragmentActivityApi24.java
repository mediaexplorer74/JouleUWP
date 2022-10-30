package android.support.v4.app;

import android.support.annotation.CallSuper;
import android.support.v4.os.BuildCompat;

abstract class BaseFragmentActivityApi24 extends BaseFragmentActivityHoneycomb {
    abstract void dispatchFragmentsOnMultiWindowModeChanged(boolean z);

    abstract void dispatchFragmentsOnPictureInPictureModeChanged(boolean z);

    BaseFragmentActivityApi24() {
    }

    @CallSuper
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        if (BuildCompat.isAtLeastN()) {
            super.onMultiWindowModeChanged(isInMultiWindowMode);
        }
        dispatchFragmentsOnMultiWindowModeChanged(isInMultiWindowMode);
    }

    @CallSuper
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        if (BuildCompat.isAtLeastN()) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        }
        dispatchFragmentsOnPictureInPictureModeChanged(isInPictureInPictureMode);
    }
}
