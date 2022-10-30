package org.chromium.content.browser.input;

import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.UnderlineSpan;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.base.VisibleForTesting;
import org.chromium.blink_public.web.WebInputEventModifier;
import org.chromium.blink_public.web.WebTextInputFlags;
import org.chromium.ui.picker.InputDialogContainer;

@JNINamespace("content")
public class ImeAdapter {
    private static final int COMPOSITION_KEY_CODE = 229;
    private static final int INPUT_DISMISS_DELAY = 150;
    static KeyCharacterMap sKeyCharacterMap;
    static char[] sSingleCharArray;
    private final Runnable mDismissInputRunnable;
    private final Handler mHandler;
    private AdapterInputConnection mInputConnection;
    private InputMethodManagerWrapper mInputMethodManagerWrapper;
    @VisibleForTesting
    boolean mIsShowWithoutHideOutstanding;
    private String mLastComposeText;
    @VisibleForTesting
    int mLastSyntheticKeyCode;
    private long mNativeImeAdapterAndroid;
    private int mTextInputFlags;
    private int mTextInputType;
    private final ImeAdapterDelegate mViewEmbedder;

    /* renamed from: org.chromium.content.browser.input.ImeAdapter.1 */
    class C03611 implements Runnable {
        C03611() {
        }

        public void run() {
            ImeAdapter.this.dismissInput(true);
        }
    }

    public static class AdapterInputConnectionFactory {
        public AdapterInputConnection get(View view, ImeAdapter imeAdapter, Editable editable, EditorInfo outAttrs) {
            return new AdapterInputConnection(view, imeAdapter, editable, outAttrs);
        }
    }

    public interface ImeAdapterDelegate {
        View getAttachedView();

        ResultReceiver getNewShowKeyboardReceiver();

        void onImeEvent();

        void onKeyboardBoundsUnchanged();

        boolean performContextMenuAction(int i);
    }

    private static native void nativeAppendBackgroundColorSpan(long j, int i, int i2, int i3);

    private static native void nativeAppendUnderlineSpan(long j, int i, int i2);

    private native void nativeAttachImeAdapter(long j);

    private native void nativeCommitText(long j, String str);

    private native void nativeDeleteSurroundingText(long j, int i, int i2);

    private native void nativeFinishComposingText(long j);

    private native void nativeResetImeAdapter(long j);

    private native boolean nativeSendKeyEvent(long j, KeyEvent keyEvent, int i, int i2, long j2, int i3, boolean z, int i4);

    private native boolean nativeSendSyntheticKeyEvent(long j, int i, long j2, int i2, int i3, int i4);

    private native void nativeSetComposingRegion(long j, int i, int i2);

    private native void nativeSetComposingText(long j, CharSequence charSequence, String str, int i);

    private native void nativeSetEditableSelectionOffsets(long j, int i, int i2);

    static {
        sSingleCharArray = new char[1];
    }

    public ImeAdapter(InputMethodManagerWrapper wrapper, ImeAdapterDelegate embedder) {
        this.mDismissInputRunnable = new C03611();
        this.mIsShowWithoutHideOutstanding = false;
        this.mInputMethodManagerWrapper = wrapper;
        this.mViewEmbedder = embedder;
        this.mHandler = new Handler();
    }

    @VisibleForTesting
    public void setInputMethodManagerWrapper(InputMethodManagerWrapper immw) {
        this.mInputMethodManagerWrapper = immw;
    }

    InputMethodManagerWrapper getInputMethodManagerWrapper() {
        return this.mInputMethodManagerWrapper;
    }

    void setInputConnection(AdapterInputConnection inputConnection) {
        this.mInputConnection = inputConnection;
        this.mLastComposeText = null;
    }

    int getTextInputType() {
        return this.mTextInputType;
    }

    int getTextInputFlags() {
        return this.mTextInputFlags;
    }

    private static int getModifiers(int metaState) {
        int modifiers = 0;
        if ((metaState & 1) != 0) {
            modifiers = 0 | 1;
        }
        if ((metaState & 2) != 0) {
            modifiers |= 4;
        }
        if ((metaState & WebInputEventModifier.IsRight) != 0) {
            modifiers |= 2;
        }
        if ((AccessibilityNodeInfoCompat.ACTION_DISMISS & metaState) != 0) {
            modifiers |= WebTextInputFlags.AutocapitalizeSentences;
        }
        if ((AccessibilityNodeInfoCompat.ACTION_SET_TEXT & metaState) != 0) {
            return modifiers | WebInputEventModifier.NumLockOn;
        }
        return modifiers;
    }

    public void updateKeyboardVisibility(long nativeImeAdapter, int textInputType, int textInputFlags, boolean showIfNeeded) {
        if (this.mTextInputType == 0 && !showIfNeeded) {
            return;
        }
        if (this.mNativeImeAdapterAndroid != nativeImeAdapter || this.mTextInputType != textInputType) {
            attach(nativeImeAdapter, textInputType, textInputFlags, true);
            if (this.mTextInputType != 0) {
                this.mInputMethodManagerWrapper.restartInput(this.mViewEmbedder.getAttachedView());
                if (showIfNeeded) {
                    showKeyboard();
                }
            }
        } else if (hasInputType() && showIfNeeded) {
            showKeyboard();
        }
    }

