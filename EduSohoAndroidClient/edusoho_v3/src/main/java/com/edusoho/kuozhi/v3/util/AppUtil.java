package com.edusoho.kuozhi.v3.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.StatusCallback;
import com.edusoho.kuozhi.v3.model.sys.AppUpdateInfo;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;

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
        activity.ajaxPost(requestUrl, new Response.Listener<String>() {
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
        });
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

    public static boolean isNetConnect(Context context)
    {
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
            InputStream inputStream, File file, boolean inClose)
    {
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
            try{
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

    public static File getAppStorage()
    {
        File store = getSystemStorage();
        File appStorage = new File(store, "edusoho");
        if (!appStorage.exists()) {
            appStorage.mkdirs();
        }

        return appStorage;
    }

    public static File getSchoolStorage(String host)
    {
        File store = getAppStorage();
        File schoolStorage = new File(store, host);
        if (!schoolStorage.exists()) {
            schoolStorage.mkdirs();
        }

        return schoolStorage;
    }

    public static File getSystemStorage()
    {
        if (! Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
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
}
