package org.chromium.ui.resources.statics;

import android.content.res.Resources;
import org.chromium.ui.resources.Resource;
import org.chromium.ui.resources.ResourceLoader.ResourceLoaderCallback;
import org.chromium.ui.resources.async.AsyncPreloadResourceLoader;
import org.chromium.ui.resources.async.AsyncPreloadResourceLoader.ResourceCreator;

public class StaticResourceLoader extends AsyncPreloadResourceLoader {

    /* renamed from: org.chromium.ui.resources.statics.StaticResourceLoader.1 */
    class C06411 implements ResourceCreator {
        final /* synthetic */ Resources val$resources;

        C06411(Resources resources) {
            this.val$resources = resources;
        }

        public Resource create(int resId) {
            return StaticResource.create(this.val$resources, resId, 0, 0);
        }
    }

    public StaticResourceLoader(int resourceType, ResourceLoaderCallback callback, Resources resources) {
        super(resourceType, callback, new C06411(resources));
    }
}
