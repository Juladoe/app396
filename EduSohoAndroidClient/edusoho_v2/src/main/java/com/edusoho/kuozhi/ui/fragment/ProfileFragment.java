package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ProfileAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.UserRole;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Melomelon on 2014/12/31.
 */
public class ProfileFragment extends BaseFragment {


    public ProfileAdapter profileAdapter;

    private ListView mInfoList;

    public String mTitle = "详细资料";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.profile_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mInfoList = (ListView) view.findViewById(R.id.info_list);

        profileAdapter = new ProfileAdapter(mContext, R.layout.profile_item_header, app.loginUser, mActivity);

        if (isTeacher()) {
            loadTeachingCourse();
        } else {
            loadCourseList(0);
        }
    }

    /**
     * 获取在学课程
     *
     * @param start
     */
    public void loadCourseList(int start) {
        profileAdapter.setListViewLayout(R.layout.profile_item);
        RequestUrl url = app.bindUrl(Const.LEARNING, true);
        HashMap<String, String> params = url.getParams();
        params.put("start", String.valueOf(start));
        params.put("limit", String.valueOf(Const.LIMIT));

        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                CourseResult courseResult = mActivity.gson.fromJson(
                        object, new TypeToken<CourseResult>() {
                        }.getType());

                if (courseResult == null) {
                    return;
                }
                profileAdapter.addItems(courseResult.data);
                mInfoList.setAdapter(profileAdapter);
            }
        });
    }

    /**
     * 获取在教课程
     */
    public void loadTeachingCourse() {
        profileAdapter.setListViewLayout(R.layout.profile_item);
        RequestUrl url = app.bindUrl(Const.TEACHER_COURSES, true);
        url.setParams(new String[]{
                "userId", app.loginUser.id + ""
        });
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                ArrayList<Course> list = mActivity.parseJsonValue(
                        object, new TypeToken<ArrayList<Course>>() {
                        });

                if (list == null) {
                    return;
                }
                profileAdapter.addItems(list);
                mInfoList.setAdapter(profileAdapter);
            }
        });
    }

    public boolean isTeacher() {
        for (UserRole role : app.loginUser.roles) {
            if (role == UserRole.ROLE_TEACHER) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }
}
