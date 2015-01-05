package com.edusoho.kuozhi.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.testpaper.TestpaperListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.BaseResult;
import com.edusoho.kuozhi.model.Testpaper.MyTestpaperData;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import library.PullToRefreshBase;

/**
 * Created by howzhi on 14-8-19.
 */
public class MyTestpaperFragment extends BaseFragment {

    public static final String TITLE = "title";
    private RefreshListWidget mCourseListView;
    private View mLoadView;


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
        view.setPadding(20, 0, 20, 0);
        view.setBackgroundColor(Color.WHITE);
        mLoadView = view.findViewById(R.id.load_layout);
        mCourseListView =(RefreshListWidget) view.findViewById(R.id.course_liseview);
        mCourseListView.setMode(PullToRefreshBase.Mode.BOTH);
        mCourseListView.setAdapter(new TestpaperListAdapter(
                mActivity, R.layout.my_testpaper_item));
        mCourseListView.setEmptyText(new String[] { "暂无考试" }, R.drawable.icon_testpaper);

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

        loadCourseFromNet(0);
    }

    private void loadCourseFromNet(int start)
    {
        RequestUrl url = app.bindUrl(Const.MY_TESTPAPER, true);
        HashMap<String, String> params = url.getParams();
        params.put("start", start + "");
        params.put("limit", Const.LIMIT + "");

        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLoadView.setVisibility(View.GONE);
                mCourseListView.onRefreshComplete();
                BaseResult<MyTestpaperData> result = mActivity.gson.fromJson(
                        object, new TypeToken<BaseResult<MyTestpaperData>>() {
                }.getType());

                if (result == null) {
                    return;
                }

                mCourseListView.pushItem(result.data, result.data.myTestpaperResults.isEmpty());
                mCourseListView.setStart(result.start, result.total);
            }
        });
    }
}
