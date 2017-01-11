package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.course.DownloadCourse;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suju on 17/1/10.
 */

public class CourseDownloadAdapter extends BaseAdapter {

    private Context mContext;
    private List<DownloadCourse> mList;

    public CourseDownloadAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_download_manager_course_group, null);
            convertView.setTag(new DownloadCourseItem(convertView));
        }

        DownloadCourseItem downloadCourseItem = (DownloadCourseItem) convertView.getTag();
        downloadCourseItem.renderData(mList.get(position));
        return convertView;
    }

    public void setCourseList(List<DownloadCourse> courseList) {
        mList.clear();
        mList.addAll(courseList);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Course getItem(int position) {
        return mList.get(position);
    }

    public static class DownloadCourseItem {

        public ImageView ivCover;
        public TextView tvCourseTitle;
        public TextView tvSourse;
        public TextView ivVideoSum;
        public TextView ivVideoSizes;

        public DownloadCourseItem(View view) {
            ivCover = (ImageView) view.findViewById(R.id.iv_avatar);
            tvCourseTitle = (TextView) view.findViewById(R.id.tv_course_title);
            ivVideoSum = (TextView) view.findViewById(R.id.tv_video_sum);
            ivVideoSizes = (TextView) view.findViewById(R.id.tv_video_size);
            tvSourse = (TextView) view.findViewById(R.id.tv_download_source);
        }

        public void renderData(DownloadCourse course) {
            ImageLoader.getInstance().displayImage(course.getPicture(), ivCover);
            tvCourseTitle.setText(course.title);
            ivVideoSizes.setText(getCacheSize(course.getCachedSize()));
            ivVideoSum.setText(String.format("已缓存%d课", course.getCachedLessonNum()));

            if ("classroom".equals(course.getSource())) {
                tvSourse.setVisibility(View.VISIBLE);
                tvSourse.setText(AppUtil.getColorTextAfter("来自班级 | ", course.getSource(), Color.rgb(113, 119, 125)));
            } else {
                tvSourse.setVisibility(View.GONE);
                tvSourse.setText("");
            }
        }

        private String getCacheSize(long size) {
            float realSize = size / 1024.0f / 1024.0f;
            if (realSize == 0) {
                return "0M";
            } else {
                return String.format("%.0f%s", realSize, "M");
            }
        }
    }
}
