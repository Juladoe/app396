package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.QuestionResult;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hby on 14-9-15.
 */
public class QuestionListAdapter extends ListBaseAdapter {
//    private Context mContext;
    private List<QuestionDetailModel> mQuestionList;
//    private int mResourceId;
    private int mScreenW;

    public QuestionListAdapter(Context context, QuestionResult questionResult, int layoutId) {
        super(context, layoutId);
//        this.mContext = context;
//        this.mResourceId = layoutId;
        mQuestionList = new ArrayList<QuestionDetailModel>();
        mScreenW = EdusohoApp.app.screenW;
        listAddItem(questionResult.threads);
    }

    public void addItem(QuestionResult questionResult) {
        listAddItem(questionResult.threads);
        notifyDataSetChanged();
    }

    private void listAddItem(QuestionDetailModel[] questionDetailModels) {
        for (QuestionDetailModel item : questionDetailModels) {
            mQuestionList.add(item);
        }
    }

    public void clearAdapter() {
        mQuestionList.clear();
    }

    public List<QuestionDetailModel> getQuestionList() {
        return mQuestionList;
    }

    @Override
    public int getCount() {
        Log.d("QuestionListAdapter.getCount()-->", mQuestionList.size() + "");
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
        Log.d("QuestionListAdapter.getView()", String.valueOf(position));
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResource, null);
            holder = new ViewHolder();
            holder.tvQuestionTitle = (TextView) convertView.findViewById(R.id.tv_question_title);
            holder.tvLesson = (TextView) convertView.findViewById(R.id.tv_question_lesson);
            holder.tvTeacherReply = (TextView) convertView.findViewById(R.id.tv_teacher_reply);
            holder.tvReplyAmount = (TextView) convertView.findViewById(R.id.tv_reply_amount);
            holder.aQuery = new AQuery(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QuestionDetailModel question = mQuestionList.get(position);

        holder.tvQuestionTitle.setText(question.title);
        //holder.tvLesson.setText(question.questionLesson);
        if (question.isTeacherPost) {
            holder.tvTeacherReply.setVisibility(View.VISIBLE);
        } else {
            holder.tvTeacherReply.setVisibility(View.INVISIBLE);
        }

        if (TextUtils.isEmpty(question.coursePicture)) {
            holder.aQuery.id(R.id.iv_question).image(R.drawable.noram_course);
        } else {
            holder.aQuery.id(R.id.iv_question).image(question.coursePicture, false, true,
                    0, R.drawable.noram_course, null, AQuery.FADE_IN_NETWORK);
        }

        holder.aQuery.id(R.id.iv_question).height(AppUtil.getImageWidth(mScreenW), false);

        holder.tvReplyAmount.setText(String.valueOf(question.postNum));
        return convertView;
    }

    @Override
    public void addItems(ArrayList list) {

    }


    private static class ViewHolder {
        public AQuery aQuery;
        public TextView tvQuestionTitle;
        public TextView tvLesson;
        public TextView tvTeacherReply;
        public TextView tvReplyAmount;
    }
}
