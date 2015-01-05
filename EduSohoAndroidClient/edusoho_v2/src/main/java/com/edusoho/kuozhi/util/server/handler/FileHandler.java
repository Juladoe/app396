package com.edusoho.kuozhi.util.server.handler;

import android.net.Uri;
import android.util.Log;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.core.model.Cache;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.model.m3u8.M3U8DbModle;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.M3U8Uitl;
import com.edusoho.kuozhi.util.SqliteUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.protocol.RequestExpectContinue;
import ch.boye.httpclientandroidlib.entity.FileEntity;
import ch.boye.httpclientandroidlib.entity.InputStreamEntity;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.impl.DefaultBHttpClientConnection;
import ch.boye.httpclientandroidlib.message.BasicHttpRequest;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import ch.boye.httpclientandroidlib.protocol.HttpCoreContext;
import ch.boye.httpclientandroidlib.protocol.HttpProcessor;
import ch.boye.httpclientandroidlib.protocol.HttpProcessorBuilder;
import ch.boye.httpclientandroidlib.protocol.HttpRequestExecutor;
import ch.boye.httpclientandroidlib.protocol.HttpRequestHandler;
import ch.boye.httpclientandroidlib.protocol.RequestConnControl;
import ch.boye.httpclientandroidlib.protocol.RequestContent;
import ch.boye.httpclientandroidlib.protocol.RequestTargetHost;
import ch.boye.httpclientandroidlib.protocol.RequestUserAgent;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import cn.trinea.android.common.util.DigestUtils;

/**
 * Created by howzhi on 14-10-25.
 */
public class FileHandler implements HttpRequestHandler {

    private static final String TAG = "FileHandler";

    private String mTargetHost;
    private ActionBarBaseActivity mActivity;

    public FileHandler(String targetHost, ActionBarBaseActivity actionBarBaseActivity)
    {
        Uri hostUri = Uri.parse(targetHost);
        if (hostUri != null) {
            this.mTargetHost = hostUri.getHost();
        }
        this.mActivity = actionBarBaseActivity;
    }

    @Override
    public void handle(
            final HttpRequest httpRequest, final HttpResponse httpResponse, HttpContext httpContext)
            throws HttpException, IOException {

        String url = httpRequest.getRequestLine().getUri();
        url = url.substring(1, url.length());
        Uri queryUri = Uri.parse(url);

        String queryName = queryUri.toString();
        Log.d(null, "queryName->" + queryName);

        if (queryName.startsWith("playlist")) {
            int lessonId = AppUtil.parseInt(queryName.substring("playlist/".length(), queryName.length()));
            User loginUser = mActivity.app.loginUser;
            if (loginUser == null) {
                return;
            }
            M3U8DbModle m3U8DbModle = M3U8Uitl.queryM3U8Modle(
                    mActivity, loginUser.id, lessonId, this.mTargetHost, M3U8Uitl.ALL);
            if (m3U8DbModle != null) {
                httpResponse.setEntity(new StringEntity(m3U8DbModle.playList));
                return;
            }
        }

        //判断是不是key
        if (queryName.startsWith("ext_x_key")) {
            SqliteUtil sqliteUtil = SqliteUtil.getUtil(mActivity);
            Cache keyCache = sqliteUtil.query(
                    "select * from data_cache where key=? and type=?",
                    queryName,
                    Const.CACHE_KEY_TYPE
            );
            if (keyCache != null) {
                httpResponse.setEntity(new StringEntity(keyCache.value));
                return;
            }
        }

        //本地ts文件
        File videoFile = getLocalFile(queryName.toString());
        if (videoFile.exists()) {
            Log.d(null, "cache->" + videoFile);
            FileEntity fileEntity = new FileEntity(videoFile);
            //httpResponse.setHeader("Content-Type", "video/mp2t; charset=UTF-8");
            httpResponse.setEntity(fileEntity);
            return;
        }

        HttpEntity entity = proxyRequest(queryUri.getHost(), queryName);
        httpResponse.setEntity(entity);
    }

    private HttpEntity proxyRequest(String host, String url)
    {
        try {
            Log.d(TAG, String.format("proxy host->%s, url->%s", host, url));
            Socket outsocket = new Socket(host, 80);
            DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
            conn.bind(outsocket);

            HttpProcessor httpproc = HttpProcessorBuilder.create()
                    .add(new RequestContent())
                    .add(new RequestTargetHost())
                    .add(new RequestConnControl())
                    .add(new RequestUserAgent())
                    .add(new RequestExpectContinue())
                    .build();
            HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

            HttpRequest request = new BasicHttpRequest("GET", url);
            Log.d(TAG, "proxy url->" + request.getRequestLine().getUri());
            HttpCoreContext context = HttpCoreContext.create();

            HttpHost httpHost = new HttpHost(host, 80);
            context.setTargetHost(httpHost);
            httpexecutor.preProcess(request, httpproc, context);
            HttpResponse response = httpexecutor.execute(request, conn, context);
            httpexecutor.postProcess(response, httpproc, context);

            HttpEntity entity = response.getEntity();

            String type = entity.getContentType().getValue();
            if (type.equals("application/vnd.apple.mpegurl")) {
                String entityStr = EntityUtils.toString(entity);
                entityStr = reEncodeM3U8File(entityStr);
                return new StringEntity(entityStr, "application/vnd.apple.mpegurl", "utf-8");
            } else if (type.equals("video/mp2t")){
                WrapInputStream wrapInput = new WrapInputStream(url, entity.getContent());
                HttpEntity wrapEntity = new InputStreamEntity(wrapInput);
                return wrapEntity;
            }

            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String reEncodeM3U8File(String text)
    {
        return text.replaceAll("http://", "http://localhost:5820/http://");
    }

    public class WrapInputStream extends BufferedInputStream
    {
        private String name;
        private FileOutputStream outputStream;
        private boolean mWriteMode;

        public WrapInputStream(InputStream in)
        {
            super(in);
        }

        public WrapInputStream(String name, InputStream in) {
            super(in);
            this.name = name;
            try {
                String md5Name = DigestUtils.md5(name);
                File videoFile = getLocalFile(md5Name);
                outputStream = new FileOutputStream(videoFile);
                mWriteMode = true;
                Log.d(TAG, "create file->" + md5Name);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int read(byte[] b) throws IOException {
            if (mWriteMode) {
                outputStream.write(b);
            } else {
                Log.d(null, "temp read->");
            }
            return super.read(b);
        }

        @Override
        public synchronized int read(byte[] b, int off, int len)
                throws IOException {
            if (mWriteMode) {
                outputStream.write(b, off, len);
            }
            return super.read(b, off, len);
        }

        @Override
        public void close() throws IOException {
            super.close();
            if (mWriteMode) {
                outputStream.close();
            }
            Log.d(TAG, "outputStream close");
        }
    }

    private File getLocalFile(String name)
    {
        File videoDir = getVideoDir();
        File videoFile = new File(videoDir, name);
        return videoFile;
    }

    private File getVideoDir()
    {
        File workSpace = EdusohoApp.getWorkSpace();
        if (workSpace == null) {
            return null;
        }

        User loginUser = mActivity.app.loginUser;
        if (loginUser == null) {
            return null;
        }
        StringBuffer dirBuilder = new StringBuffer(workSpace.getAbsolutePath());
        dirBuilder.append("/videos/")
                .append(loginUser.id)
                .append("/")
                .append(mTargetHost);

        return new File(dirBuilder.toString());
    }
}