package com.edusoho.kuozhi.imserver.ui.util;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.edusoho.kuozhi.imserver.ui.entity.UpYunUploadResult;
import com.edusoho.kuozhi.imserver.util.SystemUtil;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.FileBody;
import com.koushikdutta.async.http.body.JSONObjectBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by suju on 16/8/28.
 */
public class UpYunUploadTask implements IResourceTask {

    private static final String TAG = "UpYunUploadTask";

    private File mTargetFile;
    private int mTargetId;
    private int mTaskId;
    private Map<String, String> mHeaders;
    private Future<String> mFuture;
    private TaskFeature mTaskFeature;

    public UpYunUploadTask(int taskId, int targetId, File targetFile, Map<String, String> headers) {
        this.mTaskId = taskId;
        this.mTargetId = targetId;
        this.mTargetFile = targetFile;
        this.mHeaders = headers;
    }

    @Override
    public int getTaskId() {
        return mTaskId;
    }

    @Override
    public void cancel() {
        if (mFuture != null) {
            mFuture.cancel();
        }
    }

    @Override
    public TaskFeature execute() {
        mTaskFeature = new TaskFeature(mTaskId, ITaskStatusListener.UPLOAD);
        String path = String.format(ApiConst.PUSH_HOST + ApiConst.GET_UPLOAD_INFO, mTargetId, mTargetFile.length(), mTargetFile.getName());
        AsyncHttpRequest request = new AsyncHttpGet(path);

        for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
            request.setHeader(entry.getKey(), entry.getValue());
        }
        mFuture = AsyncHttpClient.getDefaultInstance().executeString(request, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                UpYunUploadCallback upYunUploadCallback = new UpYunUploadCallback();
                if (e != null || TextUtils.isEmpty(result)) {
                    //CommonUtil.longToast(mActivity, getString(R.string.request_fail_text));
                    e.printStackTrace();
                    Log.d(TAG, "get upload info from upyun failed");
                    upYunUploadCallback.success(null);
                    return;
                }

                try {
                    UpYunUploadResult upYunUploadResult = new UpYunUploadResult();
                    JSONObject jsonObject = new JSONObject(result);
                    upYunUploadResult.setPutUrl(jsonObject.optString("putUrl"));
                    upYunUploadResult.setGetUrl(jsonObject.optString("getUrl"));

                    JSONArray jsonArray = jsonObject.optJSONArray("headers");
                    if (jsonArray == null || jsonArray.length() == 0) {
                        upYunUploadCallback.success(null);
                        return;
                    }
                    int length = jsonArray.length();
                    String[] headers = new String[length];
                    for (int i = 0; i < length; i++) {
                        headers[i] = jsonArray.optString(i);
                    }
                    upYunUploadResult.setHeaders(headers);
                    upYunUploadCallback.success(upYunUploadResult);
                } catch (JSONException je) {
                    upYunUploadCallback.success(null);
                }
            }
        });

        return mTaskFeature;
    }

    private class UpYunUploadCallback {

        public void success(UpYunUploadResult result) {
            if (result != null) {
                uploadUnYunMedia(result, mTargetFile, result.getHeaders());
            } else {
                mTaskFeature.fail();
            }
        }
    }

    public void saveUploadResult(String putUrl, String getUrl, int fromId) {

        String path = String.format(ApiConst.PUSH_HOST + ApiConst.SAVE_UPLOAD_INFO, fromId);
        AsyncHttpPost post = new AsyncHttpPost(path);
        JSONObject params = new JSONObject();
        try {
            params.put("putUrl", putUrl);
            params.put("getUrl", getUrl);
        } catch (JSONException e) {
        }

        JSONObjectBody body = new JSONObjectBody(params);
        post.setBody(body);
        mFuture = AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, String result) {
                if (ex != null) {
                    Log.d(TAG, "save upload info error");
                    return;
                }
                try {
                    JSONObject resultJsonObject = new JSONObject(result);
                    if ("success".equals(resultJsonObject.getString("result"))) {
                        Log.d(TAG, "save upload result success");
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "convert json to obj error");
                }
            }
        });
    }

    private void uploadUnYunMedia(final UpYunUploadResult result, File file, HashMap<String, String> headers) {

        AsyncHttpRequest post = new AsyncHttpRequest(Uri.parse(result.putUrl), "PUT");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.setHeader(entry.getKey(), entry.getValue());
        }
        FileBody fileBody = new FileBody(file);
        post.setBody(fileBody);
        mFuture = AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, String resopne) {
                if (ex != null || source.code() != 200) {
                    Log.d(TAG, "upload media res to upyun failed");
                    mTaskFeature.fail();
                    return;
                }

                saveUploadResult(result.putUrl, result.getUrl, mTargetId);
                mTaskFeature.success(result.getUrl);
            }
        });
    }
}
