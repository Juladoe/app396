package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.Notify;
import com.edusoho.kuozhi.model.Testpaper.MyTestpaperResult;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.fragment.TeacherInfoFragment;
import com.edusoho.kuozhi.ui.fragment.testpaper.TestpaperResultFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by howzhi on 14-9-16.
 */
public class MessageListAdapter extends ListBaseAdapter<Notify>
{
    public MessageListAdapter(
            Context context,  int resource)
    {
        super(context, resource);
    }

    @Override
    public void addItems(ArrayList<Notify> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int index) {
        return mList.get(index);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        if (view == null) {
            view = inflater.inflate(mResource, null);
        }

        TextView textView = (TextView) view;
        Notify notify = mList.get(index);

        textView.setText(coverSpanned(notify.message));
        textView.setClickable(true);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    private SpannableStringBuilder coverSpanned(String text)
    {
        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(text);
        CharacterStyle[] styleSpans = spanned.getSpans(0, spanned.length(), CharacterStyle.class);
        for (CharacterStyle styleSpan : styleSpans) {
            if (styleSpan instanceof URLSpan) {
                URLSpan urlSpan = (URLSpan) styleSpan;
                int start = spanned.getSpanStart(urlSpan);
                int end = spanned.getSpanEnd(urlSpan);
                spanned.removeSpan(urlSpan);
                spanned.setSpan(
                        new MessageUrlSpan(urlSpan.getURL(), spanned.subSequence(start, end)),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }

        return spanned;
    }

    private class MessageUrlSpan extends URLSpan
    {
        private CharSequence mTitle;

        public MessageUrlSpan(String url, CharSequence title)
        {
            super(url);
            this.mTitle = title;
        }

        @Override
        public void onClick(View widget) {
            parseUrlAction(getURL());
            Log.d(null, "click------>" + getURL());
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(false);
        }

        private void parseUrlAction(String url)
        {
            Matcher typeMatcher = TYPE_PAT.matcher(url);
            if (typeMatcher.find()) {
                String type1 = typeMatcher.group(1);
                String type1_value = typeMatcher.group(2);
                String type2 = typeMatcher.group(4);
                String type2_value = typeMatcher.group(5);
                String param = typeMatcher.group(7);
                String param_value = typeMatcher.group(8);

                Log.d(null, "type-->" + type1);
                if ("user".equalsIgnoreCase(type1)) {
                    showUser(AppUtil.parseInt(type1_value));
                } else if ("course".equalsIgnoreCase(type1)) {
                    if ("thread".equalsIgnoreCase(type2)) {
                        showThread(
                                AppUtil.parseInt(type1_value),
                                AppUtil.parseInt(type2_value),
                                mTitle.toString()
                        );
                        return;
                    }
                    showCourse(AppUtil.parseInt(type1_value));
                } else if ("test".equalsIgnoreCase(type1)) {
                    if ("result".equalsIgnoreCase(type2)) {
                        showTestPaperResult(AppUtil.parseInt(type1_value));
                    }
                }
            }
        }
    }

    private void showThread(int courseId, int threadId, String title)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.COURSE_ID, courseId);
        bundle.putInt(Const.THREAD_ID, threadId);
        bundle.putString(Const.QUESTION_TITLE, title);
        EdusohoApp.app.mEngine.runNormalPluginWithBundle("QuestionDetailActivity", mContext, bundle);
    }

    private void showTestPaperResult(int testResultId)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FragmentPageActivity.FRAGMENT, "TestpaperResultFragment");
        bundle.putInt(TestpaperResultFragment.RESULT_ID, testResultId);
        EdusohoApp.app.mEngine.runNormalPluginWithBundle(
                "FragmentPageActivity", mContext, bundle);
    }

    private void showCourse(final int courseId)
    {
        EdusohoApp.app.mEngine.runNormalPlugin(
                CourseDetailsActivity.TAG, mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(Const.COURSE_ID, courseId);
            }
        });
    }

    private void showUser(final int id)
    {
        EdusohoApp.app.mEngine.runNormalPlugin("FragmentPageActivity", mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "TeacherInfoFragment");
                startIntent.putExtra(Const.ACTIONBAT_TITLE, "用户信息");
                startIntent.putExtra(TeacherInfoFragment.TEACHER_ID, new int[]{ id });
            }
        });
    }

    public static Pattern TYPE_PAT = Pattern.compile(
            "/([a-zA-Z]+)/(\\w+)/?(([a-zA-Z]+)/?(\\w*))?(#(\\w+)-(\\w+))?",
            Pattern.DOTALL
    );
}