package com.edusoho.kuozhi.clean.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JesseHuang on 2017/4/6.
 */

public class TimeUtils {
    private static final SimpleDateFormat UTC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String getPostDays(String postTime) {
        long l = 1;
        try {
            Date date = UTC_DATE_FORMAT.parse(postTime);
            l = (new Date().getTime() - date.getTime()) / (1000);

            if (l > 30 * 24 * 60 * 60) {
                return DEFAULT_DATE_FORMAT.format(date);
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

    public static Date getUTCtoDate(String time) {
        Date date = new Date();
        try {
            date = UTC_DATE_FORMAT.parse(time);
        } catch (ParseException ex) {

        }
        return date;
    }
}
