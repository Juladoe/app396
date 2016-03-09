package com.edusoho.kuozhi.v3.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.shard.ThirdPartyLogin;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * Created by howzhi on 15/7/7.
 */
public class OpenLoginUtil {

    private static final String EnterSchool = "enter_school";

    private NormalCallback<UserResult> mLoginhandler = new NormalCallback<UserResult>() {
        @Override
        public void success(UserResult obj) {
        }
    };

    private Context mContext;
    private String mAuthCancel;
    private Promise mPromise;

    private OpenLoginUtil(Context context) {
        this.mContext = context;
        mAuthCancel = mContext.getResources().getString(R.string.authorize_cancelled);
    }

    public static OpenLoginUtil getUtil(Context context) {
        return new OpenLoginUtil(context);
    }

    public void setLoginHandler(NormalCallback<UserResult> callback) {
        this.mLoginhandler = callback;
    }

    public void bindOpenUser(final BaseActivity activity, String[] params) {
        if (params == null) {
            CommonUtil.longToast(mContext, "授权失败!");
            return;
        }
        EdusohoApp app = activity.app;
        RequestUrl requestUrl = app.bindNewUrl(Const.BIND_LOGIN, false);
        requestUrl.setParams(new String[]{
                "type", params[3],
                "id", params[0],
                "name", params[1],
                "avatar", params[2]
        });

        Looper.prepare();
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
                mLoginhandler.success(userResult);
                SimpleDateFormat nowfmt = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                Date date = new Date();
                String entertime = nowfmt.format(date);
                saveEnterSchool(activity.app.defaultSchool.name, entertime, "登录名："+activity.app.loginUser.nickname, activity.app.domain);
            }
        }, null);
        Looper.loop();
    }

    private String[] getWeixinLoginResult(HashMap<String, Object> res) {
        String id = res.get("unionid").toString();
        String name = res.get("nickname").toString();
        String avatar = res.get("headimgurl").toString();

        return new String[]{id, name, avatar, "weixinmob"};
    }

    private String[] getWeiboLoginResult(HashMap<String, Object> res) {
        String id = res.get("id").toString();
        String name = res.get("name").toString();
        String avatar = res.get("avatar_large").toString();

        return new String[]{id, name, avatar, "weibo"};
    }

    private String[] getQQLoginResult(HashMap<String, Object> res) {
        String id = res.get("id").toString();
        String name = res.get("nickname").toString();
        String avatar = res.get("figureurl_qq_2").toString();

        return new String[]{id, name, avatar, "qq"};
    }

    public String[] bindByPlatform(String type, HashMap<String, Object> res) {
        String[] params = null;
        if ("QQ".equals(type)) {
            params = getQQLoginResult(res);
        } else if ("Wechat".equals(type)) {
            params = getWeixinLoginResult(res);
        } else if ("SinaWeibo".equals(type)) {
            params = getWeiboLoginResult(res);
        }

        return params;
    }

    private void startOpenLogin(final String type) {
        ThirdPartyLogin.getInstance(mContext).login(new PlatformActionListener() {

            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
                if (action == Platform.ACTION_USER_INFOR) {
                    try {
                        if (!res.containsKey("id")) {
                            res.put("id", platform.getDb().getToken());
                        }
                        String[] params = bindByPlatform(type, res);
                        mPromise.resolve(params);
                    } catch (Exception ex) {
                        Log.e("ThirdPartyLogin-->", ex.getMessage());
                    }
                }
            }

            @Override
            public void onError(Platform platform, int action, Throwable throwable) {
                platform.removeAccount();
            }

            @Override
            public void onCancel(Platform platform, int action) {
                CommonUtil.longToast(mContext, mAuthCancel);
            }
        }, type);
    }

    public Promise login(String type) {
        mPromise = new Promise();
        startOpenLogin(type);

        return mPromise;
    }

    public void saveEnterSchool(String schoolname,String entertime,String loginname,String schoolhost) {
        Map map = new HashMap();
        map.put("schoolname",schoolname);
        map.put("entertime",entertime);
        map.put("loginname",loginname);
        map.put("schoolhost",schoolhost);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (loadEnterSchool(EnterSchool) != null){
            list = loadEnterSchool(EnterSchool);
        }
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).get("schoolname").toString().equals(map.get("schoolname"))){
                list.remove(i);
                i--;
            }
        }
        list.add(map);
        if (list.size()>4){
            list.remove(0);
        }
        JSONArray mJsonArray;
        mJsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> itemMap = list.get(i);
            Iterator<Map.Entry<String, Object>> iterator = itemMap.entrySet().iterator();

            JSONObject object = new JSONObject();

            while (iterator.hasNext()) {
                Map.Entry<String,Object> entry = iterator.next();
                try {
                    object.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {

                }
            }
            mJsonArray.put(object);
        }

        SharedPreferences sp = mContext.getSharedPreferences("EnterSchool", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(EnterSchool, mJsonArray.toString());
        editor.commit();
    }

    private List<Map<String, Object>> loadEnterSchool(String fileName) {
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        SharedPreferences sp = mContext.getSharedPreferences("EnterSchool", Context.MODE_PRIVATE);
        String result = sp.getString(EnterSchool, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                Map<String, Object> itemMap = new HashMap<String, Object>();
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        itemMap.put(name, value);
                    }
                }
                datas.add(itemMap);
            }
        } catch (JSONException e) {

        }

        return datas;
    }
}
