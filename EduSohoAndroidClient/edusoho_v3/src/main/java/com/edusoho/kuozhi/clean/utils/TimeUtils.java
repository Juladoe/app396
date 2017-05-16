package com.edusoho.kuozhi.clean.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by JesseHuang on 2017/4/6.
 */

public class TimeUtils {
    private static final SimpleDateFormat UTC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat LIVE_TASK_DATE_FORMAT = new SimpleDateFormat("MM月dd号 HH:mm");
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

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

    public static String getSecond2Min(int time) {
        String timeStr;
        int hour;
        int minute;
        int second;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String getLiveTime(long time) {
        String liveTime = "";
        try {
            Date date = new Date(time);
            liveTime = LIVE_TASK_DATE_FORMAT.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return liveTime;
    }

    private static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}
