package com.xiaoma.code;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @Author majing1in
 * @Date 2022/7/15 14:09:39
 */
public class DateUtil {

    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    private static final String FORMAT_DATE = "yyyy-MM-dd";

    public static final String FORMAT_TIME = "HH:mm:ss";

    private static final long DAY_MILLISECOND = 1000 * 60 * 60 * 24;

    public static Calendar getCalendar() {
        return Calendar.getInstance(Locale.CANADA);
    }

    public static Calendar getCalendar(Date dateTime) {
        if (dateTime == null) {
            return null;
        }
        Calendar calendar = getCalendar();
        calendar.setTime(dateTime);
        return calendar;
    }

    public static SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public static Calendar strToCalendar(String dateStr) {
        Date date = parseToDate(dateStr);
        return getCalendar(date);
    }

    /******************************************************************************************************************/

    public static String getCurrentYear() {
        Calendar calendar = getCalendar();
        int year = calendar.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public static String getCurrentMonth() {
        Calendar calendar = getCalendar();
        int month = calendar.get(Calendar.MONTH) + 1;
        return String.valueOf(month);
    }

    public static int getCurrentDayOfYear() {
        Calendar calendar = getCalendar();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static int getCurrentDayOfMonth() {
        Calendar calendar = getCalendar();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentDayOfWeek() {
        Calendar calendar = getCalendar();
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static String getCurrentTime() {
        Calendar calendar = getCalendar();
        Date time = calendar.getTime();
        return getSimpleDateFormat(FORMAT_TIME).format(time);
    }

    public static String getCurrentDate() {
        Calendar calendar = getCalendar();
        Date time = calendar.getTime();
        return getSimpleDateFormat(FORMAT_DATE).format(time);
    }

    public static String getCurrentDateTime() {
        Calendar calendar = getCalendar();
        Date time = calendar.getTime();
        return getSimpleDateFormat(FORMAT_DATE_TIME).format(time);
    }

    public static String getCurrentDateTime(String pattern) {
        Calendar calendar = getCalendar();
        Date time = calendar.getTime();
        return getSimpleDateFormat(pattern).format(time);
    }

    public static int getDayOfDate(String dateStr, int type) {
        Date date = parseToDate(dateStr);
        Calendar calendar = getCalendar(date);
        return calendar.get(type);
    }

    public static int geDayOfYear(String dateStr) {
        return getDayOfDate(dateStr, Calendar.DAY_OF_YEAR);
    }

    public static int geDayOfMonth(String dateStr) {
        return getDayOfDate(dateStr, Calendar.DAY_OF_MONTH);
    }

    public static int geDayOfWeek(String dateStr) {
        return getDayOfDate(dateStr, Calendar.DAY_OF_WEEK);
    }

    /******************************************************************************************************************/

    public static Date parseToDate(String dateStr, String pattern) {
        Date date = null;
        SimpleDateFormat format = getSimpleDateFormat(pattern);
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date parseToDate(String dateStr) {
        switch (dateStr.length()) {
            case 8:
                return parseToDate(dateStr, FORMAT_TIME);
            case 10:
                return parseToDate(dateStr, FORMAT_DATE);
            default:
                return parseToDate(dateStr, FORMAT_DATE_TIME);
        }
    }

    public static String dateToStr(Date date, int length) {
        SimpleDateFormat simpleDateFormat = null;
        switch (length) {
            case 8:
                simpleDateFormat = getSimpleDateFormat(FORMAT_TIME);
                break;
            case 10:
                simpleDateFormat = getSimpleDateFormat(FORMAT_DATE);
                break;
            default:
                simpleDateFormat = getSimpleDateFormat(FORMAT_DATE_TIME);
        }
        return simpleDateFormat.format(date);
    }

    /******************************************************************************************************************/

    public static Date getCalculateDate(Date source, int calculation, int amount) {
        Calendar calendar = getCalendar(source);
        calendar.add(calculation, amount);
        return calendar.getTime();
    }

    public static String addMinutes(String dateStr, int minutes) {
        Date date = parseToDate(dateStr);
        Date calculateDate = getCalculateDate(date, Calendar.MINUTE, minutes);
        return dateToStr(calculateDate, dateStr.length());
    }

    public static String addHours(String dateStr, int honurs) {
        Date date = parseToDate(dateStr);
        Date calculateDate = getCalculateDate(date, Calendar.HOUR, honurs);
        return dateToStr(calculateDate, dateStr.length());
    }

    public static String addDays(String dateStr, int days) {
        Date date = parseToDate(dateStr);
        Date calculateDate = getCalculateDate(date, Calendar.DATE, days);
        return dateToStr(calculateDate, dateStr.length());
    }

    public static String addMonths(String dateStr, int months) {
        Date date = parseToDate(dateStr);
        Date calculateDate = getCalculateDate(date, Calendar.MONTH, months);
        return dateToStr(calculateDate, dateStr.length());
    }

    public static String addYears(String dateStr, int years) {
        Date date = parseToDate(dateStr);
        Date calculateDate = getCalculateDate(date, Calendar.YEAR, years);
        return dateToStr(calculateDate, dateStr.length());
    }

    /******************************************************************************************************************/

    public static int getIntervalDays(String startDate, String endDate) {
        Calendar c1 = strToCalendar(startDate);
        Calendar c2 = strToCalendar(endDate);
        long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
        return (int) (diff / DAY_MILLISECOND);
    }

    public static double getIntervalMonths(String startDate, String endDate) {
        Calendar c1 = strToCalendar(startDate);
        Calendar c2 = strToCalendar(endDate);
        int y1 = c1.get(Calendar.YEAR);
        int y2 = c2.get(Calendar.YEAR);
        int years = y2 - y1;
        if (years < 0) {
            return 0.0;
        }
        int m1 = c1.get(Calendar.MONTH);
        int m2 = c2.get(Calendar.MONTH);
        if (years == 0) {
            return m2 - m1;
        }
        if (m1 > m2) {
            return years * 12 - (m1 - m2);
        } else {
            return years * 12 + (m2 - m1);
        }
    }

    public static double getIntervalYears(String startDate, String endDate) {
        Calendar c1 = strToCalendar(startDate);
        Calendar c2 = strToCalendar(endDate);
        int y1 = c1.get(Calendar.YEAR);
        int y2 = c2.get(Calendar.YEAR);
        return y2 - y1;
    }

    public static void main(String[] args) {
        String s1 = "2021-02-02 09:56:23";
        String s2 = "2021-07-07 14:56:23";
        double days1 = getIntervalMonths(s1, s2);
        int days2 = getIntervalDays(s1, s2);
        System.out.println(days1);
        System.out.println(days2);
    }
}
