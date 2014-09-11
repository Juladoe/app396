package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.course.CourseListActivity;
import com.edusoho.kuozhi.ui.widget.CourseRefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.CourseListScrollListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.HashMap;

/**
 * Created by howzhi on 14-8-19.
 */
public class CourseFragment extends BaseFragment {

    public static final String TITLE = "title";
    private CourseRefreshListWidget mCourseListView;
    private View mLoadView;

    private int mCategoryId;
    private String mTitle;
    private String mSearchText;
    private int mStart;
    private int mType;
    private String baseUrl;

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_content);
    }

    @Override
    protected void initView(View view) {
        mLoadView = view.findViewById(R.id.load_layout);
        mCourseListView =(CourseRefreshListWidget) view.findViewById(R.id.course_liseview);
        mCourseListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadCourseFromNet(mStart);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

        mCourseListView.setOnItemClickListener(new CourseListScrollListener(mActivity));

        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt(CourseListActivity.TYPE);
            mSearchText = bundle.getString(CourseListActivity.SEARCH_TEXT);
            mTitle = bundle.getString(TITLE);
            mCategoryId = bundle.getInt(CourseListActivity.CATEGORY_ID, 0);
        }

        baseUrl = Const.COURSES;
        if (mSearchText != null) {
            baseUrl = Const.SEARCH_COURSE;
        } else if (mType == CourseListActivity.RECOMMEND) {
            baseUrl = Const.RECOMMEND_COURSES;
        }

        loadCourseFromNet(0);
    }

    private void loadCourseFromNet(int start)
    {
        RequestUrl url = app.bindUrl(baseUrl, true);
        HashMap<String, String> params = url.getParams();
        params.put(CourseListActivity.CATEGORY_ID, mCategoryId + "");
        params.put(CourseListActivity.SEARCH_TEXT, mSearchText);
        params.put("start", start + "");
        params.put("limit", Const.LIMIT + "");

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
                mStart = courseResult.start;
                CourseListAdapter adapter = (CourseListAdapter) mCourseListView.getAdapter();
                if (adapter != null) {
                    adapter.addItem(courseResult);
                } else {
                    adapter = new CourseListAdapter(
                            mActivity, courseResult, R.layout.recommend_school_list_item);
                    mCourseListView.setAdapter(adapter);
                }
            }
        });
    }
}
