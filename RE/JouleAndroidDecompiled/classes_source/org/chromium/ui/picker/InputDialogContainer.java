package org.chromium.ui.picker;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.chromium.ui.C0408R;
import org.chromium.ui.picker.DateTimePickerDialog.OnDateTimeSetListener;
import org.chromium.ui.picker.MultiFieldTimePickerDialog.OnMultiFieldTimeSetListener;
import org.chromium.ui.picker.TwoFieldDatePickerDialog.OnValueSetListener;

public class InputDialogContainer {
    private final Context mContext;
    private AlertDialog mDialog;
    private boolean mDialogAlreadyDismissed;
    private final InputActionDelegate mInputActionDelegate;

    /* renamed from: org.chromium.ui.picker.InputDialogContainer.1 */
    class C04191 implements OnItemClickListener {
        final /* synthetic */ DateTimeSuggestionListAdapter val$adapter;
        final /* synthetic */ int val$dialogType;
        final /* synthetic */ double val$dialogValue;
        final /* synthetic */ double val$max;
        final /* synthetic */ double val$min;
        final /* synthetic */ double val$step;

        C04191(DateTimeSuggestionListAdapter dateTimeSuggestionListAdapter, int i, double d, double d2, double d3, double d4) {
            this.val$adapter = dateTimeSuggestionListAdapter;
            this.val$dialogType = i;
            this.val$dialogValue = d;
            this.val$min = d2;
            this.val$max = d3;
            this.val$step = d4;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (position == this.val$adapter.getCount() - 1) {
                InputDialogContainer.this.dismissDialog();
                InputDialogContainer.this.showPickerDialog(this.val$dialogType, this.val$dialogValue, this.val$min, this.val$max, this.val$step);
                return;
            }
            InputDialogContainer.this.mInputActionDelegate.replaceDateTime(((DateTimeSuggestion) this.val$adapter.getItem(position)).value());
            InputDialogContainer.this.dismissDialog();
            InputDialogContainer.this.mDialogAlreadyDismissed = true;
        }
    }

    /* renamed from: org.chromium.ui.picker.InputDialogContainer.2 */
    class C04202 implements OnClickListener {
        C04202() {
        }

        public void onClick(DialogInterface dialog, int which) {
            InputDialogContainer.this.dismissDialog();
        }
    }

    /* renamed from: org.chromium.ui.picker.InputDialogContainer.3 */
    class C04213 implements OnDismissListener {
        C04213() {
        }

        public void onDismiss(DialogInterface dialog) {
            if (InputDialogContainer.this.mDialog == dialog && !InputDialogContainer.this.mDialogAlreadyDismissed) {
                InputDialogContainer.this.mDialogAlreadyDismissed = true;
                InputDialogContainer.this.mInputActionDelegate.cancelDateTimeDialog();
            }
        }
    }

    /* renamed from: org.chromium.ui.picker.InputDialogContainer.4 */
    class C04224 implements OnClickListener {
        C04224() {
        }

        public void onClick(DialogInterface dialog, int which) {
            InputDialogContainer.this.mDialogAlreadyDismissed = true;
            InputDialogContainer.this.mInputActionDelegate.replaceDateTime(Double.NaN);
        }
    }

    /* renamed from: org.chromium.ui.picker.InputDialogContainer.5 */
    class C04235 implements OnDismissListener {
        C04235() {
        }

        public void onDismiss(DialogInterface dialog) {
            if (!InputDialogContainer.this.mDialogAlreadyDismissed) {
                InputDialogContainer.this.mDialogAlreadyDismissed = true;
                InputDialogContainer.this.mInputActionDelegate.cancelDateTimeDialog();
            }
        }
    }

    private class DateListener implements OnDateSetListener {
        private final int mDialogType;

        DateListener(int dialogType) {
            this.mDialogType = dialogType;
        }

        public void onDateSet(DatePicker view, int year, int month, int monthDay) {
            InputDialogContainer.this.setFieldDateTimeValue(this.mDialogType, year, month, monthDay, 0, 0, 0, 0, 0);
        }
    }

    public interface InputActionDelegate {
        void cancelDateTimeDialog();

        void replaceDateTime(double d);
    }

    private class DateTimeListener implements OnDateTimeSetListener {
        private final int mDialogType;
        private final boolean mLocal;

