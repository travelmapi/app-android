package com.travelmapi.app.travelmapi_app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sam on 5/10/16.
 */
public class DateHandler {

    private Date mDate;
    private Calendar mCalendar;
    public static final String DATE_FORMAT = "yyyy-MM-dd kk:mm:ss";
    public DateHandler(Date date){
        mDate = date;
        mCalendar  = Calendar.getInstance();
        mCalendar.setTime(mDate);
    }

    public String toString(){
        DateFormat formater = new SimpleDateFormat(DATE_FORMAT);
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));
        String result = formater.format(mDate);
        int offset = TimeZone.getDefault().getOffset(Calendar.getInstance().getTimeInMillis())/(1000*60*60);
        if(offset > 0){
            result = result + " GMT +" + String.format("%02d", offset)+":00";
        }else{
            result = result + " GMT " + String.format("%03d", offset)+":00";
        }
        return result;
    }

    public void setDate(Date date){
        mDate = date;
        mCalendar  = Calendar.getInstance();
        mCalendar.setTime(mDate);
    }

    public int getDay(){
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth(){
        return mCalendar.get(Calendar.MONTH);
    }

    public int getYear(){
        return mCalendar.get(Calendar.YEAR);
    }

    public int getHour(){
        return mCalendar.get(Calendar.HOUR);
    }
    public int getMinute(){
        return mCalendar.get(Calendar.MINUTE);
    }
    public int getSecond(){
        return mCalendar.get(Calendar.SECOND);
    }
}