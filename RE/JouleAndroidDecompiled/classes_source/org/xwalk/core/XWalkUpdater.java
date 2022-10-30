package org.xwalk.core;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.support.v4.media.TransportMediator;
import android.util.Log;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.xwalk.core.XWalkLibraryLoader.DownloadListener;

public class XWalkUpdater {
    private static final String TAG = "XWalkActivity";
    private static final String XWALK_APK_MARKET_URL = "market://details?id=org.xwalk.core";
    private Activity mActivity;
    private Runnable mCancelCommand;
    private XWalkDialogManager mDialogManager;
    private Runnable mDownloadCommand;
    private XWalkUpdateListener mUpdateListener;
    private String mXWalkApkUrl;

    /* renamed from: org.xwalk.core.XWalkUpdater.1 */
    class C04371 implements Runnable {
        C04371() {
        }

        public void run() {
            XWalkUpdater.this.downloadXWalkApk();
        }
    }

    /* renamed from: org.xwalk.core.XWalkUpdater.2 */
    class C04382 implements Runnable {
        C04382() {
        }

        public void run() {
            Log.d(XWalkUpdater.TAG, "XWalkUpdater cancelled");
            XWalkUpdater.this.mUpdateListener.onXWalkUpdateCancelled();
        }
    }

    public interface XWalkUpdateListener {
        void onXWalkUpdateCancelled();
    }

    private class XWalkLibraryListener implements DownloadListener {

        /* renamed from: org.xwalk.core.XWalkUpdater.XWalkLibraryListener.1 */
        class C04391 implements Runnable {
            C04391() {
            }

            public void run() {
                XWalkLibraryLoader.cancelDownload();
            }
        }

        private XWalkLibraryListener() {
        }

        public void onDownloadStarted() {
            XWalkUpdater.this.mDialogManager.showDownloadProgress(new C04391());
        }

        public void onDownloadUpdated(int percentage) {
            XWalkUpdater.this.mDialogManager.setProgress(percentage, 100);
        }

        public void onDownloadCancelled() {
            XWalkUpdater.this.mUpdateListener.onXWalkUpdateCancelled();
        }

        public void onDownloadCompleted(Uri uri) {
            XWalkUpdater.this.mDialogManager.dismissDialog();
            Log.d(XWalkUpdater.TAG, "Install the Crosswalk runtime: " + uri.toString());
            Intent install = new Intent("android.intent.action.VIEW");
            install.setDataAndType(uri, "application/vnd.android.package-archive");
            XWalkUpdater.this.mActivity.startActivity(install);
        }

        public void onDownloadFailed(int status, int error) {
            XWalkUpdater.this.mDialogManager.dismissDialog();
            XWalkUpdater.this.mDialogManager.showDownloadError(status, error, XWalkUpdater.this.mCancelCommand, XWalkUpdater.this.mDownloadCommand);
        }
    }

    public XWalkUpdater(XWalkUpdateListener listener, Activity activity) {
        this(listener, activity, new XWalkDialogManager(activity));
    }

    XWalkUpdater(XWalkUpdateListener listener, Activity activity, XWalkDialogManager dialogManager) {
        this.mUpdateListener = listener;
        this.mActivity = activity;
        this.mDialogManager = dialogManager;
        this.mDownloadCommand = new C04371();
        this.mCancelCommand = new C04382();
    }

    public boolean updateXWalkRuntime() {
        if (this.mDialogManager.isShowingDialog()) {
            return false;
        }
        int status = XWalkLibraryLoader.getLibraryStatus();
        if (status == 0 || status == 1) {
            return false;
        }
        Log.d(TAG, "Update the Crosswalk runtime with status " + status);
        this.mDialogManager.showInitializationError(status, this.mCancelCommand, this.mDownloadCommand);
        return true;
    }

    public boolean dismissDialog() {
        if (!this.mDialogManager.isShowingDialog()) {
            return false;
        }
        this.mDialogManager.dismissDialog();
        return true;
    }

    public void setXWalkApkUrl(String url) {
        this.mXWalkApkUrl = url;
    }

    private void downloadXWalkApk() {
        String downloadUrl = getXWalkApkUrl();
        if (downloadUrl.isEmpty()) {
            try {
                this.mActivity.startActivity(new Intent("android.intent.action.VIEW").setData(Uri.parse(XWALK_APK_MARKET_URL)));
                Log.d(TAG, "Market opened");
                this.mDialogManager.dismissDialog();
                return;
            } catch (ActivityNotFoundException e) {
                Log.d(TAG, "Market open failed");
                this.mDialogManager.showMarketOpenError(this.mCancelCommand);
                return;
            }
        }
        XWalkLibraryLoader.startDownload(new XWalkLibraryListener(), this.mActivity, downloadUrl);
    }

    private String getXWalkApkUrl() {
        if (this.mXWalkApkUrl != null) {
            return this.mXWalkApkUrl;
        }
        try {
            this.mXWalkApkUrl = this.mActivity.getPackageManager().getApplicationInfo(this.mActivity.getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT).metaData.getString("xwalk_apk_url");
        } catch (NameNotFoundException e) {
        } catch (NullPointerException e2) {
        }
        if (this.mXWalkApkUrl == null) {
            this.mXWalkApkUrl = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
        Log.d(TAG, "Crosswalk APK download URL: " + this.mXWalkApkUrl);
        return this.mXWalkApkUrl;
    }
}
