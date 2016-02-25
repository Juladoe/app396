package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.edusoho.kuozhi.v3.model.sys.FindListEntity;
import com.edusoho.kuozhi.v3.view.FindCardView;
import java.util.List;

/**
 * Created by su on 2016/2/24.
 */
public class FindListAdapter extends BaseAdapter {

    private Context mContext;
    private List<FindListEntity> mList;

    public FindListAdapter(Context context, List<FindListEntity> list)
    {
        this.mContext = context;
        this.mList = list;
    }

    public void addData(FindListEntity findCardEntity) {
        this.mList.add(findCardEntity);
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

        FindListEntity findListEntity = mList.get(position);
        FindCardView findCardView = (FindCardView) convertView;
        findCardView.setFindListEntity(findListEntity);
        return convertView;
    }
}
