package com.edusoho.kuozhi.adapter.lesson;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.LearnCourse;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by howzhi on 14-9-1.
 */
public class LearningListAdapter extends AbstractCourseListAdapter {

    public LearningListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ViewHolder holder = (ViewHolder) view.getTag();

        LearnCourse course = (LearnCourse) mInfos.get(position);
        initViewData(holder, course);
        setLearnStatus(holder, course);
        return view;
    }

    protected void setLearnStatus(ViewHolder holder, LearnCourse course){
        holder.learnStatusLayout.setVisibility(View.VISIBLE);
        holder.learnStatusProgress.setMax(course.lessonNum);
        holder.learnStatusProgress.setProgress(course.memberLearnedNum);

        StringBuffer stringBuffer = new StringBuffer("学习到第 ");
        int start = stringBuffer.length();
        stringBuffer.append(course.memberLearnedNum).append(" 课时");
        SpannableString spannableString = new SpannableString(stringBuffer);
        spannableString.setSpan(
                new ForegroundColorSpan(Color.GREEN)
                , start
                , start + 1
                , SpannableString.SPAN_EXCLUSIVE_INCLUSIVE
        );
        holder.learnStatus.setText(spannableString);
    }

}
