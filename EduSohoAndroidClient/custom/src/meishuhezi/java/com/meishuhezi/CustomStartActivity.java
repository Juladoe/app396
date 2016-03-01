package com.meishuhezi;

import android.content.Intent;
import android.os.Bundle;
import com.android.volley.Response;
import com.edusoho.kuozhi.KuozhiActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.LinkedHashMap;
import cn.trinea.android.common.util.DigestUtils;

public class CustomStartActivity extends KuozhiActivity {

    String token = new String();
    String salt = new String();
    String securityKey = new String();
    String appId = new String("4028819952411C8301525D8CD5E20FA7");
    String appString = "weike_meishuhezi";
    HashMap hashMap = new HashMap();
    String avatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentIntent = getIntent();
        if (mCurrentIntent != null && !mCurrentIntent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
            startApp();
            return;
        }
        salt = mCurrentIntent.getStringExtra("salt");
        token = mCurrentIntent.getStringExtra("token");
        if (salt != null && token != null){
            loadUserInfo();
        }
    }

    protected void loadUserInfo() {
        String md5String = token+salt+appString;
        securityKey = DigestUtils.md5(md5String);
        RequestUrl requestUrl = bindCustomUrl("/api/2/userInfo.json", true,true,true,true);
        ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hashMap = parseJsonValue(
                        response, new TypeToken<HashMap>() {
                        });
                LinkedHashMap userInfoMap = (LinkedHashMap) hashMap.get("userInfo");
                if(userInfoMap.get("avatarUrl") != null){
                    avatarUrl = userInfoMap.get("avatarUrl").toString();
                }
                else {
                    avatarUrl = "";
                }

                bindOpenUser(mActivity, new String[]{
                        "WP",
                        DigestUtils.md5(userInfoMap.get("openId").toString()),
                        //传值id为64位过长 加密缩短
                        userInfoMap.get("realName").toString(),
                        avatarUrl
                });
            }
        }, null);
    }

    public void bindOpenUser(final BaseActivity activity, String[] params) {
        if (params == null) {
            CommonUtil.longToast(mContext, "授权失败!");
            return;
        }
        EdusohoApp app = activity.app;
        RequestUrl requestUrl = app.bindNewUrl(Const.BIND_LOGIN, false);
        requestUrl.setParams(new String[]{
                "type", params[0],
                "id", params[1],
                "name", params[2],
                "avatar", params[3]
        });

        final LoadDialog loadDialog = LoadDialog.create(activity);
        loadDialog.setMessage("登录中...");
        loadDialog.show();
        activity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadDialog.dismiss();
                UserResult userResult = activity.parseJsonValue(
                        response, new TypeToken<UserResult>() {
                        });
                activity.app.saveToken(userResult);
                activity.app.sendMessage(Const.THIRD_PARTY_LOGIN_SUCCESS, null);
            Bundle bundle = new Bundle();
            bundle.putString(Const.BIND_USER_ID, String.valueOf(activity.app.loginUser.id));
            activity.app.pushRegister(bundle);
        }
        }, null);
    }

    public RequestUrl bindCustomUrl(String url, boolean addToken ,boolean addSecurityKey ,boolean addSalt ,boolean addappId) {
        StringBuffer sb = new StringBuffer("http://open188.weike.wanpeng.com");
        sb.append(url);
        RequestUrl requestUrl = new RequestUrl(sb.toString());
        requestUrl.setParams(new String[]{
                "token", token + "",
                "securityKey", securityKey + "",
                "salt", salt + "",
                "appId", appId + ""
        });

        return requestUrl;
    }

}

