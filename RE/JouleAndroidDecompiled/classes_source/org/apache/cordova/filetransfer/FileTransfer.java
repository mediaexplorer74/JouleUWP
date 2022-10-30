package org.apache.cordova.filetransfer;

import android.net.Uri;
import android.util.Log;
import android.webkit.CookieManager;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaResourceApi.OpenForReadResult;
import org.apache.cordova.PluginManager;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.apache.cordova.Whitelist;
import org.apache.cordova.file.FileUtils;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

public class FileTransfer extends CordovaPlugin {
    public static int ABORTED_ERR = 0;
    private static final String BOUNDARY = "+++++";
    public static int CONNECTION_ERR = 0;
    private static final HostnameVerifier DO_NOT_VERIFY;
    public static int FILE_NOT_FOUND_ERR = 0;
    public static int INVALID_URL_ERR = 0;
    private static final String LINE_END = "\r\n";
    private static final String LINE_START = "--";
    private static final String LOG_TAG = "FileTransfer";
    private static final int MAX_BUFFER_SIZE = 16384;
    public static int NOT_MODIFIED_ERR;
    private static HashMap<String, RequestContext> activeRequests;
    private static final TrustManager[] trustAllCerts;

    /* renamed from: org.apache.cordova.filetransfer.FileTransfer.1 */
    class C02681 implements Runnable {
        final /* synthetic */ boolean val$chunkedMode;
        final /* synthetic */ RequestContext val$context;
        final /* synthetic */ String val$fileKey;
        final /* synthetic */ String val$fileName;
        final /* synthetic */ JSONObject val$headers;
        final /* synthetic */ String val$httpMethod;
        final /* synthetic */ String val$mimeType;
        final /* synthetic */ String val$objectId;
        final /* synthetic */ JSONObject val$params;
        final /* synthetic */ CordovaResourceApi val$resourceApi;
        final /* synthetic */ String val$source;
        final /* synthetic */ Uri val$sourceUri;
        final /* synthetic */ String val$target;
        final /* synthetic */ Uri val$targetUri;
        final /* synthetic */ boolean val$trustEveryone;
        final /* synthetic */ boolean val$useHttps;

