package com.edusoho.kuozhi.util.server.handler;

import android.net.Uri;
import android.util.Log;

import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

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

        Uri proxyUri = Uri.parse(url);

        String md5Name = DigestUtils.md5(proxyUri.toString());
        File videoFile = getVideoFile(md5Name);
        Log.d(TAG, "proxyUri-->" + proxyUri.toString());
        Log.d(TAG, "md5Name-->" + md5Name);
        if (videoFile.exists()) {
            Log.d(TAG, "cache-->" + videoFile);
            FileEntity fileEntity = new FileEntity(videoFile, "video/mp2t");
            Log.d(null, "file->" + fileEntity.getContentLength());
            httpResponse.setEntity(fileEntity);
            return;
        }
        HttpEntity entity = proxyRequest(proxyUri.getHost(), proxyUri.toString());
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
                File videoFile = getVideoFile(md5Name);
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

    private File getVideoFile(String name)
    {
        File videoDir = getVideoDir();
        File videoFile = new File(videoDir, name);
        return videoFile;
    }

    private File getVideoDir()
    {
        File cacheDir = AQUtility.getCacheDir(mActivity);
        File videoDir = new File(cacheDir, "videos");
        if (!videoDir.exists()) {
            videoDir.mkdir();
        }

        File hostDir = new File(videoDir, mTargetHost);
        if (!hostDir.exists()) {
            hostDir.mkdir();
        }

        return hostDir;
    }
}