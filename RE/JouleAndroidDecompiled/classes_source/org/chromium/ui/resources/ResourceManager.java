package org.chromium.ui.resources;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.SparseArray;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.ui.resources.ResourceLoader.ResourceLoaderCallback;
import org.chromium.ui.resources.dynamics.DynamicResourceLoader;
import org.chromium.ui.resources.statics.StaticResourceLoader;
import org.chromium.ui.resources.system.SystemResourceLoader;

@JNINamespace("ui")
public class ResourceManager implements ResourceLoaderCallback {
    static final /* synthetic */ boolean $assertionsDisabled;
    private final SparseArray<SparseArray<LayoutResource>> mLoadedResources;
    private long mNativeResourceManagerPtr;
    private final float mPxToDp;
    private final SparseArray<ResourceLoader> mResourceLoaders;

    private native void nativeOnResourceReady(long j, int i, int i2, Bitmap bitmap, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

    static {
        $assertionsDisabled = !ResourceManager.class.desiredAssertionStatus();
    }

    private ResourceManager(Context context, long staticResourceManagerPtr) {
        this.mResourceLoaders = new SparseArray();
        this.mLoadedResources = new SparseArray();
        Resources resources = context.getResources();
        this.mPxToDp = 1.0f / resources.getDisplayMetrics().density;
        registerResourceLoader(new StaticResourceLoader(0, this, resources));
        registerResourceLoader(new DynamicResourceLoader(1, this));
        registerResourceLoader(new DynamicResourceLoader(2, this));
        registerResourceLoader(new SystemResourceLoader(3, this, context));
        this.mNativeResourceManagerPtr = staticResourceManagerPtr;
    }

    @CalledByNative
    private static ResourceManager create(Context context, long staticResourceManagerPtr) {
        return new ResourceManager(context, staticResourceManagerPtr);
    }

    public DynamicResourceLoader getDynamicResourceLoader() {
        return (DynamicResourceLoader) this.mResourceLoaders.get(1);
    }

    public DynamicResourceLoader getBitmapDynamicResourceLoader() {
        return (DynamicResourceLoader) this.mResourceLoaders.get(2);
    }

    public void preloadResources(int type, int[] syncIds, int[] asyncIds) {
        ResourceLoader loader = (ResourceLoader) this.mResourceLoaders.get(type);
        if (asyncIds != null) {
            for (int valueOf : asyncIds) {
                loader.preloadResource(Integer.valueOf(valueOf).intValue());
            }
        }
        if (syncIds != null) {
            for (int valueOf2 : syncIds) {
                loader.loadResource(Integer.valueOf(valueOf2).intValue());
            }
        }
    }

    public LayoutResource getResource(int resType, int resId) {
        SparseArray<LayoutResource> bucket = (SparseArray) this.mLoadedResources.get(resType);
        return bucket != null ? (LayoutResource) bucket.get(resId) : null;
    }

    public void onResourceLoaded(int resType, int resId, Resource resource) {
        if (resource != null) {
            saveMetadataForLoadedResource(resType, resId, resource);
            if (this.mNativeResourceManagerPtr != 0) {
                Rect padding = resource.getPadding();
                Rect aperture = resource.getAperture();
                int i = resType;
                int i2 = resId;
                nativeOnResourceReady(this.mNativeResourceManagerPtr, i, i2, resource.getBitmap(), padding.left, padding.top, padding.right, padding.bottom, aperture.left, aperture.top, aperture.right, aperture.bottom);
            }
        }
    }

    private void saveMetadataForLoadedResource(int resType, int resId, Resource resource) {
        SparseArray<LayoutResource> bucket = (SparseArray) this.mLoadedResources.get(resType);
        if (bucket == null) {
            bucket = new SparseArray();
            this.mLoadedResources.put(resType, bucket);
        }
        bucket.put(resId, new LayoutResource(this.mPxToDp, resource));
    }

    @CalledByNative
    private void destroy() {
        if ($assertionsDisabled || this.mNativeResourceManagerPtr != 0) {
            this.mNativeResourceManagerPtr = 0;
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private void resourceRequested(int resType, int resId) {
        ResourceLoader loader = (ResourceLoader) this.mResourceLoaders.get(resType);
        if (loader != null) {
            loader.loadResource(resId);
        }
    }

    @CalledByNative
    private void preloadResource(int resType, int resId) {
        ResourceLoader loader = (ResourceLoader) this.mResourceLoaders.get(resType);
        if (loader != null) {
            loader.preloadResource(resId);
        }
    }

    @CalledByNative
    private long getNativePtr() {
        return this.mNativeResourceManagerPtr;
    }

    private void registerResourceLoader(ResourceLoader loader) {
        this.mResourceLoaders.put(loader.getResourceType(), loader);
    }
}
