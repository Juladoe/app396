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

public abstract class BaseDetailFragment extends BaseFragment {

    public BaseDetailFragment() {
    }
    protected TextView mTvPriceOld;
    protected TextView mTvPriceNow;
    protected View mPriceLayout;
    protected View mTitleLayout;
    protected TextView mTvTitle;
    protected ReviewStarView mReviewStar;
    protected TextView mTvTitleStudentNum;
    protected TextView mTvTitleDesc;
    protected View mVipLayout;
    protected ImageView mIvVip;
    protected TextView mTvVipDesc;
    protected TextView mTvPeopleDesc;
    protected TextView mTvTeacherName;
    protected TextView mTvTeacherDesc;
    protected TextView mTvStudentNum;
    protected CircleImageView mIvTeacherIcon;
    protected View mStudentMore;
    protected LinearLayout mStudentIconLayout;
    protected TextView mTvReviewNum;
    protected TextView mTvReviewMore;
    protected ListView mLvReview;

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
        mStudentMore = view.findViewById(R.id.tv_student_more);
        mStudentIconLayout = (LinearLayout) view.findViewById(R.id.student_icon_llayout);
        mTvReviewNum = (TextView) view.findViewById(R.id.tv_review_num);
        mTvReviewMore = (TextView) view.findViewById(R.id.tv_review_more);
        mLvReview = (ListView) view.findViewById(R.id.lv_review);
    }

    abstract protected void initData();

    private void initEvent() {

    }
}
