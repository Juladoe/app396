package com.edusoho.kuozhi.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.ui.course.LessonActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.html.EduHtml;
import com.edusoho.kuozhi.util.html.EduImageGetterHandler;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
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
    private View mLessonEntrance;

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
        mTitle = bundle.getString(Const.ACTIONBAR_TITLE);
        mNoteContent = bundle.getString(CONTENT);
        mLessonId = bundle.getInt(Const.LESSON_ID);
        mCourseId = bundle.getInt(Const.COURSE_ID);
        mLessonTitle = bundle.getString(Const.LESSON_NAME);
        mLearnStatus = bundle.getString(Const.LEARN_STATUS);
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
        mLessonEntrance = view.findViewById(R.id.note_lesson_entrance);
        mLessonEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLessonInfo();
            }
        });

        setContent();
    }

    /**
     * 获取单个lesson信息并跳转到lessonActivity
     */
    private void getLessonInfo() {
        RequestUrl url = app.bindUrl(Const.LESSON, true);
        HashMap<String, String> params = url.getParams();
        params.put("courseId", mCourseId + "");
        params.put("lessonId", mLessonId + "");
        ResultCallback callback = new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                final LessonItem lessonItem = mActivity.gson.fromJson(object, new TypeToken<LessonItem>() {
                }.getType());
                if (lessonItem == null) {
                    return;
                }
                mActivity.getCoreEngine().runNormalPlugin(
                        LessonActivity.TAG, mActivity, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                if (mLearnStatus.equals("finished")) {
                                    isLearned = true;
                                }
                                startIntent.putExtra(Const.COURSE_ID, mCourseId);
                                //startIntent.putExtra(Const.FREE, lessonItem.free);
                                startIntent.putExtra(Const.LESSON_ID, mLessonId);
                                startIntent.putExtra(Const.LESSON_TYPE, lessonItem.type);
                                startIntent.putExtra(Const.ACTIONBAR_TITLE, mLessonTitle);
                                startIntent.putExtra(Const.IS_LEARN, isLearned);
                            }
                        }

                );
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
            }
        };
        mActivity.ajaxPost(url, callback);
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
            bundle.putString(Const.ACTIONBAR_TITLE, mTitle);
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
        content = content.replaceAll("\\n|\\t", "");
        Matcher m = Pattern.compile("<p>\\s*<img src=\".*?\" .>\\s*</p>|<img src=\".*?\" .>").matcher(content);
        while (m.find()) {
            imgList.add(m.group(0));
            content = content.replace(m.group(0), "");
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
