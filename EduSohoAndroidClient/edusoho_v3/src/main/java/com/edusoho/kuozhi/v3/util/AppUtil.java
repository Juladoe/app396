package com.edusoho.kuozhi.v3.util;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.StatusCallback;
import com.edusoho.kuozhi.v3.model.sys.AppUpdateInfo;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JesseHuang on 15/4/26.
 */
public class AppUtil {

    public static int parseInt(String value) {
        int i = 0;
        if (value == null) {
            return i;
        }
        try {
            i = Integer.parseInt(value);
        } catch (Exception e) {
            i = 0;
        }

        return i;
    }

    public static float parseFloat(String value) {
        float i = 0.0f;
        if (value == null) {
            return i;
        }
        try {
            i = Float.parseFloat(value);
        } catch (Exception e) {
            i = 0.0f;
        }

        return i;
    }

    public static Bitmap getBitmapFromFile(File file) {
        Bitmap bitmap = null;
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), option);
        int width = (int) (EdusohoApp.screenW * 0.5f);
        option.inSampleSize = computeSampleSize(option, -1, width * width);
        option.inJustDecodeBounds = false;
        try {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), option);
            Log.d(null, "bm->" + bitmap);
        } catch (Exception e) {
            bitmap = null;
        }

        return bitmap;
    }

    private static int computeInitialSampleSize(
            BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static int computeSampleSize(
            BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    /**
     * 获取系统图片路径
     */

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static SpannableString getColorTextAfter(String text, String newStr, int color) {
        StringBuffer stringBuffer = new StringBuffer(text);
        int start = stringBuffer.length();
        stringBuffer.append(newStr);
        SpannableString spannableString = new SpannableString(stringBuffer);
        spannableString.setSpan(
                new ForegroundColorSpan(color), start, stringBuffer.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    public static void checkUpateApp(
            final BaseActivity activity, final StatusCallback<AppUpdateInfo> callback) {
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

    public static Bitmap compressImage(Bitmap image, ByteArrayOutputStream baos, int size) {
        image.compress(Bitmap.CompressFormat.JPEG, size, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//        int options = 100;
//        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
//            options -= 10;//每次都减少10
//            baos.reset();//重置baos即清空baos
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//
//        }
        //ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        //Bitmap bitmap = BitmapFactory.decodeStream(isBm);//把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
        return bitmap;
    }

    /**
     * bitmap转File
     *
     * @param bitmap
     * @param path   file位置
     * @return
     */
    public static File convertBitmap2File(Bitmap bitmap, String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
        return file;
    }

    /**
     * 获取图片旋转的角度
     *
     * @param imagePath
     * @return
     */
    public static int getImageDegree(String imagePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception ex) {
            Log.d("AppUtil.getImageDegree", ex.toString());
        }
        return degree;
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
     * 去掉所有<Img>标签
     *
     * @param content
     * @return
     */
    public static String removeImgTagFromString(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        Matcher m = Pattern.compile("(<img src=\".*?\" .>)").matcher(content);
        new StringBuffer().append("1");
        while (m.find()) {
            content = content.replace(m.group(1), "");
        }
        return content;
    }

    /**
     * 去掉字符串中的\n\t
     *
     * @param content
     * @return
     */
    public static String removeHtmlSpace(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        Matcher m = Pattern.compile("\\t|\\n").matcher(content);
        while (m.find()) {
            content = content.replace(m.group(0), "");
        }
        return content;
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

    public static long convertMilliSec(int time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long returnTime = 0;
        try {
            String tDate = String.valueOf(time).split("[+]")[0].replace('T', ' ');
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
    public static String convertWeekTime(int t) {
        String result = "";
        try {
            String tDate = String.valueOf(t).split("[+]")[0].replace('T', ' ');
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

    public static String convertMills2Date(long millsTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            String nowTime = sdf.format(System.currentTimeMillis());
            String showTime = sdf.format(millsTime);
            if (nowTime.substring(0, 11).equals(
                    showTime.substring(0, 11))) {
                return showTime.substring(12);
            } else {
                return showTime.substring(5, 11);
            }
        } catch (Exception ex) {
            Log.e("convertMills2Date", ex.getMessage());
            return "";
        }
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

    public static String coverCourseAbout(String about) {
        return about.replaceAll("<[^>]+>", "");
    }

    public static boolean saveBitmap2File(Bitmap bmp, String filename) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(filename);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp.compress(format, quality, stream);
    }

    public static void showAlertDialog(final BaseActivity activity, String content) {
        PopupDialog popupDialog = PopupDialog.createMuilt(
                activity,
                "播放提示",
                content,
                new PopupDialog.PopupClickListener() {
                    @Override
                    public void onClick(int button) {
                        if (button == PopupDialog.OK) {
                            activity.app.mEngine.runNormalPlugin("SettingActivity", activity, null);
                        }
                    }
                }
        );
        popupDialog.setOkText("去设置");
        popupDialog.show();
    }

    /**
     * 图片分辨率压缩
     *
     * @param bitmap    图片
     * @param imageSize 图片大小
     * @param degree    图片旋转的角度，如果没有旋转，则为0
     * @return
     */
    public static Bitmap scaleImage(Bitmap bitmap, float imageSize, int degree) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //float density = context.getResources().getDisplayMetrics().density;
        int bounding = Math.round(imageSize);

        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        Matrix matrix = new Matrix();
        matrix.postScale(xScale, xScale);
        matrix.postRotate((float) degree);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return scaledBitmap;
    }
}
