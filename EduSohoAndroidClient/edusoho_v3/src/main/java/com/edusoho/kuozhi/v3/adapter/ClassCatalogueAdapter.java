package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.List;

/**
 * Created by DF on 2016/12/15.
 */

public class ClassCatalogueAdapter extends BaseAdapter{
    public Context mContext;
//    public List<ClassCatalogue.Course> mCourseList;
    public List mCourseList;


    public ClassCatalogueAdapter(Context mContext, List mCourseList) {
        this.mContext = mContext;
        this.mCourseList = mCourseList;
    }

    @Override
    public int getCount() {
        return mCourseList == null ? 0 : mCourseList.size();
    }

    @Override
    public Object getItem(int position) {
//        return mCourseList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ClassHolder classHolder;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_class_catalog, parent, false);
//            classHolder = new ClassHolder(convertView);
//            convertView.setTag(classHolder);
//        } else {
//            classHolder = (ClassHolder) convertView.getTag();
//        }
//        ClassCatalogue.Course coursesBean = mCourseList.get(position);
//        ImageLoader.getInstance().displayImage(coursesBean.getSmallPicture(), classHolder.mIvClass);
//        classHolder.mTvTitle.setText(coursesBean.getTitle());
//        classHolder.mTvPeople.setText(coursesBean.getStudentNum());
//        if ("0.00".equals(coursesBean.getPrice())) {
//            classHolder.mTvFree.setText("免费");
//            classHolder.mTvFree.setTextColor(mContext.getResources().getColor(R.color.primary_color));
//        } else {
//            classHolder.mTvFree.setText("¥" + coursesBean.getPrice());
//        }
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
            mTvPeople = (TextView) itemView.findViewById(R.id.tv_people_join);
        }
    }
}
