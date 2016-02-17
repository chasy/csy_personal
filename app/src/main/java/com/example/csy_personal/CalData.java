package com.example.csy_personal;

/**
 * Created by 차승용 on 2016-02-16.
 */
public class CalData {
    int day;
    int dayofweek;

    int year;
    int month;

    public CalData(int d, int h) {
        day = d;
        dayofweek = h;
    }

    public int getDay() {
        return day;
    }

    public int getDayofweek() {
        return dayofweek;
    }
}
