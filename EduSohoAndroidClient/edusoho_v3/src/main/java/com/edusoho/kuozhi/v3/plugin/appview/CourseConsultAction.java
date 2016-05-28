package com.edusoho.kuozhi.v3.plugin.appview;

import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.ImChatActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by 菊 on 2016/4/11.
 */
public class CourseConsultAction {

    private BaseActivity mActivity;

    public CourseConsultAction(BaseActivity activity)
    {
        this.mActivity = activity;
    }

    public void invoke(final Bundle bundle) {
        RequestUrl requestUrl = mActivity.app.bindUrl(Const.USERINFO, false);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("userId", bundle.getString("userId"));
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                User user = mActivity.parseJsonValue(response, new TypeToken<User>() {
                });
                if (user != null) {
                    bundle.putString(Const.ACTIONBAR_TITLE, user.nickname);
                    bundle.putInt(ImChatActivity.FROM_ID, user.id);
                    bundle.putString(ImChatActivity.HEAD_IMAGE_URL, user.mediumAvatar);
                    bundle.putString(Const.NEWS_TYPE, PushUtil.ChatUserType.TEACHER);
                    mActivity.app.mEngine.runNormalPluginWithBundle("ImChatActivity", mActivity, bundle);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtil.shortToast(mActivity.getBaseContext(), "无法获取教师信息");
            }
        });
    }
}
