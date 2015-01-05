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
 * Created by onewoman on 2014/12/3.
 */
public class LessionListAdapter extends ListBaseAdapter<LearnCourse>{
    public LessionListAdapter(int inflate, Context content){
        super(content,inflate);
    }

    @Override
    public void addItems(ArrayList<LearnCourse> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if(convertView == null){
            convertView = inflater.inflate(mResource, null);
            viewHolder.tvItemTitle = (TextView) convertView.findViewById(R.id.lession_list_title);
            viewHolder.tvItemLasslessionTitle = (TextView) convertView.findViewById(R.id.lession_list_lasslessontitle);
            viewHolder.itemProgress = (ProgressBar) convertView.findViewById(R.id.lession_list_progress);
            viewHolder.tvItemProgress = (TextView) convertView.findViewById(R.id.lession_list_progress_text);
            viewHolder.aQuery = new AQuery(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        LearnCourse learnCourse = mList.get(position);
        viewHolder.aQuery.id(R.id.lession_list_img).image(learnCourse.largePicture);
        viewHolder.tvItemTitle.setText(learnCourse.title);

        if(learnCourse.lastLessonTitle != null) {
            String lastLessonTitle = String.format("学习到课时%d:%s",learnCourse.memberLearnedNum,learnCourse.lastLessonTitle);
            viewHolder.tvItemLasslessionTitle.setText(lastLessonTitle);
        }

        String progress = Math.round((learnCourse.memberLearnedNum/ (learnCourse.lessonNum * 1.0)) * 100) + "%";
        viewHolder.tvItemProgress.setText(progress);
        viewHolder.itemProgress.setMax(learnCourse.lessonNum);
        viewHolder.itemProgress.setProgress(learnCourse.memberLearnedNum);

        return convertView;
    }

    private class ViewHolder{
        TextView tvItemTitle;
        TextView tvItemLasslessionTitle;
        TextView tvItemProgress;
        ProgressBar itemProgress;
        AQuery aQuery;
    }
}
