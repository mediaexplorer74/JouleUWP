package org.chromium.ui;

import android.content.Context;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ListView;
import org.chromium.ui.ColorSuggestionListAdapter.OnColorSuggestionClickListener;
import org.chromium.ui.base.PageTransition;

public class ColorPickerSimple extends ListView implements OnColorSuggestionClickListener {
    private static final int[] DEFAULT_COLORS;
    private static final int[] DEFAULT_COLOR_LABEL_IDS;
    private OnColorChangedListener mOnColorChangedListener;

    static {
        DEFAULT_COLORS = new int[]{SupportMenu.CATEGORY_MASK, -16711681, -16776961, -16711936, -65281, PageTransition.QUALIFIER_MASK, ViewCompat.MEASURED_STATE_MASK, -1};
        DEFAULT_COLOR_LABEL_IDS = new int[]{C0408R.string.color_picker_button_red, C0408R.string.color_picker_button_cyan, C0408R.string.color_picker_button_blue, C0408R.string.color_picker_button_green, C0408R.string.color_picker_button_magenta, C0408R.string.color_picker_button_yellow, C0408R.string.color_picker_button_black, C0408R.string.color_picker_button_white};
    }

    public ColorPickerSimple(Context context) {
        super(context);
    }

    public ColorPickerSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPickerSimple(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(ColorSuggestion[] suggestions, OnColorChangedListener onColorChangedListener) {
        this.mOnColorChangedListener = onColorChangedListener;
        if (suggestions == null) {
            suggestions = new ColorSuggestion[DEFAULT_COLORS.length];
            for (int i = 0; i < suggestions.length; i++) {
                suggestions[i] = new ColorSuggestion(DEFAULT_COLORS[i], getContext().getString(DEFAULT_COLOR_LABEL_IDS[i]));
            }
        }
        ColorSuggestionListAdapter adapter = new ColorSuggestionListAdapter(getContext(), suggestions);
        adapter.setOnColorSuggestionClickListener(this);
        setAdapter(adapter);
    }

    public void onColorSuggestionClick(ColorSuggestion suggestion) {
        this.mOnColorChangedListener.onColorChanged(suggestion.mColor);
    }
}
