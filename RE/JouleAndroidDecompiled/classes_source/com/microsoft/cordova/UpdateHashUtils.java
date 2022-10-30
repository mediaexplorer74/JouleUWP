package com.microsoft.cordova;

import android.app.Activity;
import android.content.res.AssetManager;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.json.JSONArray;

public class UpdateHashUtils {
    public static String getBinaryHash(Activity activity) throws IOException, NoSuchAlgorithmException {
        ArrayList<String> manifestEntries = new ArrayList();
        addFolderEntriesToManifest(manifestEntries, "www", activity.getAssets());
        Collections.sort(manifestEntries);
        JSONArray manifestJSONArray = new JSONArray();
        Iterator it = manifestEntries.iterator();
        while (it.hasNext()) {
            manifestJSONArray.put((String) it.next());
        }
        return computeHash(new ByteArrayInputStream(manifestJSONArray.toString().replace("\\/", "/").getBytes()));
    }

    private static void addFolderEntriesToManifest(ArrayList<String> manifestEntries, String path, AssetManager assetManager) throws IOException, NoSuchAlgorithmException {
        String[] fileList = assetManager.list(path);
        if (fileList.length > 0) {
            for (String pathInFolder : fileList) {
                addFolderEntriesToManifest(manifestEntries, path + "/" + pathInFolder, assetManager);
            }
            return;
        }
        manifestEntries.add(path + ":" + computeHash(assetManager.open(path)));
    }

    private static String computeHash(InputStream dataStream) throws IOException, NoSuchAlgorithmException {
        Throwable th;
        DigestInputStream digestInputStream = null;
        try {
            DigestInputStream digestInputStream2 = new DigestInputStream(dataStream, MessageDigest.getInstance(XWalkAppVersion.XWALK_APK_HASH_ALGORITHM));
            try {
                do {
                } while (digestInputStream2.read(new byte[AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD]) != -1);
                if (digestInputStream2 != null) {
                    try {
                        digestInputStream2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dataStream != null) {
                    dataStream.close();
                }
                return String.format("%064x", new Object[]{new BigInteger(1, messageDigest.digest())});
            } catch (Throwable th2) {
                th = th2;
                digestInputStream = digestInputStream2;
                if (digestInputStream != null) {
                    try {
                        digestInputStream.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        throw th;
                    }
                }
                if (dataStream != null) {
                    dataStream.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (digestInputStream != null) {
                digestInputStream.close();
            }
            if (dataStream != null) {
                dataStream.close();
            }
            throw th;
        }
    }
}
