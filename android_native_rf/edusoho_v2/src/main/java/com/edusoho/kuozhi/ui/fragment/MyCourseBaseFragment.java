package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseListAdapter;
import com.edusoho.kuozhi.adapter.ScrollListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.widget.XCourseListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.CourseListScrollListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huewu.pla.lib.internal.PLA_AdapterView;

import me.maxwin.view.XListView;

/**
 * Created by howzhi on 14-8-19.
 */
public abstract class MyCourseBaseFragment extends BaseFragment {

    public static final String TITLE = "title";
    private XCourseListWidget mCourseListWidget;
    private ScrollListAdapter mAdapter;
    private View mLoadView;

    private String mTitle;
    private int mStart;

    protected String mBaseUrl;

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
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

    @Override
    protected void initView(View view) {
        mLoadView = view.findViewById(R.id.load_layout);
        mCourseListWidget =(XCourseListWidget) view.findViewById(R.id.my_course_xlistview);
        mCourseListWidget.setEmptyText(getEmptyTitle());
        mAdapter = new ScrollListAdapter(mContext);
        mCourseListWidget.setAdapter(mAdapter);

        bindListener();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(TITLE);
        }

        loadCourseFromNet(0);
    }

    private void bindListener()
    {
        mCourseListWidget.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                final Course course = (Course) parent.getItemAtPosition(position);
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

        mCourseListWidget.setRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshBase refreshView) {
                loadCourseFromNet(0);
            }
        });

        mCourseListWidget.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                loadCourseFromNet(mStart);
            }
        });
    }

    private void loadCourseFromNet(int start)
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
                mLoadView.setVisibility(View.GONE);
                CourseResult courseResult = mActivity.gson.fromJson(
                        object, new TypeToken<CourseResult>() {
                }.getType());

                Log.d(null, "courseResult->" + courseResult);
                if (courseResult == null) {
                    return;
                }
                int start = courseResult.start + Const.LIMIT;
                if (start < courseResult.total) {
                    mStart = start;
                } else {
                }
                mAdapter.addItemLast(courseResult.data);
            }
        });
    }
}
