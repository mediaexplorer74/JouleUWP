package com.microsoft.cordova;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.ZipFile;

public class Utilities {
    public static String readFileContents(File file) throws IOException {
        Throwable th;
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            BufferedReader br2 = new BufferedReader(new FileReader(file));
            while (true) {
                try {
                    String currentLine = br2.readLine();
                    if (currentLine == null) {
                        break;
                    }
                    sb.append(currentLine);
                    sb.append('\n');
                } catch (Throwable th2) {
                    th = th2;
                    br = br2;
                }
            }
            if (br2 != null) {
                br2.close();
            }
            return sb.toString();
        } catch (Throwable th3) {
            th = th3;
            if (br != null) {
                br.close();
            }
            throw th;
        }
    }

    public static void deleteEntryRecursively(File entry) {
        if (entry.isDirectory()) {
            for (File child : entry.listFiles()) {
                deleteEntryRecursively(child);
            }
        }
        entry.delete();
    }

    public static String getAppVersionName(Context context) throws NameNotFoundException {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
    }

    public static long getApkEntryBuildTime(String entryName, Context context) {
        long result = -1;
        try {
            ZipFile applicationFile = new ZipFile(context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir);
            result = applicationFile.getEntry(entryName).getTime();
            applicationFile.close();
            return result;
        } catch (Exception e) {
            return result;
        }
    }

    public static void logException(Throwable e) {
        Log.e(CodePush.class.getName(), "An error occured. " + e.getMessage(), e);
    }
}
