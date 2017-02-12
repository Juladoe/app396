package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.Study;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.ui.fragment.mine.MyStudyFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by JesseHuang on 2017/2/10.
 */

public class MyCourseStudyAdapter extends RecyclerView.Adapter<MyStudyFragment.CourseStudyViewHolder> {
    private Context mContext;
    private int mCourseType = 1;
    private static final int COURSE_TYPE_LATEST = 1;
    private static final int COURSE_TYPE_NORMAL = 2;
    private static final int COURSE_TYPE_LIVE = 3;

    private List<Study.Resource> mLatestCourses;
    private List<Course> mNormalCourses;
    private List<Course> mLiveCourses;


    public MyCourseStudyAdapter(Context context) {
        this.mContext = context;
    }

    public void setLatestCourses(List<Study.Resource> list) {
        mCourseType = COURSE_TYPE_LATEST;
        mLatestCourses = list;
        notifyDataSetChanged();
    }

    public List<Study.Resource> getLatestCourses() {
        return mLatestCourses;
    }

    public void setNormalCourses(List<Course> list) {
        mCourseType = COURSE_TYPE_NORMAL;
        mNormalCourses = list;
        notifyDataSetChanged();
    }

    public List<Course> getNormalCourses() {
        return mNormalCourses;
    }

    public void setLiveCourses(List<Course> list) {
        mCourseType = COURSE_TYPE_LIVE;
        mLiveCourses = list;
        notifyDataSetChanged();
    }

    public List<Course> getLiveCourses() {
        return mLiveCourses;
    }

    @Override
    public MyStudyFragment.CourseStudyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_study, parent, false);
        return new MyStudyFragment.CourseStudyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyStudyFragment.CourseStudyViewHolder viewHolder, int position) {
        viewHolder.layoutClass.setVisibility(View.GONE);
        viewHolder.layoutLive.setVisibility(View.GONE);
        viewHolder.tvStudyState.setText("");
        switch (mCourseType) {
            case COURSE_TYPE_LATEST:
                final Study.Resource latestCourse = mLatestCourses.get(position);
                switch (latestCourse.getJoinedType()) {
                    case "classroom":
                        if (latestCourse.getClassroomTitle() != null &&
                                latestCourse.getClassroomTitle().length() > 0) {
                            viewHolder.layoutClass.setVisibility(View.VISIBLE);
                            viewHolder.tvClassName.setText(latestCourse.getClassroomTitle());
                        }
                        viewHolder.tvMore.setVisibility(View.GONE);
                        break;
                    case "course":
                        if (latestCourse.getClassroomTitle() != null &&
                                latestCourse.getClassroomTitle().length() > 0) {
                            viewHolder.layoutClass.setVisibility(View.VISIBLE);
                            viewHolder.tvClassName.setText(latestCourse.getClassroomTitle());
                        }
                        viewHolder.tvMore.setVisibility(View.VISIBLE);
                        break;
                }
                ImageLoader.getInstance().displayImage(latestCourse.getLargePicture()
                        , viewHolder.ivPic, EdusohoApp.app.mOptions);
                viewHolder.tvTitle.setText(String.valueOf(latestCourse.getTitle()));
                if (latestCourse.getType().equals("live")) {
                    viewHolder.layoutLive.setVisibility(View.VISIBLE);
                    if (latestCourse.liveState == 1) {
                        viewHolder.tvLive.setText(R.string.lesson_living);
                        viewHolder.tvLiveIcon.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.tvLive.setText("直播");
                        viewHolder.tvLiveIcon.setVisibility(View.GONE);
                    }
                }

                setProgressStr(latestCourse.getLearnedNum(), latestCourse.getTotalLesson(), viewHolder.tvStudyState);
                break;
            case COURSE_TYPE_NORMAL:
                final Course normalCourse = mNormalCourses.get(position);
                ImageLoader.getInstance().displayImage(normalCourse.getLargePicture(), viewHolder.ivPic,
                        EdusohoApp.app.mOptions);
                viewHolder.tvTitle.setText(String.valueOf(normalCourse.title));
                setProgressStr(normalCourse.learnedNum, normalCourse.totalLesson, viewHolder.tvStudyState);
                break;
            case COURSE_TYPE_LIVE:
                final Course liveCourse = mLiveCourses.get(position);
                ImageLoader.getInstance().displayImage(liveCourse.getLargePicture(), viewHolder.ivPic,
                        EdusohoApp.app.mOptions);
                viewHolder.tvTitle.setText(String.valueOf(liveCourse.title));
                setProgressStr(liveCourse.learnedNum, liveCourse.totalLesson, viewHolder.tvStudyState);
                if (liveCourse.type.equals("live")) {
                    viewHolder.layoutLive.setVisibility(View.VISIBLE);
                    viewHolder.tvMore.setVisibility(liveCourse.parentId == 0 ? View.VISIBLE : View.GONE);
                    if (liveCourse.liveState == 1) {
                        viewHolder.tvLive.setText(R.string.lesson_living);
                        viewHolder.tvLiveIcon.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.tvLive.setText("直播");
                        viewHolder.tvLiveIcon.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        switch (mCourseType) {
            case COURSE_TYPE_LATEST:
                return mLatestCourses != null ? mLatestCourses.size() : 0;
            case COURSE_TYPE_NORMAL:
                return mNormalCourses != null ? mNormalCourses.size() : 0;
            case COURSE_TYPE_LIVE:
                return mLiveCourses != null ? mLiveCourses.size() : 0;
        }
        return 0;
    }

    private void setProgressStr(int now, int total, TextView view) {
        if (total == 0) {
            view.setText("");
            return;
        }
        String str;
        if (now == 0) {
            str = "未开始学习";
            view.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
        } else if (now == total) {
            str = "已学完";
            view.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        } else {
            str = String.format("已学习%s/%s课", now, total);
            view.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        }
        view.setText(str);
    }
}
