package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.SchoolBannerAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.SchoolAnnouncement;
import com.edusoho.kuozhi.model.SchoolBanner;
import com.edusoho.kuozhi.ui.course.CourseListActivity;
import com.edusoho.kuozhi.ui.widget.CourseListWidget;
import com.edusoho.kuozhi.ui.widget.HorizontalListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoViewPager;
import com.edusoho.listener.CourseListScrollListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


/**
 * Created by howzhi on 14-8-7.
 */
public class RecommendFragment extends BaseFragment {

    private EdusohoViewPager mSchoolBanner;
    private TextView mSchoolAnnouncement;
    private CourseListWidget mRecommendCourses;
    private CourseListWidget mNewCourses;
    private HorizontalListWidget mWeekCourse;
    public String mTitle = "推荐";

    private View mWeekCourseLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.recommend_layout);
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    protected void initView(View view)
    {
        mWeekCourse = (HorizontalListWidget) view.findViewById(R.id.recommend_week_course);
        mRecommendCourses = (CourseListWidget) view.findViewById(R.id.recommend_listview);
        mNewCourses = (CourseListWidget) view.findViewById(R.id.new_listview);
        mSchoolAnnouncement = (TextView) view.findViewById(R.id.recommend_sch_announcement);
        mSchoolBanner = (EdusohoViewPager) view.findViewById(R.id.school_banner);

        mWeekCourseLabel = view.findViewById(R.id.recommend_week_label);

        initSchoolBanner();
        initSchoolAnnouncement();
        initWeekCourse();
        initRecommendCourse();
        initNewCourse();
    }

    private void initNewCourse()
    {
        RequestUrl url = app.bindUrl(Const.LASTEST_COURSES, false);
        url.setParams(new String[]{
                "start", "0",
                "limit", "2"
        });

        mNewCourses.setFullHeight(true);
        mNewCourses.initialise(mActivity, url);

        mNewCourses.setShowMoreBtnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(null, "mNewCourses click->");
                app.mEngine.runNormalPlugin("CourseListActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(CourseListActivity.TYPE, CourseListActivity.LASTEST);
                        startIntent.putExtra(CourseListActivity.TITLE, "最新课程");
                    }
                });
            }
        });

        mNewCourses.setItemClick(new CourseListScrollListener(mActivity));
    }

    private void initWeekCourse()
    {
        RequestUrl url = app.bindUrl(Const.WEEK_COURSES, false);
        url.setParams(new String[]{
                "start", "0",
                "limit", "3"
        });

        mWeekCourse.setLabel(mWeekCourseLabel);
        mWeekCourse.initialise(mActivity, url);
        mWeekCourse.setOnItemClick(new CourseListScrollListener(mActivity));
    }

    private void initSchoolBanner()
    {
        RequestUrl url = app.bindUrl(Const.SCHOOL_BANNER, false);

        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                ArrayList<SchoolBanner> schoolBanners = app.gson.fromJson(
                        object, new TypeToken<ArrayList<SchoolBanner>>() {
                }.getType());

                if (schoolBanners == null || schoolBanners.isEmpty()) {
                    schoolBanners = new ArrayList<SchoolBanner>();
                    schoolBanners.add(SchoolBanner.def());
                }

                SchoolBannerAdapter adapter = new SchoolBannerAdapter(
                        mActivity, schoolBanners);
                mSchoolBanner.setAdapter(adapter);
                mSchoolBanner.setCurrentItem(0);
            }
        });
    }

    private void initRecommendCourse()
    {
        RequestUrl url = app.bindUrl(Const.RECOMMEND_COURSES, false);
        url.setParams(new String[]{
                "start", "0",
                "limit", "2"
        });

        mRecommendCourses.setFullHeight(true);
        mRecommendCourses.initialise(mActivity, url);
        
        mRecommendCourses.setShowMoreBtnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(null, "mRecommendCourses click->");
                app.mEngine.runNormalPlugin("CourseListActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(CourseListActivity.TITLE, "推荐课程");
                        startIntent.putExtra(CourseListActivity.TYPE, CourseListActivity.RECOMMEND);
                    }
                });
            }
        });

        mRecommendCourses.setItemClick(new CourseListScrollListener(mActivity));

    }

    private void initSchoolAnnouncement()
    {
        RequestUrl url = app.bindUrl(Const.SCHOOL_Announcement, false);
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                SchoolAnnouncement schoolAnnouncement = app.gson.fromJson(
                        object, new TypeToken<SchoolAnnouncement>() {
                }.getType());

                if (schoolAnnouncement == null) {
                    mSchoolAnnouncement.setText("暂无网校公告");
                    return;
                }
                mSchoolAnnouncement.setText(schoolAnnouncement.info);
            }
        });
    }
}
