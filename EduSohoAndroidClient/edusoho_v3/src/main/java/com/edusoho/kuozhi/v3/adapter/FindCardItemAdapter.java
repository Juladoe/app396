package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.sys.FindCardEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by su on 2016/2/19.
 */
public class FindCardItemAdapter extends BaseAdapter {

    private Context mContext;
    private List<FindCardEntity> mList;

    public FindCardItemAdapter(Context context, List<FindCardEntity> list)
    {
        this.mContext = context;
        this.mList = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.find_card_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.coverView = (ImageView) convertView.findViewById(R.id.card_cover);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.card_title);
            viewHolder.priceView = (TextView) convertView.findViewById(R.id.card_price);
            viewHolder.studentNumView = (TextView) convertView.findViewById(R.id.card_num);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        FindCardEntity findCardEntity = mList.get(position);
        viewHolder.titleView.setText(findCardEntity.title);
        viewHolder.studentNumView.setText(String.valueOf(findCardEntity.studentNum));
        viewHolder.priceView.setText(String.valueOf(findCardEntity.price));

        ImageLoader.getInstance().displayImage(findCardEntity.picture, viewHolder.coverView);
        return convertView;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    class ViewHolder
    {
        public ImageView coverView;
        public TextView titleView;
        public TextView priceView;
        public TextView studentNumView;
    }
}
