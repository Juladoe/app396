package com.edusoho.kuozhi.ui.liveCourse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.LiveingCourseListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.LivingCourse;
import com.edusoho.kuozhi.model.LivingCourseResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import library.PullToRefreshBase;

/**
 * Created by onewoman on 2015/1/30.
 */
public class LivingCourseActivity extends ActionBarBaseActivity {
    private RefreshListWidget mLivingCourseRefreshList;
    private View mLoading;
    private LiveingCourseListAdapter mLivingCourseListAdapter;
    private static final int LIVINGCOURSE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liveing_course_list_layout);
        init();
    }

    public void init() {
        setBackMode(BACK, "在学直播课程");
        mLivingCourseRefreshList = (RefreshListWidget) this.findViewById(R.id.liveing_course_refresh_list);
        mLoading = this.findViewById(R.id.load_layout);

        mLivingCourseRefreshList.setEmptyText(mActivity, R.layout.empty_page_layout, new String[]{"加入一些课程，再来这里看看吧~", ""},
                new String[]{"革命尚未成功，同志仍需努力", "暂无在学直播的课程"}, R.drawable.empty_logout, R.drawable.empty_no_data);
        mLivingCourseRefreshList.setMode(PullToRefreshBase.Mode.BOTH);
        mLivingCourseListAdapter = new LiveingCourseListAdapter(mActivity, R.layout.living_course_list_inflate);
        mLivingCourseRefreshList.setAdapter(mLivingCourseListAdapter);
        mLivingCourseRefreshList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final LivingCourse livingCourse = (LivingCourse) parent.getItemAtPosition(position);
                PluginRunCallback runCallback = new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.COURSE_ID, livingCourse.id);
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, livingCourse.title);
                    }
                };
                app.mEngine.runNormalPluginForResult("CoursePaperActivity", mActivity, LIVINGCOURSE, runCallback);
            }
        });
        getLiveingCourseRequest(0);
        refreshListener();
    }

    public void refreshListener() {
        mLivingCourseRefreshList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                getLiveingCourseRequest(mLivingCourseRefreshList.getStart());
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                getLiveingCourseRequest(0);
            }
        });
    }

    public void getLiveingCourseRequest(final int start) {
        if (app.loginUser == null) {
            mLivingCourseRefreshList.setLoginStatus(false);
            mLoading.setVisibility(View.GONE);
            mLivingCourseRefreshList.pushData(null);
            mLivingCourseRefreshList.setMode(PullToRefreshBase.Mode.DISABLED);
            return;
        } else {
            mLivingCourseRefreshList.setMode(PullToRefreshBase.Mode.BOTH);
            mLivingCourseRefreshList.setLoginStatus(true);
        }
        RequestUrl url = app.bindUrl(Const.LIVING_COURSE, true);
        url.setParams(new String[]{
                "start", String.valueOf(start),
                "limit", String.valueOf(Const.LIMIT)
        });
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mLoading.setVisibility(View.GONE);
                mLivingCourseRefreshList.onRefreshComplete();
                LivingCourseResult livingCourseResult = parseJsonValue(object, new TypeToken<LivingCourseResult>() {
                });
                mLivingCourseRefreshList.pushData(livingCourseResult.data);
                mLivingCourseRefreshList.setStart(start);
                if (livingCourseResult.data.size() < Const.LIMIT) {
                    mLivingCourseRefreshList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LoginActivity.LOGIN && resultCode == LoginActivity.OK) {
            getLiveingCourseRequest(0);
        }
    }
}