package com.edusoho.kuozhi.ui.question;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.QuestionNew.QuestionListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.QuestionResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.fragment.RegistFragment;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import ch.boye.httpclientandroidlib.util.TextUtils;
import library.PullToRefreshBase;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionNewActivity extends ActionBarBaseActivity {
    private RefreshListWidget mQuestionList;
    private QuestionListAdapter mQuestionListAdapter;
    private View mLoadView;
    private String mTitle;
    private String mQuestionType;
    private String mSecondHeaderText;
    private int mEmptyIcon;
    private String mUrl;

    private final static int QUESTIONRESULT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_main_layout);
        init();
    }

    public void init() {
        initData();
        initView();
    }

    public void initData() {
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(Const.ACTIONBAR_TITLE);
        mQuestionType = intent.getStringExtra(Const.QUESTION_TYPE);
        if (mQuestionType.equals("question")) {
            mEmptyIcon = R.drawable.empty_no_question;
            mSecondHeaderText = "所学课程内暂无问答记录";
        } else {
            mEmptyIcon = R.drawable.empty_no_discussion;
            mSecondHeaderText = "所学课程内暂无讨论记录";
        }
        //mSecondHeaderText = intent.getStringExtra("empty_text");
        //mEmptyIcon = intent.getIntExtra("empty_icon", R.drawable.icon_question);
        mUrl = intent.getStringExtra(Const.QUESTION_URL);
    }

    private void initView() {
        mLoadView = this.findViewById(R.id.load_layout);
        mQuestionList = (RefreshListWidget) this.findViewById(R.id.question_list);
        mQuestionList.setEmptyText(mActivity, R.layout.empty_page_layout, new String[]{"去课程中发起一些问题吧", ""},
                new String[]{"去课程中发起一些讨论吧", mSecondHeaderText}, R.drawable.empty_logout, mEmptyIcon);
        mQuestionListAdapter = new QuestionListAdapter(this, R.layout.question_list_item_inflate);
        mQuestionList.setAdapter(mQuestionListAdapter);
        mQuestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final QuestionDetailModel questionDetailModel = (QuestionDetailModel) parent.getItemAtPosition(position);
//                Bundle bundle = new Bundle();
//                bundle.putInt(Const.THREAD_ID, questionDetailModel.id);
//                bundle.putInt(Const.COURSE_ID, questionDetailModel.courseId);
//                bundle.putInt(Const.QUESTION_USER_ID, questionDetailModel.user.id);
//                bundle.putString(Const.QUESTION_TITLE, questionDetailModel.title);
//                bundle.putString(FragmentPageActivity.FRAGMENT, "QuestionDetatilFragment");

                app.mEngine.runNormalPluginForResult("FragmentPageActivity", mActivity, QUESTIONRESULT, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.THREAD_ID, questionDetailModel.id);
                        startIntent.putExtra(Const.COURSE_ID, questionDetailModel.courseId);
                        startIntent.putExtra(Const.QUESTION_USER_ID, questionDetailModel.user.id);
                        startIntent.putExtra(Const.QUESTION_TITLE, questionDetailModel.title);
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "QuestionDetatilFragment");
                    }
                });
            }
        });
        setBackMode(BACK, mTitle);
        refushListener();
        getQuestionListReponseDatas(0);
    }

    public void refushListener() {
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
    public void getQuestionListReponseDatas(final int start) {
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }
        if (app.loginUser == null) {
            mQuestionList.setLoginStatus(false);
            mLoadView.setVisibility(View.GONE);
            mQuestionList.pushData(null);
            mQuestionList.setMode(PullToRefreshBase.Mode.DISABLED);
            return;
        } else {
            mQuestionList.setMode(PullToRefreshBase.Mode.BOTH);
            mQuestionList.setLoginStatus(true);
        }
        RequestUrl requestUrl = app.bindUrl(mUrl, true);
        requestUrl.setParams(new String[]{
                "strat", String.valueOf(start)
                , "limit", String.valueOf(Const.LIMIT)
                , "type", mQuestionType
        });
        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mQuestionList.onRefreshComplete();
                mLoadView.setVisibility(View.GONE);
                QuestionResult questionResult = parseJsonValue(object, new TypeToken<QuestionResult>() {
                });

                mQuestionList.pushData(questionResult.threads);
                mQuestionList.setStart(start, questionResult.total);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == LoginActivity.OK || resultCode == RegistFragment.OK) {
            getQuestionListReponseDatas(0);
        }
    }
}
