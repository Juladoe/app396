package com.edusoho.kuozhi.ui.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.lesson.LessonQuestionListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.QuestionResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.HashMap;

/**
 * Created by MyPC on 14-11-10.
 */
public class LessonQuestionActivity extends ActionBarBaseActivity {
    private static final String TAG = "LessonQuestionActivity";

    private int mLessonId;
    private String mLessonName;
    private int mStart;
    private RefreshListWidget mLessonQuestionList;
    private View mLoadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_question_activity);
        mActivity = this;
        initViews();
    }

    private void initViews() {
        Intent data = getIntent();
        if (data != null) {
            mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
            mLessonName = data.getStringExtra(Const.LESSON_NAME);
        } else {
            longToast("课程信息错误！");
            return;
        }
        setBackMode(BACK, mLessonName);

        mLessonQuestionList = (RefreshListWidget) findViewById(R.id.qrlw_question_reply);
        mLoadView = (View) findViewById(R.id.load_layout);
        mLessonQuestionList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mLessonQuestionList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                loadQuestionDataFromSeek(mStart, false);
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                loadQuestionDataFromSeek(0, true);
            }
        });
        mLessonQuestionList.setEmptyText(new String[]{"暂无提问"});
        mLessonQuestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuestionDetailModel model = (QuestionDetailModel) parent.getItemAtPosition(position);
                final String title = model.title;
                final int courseId = model.courseId;
                final int threadId = model.id;
                app.mEngine.runNormalPlugin("QuestionDetailActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.QUESTION_TITLE, title);
                        startIntent.putExtra(Const.COURSE_ID, courseId);
                        startIntent.putExtra(Const.THREAD_ID, threadId);
                    }
                });
            }
        });
        loadQuestionDataFromSeek(0, false);

    }

    private void loadQuestionDataFromSeek(int start, final boolean isRefresh) {
        RequestUrl url = app.bindUrl(Const.QUESTION, true);
        HashMap<String, String> params = url.getParams();
        params.put("start", String.valueOf(start));
        params.put("limit", String.valueOf(Const.LIMIT));
        params.put("lessonId", String.valueOf(mLessonId));

        this.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    mLessonQuestionList.onRefreshComplete();
                    mLoadView.setVisibility(View.GONE);
                    QuestionResult questionResult = mActivity.gson.fromJson(object, new TypeToken<QuestionResult>() {
                    }.getType());

                    if (questionResult == null) {
                        return;
                    }
                    mStart = questionResult.limit + questionResult.start;
                    LessonQuestionListAdapter adapter = (LessonQuestionListAdapter) mLessonQuestionList.getAdapter();
                    if (adapter != null) {
                        if (isRefresh) {
                            adapter.clear();
                        }
                        adapter.addItems(questionResult.threads);
                    } else {
                        adapter = new LessonQuestionListAdapter(mContext, R.layout.item_lesson_question);
                        adapter.addItems(questionResult.threads);
                        mLessonQuestionList.setAdapter(adapter);
                    }
                    mLessonQuestionList.setStart(questionResult.start, questionResult.total);
                } catch (Exception ex) {
                    Log.d(TAG, ex.toString());
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
            }
        });


    }
}
