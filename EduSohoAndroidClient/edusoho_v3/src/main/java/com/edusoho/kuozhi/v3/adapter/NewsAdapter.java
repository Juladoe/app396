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
import com.edusoho.kuozhi.v3.model.bal.news.NewsItem;
import com.edusoho.kuozhi.v3.view.EduBadgeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class NewsAdapter extends BaseAdapter {
    private Context mContext;
    private int mLayoutId;
    private List<NewsItem> mList;

    public NewsAdapter(Context ctx, int id, List<NewsItem> list) {
        mContext = ctx;
        mLayoutId = id;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public NewsItem getItem(int position) {
        return mList.get(position) != null ? mList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
            viewHolder = new ViewHolder();
            viewHolder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            viewHolder.bvUnread = (EduBadgeView) convertView.findViewById(R.id.bv_unread);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.tvPostTime = (TextView) convertView.findViewById(R.id.tv_post_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        NewsItem item = mList.get(position);
        ImageLoader.getInstance().displayImage(item.srcUrl, viewHolder.ivAvatar, EdusohoApp.app.mOptions);
        if (item.unread == 0) {
            viewHolder.bvUnread.setVisibility(View.GONE);
        } else {
            viewHolder.bvUnread.setVisibility(View.VISIBLE);
            viewHolder.bvUnread.setText(String.valueOf(item.unread));
        }

        viewHolder.tvTitle.setText(item.title);
        viewHolder.tvContent.setText(item.content);
        viewHolder.tvPostTime.setText(item.postTime);
        return convertView;
    }

    public static class ViewHolder {
        public ImageView ivAvatar;
        public EduBadgeView bvUnread;
        public TextView tvTitle;
        public TextView tvContent;
        public TextView tvPostTime;
    }

    public void findNews(NewsItem newItem) {
        if (mList.size() > 0) {
            for (NewsItem item : mList) {
                if (item.title.equals(newItem.title)) {
                    item.unread = item.unread + 1;
                    item.title = newItem.title;
                    item.content = newItem.content;
                    item.postTime = newItem.postTime;
                }
            }
        }
        this.notifyDataSetChanged();
    }
}
