package org.chromium.content.browser;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.ui.base.WindowAndroid;

@JNINamespace("content")
public class ContentViewRenderView extends FrameLayout {
    static final /* synthetic */ boolean $assertionsDisabled;
    private CompositingSurfaceType mCompositingSurfaceType;
    private ContentReadbackHandler mContentReadbackHandler;
    protected ContentViewCore mContentViewCore;
    private boolean mFirstFrameReceived;
    private FirstRenderedFrameListener mFirstRenderedFrameListener;
    private long mNativeContentViewRenderView;
    private Surface mSurface;
    private Callback mSurfaceCallback;
    private final SurfaceView mSurfaceView;
    private TextureView mTextureView;

    /* renamed from: org.chromium.content.browser.ContentViewRenderView.1 */
    class C03381 implements SurfaceTextureListener {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !ContentViewRenderView.class.desiredAssertionStatus();
        }

        C03381() {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            if ($assertionsDisabled || ContentViewRenderView.this.mNativeContentViewRenderView != 0) {
                ContentViewRenderView.this.mSurface = new Surface(surfaceTexture);
                ContentViewRenderView.this.nativeSurfaceCreated(ContentViewRenderView.this.mNativeContentViewRenderView);
                onSurfaceTextureSizeChanged(surfaceTexture, width, height);
                ContentViewRenderView.this.onReadyToRender();
                return;
            }
            throw new AssertionError();
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            if (!$assertionsDisabled && (ContentViewRenderView.this.mNativeContentViewRenderView == 0 || ContentViewRenderView.this.mSurface == null)) {
                throw new AssertionError();
            } else if (!$assertionsDisabled && surfaceTexture != ContentViewRenderView.this.mTextureView.getSurfaceTexture()) {
                throw new AssertionError();
            } else if ($assertionsDisabled || ContentViewRenderView.this.mSurface != null) {
                ContentViewRenderView.this.nativeSurfaceChanged(ContentViewRenderView.this.mNativeContentViewRenderView, 1, width, height, ContentViewRenderView.this.mSurface);
                if (ContentViewRenderView.this.mContentViewCore != null) {
                    ContentViewRenderView.this.mContentViewCore.onPhysicalBackingSizeChanged(width, height);
                }
            } else {
                throw new AssertionError();
            }
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if ($assertionsDisabled || ContentViewRenderView.this.mNativeContentViewRenderView != 0) {
                ContentViewRenderView.this.nativeSurfaceDestroyed(ContentViewRenderView.this.mNativeContentViewRenderView);
                ContentViewRenderView.this.mSurface.release();
                ContentViewRenderView.this.mSurface = null;
                return true;
            }
            throw new AssertionError();
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    }

    /* renamed from: org.chromium.content.browser.ContentViewRenderView.3 */
    class C03393 implements Callback {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !ContentViewRenderView.class.desiredAssertionStatus();
        }

        C03393() {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if ($assertionsDisabled || ContentViewRenderView.this.mNativeContentViewRenderView != 0) {
                ContentViewRenderView.this.nativeSurfaceChanged(ContentViewRenderView.this.mNativeContentViewRenderView, format, width, height, holder.getSurface());
                if (ContentViewRenderView.this.mContentViewCore != null) {
                    ContentViewRenderView.this.mContentViewCore.onPhysicalBackingSizeChanged(width, height);
                    return;
                }
                return;
            }
            throw new AssertionError();
        }

