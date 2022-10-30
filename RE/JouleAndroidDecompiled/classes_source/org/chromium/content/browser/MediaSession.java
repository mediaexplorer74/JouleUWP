package org.chromium.content.browser;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.support.v4.widget.ExploreByTouchHelper;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.xwalk.core.internal.XWalkResourceClientInternal;

@JNINamespace("content")
public class MediaSession implements OnAudioFocusChangeListener {
    private static final String TAG = "cr.MediaSession";
    private Context mContext;
    private int mFocusType;
    private final long mNativeMediaSession;

    private native void nativeOnResume(long j);

    private native void nativeOnSuspend(long j, boolean z);

    private MediaSession(Context context, long nativeMediaSession) {
        this.mContext = context;
        this.mNativeMediaSession = nativeMediaSession;
    }

    @CalledByNative
    private static MediaSession createMediaSession(Context context, long nativeMediaSession) {
        return new MediaSession(context, nativeMediaSession);
    }

    @CalledByNative
    private boolean requestAudioFocus(boolean transientFocus) {
        this.mFocusType = transientFocus ? 3 : 1;
        return requestAudioFocusInternal();
    }

    @CalledByNative
    private void abandonAudioFocus() {
        ((AudioManager) this.mContext.getSystemService("audio")).abandonAudioFocus(this);
    }

    private boolean requestAudioFocusInternal() {
        if (((AudioManager) this.mContext.getSystemService("audio")).requestAudioFocus(this, 3, this.mFocusType) == 1) {
            return true;
        }
        return false;
    }

    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case XWalkResourceClientInternal.ERROR_UNSUPPORTED_AUTH_SCHEME /*-3*/:
            case XWalkResourceClientInternal.ERROR_HOST_LOOKUP /*-2*/:
                nativeOnSuspend(this.mNativeMediaSession, true);
            case ExploreByTouchHelper.HOST_ID /*-1*/:
                abandonAudioFocus();
                nativeOnSuspend(this.mNativeMediaSession, false);
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                if (requestAudioFocusInternal()) {
                    nativeOnResume(this.mNativeMediaSession);
                }
            default:
                Log.m42w(TAG, "onAudioFocusChange called with unexpected value %d", Integer.valueOf(focusChange));
        }
    }
}
