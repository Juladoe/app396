package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.sys.FindCardEntity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by su on 2016/2/19.
 */
public class FindCardItemAdapter extends BaseAdapter {

    private static final int EMPTY = 0;
    private static final int COURSE = 1;
    private static final int LIVE = 2;
    private static final int CLASSROOM = 3;

    private Context mContext;
    private List<FindCardEntity> mList;
    private DisplayImageOptions mOptions;

    public FindCardItemAdapter(Context context)
    {
        this(context, new ArrayList<FindCardEntity>());
    }

    public FindCardItemAdapter(Context context, List<FindCardEntity> list)
    {
        this.mContext = context;
        this.mList = list;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).showImageForEmptyUri(R.drawable.default_course).
                showImageOnFail(R.drawable.default_course).build();
    }

    public void clear() {
        mList.clear();
    }

    public void addData(FindCardEntity findCardEntity) {
        this.mList.add(findCardEntity);
        notifyDataSetChanged();
    }

    public void addList(List<FindCardEntity> list) {
        this.mList.addAll(list);
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
    public int getItemViewType(int position) {
        FindCardEntity findCardEntity = mList.get(position);
        if (findCardEntity.isEmpty()) {
            return EMPTY;
        }
        switch (findCardEntity.type) {
            case "course":
                return COURSE;
            case "classroom":
                return CLASSROOM;
            case "live":
                return LIVE;
        }
        return COURSE;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    private int getItemHeight(ViewGroup parent) {
        int count = getCount();
        if (count % 2 == 0) {
            return parent.getHeight() / (count / 2);
        }

        return parent.getHeight() / (count / 2 + 1 );
    }

    private View getViewByType(int position, ViewGroup parent) {
        int viewType = getItemViewType(position);
        View convertView = null;

        if (viewType == EMPTY) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.find_card_item_empty_layout, null);
            return convertView;
        }

        ViewHolder viewHolder = new ViewHolder();
        switch (viewType) {
            case CLASSROOM:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.find_card_item_classroom_layout, null);
                break;
            case LIVE:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.find_card_item_live_layout, null);
                viewHolder.liveTimeView = (TextView) convertView.findViewById(R.id.card_live_time);
                viewHolder.liveStartLabelView = (TextView) convertView.findViewById(R.id.card_live_start_label);
                viewHolder.liveAvatarView = (ImageView) convertView.findViewById(R.id.card_user_avatar);
                viewHolder.liveNicknameView = (TextView) convertView.findViewById(R.id.card_nickname);
                break;
            case COURSE:
            default:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.find_card_item_layout, null);
        }

        viewHolder.coverView = (ImageView) convertView.findViewById(R.id.card_cover);
        viewHolder.titleView = (TextView) convertView.findViewById(R.id.card_title);
        viewHolder.priceView = (TextView) convertView.findViewById(R.id.card_price);
        viewHolder.studentNumView = (TextView) convertView.findViewById(R.id.card_num);
        convertView.setTag(viewHolder);

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = getViewByType(position, parent);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        FindCardEntity findCardEntity = mList.get(position);
        if (findCardEntity.isEmpty()) {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    parent.getWidth() / 2, getItemHeight(parent));
            convertView.setLayoutParams(lp);
            return convertView;
        }

        ImageLoader.getInstance().displayImage(findCardEntity.picture, viewHolder.coverView, mOptions);
        viewHolder.titleView.setText(findCardEntity.title);
        int padding = AppUtil.dp2px(mContext, 8);
        if (position % 2 == 0) {
            convertView.setPadding(0, padding, padding, padding);
        } else {
            convertView.setPadding(padding, padding, 0, padding);
        }

        if ("live".equals(findCardEntity.type)) {
            setLiveViewInfo(viewHolder, findCardEntity);
            return convertView;
        }

        viewHolder.studentNumView.setText(String.valueOf(findCardEntity.studentNum));
        if (findCardEntity.price > 0) {
            viewHolder.priceView.setTextColor(mContext.getResources().getColor(R.color.red));
            viewHolder.priceView.setText(String.format("%.2f元", findCardEntity.price));
        } else {
            viewHolder.priceView.setTextColor(mContext.getResources().getColor(R.color.post_edit_color));
            viewHolder.priceView.setText("免费");
        }
        return convertView;
    }

    private void setLiveViewInfo(ViewHolder viewHolder, FindCardEntity findCardEntity) {
        SpannableString colorStr = AppUtil.getColorTextAfter(
                String.valueOf(findCardEntity.studentNum),
                " 人参与",
                mContext.getResources().getColor(R.color.base_black_35)
        );
        viewHolder.studentNumView.setText(colorStr);
        viewHolder.liveNicknameView.setText(findCardEntity.nickname);
        ImageLoader.getInstance().displayImage(findCardEntity.avatar, viewHolder.liveAvatarView, mOptions);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long currentTime = new Date().getTime();
            Date startTimeDate = dateFormat.parse(findCardEntity.startTime);
            long startTime = startTimeDate.getTime();
            long endTime = dateFormat.parse(findCardEntity.endTime).getTime();
            if (currentTime > startTime && currentTime < endTime) {
                viewHolder.liveStartLabelView.setText("直播中");
                viewHolder.liveStartLabelView.setBackgroundResource(R.drawable.find_card_item_image_green_label);
            } else if (currentTime > endTime) {
                viewHolder.liveStartLabelView.setText("已结束");
                viewHolder.liveStartLabelView.setBackgroundResource(R.drawable.find_card_item_image_gray_label);
            } else if (currentTime < startTime) {
                viewHolder.liveStartLabelView.setText("未开始");
                viewHolder.liveStartLabelView.setBackgroundResource(R.drawable.find_card_item_image_blue_label);
            }
            viewHolder.liveTimeView.setText("直播时间: " + new SimpleDateFormat("MM-dd HH:mm").format(startTimeDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        public TextView liveTimeView;
        public TextView liveStartLabelView;
        public TextView liveNicknameView;
        public ImageView liveAvatarView;
    }
}
