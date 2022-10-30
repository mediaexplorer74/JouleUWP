package org.chromium.content.browser;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.util.SparseArray;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.ThreadUtils;
import org.chromium.ui.base.WindowAndroid;

@JNINamespace("content")
public abstract class ContentReadbackHandler {
    static final /* synthetic */ boolean $assertionsDisabled;
    private SparseArray<GetBitmapCallback> mGetBitmapRequests;
    private long mNativeContentReadbackHandler;
    private int mNextReadbackId;

    /* renamed from: org.chromium.content.browser.ContentReadbackHandler.1 */
    class C03311 implements Runnable {
        final /* synthetic */ GetBitmapCallback val$callback;

        C03311(GetBitmapCallback getBitmapCallback) {
            this.val$callback = getBitmapCallback;
        }

        public void run() {
            this.val$callback.onFinishGetBitmap(null, 2);
        }
    }

    public interface GetBitmapCallback {
        void onFinishGetBitmap(Bitmap bitmap, int i);
    }

    private native void nativeDestroy(long j);

    private native void nativeGetCompositorBitmap(long j, int i, long j2);

    private native void nativeGetContentBitmap(long j, int i, float f, Config config, float f2, float f3, float f4, float f5, Object obj);

    private native long nativeInit();

    protected abstract boolean readyForReadback();

    static {
        $assertionsDisabled = !ContentReadbackHandler.class.desiredAssertionStatus();
    }

    public ContentReadbackHandler() {
        this.mNextReadbackId = 1;
        this.mGetBitmapRequests = new SparseArray();
    }

    public void initNativeContentReadbackHandler() {
        this.mNativeContentReadbackHandler = nativeInit();
    }

    public void destroy() {
        if (this.mNativeContentReadbackHandler != 0) {
            nativeDestroy(this.mNativeContentReadbackHandler);
        }
        this.mNativeContentReadbackHandler = 0;
    }

    @CalledByNative
    private void notifyGetBitmapFinished(int readbackId, Bitmap bitmap, int response) {
        GetBitmapCallback callback = (GetBitmapCallback) this.mGetBitmapRequests.get(readbackId);
        if (callback != null) {
            this.mGetBitmapRequests.delete(readbackId);
            callback.onFinishGetBitmap(bitmap, response);
        } else if (!$assertionsDisabled) {
            throw new AssertionError("Readback finished for unregistered Id: " + readbackId);
        }
    }

    public void getContentBitmapAsync(float scale, Rect srcRect, ContentViewCore view, Config config, GetBitmapCallback callback) {
        if (readyForReadback()) {
            ThreadUtils.assertOnUiThread();
            int readbackId = this.mNextReadbackId;
            this.mNextReadbackId = readbackId + 1;
            this.mGetBitmapRequests.put(readbackId, callback);
            nativeGetContentBitmap(this.mNativeContentReadbackHandler, readbackId, scale, config, (float) srcRect.top, (float) srcRect.left, (float) srcRect.width(), (float) srcRect.height(), view);
            return;
        }
        callback.onFinishGetBitmap(null, 2);
    }

    public void getCompositorBitmapAsync(WindowAndroid windowAndroid, GetBitmapCallback callback) {
        if (readyForReadback()) {
            ThreadUtils.assertOnUiThread();
            int readbackId = this.mNextReadbackId;
            this.mNextReadbackId = readbackId + 1;
            this.mGetBitmapRequests.put(readbackId, callback);
            nativeGetCompositorBitmap(this.mNativeContentReadbackHandler, readbackId, windowAndroid.getNativePointer());
            return;
        }
        ThreadUtils.postOnUiThread(new C03311(callback));
    }
}
