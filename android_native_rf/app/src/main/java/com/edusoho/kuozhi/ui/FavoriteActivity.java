package com.edusoho.kuozhi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.FavoriteCourseListAdapter;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoListView;
import com.edusoho.kuozhi.view.OverScrollView;
import com.edusoho.listener.CourseListScrollListener;
import com.edusoho.listener.MoveListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

public class FavoriteActivity extends BaseActivity {

    private ViewGroup mFavoirteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_layout);
        initView();
    }

    public static void start(Activity context)
    {
        Intent intent = new Intent();
        intent.setClass(context, FavoriteActivity.class);
        context.startActivityForResult(intent, Const.FAVORITE_REQUEST);
    }

    private void initView() {
        setBackMode("收藏的课程", true, null);
        mFavoirteContent = (ViewGroup) findViewById(R.id.favoirte_content);

        if (app.loginUser == null) {
            LoginActivity.startForResult(mActivity);
            return;
        }

        setPagerContent(mFavoirteContent, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case LoginActivity.OK:
                if (app.loginUser != null) {
                    setPagerContent(mFavoirteContent, true);
                }
                break;
            case LoginActivity.EXIT:
                finish();
                break;
            case Const.NORMAL_RESULT_REFRESH:
                if (app.loginUser != null) {
                    setPagerContent(mFavoirteContent, true);
                }
                break;
        }
    }

    private void setPagerContent(ViewGroup parent, boolean showLoading)
    {
        parent.removeAllViews();
        View course_content = getLayoutInflater().inflate(R.layout.course_content, null);
        parent.addView(course_content);
        loadCourse(0, false, showLoading);
    }

    private void loadCourse(int page, final boolean isAppend, boolean showLoading)
    {
        final EdusohoListView listView = (EdusohoListView) findViewById(R.id.course_liseview);

        StringBuffer param = new StringBuffer(Const.FAVORITES);
        param.append("?start=").append(page);

        String url = app.bindToken2Url(param.toString(), true);

        ajax(url, new ResultCallback(){
            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
                findViewById(R.id.course_content_scrollview).setVisibility(View.GONE);
                showErrorLayout("网络数据加载错误！请重新尝试刷新。", new ListErrorListener() {
                    @Override
                    public void error(View errorBtn) {
                        setPagerContent(mFavoirteContent, false);
                    }
                });
                findViewById(R.id.load_layout).setVisibility(View.GONE);
            }

            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                findViewById(R.id.load_layout).setVisibility(View.GONE);
                final CourseResult result = app.gson.fromJson(
                        object, new TypeToken<CourseResult>(){}.getType());

                if (result == null || result.data.length == 0) {
                    showEmptyLayout("暂无收藏课程");
                    return;
                }
                if (! isAppend) {
                    FavoriteCourseListAdapter adapter = new FavoriteCourseListAdapter(
                            mContext, result, R.layout.course_list_normal_item);

                    listView.setAdapter(adapter);
                    CourseListScrollListener listener = new CourseListScrollListener(mActivity, listView);
                    listView.setOnItemClickListener(listener);

                    OverScrollView scrollView = (OverScrollView) findViewById(R.id.course_content_scrollview);
                    scrollView.setMoveListener(new MoveListener(){
                        @Override
                        public void moveToBottom() {
                            View course_more_btn = findViewById(R.id.course_more_btn);
                            if (course_more_btn.getVisibility() == View.VISIBLE) {
                                course_more_btn.findViewById(R.id.more_btn_loadbar).setVisibility(View.VISIBLE);
                                loadCourse((result.start + 1) * Const.LIMIT, true, false);
                            }
                        }
                    });

                } else {
                    FavoriteCourseListAdapter adapter = (FavoriteCourseListAdapter) listView.getAdapter();
                    adapter.addItem(result);
                    listView.initListHeight();
                }

                View course_more_btn = findViewById(R.id.course_more_btn);
                int start = (result.start + 1) * Const.LIMIT;
                if (start < result.total) {
                    course_more_btn.setVisibility(View.VISIBLE);
                } else {
                    course_more_btn.setVisibility(View.GONE);
                }
            }
        }, showLoading);

    }
}
