package com.edusoho.kuozhi.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

public class LoginActivity extends BaseActivity {
    public static final int EXIT = 1002;
    public static final int LOGIN = 1001;
    public static final int OK = 1003;
    private AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initView();
    }

    public static void start(Activity context)
    {
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startForResult(Activity context)
    {
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        context.startActivityForResult(intent, LOGIN);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(EXIT);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RegistActivity.RESULT:
                finish();
                break;
        }
    }

    private void initView() {
        setBackMode("登录", true, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(EXIT);
                finish();
            }
        });

        setMenu(R.layout.login_menu, new MenuListener() {
            @Override
            public void bind(View menuView) {
                View btn = menuView.findViewById(R.id.login_menu_btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent registIntent = new Intent();
                        registIntent.setClass(mContext, RegistActivity.class);
                        startActivityForResult(registIntent, RegistActivity.REQUEST);
                    }
                });
            }
        });

        aq = new AQuery(this);
        aq.id(R.id.login_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = aq.id(R.id.login_email_edt).getText().toString();
                if (TextUtils.isEmpty(email)) {
                    longToast("请输入用户名或者邮箱");
                    return;
                }

                String pass = aq.id(R.id.login_pass_edt).getText().toString();
                if (TextUtils.isEmpty(pass)) {
                    longToast("请输入密码");
                    return;
                }

                StringBuffer params = new StringBuffer(Const.LOGIN);
                params.append("?_username=").append(email);
                params.append("&_password=").append(pass);

                String url = app.bindToken2Url(params.toString(), false);
                ajaxGetString(url, new ResultCallback(){
                    @Override
                    public void callback(String url, String object, AjaxStatus status) {
                        TokenResult result = app.gson.fromJson(
                                object, new TypeToken<TokenResult>(){}.getType());
                        if (result != null) {
                            app.saveToken(result);
                            setResult(OK);
                            finish();
                        } else {
                            longToast("用户名或密码错误！");
                        }
                    }
                });
            }
        });
    }
}
