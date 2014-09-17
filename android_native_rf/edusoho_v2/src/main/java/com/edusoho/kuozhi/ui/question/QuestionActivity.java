package com.edusoho.kuozhi.ui.question;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.QuestionListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.QuestionResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.QuestionRefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

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
                loadQuestionDataFromSeek(mStart);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadQuestionDataFromSeek(0);
            }
        });
        mQuestionRefreshList.setEmptyText(new String[]{"暂无提问的记录"});
        mQuestionRefreshList.setOnItemClickListener(new QuestionListScrollListener());
        loadQuestionDataFromSeek(0);
    }

    private void loadQuestionDataFromSeek(int position) {
        RequestUrl url = app.bindUrl(Const.QUESTION, true);

        this.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    mQuestionRefreshList.onRefreshComplete();
                    mLoadView.setVisibility(View.GONE);
                    QuestionResult questionResult = mActivity.gson.fromJson(object, new TypeToken<QuestionResult>() {
                    }.getType());
                    mStart = questionResult.start;
                    Log.d(TAG, String.valueOf(questionResult.limit));
                    if (questionResult == null) {
                        return;
                    }
                    mStart = questionResult.start;
                    QuestionListAdapter adapter = (QuestionListAdapter) mQuestionRefreshList.getAdapter();
                    if (adapter != null) {
                        adapter.addItem(questionResult);
                    } else {
                        adapter = new QuestionListAdapter(mContext, questionResult, R.layout.question_item);
                        mQuestionRefreshList.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

            }
        });
    }

    private class QuestionListScrollListener implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }

}
