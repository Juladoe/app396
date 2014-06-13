package com.edusoho.kowzhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kowzhi.EdusohoApp;
import com.edusoho.kowzhi.R;
import com.edusoho.kowzhi.model.LearnCourse;
import com.edusoho.kowzhi.util.AppUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class LearnCourseListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<LearnCourse> mList;
    public int page = 0;
    public int count = 0;

    public LearnCourseListAdapter(Context context, HashMap<String,LearnCourse> list,
                                  int resource) {
        mList = new ArrayList<LearnCourse>();
        listAddItem(list);
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
    }

    /**
     *
     * @param courseItems
     */
    private void listAddItem(HashMap<String,LearnCourse> courseItems)
    {
        for (LearnCourse item : courseItems.values()) {
            mList.add(item);
        }
    }

    public void addItem(HashMap<String,LearnCourse> list)
    {
        listAddItem(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int index) {
        return mList.get(index);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        LearnCourse course =  mList.get(index);

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.course_title = (TextView) view.findViewById(R.id.course_title);
            holder.course_subtitle = (TextView) view.findViewById(R.id.course_subtitle);
            holder.course_learn_progress = (ProgressBar) view.findViewById(R.id.course_learn_progress);
            holder.course_memberLearnedNum = (TextView) view.findViewById(R.id.course_memberLearnedNum);
            holder.aq = new AQuery(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.course_subtitle.setText(course.subtitle);
        holder.course_title.setText(course.title);
        holder.course_learn_progress.setMax(course.lessonNum);
        holder.course_learn_progress.setProgress(course.memberLearnedNum);

        if (course.memberLearnedNum == course.lessonNum) {
            holder.course_memberLearnedNum.setText("已学完");
        } else {
            holder.course_memberLearnedNum.setText("学习到第" + course.memberLearnedNum + "课时");
        }

        holder.aq.id(R.id.course_pic).image(
                course.largePicture, false, true);
        int width = EdusohoApp.app.screenW;
        holder.aq.id(R.id.course_pic).height(AppUtil.getLearnCourseListCoverHeight(width), false);
        return view;
    }

    private class ViewHolder {
        public AQuery aq;
        public TextView course_title;
        public ProgressBar course_learn_progress;
        public TextView course_memberLearnedNum;
        public TextView course_subtitle;
    }

}
