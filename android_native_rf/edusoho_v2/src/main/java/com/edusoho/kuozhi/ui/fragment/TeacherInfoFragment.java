package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.lesson.ScrollListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.widget.CourseDetailsTeacherWidget;
import com.edusoho.kuozhi.ui.widget.XCourseListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-8-31.
 */
public class TeacherInfoFragment extends BaseFragment {

    private CourseDetailsTeacherWidget mTeacherView;
    public static final String TEACHER_ID = "teacherId";

    private int[] mTeacherIds;

    private XCourseListWidget mTeacherCoursesView;

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
        mTeacherCoursesView = (XCourseListWidget) view.findViewById(R.id.course_details_teacher_course);
        mTeacherView = (CourseDetailsTeacherWidget) view.findViewById(R.id.course_details_teacher);

        Bundle bundle = getArguments();
        if (bundle == null) {
            mActivity.longToast("无效教师信息！");
            return;
        }

        mTeacherIds = bundle.getIntArray(TEACHER_ID);
        if (mTeacherIds == null || mTeacherIds.length == 0) {
            mActivity.longToast("无效教师信息！");
            return;
        }
        mTeacherView.initUser(mTeacherIds[0], mActivity);
        final ScrollListAdapter adapter = new ScrollListAdapter(mContext);
        mTeacherCoursesView.setAdapter(adapter);
        mTeacherCoursesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                final Course course = (Course) parent.getItemAtPosition(position);
                Bundle data = new Bundle();
                data.putInt(Const.COURSE_ID, course.id);
                data.putString(Const.ACTIONBAT_TITLE, course.title);
                data.putString(CourseDetailsActivity.COURSE_PIC, course.largePicture);

                mActivity.app.mEngine.runNormalPluginWithBundle(
                        CourseDetailsActivity.TAG, mActivity, data);
            }
        });

        RequestUrl url = app.bindUrl(Const.TEACHER_COURSES, true);
        url.setParams(new String[]{
                "userId", mTeacherIds[0] + ""
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
        ViewGroup listView = mTeacherCoursesView.getListView();
        for (int i=0; i < count; i = i + 2) {
            View child = adapter.getView(i, null, listView);
            child.measure(0, 0);
            totalHeight += child.getMeasuredHeight();
        }

        ViewGroup.LayoutParams lp = mTeacherCoursesView.getLayoutParams();
        lp.height = totalHeight + 40;
        mTeacherCoursesView.setLayoutParams(lp);
    }
}
