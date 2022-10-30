package org.apache.cordova;

import android.net.Uri;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.cordova.CordovaResourceApi.OpenForReadResult;
import org.apache.cordova.PluginResult.Status;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.json.JSONException;
import org.json.JSONObject;

public class Zip extends CordovaPlugin {
    private static final String LOG_TAG = "Zip";

    /* renamed from: org.apache.cordova.Zip.1 */
    class C02471 implements Runnable {
        final /* synthetic */ CordovaArgs val$args;
        final /* synthetic */ CallbackContext val$callbackContext;

        C02471(CordovaArgs cordovaArgs, CallbackContext callbackContext) {
            this.val$args = cordovaArgs;
            this.val$callbackContext = callbackContext;
        }

        public void run() {
            Zip.this.unzipSync(this.val$args, this.val$callbackContext);
        }
    }

    private static class ProgressEvent {
        private long loaded;
        private long total;

        private ProgressEvent() {
        }

        public long getLoaded() {
            return this.loaded;
        }

        public void setLoaded(long loaded) {
            this.loaded = loaded;
        }

        public void addLoaded(long add) {
            this.loaded += add;
        }

        public long getTotal() {
            return this.total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public JSONObject toJSONObject() throws JSONException {
            return new JSONObject("{loaded:" + this.loaded + ",total:" + this.total + "}");
        }
    }

    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if (!"unzip".equals(action)) {
            return false;
        }
        unzip(args, callbackContext);
        return true;
    }

    private void unzip(CordovaArgs args, CallbackContext callbackContext) {
        this.cordova.getThreadPool().execute(new C02471(args, callbackContext));
    }

    private static int readInt(InputStream is) throws IOException {
        return (((is.read() << 8) | is.read()) | (is.read() << 16)) | (is.read() << 24);
    }

    private void unzipSync(CordovaArgs args, CallbackContext callbackContext) {
        Exception e;
        Throwable th;
        InputStream inputStream = null;
        String zipFileName = args.getString(0);
        String outputDirectory = args.getString(1);
        Uri zipUri = getUriForArg(zipFileName);
        Uri outputUri = getUriForArg(outputDirectory);
        CordovaResourceApi resourceApi = this.webView.getResourceApi();
        File tempFile = resourceApi.mapUriToFile(zipUri);
        if (tempFile == null || !tempFile.exists()) {
            String errorMessage = "Zip file does not exist";
            callbackContext.error(errorMessage);
            Log.e(LOG_TAG, errorMessage);
            if (inputStream != null) {
                try {
                    inputStream.close();
                    return;
                } catch (IOException e2) {
                    return;
                }
            }
            return;
        }
        File outputDir = resourceApi.mapUriToFile(outputUri);
        outputDirectory = outputDir.getAbsolutePath();
        outputDirectory = outputDirectory + (outputDirectory.endsWith(File.separator) ? CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE : File.separator);
        if (outputDir == null || !(outputDir.exists() || outputDir.mkdirs())) {
            errorMessage = "Could not create output directory";
            callbackContext.error(errorMessage);
            Log.e(LOG_TAG, errorMessage);
            if (inputStream != null) {
                try {
                    inputStream.close();
                    return;
                } catch (IOException e3) {
                    return;
                }
            }
            return;
        }
        OpenForReadResult zipFile = resourceApi.openForRead(zipUri);
        ProgressEvent progressEvent = new ProgressEvent();
        progressEvent.setTotal(zipFile.length);
        InputStream inputStream2 = new BufferedInputStream(zipFile.inputStream);
        try {
            inputStream2.mark(10);
            if (readInt(inputStream2) != 875721283) {
                inputStream2.reset();
            } else {
                readInt(inputStream2);
                int pubkeyLength = readInt(inputStream2);
                int signatureLength = readInt(inputStream2);
                inputStream2.skip((long) (pubkeyLength + signatureLength));
                progressEvent.setLoaded((long) ((pubkeyLength + 16) + signatureLength));
            }
            InputStream zipInputStream = new ZipInputStream(inputStream2);
            inputStream = zipInputStream;
            byte[] buffer = new byte[AccessibilityNodeInfoCompat.ACTION_PASTE];
            boolean anyEntries = false;
            while (true) {
                ZipEntry ze = zipInputStream.getNextEntry();
                if (ze == null) {
                    break;
                }
                anyEntries = true;
                String compressedName = ze.getName();
                if (ze.isDirectory()) {
                    new File(outputDirectory + compressedName).mkdirs();
                } else {
                    File file = new File(outputDirectory + compressedName);
                    file.getParentFile().mkdirs();
                    if (file.exists() || file.createNewFile()) {
                        Log.w(LOG_TAG, "extracting: " + file.getPath());
                        FileOutputStream fout = new FileOutputStream(file);
                        while (true) {
                            int count = zipInputStream.read(buffer);
                            if (count != -1) {
                                fout.write(buffer, 0, count);
                            } else {
                                try {
                                    break;
                                } catch (Exception e4) {
                                    e = e4;
                                }
                            }
                        }
                        fout.close();
                    }
                }
                progressEvent.addLoaded(ze.getCompressedSize());
                updateProgress(callbackContext, progressEvent);
                zipInputStream.closeEntry();
            }
            progressEvent.setLoaded(progressEvent.getTotal());
            updateProgress(callbackContext, progressEvent);
            if (anyEntries) {
                callbackContext.success();
            } else {
                callbackContext.error("Bad zip file");
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e5) {
                }
            }
        } catch (Exception e6) {
            e = e6;
            inputStream = inputStream2;
            try {
                errorMessage = "An error occurred while unzipping.";
                callbackContext.error(errorMessage);
                Log.e(LOG_TAG, errorMessage, e);
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e7) {
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e8) {
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            inputStream = inputStream2;
            if (inputStream != null) {
                inputStream.close();
            }
            throw th;
        }
    }

    private void updateProgress(CallbackContext callbackContext, ProgressEvent progress) throws JSONException {
        PluginResult pluginResult = new PluginResult(Status.OK, progress.toJSONObject());
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }

    private Uri getUriForArg(String arg) {
        CordovaResourceApi resourceApi = this.webView.getResourceApi();
        Uri tmpTarget = Uri.parse(arg);
        if (tmpTarget.getScheme() == null) {
            tmpTarget = Uri.fromFile(new File(arg));
        }
        return resourceApi.remapUri(tmpTarget);
    }
}
