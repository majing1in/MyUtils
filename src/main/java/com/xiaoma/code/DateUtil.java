package com.xiaoma.code;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private static final long DAY_MILLISECONDS = 1000 * 60 * 60 * 24;

    public static Calendar getCalendar() {
        return Calendar.getInstance(Locale.CANADA);
    }

    /******************************************************************************************************************/

    public static Calendar getCalendar(Date dateTime) {
        if (dateTime == null) {
            return null;
        }
        Calendar calendar = getCalendar();
        calendar.setTime(dateTime);
        return calendar;
    }

    public static Calendar strToCalendar(String dateStr) {
        Date date = parseToDate(dateStr);
        return getCalendar(date);
    }

    public static SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    private static void AssertParameter(Calendar c1, Calendar c2) {
        if (c1.compareTo(c2) > 0) {
            throw new IllegalArgumentException("开始日期大于结束日期,参数错误!");
        }
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
            case 19:
                simpleDateFormat = getSimpleDateFormat(FORMAT_DATE_TIME);
                break;
            default:
                throw new IllegalArgumentException("日期字符串错误! date = " + date + "\t length = " + length);
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

    private static double calculate(long diff) {
        BigDecimal decimal1 = new BigDecimal(diff);
        BigDecimal decimal2 = new BigDecimal(DAY_MILLISECONDS);
        BigDecimal divide = decimal1.divide(decimal2, 2, RoundingMode.HALF_UP);
        return divide.doubleValue();
    }

    public static double getIntervalHours(String startDate, String endDate) {
        Calendar c1 = strToCalendar(startDate);
        Calendar c2 = strToCalendar(endDate);
        AssertParameter(c1, c2);
        long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
        return calculate(diff) * 24;
    }

    public static double getIntervalDays(String startDate, String endDate) {
        Calendar c1 = strToCalendar(startDate);
        Calendar c2 = strToCalendar(endDate);
        AssertParameter(c1, c2);
        long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
        return calculate(diff);
    }

    public static double getIntervalMonths(String startDate, String endDate) {
        Calendar c1 = strToCalendar(startDate);
        Calendar c2 = strToCalendar(endDate);
        AssertParameter(c1, c2);
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
        double months;
        if (m1 > m2) {
            months =  years * 12 - (m1 - m2);
        } else {
            months =  years * 12 + (m2 - m1);
        }
        return months;
    }

    public static double getIntervalYears(String startDate, String endDate) {
        Calendar c1 = strToCalendar(startDate);
        Calendar c2 = strToCalendar(endDate);
        AssertParameter(c1, c2);
        int y1 = c1.get(Calendar.YEAR);
        int y2 = c2.get(Calendar.YEAR);
        double years = y2 - y1;
        double months = getIntervalMonths(startDate, endDate) / 12;
        BigDecimal decimal = new BigDecimal(years).add(new BigDecimal(months)).setScale(2, RoundingMode.HALF_UP);
        return decimal.doubleValue();
    }

    public static void main(String[] args) {
        String s1 = "2020-02-02 09:56:23";
        String s2 = "2021-07-07 14:56:23";
        double days1 = getIntervalHours(s1, s2);
        double days2 = getIntervalDays(s1, s2);
        double days3 = getIntervalMonths(s1, s2);
        double days4 = getIntervalYears(s1, s2);
        String days5 = addDays(s1, -20);
        System.out.println(days1);
        System.out.println(days2);
        System.out.println(days3);
        System.out.println(days4);
        System.out.println(days5);
    }
}
