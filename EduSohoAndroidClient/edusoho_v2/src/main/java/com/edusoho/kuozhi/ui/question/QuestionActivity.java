package com.edusoho.kuozhi.ui.question;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ErrorAdapter;
import com.edusoho.kuozhi.adapter.Question.QuestionListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.QuestionResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.QuestionRefreshListWidget;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import library.PullToRefreshBase;

/**
 * @author hby
 *         我的问答/讨论
 */
public class QuestionActivity extends ActionBarBaseActivity {
    private static final String TAG = "QuestionActivity";
    private QuestionRefreshListWidget mQuestionRefreshList;
    private View mLoadView;
    private int mStart;
    private ActionBarBaseActivity mActivity;
    private String mType;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_question);
        initView();
        setBackMode(BACK, mTitle);
    }


    private void initView() {
        mActivity = this;
        mTitle = getIntent().getStringExtra(Const.ACTIONBAT_TITLE);
        mType = getIntent().getStringExtra(Const.QUESTION_TYPE);
        mLoadView = (View) findViewById(R.id.load_layout);
        mQuestionRefreshList = (QuestionRefreshListWidget) findViewById(R.id.question_listview);
        mQuestionRefreshList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mQuestionRefreshList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                loadQuestionDataFromSeek(mStart, false);
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                loadQuestionDataFromSeek(0, true);
            }
        });

        mQuestionRefreshList.setEmptyText(new String[]{"暂无提问的记录"});
        mQuestionRefreshList.setOnItemClickListener(new QuestionListScrollListener());
        loadQuestionDataFromSeek(0, false);
    }

    private void loadQuestionDataFromSeek(int start, final boolean isRefresh) {
        RequestUrl url = app.bindUrl(Const.QUESTION, true);
        HashMap<String, String> params = url.getParams();
        params.put("type", mType);
        params.put("start", String.valueOf(start));
        params.put("limit", String.valueOf(Const.LIMIT));

        this.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    mQuestionRefreshList.onRefreshComplete();
                    mLoadView.setVisibility(View.GONE);
                    QuestionResult questionResult = mActivity.gson.fromJson(object, new TypeToken<QuestionResult>() {
                    }.getType());
                    if (questionResult == null) {
                        return;
                    }
                    mStart = questionResult.limit + questionResult.start;
                    QuestionListAdapter adapter = (QuestionListAdapter) mQuestionRefreshList.getAdapter();
                    if (adapter != null) {
                        //刷新
                        if (isRefresh) {
                            adapter.clearAdapter();
                        }
                        adapter.addItems(questionResult.threads);
                    } else {
                        adapter = new QuestionListAdapter(mContext, R.layout.question_item);
                        adapter.addItems(questionResult.threads);
                        mQuestionRefreshList.setAdapter(adapter);
                    }
                    mQuestionRefreshList.setStart(questionResult.start, questionResult.total);
                } catch (Exception ex) {
                    Log.e(TAG, ex.toString());
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                if (ajaxStatus.getCode() != 200) {
                    mQuestionRefreshList.setMode(PullToRefreshBase.Mode.DISABLED);
                    mLoadView.setVisibility(View.GONE);
                    ErrorAdapter<String> errorAdapter = new ErrorAdapter<String>(mContext, new String[]{"加载失败，请点击重试"},
                            R.layout.list_error_layout, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mQuestionRefreshList.setMode(PullToRefreshBase.Mode.BOTH);
                            loadQuestionDataFromSeek(0, true);
                        }
                    });
                    mQuestionRefreshList.setAdapter(errorAdapter);
                }
            }
        });
    }

    private class QuestionListScrollListener implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final String title = ((QuestionDetailModel) parent.getItemAtPosition(position)).title;
            final int courseId = ((QuestionDetailModel) parent.getItemAtPosition(position)).courseId;
            final int threadId = ((QuestionDetailModel) parent.getItemAtPosition(position)).id;
            app.mEngine.runNormalPlugin("QuestionDetailActivity", mActivity, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(Const.QUESTION_TITLE, title);
                    startIntent.putExtra(Const.COURSE_ID, courseId);
                    startIntent.putExtra(Const.THREAD_ID, threadId);
                }
            });
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }

}
