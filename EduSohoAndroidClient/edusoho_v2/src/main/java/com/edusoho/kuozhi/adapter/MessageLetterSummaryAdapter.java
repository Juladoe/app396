package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Message.LetterSummaryModel;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by JesseHuang on 14/11/23.
 */
public class MessageLetterSummaryAdapter extends ListBaseAdapter<LetterSummaryModel> {
    private static final String TAG = "MessageLetterSummaryAdapter";
    private DisplayImageOptions mOptions;

    public MessageLetterSummaryAdapter(Context context, int resource) {
        super(context, resource);
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    public MessageLetterSummaryAdapter(Context context, int resource, boolean isCache) {
        super(context, resource, isCache);
    }

    @Override
    public void addItems(ArrayList<LetterSummaryModel> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public long getItemId(int i) {
        return super.getItemId(i);
    }

    @Override
    public Object getItem(int i) {
        return super.getItem(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(mResource, null);
                holder = new ViewHolder();
                holder.ivPic = (CircularImageView) convertView.findViewById(R.id.ci_send_pic);
                holder.tvSendName = (TextView) convertView.findViewById(R.id.tv_send_name);
                holder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.tvSendContent = (TextView) convertView.findViewById(R.id.tv_send_content);
                holder.tvUnreadNum = (TextView) convertView.findViewById(R.id.tv_msg_sum);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LetterSummaryModel model = mList.get(position);
            ImageLoader.getInstance().displayImage(model.user.mediumAvatar, holder.ivPic, mOptions);
            holder.tvSendName.setText(model.user.nickname);
            holder.tvSendTime.setText(AppUtil.getPostDays(model.latestMessageTime));
            Log.d(TAG, holder.tvSendContent.getWidth() + "");
            holder.tvSendContent.setText(model.latestMessageContent);
            if (model.unreadNum > 0) {
                holder.tvUnreadNum.setVisibility(View.VISIBLE);
                holder.tvUnreadNum.setText(model.unreadNum + "");
            } else {
                holder.tvUnreadNum.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return convertView;
    }

    private static class ViewHolder {
        public CircularImageView ivPic;
        public TextView tvSendName;
        public TextView tvSendContent;
        public TextView tvSendTime;
        public TextView tvUnreadNum;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void addItem(LetterSummaryModel item) {
        super.addItem(item);
    }

    public void setReadMsgNum(int position) {
        LetterSummaryModel model = mList.get(position);
        if (model.unreadNum != 0) {
            model.unreadNum = 0;
            notifyDataSetChanged();
        }
    }
}
