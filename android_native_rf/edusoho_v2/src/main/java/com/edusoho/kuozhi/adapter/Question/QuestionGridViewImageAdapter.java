package com.edusoho.kuozhi.adapter.Question;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyPC on 14-10-30.
 */
public class QuestionGridViewImageAdapter extends BaseAdapter {
    private Context mContext;
    private int mResourceId;
    private List<String> mImageUrlList;

    public QuestionGridViewImageAdapter(Context context, int layoutId, ArrayList<String> list) {
        this.mContext = context;
        this.mResourceId = layoutId;
        this.mImageUrlList = list;
    }

    @Override
    public int getCount() {
        if (mImageUrlList != null) {
            return mImageUrlList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mImageUrlList != null) {
            return mImageUrlList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView != null) {
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_question_content_image);

        }

        return null;
    }

    public static class ViewHolder {
        public ImageView ivContentImage;
    }
}
