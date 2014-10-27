package com.edusoho.kuozhi.ui.question;

import android.content.Intent;
import android.os.Bundle;
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
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
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


    private static final String TAG = "QuestionDetailActivity";
    private int mStart;
    private Button btnReply;

    private int mThreadId;
    private int mCourseId;

    private ActionBarBaseActivity mActivity;
    private QuestionReplyListWidget mQuestionRelyList;


    private AQuery mAQuery;

    private HashMap<String, String> mParams;

    public static String mHost = "";

    private QuestionDetailModel mQuestionDetailModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_detail_layout);
        initView();
        setBackMode(BACK, getIntent().getStringExtra(Const.QUESTION_TITLE));
        mHost = this.app.host;
        mActivity = this;

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

        mQuestionRelyList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mQuestionRelyList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                loadReplyDataFromSeek(mStart, false);
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                loadReplyDataFromSeek(0, true);
            }
        });

        mQuestionRelyList.setEmptyText(new String[]{"暂无回复"});
        //getQuestionPostUser();
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
        this.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    mQuestionRelyList.onRefreshComplete();
                    ReplyResult replyResult = mActivity.gson.fromJson(object, new TypeToken<ReplyResult>() {
                    }.getType());
                    if (replyResult == null) {
                        return;
                    }
                    mStart = replyResult.limit + replyResult.start;
                    QuestionReplyListAdapter adapter = (QuestionReplyListAdapter) mQuestionRelyList.getAdapter();
                    if (adapter != null) {
                        if (isRefresh) {
                            //下拉刷新清空
                            adapter.clearAdapter();
                            adapter.setCacheClear();
                        }
                        adapter.addItem(replyResult);
                    } else {
                        //第一次打开
                        adapter = new QuestionReplyListAdapter(mContext, mActivity, replyResult, R.layout.question_reply_item, app.loginUser);
                    }
                    getQuestionPostUser(adapter, replyResult);
//                    mQuestionRelyList.setAdapter(adapter);
//                    mQuestionRelyList.setStart(replyResult.start, replyResult.total);
                } catch (Exception ex) {
                    Log.e(TAG, ex.toString());
                }
            }
        });
    }

    /**
     * 获取问题信息，绑定ListView
     */
    private void getQuestionPostUser(final QuestionReplyListAdapter adapter, final ReplyResult replyResult) {
        RequestUrl url = app.bindUrl(Const.QUESTION_INFO, true);
        url.setParams(mParams);
        app.postUrl(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mQuestionDetailModel = mActivity.gson.fromJson(object, new TypeToken<QuestionDetailModel>() {
                }.getType());
                adapter.setQuestionInfo(mQuestionDetailModel, R.layout.question_content_item);
                mQuestionRelyList.setAdapter(adapter);
                if (mQuestionRelyList.getAdapter() != null) {
                    ((QuestionReplyListAdapter) mQuestionRelyList.getAdapter()).setViewOnClickListener(QuestionDetailActivity.this);
                }
                mQuestionRelyList.setStart(replyResult.start, replyResult.total + 1);
                if (mQuestionDetailModel == null) {
                    return;
                }

//                mAQuery.id(R.id.tv_post_name).text(qdModel.user.nickname);
//                mAQuery.id(R.id.tv_post_date).text(AppUtil.getPostDays(qdModel.createdTime));
//                mAQuery.id(R.id.post_title).text(qdModel.title);
//                TextView tvContent = (TextView) findViewById(R.id.htv_post_content);
//                ProgressBar contentLoading = (ProgressBar) findViewById(R.id.pb_content);
//                if (!qdModel.content.contains("img src")) {
//                    contentLoading.setVisibility(View.GONE);
//                    tvContent.setVisibility(View.VISIBLE);
//                }
//                URLImageGetter urlImageGetter = new URLImageGetter(tvContent, mContext, contentLoading);
//                tvContent.setText(AppUtil.setHtmlContent(Html.fromHtml(AppUtil.removeHtml(qdModel.content), urlImageGetter, null)));


                mAQuery.id(R.id.btn_post_reply).clicked(QuestionDetailActivity.this);
                //mAQuery.id(R.id.edu_btn_question_edit).clicked(QuestionDetailActivity.this);
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
                    startIntent.putExtra(Const.QUESTION_TITLE, mQuestionDetailModel.title);
                    startIntent.putExtra(Const.QUESTION_CONTENT, mQuestionDetailModel.content);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //String content = data.getStringExtra(Const.NORMAL_CONTENT);
        switch (requestCode) {
            case Const.REPLY:
                mQuestionRelyList.clearAdapterCache();
                mQuestionRelyList.setRefreshing();
                //loadReplyDataFromSeek(0, true);
                break;
            case Const.EDIT_QUESTION:
                mQuestionRelyList.clearAdapterCache();
                mQuestionRelyList.setRefreshing();
                //Toast.makeText(this, "问题编辑", 500).show();
                //getQuestionPostUser();
//                if (data != null) {
//                    QuestionDetailModel qdModel = (QuestionDetailModel) data.getSerializableExtra(Const.QUESTION_EDIT_RESULT);
//                    TextView tvContent = (TextView) findViewById(R.id.htv_post_content);
//                    ProgressBar contentLoading = (ProgressBar) findViewById(R.id.pb_content);
//                    if (!qdModel.content.contains("img src")) {
//                        contentLoading.setVisibility(View.GONE);
//                        tvContent.setVisibility(View.VISIBLE);
//                    }
//                    URLImageGetter urlImageGetter = new URLImageGetter(tvContent, mContext, contentLoading);
//                    tvContent.setText(AppUtil.setHtmlContent(Html.fromHtml(AppUtil.removeHtml(qdModel.content), urlImageGetter, null)));
//                }
                break;
            case Const.EDIT_REPLY:
                //Toast.makeText(this, "回复编辑", 500).show();
                mQuestionRelyList.clearAdapterCache();
                mQuestionRelyList.setRefreshing();
                //loadReplyDataFromSeek(0, true);
                break;
        }
    }
}
