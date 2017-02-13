package com.edusoho.kuozhi.v3.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.course.Study;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.fragment.mine.MyStudyFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseCacheHelper;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.view.dialog.MoreDialog;
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
                viewHolder.rLayoutItem.setTag(latestCourse);
                viewHolder.rLayoutItem.setOnClickListener(getLatestCourseViewClickListener());
                viewHolder.tvMore.setTag(latestCourse);
                viewHolder.tvMore.setOnClickListener(getMoreClickListener());
                break;
            case COURSE_TYPE_NORMAL:
                final Course normalCourse = mNormalCourses.get(position);
                ImageLoader.getInstance().displayImage(normalCourse.getLargePicture(), viewHolder.ivPic,
                        EdusohoApp.app.mOptions);
                viewHolder.tvTitle.setText(String.valueOf(normalCourse.title));
                setProgressStr(normalCourse.learnedNum, normalCourse.totalLesson, viewHolder.tvStudyState);
                viewHolder.rLayoutItem.setTag(normalCourse);
                viewHolder.rLayoutItem.setOnClickListener(getCourseViewClickListener());
                viewHolder.tvMore.setTag(normalCourse);
                viewHolder.tvMore.setOnClickListener(getMoreClickListener());
                break;
            case COURSE_TYPE_LIVE:
                final Course liveCourse = mLiveCourses.get(position);
                ImageLoader.getInstance().displayImage(liveCourse.getLargePicture(), viewHolder.ivPic,
                        EdusohoApp.app.mOptions);
                viewHolder.tvTitle.setText(String.valueOf(liveCourse.title));
                setProgressStr(liveCourse.learnedNum, liveCourse.totalLesson, viewHolder.tvStudyState);
                viewHolder.rLayoutItem.setTag(liveCourse);
                viewHolder.rLayoutItem.setOnClickListener(getCourseViewClickListener());
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
                viewHolder.tvMore.setTag(liveCourse);
                viewHolder.tvMore.setOnClickListener(getMoreClickListener());
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

    private View.OnClickListener getLatestCourseViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Study.Resource study = (Study.Resource) v.getTag();
                CoreEngine.create(mContext).runNormalPlugin("CourseActivity"
                        , mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.COURSE_ID, study.getId());
                                startIntent.putExtra(Const.SOURCE, study.getTitle());
                            }
                        });
            }
        };
    }

    private View.OnClickListener getCourseViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Course course = (Course) v.getTag();
                CoreEngine.create(mContext).runNormalPlugin("CourseActivity"
                        , mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.COURSE_ID, course.id);
                                startIntent.putExtra(Const.SOURCE, course.title);
                            }
                        });
            }
        };
    }

    private View.OnClickListener getMoreClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Object data = v.getTag();
                MoreDialog dialog = new MoreDialog(mContext);
                dialog.init("退出课程", new MoreDialog.MoreCallBack() {
                    @Override
                    public void onMoveClick(View v, final Dialog dialog) {
                        AlertDialog.Builder latestCoursebuilder = new AlertDialog.Builder(mContext);
                        if (data instanceof Study.Resource) {
                            final Study.Resource study = (Study.Resource) data;
                            latestCoursebuilder.setTitle("确认退出课程")
                                    .setMessage(R.string.delete_course)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dlg, int which) {
                                            CourseUtil.deleteCourse(study.getId(), new CourseUtil.CallBack() {
                                                @Override
                                                public void onSuccess(String response) {
                                                    CommonUtil.shortToast(mContext, "退出成功");
                                                    mLatestCourses.remove(study);
                                                    dialog.dismiss();
                                                    clearCoursesCache(study.getId());
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
                                    , course.about.length() > 20 ?
                                    course.about.substring(0, 20)
                                    : course.about
                                    , course.middlePicture);
                        } else {
                            Study.Resource study = (Study.Resource) data;
                            shareTool = new ShareTool(mContext
                                    , EdusohoApp.app.host + "/course/" + study.getId()
                                    , study.getTitle()
                                    , study.getAbout().length() > 20 ?
                                    study.getAbout().substring(0, 20)
                                    : study.getAbout()
                                    , study.getMiddlePicture());
                        }
                        new Handler((mContext.getMainLooper())).post(new Runnable() {
                            @Override
                            public void run() {
                                shareTool.shardCourse();
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
