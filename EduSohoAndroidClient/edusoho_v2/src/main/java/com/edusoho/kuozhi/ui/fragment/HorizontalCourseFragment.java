package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.LessionListAdapter;
import com.edusoho.kuozhi.model.LearnCourse;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;

import library.PullToRefreshBase;

/**
 * Created by JesseHuang on 15/1/7.
 */
public abstract class HorizontalCourseFragment extends BaseFragment {
    protected RefreshListWidget mLessioningList;
    protected View mLoadView;
    protected static final int LEARNCOURSE = 0;
    protected BaseAdapter mLessionListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.lessioning_main_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mLessioningList = (RefreshListWidget) view.findViewById(R.id.lession_listview);
        mLessioningList.setMode(PullToRefreshBase.Mode.BOTH);
        mLessioningList.setEmptyText(new String[]{"没有在学课程"},R.drawable.course_details_menu_courseinfo);
        mLessionListAdapter = getAdapter();
        mLessioningList.setAdapter(mLessionListAdapter);
        mLessioningList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LearnCourse learnCourse = (LearnCourse) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putInt(Const.COURSE_ID, learnCourse.id);
                bundle.putString(Const.ACTIONBAR_TITLE, learnCourse.title);
                startActivityWithBundleAndResult("CorusePaperActivity", LEARNCOURSE, bundle);
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

    @Override
    public String getTitle() {
        return null;
    }

    public abstract void getLeaenCourseReponseDatas(final int start);

    public abstract BaseAdapter getAdapter();
}
