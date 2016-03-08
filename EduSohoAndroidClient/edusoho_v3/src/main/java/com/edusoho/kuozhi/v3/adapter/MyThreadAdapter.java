package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.thread.MyThreadEntity;
import com.edusoho.kuozhi.v3.ui.ThreadDiscussActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by melomelon on 16/2/29.
 */
public class MyThreadAdapter extends RecyclerView.Adapter {

    private LayoutInflater mLayoutInflater;
    private List<MyThreadEntity> mDataList;
    private EdusohoApp mApp;
    private Context mContext;

    public MyThreadAdapter(Context context, EdusohoApp app) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        mDataList = new ArrayList();
        mApp = app;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ThreadItemViewHolder(mLayoutInflater.inflate(R.layout.my_thread_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ThreadItemViewHolder) {
            final MyThreadEntity threadEntity = mDataList.get(position);
            if ("question".equals(threadEntity.getType())) {
                ((ThreadItemViewHolder) holder).tvThreadType.setText("问");
                ((ThreadItemViewHolder) holder).tvThreadType.setBackgroundResource(R.drawable.round_green_bg);
            } else {
                ((ThreadItemViewHolder) holder).tvThreadType.setText("话");
                ((ThreadItemViewHolder) holder).tvThreadType.setBackgroundResource(R.drawable.round_blue_bg);
            }
            if ("".equals(threadEntity.getSmallPicture())) {
                ((ThreadItemViewHolder) holder).ivAvatar.setImageResource(R.drawable.defaultpic);
            } else {
                ImageLoader.getInstance().displayImage(threadEntity.getSmallPicture(), ((ThreadItemViewHolder) holder).ivAvatar, mApp.mOptions);
            }
            ((ThreadItemViewHolder) holder).tvThreadCourseTitle.setText(String.format("来自课程：『%s』", threadEntity.getCourseTitle()));
            ((ThreadItemViewHolder) holder).tvThreadTitle.setText(threadEntity.getTitle());
            ((ThreadItemViewHolder) holder).tvTime.setText(AppUtil.convertMills2Date(Long.parseLong(threadEntity.getCreatedTime()) * 1000));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mApp.mEngine.runNormalPlugin("ThreadDiscussActivity", mContext, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(ThreadDiscussActivity.COURSE_ID, Integer.parseInt(threadEntity.getCourseId()));
                            startIntent.putExtra(ThreadDiscussActivity.THREAD_ID, Integer.parseInt(threadEntity.getThreadId()));
                            startIntent.putExtra(ThreadDiscussActivity.ACTIVITY_TYPE, PushUtil.ThreadMsgType.THREAD_POST);
                        }
                    });
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ThreadItemViewHolder extends RecyclerView.ViewHolder {

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

    public void addDataList(List list) {
        mDataList.addAll(list);
        notifyDataSetChanged();
    }


}
