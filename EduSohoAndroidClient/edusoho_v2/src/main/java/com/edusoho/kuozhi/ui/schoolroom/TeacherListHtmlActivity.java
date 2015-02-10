package com.edusoho.kuozhi.ui.schoolroom;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.FollowFragment;
import com.edusoho.kuozhi.ui.fragment.ProfileFragment;
import com.edusoho.kuozhi.ui.htmlView.EduHtmlAppActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 15/2/10.
 */
public class TeacherListHtmlActivity extends EduHtmlAppActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initView() {
        super.initView();
        cordovaWebView.addJavascriptInterface(this, "edusoho");
    }

    @JavascriptInterface
    public void clickOnAndroid(final int id) {
        RequestUrl url = mActivity.app.bindUrl(Const.USERINFO, true);
        url.setParams(new String[]{
                "userId", id + ""
        });

        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                final User user = mActivity.gson.fromJson(object, new TypeToken<User>() {
                }.getType());
                if (user == null) {
                    ToastUtils.show(mContext, "获取不到该用户信息");
                    return;
                }
                EdusohoApp.app.mEngine.runNormalPlugin("FragmentPageActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, user.nickname);
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "ProfileFragment");
                        startIntent.putExtra(ProfileFragment.FOLLOW_USER, user);
                    }
                });
            }
        });
    }
}
