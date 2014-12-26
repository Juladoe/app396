package com.edusoho.kuozhi.ui.QuestionNew;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;

import java.util.ArrayList;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionDetatilAnswerListAdapter extends ListBaseAdapter<QuestionDetatilAnswerListData>{
    public QuestionDetatilAnswerListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void addItems(ArrayList list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.from(mContext).inflate(mResource,null);
            holder = new ViewHolder();
            holder.tvQuestionDetatilListUserName = (TextView) view.findViewById(R.id.question_detail_answer_user_name);
            holder.tvQuestionDetatilListTime = (TextView) view.findViewById(R.id.question_detail_answer_time);
            holder.tvQuestionDetatilListAnswer = (TextView) view.findViewById(R.id.question_detatil_answer_content);
            holder.aQuery = new AQuery(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        QuestionDetatilAnswerListData questionDetatilListData = mList.get(i);

        holder.aQuery.id(R.id.question_detail_answer_user_head_image).image(
                "http://trymob.edusoho.cn/files/course/2014/12-17/1644171d1f28828451.png", false, true, 200,R.drawable.question_answer_icon);
        holder.tvQuestionDetatilListUserName.setText(questionDetatilListData.questionDetatilListUserName);
        holder.tvQuestionDetatilListTime.setText(questionDetatilListData.questionDetatilListTime);
        holder.tvQuestionDetatilListAnswer.setText(questionDetatilListData.questionDetatilListAnswer);
        return view;
    }

    public class ViewHolder {
        TextView tvQuestionDetatilListUserName;
        TextView tvQuestionDetatilListTime;
        TextView tvQuestionDetatilListAnswer;
        AQuery aQuery;
    }
}
