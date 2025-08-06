package com.example.myapplication.main_activity_usages.time_class;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

// Day navigation helper
public class Days {
    String yesterday, today, tomorrow;
    public void calculateDays() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        today = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_WEEK, -1);
        yesterday = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, 2);
        tomorrow = dateFormat.format(calendar.getTime());
    }
    public String getYesterday() { return yesterday; }
    public String getToday() { return today; }
    public String getTomorrow() { return tomorrow; }
}