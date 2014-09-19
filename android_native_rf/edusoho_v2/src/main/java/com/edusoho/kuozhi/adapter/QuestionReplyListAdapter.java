package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Question.EntireReply;
import com.edusoho.kuozhi.model.Question.ReplyModel;
import com.edusoho.kuozhi.model.Question.ReplyResult;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hby on 14-9-18.
 * 回复List适配器
 */
public class QuestionReplyListAdapter extends EdusohoBaseAdapter {
    private Context mContext;
    private List<EntireReply> mEntireReplyList;
    private int mRecourseId;
    private User mUser;

    private List<EntireReply> mTeacherReplyList;
    private List<EntireReply> mNormalReplyList;

    public QuestionReplyListAdapter(Context context, ReplyResult replyResult, int layoutId, User user) {
        mEntireReplyList = new ArrayList<EntireReply>();
        this.mContext = context;
        this.mRecourseId = layoutId;
        this.mUser = user;
        listAddItem(replyResult.data);
    }

    public void addItem(ReplyResult replyResult) {
        listAddItem(replyResult.data);
        notifyDataSetChanged();
    }

    private void listAddItem(ReplyModel[] replyModels) {
        mNormalReplyList = new ArrayList<EntireReply>();
        mTeacherReplyList = new ArrayList<EntireReply>();
        boolean isNormalFirst = true;
        boolean isTeacherFirst = true;
        //如果刷新或者显示更多，考虑原来mEntireReplyList中的数据，mEntireReplyList+replyModels
        for (ReplyModel replyModel : replyModels) {
            EntireReply entireReply;
            if (replyModel.isElite == 0) {
                if (isNormalFirst) {
                    entireReply = new EntireReply(isNormalFirst, replyModel);
                    isNormalFirst = false;
                } else {
                    entireReply = new EntireReply(isNormalFirst, replyModel);
                }
                mNormalReplyList.add(entireReply);
            } else {
                if (isTeacherFirst) {
                    entireReply = new EntireReply(isTeacherFirst, replyModel);
                    isTeacherFirst = false;
                } else {
                    entireReply = new EntireReply(isTeacherFirst, replyModel);
                }
                mTeacherReplyList.add(entireReply);
            }
        }
        mEntireReplyList.addAll(mTeacherReplyList);
        mEntireReplyList.addAll(mNormalReplyList);
    }

    public void clearAdapter() {
        mEntireReplyList.clear();
    }

    @Override
    public int getCount() {
        Log.d("getCount()", String.valueOf(mEntireReplyList.size()));
        return mEntireReplyList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mEntireReplyList != null && mEntireReplyList.size() > 0) {
            return mEntireReplyList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("getView()", String.valueOf(position));
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(mRecourseId, null);
        }

        EntireReply entireReply = mEntireReplyList.get(position);

        TextView tvReplyType = AppUtil.getViewHolder(convertView, R.id.tv_reply_type);
        TextView tvReplyName = AppUtil.getViewHolder(convertView, R.id.tv_reply_name);
        TextView tvReplyTime = AppUtil.getViewHolder(convertView, R.id.tv_reply_time);
        TextView tvReplyContent = AppUtil.getViewHolder(convertView, R.id.tv_reply_content);
        ImageView ivEdit = AppUtil.getViewHolder(convertView, R.id.iv_reply_edit);
        RelativeLayout rlReplay = AppUtil.getViewHolder(convertView, R.id.rl_reply_info);
        tvReplyName.setText(entireReply.replyModel.user.nickname);
        if (tvReplyName.getText().equals(mUser.nickname)) {
            ivEdit.setVisibility(View.VISIBLE);
        } else {
            ivEdit.setVisibility(View.INVISIBLE);
        }

        tvReplyContent.setText(Html.fromHtml(entireReply.replyModel.content));
        tvReplyTime.setText(AppUtil.getPostDays(entireReply.replyModel.createdTime));

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlReplay.getLayoutParams();

        if (entireReply.replyModel.isElite == 1) {
            if (entireReply.isFirstReply) {
                tvReplyType.setVisibility(View.VISIBLE);
                tvReplyType.setText("教师的答案（" + String.valueOf(this.mTeacherReplyList.size()) + "条）：");
                createDrawables(tvReplyType, R.drawable.recommend_week_label_icon);
                lp.topMargin = 16;
            } else {
                tvReplyType.setVisibility(View.GONE);
                lp.topMargin = 0;
            }
            tvReplyName.setTextColor(mContext.getResources().getColor(R.color.teacher_reply));
        } else {
            if (entireReply.isFirstReply) {
                tvReplyType.setVisibility(View.VISIBLE);
                tvReplyType.setText("所有的回复（" + String.valueOf(this.mNormalReplyList.size()) + "条）：");
                createDrawables(tvReplyType, R.drawable.normal_reply_tag);
            } else {
                tvReplyType.setVisibility(View.GONE);
                lp.topMargin = 16;
            }
            tvReplyName.setTextColor(mContext.getResources().getColor(R.color.question_lesson));
        }

        if (tvReplyName.getText().equals(mUser.nickname)) {
            ivEdit.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private void createDrawables(TextView tv, int drawableId) {
        Drawable drawable = mContext.getResources().getDrawable(drawableId);
        tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }
}
