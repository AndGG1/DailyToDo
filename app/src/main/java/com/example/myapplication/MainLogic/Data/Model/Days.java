package com.example.myapplication.MainLogic.Data.Model;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

// Day navigation helper
public class Days {
    String yesterday, today, tomorrow;
    int currentDay = 2;
    public void calculateDays() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        today = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_WEEK, -1);
        yesterday = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, 2);
        tomorrow = dateFormat.format(calendar.getTime());
    }
    public String getYesterday() {
        currentDay = 1;
        return yesterday; }
    public String getToday() {
        currentDay = 2;
        return today; }
    public String getTomorrow() {
        currentDay = 3;
        return tomorrow; }

    public int getCurrentDay() {
        Log.d("Day", currentDay+"");
        return currentDay;
    }
}