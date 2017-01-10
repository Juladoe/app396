package com.edusoho.kuozhi.v3.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.CourseProgress;
import com.edusoho.kuozhi.v3.entity.course.LearningClassroom;
import com.edusoho.kuozhi.v3.entity.course.LearningCourse;
import com.edusoho.kuozhi.v3.entity.course.Study;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.ClassroomActivity;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.MyTabFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.view.dialog.MoreDialog;
import com.edusoho.kuozhi.v3.view.dialog.SureDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remilia on 2017/1/5.
 */
public class MyStudyAdapter extends BaseAdapter {

    private Context mContext;
    private int type = 0;
    private List<Object> mLists = new ArrayList<>();
    private int mPage = 0;
    private boolean mCanLoad = true;

    public MyStudyAdapter(Context context, int type) {
        this.mContext = context;
        this.type = type;
        initData();
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1 && mCanLoad) {
            mPage++;
            addData();
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_study, null, false);
            viewHolder = new ViewHolder();
            viewHolder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
            viewHolder.layoutLive = convertView.findViewById(R.id.layout_live);
            viewHolder.tvLiveIcon = (TextView) convertView.findViewById(R.id.tv_live_icon);
            viewHolder.tvLive = (TextView) convertView.findViewById(R.id.tv_live);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvStudyState = (TextView) convertView.findViewById(R.id.tv_study_state);
            viewHolder.tvMore = (TextView) convertView.findViewById(R.id.tv_more);
            viewHolder.layoutClass = convertView.findViewById(R.id.layout_class);
            viewHolder.tvClassName = (TextView) convertView.findViewById(R.id.tv_class_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.layoutClass.setVisibility(View.GONE);
        viewHolder.layoutLive.setVisibility(View.GONE);
        Object object = mLists.get(position);
        switch (type) {
            case 0:
                if (object instanceof Study.Resource) {
                    Study.Resource study = (Study.Resource) object;
                    switch (study.getJoinedType()) {
                        case "classroom":
                            ImageLoader.getInstance().displayImage(study.getLargePicture()
                                    , viewHolder.ivPic, EdusohoApp.app.mOptions);
                            viewHolder.tvTitle.setText(String.valueOf(study.getTitle()));
                            if (study.getClassroomTitle() != null &&
                                    study.getClassroomTitle().length() > 0) {
                                viewHolder.layoutClass.setVisibility(View.VISIBLE);
                                viewHolder.tvClassName.setText(study.getClassroomTitle());
                            }
                            getProgress(Integer.parseInt(study.getId()), viewHolder.tvStudyState);
                            break;
                        case "course":
                            ImageLoader.getInstance().displayImage(study.getLargePicture()
                                    , viewHolder.ivPic, EdusohoApp.app.mOptions);
                            viewHolder.tvTitle.setText(String.valueOf(study.getTitle()));
                            if (study.getClassroomTitle() != null &&
                                    study.getClassroomTitle().length() > 0) {
                                viewHolder.layoutClass.setVisibility(View.VISIBLE);
                                viewHolder.tvClassName.setText(study.getClassroomTitle());
                            }
                            getProgress(Integer.parseInt(study.getId()), viewHolder.tvStudyState);
                            break;
                    }
                }
                break;
            case 1:
                if (object instanceof Course) {
                    Course course = (Course) object;
                    ImageLoader.getInstance().displayImage(course.largePicture, viewHolder.ivPic,
                            EdusohoApp.app.mOptions);
                    viewHolder.tvTitle.setText(String.valueOf(course.title));
                    getProgress(course.id, viewHolder.tvStudyState);
                }
                break;
            case 2:
                if (object instanceof Course) {
                    Course course = (Course) object;
                    ImageLoader.getInstance().displayImage(course.largePicture, viewHolder.ivPic,
                            EdusohoApp.app.mOptions);
                    viewHolder.tvTitle.setText(String.valueOf(course.title));
                    getProgress(course.id, viewHolder.tvStudyState);
                }
                break;
            case 3:
                if (object instanceof Classroom) {
                    Classroom classroom = (Classroom) object;
                    viewHolder.tvTitle.setText(String.valueOf(classroom.title));
                    ImageLoader.getInstance().displayImage(classroom.largePicture, viewHolder.ivPic,
                            EdusohoApp.app.mOptions);
                }
                break;
        }
        convertView.setTag(R.id.tv_title, position);
        convertView.setOnClickListener(mViewOnClickListener);
        viewHolder.tvMore.setTag(position);
        viewHolder.tvMore.setOnClickListener(mOnClickListener);
        return convertView;
    }

