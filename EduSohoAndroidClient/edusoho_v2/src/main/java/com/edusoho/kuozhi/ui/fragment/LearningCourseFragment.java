package com.edusoho.kuozhi.ui.fragment;

import android.util.Log;
import android.view.View;

import com.edusoho.kuozhi.adapter.lesson.AbstractCourseListAdapter;
import com.edusoho.kuozhi.adapter.lesson.LearningListAdapter;
import com.edusoho.kuozhi.model.LearnCourseResult;
import com.edusoho.kuozhi.util.Const;
import com.google.gson.reflect.TypeToken;

import library.PullToRefreshBase;

/**
 * Created by howzhi on 14-9-16.
 */
public class LearningCourseFragment extends MyCourseBaseFragment {

    @Override
    protected String getBaseUrl() {
        return Const.LEARNING;
    }

    @Override
    protected String getEmptyTitle() {
        return "没有在学课程";
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }

    @Override
    protected AbstractCourseListAdapter getListAdapter() {
        return new LearningListAdapter(mContext);
    }

    @Override
    protected void parseResponse(String object, boolean isAppend) {
        LearnCourseResult courseResult = mActivity.gson.fromJson(
                object, new TypeToken<LearnCourseResult>() {
        }.getType());

        Log.d(null, "courseResult->" + courseResult);
        if (courseResult == null) {
            return;
        }

        if (isAppend) {
            mAdapter.addItemLast(courseResult.data);
            mCourseListWidget.scrollLater();
        } else {
            mAdapter.setItem(courseResult.data);
        }
        int start = courseResult.start + Const.LIMIT;
        if (start < courseResult.total) {
            mCourseListWidget.setTag(start);
            mCourseListWidget.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            mCourseListWidget.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
    }
}
