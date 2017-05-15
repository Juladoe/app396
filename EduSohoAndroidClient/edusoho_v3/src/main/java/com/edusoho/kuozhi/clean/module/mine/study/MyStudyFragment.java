package com.edusoho.kuozhi.clean.module.mine.study;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.StudyCourse;
import com.edusoho.kuozhi.clean.bean.innerbean.Study;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.mine.MineFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by JesseHuang on 2017/2/7.
 */

public class MyStudyFragment extends BaseFragment implements MineFragment.RefreshFragment, MyStudyContract.View {

    public static final int NORMAL_COURSE = 2;
    public static final int LIVE_COURSE = 3;
    public static final int CLASSROOM = 4;

    private int mCurrent_TYPE = NORMAL_COURSE;

    private SwipeRefreshLayout srlContent;
    private RecyclerView rvContent;

    private MyCourseStudyAdapter mCourseAdapter;
    private MyClassroomAdapter mClassroomAdapter;

    private MyStudyContract.Presenter mPresenter;

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
        mCourseAdapter = new MyCourseStudyAdapter(getActivity());
        mClassroomAdapter = new MyClassroomAdapter(getActivity());
        rvContent.setAdapter(mCourseAdapter);
        mPresenter = new MyStudyPresenter(this);
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
        srlContent.setRefreshing(true);
        switch (type) {
            case NORMAL_COURSE:
                MobclickAgent.onEvent(mActivity, "i_study_cores");
                mPresenter.getMyStudyCourse();
                break;
            case LIVE_COURSE:
                MobclickAgent.onEvent(mActivity, "i_study_live");
                mPresenter.getMyStudyLiveCourseSet();
                break;
            case CLASSROOM:
                MobclickAgent.onEvent(mActivity, "i_study_classroom");
                mPresenter.getMyStudyClassRoom();
                break;
        }
        mCurrent_TYPE = type;
    }

    @Override
    public void refreshData() {
        loadData();
    }

    @Override
    public void showStudyCourseComplete(List<StudyCourse> studyCourses) {
        mCourseAdapter.setNormalCourses(studyCourses);
        rvContent.setAdapter(mCourseAdapter);
    }

    @Override
    public void showLiveCourseComplete(List<Study> studies) {
        mCourseAdapter.setLiveCourses(studies);
        rvContent.setAdapter(mCourseAdapter);
    }

    @Override
    public void showClassRoomComplete(List<Study> studies) {
        mClassroomAdapter.setClassrooms(studies);
        rvContent.setAdapter(mClassroomAdapter);
    }

    @Override
    public void hideSwp() {
        srlContent.setRefreshing(false);
    }

    @Override
    public void showToast(int resId) {

    }

    @Override
    public void showToast(String msg) {

    }

    public static class CourseStudyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPic;
        View layoutLive;
        View layoutFrom;
        TextView tvFrom;
        TextView tvLiveIcon;
        TextView tvLive;
        TextView tvTitle;
        TextView tvStudyState;
        TextView tvMore;
        TextView tvClassName;
        View rLayoutItem;

        CourseStudyViewHolder(View view) {
            super(view);
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            layoutLive = view.findViewById(R.id.layout_live);
            layoutFrom = view.findViewById(R.id.layout_course_set);
            tvFrom = (TextView) view.findViewById(R.id.tv_course_set_name);
            tvLiveIcon = (TextView) view.findViewById(R.id.tv_live_icon);
            tvLive = (TextView) view.findViewById(R.id.tv_live);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvStudyState = (TextView) view.findViewById(R.id.tv_study_state);
            tvMore = (TextView) view.findViewById(R.id.tv_more);
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
