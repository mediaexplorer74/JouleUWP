package org.chromium.net;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import java.io.IOException;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.ApplicationStatus;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.content.browser.ContentViewCore;

@JNINamespace("net::android")
public class HttpNegotiateAuthenticator {
    static final /* synthetic */ boolean $assertionsDisabled;
    private final String mAccountType;
    private AccountManagerFuture<Bundle> mFuture;
    private Bundle mSpnegoContext;

    /* renamed from: org.chromium.net.HttpNegotiateAuthenticator.1 */
    class C03991 implements AccountManagerCallback<Bundle> {
        final /* synthetic */ long val$nativeResultObject;

        C03991(long j) {
            this.val$nativeResultObject = j;
        }

        public void run(AccountManagerFuture<Bundle> future) {
            try {
                int status;
                Bundle result = (Bundle) future.getResult();
                HttpNegotiateAuthenticator.this.mSpnegoContext = result.getBundle(HttpNegotiateConstants.KEY_SPNEGO_CONTEXT);
                switch (result.getInt(HttpNegotiateConstants.KEY_SPNEGO_RESULT, 1)) {
                    case ContentViewCore.INVALID_RENDER_PROCESS_PID /*0*/:
                        status = 0;
                        break;
                    case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                        status = -9;
                        break;
                    case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                        status = -3;
                        break;
                    case ConnectionResult.SERVICE_DISABLED /*3*/:
                        status = NetError.ERR_UNEXPECTED_SECURITY_LIBRARY_STATUS;
                        break;
                    case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                        status = NetError.ERR_INVALID_RESPONSE;
                        break;
                    case ConnectionResult.INVALID_ACCOUNT /*5*/:
                        status = NetError.ERR_INVALID_AUTH_CREDENTIALS;
                        break;
                    case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                        status = NetError.ERR_UNSUPPORTED_AUTH_SCHEME;
                        break;
                    case ConnectionResult.NETWORK_ERROR /*7*/:
                        status = NetError.ERR_MISSING_AUTH_CREDENTIALS;
                        break;
                    case ConnectionResult.INTERNAL_ERROR /*8*/:
                        status = NetError.ERR_UNDOCUMENTED_SECURITY_LIBRARY_STATUS;
                        break;
                    case ConnectionResult.SERVICE_INVALID /*9*/:
                        status = NetError.ERR_MALFORMED_IDENTITY;
                        break;
                    default:
                        status = -9;
                        break;
                }
                HttpNegotiateAuthenticator.this.nativeSetResult(this.val$nativeResultObject, status, result.getString("authtoken"));
            } catch (OperationCanceledException e) {
                HttpNegotiateAuthenticator.this.nativeSetResult(this.val$nativeResultObject, -3, null);
            } catch (AuthenticatorException e2) {
                HttpNegotiateAuthenticator.this.nativeSetResult(this.val$nativeResultObject, -3, null);
            } catch (IOException e3) {
                HttpNegotiateAuthenticator.this.nativeSetResult(this.val$nativeResultObject, -3, null);
            }
        }
    }

    @VisibleForTesting
    native void nativeSetResult(long j, int i, String str);

    static {
        $assertionsDisabled = !HttpNegotiateAuthenticator.class.desiredAssertionStatus();
    }

    private HttpNegotiateAuthenticator(String accountType) {
        this.mSpnegoContext = null;
        if ($assertionsDisabled || !TextUtils.isEmpty(accountType)) {
            this.mAccountType = accountType;
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    @VisibleForTesting
    static HttpNegotiateAuthenticator create(String accountType) {
        return new HttpNegotiateAuthenticator(accountType);
    }

    @CalledByNative
    @VisibleForTesting
    void getNextAuthToken(long nativeResultObject, String principal, String authToken, boolean canDelegate) {
        if ($assertionsDisabled || principal != null) {
            String authTokenType = HttpNegotiateConstants.SPNEGO_TOKEN_TYPE_BASE + principal;
            Activity activity = ApplicationStatus.getLastTrackedFocusedActivity();
            if (activity == null) {
                nativeSetResult(nativeResultObject, -9, null);
                return;
            }
            AccountManager am = AccountManager.get(activity);
            String[] features = new String[]{HttpNegotiateConstants.SPNEGO_FEATURE};
            Bundle options = new Bundle();
            if (authToken != null) {
                options.putString(HttpNegotiateConstants.KEY_INCOMING_AUTH_TOKEN, authToken);
            }
            if (this.mSpnegoContext != null) {
                options.putBundle(HttpNegotiateConstants.KEY_SPNEGO_CONTEXT, this.mSpnegoContext);
            }
            options.putBoolean(HttpNegotiateConstants.KEY_CAN_DELEGATE, canDelegate);
            this.mFuture = am.getAuthTokenByFeatures(this.mAccountType, authTokenType, features, activity, null, options, new C03991(nativeResultObject), new Handler(ThreadUtils.getUiThreadLooper()));
            return;
        }
        throw new AssertionError();
    }
}
