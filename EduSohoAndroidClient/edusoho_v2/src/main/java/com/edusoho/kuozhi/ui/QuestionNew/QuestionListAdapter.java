package com.edusoho.kuozhi.ui.QuestionNew;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;

import java.util.ArrayList;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionListAdapter extends ListBaseAdapter<QuestionListData>{

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
        }
        tvQuestiongTitle = (TextView) view.findViewById(R.id.question_title);
        tvQuestionAnswerCount = (TextView) view.findViewById(R.id.question_answer_count);
        tvQuestionAnswerContent = (TextView) view.findViewById(R.id.question_answer_content);
        tvQuestionAnswerTime = (TextView) view.findViewById(R.id.question_answer_time);
        tvQuestionCourseTitle = (TextView) view.findViewById(R.id.question_course_title);

        QuestionListData questionListData = mList.get(i);
        tvQuestiongTitle.setText(questionListData.questiongTitle);
        tvQuestionAnswerCount.setText(questionListData.questionAnswerCount);
        tvQuestionAnswerContent.setText(questionListData.questionAnswerContent);
        tvQuestionAnswerTime.setText(questionListData.questionAnswerTime);
        tvQuestionCourseTitle.setText(questionListData.questionCourseTitle);

        return view;
    }
}
