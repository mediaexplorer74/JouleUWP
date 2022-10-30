package org.chromium.content.browser.input;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.view.View;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.InputMethodManager;

public class InputMethodManagerWrapper {
    private final Context mContext;

    public InputMethodManagerWrapper(Context context) {
        this.mContext = context;
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) this.mContext.getSystemService("input_method");
    }

    public void restartInput(View view) {
        getInputMethodManager().restartInput(view);
    }

    public void showSoftInput(View view, int flags, ResultReceiver resultReceiver) {
        getInputMethodManager().showSoftInput(view, flags, resultReceiver);
    }

    public boolean isActive(View view) {
        return getInputMethodManager().isActive(view);
    }

    public boolean hideSoftInputFromWindow(IBinder windowToken, int flags, ResultReceiver resultReceiver) {
        return getInputMethodManager().hideSoftInputFromWindow(windowToken, flags, resultReceiver);
    }

    public void updateSelection(View view, int selStart, int selEnd, int candidatesStart, int candidatesEnd) {
        getInputMethodManager().updateSelection(view, selStart, selEnd, candidatesStart, candidatesEnd);
    }

    @TargetApi(21)
    public void updateCursorAnchorInfo(View view, CursorAnchorInfo cursorAnchorInfo) {
        if (VERSION.SDK_INT >= 21) {
            getInputMethodManager().updateCursorAnchorInfo(view, cursorAnchorInfo);
        }
    }
}
