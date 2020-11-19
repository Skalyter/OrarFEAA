package com.tiberiugaspar.oraruaic.util;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final String TAG = "OrarFEAA";

    public static String getStringDateFromCalendar(Calendar calendar) {
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy");
        String dateInString;

        try {
            dateInString = formatter.format(date);

        } catch (Exception e) {
            Date date1 = new Date();
            dateInString = formatter.format(date1);
        }
        return dateInString;
    }

    public static String getDataAfisare(Calendar calendar) {
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMM");
        return formatter.format(date);
    }

    public static String getSaptamanaAfisare(Calendar calendar) {
        Date date1 = getTimestampLuni(calendar).toDate();
        Date date2 = getTimestampDuminica(calendar).toDate();
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd MMM");
        if (getCalendarLuni(calendar).get(Calendar.MONTH) ==
                getCalendarDuminica(calendar).get(Calendar.MONTH)){
            return formatter1.format(date1) + " - " + formatter2.format(date2);
        } else {
            return formatter2.format(date1) + " - " + formatter2.format(date2);
        }
    }

    public static String getStringTimeFromCalendar(Calendar calendar) {
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String stringTime;
        try {
            stringTime = formatter.format(date);
        } catch (Exception e) {
            stringTime = "Time format doesn't match";
        }
        return stringTime;
    }

    public static String getZiuaFromCalendar(Calendar calendar) {
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        return formatter.format(date);
    }

    public static int getAnPromotie(int anStudii) {
        if (Calendar.getInstance().get(Calendar.MONTH) < 7) {
            return Calendar.getInstance().get(Calendar.YEAR) + 3 - anStudii;
        } else {
            return Calendar.getInstance().get(Calendar.YEAR) + 3 - anStudii + 1;
        }
    }

    public static Timestamp getTimestamp(Calendar calendar) {
        Log.d(TAG, "getTimestamp: " + getStringDateFromCalendar(calendar) + " " + getStringTimeFromCalendar(calendar));
        return new Timestamp(calendar.getTime());
    }

    public static Calendar getCalendar(Timestamp timestamp) {
        long time = timestamp.getSeconds() * 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Log.d(TAG, "getCalendar: " + getStringDateFromCalendar(calendar) + " " + getStringTimeFromCalendar(calendar));
        return calendar;
    }

    public static Timestamp getCalendarDimineata(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        Log.d(TAG, "getCalendarDimineata: " + getStringDateFromCalendar(calendar) + " " + getStringTimeFromCalendar(calendar));
        return getTimestamp(calendar);
    }

    public static Timestamp getCalendarSeara(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        Log.d(TAG, "getCalendarSeara: " + getStringDateFromCalendar(calendar) + " " + getStringTimeFromCalendar(calendar));
        return getTimestamp(calendar);
    }

    public static Timestamp getTimestampLuni(Calendar calendar) {
        return getTimestamp(getCalendarLuni(calendar));
    }

    public static Timestamp getTimestampDuminica(Calendar calendar) {
        return getTimestamp(getCalendarDuminica(calendar));
    }

    public static Calendar getCalendarLuni(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        return calendar;
    }

    public static Calendar getCalendarDuminica(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        return calendar;
    }

    public static Timestamp getTimestampOraCurenta(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.get(Calendar.HOUR_OF_DAY) % 2 != 0){
            calendar.add(Calendar.HOUR_OF_DAY, -1);
        }
        return getTimestamp(calendar);
    }
    public static Timestamp getTimestampOraCurenta(Calendar calendar){
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.get(Calendar.HOUR_OF_DAY) % 2 != 0){
            calendar.add(Calendar.HOUR_OF_DAY, -1);
        }
        return getTimestamp(calendar);
    }
    public static Calendar getCalendarOraCurenta(Calendar calendar){
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.get(Calendar.HOUR_OF_DAY) % 2 != 0){
            calendar.add(Calendar.HOUR_OF_DAY, -1);
        }
        return calendar;
    }

    public static int getZiuaSaptamanii(int pozitieSpinner) {
        switch (pozitieSpinner) {
            case 0:
                return Calendar.MONDAY;
            case 1:
                return Calendar.TUESDAY;
            case 2:
                return Calendar.WEDNESDAY;
            case 3:
                return Calendar.THURSDAY;
            case 4:
                return Calendar.FRIDAY;
            case 5:
                return Calendar.SATURDAY;
            default:
                return Calendar.SUNDAY;
        }
    }

    public static int getHourFromPosition(int pozitieSpinner) {
        switch (pozitieSpinner) {
            case 0:
                return 8;
            case 1:
                return 10;
            case 2:
                return 12;
            case 3:
                return 14;
            case 4:
                return 16;
            case 5:
                return 18;
            default:
                return -1;
        }
    }

    public static int getPozitieIntervalOrarSpinner(Calendar calendar){
        switch (calendar.get(Calendar.HOUR_OF_DAY)){
            case 10:
                return 1;
            case 12:
                return 2;
            case 14:
                return 3;
            case 16:
                return 4;
            case 18:
                return 5;
            case 8:
            default:
                return 0;
        }
    }

    public static String getDataAnunt(Calendar calendar){
        return String.format("%s | %s",
                DateUtil.getStringDateFromCalendar(calendar),
                DateUtil.getStringTimeFromCalendar(calendar));
    }
}
