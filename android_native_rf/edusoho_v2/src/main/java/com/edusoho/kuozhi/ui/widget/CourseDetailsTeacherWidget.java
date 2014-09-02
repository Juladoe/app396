package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by howzhi on 14-8-27.
 */
public class CourseDetailsTeacherWidget extends CourseDetailsLabelWidget {

    private View mContentView;
    private AQuery mAQuery;

    public CourseDetailsTeacherWidget(Context context) {
        super(context);
    }

    public CourseDetailsTeacherWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(AttributeSet attrs) {
        super.initView(attrs);
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.course_userinfo, null);

        mAQuery = new AQuery(mContentView);
        setLoadView();
        setContentView(mContentView);
    }

    public void initUser(int userId, final ActionBarBaseActivity mActivity)
    {
        String url = mActivity.app.bindUrl(Const.USERINFO);
        HashMap<String, String> params = mActivity.app.createParams(true, null);
        params.put("userId", userId + "");

        mActivity.ajaxPost(url, params, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLoadView.setVisibility(View.GONE);
                mContentView.setVisibility(View.VISIBLE);
                User user = mActivity.parseJsonValue(object, new TypeToken<User>(){});
                if (user == null) {
                    return;
                }
                mAQuery.id(R.id.course_userinfo_face).image(
                        user.mediumAvatar, false, true, 0, R.drawable.myinfo_default_face);
                mAQuery.id(R.id.course_userinfo_nickname).text(user.nickname);
                mAQuery.id(R.id.course_userinfo_sign).text(user.title);
                mAQuery.id(R.id.course_userinfo_about).text(AppUtil.coverCourseAbout(user.about));
            }
        });
    }
}
