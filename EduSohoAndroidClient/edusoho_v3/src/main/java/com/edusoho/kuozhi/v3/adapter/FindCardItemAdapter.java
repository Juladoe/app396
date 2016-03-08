package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.discovery.DiscoveryCardProperty;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private List<DiscoveryCardProperty> mList;
    private DisplayImageOptions mOptions;

    public FindCardItemAdapter(Context context) {
        this(context, new ArrayList<DiscoveryCardProperty>());
    }

    public FindCardItemAdapter(Context context, List<DiscoveryCardProperty> list) {
        this.mContext = context;
        this.mList = list;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).showImageForEmptyUri(R.drawable.default_course).
                showImageOnFail(R.drawable.default_course).build();
    }

    public void clear() {
        mList.clear();
    }

    public void setData(List<DiscoveryCardProperty> list) {
        mList = list;
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
        DiscoveryCardProperty discoveryCardEntity = mList.get(position);
        if (discoveryCardEntity.isEmpty()) {
            return EMPTY;
        }
        switch (discoveryCardEntity.getType()) {
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

        return parent.getHeight() / (count / 2 + 1);
    }

    private View getViewByType(int position) {
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = getViewByType(position);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        DiscoveryCardProperty discoveryCardEntity = mList.get(position);

        if (discoveryCardEntity.isEmpty()) {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    parent.getWidth() / 2, getItemHeight(parent));
            convertView.setLayoutParams(lp);
            return convertView;
        }

        ImageLoader.getInstance().displayImage(discoveryCardEntity.getPicture(), viewHolder.coverView, mOptions);
        viewHolder.titleView.setText(discoveryCardEntity.getTitle());
        int padding = AppUtil.dp2px(mContext, 10);
        if (position % 2 == 0) {
            convertView.setPadding(0, padding, padding, padding);
        } else {
            convertView.setPadding(padding, padding, 0, padding);
        }

        if ("live".equals(discoveryCardEntity.getType())) {
            setLiveViewInfo(viewHolder, discoveryCardEntity);
            return convertView;
        }

        viewHolder.studentNumView.setText(String.valueOf(discoveryCardEntity.getStudentNum()));
        if (discoveryCardEntity.getPrice() > 0) {
            viewHolder.priceView.setTextColor(mContext.getResources().getColor(R.color.red_primary));
            viewHolder.priceView.setText(String.format("%.2f元", discoveryCardEntity.getPrice()));
        } else {
            viewHolder.priceView.setTextColor(mContext.getResources().getColor(R.color.green_primary));
            viewHolder.priceView.setText("免费");
        }
        setDiscoveryCardClickListener(convertView, discoveryCardEntity.getType(), discoveryCardEntity.getId());
        return convertView;
    }

    private void setLiveViewInfo(ViewHolder viewHolder, DiscoveryCardProperty discoveryCardEntity) {
        SpannableString colorStr = AppUtil.getColorTextAfter(String.valueOf(discoveryCardEntity.getStudentNum()), " 人参与",
                mContext.getResources().getColor(R.color.base_black_35)
        );
        viewHolder.studentNumView.setText(colorStr);
        viewHolder.liveNicknameView.setText(discoveryCardEntity.getTeacherNickname());
        ImageLoader.getInstance().displayImage(discoveryCardEntity.getTeacherAvatar(), viewHolder.liveAvatarView, mOptions);
        try {
            long startTime = discoveryCardEntity.getStartTime();
            long currentTime = System.currentTimeMillis();
            long endTime = discoveryCardEntity.getStartTime();
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
            viewHolder.liveTimeView.setText("直播时间: " + new SimpleDateFormat("MM-dd HH:mm").format(startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    private void setDiscoveryCardClickListener(View view, String type, int id) {
        final String url;
        switch (type) {
            case "normal":
            case "live":
                url = String.format(Const.MOBILE_APP_URL, EdusohoApp.app.schoolHost, String.format(Const.MOBILE_WEB_COURSE, id));
                break;
            case "classroom":
            default:
                url = String.format(Const.MOBILE_APP_URL, EdusohoApp.app.schoolHost, String.format(Const.CLASSROOM_COURSES, id));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EdusohoApp.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
            }
        });
    }

    class ViewHolder {
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
