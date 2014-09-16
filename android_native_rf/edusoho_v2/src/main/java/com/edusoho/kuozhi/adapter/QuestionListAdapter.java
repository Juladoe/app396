package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.QuestionResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hby on 14-9-15.
 */
public class QuestionListAdapter extends EdusohoBaseAdapter {
    private Context mContext;
    private List<QuestionDetailModel> mQuestionList;
    private int mResourceId;

    public QuestionListAdapter(Context context, QuestionResult questionResult, int layoutId) {
        this.mContext = context;
        this.mResourceId = layoutId;
        mQuestionList = new ArrayList<QuestionDetailModel>();
        listAddItem(questionResult.threads);
        setMode(NORMAL);
    }

    public void addItem(QuestionResult questionResult) {
        setMode(UPDATE);
        listAddItem(questionResult.threads);
        notifyDataSetChanged();
    }

    private void listAddItem(QuestionDetailModel[] questionDetailModels) {
        for (QuestionDetailModel item : questionDetailModels) {
            mQuestionList.add(item);
        }
    }

    @Override
    public int getCount() {
        return mQuestionList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mQuestionList != null && mQuestionList.size() > 0) {
            return mQuestionList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, null);
            holder = new ViewHolder();
            holder.tvQuestionTitle = (TextView) convertView.findViewById(R.id.tv_question_title);
            holder.tvLesson = (TextView) convertView.findViewById(R.id.tv_question_lesson);
            holder.tvTeacherReply = (TextView) convertView.findViewById(R.id.tv_teacher_reply);
            holder.tvReplyAmount = (TextView) convertView.findViewById(R.id.tv_reply_amount);
            holder.aQuery = new AQuery(mContext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QuestionDetailModel question = mQuestionList.get(position);
        holder.tvQuestionTitle.setText(question.title);
        //holder.tvLesson.setText(question.questionLesson);
        if (true) {
            holder.tvTeacherReply.setVisibility(View.VISIBLE);
        } else {
            holder.tvTeacherReply.setVisibility(View.INVISIBLE);
        }

//        if (TextUtils.isEmpty(question.largeImageUrl)) {
//            holder.aQuery.id(R.id.iv_question).image(R.drawable.noram_course);
//        } else {
//            holder.aQuery.id(R.id.iv_question).image(question.largeImageUrl, false, true, 200, R.drawable.noram_course);
//        }

        holder.tvReplyAmount.setText(String.valueOf(question.postNum));
        return convertView;
    }

    private class ViewHolder {
        public AQuery aQuery;
        public TextView tvQuestionTitle;
        public TextView tvLesson;
        public TextView tvTeacherReply;
        public TextView tvReplyAmount;
    }
}
