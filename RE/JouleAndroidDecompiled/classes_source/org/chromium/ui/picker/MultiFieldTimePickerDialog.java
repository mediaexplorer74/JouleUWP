package org.chromium.ui.picker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import java.util.ArrayList;
import org.chromium.ui.C0408R;

public class MultiFieldTimePickerDialog extends AlertDialog implements OnClickListener {
    private static final int HOUR_IN_MILLIS = 3600000;
    private static final int MINUTE_IN_MILLIS = 60000;
    private static final int SECOND_IN_MILLIS = 1000;
    private final NumberPicker mAmPmSpinner;
    private final int mBaseMilli;
    private final NumberPicker mHourSpinner;
    private final boolean mIs24hourFormat;
    private final OnMultiFieldTimeSetListener mListener;
    private final NumberPicker mMilliSpinner;
    private final NumberPicker mMinuteSpinner;
    private final NumberPicker mSecSpinner;
    private final int mStep;

    private static class NumberFormatter implements Formatter {
        private final String mFormat;

        NumberFormatter(String format) {
            this.mFormat = format;
        }

        public String format(int value) {
            return String.format(this.mFormat, new Object[]{Integer.valueOf(value)});
        }
    }

    public interface OnMultiFieldTimeSetListener {
        void onTimeSet(int i, int i2, int i3, int i4);
    }

