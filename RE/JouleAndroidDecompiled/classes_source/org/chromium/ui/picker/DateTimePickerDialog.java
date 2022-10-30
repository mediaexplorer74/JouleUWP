package org.chromium.ui.picker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.chromium.ui.C0408R;

public class DateTimePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener, OnTimeChangedListener {
    private final OnDateTimeSetListener mCallBack;
    private final DatePicker mDatePicker;
    private final long mMaxTimeMillis;
    private final long mMinTimeMillis;
    private final TimePicker mTimePicker;

    public interface OnDateTimeSetListener {
        void onDateTimeSet(DatePicker datePicker, TimePicker timePicker, int i, int i2, int i3, int i4, int i5);
    }

    public DateTimePickerDialog(Context context, OnDateTimeSetListener callBack, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute, boolean is24HourView, double min, double max) {
        super(context, 0);
        this.mMinTimeMillis = (long) min;
        this.mMaxTimeMillis = (long) max;
        this.mCallBack = callBack;
        setButton(-1, context.getText(C0408R.string.date_picker_dialog_set), this);
        setButton(-2, context.getText(17039360), (OnClickListener) null);
        setIcon(0);
        setTitle(context.getText(C0408R.string.date_time_picker_dialog_title));
        View view = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(C0408R.layout.date_time_picker_dialog, null);
        setView(view);
        this.mDatePicker = (DatePicker) view.findViewById(C0408R.id.date_picker);
        DateDialogNormalizer.normalize(this.mDatePicker, this, year, monthOfYear, dayOfMonth, hourOfDay, minute, this.mMinTimeMillis, this.mMaxTimeMillis);
        this.mTimePicker = (TimePicker) view.findViewById(C0408R.id.time_picker);
        this.mTimePicker.setIs24HourView(Boolean.valueOf(is24HourView));
        this.mTimePicker.setCurrentHour(Integer.valueOf(hourOfDay));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(minute));
        this.mTimePicker.setOnTimeChangedListener(this);
        onTimeChanged(this.mTimePicker, this.mTimePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue());
    }

    public void onClick(DialogInterface dialog, int which) {
        tryNotifyDateTimeSet();
    }

    private void tryNotifyDateTimeSet() {
        if (this.mCallBack != null) {
            this.mDatePicker.clearFocus();
            this.mTimePicker.clearFocus();
            this.mCallBack.onDateTimeSet(this.mDatePicker, this.mTimePicker, this.mDatePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth(), this.mTimePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue());
        }
    }

    public void onDateChanged(DatePicker view, int year, int month, int day) {
        if (this.mTimePicker != null) {
            onTimeChanged(this.mTimePicker, this.mTimePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue());
        }
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = new GregorianCalendar(this.mDatePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth(), this.mTimePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue(), 0);
        if (calendar.getTimeInMillis() < this.mMinTimeMillis) {
            calendar.setTimeInMillis(this.mMinTimeMillis);
        } else if (calendar.getTimeInMillis() > this.mMaxTimeMillis) {
            calendar.setTimeInMillis(this.mMaxTimeMillis);
        }
        this.mTimePicker.setCurrentHour(Integer.valueOf(calendar.get(10)));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(calendar.get(12)));
    }

    public void updateDateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minutOfHour) {
        this.mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
        this.mTimePicker.setCurrentHour(Integer.valueOf(hourOfDay));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(minutOfHour));
    }
}
