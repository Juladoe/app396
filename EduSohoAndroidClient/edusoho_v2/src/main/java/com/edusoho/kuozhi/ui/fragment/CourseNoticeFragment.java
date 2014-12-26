package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseNoticeListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.CourseNotice;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import library.PullToRefreshBase;

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
        mRefreshList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mRefreshList.setEmptyText(new String[]{ "没有公告" });
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
                CourseNotice courseNotice = (CourseNotice) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putInt(AboutFragment.TYPE, AboutFragment.FROM_STR);
                bundle.putString(AboutFragment.CONTENT, filterContent(courseNotice.content));
                bundle.putString(Const.ACTIONBAR_TITLE, "公告");
                bundle.putString(FragmentPageActivity.FRAGMENT, "AboutFragment");
                startAcitivityWithBundle("FragmentPageActivity", bundle);
            }
        });

        courseNoticeGsonResponse(0);
    }

    private String filterContent(String content)
    {
        StringBuffer stringBuffer = new StringBuffer();
        Matcher m = Pattern.compile("(img src=\".*?\")").matcher(content);
        while (m.find()) {
            String[] s = m.group(1).split("src=");
            String strUrl = s[1].toString().substring(1, s[1].length() - 1);
            if (!strUrl.contains("http")) {
                m.appendReplacement(stringBuffer, String.format("img src='%s'", EdusohoApp.app.host + strUrl));
            }
        }
        m.appendTail(stringBuffer);

        return stringBuffer.toString();
    }

    public void courseNoticeGsonResponse(final int start) {
        RequestUrl url = app.bindUrl(Const.COURSE_NOTICE, false);
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
