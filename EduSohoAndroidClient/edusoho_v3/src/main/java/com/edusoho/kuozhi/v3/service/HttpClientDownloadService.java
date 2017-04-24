package com.edusoho.kuozhi.v3.service;

/**
 * Created by suju on 17/3/8.
 */

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    public HttpClientDownloadService(Context context) {
        this.mContext = context;
    }

    private void initRequestHeader(HttpURLConnection urlConnection) {
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(5000);
        urlConnection.addRequestProperty("User-Agent", "Android kuozhi v3 downservice 1.0");
    }

    private boolean checkTargetFileIsWrited(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return file.length() > 0;
    }

    public void download(File file, String resourceUrl) {
        try {
            URL url = new URL(resourceUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            initRequestHeader(urlConnection);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream stream = urlConnection.getInputStream();
            if (FileUtils.writeFile(file, stream) && checkTargetFileIsWrited(file)) {
                sendDownloadCompleteBroadcastReceiver(resourceUrl, DownloadManager.STATUS_SUCCESSFUL);
                return;
            }
            Log.i(TAG, "download fail");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        sendDownloadCompleteBroadcastReceiver(resourceUrl, DownloadManager.STATUS_FAILED);
    }

    private void sendDownloadCompleteBroadcastReceiver(String url, int status) {
        Intent intent = new Intent(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intent.putExtra(DOWNLOAD_COMPLETE_URL, url);
        intent.putExtra(DOWNLOAD_STATUS, status);
        mContext.sendBroadcast(intent);
    }
}
