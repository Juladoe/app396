package com.edusoho.kuozhi.ui.QuestionNew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.QuestionNew.QuestionListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.QuestionResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import library.PullToRefreshBase;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionNewActivity extends ActionBarBaseActivity{
    private RefreshListWidget mQuestionList;
    private QuestionListAdapter mQuestionListAdapter;
    private View mLoadView;
    private String mTitle;
    private String mquestionType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_main_layout);
        init();
    }

    public void init(){
        initData();
        initView();
    }

    public void initData(){
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(Const.ACTIONBAR_TITLE);
        mquestionType = intent.getStringExtra(Const.QUESTION_TYPE);
    }

    private void initView() {
        mLoadView = this.findViewById(R.id.load_layout);
        mQuestionList = (RefreshListWidget) this.findViewById(R.id.question_list);
        mQuestionList.setEmptyText(new String[]{"没有提问"});
        mQuestionList.setMode(PullToRefreshBase.Mode.BOTH);
        mQuestionListAdapter = new QuestionListAdapter(this,R.layout.question_list_item_inflate);
        mQuestionList.setAdapter(mQuestionListAdapter);
        mQuestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuestionDetailModel questionDetailModel = (QuestionDetailModel) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putInt(Const.THREAD_ID,questionDetailModel.id);
                bundle.putInt(Const.COURSE_ID,questionDetailModel.courseId);
                bundle.putString(FragmentPageActivity.FRAGMENT, "QuestionDetatilFragment");
                app.mEngine.runNormalPluginWithBundle("FragmentPageActivity",mActivity,bundle);
            }
        });
        setBackMode(BACK,mTitle);
        refushListener();
        getQuestionListReponseDatas(0);
    }

    public void refushListener(){
        mQuestionList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                getQuestionListReponseDatas(0);
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                getQuestionListReponseDatas(mQuestionList.getStart());
            }
        });
    }

    //获取问答显示列表
    public void getQuestionListReponseDatas(final int start){
        RequestUrl requestUrl = app.bindUrl(Const.QUESTION, true);
        requestUrl.setParams(new String[]{
                "strat", String.valueOf(start)
                ,"limit",String.valueOf(Const.LIMIT)
                ,"type",mquestionType
        });
        ajaxPost(requestUrl,new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mQuestionList.onRefreshComplete();
                mLoadView.setVisibility(View.GONE);
                QuestionResult questionResult = parseJsonValue(object,new TypeToken<QuestionResult>(){});

                mQuestionList.pushData(questionResult.threads);
                mQuestionList.setStart(start,questionResult.total);
            }
        });
    }

}
