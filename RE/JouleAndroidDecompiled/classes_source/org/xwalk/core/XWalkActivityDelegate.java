package org.xwalk.core;

import android.app.Activity;
import android.util.Log;
import android.view.Window;
import org.xwalk.core.XWalkLibraryLoader.ActivateListener;
import org.xwalk.core.XWalkLibraryLoader.DecompressListener;
import org.xwalk.core.XWalkUpdater.XWalkUpdateListener;

public class XWalkActivityDelegate implements DecompressListener, ActivateListener, XWalkUpdateListener {
    private static final String TAG = "XWalkActivity";
    private Activity mActivity;
    private boolean mBackgroundDecorated;
    private Runnable mCancelCommand;
    private Runnable mCompleteCommand;
    private XWalkDialogManager mDialogManager;
    private boolean mIsInitializing;
    private boolean mIsXWalkReady;
    private boolean mWillDecompress;
    private XWalkUpdater mXWalkUpdater;

    /* renamed from: org.xwalk.core.XWalkActivityDelegate.1 */
    class C04331 implements Runnable {
        C04331() {
        }

        public void run() {
            Log.d(XWalkActivityDelegate.TAG, "Cancel by XWalkActivity");
            XWalkLibraryLoader.cancelDecompress();
        }
    }

    public XWalkActivityDelegate(Activity activity, Runnable cancelCommand, Runnable completeCommand) {
        this.mActivity = activity;
        this.mCancelCommand = cancelCommand;
        this.mCompleteCommand = completeCommand;
        this.mDialogManager = new XWalkDialogManager(this.mActivity);
        this.mXWalkUpdater = new XWalkUpdater(this, this.mActivity, this.mDialogManager);
        XWalkLibraryLoader.prepareToInit(this.mActivity);
    }

    public boolean isXWalkReady() {
        return this.mIsXWalkReady;
    }

    public boolean isSharedMode() {
        return this.mIsXWalkReady && XWalkLibraryLoader.isSharedLibrary();
    }

    public void setXWalkApkUrl(String url) {
        this.mXWalkUpdater.setXWalkApkUrl(url);
    }

    public void onResume() {
        if (!this.mIsInitializing && !this.mIsXWalkReady) {
            this.mIsInitializing = true;
            if (XWalkLibraryLoader.isLibraryReady()) {
                Log.d(TAG, "Activate by XWalkActivity");
                XWalkLibraryLoader.startActivate(this, this.mActivity);
                return;
            }
            Log.d(TAG, "Initialize by XWalkActivity");
            XWalkLibraryLoader.startDecompress(this, this.mActivity);
        }
    }

    public void onDecompressStarted() {
        this.mDialogManager.showDecompressProgress(new C04331());
        this.mWillDecompress = true;
    }

    public void onDecompressCancelled() {
        this.mDialogManager.dismissDialog();
        this.mWillDecompress = false;
        this.mIsInitializing = false;
        this.mCancelCommand.run();
    }

    public void onDecompressCompleted() {
        if (this.mWillDecompress) {
            this.mDialogManager.dismissDialog();
            this.mWillDecompress = false;
        }
        XWalkLibraryLoader.startActivate(this, this.mActivity);
    }

    public void onActivateStarted() {
    }

    public void onActivateFailed() {
        this.mIsInitializing = false;
        if (this.mXWalkUpdater.updateXWalkRuntime()) {
            Window window = this.mActivity.getWindow();
            if (window != null && window.getDecorView().getBackground() == null) {
                Log.d(TAG, "Set the background to screen_background_dark");
                window.setBackgroundDrawableResource(17301656);
                this.mBackgroundDecorated = true;
            }
        }
    }

    public void onActivateCompleted() {
        if (this.mDialogManager.isShowingDialog()) {
            this.mDialogManager.dismissDialog();
        }
        if (this.mBackgroundDecorated) {
            Log.d(TAG, "Recover the background");
            this.mActivity.getWindow().setBackgroundDrawable(null);
            this.mBackgroundDecorated = false;
        }
        this.mIsInitializing = false;
        this.mIsXWalkReady = true;
        this.mCompleteCommand.run();
    }

    public void onXWalkUpdateCancelled() {
        this.mCancelCommand.run();
    }
}
