package com.edusoho.kuozhi.v3.cache.request.model;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by howzhi on 15/4/28.
 */
public class Response<T> {

    private String mMimeType;
    private String mEncoding;
    private int mStatusCode;
    private Map<String, String> mResponseHeaders;
    private InputStream mInputStream;
    private T mData;

    public String getMimeType() {
        return mMimeType;
    }

    public void setMimeType(String mMimeType) {
        this.mMimeType = mMimeType;
    }

    public String getEncoding() {
        return mEncoding;
    }

    public void setEncoding(String mEncoding) {
        this.mEncoding = mEncoding;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int mStatusCode) {
        this.mStatusCode = mStatusCode;
    }

    public Map<String, String> getResponseHeaders() {
        return mResponseHeaders;
    }

    public void setResponseHeaders(Map<String, String> mResponseHeaders) {
        this.mResponseHeaders = mResponseHeaders;
    }

    public InputStream getContent() {
        return mInputStream;
    }

    public void setContent(InputStream mInputStream) {
        this.mInputStream = mInputStream;
    }

    public T getData()
    {
        return mData;
    }

    public void setData(T data) {
        this.mData = data;
    }

    public boolean isEmpty()
    {
        return mInputStream == null;
    }
}