        public DateTimeListener(int dialogType) {
            this.mLocal = dialogType == 10;
            this.mDialogType = dialogType;
        }

        public void onDateTimeSet(DatePicker dateView, TimePicker timeView, int year, int month, int monthDay, int hourOfDay, int minute) {
            InputDialogContainer.this.setFieldDateTimeValue(this.mDialogType, year, month, monthDay, hourOfDay, minute, 0, 0, 0);
        }
    }

    private class FullTimeListener implements OnMultiFieldTimeSetListener {
        private final int mDialogType;

        FullTimeListener(int dialogType) {
            this.mDialogType = dialogType;
        }

        public void onTimeSet(int hourOfDay, int minute, int second, int milli) {
            InputDialogContainer.this.setFieldDateTimeValue(this.mDialogType, 0, 0, 0, hourOfDay, minute, second, milli, 0);
        }
    }

    private class MonthOrWeekListener implements OnValueSetListener {
        private final int mDialogType;

        MonthOrWeekListener(int dialogType) {
            this.mDialogType = dialogType;
        }

        public void onValueSet(int year, int positionInYear) {
            if (this.mDialogType == 11) {
                InputDialogContainer.this.setFieldDateTimeValue(this.mDialogType, year, positionInYear, 0, 0, 0, 0, 0, 0);
            } else {
                InputDialogContainer.this.setFieldDateTimeValue(this.mDialogType, year, 0, 0, 0, 0, 0, 0, positionInYear);
            }
        }
    }

    public static boolean isDialogInputType(int type) {
        return type == 8 || type == 12 || type == 9 || type == 10 || type == 11 || type == 13;
    }

    public InputDialogContainer(Context context, InputActionDelegate inputActionDelegate) {
        this.mContext = context;
        this.mInputActionDelegate = inputActionDelegate;
    }

    public void showPickerDialog(int dialogType, double dialogValue, double min, double max, double step) {
        Calendar cal;
        if (Double.isNaN(dialogValue)) {
            cal = Calendar.getInstance();
            cal.set(14, 0);
        } else if (dialogType == 11) {
            cal = MonthPicker.createDateFromValue(dialogValue);
        } else if (dialogType == 13) {
            cal = WeekPicker.createDateFromValue(dialogValue);
        } else {
            Calendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            gregorianCalendar.setGregorianChange(new Date(Long.MIN_VALUE));
            gregorianCalendar.setTimeInMillis((long) dialogValue);
            cal = gregorianCalendar;
        }
        if (dialogType == 8) {
            showPickerDialog(dialogType, cal.get(1), cal.get(2), cal.get(5), 0, 0, 0, 0, 0, min, max, step);
        } else if (dialogType == 12) {
            showPickerDialog(dialogType, 0, 0, 0, cal.get(11), cal.get(12), 0, 0, 0, min, max, step);
        } else if (dialogType == 9 || dialogType == 10) {
            showPickerDialog(dialogType, cal.get(1), cal.get(2), cal.get(5), cal.get(11), cal.get(12), cal.get(13), cal.get(14), 0, min, max, step);
        } else if (dialogType == 11) {
            showPickerDialog(dialogType, cal.get(1), cal.get(2), 0, 0, 0, 0, 0, 0, min, max, step);
        } else if (dialogType == 13) {
            showPickerDialog(dialogType, WeekPicker.getISOWeekYearForDate(cal), 0, 0, 0, 0, 0, 0, WeekPicker.getWeekForDate(cal), min, max, step);
        }
    }

    void showSuggestionDialog(int dialogType, double dialogValue, double min, double max, double step, DateTimeSuggestion[] suggestions) {
        ListView suggestionListView = new ListView(this.mContext);
        DateTimeSuggestionListAdapter adapter = new DateTimeSuggestionListAdapter(this.mContext, Arrays.asList(suggestions));
        suggestionListView.setAdapter(adapter);
        suggestionListView.setOnItemClickListener(new C04191(adapter, dialogType, dialogValue, min, max, step));
        int dialogTitleId = C0408R.string.date_picker_dialog_title;
        if (dialogType == 12) {
            dialogTitleId = C0408R.string.time_picker_dialog_title;
        } else if (dialogType == 9 || dialogType == 10) {
            dialogTitleId = C0408R.string.date_time_picker_dialog_title;
        } else if (dialogType == 11) {
            dialogTitleId = C0408R.string.month_picker_dialog_title;
        } else if (dialogType == 13) {
            dialogTitleId = C0408R.string.week_picker_dialog_title;
        }
        this.mDialog = new Builder(this.mContext).setTitle(dialogTitleId).setView(suggestionListView).setNegativeButton(this.mContext.getText(17039360), new C04202()).create();
        this.mDialog.setOnDismissListener(new C04213());
        this.mDialogAlreadyDismissed = false;
        this.mDialog.show();
    }

