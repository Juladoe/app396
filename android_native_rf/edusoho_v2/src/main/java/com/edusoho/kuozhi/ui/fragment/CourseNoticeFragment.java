package com.edusoho.kuozhi.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseNoticeListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.CourseNotice;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.html.EduHtml;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;

/**
 * Created by onewoman on 14-11-10.
 */
public class CourseNoticeFragment extends BaseFragment {
    private RefreshListWidget mRefreshList;
    private int mCourseId;

    @Override
    public String getTitle() {
        return "公告历史";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_notice_fragment_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        changeTitle("公告历史");
        mCourseId = getArguments().getInt(Const.COURSE_ID);
        mRefreshList = (RefreshListWidget) view.findViewById(R.id.course_notice_refreshlist);
        mRefreshList.setMode(PullToRefreshBase.Mode.BOTH);
        mRefreshList.setEmptyText(new String[]{"没有公告"});
        mRefreshList.setAdapter(new CourseNoticeListAdapter(mContext, R.layout.course_notice_item_layout, false));

        mRefreshList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                courseNoticeGsonResponse(mRefreshList.getStart());
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                courseNoticeGsonResponse(0);
                mRefreshList.setMode(PullToRefreshBase.Mode.BOTH);
            }
        });

        mRefreshList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
                CourseNotice courseNotice = (CourseNotice) parent.getItemAtPosition(position);
                TextView textView = new TextView(mContext);
                textView.setText(EduHtml.coverHtmlImages(courseNotice.content, textView, mContext));
                AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setTitle("公告")
                        .setView(textView)
                        .create();
                alertDialog.show();
            }
        });
        courseNoticeGsonResponse(0);
    }

    public void courseNoticeGsonResponse(final int start) {
        RequestUrl url = app.bindUrl(Const.COURSE_NOTICES, false);
        url.setParams(new String[]{
                "start", String.valueOf(start),
                "limit", String.valueOf(Const.LIMIT),
                Const.COURSE_ID, String.valueOf(mCourseId)
        });
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mRefreshList.onRefreshComplete();
                ArrayList<CourseNotice> courseNotice = mActivity.parseJsonValue(object, new TypeToken<ArrayList<CourseNotice>>() {
                });
                if (courseNotice == null) {
                    mRefreshList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    return;
                }
                mRefreshList.pushData(courseNotice);
                mRefreshList.setStart(start + Const.LIMIT);
            }
        });
    }
}
