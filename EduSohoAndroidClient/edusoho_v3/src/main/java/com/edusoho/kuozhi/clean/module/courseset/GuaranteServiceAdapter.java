package com.edusoho.kuozhi.clean.module.courseset;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DF on 2017/3/24.
 */

public class GuaranteServiceAdapter extends RecyclerView.Adapter<GuaranteServiceAdapter.GuaranteServiceViewHolder> {

    private List mList;

    public GuaranteServiceAdapter() {
        this.mList = new ArrayList();
    }

    public void setData(List list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public GuaranteServiceAdapter.GuaranteServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GuaranteServiceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guarante_service, parent, false));
    }

    @Override
    public void onBindViewHolder(GuaranteServiceAdapter.GuaranteServiceViewHolder holder, int position) {
//        holder.mTvType.setText();
//        holder.mTvTitle.setText();
//        holder.mTvContent.setText();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class GuaranteServiceViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTvType;
        private final TextView mTvTitle;
        private final TextView mTvContent;

        public GuaranteServiceViewHolder(View itemView) {
            super(itemView);
            mTvType = (TextView) itemView.findViewById(R.id.tv_type);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
