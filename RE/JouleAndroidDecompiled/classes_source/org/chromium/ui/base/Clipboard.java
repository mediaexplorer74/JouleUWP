package org.chromium.ui.base;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build.VERSION;
import android.widget.Toast;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.ui.C0408R;

@JNINamespace("ui")
public class Clipboard {
    private static final boolean IS_HTML_CLIPBOARD_SUPPORTED;
    private final ClipboardManager mClipboardManager;
    private final Context mContext;

    static {
        IS_HTML_CLIPBOARD_SUPPORTED = VERSION.SDK_INT >= 16;
    }

    public Clipboard(Context context) {
        this.mContext = context;
        this.mClipboardManager = (ClipboardManager) context.getSystemService("clipboard");
    }

    @CalledByNative
    private static Clipboard create(Context context) {
        return new Clipboard(context);
    }

    @CalledByNative
    private String getCoercedText() {
        ClipData clip = this.mClipboardManager.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            CharSequence sequence = clip.getItemAt(0).coerceToText(this.mContext);
            if (sequence != null) {
                return sequence.toString();
            }
        }
        return null;
    }

    @TargetApi(16)
    @CalledByNative
    private String getHTMLText() {
        if (IS_HTML_CLIPBOARD_SUPPORTED) {
            ClipData clip = this.mClipboardManager.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                return clip.getItemAt(0).getHtmlText();
            }
        }
        return null;
    }

    public void setText(String label, String text) {
        setPrimaryClipNoException(ClipData.newPlainText(label, text));
    }

    @CalledByNative
    public void setText(String text) {
        setText(null, text);
    }

    @TargetApi(16)
    public void setHTMLText(String html, String label, String text) {
        if (IS_HTML_CLIPBOARD_SUPPORTED) {
            setPrimaryClipNoException(ClipData.newHtmlText(label, text, html));
        }
    }

    @CalledByNative
    public void setHTMLText(String html, String text) {
        setHTMLText(html, null, text);
    }

    @CalledByNative
    private static boolean isHTMLClipboardSupported() {
        return IS_HTML_CLIPBOARD_SUPPORTED;
    }

    private void setPrimaryClipNoException(ClipData clip) {
        try {
            this.mClipboardManager.setPrimaryClip(clip);
        } catch (Exception e) {
            Toast.makeText(this.mContext, this.mContext.getString(C0408R.string.copy_to_clipboard_failure_message), 0).show();
        }
    }
}
