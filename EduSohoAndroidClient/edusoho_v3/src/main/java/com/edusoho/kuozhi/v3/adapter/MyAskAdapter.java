package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remilia on 2017/1/5.
 */
public class MyAskAdapter extends BaseAdapter {

    private Context mContext;
    private int type = 0;
    private List<Object> mLists = new ArrayList<>();

    public MyAskAdapter(Context context, int type) {
        this.mContext = context;
        this.type = type;
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (type == 0) {
            convertView = buildAskView(position, convertView, parent);
        } else {
            convertView = buildAskView(position, convertView, parent);
        }
        return convertView;
    }

    private View buildAskView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_ask1, null, false);
            viewHolderAsk = new ViewHolderAsk();
            viewHolderAsk.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            viewHolderAsk.tvNickname = (TextView) convertView.findViewById(R.id.tv_nickname);
            viewHolderAsk.tvType = (TextView) convertView.findViewById(R.id.tv_type);
            viewHolderAsk.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolderAsk.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolderAsk.tvReviewNum = (TextView) convertView.findViewById(R.id.tv_review_num);
            convertView.setTag(viewHolderAsk);
        } else {
            viewHolderAsk = (ViewHolderAsk) convertView.getTag();
        }
        return convertView;
    }

    private View buildAnswerView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_ask2, null, false);
            viewHolderAnswer = new ViewHolderAnswer();
            viewHolderAnswer.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            viewHolderAnswer.tvNicknameAnswer = (TextView) convertView.findViewById(R.id.tv_nickname_answer);
            viewHolderAnswer.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolderAnswer.tvContentAnswer = (TextView) convertView.findViewById(R.id.tv_content_answer);
            viewHolderAnswer.tvNicknameAsk = (TextView) convertView.findViewById(R.id.tv_nickname_ask);
            viewHolderAnswer.tvContentAsk = (TextView) convertView.findViewById(R.id.tv_content_ask);
            convertView.setTag(viewHolderAnswer);
        } else {
            viewHolderAnswer = (ViewHolderAnswer) convertView.getTag();
        }
        return convertView;
    }

    private void initData() {

    }

    private static ViewHolderAsk viewHolderAsk;
    private static ViewHolderAnswer viewHolderAnswer;

    private class ViewHolderAsk {
        ImageView ivAvatar;
        TextView tvNickname;
        TextView tvType;
        TextView tvContent;
        TextView tvTime;
        TextView tvReviewNum;
    }

    private class ViewHolderAnswer {
        ImageView ivAvatar;
        TextView tvNicknameAnswer;
        TextView tvTime;
        TextView tvContentAnswer;
        TextView tvNicknameAsk;
        TextView tvContentAsk;
    }

    public void setType(int type) {
        this.type = type;
        initData();
    }
}
