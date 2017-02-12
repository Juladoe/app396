package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.MyClassroomAdapter;
import com.edusoho.kuozhi.v3.adapter.MyCourseStudyAdapter;
import com.edusoho.kuozhi.v3.entity.course.LearningClassroom;
import com.edusoho.kuozhi.v3.entity.course.LearningCourse;
import com.edusoho.kuozhi.v3.entity.course.Study;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.bal.course.CourseResult;
import com.edusoho.kuozhi.v3.model.provider.CourseProvider;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;

import java.util.Arrays;

/**
 * Created by JesseHuang on 2017/2/7.
 */

public class MyStudyFragment extends BaseFragment {

    public static final int LATEST_COURSE = 1;
    public static final int NORMAL_COURSE = 2;
    public static final int LIVE_COURSE = 3;
    public static final int CLASSROOM = 4;

    private RecyclerView rvContent;
    private View viewEmpty;
    private View rlayoutFilterType;
    private View llayoutFilterQuestionTypeList;
    private View viewCoverScreen;
    private TextView tvFilterName;
    private EduSohoNewIconView esivFilterArrow;

    private TextView tvLatestCourse;
    private TextView tvNormalCourse;
    private TextView tvLiveCourse;
    private TextView tvClassroom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_my_study);
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void initView(View view) {
        viewEmpty = view.findViewById(R.id.view_empty);
        viewEmpty.setVisibility(View.GONE);

        rvContent = (RecyclerView) view.findViewById(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));

        rlayoutFilterType = view.findViewById(R.id.rlayout_filter_type);
        rlayoutFilterType.setVisibility(View.VISIBLE);
        rlayoutFilterType.setOnClickListener(getShowTypeLayoutClickListener());

        llayoutFilterQuestionTypeList = view.findViewById(R.id.llayout_filter_question_type_list);
        llayoutFilterQuestionTypeList.setVisibility(View.GONE);
        llayoutFilterQuestionTypeList.bringToFront();

        viewCoverScreen = view.findViewById(R.id.view_cover_screen);
        viewCoverScreen.setOnClickListener(getCoverScreenClickListener());

        tvFilterName = (TextView) view.findViewById(R.id.tv_filter_name);

        esivFilterArrow = (EduSohoNewIconView) view.findViewById(R.id.tv_filter_arrow);

        tvLatestCourse = (TextView) view.findViewById(R.id.tv_latest_course);
        tvNormalCourse = (TextView) view.findViewById(R.id.tv_normal_course);
        tvLiveCourse = (TextView) view.findViewById(R.id.tv_live_course);
        tvClassroom = (TextView) view.findViewById(R.id.tv_classroom);
        tvLatestCourse.setOnClickListener(getTypeClickListener());
        tvNormalCourse.setOnClickListener(getTypeClickListener());
        tvLiveCourse.setOnClickListener(getTypeClickListener());
        tvClassroom.setOnClickListener(getTypeClickListener());
    }

    private void initData() {
        switchType(LATEST_COURSE);
    }

    /**
     * 筛选显示数据类型事件
     *
     * @param type
     */
    private void switchType(int type) {
        tvLatestCourse.setTextColor(getResources().getColor(R.color.primary_font_color));
        tvNormalCourse.setTextColor(getResources().getColor(R.color.primary_font_color));
        tvLiveCourse.setTextColor(getResources().getColor(R.color.primary_font_color));
        tvClassroom.setTextColor(getResources().getColor(R.color.primary_font_color));
        switch (type) {
            case LATEST_COURSE:
                loadLatestCourse();
                tvLatestCourse.setTextColor(getResources().getColor(R.color.primary_color));
                tvFilterName.setText(getString(R.string.filter_type_latest));
                break;
            case NORMAL_COURSE:
                loadNormalCourse();
                tvNormalCourse.setTextColor(getResources().getColor(R.color.primary_color));
                tvFilterName.setText(getString(R.string.filter_type_course));
                break;
            case LIVE_COURSE:
                loadLiveCourse();
                tvLiveCourse.setTextColor(getResources().getColor(R.color.primary_color));
                tvFilterName.setText(getString(R.string.filter_type_live));
                break;
            case CLASSROOM:
                loadClassroom();
                tvClassroom.setTextColor(getResources().getColor(R.color.primary_color));
                tvFilterName.setText(getString(R.string.filter_type_classroom));
                break;
        }
        llayoutFilterQuestionTypeList.setVisibility(View.GONE);
        esivFilterArrow.setText(getString(R.string.new_font_unfold));
    }

    private void loadLatestCourse() {
        final MyCourseStudyAdapter adapter = new MyCourseStudyAdapter(mContext);
        rvContent.setAdapter(adapter);
        CourseDetailModel.getStudy(new ResponseCallbackListener<Study>() {
            @Override
            public void onSuccess(Study data) {
                adapter.setLatestCourses(data.getResources());
            }

            @Override
            public void onFailure(String code, String message) {
            }
        });
    }

    private void loadNormalCourse() {
        final MyCourseStudyAdapter adapter = new MyCourseStudyAdapter(mContext);
        rvContent.setAdapter(adapter);
        CourseProvider courseProvider = new CourseProvider(mContext);
        courseProvider.getLearnCourses().success(new NormalCallback<CourseResult>() {
            @Override
            public void success(CourseResult courseResult) {
                adapter.setNormalCourses(Arrays.asList(courseResult.resources));
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {

            }
        });
    }

    private void loadLiveCourse() {
        final MyCourseStudyAdapter adapter = new MyCourseStudyAdapter(mContext);
        rvContent.setAdapter(adapter);
        CourseDetailModel.getLiveCourses(100, 0, new ResponseCallbackListener<LearningCourse>() {
            @Override
            public void onSuccess(LearningCourse data) {
                adapter.setLiveCourses(data.data);
            }

            @Override
            public void onFailure(String code, String message) {
            }
        });
    }

    private void loadClassroom() {
        final MyClassroomAdapter adapter = new MyClassroomAdapter(mContext);
        rvContent.setAdapter(adapter);
        CourseDetailModel.getAllUserClassroom(100, 0, new ResponseCallbackListener<LearningClassroom>() {
            @Override
            public void onSuccess(LearningClassroom data) {
                adapter.setClassrooms(data.getData());
            }

            @Override
            public void onFailure(String code, String message) {
            }
        });
    }

    private View.OnClickListener getTypeClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_latest_course) {
                    switchType(LATEST_COURSE);
                } else if (v.getId() == R.id.tv_normal_course) {
                    switchType(NORMAL_COURSE);
                } else if (v.getId() == R.id.tv_live_course) {
                    switchType(LIVE_COURSE);
                } else {
                    switchType(CLASSROOM);
                }

            }
        };
    }

    private View.OnClickListener getShowTypeLayoutClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llayoutFilterQuestionTypeList.getVisibility() == View.VISIBLE) {
                    llayoutFilterQuestionTypeList.setVisibility(View.GONE);
                    esivFilterArrow.setText(getString(R.string.new_font_unfold));
                } else {
                    llayoutFilterQuestionTypeList.setVisibility(View.VISIBLE);
                    esivFilterArrow.setText(getString(R.string.new_font_fold));
                }
            }
        };
    }

    private View.OnClickListener getCoverScreenClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llayoutFilterQuestionTypeList.setVisibility(View.GONE);
                esivFilterArrow.setText(getString(R.string.new_font_unfold));
            }
        };
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
        public View vLine;

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
            vLine = view.findViewById(R.id.v_line);
        }
    }

    public static class ClassroomViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivPic;
        public TextView tvTitle;
        public TextView tvMore;

        public ClassroomViewHolder(View view) {
            super(view);
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvMore = (TextView) view.findViewById(R.id.tv_more);
        }
    }
}
