package nl.xservices.plugins;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Html;
import android.util.Base64;
import android.widget.Toast;
import com.adobe.phonegap.push.PushConstants;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

public class SocialSharing extends CordovaPlugin {
    private static final String ACTION_AVAILABLE_EVENT = "available";
    private static final String ACTION_CAN_SHARE_VIA = "canShareVia";
    private static final String ACTION_CAN_SHARE_VIA_EMAIL = "canShareViaEmail";
    private static final String ACTION_SHARE_EVENT = "share";
    private static final String ACTION_SHARE_VIA = "shareVia";
    private static final String ACTION_SHARE_VIA_EMAIL_EVENT = "shareViaEmail";
    private static final String ACTION_SHARE_VIA_FACEBOOK_EVENT = "shareViaFacebook";
    private static final String ACTION_SHARE_VIA_FACEBOOK_WITH_PASTEMESSAGEHINT = "shareViaFacebookWithPasteMessageHint";
    private static final String ACTION_SHARE_VIA_INSTAGRAM_EVENT = "shareViaInstagram";
    private static final String ACTION_SHARE_VIA_SMS_EVENT = "shareViaSMS";
    private static final String ACTION_SHARE_VIA_TWITTER_EVENT = "shareViaTwitter";
    private static final String ACTION_SHARE_VIA_WHATSAPP_EVENT = "shareViaWhatsApp";
    private static final int ACTIVITY_CODE_SEND = 1;
    private static final int ACTIVITY_CODE_SENDVIAEMAIL = 2;
    private static final int ACTIVITY_CODE_SENDVIAWHATSAPP = 3;
    private CallbackContext _callbackContext;
    private String pasteMessage;

    private abstract class SocialSharingRunnable implements Runnable {
        public CallbackContext callbackContext;

        SocialSharingRunnable(CallbackContext cb) {
            this.callbackContext = cb;
        }
    }

    /* renamed from: nl.xservices.plugins.SocialSharing.1 */
    class C05741 extends SocialSharingRunnable {
        final /* synthetic */ JSONArray val$bcc;
        final /* synthetic */ JSONArray val$cc;
        final /* synthetic */ JSONArray val$files;
        final /* synthetic */ String val$message;
        final /* synthetic */ SocialSharing val$plugin;
        final /* synthetic */ String val$subject;
        final /* synthetic */ JSONArray val$to;

        C05741(CallbackContext cb, String str, String str2, JSONArray jSONArray, JSONArray jSONArray2, JSONArray jSONArray3, JSONArray jSONArray4, SocialSharing socialSharing) {
            this.val$message = str;
            this.val$subject = str2;
            this.val$to = jSONArray;
            this.val$cc = jSONArray2;
            this.val$bcc = jSONArray3;
            this.val$files = jSONArray4;
            this.val$plugin = socialSharing;
            super(cb);
        }

        public void run() {
            Intent draft = new Intent("android.intent.action.SEND_MULTIPLE");
            if (SocialSharing.notEmpty(this.val$message)) {
                if (Pattern.compile(".*\\<[^>]+>.*", 32).matcher(this.val$message).matches()) {
                    draft.putExtra("android.intent.extra.TEXT", Html.fromHtml(this.val$message));
                    draft.setType("text/html");
                } else {
                    draft.putExtra("android.intent.extra.TEXT", this.val$message);
                    draft.setType("text/plain");
                }
            }
            if (SocialSharing.notEmpty(this.val$subject)) {
                draft.putExtra("android.intent.extra.SUBJECT", this.val$subject);
            }
            try {
                if (this.val$to != null && this.val$to.length() > 0) {
                    draft.putExtra("android.intent.extra.EMAIL", SocialSharing.toStringArray(this.val$to));
                }
                if (this.val$cc != null && this.val$cc.length() > 0) {
                    draft.putExtra("android.intent.extra.CC", SocialSharing.toStringArray(this.val$cc));
                }
                if (this.val$bcc != null && this.val$bcc.length() > 0) {
                    draft.putExtra("android.intent.extra.BCC", SocialSharing.toStringArray(this.val$bcc));
                }
                if (this.val$files.length() > 0) {
                    String dir = SocialSharing.this.getDownloadDir();
                    if (dir != null) {
                        ArrayList<Uri> fileUris = new ArrayList();
                        for (int i = 0; i < this.val$files.length(); i += SocialSharing.ACTIVITY_CODE_SEND) {
                            Uri fileUri = SocialSharing.this.getFileUriAndSetType(draft, dir, this.val$files.getString(i), this.val$subject, i);
                            if (fileUri != null) {
                                fileUris.add(fileUri);
                            }
                        }
                        if (!fileUris.isEmpty()) {
                            draft.putExtra("android.intent.extra.STREAM", fileUris);
                        }
                    }
                }
            } catch (Exception e) {
                this.callbackContext.error(e.getMessage());
            }
            draft.setType("application/octet-stream");
            SocialSharing.this.cordova.startActivityForResult(this.val$plugin, Intent.createChooser(draft, "Choose Email App"), SocialSharing.ACTIVITY_CODE_SENDVIAEMAIL);
        }
    }

