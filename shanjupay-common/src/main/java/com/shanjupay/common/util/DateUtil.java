package com.shanjupay.common.util;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateUtil {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYYMMDD = "yyyyMMdd";

    public static final String HHmmss = "HHmmss";

    public static final String YYYYMM = "yyyyMM";

    private DateUtil(){}

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static String toDateTime(LocalDateTime dateTime) {
        return toDateTime(dateTime, YYYY_MM_DD_HH_MM_SS);
    }

    public static String toDateTime(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.SIMPLIFIED_CHINESE));
    }

    public static String toDateText(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern, Locale.SIMPLIFIED_CHINESE));
    }

    public static Date addExtraHour(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.HOUR_OF_DAY, hour);
        return cal.getTime();
    }

    public static Date increaseDay2Date(Date date, int increase) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.DAY_OF_MONTH, increase);
        return cal.getTime();
    }

    public static Date format(String strDate, String format) {
        Date d = null;
        if (null == strDate || "".equals(strDate)) {
            return null;
        } else {
            try {
                d = getFormatter(format).parse(strDate);
            } catch (ParseException e) {
                return null;
            }
        }
        return d;
    }

    private static SimpleDateFormat getFormatter(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public static List<String> getAllDaysOfMouthInString(Date month, DateFormat dateFormat) {
        List<String> rs = new ArrayList<>();
        DateFormat df = null;
        if (null == dateFormat) {
            df = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            df = dateFormat;
        }
        Calendar cad = Calendar.getInstance();
        if (null != month) {
            cad.setTime(month);
        }
        int day_month = cad.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < day_month; i++) {
            cad.set(Calendar.DAY_OF_MONTH, i + 1);
            rs.add(df.format(cad.getTime()));
        }
        return rs;
    }

    public static List<String> getSpecifyDaysOfMonthInString(Date begin, Date end, DateFormat dateFormat) {
        DateFormat df = null;
        if (null == dateFormat) {
            df = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            df = dateFormat;
        }
        List<String> rs = new ArrayList<>();
        List<Date> tmpList = getSpecifyDaysOfMonth(begin, end);
        for (Date date : tmpList) {
            rs.add(df.format(date));
        }
        return rs;
    }

    public static List<Date> getSpecifyDaysOfMonth(Date begin, Date end) {
        List<Date> rs = new ArrayList<>();
        Calendar cad = Calendar.getInstance();
        int day_month = -1;
        if (null == begin) {
            cad.set(Calendar.DAY_OF_MONTH, 1);
            begin = cad.getTime();
        }
        if (null == end) {
            day_month = cad.getActualMaximum(Calendar.DAY_OF_MONTH);
            cad.set(Calendar.DAY_OF_MONTH, day_month + 1);
            end = cad.getTime();
        }
        cad.set(Calendar.DAY_OF_MONTH, 1);
        Date tmp = begin;
        int i = 1;
        while (true) {
            cad.set(Calendar.DAY_OF_MONTH, i);
            i++;
            tmp = cad.getTime();
            if (tmp.before(end)) {
                rs.add(cad.getTime());
            } else {
                break;
            }
        }
        return rs;
    }

    public static synchronized Date getCurDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public static String getCurrDateTimeStr() {
        return format(getCurDate(), YYYY_MM_DD_HH_MM_SS);
    }

    public static String getSpecifiedDayBefore(String specifiedDay, String formatStr) {
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day - 1);
        String dayBefore = new SimpleDateFormat(formatStr).format(calendar.getTime());
        return dayBefore;
    }

    public static String getSpecifiedDayAfter(String specifiedDay, String formatStr) {
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day + 1);
        String dayBefore = new SimpleDateFormat(formatStr).format(calendar.getTime());
        return dayBefore;
    }

    public static String getWeekFirstDay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 2;
        cal.add(Calendar.DATE, -day_of_week);
        return simpleDateFormat.format(cal.getTime());
    }

    public static String getCurrentMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar.set(GregorianCalendar.DAY_OF_MONTH, 1);
        Date beginTime = calendar.getTime();
        return simpleDateFormat.format(beginTime);
    }

    public static String getYesterdayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getYesterdayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(calendar.getTime()) + "23:59:59";
    }

    public static String getCurrDayStart() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getStartDayWithMonth(String month) throws ParseException {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat mf = new SimpleDateFormat("yyyy-MM");
        Date date = mf.parse(month);
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 0);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getEndDayWithMonth(String month) throws ParseException {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat mf = new SimpleDateFormat("yyyy-MM");
        Date date = mf.parse(month);
        calendar.setTime(date);
        calendar.roll(Calendar.DATE, -1);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String formatYearMonthDay(String dateStr) throws ParseException {
        if (StringUtil.isNotBlank(dateStr)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(dateStr);
            return simpleDateFormat.format(date);
        } else {
            return "";
        }
    }

    public static int getWeekIndexOfMonth(String dateStr) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    public static int getSecondToDesignationTime(String designationTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date toDate;
        try {
            toDate = simpleDateFormat.parse(designationTime);
            int u = (int) (toDate.getTime() - simpleDateFormat.parse(DateUtil.getCurrDateTimeStr()).getTime() / 1000);
            return u;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }
}
