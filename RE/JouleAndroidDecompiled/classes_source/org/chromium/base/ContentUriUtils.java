package org.chromium.base;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;

public abstract class ContentUriUtils {
    private static final String TAG = "ContentUriUtils";
    private static FileProviderUtil sFileProviderUtil;

    public interface FileProviderUtil {
        Uri getContentUriFromFile(Context context, File file);
    }

    private ContentUriUtils() {
    }

    public static void setFileProviderUtil(FileProviderUtil util) {
        sFileProviderUtil = util;
    }

    public static Uri getContentUriFromFile(Context context, File file) {
        ThreadUtils.assertOnUiThread();
        if (sFileProviderUtil != null) {
            return sFileProviderUtil.getContentUriFromFile(context, file);
        }
        return null;
    }

    @CalledByNative
    public static int openContentUriForRead(Context context, String uriString) {
        ParcelFileDescriptor pfd = getParcelFileDescriptor(context, uriString);
        if (pfd != null) {
            return pfd.detachFd();
        }
        return -1;
    }

    @CalledByNative
    public static boolean contentUriExists(Context context, String uriString) {
        return getParcelFileDescriptor(context, uriString) != null;
    }

    @CalledByNative
    public static String getMimeType(Context context, String uriString) {
        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {
            return null;
        }
        return resolver.getType(Uri.parse(uriString));
    }

    private static ParcelFileDescriptor getParcelFileDescriptor(Context context, String uriString) {
        ParcelFileDescriptor pfd = null;
        try {
            pfd = context.getContentResolver().openFileDescriptor(Uri.parse(uriString), "r");
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Cannot find content uri: " + uriString, e);
        } catch (SecurityException e2) {
            Log.w(TAG, "Cannot open content uri: " + uriString, e2);
        } catch (IllegalArgumentException e3) {
            Log.w(TAG, "Unknown content uri: " + uriString, e3);
        } catch (IllegalStateException e4) {
            Log.w(TAG, "Unknown content uri: " + uriString, e4);
        }
        return pfd;
    }

    public static String getDisplayName(Uri uri, ContentResolver contentResolver, String columnField) {
        if (contentResolver == null || uri == null) {
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        }
        Cursor cursor = null;
        String string;
        try {
            cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.getCount() >= 1) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(columnField);
                if (index > -1) {
                    string = cursor.getString(index);
                    if (cursor == null) {
                        return string;
                    }
                    cursor.close();
                    return string;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        } catch (NullPointerException e) {
            string = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
            if (cursor == null) {
                return string;
            }
            cursor.close();
            return string;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
