package org.chromium.ui.autofill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.ui.C0408R;
import org.chromium.ui.base.WindowAndroid;
import org.chromium.ui.base.WindowAndroid.KeyboardVisibilityListener;

public class AutofillKeyboardAccessory extends ListView implements OnItemClickListener, KeyboardVisibilityListener {
    static final /* synthetic */ boolean $assertionsDisabled;
    private final AutofillKeyboardAccessoryDelegate mAutofillCallback;
    private final WindowAndroid mWindowAndroid;

    public interface AutofillKeyboardAccessoryDelegate {
        void dismissed();

        void suggestionSelected(int i);
    }

    static {
        $assertionsDisabled = !AutofillKeyboardAccessory.class.desiredAssertionStatus();
    }

    public AutofillKeyboardAccessory(WindowAndroid windowAndroid, AutofillKeyboardAccessoryDelegate autofillCallback) {
        super((Context) windowAndroid.getActivity().get());
        if (!$assertionsDisabled && autofillCallback == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || windowAndroid.getActivity().get() != null) {
            this.mWindowAndroid = windowAndroid;
            this.mAutofillCallback = autofillCallback;
            this.mWindowAndroid.addKeyboardVisibilityListener(this);
            setOnItemClickListener(this);
            setContentDescription(getContext().getString(C0408R.string.autofill_popup_content_description));
            setBackgroundColor(getResources().getColor(C0408R.color.keyboard_accessory_suggestion_background_color));
        } else {
            throw new AssertionError();
        }
    }

    @SuppressLint({"InlinedApi"})
    public void showWithSuggestions(AutofillSuggestion[] suggestions, boolean isRtl) {
        int i;
        setAdapter(new SuggestionAdapter(getContext(), suggestions));
        if (isRtl) {
            i = 1;
        } else {
            i = 0;
        }
        ApiCompatibilityUtils.setLayoutDirection(this, i);
        int height = -2;
        ListAdapter listAdapter = getAdapter();
        if (listAdapter.getCount() > 2) {
            height = 0;
            for (int i2 = 0; i2 < 2; i2++) {
                height += listAdapter.getView(i2, null, this).getLayoutParams().height;
            }
        }
        setLayoutParams(new LayoutParams(-1, height));
        if (getParent() == null) {
            ViewGroup container = this.mWindowAndroid.getKeyboardAccessoryView();
            container.addView(this);
            container.setVisibility(0);
            sendAccessibilityEvent(32);
        }
    }

    public void dismiss() {
        ViewGroup container = this.mWindowAndroid.getKeyboardAccessoryView();
        container.removeView(this);
        container.setVisibility(8);
        this.mWindowAndroid.removeKeyboardVisibilityListener(this);
        ((View) container.getParent()).requestLayout();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        this.mAutofillCallback.suggestionSelected(position);
    }

    public void keyboardVisibilityChanged(boolean isShowing) {
        if (!isShowing) {
            dismiss();
            this.mAutofillCallback.dismissed();
        }
    }
}