    /* renamed from: nl.xservices.plugins.SocialSharing.2 */
    class C05752 extends SocialSharingRunnable {
        final /* synthetic */ String val$appPackageName;
        final /* synthetic */ JSONArray val$files;
        final /* synthetic */ String val$msg;
        final /* synthetic */ CordovaInterface val$mycordova;
        final /* synthetic */ boolean val$peek;
        final /* synthetic */ CordovaPlugin val$plugin;
        final /* synthetic */ String val$subject;
        final /* synthetic */ String val$url;

        /* renamed from: nl.xservices.plugins.SocialSharing.2.1 */
        class C02201 extends TimerTask {

            /* renamed from: nl.xservices.plugins.SocialSharing.2.1.1 */
            class C02191 implements Runnable {
                C02191() {
                }

                public void run() {
                    SocialSharing.this.copyHintToClipboard(C05752.this.val$msg, SocialSharing.this.pasteMessage);
                    SocialSharing.this.showPasteMessage(SocialSharing.this.pasteMessage);
                }
            }

            C02201() {
            }

            public void run() {
                SocialSharing.this.cordova.getActivity().runOnUiThread(new C02191());
            }
        }

        C05752(CallbackContext cb, String str, JSONArray jSONArray, String str2, String str3, String str4, boolean z, CordovaInterface cordovaInterface, CordovaPlugin cordovaPlugin) {
            this.val$msg = str;
            this.val$files = jSONArray;
            this.val$subject = str2;
            this.val$url = str3;
            this.val$appPackageName = str4;
            this.val$peek = z;
            this.val$mycordova = cordovaInterface;
            this.val$plugin = cordovaPlugin;
            super(cb);
        }

