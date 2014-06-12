package com.edusoho.kowzhi.util;

import android.content.Context;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kowzhi.EdusohoApp;
import com.edusoho.kowzhi.view.LoadDialog;

public class NetUtil {

    /**
     * @param context
     * @param url
     */


    public static void modelAjax(Context context, String url, final modelAjaxCallback<String> modelCallback) {
        final LoadDialog dlg = LoadDialog.create(context);
        dlg.show();
        EdusohoApp.app.query.ajax(
                url, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                super.callback(url, object, status);
                modelCallback.callback(object);
            }
        });
    }

    /**
     * @author howzhi
     */
    public static abstract class modelAjaxCallback<T> {
        public abstract void callback(T t);
    }
}
