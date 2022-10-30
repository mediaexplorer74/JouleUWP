package org.apache.cordova;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.widget.EditText;

public class CordovaDialogsHelper {
    private final Context context;
    private AlertDialog lastHandledDialog;

    /* renamed from: org.apache.cordova.CordovaDialogsHelper.1 */
    class C02251 implements OnClickListener {
        final /* synthetic */ Result val$result;

        C02251(Result result) {
            this.val$result = result;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$result.gotResult(true, null);
        }
    }

    /* renamed from: org.apache.cordova.CordovaDialogsHelper.2 */
    class C02262 implements OnCancelListener {
        final /* synthetic */ Result val$result;

        C02262(Result result) {
            this.val$result = result;
        }

        public void onCancel(DialogInterface dialog) {
            this.val$result.gotResult(false, null);
        }
    }

    /* renamed from: org.apache.cordova.CordovaDialogsHelper.3 */
    class C02273 implements OnKeyListener {
        final /* synthetic */ Result val$result;

        C02273(Result result) {
            this.val$result = result;
        }

        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode != 4) {
                return true;
            }
            this.val$result.gotResult(true, null);
            return false;
        }
    }

    /* renamed from: org.apache.cordova.CordovaDialogsHelper.4 */
    class C02284 implements OnClickListener {
        final /* synthetic */ Result val$result;

        C02284(Result result) {
            this.val$result = result;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$result.gotResult(true, null);
        }
    }

    /* renamed from: org.apache.cordova.CordovaDialogsHelper.5 */
    class C02295 implements OnClickListener {
        final /* synthetic */ Result val$result;

        C02295(Result result) {
            this.val$result = result;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$result.gotResult(false, null);
        }
    }

    /* renamed from: org.apache.cordova.CordovaDialogsHelper.6 */
    class C02306 implements OnCancelListener {
        final /* synthetic */ Result val$result;

        C02306(Result result) {
            this.val$result = result;
        }

        public void onCancel(DialogInterface dialog) {
            this.val$result.gotResult(false, null);
        }
    }

    /* renamed from: org.apache.cordova.CordovaDialogsHelper.7 */
    class C02317 implements OnKeyListener {
        final /* synthetic */ Result val$result;

        C02317(Result result) {
            this.val$result = result;
        }

        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode != 4) {
                return true;
            }
            this.val$result.gotResult(false, null);
            return false;
        }
    }

    /* renamed from: org.apache.cordova.CordovaDialogsHelper.8 */
    class C02328 implements OnClickListener {
        final /* synthetic */ EditText val$input;
        final /* synthetic */ Result val$result;

        C02328(EditText editText, Result result) {
            this.val$input = editText;
            this.val$result = result;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$result.gotResult(true, this.val$input.getText().toString());
        }
    }

    /* renamed from: org.apache.cordova.CordovaDialogsHelper.9 */
    class C02339 implements OnClickListener {
        final /* synthetic */ Result val$result;

        C02339(Result result) {
            this.val$result = result;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$result.gotResult(false, null);
        }
    }

    public interface Result {
        void gotResult(boolean z, String str);
    }

    public CordovaDialogsHelper(Context context) {
        this.context = context;
    }

    public void showAlert(String message, Result result) {
        Builder dlg = new Builder(this.context);
        dlg.setMessage(message);
        dlg.setTitle("Alert");
        dlg.setCancelable(true);
        dlg.setPositiveButton(17039370, new C02251(result));
        dlg.setOnCancelListener(new C02262(result));
        dlg.setOnKeyListener(new C02273(result));
        this.lastHandledDialog = dlg.show();
    }

    public void showConfirm(String message, Result result) {
        Builder dlg = new Builder(this.context);
        dlg.setMessage(message);
        dlg.setTitle("Confirm");
        dlg.setCancelable(true);
        dlg.setPositiveButton(17039370, new C02284(result));
        dlg.setNegativeButton(17039360, new C02295(result));
        dlg.setOnCancelListener(new C02306(result));
        dlg.setOnKeyListener(new C02317(result));
        this.lastHandledDialog = dlg.show();
    }

    public void showPrompt(String message, String defaultValue, Result result) {
        Builder dlg = new Builder(this.context);
        dlg.setMessage(message);
        EditText input = new EditText(this.context);
        if (defaultValue != null) {
            input.setText(defaultValue);
        }
        dlg.setView(input);
        dlg.setCancelable(false);
        dlg.setPositiveButton(17039370, new C02328(input, result));
        dlg.setNegativeButton(17039360, new C02339(result));
        this.lastHandledDialog = dlg.show();
    }

    public void destroyLastDialog() {
        if (this.lastHandledDialog != null) {
            this.lastHandledDialog.cancel();
        }
    }
}
