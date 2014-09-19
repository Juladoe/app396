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
import com.edusoho.kuozhi.adapter.QuestionListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.QuestionResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.QuestionRefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.HashMap;

/**
 * @author hby
 *         我的问答
 */
public class QuestionActivity extends ActionBarBaseActivity {
    private static final String TAG = "QuestionActivity";
    private QuestionRefreshListWidget mQuestionRefreshList;
    private View mLoadView;
    private int mStart;
    private ActionBarBaseActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_question);
        initView();
        setBackMode(BACK, "我的问答");
    }


    private void initView() {
        mActivity = this;
        mLoadView = (View) findViewById(R.id.load_layout);
        mQuestionRefreshList = (QuestionRefreshListWidget) findViewById(R.id.question_listview);
        mQuestionRefreshList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadQuestionDataFromSeek(0, true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadQuestionDataFromSeek(mStart, false);
            }
        });
        mQuestionRefreshList.setEmptyText(new String[]{"暂无提问的记录"});
        mQuestionRefreshList.setOnItemClickListener(new QuestionListScrollListener());
        loadQuestionDataFromSeek(0, false);
    }

    private void loadQuestionDataFromSeek(int start, final boolean isRefresh) {
        RequestUrl url = app.bindUrl(Const.QUESTION, true);
        HashMap<String, String> params = url.getParams();
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
                    mStart = questionResult.total + questionResult.start;
                    QuestionListAdapter adapter = (QuestionListAdapter) mQuestionRefreshList.getAdapter();
                    if (adapter != null) {
                        //刷新
                        if (isRefresh) {
                            adapter.clearAdapter();
                        }
                        adapter.addItem(questionResult);
                    } else {
                        adapter = new QuestionListAdapter(mContext, questionResult, R.layout.question_item);
                        mQuestionRefreshList.setAdapter(adapter);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.toString());
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
                    startIntent.putExtra(QuestionDetailActivity.QUESTION_TITLE, title);
                    startIntent.putExtra(QuestionDetailActivity.COURSE_ID, courseId);
                    startIntent.putExtra(QuestionDetailActivity.THREAD_ID, threadId);
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
