package org.chromium.ui.autofill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.ui.C0408R;
import org.chromium.ui.DropdownItem;

public class SuggestionAdapter extends ArrayAdapter<DropdownItem> {
    private Context mContext;

    public SuggestionAdapter(Context context, DropdownItem[] items) {
        super(context, C0408R.layout.autofill_suggestion_item, items);
        this.mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int iconId;
        View layout = convertView;
        if (convertView == null) {
            layout = LayoutInflater.from(this.mContext).inflate(C0408R.layout.autofill_suggestion_item, parent, false);
        }
        DropdownItem item = (DropdownItem) getItem(position);
        TextView labelView = (TextView) layout;
        labelView.setText(item.getLabel());
        if (item.getIconId() != 0) {
            iconId = item.getIconId();
        } else {
            iconId = 0;
        }
        ApiCompatibilityUtils.setCompoundDrawablesRelativeWithIntrinsicBounds(labelView, iconId, 0, 0, 0);
        return layout;
    }
}
