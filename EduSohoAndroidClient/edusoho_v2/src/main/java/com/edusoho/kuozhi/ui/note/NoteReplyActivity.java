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
import com.edusoho.kuozhi.model.Note.Note;
import com.edusoho.kuozhi.model.Note.NoteSubmitResult;
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
public class NoteReplyActivity extends ActionBarBaseActivity {
    private String TAG = "NoteReplyActivity";

    public static final String TYPE = "type";
    public static final int ADD = 0001;
    public static final int UPDATE = 0002;

    private RichTextBoxFragment richFragment;
    private ProgressDialog mProgressDialog;

    private String mCourseId;
    private String mLessonId;
    private String mTitle;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_reply_layout);
        initIntentData();
        initViews();
        initProgressDialog();
    }

    private void initIntentData()
    {
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(Const.ACTIONBAT_TITLE);
        mLessonId = intent.getStringExtra(Const.LESSON_ID);
        mCourseId = intent.getStringExtra(Const.COURSE_ID);
        mType = intent.getIntExtra(TYPE, UPDATE);
    }

    private void initViews() {
        setBackMode(BACK, mTitle);

        if (mType == ADD) {
            loadLessonNote();
        } else {
            showRichFragment(null);
        }
    }

    private void showRichFragment(String content)
    {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        richFragment = new RichTextBoxFragment();
        byte[] itemArgs = new byte[]{View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE};
        Bundle bundle = new Bundle();
        if (content != null) {
            getIntent().putExtra(Const.NORMAL_CONTENT, content);
        }
        bundle.putString(RichTextBoxFragment.HIT, "添加笔记");
        bundle.putByteArray(Const.RICH_ITEM_AGRS, itemArgs);
        richFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.linear, richFragment);
        fragmentTransaction.commit();
    }

    private void loadLessonNote()
    {
        RequestUrl requestUrl = app.bindUrl(Const.GET_LESSON_NOTE, true);
        requestUrl.setParams(new String[] {
                Const.COURSE_ID, String.valueOf(mCourseId),
                Const.LESSON_ID, String.valueOf(mLessonId),
        });
        setProgressBarIndeterminateVisibility(true);
        ajaxPost(requestUrl, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
                Note note = parseJsonValue(object, new TypeToken<Note>(){});
                if (note == null) {
                    showRichFragment("");
                    return;
                }
                showRichFragment(note.content);
            }
        });
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
                HashMap<String, String> params = url.params;
                params.put(Const.COURSE_ID, String.valueOf(mCourseId));
                params.put(Const.LESSON_ID, String.valueOf(mLessonId));
                final String content = AppUtil.removeHtml(Html.toHtml(richFragment.getContent()))
                                        + AppUtil.removeHtml(Html.toHtml(richFragment.getImageContent()));
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
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    NoteSubmitResult noteSubmitResult = mActivity.parseJsonValue(
                            object, new TypeToken<NoteSubmitResult>() {
                    });
                    mProgressDialog.cancel();
                    if (noteSubmitResult == null) {
                        return;
                    } else {
                        longToast("提交成功!");
                        setResult(Const.OK, new Intent().putExtra(Const.QUESTION_EDIT_RESULT, noteSubmitResult.content));
                        finish();
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
