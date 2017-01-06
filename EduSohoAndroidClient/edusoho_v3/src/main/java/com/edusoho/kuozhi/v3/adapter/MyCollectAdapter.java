package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remilia on 2017/1/5.
 */
public class MyCollectAdapter extends BaseAdapter {

    private Context mContext;
    private List<Object> mLists = new ArrayList<>();

    public MyCollectAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_study, null, false);
            viewHolder = new ViewHolder();
            viewHolder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
            viewHolder.tvAddNum = (TextView) convertView.findViewById(R.id.tv_add_num);
            viewHolder.tvMore = (TextView) convertView.findViewById(R.id.tv_more);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private static ViewHolder viewHolder;

    private class ViewHolder {
        ImageView ivPic;
        TextView tvAddNum;
        TextView tvMore;
    }

    private void initData() {

    }
}
