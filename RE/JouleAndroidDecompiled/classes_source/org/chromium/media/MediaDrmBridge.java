package org.chromium.media;

import android.annotation.TargetApi;
import android.media.DeniedByServerException;
import android.media.MediaCrypto;
import android.media.MediaCryptoException;
import android.media.MediaDrm;
import android.media.MediaDrm.KeyRequest;
import android.media.MediaDrm.MediaDrmStateException;
import android.media.MediaDrm.OnEventListener;
import android.media.MediaDrm.ProvisionRequest;
import android.media.MediaDrmException;
import android.media.NotProvisionedException;
import android.media.UnsupportedSchemeException;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import com.google.android.gms.common.ConnectionResult;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Executor;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.Log;
import org.chromium.blink_public.web.WebTextInputFlags;
import org.chromium.ui.base.PageTransition;

@JNINamespace("media")
@TargetApi(19)
public class MediaDrmBridge {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String ENABLE = "enable";
    private static final char[] HEX_CHAR_LOOKUP;
    private static final long INVALID_NATIVE_MEDIA_DRM_BRIDGE = 0;
    private static final int INVALID_SESSION_ID = 0;
    private static final int KEY_STATUS_EXPIRED = 2;
    private static final int KEY_STATUS_INTERNAL_ERROR = 1;
    private static final int KEY_STATUS_OUTPUT_NOT_ALLOWED = 3;
    private static final int KEY_STATUS_USABLE = 0;
    private static final String PRIVACY_MODE = "privacyMode";
    private static final String SECURITY_LEVEL = "securityLevel";
    private static final String SERVER_CERTIFICATE = "serviceCertificate";
    private static final String SESSION_SHARING = "sessionSharing";
    private static final String TAG = "cr.media";
    private Handler mHandler;
    private MediaCrypto mMediaCrypto;
    private byte[] mMediaCryptoSession;
    private MediaDrm mMediaDrm;
    private long mNativeMediaDrmBridge;
    private ArrayDeque<PendingCreateSessionData> mPendingCreateSessionDataQueue;
    private boolean mProvisioningPending;
    private boolean mResetDeviceCredentialsPending;
    private UUID mSchemeUUID;
    private HashMap<ByteBuffer, String> mSessionIds;

    /* renamed from: org.chromium.media.MediaDrmBridge.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ boolean val$success;

        AnonymousClass10(boolean z) {
            this.val$success = z;
        }

        public void run() {
            if (MediaDrmBridge.this.isNativeMediaDrmBridgeValid()) {
                MediaDrmBridge.this.nativeOnResetDeviceCredentialsCompleted(MediaDrmBridge.this.mNativeMediaDrmBridge, this.val$success);
            }
        }
    }

    /* renamed from: org.chromium.media.MediaDrmBridge.1 */
    class C03811 implements Runnable {
        C03811() {
        }

        public void run() {
            MediaDrmBridge.this.processPendingCreateSessionData();
        }
    }

    /* renamed from: org.chromium.media.MediaDrmBridge.2 */
    class C03822 implements Runnable {
        C03822() {
        }

        public void run() {
            if (MediaDrmBridge.this.isNativeMediaDrmBridgeValid()) {
                MediaDrmBridge.this.nativeOnMediaCryptoReady(MediaDrmBridge.this.mNativeMediaDrmBridge);
            }
        }
    }

    /* renamed from: org.chromium.media.MediaDrmBridge.3 */
    class C03833 implements Runnable {
        final /* synthetic */ long val$promiseId;

        C03833(long j) {
            this.val$promiseId = j;
        }

        public void run() {
            if (MediaDrmBridge.this.isNativeMediaDrmBridgeValid()) {
                MediaDrmBridge.this.nativeOnPromiseResolved(MediaDrmBridge.this.mNativeMediaDrmBridge, this.val$promiseId);
            }
        }
    }

    /* renamed from: org.chromium.media.MediaDrmBridge.4 */
    class C03844 implements Runnable {
        final /* synthetic */ long val$promiseId;
        final /* synthetic */ byte[] val$sessionId;

        C03844(long j, byte[] bArr) {
            this.val$promiseId = j;
            this.val$sessionId = bArr;
        }

        public void run() {
            if (MediaDrmBridge.this.isNativeMediaDrmBridgeValid()) {
                MediaDrmBridge.this.nativeOnPromiseResolvedWithSession(MediaDrmBridge.this.mNativeMediaDrmBridge, this.val$promiseId, this.val$sessionId);
            }
        }
    }

    /* renamed from: org.chromium.media.MediaDrmBridge.5 */
    class C03855 implements Runnable {
        final /* synthetic */ String val$errorMessage;
        final /* synthetic */ long val$promiseId;

        C03855(long j, String str) {
            this.val$promiseId = j;
            this.val$errorMessage = str;
        }

