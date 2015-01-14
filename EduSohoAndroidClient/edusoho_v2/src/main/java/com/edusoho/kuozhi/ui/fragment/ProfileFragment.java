package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ProfileAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.User;
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
    public static final String FOLLOW_USER = "follow_user";
    private static final int LEARNCOURSE = 0;
    public ProfileAdapter profileAdapter;

    private ListView mInfoList;

    public String mTitle = "详细资料";
    private User mUser;
    private String mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.profile_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mInfoList = (ListView) view.findViewById(R.id.info_list);
        Bundle bundle = mActivity.getIntent().getExtras();
        mUser = (User) bundle.getSerializable(FOLLOW_USER);
        mType = bundle.getString(FollowFragment.FOLLOW_TYPE);
        if (mUser == null) {
            mUser = app.loginUser;
        }

        profileAdapter = new ProfileAdapter(mContext, R.layout.profile_item_header, mUser, mActivity, mType);

        if (isTeacher()) {
            loadTeachingCourse();
        } else {
            loadCourseList(0);
        }
        mInfoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Course course = (Course) parent.getAdapter().getItem(position - 1);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Const.COURSE_ID, course.id);
                    bundle.putString(Const.ACTIONBAR_TITLE, course.title);
                    startActivityWithBundleAndResult("CorusePaperActivity", LEARNCOURSE, bundle);
                }
            }
        });
    }

    /**
     * 获取在学课程
     *
     * @param start
     */
    public void loadCourseList(int start) {
        profileAdapter.setListViewLayout(R.layout.profile_item);
        RequestUrl url = app.bindUrl(Const.LEARNING_WITHOUT_TOKEN, false);
        HashMap<String, String> params = url.getParams();
        params.put("userId", mUser.id + "");
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
                "userId", mUser.id + ""
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
        for (UserRole role : mUser.roles) {
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
