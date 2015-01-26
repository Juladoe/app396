package com.edusoho.kuozhi.ui.questionnew;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.QuestionNew.QuestionReplyAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.OneReply;
import com.edusoho.kuozhi.model.Question.ReplyModel;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionReplyFragment extends BaseFragment {
    private View mLoadLayoutView;
    private ReplyModel mReplyModel;
    private TextView mQuestionAnswerContent;
    private ListView mQuestionAnswerContentImage;

    private HashMap<String, String> mOneReplyParams = new HashMap<String, String>();

    private OneReply mOneReply;
    private int mThreadId;
    private int mUseId;
    private String mContent;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.question_reply_fragment_layout);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mUseId == mReplyModel.userId) {
            inflater.inflate(R.menu.question_reply_new_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.question_reply_edit) {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.REQUEST_CODE, Const.EDIT_REPLY);
            bundle.putString(Const.THREAD_ID, String.valueOf(mThreadId));
            bundle.putString(Const.COURSE_ID, String.valueOf(mReplyModel.courseId));
            bundle.putString(Const.POST_ID, String.valueOf(mReplyModel.id));
            bundle.putString(Const.NORMAL_CONTENT, AppUtil.filterSpace(mContent));
            startActivityWithBundleAndResult("QuestionReplyActivity", Const.EDIT_REPLY, bundle);
        }
        return true;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        Bundle bundle = getArguments();
        mReplyModel = (ReplyModel) bundle.getSerializable(Const.QUESTION_CONTENT);
        changeTitle(bundle.getString(Const.QUESTION_TITLE));
        mThreadId = bundle.getInt(Const.THREAD_ID);
        mContent = mReplyModel.content;
        mUseId = bundle.getInt(Const.USER_ID);

        mOneReplyParams.put("courseId", String.valueOf(mReplyModel.courseId));
        mOneReplyParams.put("postId", String.valueOf(mReplyModel.id));

        mLoadLayoutView = view.findViewById(R.id.load_layout);
        CircularImageView circularImageView = (CircularImageView) view.findViewById(R.id.question_answer_head_image);
        ImageLoader.getInstance().displayImage(mReplyModel.user.mediumAvatar, circularImageView);
        ((TextView) view.findViewById(R.id.question_answer_user_name)).setText(mReplyModel.user.nickname);
        ((TextView) view.findViewById(R.id.question_answer_time)).setText(AppUtil.getPostDays(mReplyModel.createdTime));
        mQuestionAnswerContent = ((TextView) view.findViewById(R.id.question_answer_content));
        mQuestionAnswerContentImage = (ListView) view.findViewById(R.id.question_answer_image_list);

        getQuestionOneReplyReponseData();
    }

    public void getQuestionOneReplyReponseData() {
        RequestUrl requestUrl = app.bindUrl(Const.ONE_REPLY, true);
        requestUrl.setParams(mOneReplyParams);

        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mLoadLayoutView.setVisibility(View.GONE);
                mOneReply = mActivity.parseJsonValue(object, new TypeToken<OneReply>() {
                });
                setQuestionOneReplyData();
            }
        });
    }

    public void setQuestionOneReplyData() {
        mQuestionAnswerContent.setText(Html.fromHtml(fitlerImgTag(mOneReply.content)));
        mContent = mOneReply.content;
        mContent = removeImgPath(mContent).toString();

        QuestionReplyAdapter questionReplyAdapter = new QuestionReplyAdapter(mContext, R.layout.question_reply_inflate);
        mQuestionAnswerContentImage.setAdapter(questionReplyAdapter);
        questionReplyAdapter.addItems(convertUrlStringList(mOneReply.content));
    }

    private String fitlerImgTag(String content) {
        return content.replaceAll("(<img src=\".*?\" .>)", "");
    }

    private ArrayList<String> convertUrlStringList(String content) {
        ArrayList<String> urlLits = new ArrayList<String>();
        Matcher m = Pattern.compile("(img src=\".*?\")").matcher(content);
        while (m.find()) {
            String[] s = m.group(1).split("src=");
            String strUrl = s[1].toString().substring(1, s[1].length() - 1);
            if (strUrl.indexOf("http:") == -1) {
                strUrl = app.defaultSchool.host + strUrl;
            }
            urlLits.add(strUrl);
        }
        return urlLits;
    }

    public StringBuffer removeImgPath(String content) {
        StringBuffer result = new StringBuffer();
        Matcher m = Pattern.compile("(img src=\".*?\")").matcher(content);
        while (m.find()) {
            String[] s = m.group(1).split("src=");
            String strUrl = s[1].toString().substring(1, s[1].length() - 1);
            if (strUrl.indexOf("http:") == -1) {
                strUrl = "img src=\"" + app.defaultSchool.host + strUrl;
                m.appendReplacement(result, strUrl);
            }
        }
        m.appendTail(result);
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.EDIT_REPLY) {
            getQuestionOneReplyReponseData();
        }
    }
}
