package com.edusoho.kuozhi.v3.service.handler;

import android.net.Uri;
import android.util.Log;

import com.belladati.httpclientandroidlib.HttpEntity;
import com.belladati.httpclientandroidlib.HttpException;
import com.belladati.httpclientandroidlib.HttpHost;
import com.belladati.httpclientandroidlib.HttpRequest;
import com.belladati.httpclientandroidlib.HttpResponse;
import com.belladati.httpclientandroidlib.client.protocol.RequestExpectContinue;
import com.belladati.httpclientandroidlib.entity.InputStreamEntity;
import com.belladati.httpclientandroidlib.entity.StringEntity;
import com.belladati.httpclientandroidlib.impl.DefaultBHttpClientConnection;
import com.belladati.httpclientandroidlib.message.BasicHttpRequest;
import com.belladati.httpclientandroidlib.protocol.HttpContext;
import com.belladati.httpclientandroidlib.protocol.HttpCoreContext;
import com.belladati.httpclientandroidlib.protocol.HttpProcessor;
import com.belladati.httpclientandroidlib.protocol.HttpProcessorBuilder;
import com.belladati.httpclientandroidlib.protocol.HttpRequestExecutor;
import com.belladati.httpclientandroidlib.protocol.HttpRequestHandler;
import com.belladati.httpclientandroidlib.protocol.RequestConnControl;
import com.belladati.httpclientandroidlib.protocol.RequestContent;
import com.belladati.httpclientandroidlib.protocol.RequestTargetHost;
import com.belladati.httpclientandroidlib.protocol.RequestUserAgent;
import com.belladati.httpclientandroidlib.util.EntityUtils;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import cn.trinea.android.common.util.DigestUtils;

/**
 * Created by howzhi on 14/11/28.
 */
public class WebResourceHandler implements HttpRequestHandler {

    private static final String TAG = "FileHandler";

    private String mTargetHost;
    private ActionBarBaseActivity mActivity;

    public WebResourceHandler(String targetHost, ActionBarBaseActivity actionBarBaseActivity) {
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

        try {
            InputStream inputStream = mActivity.getAssets().open(url);
            httpResponse.setEntity(new InputStreamEntity(inputStream));
        } catch (Exception e) {
            Log.e(null, e.toString());
        }
    }

    private HttpEntity proxyRequest(String host, String url) {
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
            } else if (type.equals("video/mp2t")) {
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

    private static String reEncodeM3U8File(String text) {
        return text.replaceAll("http://", "http://localhost:5820/http://");
    }

    public class WrapInputStream extends BufferedInputStream {
        private String name;
        private FileOutputStream outputStream;
        private boolean mWriteMode;

        public WrapInputStream(InputStream in) {
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

    private File getVideoFile(String name) {
        File videoDir = getVideoDir();
        File videoFile = new File(videoDir, name);
        return videoFile;
    }

    private File getVideoDir() {
        File cacheDir = CommonUtil.getCacheFileDir();
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