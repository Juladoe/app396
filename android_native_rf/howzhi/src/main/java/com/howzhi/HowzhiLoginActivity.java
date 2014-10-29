package com.howzhi;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by howzhi on 14-8-14.
 */
public class HowzhiLoginActivity extends LoginActivity {

    @Override
    protected void setLayout() {
        setContentView(R.layout.howzhi_login_layout);
    }

    @Override
    protected void loginUser(String email, String pass)
    {
        String url = app.bindToken2Url(Const.LOGIN + "?", false);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("_username", email);
        params.put("_password", pass);

        ajaxPostString(url, params, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                TokenResult result = app.gson.fromJson(
                        object, new TypeToken<TokenResult>() {
                }.getType());
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
}
