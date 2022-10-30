package org.chromium.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.List;
import java.util.Set;
import org.chromium.base.ApiCompatibilityUtils;

public class DropdownAdapter extends ArrayAdapter<DropdownItem> {
    private boolean mAreAllItemsEnabled;
    private Context mContext;
    private Set<Integer> mSeparators;

    public DropdownAdapter(Context context, List<DropdownItem> items, Set<Integer> separators) {
        super(context, C0408R.layout.dropdown_item, items);
        this.mSeparators = separators;
        this.mContext = context;
        this.mAreAllItemsEnabled = checkAreAllItemsEnabled();
    }

    public DropdownAdapter(Context context, DropdownItem[] items, Set<Integer> separators) {
        super(context, C0408R.layout.dropdown_item, items);
        this.mSeparators = separators;
        this.mContext = context;
        this.mAreAllItemsEnabled = checkAreAllItemsEnabled();
    }

    private boolean checkAreAllItemsEnabled() {
        for (int i = 0; i < getCount(); i++) {
            DropdownItem item = (DropdownItem) getItem(i);
            if (item.isEnabled() && !item.isGroupHeader()) {
                return false;
            }
        }
        return true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = convertView;
        if (convertView == null) {
            layout = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0408R.layout.dropdown_item, null);
            ApiCompatibilityUtils.setBackgroundForView(layout, new DropdownDividerDrawable());
        }
        DropdownDividerDrawable divider = (DropdownDividerDrawable) layout.getBackground();
        int height = this.mContext.getResources().getDimensionPixelSize(C0408R.dimen.dropdown_item_height);
        if (position == 0) {
            divider.setColor(0);
        } else {
            int dividerHeight = this.mContext.getResources().getDimensionPixelSize(C0408R.dimen.dropdown_item_divider_height);
            height += dividerHeight;
            divider.setHeight(dividerHeight);
            if (this.mSeparators == null || !this.mSeparators.contains(Integer.valueOf(position))) {
                divider.setColor(this.mContext.getResources().getColor(C0408R.color.dropdown_divider_color));
            } else {
                divider.setColor(this.mContext.getResources().getColor(C0408R.color.dropdown_dark_divider_color));
            }
        }
        layout.findViewById(C0408R.id.dropdown_label_wrapper).setLayoutParams(new LayoutParams(-2, height, 1.0f));
        DropdownItem item = (DropdownItem) getItem(position);
        TextView labelView = (TextView) layout.findViewById(C0408R.id.dropdown_label);
        labelView.setText(item.getLabel());
        labelView.setEnabled(item.isEnabled());
        if (item.isGroupHeader()) {
            labelView.setTypeface(null, 1);
        } else {
            labelView.setTypeface(null, 0);
        }
        TextView sublabelView = (TextView) layout.findViewById(C0408R.id.dropdown_sublabel);
        CharSequence sublabel = item.getSublabel();
        if (TextUtils.isEmpty(sublabel)) {
            sublabelView.setVisibility(8);
        } else {
            sublabelView.setText(sublabel);
            sublabelView.setVisibility(0);
        }
        ImageView iconView = (ImageView) layout.findViewById(C0408R.id.dropdown_icon);
        if (item.getIconId() == 0) {
            iconView.setVisibility(8);
        } else {
            iconView.setImageResource(item.getIconId());
            iconView.setVisibility(0);
        }
        return layout;
    }

    public boolean areAllItemsEnabled() {
        return this.mAreAllItemsEnabled;
    }

    public boolean isEnabled(int position) {
        if (position < 0 || position >= getCount()) {
            return false;
        }
        DropdownItem item = (DropdownItem) getItem(position);
        if (!item.isEnabled() || item.isGroupHeader()) {
            return false;
        }
        return true;
    }
}
