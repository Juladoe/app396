package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;

/**
 * Created by howzhi on 14/11/22.
 */
public class RecyclerEmptyAdapter extends
        RecyclerViewListBaseAdapter<String, RecyclerEmptyAdapter.ViewHolder> {

    public RecyclerEmptyAdapter(Context context, int resource, String[] emptyTexts)
    {
        super(context, resource);
        for (String str: emptyTexts) {
            mList.add(str);
        }
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
        viewHolder.mEmptyText.setText(mList.get(i));
    }

    @Override
    public void addItems(ArrayList list) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mEmptyText;

        public ViewHolder(View view)
        {
            super(view);
            mEmptyText = (TextView) view.findViewById(R.id.list_empty_text);
        }
    }
}
