package com.edusoho.kuozhi.ui.common;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

public class RegistActivity extends BaseActivity {

    private AQuery aq;
    public static final int RESULT = 1001;
    public static final int REQUEST = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);
        initView();
    }

    private void initView() {
        setBackMode("注册用户", true, null);
        aq = new AQuery(this);
        regist();
    }

    private void regist()
    {
        findViewById(R.id.regist_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = aq.id(R.id.regist_email_edt).getText().toString();
                if (TextUtils.isEmpty(email)) {
                    longToast("请输入邮箱地址");
                    return;
                }

                String user =  aq.id(R.id.regist_user_edt).getText().toString();
                if (TextUtils.isEmpty(user)) {
                    longToast("请输入昵称");
                    return;
                }

                String pass =  aq.id(R.id.regist_pass_edt).getText().toString();
                if (TextUtils.isEmpty(pass)) {
                    longToast("请输入密码");
                    return;
                }

                registUser(email, user, pass);
            }
        });
    }

    protected void registUser(String email, String user, String pass)
    {
        StringBuffer params = new StringBuffer();
        params.append("?email=").append(email);
        params.append("&nickname=").append(user);
        params.append("&password=").append(pass);

        String url = app.bindToken2Url(Const.REGIST + params.toString(), false);

        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if (ajaxStatus.getCode() != Const.OK) {
                    longToast("网络异常！");
                    return;
                }
                TokenResult result = app.gson.fromJson(
                        object, new TypeToken<TokenResult>(){}.getType());
                if (result != null) {
                    app.saveToken(result);
                    PopupDialog.createMuilt(
                            mContext,
                            "注册成功",
                            "恭喜你！网校账号注册成功！",
                            new PopupDialog.PopupClickListener() {
                                @Override
                                public void onClick(int button) {
                                    setResult(RESULT);
                                    finish();
                                }
                            }).show();
                } else {
                    PopupDialog.createNormal(
                            mContext, "注册失败", "账号注册失败！请重新尝试！").show();
                }
            }
        });
    }

}
