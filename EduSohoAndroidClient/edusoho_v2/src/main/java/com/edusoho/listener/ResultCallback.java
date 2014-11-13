package com.edusoho.listener;

import com.androidquery.callback.AjaxStatus;

/**
 * Created by howzhi on 14-5-18.
 */
public class ResultCallback implements AjaxResultCallback {
    public void callback(String url, String object, AjaxStatus ajaxStatus){};
    public void error(String url, AjaxStatus ajaxStatus){};
    public void update(String url, String object, AjaxStatus ajaxStatus){};
}