        public void run() {
            String message = this.val$msg;
            boolean hasMultipleAttachments = this.val$files.length() > SocialSharing.ACTIVITY_CODE_SEND;
            Intent sendIntent = new Intent(hasMultipleAttachments ? "android.intent.action.SEND_MULTIPLE" : "android.intent.action.SEND");
            sendIntent.addFlags(AccessibilityNodeInfoCompat.ACTION_COLLAPSE);
            try {
                String packageName;
                String passedActivityName;
                String[] items;
                ActivityInfo activity;
                String str;
                if (this.val$files.length() <= 0 || CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE.equals(this.val$files.getString(0))) {
                    sendIntent.setType("text/plain");
                    if (SocialSharing.notEmpty(this.val$subject)) {
                        sendIntent.putExtra("android.intent.extra.SUBJECT", this.val$subject);
                    }
                    if (SocialSharing.notEmpty(this.val$url)) {
                        if (SocialSharing.notEmpty(message)) {
                            message = message + " " + this.val$url;
                        } else {
                            message = this.val$url;
                        }
                    }
                    if (SocialSharing.notEmpty(message)) {
                        sendIntent.putExtra("android.intent.extra.TEXT", message);
                        if (VERSION.SDK_INT < 21) {
                            sendIntent.putExtra("sms_body", message);
                        }
                    }
                    if (this.val$appPackageName == null) {
                        packageName = this.val$appPackageName;
                        passedActivityName = null;
                        if (packageName.contains("/")) {
                            items = this.val$appPackageName.split("/");
                            packageName = items[0];
                            passedActivityName = items[SocialSharing.ACTIVITY_CODE_SEND];
                        }
                        activity = SocialSharing.this.getActivity(this.callbackContext, sendIntent, packageName);
                        if (activity == null) {
                            return;
                        }
                        if (this.val$peek) {
                            this.callbackContext.sendPluginResult(new PluginResult(Status.OK));
                            return;
                        }
                        sendIntent.addCategory("android.intent.category.LAUNCHER");
                        str = activity.applicationInfo.packageName;
                        if (passedActivityName == null) {
                            passedActivityName = activity.name;
                        }
                        sendIntent.setComponent(new ComponentName(str, passedActivityName));
                        this.val$mycordova.startActivityForResult(this.val$plugin, sendIntent, 0);
                        if (SocialSharing.this.pasteMessage == null) {
                            new Timer().schedule(new C02201(), 2000);
                        }
                    } else if (this.val$peek) {
                        this.callbackContext.sendPluginResult(new PluginResult(Status.OK));
                    } else {
                        this.val$mycordova.startActivityForResult(this.val$plugin, Intent.createChooser(sendIntent, null), SocialSharing.ACTIVITY_CODE_SEND);
                    }
                }
                String dir = SocialSharing.this.getDownloadDir();
                if (dir != null) {
                    ArrayList<Uri> fileUris = new ArrayList();
                    Uri fileUri = null;
                    for (int i = 0; i < this.val$files.length(); i += SocialSharing.ACTIVITY_CODE_SEND) {
                        fileUri = SocialSharing.this.getFileUriAndSetType(sendIntent, dir, this.val$files.getString(i), this.val$subject, i);
                        if (fileUri != null) {
                            fileUris.add(fileUri);
                        }
                    }
                    if (!fileUris.isEmpty()) {
                        if (hasMultipleAttachments) {
                            sendIntent.putExtra("android.intent.extra.STREAM", fileUris);
                        } else {
                            sendIntent.putExtra("android.intent.extra.STREAM", fileUri);
                        }
                    }
                    if (SocialSharing.notEmpty(this.val$subject)) {
                        sendIntent.putExtra("android.intent.extra.SUBJECT", this.val$subject);
                    }
                    if (SocialSharing.notEmpty(this.val$url)) {
                        if (SocialSharing.notEmpty(message)) {
                            message = this.val$url;
                        } else {
                            message = message + " " + this.val$url;
                        }
                    }
                    if (SocialSharing.notEmpty(message)) {
                        sendIntent.putExtra("android.intent.extra.TEXT", message);
                        if (VERSION.SDK_INT < 21) {
                            sendIntent.putExtra("sms_body", message);
                        }
                    }
                    if (this.val$appPackageName == null) {
                        packageName = this.val$appPackageName;
                        passedActivityName = null;
                        if (packageName.contains("/")) {
                            items = this.val$appPackageName.split("/");
                            packageName = items[0];
                            passedActivityName = items[SocialSharing.ACTIVITY_CODE_SEND];
                        }
                        activity = SocialSharing.this.getActivity(this.callbackContext, sendIntent, packageName);
                        if (activity == null) {
                            if (this.val$peek) {
                                sendIntent.addCategory("android.intent.category.LAUNCHER");
                                str = activity.applicationInfo.packageName;
                                if (passedActivityName == null) {
                                    passedActivityName = activity.name;
                                }
                                sendIntent.setComponent(new ComponentName(str, passedActivityName));
                                this.val$mycordova.startActivityForResult(this.val$plugin, sendIntent, 0);
                                if (SocialSharing.this.pasteMessage == null) {
                                    new Timer().schedule(new C02201(), 2000);
                                }
                            }
                            this.callbackContext.sendPluginResult(new PluginResult(Status.OK));
                            return;
                        }
                        return;
                    } else if (this.val$peek) {
                        this.val$mycordova.startActivityForResult(this.val$plugin, Intent.createChooser(sendIntent, null), SocialSharing.ACTIVITY_CODE_SEND);
                    } else {
                        this.callbackContext.sendPluginResult(new PluginResult(Status.OK));
                    }
                }
                sendIntent.setType("text/plain");
                if (SocialSharing.notEmpty(this.val$subject)) {
                    sendIntent.putExtra("android.intent.extra.SUBJECT", this.val$subject);
                }
                if (SocialSharing.notEmpty(this.val$url)) {
                    if (SocialSharing.notEmpty(message)) {
                        message = message + " " + this.val$url;
                    } else {
                        message = this.val$url;
                    }
                }
                if (SocialSharing.notEmpty(message)) {
                    sendIntent.putExtra("android.intent.extra.TEXT", message);
                    if (VERSION.SDK_INT < 21) {
                        sendIntent.putExtra("sms_body", message);
                    }
                }
                if (this.val$appPackageName == null) {
                    packageName = this.val$appPackageName;
                    passedActivityName = null;
                    if (packageName.contains("/")) {
                        items = this.val$appPackageName.split("/");
                        packageName = items[0];
                        passedActivityName = items[SocialSharing.ACTIVITY_CODE_SEND];
                    }
                    activity = SocialSharing.this.getActivity(this.callbackContext, sendIntent, packageName);
                    if (activity == null) {
                        return;
                    }
                    if (this.val$peek) {
                        this.callbackContext.sendPluginResult(new PluginResult(Status.OK));
                        return;
                    }
                    sendIntent.addCategory("android.intent.category.LAUNCHER");
                    str = activity.applicationInfo.packageName;
                    if (passedActivityName == null) {
                        passedActivityName = activity.name;
                    }
                    sendIntent.setComponent(new ComponentName(str, passedActivityName));
                    this.val$mycordova.startActivityForResult(this.val$plugin, sendIntent, 0);
                    if (SocialSharing.this.pasteMessage == null) {
                        new Timer().schedule(new C02201(), 2000);
                    }
                } else if (this.val$peek) {
                    this.callbackContext.sendPluginResult(new PluginResult(Status.OK));
                } else {
                    this.val$mycordova.startActivityForResult(this.val$plugin, Intent.createChooser(sendIntent, null), SocialSharing.ACTIVITY_CODE_SEND);
                }
            } catch (Exception e) {
                this.callbackContext.error(e.getMessage());
            }
        }
    }

