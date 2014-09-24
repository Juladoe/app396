package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.dodowaterfall.widget.ScaleImageView;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by howzhi on 14-9-1.
 */
public class ScrollListAdapter extends BaseAdapter {

    private Context mContext;
    private LinkedList<Course> mInfos;
    public ScrollListAdapter(Context context) {
        mContext = context;
        mInfos = new LinkedList<Course>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        Course course = mInfos.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.teacher_course_item, null);
            holder = new ViewHolder();
            holder.aQuery = new AQuery(convertView);
            holder.title = (TextView) convertView.findViewById(R.id.auto_course_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int width = (int) (EdusohoApp.screenW * 0.45);
        holder.aQuery.id(R.id.auto_course_pic).image(
                course.largePicture, false, true, 200, R.drawable.noram_course);
        holder.aQuery.id(R.id.auto_course_pic)
                .width(width, false)
                .height(AppUtil.getCourseListCoverHeight(width), false);

        holder.title.setText(course.title);

        return convertView;
    }

    class ViewHolder {
        AQuery aQuery;
        TextView title;
    }

    @Override
    public int getCount() {
        return mInfos.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mInfos.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public void addItemLast(List<Course> datas) {
        mInfos.addAll(datas);
        notifyDataSetChanged();
    }

    public void addItemTop(List<Course> datas) {
        for (Course course : datas) {
            mInfos.addFirst(course);
        }

        notifyDataSetChanged();
    }
}
