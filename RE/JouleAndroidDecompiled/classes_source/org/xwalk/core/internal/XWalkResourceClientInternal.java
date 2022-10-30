package org.xwalk.core.internal;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.net.http.SslError;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

@XWalkAPI(createExternally = true)
public class XWalkResourceClientInternal {
    @XWalkAPI
    public static final int ERROR_AUTHENTICATION = -4;
    @XWalkAPI
    public static final int ERROR_BAD_URL = -12;
    @XWalkAPI
    public static final int ERROR_CONNECT = -6;
    @XWalkAPI
    public static final int ERROR_FAILED_SSL_HANDSHAKE = -11;
    @XWalkAPI
    public static final int ERROR_FILE = -13;
    @XWalkAPI
    public static final int ERROR_FILE_NOT_FOUND = -14;
    @XWalkAPI
    public static final int ERROR_HOST_LOOKUP = -2;
    @XWalkAPI
    public static final int ERROR_IO = -7;
    @XWalkAPI
    public static final int ERROR_OK = 0;
    @XWalkAPI
    public static final int ERROR_PROXY_AUTHENTICATION = -5;
    @XWalkAPI
    public static final int ERROR_REDIRECT_LOOP = -9;
    @XWalkAPI
    public static final int ERROR_TIMEOUT = -8;
    @XWalkAPI
    public static final int ERROR_TOO_MANY_REQUESTS = -15;
    @XWalkAPI
    public static final int ERROR_UNKNOWN = -1;
    @XWalkAPI
    public static final int ERROR_UNSUPPORTED_AUTH_SCHEME = -3;
    @XWalkAPI
    public static final int ERROR_UNSUPPORTED_SCHEME = -10;

    /* renamed from: org.xwalk.core.internal.XWalkResourceClientInternal.1 */
    class C04641 implements OnCancelListener {
        final /* synthetic */ ValueCallback val$valueCallback;

        C04641(ValueCallback valueCallback) {
            this.val$valueCallback = valueCallback;
        }

        public void onCancel(DialogInterface dialog) {
            this.val$valueCallback.onReceiveValue(Boolean.valueOf(false));
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkResourceClientInternal.2 */
    class C04652 implements OnClickListener {
        final /* synthetic */ ValueCallback val$valueCallback;

        C04652(ValueCallback valueCallback) {
            this.val$valueCallback = valueCallback;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$valueCallback.onReceiveValue(Boolean.valueOf(false));
            dialog.dismiss();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkResourceClientInternal.3 */
    class C04663 implements OnClickListener {
        final /* synthetic */ ValueCallback val$valueCallback;

        C04663(ValueCallback valueCallback) {
            this.val$valueCallback = valueCallback;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$valueCallback.onReceiveValue(Boolean.valueOf(true));
            dialog.dismiss();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkResourceClientInternal.4 */
    class C04674 implements OnCancelListener {
        final /* synthetic */ XWalkHttpAuthHandlerInternal val$haHandler;

        C04674(XWalkHttpAuthHandlerInternal xWalkHttpAuthHandlerInternal) {
            this.val$haHandler = xWalkHttpAuthHandlerInternal;
        }

        public void onCancel(DialogInterface dialog) {
            this.val$haHandler.cancel();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkResourceClientInternal.5 */
    class C04685 implements OnClickListener {
        final /* synthetic */ XWalkHttpAuthHandlerInternal val$haHandler;
        final /* synthetic */ EditText val$passwordEditText;
        final /* synthetic */ EditText val$userNameEditText;

        C04685(EditText editText, EditText editText2, XWalkHttpAuthHandlerInternal xWalkHttpAuthHandlerInternal) {
            this.val$userNameEditText = editText;
            this.val$passwordEditText = editText2;
            this.val$haHandler = xWalkHttpAuthHandlerInternal;
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            this.val$haHandler.proceed(this.val$userNameEditText.getText().toString(), this.val$passwordEditText.getText().toString());
            dialog.dismiss();
        }
    }

    @XWalkAPI
    public XWalkResourceClientInternal(XWalkViewInternal view) {
    }

    @XWalkAPI
    public void onDocumentLoadedInFrame(XWalkViewInternal view, long frameId) {
    }

    @XWalkAPI
    public void onLoadStarted(XWalkViewInternal view, String url) {
    }

    @XWalkAPI
    public void onLoadFinished(XWalkViewInternal view, String url) {
    }

    @XWalkAPI
    public void onProgressChanged(XWalkViewInternal view, int progressInPercent) {
    }

    @XWalkAPI
    public WebResourceResponse shouldInterceptLoadRequest(XWalkViewInternal view, String url) {
        return null;
    }

    @XWalkAPI
    public void onReceivedLoadError(XWalkViewInternal view, int errorCode, String description, String failingUrl) {
        Toast.makeText(view.getActivity(), description, ERROR_OK).show();
    }

    @XWalkAPI
    public boolean shouldOverrideUrlLoading(XWalkViewInternal view, String url) {
        return false;
    }

    @XWalkAPI
    public void onReceivedSslError(XWalkViewInternal view, ValueCallback<Boolean> callback, SslError error) {
        ValueCallback<Boolean> valueCallback = callback;
        Builder dialogBuilder = new Builder(view.getContext());
        dialogBuilder.setTitle(C0444R.string.ssl_alert_title).setPositiveButton(17039370, new C04663(valueCallback)).setNegativeButton(17039360, new C04652(valueCallback)).setOnCancelListener(new C04641(valueCallback));
        dialogBuilder.create().show();
    }

    @XWalkAPI
    public void onReceivedClientCertRequest(XWalkViewInternal view, ClientCertRequestInternal handler) {
        handler.cancel();
    }

    @XWalkAPI
    public void doUpdateVisitedHistory(XWalkViewInternal view, String url, boolean isReload) {
    }

    @XWalkAPI
    public void onReceivedHttpAuthRequest(XWalkViewInternal view, XWalkHttpAuthHandlerInternal handler, String host, String realm) {
        if (view != null) {
            XWalkHttpAuthHandlerInternal haHandler = handler;
            Context context = view.getContext();
            LinearLayout layout = new LinearLayout(context);
            EditText userNameEditText = new EditText(context);
            EditText passwordEditText = new EditText(context);
            layout.setOrientation(1);
            layout.setPaddingRelative(10, ERROR_OK, 10, 20);
            userNameEditText.setHint(C0444R.string.http_auth_user_name);
            passwordEditText.setHint(C0444R.string.http_auth_password);
            passwordEditText.setInputType(129);
            layout.addView(userNameEditText);
            layout.addView(passwordEditText);
            new Builder(view.getActivity()).setTitle(C0444R.string.http_auth_title).setView(layout).setCancelable(false).setPositiveButton(C0444R.string.http_auth_log_in, new C04685(userNameEditText, passwordEditText, haHandler)).setNegativeButton(17039360, null).setOnCancelListener(new C04674(haHandler)).create().show();
        }
    }
}
