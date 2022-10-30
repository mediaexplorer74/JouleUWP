package org.chromium.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ColorPickerAdvanced extends LinearLayout implements OnSeekBarChangeListener {
    private static final int HUE_COLOR_COUNT = 7;
    private static final int HUE_SEEK_BAR_MAX = 360;
    private static final int SATURATION_COLOR_COUNT = 2;
    private static final int SATURATION_SEEK_BAR_MAX = 100;
    private static final int VALUE_COLOR_COUNT = 2;
    private static final int VALUE_SEEK_BAR_MAX = 100;
    private int mCurrentColor;
    private final float[] mCurrentHsvValues;
    ColorPickerAdvancedComponent mHueDetails;
    private OnColorChangedListener mOnColorChangedListener;
    ColorPickerAdvancedComponent mSaturationDetails;
    ColorPickerAdvancedComponent mValueDetails;

    public ColorPickerAdvanced(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurrentHsvValues = new float[3];
        init();
    }

    public ColorPickerAdvanced(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCurrentHsvValues = new float[3];
        init();
    }

    public ColorPickerAdvanced(Context context) {
        super(context);
        this.mCurrentHsvValues = new float[3];
        init();
    }

    private void init() {
        setOrientation(1);
        this.mHueDetails = createAndAddNewGradient(C0408R.string.color_picker_hue, HUE_SEEK_BAR_MAX, this);
        this.mSaturationDetails = createAndAddNewGradient(C0408R.string.color_picker_saturation, VALUE_SEEK_BAR_MAX, this);
        this.mValueDetails = createAndAddNewGradient(C0408R.string.color_picker_value, VALUE_SEEK_BAR_MAX, this);
        refreshGradientComponents();
    }

    public ColorPickerAdvancedComponent createAndAddNewGradient(int textResourceId, int seekBarMax, OnSeekBarChangeListener seekBarListener) {
        View newComponent = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C0408R.layout.color_picker_advanced_component, null);
        addView(newComponent);
        return new ColorPickerAdvancedComponent(newComponent, textResourceId, seekBarMax, seekBarListener);
    }

    public void setListener(OnColorChangedListener onColorChangedListener) {
        this.mOnColorChangedListener = onColorChangedListener;
    }

    public int getColor() {
        return this.mCurrentColor;
    }

    public void setColor(int color) {
        this.mCurrentColor = color;
        Color.colorToHSV(this.mCurrentColor, this.mCurrentHsvValues);
        refreshGradientComponents();
    }

    private void notifyColorChanged() {
        if (this.mOnColorChangedListener != null) {
            this.mOnColorChangedListener.onColorChanged(getColor());
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            this.mCurrentHsvValues[0] = this.mHueDetails.getValue();
            this.mCurrentHsvValues[1] = this.mSaturationDetails.getValue() / 100.0f;
            this.mCurrentHsvValues[VALUE_COLOR_COUNT] = this.mValueDetails.getValue() / 100.0f;
            this.mCurrentColor = Color.HSVToColor(this.mCurrentHsvValues);
            updateHueGradient();
            updateSaturationGradient();
            updateValueGradient();
            notifyColorChanged();
        }
    }

    private void updateHueGradient() {
        float[] tempHsvValues = new float[3];
        tempHsvValues[1] = this.mCurrentHsvValues[1];
        tempHsvValues[VALUE_COLOR_COUNT] = this.mCurrentHsvValues[VALUE_COLOR_COUNT];
        int[] newColors = new int[HUE_COLOR_COUNT];
        for (int i = 0; i < HUE_COLOR_COUNT; i++) {
            tempHsvValues[0] = ((float) i) * 60.0f;
            newColors[i] = Color.HSVToColor(tempHsvValues);
        }
        this.mHueDetails.setGradientColors(newColors);
    }

    private void updateSaturationGradient() {
        float[] tempHsvValues = new float[]{this.mCurrentHsvValues[0], 0.0f, this.mCurrentHsvValues[VALUE_COLOR_COUNT]};
        int[] newColors = new int[VALUE_COLOR_COUNT];
        newColors[0] = Color.HSVToColor(tempHsvValues);
        tempHsvValues[1] = 1.0f;
        newColors[1] = Color.HSVToColor(tempHsvValues);
        this.mSaturationDetails.setGradientColors(newColors);
    }

    private void updateValueGradient() {
        float[] tempHsvValues = new float[]{this.mCurrentHsvValues[0], this.mCurrentHsvValues[1], 0.0f};
        int[] newColors = new int[VALUE_COLOR_COUNT];
        newColors[0] = Color.HSVToColor(tempHsvValues);
        tempHsvValues[VALUE_COLOR_COUNT] = 1.0f;
        newColors[1] = Color.HSVToColor(tempHsvValues);
        this.mValueDetails.setGradientColors(newColors);
    }

    private void refreshGradientComponents() {
        int saturationValue = Math.max(Math.min(Math.round(this.mCurrentHsvValues[1] * 100.0f), VALUE_SEEK_BAR_MAX), 0);
        int valueValue = Math.max(Math.min(Math.round(this.mCurrentHsvValues[VALUE_COLOR_COUNT] * 100.0f), VALUE_SEEK_BAR_MAX), 0);
        this.mHueDetails.setValue(this.mCurrentHsvValues[0]);
        this.mSaturationDetails.setValue((float) saturationValue);
        this.mValueDetails.setValue((float) valueValue);
        updateHueGradient();
        updateSaturationGradient();
        updateValueGradient();
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
