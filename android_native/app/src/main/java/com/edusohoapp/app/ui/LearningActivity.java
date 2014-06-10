package com.edusohoapp.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusohoapp.app.R;
import com.edusohoapp.app.adapter.LearnCourseListAdapter;
import com.edusohoapp.app.model.Course;
import com.edusohoapp.app.model.LearnCourse;
import com.edusohoapp.app.model.LearnCourseResult;
import com.edusohoapp.app.util.Const;
import com.edusohoapp.app.view.EdusohoListView;
import com.edusohoapp.app.view.OverScrollView;
import com.edusohoapp.listener.CourseListScrollListener;
import com.edusohoapp.listener.MoveListener;
import com.edusohoapp.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

public class LearningActivity extends BaseActivity {

    private ViewGroup mLearnContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_layout);
        initView();
    }

    public static void start(Activity context) {
        Intent intent = new Intent();
        intent.setClass(context, LearningActivity.class);
        context.startActivityForResult(intent, Const.LEARNING_REQUEST);
    }

    private void initView() {
        setBackMode("在学", true, null);

        if (app.loginUser == null) {
            LoginActivity.startForResult(mActivity);
            return;
        }

        mLearnContent = (ViewGroup) findViewById(R.id.learn_content);
        setPagerContent(mLearnContent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case LoginActivity.OK:
                if (app.loginUser != null) {
                    loadCourse(0, false, true);
                }
                break;
            case LoginActivity.EXIT:
                finish();
                break;
            case Const.NORMAL_RESULT_REFRESH:
                if (app.loginUser != null) {
                    loadCourse(0, false, true);
                }
                break;
        }
    }

    private void setPagerContent(ViewGroup parent)
    {
        parent.removeAllViews();
        View course_content = getLayoutInflater().inflate(R.layout.course_content, null);
        parent.addView(course_content);
        loadCourse(0, false, false);
    }

    private void loadCourse(int page, final boolean isAppend, boolean showLoading) {

        final EdusohoListView listView = (EdusohoListView) findViewById(R.id.course_liseview);

        StringBuffer param = new StringBuffer(Const.LEARN);
        param.append("?start=").append(page);

        String url = app.bindToken2Url(param.toString(), true);

        ajax(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                //hide loading layout
                findViewById(R.id.load_layout).setVisibility(View.GONE);
                final LearnCourseResult result = app.gson.fromJson(
                        object, new TypeToken<LearnCourseResult>() {
                }.getType());

                if (result == null || result.data.size() == 0) {
                    showEmptyLayout("暂无学习中的课程");
                    return;
                }
                if (!isAppend) {
                    LearnCourseListAdapter adapter = new LearnCourseListAdapter(
                            mContext, result.data, R.layout.learn_list_item);

                    listView.setAdapter(adapter);
                    CourseListScrollListener listener = new CourseListScrollListener(mActivity, listView) {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int index, long arg3) {
                            Course course = (Course) parent.getItemAtPosition(index);

                            Intent intent = new Intent(mContext, CourseInfoActivity.class);
                            intent.putExtra("courseId", course.id);
                            intent.putExtra("largePicture", course.largePicture);
                            intent.putExtra("courseTitle", course.title);
                            intent.putExtra("currentPage", 0);
                            startActivityForResult(intent, Const.COURSEINFO_REQUEST);
                        }
                    };
                    listView.setOnItemClickListener(listener);

                    OverScrollView scrollView = (OverScrollView) findViewById(R.id.course_content_scrollview);
                    scrollView.setMoveListener(new MoveListener() {
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
                    LearnCourseListAdapter adapter = (LearnCourseListAdapter) listView.getAdapter();
                    adapter.addItem(result.data);
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

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                findViewById(R.id.course_content_scrollview).setVisibility(View.GONE);
                showErrorLayout("网络数据加载错误！请重新尝试刷新。", new ListErrorListener() {
                    @Override
                    public void error(View errorBtn) {
                        setPagerContent(mLearnContent);
                    }
                });
                findViewById(R.id.load_layout).setVisibility(View.GONE);
            }
        }, showLoading);

    }
}
