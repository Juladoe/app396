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
import com.edusoho.kuozhi.v3.cache.request.model.StringResponse;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
        if (!"qq".equals(params[3])) {
            requestUrl.setParams(new String[]{
                    "type", params[3],
                    "id", params[0],
                    "name", params[1],
                    "avatar", params[2],
            });
        } else {
            requestUrl.setParams(new String[]{
                    "type", params[3],
                    "id", params[0],
                    "name", params[1],
                    "avatar", params[2],
                    "unionid", getUnionid(params[5]),
            });
        }
        final String thirdPartyType = params.length > 4 ? params[4] : "";
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
                activity.app.loginUser.thirdParty = thirdPartyType;
                activity.app.sendMessage(Const.THIRD_PARTY_LOGIN_SUCCESS, null);
                Bundle bundle = new Bundle();
                bundle.putString(Const.BIND_USER_ID, String.valueOf(activity.app.loginUser.id));
                activity.app.pushRegister(bundle);
                mLoginhandler.success(userResult);
                SimpleDateFormat nowfmt = new SimpleDateFormat("登录时间：yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String entertime = nowfmt.format(date);
                saveEnterSchool(activity.app.defaultSchool.name, entertime, "登录账号：" + activity.app.loginUser.nickname, activity.app.domain);
            }
        }, null);
        Looper.loop();
    }

    private String getUnionid(String accessToken) {
        String unionIdStr = null;
        BufferedReader br = null;
        HttpsURLConnection conn = null;
        String https = String.format("https://graph.qq.com/oauth2.0/me?access_token=%s&unionid=1", accessToken);
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
            conn = (HttpsURLConnection) new URL(https).openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(50000);
            conn.setRequestMethod("GET");
            conn.connect();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line);
            String responseStr = sb.toString();
            unionIdStr = responseStr.substring(responseStr.indexOf("UID"), responseStr.length() - 5);
            Log.v("BindQQ", unionIdStr);
        } catch (Exception e) {
            Log.e("BindQQ", e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return unionIdStr;
    }

    private String[] getWeixinLoginResult(HashMap<String, Object> res) {
        String id = res.get("unionid").toString();
        String name = res.get("nickname").toString();
        String avatar = res.get("headimgurl").toString();

        return new String[]{id, name, avatar, "weixinmob", "Wechat"};
    }

    private String[] getWeiboLoginResult(HashMap<String, Object> res) {
        String id = res.get("id").toString();
        String name = res.get("name").toString();
        String avatar = res.get("avatar_large").toString();

        return new String[]{id, name, avatar, "weibo", "SinaWeibo"};
    }

    private String[] getQQLoginResult(HashMap<String, Object> res) {
        String id = res.get("id").toString();
        String name = res.get("nickname").toString();
        String avatar = res.get("figureurl_qq_2").toString();
        String accessToken = res.get("accessToken").toString();
        return new String[]{id, name, avatar, "qq", "QQ", accessToken};
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
                            res.put("id", platform.getDb().getUserId());
                            res.put("accessToken", platform.getDb().getToken());
                            platform.getDb().exportData();
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

    public void saveEnterSchool(String schoolname, String entertime, String loginname, String schoolhost) {
        Map map = new HashMap();
        String lable = new String();
        if (schoolname.length() != 0) {
            lable = schoolname.substring(0, 2);
        }
        map.put("lable", lable);
        map.put("schoolname", schoolname);
        map.put("entertime", entertime);
        map.put("loginname", loginname);
        map.put("schoolhost", schoolhost);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (loadEnterSchool(EnterSchool) != null) {
            list = loadEnterSchool(EnterSchool);
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get("schoolhost").toString().equals(map.get("schoolhost"))) {
                list.remove(i);
                i--;
            }
        }
        list.add(map);
        if (list.size() > 4) {
            list.remove(0);
        }
        JSONArray mJsonArray;
        mJsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> itemMap = list.get(i);
            Iterator<Map.Entry<String, Object>> iterator = itemMap.entrySet().iterator();

            JSONObject object = new JSONObject();

            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
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

    private class MyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
            return true;
        }
    }

    private class MyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