    /* renamed from: nl.xservices.plugins.SocialSharing.3 */
    class C05763 extends SocialSharingRunnable {
        final /* synthetic */ JSONArray val$files;
        final /* synthetic */ String val$number;
        final /* synthetic */ SocialSharing val$plugin;
        final /* synthetic */ String val$shareMessage;
        final /* synthetic */ String val$subject;

        C05763(CallbackContext cb, String str, String str2, String str3, JSONArray jSONArray, SocialSharing socialSharing) {
            this.val$number = str;
            this.val$shareMessage = str2;
            this.val$subject = str3;
            this.val$files = jSONArray;
            this.val$plugin = socialSharing;
            super(cb);
        }

        public void run() {
            boolean hasMultipleAttachments = true;
            Intent intent = new Intent("android.intent.action.SENDTO");
            intent.setData(Uri.parse("smsto:" + this.val$number));
            intent.putExtra("sms_body", this.val$shareMessage);
            intent.putExtra("sms_subject", this.val$subject);
            intent.setPackage("com.whatsapp");
            try {
                if (this.val$files.length() > 0 && !CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE.equals(this.val$files.getString(0))) {
                    if (this.val$files.length() <= SocialSharing.ACTIVITY_CODE_SEND) {
                        hasMultipleAttachments = false;
                    }
                    String dir = SocialSharing.this.getDownloadDir();
                    if (dir != null) {
                        ArrayList<Uri> fileUris = new ArrayList();
                        Uri fileUri = null;
                        for (int i = 0; i < this.val$files.length(); i += SocialSharing.ACTIVITY_CODE_SEND) {
                            fileUri = SocialSharing.this.getFileUriAndSetType(intent, dir, this.val$files.getString(i), this.val$subject, i);
                            if (fileUri != null) {
                                fileUris.add(fileUri);
                            }
                        }
                        if (!fileUris.isEmpty()) {
                            if (hasMultipleAttachments) {
                                intent.putExtra("android.intent.extra.STREAM", fileUris);
                            } else {
                                intent.putExtra("android.intent.extra.STREAM", fileUri);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                this.callbackContext.error(e.getMessage());
            }
            try {
                SocialSharing.this.cordova.startActivityForResult(this.val$plugin, intent, SocialSharing.ACTIVITY_CODE_SENDVIAWHATSAPP);
            } catch (Exception e2) {
                this.callbackContext.error(e2.getMessage());
            }
        }
    }

    /* renamed from: nl.xservices.plugins.SocialSharing.4 */
    class C05774 extends SocialSharingRunnable {
        final /* synthetic */ String val$image;
        final /* synthetic */ String val$message;
        final /* synthetic */ String val$phonenumbers;
        final /* synthetic */ SocialSharing val$plugin;
        final /* synthetic */ String val$subject;

        C05774(CallbackContext cb, String str, String str2, String str3, String str4, SocialSharing socialSharing) {
            this.val$phonenumbers = str;
            this.val$message = str2;
            this.val$subject = str3;
            this.val$image = str4;
            this.val$plugin = socialSharing;
            super(cb);
        }

        public void run() {
            Intent intent;
            if (VERSION.SDK_INT >= 19) {
                intent = new Intent("android.intent.action.SENDTO");
                intent.setData(Uri.parse("smsto:" + (SocialSharing.notEmpty(this.val$phonenumbers) ? this.val$phonenumbers : CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE)));
            } else {
                intent = new Intent("android.intent.action.VIEW");
                intent.setType("vnd.android-dir/mms-sms");
                if (this.val$phonenumbers != null) {
                    intent.putExtra(MessagingSmsConsts.ADDRESS, this.val$phonenumbers);
                }
            }
            intent.putExtra("sms_body", this.val$message);
            intent.putExtra("sms_subject", this.val$subject);
            try {
                if (!(this.val$image == null || CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE.equals(this.val$image))) {
                    Uri fileUri = SocialSharing.this.getFileUriAndSetType(intent, SocialSharing.this.getDownloadDir(), this.val$image, this.val$subject, 0);
                    if (fileUri != null) {
                        intent.putExtra("android.intent.extra.STREAM", fileUri);
                    }
                }
                SocialSharing.this.cordova.startActivityForResult(this.val$plugin, intent, 0);
            } catch (Exception e) {
                this.callbackContext.error(e.getMessage());
            }
        }
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this._callbackContext = callbackContext;
        this.pasteMessage = null;
        if (ACTION_AVAILABLE_EVENT.equals(action)) {
            callbackContext.sendPluginResult(new PluginResult(Status.OK));
            return true;
        } else if (ACTION_SHARE_EVENT.equals(action)) {
            return doSendIntent(callbackContext, args.getString(0), args.getString(ACTIVITY_CODE_SEND), args.getJSONArray(ACTIVITY_CODE_SENDVIAEMAIL), args.getString(ACTIVITY_CODE_SENDVIAWHATSAPP), null, false);
        } else if (ACTION_SHARE_VIA_TWITTER_EVENT.equals(action)) {
            return doSendIntent(callbackContext, args.getString(0), args.getString(ACTIVITY_CODE_SEND), args.getJSONArray(ACTIVITY_CODE_SENDVIAEMAIL), args.getString(ACTIVITY_CODE_SENDVIAWHATSAPP), "twitter", false);
        } else if (ACTION_SHARE_VIA_FACEBOOK_EVENT.equals(action)) {
            return doSendIntent(callbackContext, args.getString(0), args.getString(ACTIVITY_CODE_SEND), args.getJSONArray(ACTIVITY_CODE_SENDVIAEMAIL), args.getString(ACTIVITY_CODE_SENDVIAWHATSAPP), "com.facebook.katana", false);
        } else if (ACTION_SHARE_VIA_FACEBOOK_WITH_PASTEMESSAGEHINT.equals(action)) {
            this.pasteMessage = args.getString(4);
            return doSendIntent(callbackContext, args.getString(0), args.getString(ACTIVITY_CODE_SEND), args.getJSONArray(ACTIVITY_CODE_SENDVIAEMAIL), args.getString(ACTIVITY_CODE_SENDVIAWHATSAPP), "com.facebook.katana", false);
        } else if (ACTION_SHARE_VIA_WHATSAPP_EVENT.equals(action)) {
            if (notEmpty(args.getString(4))) {
                return shareViaWhatsAppDirectly(callbackContext, args.getString(0), args.getString(ACTIVITY_CODE_SEND), args.getJSONArray(ACTIVITY_CODE_SENDVIAEMAIL), args.getString(ACTIVITY_CODE_SENDVIAWHATSAPP), args.getString(4));
            }
            return doSendIntent(callbackContext, args.getString(0), args.getString(ACTIVITY_CODE_SEND), args.getJSONArray(ACTIVITY_CODE_SENDVIAEMAIL), args.getString(ACTIVITY_CODE_SENDVIAWHATSAPP), "whatsapp", false);
        } else if (ACTION_SHARE_VIA_INSTAGRAM_EVENT.equals(action)) {
            if (notEmpty(args.getString(0))) {
                copyHintToClipboard(args.getString(0), "Instagram paste message");
            }
            return doSendIntent(callbackContext, args.getString(0), args.getString(ACTIVITY_CODE_SEND), args.getJSONArray(ACTIVITY_CODE_SENDVIAEMAIL), args.getString(ACTIVITY_CODE_SENDVIAWHATSAPP), "instagram", false);
        } else if (ACTION_CAN_SHARE_VIA.equals(action)) {
            return doSendIntent(callbackContext, args.getString(0), args.getString(ACTIVITY_CODE_SEND), args.getJSONArray(ACTIVITY_CODE_SENDVIAEMAIL), args.getString(ACTIVITY_CODE_SENDVIAWHATSAPP), args.getString(4), true);
        } else if (ACTION_CAN_SHARE_VIA_EMAIL.equals(action)) {
            if (isEmailAvailable()) {
                callbackContext.sendPluginResult(new PluginResult(Status.OK));
                return true;
            }
            callbackContext.sendPluginResult(new PluginResult(Status.ERROR, "not available"));
            return false;
        } else if (ACTION_SHARE_VIA.equals(action)) {
            return doSendIntent(callbackContext, args.getString(0), args.getString(ACTIVITY_CODE_SEND), args.getJSONArray(ACTIVITY_CODE_SENDVIAEMAIL), args.getString(ACTIVITY_CODE_SENDVIAWHATSAPP), args.getString(4), false);
        } else if (ACTION_SHARE_VIA_SMS_EVENT.equals(action)) {
            return invokeSMSIntent(callbackContext, args.getJSONObject(0), args.getString(ACTIVITY_CODE_SEND));
        } else {
            if (ACTION_SHARE_VIA_EMAIL_EVENT.equals(action)) {
                return invokeEmailIntent(callbackContext, args.getString(0), args.getString(ACTIVITY_CODE_SEND), args.getJSONArray(ACTIVITY_CODE_SENDVIAEMAIL), args.isNull(ACTIVITY_CODE_SENDVIAWHATSAPP) ? null : args.getJSONArray(ACTIVITY_CODE_SENDVIAWHATSAPP), args.isNull(4) ? null : args.getJSONArray(4), args.isNull(5) ? null : args.getJSONArray(5));
            }
            callbackContext.error("socialSharing." + action + " is not a supported function. Did you mean '" + ACTION_SHARE_EVENT + "'?");
            return false;
        }
    }

    private boolean isEmailAvailable() {
        if (this.cordova.getActivity().getPackageManager().queryIntentActivities(new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", "someone@domain.com", null)), 0).size() > 0) {
            return true;
        }
        return false;
    }

    private boolean invokeEmailIntent(CallbackContext callbackContext, String message, String subject, JSONArray to, JSONArray cc, JSONArray bcc, JSONArray files) throws JSONException {
        this.cordova.getThreadPool().execute(new C05741(callbackContext, message, subject, to, cc, bcc, files, this));
        return true;
    }

    private String getDownloadDir() throws IOException {
        if (!"mounted".equals(Environment.getExternalStorageState())) {
            return null;
        }
        String dir = this.webView.getContext().getExternalFilesDir(null) + "/socialsharing-downloads";
        createOrCleanDir(dir);
        return dir;
    }

    private boolean doSendIntent(CallbackContext callbackContext, String msg, String subject, JSONArray files, String url, String appPackageName, boolean peek) {
        this.cordova.getThreadPool().execute(new C05752(callbackContext, msg, files, subject, url, appPackageName, peek, this.cordova, this));
        return true;
    }

    @SuppressLint({"NewApi"})
    private void copyHintToClipboard(String msg, String label) {
        if (VERSION.SDK_INT >= 11) {
            ((ClipboardManager) this.cordova.getActivity().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(label, msg));
        }
    }

    @SuppressLint({"NewApi"})
    private void showPasteMessage(String label) {
        if (VERSION.SDK_INT >= 11) {
            Toast toast = Toast.makeText(this.webView.getContext(), label, ACTIVITY_CODE_SEND);
            toast.setGravity(17, 0, 0);
            toast.show();
        }
    }

    private Uri getFileUriAndSetType(Intent sendIntent, String dir, String image, String subject, int nthFile) throws IOException {
        String localImage = image;
        sendIntent.setType("image/*");
        if (!image.startsWith("http")) {
            if (!image.startsWith("www/")) {
                String encodedImg;
                String fileName;
                if (image.startsWith("data:")) {
                    if (image.contains(";base64,")) {
                        encodedImg = image.substring(image.indexOf(";base64,") + 8);
                        if (!image.contains("data:image/")) {
                            sendIntent.setType(image.substring(image.indexOf("data:") + 5, image.indexOf(";base64")));
                        }
                        String imgExtension = image.substring(image.indexOf("/") + ACTIVITY_CODE_SEND, image.indexOf(";base64"));
                        if (notEmpty(subject)) {
                            fileName = sanitizeFilename(subject) + (nthFile == 0 ? CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE : "_" + nthFile) + "." + imgExtension;
                        } else {
                            fileName = AndroidProtocolHandler.FILE_SCHEME + (nthFile == 0 ? CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE : "_" + nthFile) + "." + imgExtension;
                        }
                        saveFile(Base64.decode(encodedImg, 0), dir, fileName);
                        localImage = "file://" + dir + "/" + fileName;
                    } else {
                        sendIntent.setType("text/plain");
                        return null;
                    }
                }
                if (image.startsWith("df:")) {
                    if (image.contains(";base64,")) {
                        fileName = image.substring(image.indexOf("df:") + ACTIVITY_CODE_SENDVIAWHATSAPP, image.indexOf(";data:"));
                        String fileType = image.substring(image.indexOf(";data:") + 6, image.indexOf(";base64,"));
                        encodedImg = image.substring(image.indexOf(";base64,") + 8);
                        sendIntent.setType(fileType);
                        saveFile(Base64.decode(encodedImg, 0), dir, sanitizeFilename(fileName));
                        localImage = "file://" + dir + "/" + fileName;
                    } else {
                        sendIntent.setType("text/plain");
                        return null;
                    }
                }
                if (!image.startsWith("file://")) {
                    throw new IllegalArgumentException("URL_NOT_SUPPORTED");
                }
                return Uri.parse(localImage);
            }
        }
        String filename = getFileName(image);
        localImage = "file://" + dir + "/" + filename;
        if (image.startsWith("http")) {
            URLConnection connection = new URL(image).openConnection();
            String disposition = connection.getHeaderField("Content-Disposition");
            if (disposition != null) {
                Matcher matcher = Pattern.compile("filename=([^;]+)").matcher(disposition);
                if (matcher.find()) {
                    filename = matcher.group(ACTIVITY_CODE_SEND).replaceAll("[^a-zA-Z0-9._-]", CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE);
                    if (filename.length() == 0) {
                        filename = AndroidProtocolHandler.FILE_SCHEME;
                    }
                    localImage = "file://" + dir + "/" + filename;
                }
            }
            saveFile(getBytes(connection.getInputStream()), dir, filename);
        } else {
            saveFile(getBytes(this.webView.getContext().getAssets().open(image)), dir, filename);
        }
        return Uri.parse(localImage);
    }

    private boolean shareViaWhatsAppDirectly(CallbackContext callbackContext, String message, String subject, JSONArray files, String url, String number) {
        if (notEmpty(url)) {
            if (notEmpty(message)) {
                message = message + " " + url;
            } else {
                message = url;
            }
        }
        this.cordova.getThreadPool().execute(new C05763(callbackContext, number, message, subject, files, this));
        return true;
    }

    private boolean invokeSMSIntent(CallbackContext callbackContext, JSONObject options, String p_phonenumbers) {
        CallbackContext callbackContext2 = callbackContext;
        this.cordova.getThreadPool().execute(new C05774(callbackContext2, getPhoneNumbersWithManufacturerSpecificSeparators(p_phonenumbers), options.optString(PushConstants.MESSAGE), null, null, this));
        return true;
    }

    private static String getPhoneNumbersWithManufacturerSpecificSeparators(String phonenumbers) {
        if (!notEmpty(phonenumbers)) {
            return null;
        }
        char separator;
        if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            separator = ',';
        } else {
            separator = ';';
        }
        return phonenumbers.replace(';', separator).replace(',', separator);
    }

    private ActivityInfo getActivity(CallbackContext callbackContext, Intent shareIntent, String appPackageName) {
        List<ResolveInfo> activityList = this.webView.getContext().getPackageManager().queryIntentActivities(shareIntent, 0);
        for (ResolveInfo app : activityList) {
            if (app.activityInfo.packageName.contains(appPackageName)) {
                return app.activityInfo;
            }
        }
        callbackContext.sendPluginResult(new PluginResult(Status.ERROR, getShareActivities(activityList)));
        return null;
    }

    private JSONArray getShareActivities(List<ResolveInfo> activityList) {
        List<String> packages = new ArrayList();
        for (ResolveInfo app : activityList) {
            packages.add(app.activityInfo.packageName);
        }
        return new JSONArray(packages);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (this._callbackContext == null) {
            return;
        }
        if (ACTIVITY_CODE_SENDVIAEMAIL == requestCode) {
            this._callbackContext.success();
        } else {
            this._callbackContext.sendPluginResult(new PluginResult(Status.OK, resultCode == -1));
        }
    }

    private void createOrCleanDir(String downloadDir) throws IOException {
        File dir = new File(downloadDir);
        if (dir.exists()) {
            cleanupOldFiles(dir);
        } else if (!dir.mkdirs()) {
            throw new IOException("CREATE_DIRS_FAILED");
        }
    }

    private static String getFileName(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        String pattern = ".*/([^?#]+)?";
        Matcher m = Pattern.compile(".*/([^?#]+)?").matcher(url);
        if (m.find()) {
            return m.group(ACTIVITY_CODE_SEND);
        }
        return AndroidProtocolHandler.FILE_SCHEME;
    }

    private byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[AccessibilityNodeInfoCompat.ACTION_COPY];
        while (true) {
            int nRead = is.read(data, 0, data.length);
            if (nRead != -1) {
                buffer.write(data, 0, nRead);
            } else {
                buffer.flush();
                return buffer.toByteArray();
            }
        }
    }

    private void saveFile(byte[] bytes, String dirName, String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(new File(dirName), fileName));
        fos.write(bytes);
        fos.flush();
        fos.close();
    }

    private void cleanupOldFiles(File dir) {
        File[] listFiles = dir.listFiles();
        int length = listFiles.length;
        for (int i = 0; i < length; i += ACTIVITY_CODE_SEND) {
            listFiles[i].delete();
        }
    }

    private static boolean notEmpty(String what) {
        return (what == null || CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE.equals(what) || "null".equalsIgnoreCase(what)) ? false : true;
    }

    private static String[] toStringArray(JSONArray jsonArray) throws JSONException {
        String[] result = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i += ACTIVITY_CODE_SEND) {
            result[i] = jsonArray.getString(i);
        }
        return result;
    }

    public static String sanitizeFilename(String name) {
        return name.replaceAll("[:\\\\/*?|<> ]", "_");
    }
}
