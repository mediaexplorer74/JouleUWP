package org.xwalk.core;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import junit.framework.Assert;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

class XWalkLibraryLoader {
    private static final String TAG = "XWalkLib";
    private static final String XWALK_APK_NAME = "XWalkRuntimeLib.apk";
    private static AsyncTask<Void, Integer, Integer> sActiveTask;

    public interface ActivateListener {
        void onActivateCompleted();

        void onActivateFailed();

        void onActivateStarted();
    }

    private static class ActivateTask extends AsyncTask<Void, Integer, Integer> {
        Activity mActivity;
        ActivateListener mListener;

        ActivateTask(ActivateListener listener, Activity activity) {
            this.mListener = listener;
            this.mActivity = activity;
        }

        protected void onPreExecute() {
            Log.d(XWalkLibraryLoader.TAG, "ActivateTask started");
            XWalkLibraryLoader.sActiveTask = this;
            this.mListener.onActivateStarted();
        }

        protected Integer doInBackground(Void... params) {
            if (XWalkCoreWrapper.getInstance() != null) {
                return Integer.valueOf(-1);
            }
            return Integer.valueOf(XWalkCoreWrapper.attachXWalkCore(this.mActivity));
        }

        protected void onPostExecute(Integer result) {
            if (result.intValue() == 1) {
                XWalkCoreWrapper.dockXWalkCore();
            }
            if (XWalkCoreWrapper.getInstance() != null) {
                XWalkCoreWrapper.handlePostInit(this.mActivity.getClass().getName());
            }
            Log.d(XWalkLibraryLoader.TAG, "ActivateTask finished, " + result);
            XWalkLibraryLoader.sActiveTask = null;
            if (result.intValue() > 1) {
                this.mListener.onActivateFailed();
            } else {
                this.mListener.onActivateCompleted();
            }
        }
    }

    public interface DecompressListener {
        void onDecompressCancelled();

        void onDecompressCompleted();

        void onDecompressStarted();
    }

    private static class DecompressTask extends AsyncTask<Void, Integer, Integer> {
        Context mContext;
        boolean mIsCompressed;
        boolean mIsDecompressed;
        DecompressListener mListener;

        DecompressTask(DecompressListener listener, Context context) {
            this.mListener = listener;
            this.mContext = context;
        }

        protected void onPreExecute() {
            Log.d(XWalkLibraryLoader.TAG, "DecompressTask started");
            XWalkLibraryLoader.sActiveTask = this;
            this.mIsCompressed = XWalkLibraryDecompressor.isCompressed(this.mContext);
            if (this.mIsCompressed) {
                this.mIsDecompressed = XWalkLibraryDecompressor.isDecompressed(this.mContext);
            }
            if (this.mIsCompressed && !this.mIsDecompressed) {
                this.mListener.onDecompressStarted();
            }
        }

        protected Integer doInBackground(Void... params) {
            if (!this.mIsCompressed || this.mIsDecompressed) {
                return Integer.valueOf(0);
            }
            if (XWalkLibraryDecompressor.decompressLibrary(this.mContext)) {
                return Integer.valueOf(0);
            }
            return Integer.valueOf(1);
        }

        protected void onCancelled(Integer result) {
            Log.d(XWalkLibraryLoader.TAG, "DecompressTask cancelled");
            XWalkLibraryLoader.sActiveTask = null;
            this.mListener.onDecompressCancelled();
        }

        protected void onPostExecute(Integer result) {
            Log.d(XWalkLibraryLoader.TAG, "DecompressTask finished, " + result);
            Assert.assertEquals(result.intValue(), 0);
            XWalkLibraryLoader.sActiveTask = null;
            this.mListener.onDecompressCompleted();
        }
    }

    public interface DownloadListener {
        void onDownloadCancelled();

        void onDownloadCompleted(Uri uri);

        void onDownloadFailed(int i, int i2);

        void onDownloadStarted();

        void onDownloadUpdated(int i);
    }

    private static class DownloadTask extends AsyncTask<Void, Integer, Integer> {
        private static final int MAX_PAUSED_COUNT = 6000;
        private static final int QUERY_INTERVAL_MS = 100;
        private long mDownloadId;
        private DownloadManager mDownloadManager;
        private String mDownloadUrl;
        private DownloadListener mListener;

        DownloadTask(DownloadListener listener, Context context, String url) {
            this.mListener = listener;
            this.mDownloadUrl = url;
            this.mDownloadManager = (DownloadManager) context.getSystemService("download");
        }

