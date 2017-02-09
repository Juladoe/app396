package com.edusoho.kuozhi.v3.adapter.discuss;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by DF on 2017/2/9.
 */

public class CourseDiscussAdapter extends RecyclerView.Adapter<CourseDiscussAdapter.MyViewHolder> {

    public List<DiscussDetail.ResourcesBean> mList;
    private Context mContext;

    public CourseDiscussAdapter(List<DiscussDetail.ResourcesBean> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discuss_topic,parent,false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DiscussDetail.ResourcesBean resourcesBean = mList.get(position);
        ImageLoader.getInstance().displayImage(resourcesBean.getUser().getAvatar(), holder.ivUser, EdusohoApp.app.mAvatarOptions);
        holder.tvUserName.setText(resourcesBean.getUser().getNickname());
        holder.tvContent.setText(String.format("         %s", resourcesBean.getTitle()));
        holder.tvCommentNum.setText(resourcesBean.getPostNum());
        holder.tvTime.setText(CommonUtil.conver2Date(CommonUtil.convertMilliSec(mContext instanceof CourseActivity ? resourcesBean.getLatestPostTime() : resourcesBean.getUpdatedTime()) + 28800000).substring(2, 16));
        if ("question".equals(resourcesBean.getType())) {
            holder.tvKind.setText("问题");
            holder.tvKind.setTextColor(mContext.getResources().getColor(R.color.primary_color));
            holder.tvKind.setBackgroundResource(R.drawable.discuss_question);
        } else {
            holder.tvKind.setText("话题");
            holder.tvKind.setTextColor(mContext.getResources().getColor(R.color.secondary2_color));
            holder.tvKind.setBackgroundResource(R.drawable.discuss_topic);
        }
    }

    @Override
    public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivUser;
        public TextView tvUserName;
        public TextView tvKind;
        public TextView tvContent;
        public TextView tvCommentNum;
        public TextView tvTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivUser = (ImageView) itemView.findViewById(R.id.iv_user_icon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvKind = (TextView) itemView.findViewById(R.id.tv_kind);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvCommentNum = (TextView) itemView.findViewById(R.id.tv_comment_num);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
