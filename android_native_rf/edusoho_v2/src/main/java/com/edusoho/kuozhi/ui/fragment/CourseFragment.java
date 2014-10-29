package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.course.CourseListActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
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

    public static final String TITLE = "标题";
    private RefreshListWidget mCourseListView;
    private View mLoadView;

    private int mCategoryId;
    private String mTitle;
    private String mSearchText;
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
        mCourseListView =(RefreshListWidget) view.findViewById(R.id.course_liseview);
        mCourseListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mCourseListView.setAdapter(new CourseListAdapter(mContext, R.layout.recommend_school_list_item));
        mCourseListView.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                loadCourseFromNet(mCourseListView.getStart());
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                loadCourseFromNet(0);
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
        if (mSearchText != null && !TextUtils.isEmpty(mSearchText)) {
            baseUrl = Const.SEARCH_COURSE;
        } else if (mType == CourseListActivity.RECOMMEND) {
            baseUrl = Const.RECOMMEND_COURSES;
        } else if (mType == CourseListActivity.LASTEST) {
            baseUrl = Const.LASTEST_COURSES;
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
                mCourseListView.onRefreshComplete();
                CourseResult courseResult = mActivity.gson.fromJson(
                        object, new TypeToken<CourseResult>() {
                }.getType());

                if (courseResult == null) {
                    return;
                }

                mCourseListView.pushData(courseResult.data);
                mCourseListView.setStart(courseResult.start, courseResult.total);
            }
        });
    }
}
