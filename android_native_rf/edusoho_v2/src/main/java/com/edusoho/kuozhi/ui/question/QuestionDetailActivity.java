package com.edusoho.kuozhi.ui.question;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.QuestionReplyListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.ReplyResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.QuestionReplyListWidget;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.HashMap;

/**
 * Created by hby on 14-9-17.
 * 问答详情
 */
public class QuestionDetailActivity extends ActionBarBaseActivity implements View.OnClickListener {
    public static final String QUESTION_TITLE = "title";
    public static final String THREAD_ID = "thread_id";
    public static final String COURSE_ID = "course_id";

    private static final String TAG = "QuestionDetailActivity";
    private int mStart;
    private Button btnReply;

    private int mThreadId;
    private int mCourseId;

    private int mTeacherReplySum;

    private ActionBarBaseActivity mActivity;
    private QuestionReplyListWidget mQuestionRelyList;


    private AQuery mAQuery;

    private HashMap<String, String> mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_detail_layout);
        setBackMode(BACK, getIntent().getStringExtra(QUESTION_TITLE));
        mActivity = this;
        initView();
    }

    private void initView() {
        mAQuery = new AQuery(this);
        mAQuery.id(R.id.btn_post_reply).clicked(this);
        mThreadId = getIntent().getIntExtra(THREAD_ID, 0);
        mCourseId = getIntent().getIntExtra(COURSE_ID, 0);

        mParams = new HashMap<String, String>();
        mParams.put("courseId", String.valueOf(mCourseId));
        mParams.put("threadId", String.valueOf(mThreadId));

        mQuestionRelyList = (QuestionReplyListWidget) findViewById(R.id.qrlw_question_reply);

        mQuestionRelyList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadReplyDataFromSeek(0, true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadReplyDataFromSeek(mStart, false);
            }
        });


        mQuestionRelyList.setEmptyText(new String[]{"暂无回复"});

        getQuestionPostUser();
        loadReplyDataFromSeek(0, false);
    }

    /**
     * 普通回复
     *
     * @param start
     */
    private void loadReplyDataFromSeek(int start, final boolean isRefresh) {
        RequestUrl url = app.bindUrl(Const.NORMAL_REPLY, true);
        mParams.put("start", String.valueOf(start));
        mParams.put("limit", String.valueOf(Const.LIMIT));
        url.setParams(mParams);
        app.postUrl(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    mQuestionRelyList.onRefreshComplete();
                    ReplyResult replyResult = mActivity.gson.fromJson(object, new TypeToken<ReplyResult>() {
                    }.getType());
                    if (replyResult == null) {
                        return;
                    }
                    mStart = replyResult.total + replyResult.start;
                    QuestionReplyListAdapter adapter = (QuestionReplyListAdapter) mQuestionRelyList.getAdapter();
                    if (adapter != null) {
                        if (isRefresh) {
                            adapter.clearAdapter();
                        }
                        adapter.addItem(replyResult);
                    } else {
                        adapter = new QuestionReplyListAdapter(mContext, replyResult, R.layout.question_reply_item, app.loginUser);
                        mQuestionRelyList.setAdapter(adapter);
                    }

                } catch (Exception ex) {
                    Log.e(TAG, ex.toString());
                }

            }
        });
    }

    /**
     * 获取绑定问题信息
     */
    private void getQuestionPostUser() {
        RequestUrl url = app.bindUrl(Const.QUESITION_INFO, true);
        url.setParams(mParams);
        app.postUrl(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                QuestionDetailModel qdModel = mActivity.gson.fromJson(object, new TypeToken<QuestionDetailModel>() {
                }.getType());
                if (qdModel == null) {
                    return;
                }
                mAQuery.id(R.id.tv_post_name).text(qdModel.user.nickname);
                mAQuery.id(R.id.tv_post_date).text(AppUtil.getPostDays(qdModel.createdTime));
                mAQuery.id(R.id.post_title).text(qdModel.title);
                mAQuery.id(R.id.htv_post_content).text(Html.fromHtml(qdModel.content));
            }
        });
    }

    @Override
    public void onClick(View v) {
        int requestCode = 0;
        if (v.getId() == R.id.btn_post_reply) {
            //回复按钮
            requestCode = QuestionReplyActivity.REPLY;
        } else if (v.getId() == R.id.edu_btn_question_edit) {
            requestCode = QuestionReplyActivity.REPLY;
        }

        final int finalRequestCode = requestCode;
        app.mEngine.runNormalPluginForResult("QuestionReplyActivity", mActivity, requestCode, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(QuestionReplyActivity.REQUESTI_CODE, finalRequestCode);
                startIntent.putExtra(THREAD_ID, String.valueOf(mThreadId));
                startIntent.putExtra(COURSE_ID, String.valueOf(mCourseId));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String content = data.getStringExtra(QuestionReplyActivity.CONTENT);
            switch (requestCode) {
                case QuestionReplyActivity.REPLY:
                    break;
                case QuestionReplyActivity.EDIT_QUESTION:
                    break;
                case QuestionReplyActivity.EDIT_REPLY:
                    break;
            }
        }
    }
}