    private View.OnClickListener mViewOnClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag(R.id.tv_title);
                    Object object = mLists.get(position);
                    if (object instanceof Classroom) {
                        final Classroom classroom = (Classroom) object;
                        EdusohoApp.app.mEngine.runNormalPlugin("ClassroomActivity", mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(ClassroomActivity.CLASSROOM_ID, classroom.id);
                            }
                        });
                    } else if (object instanceof Course) {
                        final Course course = (Course) object;
                        EdusohoApp.app.mEngine.runNormalPlugin("CourseActivity"
                                , mContext, new PluginRunCallback() {
                                    @Override
                                    public void setIntentDate(Intent startIntent) {
                                        startIntent.putExtra(CourseActivity.COURSE_ID, course.id + "");
                                    }
                                });
                    } else {
                        final Study.Resource study = (Study.Resource) object;
                        EdusohoApp.app.mEngine.runNormalPlugin("CourseActivity"
                                , mContext, new PluginRunCallback() {
                                    @Override
                                    public void setIntentDate(Intent startIntent) {
                                        startIntent.putExtra(CourseActivity.COURSE_ID, study.getId() + "");
                                    }
                                });
                    }
                }
            };

    private View.OnClickListener mOnClickListener
            = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            final Object object = mLists.get(position);
            MoreDialog dialog = new MoreDialog(mContext);
            dialog.init("退出课程", new MoreDialog.MoreCallBack() {
                @Override
                public void onMoveClick(View v, final Dialog dialog) {
                    if (object instanceof Classroom) {
                        final Classroom classroom = (Classroom) object;
                        new SureDialog(mContext).init("是否退出班级?",
                                new SureDialog.CallBack() {
                                    @Override
                                    public void onSureClick(View v,final Dialog dialog2) {
                                        CourseUtil.deleteClassroom(classroom.id, new CourseUtil.CallBack() {
                                            @Override
                                            public void onSuccee(String response) {
                                                CommonUtil.shortToast(mContext, "退出成功");
                                                mLists.remove(object);
                                                notifyDataSetChanged();
                                                dialog2.dismiss();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onError(String response) {
                                                dialog2.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelClick(View v, Dialog dialog2) {
                                        dialog2.dismiss();
                                    }
                                }).show();
                    } else if (object instanceof Course) {
                        final Course course = (Course) object;
                        new SureDialog(mContext).init("是否退出课程?",
                                new SureDialog.CallBack() {
                                    @Override
                                    public void onSureClick(View v,final Dialog dialog2) {
                                        CourseUtil.deleteCourse(course.id, new CourseUtil.CallBack() {
                                            @Override
                                            public void onSuccee(String response) {
                                                CommonUtil.shortToast(mContext, "退出成功");
                                                mLists.remove(object);
                                                notifyDataSetChanged();
                                                dialog2.dismiss();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onError(String response) {
                                                dialog2.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelClick(View v, Dialog dialog2) {
                                        dialog2.dismiss();
                                    }
                                }).show();
                    } else {
                        final Study.Resource study = (Study.Resource) object;
                        new SureDialog(mContext).init("是否退出课程?",
                                new SureDialog.CallBack() {
                                    @Override
                                    public void onSureClick(View v,final Dialog dialog2) {
                                        CourseUtil.deleteCourse(Integer.parseInt(study.getId()), new CourseUtil.CallBack() {
                                            @Override
                                            public void onSuccee(String response) {
                                                CommonUtil.shortToast(mContext, "退出成功");
                                                mLists.remove(object);
                                                notifyDataSetChanged();
                                                dialog2.dismiss();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onError(String response) {
                                                dialog2.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelClick(View v, Dialog dialog2) {
                                        dialog2.dismiss();
                                    }
                                }).show();

                    }
                }

                @Override
                public void onShareClick(View v, Dialog dialog) {
                    final ShareTool shareTool;
                    if (object instanceof Course) {
                        Course course = (Course) object;
                        shareTool = new ShareTool(mContext
                                , EdusohoApp.app.host + "/course/" + course.id
                                , course.title
                                , course.about.length() > 20 ?
                                course.about.substring(0, 20)
                                : course.about
                                , course.middlePicture);
                    } else {
                        if (object instanceof Classroom) {
                            Classroom classroom = (Classroom) object;
                            shareTool = new ShareTool(mContext
                                    , EdusohoApp.app.host + "/classroom/" + classroom.id
                                    , classroom.title
                                    , classroom.about.toString().length() > 20 ?
                                    classroom.about.toString().substring(0, 20)
                                    : classroom.about.toString()
                                    , classroom.largePicture);
                        } else {
                            Study.Resource study = (Study.Resource) object;
                            shareTool = new ShareTool(mContext
                                    , EdusohoApp.app.host + "/course/" + study.getId()
                                    , study.getTitle()
                                    , study.getAbout().length() > 20 ?
                                    study.getAbout().substring(0, 20)
                                    : study.getAbout()
                                    , study.getMiddlePicture());
                        }
                    }
                    new Handler((mContext.getMainLooper())).post(new Runnable() {
                        @Override
                        public void run() {
                            shareTool.shardCourse();
                        }
                    });
                }

                @Override
                public void onCancelClick(View v, Dialog dialog) {
                    dialog.dismiss();
                }
            }).show();
        }
    };


    private static ViewHolder viewHolder;

    private class ViewHolder {
        ImageView ivPic;
        View layoutLive;
        TextView tvLiveIcon;
        TextView tvLive;
        TextView tvTitle;
        TextView tvStudyState;
        TextView tvMore;
        View layoutClass;
        TextView tvClassName;
    }

    public void initData() {
        mPage = 0;
        mLists.clear();
        notifyDataSetChanged();
        switch (type) {
            case 0:
                CourseDetailModel.getStudy(new ResponseCallbackListener<Study>() {
                    @Override
                    public void onSuccess(Study data) {
                        mLists.clear();
                        mLists.addAll(data.getResources());
                        mCanLoad = false;
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String code, String message) {

                    }
                });
                break;
            case 1:
                CourseDetailModel.getAllUserCourses(10, mPage * 10, new ResponseCallbackListener<LearningCourse>() {
                    @Override
                    public void onSuccess(LearningCourse data) {
                        mLists.clear();
                        mLists.addAll(data.getData());
                        if (data.getData().size() < 10) {
                            mCanLoad = false;
                        } else {
                            mCanLoad = true;
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String code, String message) {
                    }
                });
                break;
            case 2:
                CourseDetailModel.getLiveCourses(10, mPage * 10, new ResponseCallbackListener<LearningCourse>() {
                    @Override
                    public void onSuccess(LearningCourse data) {
                        mLists.clear();
                        mLists.addAll(data.getData());
                        if (data.getData().size() < 10) {
                            mCanLoad = false;
                        } else {
                            mCanLoad = true;
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String code, String message) {
                    }
                });
                break;
            case 3:
                CourseDetailModel.getAllUserClassroom(10, mPage * 10, new ResponseCallbackListener<LearningClassroom>() {
                    @Override
                    public void onSuccess(LearningClassroom data) {
                        mLists.clear();
                        mLists.addAll(data.getData());
                        if (data.getData().size() < 10) {
                            mCanLoad = false;
                        } else {
                            mCanLoad = true;
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String code, String message) {
                    }
                });

                break;
        }
    }

    private void addData() {

        switch (type) {
            case 0:
                break;
            case 1:
                CourseDetailModel.getAllUserCourses(10, mPage * 10, new ResponseCallbackListener<LearningCourse>() {
                    @Override
                    public void onSuccess(LearningCourse data) {
                        if (data.getData().size() > 0 && (mLists.size() == 0 || mLists.get(0).getClass()
                                .equals(data.getData().get(0).getClass()))) {
                            mLists.addAll(data.getData());
                        }
                        if (data.getData().size() < 10) {
                            mCanLoad = false;
                        } else {
                            mCanLoad = true;
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String code, String message) {

                    }
                });
                break;
            case 2:
                CourseDetailModel.getLiveCourses(10, mPage * 10, new ResponseCallbackListener<LearningCourse>() {
                    @Override
                    public void onSuccess(LearningCourse data) {
                        if (data.getData().size() > 0 && (mLists.size() == 0 || mLists.get(0).getClass()
                                .equals(data.getData().get(0).getClass()))) {
                            mLists.addAll(data.getData());
                        }
                        if (data.getData().size() < 10) {
                            mCanLoad = false;
                        } else {
                            mCanLoad = true;
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String code, String message) {

                    }
                });
                break;
            case 3:
                CourseDetailModel.getAllUserClassroom(10, mPage * 10, new ResponseCallbackListener<LearningClassroom>() {
                    @Override
                    public void onSuccess(LearningClassroom data) {
                        if (data.getData().size() > 0 && (mLists.size() == 0 || mLists.get(0).getClass()
                                .equals(data.getData().get(0).getClass()))) {
                            mLists.addAll(data.getData());
                        }
                        if (data.getData().size() < 10) {
                            mCanLoad = false;
                        } else {
                            mCanLoad = true;
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String code, String message) {

                    }
                });
                break;
        }
    }

    public void setType(int type) {
        this.type = type;
        initData();
    }

    private void getProgress(int courseId, final TextView view) {
        view.setTag(courseId);
        CourseDetailModel.getCourseProgress(courseId, new ResponseCallbackListener<CourseProgress>() {
            @Override
            public void onSuccess(CourseProgress data) {
                int id = (int) view.getTag();
                CourseProgress.Progress progress = data.resources.get(0);
                if (progress != null && id == progress.courseId) {
                    if (progress.learnedNum == 0) {
                        view.setText("未开始学习");
                        view.setTextColor(Color.parseColor("#71777d"));
                    } else {
                        view.setText(String.format("已学习%s/%s课"
                                , progress.learnedNum, progress.totalLesson));
                        view.setTextColor(Color.parseColor("#03c777"));
                    }
                }
            }

            @Override
            public void onFailure(String code, String message) {

            }
        });
    }

}