    public void showDialog(int type, double value, double min, double max, double step, DateTimeSuggestion[] suggestions) {
        dismissDialog();
        if (suggestions == null) {
            showPickerDialog(type, value, min, max, step);
        } else {
            showSuggestionDialog(type, value, min, max, step, suggestions);
        }
    }

    protected void showPickerDialog(int dialogType, int year, int month, int monthDay, int hourOfDay, int minute, int second, int millis, int week, double min, double max, double step) {
        if (isDialogShowing()) {
            this.mDialog.dismiss();
        }
        int stepTime = (int) step;
        if (dialogType == 8) {
            ChromeDatePickerDialog dialog = new ChromeDatePickerDialog(this.mContext, new DateListener(dialogType), year, month, monthDay);
            DateDialogNormalizer.normalize(dialog.getDatePicker(), dialog, year, month, monthDay, 0, 0, (long) min, (long) max);
            dialog.setTitle(this.mContext.getText(C0408R.string.date_picker_dialog_title));
            this.mDialog = dialog;
        } else if (dialogType == 12) {
            this.mDialog = new MultiFieldTimePickerDialog(this.mContext, 0, hourOfDay, minute, second, millis, (int) min, (int) max, stepTime, DateFormat.is24HourFormat(this.mContext), new FullTimeListener(dialogType));
        } else if (dialogType == 9 || dialogType == 10) {
            this.mDialog = new DateTimePickerDialog(this.mContext, new DateTimeListener(dialogType), year, month, monthDay, hourOfDay, minute, DateFormat.is24HourFormat(this.mContext), min, max);
        } else if (dialogType == 11) {
            this.mDialog = new MonthPickerDialog(this.mContext, new MonthOrWeekListener(dialogType), year, month, min, max);
        } else if (dialogType == 13) {
            this.mDialog = new WeekPickerDialog(this.mContext, new MonthOrWeekListener(dialogType), year, week, min, max);
        }
        this.mDialog.setButton(-1, this.mContext.getText(C0408R.string.date_picker_dialog_set), (OnClickListener) this.mDialog);
        this.mDialog.setButton(-2, this.mContext.getText(17039360), (OnClickListener) null);
        this.mDialog.setButton(-3, this.mContext.getText(C0408R.string.date_picker_dialog_clear), new C04224());
        this.mDialog.setOnDismissListener(new C04235());
        this.mDialogAlreadyDismissed = false;
        this.mDialog.show();
    }

    boolean isDialogShowing() {
        return this.mDialog != null && this.mDialog.isShowing();
    }

    void dismissDialog() {
        if (isDialogShowing()) {
            this.mDialog.dismiss();
        }
    }

    protected void setFieldDateTimeValue(int dialogType, int year, int month, int monthDay, int hourOfDay, int minute, int second, int millis, int week) {
        if (!this.mDialogAlreadyDismissed) {
            this.mDialogAlreadyDismissed = true;
            if (dialogType == 11) {
                this.mInputActionDelegate.replaceDateTime((double) (((year - 1970) * 12) + month));
            } else if (dialogType == 13) {
                this.mInputActionDelegate.replaceDateTime((double) WeekPicker.createDateFromWeek(year, week).getTimeInMillis());
            } else if (dialogType == 12) {
                this.mInputActionDelegate.replaceDateTime((double) (((TimeUnit.HOURS.toMillis((long) hourOfDay) + TimeUnit.MINUTES.toMillis((long) minute)) + TimeUnit.SECONDS.toMillis((long) second)) + ((long) millis)));
            } else {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.clear();
                cal.set(1, year);
                cal.set(2, month);
                cal.set(5, monthDay);
                cal.set(11, hourOfDay);
                cal.set(12, minute);
                cal.set(13, second);
                cal.set(14, millis);
                this.mInputActionDelegate.replaceDateTime((double) cal.getTimeInMillis());
            }
        }
    }
}
