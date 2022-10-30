package org.chromium.ui.autofill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.PopupWindow.OnDismissListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.chromium.ui.C0408R;
import org.chromium.ui.DropdownAdapter;
import org.chromium.ui.DropdownPopupWindow;
import org.chromium.ui.base.ViewAndroidDelegate;

public class AutofillPopup extends DropdownPopupWindow implements OnItemClickListener, OnItemLongClickListener, OnDismissListener {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int ITEM_ID_SEPARATOR_ENTRY = -3;
    private final AutofillPopupDelegate mAutofillCallback;
    private final Context mContext;
    private List<AutofillSuggestion> mSuggestions;

    public interface AutofillPopupDelegate {
        void deleteSuggestion(int i);

        void dismissed();

        void suggestionSelected(int i);
    }

    static {
        $assertionsDisabled = !AutofillPopup.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public AutofillPopup(Context context, ViewAndroidDelegate viewAndroidDelegate, AutofillPopupDelegate autofillCallback) {
        super(context, viewAndroidDelegate);
        this.mContext = context;
        this.mAutofillCallback = autofillCallback;
        setOnItemClickListener(this);
        setOnDismissListener(this);
        disableHideOnOutsideTap();
        setContentDescriptionForAccessibility(this.mContext.getString(C0408R.string.autofill_popup_content_description));
    }

    @SuppressLint({"InlinedApi"})
    public void filterAndShow(AutofillSuggestion[] suggestions, boolean isRtl) {
        this.mSuggestions = new ArrayList(Arrays.asList(suggestions));
        List cleanedData = new ArrayList();
        Set separators = new HashSet();
        for (int i = 0; i < suggestions.length; i++) {
            if (suggestions[i].getSuggestionId() == ITEM_ID_SEPARATOR_ENTRY) {
                separators.add(Integer.valueOf(cleanedData.size()));
            } else {
                cleanedData.add(suggestions[i]);
            }
        }
        setAdapter(new DropdownAdapter(this.mContext, cleanedData, separators));
        setRtl(isRtl);
        show();
        getListView().setOnItemLongClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int listIndex = this.mSuggestions.indexOf(((DropdownAdapter) parent.getAdapter()).getItem(position));
        if ($assertionsDisabled || listIndex > -1) {
            this.mAutofillCallback.suggestionSelected(listIndex);
            return;
        }
        throw new AssertionError();
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AutofillSuggestion suggestion = (AutofillSuggestion) ((DropdownAdapter) parent.getAdapter()).getItem(position);
        if (!suggestion.isDeletable()) {
            return $assertionsDisabled;
        }
        int listIndex = this.mSuggestions.indexOf(suggestion);
        if ($assertionsDisabled || listIndex > -1) {
            this.mAutofillCallback.deleteSuggestion(listIndex);
            return true;
        }
        throw new AssertionError();
    }

    public void onDismiss() {
        this.mAutofillCallback.dismissed();
    }
}
