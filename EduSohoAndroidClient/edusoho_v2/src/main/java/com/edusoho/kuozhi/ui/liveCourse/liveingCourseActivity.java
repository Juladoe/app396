package com.edusoho.kuozhi.ui.liveCourse;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.LiveingCourseListAdapter;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;

import library.PullToRefreshBase;

/**
 * Created by onewoman on 2015/1/30.
 */
public class liveingCourseActivity extends ActionBarBaseActivity{
    private RefreshListWidget mLiveingCourseRefreshList;
    private View mLoading;
    private LiveingCourseListAdapter mLiveingCourseListAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.liveing_course_list_layout);
        init();
    }

    public void init(){
        mLiveingCourseRefreshList = (RefreshListWidget) this.findViewById(R.id.liveing_course_refresh_list);
        mLoading = this.findViewById(R.id.load_layout);

        mLiveingCourseRefreshList.setEmptyText(mActivity, R.layout.empty_page_layout, new String[]{"加入一些课程，再来这里看看吧~", ""},
                new String[]{"革命尚未成功，同志仍需努力", "还未有学完的课程"}, R.drawable.empty_logout, R.drawable.empty_no_data);
        mLiveingCourseRefreshList.setMode(PullToRefreshBase.Mode.BOTH);
        mLiveingCourseListAdapter = new LiveingCourseListAdapter(mActivity, R.layout.liveing_course_list_inflate);
        mLiveingCourseRefreshList.setAdapter(mLiveingCourseListAdapter);
    }


}