    public MultiFieldTimePickerDialog(Context context, int theme, int hour, int minute, int second, int milli, int min, int max, int step, boolean is24hourFormat, OnMultiFieldTimeSetListener listener) {
        super(context, theme);
        this.mListener = listener;
        this.mStep = step;
        this.mIs24hourFormat = is24hourFormat;
        if (min >= max) {
            min = 0;
            max = 86399999;
        }
        if (step < 0 || step >= 86400000) {
            step = MINUTE_IN_MILLIS;
        }
        int i = C0408R.layout.multi_field_time_picker_dialog;
        View view = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(r16, null);
        setView(view);
        this.mHourSpinner = (NumberPicker) view.findViewById(C0408R.id.hour);
        this.mMinuteSpinner = (NumberPicker) view.findViewById(C0408R.id.minute);
        this.mSecSpinner = (NumberPicker) view.findViewById(C0408R.id.second);
        this.mMilliSpinner = (NumberPicker) view.findViewById(C0408R.id.milli);
        this.mAmPmSpinner = (NumberPicker) view.findViewById(C0408R.id.ampm);
        int minHour = min / HOUR_IN_MILLIS;
        int maxHour = max / HOUR_IN_MILLIS;
        min -= HOUR_IN_MILLIS * minHour;
        max -= HOUR_IN_MILLIS * maxHour;
        if (minHour == maxHour) {
            this.mHourSpinner.setEnabled(false);
            hour = minHour;
        }
        if (is24hourFormat) {
            this.mAmPmSpinner.setVisibility(8);
        } else {
            int minAmPm = minHour / 12;
            int maxAmPm = maxHour / 12;
            int amPm = hour / 12;
            this.mAmPmSpinner.setMinValue(minAmPm);
            this.mAmPmSpinner.setMaxValue(maxAmPm);
            NumberPicker numberPicker = this.mAmPmSpinner;
            r17 = new String[2];
            r17[0] = context.getString(C0408R.string.time_picker_dialog_am);
            r17[1] = context.getString(C0408R.string.time_picker_dialog_pm);
            numberPicker.setDisplayedValues(r17);
            hour %= 12;
            if (hour == 0) {
                hour = 12;
            }
            if (minAmPm == maxAmPm) {
                this.mAmPmSpinner.setEnabled(false);
                amPm = minAmPm;
                minHour %= 12;
                maxHour %= 12;
                if (minHour == 0 && maxHour == 0) {
                    minHour = 12;
                    maxHour = 12;
                } else if (minHour == 0) {
                    minHour = maxHour;
                    maxHour = 12;
                } else if (maxHour == 0) {
                    maxHour = 12;
                }
            } else {
                minHour = 1;
                maxHour = 12;
            }
            this.mAmPmSpinner.setValue(amPm);
        }
        if (minHour == maxHour) {
            this.mHourSpinner.setEnabled(false);
        }
        this.mHourSpinner.setMinValue(minHour);
        this.mHourSpinner.setMaxValue(maxHour);
        this.mHourSpinner.setValue(hour);
        NumberFormatter twoDigitPaddingFormatter = new NumberFormatter("%02d");
        int minMinute = min / MINUTE_IN_MILLIS;
        int maxMinute = max / MINUTE_IN_MILLIS;
        min -= MINUTE_IN_MILLIS * minMinute;
        max -= MINUTE_IN_MILLIS * maxMinute;
        if (minHour == maxHour) {
            this.mMinuteSpinner.setMinValue(minMinute);
            this.mMinuteSpinner.setMaxValue(maxMinute);
            if (minMinute == maxMinute) {
                this.mMinuteSpinner.setDisplayedValues(new String[]{twoDigitPaddingFormatter.format(minMinute)});
                this.mMinuteSpinner.setEnabled(false);
                minute = minMinute;
            }
        } else {
            this.mMinuteSpinner.setMinValue(0);
            this.mMinuteSpinner.setMaxValue(59);
        }
        this.mMinuteSpinner.setValue(minute);
        if (step % HOUR_IN_MILLIS == 0) {
            this.mMinuteSpinner.setEnabled(false);
            this.mMinuteSpinner.setValue(minMinute);
        }
        this.mMinuteSpinner.setFormatter(twoDigitPaddingFormatter);
        if (step >= MINUTE_IN_MILLIS) {
            view.findViewById(C0408R.id.second_colon).setVisibility(8);
            this.mSecSpinner.setVisibility(8);
        }
        int minSecond = min / SECOND_IN_MILLIS;
        int maxSecond = max / SECOND_IN_MILLIS;
        min -= minSecond * SECOND_IN_MILLIS;
        max -= maxSecond * SECOND_IN_MILLIS;
        if (minHour == maxHour && minMinute == maxMinute) {
            this.mSecSpinner.setMinValue(minSecond);
            this.mSecSpinner.setMaxValue(maxSecond);
            if (minSecond == maxSecond) {
                this.mSecSpinner.setDisplayedValues(new String[]{twoDigitPaddingFormatter.format(minSecond)});
                this.mSecSpinner.setEnabled(false);
                second = minSecond;
            }
        } else {
            this.mSecSpinner.setMinValue(0);
            this.mSecSpinner.setMaxValue(59);
        }
        this.mSecSpinner.setValue(second);
        this.mSecSpinner.setFormatter(twoDigitPaddingFormatter);
        if (step >= SECOND_IN_MILLIS) {
            view.findViewById(C0408R.id.second_dot).setVisibility(8);
            this.mMilliSpinner.setVisibility(8);
        }
        milli = (((step / 2) + milli) / step) * step;
        if (step == 1 || step == 10 || step == 100) {
            if (minHour == maxHour && minMinute == maxMinute && minSecond == maxSecond) {
                this.mMilliSpinner.setMinValue(min / step);
                this.mMilliSpinner.setMaxValue(max / step);
                if (min == max) {
                    this.mMilliSpinner.setEnabled(false);
                    milli = min;
                }
            } else {
                this.mMilliSpinner.setMinValue(0);
                this.mMilliSpinner.setMaxValue(999 / step);
            }
            if (step == 1) {
                this.mMilliSpinner.setFormatter(new NumberFormatter("%03d"));
            } else if (step == 10) {
                this.mMilliSpinner.setFormatter(new NumberFormatter("%02d"));
            } else if (step == 100) {
                this.mMilliSpinner.setFormatter(new NumberFormatter("%d"));
            }
            this.mMilliSpinner.setValue(milli / step);
            this.mBaseMilli = 0;
        } else if (step < SECOND_IN_MILLIS) {
            ArrayList<String> strValue = new ArrayList();
            for (int i2 = min; i2 < max; i2 += step) {
                strValue.add(String.format("%03d", new Object[]{Integer.valueOf(i2)}));
            }
            this.mMilliSpinner.setMinValue(0);
            this.mMilliSpinner.setMaxValue(strValue.size() - 1);
            this.mMilliSpinner.setValue((milli - min) / step);
            this.mMilliSpinner.setDisplayedValues((String[]) strValue.toArray(new String[strValue.size()]));
            this.mBaseMilli = min;
        } else {
            this.mBaseMilli = 0;
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        notifyDateSet();
    }

    private void notifyDateSet() {
        int hour = getPickerValue(this.mHourSpinner);
        int minute = getPickerValue(this.mMinuteSpinner);
        int sec = getPickerValue(this.mSecSpinner);
        int milli = (getPickerValue(this.mMilliSpinner) * this.mStep) + this.mBaseMilli;
        if (!this.mIs24hourFormat) {
            int ampm = getPickerValue(this.mAmPmSpinner);
            if (hour == 12) {
                hour = 0;
            }
            hour += ampm * 12;
        }
        this.mListener.onTimeSet(hour, minute, sec, milli);
    }

    private int getPickerValue(NumberPicker picker) {
        picker.clearFocus();
        return picker.getValue();
    }
}
