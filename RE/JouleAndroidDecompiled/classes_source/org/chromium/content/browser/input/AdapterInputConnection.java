package org.chromium.content.browser.input;

import android.os.SystemClock;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import org.chromium.base.VisibleForTesting;
import org.chromium.blink_public.web.WebInputEventModifier;
import org.chromium.blink_public.web.WebTextInputFlags;

public class AdapterInputConnection extends BaseInputConnection {
    private static final boolean DEBUG = false;
    public static final int INVALID_COMPOSITION = -1;
    public static final int INVALID_SELECTION = -1;
    private static final String TAG = "cr.InputConnection";
    private final Editable mEditable;
    private final ImeAdapter mImeAdapter;
    private final View mInternalView;
    private int mLastUpdateCompositionEnd;
    private int mLastUpdateCompositionStart;
    private int mLastUpdateSelectionEnd;
    private int mLastUpdateSelectionStart;
    private int mNumNestedBatchEdits;
    private int mPendingAccent;
    private boolean mSingleLine;

    @VisibleForTesting
    static class ImeState {
        public final int compositionEnd;
        public final int compositionStart;
        public final int selectionEnd;
        public final int selectionStart;
        public final String text;

        public ImeState(String text, int selectionStart, int selectionEnd, int compositionStart, int compositionEnd) {
            this.text = text;
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
            this.compositionStart = compositionStart;
            this.compositionEnd = compositionEnd;
        }
    }

    @VisibleForTesting
    AdapterInputConnection(View view, ImeAdapter imeAdapter, Editable editable, EditorInfo outAttrs) {
        super(view, true);
        this.mNumNestedBatchEdits = 0;
        this.mLastUpdateSelectionStart = INVALID_SELECTION;
        this.mLastUpdateSelectionEnd = INVALID_SELECTION;
        this.mLastUpdateCompositionStart = INVALID_SELECTION;
        this.mLastUpdateCompositionEnd = INVALID_SELECTION;
        this.mInternalView = view;
        this.mImeAdapter = imeAdapter;
        this.mImeAdapter.setInputConnection(this);
        this.mEditable = editable;
        finishComposingText();
        this.mSingleLine = true;
        outAttrs.imeOptions = 301989888;
        outAttrs.inputType = 161;
        int inputType = imeAdapter.getTextInputType();
        int inputFlags = imeAdapter.getTextInputFlags();
        if ((inputFlags & 2) != 0) {
            outAttrs.inputType |= AccessibilityNodeInfoCompat.ACTION_COLLAPSE;
        }
        if (inputType == 1) {
            outAttrs.imeOptions |= 2;
            if ((inputFlags & 8) == 0) {
                outAttrs.inputType |= AccessibilityNodeInfoCompat.ACTION_PASTE;
            }
        } else if (inputType == 14 || inputType == 15) {
            outAttrs.inputType |= AccessibilityNodeInfoCompat.ACTION_SET_SELECTION;
            if ((inputFlags & 8) == 0) {
                outAttrs.inputType |= AccessibilityNodeInfoCompat.ACTION_PASTE;
            }
            outAttrs.imeOptions |= 1;
            this.mSingleLine = DEBUG;
        } else if (inputType == 2) {
            outAttrs.inputType = 225;
            outAttrs.imeOptions |= 2;
        } else if (inputType == 3) {
            outAttrs.imeOptions |= 3;
        } else if (inputType == 7) {
            outAttrs.inputType = 17;
            outAttrs.imeOptions |= 2;
        } else if (inputType == 4) {
            outAttrs.inputType = 209;
            outAttrs.imeOptions |= 2;
        } else if (inputType == 6) {
            outAttrs.inputType = 3;
            outAttrs.imeOptions |= 5;
        } else if (inputType == 5) {
            outAttrs.inputType = InputDeviceCompat.SOURCE_MOUSE;
            outAttrs.imeOptions |= 5;
        }
        if ((inputFlags & TransportMediator.FLAG_KEY_MEDIA_NEXT) != 0) {
            outAttrs.inputType |= WebInputEventModifier.IsRight;
        } else if ((inputFlags & WebTextInputFlags.AutocapitalizeWords) != 0) {
            outAttrs.inputType |= AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD;
        } else if ((inputFlags & WebTextInputFlags.AutocapitalizeSentences) != 0) {
            outAttrs.inputType |= AccessibilityNodeInfoCompat.ACTION_COPY;
        }
        if (inputType == 15) {
            outAttrs.inputType |= AccessibilityNodeInfoCompat.ACTION_COPY;
        }
        outAttrs.initialSelStart = Selection.getSelectionStart(this.mEditable);
        outAttrs.initialSelEnd = Selection.getSelectionEnd(this.mEditable);
        this.mLastUpdateSelectionStart = outAttrs.initialSelStart;
        this.mLastUpdateSelectionEnd = outAttrs.initialSelEnd;
        Selection.setSelection(this.mEditable, outAttrs.initialSelStart, outAttrs.initialSelEnd);
        updateSelectionIfRequired();
    }

