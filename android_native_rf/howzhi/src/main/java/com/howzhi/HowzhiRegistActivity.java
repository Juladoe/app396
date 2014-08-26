package com.howzhi;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.ui.common.RegistActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by howzhi on 14-8-23.
 */
public class HowzhiRegistActivity extends RegistActivity {

    @Override
    protected void registUser(String email, String user, String pass)
    {
        String url = app.bindToken2Url(Const.REGIST, false);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", pass);
        params.put("nickname", user);

        ajaxPostString(url, params, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if (ajaxStatus.getCode() != Const.OK) {
                    longToast("网络异常！");
                    return;
                }
                TokenResult result = app.gson.fromJson(
                        object, new TypeToken<TokenResult>() {
                }.getType());
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
