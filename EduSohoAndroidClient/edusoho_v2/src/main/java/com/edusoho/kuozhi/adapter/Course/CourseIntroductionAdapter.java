package com.edusoho.kuozhi.adapter.Course;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseIntroductionAdapter
        extends RecyclerViewListBaseAdapter<String[], CourseIntroductionAdapter.ViewHolder> {

    public CourseIntroductionAdapter(Context context, int resource)
    {
        super(context, resource);
    }

    @Override
    public void addItem(String[] item) {
        mList.add(item);
        notifyItemInserted(mList.size() - 1);
    }

    @Override
    public void addItems(List<String[]> list) {
        mList.addAll(list);
        notifyItemRangeInserted(mList.size() - 1 - list.size(), mList.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(mResource, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        String[] contents = mList.get(i);
        viewHolder.mTitle.setText(contents[0]);
        viewHolder.mContent.setText(contents[1]);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mTitle;
        public TextView mContent;
        public ViewHolder(View view){
            super(view);

            mTitle = (TextView) view.findViewById(R.id.course_introduction_title);
            mContent = (TextView) view.findViewById(R.id.course_introduction_content);
        }
    }
}
