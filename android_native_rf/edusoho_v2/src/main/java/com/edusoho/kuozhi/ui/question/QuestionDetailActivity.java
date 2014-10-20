package com.edusoho.kuozhi.ui.question;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.edusoho.listener.URLImageGetter;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.HashMap;

/**
 * Created by hby on 14-9-17.
 * 问答详情
 */
public class QuestionDetailActivity extends ActionBarBaseActivity implements View.OnClickListener {


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

    public static String mHost = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_detail_layout);
        setBackMode(BACK, getIntent().getStringExtra(Const.QUESTION_TITLE));
        mActivity = this;
        initView();
        mHost = this.app.host;

    }

    private void initView() {
        mAQuery = new AQuery(this);

        mThreadId = getIntent().getIntExtra(Const.THREAD_ID, 0);
        mCourseId = getIntent().getIntExtra(Const.COURSE_ID, 0);

        //初始化
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
     * @param start 数据起始index
     */
    public void loadReplyDataFromSeek(int start, final boolean isRefresh) {
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
                        adapter = new QuestionReplyListAdapter(mContext, mActivity, replyResult, R.layout.question_reply_item, app.loginUser);
                    }
                    mQuestionRelyList.setAdapter(adapter);
                } catch (Exception ex) {
                    Log.e(TAG, ex.toString());
                }
            }
        });
    }

    /**
     * 获取问题信息
     */
    private void getQuestionPostUser() {
        RequestUrl url = app.bindUrl(Const.QUESTION_INFO, true);
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
                TextView tvContent = (TextView) findViewById(R.id.htv_post_content);
                ProgressBar contentLoading = (ProgressBar) findViewById(R.id.pb_content);
                if (!qdModel.content.contains("img src")) {
                    contentLoading.setVisibility(View.GONE);
                    tvContent.setVisibility(View.VISIBLE);
                }
                URLImageGetter urlImageGetter = new URLImageGetter(tvContent, mAQuery, mContext, contentLoading);
                tvContent.setText(AppUtil.setHtmlContent(Html.fromHtml(AppUtil.removeHtml(qdModel.content), urlImageGetter, null)));

                //mAQuery.id(R.id.htv_post_content).text(Html.fromHtml(AppUtil.removeHtml(qdModel.content)));
                //mAQuery.id(R.id.htv_post_content).text(Html.fromHtml(qdModel.content));
                mAQuery.id(R.id.btn_post_reply).clicked(QuestionDetailActivity.this);
                mAQuery.id(R.id.edu_btn_question_edit).clicked(QuestionDetailActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int requestCode = 0;
        if (v.getId() == R.id.btn_post_reply) {
            //普通回复
            requestCode = Const.REPLY;
        } else if (v.getId() == R.id.edu_btn_question_edit) {
            //编辑问题
            requestCode = Const.EDIT_QUESTION;
        }

        final int finalRequestCode = requestCode;
        app.mEngine.runNormalPluginForResult("QuestionReplyActivity", mActivity, requestCode, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(Const.REQUEST_CODE, finalRequestCode);
                startIntent.putExtra(Const.THREAD_ID, String.valueOf(mThreadId));
                startIntent.putExtra(Const.COURSE_ID, String.valueOf(mCourseId));
                if (finalRequestCode == Const.EDIT_QUESTION) {
                    startIntent.putExtra(Const.QUESTION_TITLE, mAQuery.id(R.id.post_title).getText().toString());
                    startIntent.putExtra(Const.QUESTION_CONTENT, Html.toHtml((Spanned) mAQuery.id(R.id.htv_post_content).getText()).toString());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //String content = data.getStringExtra(Const.NORMAL_CONTENT);
        switch (requestCode) {
            case Const.REPLY:
                loadReplyDataFromSeek(0, true);
                break;
            case Const.EDIT_QUESTION:
                //Toast.makeText(this, "问题编辑", 500).show();
                //getQuestionPostUser();
                if (data != null) {
                    QuestionDetailModel qdModel = (QuestionDetailModel) data.getSerializableExtra(Const.QUESTION_EDIT_RESULT);
                    TextView tvContent = (TextView) findViewById(R.id.htv_post_content);
                    ProgressBar contentLoading = (ProgressBar) findViewById(R.id.pb_content);
                    if (!qdModel.content.contains("img src")) {
                        contentLoading.setVisibility(View.GONE);
                        tvContent.setVisibility(View.VISIBLE);
                    }
                    URLImageGetter urlImageGetter = new URLImageGetter(tvContent, mAQuery, mContext, contentLoading);
                    tvContent.setText(AppUtil.setHtmlContent(Html.fromHtml(AppUtil.removeHtml(qdModel.content), urlImageGetter, null)));
                }
                break;
            case Const.EDIT_REPLY:
                //Toast.makeText(this, "回复编辑", 500).show();
                loadReplyDataFromSeek(0, true);
                break;
        }
    }
}
