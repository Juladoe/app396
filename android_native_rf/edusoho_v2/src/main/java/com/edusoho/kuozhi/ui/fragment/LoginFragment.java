package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.common.QrSchoolActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.edusoho.plugin.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by howzhi on 14-8-21.
 */
public class LoginFragment extends BaseFragment {

    private AQuery aq;
    public static final String TAG = "LoginFragment";

    @Override
    public String getTitle() {
        return "登录";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.login_fragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.setTitle("登录");
        aq.id(R.id.login_email_edt).getView().requestFocus();
        Log.d(null, "LoginFragment->onResume");
    }

    @Override
    protected void initView(View view) {
        aq = new AQuery(view);

        aq.id(R.id.login_regist_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity loginActivity = (LoginActivity) getActivity();
                loginActivity.showFragment(RegistFragment.TAG);
            }
        });

        aq.id(R.id.login_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = aq.id(R.id.login_email_edt).getText().toString();
                if (TextUtils.isEmpty(email)) {
                    mActivity.longToast("请输入用户名或者邮箱");
                    return;
                }

                String pass = aq.id(R.id.login_pass_edt).getText().toString();
                if (TextUtils.isEmpty(pass)) {
                    mActivity.longToast("请输入密码");
                    return;
                }

                RequestUrl url = app.bindUrl(Const.LOGIN, false);
                HashMap<String, String> params = url.getParams();
                params.put("_username", email);
                params.put("_password", pass);
                url.setParams(params);

                mActivity.ajaxPostByLoding(url, new ResultCallback() {
                    @Override
                    public void callback(String url, String object, AjaxStatus ajaxStatus) {
                        TokenResult result = app.gson.fromJson(
                                object, new TypeToken<TokenResult>() {
                        }.getType());
                        if (result != null) {
                            app.saveToken(result);
                            mActivity.finish();
                            app.sendMsgToTarget(MyInfoFragment.REFRESH, null, MyInfoFragment.class);
                        } else {
                            mActivity.longToast("用户名或密码错误！");
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.menu.login_activity_menu) {
            Intent qrIntent = new Intent();
            qrIntent.setClass(mContext, CaptureActivity.class);
            startActivityForResult(qrIntent, QrSchoolActivity.REQUEST_QR);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(null, "loginfragment->onCreateOptionsMenu");
        inflater.inflate(R.menu.login_activity_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