        public void surfaceCreated(SurfaceHolder holder) {
            if ($assertionsDisabled || ContentViewRenderView.this.mNativeContentViewRenderView != 0) {
                ContentViewRenderView.this.nativeSurfaceCreated(ContentViewRenderView.this.mNativeContentViewRenderView);
                ContentViewRenderView.this.onReadyToRender();
                return;
            }
            throw new AssertionError();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if ($assertionsDisabled || ContentViewRenderView.this.mNativeContentViewRenderView != 0) {
                ContentViewRenderView.this.nativeSurfaceDestroyed(ContentViewRenderView.this.mNativeContentViewRenderView);
                return;
            }
            throw new AssertionError();
        }
    }

    /* renamed from: org.chromium.content.browser.ContentViewRenderView.4 */
    class C03404 implements Runnable {
        C03404() {
        }

        public void run() {
            ContentViewRenderView.this.mSurfaceView.setBackgroundResource(0);
        }
    }

    public enum CompositingSurfaceType {
        SURFACE_VIEW,
        TEXTURE_VIEW
    }

    public interface FirstRenderedFrameListener {
        void onFirstFrameReceived();
    }

    /* renamed from: org.chromium.content.browser.ContentViewRenderView.2 */
    class C06082 extends ContentReadbackHandler {
        C06082() {
        }

        protected boolean readyForReadback() {
            return (ContentViewRenderView.this.mNativeContentViewRenderView == 0 || ContentViewRenderView.this.mContentViewCore == null) ? false : true;
        }
    }

    private native void nativeDestroy(long j);

    private native long nativeGetUIResourceProvider(long j);

    private native long nativeInit(long j);

    private native void nativeSetCurrentContentViewCore(long j, long j2);

    private native void nativeSetNeedsComposite(long j);

    private native void nativeSetOverlayVideoMode(long j, boolean z);

    private native void nativeSurfaceChanged(long j, int i, int i2, int i3, Surface surface);

    private native void nativeSurfaceCreated(long j);

    private native void nativeSurfaceDestroyed(long j);

    static {
        $assertionsDisabled = !ContentViewRenderView.class.desiredAssertionStatus();
    }

    private void initTextureView(Context context) {
        this.mTextureView = new TextureView(context);
        this.mTextureView.setBackgroundColor(-1);
        this.mTextureView.setSurfaceTextureListener(new C03381());
    }

    public ContentViewRenderView(Context context) {
        this(context, CompositingSurfaceType.SURFACE_VIEW);
    }

    public ContentViewRenderView(Context context, CompositingSurfaceType surfaceType) {
        super(context);
        this.mCompositingSurfaceType = surfaceType;
        if (surfaceType == CompositingSurfaceType.TEXTURE_VIEW) {
            initTextureView(context);
            addView(this.mTextureView, new LayoutParams(-1, -1));
            this.mSurfaceView = null;
            this.mSurfaceCallback = null;
            return;
        }
        this.mSurfaceView = createSurfaceView(getContext());
        this.mSurfaceView.setZOrderMediaOverlay(true);
        setSurfaceViewBackgroundColor(-1);
        addView(this.mSurfaceView, new LayoutParams(-1, -1));
        this.mSurfaceView.setVisibility(8);
    }

    public void onNativeLibraryLoaded(WindowAndroid rootWindow) {
        if ($assertionsDisabled || rootWindow != null) {
            this.mNativeContentViewRenderView = nativeInit(rootWindow.getNativePointer());
            if ($assertionsDisabled || this.mNativeContentViewRenderView != 0) {
                this.mContentReadbackHandler = new C06082();
                this.mContentReadbackHandler.initNativeContentReadbackHandler();
                if (this.mCompositingSurfaceType != CompositingSurfaceType.TEXTURE_VIEW) {
                    if ($assertionsDisabled || !this.mSurfaceView.getHolder().getSurface().isValid()) {
                        this.mSurfaceCallback = new C03393();
                        this.mSurfaceView.getHolder().addCallback(this.mSurfaceCallback);
                        this.mSurfaceView.setVisibility(0);
                        return;
                    }
                    throw new AssertionError("Surface created before native library loaded.");
                }
                return;
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    public ContentReadbackHandler getContentReadbackHandler() {
        return this.mContentReadbackHandler;
    }

    public void setSurfaceViewBackgroundColor(int color) {
        if (this.mSurfaceView != null) {
            this.mSurfaceView.setBackgroundColor(color);
        }
    }

    public SurfaceView getSurfaceView() {
        return this.mSurfaceView;
    }

    public void destroy() {
        this.mContentReadbackHandler.destroy();
        this.mContentReadbackHandler = null;
        if (this.mCompositingSurfaceType == CompositingSurfaceType.TEXTURE_VIEW) {
            this.mTextureView.setSurfaceTextureListener(null);
            if (this.mSurface != null) {
                this.mSurface.release();
                this.mSurface = null;
            }
        } else {
            this.mSurfaceView.getHolder().removeCallback(this.mSurfaceCallback);
        }
        nativeDestroy(this.mNativeContentViewRenderView);
        this.mNativeContentViewRenderView = 0;
    }

    public void setCurrentContentViewCore(ContentViewCore contentViewCore) {
        if ($assertionsDisabled || this.mNativeContentViewRenderView != 0) {
            this.mContentViewCore = contentViewCore;
            if (this.mContentViewCore != null) {
                this.mContentViewCore.onPhysicalBackingSizeChanged(getWidth(), getHeight());
                nativeSetCurrentContentViewCore(this.mNativeContentViewRenderView, this.mContentViewCore.getNativeContentViewCore());
                return;
            }
            nativeSetCurrentContentViewCore(this.mNativeContentViewRenderView, 0);
            return;
        }
        throw new AssertionError();
    }

    public void setNeedsComposite() {
        if (this.mNativeContentViewRenderView != 0) {
            nativeSetNeedsComposite(this.mNativeContentViewRenderView);
        }
    }

    protected void onReadyToRender() {
    }

    protected SurfaceView createSurfaceView(Context context) {
        return new SurfaceView(context);
    }

    public void registerFirstRenderedFrameListener(FirstRenderedFrameListener listener) {
        this.mFirstRenderedFrameListener = listener;
        if (this.mFirstFrameReceived && this.mFirstRenderedFrameListener != null) {
            this.mFirstRenderedFrameListener.onFirstFrameReceived();
        }
    }

    public boolean isInitialized() {
        return (this.mSurfaceView.getHolder().getSurface() == null && this.mSurface == null) ? false : true;
    }

    public void setZOrderOnTop(boolean onTop) {
        if (this.mSurfaceView != null) {
            this.mSurfaceView.setZOrderOnTop(onTop);
        }
    }

    public void setOverlayVideoMode(boolean enabled) {
        if (this.mCompositingSurfaceType == CompositingSurfaceType.TEXTURE_VIEW) {
            nativeSetOverlayVideoMode(this.mNativeContentViewRenderView, enabled);
            return;
        }
        this.mSurfaceView.getHolder().setFormat(enabled ? -3 : -1);
        nativeSetOverlayVideoMode(this.mNativeContentViewRenderView, enabled);
    }

    @CalledByNative
    protected void onCompositorLayout() {
    }

    @CalledByNative
    private void onSwapBuffersCompleted() {
        if (!(this.mFirstFrameReceived || this.mContentViewCore == null || !this.mContentViewCore.getWebContents().isReady())) {
            this.mFirstFrameReceived = true;
            if (this.mFirstRenderedFrameListener != null) {
                this.mFirstRenderedFrameListener.onFirstFrameReceived();
            }
        }
        if (this.mCompositingSurfaceType != CompositingSurfaceType.TEXTURE_VIEW && this.mSurfaceView.getBackground() != null) {
            post(new C03404());
        }
    }

    public long getUIResourceProvider() {
        return nativeGetUIResourceProvider(this.mNativeContentViewRenderView);
    }
}
