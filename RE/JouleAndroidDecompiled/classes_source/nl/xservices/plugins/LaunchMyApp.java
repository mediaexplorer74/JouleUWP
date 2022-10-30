package nl.xservices.plugins;

import android.content.Intent;
import android.support.v4.view.MotionEventCompat;
import com.google.android.gms.common.ConnectionResult;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.chromium.ui.base.ime.TextInputType;
import org.json.JSONArray;
import org.json.JSONException;

public class LaunchMyApp extends CordovaPlugin {
    private static final String ACTION_CHECKINTENT = "checkIntent";
    private static final String ACTION_CLEARINTENT = "clearIntent";
    private static final String ACTION_GETLASTINTENT = "getLastIntent";
    private String lastIntentString;

    public LaunchMyApp() {
        this.lastIntentString = null;
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (ACTION_CLEARINTENT.equalsIgnoreCase(action)) {
            ((CordovaActivity) this.webView.getContext()).getIntent().setData(null);
            return true;
        } else if (ACTION_CHECKINTENT.equalsIgnoreCase(action)) {
            Intent intent = ((CordovaActivity) this.webView.getContext()).getIntent();
            String intentString = intent.getDataString();
            if (intentString == null || intent.getScheme() == null) {
                callbackContext.error("App was not started via the launchmyapp URL scheme. Ignoring this errorcallback is the best approach.");
            } else {
                this.lastIntentString = intentString;
                callbackContext.sendPluginResult(new PluginResult(Status.OK, intent.getDataString()));
            }
            return true;
        } else if (ACTION_GETLASTINTENT.equalsIgnoreCase(action)) {
            if (this.lastIntentString != null) {
                callbackContext.sendPluginResult(new PluginResult(Status.OK, this.lastIntentString));
            } else {
                callbackContext.error("No intent received so far.");
            }
            return true;
        } else {
            callbackContext.error("This plugin only responds to the checkIntent action.");
            return false;
        }
    }

    public void onNewIntent(Intent intent) {
        String intentString = intent.getDataString();
        if (intentString != null && intent.getScheme() != null) {
            intent.setData(null);
            try {
                StringWriter writer = new StringWriter(intentString.length() * 2);
                escapeJavaStyleString(writer, intentString, true, false);
                this.webView.loadUrl("javascript:handleOpenURL('" + writer.toString() + "');");
            } catch (IOException e) {
            }
        }
    }

    private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote, boolean escapeForwardSlash) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if (str != null) {
            int sz = str.length();
            for (int i = 0; i < sz; i++) {
                char ch = str.charAt(i);
                if (ch <= '\u0fff') {
                    if (ch <= '\u00ff') {
                        if (ch <= '\u007f') {
                            if (ch >= ' ') {
                                switch (ch) {
                                    case MotionEventCompat.AXIS_GENERIC_3 /*34*/:
                                        out.write(92);
                                        out.write(34);
                                        break;
                                    case MotionEventCompat.AXIS_GENERIC_8 /*39*/:
                                        if (escapeSingleQuote) {
                                            out.write(92);
                                        }
                                        out.write(39);
                                        break;
                                    case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
                                        if (escapeForwardSlash) {
                                            out.write(92);
                                        }
                                        out.write(47);
                                        break;
                                    case '\\':
                                        out.write(92);
                                        out.write(92);
                                        break;
                                    default:
                                        out.write(ch);
                                        break;
                                }
                            }
                            switch (ch) {
                                case ConnectionResult.INTERNAL_ERROR /*8*/:
                                    out.write(92);
                                    out.write(98);
                                    break;
                                case ConnectionResult.SERVICE_INVALID /*9*/:
                                    out.write(92);
                                    out.write(116);
                                    break;
                                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                                    out.write(92);
                                    out.write(110);
                                    break;
                                case TextInputType.TIME /*12*/:
                                    out.write(92);
                                    out.write(102);
                                    break;
                                case ConnectionResult.CANCELED /*13*/:
                                    out.write(92);
                                    out.write(114);
                                    break;
                                default:
                                    if (ch <= '\u000f') {
                                        out.write("\\u000" + hex(ch));
                                        break;
                                    } else {
                                        out.write("\\u00" + hex(ch));
                                        break;
                                    }
                            }
                        }
                        out.write("\\u00" + hex(ch));
                    } else {
                        out.write("\\u0" + hex(ch));
                    }
                } else {
                    out.write("\\u" + hex(ch));
                }
            }
        }
    }

    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
    }
}
