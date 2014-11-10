package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseNoticeListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.CourseNotice;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;

/**
 * Created by onewoman on 14-11-10.
 */
public class CourseNoticeFragment extends BaseFragment{
    private RefreshListWidget mRefreshList;
    private String mCourseId;
    @Override
    public String getTitle() {
        return "公告历史";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_notice_fragment_layout);
        mCourseId = getArguments().getString(Const.COURSE_ID);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mRefreshList = (RefreshListWidget) view.findViewById(R.id.course_notice_refreshlist);
        mRefreshList.setMode(PullToRefreshBase.Mode.BOTH);
        mRefreshList.setAdapter(new CourseNoticeListAdapter(mContext,R.layout.course_notice_item_layout,true));

        mRefreshList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
            }
        });
        courseNoticeGsonResponse(0);
    }

    public void courseNoticeGsonResponse(final int start){
        RequestUrl url = app.bindUrl(Const.COURSE_NOTICE,false);
        url.setParams(new String[]{
                "start",String.valueOf(start),
                "limit",String.valueOf(Const.LIMIT),
                Const.COURSE_ID,mCourseId
        });
        mActivity.ajaxPost(url,new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                //ToDo
                ArrayList<CourseNotice> courseNotice = mActivity.parseJsonValue(object,new TypeToken<ArrayList<CourseNotice>>(){});
                if(courseNotice == null){
                    return ;
                }
                mRefreshList.pushData(courseNotice);
                mRefreshList.setStart(start+Const.LIMIT);
            }
        });
    }
}
