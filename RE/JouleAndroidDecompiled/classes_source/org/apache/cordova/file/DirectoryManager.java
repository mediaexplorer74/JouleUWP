package org.apache.cordova.file;

import android.os.Environment;
import android.os.StatFs;
import android.support.v4.media.session.PlaybackStateCompat;
import java.io.File;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

public class DirectoryManager {
    private static final String LOG_TAG = "DirectoryManager";

    public static boolean testFileExists(String name) {
        if (!testSaveLocationExists() || name.equals(CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE)) {
            return false;
        }
        return constructFilePaths(Environment.getExternalStorageDirectory().toString(), name).exists();
    }

    public static long getFreeDiskSpace(boolean checkInternal) {
        long freeSpace;
        if (Environment.getExternalStorageState().equals("mounted")) {
            freeSpace = freeSpaceCalculation(Environment.getExternalStorageDirectory().getPath());
        } else if (!checkInternal) {
            return -1;
        } else {
            freeSpace = freeSpaceCalculation("/");
        }
        return freeSpace;
    }

    private static long freeSpaceCalculation(String path) {
        StatFs stat = new StatFs(path);
        return (((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize())) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
    }

    public static boolean testSaveLocationExists() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return true;
        }
        return false;
    }

    private static File constructFilePaths(String file1, String file2) {
        if (file2.startsWith(file1)) {
            return new File(file2);
        }
        return new File(file1 + "/" + file2);
    }
}
