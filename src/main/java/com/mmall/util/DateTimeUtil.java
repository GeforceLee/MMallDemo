package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * @author geforce
 * @date 2018/4/3
 */
public class DateTimeUtil {

    //joda-time


    private static final String STANDARD_FORMAT = "yyyy-MM-dd hh:mm:ss";

    /**
     * 字符串转时间
     * @param dateTimeStr
     * @param formatStr
     * @return
     */
    public static Date strToDate(String dateTimeStr,String formatStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime  dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     *
     * @param dateTimeStr
     * @return
     */
    public static Date strToDate(String dateTimeStr) {
        return strToDate(dateTimeStr,STANDARD_FORMAT);
    }

    /**
     *  日期转字符串
     * @param date
     * @param formatStr
     * @return
     */
    public static String dateToStr(Date date,String formatStr) {
        if (date ==null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    /**
     *
     * @param date
     * @return
     */
    public static String dateToStr(Date date) {
        return dateToStr(date,STANDARD_FORMAT);
    }

    public static void main(String[] args) {
        System.out.println(dateToStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
        System.out.println(strToDate("2010-01-11 11:22:11","yyyy-MM-dd hh:mm:ss"));
    }
}
