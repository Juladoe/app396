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
import com.edusoho.kuozhi.model.LearnCourseResult;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import library.PullToRefreshBase;

/**
 * Created by JesseHuang on 15/1/7.
 * 在学课程中的已学课程
 */
public class LearnedCourseFragmentHorizontal extends HorizontalCourseFragment {

    @Override
    public String getTitle() {
        return "已学完";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        RequestUrl url = app.bindUrl(Const.LEARNED, true);
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
            }
        });
    }

    @Override
    public BaseAdapter getAdapter() {
        return new LearnedCourseAdapter(R.layout.learned_course_item, mContext);
    }

    @Override
    public String getEmptyText() {
        return "没有已学课程";
    }

    @Override
    public String[] getLogoutText() {
        return new String[]{"加入一些课程，再来这里看看吧~", ""};
    }

    @Override
    public String[] getLoginText() {
        return new String[]{"革命尚未成功，同志仍需努力", "暂无已学课程"};
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LEARNCOURSE || resultCode == LoginActivity.OK || resultCode == RegistFragment.OK) {
            getCourseResponseDatas(0);
        }
    }
}
