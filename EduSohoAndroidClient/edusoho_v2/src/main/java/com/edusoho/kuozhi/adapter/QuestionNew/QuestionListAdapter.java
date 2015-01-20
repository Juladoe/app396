package com.edusoho.kuozhi.adapter.QuestionNew;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionListAdapter extends ListBaseAdapter<QuestionDetailModel>{
    private ViewGroup.LayoutParams mTvLayoutParams;
    public QuestionListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void addItems(ArrayList list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mList != null && mList.size()>0){
            return mList.size();
        }
        return mList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView tvQuestiongTitle;
        TextView tvQuestionAnswerCount;
        TextView tvQuestionAnswerContent;
        TextView tvQuestionAnswerTime;
        TextView tvQuestionCourseTitle;


        if(view == null){
            view = inflater.from(mContext).inflate(mResource,null);
            mTvLayoutParams = ((TextView) view.findViewById(R.id.question_course_title)).getLayoutParams();
        }
        tvQuestiongTitle = (TextView) view.findViewById(R.id.question_title);
        tvQuestionAnswerCount = (TextView) view.findViewById(R.id.question_answer_count);
        tvQuestionAnswerContent = (TextView) view.findViewById(R.id.question_answer_content);
        tvQuestionAnswerTime = (TextView) view.findViewById(R.id.question_answer_time);
        tvQuestionCourseTitle = (TextView) view.findViewById(R.id.question_course_title);
        tvQuestionCourseTitle.setLayoutParams(mTvLayoutParams);

        QuestionDetailModel questionListData = mList.get(i);
        tvQuestiongTitle.setText(questionListData.title);
        tvQuestionAnswerCount.setText(String.valueOf(questionListData.postNum));
        if(questionListData.latestPostContent != null){
            tvQuestionAnswerContent.setText(Html.fromHtml(filtlerBlank(fitlerImgTag(questionListData.latestPostContent))));
            tvQuestionAnswerContent.setMaxLines(3);
        }else{
            tvQuestionAnswerContent.setText("");
            tvQuestionAnswerContent.setMaxLines(0);
        }
        tvQuestionAnswerTime.setText(AppUtil.getPostDays(questionListData.latestPostTime));

        if(questionListData.courseTitle.length() < 15){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,24,0);
            tvQuestionCourseTitle.setLayoutParams(layoutParams);
        }
        tvQuestionCourseTitle.setText(questionListData.courseTitle);

        return view;
    }

    //过滤html标签里的img图片
    private String fitlerImgTag(String content) {
        return content.replaceAll("(<img src=\".*?\" .>)", "");
    }

    private String filtlerBlank(String content){
        return content.replaceAll("<p[^>]*>|</p>|<br />","");
    }
}
