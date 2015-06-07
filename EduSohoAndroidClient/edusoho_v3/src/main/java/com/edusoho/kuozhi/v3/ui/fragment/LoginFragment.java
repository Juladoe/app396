package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
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
 * Created by JesseHuang on 15/5/23.
 */
public class LoginFragment extends BaseFragment {
    public static final String TAG = "LoginFragment";
    private EditText etUsername;
    private EditText etPassword;
    private Button mBtnLogin;
    private ImageView ivWeibo;
    private ImageView ivQQ;
    private ImageView ivWeixin;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_login);
        mActivity.setTitle("登录");
    }

    @Override
    protected void initView(View view) {
        etUsername = (EditText) mContainerView.findViewById(R.id.et_username);
        etPassword = (EditText) mContainerView.findViewById(R.id.et_password);
        mBtnLogin = (Button) mContainerView.findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(mLoginClickListener);
        ivWeibo = (ImageView) mContainerView.findViewById(R.id.iv_weibo);
        ivWeibo.setOnClickListener(mWeiboLoginClickListener);
        ivQQ = (ImageView) mContainerView.findViewById(R.id.iv_qq);
        ivQQ.setOnClickListener(mQQLoginClickListener);
        ivWeixin = (ImageView) mContainerView.findViewById(R.id.iv_weixin);
        ivWeixin.setOnClickListener(mWeChatLoginClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.register_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.register) {
            ((LoginActivity) mActivity).showFragment(RegisterFragment.TAG);
        }

        return super.onOptionsItemSelected(item);
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
                    app.sendMsgToTarget(DefaultPageActivity.XINGGE_PUSH_REGISTER, null, DefaultPageActivity.class);
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

    private View.OnClickListener mWeiboLoginClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ThirdPartyLogin.getInstance(mContext).login(new PlatformActionListener() {

                @Override
                public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
                    if (action == Platform.ACTION_USER_INFOR) {
                        try {
                            User user = new User();
                            user.nickname = res.get("name").toString();
                            user.largeAvatar = res.get("avatar_large").toString();
                            user.mediumAvatar = res.get("avatar_hd").toString();
                            user.smallAvatar = res.get("profile_image_url").toString();
                            user.thirdParty = platform.getDb().getPlatformNname();
                            app.saveToken(new UserResult(user, res.get("id").toString(), null));
                            app.sendMessage(Const.THIRD_PARTY_LOGIN_SUCCESS, null);
                            app.sendMsgToTarget(DefaultPageActivity.XINGGE_PUSH_REGISTER, null, DefaultPageActivity.class);
                            mActivity.finish();
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
                        User user = new User();
                        user.nickname = res.get("nickname").toString();
                        user.mediumAvatar = res.get("figureurl_qq_2").toString();
                        user.smallAvatar = res.get("figureurl_qq_1").toString();
                        user.thirdParty = platform.getDb().getPlatformNname();
                        app.saveToken(new UserResult(user, platform.getDb().getToken(), null));
                        app.sendMessage(Const.THIRD_PARTY_LOGIN_SUCCESS, null);
                        app.sendMsgToTarget(DefaultPageActivity.XINGGE_PUSH_REGISTER, null, DefaultPageActivity.class);
                        mActivity.finish();
                    }
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {

                }

                @Override
                public void onCancel(Platform platform, int i) {

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
                        User user = new User();
                        user.nickname = res.get("nickname").toString();
                        user.mediumAvatar = res.get("headimgurl").toString();
                        user.smallAvatar = res.get("headimgurl").toString();
                        user.thirdParty = platform.getDb().getPlatformNname();
                        app.saveToken(new UserResult(user, res.get("unionid").toString(), null));
                        app.sendMessage(Const.THIRD_PARTY_LOGIN_SUCCESS, null);
                        app.sendMsgToTarget(DefaultPageActivity.XINGGE_PUSH_REGISTER, null, DefaultPageActivity.class);
                        mActivity.finish();
                    }
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    Log.d("onError", "");
                }

                @Override
                public void onCancel(Platform platform, int i) {

                }
            }, Wechat.NAME);
        }
    };
    
}
