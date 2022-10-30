package org.chromium.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Trace;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import org.chromium.base.annotations.SuppressFBWarnings;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

public class ResourceExtractor {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String ICU_DATA_FILENAME = "icudtl.dat";
    private static final String LOGTAG = "ResourceExtractor";
    private static final String V8_NATIVES_DATA_FILENAME = "natives_blob.bin";
    private static final String V8_SNAPSHOT_DATA_FILENAME = "snapshot_blob.bin";
    private static ResourceExtractor sInstance;
    private static ResourceInterceptor sInterceptor;
    private static ResourceEntry[] sResourcesToExtract;
    private final Context mContext;
    private ExtractTask mExtractTask;

    private class ExtractTask extends AsyncTask<Void, Void, Void> {
        private static final int BUFFER_SIZE = 16384;
        private final List<Runnable> mCompletionCallbacks;

        /* renamed from: org.chromium.base.ResourceExtractor.ExtractTask.1 */
        class C03101 implements FilenameFilter {
            C03101() {
            }

            public boolean accept(File dir, String name) {
                return name.startsWith("pak_timestamp-");
            }
        }

        private void doInBackgroundImpl() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:52:0x008f in {9, 12, 16, 20, 22, 23, 30, 39, 41, 42, 44, 53, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 68, 69, 70} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r15 = this;
            r12 = org.chromium.base.ResourceExtractor.this;
            r10 = r12.getOutputDir();
            r12 = org.chromium.base.ResourceExtractor.this;
            r0 = r12.getAppDataDir();
            r12 = r10.exists();
            if (r12 != 0) goto L_0x0020;
        L_0x0012:
            r12 = r10.mkdirs();
            if (r12 != 0) goto L_0x0020;
        L_0x0018:
            r12 = "ResourceExtractor";
            r13 = "Unable to create pak resources directory!";
            android.util.Log.e(r12, r13);
        L_0x001f:
            return;
        L_0x0020:
            r11 = 0;
            r12 = "checkPakTimeStamp";
            r15.beginTraceSection(r12);
            r11 = r15.checkPakTimestamp(r10);	 Catch:{ all -> 0x0060 }
            r15.endTraceSection();
            if (r11 == 0) goto L_0x0034;
        L_0x002f:
            r12 = org.chromium.base.ResourceExtractor.this;
            r12.deleteFiles();
        L_0x0034:
            r12 = "WalkAssets";
            r15.beginTraceSection(r12);
            r12 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
            r2 = new byte[r12];
            r1 = org.chromium.base.ResourceExtractor.sResourcesToExtract;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r8 = r1.length;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r6 = 0;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
        L_0x0043:
            if (r6 >= r8) goto L_0x00d1;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
        L_0x0045:
            r5 = r1[r6];	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r12 = r5.extractedFileName;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r12 = org.chromium.base.ResourceExtractor.isAppDataFile(r12);	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            if (r12 == 0) goto L_0x0065;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
        L_0x004f:
            r3 = r0;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
        L_0x0050:
            r9 = new java.io.File;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r12 = r5.extractedFileName;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r9.<init>(r3, r12);	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r12 = r9.exists();	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            if (r12 == 0) goto L_0x0067;
        L_0x005d:
            r6 = r6 + 1;
            goto L_0x0043;
        L_0x0060:
            r12 = move-exception;
            r15.endTraceSection();
            throw r12;
        L_0x0065:
            r3 = r10;
            goto L_0x0050;
        L_0x0067:
            r12 = "ExtractResource";	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r15.beginTraceSection(r12);	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r12 = org.chromium.base.ResourceExtractor.sInterceptor;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            if (r12 == 0) goto L_0x00b6;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
        L_0x0072:
            r12 = org.chromium.base.ResourceExtractor.sInterceptor;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r13 = r5.extractedFileName;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r12 = r12.shouldInterceptLoadRequest(r13);	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            if (r12 == 0) goto L_0x00b6;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
        L_0x007e:
            r12 = org.chromium.base.ResourceExtractor.sInterceptor;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r13 = r5.extractedFileName;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r7 = r12.openRawResource(r13);	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
        L_0x0088:
            r15.extractResourceHelper(r7, r9, r2);	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r15.endTraceSection();
            goto L_0x005d;
        L_0x008f:
            r4 = move-exception;
            r12 = "ResourceExtractor";	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r13 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r13.<init>();	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r14 = "Exception unpacking required pak resources: ";	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r13 = r13.append(r14);	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r14 = r4.getMessage();	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r13 = r13.append(r14);	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r13 = r13.toString();	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            android.util.Log.w(r12, r13);	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r12 = org.chromium.base.ResourceExtractor.this;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r12.deleteFiles();	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r15.endTraceSection();
            goto L_0x001f;
        L_0x00b6:
            r12 = org.chromium.base.ResourceExtractor.this;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r12 = r12.mContext;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r12 = r12.getResources();	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r13 = r5.resourceId;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r7 = r12.openRawResource(r13);	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            goto L_0x0088;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
        L_0x00c7:
            r12 = move-exception;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
            r15.endTraceSection();
            throw r12;	 Catch:{ all -> 0x00c7, IOException -> 0x008f, all -> 0x00cc }
        L_0x00cc:
            r12 = move-exception;
            r15.endTraceSection();
            throw r12;
        L_0x00d1:
            r15.endTraceSection();
            if (r11 == 0) goto L_0x001f;
        L_0x00d6:
            r12 = new java.io.File;	 Catch:{ IOException -> 0x00e0 }
            r12.<init>(r10, r11);	 Catch:{ IOException -> 0x00e0 }
            r12.createNewFile();	 Catch:{ IOException -> 0x00e0 }
            goto L_0x001f;
        L_0x00e0:
            r4 = move-exception;
            r12 = "ResourceExtractor";
            r13 = "Failed to write resource pak timestamp!";
            android.util.Log.w(r12, r13);
            goto L_0x001f;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.chromium.base.ResourceExtractor.ExtractTask.doInBackgroundImpl():void");
        }

