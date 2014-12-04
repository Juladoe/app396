package com.edusoho.kuozhi.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.ui.course.LessonActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.html.EduHtml;
import com.edusoho.kuozhi.util.html.EduImageGetterHandler;
import com.edusoho.kuozhi.util.html.EduTagHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String mLearnStatus;
    private Boolean isLearned = false;
    private String mLessonTitle;

    private TextView mNoteContentView;
    private TextView mNoteTitleView;
    private ImageView mLessonEntrance;

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
        mLessonTitle = bundle.getString(Const.LESSON_NAME);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        changeTitle("笔记");

        mNoteContentView = (TextView) view.findViewById(R.id.note_content);
        mNoteTitleView = (TextView) view.findViewById(R.id.note_lesson_title);
        mNoteTitleView.setText(mLessonTitle);

        /**
         * 跳转到课时页面
         */
        mLessonEntrance = (ImageView) view.findViewById(R.id.lesson_entrance);
        mLessonEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.getCoreEngine().runNormalPlugin(
                        LessonActivity.TAG, mActivity, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                if (mLearnStatus == "finished")
                                    isLearned = true;
//                                startIntent.putExtra(Const.COURSE_ID, mCourseId);
//                                startIntent.putExtra(Const.FREE, );
//                                startIntent.putExtra(Const.LESSON_ID, mLessonId);
//                                startIntent.putExtra(Const.LESSON_TYPE, lesson.type);
//                                startIntent.putExtra(Const.ACTIONBAT_TITLE, mLessonTitle);
//                                startIntent.putExtra(Const.LIST_JSON, mLessonListJson);
//                                startIntent.putExtra(Const.IS_LEARN, isLearned);
                            }
                        }

                );
            }
        });
        setContent();
    }

    private void setContent() {
        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(setImgToEnd(mNoteContent), new EduImageGetterHandler(mContext, mNoteContentView),
                null);
        spanned = EduHtml.addImageClickListener(spanned, mNoteContentView, mContext);
        mNoteContentView.setText(spanned);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.note_edit) {
            Bundle bundle = new Bundle();
            bundle.putString(Const.ACTIONBAT_TITLE, mTitle);
            bundle.putString(Const.LESSON_ID, String.valueOf(mLessonId));
            bundle.putString(Const.COURSE_ID, String.valueOf(mCourseId));
            bundle.putString(Const.NORMAL_CONTENT, mNoteContent);
            startActivityWithBundleAndResult("NoteReplyActivity", REQUEST_RICH_FRAGMENT, bundle);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_reply_menu, menu);
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

    /**
     * 把img放到content的最后，并在图片两端加上<p><p/>
     *
     * @param content
     * @return
     */
    private String setImgToEnd(String content) {
        List<String> imgList = new ArrayList<String>();
        Matcher m = Pattern.compile("(<img src=\".*?\" .>)").matcher(content);
        while (m.find()) {
            imgList.add(m.group(1));
            content = content.replace(m.group(1), "");
        }
        if (imgList.size() > 0) {
            for (String imgStr : imgList) {
                content = content + "<p>" + imgStr + "</p>";
            }
        }
        content = content.replaceAll("\\n|\\t", "");
        return content;
    }
}
