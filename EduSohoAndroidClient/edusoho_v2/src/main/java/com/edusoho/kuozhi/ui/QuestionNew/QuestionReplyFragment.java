package com.edusoho.kuozhi.ui.QuestionNew;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Question.ReplyModel;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionReplyFragment extends BaseFragment{
//    private View mQuestionReplyLoadView;
    private ListView mQuestionAnswerContentImage;
    private ReplyModel mReplyModel;
    private int mThreadId;
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
        inflater.inflate(R.menu.question_reply_new_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.question_reply_edit){
            Bundle bundle = new Bundle();
            bundle.putInt(Const.REQUEST_CODE, Const.EDIT_REPLY);
            bundle.putString(Const.THREAD_ID, String.valueOf(mThreadId));
            bundle.putString(Const.COURSE_ID, String.valueOf(mReplyModel.courseId));
            bundle.putString(Const.POST_ID, String.valueOf(mReplyModel.id));
            bundle.putString(Const.NORMAL_CONTENT, AppUtil.filterSpace(mReplyModel.content));
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

//        mQuestionReplyLoadView = view.findViewById(R.id.load_layout);
        ImageView imageView = (ImageView) view.findViewById(R.id.question_answer_head_image);
        ImageLoader.getInstance().displayImage(mReplyModel.user.mediumAvatar,imageView);
        ((TextView)view.findViewById(R.id.question_answer_user_name)).setText(mReplyModel.user.nickname);
        ((TextView)view.findViewById(R.id.question_answer_time)).setText(AppUtil.getPostDays(mReplyModel.createdTime));
        ((TextView)view.findViewById(R.id.question_answer_content)).setText(Html.fromHtml(fitlerImgTag(mReplyModel.content)));
        mQuestionAnswerContentImage = (ListView) view.findViewById(R.id.question_answer_image_list);
        QuestionReplyAdapter questionReplyAdapter = new QuestionReplyAdapter(mContext,R.layout.question_reply_inflate);
        mQuestionAnswerContentImage.setAdapter(questionReplyAdapter);
        questionReplyAdapter.addItems(convertUrlStringList(mReplyModel.content));
//        mQuestionReplyLoadView.setVisibility(View.GONE);
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
            urlLits.add(strUrl);
        }
        return urlLits;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Const.EDIT_REPLY){

        }
    }
}
