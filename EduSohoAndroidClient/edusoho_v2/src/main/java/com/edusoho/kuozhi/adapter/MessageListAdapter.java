package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Notify;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.QuestionResult;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.AboutFragment;
import com.edusoho.kuozhi.ui.fragment.FollowFragment;
import com.edusoho.kuozhi.ui.fragment.ProfileFragment;
import com.edusoho.kuozhi.ui.fragment.TeacherInfoFragment;
import com.edusoho.kuozhi.ui.fragment.testpaper.TestpaperResultFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.ESTextView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by howzhi on 14-9-16.
 */
public class MessageListAdapter extends ListBaseAdapter<Notify> {
    private ActionBarBaseActivity mActivity;
    private int mFontStartIndex = 0;
    private int mFontEndIndex = 0;

    public MessageListAdapter(
            Context context, ActionBarBaseActivity activity, int resource) {
        super(context, resource);
        mActivity = activity;
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

        TextView textView = (TextView) view.findViewById(R.id.message_content);
        Notify notify = mList.get(index);

        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(notify.message);
        String[] notifyStrs = spanned.toString().trim().split("[\\n\\n]");
        String content = notifyStrs[0];
        String notifyTime = notifyStrs[notifyStrs.length - 1];

        textView.setText(coverSpanned(notify.message));
        textView.setClickable(true);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    private SpannableStringBuilder coverSpanned(String text) {
        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(text, null, new Html.TagHandler() {
            @Override
            public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
                if (tag.equalsIgnoreCase("fontsize")) {
                    if (opening) {
                        mFontStartIndex = output.length();
                    } else {
                        mFontEndIndex = output.length();
                        output.setSpan(new AbsoluteSizeSpan(12, true),
                                mFontStartIndex,
                                mFontEndIndex,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        });
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

    private class MessageUrlSpan extends URLSpan {
        private CharSequence mTitle;

        public MessageUrlSpan(String url, CharSequence title) {
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

        private void parseUrlAction(String url) {
            Matcher typeMatcher = TYPE_PAT.matcher(url);
            if (typeMatcher.find()) {
                String type1 = typeMatcher.group(1);
                String type1_value = typeMatcher.group(2);
                String type2 = typeMatcher.group(4);
                String type2_value = typeMatcher.group(5);
                String param = typeMatcher.group(7);
                String param_value = typeMatcher.group(8);

                Log.d(null, "url-->" + url);
                if ("user".equalsIgnoreCase(type1)) {
                    showUser(AppUtil.parseInt(type1_value));
                    return;
                } else if ("course".equalsIgnoreCase(type1)) {
                    if ("thread".equalsIgnoreCase(type2)) {
                        showThread(AppUtil.parseInt(type1_value), AppUtil.parseInt(type2_value), this.mTitle.toString());
                        return;
                    }
                    showCourse(AppUtil.parseInt(type1_value));
                    return;
                } else if ("test".equalsIgnoreCase(type1)) {
                    if ("result".equalsIgnoreCase(type2)) {
                        showTestPaperResult(AppUtil.parseInt(type1_value));
                        return;
                    }
                }
            }

            if (!url.startsWith("http://")) {
                url = EdusohoApp.app.host + url;
            }
            showUrlInWebView(url);
        }
    }

    private void showUrlInWebView(String url) {
        //webview打开
        Bundle bundle = new Bundle();
        bundle.putString(AboutFragment.URL, url);
        bundle.putString(FragmentPageActivity.FRAGMENT, "AboutFragment");
        EdusohoApp.app.mEngine.runNormalPluginWithBundle(
                "FragmentPageActivity", mContext, bundle);
    }

    private void showThread(int courseId, int threadId, String title) {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.THREAD_ID, threadId);
        bundle.putInt(Const.COURSE_ID, courseId);
        bundle.putInt(Const.QUESTION_USER_ID, EdusohoApp.app.loginUser.id);
        bundle.putString(Const.QUESTION_TITLE, title);
        bundle.putString(FragmentPageActivity.FRAGMENT, "QuestionDetatilFragment");
        EdusohoApp.app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
    }

    private void showTestPaperResult(int testResultId) {
        Bundle bundle = new Bundle();
        bundle.putString(FragmentPageActivity.FRAGMENT, "TestpaperResultFragment");
        bundle.putInt(TestpaperResultFragment.RESULT_ID, testResultId);
        EdusohoApp.app.mEngine.runNormalPluginWithBundle(
                "FragmentPageActivity", mContext, bundle);
    }

    private void showCourse(final int courseId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.COURSE_ID, courseId);
        EdusohoApp.app.mEngine.runNormalPluginWithBundle("CorusePaperActivity", mContext, bundle);
    }

    private void showUser(final int userId) {
        RequestUrl url = mActivity.app.bindUrl(Const.USERINFO, true);
        url.setParams(new String[]{
                "userId", userId + ""
        });

        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                final User user = mActivity.gson.fromJson(object, new TypeToken<User>() {
                }.getType());
                if (user == null) {
                    ToastUtils.show(mContext, "获取不到该用户信息");
                    return;
                }
                EdusohoApp.app.mEngine.runNormalPlugin("FragmentPageActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, user.nickname);
                        startIntent.putExtra(FragmentPageActivity.FRAGMENT, "ProfileFragment");
                        startIntent.putExtra(ProfileFragment.FOLLOW_USER, user);
                        startIntent.putExtra(FollowFragment.FOLLOW_TYPE, FollowFragment.OTHER);
                    }
                });
            }
        });


    }

    public static Pattern TYPE_PAT = Pattern.compile(
            "/([a-zA-Z]+)/(\\w+)/?(([a-zA-Z]+)/?(\\w*))?(#(\\w+)-(\\w+))?",
            Pattern.DOTALL
    );
}