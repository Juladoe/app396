package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.sys.FindCardEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.sql.SQLData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            viewHolder.labelContet = (ViewGroup) convertView.findViewById(R.id.card_label_content);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        FindCardEntity findCardEntity = mList.get(position);
        viewHolder.titleView.setText(findCardEntity.title);
        viewHolder.studentNumView.setText(String.valueOf(findCardEntity.studentNum));
        viewHolder.priceView.setText(String.format("%.2f元", findCardEntity.price));

        ImageLoader.getInstance().displayImage(findCardEntity.picture, viewHolder.coverView);
        if ("live".equals(findCardEntity.type)) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewHolder.labelContet.removeAllViews();
            viewHolder.labelContet.addView(createLiveLabel(findCardEntity), lp);
        } else if ("classroom".equals(findCardEntity.type)) {
            viewHolder.labelContet.removeAllViews();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 8;
            lp.topMargin = 8;
            lp.rightMargin = 8;
            lp.bottomMargin = 8;
            for (View view : createClassRoomLabel(findCardEntity)) {
                viewHolder.labelContet.addView(view, lp);
            }
        }
        return convertView;
    }

    protected List<View> createClassRoomLabel(FindCardEntity findCardEntity) {
        List<View> viewList = new ArrayList<>();

        TextView textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.base_small_size));
        textView.setPadding(3, 3, 3, 3);
        textView.setBackgroundColor(Color.BLUE);
        textView.setText("练");
        viewList.add(textView);

        textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.base_small_size));
        textView.setPadding(3, 3, 3, 3);
        textView.setBackgroundColor(Color.RED);
        textView.setText("试");
        viewList.add(textView);

        textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.base_small_size));
        textView.setPadding(3, 3, 3, 3);
        textView.setBackgroundColor(Color.DKGRAY);
        textView.setText("问");
        viewList.add(textView);
        return viewList;
    }

    protected View createLiveLabel(FindCardEntity findCardEntity) {

        TextView textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.base_small_size));
        textView.setPadding(16, 8, 8, 8);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long currentTime = new Date().getTime();
            long startTime = dateFormat.parse(findCardEntity.startTime).getTime();
            long endTime = dateFormat.parse(findCardEntity.endTime).getTime();
            if (currentTime > startTime && currentTime < endTime) {
                textView.setText("直播中");
                textView.setBackgroundResource(R.drawable.find_card_item_image_blue_label);
            } else if (currentTime > endTime) {
                textView.setText("直播已结束");
                textView.setBackgroundResource(R.drawable.find_card_item_image_red_label);
            } else if (currentTime < startTime) {
                textView.setText("最近直播时间:" + new SimpleDateFormat("MM-dd HH:mm").format(dateFormat.parse(findCardEntity.startTime)));
                textView.setBackgroundResource(R.drawable.find_card_item_image_red_label);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textView;
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
        public ViewGroup labelContet;
    }
}
