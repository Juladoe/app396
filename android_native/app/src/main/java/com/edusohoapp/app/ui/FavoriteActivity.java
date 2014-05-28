package com.edusohoapp.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusohoapp.app.R;
import com.edusohoapp.app.adapter.FavoriteCourseListAdapter;
import com.edusohoapp.app.model.CourseResult;
import com.edusohoapp.app.util.Const;
import com.edusohoapp.app.view.EdusohoListView;
import com.edusohoapp.app.view.OverScrollView;
import com.edusohoapp.listener.CourseListScrollListener;
import com.edusohoapp.listener.MoveListener;
import com.google.gson.reflect.TypeToken;

public class FavoriteActivity extends BaseActivity {

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
        context.startActivity(intent);
    }

    private void initView() {
        setBackMode("收藏的课程", true, null);

        if (app.loginUser == null) {
            LoginActivity.startForResult(mActivity);
            return;
        }
        loadCourse(0, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case LoginActivity.OK:
                if (app.loginUser != null) {
                    loadCourse(0, false);
                }
                break;
            case LoginActivity.EXIT:
                finish();
                break;
        }
    }

    /**
     *
     *
     */
    private void loadCourse(int page, final boolean isAppend)
    {
        ViewStub vStub = (ViewStub) findViewById(R.id.favoirte_content_vs);
        if (vStub != null) {
            vStub.inflate();
        }
        final EdusohoListView listView = (EdusohoListView) findViewById(R.id.course_liseview);

        StringBuffer param = new StringBuffer(Const.FAVORITES);
        param.append("?start=").append(page);

        String url = app.bindToken2Url(param.toString(), true);

        app.query.ajax(
                url, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                super.callback(url, object, status);
                //hide loading layout
                findViewById(R.id.load_layout).setVisibility(View.GONE);
                final CourseResult result = app.gson.fromJson(
                        object, new TypeToken<CourseResult>(){}.getType());
                Log.i(null, result.toString());
                if (result == null) {
                    return;
                }
                if (! isAppend) {
                    FavoriteCourseListAdapter adapter = new FavoriteCourseListAdapter(
                            mContext, result, R.layout.course_list_normal_item);

                    listView.setAdapter(adapter);
                    CourseListScrollListener listener = new CourseListScrollListener(mContext, listView);
                    listView.setOnItemClickListener(listener);

                    OverScrollView scrollView = (OverScrollView) findViewById(R.id.course_content_scrollview);
                    scrollView.setMoveListener(new MoveListener(){
                        @Override
                        public void moveToBottom() {
                            View course_more_btn = findViewById(R.id.course_more_btn);
                            if (course_more_btn.getVisibility() == View.VISIBLE) {
                                course_more_btn.findViewById(R.id.more_btn_loadbar).setVisibility(View.VISIBLE);
                                loadCourse((result.start + 1) * Const.LIMIT, true);
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
        });
    }
}