        public void run() {
            if (MediaDrmBridge.this.isNativeMediaDrmBridgeValid()) {
                MediaDrmBridge.this.nativeOnPromiseRejected(MediaDrmBridge.this.mNativeMediaDrmBridge, this.val$promiseId, this.val$errorMessage);
            }
        }
    }

    /* renamed from: org.chromium.media.MediaDrmBridge.6 */
    class C03866 implements Runnable {
        final /* synthetic */ KeyRequest val$request;
        final /* synthetic */ byte[] val$sessionId;

        C03866(byte[] bArr, KeyRequest keyRequest) {
            this.val$sessionId = bArr;
            this.val$request = keyRequest;
        }

        public void run() {
            if (MediaDrmBridge.this.isNativeMediaDrmBridgeValid()) {
                MediaDrmBridge.this.nativeOnSessionMessage(MediaDrmBridge.this.mNativeMediaDrmBridge, this.val$sessionId, this.val$request.getData(), this.val$request.getDefaultUrl());
            }
        }
    }

    /* renamed from: org.chromium.media.MediaDrmBridge.7 */
    class C03877 implements Runnable {
        final /* synthetic */ byte[] val$sessionId;

        C03877(byte[] bArr) {
            this.val$sessionId = bArr;
        }

        public void run() {
            if (MediaDrmBridge.this.isNativeMediaDrmBridgeValid()) {
                MediaDrmBridge.this.nativeOnSessionClosed(MediaDrmBridge.this.mNativeMediaDrmBridge, this.val$sessionId);
            }
        }
    }

    /* renamed from: org.chromium.media.MediaDrmBridge.8 */
    class C03888 implements Runnable {
        final /* synthetic */ boolean val$hasAdditionalUsableKey;
        final /* synthetic */ int val$keyStatus;
        final /* synthetic */ byte[] val$sessionId;

        C03888(byte[] bArr, boolean z, int i) {
            this.val$sessionId = bArr;
            this.val$hasAdditionalUsableKey = z;
            this.val$keyStatus = i;
        }

        public void run() {
            if (MediaDrmBridge.this.isNativeMediaDrmBridgeValid()) {
                MediaDrmBridge.this.nativeOnSessionKeysChange(MediaDrmBridge.this.mNativeMediaDrmBridge, this.val$sessionId, this.val$hasAdditionalUsableKey, this.val$keyStatus);
            }
        }
    }

    /* renamed from: org.chromium.media.MediaDrmBridge.9 */
    class C03899 implements Runnable {
        final /* synthetic */ String val$errorMessage;
        final /* synthetic */ byte[] val$sessionId;

        C03899(byte[] bArr, String str) {
            this.val$sessionId = bArr;
            this.val$errorMessage = str;
        }

        public void run() {
            if (MediaDrmBridge.this.isNativeMediaDrmBridgeValid()) {
                MediaDrmBridge.this.nativeOnLegacySessionError(MediaDrmBridge.this.mNativeMediaDrmBridge, this.val$sessionId, this.val$errorMessage);
            }
        }
    }

    private class MediaDrmListener implements OnEventListener {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !MediaDrmBridge.class.desiredAssertionStatus() ? true : MediaDrmBridge.$assertionsDisabled;
        }

        private MediaDrmListener() {
        }