        protected void onPreExecute() {
            Log.d(XWalkLibraryLoader.TAG, "DownloadTask started, " + this.mDownloadUrl);
            XWalkLibraryLoader.sActiveTask = this;
            Request request = new Request(Uri.parse(this.mDownloadUrl));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, XWalkLibraryLoader.XWALK_APK_NAME);
            this.mDownloadId = this.mDownloadManager.enqueue(request);
            this.mListener.onDownloadStarted();
        }

        protected Integer doInBackground(Void... params) {
            Query query = new Query();
            long[] jArr = new long[1];
            jArr[0] = this.mDownloadId;
            Query query2 = query.setFilterById(jArr);
            int pausedCount = 0;
            while (!isCancelled()) {
                try {
                    Thread.sleep(100);
                    Cursor cursor = this.mDownloadManager.query(query2);
                    if (cursor != null && cursor.moveToFirst()) {
                        int totalIdx = cursor.getColumnIndex("total_size");
                        int downloadIdx = cursor.getColumnIndex("bytes_so_far");
                        int totalSize = cursor.getInt(totalIdx);
                        int downloadSize = cursor.getInt(downloadIdx);
                        if (totalSize > 0) {
                            publishProgress(new Integer[]{Integer.valueOf(downloadSize), Integer.valueOf(totalSize)});
                        }
                        int status = cursor.getInt(cursor.getColumnIndex(MessagingSmsConsts.STATUS));
                        if (status == 16 || status == 8) {
                            return Integer.valueOf(status);
                        }
                        if (status == 4) {
                            pausedCount++;
                            if (pausedCount == MAX_PAUSED_COUNT) {
                                return Integer.valueOf(status);
                            }
                        } else {
                            continue;
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
            return Integer.valueOf(2);
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d(XWalkLibraryLoader.TAG, "DownloadTask updated: " + progress[0] + "/" + progress[1]);
            int percentage = 0;
            if (progress[1].intValue() > 0) {
                percentage = (int) ((((double) progress[0].intValue()) * 100.0d) / ((double) progress[1].intValue()));
            }
            this.mListener.onDownloadUpdated(percentage);
        }

        protected void onCancelled(Integer result) {
            this.mDownloadManager.remove(new long[]{this.mDownloadId});
            Log.d(XWalkLibraryLoader.TAG, "DownloadTask cancelled");
            XWalkLibraryLoader.sActiveTask = null;
            this.mListener.onDownloadCancelled();
        }

        protected void onPostExecute(Integer result) {
            Log.d(XWalkLibraryLoader.TAG, "DownloadTask finished, " + result);
            XWalkLibraryLoader.sActiveTask = null;
            if (result.intValue() == 8) {
                this.mListener.onDownloadCompleted(this.mDownloadManager.getUriForDownloadedFile(this.mDownloadId));
                return;
            }
            int error = -1;
            if (result.intValue() == 16) {
                Cursor cursor = this.mDownloadManager.query(new Query().setFilterById(new long[]{this.mDownloadId}));
                if (cursor != null && cursor.moveToFirst()) {
                    error = cursor.getInt(cursor.getColumnIndex("reason"));
                }
            }
            this.mListener.onDownloadFailed(result.intValue(), error);
        }
    }

    XWalkLibraryLoader() {
    }

    public static boolean isSharedLibrary() {
        return XWalkCoreWrapper.getInstance().isSharedMode();
    }

    public static boolean isLibraryReady() {
        return XWalkCoreWrapper.getInstance() != null;
    }

    public static int getLibraryStatus() {
        return XWalkCoreWrapper.getCoreStatus();
    }

    public static void prepareToInit(Activity activity) {
        XWalkCoreWrapper.handlePreInit(activity.getClass().getName());
    }

    public static void startDecompress(DecompressListener listener, Context context) {
        new DecompressTask(listener, context).execute(new Void[0]);
    }

    public static boolean cancelDecompress() {
        DecompressTask task = sActiveTask;
        if (task == null || !task.cancel(true)) {
            return false;
        }
        return true;
    }

    public static void startActivate(ActivateListener listener, Activity activity) {
        new ActivateTask(listener, activity).execute(new Void[0]);
    }

    public static void startDownload(DownloadListener listener, Context context, String url) {
        new DownloadTask(listener, context, url).execute(new Void[0]);
    }

    public static boolean cancelDownload() {
        DownloadTask task = sActiveTask;
        if (task == null || !task.cancel(true)) {
            return false;
        }
        return true;
    }
}
