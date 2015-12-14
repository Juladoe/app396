package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.push.NewsCourseEntity;

import java.util.List;

/**
 * Created by melomelon on 15/12/10.
 */
public class StudyProcessRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LESSON_SUMMARY = 0;
    public static final int LESSON_TITLE = 1;
    public static final int COST_TIME = 2;
    public static final int NORMAL_NOTI = 3;
    public static final int INTENT_NOTI = 4;

    private List<NewsCourseEntity> mDataList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;


    public StudyProcessRecyclerAdapter(Context context, List list) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDataList = list;
    }

    @Override
    public int getItemViewType(int position) {
        NewsCourseEntity entity = mDataList.get(position);

        if (entity.getBodyType().equals("testpaper.reviewed")) {
            return INTENT_NOTI;
        } else if (entity.getBodyType().equals("course.lessonTitle")) {
            return LESSON_TITLE;
        } else {
            return NORMAL_NOTI;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case LESSON_SUMMARY:
                return new LessonSummaryViewHolder(mLayoutInflater.inflate(R.layout.item_study_process_lesson_summary, parent,false));

            case LESSON_TITLE:
                return new LessonTitleViewHolder(mLayoutInflater.inflate(R.layout.item_study_process_lesson_title, parent,false));

            case COST_TIME:
                return new CostTimeViewHolder(mLayoutInflater.inflate(R.layout.item_study_process_cost_time, parent,false));

            case NORMAL_NOTI:
                return new NormalNotificationViewHolder(mLayoutInflater.inflate(R.layout.item_study_process_notification, parent,false));

            case INTENT_NOTI:
                return new IntentNotificationViewHolder(mLayoutInflater.inflate(R.layout.item_study_process_notification, parent,false));

            default:
                return null;

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof LessonSummaryViewHolder) {

        }
        if (holder instanceof LessonTitleViewHolder) {
            String lessonTitle = mDataList.get(position).getContent();
            ((LessonTitleViewHolder) holder).lessonTitle.setText(lessonTitle);

        }
        if (holder instanceof CostTimeViewHolder) {

        }
        if (holder instanceof NormalNotificationViewHolder) {

            NewsCourseEntity entity = mDataList.get(position);
            String content = getTextContent(entity);
            ((NormalNotificationViewHolder) holder).notificationContent.setText(content);
//            ((NormalNotificationViewHolder) holder).notificationTeacherTime.setText(entity.getCreatedTime());
        }
        if (holder instanceof IntentNotificationViewHolder) {

            NewsCourseEntity entity = mDataList.get(position);
            String content = getTextContent(entity);
            ((IntentNotificationViewHolder) holder).notificationContent.setText(content);
//            ((IntentNotificationViewHolder) holder).notificationTeacherTime.setText(entity.getCreatedTime());
        }
    }

    private String getTextContent(NewsCourseEntity entity) {
        String textContent;

        textContent = entity.getTitle() + ":\n";
        textContent += "    " + entity.getContent();

        return textContent;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class LessonTitleViewHolder extends RecyclerView.ViewHolder {

        private TextView lessonTitle;

        public LessonTitleViewHolder(View itemView) {
            super(itemView);
            lessonTitle = (TextView) itemView.findViewById(R.id.study_process_lesson_title);

        }
    }

    private class LessonSummaryViewHolder extends RecyclerView.ViewHolder {

        private TextView summaryLessonTitle;
        private ImageView summaryLessonImage;
        private TextView summaryLessonIntroduction;
        private ImageView summaryTeacherAvatar;
        private TextView summaryLessonTeacher;

        public LessonSummaryViewHolder(View itemView) {
            super(itemView);
            summaryLessonTitle = (TextView) itemView.findViewById(R.id.study_process_lesson_summary_title);
            summaryLessonImage = (ImageView) itemView.findViewById(R.id.study_process_lesson_summary_image);
            summaryLessonIntroduction = (TextView) itemView.findViewById(R.id.study_process_lesson_summary_introduction);
            summaryLessonTeacher = (TextView) itemView.findViewById(R.id.study_process_lesson_summary_teacher);
            summaryTeacherAvatar = (ImageView) itemView.findViewById(R.id.study_process_lesson_summary_teacher_avatar);

        }
    }

    private class CostTimeViewHolder extends RecyclerView.ViewHolder {
        private TextView costTime;

        public CostTimeViewHolder(View itemView) {
            super(itemView);
            costTime = (TextView) itemView.findViewById(R.id.study_process_cost_time);
        }
    }

    private class NormalNotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView notificationContent;
        private TextView notificationTeacherTime;

        public NormalNotificationViewHolder(View itemView) {
            super(itemView);
            notificationContent = (TextView) itemView.findViewById(R.id.study_process_notification_content);
//            notificationTeacherTime = (TextView) itemView.findViewById(R.id.study_process_notification_teacher_time);

        }
    }

    private class IntentNotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView notificationContent;
        private TextView notificationTeacherTime;

        public IntentNotificationViewHolder(View itemView) {
            super(itemView);
            notificationContent = (TextView) itemView.findViewById(R.id.study_process_notification_content);
//            notificationTeacherTime = (TextView) itemView.findViewById(R.id.study_process_notification_teacher_time);

        }
    }
}
