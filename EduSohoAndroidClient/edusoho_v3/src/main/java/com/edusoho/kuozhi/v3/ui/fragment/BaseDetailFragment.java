package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Zhang on 2016/12/8.
 */

public abstract class BaseDetailFragment extends BaseFragment implements View.OnClickListener {

    public BaseDetailFragment() {
    }

    protected TextView mTvPriceOld;
    protected TextView mTvPrice1;
    protected TextView mTvPriceNow;
    protected View mPriceLayout;
    protected View mTitleLayout;
    protected TextView mTvTitle;
    protected View mVTitleLine;
    protected ReviewStarView mReviewStar;
    protected TextView mTvTitleStudentNum;
    protected HtmlTextView mTvTitleDesc;
    protected View mVipLayout;
    protected ImageView mIvVip;
    protected TextView mTvVipDesc;
    protected TextView mTvPeopleDesc;
    protected TextView mTvPeople1;
    protected TextView mTvTeacherName;
    protected TextView mTvTeacherDesc;
    protected View mTeacherLayout;
    protected TextView mTvStudentNum;
    protected ImageView mIvTeacherIcon;
    protected View mStudentMore;
    protected TextView mTvTitleFull;
    protected LinearLayout mStudentIconLayout;
    protected TextView mTvReviewNum;
    protected TextView mTvReviewMore;
    protected ListView mLvReview;
    protected String mTeacherId;
    protected View mLoadView;
    protected View mPeopleLayout;
    protected View mTvStudentNone;
    protected View mReviewNoneLayout;
    protected TextView mTvStudent1;
    protected TextView mTvReview1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_course_detail);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mTvPriceOld = (TextView) view.findViewById(R.id.tv_price_old);
        mTvPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTvPriceNow = (TextView) view.findViewById(R.id.tv_price_now);
        mTvPrice1 = (TextView) view.findViewById(R.id.tv_price1);
        mVTitleLine = view.findViewById(R.id.v_title_line);
        mPriceLayout = view.findViewById(R.id.price_rlayout);
        mPriceLayout.setFocusable(true);
        mPriceLayout.setFocusableInTouchMode(true);
        mPriceLayout.requestFocus();
        mTitleLayout = view.findViewById(R.id.title_rlayout);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mReviewStar = (ReviewStarView) view.findViewById(R.id.review_star);
        mTvTitleStudentNum = (TextView) view.findViewById(R.id.tv_title_student_num);
        mTvTitleDesc = (HtmlTextView) view.findViewById(R.id.tv_title_desc);
        mVipLayout = view.findViewById(R.id.vip_rlayout);
        mIvVip = (ImageView) view.findViewById(R.id.iv_vip);
        mTvVipDesc = (TextView) view.findViewById(R.id.tv_vip_desc);
        mTvPeopleDesc = (TextView) view.findViewById(R.id.tv_people_desc);
        mTvTeacherName = (TextView) view.findViewById(R.id.tv_teacher_name);
        mTvTeacherDesc = (TextView) view.findViewById(R.id.tv_teacher_desc);
        mTvStudentNum = (TextView) view.findViewById(R.id.tv_student_num);
        mIvTeacherIcon = (ImageView) view.findViewById(R.id.iv_teacher_icon);
        mStudentMore = view.findViewById(R.id.tv_student_more);
        mStudentIconLayout = (LinearLayout) view.findViewById(R.id.student_icon_llayout);
        mTvReviewNum = (TextView) view.findViewById(R.id.tv_review_num);
        mTvReviewMore = (TextView) view.findViewById(R.id.tv_review_more);
        mLvReview = (ListView) view.findViewById(R.id.lv_review);
        mTvTitleFull = (TextView) view.findViewById(R.id.tv_title_full);
        mTvReview1 = (TextView) view.findViewById(R.id.tv_review1);
        mTvStudent1 = (TextView) view.findViewById(R.id.tv_student1);
        mTvPeople1 = (TextView) view.findViewById(R.id.tv_people1);
        mPeopleLayout = view.findViewById(R.id.people_rlayout);
        mTeacherLayout = view.findViewById(R.id.teacher_rlayout);
        mTvStudentNone = view.findViewById(R.id.tv_student_none);
        mReviewNoneLayout = view.findViewById(R.id.layout_review_none);
        mLoadView = view.findViewById(R.id.ll_detail_load);
    }

    protected void refreshView() {
        mTvTitleDesc.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int lineCount = mTvTitleDesc.getLineCount();
                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) mVTitleLine.getLayoutParams();
                if (lineCount > 2) {
                    mTvTitleDesc.setMaxLines(2);
                    mTvTitleDesc.setEllipsize(TextUtils.TruncateAt.END);
                    mTvTitleFull.setVisibility(View.VISIBLE);
                    mTvTitleFull.setText(getString(R.string.new_font_unfold));
                    mTvTitleDesc.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (params != null) {
                        params.setMargins(0, AppUtil.dp2px(mContext, 38),
                                AppUtil.dp2px(mContext, 15), 0);
                        mVTitleLine.setLayoutParams(params);
                    }
                } else {
                    mTvTitleFull.setVisibility(View.GONE);
                    if (params != null) {
                        params.setMargins(0, AppUtil.dp2px(mContext, 25),
                                AppUtil.dp2px(mContext, 15), 0);
                        mVTitleLine.setLayoutParams(params);
                    }
                }
            }
        });
    }

    abstract protected void initData();

    protected void initEvent() {
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
                mTvTitleDesc.setMaxLines(999);
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
                CoreEngine.create(mContext).runNormalPlugin("WebViewActivity"
                        , mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
            }
        }
    }

    protected void setLoadViewStatus(int visibility) {
        mLoadView.setVisibility(visibility);
    }

    protected abstract void moreStudent();

    protected abstract void moreReview();

    protected void vipInfo() {
        if (EdusohoApp.app.loginUser == null) {
            CourseUtil.notLogin();
            return;
        }
        final String url = String.format(
                Const.MOBILE_APP_URL,
                app.schoolHost,
                "main#/viplist"
        );
        CoreEngine.create(mContext).runNormalPlugin("WebViewActivity"
                , mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
    }

}
