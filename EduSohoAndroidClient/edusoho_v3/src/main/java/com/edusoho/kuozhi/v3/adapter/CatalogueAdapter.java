package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by DF on 2017/1/4.
 */

public class CatalogueAdapter extends BaseAdapter {

    public List<DiscussDetail.ResourcesBean> mList;
    private Context mContext;

    public CatalogueAdapter(List<DiscussDetail.ResourcesBean> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_discuss_topic, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DiscussDetail.ResourcesBean resourcesBean =  mList.get(position);
        ImageLoader.getInstance().displayImage(resourcesBean.getUser().getAvatar(), viewHolder.ivUser, EdusohoApp.app.mAvatarOptions);
        viewHolder.tvUserName.setText(resourcesBean.getUser().getNickname());
        viewHolder.tvContent.setText(String.format("         %s", resourcesBean.getTitle()));
        viewHolder.tvCommentNum.setText(resourcesBean.getPostNum());
        viewHolder.tvTime.setText(resourcesBean.getLatestPostTime().replace("T", " ").split("[+]")[0].substring(2, 16).replace("-", "/"));
        if ("question".equals(resourcesBean.getType())) {
            viewHolder.tvKind.setText("问题");
            viewHolder.tvKind.setTextColor(mContext.getResources().getColor(R.color.primary_color));
            viewHolder.tvKind.setBackgroundResource(R.drawable.discuss_question);
        } else {
            viewHolder.tvKind.setText("话题");
            viewHolder.tvKind.setTextColor(mContext.getResources().getColor(R.color.secondary2_color));
            viewHolder.tvKind.setBackgroundResource(R.drawable.discuss_topic);
        }
        return convertView;
    }

    public static class ViewHolder{
        public ImageView ivUser;
        public TextView tvUserName;
        public TextView tvKind;
        public TextView tvContent;
        public TextView tvCommentNum;
        public TextView tvTime;

        public ViewHolder(View view) {
            ivUser = (ImageView) view.findViewById(R.id.iv_user_icon);
            tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
            tvKind = (TextView) view.findViewById(R.id.tv_kind);
            tvContent = (TextView) view.findViewById(R.id.tv_content);
            tvCommentNum = (TextView) view.findViewById(R.id.tv_comment_num);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
        }
    }
}
