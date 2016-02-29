package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.view.EduSohoDivederLine;

/**
 * Created by melomelon on 16/2/26.
 */
public class MyPostedThreadFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private EduSohoDivederLine mDividerLine;

    public MyPostedThreadFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.my_posted_thread_fragment_layout);
    }

    @Override
    protected void initView(View view) {
        mDividerLine = new EduSohoDivederLine(EduSohoDivederLine.VERTICAL);
        mDividerLine.setColor(getResources().getColor(R.color.material_grey));
        mDividerLine.setSize(1);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_posted_Thread_recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MyAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(mDividerLine);

    }




    public class MyAdapter extends RecyclerView.Adapter{

        private LayoutInflater mLayoutInflater;

        public MyAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ThreadItemViewHolder(mLayoutInflater.inflate(R.layout.my_thread_item_layout,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ThreadItemViewHolder){
                ((ThreadItemViewHolder) holder).ivAvatar.setVisibility(View.VISIBLE);
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
}
