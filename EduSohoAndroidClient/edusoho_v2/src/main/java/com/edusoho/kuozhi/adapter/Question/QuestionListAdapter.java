package com.edusoho.kuozhi.adapter.Question;

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
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hby on 14-9-15.
 */
public class QuestionListAdapter<T> extends ListBaseAdapter<T> {
    //    private Context mContext;
    //    private int mResourceId;
    private int mScreenW;

    public QuestionListAdapter(Context context, int layoutId) {
        super(context, layoutId);
//        this.mContext = context;
//        this.mResourceId = layoutId;
        mScreenW = EdusohoApp.app.screenW;
    }

    @Override
    public void addItems(ArrayList<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearAdapter() {
        mList.clear();
    }

    public List<T> getQuestionList() {
        return mList;
    }

    @Override
    public int getCount() {
        Log.d("QuestionListAdapter.getCount()-->", mList.size() + "");
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mList != null && mList.size() > 0) {
            return mList.get(position);
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

        QuestionDetailModel question = (QuestionDetailModel) mList.get(position);

        holder.tvQuestionTitle.setText(question.title);
        if (question.number != 0) {
            holder.tvLesson.setText("课时" + question.number);
        } else {
            holder.tvLesson.setVisibility(View.INVISIBLE);
        }

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

    private static class ViewHolder {
        public AQuery aQuery;
        public TextView tvQuestionTitle;
        public TextView tvLesson;
        public TextView tvTeacherReply;
        public TextView tvReplyAmount;
    }
}
