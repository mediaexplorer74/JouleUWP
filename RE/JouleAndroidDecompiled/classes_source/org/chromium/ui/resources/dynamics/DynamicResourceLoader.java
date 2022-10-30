package org.chromium.ui.resources.dynamics;

import android.util.SparseArray;
import org.chromium.ui.resources.ResourceLoader;
import org.chromium.ui.resources.ResourceLoader.ResourceLoaderCallback;

public class DynamicResourceLoader extends ResourceLoader {
    static final /* synthetic */ boolean $assertionsDisabled;
    private final SparseArray<DynamicResource> mDynamicResources;

    static {
        $assertionsDisabled = !DynamicResourceLoader.class.desiredAssertionStatus();
    }

    public DynamicResourceLoader(int resourceType, ResourceLoaderCallback callback) {
        super(resourceType, callback);
        this.mDynamicResources = new SparseArray();
    }

    public void registerResource(int resId, DynamicResource resource) {
        if ($assertionsDisabled || this.mDynamicResources.get(resId) == null) {
            this.mDynamicResources.put(resId, resource);
            return;
        }
        throw new AssertionError();
    }

    public void unregisterResource(int resId) {
        this.mDynamicResources.remove(resId);
    }

    public void loadResource(int resId) {
        DynamicResource resource = (DynamicResource) this.mDynamicResources.get(resId);
        if (resource != null && resource.isDirty()) {
            notifyLoadFinished(resId, resource);
        }
    }

    public void preloadResource(int resId) {
        if (!$assertionsDisabled) {
            throw new AssertionError("Preloading dynamic resources isn't supported.");
        }
    }
}
