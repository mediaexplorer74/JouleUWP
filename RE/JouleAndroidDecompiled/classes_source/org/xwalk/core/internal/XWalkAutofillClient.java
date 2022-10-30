package org.xwalk.core.internal;

import org.chromium.base.CalledByNative;
import org.chromium.base.JNINamespace;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.ui.autofill.AutofillPopup;
import org.chromium.ui.autofill.AutofillPopup.AutofillPopupDelegate;
import org.chromium.ui.autofill.AutofillSuggestion;

@JNINamespace("xwalk")
public class XWalkAutofillClient {
    private AutofillPopup mAutofillPopup;
    private ContentViewCore mContentViewCore;
    private final long mNativeXWalkAutofillClient;

    /* renamed from: org.xwalk.core.internal.XWalkAutofillClient.1 */
    class C06491 implements AutofillPopupDelegate {
        C06491() {
        }

        public void dismissed() {
        }

        public void suggestionSelected(int listIndex) {
            XWalkAutofillClient.this.nativeSuggestionSelected(XWalkAutofillClient.this.mNativeXWalkAutofillClient, listIndex);
        }

        public void deleteSuggestion(int listIndex) {
        }
    }

    private native void nativeSuggestionSelected(long j, int i);

    @CalledByNative
    public static XWalkAutofillClient create(long nativeClient) {
        return new XWalkAutofillClient(nativeClient);
    }

    private XWalkAutofillClient(long nativeXWalkAutofillClient) {
        this.mNativeXWalkAutofillClient = nativeXWalkAutofillClient;
    }

    public void init(ContentViewCore contentViewCore) {
        this.mContentViewCore = contentViewCore;
    }

    @CalledByNative
    private void showAutofillPopup(float x, float y, float width, float height, boolean isRtl, AutofillSuggestion[] suggestions) {
        if (this.mContentViewCore != null) {
            if (this.mAutofillPopup == null) {
                this.mAutofillPopup = new AutofillPopup(this.mContentViewCore.getContext(), this.mContentViewCore.getViewAndroidDelegate(), new C06491());
            }
            this.mAutofillPopup.setAnchorRect(x, y, width, height);
            this.mAutofillPopup.filterAndShow(suggestions, isRtl);
        }
    }

    @CalledByNative
    public void hideAutofillPopup() {
        if (this.mAutofillPopup != null) {
            this.mAutofillPopup.dismiss();
            this.mAutofillPopup = null;
        }
    }

    @CalledByNative
    private static AutofillSuggestion[] createAutofillSuggestionArray(int size) {
        return new AutofillSuggestion[size];
    }

    @CalledByNative
    private static void addToAutofillSuggestionArray(AutofillSuggestion[] array, int index, String name, String label, int uniqueId) {
        array[index] = new AutofillSuggestion(name, label, 0, uniqueId, false);
    }
}
