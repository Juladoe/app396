package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseListAdapter;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.widget.CourseRefreshListWidget;
import com.edusoho.kuozhi.util.Const;
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
    private int mStart;

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

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(TITLE);
            mCategoryId = bundle.getInt("categoryId", 0);
        }

        loadCourseFromNet(0);
    }

    private void loadCourseFromNet(int start)
    {
        String url = app.bindUrl(Const.COURSES);
        HashMap<String, String> params = app.createParams(true, null);
        params.put("categoryId", mCategoryId + "");
        params.put("start", start + "");
        params.put("limit", Const.LIMIT + "");

        mActivity.ajaxPost(url, params, new ResultCallback(){
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
