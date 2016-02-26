package com.example.csy_personal;

/**
 * Created by 차승용 on 2016-02-18.
 */
public class CalendarT {
    int _id;
    String title;
    String contact;
    String date;
    int hour;
    int minute;
    String isam;
    String alarmYn;

    public CalendarT(int _id , String title , String contact , String date, int hour, int minute, String isam, String alarmYn){
        this._id = _id;
        this.title = title;
        this.contact = contact;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.isam = isam;
        this.alarmYn = alarmYn;
    }
}
