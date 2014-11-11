package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.lesson.AbstractCourseListAdapter;
import com.edusoho.kuozhi.adapter.lesson.ScrollListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.widget.XCourseListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import library.PullToRefreshBase;

/**
 * Created by howzhi on 14-8-19.
 */
public abstract class MyCourseBaseFragment extends BaseFragment {

    public static final String TITLE = "title";
    protected XCourseListWidget mCourseListWidget;
    protected AbstractCourseListAdapter mAdapter;
    private View mLoadView;

    protected String mTitle;
    protected String mBaseUrl;

    public static final String RELOAD = "reload";

    public static final int PULL_DOWN = 0001;
    public static final int PULL_UP = 0002;

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PULL_DOWN:
                    loadCourseFromNet(0, false);
                    break;
                case PULL_UP:
                    break;
            }
        }
    };

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (RELOAD.equals(messageType.type) || Const.LOGING_SUCCESS.equals(message.type)) {
            mCourseListWidget.reload();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(RELOAD),
                new MessageType(Const.LOGING_SUCCESS),
        };
        return messageTypes;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    protected abstract String getBaseUrl();

    protected abstract String getEmptyTitle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseUrl = getBaseUrl();
        setContainerView(R.layout.my_course_content_layout);
    }

    protected AbstractCourseListAdapter getListAdapter()
    {
        return new ScrollListAdapter(mContext);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (app.loginUser == null) {
            LoginActivity.start(activity);
            return;
        }
    }

    @Override
    protected void initView(View view) {
        mLoadView = view.findViewById(R.id.load_layout);
        mCourseListWidget =(XCourseListWidget) view.findViewById(R.id.my_course_xlistview);
        mCourseListWidget.setEmptyText(getEmptyTitle());
        mCourseListWidget.getListView().setMode(PullToRefreshBase.Mode.BOTH);
        mAdapter = getListAdapter();
        mCourseListWidget.setAdapter(mAdapter);

        bindListener();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(TITLE);
        }

        loadCourseFromNet(0, false);
    }

    private void bindListener()
    {
        mCourseListWidget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Course course = (Course) adapterView.getItemAtPosition(i);
                mActivity.app.mEngine.runNormalPlugin(
                        CourseDetailsActivity.TAG, mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.COURSE_ID, course.id);
                        startIntent.putExtra(Const.ACTIONBAT_TITLE, course.title);
                        startIntent.putExtra(CourseDetailsActivity.COURSE_PIC, course.largePicture);
                    }
                });
            }
        });

        mCourseListWidget.setRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>(){
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                mHandler.obtainMessage(PULL_DOWN).sendToTarget();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                Integer startPage = (Integer) mCourseListWidget.getTag();
                if (startPage == null) {
                    return;
                }
                loadCourseFromNet(startPage, true);
            }
        });
    }

    private void loadCourseFromNet(int start, final boolean isAppend)
    {
        if (mBaseUrl == null) {
            return;
        }
        RequestUrl url = app.bindUrl(mBaseUrl, true);
        url.setParams(new String[] {
                "start", start + "",
                "limit", Const.LIMIT + ""
        });

        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mCourseListWidget.onRefreshComplete();
                mLoadView.setVisibility(View.GONE);
                parseResponse(object, isAppend);
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                mCourseListWidget.onRefreshComplete();
                mLoadView.setVisibility(View.GONE);
            }
        });
    }

    protected void parseResponse(String object, boolean isAppend)
    {
        CourseResult courseResult = mActivity.gson.fromJson(
                object, new TypeToken<CourseResult>() {
        }.getType());

        Log.d(null, "courseResult->" + courseResult);
        if (courseResult == null) {
            return;
        }

        if (isAppend) {
            mAdapter.addItemLast(courseResult.data);
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
