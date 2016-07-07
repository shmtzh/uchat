package com.roket.shmtzh.uchat.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by shmtzh on 7/6/16.
 */
public class DateUtils {

    public static String getFormattedDate( long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "HH:mm";
        final String dateTimeFormatString = "EEEE, MMMM d, HH:mm";
        final long HOURS = 60 * 60 * 60;
        if(now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ){
            return "Сегодня, " + DateFormat.format(timeFormatString, smsTime);
        }else if(now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1 ){
            return "Вчера, " + DateFormat.format(timeFormatString, smsTime);
        }else if(now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)){
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        }else
            return DateFormat.format("MMMM dd yyyy, HH:mm", smsTime).toString();
    }




    public static long getCurentDate() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }




}
