package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;

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

    }

    private void loadNormalCourse() {

    }

    private void loadLiveCourse() {

    }

    private void loadClassroom() {

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

            }
        };
    }
}
