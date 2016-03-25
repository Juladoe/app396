package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.util.SparseArray;
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
    private SparseArray<DiscoveryColumn> mList;
    private List<Integer> mIndexs;

    public FindListAdapter(Context context) {
        mContext = context;
        mList = new SparseArray<>();
        mIndexs = new ArrayList<>();
    }

    public void addData(int position, DiscoveryColumn findCardEntity) {
        mList.append(position, findCardEntity);
        mIndexs.add(position);
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
        mIndexs.clear();
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

        DiscoveryColumn discoveryColumn = mList.get(mIndexs.get(position));
        if (discoveryColumn == null) {
            convertView.setVisibility(View.GONE);
            return convertView;
        }
        convertView.setVisibility(View.VISIBLE);
        FindCardView findCardView = (FindCardView) convertView;
        findCardView.setDiscoveryCardEntity(discoveryColumn);
        findCardView.setMoreClickListener(discoveryColumn.type);
        return convertView;
    }
}
