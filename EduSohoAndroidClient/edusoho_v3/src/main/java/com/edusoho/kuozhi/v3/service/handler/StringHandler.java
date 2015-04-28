package com.edusoho.kuozhi.v3.service.handler;

import android.util.Log;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.v3.listener.ResultCallback;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;

/**
 * Created by howzhi on 14-10-24.
 */
public class StringHandler implements HttpRequestHandler {

    private ActionBarBaseActivity mActivity;

    public StringHandler(ActionBarBaseActivity actionBarBaseActivity) {
        this.mActivity = actionBarBaseActivity;
    }

    @Override
    public void handle(
            final HttpRequest httpRequest, final HttpResponse httpResponse, HttpContext httpContext)
            throws HttpException, IOException {

        String url = httpRequest.getRequestLine().getUri();
        RequestUrl requestUrl = mActivity.app.bindUrl(url.substring(1, url.length()), false);
        Log.d(null, "proxy url->" + requestUrl.url);
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                try {
                    Log.d(null, "proxy--->");
                    httpResponse.setEntity(new StringEntity(object));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
