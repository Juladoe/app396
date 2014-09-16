package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Announcement;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.widget.CourseDetailsLessonWidget;
import com.edusoho.kuozhi.ui.widget.LearnStatusWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.LessonItemClickListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-3.
 */
public class CourseLearningFragment extends BaseFragment {

    private String mTitle;
    private String mCourseId;

    private TextView mCourseNoticeView;
    private LearnStatusWidget mCourseStatusView;
    private CourseDetailsLessonWidget mCourseLessonList;
    private View mBtnLayout;

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setContainerView(R.layout.course_learning_fragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.course_details_menu_exit) {
            Log.d(null, "exit course->");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void unLearnCourse()
    {
        RequestUrl url = app.bindUrl(Const.UN_LEARN_COURSE, true);
        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.course_learning_menu, menu);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mBtnLayout = view.findViewById(R.id.course_details_btn_layouts);
        mCourseLessonList = (CourseDetailsLessonWidget) view.findViewById(R.id.course_learning_lessonlist);
        mCourseStatusView = (LearnStatusWidget) view.findViewById(R.id.course_learning_status_widget);
        mCourseNoticeView = (TextView) view.findViewById(R.id.course_learning_notice);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(Const.ACTIONBAT_TITLE);
            mCourseId = bundle.getString(Const.COURSE_ID);
        }

        setTitle(mTitle);
        initCourseAnnouncement();

        mCourseLessonList.hideTitle();
        mCourseLessonList.initLesson(mCourseId, mActivity, true);
        mCourseLessonList.setItemClickListener(
                new LessonItemClickListener(mActivity, mCourseLessonList.getLessonListJson()));
        mCourseLessonList.onShow();
        showBtnLayout(mBtnLayout);

        app.sendMsgToTarget(
                CourseDetailsActivity.HIDE_COURSE_PIC, null, CourseDetailsActivity.class);
    }

    private void initCourseAnnouncement()
    {
        RequestUrl url = app.bindUrl(Const.COURSE_NOTICE, true);
        url.setParams(new String[]{
                "courseId", mCourseId
        });
        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                ArrayList<Announcement> announcements = mActivity.parseJsonValue(
                        object, new TypeToken<ArrayList<Announcement>>(){});

                if (announcements == null) {
                    return;
                }

                mCourseNoticeView.setText(announcements.get(0).content);
            }
        });
    }
}
