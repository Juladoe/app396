package com.edusoho.kuozhi.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.listener.NormalCallback;
import com.nineoldandroids.animation.ObjectAnimator;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.trinea.android.common.util.DigestUtils;

public class AppUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int[] getTeacherIds(Teacher[] teachers)
    {
        if (teachers == null) {
            return new int[0];
        }
        int[] ids = new int[teachers.length];
        for (int i=0; i < teachers.length; i++) {
            ids[i] = teachers[i].id;
        }

        return ids;
    }

    public static boolean inArray(String find, String[] array)
    {
        int result = Arrays.binarySearch(array, find);
        return result >= 0;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void touchByGestureDetector(
            View view, GestureDetector.SimpleOnGestureListener gestureListener) {
        final GestureDetector gestureDetector = new GestureDetector(gestureListener);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    public static boolean urlCacheExistsed(Context context, String url) {
        File cacheDir = AQUtility.getCacheDir(context);
        File cacheFile = AQUtility.getExistedCacheByUrl(cacheDir, url);
        return cacheFile != null;
    }

    public static void viewTreeObserver(View view, final NormalCallback callback) {
        final ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                callback.success(null);
                if (observer.isAlive()) {
                    observer.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    public static void animForHeight(Object view, int start, int end, int time) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                view, "height", start, end);
        objectAnimator.setDuration(time);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.start();
    }

    public static String coverUrlToCacheKey(RequestUrl requestUrl) {
        StringBuilder builder = new StringBuilder(requestUrl.url);

        HashMap<String, String> map = requestUrl.params;
        for (String key : map.keySet()) {
            builder.append("&").append(key);
            builder.append("&").append(map.get(key));
        }

        return DigestUtils.md5(builder.toString());
    }

    public static String gzip(String input) {
        String result = null;
        ByteArrayInputStream reader = null;
        GZIPOutputStream gzipOutputStream = null;
        try {
            reader = new ByteArrayInputStream(input.getBytes());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = reader.read(buffer)) != -1) {
                gzipOutputStream.write(buffer, 0, len);
            }

            result = byteArrayOutputStream.toString("utf-8");
        } catch (Exception e) {
            //nothing
        } finally {
            try {
                reader.close();
                gzipOutputStream.close();
            } catch (Exception e) {
                //nothing}
            }
        }

        return result;
    }

    public static String unGzip(String input) {
        StringBuilder builder = new StringBuilder();
        GZIPInputStream gzipInputStream = null;
        try {
            int len = -1;
            byte[] buffer = new byte[1024];
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
            gzipInputStream = new GZIPInputStream(byteArrayInputStream);

            while ((len = gzipInputStream.read(buffer)) != -1) {
                builder.append(new String(buffer, 0, len));
            }
        } catch (Exception e) {
            //nothing
        } finally {
            try {
                gzipInputStream.close();
            } catch (Exception e) {
                //nothing}
            }
        }

        return builder.toString();
    }

    public static String coverCourseAbout(String about) {
        return about.replaceAll("<[^>]+>", "");
    }

    public static int getCourseCorverHeight(int width) {
        float scale = (float) width / 480;
        return (int) (270 * scale);
    }

    /**
     * 转换图片长宽比
     * @param width
     * @return
     */
    public static int getCourseListCoverHeight(int width) {
        float scale = (float) width / 480;
        return (int) (270 * scale);
    }

    public static int getLearnCourseListCoverHeight(int width) {
        float scale = (float) width * 0.9f / 480;
        return (int) (270 * scale);
    }

    public static String coverLessonContent(String content) {
        return content.replaceAll("href=[^=]+\\s", "href='javascript:void();' ");
    }

    public static String coverTime(String time) {
        return "".equals(time) ? "" : time.substring(0, 10);
    }

    public static String goalsToStr(String[] goals) {
        StringBuffer sb = new StringBuffer();
        for (String goal : goals) {
            sb.append("・").append(goal).append("\n");
        }
        if (TextUtils.isEmpty(sb)) {
            return "暂无相关信息";
        }
        return sb.toString();
    }

    public static String audiencesToStr(String[] audiences) {
        StringBuffer sb = new StringBuffer();
        for (String audience : audiences) {
            sb.append("・").append(audience).append("\n");
        }
        if (TextUtils.isEmpty(sb)) {
            return "暂无相关信息";
        }
        return sb.toString();
    }

    public static void enableBtn(ViewGroup vg, boolean isEnable) {
        int count = vg.getChildCount();
        for (int i = 0; i < count; i++) {
            vg.getChildAt(i).setEnabled(isEnable);
        }
    }

    /**
     * @param layout
     * @param offset
     */
    public static void moveLayout(
            Context context, final View layout, final int offset, int type, int defsize, int time) {
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

    public static class MoveTimerTask extends TimerTask {
        public static int LEFT = 0001;
        public static int RIGHT = 0002;

        public int DEF_SIZE;

        private int step_def;
        public int step;
        public int mOffset;
        private int type;

        public MoveTimerTask(int offset, int type, int defsize) {
            this.type = type;
            this.step = offset;
            this.mOffset = type == LEFT ? 0 : offset;
            if (defsize == -1) {
                DEF_SIZE = type == LEFT ? 5 : -5;
            } else {
                DEF_SIZE = type == LEFT ? defsize : -defsize;
            }
            step_def = Math.abs(DEF_SIZE);
        }

        public boolean step() {
            if (step > 0 && step < step_def) {
                DEF_SIZE = type == LEFT ? step : -step;
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

    /**
     * @param v1
     * @param v2
     * @return
     * @throws RuntimeException
     */
    public static int compareVersion(String v1, String v2) throws RuntimeException {
        if (v1 == null || v2 == null) {
            return Const.NORMAL_VERSIO;
        }
        String[] v1Versons = v1.split("\\.");
        String[] v2Versons = v2.split("\\.");
        if (v1Versons.length != v2Versons.length) {
            throw new RuntimeException("版本不一致，无法对比");
        }

        int length = v1Versons.length;
        for (int i = 0; i < length; i++) {
            int firstVersion = Integer.parseInt(v1Versons[i]);
            int secoundVersion = Integer.parseInt(v2Versons[i]);
            if (firstVersion > secoundVersion) {
                return Const.HEIGHT_VERSIO;
            }
            if (firstVersion < secoundVersion) {
                return Const.LOW_VERSIO;
            }
        }

        return Const.NORMAL_VERSIO;
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p/>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p/>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to
     * {@link #convertActivityFromTranslucent(android.app.Activity)} .
     * <p/>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p/>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public static void convertActivityToTranslucent(Activity activity) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(activity, new Object[]{
                    null
            });
        } catch (Throwable t) {
        }
    }

    public static <T extends View> T getViewHolder(View convertView, int layoutId) {
        SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            convertView.setTag(viewHolder);
        }
        View childView = viewHolder.get(layoutId);
        if (childView == null) {
            childView = convertView.findViewById(layoutId);
            viewHolder.put(layoutId, childView);
        }
        return (T) childView;
    }

    /**
     * 计算发布问题天数,服务端获取时间格式：2014-05-20T22:03:43+08:00
     * 转换为天数或者小时
     */
    public static String getPostDays(String postTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long l = 1;
        try {
            String tDate = postTime.split("[+]")[0].replace('T', ' ');
            long milliSec = 1000;
            Date date = new Date();
            l = (date.getTime() - sdf.parse(tDate).getTime()) / (milliSec * 60 * 60);
            //如果大于24返回天数
            if (l > 24) {
                l = l / 24;
                return String.valueOf(l) + "天前";
            }
        } catch (Exception ex) {
            Log.d("AppUtil.getPostDays", ex.toString());
        }
        return String.valueOf(l) + "小时前";
    }
}
