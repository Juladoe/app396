package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.FoundCourseListAdapter;
import com.edusoho.kuozhi.adapter.CourseListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.course.CourseListActivity;
import com.edusoho.kuozhi.ui.widget.BaseRefreshListWidget;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.CourseListScrollListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import cn.trinea.android.common.util.AppUtils;
import library.PullToRefreshBase;

/**
 * Created by howzhi on 14-8-19.
 */
public class CourseFragment extends BaseFragment {

    public static final String TITLE = "标题";
    private RefreshListWidget mCourseListView;
    private View mLoadView;

    private int mCategoryId;
    private String mTitle;
    private String mTagId;
    private int mClassRoomId;
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
        mCourseListView = (RefreshListWidget) view.findViewById(R.id.course_liseview);
        mCourseListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mCourseListView.setAdapter(new FoundCourseListAdapter(mContext, R.layout.found_course_list_item));
        mCourseListView.setEmptyText(new String[] { "没有搜到相关课程，请换个关键词试试！" }, R.drawable.icon_course_empty);

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
            mClassRoomId = bundle.getInt(Const.CLASSROOM_ID, 0);
            mSearchText = bundle.getString(CourseListActivity.SEARCH_TEXT);
            mTitle = bundle.getString(TITLE);
            mCategoryId = bundle.getInt(CourseListActivity.CATEGORY_ID, 0);
            mTagId = bundle.getString(CourseListActivity.TAG_ID);
        }

        baseUrl = Const.COURSES;
        if (mSearchText != null && !TextUtils.isEmpty(mSearchText)) {
            baseUrl = Const.SEARCH_COURSE;
        } else if (mTagId != null && !TextUtils.isEmpty(mTagId)) {
            baseUrl = Const.SEARCH_COURSE;
        } else if (mType == CourseListActivity.RECOMMEND) {
            baseUrl = Const.RECOMMEND_COURSES;
        } else if (mType == CourseListActivity.LASTEST) {
            baseUrl = Const.LASTEST_COURSES;
        } else if (mType == CourseListActivity.CLASSROOM) {
            baseUrl = Const.CLASSROOM_COURSES;
        }

        loadCourseFromNet(0);
    }

    private void initRequestParams(HashMap<String, String> params) {
        if (mType == CourseListActivity.CLASSROOM) {
            params.put(Const.CLASSROOM_ID, String.valueOf(mClassRoomId));
            return;
        }
        if (mTagId != null && !TextUtils.isEmpty(mTagId)) {
            params.put(CourseListActivity.TAG_ID, mTagId);
        } else {
            params.put(CourseListActivity.SEARCH_TEXT, mSearchText);
        }

        params.put(CourseListActivity.CATEGORY_ID, mCategoryId + "");
    }

    private void loadCourseFromNet(int start) {
        RequestUrl url = app.bindUrl(baseUrl, true);
        HashMap<String, String> params = url.getParams();
        initRequestParams(params);
        params.put("start", start + "");
        params.put("limit", Const.LIMIT + "");

        mActivity.ajaxPost(url, new ResultCallback() {
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
