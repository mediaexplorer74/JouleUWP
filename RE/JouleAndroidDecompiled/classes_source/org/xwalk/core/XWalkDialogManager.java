package org.xwalk.core;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.view.PointerIconCompat;
import android.util.Log;
import junit.framework.Assert;

class XWalkDialogManager {
    private static final String PACKAGE_RE = "[a-z]+\\.[a-z0-9]+\\.[a-z0-9]+.*";
    private static final String TAG = "XWalkLib";
    private Dialog mActiveDialog;
    private String mApplicationName;
    private Context mContext;

    /* renamed from: org.xwalk.core.XWalkDialogManager.1 */
    class C04341 implements OnClickListener {
        final /* synthetic */ Runnable val$command;

        C04341(Runnable runnable) {
            this.val$command = runnable;
        }

        public void onClick(DialogInterface dialog, int id) {
            this.val$command.run();
        }
    }

    /* renamed from: org.xwalk.core.XWalkDialogManager.2 */
    class C04352 implements OnClickListener {
        final /* synthetic */ Runnable val$command;

        C04352(Runnable runnable) {
            this.val$command = runnable;
        }

        public void onClick(DialogInterface dialog, int id) {
            this.val$command.run();
        }
    }

    public XWalkDialogManager(Context context) {
        this.mContext = context;
    }

    private void showDialog(Dialog dialog) {
        this.mActiveDialog = dialog;
        this.mActiveDialog.show();
    }

    public void dismissDialog() {
        this.mActiveDialog.dismiss();
        this.mActiveDialog = null;
    }

    public boolean isShowingDialog() {
        return this.mActiveDialog != null && this.mActiveDialog.isShowing();
    }

    public boolean isShowingProgressDialog() {
        return isShowingDialog() && (this.mActiveDialog instanceof ProgressDialog);
    }

    public void setProgress(int progress, int max) {
        ProgressDialog dialog = this.mActiveDialog;
        dialog.setIndeterminate(false);
        dialog.setMax(max);
        dialog.setProgress(progress);
    }

    public void showInitializationError(int status, Runnable cancelCommand, Runnable downloadCommand) {
        AlertDialog dialog = buildAlertDialog();
        String cancelText = this.mContext.getString(C0430R.string.xwalk_close);
        String downloadText = this.mContext.getString(C0430R.string.xwalk_get_crosswalk);
        if (status == 2) {
            dialog.setTitle(this.mContext.getString(C0430R.string.startup_not_found_title));
            dialog.setMessage(replaceApplicationName(this.mContext.getString(C0430R.string.startup_not_found_message)));
            setPositiveButton(dialog, downloadText, downloadCommand);
            setNegativeButton(dialog, cancelText, cancelCommand);
        } else if (status == 3) {
            dialog.setTitle(this.mContext.getString(C0430R.string.startup_older_version_title));
            dialog.setMessage(replaceApplicationName(this.mContext.getString(C0430R.string.startup_older_version_message)));
            setPositiveButton(dialog, downloadText, downloadCommand);
            setNegativeButton(dialog, cancelText, cancelCommand);
        } else if (status == 4) {
            dialog.setTitle(this.mContext.getString(C0430R.string.startup_newer_version_title));
            dialog.setMessage(replaceApplicationName(this.mContext.getString(C0430R.string.startup_newer_version_message)));
            setNegativeButton(dialog, cancelText, cancelCommand);
        } else if (status == 5) {
            dialog.setTitle(this.mContext.getString(C0430R.string.startup_incomplete_library_title));
            dialog.setMessage(replaceApplicationName(this.mContext.getString(C0430R.string.startup_incomplete_library_message)));
            setNegativeButton(dialog, cancelText, cancelCommand);
        } else if (status == 6) {
            dialog.setTitle(this.mContext.getString(C0430R.string.startup_architecture_mismatch_title));
            dialog.setMessage(replaceApplicationName(this.mContext.getString(C0430R.string.startup_architecture_mismatch_message)));
            setPositiveButton(dialog, downloadText, downloadCommand);
            setNegativeButton(dialog, cancelText, cancelCommand);
        } else if (status == 7) {
            dialog.setTitle(this.mContext.getString(C0430R.string.startup_signature_check_error_title));
            dialog.setMessage(replaceApplicationName(this.mContext.getString(C0430R.string.startup_signature_check_error_message)));
            setNegativeButton(dialog, cancelText, cancelCommand);
        } else {
            Assert.fail("Invalid status for alert dialog " + status);
        }
        showDialog(dialog);
    }

