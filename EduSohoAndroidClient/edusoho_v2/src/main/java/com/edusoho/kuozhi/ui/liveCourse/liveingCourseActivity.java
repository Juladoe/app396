package com.edusoho.kuozhi.ui.liveCourse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.LiveingCourseListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.LiveingCourse;
import com.edusoho.kuozhi.model.LiveingCourseResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import library.PullToRefreshBase;

/**
 * Created by onewoman on 2015/1/30.
 */
public class liveingCourseActivity extends ActionBarBaseActivity {
    private RefreshListWidget mLiveingCourseRefreshList;
    private View mLoading;
    private LiveingCourseListAdapter mLiveingCourseListAdapter;
    private static final int LIVINGCOURSE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liveing_course_list_layout);
        init();
    }

    public void init() {
        setBackMode(BACK, "在学直播课程");
        mLiveingCourseRefreshList = (RefreshListWidget) this.findViewById(R.id.liveing_course_refresh_list);
        mLoading = this.findViewById(R.id.load_layout);

        mLiveingCourseRefreshList.setEmptyText(mActivity, R.layout.empty_page_layout, new String[]{"加入一些课程，再来这里看看吧~", ""},
                new String[]{"革命尚未成功，同志仍需努力", "还未有在学的课程"}, R.drawable.empty_logout, R.drawable.empty_no_data);
        mLiveingCourseRefreshList.setMode(PullToRefreshBase.Mode.BOTH);
        mLiveingCourseListAdapter = new LiveingCourseListAdapter(mActivity, R.layout.liveing_course_list_inflate);
        mLiveingCourseRefreshList.setAdapter(mLiveingCourseListAdapter);
        mLiveingCourseRefreshList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final LiveingCourse liveingCourse = (LiveingCourse) parent.getItemAtPosition(position);
                PluginRunCallback runCallback = new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.COURSE_ID, liveingCourse.id);
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, liveingCourse.title);
                    }
                };
                app.mEngine.runNormalPluginForResult("CorusePaperActivity", mActivity, LIVINGCOURSE, runCallback);
            }
        });
        getLiveingCourseRequest(0);
        refreshListener();
    }

    public void refreshListener() {
        mLiveingCourseRefreshList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                getLiveingCourseRequest(mLiveingCourseRefreshList.getStart());
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                getLiveingCourseRequest(0);
            }
        });
    }

    public void getLiveingCourseRequest(final int start) {
        if (app.loginUser == null) {
            mLiveingCourseRefreshList.setLoginStatus(false);
            mLoading.setVisibility(View.GONE);
            mLiveingCourseRefreshList.pushData(null);
            mLiveingCourseRefreshList.setMode(PullToRefreshBase.Mode.DISABLED);
            return;
        } else {
            mLiveingCourseRefreshList.setMode(PullToRefreshBase.Mode.BOTH);
            mLiveingCourseRefreshList.setLoginStatus(true);
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
                mLiveingCourseRefreshList.onRefreshComplete();
                LiveingCourseResult liveingCourseResult = parseJsonValue(object, new TypeToken<LiveingCourseResult>() {
                });
                mLiveingCourseRefreshList.pushData(liveingCourseResult.data);
                mLiveingCourseRefreshList.setStart(start);
                if (liveingCourseResult.data.size() < Const.LIMIT) {
                    mLiveingCourseRefreshList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            }
        });
    }


}