        public void onEvent(MediaDrm mediaDrm, byte[] sessionId, int event, int extra, byte[] data) {
            if (sessionId == null) {
                Log.m32e(MediaDrmBridge.TAG, "MediaDrmListener: Null session.", new Object[MediaDrmBridge.KEY_STATUS_USABLE]);
            } else if (MediaDrmBridge.this.sessionExists(sessionId)) {
                switch (event) {
                    case MediaDrmBridge.KEY_STATUS_INTERNAL_ERROR /*1*/:
                        Log.m24d(MediaDrmBridge.TAG, "MediaDrm.EVENT_PROVISION_REQUIRED");
                    case MediaDrmBridge.KEY_STATUS_EXPIRED /*2*/:
                        Log.m24d(MediaDrmBridge.TAG, "MediaDrm.EVENT_KEY_REQUIRED");
                        if (!MediaDrmBridge.this.mProvisioningPending) {
                            try {
                                KeyRequest request = MediaDrmBridge.this.getKeyRequest(sessionId, data, (String) MediaDrmBridge.this.mSessionIds.get(ByteBuffer.wrap(sessionId)), null);
                                if (request != null) {
                                    MediaDrmBridge.this.onSessionMessage(sessionId, request);
                                    return;
                                }
                                MediaDrmBridge.this.onLegacySessionError(sessionId, "MediaDrm EVENT_KEY_REQUIRED: Failed to generate request.");
                                MediaDrmBridge.this.onSessionKeysChange(sessionId, MediaDrmBridge.$assertionsDisabled, MediaDrmBridge.KEY_STATUS_INTERNAL_ERROR);
                            } catch (NotProvisionedException e) {
                                r5 = new Object[MediaDrmBridge.KEY_STATUS_INTERNAL_ERROR];
                                r5[MediaDrmBridge.KEY_STATUS_USABLE] = e;
                                Log.m32e(MediaDrmBridge.TAG, "Device not provisioned", r5);
                                MediaDrmBridge.this.startProvisioning();
                            }
                        }
                    case MediaDrmBridge.KEY_STATUS_OUTPUT_NOT_ALLOWED /*3*/:
                        Log.m24d(MediaDrmBridge.TAG, "MediaDrm.EVENT_KEY_EXPIRED");
                        MediaDrmBridge.this.onLegacySessionError(sessionId, "MediaDrm EVENT_KEY_EXPIRED.");
                        MediaDrmBridge.this.onSessionKeysChange(sessionId, MediaDrmBridge.$assertionsDisabled, MediaDrmBridge.KEY_STATUS_EXPIRED);
                    case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                        Log.m24d(MediaDrmBridge.TAG, "MediaDrm.EVENT_VENDOR_DEFINED");
                        if (!$assertionsDisabled) {
                            throw new AssertionError();
                        }
                    default:
                        Log.m32e(MediaDrmBridge.TAG, "Invalid DRM event " + event, new Object[MediaDrmBridge.KEY_STATUS_USABLE]);
                }
            } else {
                r5 = new Object[MediaDrmBridge.KEY_STATUS_INTERNAL_ERROR];
                r5[MediaDrmBridge.KEY_STATUS_USABLE] = MediaDrmBridge.bytesToHexString(sessionId);
                Log.m32e(MediaDrmBridge.TAG, "MediaDrmListener: Invalid session %s", r5);
            }
        }
    }

    private static class PendingCreateSessionData {
        private final byte[] mInitData;
        private final String mMimeType;
        private final HashMap<String, String> mOptionalParameters;
        private final long mPromiseId;

        private PendingCreateSessionData(byte[] initData, String mimeType, HashMap<String, String> optionalParameters, long promiseId) {
            this.mInitData = initData;
            this.mMimeType = mimeType;
            this.mOptionalParameters = optionalParameters;
            this.mPromiseId = promiseId;
        }

        private byte[] initData() {
            return this.mInitData;
        }

        private String mimeType() {
            return this.mMimeType;
        }

        private HashMap<String, String> optionalParameters() {
            return this.mOptionalParameters;
        }

        private long promiseId() {
            return this.mPromiseId;
        }
    }

    private class PostRequestTask extends AsyncTask<String, Void, Void> {
        private static final String TAG = "PostRequestTask";
        private byte[] mDrmRequest;
        private byte[] mResponseBody;

        public PostRequestTask(byte[] drmRequest) {
            this.mDrmRequest = drmRequest;
        }

        protected Void doInBackground(String... urls) {
            this.mResponseBody = postRequest(urls[MediaDrmBridge.KEY_STATUS_USABLE], this.mDrmRequest);
            if (this.mResponseBody != null) {
                Log.m25d(TAG, "response length=%d", Integer.valueOf(this.mResponseBody.length));
            }
            return null;
        }

        private byte[] postRequest(String url, byte[] drmRequest) {
            HttpURLConnection urlConnection = null;
            BufferedInputStream bis;
            try {
                urlConnection = (HttpURLConnection) new URL(url + "&signedRequest=" + new String(drmRequest)).openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(MediaDrmBridge.$assertionsDisabled);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("User-Agent", "Widevine CDM v1.0");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    bis = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[WebTextInputFlags.AutocapitalizeSentences];
                    while (true) {
                        int read = bis.read(buffer);
                        if (read == -1) {
                            break;
                        }
                        bos.write(buffer, MediaDrmBridge.KEY_STATUS_USABLE, read);
                    }
                    bis.close();
                    byte[] toByteArray = bos.toByteArray();
                    if (urlConnection == null) {
                        return toByteArray;
                    }
                    urlConnection.disconnect();
                    return toByteArray;
                }
                Log.m25d(TAG, "Server returned HTTP error code %d", Integer.valueOf(responseCode));
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                return null;
            } catch (MalformedURLException e) {
                try {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                return null;
            } catch (IllegalStateException e3) {
                e3.printStackTrace();
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                return null;
            } catch (Throwable th) {
                bis.close();
            }
        }

        protected void onPostExecute(Void v) {
            MediaDrmBridge.this.onProvisionResponse(this.mResponseBody);
        }
    }

    private native void nativeOnLegacySessionError(long j, byte[] bArr, String str);

    private native void nativeOnMediaCryptoReady(long j);

    private native void nativeOnPromiseRejected(long j, long j2, String str);

    private native void nativeOnPromiseResolved(long j, long j2);

    private native void nativeOnPromiseResolvedWithSession(long j, long j2, byte[] bArr);

    private native void nativeOnResetDeviceCredentialsCompleted(long j, boolean z);

    private native void nativeOnSessionClosed(long j, byte[] bArr);

    private native void nativeOnSessionKeysChange(long j, byte[] bArr, boolean z, int i);

    private native void nativeOnSessionMessage(long j, byte[] bArr, byte[] bArr2, String str);

    static {
        boolean z;
        if (MediaDrmBridge.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        HEX_CHAR_LOOKUP = "0123456789ABCDEF".toCharArray();
    }

    private static UUID getUUIDFromBytes(byte[] data) {
        if (data.length != 16) {
            return null;
        }
        int i;
        long mostSigBits = INVALID_NATIVE_MEDIA_DRM_BRIDGE;
        long leastSigBits = INVALID_NATIVE_MEDIA_DRM_BRIDGE;
        for (i = KEY_STATUS_USABLE; i < 8; i += KEY_STATUS_INTERNAL_ERROR) {
            mostSigBits = (mostSigBits << 8) | ((long) (data[i] & PageTransition.CORE_MASK));
        }
        for (i = 8; i < 16; i += KEY_STATUS_INTERNAL_ERROR) {
            leastSigBits = (leastSigBits << 8) | ((long) (data[i] & PageTransition.CORE_MASK));
        }
        return new UUID(mostSigBits, leastSigBits);
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int i = KEY_STATUS_USABLE; i < bytes.length; i += KEY_STATUS_INTERNAL_ERROR) {
            hexString.append(HEX_CHAR_LOOKUP[bytes[i] >>> 4]);
            hexString.append(HEX_CHAR_LOOKUP[bytes[i] & 15]);
        }
        return hexString.toString();
    }

    private boolean isNativeMediaDrmBridgeValid() {
        return this.mNativeMediaDrmBridge != INVALID_NATIVE_MEDIA_DRM_BRIDGE ? true : $assertionsDisabled;
    }

    private MediaDrmBridge(UUID schemeUUID, long nativeMediaDrmBridge) throws UnsupportedSchemeException {
        this.mSchemeUUID = schemeUUID;
        this.mMediaDrm = new MediaDrm(schemeUUID);
        this.mNativeMediaDrmBridge = nativeMediaDrmBridge;
        if ($assertionsDisabled || isNativeMediaDrmBridgeValid()) {
            this.mHandler = new Handler();
            this.mSessionIds = new HashMap();
            this.mPendingCreateSessionDataQueue = new ArrayDeque();
            this.mResetDeviceCredentialsPending = $assertionsDisabled;
            this.mProvisioningPending = $assertionsDisabled;
            this.mMediaDrm.setOnEventListener(new MediaDrmListener());
            this.mMediaDrm.setPropertyString(PRIVACY_MODE, ENABLE);
            this.mMediaDrm.setPropertyString(SESSION_SHARING, ENABLE);
            return;
        }
        throw new AssertionError();
    }

    private boolean createMediaCrypto() throws NotProvisionedException {
        if (this.mMediaDrm == null) {
            return $assertionsDisabled;
        }
        if (!$assertionsDisabled && this.mProvisioningPending) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && this.mMediaCryptoSession != null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || this.mMediaCrypto == null) {
            this.mMediaCryptoSession = openSession();
            if (this.mMediaCryptoSession == null) {
                Log.m32e(TAG, "Cannot create MediaCrypto Session.", new Object[KEY_STATUS_USABLE]);
                return $assertionsDisabled;
            }
            Log.m25d(TAG, "MediaCrypto Session created: %s", bytesToHexString(this.mMediaCryptoSession));
            try {
                if (MediaCrypto.isCryptoSchemeSupported(this.mSchemeUUID)) {
                    this.mMediaCrypto = new MediaCrypto(this.mSchemeUUID, this.mMediaCryptoSession);
                    Log.m24d(TAG, "MediaCrypto successfully created!");
                    onMediaCryptoReady();
                    return true;
                }
                Log.m32e(TAG, "Cannot create MediaCrypto for unsupported scheme.", new Object[KEY_STATUS_USABLE]);
                release();
                return $assertionsDisabled;
            } catch (MediaCryptoException e) {
                Object[] objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e;
                Log.m32e(TAG, "Cannot create MediaCrypto", objArr);
            }
        } else {
            throw new AssertionError();
        }
    }

    private byte[] openSession() throws NotProvisionedException {
        Object[] objArr;
        if ($assertionsDisabled || this.mMediaDrm != null) {
            try {
                return (byte[]) this.mMediaDrm.openSession().clone();
            } catch (RuntimeException e) {
                objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e;
                Log.m32e(TAG, "Cannot open a new session", objArr);
                release();
                return null;
            } catch (NotProvisionedException e2) {
                throw e2;
            } catch (MediaDrmException e3) {
                objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e3;
                Log.m32e(TAG, "Cannot open a new session", objArr);
                release();
                return null;
            }
        }
        throw new AssertionError();
    }

    @CalledByNative
    private static boolean isCryptoSchemeSupported(byte[] schemeUUID, String containerMimeType) {
        UUID cryptoScheme = getUUIDFromBytes(schemeUUID);
        if (containerMimeType.isEmpty()) {
            return MediaDrm.isCryptoSchemeSupported(cryptoScheme);
        }
        return MediaDrm.isCryptoSchemeSupported(cryptoScheme, containerMimeType);
    }

    @CalledByNative
    private static MediaDrmBridge create(byte[] schemeUUID, long nativeMediaDrmBridge) {
        UnsupportedSchemeException e;
        Object[] objArr;
        IllegalArgumentException e2;
        IllegalStateException e3;
        UUID cryptoScheme = getUUIDFromBytes(schemeUUID);
        if (cryptoScheme == null || !MediaDrm.isCryptoSchemeSupported(cryptoScheme)) {
            return null;
        }
        MediaDrmBridge mediaDrmBridge = null;
        try {
            MediaDrmBridge mediaDrmBridge2 = new MediaDrmBridge(cryptoScheme, nativeMediaDrmBridge);
            try {
                Log.m24d(TAG, "MediaDrmBridge successfully created.");
                return mediaDrmBridge2;
            } catch (UnsupportedSchemeException e4) {
                e = e4;
                mediaDrmBridge = mediaDrmBridge2;
                objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e;
                Log.m32e(TAG, "Unsupported DRM scheme", objArr);
                return mediaDrmBridge;
            } catch (IllegalArgumentException e5) {
                e2 = e5;
                mediaDrmBridge = mediaDrmBridge2;
                objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e2;
                Log.m32e(TAG, "Failed to create MediaDrmBridge", objArr);
                return mediaDrmBridge;
            } catch (IllegalStateException e6) {
                e3 = e6;
                mediaDrmBridge = mediaDrmBridge2;
                objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e3;
                Log.m32e(TAG, "Failed to create MediaDrmBridge", objArr);
                return mediaDrmBridge;
            }
        } catch (UnsupportedSchemeException e7) {
            e = e7;
            objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
            objArr[KEY_STATUS_USABLE] = e;
            Log.m32e(TAG, "Unsupported DRM scheme", objArr);
            return mediaDrmBridge;
        } catch (IllegalArgumentException e8) {
            e2 = e8;
            objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
            objArr[KEY_STATUS_USABLE] = e2;
            Log.m32e(TAG, "Failed to create MediaDrmBridge", objArr);
            return mediaDrmBridge;
        } catch (IllegalStateException e9) {
            e3 = e9;
            objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
            objArr[KEY_STATUS_USABLE] = e3;
            Log.m32e(TAG, "Failed to create MediaDrmBridge", objArr);
            return mediaDrmBridge;
        }
    }

    @CalledByNative
    private boolean setSecurityLevel(String securityLevel) {
        Object[] objArr;
        if (this.mMediaDrm == null || this.mMediaCrypto != null) {
            return $assertionsDisabled;
        }
        String currentSecurityLevel = this.mMediaDrm.getPropertyString(SECURITY_LEVEL);
        Object[] objArr2 = new Object[KEY_STATUS_EXPIRED];
        objArr2[KEY_STATUS_USABLE] = currentSecurityLevel;
        objArr2[KEY_STATUS_INTERNAL_ERROR] = securityLevel;
        Log.m32e(TAG, "Security level: current %s, new %s", objArr2);
        if (securityLevel.equals(currentSecurityLevel)) {
            return true;
        }
        try {
            this.mMediaDrm.setPropertyString(SECURITY_LEVEL, securityLevel);
            return true;
        } catch (IllegalArgumentException e) {
            objArr2 = new Object[KEY_STATUS_EXPIRED];
            objArr2[KEY_STATUS_USABLE] = securityLevel;
            objArr2[KEY_STATUS_INTERNAL_ERROR] = e;
            Log.m32e(TAG, "Failed to set security level %s", objArr2);
            objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
            objArr[KEY_STATUS_USABLE] = securityLevel;
            Log.m32e(TAG, "Security level %s not supported!", objArr);
            return $assertionsDisabled;
        } catch (IllegalStateException e2) {
            objArr2 = new Object[KEY_STATUS_EXPIRED];
            objArr2[KEY_STATUS_USABLE] = securityLevel;
            objArr2[KEY_STATUS_INTERNAL_ERROR] = e2;
            Log.m32e(TAG, "Failed to set security level %s", objArr2);
            objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
            objArr[KEY_STATUS_USABLE] = securityLevel;
            Log.m32e(TAG, "Security level %s not supported!", objArr);
            return $assertionsDisabled;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @org.chromium.base.CalledByNative
    private boolean setServerCertificate(byte[] r6) {
        /*
        r5 = this;
        r1 = 1;
        r2 = 0;
        r3 = r5.mMediaDrm;	 Catch:{ IllegalArgumentException -> 0x000a, IllegalStateException -> 0x0018 }
        r4 = "serviceCertificate";
        r3.setPropertyByteArray(r4, r6);	 Catch:{ IllegalArgumentException -> 0x000a, IllegalStateException -> 0x0018 }
    L_0x0009:
        return r1;
    L_0x000a:
        r0 = move-exception;
        r3 = "cr.media";
        r4 = "Failed to set server certificate";
        r1 = new java.lang.Object[r1];
        r1[r2] = r0;
        org.chromium.base.Log.m32e(r3, r4, r1);
    L_0x0016:
        r1 = r2;
        goto L_0x0009;
    L_0x0018:
        r0 = move-exception;
        r3 = "cr.media";
        r4 = "Failed to set server certificate";
        r1 = new java.lang.Object[r1];
        r1[r2] = r0;
        org.chromium.base.Log.m32e(r3, r4, r1);
        goto L_0x0016;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.media.MediaDrmBridge.setServerCertificate(byte[]):boolean");
    }

    @CalledByNative
    private MediaCrypto getMediaCrypto() {
        return this.mMediaCrypto;
    }

    @CalledByNative
    private void resetDeviceCredentials() {
        this.mResetDeviceCredentialsPending = true;
        ProvisionRequest request = this.mMediaDrm.getProvisionRequest();
        PostRequestTask postTask = new PostRequestTask(request.getData());
        Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;
        String[] strArr = new String[KEY_STATUS_INTERNAL_ERROR];
        strArr[KEY_STATUS_USABLE] = request.getDefaultUrl();
        postTask.executeOnExecutor(executor, strArr);
    }

    @CalledByNative
    private void destroy() {
        this.mNativeMediaDrmBridge = INVALID_NATIVE_MEDIA_DRM_BRIDGE;
        if (this.mMediaDrm != null) {
            release();
        }
    }

    private void release() {
        Iterator i$ = this.mPendingCreateSessionDataQueue.iterator();
        while (i$.hasNext()) {
            onPromiseRejected(((PendingCreateSessionData) i$.next()).promiseId(), "Create session aborted.");
        }
        this.mPendingCreateSessionDataQueue.clear();
        this.mPendingCreateSessionDataQueue = null;
        for (ByteBuffer sessionId : this.mSessionIds.keySet()) {
            try {
                this.mMediaDrm.removeKeys(sessionId.array());
            } catch (Exception e) {
                Object[] objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e;
                Log.m32e(TAG, "removeKeys failed: ", objArr);
            }
            this.mMediaDrm.closeSession(sessionId.array());
            onSessionClosed(sessionId.array());
        }
        this.mSessionIds.clear();
        this.mSessionIds = null;
        this.mMediaCryptoSession = null;
        if (this.mMediaCrypto != null) {
            this.mMediaCrypto.release();
            this.mMediaCrypto = null;
        }
        if (this.mMediaDrm != null) {
            this.mMediaDrm.release();
            this.mMediaDrm = null;
        }
    }

    private KeyRequest getKeyRequest(byte[] sessionId, byte[] data, String mime, HashMap<String, String> optionalParameters) throws NotProvisionedException {
        if (!$assertionsDisabled && this.mMediaDrm == null) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && this.mMediaCrypto == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || !this.mProvisioningPending) {
            if (optionalParameters == null) {
                optionalParameters = new HashMap();
            }
            KeyRequest request = null;
            try {
                request = this.mMediaDrm.getKeyRequest(sessionId, data, mime, KEY_STATUS_INTERNAL_ERROR, optionalParameters);
            } catch (IllegalStateException e) {
                if (VERSION.SDK_INT >= 21 && (e instanceof MediaDrmStateException)) {
                    Object[] objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                    objArr[KEY_STATUS_USABLE] = e;
                    Log.m32e(TAG, "MediaDrmStateException fired during getKeyRequest().", objArr);
                }
            }
            Log.m25d(TAG, "getKeyRequest %s!", request != null ? "successed" : "failed");
            return request;
        } else {
            throw new AssertionError();
        }
    }

    private void savePendingCreateSessionData(byte[] initData, String mime, HashMap<String, String> optionalParameters, long promiseId) {
        Log.m24d(TAG, "savePendingCreateSessionData()");
        this.mPendingCreateSessionDataQueue.offer(new PendingCreateSessionData(mime, optionalParameters, promiseId, null));
    }

    private void processPendingCreateSessionData() {
        Log.m24d(TAG, "processPendingCreateSessionData()");
        if ($assertionsDisabled || this.mMediaDrm != null) {
            while (this.mMediaDrm != null && !this.mProvisioningPending && !this.mPendingCreateSessionDataQueue.isEmpty()) {
                PendingCreateSessionData pendingData = (PendingCreateSessionData) this.mPendingCreateSessionDataQueue.poll();
                createSession(pendingData.initData(), pendingData.mimeType(), pendingData.optionalParameters(), pendingData.promiseId());
            }
            return;
        }
        throw new AssertionError();
    }

    private void resumePendingOperations() {
        this.mHandler.post(new C03811());
    }

    @CalledByNative
    private void createSessionFromNative(byte[] initData, String mime, String[] optionalParamsArray, long promiseId) {
        HashMap<String, String> optionalParameters = new HashMap();
        if (optionalParamsArray != null) {
            if (optionalParamsArray.length % KEY_STATUS_EXPIRED != 0) {
                throw new IllegalArgumentException("Additional data array doesn't have equal keys/values");
            }
            for (int i = KEY_STATUS_USABLE; i < optionalParamsArray.length; i += KEY_STATUS_EXPIRED) {
                optionalParameters.put(optionalParamsArray[i], optionalParamsArray[i + KEY_STATUS_INTERNAL_ERROR]);
            }
        }
        createSession(initData, mime, optionalParameters, promiseId);
    }

    private void createSession(byte[] initData, String mime, HashMap<String, String> optionalParameters, long promiseId) {
        Log.m24d(TAG, "createSession()");
        if (this.mMediaDrm == null) {
            Log.m32e(TAG, "createSession() called when MediaDrm is null.", new Object[KEY_STATUS_USABLE]);
        } else if (!this.mProvisioningPending) {
            try {
                if (this.mMediaCrypto == null && !createMediaCrypto()) {
                    onPromiseRejected(promiseId, "MediaCrypto creation failed.");
                } else if (!$assertionsDisabled && this.mMediaCryptoSession == null) {
                    throw new AssertionError();
                } else if ($assertionsDisabled || this.mMediaCrypto != null) {
                    byte[] sessionId = openSession();
                    if (sessionId == null) {
                        onPromiseRejected(promiseId, "Open session failed.");
                    } else if ($assertionsDisabled || !sessionExists(sessionId)) {
                        KeyRequest request = getKeyRequest(sessionId, initData, mime, optionalParameters);
                        if (request == null) {
                            this.mMediaDrm.closeSession(sessionId);
                            onPromiseRejected(promiseId, "Generate request failed.");
                            return;
                        }
                        Log.m25d(TAG, "createSession(): Session (%s) created.", bytesToHexString(sessionId));
                        onPromiseResolvedWithSession(promiseId, sessionId);
                        onSessionMessage(sessionId, request);
                        this.mSessionIds.put(ByteBuffer.wrap(sessionId), mime);
                    } else {
                        throw new AssertionError();
                    }
                } else {
                    throw new AssertionError();
                }
            } catch (NotProvisionedException e) {
                Object[] objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e;
                Log.m32e(TAG, "Device not provisioned", objArr);
                if ($assertionsDisabled) {
                    this.mMediaDrm.closeSession(null);
                }
                savePendingCreateSessionData(initData, mime, optionalParameters, promiseId);
                startProvisioning();
            }
        } else if ($assertionsDisabled || this.mMediaCrypto == null) {
            savePendingCreateSessionData(initData, mime, optionalParameters, promiseId);
        } else {
            throw new AssertionError();
        }
    }

    private boolean sessionExists(byte[] sessionId) {
        if (this.mMediaCryptoSession == null) {
            if ($assertionsDisabled || this.mSessionIds.isEmpty()) {
                Log.m32e(TAG, "Session doesn't exist because media crypto session is not created.", new Object[KEY_STATUS_USABLE]);
                return $assertionsDisabled;
            }
            throw new AssertionError();
        } else if (Arrays.equals(sessionId, this.mMediaCryptoSession) || !this.mSessionIds.containsKey(ByteBuffer.wrap(sessionId))) {
            return $assertionsDisabled;
        } else {
            return true;
        }
    }

    @CalledByNative
    private void closeSession(byte[] sessionId, long promiseId) {
        Log.m24d(TAG, "closeSession()");
        if (this.mMediaDrm == null) {
            onPromiseRejected(promiseId, "closeSession() called when MediaDrm is null.");
        } else if (sessionExists(sessionId)) {
            try {
                this.mMediaDrm.removeKeys(sessionId);
            } catch (Exception e) {
                Object[] objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e;
                Log.m32e(TAG, "removeKeys failed: ", objArr);
            }
            this.mMediaDrm.closeSession(sessionId);
            this.mSessionIds.remove(ByteBuffer.wrap(sessionId));
            onPromiseResolved(promiseId);
            onSessionClosed(sessionId);
            Log.m25d(TAG, "Session %s closed", bytesToHexString(sessionId));
        } else {
            onPromiseRejected(promiseId, "Invalid sessionId in closeSession(): " + bytesToHexString(sessionId));
        }
    }

    @CalledByNative
    private void updateSession(byte[] sessionId, byte[] response, long promiseId) {
        Object[] objArr;
        Log.m24d(TAG, "updateSession()");
        if (this.mMediaDrm == null) {
            onPromiseRejected(promiseId, "updateSession() called when MediaDrm is null.");
        } else if (sessionExists(sessionId)) {
            try {
                this.mMediaDrm.provideKeyResponse(sessionId, response);
            } catch (IllegalStateException e) {
                objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e;
                Log.m32e(TAG, "Exception intentionally caught when calling provideKeyResponse()", objArr);
            }
            try {
                Log.m25d(TAG, "Key successfully added for session %s", bytesToHexString(sessionId));
                onPromiseResolved(promiseId);
                onSessionKeysChange(sessionId, true, KEY_STATUS_USABLE);
            } catch (NotProvisionedException e2) {
                objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e2;
                Log.m32e(TAG, "failed to provide key response", objArr);
                onPromiseRejected(promiseId, "Update session failed.");
                release();
            } catch (DeniedByServerException e3) {
                objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
                objArr[KEY_STATUS_USABLE] = e3;
                Log.m32e(TAG, "failed to provide key response", objArr);
                onPromiseRejected(promiseId, "Update session failed.");
                release();
            }
        } else {
            onPromiseRejected(promiseId, "Invalid session in updateSession: " + bytesToHexString(sessionId));
        }
    }

    @CalledByNative
    private String getSecurityLevel() {
        if (this.mMediaDrm != null) {
            return this.mMediaDrm.getPropertyString(SECURITY_LEVEL);
        }
        Log.m32e(TAG, "getSecurityLevel() called when MediaDrm is null.", new Object[KEY_STATUS_USABLE]);
        return null;
    }

    private void startProvisioning() {
        Log.m24d(TAG, "startProvisioning");
        if (!$assertionsDisabled && this.mMediaDrm == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || !this.mProvisioningPending) {
            this.mProvisioningPending = true;
            ProvisionRequest request = this.mMediaDrm.getProvisionRequest();
            PostRequestTask postTask = new PostRequestTask(request.getData());
            Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;
            String[] strArr = new String[KEY_STATUS_INTERNAL_ERROR];
            strArr[KEY_STATUS_USABLE] = request.getDefaultUrl();
            postTask.executeOnExecutor(executor, strArr);
        } else {
            throw new AssertionError();
        }
    }

    private void onProvisionResponse(byte[] response) {
        Log.m24d(TAG, "onProvisionResponse()");
        if ($assertionsDisabled || this.mProvisioningPending) {
            this.mProvisioningPending = $assertionsDisabled;
            if (this.mMediaDrm != null) {
                boolean success = provideProvisionResponse(response);
                if (this.mResetDeviceCredentialsPending) {
                    onResetDeviceCredentialsCompleted(success);
                    this.mResetDeviceCredentialsPending = $assertionsDisabled;
                }
                if (success) {
                    resumePendingOperations();
                    return;
                }
                return;
            }
            return;
        }
        throw new AssertionError();
    }

    boolean provideProvisionResponse(byte[] response) {
        Object[] objArr;
        if (response == null || response.length == 0) {
            Log.m32e(TAG, "Invalid provision response.", new Object[KEY_STATUS_USABLE]);
            return $assertionsDisabled;
        }
        try {
            this.mMediaDrm.provideProvisionResponse(response);
            return true;
        } catch (DeniedByServerException e) {
            objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
            objArr[KEY_STATUS_USABLE] = e;
            Log.m32e(TAG, "failed to provide provision response", objArr);
            return $assertionsDisabled;
        } catch (IllegalStateException e2) {
            objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
            objArr[KEY_STATUS_USABLE] = e2;
            Log.m32e(TAG, "failed to provide provision response", objArr);
            return $assertionsDisabled;
        }
    }

    private void onMediaCryptoReady() {
        this.mHandler.post(new C03822());
    }

    private void onPromiseResolved(long promiseId) {
        this.mHandler.post(new C03833(promiseId));
    }

    private void onPromiseResolvedWithSession(long promiseId, byte[] sessionId) {
        this.mHandler.post(new C03844(promiseId, sessionId));
    }

    private void onPromiseRejected(long promiseId, String errorMessage) {
        Object[] objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
        objArr[KEY_STATUS_USABLE] = errorMessage;
        Log.m32e(TAG, "onPromiseRejected: %s", objArr);
        this.mHandler.post(new C03855(promiseId, errorMessage));
    }

    private void onSessionMessage(byte[] sessionId, KeyRequest request) {
        this.mHandler.post(new C03866(sessionId, request));
    }

    private void onSessionClosed(byte[] sessionId) {
        this.mHandler.post(new C03877(sessionId));
    }

    private void onSessionKeysChange(byte[] sessionId, boolean hasAdditionalUsableKey, int keyStatus) {
        this.mHandler.post(new C03888(sessionId, hasAdditionalUsableKey, keyStatus));
    }

    private void onLegacySessionError(byte[] sessionId, String errorMessage) {
        Object[] objArr = new Object[KEY_STATUS_INTERNAL_ERROR];
        objArr[KEY_STATUS_USABLE] = errorMessage;
        Log.m32e(TAG, "onLegacySessionError: %s", objArr);
        this.mHandler.post(new C03899(sessionId, errorMessage));
    }

    private void onResetDeviceCredentialsCompleted(boolean success) {
        this.mHandler.post(new AnonymousClass10(success));
    }
}
