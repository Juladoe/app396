package com.edusoho.kuozhi.clean.module.courseset.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.module.courseset.BaseFinishActivity;
import com.edusoho.kuozhi.clean.module.courseset.dialog.coupons.CouponsDialog;
import com.edusoho.kuozhi.clean.module.courseset.payments.PaymentsActivity;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by DF on 2017/3/25.
 */

public class ConfirmOrderActivity extends BaseFinishActivity
        implements View.OnClickListener, ConfirmOrderContract.View, CouponsDialog.ModifyView{

    private static final String COURSE_SET = "course_set";
    private static final String COURSE_PROJECT = "course_project";
    private static final String MINUS = "minus";

    private Toolbar mToolbar;
    private ImageView mCourseImg;
    private TextView mPlanTitle;
    private TextView mPlanPrice;
    private TextView mPlanFrom;
    private ViewGroup mRlCoupon;
    private TextView mCouponSub;
    private View mPay;
    private TextView mTotal;
    private TextView mOriginal;
    private LoadDialog mProcessDialog;

    private ConfirmOrderContract.Presenter mPresenter;

    private CourseSet mCourseSet;
    private CourseProject mCourseProject;
    private float mTotalPrice;
    private float mPayPrice;
    private OrderInfo mOrderInfo;
    private OrderInfo.Coupon mCoupon;
    private CouponsDialog mCouponsDialog;

    public static void launch(Context context, CourseSet courseSet, CourseProject courseStudyPlan) {
        Intent intent = new Intent(context, ConfirmOrderActivity.class);
        intent.putExtra(COURSE_SET, courseSet);
        intent.putExtra(COURSE_PROJECT, courseStudyPlan);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        showProcessDialog();
        Intent intent = getIntent();
        mCourseSet = (CourseSet) intent.getSerializableExtra(COURSE_SET);
        mCourseProject = (CourseProject) intent.getSerializableExtra(COURSE_PROJECT);
        initView();
        initEvent();
    }

    private void initView() {
        mCourseImg = (ImageView) findViewById(R.id.iv_course_image);
        mPlanTitle = (TextView) findViewById(R.id.tv_title);
        mPlanPrice = (TextView) findViewById(R.id.tv_price);
        mPlanFrom = (TextView) findViewById(R.id.tv_from_course);
        mRlCoupon = (ViewGroup) findViewById(R.id.rl_coupon);
        mCouponSub = (TextView) findViewById(R.id.tv_coupon_subtract);
        mPay = findViewById(R.id.tv_pay);
        mTotal = (TextView) findViewById(R.id.tv_sum);
        mOriginal = (TextView) findViewById(R.id.tv_original);
        mToolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        mPresenter = new ConfirmOrderPresenter(this, mCourseProject.id);
        mPresenter.subscribe();
    }

    private void initEvent() {
        mRlCoupon.setOnClickListener(this);
        mPay.setOnClickListener(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void showView(OrderInfo orderInfo) {
        mOrderInfo = orderInfo;
        mTotalPrice = orderInfo.totalPrice;
        if (orderInfo.availableCoupons != null && orderInfo.availableCoupons.size() != 0) {
            mCoupon = orderInfo.availableCoupons.get(0);
            showCouponPrice();
        } else {
            mTotal.setText(String.format(getString(R.string.order_price_total), mTotalPrice));
        }
        showTopView();
    }

    private void showCouponPrice() {
        float rate = mCoupon.rate;
        mPayPrice = MINUS.equals(mCoupon.type) ? mTotalPrice - rate : mTotalPrice * rate;
        mCouponSub.setText(MINUS.equals(mCoupon.type) ? String.format(getString(R.string.order_price_subtract), rate)
                : String.format(getString(R.string.order_price_discount), rate));
        mRlCoupon.setVisibility(View.VISIBLE);
        mTotal.setText(String.format(getString(R.string.order_price_total), mPayPrice > 0 ? mPayPrice : 0));
        mOriginal.setText(String.format(getString(R.string.yuan_symbol), mTotalPrice));
        mOriginal.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void showTopView() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_course)
                .showImageOnFail(R.drawable.default_course)
                .showImageOnLoading(R.drawable.default_course)
                .build();
        ImageLoader.getInstance().displayImage(mCourseSet.cover.middle, mCourseImg, imageOptions);
        mPlanTitle.setText(mOrderInfo.title);
        mPlanPrice.setText(String.format(getString(R.string.yuan_symbol), mOrderInfo.totalPrice));
        mPlanFrom.setText(mCourseSet.title);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.rl_coupon){
            showCouponDialog();
        }else if(id == R.id.tv_pay) {
            PaymentsActivity.launch(this, mOrderInfo, mPayPrice < 0 ? 0 : mPayPrice, mCoupon == null ? -1
                    : mOrderInfo.availableCoupons.indexOf(mCoupon));
        }
    }

    private void showCouponDialog() {
        if (mCouponsDialog == null) {
            mCouponsDialog = new CouponsDialog();
            mCouponsDialog.setData(mOrderInfo.availableCoupons);
        }
        mCouponsDialog.show(getSupportFragmentManager(), "CouponsDialog");
    }

    @Override
    public void showProcessDialog(boolean isShow) {
        if (isShow) {
            showProcessDialog();
        } else {
            hideProcessDialog();
        }
    }

    protected void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
        }
        mProcessDialog.show();
    }

    protected void hideProcessDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    @Override
    public void setPriceView(int position) {
        if (position == -1) {
            mCoupon = null;
            mCouponSub.setText("");
            mPayPrice = mTotalPrice;
            mTotal.setText(String.format(getString(R.string.order_price_total),
                                mTotalPrice > 0 ? mTotalPrice : 0));
            return;
        }
        mCoupon = mOrderInfo.availableCoupons.get(position);
        float rate = mCoupon.rate;
        mPayPrice = MINUS.equals(mCoupon.type) ? mTotalPrice - rate : mTotalPrice * rate;
        mTotal.setText(String.format(getString(R.string.order_price_total), mPayPrice > 0 ? mPayPrice : 0));
        mCouponSub.setText(MINUS.equals(mCoupon.type) ? String.format(getString(R.string.order_price_subtract), rate)
                : String.format(getString(R.string.order_price_discount), rate));
    }
}
