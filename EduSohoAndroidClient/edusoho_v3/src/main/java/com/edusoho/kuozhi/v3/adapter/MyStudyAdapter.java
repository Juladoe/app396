package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.fragment.MyTabFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remilia on 2017/1/5.
 */
public class MyStudyAdapter extends BaseAdapter {

    private Context mContext;
    private int type = 0;
    private List<Object> mLists = new ArrayList<>();

    public MyStudyAdapter(Context context, int type) {
        this.mContext = context;
        this.type = type;
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
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_study,null,false);
            viewHolder = new ViewHolder();
            viewHolder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
            viewHolder.layoutLive = convertView.findViewById(R.id.layout_live);
            viewHolder.tvLiveIcon = (TextView) convertView.findViewById(R.id.tv_live_icon);
            viewHolder.tvLive = (TextView) convertView.findViewById(R.id.tv_live);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvStudyState = (TextView) convertView.findViewById(R.id.tv_study_state);
            viewHolder.tvMore = (TextView) convertView.findViewById(R.id.tv_more);
            viewHolder.layoutClass = convertView.findViewById(R.id.layout_class);
            viewHolder.tvClassName = (TextView) convertView.findViewById(R.id.tv_class_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private static ViewHolder viewHolder;

    private class ViewHolder{
        ImageView ivPic;
        View layoutLive;
        TextView tvLiveIcon;
        TextView tvLive;
        TextView tvTitle;
        TextView tvStudyState;
        TextView tvMore;
        View layoutClass;
        TextView tvClassName;
    }

    private void initData(){

    }

    public void setType(int type){
        this.type = type;
        initData();
    }
}
