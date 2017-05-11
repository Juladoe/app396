package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.MyClassroomAdapter;
import com.edusoho.kuozhi.v3.adapter.MyCourseStudyAdapter;
import com.edusoho.kuozhi.v3.entity.course.CourseProgress;
import com.edusoho.kuozhi.v3.entity.course.LearningClassroom;
import com.edusoho.kuozhi.v3.entity.course.LearningCourse;
import com.edusoho.kuozhi.v3.entity.lesson.Lesson;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.bal.course.CourseResult;
import com.edusoho.kuozhi.v3.model.provider.CourseProvider;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/2/7.
 */

public class MyStudyFragment extends BaseFragment implements MineFragment.RefreshFragment {

    public static final int NORMAL_COURSE = 2;
    public static final int LIVE_COURSE = 3;
    public static final int CLASSROOM = 4;

    private int mCurrent_TYPE = NORMAL_COURSE;

    private SwipeRefreshLayout srlContent;
    private RecyclerView rvContent;

    private MyCourseStudyAdapter mCourseAdapter;
    private MyClassroomAdapter mClassroomAdapter;
    private CourseProvider mCourseProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_my_study);
    }

    @Override
    protected void initView(View view) {
        srlContent = (SwipeRefreshLayout) view.findViewById(R.id.srl_content);
        srlContent.setColorSchemeResources(R.color.primary_color);

        rvContent = (RecyclerView) view.findViewById(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));
        initData();
        loadData();
        srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        ((RadioGroup) view.findViewById(R.id.rg)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_normal_course) {
                    switchType(NORMAL_COURSE);
                } else if (checkedId == R.id.rb_live_course) {
                    switchType(LIVE_COURSE);
                } else {
                    switchType(CLASSROOM);
                }
            }
        });
    }

    private void initData() {
        mCourseProvider = new CourseProvider(mContext);
        mCourseAdapter = new MyCourseStudyAdapter(getActivity());
        mClassroomAdapter = new MyClassroomAdapter(getActivity());
        rvContent.setAdapter(mCourseAdapter);
    }

    private void loadData() {
        switchType(mCurrent_TYPE);
    }

    /**
     * 筛选显示数据类型事件
     * @param type
     */
    private void switchType(int type) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        showLoadingView();
        switch (type) {
            case NORMAL_COURSE:
                MobclickAgent.onEvent(mActivity, "i_study_cores");
                loadNormalCourse();
                break;
            case LIVE_COURSE:
                MobclickAgent.onEvent(mActivity, "i_study_live");
                loadLiveCourse();
                break;
            case CLASSROOM:
                MobclickAgent.onEvent(mActivity, "i_study_classroom");
                loadClassroom();
                break;
        }
        mCurrent_TYPE = type;
    }

    private void loadNormalCourse() {
        mCourseProvider.getLearnCourses().success(new NormalCallback<CourseResult>() {
            @Override
            public void success(CourseResult courseResult) {
                disabledLoadingView();
                List<Course> list = new LinkedList<>(Arrays.asList(courseResult.resources));
                mCourseAdapter.setNormalCourses(list);
                rvContent.setAdapter(mCourseAdapter);
                List<Integer> ids = new ArrayList<>();
                for (Course course : courseResult.resources) {
                    ids.add(course.id);
                }
                if (ids.size() != 0) {
                    getCourseProgresses(ids, NORMAL_COURSE);
                }
                getLiveLesson(NORMAL_COURSE);
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError error) {
                disabledLoadingView();
            }
        });
    }

    private void loadLiveCourse() {
        CourseDetailModel.getLiveCourses(1000, 0, new ResponseCallbackListener<LearningCourse>() {
            @Override
            public void onSuccess(LearningCourse liveCourses) {
                disabledLoadingView();
                mCourseAdapter.setLiveCourses(liveCourses.data);
                rvContent.setAdapter(mCourseAdapter);
                List<Integer> ids = new ArrayList<>();
                for (Course course : liveCourses.data) {
                    ids.add(course.id);
                }
                if (ids.size() != 0) {
                    getCourseProgresses(ids, LIVE_COURSE);
                }
                getLiveLesson(LIVE_COURSE);
            }

            @Override
            public void onFailure(String code, String message) {
                disabledLoadingView();
            }
        });
    }

    private void loadClassroom() {
        CourseDetailModel.getAllUserClassroom(1000, 0, new ResponseCallbackListener<LearningClassroom>() {
            @Override
            public void onSuccess(LearningClassroom data) {
                disabledLoadingView();
                mClassroomAdapter.setClassrooms(data.getData());
                rvContent.setAdapter(mClassroomAdapter);
            }

            @Override
            public void onFailure(String code, String message) {
                disabledLoadingView();
            }
        });
    }

    private void getCourseProgresses(List<Integer> ids, final int type) {
        CourseDetailModel.getCourseProgress(ids, new ResponseCallbackListener<CourseProgress>() {
            @Override
            public void onSuccess(CourseProgress data) {
                MyCourseStudyAdapter myCourseStudyAdapter = (MyCourseStudyAdapter) rvContent.getAdapter();
                switch (type) {
                    case NORMAL_COURSE:
                        List<Course> normalCourses = myCourseStudyAdapter.getNormalCourses();
                        int normalCourseSize = data.resources.size();
                        for (int i = 0; i < normalCourseSize; i++) {
                            CourseProgress.Progress progress = data.resources.get(i);
                            for (int j = 0; j < normalCourses.size(); j++) {
                                if (progress.courseId == normalCourses.get(j).id) {
                                    normalCourses.get(j).learnedNum = progress.learnedNum;
                                    normalCourses.get(j).totalLesson = progress.totalLesson;
                                    break;
                                }
                            }
                        }
                        break;
                    case LIVE_COURSE:
                        List<Course> liveCourses = myCourseStudyAdapter.getLiveCourses();
                        int liveCoursesSize = data.resources.size();
                        for (int i = 0; i < liveCoursesSize; i++) {
                            CourseProgress.Progress progress = data.resources.get(i);
                            for (int j = 0; j < liveCourses.size(); j++) {
                                if (progress.courseId == liveCourses.get(j).id) {
                                    liveCourses.get(j).learnedNum = progress.learnedNum;
                                    liveCourses.get(j).totalLesson = progress.totalLesson;
                                    break;
                                }
                            }
                        }
                        break;
                }
                rvContent.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(String code, String message) {
            }
        });
    }

    private void getLiveLesson(int type) {
        final MyCourseStudyAdapter myCourseStudyAdapter = (MyCourseStudyAdapter) rvContent.getAdapter();
        switch (type) {
            case NORMAL_COURSE:
                List<Course> normalCourses = myCourseStudyAdapter.getNormalCourses();
                final int normalCoursesSize = normalCourses.size();
                for (int i = 0; i < normalCoursesSize; i++) {
                    final Course course = normalCourses.get(i);
                    final int finalI = i;
                    CourseDetailModel.getLiveLesson(course.id, new NormalCallback<List<Lesson>>() {
                        @Override
                        public void success(List<Lesson> lessons) {
                            disabledLoadingView();
                            if (lessons != null) {
                                for (Lesson lesson : lessons) {
                                    long currentTime = System.currentTimeMillis();
                                    if (lesson.startTime * 1000 < currentTime && lesson.endTime * 1000 > currentTime) {
                                        course.liveState = 1;
                                        myCourseStudyAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                if (finalI == normalCoursesSize - 1) {
                                    myCourseStudyAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
                break;
            case LIVE_COURSE:
                List<Course> liveCourses = myCourseStudyAdapter.getLiveCourses();
                final int liveCoursesSize = liveCourses.size();
                for (int i = 0; i < liveCoursesSize; i++) {
                    final Course course = liveCourses.get(i);
                    final int finalI = i;
                    CourseDetailModel.getLiveLesson(course.id, new NormalCallback<List<Lesson>>() {
                        @Override
                        public void success(List<Lesson> lessons) {
                            disabledLoadingView();
                            if (lessons != null) {
                                for (Lesson lesson : lessons) {
                                    long currentTime = System.currentTimeMillis();
                                    if (lesson.startTime * 1000 < currentTime && lesson.endTime * 1000 > currentTime) {
                                        course.liveState = 1;
                                        myCourseStudyAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                if (finalI == liveCoursesSize - 1) {
                                    myCourseStudyAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void refreshData() {
        loadData();
    }

    @Override
    public void setSwipeEnabled(int i) {
        srlContent.setEnabled(i == 0);
    }

    private void showLoadingView() {
        srlContent.post(new Runnable() {
            @Override
            public void run() {
                srlContent.setRefreshing(true);
            }
        });
    }

    private void disabledLoadingView() {
        srlContent.setRefreshing(false);
    }

    public static class CourseStudyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivPic;
        public View layoutLive;
        public TextView tvLiveIcon;
        public TextView tvLive;
        public TextView tvTitle;
        public TextView tvStudyState;
        public TextView tvMore;
        public View layoutClass;
        public TextView tvClassName;
        public View rLayoutItem;

        public CourseStudyViewHolder(View view) {
            super(view);
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            layoutLive = view.findViewById(R.id.layout_live);
            tvLiveIcon = (TextView) view.findViewById(R.id.tv_live_icon);
            tvLive = (TextView) view.findViewById(R.id.tv_live);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvStudyState = (TextView) view.findViewById(R.id.tv_study_state);
            tvMore = (TextView) view.findViewById(R.id.tv_more);
            layoutClass = view.findViewById(R.id.layout_class);
            tvClassName = (TextView) view.findViewById(R.id.tv_class_name);
            rLayoutItem = view.findViewById(R.id.rlayout_item);
        }
    }

    public static class ClassroomViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivPic;
        public TextView tvTitle;
        public TextView tvMore;
        public View rLayoutItem;

        public ClassroomViewHolder(View view) {
            super(view);
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvMore = (TextView) view.findViewById(R.id.tv_more);
            rLayoutItem = view.findViewById(R.id.rlayout_item);
        }
    }
}
