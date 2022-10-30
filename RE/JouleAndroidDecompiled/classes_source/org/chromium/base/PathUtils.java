package org.chromium.base;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.media.TransportMediator;
import java.io.File;
import java.util.concurrent.Executor;

public abstract class PathUtils {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int CACHE_DIRECTORY = 2;
    private static final int DATABASE_DIRECTORY = 1;
    private static final int DATA_DIRECTORY = 0;
    private static final int NUM_DIRECTORIES = 3;
    private static final String THUMBNAIL_DIRECTORY = "textures";
    private static AsyncTask<String, Void, String[]> sDirPathFetchTask;
    private static File sThumbnailDirectory;

    /* renamed from: org.chromium.base.PathUtils.1 */
    static class C03071 extends AsyncTask<String, Void, String[]> {
        final /* synthetic */ Context val$appContext;

        C03071(Context context) {
            this.val$appContext = context;
        }

        protected String[] doInBackground(String... dataDirectorySuffix) {
            String[] paths = new String[PathUtils.NUM_DIRECTORIES];
            paths[PathUtils.DATA_DIRECTORY] = this.val$appContext.getDir(dataDirectorySuffix[PathUtils.DATA_DIRECTORY], PathUtils.DATA_DIRECTORY).getPath();
            paths[PathUtils.DATABASE_DIRECTORY] = this.val$appContext.getDatabasePath("foo").getParent();
            if (this.val$appContext.getCacheDir() != null) {
                paths[PathUtils.CACHE_DIRECTORY] = this.val$appContext.getCacheDir().getPath();
            }
            return paths;
        }
    }

    static {
        $assertionsDisabled = !PathUtils.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    private PathUtils() {
    }

    public static void setPrivateDataDirectorySuffix(String suffix, Context context) {
        C03071 c03071 = new C03071(context.getApplicationContext());
        Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;
        String[] strArr = new String[DATABASE_DIRECTORY];
        strArr[DATA_DIRECTORY] = suffix;
        sDirPathFetchTask = c03071.executeOnExecutor(executor, strArr);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String getDirectoryPath(int r1) {
        /*
        r0 = sDirPathFetchTask;	 Catch:{ InterruptedException -> 0x000e, ExecutionException -> 0x000b }
        r0 = r0.get();	 Catch:{ InterruptedException -> 0x000e, ExecutionException -> 0x000b }
        r0 = (java.lang.String[]) r0;	 Catch:{ InterruptedException -> 0x000e, ExecutionException -> 0x000b }
        r0 = r0[r1];	 Catch:{ InterruptedException -> 0x000e, ExecutionException -> 0x000b }
    L_0x000a:
        return r0;
    L_0x000b:
        r0 = move-exception;
    L_0x000c:
        r0 = 0;
        goto L_0x000a;
    L_0x000e:
        r0 = move-exception;
        goto L_0x000c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.base.PathUtils.getDirectoryPath(int):java.lang.String");
    }

    @CalledByNative
    public static String getDataDirectory(Context appContext) {
        if ($assertionsDisabled || sDirPathFetchTask != null) {
            return getDirectoryPath(DATA_DIRECTORY);
        }
        throw new AssertionError("setDataDirectorySuffix must be called first.");
    }

    @CalledByNative
    public static String getDatabaseDirectory(Context appContext) {
        if ($assertionsDisabled || sDirPathFetchTask != null) {
            return getDirectoryPath(DATABASE_DIRECTORY);
        }
        throw new AssertionError("setDataDirectorySuffix must be called first.");
    }

    @CalledByNative
    public static String getCacheDirectory(Context appContext) {
        if ($assertionsDisabled || sDirPathFetchTask != null) {
            return getDirectoryPath(CACHE_DIRECTORY);
        }
        throw new AssertionError("setDataDirectorySuffix must be called first.");
    }

    public static File getThumbnailCacheDirectory(Context appContext) {
        if (sThumbnailDirectory == null) {
            sThumbnailDirectory = appContext.getDir(THUMBNAIL_DIRECTORY, DATA_DIRECTORY);
        }
        return sThumbnailDirectory;
    }

    @CalledByNative
    public static String getThumbnailCacheDirectoryPath(Context appContext) {
        return getThumbnailCacheDirectory(appContext).getAbsolutePath();
    }

    @CalledByNative
    private static String getDownloadsDirectory(Context appContext) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    }

    @CalledByNative
    private static String getNativeLibraryDirectory(Context appContext) {
        ApplicationInfo ai = appContext.getApplicationInfo();
        if ((ai.flags & TransportMediator.FLAG_KEY_MEDIA_NEXT) != 0 || (ai.flags & DATABASE_DIRECTORY) == 0) {
            return ai.nativeLibraryDir;
        }
        return "/system/lib/";
    }

    @CalledByNative
    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