    private void attach(long nativeImeAdapter, int textInputType, int textInputFlags, boolean delayDismissInput) {
        if (this.mNativeImeAdapterAndroid != 0) {
            nativeResetImeAdapter(this.mNativeImeAdapterAndroid);
        }
        if (nativeImeAdapter != 0) {
            nativeAttachImeAdapter(nativeImeAdapter);
        }
        this.mNativeImeAdapterAndroid = nativeImeAdapter;
        this.mLastComposeText = null;
        this.mTextInputFlags = textInputFlags;
        if (textInputType != this.mTextInputType) {
            this.mTextInputType = textInputType;
            this.mHandler.removeCallbacks(this.mDismissInputRunnable);
            if (this.mTextInputType != 0) {
                return;
            }
            if (delayDismissInput) {
                this.mHandler.postDelayed(this.mDismissInputRunnable, 150);
                this.mIsShowWithoutHideOutstanding = false;
                return;
            }
            dismissInput(true);
        }
    }

    public void attach(long nativeImeAdapter) {
        attach(nativeImeAdapter, 0, 0, false);
    }

    private void showKeyboard() {
        this.mIsShowWithoutHideOutstanding = true;
        this.mInputMethodManagerWrapper.showSoftInput(this.mViewEmbedder.getAttachedView(), 0, this.mViewEmbedder.getNewShowKeyboardReceiver());
        if (this.mViewEmbedder.getAttachedView().getResources().getConfiguration().keyboard != 1) {
            this.mViewEmbedder.onKeyboardBoundsUnchanged();
        }
    }

    private void dismissInput(boolean unzoomIfNeeded) {
        this.mIsShowWithoutHideOutstanding = false;
        View view = this.mViewEmbedder.getAttachedView();
        if (this.mInputMethodManagerWrapper.isActive(view)) {
            this.mInputMethodManagerWrapper.hideSoftInputFromWindow(view.getWindowToken(), 0, unzoomIfNeeded ? this.mViewEmbedder.getNewShowKeyboardReceiver() : null);
        }
    }

    private boolean hasInputType() {
        return this.mTextInputType != 0;
    }

    private static boolean isTextInputType(int type) {
        return (type == 0 || InputDialogContainer.isDialogInputType(type)) ? false : true;
    }

    public boolean hasTextInputType() {
        return isTextInputType(this.mTextInputType);
    }

