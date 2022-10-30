package org.chromium.ui.gl;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.os.Build.VERSION;
import android.util.Log;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;

@JNINamespace("gfx")
class SurfaceTexturePlatformWrapper {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String TAG = "SurfaceTexturePlatformWrapper";

    static {
        $assertionsDisabled = !SurfaceTexturePlatformWrapper.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    SurfaceTexturePlatformWrapper() {
    }

    @CalledByNative
    private static SurfaceTexture create(int textureId) {
        return new SurfaceTexture(textureId);
    }

    @TargetApi(19)
    @CalledByNative
    private static SurfaceTexture createSingleBuffered(int textureId) {
        if ($assertionsDisabled || VERSION.SDK_INT >= 19) {
            return new SurfaceTexture(textureId, true);
        }
        throw new AssertionError();
    }

    @CalledByNative
    private static void destroy(SurfaceTexture surfaceTexture) {
        surfaceTexture.setOnFrameAvailableListener(null);
        surfaceTexture.release();
    }

    @CalledByNative
    private static void setFrameAvailableCallback(SurfaceTexture surfaceTexture, long nativeSurfaceTextureListener) {
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTextureListener(nativeSurfaceTextureListener));
    }

    @CalledByNative
    private static void updateTexImage(SurfaceTexture surfaceTexture) {
        try {
            surfaceTexture.updateTexImage();
        } catch (RuntimeException e) {
            Log.e(TAG, "Error calling updateTexImage", e);
        }
    }

    @TargetApi(19)
    @CalledByNative
    private static void releaseTexImage(SurfaceTexture surfaceTexture) {
        if ($assertionsDisabled || VERSION.SDK_INT >= 19) {
            surfaceTexture.releaseTexImage();
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private static void getTransformMatrix(SurfaceTexture surfaceTexture, float[] matrix) {
        surfaceTexture.getTransformMatrix(matrix);
    }

    @TargetApi(16)
    @CalledByNative
    private static void attachToGLContext(SurfaceTexture surfaceTexture, int texName) {
        if ($assertionsDisabled || VERSION.SDK_INT >= 16) {
            surfaceTexture.attachToGLContext(texName);
            return;
        }
        throw new AssertionError();
    }

    @TargetApi(16)
    @CalledByNative
    private static void detachFromGLContext(SurfaceTexture surfaceTexture) {
        if ($assertionsDisabled || VERSION.SDK_INT >= 16) {
            surfaceTexture.detachFromGLContext();
            return;
        }
        throw new AssertionError();
    }
}
