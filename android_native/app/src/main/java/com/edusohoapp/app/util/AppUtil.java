package com.edusohoapp.app.util;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import com.edusohoapp.app.entity.SettingItem;

public class AppUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static String coverCourseAbout(String about)
    {
        return about.replaceAll("<[^>]+>", "");
    }

    public static String coverLessonContent(String content)
    {
        return content.replaceAll("href=[^=]+\\s", "href='javascript:void();' ");
    }

    public static String coverTime(String time)
    {
        return time.substring(0, 10);
    }

    public static String goalsToStr(String[] goals)
    {
        StringBuffer sb = new StringBuffer();
        for (String goal : goals) {
            sb.append("・").append(goal).append("\n");
        }
        return sb.toString();
    }

    public static String audiencesToStr(String[] audiences)
    {
        StringBuffer sb = new StringBuffer();
        for (String audience : audiences) {
            sb.append("・").append(audience).append("\n");
        }
        return sb.toString();
    }

    public static void enableBtn(ViewGroup vg, boolean isEnable)
    {
        int count = vg.getChildCount();
        for (int i=0; i < count; i++) {
            vg.getChildAt(i).setEnabled(isEnable);
        }
    }

    /**
     *
     * @param layout
     * @param offset
     */
    public static void moveLayout(
            Context context, final View layout, final int offset, int type, int defsize, int time)
    {
        final int w = layout.getWidth();
        final int h = layout.getHeight();
        final Handler handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                layout.layout(msg.arg1, 0, w + msg.arg1, h);
            }
        };

        Timer moveTimer = new Timer();
        moveTimer.schedule(new MoveTimerTask(offset, type, defsize) {
            @Override
            public void run() {
                if (step()) {
                    Message msg = handler.obtainMessage();
                    this.mOffset += this.DEF_SIZE;
                    msg.arg1 = this.mOffset;
                    msg.sendToTarget();
                } else {
                    cancel();
                }
            }
        }, 1, time);
    }

    public static class MoveTimerTask extends TimerTask
    {
        public static int LEFT = 0001;
        public static int RIGHT = 0002;

        public int DEF_SIZE;

        private int step_def;
        public int step;
        public int mOffset;
        private int type;

        public MoveTimerTask(int offset, int type, int defsize)
        {
            this.type = type;
            this.step = offset;
            this.mOffset = type == LEFT? 0: offset;
            if (defsize == -1) {
                DEF_SIZE = type == LEFT? 5: -5;
            } else {
                DEF_SIZE = type == LEFT? defsize: -defsize;
            }
            step_def = Math.abs(DEF_SIZE);
        }

        public boolean step()
        {
            if (step > 0 && step < step_def) {
                DEF_SIZE = type == LEFT ?step : -step;
                step = 0;
                return true;
            }
            step -= step_def;
            return step > 0;
        }

        @Override
        public void run() {

        }
    }
}
