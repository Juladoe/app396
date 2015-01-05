package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.LessionListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.LearnCourse;
import com.edusoho.kuozhi.model.LearnCourseResult;
import com.edusoho.kuozhi.model.SystemInfo;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import library.PullToRefreshBase;

/**
 * Created by onewoman on 2014/12/3.
 */
public class LeaenCourseFragment extends BaseFragment{
    private LessionListAdapter mLessionListAdapter;
    private RefreshListWidget mLessioningList;
    private View mLoadView;
    private static final int LEAENCOURSE = 0;
    @Override
    public String getTitle() {
        return "在学课程";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.lessioning_main_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mLessioningList = (RefreshListWidget) view.findViewById(R.id.lession_listview);
        changeTitle("在学课程");
        mLessioningList.setMode(PullToRefreshBase.Mode.BOTH);
        mLessionListAdapter = new LessionListAdapter(R.layout.lessioning_item_inflate,mContext);
        mLessioningList.setAdapter(mLessionListAdapter);
        mLessioningList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LearnCourse learnCourse = (LearnCourse) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putInt(Const.COURSE_ID, learnCourse.id);
                bundle.putString(Const.ACTIONBAR_TITLE, learnCourse.title);
                startActivityWithBundleAndResult("CorusePaperActivity", LEAENCOURSE, bundle);
            }
        });

        mLessioningList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                getLeaenCourseReponseDatas(mLessioningList.getStart());
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                getLeaenCourseReponseDatas(0);
            }
        });
        mLoadView = view.findViewById(R.id.load_layout);
        getLeaenCourseReponseDatas(0);
    }


    private void getLeaenCourseReponseDatas(final int start){
        RequestUrl url = app.bindUrl(Const.LEARNING, true);
        url.setParams(new String[] {
                "start", start + "",
                "limit", Const.LIMIT + ""
        });

        mActivity.ajaxPost(url, new ResultCallback(){
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

    private void parseResponse(String object,int start) {
        LearnCourseResult courseResult = mActivity.gson.fromJson(
                object, new TypeToken<LearnCourseResult>() {
                }.getType());

        Log.d(null, "courseResult->" + courseResult);
        if (courseResult == null) {
            return;
        }

        mLessioningList.pushData(courseResult.data);
        mLessioningList.setStart(start,courseResult.total);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LEAENCOURSE){
            getLeaenCourseReponseDatas(0);
        }
    }
}
