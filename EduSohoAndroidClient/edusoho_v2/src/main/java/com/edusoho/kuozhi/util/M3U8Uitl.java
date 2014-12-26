package com.edusoho.kuozhi.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.broadcast.DownLoadStatusReceiver;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.model.m3u8.M3U8File;
import com.edusoho.kuozhi.model.m3u8.M3U8ListItem;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.trinea.android.common.util.DigestUtils;
import cn.trinea.android.common.util.FileUtils;
import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by howzhi on 14/12/10.
 */
public class M3U8Uitl {

    private static Pattern M3U8_STREAM_PAT = Pattern.compile(
            "#EXT-X-STREAM-INF:PROGRAM-ID=(\\d+),BANDWIDTH=(\\d+)", Pattern.DOTALL);
    private static Pattern M3U8_EXTINF_PAT = Pattern.compile(
            "#EXTINF:([\\d\\.]+),", Pattern.DOTALL);
    private static Pattern M3U8_EXT_X_KEY_PAT = Pattern.compile(
            "#EXT-X-KEY:METHOD=AES-128,URI=\"([^,\"]+)\",IV=(\\w+)", Pattern.DOTALL);
    private static Pattern URL_PAT = Pattern.compile("(http://[^\"\n]+)", Pattern.DOTALL);

    private Context mContext;
    private LessonItem mLessonItem;
    private EdusohoApp app;
    private SqliteUtil mSqliteUtil;
    private String mTargetHost;
    private HttpClient mHttpClient;

    private Hashtable<String, Integer> mTimeOutList;

    private static final String TAG = "M3U8Uitl";
    private ScheduledThreadPoolExecutor mThreadPoolExecutor;