    public boolean isSelectionPassword() {
        return this.mTextInputType == 2;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.mInputConnection != null) {
            return this.mInputConnection.sendKeyEvent(event);
        }
        return translateAndSendNativeEvents(event);
    }

    private int shouldSendKeyEventWithKeyCode(String text) {
        if (text.length() != 1) {
            return COMPOSITION_KEY_CODE;
        }
        if (text.equals("\n")) {
            return 66;
        }
        if (text.equals("\t")) {
            return 61;
        }
        return COMPOSITION_KEY_CODE;
    }

    private static KeyEvent androidKeyEventForCharacter(char chr) {
        if (sKeyCharacterMap == null) {
            sKeyCharacterMap = KeyCharacterMap.load(-1);
        }
        sSingleCharArray[0] = chr;
        KeyEvent[] events = sKeyCharacterMap.getEvents(sSingleCharArray);
        if (events == null) {
            return null;
        }
        int i = 0;
        while (i < events.length) {
            if (events[i].getAction() == 0 && !KeyEvent.isModifierKey(events[i].getKeyCode())) {
                return events[i];
            }
            i++;
        }
        return null;
    }

    public boolean performContextMenuAction(int id) {
        return this.mViewEmbedder.performContextMenuAction(id);
    }

    @VisibleForTesting
    public static KeyEvent getTypedKeyEventGuess(String oldtext, String newtext) {
        if (oldtext == null) {
            if (newtext.length() == 1) {
                return androidKeyEventForCharacter(newtext.charAt(0));
            }
            return null;
        } else if (newtext.length() > oldtext.length() && newtext.startsWith(oldtext)) {
            return androidKeyEventForCharacter(newtext.charAt(newtext.length() - 1));
        } else {
            if (oldtext.length() <= newtext.length() || !oldtext.startsWith(newtext)) {
                return null;
            }
            return new KeyEvent(0, 67);
        }
    }

    void sendKeyEventWithKeyCode(int keyCode, int flags) {
        long eventTime = SystemClock.uptimeMillis();
        this.mLastSyntheticKeyCode = keyCode;
        translateAndSendNativeEvents(new KeyEvent(eventTime, eventTime, 0, keyCode, 0, 0, -1, 0, flags));
        translateAndSendNativeEvents(new KeyEvent(SystemClock.uptimeMillis(), eventTime, 1, keyCode, 0, 0, -1, 0, flags));
    }

    boolean checkCompositionQueueAndCallNative(CharSequence text, int newCursorPosition, boolean isCommit) {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        this.mViewEmbedder.onImeEvent();
        String textStr = text.toString();
        int keyCode = shouldSendKeyEventWithKeyCode(textStr);
        long timeStampMs = SystemClock.uptimeMillis();
        if (keyCode != COMPOSITION_KEY_CODE) {
            sendKeyEventWithKeyCode(keyCode, 6);
        } else {
            KeyEvent keyEvent = getTypedKeyEventGuess(this.mLastComposeText, textStr);
            if (keyEvent != null) {
                keyCode = keyEvent.getKeyCode();
                int modifiers = getModifiers(keyEvent.getMetaState());
            } else if (textStr.equals(this.mLastComposeText)) {
                keyCode = -1;
            } else {
                keyCode = 0;
            }
            if (keyCode > 0 && isCommit && this.mLastComposeText == null && textStr.length() == 1) {
                this.mLastSyntheticKeyCode = keyCode;
                if (translateAndSendNativeEvents(keyEvent)) {
                    if (translateAndSendNativeEvents(KeyEvent.changeAction(keyEvent, 1))) {
                        return true;
                    }
                }
                return false;
            }
            if (COMPOSITION_KEY_CODE >= null) {
                nativeSendSyntheticKeyEvent(this.mNativeImeAdapterAndroid, 7, timeStampMs, COMPOSITION_KEY_CODE, 0, 0);
            }
            if (isCommit) {
                nativeCommitText(this.mNativeImeAdapterAndroid, textStr);
                textStr = null;
            } else {
                nativeSetComposingText(this.mNativeImeAdapterAndroid, text, textStr, newCursorPosition);
            }
            if (COMPOSITION_KEY_CODE >= null) {
                nativeSendSyntheticKeyEvent(this.mNativeImeAdapterAndroid, 9, timeStampMs, COMPOSITION_KEY_CODE, 0, 0);
            }
            this.mLastSyntheticKeyCode = COMPOSITION_KEY_CODE;
        }
        this.mLastComposeText = textStr;
        return true;
    }

    @VisibleForTesting
    protected void finishComposingText() {
        this.mLastComposeText = null;
        if (this.mNativeImeAdapterAndroid != 0) {
            nativeFinishComposingText(this.mNativeImeAdapterAndroid);
        }
    }

    boolean translateAndSendNativeEvents(KeyEvent event) {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        int action = event.getAction();
        if (action != 0 && action != 1) {
            return false;
        }
        this.mViewEmbedder.onImeEvent();
        return nativeSendKeyEvent(this.mNativeImeAdapterAndroid, event, event.getAction(), getModifiers(event.getMetaState()), event.getEventTime(), event.getKeyCode(), false, event.getUnicodeChar());
    }

    boolean sendSyntheticKeyEvent(int eventType, long timestampMs, int keyCode, int modifiers, int unicodeChar) {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        nativeSendSyntheticKeyEvent(this.mNativeImeAdapterAndroid, eventType, timestampMs, keyCode, modifiers, unicodeChar);
        return true;
    }

    boolean deleteSurroundingText(int beforeLength, int afterLength) {
        this.mViewEmbedder.onImeEvent();
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        nativeDeleteSurroundingText(this.mNativeImeAdapterAndroid, beforeLength, afterLength);
        return true;
    }

    boolean setEditableSelectionOffsets(int start, int end) {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        nativeSetEditableSelectionOffsets(this.mNativeImeAdapterAndroid, start, end);
        return true;
    }

    boolean setComposingRegion(CharSequence text, int start, int end) {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        nativeSetComposingRegion(this.mNativeImeAdapterAndroid, start, end);
        this.mLastComposeText = text != null ? text.toString() : null;
        return true;
    }

    @CalledByNative
    private void focusedNodeChanged(boolean isEditable) {
        if (this.mInputConnection != null && isEditable) {
            this.mInputConnection.restartInput();
        }
    }

    @CalledByNative
    private void populateUnderlinesFromSpans(CharSequence text, long underlines) {
        if (text instanceof SpannableString) {
            SpannableString spannableString = (SpannableString) text;
            for (CharacterStyle span : (CharacterStyle[]) spannableString.getSpans(0, text.length(), CharacterStyle.class)) {
                if (span instanceof BackgroundColorSpan) {
                    nativeAppendBackgroundColorSpan(underlines, spannableString.getSpanStart(span), spannableString.getSpanEnd(span), ((BackgroundColorSpan) span).getBackgroundColor());
                } else if (span instanceof UnderlineSpan) {
                    nativeAppendUnderlineSpan(underlines, spannableString.getSpanStart(span), spannableString.getSpanEnd(span));
                }
            }
        }
    }

    @CalledByNative
    private void cancelComposition() {
        if (this.mInputConnection != null) {
            this.mInputConnection.restartInput();
        }
        this.mLastComposeText = null;
    }

    @CalledByNative
    void detach() {
        this.mHandler.removeCallbacks(this.mDismissInputRunnable);
        this.mNativeImeAdapterAndroid = 0;
        this.mTextInputType = 0;
    }
}
