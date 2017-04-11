package com.edusoho.kuozhi.clean.module.courseset.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.courseset.payment.PayWayActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by DF on 2017/3/25.
 */

public class ConfirmOrderActivity extends AppCompatActivity implements View.OnClickListener, ConfirmOrderContract.View{

    public static final String COURSEIMG = "course_img";
    public static final String PLANFROM = "from_course";
    public static final String PLANPRICE = "plan_price";
    public static final String PLANTITLE = "plan_title";
    public static final String PLANID = "plan_id";

    private View mBack;
    private ImageView mCourseImg;
    private TextView mPlanTitle;
    private TextView mPlanPrice;
    private TextView mPlanFrom;
    private ViewGroup mRlDiscount;
    private TextView mDiscount;
    private TextView mDiscountSub;
    private View mPay;
    private TextView mSum;
    private TextView mOriginal;
    private ConfirmOrderContract.Presenter mPresenter;
    private int mPlanId;

    public static void newInstance(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ConfirmOrderActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        mPlanId = getIntent().getExtras().getInt(PLANID);
        initView();
        initEvent();
        showView();
    }

    private void initView() {
        mBack = findViewById(R.id.iv_back);
        mCourseImg = (ImageView) findViewById(R.id.iv_course_image);
        mPlanTitle = (TextView) findViewById(R.id.tv_title);
        mPlanPrice = (TextView) findViewById(R.id.tv_price);
        mPlanFrom = (TextView) findViewById(R.id.tv_from_course);
        mRlDiscount = (ViewGroup) findViewById(R.id.rl_discount);
        mDiscount = (TextView) findViewById(R.id.iv_discount);
        mDiscountSub = (TextView) findViewById(R.id.tv_discount_subtract);
        mPay = findViewById(R.id.tv_pay);
        mSum = (TextView) findViewById(R.id.tv_sum);
        mOriginal = (TextView) findViewById(R.id.tv_original);

        mPresenter = new ConfirmOrderPresenter(this, mPlanId);
        mPresenter.subscribe();
    }

    private void initEvent() {
        mBack.setOnClickListener(this);
        mRlDiscount.setOnClickListener(this);
        mPay.setOnClickListener(this);
    }

    private void showView() {
        Bundle bundle = getIntent().getExtras();
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_course)
                .showImageOnFail(R.drawable.default_course)
                .showImageOnLoading(R.drawable.default_course)
                .build();
        ImageLoader.getInstance().displayImage(bundle.getString(COURSEIMG), mCourseImg, imageOptions);
        mPlanTitle.setText(bundle.getString(PLANTITLE));
        float price = bundle.getFloat(PLANPRICE);
        if (price > 0) {
            mPlanPrice.setText(String.format("%s%s", "¥", price));
        } else {
            mPlanPrice.setTextColor(getResources().getColor(R.color.primary));
            mPlanPrice.setText(getString(R.string.free_course_project));
        }
        mPlanFrom.setText(bundle.getString(PLANFROM));
        mOriginal.setText(String.format("%s%s", "¥", price));
        mOriginal.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        }else if(id == R.id.rl_discount){

        }else if(id == R.id.tv_pay) {
            PayWayActivity.newInstance(this, mPlanId);
        }
    }
}