        private ExtractTask() {
            this.mCompletionCallbacks = new ArrayList();
        }

        private void extractResourceHelper(InputStream is, File outFile, byte[] buffer) throws IOException {
            Throwable th;
            OutputStream os = null;
            try {
                OutputStream os2 = new FileOutputStream(outFile);
                try {
                    Log.i(ResourceExtractor.LOGTAG, "Extracting resource " + outFile);
                    while (true) {
                        int count = is.read(buffer, 0, BUFFER_SIZE);
                        if (count == -1) {
                            break;
                        }
                        os2.write(buffer, 0, count);
                    }
                    os2.flush();
                    if (outFile.length() == 0) {
                        throw new IOException(outFile + " extracted with 0 length!");
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Throwable th2) {
                            if (os2 != null) {
                                os2.close();
                            }
                        }
                    }
                    if (os2 != null) {
                        os2.close();
                    }
                } catch (Throwable th3) {
                    th = th3;
                    os = os2;
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Throwable th4) {
                            if (os != null) {
                                os.close();
                            }
                        }
                    }
                    if (os != null) {
                        os.close();
                    }
                    throw th;
                }
            } catch (Throwable th5) {
                th = th5;
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
                throw th;
            }
        }

        protected Void doInBackground(Void... unused) {
            beginTraceSection("ResourceExtractor.ExtractTask.doInBackground");
            try {
                doInBackgroundImpl();
                return null;
            } finally {
                endTraceSection();
            }
        }

        private void onPostExecuteImpl() {
            for (int i = 0; i < this.mCompletionCallbacks.size(); i++) {
                ((Runnable) this.mCompletionCallbacks.get(i)).run();
            }
            this.mCompletionCallbacks.clear();
        }

        protected void onPostExecute(Void result) {
            beginTraceSection("ResourceExtractor.ExtractTask.onPostExecute");
            try {
                onPostExecuteImpl();
            } finally {
                endTraceSection();
            }
        }

        private String checkPakTimestamp(File outputDir) {
            String timestampPrefix = "pak_timestamp-";
            try {
                PackageInfo pi = ResourceExtractor.this.mContext.getPackageManager().getPackageInfo(ResourceExtractor.this.mContext.getPackageName(), 0);
                if (pi == null) {
                    return "pak_timestamp-";
                }
                String expectedTimestamp = "pak_timestamp-" + pi.versionCode + "-" + pi.lastUpdateTime;
                String[] timestamps = outputDir.list(new C03101());
                return (timestamps.length == 1 && expectedTimestamp.equals(timestamps[0])) ? null : expectedTimestamp;
            } catch (NameNotFoundException e) {
                return "pak_timestamp-";
            }
        }

        @TargetApi(18)
        private void beginTraceSection(String section) {
            if (VERSION.SDK_INT >= 18) {
                Trace.beginSection(section);
            }
        }

        @TargetApi(18)
        private void endTraceSection() {
            if (VERSION.SDK_INT >= 18) {
                Trace.endSection();
            }
        }
    }

    public static final class ResourceEntry {
        public final String extractedFileName;
        public final String pathWithinApk;
        public final int resourceId;

        public ResourceEntry(int resourceId, String pathWithinApk, String extractedFileName) {
            this.resourceId = resourceId;
            this.pathWithinApk = pathWithinApk;
            this.extractedFileName = extractedFileName;
        }
    }

    public interface ResourceInterceptor {
        InputStream openRawResource(String str);

        boolean shouldInterceptLoadRequest(String str);
    }

    static {
        $assertionsDisabled = !ResourceExtractor.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        sResourcesToExtract = new ResourceEntry[0];
        sInterceptor = null;
    }

    private static boolean isAppDataFile(String file) {
        return (ICU_DATA_FILENAME.equals(file) || V8_NATIVES_DATA_FILENAME.equals(file) || V8_SNAPSHOT_DATA_FILENAME.equals(file)) ? true : $assertionsDisabled;
    }

    public static ResourceExtractor get(Context context) {
        if (sInstance == null) {
            sInstance = new ResourceExtractor(context);
        }
        return sInstance;
    }

    public static void setResourceInterceptor(ResourceInterceptor interceptor) {
        if ($assertionsDisabled || sInstance == null || sInstance.mExtractTask == null) {
            sInterceptor = interceptor;
            return;
        }
        throw new AssertionError("Must be called before startExtractingResources is called");
    }

    @SuppressFBWarnings({"EI_EXPOSE_STATIC_REP2"})
    public static void setResourcesToExtract(ResourceEntry[] entries) {
        if ($assertionsDisabled || sInstance == null || sInstance.mExtractTask == null) {
            sResourcesToExtract = entries;
            return;
        }
        throw new AssertionError("Must be called before startExtractingResources is called");
    }

    public static void setMandatoryPaksToExtract(String... paths) {
        if (!$assertionsDisabled) {
            if (paths.length != 1 || !CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE.equals(paths[0])) {
                throw new AssertionError();
            }
        }
    }

    private ResourceExtractor(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void waitForCompletion() {
        if (!shouldSkipPakExtraction()) {
            if ($assertionsDisabled || this.mExtractTask != null) {
                try {
                    this.mExtractTask.get();
                    sInterceptor = null;
                    sInstance = null;
                    return;
                } catch (CancellationException e) {
                    deleteFiles();
                    return;
                } catch (ExecutionException e2) {
                    deleteFiles();
                    return;
                } catch (InterruptedException e3) {
                    deleteFiles();
                    return;
                }
            }
            throw new AssertionError();
        }
    }

    public void addCompletionCallback(Runnable callback) {
        ThreadUtils.assertOnUiThread();
        Handler handler = new Handler(Looper.getMainLooper());
        if (shouldSkipPakExtraction()) {
            handler.post(callback);
        } else if (!$assertionsDisabled && this.mExtractTask == null) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && this.mExtractTask.isCancelled()) {
            throw new AssertionError();
        } else if (this.mExtractTask.getStatus() == Status.FINISHED) {
            handler.post(callback);
        } else {
            this.mExtractTask.mCompletionCallbacks.add(callback);
        }
    }

    public void startExtractingResources() {
        if (this.mExtractTask == null && !shouldSkipPakExtraction()) {
            this.mExtractTask = new ExtractTask();
            this.mExtractTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    private File getAppDataDir() {
        return new File(PathUtils.getDataDirectory(this.mContext));
    }

    private File getOutputDir() {
        return new File(getAppDataDir(), "paks");
    }

    private void deleteFiles() {
        File icudata = new File(getAppDataDir(), ICU_DATA_FILENAME);
        if (icudata.exists() && !icudata.delete()) {
            Log.e(LOGTAG, "Unable to remove the icudata " + icudata.getName());
        }
        File v8_natives = new File(getAppDataDir(), V8_NATIVES_DATA_FILENAME);
        if (v8_natives.exists() && !v8_natives.delete()) {
            Log.e(LOGTAG, "Unable to remove the v8 data " + v8_natives.getName());
        }
        File v8_snapshot = new File(getAppDataDir(), V8_SNAPSHOT_DATA_FILENAME);
        if (v8_snapshot.exists() && !v8_snapshot.delete()) {
            Log.e(LOGTAG, "Unable to remove the v8 data " + v8_snapshot.getName());
        }
        File dir = getOutputDir();
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (!file.delete()) {
                    Log.e(LOGTAG, "Unable to remove existing resource " + file.getName());
                }
            }
        }
    }

    private static boolean shouldSkipPakExtraction() {
        return sResourcesToExtract.length == 0 ? true : $assertionsDisabled;
    }
}
