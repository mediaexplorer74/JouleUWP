package org.chromium.ui;

import android.annotation.TargetApi;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build.VERSION;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import org.chromium.base.ApiCompatibilityUtils;

public class ColorPickerAdvancedComponent {
    private int[] mGradientColors;
    private GradientDrawable mGradientDrawable;
    private final View mGradientView;
    private final SeekBar mSeekBar;
    private final TextView mText;

    ColorPickerAdvancedComponent(View rootView, int textResourceId, int seekBarMax, OnSeekBarChangeListener seekBarListener) {
        this.mGradientView = rootView.findViewById(C0408R.id.gradient);
        this.mText = (TextView) rootView.findViewById(C0408R.id.text);
        this.mText.setText(textResourceId);
        this.mGradientDrawable = new GradientDrawable(Orientation.LEFT_RIGHT, null);
        this.mSeekBar = (SeekBar) rootView.findViewById(C0408R.id.seek_bar);
        this.mSeekBar.setOnSeekBarChangeListener(seekBarListener);
        this.mSeekBar.setMax(seekBarMax);
        this.mSeekBar.setThumbOffset(ApiCompatibilityUtils.getDrawable(rootView.getContext().getResources(), C0408R.drawable.color_picker_advanced_select_handle).getIntrinsicWidth() / 2);
    }

    public float getValue() {
        return (float) this.mSeekBar.getProgress();
    }

    public void setValue(float newValue) {
        this.mSeekBar.setProgress((int) newValue);
    }

    @TargetApi(16)
    public void setGradientColors(int[] newColors) {
        this.mGradientColors = (int[]) newColors.clone();
        if (VERSION.SDK_INT < 16) {
            this.mGradientDrawable = new GradientDrawable(Orientation.LEFT_RIGHT, this.mGradientColors);
        } else {
            this.mGradientDrawable.setColors(this.mGradientColors);
        }
        ApiCompatibilityUtils.setBackgroundForView(this.mGradientView, this.mGradientDrawable);
    }
}
