package org.xwalk.core;

import android.app.Activity;
import android.util.Log;
import org.xwalk.core.XWalkLibraryLoader.ActivateListener;
import org.xwalk.core.XWalkLibraryLoader.DecompressListener;

public class XWalkInitializer {
    private static final String TAG = "XWalkActivity";
    private Activity mActivity;
    private XWalkInitListener mInitListener;
    private boolean mIsInitializing;
    private boolean mIsXWalkReady;

    public interface XWalkInitListener {
        void onXWalkInitCancelled();

        void onXWalkInitCompleted();

        void onXWalkInitFailed();

        void onXWalkInitStarted();
    }

    private class XWalkLibraryListener implements DecompressListener, ActivateListener {
        private XWalkLibraryListener() {
        }

        public void onDecompressStarted() {
        }

        public void onDecompressCancelled() {
            XWalkInitializer.this.mIsInitializing = false;
            XWalkInitializer.this.mInitListener.onXWalkInitCancelled();
        }

        public void onDecompressCompleted() {
            XWalkLibraryLoader.startActivate(this, XWalkInitializer.this.mActivity);
        }

        public void onActivateStarted() {
        }

        public void onActivateFailed() {
            XWalkInitializer.this.mIsInitializing = false;
            XWalkInitializer.this.mInitListener.onXWalkInitFailed();
        }

        public void onActivateCompleted() {
            XWalkInitializer.this.mIsInitializing = false;
            XWalkInitializer.this.mIsXWalkReady = true;
            XWalkInitializer.this.mInitListener.onXWalkInitCompleted();
        }
    }

    public XWalkInitializer(XWalkInitListener listener, Activity activity) {
        this.mInitListener = listener;
        this.mActivity = activity;
        XWalkLibraryLoader.prepareToInit(this.mActivity);
    }

    public boolean initAsync() {
        if (this.mIsInitializing || this.mIsXWalkReady) {
            return false;
        }
        this.mIsInitializing = true;
        this.mInitListener.onXWalkInitStarted();
        if (XWalkLibraryLoader.isLibraryReady()) {
            Log.d(TAG, "Activate by XWalkInitializer");
            XWalkLibraryLoader.startActivate(new XWalkLibraryListener(), this.mActivity);
            return true;
        }
        Log.d(TAG, "Initialize by XWalkInitializer");
        XWalkLibraryLoader.startDecompress(new XWalkLibraryListener(), this.mActivity);
        return true;
    }

    public boolean cancelInit() {
        Log.d(TAG, "Cancel by XWalkInitializer");
        return this.mIsInitializing && XWalkLibraryLoader.cancelDecompress();
    }
}
