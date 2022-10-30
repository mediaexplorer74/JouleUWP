package org.chromium.content.app;

import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import org.chromium.base.BaseChromiumApplication;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.library_loader.LibraryLoader;
import org.chromium.content.browser.TracingControllerAndroid;

public abstract class ContentApplication extends BaseChromiumApplication {
    private boolean mLibraryDependenciesInitialized;
    private TracingControllerAndroid mTracingController;

    /* renamed from: org.chromium.content.app.ContentApplication.1 */
    class C03191 implements IdleHandler {
        C03191() {
        }

        public boolean queueIdle() {
            if (!LibraryLoader.isInitialized()) {
                return true;
            }
            try {
                ContentApplication.this.getTracingController().registerReceiver(ContentApplication.this);
            } catch (SecurityException e) {
            }
            return false;
        }
    }

    TracingControllerAndroid getTracingController() {
        if (this.mTracingController == null) {
            this.mTracingController = new TracingControllerAndroid(this);
        }
        return this.mTracingController;
    }

    public void onCreate() {
        super.onCreate();
        Looper.myQueue().addIdleHandler(new C03191());
        initializeLibraryDependencies();
        this.mLibraryDependenciesInitialized = true;
    }

    protected void initializeLibraryDependencies() {
    }

    @VisibleForTesting
    public boolean areLibraryDependenciesInitialized() {
        return this.mLibraryDependenciesInitialized;
    }

    public void onTerminate() {
        try {
            getTracingController().unregisterReceiver(this);
            getTracingController().destroy();
        } catch (SecurityException e) {
        }
        super.onTerminate();
    }
}
