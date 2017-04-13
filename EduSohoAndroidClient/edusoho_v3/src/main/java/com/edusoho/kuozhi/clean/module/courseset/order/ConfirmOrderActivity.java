package com.edusoho.kuozhi.clean.module.courseset.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.OrderInfo;
import com.edusoho.kuozhi.clean.module.courseset.BaseFinishActivity;
import com.edusoho.kuozhi.clean.module.courseset.payment.PaymentsActivity;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by DF on 2017/3/25.
 */

public class ConfirmOrderActivity extends BaseFinishActivity implements View.OnClickListener, ConfirmOrderContract.View{

    public static final String COURSE_SET = "course_set";
    public static final String STUDY_PLAN = "study_plan";

    private View mBack;
    private ImageView mCourseImg;
    private TextView mPlanTitle;
    private TextView mPlanPrice;
    private TextView mPlanFrom;
    private ViewGroup mRlCoupon;
    private TextView mCouponSub;
    private View mPay;
    private TextView mSum;
    private TextView mOriginal;
    private ConfirmOrderContract.Presenter mPresenter;
    private CourseSet mCourseSet;
    private CourseProject mStudyPlan;
    private LoadDialog mProcessDialog;

    public static void launch(Context context, CourseSet courseSet, CourseProject courseStudyPlan) {
        Intent intent = new Intent(context, ConfirmOrderActivity.class);
        intent.putExtra(COURSE_SET, courseSet);
        intent.putExtra(STUDY_PLAN, courseStudyPlan);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        showProcessDialog();
        Intent intent = getIntent();
        mCourseSet = (CourseSet) intent.getSerializableExtra(COURSE_SET);
        mStudyPlan = (CourseProject) intent.getSerializableExtra(STUDY_PLAN);
        initView();
        initEvent();
        showTopView();
    }

    private void initView() {
        mBack = findViewById(R.id.iv_back);
        mCourseImg = (ImageView) findViewById(R.id.iv_course_image);
        mPlanTitle = (TextView) findViewById(R.id.tv_title);
        mPlanPrice = (TextView) findViewById(R.id.tv_price);
        mPlanFrom = (TextView) findViewById(R.id.tv_from_course);
        mRlCoupon = (ViewGroup) findViewById(R.id.rl_coupon);
        mCouponSub = (TextView) findViewById(R.id.tv_coupon_subtract);
        mPay = findViewById(R.id.tv_pay);
        mSum = (TextView) findViewById(R.id.tv_sum);
        mOriginal = (TextView) findViewById(R.id.tv_original);

        mPresenter = new ConfirmOrderPresenter(this, mStudyPlan.id);
        mPresenter.subscribe();
    }

    private void initEvent() {
        mBack.setOnClickListener(this);
        mRlCoupon.setOnClickListener(this);
        mPay.setOnClickListener(this);
    }

    private void showTopView() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_course)
                .showImageOnFail(R.drawable.default_course)
                .showImageOnLoading(R.drawable.default_course)
                .build();
        ImageLoader.getInstance().displayImage(mCourseSet.cover.middle, mCourseImg, imageOptions);
        mPlanTitle.setText(mStudyPlan.title);
        float price = mStudyPlan.price;
        if (price > 0) {
            mPlanPrice.setText(String.format("%s%s", "¥", price));
        } else {
            mPlanPrice.setTextColor(getResources().getColor(R.color.primary));
            mPlanPrice.setText(getString(R.string.free_course_project));
        }
        mPlanFrom.setText(mCourseSet.title);
        mOriginal.setText(String.format("%s%.2f", "¥", price));
        mOriginal.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        }else if(id == R.id.rl_coupon){

        }else if(id == R.id.tv_pay) {
            PaymentsActivity.launch(this, mStudyPlan);
        }
    }

    @Override
    public void showCouponView(List<OrderInfo.AvailableCouponsBean> availableCoupons) {
        mCouponSub.setText(String.format("%s%.2f", "减¥", availableCoupons.get(0).rate));
        mRlCoupon.setVisibility(View.VISIBLE);
        mRlCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
}
