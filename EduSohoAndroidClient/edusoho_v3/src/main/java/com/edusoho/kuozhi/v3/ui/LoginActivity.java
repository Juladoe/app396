package com.edusoho.kuozhi.v3.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.OpenLoginUtil;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.view.EduSohoLoadingButton;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by JesseHuang on 15/5/22.
 */
public class LoginActivity extends ActionBarBaseActivity {

    public static final int TYPE_LOGIN = 1;
    public static final int OK = 1003;
    private static boolean isRun;
    private EditText etUsername;
    private EditText etPassword;
    private EduSohoLoadingButton mBtnLogin;
    private ImageView ivWeibo;
    private ImageView ivQQ;
    private ImageView ivWeixin;
    private TextView tvMore;
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
        mBtnLogin = (EduSohoLoadingButton) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(mLoginClickListener);
        ivWeibo = (ImageView) findViewById(R.id.iv_weibo);
        ivWeibo.setOnClickListener(mWeiboLoginClickListener);
        ivQQ = (ImageView) findViewById(R.id.iv_qq);
        ivQQ.setOnClickListener(mQQLoginClickListener);
        ivWeixin = (ImageView) findViewById(R.id.iv_weixin);
        ivWeixin.setOnClickListener(mWeChatLoginClickListener);
        tvMore = (TextView) findViewById(R.id.tv_more);
        tvMore.setOnClickListener(mMoreClickListener);
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

            mBtnLogin.setLoadingState();

            mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    UserResult userResult = mActivity.parseJsonValue(response, new TypeToken<UserResult>() {
                    });
                    if (userResult != null && userResult.user != null) {
                        mActivity.app.saveToken(userResult);
                        mActivity.setResult(LoginActivity.OK);
                        app.sendMessage(Const.LOGIN_SUCCESS, null);
                        Bundle bundle = new Bundle();
                        bundle.putString(Const.BIND_USER_ID, userResult.user.id + "");
                        app.pushRegister(bundle);
                        mBtnLogin.setSuccessState();
                        mBtnLogin.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.finish();
                            }
                        }, 500);
                    } else {
                        mBtnLogin.setInitState();
                        CommonUtil.longToast(mContext, getResources().getString(R.string.user_not_exist));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mBtnLogin.setInitState();
                    CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                }
            });
        }
    };

    private void bindOpenUser(String type, String id, String name, String avatar) {
        RequestUrl requestUrl = app.bindNewUrl(Const.BIND_LOGIN, false);
        requestUrl.setParams(new String[]{
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
                        response, new TypeToken<UserResult>() {
                        });
                app.saveToken(userResult);
                app.sendMessage(Const.THIRD_PARTY_LOGIN_SUCCESS, null);
                Bundle bundle = new Bundle();
                bundle.putString(Const.BIND_USER_ID, String.valueOf(app.loginUser.id));
                app.pushRegister(bundle);
                mActivity.finish();
            }
        }, null);
    }

    private void loginByPlatform(String type) {
        final OpenLoginUtil openLoginUtil = OpenLoginUtil.getUtil((ActionBarBaseActivity) mActivity);
        openLoginUtil.setLoginHandler(new NormalCallback<UserResult>() {
            @Override
            public void success(UserResult obj) {
                mActivity.finish();
            }
        });

        openLoginUtil.login(type).then(new PromiseCallback<String[]>() {
            @Override
            public Promise invoke(String[] obj) {
                openLoginUtil.bindOpenUser(LoginActivity.this, obj);
                return null;
            }
        });
    }

    private View.OnClickListener mWeiboLoginClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            loginByPlatform("SinaWeibo");
        }
    };

    private View.OnClickListener mQQLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loginByPlatform("QQ");
        }
    };


    private View.OnClickListener mWeChatLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loginByPlatform("Wechat");
        }
    };

    private View.OnClickListener mMoreClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.app.mEngine.runNormalPlugin("SettingActivity", mContext, null);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_register) {
            mActivity.app.mEngine.runNormalPlugin("RegisterActivity", mActivity, null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.up_to_down, R.anim.none);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
    }
}