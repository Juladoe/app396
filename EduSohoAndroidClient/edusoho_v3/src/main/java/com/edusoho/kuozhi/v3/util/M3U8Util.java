package com.edusoho.kuozhi.v3.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.broadcast.DownLoadStatusReceiver;
import com.edusoho.kuozhi.v3.listener.ResultCallback;
import com.edusoho.kuozhi.v3.model.bal.Lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8File;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8ListItem;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.trinea.android.common.util.DigestUtils;
import cn.trinea.android.common.util.FileUtils;
import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by howzhi on 14/12/10.
 */
public class M3U8Util {

    private static Pattern M3U8_STREAM_PAT = Pattern.compile(
            "#EXT-X-STREAM-INF:PROGRAM-ID=(\\d+),BANDWIDTH=(\\d+)", Pattern.DOTALL);
    private static Pattern M3U8_EXTINF_PAT = Pattern.compile(
            "#EXTINF:([\\d\\.]+),", Pattern.DOTALL);
    private static Pattern M3U8_EXT_X_KEY_PAT = Pattern.compile(
            "#EXT-X-KEY:METHOD=AES-128,URI=\"([^,\"]+)\",IV=(\\w+)", Pattern.DOTALL);
    private static Pattern URL_PAT = Pattern.compile("(#EXT-X-KEY:[^\n]+)?(http://[^\"\n]+)", Pattern.DOTALL);

    public static final int FINISH = 1;
    public static final int UN_FINISH = 0;
    public static final int ALL = 2;
    public static final int START = -1;

    public static final int KEY = 0;
    public static final int URL = 1;

    private Context mContext;
    private int mLessonId;
    private int mCourseId;
    private int mUserId;
    private String mLessonTitle;
    private String mLessonMediaUrl;
    private EdusohoApp app;
    private SqliteUtil mSqliteUtil;
    private String mTargetHost;
    private HttpClient mHttpClient;

    private boolean isCancel;

    private Hashtable<String, Integer> mTimeOutList;
    private ArrayList<HttpGet> mFutures;

    private static final String TAG = "M3U8Uitl";
    private ScheduledThreadPoolExecutor mThreadPoolExecutor;

    public M3U8Util(Context context) {
        this.mContext = context;
        this.app = EdusohoApp.app;
        this.mUserId = app.loginUser.id;

        Uri hostUri = Uri.parse(app.host);
        if (hostUri != null) {
            this.mTargetHost = hostUri.getHost();
        }

        mFutures = new ArrayList<HttpGet>();
        mTimeOutList = new Hashtable<String, Integer>();
        mThreadPoolExecutor = new ScheduledThreadPoolExecutor(3);
        mThreadPoolExecutor.setMaximumPoolSize(4);
        mSqliteUtil = new SqliteUtil(mContext, null, null);
    }

    public String getLessonTitle() {
        return mLessonTitle;
    }

    private String readStringFromNet(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {

        }
        return null;
    }

