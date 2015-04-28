package com.edusoho.kuozhi.v3.listener;

import com.androidquery.callback.AjaxStatus;

/**
 * Created by JesseHuang on 15/4/23.
 */
public interface AjaxResultCallback {

    public void callback(String url, String object, AjaxStatus ajaxStatus);

    public void error(String url, AjaxStatus ajaxStatus);

    public void update(String url, String object, AjaxStatus ajaxStatus);
}
