package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;

/**
 * Created by Zhang on 2016/12/8.
 */

public abstract class BaseDetailFragment extends BaseFragment implements View.OnClickListener {

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
    protected TextView mTvTitleFull;
    protected LinearLayout mStudentIconLayout;
    protected TextView mTvReviewNum;
    protected TextView mTvReviewMore;
    protected ListView mLvReview;
    protected String mTeacherId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_course_detail);

        initData();
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
        mTvTitleFull = (TextView) view.findViewById(R.id.tv_title_full);
        initEvent();
        initData();
    }

    protected abstract void refreshView();

    abstract protected void initData();

    protected void initEvent() {
        mTvTitleDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int lineCount = mTvTitleDesc.getLineCount();
                if (lineCount > 2) {
                    mTvTitleDesc.setMaxLines(2);
                    mTvTitleDesc.setEllipsize(TextUtils.TruncateAt.END);
                    mTvTitleFull.setVisibility(View.VISIBLE);
                    mTvTitleFull.setText(getString(R.string.new_font_unfold));
                } else {
                    mTvTitleFull.setVisibility(View.GONE);
                }
            }
        });
        mTvTitleFull.setOnClickListener(this);
        mTvTitleDesc.setOnClickListener(this);
        mStudentMore.setOnClickListener(this);
        mVipLayout.setOnClickListener(this);
        mTvReviewMore.setOnClickListener(this);
        mIvTeacherIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_title_full ||
                v.getId() == R.id.tv_title_desc) {
            if (mTvTitleFull.getVisibility() == View.GONE) {
                return;
            }
            if (mTvTitleFull.getText().equals(getString(R.string.new_font_unfold))) {
                mTvTitleDesc.setMaxLines(-1);
                mTvTitleFull.setText(getString(R.string.new_font_fold));
            } else {
                mTvTitleDesc.setMaxLines(2);
                mTvTitleFull.setText(getString(R.string.new_font_unfold));
            }
        } else if (v.getId() == R.id.vip_rlayout) {
            vipInfo();
        } else if (v.getId() == R.id.tv_review_more) {
            moreReview();
        } else if (v.getId() == R.id.tv_student_more) {
            moreStudent();
        } else if (v.getId() == R.id.iv_teacher_icon) {
            if (mTeacherId != null) {
                final String url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        String.format("main#/userinfo/%s",
                                mTeacherId)
                );
                EdusohoApp.app.mEngine.runNormalPlugin("WebViewActivity"
                        , EdusohoApp.app.mActivity, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
            }
        }
    }

    protected abstract void moreStudent();

    protected abstract void moreReview();

    protected abstract void vipInfo();

}
