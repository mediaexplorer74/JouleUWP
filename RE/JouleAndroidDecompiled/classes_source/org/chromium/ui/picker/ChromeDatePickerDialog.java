package org.chromium.ui.picker;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.DatePicker;

class ChromeDatePickerDialog extends DatePickerDialog {
    private final OnDateSetListener mCallBack;

    public ChromeDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, 0, callBack, year, monthOfYear, dayOfMonth);
        this.mCallBack = callBack;
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which == -1 && this.mCallBack != null) {
            DatePicker datePicker = getDatePicker();
            datePicker.clearFocus();
            this.mCallBack.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        }
    }
}
