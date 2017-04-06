package com.edusoho.kuozhi.clean.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JesseHuang on 2017/4/6.
 */

public class DataUtils {

    public static String getPostDays(String postTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long l = 1;
        try {
            String tDate = postTime.split("[+]")[0].replace('T', ' ');
            long milliSec = 1000;
            Date date = new Date();
            l = (date.getTime() - sdf.parse(tDate).getTime()) / (milliSec);

            //如果大于24返回天数
            if (l > 30 * 24 * 60 * 60) {
                return postTime.split("T")[0];
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
            Log.d("AppUtil.getPostDays", ex.toString());
        }

        return String.valueOf(l) + "秒前";
    }
}