    public void showMarketOpenError(Runnable cancelCommand) {
        AlertDialog dialog = buildAlertDialog();
        dialog.setTitle(this.mContext.getString(C0430R.string.crosswalk_install_title));
        dialog.setMessage(this.mContext.getString(C0430R.string.market_open_failed_message));
        setNegativeButton(dialog, this.mContext.getString(C0430R.string.xwalk_close), cancelCommand);
        showDialog(dialog);
    }

    public void showDecompressProgress(Runnable cancelCommand) {
        ProgressDialog dialog = buildProgressDialog();
        dialog.setTitle(this.mContext.getString(C0430R.string.crosswalk_install_title));
        dialog.setMessage(this.mContext.getString(C0430R.string.decompression_progress_message));
        setNegativeButton(dialog, this.mContext.getString(C0430R.string.xwalk_cancel), cancelCommand);
        showDialog(dialog);
    }

    public void showDownloadProgress(Runnable cancelCommand) {
        ProgressDialog dialog = buildProgressDialog();
        dialog.setTitle(this.mContext.getString(C0430R.string.crosswalk_install_title));
        dialog.setMessage(this.mContext.getString(C0430R.string.download_progress_message));
        dialog.setProgressStyle(1);
        setNegativeButton(dialog, this.mContext.getString(C0430R.string.xwalk_cancel), cancelCommand);
        showDialog(dialog);
    }

    public void showDownloadError(int status, int error, Runnable cancelCommand, Runnable downloadCommand) {
        String message = this.mContext.getString(C0430R.string.download_failed_message);
        if (status == 16) {
            if (error == PointerIconCompat.STYLE_CROSSHAIR) {
                message = this.mContext.getString(C0430R.string.download_failed_device_not_found);
            } else if (error == PointerIconCompat.STYLE_CELL) {
                message = this.mContext.getString(C0430R.string.download_failed_insufficient_space);
            }
        } else if (status == 4) {
            message = this.mContext.getString(C0430R.string.download_failed_time_out);
        }
        AlertDialog dialog = buildAlertDialog();
        dialog.setTitle(this.mContext.getString(C0430R.string.crosswalk_install_title));
        dialog.setMessage(message);
        setPositiveButton(dialog, this.mContext.getString(C0430R.string.xwalk_retry), downloadCommand);
        setNegativeButton(dialog, this.mContext.getString(C0430R.string.xwalk_cancel), cancelCommand);
        showDialog(dialog);
    }

    private ProgressDialog buildProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this.mContext);
        dialog.setProgressStyle(0);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private AlertDialog buildAlertDialog() {
        AlertDialog dialog = new Builder(this.mContext).create();
        dialog.setIcon(17301543);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void setPositiveButton(AlertDialog dialog, String text, Runnable command) {
        dialog.setButton(-1, text, new C04341(command));
    }

    private void setNegativeButton(AlertDialog dialog, String text, Runnable command) {
        dialog.setButton(-2, text, new C04352(command));
    }

    private String replaceApplicationName(String text) {
        if (this.mApplicationName == null) {
            try {
                PackageManager packageManager = this.mContext.getPackageManager();
                this.mApplicationName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.mContext.getPackageName(), 0));
            } catch (NameNotFoundException e) {
            }
            if (this.mApplicationName == null || this.mApplicationName.matches(PACKAGE_RE)) {
                this.mApplicationName = "this application";
            }
            Log.d(TAG, "Crosswalk application name: " + this.mApplicationName);
        }
        text = text.replaceAll("APP_NAME", this.mApplicationName);
        if (text.startsWith("this")) {
            return text.replaceFirst("this", "This");
        }
        return text;
    }
}
