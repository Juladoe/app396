package com.edusoho.kuozhi.v3.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.StatusCallback;
import com.edusoho.kuozhi.v3.model.sys.AppUpdateInfo;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by JesseHuang on 15/4/26.
 */
public class AppUtil {

    public static void checkUpateApp(
            final ActionBarBaseActivity activity, final StatusCallback<AppUpdateInfo> callback) {
        final EdusohoApp app = activity.app;
        RequestUrl requestUrl = app.bindUrl(Const.APP_UPDATE, false);
        String code = activity.getResources().getString(R.string.app_code);
        requestUrl.setParams(new String[]{
                "code", code
        });
        Log.d(null, "code->" + code);
        activity.ajaxPostWithLoading(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                final AppUpdateInfo appUpdateInfo = activity.parseJsonValue(response.toString(), new TypeToken<AppUpdateInfo>() {
                });
                if (appUpdateInfo == null || appUpdateInfo.androidVersion == null) {
                    return;
                }

                String newVersion = appUpdateInfo.androidVersion;
                Log.d(null, "old version->" + app.getApkVersion());
                int result = CommonUtil.compareVersion(app.getApkVersion(), newVersion);
                if (result == Const.LOW_VERSIO) {
                    callback.success(appUpdateInfo);
                } else {
                    callback.error(appUpdateInfo);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, null);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue / scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
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

    public static ProgressDialog initProgressDialog(Context context, String msg) {
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        return mProgressDialog;
    }

    public static boolean isNetConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    public static boolean isWiFiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isAvailable();
    }

    public static boolean saveStreamToFile(
            InputStream inputStream, File file, boolean inClose) {
        byte[] buffer = new byte[1024];
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            int length = -1;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inClose) {
                    inputStream.close();
                }
                outputStream.close();
            } catch (Exception e) {
                //nothing
            }
        }

        return false;
    }

    public static File getAppStorage() {
        File store = getSystemStorage();
        File appStorage = new File(store, "edusoho");
        if (!appStorage.exists()) {
            appStorage.mkdirs();
        }

        return appStorage;
    }

    public static File getSchoolStorage(String host) {
        File store = getAppStorage();
        File schoolStorage = new File(store, host);
        if (!schoolStorage.exists()) {
            schoolStorage.mkdirs();
        }

        return schoolStorage;
    }

    public static File getSystemStorage() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getDataDirectory();
        }
        return Environment.getExternalStorageDirectory();
    }

    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_LOWER = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * encode By MD5
     *
     * @param str
     * @return String
     */
    public static String md5(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes());
            return new String(encodeHex(messageDigest.digest()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data a byte[] to convert to Hex characters
     * @return A char[] containing hexadecimal characters
     */
    private static char[] encodeHex(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return out;
    }

    /**
     * 将服务器端的时间格式转化为milli Second
     *
     * @param time
     * @return
     */
    public static long convertMilliSec(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long returnTime = 0;
        try {
            String tDate = time.split("[+]")[0].replace('T', ' ');
            return sdf.parse(tDate).getTime();

        } catch (Exception ex) {
            Log.d("AppUtil.convertMilliSec", ex.toString());
        }
        return returnTime;
    }

    /**
     * 根据时间转化私信显示的时间
     * 当天显示，18：00
     * 昨天显示，昨天 18：00
     * 比昨天更早，星期几 18：00
     *
     * @param t
     * @return
     */
    public static String convertWeekTime(String t) {
        String result = "";
        try {
            String tDate = t.split("[+]")[0].replace('T', ' ');
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar nowCalendar = Calendar.getInstance();
            Date paramDate = sdf.parse(tDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(paramDate);
            int interval = nowCalendar.get(Calendar.DATE) - calendar.get(Calendar.DATE);
            String postTime = (calendar.get(Calendar.HOUR_OF_DAY) >= 10 ? calendar.get(Calendar.HOUR_OF_DAY) : "0" + calendar.get(Calendar.HOUR)) + ":"
                    + (calendar.get(Calendar.MINUTE) >= 10 ? calendar.get(Calendar.MINUTE) : ("0" + calendar.get(Calendar.MINUTE)));
            if (interval == 0) {
                result = postTime;
            } else if (interval == 1) {
                result = "昨天 " + postTime;
            } else {
                result = (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DATE) + "日 " + postTime;
            }
        } catch (Exception ex) {
            Log.d("AppUtil.getPostDays", ex.toString());
        }
        return result;
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

    /**
     * 格式化容量
     *
     * @param totalSize
     * @return
     */
    public static String formatSize(long totalSize) {
        Log.d(null, "totalSize->" + totalSize);
        float kb = 1024.0f;
        if (totalSize < (kb * kb)) {
            return String.format("%.1f%s", (totalSize / kb), "KB");
        }

        if (totalSize < (kb * kb * kb)) {
            return String.format("%.1f%s", (totalSize / (kb * kb)), "M");
        }

        return String.format("%.1f%s", (totalSize / (kb * kb * kb)), "G");
    }

    public static ObjectAnimator animForHeight(Object view, int start, int end, int time) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                view, "height", start, end);
        objectAnimator.setDuration(time);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.start();
        return objectAnimator;
    }

    public static String convertCNTime(String time) {
        String[] times = time.split(":");
        String hour = "";
        String min = times[0];
        String sec = times[1];

        if (Integer.valueOf(min) / 60 > 1) {
            hour = Integer.valueOf(min) / 60 + "";
        } else {
            if (min.length() > 1 && min.substring(0, 1).equals("0")) {
                min = min.substring(1, 2);
            }
        }

        if (hour.length() > 0) {
            return String.format("%s小时%s分%s秒", hour, min, sec);
        } else {
            return String.format("%s分%s秒", min, sec);
        }
    }
}
