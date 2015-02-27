package com.edusoho.kuozhi.adapter.Message;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.model.Message.LetterModel;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.edusoho.listener.IconClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by JesseHuang on 14/11/24.
 */
public class LetterListAdapter extends ListBaseAdapter<LetterModel> {
    private static final String TAG = "LetterListAdapter";
    private DisplayImageOptions mOptions;
    private static final int TYPE_ITEMS = 2, TYPE_ME = 0, TYPE_OTHER_SIDE = 1;
    private User mLoginUser;
    private static long TIME_INTERVAL = 1000 * 60 * 5;
    private ActionBarBaseActivity mActivity;


    public LetterListAdapter(Context context, ActionBarBaseActivity activity, int resource) {
        super(context, resource);
        mActivity = activity;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    public LetterListAdapter(Context context, ActionBarBaseActivity activity, User user) {
        this(context, activity, 0);
        mLoginUser = user;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
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
    public int getItemViewType(int position) {
        LetterModel model = mList.get(position);
        //自己发送的
        if (model.fromId == mLoginUser.id) {
            return TYPE_ME;
        } else {
            return TYPE_OTHER_SIDE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_ITEMS;
    }

    @Override
    public Object getItem(int i) {
        return super.getItem(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        try {
            if (convertView == null) {
                int type = getItemViewType(position);
                switch (type) {
                    case TYPE_ME:
                        convertView = inflater.from(mContext).inflate(R.layout.letter_my_item, null);
                        break;
                    case TYPE_OTHER_SIDE:
                        convertView = inflater.from(mContext).inflate(R.layout.letter_otherside_item, null);
                        break;
                }
                holder = new ViewHolder();
                holder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_send_time);
                holder.tvSendContent = (TextView) convertView.findViewById(R.id.tv_send_content);
                holder.ciPic = (CircularImageView) convertView.findViewById(R.id.ci_send_pic);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            LetterModel model = mList.get(position);

            //如果私信与上一条私信时间超过TIME_INTERVAL的得值，则显示时间控件
            holder.tvSendTime.setVisibility(View.GONE);
            if (position != 0) {
                if (AppUtil.convertMilliSec(model.createdTime) - AppUtil.convertMilliSec(mList.get(position - 1).createdTime) > TIME_INTERVAL) {
                    holder.tvSendTime.setVisibility(View.VISIBLE);
                    holder.tvSendTime.setText(AppUtil.convertWeekTime(model.createdTime));
                }
            } else {
                holder.tvSendTime.setVisibility(View.VISIBLE);
                holder.tvSendTime.setText(AppUtil.convertWeekTime(model.createdTime));
            }
            holder.tvSendContent.setText(model.content);
            holder.ciPic.setOnClickListener(new IconClickListener(mActivity, model.createdUser));
            ImageLoader.getInstance().displayImage(model.createdUser.mediumAvatar, holder.ciPic, mOptions);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return convertView;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void addItems(ArrayList<LetterModel> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItemsToBottom(ArrayList<LetterModel> list) {
        mList.addAll(0, list);
        notifyDataSetChanged();
    }

    @Override
    public void addItem(LetterModel item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvSendContent;
        public CircularImageView ciPic;
    }
}
