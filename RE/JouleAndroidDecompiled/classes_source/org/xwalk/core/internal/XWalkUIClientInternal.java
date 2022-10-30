package org.xwalk.core.internal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.EditText;
import com.google.android.gms.common.ConnectionResult;
import org.apache.cordova.camera.CameraLauncher;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.blink_public.web.WebInputEventModifier;

@XWalkAPI(createExternally = true)
public class XWalkUIClientInternal {
    static final /* synthetic */ boolean $assertionsDisabled;
    private Context mContext;
    private View mDecorView;
    private AlertDialog mDialog;
    private boolean mIsFullscreen;
    private boolean mOriginalForceNotFullscreen;
    private boolean mOriginalFullscreen;
    private EditText mPromptText;
    private int mSystemUiFlag;
    private XWalkViewInternal mXWalkView;

    /* renamed from: org.xwalk.core.internal.XWalkUIClientInternal.1 */
    class C04751 implements OnCancelListener {
        final /* synthetic */ XWalkJavascriptResultInternal val$fResult;

        C04751(XWalkJavascriptResultInternal xWalkJavascriptResultInternal) {
            this.val$fResult = xWalkJavascriptResultInternal;
        }

        public void onCancel(DialogInterface dialog) {
            this.val$fResult.cancel();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkUIClientInternal.2 */
    class C04762 implements OnClickListener {
        final /* synthetic */ XWalkJavascriptResultInternal val$fResult;

        C04762(XWalkJavascriptResultInternal xWalkJavascriptResultInternal) {
            this.val$fResult = xWalkJavascriptResultInternal;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$fResult.confirm();
            dialog.dismiss();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkUIClientInternal.3 */
    class C04773 implements OnCancelListener {
        final /* synthetic */ XWalkJavascriptResultInternal val$fResult;

        C04773(XWalkJavascriptResultInternal xWalkJavascriptResultInternal) {
            this.val$fResult = xWalkJavascriptResultInternal;
        }

        public void onCancel(DialogInterface dialog) {
            this.val$fResult.cancel();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkUIClientInternal.4 */
    class C04784 implements OnClickListener {
        C04784() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkUIClientInternal.5 */
    class C04795 implements OnClickListener {
        final /* synthetic */ XWalkJavascriptResultInternal val$fResult;

        C04795(XWalkJavascriptResultInternal xWalkJavascriptResultInternal) {
            this.val$fResult = xWalkJavascriptResultInternal;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$fResult.confirm();
            dialog.dismiss();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkUIClientInternal.6 */
    class C04806 implements OnCancelListener {
        final /* synthetic */ XWalkJavascriptResultInternal val$fResult;

        C04806(XWalkJavascriptResultInternal xWalkJavascriptResultInternal) {
            this.val$fResult = xWalkJavascriptResultInternal;
        }

        public void onCancel(DialogInterface dialog) {
            this.val$fResult.cancel();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkUIClientInternal.7 */
    class C04817 implements OnClickListener {
        C04817() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkUIClientInternal.8 */
    class C04828 implements OnClickListener {
        final /* synthetic */ XWalkJavascriptResultInternal val$fResult;

        C04828(XWalkJavascriptResultInternal xWalkJavascriptResultInternal) {
            this.val$fResult = xWalkJavascriptResultInternal;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$fResult.confirmWithResult(XWalkUIClientInternal.this.mPromptText.getText().toString());
            dialog.dismiss();
        }
    }

    /* renamed from: org.xwalk.core.internal.XWalkUIClientInternal.9 */
    static /* synthetic */ class C04839 {
        static final /* synthetic */ int[] f8xe7259465;

        static {
            f8xe7259465 = new int[JavascriptMessageTypeInternal.values().length];
            try {
                f8xe7259465[JavascriptMessageTypeInternal.JAVASCRIPT_ALERT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f8xe7259465[JavascriptMessageTypeInternal.JAVASCRIPT_CONFIRM.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f8xe7259465[JavascriptMessageTypeInternal.JAVASCRIPT_PROMPT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f8xe7259465[JavascriptMessageTypeInternal.JAVASCRIPT_BEFOREUNLOAD.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    @XWalkAPI
    public enum ConsoleMessageType {
        DEBUG,
        ERROR,
        LOG,
        INFO,
        WARNING
    }

    @XWalkAPI
    public enum InitiateByInternal {
        BY_USER_GESTURE,
        BY_JAVASCRIPT
    }

    @XWalkAPI
    public enum JavascriptMessageTypeInternal {
        JAVASCRIPT_ALERT,
        JAVASCRIPT_CONFIRM,
        JAVASCRIPT_PROMPT,
        JAVASCRIPT_BEFOREUNLOAD
    }

    @XWalkAPI
    public enum LoadStatusInternal {
        FINISHED,
        FAILED,
        CANCELLED
    }

    static {
        $assertionsDisabled = !XWalkUIClientInternal.class.desiredAssertionStatus();
    }

    @XWalkAPI
    public XWalkUIClientInternal(XWalkViewInternal view) {
        this.mIsFullscreen = false;
        this.mContext = view.getContext();
        this.mDecorView = view.getActivity().getWindow().getDecorView();
        if (VERSION.SDK_INT >= 19) {
            this.mSystemUiFlag = 1792;
        }
        this.mXWalkView = view;
    }

    @XWalkAPI
    public boolean onCreateWindowRequested(XWalkViewInternal view, InitiateByInternal initiator, ValueCallback<XWalkViewInternal> valueCallback) {
        return false;
    }

    public void onDidChangeThemeColor(XWalkViewInternal view, int color) {
        if (view != null && view.getActivity() != null) {
            ApiCompatibilityUtils.setStatusBarColor(view.getActivity().getWindow(), color);
            ApiCompatibilityUtils.setTaskDescription(view.getActivity(), null, null, color);
        }
    }

    @XWalkAPI
    public void onIconAvailable(XWalkViewInternal view, String url, Message startDownload) {
    }

    @XWalkAPI
    public void onReceivedIcon(XWalkViewInternal view, String url, Bitmap icon) {
    }

    @XWalkAPI
    public void onRequestFocus(XWalkViewInternal view) {
    }

    @XWalkAPI
    public void onJavascriptCloseWindow(XWalkViewInternal view) {
        if (view != null && view.getActivity() != null) {
            view.getActivity().finish();
        }
    }

    @XWalkAPI
    public boolean onJavascriptModalDialog(XWalkViewInternal view, JavascriptMessageTypeInternal type, String url, String message, String defaultValue, XWalkJavascriptResultInternal result) {
        switch (C04839.f8xe7259465[type.ordinal()]) {
            case CameraLauncher.SAVE_TO_ALBUM_SEC /*1*/:
                return onJsAlert(view, url, message, result);
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                return onJsConfirm(view, url, message, result);
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return onJsPrompt(view, url, message, defaultValue, result);
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                return onJsConfirm(view, url, message, result);
            default:
                if ($assertionsDisabled) {
                    return false;
                }
                throw new AssertionError();
        }
    }

    @XWalkAPI
    public void onFullscreenToggled(XWalkViewInternal view, boolean enterFullscreen) {
        Activity activity = view.getActivity();
        if (enterFullscreen) {
            if ((activity.getWindow().getAttributes().flags & WebInputEventModifier.IsLeft) != 0) {
                this.mOriginalForceNotFullscreen = true;
                activity.getWindow().clearFlags(WebInputEventModifier.IsLeft);
            } else {
                this.mOriginalForceNotFullscreen = false;
            }
            if (!this.mIsFullscreen) {
                if (VERSION.SDK_INT >= 19) {
                    this.mSystemUiFlag = this.mDecorView.getSystemUiVisibility();
                    this.mDecorView.setSystemUiVisibility(5894);
                } else if ((activity.getWindow().getAttributes().flags & WebInputEventModifier.NumLockOn) != 0) {
                    this.mOriginalFullscreen = true;
                } else {
                    this.mOriginalFullscreen = false;
                    activity.getWindow().addFlags(WebInputEventModifier.NumLockOn);
                }
                this.mIsFullscreen = true;
                return;
            }
            return;
        }
        if (this.mOriginalForceNotFullscreen) {
            activity.getWindow().addFlags(WebInputEventModifier.IsLeft);
        }
        if (VERSION.SDK_INT >= 19) {
            this.mDecorView.setSystemUiVisibility(this.mSystemUiFlag);
        } else if (!this.mOriginalFullscreen) {
            activity.getWindow().clearFlags(WebInputEventModifier.NumLockOn);
        }
        this.mIsFullscreen = false;
    }

    @XWalkAPI
    public void openFileChooser(XWalkViewInternal view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        uploadFile.onReceiveValue(null);
    }

    @XWalkAPI
    public void onScaleChanged(XWalkViewInternal view, float oldScale, float newScale) {
    }

    @XWalkAPI
    public boolean shouldOverrideKeyEvent(XWalkViewInternal view, KeyEvent event) {
        return false;
    }

    @XWalkAPI
    public void onUnhandledKeyEvent(XWalkViewInternal view, KeyEvent event) {
    }

    @XWalkAPI
    public boolean onConsoleMessage(XWalkViewInternal view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
        return false;
    }

    @XWalkAPI
    public void onReceivedTitle(XWalkViewInternal view, String title) {
    }

    @XWalkAPI
    public void onPageLoadStarted(XWalkViewInternal view, String url) {
    }

    @XWalkAPI
    public void onPageLoadStopped(XWalkViewInternal view, String url, LoadStatusInternal status) {
    }

    private boolean onJsAlert(XWalkViewInternal view, String url, String message, XWalkJavascriptResultInternal result) {
        XWalkJavascriptResultInternal fResult = result;
        Builder dialogBuilder = new Builder(this.mContext);
        dialogBuilder.setTitle(this.mContext.getString(C0444R.string.js_alert_title)).setMessage(message).setCancelable(true).setPositiveButton(this.mContext.getString(17039370), new C04762(fResult)).setOnCancelListener(new C04751(fResult));
        this.mDialog = dialogBuilder.create();
        this.mDialog.show();
        return false;
    }

    private boolean onJsConfirm(XWalkViewInternal view, String url, String message, XWalkJavascriptResultInternal result) {
        XWalkJavascriptResultInternal fResult = result;
        Builder dialogBuilder = new Builder(this.mContext);
        dialogBuilder.setTitle(this.mContext.getString(C0444R.string.js_confirm_title)).setMessage(message).setCancelable(true).setPositiveButton(this.mContext.getString(17039370), new C04795(fResult)).setNegativeButton(this.mContext.getString(17039360), new C04784()).setOnCancelListener(new C04773(fResult));
        this.mDialog = dialogBuilder.create();
        this.mDialog.show();
        return false;
    }

    private boolean onJsPrompt(XWalkViewInternal view, String url, String message, String defaultValue, XWalkJavascriptResultInternal result) {
        XWalkJavascriptResultInternal fResult = result;
        Builder dialogBuilder = new Builder(this.mContext);
        dialogBuilder.setTitle(this.mContext.getString(C0444R.string.js_prompt_title)).setMessage(message).setPositiveButton(this.mContext.getString(17039370), new C04828(fResult)).setNegativeButton(this.mContext.getString(17039360), new C04817()).setOnCancelListener(new C04806(fResult));
        this.mPromptText = new EditText(this.mContext);
        this.mPromptText.setVisibility(0);
        this.mPromptText.setText(defaultValue);
        this.mPromptText.selectAll();
        dialogBuilder.setView(this.mPromptText);
        this.mDialog = dialogBuilder.create();
        this.mDialog.show();
        return false;
    }
}
