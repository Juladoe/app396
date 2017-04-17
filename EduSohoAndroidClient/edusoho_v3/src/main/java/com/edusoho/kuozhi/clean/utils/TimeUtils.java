package com.edusoho.kuozhi.clean.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JesseHuang on 2017/4/6.
 */

public class TimeUtils {
    private static final SimpleDateFormat UTC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getPostDays(String postTime) {
        long l = 1;
        try {
            Date date = UTC_DATE_FORMAT.parse(postTime);
            l = (new Date().getTime() - date.getTime()) / (1000);

            if (l > 30 * 24 * 60 * 60) {
                return SIMPLE_DATE_FORMAT.format(date);
            } else if (l > 24 * 60 * 60) {
                l = l / (24 * 60 * 60);
                return String.valueOf(l) + "天前";
            } else if (l > 60 * 60) {
                l = l / (60 * 60);
                return String.valueOf(l) + "小时前";
            } else if (l > 60) {
                l = l / (60);
                return String.valueOf(l) + "分钟前";
            }
            if (l < 1) {
                return "刚刚";
            }
        } catch (Exception ex) {
            Log.d("DateUtils::getPostDays", ex.toString());
        }
        return String.valueOf(l) + "秒前";
    }

    /**
     * convert to date type
     *
     * @param time UTC TIME, 1970-01-01T08:00:00+08:00
     * @return
     */
    public static Date getUTCtoDate(String time) {
        Date date = new Date();
        try {
            date = UTC_DATE_FORMAT.parse(time);
        } catch (ParseException ex) {

        }
        return date;
    }

    /**
     * convert to millisecond
     *
     * @param time UTC TIME, 1970-01-01T08:00:00+08:00
     * @return
     */
    public static long getMillisecond(String time) {
        return getUTCtoDate(time).getTime();
    }

    /**
     * convert to custom time
     *
     * @param dateFormat UTC TIME, 1970-01-01T08:00:00+08:00
     * @return
     */
    public static String getStringTime(String time, String dateFormat) {
        String customTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            Date date = UTC_DATE_FORMAT.parse(time);
            customTime = sdf.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return customTime;
    }
}
