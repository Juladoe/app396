package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.LearnCourse;

import java.util.ArrayList;

/**
 * Created by JesseHuang on 15/1/7.
 * 在学课程中的已学完Adapter
 */
public class LearnedCourseAdapter extends ListBaseAdapter<LearnCourse> {
    public LearnedCourseAdapter(int inflate, Context content) {
        super(content, inflate);
    }

    @Override
    public void addItems(ArrayList<LearnCourse> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(mResource, null);
            viewHolder.tvItemTitle = (TextView) convertView.findViewById(R.id.lession_list_title);
            viewHolder.aQuery = new AQuery(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        LearnCourse learnCourse = mList.get(position);
        viewHolder.aQuery.id(R.id.lession_list_img).image(learnCourse.largePicture);
        viewHolder.tvItemTitle.setText(learnCourse.title);

        return convertView;
    }

    private class ViewHolder {
        TextView tvItemTitle;
        AQuery aQuery;
    }
}
