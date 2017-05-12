package com.edusoho.kuozhi.clean.module.mine.study;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.StudyCourse;
import com.edusoho.kuozhi.clean.bean.innerbean.Study;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.clean.module.mine.me.MineFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.CourseCacheHelper;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.view.dialog.MoreDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/2/10.
 */

public class MyCourseStudyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private int mCourseType = 1;
    public static final int COURSE_TYPE_NORMAL = 2;
    public static final int COURSE_TYPE_LIVE = 3;

    private static final int EMPTY = 0;
    private static final int NOT_EMPTY = 1;
    private int mCurrentDataStatus;

    private List<StudyCourse> mNormalCourses;
    private List<Study> mLiveCourses;


    MyCourseStudyAdapter(Context context) {
        this.mContext = context;
        mNormalCourses = new ArrayList<>();
        mLiveCourses = new ArrayList<>();
    }

    void setNormalCourses(List<StudyCourse> list) {
        mCourseType = COURSE_TYPE_NORMAL;
        mNormalCourses = list;
    }

    void setLiveCourses(List<Study> list) {
        mCourseType = COURSE_TYPE_LIVE;
        mLiveCourses = list;
    }

    public void clear() {
        mNormalCourses.clear();
        mLiveCourses.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return mCurrentDataStatus;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mCurrentDataStatus == NOT_EMPTY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_study, parent, false);
            return new MyStudyFragment.CourseStudyViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_empty, parent, false);
            return new MineFragment.EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (mCurrentDataStatus == NOT_EMPTY) {
            MyStudyFragment.CourseStudyViewHolder courseStudyViewHolder = (MyStudyFragment.CourseStudyViewHolder) viewHolder;
            courseStudyViewHolder.layoutClass.setVisibility(View.GONE);
            courseStudyViewHolder.layoutLive.setVisibility(View.GONE);
            courseStudyViewHolder.tvStudyState.setText("");
            switch (mCourseType) {
                case COURSE_TYPE_NORMAL:
                    final StudyCourse studyCourse = mNormalCourses.get(position);
                    ImageLoader.getInstance().displayImage(studyCourse.courseSet.cover.large, courseStudyViewHolder.ivPic,
                            EdusohoApp.app.mOptions);
                    courseStudyViewHolder.tvTitle.setText(String.valueOf(studyCourse.title));
                    setProgressStr(studyCourse.learnedNum, studyCourse.publishedTaskNum, courseStudyViewHolder.tvStudyState);
                    courseStudyViewHolder.rLayoutItem.setTag(studyCourse);
                    courseStudyViewHolder.rLayoutItem.setOnClickListener(getCourseViewClickListener());
                    courseStudyViewHolder.tvMore.setTag(studyCourse);
                    courseStudyViewHolder.tvMore.setOnClickListener(getMoreClickListener());
                    break;
                case COURSE_TYPE_LIVE:
                    final Study study = mLiveCourses.get(position);
                    ImageLoader.getInstance().displayImage(study.cover.large, courseStudyViewHolder.ivPic,
                            EdusohoApp.app.mOptions);
                    courseStudyViewHolder.tvTitle.setText(String.valueOf(study.title));
//                    setProgressStr(study.learnedNum, liveCourse.totalLesson, courseStudyViewHolder.tvStudyState);
                    courseStudyViewHolder.rLayoutItem.setTag(study);
                    courseStudyViewHolder.rLayoutItem.setOnClickListener(getCourseViewClickListener());
                    if (study.type.equals("live")) {
                        courseStudyViewHolder.layoutLive.setVisibility(View.VISIBLE);
//                        courseStudyViewHolder.tvMore.setVisibility(study.parentId == 0 ? View.VISIBLE : View.GONE);
//                        if (liveCourse.liveState == 1) {
//                            courseStudyViewHolder.tvLive.setText(R.string.lesson_living);
//                            courseStudyViewHolder.tvLiveIcon.setVisibility(View.VISIBLE);
//                        } else {
//                            courseStudyViewHolder.tvLive.setText("直播");
//                            courseStudyViewHolder.tvLiveIcon.setVisibility(View.GONE);
//                        }
                    }
                    courseStudyViewHolder.tvMore.setTag(study);
                    courseStudyViewHolder.tvMore.setOnClickListener(getMoreClickListener());
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        switch (mCourseType) {
            case COURSE_TYPE_NORMAL:
                if (mNormalCourses != null && mNormalCourses.size() != 0) {
                    mCurrentDataStatus = NOT_EMPTY;
                    return mNormalCourses.size();
                }
                break;
            case COURSE_TYPE_LIVE:
                if (mLiveCourses != null && mLiveCourses.size() != 0) {
                    mCurrentDataStatus = NOT_EMPTY;
                    return mLiveCourses.size();
                }
                break;
        }
        mCurrentDataStatus = EMPTY;
        return 1;
    }

    private View.OnClickListener getCourseViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StudyCourse studyCourse = (StudyCourse) v.getTag();
                CourseProjectActivity.launch(mContext, studyCourse.courseSet.id);
            }
        };
    }

    private View.OnClickListener getMoreClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "i_learn_threePoints");
                final Object data = v.getTag();
                MoreDialog dialog = new MoreDialog(mContext);
                dialog.init("退出课程", new MoreDialog.MoreCallBack() {
                    @Override
                    public void onMoveClick(View v, final Dialog dialog) {
                        AlertDialog.Builder latestCoursebuilder = new AlertDialog.Builder(mContext);
                        if (data instanceof Study) {
                            final Study study = (Study) data;
                            latestCoursebuilder.setTitle("确认退出课程")
                                    .setMessage(R.string.delete_course)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dlg, int which) {
                                            CourseUtil.deleteCourse(study.id, new CourseUtil.CallBack() {
                                                @Override
                                                public void onSuccess(String response) {
                                                    CommonUtil.shortToast(mContext, "退出成功");
                                                    dialog.dismiss();
                                                    clearCoursesCache(study.id);
                                                    notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onError(String response) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create()
                                    .show();
                        } else if (data instanceof Course) {
                            final Course course = (Course) data;
                            AlertDialog.Builder normalCourseBuilder = new AlertDialog.Builder(mContext);
                            normalCourseBuilder.setTitle("确认退出课程")
                                    .setMessage(R.string.delete_course)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dlg, int which) {
                                            CourseUtil.deleteCourse(course.id, new CourseUtil.CallBack() {
                                                @Override
                                                public void onSuccess(String response) {
                                                    CommonUtil.shortToast(mContext, "退出成功");
                                                    if (mNormalCourses.contains(course)) {
                                                        mNormalCourses.remove(course);
                                                        clearCoursesCache(course.id);
                                                    } else if (mLiveCourses.contains(course)) {
                                                        mLiveCourses.remove(course);
                                                    }
                                                    dialog.dismiss();
                                                    notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onError(String response) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create()
                                    .show();
                        }
                    }

                    @Override
                    public void onShareClick(View v, Dialog dialog) {
                        final ShareTool shareTool;
                        if (data instanceof Course) {
                            Course course = (Course) data;
                            shareTool = new ShareTool(mContext
                                    , EdusohoApp.app.host + "/course/" + course.id
                                    , course.title
                                    , course.about.length() > 20 ? course.about.substring(0, 20) : course.about
                                    , course.middlePicture);
                        } else {
//                            Study study = (Study) data;
//                            String about = Html.fromHtml(study.getAbout()).toString();
//                            shareTool = new ShareTool(mContext
//                                    , EdusohoApp.app.host + "/course/" + study.getId()
//                                    , study.getTitle()
//                                    , about.length() > 20 ? about.substring(0, 20) : about
//                                    , study.getMiddlePicture());
                        }
                        new Handler((mContext.getMainLooper())).post(new Runnable() {
                            @Override
                            public void run() {
//                                shareTool.shardCourse();
                            }
                        });
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelClick(View v, Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
            }
        };
    }

    private void clearCoursesCache(int... courseIds) {
        School school = getAppSettingProvider().getCurrentSchool();
        User user = getAppSettingProvider().getCurrentUser();
        new CourseCacheHelper(mContext, school.getDomain(), user.id).clearLocalCacheByCourseId(courseIds);
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
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
