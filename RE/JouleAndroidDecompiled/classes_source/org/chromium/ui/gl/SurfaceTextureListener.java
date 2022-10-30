package org.chromium.ui.gl;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import org.chromium.base.JNINamespace;

@JNINamespace("gfx")
class SurfaceTextureListener implements OnFrameAvailableListener {
    static final /* synthetic */ boolean $assertionsDisabled;
    private final long mNativeSurfaceTextureListener;

    private native void nativeDestroy(long j);

    private native void nativeFrameAvailable(long j);

    static {
        $assertionsDisabled = !SurfaceTextureListener.class.desiredAssertionStatus();
    }

    SurfaceTextureListener(long nativeSurfaceTextureListener) {
        if ($assertionsDisabled || nativeSurfaceTextureListener != 0) {
            this.mNativeSurfaceTextureListener = nativeSurfaceTextureListener;
            return;
        }
        throw new AssertionError();
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        nativeFrameAvailable(this.mNativeSurfaceTextureListener);
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestroy(this.mNativeSurfaceTextureListener);
        } finally {
            super.finalize();
        }
    }
}
