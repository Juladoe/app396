package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class ChatAdapter extends BaseAdapter {

    private Context mContext;
    private List<Chat> mList;
    private User mLoginUser;
    private static long TIME_INTERVAL = 1000 * 60 * 5;

    private static final int TYPE_ITEMS = 2, TYPE_ME = 0, TYPE_OTHER_SIDE = 1;

    public ChatAdapter(Context ctx, List<Chat> list) {
        mContext = ctx;
        mList = list;
        mLoginUser = EdusohoApp.app.loginUser;
    }

    public void updateList(List<Chat> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addOneChat(Chat chat) {
        mList.add(chat);
        notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_ITEMS;
    }

    @Override
    public int getItemViewType(int position) {
        Chat msg = mList.get(position);
        if (msg.fromId == mLoginUser.id) {
            return TYPE_ME;
        } else {
            return TYPE_OTHER_SIDE;
        }
    }

    @Override
    public int getCount() {
        return mList.size();
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
        ViewHolder holder;
        if (convertView == null) {
            int type = getItemViewType(position);
            switch (type) {
                case TYPE_ME:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.me_message, null);
                    break;
                case TYPE_OTHER_SIDE:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.opposite_message, null);
                    break;
            }
            holder = new ViewHolder();
            holder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_send_time);
            holder.tvSendContent = (TextView) convertView.findViewById(R.id.tv_send_content);
            holder.ciPic = (CircleImageView) convertView.findViewById(R.id.ci_send_pic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Chat model = mList.get(position);
        holder.tvSendTime.setVisibility(View.GONE);
        if (position > 0) {
            if (model.createdTime - mList.get(position - 1).createdTime > TIME_INTERVAL) {
                holder.tvSendTime.setVisibility(View.VISIBLE);
                holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
            }
        } else {
            holder.tvSendTime.setVisibility(View.VISIBLE);
            holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
        }
        holder.tvSendContent.setText(model.content);
        ImageLoader.getInstance().displayImage(model.headimgurl, holder.ciPic, EdusohoApp.app.mOptions);
        return convertView;
    }

    private static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvSendContent;
        public CircleImageView ciPic;
    }
}