    private InputStream readStreamFromNet(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            Log.d(TAG, "get url " + url);
            HttpResponse response = client.execute(httpGet);
            return response.getEntity().getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static M3U8DbModle parseM3U8Modle(Cursor cursor) {
        M3U8DbModle m3U8DbModle = new M3U8DbModle();
        m3U8DbModle.id = cursor.getInt(cursor.getColumnIndex("id"));
        m3U8DbModle.finish = cursor.getInt(cursor.getColumnIndex("finish"));
        m3U8DbModle.downloadNum = cursor.getInt(cursor.getColumnIndex("download_num"));
        m3U8DbModle.totalNum = cursor.getInt(cursor.getColumnIndex("total_num"));
        m3U8DbModle.lessonId = cursor.getInt(cursor.getColumnIndex("lessonId"));
        m3U8DbModle.host = cursor.getString(cursor.getColumnIndex("host"));
        m3U8DbModle.playList = cursor.getString(cursor.getColumnIndex("play_list"));

        return m3U8DbModle;
    }

    /**
     * 获取视频缓存
     *
     * @param id
     * @param host
     * @return
     */
    public static M3U8DbModle queryM3U8Modle(
            Context context, int userId, int id, String host, int isFinish) {
        SqliteUtil.QueryPaser<M3U8DbModle> queryCallBack =
                new SqliteUtil.QueryPaser<M3U8DbModle>() {
                    @Override
                    public M3U8DbModle parse(Cursor cursor) {
                        return parseM3U8Modle(cursor);
                    }
                };

        String finishQuery = isFinish == ALL ? "" : " and finish=" + isFinish;
        M3U8DbModle m3U8DbModle = SqliteUtil.getUtil(context).query(
                queryCallBack,
                "select * from data_m3u8 where userId=? and host=? and lessonId=?" + finishQuery,
                String.valueOf(userId), host, String.valueOf(id)
        );

        return m3U8DbModle;
    }

    public static ArrayList<M3U8DbModle> queryM3U8DownTasks(Context context, String host, int userId) {
        final ArrayList<M3U8DbModle> list = new ArrayList<M3U8DbModle>();
        SqliteUtil.QueryPaser<M3U8DbModle> queryCallBack =
                new SqliteUtil.QueryPaser<M3U8DbModle>() {
                    @Override
                    public M3U8DbModle parse(Cursor cursor) {
                        M3U8DbModle m3U8DbModle = parseM3U8Modle(cursor);
                        list.add(m3U8DbModle);
                        return null;
                    }
                };

        SqliteUtil.getUtil(context).query(
                queryCallBack,
                "select * from data_m3u8 where userId=? and host=? and finish in (0, -1)",
                String.valueOf(userId),
                host
        );

        return list;
    }

    public static SparseArray<M3U8DbModle> getM3U8ModleList(
            Context context, int[] ids, int userId, String host, int isFinish) {
        final StringBuffer lessonIds = new StringBuffer("(");
        for (int id : ids) {
            lessonIds.append(id).append(",");
        }
        if (lessonIds.length() > 1) {
            lessonIds.deleteCharAt(lessonIds.length() - 1);
        }
        lessonIds.append(")");

        final SparseArray<M3U8DbModle> list = new SparseArray<M3U8DbModle>();
        SqliteUtil.QueryPaser<M3U8DbModle> queryCallBack =
                new SqliteUtil.QueryPaser<M3U8DbModle>() {
                    @Override
                    public M3U8DbModle parse(Cursor cursor) {
                        M3U8DbModle m3U8DbModle = parseM3U8Modle(cursor);
                        list.put(m3U8DbModle.lessonId, m3U8DbModle);
                        return null;
                    }
                };

        if (isFinish == ALL) {
            SqliteUtil.getUtil(context).query(
                    queryCallBack,
                    "select * from data_m3u8 where userId=? and host=? and lessonId in " + lessonIds,
                    String.valueOf(userId),
                    host
            );
        } else {
            String finishQuery = isFinish == FINISH ? "finish=" + isFinish : "finish in (0, -1)";
            SqliteUtil.getUtil(context).query(
                    queryCallBack,
                    "select * from data_m3u8 where userId=? and host=? and " + finishQuery + " and lessonId in " + lessonIds,
                    String.valueOf(userId),
                    host
            );
        }
        return list;
    }

    private void loadLessonUrl(final int lessonId, int courseId) {
        RequestUrl requestUrl = app.bindUrl(Const.COURSELESSON, true);
        requestUrl.setParams(new String[]{
                "courseId", String.valueOf(courseId),
                "lessonId", String.valueOf(lessonId)
        });

        app.postUrl(false, requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                LessonItem lessonItem = app.gson.fromJson(
                        object, new TypeToken<LessonItem>() {
                        }.getType()
                );

                Log.d(TAG, "load lessonItem " + lessonItem);
                if (lessonItem == null) {
                    return;
                }

                mLessonMediaUrl = lessonItem.mediaUri;
                mLessonTitle = lessonItem.title;
                mThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        parseM3U8();
                    }
                });
            }
        });
    }

    public void download(int lessonId, int courseId, int userId) {
        mLessonId = lessonId;
        mCourseId = courseId;
        M3U8DbModle m3U8DbModle = queryM3U8Modle(mContext, userId, lessonId, mTargetHost, ALL);

        if (m3U8DbModle != null && m3U8DbModle.finish == UN_FINISH) {
            Log.d(TAG, "continue M3U8DbModle");
            LessonItem lessonItem = mSqliteUtil.queryForObj(
                    new TypeToken<LessonItem>() {
                    },
                    "where type=? and key=?",
                    Const.CACHE_LESSON_TYPE,
                    "lesson-" + mLessonId
            );

            if (lessonItem != null) {
                mLessonTitle = lessonItem.title;
            }

            M3U8File m3U8File = getM3U8FileFromModle(m3U8DbModle);

            Log.d(TAG, "continue m3U8File " + m3U8File.urlList);
            downloadM3U8SourceFile(m3U8File);
            return;
        }

        Log.d(TAG, "load lesson " + lessonId);
        loadLessonUrl(lessonId, courseId);
    }

    private M3U8File getM3U8FileFromModle(M3U8DbModle m3U8DbModle) {
        StringReader reader = new StringReader(m3U8DbModle.playList);

        final HashMap<String, Integer> filters = new HashMap<String, Integer>();
        SqliteUtil.QueryPaser<HashMap<String, Integer>> queryCallBack =
                new SqliteUtil.QueryPaser<HashMap<String, Integer>>() {
                    @Override
                    public HashMap<String, Integer> parse(Cursor cursor) {
                        String url = cursor.getString(cursor.getColumnIndex("url"));
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        filters.put(url, id);
                        return null;
                    }
                };

        SqliteUtil.getUtil(mContext).query(
                queryCallBack,
                "select * from data_m3u8_url where finish=? and lessonId=?",
                "1",
                String.valueOf(m3U8DbModle.lessonId)
        );
        M3U8File m3U8File = parseM3u8ListFile(new BufferedReader(reader), filters);
        return m3U8File;
    }

    /**
     * 获取网络的m3u8 file
     *
     * @param url
     * @return
     */
    private M3U8File getM3U8FileFromUrl(String url) {
        M3U8File m3U8File = null;
        /*
        if (url.startsWith("http://" + mTargetHost)) {
            m3U8File = new M3U8File();
            m3U8File.type = M3U8File.STREAM;
            m3U8File.content = url;
            return m3U8File;
        }
        */
        int type = M3U8File.STREAM_LIST;
        while (type == M3U8File.STREAM_LIST) {
            if (m3U8File != null) {
                ArrayList<M3U8ListItem> m3u8List = m3U8File.m3u8List;
                int pos = m3u8List.size() > 2 ? 1 : 0;
                M3U8ListItem m3U8ListItem = m3u8List.get(pos);
                url = m3U8ListItem.url;
            }

            Log.d(TAG, "start parse m3u8 file " + m3U8File);
            InputStream inputStream = readStreamFromNet(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            m3U8File = parseM3u8ListFile(reader, null);
            type = m3U8File.type;
        }

        return m3U8File;
    }

    /**
     * 解析m3u8
     */
    private void parseM3U8() {
        String url = mLessonMediaUrl;
        M3U8File m3U8File = getM3U8FileFromUrl(url);

        Log.d(TAG, "m3U8File " + m3U8File);
        if (m3U8File != null) {
            if (m3U8File.type == M3U8File.STREAM) {
                ToastUtils.show(mContext, "视频格式不正确,不能下载!");
                return;
            }
            initM3U8DataToDb(m3U8File);
            downloadM3U8SourceFile(m3U8File);
        }
    }

    private void downloadSingleFile(String url, int type) {
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, 100);
        //超时
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        HttpConnectionParams.setSoTimeout(params, 3000);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        mHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schReg), params);

        String key = DigestUtils.md5(url);
        HttpGet httpGet = new HttpGet(url);
        try {
            Log.d(TAG, "download " + url);
            HttpResponse response = mHttpClient.execute(httpGet);

            //发送下载广播
            Intent intent = new Intent(DownLoadStatusReceiver.ACTION);
            intent.putExtra(Const.LESSON_ID, mLessonId);
            intent.putExtra(Const.COURSE_ID, mCourseId);
            intent.putExtra(Const.ACTIONBAR_TITLE, mLessonTitle);
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
            //超时处理
            int count = mTimeOutList.get(key);
            if (count < 3) {
                Log.d(TAG, "timeiout count " + count);
                downloadSingleFile(url, type);
                mTimeOutList.put(key, ++count);
            }
            e.printStackTrace();
        } finally {
            httpGet.abort();
        }
    }

    private static void clearM3U8Db(SqliteUtil sqliteUtil, int lessonId) {
        Log.d(TAG, "clear m3u8 db");
        sqliteUtil.delete(
                "data_m3u8",
                "lessonId = ?",
                new String[]{String.valueOf(lessonId)}
        );
        //清除以前的数据库缓存项
        sqliteUtil.delete(
                "data_m3u8_url",
                "lessonId=?",
                new String[]{String.valueOf(lessonId)}
        );
    }

    public static M3U8DbModle saveM3U8Model(
            Context context, int lessonId, String host, int userId) {
        Log.d(TAG, "saveM3U8Model");
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(context);
        clearM3U8Db(sqliteUtil, lessonId);
        ContentValues cv = new ContentValues();
        cv.put("finish", START);
        cv.put("total_num", 0);
        cv.put("download_num", 0);
        cv.put("userId", userId);
        cv.put("lessonId", lessonId);
        cv.put("host", host);
        cv.put("play_list", "");
        sqliteUtil.insert("data_m3u8", cv);

        M3U8DbModle m3U8DbModle = new M3U8DbModle();
        m3U8DbModle.finish = START;
        m3U8DbModle.lessonId = lessonId;
        m3U8DbModle.host = host;
        m3U8DbModle.userId = userId;

        return m3U8DbModle;
    }

    /*
        初始化需要下载的m3u8列表
    */
    private void initM3U8DataToDb(M3U8File m3U8File) {
        Log.d(TAG, "initM3U8DataToDb");
        ContentValues cv = new ContentValues();
        cv.put("finish", 0);
        cv.put("total_num", m3U8File.urlList.size() + m3U8File.keyList.size());
        cv.put("play_list", m3U8File.content);
        mSqliteUtil.update(
                "data_m3u8",
                cv,
                "lessonId=? and host=?",
                new String[]{String.valueOf(mLessonId), mTargetHost}
        );

        //插入需要下载的任务
        for (String key : m3U8File.keyList) {
            Log.d(TAG, "insert m3u8 key " + key);
            insertM3U8SourceToDb(mLessonId, key);
        }
        for (String key : m3U8File.urlList) {
            Log.d(TAG, "insert m3u8 src " + key);
            insertM3U8SourceToDb(mLessonId, key);
        }
    }

    /**
     * 插入每个需要下载的资源 url md5保存
     *
     * @param lessonId
     * @param url
     */
    private void insertM3U8SourceToDb(int lessonId, String url) {
        ContentValues cv = new ContentValues();
        cv.put("lessonId", lessonId);
        cv.put("finish", 0);
        cv.put("url", DigestUtils.md5(url));
        mSqliteUtil.insert("data_m3u8_url", cv);
    }

    private void updateDownloadStatus(String url, int finish) {
        ContentValues cv = new ContentValues();
        cv.put("finish", finish);
        int result = mSqliteUtil.update(
                "data_m3u8_url",
                cv,
                "url=?",
                new String[]{DigestUtils.md5(url)}
        );
        Log.d(TAG, "update m3u8 src result " + result);
        if (result > 0) {
            //更新总计数器
            String updateSql = "update data_m3u8 set download_num=download_num+1 where userId=%d and host='%s' and lessonId=%d";
            mSqliteUtil.execSQL(String.format(updateSql, mUserId, mTargetHost, mLessonId));
            M3U8DbModle m3U8DbModle = queryM3U8Modle(mContext, mUserId, mLessonId, mTargetHost, ALL);

            if (m3U8DbModle.downloadNum == m3U8DbModle.totalNum) {
                String playListStr = createLocalM3U8File(m3U8DbModle);
                Log.d(TAG, "m3U8DbModle-> finish");
                cv.put("play_list", playListStr);
                mSqliteUtil.update(
                        "data_m3u8",
                        cv,
                        "host=? and lessonId=? and userId=?",
                        new String[]{
                                mTargetHost,
                                String.valueOf(mLessonId),
                                String.valueOf(mUserId)
                        }
                );
            }
        }
        Log.d(TAG, "update m3u8 src status " + url);
    }

    public void cancelDownload() {
        isCancel = true;
        for (HttpGet get : mFutures) {
            get.abort();
        }

        mThreadPoolExecutor.shutdown();
        ClientConnectionManager manager = mHttpClient.getConnectionManager();
        if (manager != null) {
            manager.shutdown();
        }
        mHttpClient = null;
        mThreadPoolExecutor = null;
        mTimeOutList.clear();
        mTimeOutList = null;
    }

    private String createLocalM3U8File(M3U8DbModle m3U8DbModle) {
        String playList = m3U8DbModle.playList;
        StringBuffer stringBuffer = new StringBuffer();
        Matcher matcher = URL_PAT.matcher(playList);

        String replaceStr = "http://localhost:8800/" + mLessonId + "/";
        while (matcher.find()) {
            String url = matcher.group(2);
            String type = matcher.group(1);
            String key = DigestUtils.md5(url);
            if (type != null) {
                matcher.appendReplacement(
                        stringBuffer, type + "http://localhost:8800/ext_x_key/" + key);
            } else {
                matcher.appendReplacement(
                        stringBuffer, replaceStr + key);
            }
        }
        matcher.appendTail(stringBuffer);

        File dir = getLocalM3U8Dir();
        File m3u8File = new File(dir, "play.m3u8");
        FileUtils.writeFile(m3u8File.getAbsolutePath(), stringBuffer.toString());
        return stringBuffer.toString();
    }

    private void getResourceFromNet(final String url, final int type) {
        mThreadPoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                if (isCancel) {
                    return;
                }

                String key = DigestUtils.md5(url);
                HttpGet httpGet = new HttpGet(url);
                try {
                    Log.d(TAG, "download " + url);
                    HttpResponse response = mHttpClient.execute(httpGet);
                    if (type == KEY) {
                        ContentValues cv = new ContentValues();
                        cv.put("type", Const.CACHE_KEY_TYPE);
                        cv.put("key", "ext_x_key/" + key);
                        cv.put("value", EntityUtils.toString(response.getEntity()));
                        mSqliteUtil.insert("data_cache", cv);
                    } else {
                        File file = createLocalM3U8SourceFile(key);
                        if (file == null) {
                            Log.d(TAG, "file download error" + url);
                            return;
                        }
                        FileUtils.writeFile(file, response.getEntity().getContent());
                    }

                    updateDownloadStatus(url, 1);

                    //发送下载广播
                    Intent intent = new Intent(DownLoadStatusReceiver.ACTION);
                    intent.putExtra(Const.LESSON_ID, mLessonId);
                    intent.putExtra(Const.COURSE_ID, mCourseId);
                    intent.putExtra(Const.ACTIONBAR_TITLE, mLessonTitle);
                    mContext.sendBroadcast(intent);
                } catch (Exception e) {
                    //超时处理
                    int count = mTimeOutList.get(key);
                    if (count < 3) {
                        Log.d(TAG, "timeiout count " + count);
                        getResourceFromNet(url, type);
                        mTimeOutList.put(key, ++count);
                    }
                    e.printStackTrace();
                } finally {
                    httpGet.abort();
                }
            }
        }, 1, TimeUnit.MILLISECONDS);
    }

    /*
        开始下载m3u8资源
    */
    private void downloadM3U8SourceFile(M3U8File m3U8File) {
        Log.d(TAG, "start downloadM3U8SourceFile");
        ArrayList<String> keyList = m3U8File.keyList;
        ArrayList<String> urlList = m3U8File.urlList;

        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, 100);
        //超时
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        HttpConnectionParams.setSoTimeout(params, 3000);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        mHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schReg), params);

        for (String url : keyList) {
            getResourceFromNet(url, KEY);
        }

        for (String url : urlList) {
            getResourceFromNet(url, URL);
        }

    }

    private File getLocalM3U8Dir() {
        File workSpace = EdusohoApp.getWorkSpace();
        if (workSpace == null) {
            ToastUtils.show(mContext, "没有内存卡，不能下载视频文件!");
            return null;
        }

        StringBuffer dirBuilder = new StringBuffer(workSpace.getAbsolutePath());
        dirBuilder.append("/videos/")
                .append(mUserId)
                .append("/")
                .append(mTargetHost)
                .append("/")
                .append(mLessonId);

        File lessonDir = new File(dirBuilder.toString());
        if (!lessonDir.exists()) {
            lessonDir.mkdirs();
        }

        return lessonDir;
    }

    private File createLocalM3U8SourceFile(String name) {
        File m3u8FileDir = getLocalM3U8Dir();
        if (m3u8FileDir == null) {
            return null;
        }

        return new File(m3u8FileDir, name);
    }

    /*
        解析m3u8列表
    */
    private M3U8File parseM3u8ListFile(
            BufferedReader reader, HashMap<String, Integer> filters) {
        M3U8File m3U8File = new M3U8File();
        StringBuilder stringBuilder = new StringBuilder();
        int pos = -1;
        String oldKey = null;
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
                Matcher matcher = M3U8_STREAM_PAT.matcher(line);
                if (matcher.find()) {
                    if (m3U8File.m3u8List == null) {
                        m3U8File.type = M3U8File.STREAM_LIST;
                        m3U8File.m3u8List = new ArrayList<M3U8ListItem>();
                    }

                    M3U8ListItem item = new M3U8ListItem();
                    item.bandwidth = Integer.parseInt(matcher.group(2));
                    item.programId = Integer.parseInt(matcher.group(1));
                    m3U8File.m3u8List.add(item);
                    pos = 0;
                    continue;
                }

                matcher = M3U8_EXT_X_KEY_PAT.matcher(line);
                if (matcher.find()) {
                    m3U8File.type = M3U8File.PLAY_LIST;
                    String key = matcher.group(1);
                    if (!key.equals(oldKey)) {
                        oldKey = key;
                        if (filters == null || !filters.containsKey(DigestUtils.md5(key))) {
                            m3U8File.keyList.add(key);
                        }
                    }
                    pos = 1;
                    continue;
                }

                matcher = M3U8_EXTINF_PAT.matcher(line);
                if (matcher.find()) {
                    m3U8File.type = M3U8File.PLAY_LIST;
                    pos = 2;
                    continue;
                }
                //判断url类型
                switch (pos) {
                    case 0:
                        M3U8ListItem m3U8ListItem = m3U8File.m3u8List.get(
                                m3U8File.m3u8List.size() - 1);
                        m3U8ListItem.url = line;
                        pos = -1;
                        break;
                    case 1:
                        break;
                    case 2:
                        if (filters == null || !filters.containsKey(DigestUtils.md5(line))) {
                            m3U8File.urlList.add(line);
                        }
                        pos = -1;
                        break;
                    default:
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        m3U8File.content = stringBuilder.toString();
        Log.d(TAG, "end parse m3u8 file ");
        return m3U8File;
    }
}
