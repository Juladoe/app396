package com.edusoho.listener;

import com.androidquery.callback.AjaxStatus;

/**
 * Created by howzhi on 14-9-12.
 */
public interface AjaxResultCallback {

    public void callback(String url, String object, AjaxStatus ajaxStatus);
    public void error(String url, AjaxStatus ajaxStatus);
    public void update(String url, String object, AjaxStatus ajaxStatus);
}
