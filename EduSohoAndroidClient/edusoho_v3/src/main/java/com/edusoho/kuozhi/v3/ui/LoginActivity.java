package com.edusoho.kuozhi.v3.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.shard.ThirdPartyLogin;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by JesseHuang on 15/5/22.
 */
public class LoginActivity extends ActionBarBaseActivity {

    public static final int TYPE_LOGIN = 1;
    public static final int OK = 1003;
    private static boolean isRun;
    private EditText etUsername;
    private EditText etPassword;
    private Button mBtnLogin;
    private ImageView ivWeibo;
    private ImageView ivQQ;
    private ImageView ivWeixin;
    private String mAuthCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setBackMode(BACK, "登录");
        mAuthCancel = mContext.getResources().getString(R.string.authorize_cancelled);
        initView();
    }

    private void initView() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(mLoginClickListener);
        ivWeibo = (ImageView) findViewById(R.id.iv_weibo);
        ivWeibo.setOnClickListener(mWeiboLoginClickListener);
        ivQQ = (ImageView) findViewById(R.id.iv_qq);
        ivQQ.setOnClickListener(mQQLoginClickListener);
        ivWeixin = (ImageView) findViewById(R.id.iv_weixin);
        ivWeixin.setOnClickListener(mWeChatLoginClickListener);
    }

    public static void startLogin(Activity activity) {
        synchronized (activity) {
            if (isRun) {
                return;
            }
            Intent intent = new Intent();
            intent.setClass(activity, LoginActivity.class);
            activity.startActivityForResult(intent, TYPE_LOGIN);
        }
    }

    private View.OnClickListener mLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                CommonUtil.longToast(mContext, "请输入用户名");
                etUsername.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                CommonUtil.longToast(mContext, "请输入密码");
                etPassword.requestFocus();
                return;
            }
            RequestUrl requestUrl = mActivity.app.bindUrl(Const.LOGIN, false);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("_username", etUsername.getText().toString().trim());
            params.put("_password", etPassword.getText().toString().trim());

            mActivity.ajaxPostWithLoading(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    UserResult userResult = mActivity.parseJsonValue(response, new TypeToken<UserResult>() {
                    });
                    mActivity.app.saveToken(userResult);
                    mActivity.setResult(LoginActivity.OK);
                    app.sendMessage(Const.LOGIN_SUCCESS, null);
                    Bundle bundle = new Bundle();
                    bundle.putString(Const.BIND_USER_ID, userResult.user.id + "");
                    app.pushRegister(bundle);
                    mActivity.finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                }
            }, "登录中...");
        }
    };

    private void bindOpenUser(String type, String id, String name, String avatar) {
        RequestUrl requestUrl = app.bindNewUrl(Const.BIND_LOGIN, false);
        requestUrl.setParams(new String[] {
                "type", type,
                "id", id,
                "name", name,
                "avatar", avatar
        });
        ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                UserResult userResult = mActivity.parseJsonValue(
                        response, new TypeToken<UserResult>(){});
                app.saveToken(userResult);
                app.sendMessage(Const.THIRD_PARTY_LOGIN_SUCCESS, null);
                Bundle bundle = new Bundle();
                bundle.putString(Const.BIND_USER_ID, String.valueOf(app.loginUser.id));
                app.pushRegister(bundle);
                mActivity.finish();
            }
        }, null);
    }

    private View.OnClickListener mWeiboLoginClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ThirdPartyLogin.getInstance(mContext).login(new PlatformActionListener() {

                @Override
                public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
                    if (action == Platform.ACTION_USER_INFOR) {
                        try {
                            String id = res.get("id").toString();
                            String name = res.get("name").toString();
                            String avatar = res.get("avatar_large").toString();
                            bindOpenUser("weibo", id, name, avatar);
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
                    CommonUtil.longToast(mContext, mAuthCancel);
                }
            }, SinaWeibo.NAME);
        }
    };

    private View.OnClickListener mQQLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ThirdPartyLogin.getInstance(mContext).login(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
                    if (action == Platform.ACTION_USER_INFOR) {
                        String id = platform.getDb().getToken();
                        String name = res.get("nickname").toString();
                        String avatar = res.get("figureurl_qq_2").toString();
                        bindOpenUser("qq", id, name, avatar);
                    }
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    CommonUtil.longToast(mContext, mAuthCancel);
                }
            }, QQ.NAME);
        }
    };


    private View.OnClickListener mWeChatLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ThirdPartyLogin.getInstance(mContext).login(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
                    if (action == Platform.ACTION_USER_INFOR) {
                        String id = res.get("unionid").toString();
                        String name = res.get("nickname").toString();
                        String avatar = res.get("headimgurl").toString();
                        bindOpenUser("weixin", id, name, avatar);
                    }
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    Looper.prepare();
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtil.longToast(mContext, "您尚未安装微信客户端");
                        }
                    });
                    Looper.loop();
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    CommonUtil.longToast(mContext, mAuthCancel);
                }
            }, Wechat.NAME);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
    }
}
