package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.UserResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by JesseHuang on 15/5/23.
 */
public class LoginFragment extends BaseFragment {
    public static final String TAG = "LoginFragment";
    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;

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
        mEtUsername = (EditText) mContainerView.findViewById(R.id.et_username);
        mEtPassword = (EditText) mContainerView.findViewById(R.id.et_password);
        mBtnLogin = (Button) mContainerView.findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(mLoginClickListener);
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
            String username = mEtUsername.getText().toString().trim();
            String password = mEtPassword.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                CommonUtil.longToast(mContext, "请输入用户名");
                mEtUsername.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                CommonUtil.longToast(mContext, "请输入密码");
                mEtPassword.requestFocus();
                return;
            }
            RequestUrl requestUrl = mActivity.app.bindUrl(Const.LOGIN, false);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("_username", mEtUsername.getText().toString().trim());
            params.put("_password", mEtPassword.getText().toString().trim());

            final LoadDialog loadDialog = LoadDialog.create(mActivity);
            loadDialog.setMessage("登录中...");
            loadDialog.show();
            mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loadDialog.dismiss();
                    UserResult userResult = mActivity.parseJsonValue(response.toString(), new TypeToken<UserResult>() {
                    });
                    if (userResult.meta.code != Const.RESULT_CODE_ERROR) {
                        mActivity.app.saveToken(userResult.data);
                        mActivity.setResult(LoginActivity.OK);
                        app.sendMessage(Const.LOGIN_SUCCESS, null);
                        mActivity.finish();
                    } else {
                        CommonUtil.longToast(mContext, userResult.meta.message);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadDialog.dismiss();
                    CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                }
            });
        }
    };
}
