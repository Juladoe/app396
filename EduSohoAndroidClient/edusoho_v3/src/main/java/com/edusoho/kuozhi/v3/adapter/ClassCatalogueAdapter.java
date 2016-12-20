package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by DF on 2016/12/15.
 */

public class ClassCatalogueAdapter extends BaseAdapter{
    public Context mContext;
    public List<Classroom> mCourseList;


    public ClassCatalogueAdapter(Context mContext, List<Classroom> mCourseList) {
        this.mContext = mContext;
        this.mCourseList = mCourseList;
    }

    @Override
    public int getCount() {
        return mCourseList == null ? 0 : mCourseList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCourseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ClassHolder classHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_class_catalog, parent, false);
            classHolder = new ClassHolder(convertView);
            convertView.setTag(classHolder);
        } else {
            classHolder = (ClassHolder) convertView.getTag();
        }
        Classroom classroom = mCourseList.get(position);
        ImageLoader.getInstance().displayImage(classroom.smallPicture, classHolder.mIvClass);
        classHolder.mTvTitle.setText(classroom.title);
        classHolder.mTvPeople.setText(classroom.studentNum+"人参与");
        if ("0.0".equals(classroom.price + "")) {
            classHolder.mTvFree.setText("免费");
            classHolder.mTvFree.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        } else {
            classHolder.mTvFree.setText("¥" + classroom.price);
        }
        return convertView;
    }

    public static class ClassHolder {
        public ImageView mIvClass;
        public TextView mTvTitle;
        public TextView mTvFree;
        public TextView mTvPeople;
        public ClassHolder(View itemView) {
            mIvClass = (ImageView) itemView.findViewById(R.id.iv_class);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvFree = (TextView) itemView.findViewById(R.id.tv_free_price);
            mTvPeople = (TextView) itemView.findViewById(R.id.tv_people_num);
        }
    }
}