    public M3U8Uitl(Context context) {
        this.mContext = context;
        this.app = EdusohoApp.app;

        Uri hostUri = Uri.parse(app.host);
        if (hostUri != null) {
            this.mTargetHost = hostUri.getHost();
        }
        mTimeOutList = new Hashtable<String, Integer>();
        mThreadPoolExecutor = new ScheduledThreadPoolExecutor(3);
        mSqliteUtil = new SqliteUtil(mContext, null, null);
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
    public static M3U8DbModle queryM3U8Modle(Context context, int id, String host) {
        SqliteUtil.QueryPaser<M3U8DbModle> queryCallBack =
                new SqliteUtil.QueryPaser<M3U8DbModle>() {
                    @Override
                    public M3U8DbModle parse(Cursor cursor) {
                        return parseM3U8Modle(cursor);
                    }
                };

        M3U8DbModle m3U8DbModle = SqliteUtil.getUtil(context).query(
                queryCallBack,
                "select * from data_m3u8 where host=? and lessonId=?",
                host, String.valueOf(id)
        );

        return m3U8DbModle;
    }

    public static SparseArray<M3U8DbModle> getM3U8ModleList(
            Context context, int[] ids, String host) {
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

        SqliteUtil.getUtil(context).query(
                queryCallBack,
                "select * from data_m3u8 where host=? and lessonId in " + lessonIds,
                host
        );

        return list;
    }

    private void loadLessonUrl(int lessonId, int courseId) {
        RequestUrl requestUrl = app.bindUrl(Const.COURSELESSON, true);
        requestUrl.setParams(new String[]{
                "courseId", String.valueOf(courseId),
                "lessonId", String.valueOf(lessonId)
        });

        app.postUrl(requestUrl, new ResultCallback() {
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
                mLessonItem = lessonItem;
                mThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        parseM3U8();
                    }
                });
            }
        });
    }

    public void download(int lessonId, int courseId) {
        clearM3U8Db(lessonId);
        M3U8DbModle m3U8DbModle = queryM3U8Modle(mContext, lessonId, mTargetHost);
        Log.d(TAG, "M3U8DbModle " + m3U8DbModle);
        if (m3U8DbModle != null) {
            return;
        }

        Log.d(TAG, "load lesson " + lessonId);
        loadLessonUrl(lessonId, courseId);
    }

    /**
     * 解析m3u8
     */
    private void parseM3U8() {
        M3U8File m3U8File = null;
        String url = mLessonItem.mediaUri;
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
            m3U8File = parseM3u8ListFile(inputStream);
            type = m3U8File.type;
        }

        Log.d(TAG, "m3U8File " + m3U8File);
        if (m3U8File != null) {
            //File m3u8File = createLocalM3U8File(lessonItem.title);
            initM3U8DataToDb(m3U8File);
            downloadM3U8SourceFile(m3U8File);
        }
    }

    private void clearM3U8Db(int lessonId) {
        Log.d(TAG, "clear m3u8 db");
        mSqliteUtil.delete(
                "data_m3u8",
                "lessonId = ?",
                new String[]{String.valueOf(lessonId)}
        );
        //清除以前的数据库缓存项
        mSqliteUtil.delete(
                "data_m3u8_url",
                "lessonId=?",
                new String[]{String.valueOf(lessonId)}
        );
    }

    /*
    初始化需要下载的m3u8列表
    */
    private void initM3U8DataToDb(M3U8File m3U8File) {
        clearM3U8Db(mLessonItem.id);
        Log.d(TAG, "initM3U8DataToDb");
        ContentValues cv = new ContentValues();
        cv.put("finish", 0);
        cv.put("total_num", m3U8File.urlList.size() + m3U8File.keyList.size());
        cv.put("download_num", 0);
        cv.put("lessonId", mLessonItem.id);
        cv.put("host", mTargetHost);
        cv.put("play_list", m3U8File.content);
        mSqliteUtil.insert("data_m3u8", cv);

        //插入需要下载的任务
        for (String key : m3U8File.keyList) {
            Log.d(TAG, "insert m3u8 key " + key);
            insertM3U8SourceToDb(mLessonItem.id, key);
        }
        for (String key : m3U8File.urlList) {
            Log.d(TAG, "insert m3u8 src " + key);
            insertM3U8SourceToDb(mLessonItem.id, key);
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
            String updateSql = "update data_m3u8 set download_num=download_num+1 where host='%s' and lessonId=%d";
            mSqliteUtil.execSQL(String.format(updateSql, mTargetHost, mLessonItem.id));
            M3U8DbModle m3U8DbModle = queryM3U8Modle(mContext, mLessonItem.id, mTargetHost);
            if (m3U8DbModle.downloadNum == m3U8DbModle.totalNum) {
                Log.d(TAG, "m3U8DbModle->" + m3U8DbModle);
                createLocalM3U8File(m3U8DbModle);
            }
        }
        Log.d(TAG, "update m3u8 src status " + url);
    }

    private void createLocalM3U8File(M3U8DbModle m3U8DbModle)
    {
        File dir = getLocalM3U8Dir();
        File m3u8File = new File(dir, "play.m3u8");

        String playList = m3U8DbModle.playList;
        StringBuffer stringBuffer = new StringBuffer();
        Matcher matcher = URL_PAT.matcher(playList);

        String replaceStr = "http://localhost:8800/" + mLessonItem.id + "/";
        while (matcher.find()) {
            String url = matcher.group();
            matcher.appendReplacement(
                    stringBuffer, replaceStr + DigestUtils.md5(url));
        }
        matcher.appendTail(stringBuffer);

        FileUtils.writeFile(m3u8File.getAbsolutePath(), stringBuffer.toString());
    }

    private void getResourceFromNet(final String url) {
        mThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String key = DigestUtils.md5(url);
                HttpGet httpGet = new HttpGet(url);
                try {
                    Log.d(TAG, "download " + url);
                    HttpResponse response = mHttpClient.execute(httpGet);
                    File file = createLocalM3U8SourceFile(key);
                    if (file == null) {
                        Log.d(TAG, "file download error" + url);
                        return;
                    }
                    FileUtils.writeFile(file, response.getEntity().getContent());
                    updateDownloadStatus(url, 1);

                    //发送下载广播
                    Intent intent = new Intent(DownLoadStatusReceiver.ACTION);
                    intent.putExtra(Const.LESSON_ID, mLessonItem.id);
                    intent.putExtra(Const.COURSE_ID, mLessonItem.courseId);
                    intent.putExtra(Const.ACTIONBAR_TITLE, mLessonItem.title);
                    mContext.sendBroadcast(intent);
                } catch (Exception e) {
                    //超时处理
                    int count = mTimeOutList.get(key);
                    if (count < 3) {
                        Log.d(TAG, "timeiout count " + count);
                        getResourceFromNet(url);
                        mTimeOutList.put(key, ++count);
                    }
                    e.printStackTrace();
                } finally {
                    httpGet.abort();
                }
            }
        });
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
            getResourceFromNet(url);
        }

        for (String url : urlList) {
            getResourceFromNet(url);
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
                .append(mTargetHost)
                .append("/")
                .append(mLessonItem.id);

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
    private M3U8File parseM3u8ListFile(InputStream inputStream) {
        M3U8File m3U8File = new M3U8File();
        StringBuilder stringBuilder = new StringBuilder();
        int pos = -1;
        String oldKey = null;
        try {
            BufferedReader bufferedInputStream = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedInputStream.readLine()) != null) {
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
                    if (! key.equals(oldKey)) {
                        oldKey = key;
                        m3U8File.keyList.add(matcher.group(1));
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
                        m3U8File.urlList.add(line);
                        pos = -1;
                        break;
                    default:
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        m3U8File.content = stringBuilder.toString();
        Log.d(TAG, "end parse m3u8 file ");
        return m3U8File;
    }
}
