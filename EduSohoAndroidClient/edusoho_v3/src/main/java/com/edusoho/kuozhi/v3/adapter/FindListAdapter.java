package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.edusoho.kuozhi.v3.entity.discovery.DiscoveryColumn;
import com.edusoho.kuozhi.v3.view.FindCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by su on 2016/2/24.
 */
public class FindListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DiscoveryColumn> mList;

    public FindListAdapter(Context context, List<DiscoveryColumn> list) {
        mContext = context;
        mList = list;
    }

    public FindListAdapter(Context mContext) {
        this.mContext = mContext;
        mList = new ArrayList<>();
    }

    public void addData(DiscoveryColumn findCardEntity) {
        Log.d("FindListAdapter", "------addData------");
        Log.d("FindListAdapter", "card.title" + findCardEntity.title);
        Log.d("FindListAdapter", "addData: " + findCardEntity.data.size());
        Log.d("FindListAdapter", "-------------------");
        mList.add(findCardEntity);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new FindCardView(mContext);
            ((FindCardView) convertView).setAdapter(new FindCardItemAdapter(mContext));
        }

        DiscoveryColumn discoveryColumn = mList.get(position);
        FindCardView findCardView = (FindCardView) convertView;
        Log.d("FindListAdapter", "------getView------");
        Log.d("FindListAdapter", "position " + position);
        Log.d("FindListAdapter", "data.size " + discoveryColumn.data.size());
        Log.d("FindListAdapter", "title " + discoveryColumn.title);
        Log.d("FindListAdapter", "list.size " + mList.size());
        Log.d("FindListAdapter", "findCardView.list.size " + findCardView.getCardViewListSize());
        Log.d("FindListAdapter", "-------------------");
        findCardView.setDiscoveryCardEntity(discoveryColumn);
        return convertView;
    }
}
