package com.edusoho.kuozhi.v3.service;

/**
 * Created by suju on 17/3/8.
 */

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.edusoho.kuozhi.v3.util.ReportUtil;
import com.umeng.analytics.MobclickAgent;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import cn.trinea.android.common.util.FileUtils;

/**
 * Created by suju on 16/11/29.
 */
public class HttpClientDownloadService {

    public static final String TAG = "HCDownloadService";
    public static final String DOWNLOAD_COMPLETE_URL = "download_complete_url";
    public static final String DOWNLOAD_STATUS = "download_status";
    private Context mContext;

    static final String PROXY_HOST = "http://183.136.223.203:10010/";

    public HttpClientDownloadService(Context context) {
        this.mContext = context;
    }

    private void initRequestHeader(HttpURLConnection urlConnection, int timeOut) {
        urlConnection.setConnectTimeout(timeOut);
        urlConnection.setReadTimeout(timeOut);
        urlConnection.addRequestProperty("User-Agent", "Android kuozhi v3 downservice 1.0");
    }

    private boolean checkTargetFileIsWrited(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return file.length() > 0;
    }

    public void download(File file, String resourceUrl) {
        Log.i(TAG, "start download resourceUrl:" + resourceUrl);
        if (checkTargetFileIsWrited(file)) {
            sendDownloadCompleteBroadcastReceiver(resourceUrl, DownloadManager.STATUS_SUCCESSFUL);
            Log.i(TAG, String.format("download file:%s is exists", file));
            return;
        }

        try {
            Log.i(TAG, String.format("download from network:%s", resourceUrl));
            InputStream stream = proxyRequest("", resourceUrl, 5000);
            if (saveStreamToFile(stream, file, resourceUrl)) {
                sendDownloadCompleteBroadcastReceiver(resourceUrl, DownloadManager.STATUS_SUCCESSFUL);
                return;
            }
            Log.i(TAG, "download fail");
            MobclickAgent.reportError(
                    mContext,
                    String.format("download fail url:%s file:%s, %s", resourceUrl, file.getAbsolutePath(), ReportUtil.getReportInfo(mContext))
            );
        } catch (Exception ie) {
            ie.printStackTrace();
            MobclickAgent.reportError(mContext, ie);
        }

        //Proxy Request
        try {
            Log.i(TAG, String.format("download from proxy:%s", resourceUrl));
            MobclickAgent.reportError(mContext, String.format("download from proxy:%s", resourceUrl));
            InputStream stream = proxyRequest(PROXY_HOST, resourceUrl, 120000);
            if (saveStreamToFile(stream, file, resourceUrl)) {
                sendDownloadCompleteBroadcastReceiver(resourceUrl, DownloadManager.STATUS_SUCCESSFUL);
                return;
            }
            Log.i(TAG, "proxy request fail");
            MobclickAgent.reportError(
                    mContext,
                    String.format("proxy fail url:%s file:%s, %s", resourceUrl, file.getAbsolutePath(), ReportUtil.getReportInfo(mContext))
            );
        } catch (Exception e) {
            MobclickAgent.reportError(mContext, e);
        }
        sendDownloadCompleteBroadcastReceiver(resourceUrl, DownloadManager.STATUS_FAILED);
    }

    private boolean saveStreamToFile(InputStream stream, File file, String resourceUrl) {
        if (FileUtils.writeFile(file, stream) && checkTargetFileIsWrited(file)) {
            return true;
        }

        return false;
    }

    public InputStream proxyRequest(String proxyHost, String requestUrl, int timeout) throws Exception {
        URL url = new URL(PROXY_HOST + requestUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        initRequestHeader(urlConnection, timeout);
        urlConnection.setDoInput(true);
        urlConnection.connect();
        return urlConnection.getInputStream();
    }

    private void sendDownloadCompleteBroadcastReceiver(String url, int status) {
        Intent intent = new Intent(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intent.putExtra(DOWNLOAD_COMPLETE_URL, url);
        intent.putExtra(DOWNLOAD_STATUS, status);
        mContext.sendBroadcast(intent);
    }

}
