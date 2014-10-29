package com.edusoho.kuozhi.ui.question;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.SubmitResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.edusoho.plugin.RichTextBox.RichTextBoxFragment;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class QuestionReplyActivity extends ActionBarBaseActivity {
    private static final String TAG = "QuestionReplyActivity";

    private RichTextBoxFragment richFragment;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_reply_layout);
        setBackMode(BACK, "添加回复");
        initViews();
        initProgressDialog();
    }

    private void initViews() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        richFragment = new RichTextBoxFragment();
        byte[] itemArgs = new byte[]{View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE};
        Bundle bundle = new Bundle();
        bundle.putByteArray(Const.RICH_ITEM_AGRS, itemArgs);
        richFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.linear, richFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question_reply_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.reply_submit) {
            if (richFragment.getContent().toString() == null || richFragment.getContent().toString().equals("")) {
                Toast.makeText(mActivity, "回复内容不能为空", Toast.LENGTH_LONG).show();
                return true;
            } else {
                switch (richFragment.getTypeCode()) {
                    case Const.REPLY: {
                        //新增回复api
                        //Toast.makeText(this, "新增回复api", 500).show();
                        RequestUrl url = app.bindUrl(Const.REPLY_SUBMIT, true);
                        HashMap<String, String> params = url.getParams();
                        params.put("courseId", richFragment.getCourseId());
                        params.put("threadId", richFragment.getThreadId());
                        final String content = AppUtil.removeHtml(Html.toHtml(richFragment.getContent()));
                        params.put("content", richFragment.setContent(content));
                        params.put("imageCount", String.valueOf(richFragment.getImageHashMapSize()));
                        url.setMuiltParams(richFragment.getObjects());
                        url.setParams(params);
                        submitReply(url);
                        break;
                    }
                    case Const.EDIT_QUESTION: {
                        RequestUrl url = app.bindUrl(Const.EDIT_QUESTION_INFO, true);
                        HashMap<String, String> params = url.getParams();
                        params.put("courseId", richFragment.getCourseId());
                        params.put("threadId", richFragment.getThreadId());
                        params.put("title", richFragment.getTitle());
                        final String content = AppUtil.removeHtml(Html.toHtml(richFragment.getContent()));
                        params.put("content", richFragment.setContent(content));
                        params.put("imageCount", String.valueOf(richFragment.getImageHashMapSize()));
                        url.setMuiltParams(richFragment.getObjects());
                        url.setParams(params);
                        editQuestionSubmit(url);
                        break;
                    }
                    case Const.EDIT_REPLY: {
                        //编辑回复api
                        //Log.e(TAG, Html.toHtml(etContent.getText()).toString());
                        RequestUrl url = app.bindUrl(Const.REPLY_EDIT_SUBMIT, true);
                        HashMap<String, String> params = url.getParams();
                        params.put("courseId", richFragment.getCourseId());
                        params.put("threadId", richFragment.getThreadId());
                        params.put("postId", richFragment.getPostId());
                        final String content = AppUtil.removeHtml(Html.toHtml(richFragment.getContent()));
                        params.put("content", richFragment.setContent(content));
                        params.put("imageCount", String.valueOf(richFragment.getImageHashMapSize()));
                        url.setMuiltParams(richFragment.getObjects());
                        url.setParams(params);
                        submitReply(url);
                        break;
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 回复提交
     */
    private void submitReply(RequestUrl url) {
        mProgressDialog.show();
        this.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    SubmitResult submitResult = mActivity.gson.fromJson(object, new TypeToken<SubmitResult>() {
                    }.getType());
                    mProgressDialog.cancel();
                    if (submitResult == null) {
                        return;
                    } else {
                        Toast.makeText(mActivity, "提交成功", 500).show();
                        mActivity.setResult(Const.OK);
                        mActivity.finish();
                    }
                } catch (Exception ex) {
                    mProgressDialog.cancel();
                    Log.e(TAG, ex.toString());
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                if (ajaxStatus.getCode() != 200) {
                    Log.e(TAG, String.valueOf(ajaxStatus.getCode()));
                }
            }
        });
    }

    /**
     * 编辑问题提交
     *
     * @param url
     */
    private void editQuestionSubmit(RequestUrl url) {
        mProgressDialog.show();
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    QuestionDetailModel modelResult = mActivity.gson.fromJson(object, new TypeToken<QuestionDetailModel>() {
                    }.getType());
                    mProgressDialog.cancel();
                    if (modelResult == null) {
                        return;
                    } else {
                        Toast.makeText(mContext, "提交成功", 500).show();
                        mActivity.setResult(Const.OK, new Intent().putExtra(Const.QUESTION_EDIT_RESULT, modelResult));
                        mActivity.finish();
                    }
                } catch (Exception ex) {
                    mProgressDialog.cancel();
                    Log.e(TAG, ex.toString());
                }
            }
        });
    }

    /**
     * 初始化对话框
     */
    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("提交中...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(true);
    }
}
