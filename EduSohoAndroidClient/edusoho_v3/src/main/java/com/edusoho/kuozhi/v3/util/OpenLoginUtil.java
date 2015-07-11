package com.edusoho.kuozhi.v3.util;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.shard.ThirdPartyLogin;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * Created by howzhi on 15/7/7.
 */
public class OpenLoginUtil {

    private NormalCallback<UserResult> mLoginhandler = new NormalCallback<UserResult>() {
        @Override
        public void success(UserResult obj) {
        }
    };
    private ActionBarBaseActivity mActivity;
    private String mAuthCancel;
    private OpenLoginUtil(ActionBarBaseActivity activity) {
        this.mActivity = activity;
        mAuthCancel = mActivity.getResources().getString(R.string.authorize_cancelled);
    }

    public static OpenLoginUtil getUtil(ActionBarBaseActivity activity) {
        return new OpenLoginUtil(activity);
    }

    public void setLoginHandler(NormalCallback<UserResult> callback) {
        this.mLoginhandler = callback;
    }

    private void bindOpenUser(String[] params) {
        if (params == null) {
            CommonUtil.longToast(mActivity, "授权失败!");
            return;
        }
        EdusohoApp app = mActivity.app;
        RequestUrl requestUrl = app.bindNewUrl(Const.BIND_LOGIN, false);
        requestUrl.setParams(new String[]{
                "type", params[3],
                "id", params[0],
                "name", params[1],
                "avatar", params[2]
        });
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                UserResult userResult = mActivity.parseJsonValue(
                        response, new TypeToken<UserResult>() {
                        });
                mActivity.app.saveToken(userResult);
                mActivity.app.sendMessage(Const.THIRD_PARTY_LOGIN_SUCCESS, null);
                Bundle bundle = new Bundle();
                bundle.putString(Const.BIND_USER_ID, String.valueOf(mActivity.app.loginUser.id));
                mActivity.app.pushRegister(bundle);
                mLoginhandler.success(userResult);
            }
        }, null);
    }

    private String[] getWeixinLoginResult(HashMap<String, Object> res) {
        String id = res.get("unionid").toString();
        String name = res.get("nickname").toString();
        String avatar = res.get("headimgurl").toString();

        return new String[]{id, name, avatar, "weixin"};
    }

    private String[] getWeiboLoginResult(HashMap<String, Object> res) {
        String id = res.get("id").toString();
        String name = res.get("name").toString();
        String avatar = res.get("avatar_large").toString();

        return new String[]{id, name, avatar, "weibo"};
    }

    private String[] getQQLoginResult(HashMap<String, Object> res, Platform platform) {
        String id = platform.getDb().getToken();
        String name = res.get("nickname").toString();
        String avatar = res.get("figureurl_qq_2").toString();

        return new String[]{id, name, avatar, "qq"};
    }

    private void startOpenLogin(final String type) {
        ThirdPartyLogin.getInstance(mActivity).login(new PlatformActionListener() {

            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
                if (action == Platform.ACTION_USER_INFOR) {
                    try {
                        String[] params = null;
                        if ("QQ".equals(type)) {
                            params = getQQLoginResult(res, platform);
                        } else if ("Wechat".equals(type)) {
                            params = getWeixinLoginResult(res);
                        } else if ("SinaWeibo".equals(type)) {
                            params = getWeiboLoginResult(res);
                        }
                        bindOpenUser(params);
                    } catch (Exception ex) {
                        Log.e("ThirdPartyLogin-->", ex.getMessage());
                    }
                }
            }

            @Override
            public void onError(Platform platform, int action, Throwable throwable) {
            }

            @Override
            public void onCancel(Platform platform, int action) {
                CommonUtil.longToast(mActivity, mAuthCancel);
            }
        }, type);
    }

    public void login(String type) {
        startOpenLogin(type);
    }
}
