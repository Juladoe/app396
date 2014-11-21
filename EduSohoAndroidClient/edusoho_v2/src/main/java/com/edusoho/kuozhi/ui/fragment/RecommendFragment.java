package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.WeekCourseAdapter;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.adapter.SchoolBannerAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.SchoolAnnouncement;
import com.edusoho.kuozhi.model.SchoolBanner;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.DividerItemDecoration;
import com.edusoho.kuozhi.view.EdusohoViewPager;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import library.PullToRefreshBase;
import library.PullToRefreshScrollView;


/**
 * Created by howzhi on 14-8-7.
 */
public class RecommendFragment extends BaseFragment {

    private EdusohoViewPager mSchoolBanner;
    private TextView mSchoolAnnouncement;
    private EduSohoListView mWeekCourse;

    private PullToRefreshScrollView mRootView;
    public String mTitle = "推荐";

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
        mRootView = (PullToRefreshScrollView) view;
        mWeekCourse = (EduSohoListView) view.findViewById(R.id.recommend_week_course);
        mSchoolAnnouncement = (TextView) view.findViewById(R.id.recommend_sch_announcement);
        mSchoolBanner = (EdusohoViewPager) view.findViewById(R.id.school_banner);

        mRootView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mRootView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                initFragment(true);
                mRootView.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
            }
        });

        mSchoolAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupDialog.createNormal(
                        mActivity,
                        "网校公告",
                        mSchoolAnnouncement.getText().toString()
                ).show();
            }
        });
        initFragment(false);
    }

    private void initFragment(boolean isUpdate)
    {
        initSchoolBanner(isUpdate);
        initSchoolAnnouncement();
        initWeekCourse();
    }

    private void initWeekCourse()
    {
        RequestUrl url = app.bindUrl(Const.WEEK_COURSES, false);
        url.setParams(new String[]{
                "start", "0",
                "limit", "3"
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mWeekCourse.setLayoutManager(linearLayoutManager);
        mWeekCourse.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL_LIST));
        WeekCourseAdapter weekCourseAdapter = new WeekCourseAdapter(mContext, R.layout.found_course_list_item);
        weekCourseAdapter.setOnItemClick(new RecyclerViewListBaseAdapter.RecyclerItemClick() {
            @Override
            public void onItemClick(Object obj, int position) {
                Log.d(null, "position=" + position);
                Course course = (Course) obj;
                Bundle bundle = new Bundle();
                bundle.putInt(Const.COURSE_ID, course.id);
                bundle.putString(Const.ACTIONBAT_TITLE, course.title);
                startAcitivityWithBundle(CourseDetailsActivity.TAG, bundle);
            }
        });

        mWeekCourse.setAdapter(weekCourseAdapter);
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                CourseResult courseResult = mActivity.parseJsonValue(
                        object, new TypeToken<CourseResult>(){});
                mWeekCourse.pushData(courseResult.data);
                mWeekCourse.initListHeight();
            }
        });
    }

    private void initSchoolBanner(final boolean isUpdate)
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

                SchoolBannerAdapter adapter;
                if (isUpdate) {
                    mSchoolBanner.update(schoolBanners);
                } else {
                    adapter = new SchoolBannerAdapter(
                            mActivity, schoolBanners);
                    mSchoolBanner.setAdapter(adapter);
                    mSchoolBanner.setCurrentItem(1);
                }
            }
        });
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
