package com.edusoho.kuozhi.adapter.lesson;

import android.content.Context;
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
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by howzhi on 14-9-1.
 */
public abstract class AbstractCourseListAdapter<T> extends BaseAdapter {

    private Context mContext;
    protected LinkedList<T> mInfos;

    public AbstractCourseListAdapter(Context context) {
        mContext = context;
        mInfos = new LinkedList<T>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.teacher_course_item, null);
            holder = new ViewHolder();

            holder.aQuery = new AQuery(convertView);
            holder.title = (TextView) convertView.findViewById(R.id.auto_course_title);
            holder.status = (TextView) convertView.findViewById(R.id.auto_course_status);
            holder.learnStatusLayout = convertView.findViewById(R.id.auto_learn_status_layout);
            holder.learnStatus = (TextView) convertView.findViewById(R.id.auto_learn_text);
            holder.learnStatusProgress = (ProgressBar) convertView.findViewById(R.id.auto_learn_progress);
            convertView.setTag(holder);
        }
        return convertView;
    }

    protected void initViewData(ViewHolder holder, Course course)
    {
        int width = (int) (EdusohoApp.screenW * 0.45);
        holder.aQuery.id(R.id.auto_course_pic).image(
                course.largePicture, false, true, 200, R.drawable.noram_course);
        holder.aQuery.id(R.id.auto_course_pic)
                .width(width, false)
                .height(AppUtil.getCourseListCoverHeight(width), false);

        holder.title.setText(course.title);
        if (Const.COURSE_CLOSE.equals(course.status)) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setBackgroundDrawable(
                    mContext.getResources().getDrawable(R.drawable.red_card_bg)
            );
            holder.status.setText("已关闭");
        } else if (Const.COURSE_SERIALIZE.equals(course.serializeMode)) {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setBackgroundDrawable(
                    mContext.getResources().getDrawable(R.drawable.blue_card_bg)
            );
            holder.status.setText("更新中");
        } else {
            holder.status.setVisibility(View.GONE);
        }
    }

    protected class ViewHolder {
        public AQuery aQuery;
        public TextView title;
        public TextView status;
        public TextView learnStatus;
        public ProgressBar learnStatusProgress;
        public View learnStatusLayout;
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

    public void addItemLast(List<T> datas) {
        mInfos.addAll(datas);
        notifyDataSetChanged();
    }

    public void setItem(List<T> datas)
    {
        mInfos.clear();
        addItemLast(datas);
    }

    public void addItemTop(List<T> datas) {
        for (T course : datas) {
            mInfos.addFirst(course);
        }

        notifyDataSetChanged();
    }
}
