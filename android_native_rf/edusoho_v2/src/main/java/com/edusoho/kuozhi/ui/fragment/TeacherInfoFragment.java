package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ScrollListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.ui.widget.CourseDetailsTeacherWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import me.maxwin.view.XListView;

/**
 * Created by howzhi on 14-8-31.
 */
public class TeacherInfoFragment extends BaseFragment {

    private CourseDetailsTeacherWidget mTeacherView;
    public static final String TEACHER_ID = "teacherId";

    private int mTeacherId;

    private XListView mTeacherCoursesView;

    @Override
    public String getTitle() {
        return "教师";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.teacher_fragment);
    }

    @Override
    protected void initView(View view) {
        mTeacherCoursesView = (XListView) view.findViewById(R.id.course_details_teacher_course);
        mTeacherView = (CourseDetailsTeacherWidget) view.findViewById(R.id.course_details_teacher);

        Bundle bundle = getArguments();
        if (bundle == null) {
            mActivity.longToast("无效教师信息！");
            return;
        }

        mTeacherId = bundle.getInt(TEACHER_ID, 0);
        if (mTeacherId == 0) {
            mActivity.longToast("无效教师信息！");
            return;
        }
        mTeacherView.initUser(mTeacherId, mActivity);

        mTeacherCoursesView.setPullLoadEnable(false);
        mTeacherCoursesView.setPullRefreshEnable(false);
        final ScrollListAdapter adapter = new ScrollListAdapter(mContext);
        mTeacherCoursesView.setAdapter(adapter);

        RequestUrl url = app.bindUrl(Const.TEACHER_COURSES, true);
        url.setParams(new String[]{
                "userId", mTeacherId + ""
        });
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                ArrayList<Course> list = mActivity.parseJsonValue(
                        object, new TypeToken<ArrayList<Course>>(){});

                if (list == null) {
                    return;
                }

                adapter.addItemLast(list);
                initTeacherCourseListHeight();
            }
        });
    }

    private void initTeacherCourseListHeight()
    {
        int totalHeight = 0;

        ListAdapter adapter = mTeacherCoursesView.getAdapter();
        int count = adapter.getCount();
        for (int i=0; i < count; i = i + 2) {
            View child = adapter.getView(i, null, mTeacherCoursesView);
            child.measure(0, 0);
            totalHeight += child.getMeasuredHeight();
        }

        ViewGroup.LayoutParams lp = mTeacherCoursesView.getLayoutParams();
        lp.height = totalHeight;
        mTeacherCoursesView.setLayoutParams(lp);
    }
}
