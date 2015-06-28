package com.edusoho.kuozhi.v3.util;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.belladati.httpclientandroidlib.HttpEntity;
import com.belladati.httpclientandroidlib.entity.ContentType;
import com.belladati.httpclientandroidlib.entity.mime.MultipartEntityBuilder;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by JesseHuang on 15/6/28.
 */
public class MultipartRequest extends Request<String> {
    /**
     * 对应于服务端get('file')
     * requestUrl.setMuiltParams(new Object[]{"file", imageFile});
     */
    public static final String KEY = "file";
    public static final String TAG = "MutlipartRequest";
    private HttpEntity mHttpEntity;
    private Response.Listener<String> mListener;
    private RequestUrl mRequestUrl;

    public MultipartRequest(int method, RequestUrl requestUrl, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, requestUrl.url, errorListener);
        mListener = listener;
        mRequestUrl = requestUrl;
        mHttpEntity = buildMultipartEntity();
    }

    private HttpEntity buildMultipartEntity() {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        Iterator iterator = mRequestUrl.getAllParams().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            File file = (File) entry.getValue();
            builder.addBinaryBody(KEY, file, ContentType.create("image/jpeg"), file.getName());
        }
        return builder.build();
    }

    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String data = new String(response.data, "UTF-8");
            return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            Log.e(TAG, String.format("Couldn't API parse JSON response. NetworkResponse:%s", response.toString()), e);
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
