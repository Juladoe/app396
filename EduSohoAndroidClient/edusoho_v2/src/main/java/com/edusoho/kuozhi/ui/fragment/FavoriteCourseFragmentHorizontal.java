package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.LearnedCourseAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.LearnCourseResult;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import library.PullToRefreshBase;

/**
 * Created by JesseHuang on 15/1/16.
 */
public class FavoriteCourseFragmentHorizontal extends HorizontalCourseFragment {

    @Override
    public String getTitle() {
        return "已收藏";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }

    @Override
    public void getCourseResponseDatas(final int start) {
        if (app.loginUser == null) {
            mLessioningList.setLoginStatus(false);
            mLessioningList.pushData(null);
            mLoadView.setVisibility(View.GONE);
            mLessioningList.setMode(PullToRefreshBase.Mode.DISABLED);
            return;
        } else {
            mLessioningList.setMode(PullToRefreshBase.Mode.BOTH);
            mLessioningList.setLoginStatus(true);
        }

        RequestUrl url = app.bindUrl(Const.FAVORITES, true);
        url.setParams(new String[]{
                "start", start + "",
                "limit", Const.LIMIT + ""
        });

        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLessioningList.onRefreshComplete();
                mLoadView.setVisibility(View.GONE);
                parseResponse(object, start);
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                mLessioningList.onRefreshComplete();
                mLoadView.setVisibility(View.GONE);
            }
        });
    }

    private void parseResponse(String object, int start) {
        LearnCourseResult courseResult = mActivity.gson.fromJson(
                object, new TypeToken<LearnCourseResult>() {
                }.getType());

        Log.d(null, "courseResult->" + courseResult);
        if (courseResult == null) {
            return;
        }

        mLessioningList.pushData(courseResult.data);
        mLessioningList.setStart(start, courseResult.total);
    }

    @Override
    public BaseAdapter getAdapter() {
        return new LearnedCourseAdapter(R.layout.learned_course_item, mContext);
    }

    @Override
    public String getEmptyText() {
        return "没有收藏课程";
    }

    @Override
    public String[] getLogoutText() {
        return new String[]{"去收藏一些课程吧~", ""};
    }

    @Override
    public String[] getLoginText() {
        return new String[]{"收藏一些课程，再来这里看看吧~", ""};
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == LoginActivity.OK || resultCode == RegistFragment.OK) {
            getCourseResponseDatas(0);
        }
    }
}
