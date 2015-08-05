package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.EduBadgeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Iterator;
import java.util.List;

/**
 * Created by JesseHuang on 15/6/7.
 */
public class SwipeAdapter extends BaseAdapter {
    private Context mContext;
    private int mLayoutId;
    private List<New> mList;

    public SwipeAdapter(Context ctx, int id, List<New> list) {
        mContext = ctx;
        mLayoutId = id;
        mList = list;
    }

    public void update(List<New> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public boolean getContainItem(New compareModel) {
        for (New newModel : mList) {
            if (newModel.fromId == compareModel.fromId) {
                return true;
            }
        }
        return false;
    }

    public void addItem(New newModel) {
        mList.add(0, newModel);
        notifyDataSetChanged();
    }

    public void updateItem(New newModel) {
        Iterator<New> iterator = mList.iterator();
        int pos = 0;
        while (iterator.hasNext()) {
            New item = iterator.next();
            if (item.fromId == newModel.fromId) {
                mList.remove(item);
                mList.add(pos, newModel);
                notifyDataSetChanged();
                break;
            }
            pos++;
        }
    }

    public void setItemToTop(New newModel) {
        Iterator<New> iterator = mList.iterator();
        while (iterator.hasNext()) {
            New item = iterator.next();
            if (item.fromId == newModel.fromId) {
                mList.remove(item);
                mList.add(0, newModel);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public New getItem(int position) {
        return mList.get(position) != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        New item = mList.get(position);
        ImageLoader.getInstance().displayImage(item.imgUrl, viewHolder.ivAvatar, EdusohoApp.app.mOptions);
        if (item.unread == 0) {
            viewHolder.bvUnread.setVisibility(View.GONE);
        } else {
            viewHolder.bvUnread.setVisibility(View.VISIBLE);
            viewHolder.bvUnread.setText(String.valueOf(item.unread));
        }

        viewHolder.tvTitle.setText(item.title);
        viewHolder.tvContent.setText(item.content);
        viewHolder.tvPostTime.setText(AppUtil.convertMills2Date(item.createdTime * 1000L));
        return convertView;

    }

    public void removeItem(int position) {
        mList.remove(position);
    }

    static class ViewHolder {
        public ImageView ivAvatar;
        public EduBadgeView bvUnread;
        public TextView tvTitle;
        public TextView tvContent;
        public TextView tvPostTime;
        public View viewAvatar;

        public ViewHolder(View view) {
            ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
            viewAvatar = view.findViewById(R.id.view_avatar);
            bvUnread = (EduBadgeView) view.findViewById(R.id.bv_unread);
            bvUnread.setTargetView(viewAvatar);
            bvUnread.setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvContent = (TextView) view.findViewById(R.id.tv_content);
            tvPostTime = (TextView) view.findViewById(R.id.tv_post_time);
            view.setTag(this);
        }
    }
}


