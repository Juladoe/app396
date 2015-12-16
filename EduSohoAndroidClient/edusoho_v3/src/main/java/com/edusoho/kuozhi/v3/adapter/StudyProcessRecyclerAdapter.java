package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.push.NewsCourseEntity;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.fragment.CourseStudyProcessFragment;
import com.edusoho.kuozhi.v3.ui.fragment.test.TestpaperResultFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

/**
 * Created by melomelon on 15/12/10.
 */
public class StudyProcessRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int COURSE_SUMMARY = 0;
    public static final int LESSON_TITLE = 1;
    public static final int COST_TIME = 2;
    public static final int NORMAL_NOTI = 3;
    public static final int INTENT_NOTI = 4;

    private List<NewsCourseEntity> mDataList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private EdusohoApp mApp;



    public StudyProcessRecyclerAdapter(Context context, List list,EdusohoApp app) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDataList = list;
        this.mApp = app;
    }

    @Override
    public int getItemViewType(int position) {
        NewsCourseEntity entity = mDataList.get(position);

        if (entity.getBodyType().equals("testpaper.reviewed")) {
            return INTENT_NOTI;
        } else if (entity.getBodyType().equals("course.lessonTitle")) {
            return LESSON_TITLE;
        } else if (entity.getBodyType().equals("course.summary")){
            return COURSE_SUMMARY;
        }else {
            return NORMAL_NOTI;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case COURSE_SUMMARY:
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
            NewsCourseEntity entity = mDataList.get(position);
            ((LessonSummaryViewHolder) holder).summaryCourseIntroduction.setText("课程简介："+entity.getContent());
            ImageLoader.getInstance().displayImage(entity.getImage(), ((LessonSummaryViewHolder) holder).summaryCourseImage);
            ((LessonSummaryViewHolder) holder).summaryCourseTitle.setText(entity.getTitle());
            ((LessonSummaryViewHolder) holder).summaryCourseTeacher.setText(entity.getTeacher());
        }

        if (holder instanceof LessonTitleViewHolder) {
            final NewsCourseEntity entity = mDataList.get(position);
            String lessonTitle = entity.getContent();
            ((LessonTitleViewHolder) holder).lessonTitle.setText(lessonTitle);
            ((LessonTitleViewHolder) holder).lessonTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mApp.mEngine.runNormalPlugin(
                            LessonActivity.TAG, mContext, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    startIntent.putExtra(Const.COURSE_ID, entity.getCourseId());
                                    startIntent.putExtra(Const.LESSON_ID, entity.getObjectId());
                                }
                            }
                    );
                }
            });

        }
        if (holder instanceof CostTimeViewHolder) {

        }
        if (holder instanceof NormalNotificationViewHolder) {

            NewsCourseEntity entity = mDataList.get(position);
            String content = getTextContent(entity);
            ((NormalNotificationViewHolder) holder).notificationContent.setText(content);
//            ((NormalNotificationViewHolder) holder).notificationTeacherTime.setText(entity.getCreatedTime());
            if (entity.getBodyType().equals("announcement.create")){
                ((NormalNotificationViewHolder) holder).typeImage.setImageResource(R.drawable.icon_course_notification);

            }
        }

        if (holder instanceof IntentNotificationViewHolder) {

            final NewsCourseEntity entity = mDataList.get(position);
            String content = getTextContent(entity);

            ((IntentNotificationViewHolder) holder).notificationContent.setText(content);
//            ((IntentNotificationViewHolder) holder).notificationTeacherTime.setText(entity.getCreatedTime());
            if (entity.getBodyType().equals("testpaper.reviewed")){
                ((IntentNotificationViewHolder) holder).typeImage.setImageResource(R.drawable.icon_testpater_reviewed);
                ((IntentNotificationViewHolder) holder).notificationContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mApp.mEngine.runNormalPlugin("FragmentPageActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "TestpaperResultFragment");
                                startIntent.putExtra(Const.ACTIONBAR_TITLE, entity.getTitle() + "考试结果");
                                startIntent.putExtra(TestpaperResultFragment.RESULT_ID, entity.getObjectId());
                                startIntent.putExtra(Const.STATUS, "finished");
                            }
                        });
                    }
                });
            }

            if (entity.getBodyType().equals("homework.reviewed")){
                ((IntentNotificationViewHolder) holder).typeImage.setImageResource(R.drawable.icon_homework_reviewed);
                ((IntentNotificationViewHolder) holder).notificationContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //// TODO: 15/12/16
                    }
                });
            }
            if (entity.getBodyType().equals("question.answered")){
                ((IntentNotificationViewHolder) holder).typeImage.setImageResource(R.drawable.icon_quenstion_answer);
                ((IntentNotificationViewHolder) holder).notificationContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //// TODO: 15/12/16
                    }
                });
            }
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

        private TextView summaryCourseTitle;
        private ImageView summaryCourseImage;
        private TextView summaryCourseIntroduction;
        private ImageView summaryTeacherAvatar;
        private TextView summaryCourseTeacher;

        public LessonSummaryViewHolder(View itemView) {
            super(itemView);
            summaryCourseTitle = (TextView) itemView.findViewById(R.id.study_process_lesson_summary_title);
            summaryCourseImage = (ImageView) itemView.findViewById(R.id.study_process_lesson_summary_image);
            summaryCourseIntroduction = (TextView) itemView.findViewById(R.id.study_process_lesson_summary_introduction);
            summaryCourseTeacher = (TextView) itemView.findViewById(R.id.study_process_lesson_summary_teacher);
//            summaryTeacherAvatar = (ImageView) itemView.findViewById(R.id.study_process_lesson_summary_teacher_avatar);

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
        private ImageView typeImage;

        public NormalNotificationViewHolder(View itemView) {
            super(itemView);
            notificationContent = (TextView) itemView.findViewById(R.id.study_process_notification_content);
//            notificationTeacherTime = (TextView) itemView.findViewById(R.id.study_process_notification_teacher_time);
            typeImage = (ImageView) itemView.findViewById(R.id.notification_type_image);
        }

    }

    private class IntentNotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView notificationContent;
        private TextView notificationTeacherTime;
        private ImageView typeImage;

        public IntentNotificationViewHolder(View itemView) {
            super(itemView);
            notificationContent = (TextView) itemView.findViewById(R.id.study_process_notification_content);
//            notificationTeacherTime = (TextView) itemView.findViewById(R.id.study_process_notification_teacher_time);
            typeImage = (ImageView) itemView.findViewById(R.id.notification_type_image);

        }
    }
}
