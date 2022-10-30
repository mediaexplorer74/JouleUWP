package org.chromium.ui.picker;

import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import java.util.Calendar;
import java.util.TimeZone;

public class DateDialogNormalizer {
    private static void setLimits(DatePicker picker, long minMillis, long maxMillis) {
        if (maxMillis > minMillis) {
            Calendar minCal = trimToDate(minMillis);
            Calendar maxCal = trimToDate(maxMillis);
            int currentYear = picker.getYear();
            int currentMonth = picker.getMonth();
            int currentDayOfMonth = picker.getDayOfMonth();
            picker.updateDate(maxCal.get(1), maxCal.get(2), maxCal.get(5));
            picker.setMinDate(minCal.getTimeInMillis());
            picker.updateDate(minCal.get(1), minCal.get(2), minCal.get(5));
            picker.setMaxDate(maxCal.getTimeInMillis());
            picker.updateDate(currentYear, currentMonth, currentDayOfMonth);
        }
    }

    private static Calendar trimToDate(long time) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.clear();
        cal.setTimeInMillis(time);
        Calendar result = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        result.clear();
        result.set(cal.get(1), cal.get(2), cal.get(5), 0, 0, 0);
        return result;
    }

    public static void normalize(DatePicker picker, OnDateChangedListener listener, int year, int month, int day, int hour, int minute, long minMillis, long maxMillis) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.clear();
        calendar.set(year, month, day, hour, minute, 0);
        if (calendar.getTimeInMillis() < minMillis) {
            calendar.clear();
            calendar.setTimeInMillis(minMillis);
        } else if (calendar.getTimeInMillis() > maxMillis) {
            calendar.clear();
            calendar.setTimeInMillis(maxMillis);
        }
        picker.init(calendar.get(1), calendar.get(2), calendar.get(5), listener);
        setLimits(picker, minMillis, maxMillis);
    }
}
