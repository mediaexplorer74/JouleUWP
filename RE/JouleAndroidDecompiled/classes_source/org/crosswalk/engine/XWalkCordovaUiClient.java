package org.crosswalk.engine;

import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import com.google.android.gms.common.ConnectionResult;
import org.apache.cordova.CordovaDialogsHelper;
import org.apache.cordova.CordovaDialogsHelper.Result;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.camera.CameraLauncher;
import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkUIClient.JavascriptMessageType;
import org.xwalk.core.XWalkUIClient.LoadStatus;
import org.xwalk.core.XWalkView;

public class XWalkCordovaUiClient extends XWalkUIClient {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int FILECHOOSER_RESULTCODE = 5173;
    private static final String TAG = "XWalkCordovaUiClient";
    protected final CordovaDialogsHelper dialogsHelper;
    protected final XWalkWebViewEngine parentEngine;

    /* renamed from: org.crosswalk.engine.XWalkCordovaUiClient.5 */
    static /* synthetic */ class C04275 {
        static final /* synthetic */ int[] $SwitchMap$org$xwalk$core$XWalkUIClient$JavascriptMessageType;

        static {
            $SwitchMap$org$xwalk$core$XWalkUIClient$JavascriptMessageType = new int[JavascriptMessageType.values().length];
            try {
                $SwitchMap$org$xwalk$core$XWalkUIClient$JavascriptMessageType[JavascriptMessageType.JAVASCRIPT_ALERT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$xwalk$core$XWalkUIClient$JavascriptMessageType[JavascriptMessageType.JAVASCRIPT_CONFIRM.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$xwalk$core$XWalkUIClient$JavascriptMessageType[JavascriptMessageType.JAVASCRIPT_PROMPT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$xwalk$core$XWalkUIClient$JavascriptMessageType[JavascriptMessageType.JAVASCRIPT_BEFOREUNLOAD.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* renamed from: org.crosswalk.engine.XWalkCordovaUiClient.1 */
    class C06431 implements Result {
        final /* synthetic */ XWalkJavascriptResult val$result;

        C06431(XWalkJavascriptResult xWalkJavascriptResult) {
            this.val$result = xWalkJavascriptResult;
        }

        public void gotResult(boolean success, String value) {
            if (success) {
                this.val$result.confirm();
            } else {
                this.val$result.cancel();
            }
        }
    }

    /* renamed from: org.crosswalk.engine.XWalkCordovaUiClient.2 */
    class C06442 implements Result {
        final /* synthetic */ XWalkJavascriptResult val$result;

        C06442(XWalkJavascriptResult xWalkJavascriptResult) {
            this.val$result = xWalkJavascriptResult;
        }

        public void gotResult(boolean success, String value) {
            if (success) {
                this.val$result.confirm();
            } else {
                this.val$result.cancel();
            }
        }
    }

    /* renamed from: org.crosswalk.engine.XWalkCordovaUiClient.3 */
    class C06453 implements Result {
        final /* synthetic */ XWalkJavascriptResult val$result;

        C06453(XWalkJavascriptResult xWalkJavascriptResult) {
            this.val$result = xWalkJavascriptResult;
        }

        public void gotResult(boolean success, String value) {
            if (success) {
                this.val$result.confirmWithResult(value);
            } else {
                this.val$result.cancel();
            }
        }
    }

    /* renamed from: org.crosswalk.engine.XWalkCordovaUiClient.4 */
    class C06464 extends CordovaPlugin {
        C06464() {
        }

        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            XWalkCordovaUiClient.this.parentEngine.webView.onActivityResult(requestCode, resultCode, intent);
        }
    }

    static {
        $assertionsDisabled = !XWalkCordovaUiClient.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public XWalkCordovaUiClient(XWalkWebViewEngine parentEngine) {
        super(parentEngine.webView);
        this.parentEngine = parentEngine;
        this.dialogsHelper = new CordovaDialogsHelper(parentEngine.webView.getContext());
    }

    public boolean onJavascriptModalDialog(XWalkView view, JavascriptMessageType type, String url, String message, String defaultValue, XWalkJavascriptResult result) {
        switch (C04275.$SwitchMap$org$xwalk$core$XWalkUIClient$JavascriptMessageType[type.ordinal()]) {
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
                    return $assertionsDisabled;
                }
                throw new AssertionError();
        }
    }

    public boolean onJsAlert(XWalkView view, String url, String message, XWalkJavascriptResult result) {
        this.dialogsHelper.showAlert(message, new C06431(result));
        return true;
    }

    public boolean onJsConfirm(XWalkView view, String url, String message, XWalkJavascriptResult result) {
        this.dialogsHelper.showConfirm(message, new C06442(result));
        return true;
    }

    public boolean onJsPrompt(XWalkView view, String origin, String message, String defaultValue, XWalkJavascriptResult result) {
        String handledRet = this.parentEngine.bridge.promptOnJsPrompt(origin, message, defaultValue);
        if (handledRet != null) {
            result.confirmWithResult(handledRet);
        } else {
            this.dialogsHelper.showPrompt(message, defaultValue, new C06453(result));
        }
        return true;
    }

    public void onPageLoadStarted(XWalkView view, String url) {
        if (view.getUrl() != null && view.getUrl().equals(url)) {
            this.parentEngine.client.onPageStarted(url);
            this.parentEngine.bridge.reset();
        }
    }

    public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
        LOG.m9d(TAG, "onPageFinished(" + url + ")");
        if (status == LoadStatus.FINISHED) {
            this.parentEngine.client.onPageFinishedLoading(url);
        } else if (status != LoadStatus.FAILED) {
        }
    }

    public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        uploadFile.onReceiveValue(null);
        this.parentEngine.cordova.setActivityResultCallback(new C06464());
    }
}