    @VisibleForTesting
    public void updateState(String text, int selectionStart, int selectionEnd, int compositionStart, int compositionEnd, boolean isNonImeChange) {
        if (isNonImeChange) {
            text = text.replace('\u00a0', ' ');
            selectionStart = Math.min(selectionStart, text.length());
            selectionEnd = Math.min(selectionEnd, text.length());
            compositionStart = Math.min(compositionStart, text.length());
            compositionEnd = Math.min(compositionEnd, text.length());
            if (!this.mEditable.toString().equals(text)) {
                this.mEditable.replace(0, this.mEditable.length(), text);
            }
            Selection.setSelection(this.mEditable, selectionStart, selectionEnd);
            if (compositionStart == compositionEnd) {
                removeComposingSpans(this.mEditable);
            } else {
                super.setComposingRegion(compositionStart, compositionEnd);
            }
            updateSelectionIfRequired();
        }
    }

    public Editable getEditable() {
        return this.mEditable;
    }

    private void updateSelectionIfRequired() {
        if (this.mNumNestedBatchEdits == 0) {
            int selectionStart = Selection.getSelectionStart(this.mEditable);
            int selectionEnd = Selection.getSelectionEnd(this.mEditable);
            int compositionStart = getComposingSpanStart(this.mEditable);
            int compositionEnd = getComposingSpanEnd(this.mEditable);
            if (this.mLastUpdateSelectionStart != selectionStart || this.mLastUpdateSelectionEnd != selectionEnd || this.mLastUpdateCompositionStart != compositionStart || this.mLastUpdateCompositionEnd != compositionEnd) {
                getInputMethodManagerWrapper().updateSelection(this.mInternalView, selectionStart, selectionEnd, compositionStart, compositionEnd);
                this.mLastUpdateSelectionStart = selectionStart;
                this.mLastUpdateSelectionEnd = selectionEnd;
                this.mLastUpdateCompositionStart = compositionStart;
                this.mLastUpdateCompositionEnd = compositionEnd;
            }
        }
    }

    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        if (maybePerformEmptyCompositionWorkaround(text)) {
            return true;
        }
        this.mPendingAccent = 0;
        super.setComposingText(text, newCursorPosition);
        updateSelectionIfRequired();
        return this.mImeAdapter.checkCompositionQueueAndCallNative(text, newCursorPosition, DEBUG);
    }

    public boolean commitText(CharSequence text, int newCursorPosition) {
        boolean z = true;
        if (maybePerformEmptyCompositionWorkaround(text)) {
            return true;
        }
        this.mPendingAccent = 0;
        super.commitText(text, newCursorPosition);
        updateSelectionIfRequired();
        ImeAdapter imeAdapter = this.mImeAdapter;
        if (text.length() <= 0) {
            z = DEBUG;
        }
        return imeAdapter.checkCompositionQueueAndCallNative(text, newCursorPosition, z);
    }

    public boolean performEditorAction(int actionCode) {
        if (actionCode == 5) {
            restartInput();
            this.mImeAdapter.sendSyntheticKeyEvent(7, SystemClock.uptimeMillis(), 61, 0, 0);
        } else {
            this.mImeAdapter.sendKeyEventWithKeyCode(66, 22);
        }
        return true;
    }

    public boolean performContextMenuAction(int id) {
        return this.mImeAdapter.performContextMenuAction(id);
    }

    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        ExtractedText et = new ExtractedText();
        et.text = this.mEditable.toString();
        et.partialEndOffset = this.mEditable.length();
        et.selectionStart = Selection.getSelectionStart(this.mEditable);
        et.selectionEnd = Selection.getSelectionEnd(this.mEditable);
        et.flags = this.mSingleLine ? 1 : 0;
        return et;
    }

    public boolean beginBatchEdit() {
        this.mNumNestedBatchEdits++;
        return true;
    }

    public boolean endBatchEdit() {
        if (this.mNumNestedBatchEdits == 0) {
            return DEBUG;
        }
        this.mNumNestedBatchEdits += INVALID_SELECTION;
        if (this.mNumNestedBatchEdits == 0) {
            updateSelectionIfRequired();
        }
        if (this.mNumNestedBatchEdits != 0) {
            return true;
        }
        return DEBUG;
    }

    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        return deleteSurroundingTextImpl(beforeLength, afterLength, DEBUG);
    }

    @VisibleForTesting
    static boolean isIndexBetweenUtf16SurrogatePair(CharSequence str, int index) {
        return (index <= 0 || index >= str.length() || !Character.isHighSurrogate(str.charAt(index + INVALID_SELECTION)) || !Character.isLowSurrogate(str.charAt(index))) ? DEBUG : true;
    }

    private boolean deleteSurroundingTextImpl(int beforeLength, int afterLength, boolean fromPhysicalKey) {
        if (this.mPendingAccent != 0) {
            finishComposingText();
        }
        int originalBeforeLength = beforeLength;
        int originalAfterLength = afterLength;
        int selectionStart = Selection.getSelectionStart(this.mEditable);
        int selectionEnd = Selection.getSelectionEnd(this.mEditable);
        int availableAfter = this.mEditable.length() - selectionEnd;
        beforeLength = Math.min(beforeLength, selectionStart);
        afterLength = Math.min(afterLength, availableAfter);
        if (isIndexBetweenUtf16SurrogatePair(this.mEditable, selectionStart - beforeLength)) {
            beforeLength++;
        }
        if (isIndexBetweenUtf16SurrogatePair(this.mEditable, selectionEnd + afterLength)) {
            afterLength++;
        }
        super.deleteSurroundingText(beforeLength, afterLength);
        updateSelectionIfRequired();
        if (fromPhysicalKey) {
            return true;
        }
        int keyCode = 0;
        if (originalBeforeLength == 1 && originalAfterLength == 0) {
            keyCode = 67;
        } else if (originalBeforeLength == 0 && originalAfterLength == 1) {
            keyCode = 112;
        }
        if (keyCode == 0) {
            return (this.mImeAdapter.sendSyntheticKeyEvent(7, SystemClock.uptimeMillis(), keyCode, 0, 0) & this.mImeAdapter.deleteSurroundingText(beforeLength, afterLength)) & this.mImeAdapter.sendSyntheticKeyEvent(9, SystemClock.uptimeMillis(), keyCode, 0, 0);
        }
        this.mImeAdapter.sendKeyEventWithKeyCode(keyCode, 6);
        return true;
    }

    public boolean sendKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keycode = event.getKeyCode();
        int unicodeChar = event.getUnicodeChar();
        if (action != 0) {
            this.mImeAdapter.translateAndSendNativeEvents(event);
        } else {
            if (keycode == 67) {
                deleteSurroundingTextImpl(1, 0, true);
            } else if (keycode == 112) {
                deleteSurroundingTextImpl(0, 1, true);
            } else if (keycode == 66) {
                finishComposingText();
            } else if ((ExploreByTouchHelper.INVALID_ID & unicodeChar) != 0) {
                int pendingAccent = unicodeChar & Integer.MAX_VALUE;
                builder = new StringBuilder();
                builder.appendCodePoint(pendingAccent);
                setComposingText(builder.toString(), 1);
                this.mPendingAccent = pendingAccent;
            } else if (!(this.mPendingAccent == 0 || unicodeChar == 0)) {
                int combined = KeyEvent.getDeadChar(this.mPendingAccent, unicodeChar);
                if (combined != 0) {
                    builder = new StringBuilder();
                    builder.appendCodePoint(combined);
                    commitText(builder.toString(), 1);
                } else {
                    finishComposingText();
                }
            }
            replaceSelectionWithUnicodeChar(unicodeChar);
            this.mImeAdapter.translateAndSendNativeEvents(event);
        }
        return true;
    }

    private void replaceSelectionWithUnicodeChar(int unicodeChar) {
        if (unicodeChar != 0) {
            int selectionStart = Selection.getSelectionStart(this.mEditable);
            int selectionEnd = Selection.getSelectionEnd(this.mEditable);
            if (selectionStart > selectionEnd) {
                int temp = selectionStart;
                selectionStart = selectionEnd;
                selectionEnd = temp;
            }
            this.mEditable.replace(selectionStart, selectionEnd, Character.toString((char) unicodeChar));
            updateSelectionIfRequired();
        }
    }

    public boolean finishComposingText() {
        this.mPendingAccent = 0;
        if (getComposingSpanStart(this.mEditable) != getComposingSpanEnd(this.mEditable)) {
            super.finishComposingText();
            updateSelectionIfRequired();
            this.mImeAdapter.finishComposingText();
        }
        return true;
    }

    public boolean setSelection(int start, int end) {
        int textLength = this.mEditable.length();
        if (start < 0 || end < 0 || start > textLength || end > textLength) {
            return true;
        }
        super.setSelection(start, end);
        updateSelectionIfRequired();
        return this.mImeAdapter.setEditableSelectionOffsets(start, end);
    }

    void restartInput() {
        getInputMethodManagerWrapper().restartInput(this.mInternalView);
        this.mNumNestedBatchEdits = 0;
        this.mPendingAccent = 0;
    }

    public boolean setComposingRegion(int start, int end) {
        int textLength = this.mEditable.length();
        int a = Math.min(start, end);
        int b = Math.max(start, end);
        if (a < 0) {
            a = 0;
        }
        if (b < 0) {
            b = 0;
        }
        if (a > textLength) {
            a = textLength;
        }
        if (b > textLength) {
            b = textLength;
        }
        CharSequence regionText = null;
        if (a == b) {
            removeComposingSpans(this.mEditable);
        } else {
            if (a == 0 && b == this.mEditable.length()) {
                regionText = this.mEditable.subSequence(a, b);
                for (int i = a; i < b; i++) {
                    if (regionText.charAt(i) == '\ufffc') {
                        return true;
                    }
                }
            }
            super.setComposingRegion(a, b);
        }
        updateSelectionIfRequired();
        return this.mImeAdapter.setComposingRegion(regionText, a, b);
    }

    boolean isActive() {
        return getInputMethodManagerWrapper().isActive(this.mInternalView);
    }

    private InputMethodManagerWrapper getInputMethodManagerWrapper() {
        return this.mImeAdapter.getInputMethodManagerWrapper();
    }

    private boolean maybePerformEmptyCompositionWorkaround(CharSequence text) {
        int selectionStart = Selection.getSelectionStart(this.mEditable);
        int selectionEnd = Selection.getSelectionEnd(this.mEditable);
        int compositionStart = getComposingSpanStart(this.mEditable);
        int compositionEnd = getComposingSpanEnd(this.mEditable);
        if (!TextUtils.isEmpty(text) || selectionStart != selectionEnd || compositionStart == INVALID_SELECTION || compositionEnd == INVALID_SELECTION) {
            return DEBUG;
        }
        beginBatchEdit();
        finishComposingText();
        int selection = Selection.getSelectionStart(this.mEditable);
        deleteSurroundingText(selection - compositionStart, selection - compositionEnd);
        endBatchEdit();
        return true;
    }

    @VisibleForTesting
    ImeState getImeStateForTesting() {
        return new ImeState(this.mEditable.toString(), Selection.getSelectionStart(this.mEditable), Selection.getSelectionEnd(this.mEditable), getComposingSpanStart(this.mEditable), getComposingSpanEnd(this.mEditable));
    }
}
