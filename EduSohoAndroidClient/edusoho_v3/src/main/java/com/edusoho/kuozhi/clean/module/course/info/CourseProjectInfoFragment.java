package com.edusoho.kuozhi.clean.module.course.info;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;
import com.wefika.flowlayout.FlowLayout;

import java.util.Locale;

/**
 * Created by JesseHuang on 2017/3/26.
 * 教学计划简介
 */

public class CourseProjectInfoFragment extends Fragment implements CourseProjectInfoContract.View, CourseProjectFragmentListener {

    private static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    private static final String FREE = "0.00";
    private CourseProjectInfoContract.Presenter mPresenter;

    private FlowLayout mPromise;
    private TextView mTitle;
    private TextView mStudentNum;
    private RatingBar mCourseRate;
    private TextView mSalePrice;
    private TextView mOriginalPrice;
    private ImageView mVipIcon;
    private TextView mSaleWord;
    private View mVipLine;
    private View mVipLayout;
    private TextView mVipText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_project_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPromise = (FlowLayout) view.findViewById(R.id.fl_promise_layout);
        mTitle = (TextView) view.findViewById(R.id.tv_course_project_title);
        mStudentNum = (TextView) view.findViewById(R.id.tv_student_num);
        mCourseRate = (RatingBar) view.findViewById(R.id.rb_course_rate);
        mSalePrice = (TextView) view.findViewById(R.id.tv_sale_price);
        mOriginalPrice = (TextView) view.findViewById(R.id.tv_original_price);
        mVipIcon = (ImageView) view.findViewById(R.id.iv_vip_icon);
        mSaleWord = (TextView) view.findViewById(R.id.tv_sale_word);
        mVipLine = view.findViewById(R.id.v_vip_line);
        mVipLayout = view.findViewById(R.id.rl_vip_layout);
        mVipText = (TextView) view.findViewById(R.id.tv_vip_text);


//        String[] str = {"24小时作业批阅", "24小时阅卷点评", "提问必答", "24小时作业11111批阅"};
//
//        for (String s : str) {
//            TextView tv = new TextView(getActivity());
//            tv.setTextColor(Color.BLACK);
//            tv.setText(s);
//            tv.setTextSize(20);
//            FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            lp.rightMargin = 20;
//            tv.setLayoutParams(lp);
//            Log.d("MainActivity", tv.getMeasuredWidth() + "");
//            promiseFlowLayout.addView(tv);
//        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        CourseProject courseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
        mPresenter = new CourseProjectInfoPresenter(courseProject, this);
        mPresenter.subscribe();
    }

    @Override
    public void showCourseProjectInfo(CourseProject courseProject) {
        mTitle.setText(courseProject.title);
        mStudentNum.setText(String.format(Locale.CHINA, "%d" + getString(R.string.student_num), courseProject.studentNum));
        mCourseRate.setRating(Float.valueOf(courseProject.rating));
    }

    @Override
    public void showPrice(CourseProjectPriceEnum type, String price, String originPrice) {
        switch (type) {
            case FREE:
                mOriginalPrice.setText(R.string.free_course_project);
                mOriginalPrice.setTextColor(getResources().getColor(R.color.primary_color));
                mSalePrice.setVisibility(View.GONE);
                mVipIcon.setVisibility(View.GONE);
                break;
            case ORIGINAL:
                mOriginalPrice.setText(String.format(Locale.CHINA, "￥%s", originPrice));
                mOriginalPrice.setTextColor(getResources().getColor(R.color.secondary_color));
                mSalePrice.setVisibility(View.GONE);
                mVipIcon.setVisibility(View.GONE);
                break;
            case SALE:
                mSalePrice.setText(String.format(Locale.CHINA, "￥%s", price));
                mOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                mOriginalPrice.setText(String.format(Locale.CHINA, "￥%s", originPrice));
                mOriginalPrice.setText(originPrice);
                mSaleWord.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void showVipAdvertising(String vipName) {
        mVipLine.setVisibility(View.VISIBLE);
        mVipLayout.setVisibility(View.VISIBLE);
        mVipText.setText(String.format(getString(R.string.join_vip), vipName));
    }

    public CourseProjectFragmentListener newInstance(CourseProject courseProject) {
        CourseProjectInfoFragment fragment = new CourseProjectInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COURSE_PROJECT_MODEL, courseProject);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }

    @Override
    public void setPresenter(CourseProjectInfoContract.Presenter presenter) {

    }
}
