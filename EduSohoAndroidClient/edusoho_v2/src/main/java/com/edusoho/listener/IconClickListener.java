package com.edusoho.listener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.FollowFragment;
import com.edusoho.kuozhi.ui.fragment.ProfileFragment;
import com.edusoho.kuozhi.util.Const;
import com.google.gson.reflect.TypeToken;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 15/2/10.
 * 头像点击时间
 */
public class IconClickListener implements View.OnClickListener {
    private String mNickName = "详细资料";
    private User mUser;
    private int mUserId;
    private ActionBarBaseActivity mActivity;

    public IconClickListener(ActionBarBaseActivity activity) {
        mActivity = activity;
    }

    public IconClickListener(ActionBarBaseActivity activity, int userId) {
        mActivity = activity;
        mUserId = userId;
    }

    public IconClickListener(ActionBarBaseActivity activity, User user) {
        mActivity = activity;
        mUser = user;
    }

    @Override
    public void onClick(View v) {
        final Bundle bundle = new Bundle();
        bundle.putString(FragmentPageActivity.FRAGMENT, "ProfileFragment");

        if (mUserId != 0) {
            RequestUrl url = mActivity.app.bindUrl(Const.USERINFO, true);
            url.setParams(new String[]{
                    "userId", mUserId + ""
            });

            mActivity.ajaxPost(url, new ResultCallback() {
                @Override
                public void callback(String url, String object, AjaxStatus ajaxStatus) {
                    mUser = mActivity.gson.fromJson(object, new TypeToken<User>() {
                    }.getType());
                    if (mUser == null) {
                        ToastUtils.show(mActivity, "获取不到该用户信息");
                        return;
                    } else {
                        bundle.putString(Const.ACTIONBAR_TITLE, mUser.nickname);
                        bundle.putSerializable(ProfileFragment.FOLLOW_USER, mUser);
                        EdusohoApp.app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
                    }
                }
            });
        } else {
            if (mUser == null) {
                mUser = EdusohoApp.app.loginUser;
            }
            bundle.putString(Const.ACTIONBAR_TITLE, mUser.nickname);
            bundle.putSerializable(ProfileFragment.FOLLOW_USER, mUser);
            EdusohoApp.app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
        }
    }
}
