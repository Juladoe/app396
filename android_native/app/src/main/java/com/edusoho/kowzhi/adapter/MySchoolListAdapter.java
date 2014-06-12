package com.edusoho.kowzhi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kowzhi.R;
import com.edusoho.kowzhi.entity.RecommendSchoolItem;

public class MySchoolListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private boolean isDelMode = false;
    private ArrayList<RecommendSchoolItem> mList;

    public MySchoolListAdapter(
            Context context, ArrayList<RecommendSchoolItem> list, int resource)
    {
        mList = list;
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int index) {
        return mList.get(index);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.my_rch_close_btn = view.findViewById(R.id.rch_close_btn);
            holder.my_sch_logo = (ImageView) view.findViewById(R.id.my_sch_logo);
            holder.my_sch_name = (TextView) view.findViewById(R.id.my_sch_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        RecommendSchoolItem rcItem = mList.get(index);
        if (isDelMode
                && rcItem.type == RecommendSchoolItem.SCHOOLITEM) {
            holder.my_rch_close_btn.setVisibility(View.VISIBLE);
            holder.my_rch_close_btn.setOnClickListener(new CloseBtnClickListener(rcItem));
        } else {
            holder.my_rch_close_btn.setVisibility(View.GONE);
        }

        if (rcItem.type == RecommendSchoolItem.ADDITEM) {
            holder.my_sch_logo.setImageResource(R.drawable.recommend_school_add_icon);
        } else {
            holder.my_sch_logo.setImageResource(R.drawable.logo1);
        }
        holder.my_sch_name.setText(rcItem.title);
        return view;
    }

    public void setDelMode(boolean mode)
    {
        isDelMode = mode;
        notifyDataSetInvalidated();
    }

    private class CloseBtnClickListener implements OnClickListener
    {
        private RecommendSchoolItem mRcItem;

        public CloseBtnClickListener(RecommendSchoolItem rcItem)
        {
            mRcItem = rcItem;
        }

        @Override
        public void onClick(View v) {
            SharedPreferences sp = mContext.getSharedPreferences("recommend_school", mContext.MODE_PRIVATE);
            Editor edit = sp.edit();
            edit.remove(mRcItem.title);
            edit.commit();
            Toast.makeText(mContext, "删除网校成功", Toast.LENGTH_LONG).show();
            mList.remove(mRcItem);
            notifyDataSetChanged();
        }

    }

    private class ViewHolder
    {
        /** recommend_school_name */
        public View my_rch_close_btn;
        public TextView my_sch_name;
        public TextView my_sch_info;
        public ImageView my_sch_logo;
    }

}
