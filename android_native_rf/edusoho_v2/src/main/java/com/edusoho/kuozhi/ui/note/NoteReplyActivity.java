package com.edusoho.kuozhi.ui.note;

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

/**
 * Created by onewoman on 14-10-27.
 */
public class NoteReplyActivity extends ActionBarBaseActivity{
    private String TAG = "NoteReplyActivity";
    private RichTextBoxFragment richFragment;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_reply_layout);
        setBackMode(BACK, getIntent().getStringExtra("note_title"));
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
                Toast.makeText(mActivity, "内容不能为空", Toast.LENGTH_LONG).show();
                return true;
            } else {
                RequestUrl url = app.bindUrl(Const.NOTE_CONTENT, true);
                HashMap<String, String> params = url.getParams();
                params.put("courseId", getIntent().getIntExtra("note_courseId",0) + "");
                params.put("lessonId", getIntent().getIntExtra("note_lessonId",0) + "");
                final String content = AppUtil.removeHtml(Html.toHtml(richFragment.getContent()));
                params.put("content", richFragment.setContent(content));
                params.put("imageCount", String.valueOf(richFragment.getImageHashMapSize()));
                url.setMuiltParams(richFragment.getObjects());
                url.setParams(params);
                editNoteContentSubmit(url);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 编辑笔记内容提交
     *
     * @param url
     */
    private void editNoteContentSubmit(RequestUrl url) {
        mProgressDialog.show();
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    NoteSubmitResult noteSubmitResult = mActivity.gson.fromJson(object, new TypeToken<NoteSubmitResult>() {
                    }.getType());
                    mProgressDialog.cancel();
                    if (noteSubmitResult == null) {
                        return;
                    } else {
                        Toast.makeText(mContext, "提交成功", 500).show();
                        mActivity.setResult(Const.OK, new Intent().putExtra(Const.QUESTION_EDIT_RESULT, noteSubmitResult));
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
