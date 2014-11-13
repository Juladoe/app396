package com.edusoho.kuozhi.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.html.EduHtml;
import com.edusoho.kuozhi.util.html.EduImageGetterHandler;
import com.edusoho.kuozhi.util.html.EduTagHandler;

/**
 * Created by howzhi on 14-10-31.
 */
public class NoteContentFragment extends BaseFragment {

    public static final String CONTENT = "noteContent";
    public static final int REQUEST_RICH_FRAGMENT = 0010;

    private String mTitle;
    private String mNoteContent;
    private int mLessonId;
    private int mCourseId;

    private TextView mNoteContentView;

    @Override
    public String getTitle() {
        return "笔记内容";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setContainerView(R.layout.note_content_layout);
        initIntentData();
    }

    public void initIntentData() {
        Bundle bundle = getArguments();

        mTitle = bundle.getString(Const.ACTIONBAT_TITLE);
        mNoteContent = bundle.getString(CONTENT);
        mLessonId = bundle.getInt(Const.LESSON_ID);
        mCourseId = bundle.getInt(Const.COURSE_ID);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mNoteContentView = (TextView) view.findViewById(R.id.note_content);
        setContent();
    }

    private void setContent()
    {
        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(
                mNoteContent,
                new EduImageGetterHandler(mContext, mNoteContentView),
                new EduTagHandler()
        );
        spanned = EduHtml.addImageClickListener(spanned, mNoteContentView, mContext);
        mNoteContentView.setText(spanned);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.note_edit)
            turnToNoteReply(mCourseId, mLessonId, mTitle, mNoteContent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_reply_menu, menu);
    }

    private void turnToNoteReply(
            int courseId, int lessonId, String title, String content) {
        Bundle bundle = new Bundle();
        bundle.putString(Const.ACTIONBAT_TITLE, title);
        bundle.putString(Const.LESSON_ID, String.valueOf(lessonId));
        bundle.putString(Const.COURSE_ID, String.valueOf(courseId));
        bundle.putString(Const.NORMAL_CONTENT, content);

        startActivityWithBundleAndResult("NoteReplyActivity", REQUEST_RICH_FRAGMENT, bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_RICH_FRAGMENT && resultCode == Const.OK) {
            if (data != null) {
                String resultContent = data.getStringExtra(Const.QUESTION_EDIT_RESULT);
                mNoteContent = resultContent;
                setContent();
            }
        }
    }
}