        C02681(RequestContext requestContext, CordovaResourceApi cordovaResourceApi, Uri uri, boolean z, boolean z2, String str, JSONObject jSONObject, String str2, JSONObject jSONObject2, String str3, String str4, String str5, Uri uri2, boolean z3, String str6, String str7) {
            this.val$context = requestContext;
            this.val$resourceApi = cordovaResourceApi;
            this.val$targetUri = uri;
            this.val$useHttps = z;
            this.val$trustEveryone = z2;
            this.val$httpMethod = str;
            this.val$headers = jSONObject;
            this.val$target = str2;
            this.val$params = jSONObject2;
            this.val$fileKey = str3;
            this.val$fileName = str4;
            this.val$mimeType = str5;
            this.val$sourceUri = uri2;
            this.val$chunkedMode = z3;
            this.val$source = str6;
            this.val$objectId = str7;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r42 = this;
            r0 = r42;
            r0 = r0.val$context;
            r37 = r0;
            r0 = r37;
            r0 = r0.aborted;
            r37 = r0;
            if (r37 == 0) goto L_0x000f;
        L_0x000e:
            return;
        L_0x000f:
            r10 = 0;
            r20 = 0;
            r21 = 0;
            r35 = 0;
            r14 = -1;
            r30 = new org.apache.cordova.filetransfer.FileUploadResult;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r30.<init>();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r23 = new org.apache.cordova.filetransfer.FileProgressResult;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r23.<init>();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$resourceApi;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            r0 = r42;
            r0 = r0.val$targetUri;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            r10 = r37.createHttpConnection(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$useHttps;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            if (r37 == 0) goto L_0x0056;
        L_0x0039:
            r0 = r42;
            r0 = r0.val$trustEveryone;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            if (r37 == 0) goto L_0x0056;
        L_0x0041:
            r0 = r10;
            r0 = (javax.net.ssl.HttpsURLConnection) r0;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r15 = r0;
            r21 = org.apache.cordova.filetransfer.FileTransfer.trustAllHosts(r15);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r20 = r15.getHostnameVerifier();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = org.apache.cordova.filetransfer.FileTransfer.DO_NOT_VERIFY;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r37;
            r15.setHostnameVerifier(r0);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x0056:
            r37 = 1;
            r0 = r37;
            r10.setDoInput(r0);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = 1;
            r0 = r37;
            r10.setDoOutput(r0);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = 0;
            r0 = r37;
            r10.setUseCaches(r0);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$httpMethod;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            r0 = r37;
            r10.setRequestMethod(r0);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$headers;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            if (r37 == 0) goto L_0x008c;
        L_0x007e:
            r0 = r42;
            r0 = r0.val$headers;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            r38 = "Content-Type";
            r37 = r37.has(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            if (r37 != 0) goto L_0x02bd;
        L_0x008c:
            r19 = 1;
        L_0x008e:
            if (r19 == 0) goto L_0x009b;
        L_0x0090:
            r37 = "Content-Type";
            r38 = "multipart/form-data; boundary=+++++";
            r0 = r37;
            r1 = r38;
            r10.setRequestProperty(r0, r1);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x009b:
            r0 = r42;
            r0 = org.apache.cordova.filetransfer.FileTransfer.this;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            r0 = r42;
            r0 = r0.val$target;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            r11 = r37.getCookies(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            if (r11 == 0) goto L_0x00b4;
        L_0x00ad:
            r37 = "Cookie";
            r0 = r37;
            r10.setRequestProperty(r0, r11);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x00b4:
            r0 = r42;
            r0 = r0.val$headers;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            if (r37 == 0) goto L_0x00c7;
        L_0x00bc:
            r0 = r42;
            r0 = r0.val$headers;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            r0 = r37;
            org.apache.cordova.filetransfer.FileTransfer.addHeadersToRequest(r10, r0);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x00c7:
            r4 = new java.lang.StringBuilder;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r4.<init>();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$params;	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r37 = r0;
            r17 = r37.keys();	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
        L_0x00d6:
            r37 = r17.hasNext();	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            if (r37 == 0) goto L_0x014a;
        L_0x00dc:
            r18 = r17.next();	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r37 = java.lang.String.valueOf(r18);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r38 = "headers";
            r37 = r37.equals(r38);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            if (r37 != 0) goto L_0x00d6;
        L_0x00ec:
            r37 = "--";
            r0 = r37;
            r37 = r4.append(r0);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r38 = "+++++";
            r37 = r37.append(r38);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r38 = "\r\n";
            r37.append(r38);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r37 = "Content-Disposition: form-data; name=\"";
            r0 = r37;
            r37 = r4.append(r0);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r38 = r18.toString();	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r37 = r37.append(r38);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r38 = 34;
            r37.append(r38);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r37 = "\r\n";
            r0 = r37;
            r37 = r4.append(r0);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r38 = "\r\n";
            r37.append(r38);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$params;	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r37 = r0;
            r38 = r18.toString();	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r37 = r37.getString(r38);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r0 = r37;
            r4.append(r0);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            r37 = "\r\n";
            r0 = r37;
            r4.append(r0);	 Catch:{ JSONException -> 0x013c, FileNotFoundException -> 0x02cd, IOException -> 0x043e, Throwable -> 0x0643 }
            goto L_0x00d6;
        L_0x013c:
            r12 = move-exception;
            r37 = "FileTransfer";
            r38 = r12.getMessage();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r37;
            r1 = r38;
            android.util.Log.e(r0, r1, r12);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x014a:
            r37 = "--";
            r0 = r37;
            r37 = r4.append(r0);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = "+++++";
            r37 = r37.append(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = "\r\n";
            r37.append(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = "Content-Disposition: form-data; name=\"";
            r0 = r37;
            r37 = r4.append(r0);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$fileKey;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            r37 = r37.append(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = "\";";
            r37.append(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = " filename=\"";
            r0 = r37;
            r37 = r4.append(r0);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$fileName;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            r37 = r37.append(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = 34;
            r37 = r37.append(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = "\r\n";
            r37.append(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = "Content-Type: ";
            r0 = r37;
            r37 = r4.append(r0);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$mimeType;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            r37 = r37.append(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = "\r\n";
            r37 = r37.append(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = "\r\n";
            r37.append(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r4.toString();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = "UTF-8";
            r5 = r37.getBytes(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = "\r\n--+++++--\r\n";
            r38 = "UTF-8";
            r34 = r37.getBytes(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$resourceApi;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            r0 = r42;
            r0 = r0.val$sourceUri;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            r27 = r37.openForRead(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r5.length;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            r0 = r34;
            r0 = r0.length;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            r32 = r37 + r38;
            r0 = r27;
            r0 = r0.length;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            r40 = 0;
            r37 = (r38 > r40 ? 1 : (r38 == r40 ? 0 : -1));
            if (r37 < 0) goto L_0x0206;
        L_0x01e6:
            r0 = r27;
            r0 = r0.length;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            r0 = r38;
            r14 = (int) r0;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            if (r19 == 0) goto L_0x01f3;
        L_0x01f1:
            r14 = r14 + r32;
        L_0x01f3:
            r37 = 1;
            r0 = r23;
            r1 = r37;
            r0.setLengthComputable(r1);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = (long) r14;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            r0 = r23;
            r1 = r38;
            r0.setTotal(r1);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x0206:
            r37 = "FileTransfer";
            r38 = new java.lang.StringBuilder;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38.<init>();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r39 = "Content Length: ";
            r38 = r38.append(r39);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r38;
            r38 = r0.append(r14);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r38.toString();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            android.util.Log.d(r37, r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$chunkedMode;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            if (r37 == 0) goto L_0x02c1;
        L_0x0228:
            r37 = android.os.Build.VERSION.SDK_INT;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = 8;
            r0 = r37;
            r1 = r38;
            if (r0 < r1) goto L_0x023a;
        L_0x0232:
            r0 = r42;
            r0 = r0.val$useHttps;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            if (r37 == 0) goto L_0x02c1;
        L_0x023a:
            r36 = 1;
        L_0x023c:
            if (r36 != 0) goto L_0x0244;
        L_0x023e:
            r37 = -1;
            r0 = r37;
            if (r14 != r0) goto L_0x02c5;
        L_0x0244:
            r36 = 1;
        L_0x0246:
            if (r36 == 0) goto L_0x02c9;
        L_0x0248:
            r37 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
            r0 = r37;
            r10.setChunkedStreamingMode(r0);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = "Transfer-Encoding";
            r38 = "chunked";
            r0 = r37;
            r1 = r38;
            r10.setRequestProperty(r0, r1);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x025a:
            r10.connect();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r31 = 0;
            r31 = r10.getOutputStream();	 Catch:{ all -> 0x0430 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x0430 }
            r38 = r0;
            monitor-enter(r38);	 Catch:{ all -> 0x0430 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x04de }
            r37 = r0;
            r0 = r37;
            r0 = r0.aborted;	 Catch:{ all -> 0x04de }
            r37 = r0;
            if (r37 == 0) goto L_0x0342;
        L_0x0278:
            monitor-exit(r38);	 Catch:{ all -> 0x04de }
            r0 = r27;
            r0 = r0.inputStream;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            org.apache.cordova.filetransfer.FileTransfer.safeClose(r37);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            org.apache.cordova.filetransfer.FileTransfer.safeClose(r31);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;
            monitor-enter(r38);
            r37 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;	 Catch:{ all -> 0x033f }
            r0 = r42;
            r0 = r0.val$objectId;	 Catch:{ all -> 0x033f }
            r39 = r0;
            r0 = r37;
            r1 = r39;
            r0.remove(r1);	 Catch:{ all -> 0x033f }
            monitor-exit(r38);	 Catch:{ all -> 0x033f }
            if (r10 == 0) goto L_0x000e;
        L_0x029e:
            r0 = r42;
            r0 = r0.val$trustEveryone;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x02a6:
            r0 = r42;
            r0 = r0.val$useHttps;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x02ae:
            r15 = r10;
            r15 = (javax.net.ssl.HttpsURLConnection) r15;
            r0 = r20;
            r15.setHostnameVerifier(r0);
            r0 = r21;
            r15.setSSLSocketFactory(r0);
            goto L_0x000e;
        L_0x02bd:
            r19 = 0;
            goto L_0x008e;
        L_0x02c1:
            r36 = 0;
            goto L_0x023c;
        L_0x02c5:
            r36 = 0;
            goto L_0x0246;
        L_0x02c9:
            r10.setFixedLengthStreamingMode(r14);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            goto L_0x025a;
        L_0x02cd:
            r12 = move-exception;
            r37 = org.apache.cordova.filetransfer.FileTransfer.FILE_NOT_FOUND_ERR;	 Catch:{ all -> 0x070e }
            r0 = r42;
            r0 = r0.val$source;	 Catch:{ all -> 0x070e }
            r38 = r0;
            r0 = r42;
            r0 = r0.val$target;	 Catch:{ all -> 0x070e }
            r39 = r0;
            r0 = r37;
            r1 = r38;
            r2 = r39;
            r13 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r0, r1, r2, r10, r12);	 Catch:{ all -> 0x070e }
            r37 = "FileTransfer";
            r38 = r13.toString();	 Catch:{ all -> 0x070e }
            r0 = r37;
            r1 = r38;
            android.util.Log.e(r0, r1, r12);	 Catch:{ all -> 0x070e }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x070e }
            r37 = r0;
            r38 = new org.apache.cordova.PluginResult;	 Catch:{ all -> 0x070e }
            r39 = org.apache.cordova.PluginResult.Status.IO_EXCEPTION;	 Catch:{ all -> 0x070e }
            r0 = r38;
            r1 = r39;
            r0.<init>(r1, r13);	 Catch:{ all -> 0x070e }
            r37.sendPluginResult(r38);	 Catch:{ all -> 0x070e }
            r38 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;
            monitor-enter(r38);
            r37 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;	 Catch:{ all -> 0x07ed }
            r0 = r42;
            r0 = r0.val$objectId;	 Catch:{ all -> 0x07ed }
            r39 = r0;
            r0 = r37;
            r1 = r39;
            r0.remove(r1);	 Catch:{ all -> 0x07ed }
            monitor-exit(r38);	 Catch:{ all -> 0x07ed }
            if (r10 == 0) goto L_0x000e;
        L_0x0320:
            r0 = r42;
            r0 = r0.val$trustEveryone;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x0328:
            r0 = r42;
            r0 = r0.val$useHttps;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x0330:
            r15 = r10;
            r15 = (javax.net.ssl.HttpsURLConnection) r15;
            r0 = r20;
            r15.setHostnameVerifier(r0);
            r0 = r21;
            r15.setSSLSocketFactory(r0);
            goto L_0x000e;
        L_0x033f:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x033f }
            throw r37;
        L_0x0342:
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x04de }
            r37 = r0;
            r0 = r37;
            r0.connection = r10;	 Catch:{ all -> 0x04de }
            monitor-exit(r38);	 Catch:{ all -> 0x04de }
            if (r19 == 0) goto L_0x0359;
        L_0x034f:
            r0 = r31;
            r0.write(r5);	 Catch:{ all -> 0x0430 }
            r0 = r5.length;	 Catch:{ all -> 0x0430 }
            r37 = r0;
            r35 = r35 + r37;
        L_0x0359:
            r0 = r27;
            r0 = r0.inputStream;	 Catch:{ all -> 0x0430 }
            r37 = r0;
            r8 = r37.available();	 Catch:{ all -> 0x0430 }
            r37 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
            r0 = r37;
            r7 = java.lang.Math.min(r8, r0);	 Catch:{ all -> 0x0430 }
            r6 = new byte[r7];	 Catch:{ all -> 0x0430 }
            r0 = r27;
            r0 = r0.inputStream;	 Catch:{ all -> 0x0430 }
            r37 = r0;
            r38 = 0;
            r0 = r37;
            r1 = r38;
            r9 = r0.read(r6, r1, r7);	 Catch:{ all -> 0x0430 }
            r24 = 0;
        L_0x037f:
            if (r9 <= 0) goto L_0x04e1;
        L_0x0381:
            r0 = r35;
            r0 = (long) r0;	 Catch:{ all -> 0x0430 }
            r38 = r0;
            r0 = r30;
            r1 = r38;
            r0.setBytesSent(r1);	 Catch:{ all -> 0x0430 }
            r37 = 0;
            r0 = r31;
            r1 = r37;
            r0.write(r6, r1, r9);	 Catch:{ all -> 0x0430 }
            r35 = r35 + r9;
            r0 = r35;
            r0 = (long) r0;	 Catch:{ all -> 0x0430 }
            r38 = r0;
            r40 = 102400; // 0x19000 float:1.43493E-40 double:5.05923E-319;
            r40 = r40 + r24;
            r37 = (r38 > r40 ? 1 : (r38 == r40 ? 0 : -1));
            if (r37 <= 0) goto L_0x03d9;
        L_0x03a6:
            r0 = r35;
            r0 = (long) r0;	 Catch:{ all -> 0x0430 }
            r24 = r0;
            r37 = "FileTransfer";
            r38 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0430 }
            r38.<init>();	 Catch:{ all -> 0x0430 }
            r39 = "Uploaded ";
            r38 = r38.append(r39);	 Catch:{ all -> 0x0430 }
            r0 = r38;
            r1 = r35;
            r38 = r0.append(r1);	 Catch:{ all -> 0x0430 }
            r39 = " of ";
            r38 = r38.append(r39);	 Catch:{ all -> 0x0430 }
            r0 = r38;
            r38 = r0.append(r14);	 Catch:{ all -> 0x0430 }
            r39 = " bytes";
            r38 = r38.append(r39);	 Catch:{ all -> 0x0430 }
            r38 = r38.toString();	 Catch:{ all -> 0x0430 }
            android.util.Log.d(r37, r38);	 Catch:{ all -> 0x0430 }
        L_0x03d9:
            r0 = r27;
            r0 = r0.inputStream;	 Catch:{ all -> 0x0430 }
            r37 = r0;
            r8 = r37.available();	 Catch:{ all -> 0x0430 }
            r37 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
            r0 = r37;
            r7 = java.lang.Math.min(r8, r0);	 Catch:{ all -> 0x0430 }
            r0 = r27;
            r0 = r0.inputStream;	 Catch:{ all -> 0x0430 }
            r37 = r0;
            r38 = 0;
            r0 = r37;
            r1 = r38;
            r9 = r0.read(r6, r1, r7);	 Catch:{ all -> 0x0430 }
            r0 = r35;
            r0 = (long) r0;	 Catch:{ all -> 0x0430 }
            r38 = r0;
            r0 = r23;
            r1 = r38;
            r0.setLoaded(r1);	 Catch:{ all -> 0x0430 }
            r26 = new org.apache.cordova.PluginResult;	 Catch:{ all -> 0x0430 }
            r37 = org.apache.cordova.PluginResult.Status.OK;	 Catch:{ all -> 0x0430 }
            r38 = r23.toJSONObject();	 Catch:{ all -> 0x0430 }
            r0 = r26;
            r1 = r37;
            r2 = r38;
            r0.<init>(r1, r2);	 Catch:{ all -> 0x0430 }
            r37 = 1;
            r0 = r26;
            r1 = r37;
            r0.setKeepCallback(r1);	 Catch:{ all -> 0x0430 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x0430 }
            r37 = r0;
            r0 = r37;
            r1 = r26;
            r0.sendPluginResult(r1);	 Catch:{ all -> 0x0430 }
            goto L_0x037f;
        L_0x0430:
            r37 = move-exception;
            r0 = r27;
            r0 = r0.inputStream;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            org.apache.cordova.filetransfer.FileTransfer.safeClose(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            org.apache.cordova.filetransfer.FileTransfer.safeClose(r31);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            throw r37;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x043e:
            r12 = move-exception;
            r37 = org.apache.cordova.filetransfer.FileTransfer.CONNECTION_ERR;	 Catch:{ all -> 0x070e }
            r0 = r42;
            r0 = r0.val$source;	 Catch:{ all -> 0x070e }
            r38 = r0;
            r0 = r42;
            r0 = r0.val$target;	 Catch:{ all -> 0x070e }
            r39 = r0;
            r0 = r37;
            r1 = r38;
            r2 = r39;
            r13 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r0, r1, r2, r10, r12);	 Catch:{ all -> 0x070e }
            r37 = "FileTransfer";
            r38 = r13.toString();	 Catch:{ all -> 0x070e }
            r0 = r37;
            r1 = r38;
            android.util.Log.e(r0, r1, r12);	 Catch:{ all -> 0x070e }
            r37 = "FileTransfer";
            r38 = new java.lang.StringBuilder;	 Catch:{ all -> 0x070e }
            r38.<init>();	 Catch:{ all -> 0x070e }
            r39 = "Failed after uploading ";
            r38 = r38.append(r39);	 Catch:{ all -> 0x070e }
            r0 = r38;
            r1 = r35;
            r38 = r0.append(r1);	 Catch:{ all -> 0x070e }
            r39 = " of ";
            r38 = r38.append(r39);	 Catch:{ all -> 0x070e }
            r0 = r38;
            r38 = r0.append(r14);	 Catch:{ all -> 0x070e }
            r39 = " bytes.";
            r38 = r38.append(r39);	 Catch:{ all -> 0x070e }
            r38 = r38.toString();	 Catch:{ all -> 0x070e }
            android.util.Log.e(r37, r38);	 Catch:{ all -> 0x070e }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x070e }
            r37 = r0;
            r38 = new org.apache.cordova.PluginResult;	 Catch:{ all -> 0x070e }
            r39 = org.apache.cordova.PluginResult.Status.IO_EXCEPTION;	 Catch:{ all -> 0x070e }
            r0 = r38;
            r1 = r39;
            r0.<init>(r1, r13);	 Catch:{ all -> 0x070e }
            r37.sendPluginResult(r38);	 Catch:{ all -> 0x070e }
            r38 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;
            monitor-enter(r38);
            r37 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;	 Catch:{ all -> 0x07f0 }
            r0 = r42;
            r0 = r0.val$objectId;	 Catch:{ all -> 0x07f0 }
            r39 = r0;
            r0 = r37;
            r1 = r39;
            r0.remove(r1);	 Catch:{ all -> 0x07f0 }
            monitor-exit(r38);	 Catch:{ all -> 0x07f0 }
            if (r10 == 0) goto L_0x000e;
        L_0x04bf:
            r0 = r42;
            r0 = r0.val$trustEveryone;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x04c7:
            r0 = r42;
            r0 = r0.val$useHttps;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x04cf:
            r15 = r10;
            r15 = (javax.net.ssl.HttpsURLConnection) r15;
            r0 = r20;
            r15.setHostnameVerifier(r0);
            r0 = r21;
            r15.setSSLSocketFactory(r0);
            goto L_0x000e;
        L_0x04de:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x04de }
            throw r37;	 Catch:{ all -> 0x0430 }
        L_0x04e1:
            if (r19 == 0) goto L_0x04f1;
        L_0x04e3:
            r0 = r31;
            r1 = r34;
            r0.write(r1);	 Catch:{ all -> 0x0430 }
            r0 = r34;
            r0 = r0.length;	 Catch:{ all -> 0x0430 }
            r37 = r0;
            r35 = r35 + r37;
        L_0x04f1:
            r31.flush();	 Catch:{ all -> 0x0430 }
            r0 = r27;
            r0 = r0.inputStream;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            org.apache.cordova.filetransfer.FileTransfer.safeClose(r37);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            org.apache.cordova.filetransfer.FileTransfer.safeClose(r31);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            monitor-enter(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x05e7 }
            r37 = r0;
            r39 = 0;
            r0 = r39;
            r1 = r37;
            r1.connection = r0;	 Catch:{ all -> 0x05e7 }
            monitor-exit(r38);	 Catch:{ all -> 0x05e7 }
            r37 = "FileTransfer";
            r38 = new java.lang.StringBuilder;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38.<init>();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r39 = "Sent ";
            r38 = r38.append(r39);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r38;
            r1 = r35;
            r38 = r0.append(r1);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r39 = " of ";
            r38 = r38.append(r39);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r38;
            r38 = r0.append(r14);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r38.toString();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            android.util.Log.d(r37, r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r28 = r10.getResponseCode();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = "FileTransfer";
            r38 = new java.lang.StringBuilder;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38.<init>();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r39 = "response code: ";
            r38 = r38.append(r39);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r38;
            r1 = r28;
            r38 = r0.append(r1);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r38.toString();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            android.util.Log.d(r37, r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = "FileTransfer";
            r38 = new java.lang.StringBuilder;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38.<init>();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r39 = "response headers: ";
            r38 = r38.append(r39);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r39 = r10.getHeaderFields();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r38.append(r39);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r38.toString();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            android.util.Log.d(r37, r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r16 = 0;
            r16 = org.apache.cordova.filetransfer.FileTransfer.getInputStream(r10);	 Catch:{ all -> 0x06f3 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x06f3 }
            r38 = r0;
            monitor-enter(r38);	 Catch:{ all -> 0x06f3 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x0742 }
            r37 = r0;
            r0 = r37;
            r0 = r0.aborted;	 Catch:{ all -> 0x0742 }
            r37 = r0;
            if (r37 == 0) goto L_0x06bc;
        L_0x0595:
            monitor-exit(r38);	 Catch:{ all -> 0x0742 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            monitor-enter(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x0640 }
            r37 = r0;
            r39 = 0;
            r0 = r39;
            r1 = r37;
            r1.connection = r0;	 Catch:{ all -> 0x0640 }
            monitor-exit(r38);	 Catch:{ all -> 0x0640 }
            org.apache.cordova.filetransfer.FileTransfer.safeClose(r16);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;
            monitor-enter(r38);
            r37 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;	 Catch:{ all -> 0x06b9 }
            r0 = r42;
            r0 = r0.val$objectId;	 Catch:{ all -> 0x06b9 }
            r39 = r0;
            r0 = r37;
            r1 = r39;
            r0.remove(r1);	 Catch:{ all -> 0x06b9 }
            monitor-exit(r38);	 Catch:{ all -> 0x06b9 }
            if (r10 == 0) goto L_0x000e;
        L_0x05c8:
            r0 = r42;
            r0 = r0.val$trustEveryone;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x05d0:
            r0 = r42;
            r0 = r0.val$useHttps;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x05d8:
            r15 = r10;
            r15 = (javax.net.ssl.HttpsURLConnection) r15;
            r0 = r20;
            r15.setHostnameVerifier(r0);
            r0 = r21;
            r15.setSSLSocketFactory(r0);
            goto L_0x000e;
        L_0x05e7:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x05e7 }
            throw r37;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x05ea:
            r12 = move-exception;
            r37 = "FileTransfer";
            r38 = r12.getMessage();	 Catch:{ all -> 0x070e }
            r0 = r37;
            r1 = r38;
            android.util.Log.e(r0, r1, r12);	 Catch:{ all -> 0x070e }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x070e }
            r37 = r0;
            r38 = new org.apache.cordova.PluginResult;	 Catch:{ all -> 0x070e }
            r39 = org.apache.cordova.PluginResult.Status.JSON_EXCEPTION;	 Catch:{ all -> 0x070e }
            r38.<init>(r39);	 Catch:{ all -> 0x070e }
            r37.sendPluginResult(r38);	 Catch:{ all -> 0x070e }
            r38 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;
            monitor-enter(r38);
            r37 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;	 Catch:{ all -> 0x07f3 }
            r0 = r42;
            r0 = r0.val$objectId;	 Catch:{ all -> 0x07f3 }
            r39 = r0;
            r0 = r37;
            r1 = r39;
            r0.remove(r1);	 Catch:{ all -> 0x07f3 }
            monitor-exit(r38);	 Catch:{ all -> 0x07f3 }
            if (r10 == 0) goto L_0x000e;
        L_0x0621:
            r0 = r42;
            r0 = r0.val$trustEveryone;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x0629:
            r0 = r42;
            r0 = r0.val$useHttps;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x0631:
            r15 = r10;
            r15 = (javax.net.ssl.HttpsURLConnection) r15;
            r0 = r20;
            r15.setHostnameVerifier(r0);
            r0 = r21;
            r15.setSSLSocketFactory(r0);
            goto L_0x000e;
        L_0x0640:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x0640 }
            throw r37;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x0643:
            r33 = move-exception;
            r37 = org.apache.cordova.filetransfer.FileTransfer.CONNECTION_ERR;	 Catch:{ all -> 0x070e }
            r0 = r42;
            r0 = r0.val$source;	 Catch:{ all -> 0x070e }
            r38 = r0;
            r0 = r42;
            r0 = r0.val$target;	 Catch:{ all -> 0x070e }
            r39 = r0;
            r0 = r37;
            r1 = r38;
            r2 = r39;
            r3 = r33;
            r13 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r0, r1, r2, r10, r3);	 Catch:{ all -> 0x070e }
            r37 = "FileTransfer";
            r38 = r13.toString();	 Catch:{ all -> 0x070e }
            r0 = r37;
            r1 = r38;
            r2 = r33;
            android.util.Log.e(r0, r1, r2);	 Catch:{ all -> 0x070e }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x070e }
            r37 = r0;
            r38 = new org.apache.cordova.PluginResult;	 Catch:{ all -> 0x070e }
            r39 = org.apache.cordova.PluginResult.Status.IO_EXCEPTION;	 Catch:{ all -> 0x070e }
            r0 = r38;
            r1 = r39;
            r0.<init>(r1, r13);	 Catch:{ all -> 0x070e }
            r37.sendPluginResult(r38);	 Catch:{ all -> 0x070e }
            r38 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;
            monitor-enter(r38);
            r37 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;	 Catch:{ all -> 0x07f6 }
            r0 = r42;
            r0 = r0.val$objectId;	 Catch:{ all -> 0x07f6 }
            r39 = r0;
            r0 = r37;
            r1 = r39;
            r0.remove(r1);	 Catch:{ all -> 0x07f6 }
            monitor-exit(r38);	 Catch:{ all -> 0x07f6 }
            if (r10 == 0) goto L_0x000e;
        L_0x069a:
            r0 = r42;
            r0 = r0.val$trustEveryone;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x06a2:
            r0 = r42;
            r0 = r0.val$useHttps;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x06aa:
            r15 = r10;
            r15 = (javax.net.ssl.HttpsURLConnection) r15;
            r0 = r20;
            r15.setHostnameVerifier(r0);
            r0 = r21;
            r15.setSSLSocketFactory(r0);
            goto L_0x000e;
        L_0x06b9:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x06b9 }
            throw r37;
        L_0x06bc:
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x0742 }
            r37 = r0;
            r0 = r37;
            r0.connection = r10;	 Catch:{ all -> 0x0742 }
            monitor-exit(r38);	 Catch:{ all -> 0x0742 }
            r22 = new java.io.ByteArrayOutputStream;	 Catch:{ all -> 0x06f3 }
            r37 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
            r38 = r10.getContentLength();	 Catch:{ all -> 0x06f3 }
            r37 = java.lang.Math.max(r37, r38);	 Catch:{ all -> 0x06f3 }
            r0 = r22;
            r1 = r37;
            r0.<init>(r1);	 Catch:{ all -> 0x06f3 }
            r37 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
            r0 = r37;
            r6 = new byte[r0];	 Catch:{ all -> 0x06f3 }
            r9 = 0;
        L_0x06e1:
            r0 = r16;
            r9 = r0.read(r6);	 Catch:{ all -> 0x06f3 }
            if (r9 <= 0) goto L_0x0745;
        L_0x06e9:
            r37 = 0;
            r0 = r22;
            r1 = r37;
            r0.write(r6, r1, r9);	 Catch:{ all -> 0x06f3 }
            goto L_0x06e1;
        L_0x06f3:
            r37 = move-exception;
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            monitor-enter(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x07e7 }
            r39 = r0;
            r40 = 0;
            r0 = r40;
            r1 = r39;
            r1.connection = r0;	 Catch:{ all -> 0x07e7 }
            monitor-exit(r38);	 Catch:{ all -> 0x07e7 }
            org.apache.cordova.filetransfer.FileTransfer.safeClose(r16);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            throw r37;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x070e:
            r37 = move-exception;
            r38 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;
            monitor-enter(r38);
            r39 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;	 Catch:{ all -> 0x07f9 }
            r0 = r42;
            r0 = r0.val$objectId;	 Catch:{ all -> 0x07f9 }
            r40 = r0;
            r39.remove(r40);	 Catch:{ all -> 0x07f9 }
            monitor-exit(r38);	 Catch:{ all -> 0x07f9 }
            if (r10 == 0) goto L_0x0741;
        L_0x0724:
            r0 = r42;
            r0 = r0.val$trustEveryone;
            r38 = r0;
            if (r38 == 0) goto L_0x0741;
        L_0x072c:
            r0 = r42;
            r0 = r0.val$useHttps;
            r38 = r0;
            if (r38 == 0) goto L_0x0741;
        L_0x0734:
            r15 = r10;
            r15 = (javax.net.ssl.HttpsURLConnection) r15;
            r0 = r20;
            r15.setHostnameVerifier(r0);
            r0 = r21;
            r15.setSSLSocketFactory(r0);
        L_0x0741:
            throw r37;
        L_0x0742:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x0742 }
            throw r37;	 Catch:{ all -> 0x06f3 }
        L_0x0745:
            r37 = "UTF-8";
            r0 = r22;
            r1 = r37;
            r29 = r0.toString(r1);	 Catch:{ all -> 0x06f3 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = r0;
            monitor-enter(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ all -> 0x07e4 }
            r37 = r0;
            r39 = 0;
            r0 = r39;
            r1 = r37;
            r1.connection = r0;	 Catch:{ all -> 0x07e4 }
            monitor-exit(r38);	 Catch:{ all -> 0x07e4 }
            org.apache.cordova.filetransfer.FileTransfer.safeClose(r16);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = "FileTransfer";
            r38 = "got response from server";
            android.util.Log.d(r37, r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = "FileTransfer";
            r38 = 0;
            r39 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
            r40 = r29.length();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r39 = java.lang.Math.min(r39, r40);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r29;
            r1 = r38;
            r2 = r39;
            r38 = r0.substring(r1, r2);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            android.util.Log.d(r37, r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r30;
            r1 = r28;
            r0.setResponseCode(r1);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r30;
            r1 = r29;
            r0.setResponse(r1);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r0 = r42;
            r0 = r0.val$context;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37 = r0;
            r38 = new org.apache.cordova.PluginResult;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r39 = org.apache.cordova.PluginResult.Status.OK;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r40 = r30.toJSONObject();	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38.<init>(r39, r40);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r37.sendPluginResult(r38);	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
            r38 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;
            monitor-enter(r38);
            r37 = org.apache.cordova.filetransfer.FileTransfer.activeRequests;	 Catch:{ all -> 0x07ea }
            r0 = r42;
            r0 = r0.val$objectId;	 Catch:{ all -> 0x07ea }
            r39 = r0;
            r0 = r37;
            r1 = r39;
            r0.remove(r1);	 Catch:{ all -> 0x07ea }
            monitor-exit(r38);	 Catch:{ all -> 0x07ea }
            if (r10 == 0) goto L_0x000e;
        L_0x07c5:
            r0 = r42;
            r0 = r0.val$trustEveryone;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x07cd:
            r0 = r42;
            r0 = r0.val$useHttps;
            r37 = r0;
            if (r37 == 0) goto L_0x000e;
        L_0x07d5:
            r15 = r10;
            r15 = (javax.net.ssl.HttpsURLConnection) r15;
            r0 = r20;
            r15.setHostnameVerifier(r0);
            r0 = r21;
            r15.setSSLSocketFactory(r0);
            goto L_0x000e;
        L_0x07e4:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x07e4 }
            throw r37;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x07e7:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x07e7 }
            throw r37;	 Catch:{ FileNotFoundException -> 0x02cd, IOException -> 0x043e, JSONException -> 0x05ea, Throwable -> 0x0643 }
        L_0x07ea:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x07ea }
            throw r37;
        L_0x07ed:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x07ed }
            throw r37;
        L_0x07f0:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x07f0 }
            throw r37;
        L_0x07f3:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x07f3 }
            throw r37;
        L_0x07f6:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x07f6 }
            throw r37;
        L_0x07f9:
            r37 = move-exception;
            monitor-exit(r38);	 Catch:{ all -> 0x07f9 }
            throw r37;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.filetransfer.FileTransfer.1.run():void");
        }
    }

    /* renamed from: org.apache.cordova.filetransfer.FileTransfer.2 */
    static class C02692 implements HostnameVerifier {
        C02692() {
        }

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /* renamed from: org.apache.cordova.filetransfer.FileTransfer.3 */
    static class C02703 implements X509TrustManager {
        C02703() {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }

    /* renamed from: org.apache.cordova.filetransfer.FileTransfer.4 */
    class C02714 implements Runnable {
        final /* synthetic */ RequestContext val$context;
        final /* synthetic */ JSONObject val$headers;
        final /* synthetic */ boolean val$isLocalTransfer;
        final /* synthetic */ String val$objectId;
        final /* synthetic */ CordovaResourceApi val$resourceApi;
        final /* synthetic */ String val$source;
        final /* synthetic */ Uri val$sourceUri;
        final /* synthetic */ String val$target;
        final /* synthetic */ Uri val$targetUri;
        final /* synthetic */ boolean val$trustEveryone;
        final /* synthetic */ boolean val$useHttps;

        C02714(RequestContext requestContext, CordovaResourceApi cordovaResourceApi, Uri uri, Uri uri2, boolean z, boolean z2, boolean z3, JSONObject jSONObject, String str, String str2, String str3) {
            this.val$context = requestContext;
            this.val$resourceApi = cordovaResourceApi;
            this.val$targetUri = uri;
            this.val$sourceUri = uri2;
            this.val$isLocalTransfer = z;
            this.val$useHttps = z2;
            this.val$trustEveryone = z3;
            this.val$headers = jSONObject;
            this.val$source = str;
            this.val$target = str2;
            this.val$objectId = str3;
        }

        public void run() {
            HttpsURLConnection https;
            PluginResult pluginResult;
            FileNotFoundException e;
            IOException e2;
            JSONException e3;
            Throwable e4;
            Throwable th;
            if (!this.val$context.aborted) {
                HttpURLConnection connection = null;
                HostnameVerifier oldHostnameVerifier = null;
                SSLSocketFactory oldSocketFactory = null;
                File file = null;
                PluginResult result = null;
                TrackingInputStream inputStream = null;
                boolean cached = false;
                PluginResult result2;
                JSONObject error;
                try {
                    file = this.val$resourceApi.mapUriToFile(this.val$targetUri);
                    this.val$context.targetFile = file;
                    Log.d(FileTransfer.LOG_TAG, "Download file:" + this.val$sourceUri);
                    FileProgressResult progress = new FileProgressResult();
                    if (this.val$isLocalTransfer) {
                        OpenForReadResult readResult = this.val$resourceApi.openForRead(this.val$sourceUri);
                        if (readResult.length != -1) {
                            progress.setLengthComputable(true);
                            progress.setTotal(readResult.length);
                        }
                        inputStream = new SimpleTrackingInputStream(readResult.inputStream);
                        result2 = null;
                    } else {
                        connection = this.val$resourceApi.createHttpConnection(this.val$sourceUri);
                        if (this.val$useHttps && this.val$trustEveryone) {
                            https = (HttpsURLConnection) connection;
                            oldSocketFactory = FileTransfer.trustAllHosts(https);
                            oldHostnameVerifier = https.getHostnameVerifier();
                            https.setHostnameVerifier(FileTransfer.DO_NOT_VERIFY);
                        }
                        connection.setRequestMethod("GET");
                        String cookie = FileTransfer.this.getCookies(this.val$sourceUri.toString());
                        if (cookie != null) {
                            connection.setRequestProperty("cookie", cookie);
                        }
                        connection.setRequestProperty("Accept-Encoding", "gzip");
                        if (this.val$headers != null) {
                            FileTransfer.addHeadersToRequest(connection, this.val$headers);
                        }
                        connection.connect();
                        if (connection.getResponseCode() == 304) {
                            cached = true;
                            connection.disconnect();
                            Log.d(FileTransfer.LOG_TAG, "Resource not modified: " + this.val$source);
                            error = FileTransfer.createFileTransferError(FileTransfer.NOT_MODIFIED_ERR, this.val$source, this.val$target, connection, null);
                            pluginResult = new PluginResult(Status.ERROR, error);
                        } else {
                            if ((connection.getContentEncoding() == null || connection.getContentEncoding().equalsIgnoreCase("gzip")) && connection.getContentLength() != -1) {
                                progress.setLengthComputable(true);
                                progress.setTotal((long) connection.getContentLength());
                            }
                            inputStream = FileTransfer.getInputStream(connection);
                            result2 = null;
                        }
                    }
                    if (cached) {
                        result = result2;
                    } else {
                        try {
                            synchronized (this.val$context) {
                                if (this.val$context.aborted) {
                                    synchronized (this.val$context) {
                                        this.val$context.connection = null;
                                    }
                                    FileTransfer.safeClose(inputStream);
                                    FileTransfer.safeClose(null);
                                    synchronized (FileTransfer.activeRequests) {
                                        FileTransfer.activeRequests.remove(this.val$objectId);
                                    }
                                    if (connection != null && this.val$trustEveryone && this.val$useHttps) {
                                        https = (HttpsURLConnection) connection;
                                        https.setHostnameVerifier(oldHostnameVerifier);
                                        https.setSSLSocketFactory(oldSocketFactory);
                                    }
                                    if (result2 == null) {
                                        pluginResult = new PluginResult(Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, null));
                                    } else {
                                        result = result2;
                                    }
                                    if (!(cached || result.getStatus() == Status.OK.ordinal() || file == null)) {
                                        file.delete();
                                    }
                                    this.val$context.sendPluginResult(result);
                                    return;
                                }
                                this.val$context.connection = connection;
                                byte[] buffer = new byte[FileTransfer.MAX_BUFFER_SIZE];
                                OutputStream outputStream = this.val$resourceApi.openOutputStream(this.val$targetUri);
                                while (true) {
                                    int bytesRead = inputStream.read(buffer);
                                    if (bytesRead <= 0) {
                                        break;
                                    }
                                    outputStream.write(buffer, 0, bytesRead);
                                    progress.setLoaded(inputStream.getTotalRawBytesRead());
                                    pluginResult = new PluginResult(Status.OK, progress.toJSONObject());
                                    pluginResult.setKeepCallback(true);
                                    this.val$context.sendPluginResult(pluginResult);
                                }
                                synchronized (this.val$context) {
                                    this.val$context.connection = null;
                                }
                                FileTransfer.safeClose(inputStream);
                                FileTransfer.safeClose(outputStream);
                                Log.d(FileTransfer.LOG_TAG, "Saved file: " + this.val$target);
                                Class webViewClass = FileTransfer.this.webView.getClass();
                                PluginManager pm = null;
                                try {
                                    pm = (PluginManager) webViewClass.getMethod("getPluginManager", new Class[0]).invoke(FileTransfer.this.webView, new Object[0]);
                                } catch (NoSuchMethodException e5) {
                                } catch (IllegalAccessException e6) {
                                } catch (InvocationTargetException e7) {
                                }
                                if (pm == null) {
                                    try {
                                        pm = (PluginManager) webViewClass.getField("pluginManager").get(FileTransfer.this.webView);
                                    } catch (NoSuchFieldException e8) {
                                    } catch (IllegalAccessException e9) {
                                    }
                                }
                                file = this.val$resourceApi.mapUriToFile(this.val$targetUri);
                                this.val$context.targetFile = file;
                                FileUtils filePlugin = (FileUtils) pm.getPlugin("File");
                                if (filePlugin != null) {
                                    JSONObject fileEntry = filePlugin.getEntryForFile(file);
                                    if (fileEntry != null) {
                                        pluginResult = new PluginResult(Status.OK, fileEntry);
                                    } else {
                                        error = FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, null);
                                        Log.e(FileTransfer.LOG_TAG, "File plugin cannot represent download path");
                                        pluginResult = new PluginResult(Status.IO_EXCEPTION, error);
                                    }
                                } else {
                                    Log.e(FileTransfer.LOG_TAG, "File plugin not found; cannot save downloaded file");
                                    pluginResult = new PluginResult(Status.ERROR, "File plugin not found; cannot save downloaded file");
                                }
                            }
                        } catch (FileNotFoundException e10) {
                            e = e10;
                        } catch (IOException e11) {
                            e2 = e11;
                            error = FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, e2);
                            Log.e(FileTransfer.LOG_TAG, error.toString(), e2);
                            pluginResult = new PluginResult(Status.IO_EXCEPTION, error);
                            synchronized (FileTransfer.activeRequests) {
                                FileTransfer.activeRequests.remove(this.val$objectId);
                            }
                            if (connection != null && this.val$trustEveryone && this.val$useHttps) {
                                https = (HttpsURLConnection) connection;
                                https.setHostnameVerifier(oldHostnameVerifier);
                                https.setSSLSocketFactory(oldSocketFactory);
                            }
                            if (pluginResult == null) {
                                pluginResult = new PluginResult(Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, null));
                            }
                            if (!(cached || result.getStatus() == Status.OK.ordinal() || file == null)) {
                                file.delete();
                            }
                            this.val$context.sendPluginResult(result);
                        } catch (JSONException e12) {
                            e3 = e12;
                            Log.e(FileTransfer.LOG_TAG, e3.getMessage(), e3);
                            pluginResult = new PluginResult(Status.JSON_EXCEPTION);
                            synchronized (FileTransfer.activeRequests) {
                                FileTransfer.activeRequests.remove(this.val$objectId);
                            }
                            if (connection != null && this.val$trustEveryone && this.val$useHttps) {
                                https = (HttpsURLConnection) connection;
                                https.setHostnameVerifier(oldHostnameVerifier);
                                https.setSSLSocketFactory(oldSocketFactory);
                            }
                            if (pluginResult == null) {
                                pluginResult = new PluginResult(Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, null));
                            }
                            if (!(cached || result.getStatus() == Status.OK.ordinal() || file == null)) {
                                file.delete();
                            }
                            this.val$context.sendPluginResult(result);
                        } catch (Throwable th2) {
                            e4 = th2;
                            error = FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, e4);
                            Log.e(FileTransfer.LOG_TAG, error.toString(), e4);
                            pluginResult = new PluginResult(Status.IO_EXCEPTION, error);
                            synchronized (FileTransfer.activeRequests) {
                                FileTransfer.activeRequests.remove(this.val$objectId);
                            }
                            if (connection != null && this.val$trustEveryone && this.val$useHttps) {
                                https = (HttpsURLConnection) connection;
                                https.setHostnameVerifier(oldHostnameVerifier);
                                https.setSSLSocketFactory(oldSocketFactory);
                            }
                            if (pluginResult == null) {
                                pluginResult = new PluginResult(Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, null));
                            }
                            if (!(cached || result.getStatus() == Status.OK.ordinal() || file == null)) {
                                file.delete();
                            }
                            this.val$context.sendPluginResult(result);
                        }
                    }
                    synchronized (FileTransfer.activeRequests) {
                        FileTransfer.activeRequests.remove(this.val$objectId);
                    }
                    if (connection != null && this.val$trustEveryone && this.val$useHttps) {
                        https = (HttpsURLConnection) connection;
                        https.setHostnameVerifier(oldHostnameVerifier);
                        https.setSSLSocketFactory(oldSocketFactory);
                    }
                    if (result == null) {
                        pluginResult = new PluginResult(Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, null));
                    }
                    if (!(cached || result.getStatus() == Status.OK.ordinal() || file == null)) {
                        file.delete();
                    }
                    this.val$context.sendPluginResult(result);
                } catch (FileNotFoundException e13) {
                    e = e13;
                    result2 = null;
                    try {
                        error = FileTransfer.createFileTransferError(FileTransfer.FILE_NOT_FOUND_ERR, this.val$source, this.val$target, connection, e);
                        Log.e(FileTransfer.LOG_TAG, error.toString(), e);
                        pluginResult = new PluginResult(Status.IO_EXCEPTION, error);
                        synchronized (FileTransfer.activeRequests) {
                            FileTransfer.activeRequests.remove(this.val$objectId);
                        }
                        if (connection != null && this.val$trustEveryone && this.val$useHttps) {
                            https = (HttpsURLConnection) connection;
                            https.setHostnameVerifier(oldHostnameVerifier);
                            https.setSSLSocketFactory(oldSocketFactory);
                        }
                        if (pluginResult == null) {
                            pluginResult = new PluginResult(Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, null));
                        }
                        if (!(cached || result.getStatus() == Status.OK.ordinal() || file == null)) {
                            file.delete();
                        }
                        this.val$context.sendPluginResult(result);
                    } catch (Throwable th3) {
                        th = th3;
                        result = result2;
                        synchronized (FileTransfer.activeRequests) {
                            FileTransfer.activeRequests.remove(this.val$objectId);
                        }
                        if (connection != null && this.val$trustEveryone && this.val$useHttps) {
                            https = (HttpsURLConnection) connection;
                            https.setHostnameVerifier(oldHostnameVerifier);
                            https.setSSLSocketFactory(oldSocketFactory);
                        }
                        if (result == null) {
                            pluginResult = new PluginResult(Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, null));
                        }
                        if (!(cached || result.getStatus() == Status.OK.ordinal() || file == null)) {
                            file.delete();
                        }
                        this.val$context.sendPluginResult(result);
                        throw th;
                    }
                } catch (IOException e14) {
                    e2 = e14;
                    result2 = null;
                    error = FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, e2);
                    Log.e(FileTransfer.LOG_TAG, error.toString(), e2);
                    pluginResult = new PluginResult(Status.IO_EXCEPTION, error);
                    synchronized (FileTransfer.activeRequests) {
                        FileTransfer.activeRequests.remove(this.val$objectId);
                    }
                    https = (HttpsURLConnection) connection;
                    https.setHostnameVerifier(oldHostnameVerifier);
                    https.setSSLSocketFactory(oldSocketFactory);
                    if (pluginResult == null) {
                        pluginResult = new PluginResult(Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, null));
                    }
                    file.delete();
                    this.val$context.sendPluginResult(result);
                } catch (JSONException e15) {
                    e3 = e15;
                    result2 = null;
                    Log.e(FileTransfer.LOG_TAG, e3.getMessage(), e3);
                    pluginResult = new PluginResult(Status.JSON_EXCEPTION);
                    synchronized (FileTransfer.activeRequests) {
                        FileTransfer.activeRequests.remove(this.val$objectId);
                    }
                    https = (HttpsURLConnection) connection;
                    https.setHostnameVerifier(oldHostnameVerifier);
                    https.setSSLSocketFactory(oldSocketFactory);
                    if (pluginResult == null) {
                        pluginResult = new PluginResult(Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, null));
                    }
                    file.delete();
                    this.val$context.sendPluginResult(result);
                } catch (Throwable th4) {
                    th = th4;
                    synchronized (FileTransfer.activeRequests) {
                        FileTransfer.activeRequests.remove(this.val$objectId);
                    }
                    https = (HttpsURLConnection) connection;
                    https.setHostnameVerifier(oldHostnameVerifier);
                    https.setSSLSocketFactory(oldSocketFactory);
                    if (result == null) {
                        pluginResult = new PluginResult(Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.CONNECTION_ERR, this.val$source, this.val$target, connection, null));
                    }
                    file.delete();
                    this.val$context.sendPluginResult(result);
                    throw th;
                }
            }
        }
    }

    /* renamed from: org.apache.cordova.filetransfer.FileTransfer.5 */
    class C02725 implements Runnable {
        final /* synthetic */ RequestContext val$context;

        C02725(RequestContext requestContext) {
            this.val$context = requestContext;
        }

        public void run() {
            synchronized (this.val$context) {
                File file = this.val$context.targetFile;
                if (file != null) {
                    file.delete();
                }
                this.val$context.sendPluginResult(new PluginResult(Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.ABORTED_ERR, this.val$context.source, this.val$context.target, null, Integer.valueOf(-1), null)));
                this.val$context.aborted = true;
                if (this.val$context.connection != null) {
                    try {
                        this.val$context.connection.disconnect();
                    } catch (Exception e) {
                        Log.e(FileTransfer.LOG_TAG, "CB-8431 Catch workaround for fatal exception", e);
                    }
                }
            }
        }
    }

    private static class ExposedGZIPInputStream extends GZIPInputStream {
        public ExposedGZIPInputStream(InputStream in) throws IOException {
            super(in);
        }

        public Inflater getInflater() {
            return this.inf;
        }
    }

    private static final class RequestContext {
        boolean aborted;
        CallbackContext callbackContext;
        HttpURLConnection connection;
        String source;
        String target;
        File targetFile;

        RequestContext(String source, String target, CallbackContext callbackContext) {
            this.source = source;
            this.target = target;
            this.callbackContext = callbackContext;
        }

        void sendPluginResult(PluginResult pluginResult) {
            synchronized (this) {
                if (!this.aborted) {
                    this.callbackContext.sendPluginResult(pluginResult);
                }
            }
        }
    }

    private static abstract class TrackingInputStream extends FilterInputStream {
        public abstract long getTotalRawBytesRead();

        public TrackingInputStream(InputStream in) {
            super(in);
        }
    }

    private static class SimpleTrackingInputStream extends TrackingInputStream {
        private long bytesRead;

        public SimpleTrackingInputStream(InputStream stream) {
            super(stream);
            this.bytesRead = 0;
        }

        private int updateBytesRead(int newBytesRead) {
            if (newBytesRead != -1) {
                this.bytesRead += (long) newBytesRead;
            }
            return newBytesRead;
        }

        public int read() throws IOException {
            return updateBytesRead(super.read());
        }

        public int read(byte[] bytes, int offset, int count) throws IOException {
            return updateBytesRead(super.read(bytes, offset, count));
        }

        public long getTotalRawBytesRead() {
            return this.bytesRead;
        }
    }

    private static class TrackingGZIPInputStream extends TrackingInputStream {
        private ExposedGZIPInputStream gzin;

        public TrackingGZIPInputStream(ExposedGZIPInputStream gzin) throws IOException {
            super(gzin);
            this.gzin = gzin;
        }

        public long getTotalRawBytesRead() {
            return this.gzin.getInflater().getBytesRead();
        }
    }

    static {
        FILE_NOT_FOUND_ERR = 1;
        INVALID_URL_ERR = 2;
        CONNECTION_ERR = 3;
        ABORTED_ERR = 4;
        NOT_MODIFIED_ERR = 5;
        activeRequests = new HashMap();
        DO_NOT_VERIFY = new C02692();
        trustAllCerts = new TrustManager[]{new C02703()};
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("upload") || action.equals("download")) {
            String source = args.getString(0);
            String target = args.getString(1);
            if (action.equals("upload")) {
                upload(source, target, args, callbackContext);
                return true;
            }
            download(source, target, args, callbackContext);
            return true;
        } else if (!action.equals("abort")) {
            return false;
        } else {
            abort(args.getString(0));
            callbackContext.success();
            return true;
        }
    }

    private static void addHeadersToRequest(URLConnection connection, JSONObject headers) {
        try {
            Iterator<?> iter = headers.keys();
            while (iter.hasNext()) {
                String headerKey = iter.next().toString();
                String cleanHeaderKey = headerKey.replaceAll("\\n", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE).replaceAll("\\s+", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE).replaceAll(":", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE).replaceAll("[^\\x20-\\x7E]+", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
                JSONArray headerValues = headers.optJSONArray(headerKey);
                if (headerValues == null) {
                    headerValues = new JSONArray();
                    headerValues.put(headers.getString(headerKey).replaceAll("\\s+", " ").replaceAll("\\n", " ").replaceAll("[^\\x20-\\x7E]+", " "));
                }
                connection.setRequestProperty(cleanHeaderKey, headerValues.getString(0));
                for (int i = 1; i < headerValues.length(); i++) {
                    connection.addRequestProperty(headerKey, headerValues.getString(i));
                }
            }
        } catch (JSONException e) {
        }
    }

    private String getCookies(String target) {
        boolean gotCookie = false;
        String cookie = null;
        try {
            Method gcmMethod = this.webView.getClass().getMethod("getCookieManager", new Class[0]);
            Class iccmClass = gcmMethod.getReturnType();
            cookie = (String) iccmClass.getMethod("getCookie", new Class[]{String.class}).invoke(iccmClass.cast(gcmMethod.invoke(this.webView, new Object[0])), new Object[]{target});
            gotCookie = true;
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e2) {
        } catch (InvocationTargetException e3) {
        } catch (ClassCastException e4) {
        }
        if (gotCookie || CookieManager.getInstance() == null) {
            return cookie;
        }
        return CookieManager.getInstance().getCookie(target);
    }

    private void upload(String source, String target, JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject headers;
        Log.d(LOG_TAG, "upload " + source + " to " + target);
        String fileKey = getArgument(args, 2, AndroidProtocolHandler.FILE_SCHEME);
        String fileName = getArgument(args, 3, "image.jpg");
        String mimeType = getArgument(args, 4, "image/jpeg");
        JSONObject params = args.optJSONObject(5) == null ? new JSONObject() : args.optJSONObject(5);
        boolean trustEveryone = args.optBoolean(6);
        boolean chunkedMode = args.optBoolean(7) || args.isNull(7);
        if (args.optJSONObject(8) == null) {
            headers = params.optJSONObject("headers");
        } else {
            headers = args.optJSONObject(8);
        }
        String objectId = args.getString(9);
        String httpMethod = getArgument(args, 10, "POST");
        CordovaResourceApi resourceApi = this.webView.getResourceApi();
        Log.d(LOG_TAG, "fileKey: " + fileKey);
        Log.d(LOG_TAG, "fileName: " + fileName);
        Log.d(LOG_TAG, "mimeType: " + mimeType);
        Log.d(LOG_TAG, "params: " + params);
        Log.d(LOG_TAG, "trustEveryone: " + trustEveryone);
        Log.d(LOG_TAG, "chunkedMode: " + chunkedMode);
        Log.d(LOG_TAG, "headers: " + headers);
        Log.d(LOG_TAG, "objectId: " + objectId);
        Log.d(LOG_TAG, "httpMethod: " + httpMethod);
        Uri targetUri = resourceApi.remapUri(Uri.parse(target));
        Uri tmpSrc = Uri.parse(source);
        if (tmpSrc.getScheme() == null) {
            tmpSrc = Uri.fromFile(new File(source));
        }
        Uri sourceUri = resourceApi.remapUri(tmpSrc);
        int uriType = CordovaResourceApi.getUriType(targetUri);
        boolean useHttps = uriType == 6;
        if (uriType == 5 || useHttps) {
            RequestContext context = new RequestContext(source, target, callbackContext);
            synchronized (activeRequests) {
                activeRequests.put(objectId, context);
            }
            this.cordova.getThreadPool().execute(new C02681(context, resourceApi, targetUri, useHttps, trustEveryone, httpMethod, headers, target, params, fileKey, fileName, mimeType, sourceUri, chunkedMode, source, objectId));
            return;
        }
        JSONObject error = createFileTransferError(INVALID_URL_ERR, source, target, null, Integer.valueOf(0), false);
        Log.e(LOG_TAG, "Unsupported URI: " + targetUri);
        callbackContext.sendPluginResult(new PluginResult(Status.IO_EXCEPTION, error));
    }

    private static void safeClose(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    private static TrackingInputStream getInputStream(URLConnection conn) throws IOException {
        String encoding = conn.getContentEncoding();
        if (encoding == null || !encoding.equalsIgnoreCase("gzip")) {
            return new SimpleTrackingInputStream(conn.getInputStream());
        }
        return new TrackingGZIPInputStream(new ExposedGZIPInputStream(conn.getInputStream()));
    }

    private static SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            connection.setSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return oldFactory;
    }

    private static JSONObject createFileTransferError(int errorCode, String source, String target, URLConnection connection, Throwable throwable) {
        int httpStatus = 0;
        StringBuilder bodyBuilder = new StringBuilder();
        String body = null;
        if (connection != null) {
            BufferedReader reader;
            try {
                if (connection instanceof HttpURLConnection) {
                    httpStatus = ((HttpURLConnection) connection).getResponseCode();
                    InputStream err = ((HttpURLConnection) connection).getErrorStream();
                    if (err != null) {
                        reader = new BufferedReader(new InputStreamReader(err, "UTF-8"));
                        String line = reader.readLine();
                        while (line != null) {
                            bodyBuilder.append(line);
                            line = reader.readLine();
                            if (line != null) {
                                bodyBuilder.append('\n');
                            }
                        }
                        body = bodyBuilder.toString();
                        reader.close();
                    }
                }
            } catch (Throwable e) {
                Log.w(LOG_TAG, "Error getting HTTP status code from connection.", e);
            }
        }
        return createFileTransferError(errorCode, source, target, body, Integer.valueOf(httpStatus), throwable);
    }

    private static JSONObject createFileTransferError(int errorCode, String source, String target, String body, Integer httpStatus, Throwable throwable) {
        JSONException e;
        JSONObject error = null;
        try {
            JSONObject error2 = new JSONObject();
            try {
                error2.put("code", errorCode);
                error2.put("source", source);
                error2.put("target", target);
                if (body != null) {
                    error2.put(MessagingSmsConsts.BODY, body);
                }
                if (httpStatus != null) {
                    error2.put("http_status", httpStatus);
                }
                if (throwable != null) {
                    String msg = throwable.getMessage();
                    if (msg == null || CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE.equals(msg)) {
                        msg = throwable.toString();
                    }
                    error2.put("exception", msg);
                }
                return error2;
            } catch (JSONException e2) {
                e = e2;
                error = error2;
                Log.e(LOG_TAG, e.getMessage(), e);
                return error;
            }
        } catch (JSONException e3) {
            e = e3;
            Log.e(LOG_TAG, e.getMessage(), e);
            return error;
        }
    }

    private static String getArgument(JSONArray args, int position, String defaultString) {
        String arg = defaultString;
        if (args.length() <= position) {
            return arg;
        }
        arg = args.optString(position);
        if (arg == null || "null".equals(arg)) {
            return defaultString;
        }
        return arg;
    }

    private void download(String source, String target, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(LOG_TAG, "download " + source + " to " + target);
        CordovaResourceApi resourceApi = this.webView.getResourceApi();
        boolean trustEveryone = args.optBoolean(2);
        String objectId = args.getString(3);
        JSONObject headers = args.optJSONObject(4);
        Uri sourceUri = resourceApi.remapUri(Uri.parse(source));
        Uri tmpTarget = Uri.parse(target);
        if (tmpTarget.getScheme() == null) {
            tmpTarget = Uri.fromFile(new File(target));
        }
        Uri targetUri = resourceApi.remapUri(tmpTarget);
        int uriType = CordovaResourceApi.getUriType(sourceUri);
        boolean useHttps = uriType == 6;
        boolean isLocalTransfer = (useHttps || uriType == 5) ? false : true;
        if (uriType == -1) {
            JSONObject error = createFileTransferError(INVALID_URL_ERR, source, target, null, Integer.valueOf(0), null);
            Log.e(LOG_TAG, "Unsupported URI: " + sourceUri);
            callbackContext.sendPluginResult(new PluginResult(Status.IO_EXCEPTION, error));
            return;
        }
        Boolean shouldAllowRequest = null;
        if (isLocalTransfer) {
            shouldAllowRequest = Boolean.valueOf(true);
        }
        if (shouldAllowRequest == null) {
            try {
                shouldAllowRequest = Boolean.valueOf(((Whitelist) this.webView.getClass().getMethod("getWhitelist", new Class[0]).invoke(this.webView, new Object[0])).isUrlWhiteListed(source));
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException e2) {
            } catch (InvocationTargetException e3) {
            }
        }
        if (shouldAllowRequest == null) {
            try {
                PluginManager pm = (PluginManager) this.webView.getClass().getMethod("getPluginManager", new Class[0]).invoke(this.webView, new Object[0]);
                shouldAllowRequest = (Boolean) pm.getClass().getMethod("shouldAllowRequest", new Class[]{String.class}).invoke(pm, new Object[]{source});
            } catch (NoSuchMethodException e4) {
            } catch (IllegalAccessException e5) {
            } catch (InvocationTargetException e6) {
            }
        }
        if (Boolean.TRUE.equals(shouldAllowRequest)) {
            RequestContext context = new RequestContext(source, target, callbackContext);
            synchronized (activeRequests) {
                activeRequests.put(objectId, context);
            }
            this.cordova.getThreadPool().execute(new C02714(context, resourceApi, targetUri, sourceUri, isLocalTransfer, useHttps, trustEveryone, headers, source, target, objectId));
            return;
        }
        Log.w(LOG_TAG, "Source URL is not in white list: '" + source + "'");
        callbackContext.sendPluginResult(new PluginResult(Status.IO_EXCEPTION, createFileTransferError(CONNECTION_ERR, source, target, null, Integer.valueOf(401), null)));
    }

    private void abort(String objectId) {
        synchronized (activeRequests) {
            RequestContext context = (RequestContext) activeRequests.remove(objectId);
        }
        if (context != null) {
            this.cordova.getThreadPool().execute(new C02725(context));
        }
    }
}
