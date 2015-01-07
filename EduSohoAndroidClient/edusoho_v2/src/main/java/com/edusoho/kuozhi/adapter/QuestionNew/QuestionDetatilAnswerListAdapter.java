package com.edusoho.kuozhi.adapter.QuestionNew;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.model.Question.ReplyModel;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionDetatilAnswerListAdapter extends ListBaseAdapter<ReplyModel>{
    private ArrayList<ReplyModel> mReplyModelListData;
    public QuestionDetatilAnswerListAdapter(Context context, int resource) {
        super(context, resource);
        mReplyModelListData = new ArrayList<ReplyModel>();
    }

    public void sortReplyModelData(){
        mReplyModelListData.clear();
        ArrayList<ReplyModel> teachReplyModelData = new ArrayList<ReplyModel>();
        ArrayList<ReplyModel> normalReplyModelData = new ArrayList<ReplyModel>();

        for(ReplyModel replyModel : mList){
            if(replyModel.isElite == 1){
                teachReplyModelData.add(replyModel);
            }else{
                normalReplyModelData.add(replyModel);
            }
        }
        mReplyModelListData.addAll(teachReplyModelData);
        mReplyModelListData.addAll(normalReplyModelData);
    }

    @Override
    public void addItems(ArrayList list) {
        mList.addAll(list);
        sortReplyModelData();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mReplyModelListData != null && mReplyModelListData.size() > 0){
            return mReplyModelListData.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return mReplyModelListData.get(i);
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
            holder.tvQuestionDetailAnswerUserRole = (TextView) (view.findViewById(R.id.question_user_head_framelayout).findViewById(R.id.question_detail_answer_user_role));
            holder.questionDetailAnswerUserHeadImage = (CircularImageView) (view.findViewById(R.id.question_user_head_framelayout).findViewById(R.id.question_detail_answer_user_head_image));
            holder.aQuery = new AQuery(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tvQuestionDetailAnswerUserRole.setVisibility(View.GONE);
        ReplyModel replyModel = mReplyModelListData.get(i);

        if(1 == replyModel.isElite){
            holder.tvQuestionDetailAnswerUserRole.setVisibility(View.VISIBLE);
        }
        ImageLoader.getInstance().displayImage(replyModel.user.mediumAvatar,holder.questionDetailAnswerUserHeadImage);
        holder.tvQuestionDetatilListUserName.setText(replyModel.user.nickname);
        holder.tvQuestionDetatilListTime.setText(AppUtil.getPostDays(replyModel.createdTime));
        holder.tvQuestionDetatilListAnswer.setText(Html.fromHtml(fitlerImgTag(replyModel.content)));
        return view;
    }

    private String fitlerImgTag(String content) {
        return content.replaceAll("(<img src=\".*?\" .>)", "");
    }

    public class ViewHolder {
        TextView tvQuestionDetatilListUserName;
        TextView tvQuestionDetatilListTime;
        TextView tvQuestionDetatilListAnswer;
        TextView tvQuestionDetailAnswerUserRole;
        CircularImageView questionDetailAnswerUserHeadImage;
        AQuery aQuery;
    }
}
