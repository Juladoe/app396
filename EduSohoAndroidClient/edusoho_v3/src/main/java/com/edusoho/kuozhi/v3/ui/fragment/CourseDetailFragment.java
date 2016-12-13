package com.edusoho.kuozhi.v3.ui.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;

/**
 * Created by Zhang on 2016/12/8.
 */

public class CourseDetailFragment extends BaseFragment {

    private String mCourseId;

    public CourseDetailFragment() {
    }

    public CourseDetailFragment(String courseId) {
        this.mCourseId = courseId;
    }

    public void setCourseId(String courseId) {
        this.mCourseId = courseId;
    }

    private TextView mTvPriceOld;
    private TextView mTvPriceNow;
    private View mPriceLayout;
    private View mTitleLayout;
    private TextView mTvTitle;
    private ReviewStarView mReviewStar;
    private TextView mTvTitleStudentNum;
    private TextView mTvTitleDesc;
    private View mVipLayout;
    private ImageView mIvVip;
    private TextView mTvVipDesc;
    private TextView mTvPeopleDesc;
    private TextView mTvTeacherName;
    private TextView mTvTeacherDesc;
    private TextView mTvStudentNum;
    private CircleImageView mIvTeacherIcon;
    private View mStudentMore;
    private LinearLayout mStudentIconLayout;
    private TextView mTvReviewNum;
    private TextView mTvReviewMore;
    private ListView mLvReview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_course_detail);

        initData();
        initEvent();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mTvPriceOld = (TextView) view.findViewById(R.id.tv_price_old);
        mTvPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTvPriceNow = (TextView) view.findViewById(R.id.tv_price_now);
        mPriceLayout = view.findViewById(R.id.tv_price_now);
        mTitleLayout = view.findViewById(R.id.title_rlayout);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mReviewStar = (ReviewStarView) view.findViewById(R.id.review_star);
        mTvTitleStudentNum = (TextView) view.findViewById(R.id.tv_title_student_num);
        mTvTitleDesc = (TextView) view.findViewById(R.id.tv_title_desc);
        mVipLayout = view.findViewById(R.id.vip_rlayout);
        mIvVip = (ImageView) view.findViewById(R.id.iv_vip);
        mTvVipDesc = (TextView) view.findViewById(R.id.tv_vip_desc);
        mTvPeopleDesc = (TextView) view.findViewById(R.id.tv_people_desc);
        mTvTeacherName = (TextView) view.findViewById(R.id.tv_teacher_name);
        mTvTeacherDesc = (TextView) view.findViewById(R.id.tv_teacher_desc);
        mTvStudentNum = (TextView) view.findViewById(R.id.tv_student_num);
        mIvTeacherIcon = (CircleImageView) view.findViewById(R.id.iv_teacher_icon);
        mStudentMore = view.findViewById(R.id.iv_student_more);
        mStudentIconLayout = (LinearLayout) view.findViewById(R.id.student_icon_llayout);
        mTvReviewNum = (TextView) view.findViewById(R.id.tv_review_num);
        mTvReviewMore = (TextView) view.findViewById(R.id.tv_review_more);
        mLvReview = (ListView) view.findViewById(R.id.lv_review);
    }

    private void initData() {

    }

    private void initEvent() {

    }
}
