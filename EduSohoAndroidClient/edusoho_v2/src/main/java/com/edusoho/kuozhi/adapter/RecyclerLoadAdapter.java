package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 14/11/22.
 */
public class RecyclerLoadAdapter extends
        RecyclerViewListBaseAdapter<String, RecyclerLoadAdapter.ViewHolder> {

    public RecyclerLoadAdapter(Context context, int resource)
    {
        super(context, resource);
        mList.add("loading");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(mResource, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void addItems(List list) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View view)
        {
            super(view);
        }
    }
}
