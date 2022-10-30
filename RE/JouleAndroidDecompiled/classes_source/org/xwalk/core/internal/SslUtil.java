package org.xwalk.core.internal;

import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.util.Log;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import org.chromium.net.NetError;
import org.chromium.net.X509Util;

class SslUtil {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String TAG = "SslUtil";

    static {
        $assertionsDisabled = !SslUtil.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    SslUtil() {
    }

    public static SslError sslErrorFromNetErrorCode(int error, SslCertificate cert, String url) {
        if ($assertionsDisabled || (error >= NetError.ERR_CERT_END && error <= NetError.ERR_CERT_COMMON_NAME_INVALID)) {
            switch (error) {
                case NetError.ERR_CERT_AUTHORITY_INVALID /*-202*/:
                    return new SslError(3, cert, url);
                case NetError.ERR_CERT_DATE_INVALID /*-201*/:
                    return new SslError(4, cert, url);
                case NetError.ERR_CERT_COMMON_NAME_INVALID /*-200*/:
                    return new SslError(2, cert, url);
                default:
                    return new SslError(5, cert, url);
            }
        }
        throw new AssertionError();
    }

    public static SslCertificate getCertificateFromDerBytes(byte[] derBytes) {
        if (derBytes == null) {
            return null;
        }
        try {
            return new SslCertificate(X509Util.createCertificateFromBytes(derBytes));
        } catch (CertificateException e) {
            Log.w(TAG, "Could not read certificate: " + e);
            return null;
        } catch (KeyStoreException e2) {
            Log.w(TAG, "Could not read certificate: " + e2);
            return null;
        } catch (NoSuchAlgorithmException e3) {
            Log.w(TAG, "Could not read certificate: " + e3);
            return null;
        }
    }
}
