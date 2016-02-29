package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by melomelon on 16/2/29.
 */
public class MyThreadAdapter extends RecyclerView.Adapter {

    private LayoutInflater mLayoutInflater;

    public MyThreadAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ThreadItemViewHolder(mLayoutInflater.inflate(R.layout.my_thread_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ThreadItemViewHolder){
            //// TODO: 16/2/29
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    private class ThreadItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivAvatar;
        private TextView tvThreadType;
        private TextView tvThreadTitle;
        private TextView tvTime;
        private TextView tvThreadCourseTitle;


        public ThreadItemViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.my_thread_item_avatar);
            tvThreadTitle = (TextView) itemView.findViewById(R.id.my_thread_item_title);
            tvTime = (TextView) itemView.findViewById(R.id.my_thread_item_time);
            tvThreadType = (TextView) itemView.findViewById(R.id.my_thread_item_type);
            tvThreadCourseTitle = (TextView) itemView.findViewById(R.id.my_thread_item_course);
        }

    }

}
