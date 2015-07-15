package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.TokenResult;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.widget.ButtonWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by howzhi on 14-8-21.
 */
public class RegistFragment extends BaseFragment {

    public static final String TAG = "RegistFragment";

    private EditText mEmailEdt;
    private EditText mUserNameEdt;
    private EditText mPassEdt;
    private EditText mPass2Edt;
    private ButtonWidget mRegistBtn;
    private View mRegistInfoBtn;
    public static final int OK = 001;

    @Override
    public String getTitle() {
        return "注册";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.regist);
    }

    @Override
    protected void initView(View view) {
        mEmailEdt = (EditText) view.findViewById(R.id.regist_email_edt);
        mEmailEdt.requestFocus();

        mRegistInfoBtn = view.findViewById(R.id.regist_info_lable);
        mUserNameEdt = (EditText) view.findViewById(R.id.regist_user_edt);
        mPassEdt = (EditText) view.findViewById(R.id.regist_pass_edt);
        mPass2Edt = (EditText) view.findViewById(R.id.regist_pass2_edt);
        mRegistBtn = (ButtonWidget) view.findViewById(R.id.regist_btn);

        mRegistInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String url = app.schoolHost + Const.USERTERMS;
                app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(AboutFragment.URL, url);
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "AboutFragment");
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, "服务条款");
                    }
                });
            }
        });

        mRegistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registUser()) {
                    mRegistBtn.setText("注  册");
                    mRegistBtn.setActionMode(false);
                    mActivity.setProgressBarIndeterminateVisibility(true);
                }
            }
        });
    }

    private void showUserTerm(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    private boolean registUser() {
        String email = mEmailEdt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mActivity.longToast("请输入邮箱地址");
            return false;
        }

        String nickname = mUserNameEdt.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            mActivity.longToast("请输入昵称");
            return false;
        }

        String pass = mPassEdt.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            mActivity.longToast("请输入密码");
            return false;
        }

        String pass2 = mPass2Edt.getText().toString();
        if (!pass.equals(pass2)) {
            mActivity.longToast("两次密码不相同");
            return false;
        }

        RequestUrl url = app.bindUrl(Const.REGIST, false);
        HashMap<String, String> params = url.getParams();
        params.put("email", email);
        params.put("nickname", nickname);
        params.put("password", pass);

        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mRegistBtn.setActionMode(true);
                mActivity.setProgressBarIndeterminateVisibility(false);
                TokenResult tokenResult = mActivity.parseJsonValue(
                        object, new TypeToken<TokenResult>() {
                        });

                if (tokenResult == null) {
                    mActivity.longToast("账号注册失败！请重新尝试！!");
                    return;
                }

                saveUserToken(tokenResult);
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                mRegistBtn.setActionMode(true);
                mActivity.setProgressBarIndeterminateVisibility(false);
            }
        });

        return true;
    }

    protected void saveUserToken(TokenResult tokenResult) {
        app.saveToken(tokenResult);
        mActivity.setResult(RegistFragment.OK);
        mActivity.finish();
        app.sendMsgToTarget(MineFragment.REFRESH, null, MineFragment.class);
        app.sendMsgToTarget(SchoolRoomFragment.REFRESH, null, SchoolRoomFragment.class);
    }
}
