package com.edusoho.kuozhi.v3.util;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.EdusohoApp;

/**
 * Created by Zhang on 2016/12/14.
 */

public class CollectUtil {

    public interface OnCollectSucceeListener{
        void onCollectSuccee();
    }

    public static void collectCourse(String courseId, final OnCollectSucceeListener onCollectSucceeListener){
        EdusohoApp.app.getUrl(EdusohoApp.app.bindUrl(Const.FAVORITE + "?courseId=" + courseId, true)
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response == null){
                    if(EdusohoApp.app.loginUser == null){

                    }
                }else if(response.equals("true")){
                    if(onCollectSucceeListener != null){
                        onCollectSucceeListener.onCollectSuccee();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtil.shortToast(EdusohoApp.app,"网络异常");
            }
        });
    }

    public static void uncollectCourse(String courseId, final OnCollectSucceeListener onCollectSucceeListener){
        EdusohoApp.app.getUrl(EdusohoApp.app.bindUrl(Const.UNFAVORITE + "?courseId=" + courseId, true)
                , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response == null){
                            if(EdusohoApp.app.loginUser == null){

                            }
                        }else if(response.equals("true")){
                            if(onCollectSucceeListener != null){
                                onCollectSucceeListener.onCollectSuccee();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonUtil.shortToast(EdusohoApp.app,"网络异常");
                    }
                });
    }
}
