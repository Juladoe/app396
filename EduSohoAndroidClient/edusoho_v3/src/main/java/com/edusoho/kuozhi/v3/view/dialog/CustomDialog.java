package com.edusoho.kuozhi.v3.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by DF on 2017/3/17.
 */

public class CustomDialog extends Dialog {

    private String mAlreadyPlan;
    private String mPlanComplete;
    private String mCourseDate;
    private int mType;
    private String mAccountBalance;
    private String mOrderAmount;

    public CustomDialog(@NonNull Context context) {
        super(context, R.style.dialog_custom);
    }

    public CustomDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView(mType));
        initView();
        initEvent();
    }

    /**
     * 初始化dialog的类型1:学习进度 2:支付方式 3:优惠券 4:虚拟币 5:输入支付密码
     * @param type
     * @return
     */
    public CustomDialog initType(int type) {
        mType = type;
        return this;
    }

    private int getContentView(int mType){
        switch (mType) {
            case 1:
                return R.layout.dialog_study_progress;
            case 2:
                return R.layout.dialog_pay_way;
            case 3:
                return R.layout.dialog_coupons;
            case 4:
                return R.layout.dialog_virtual_coin;
            case 5:
                break;
        }
        return 0;
    }

    private void initView() {
        switch (mType) {
            case 1:
                TextView tvAlreadyPlan = (TextView) findViewById(R.id.already_plan);
                TextView tvPlanCom = (TextView) findViewById(R.id.plan_complete);
                TextView tvCourseDate = (TextView) findViewById(R.id.course_date);
                tvAlreadyPlan.setText(String.format("%s%s","已经完成计划: ", mAlreadyPlan));
                tvPlanCom.setText(String.format("%s%s", "计划完成任务: ", mPlanComplete));
                tvCourseDate.setText(String.format("%s%s", "课程有效期至: ", mCourseDate));
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.width = getContext().getResources().getDisplayMetrics().widthPixels;
                getWindow().setGravity(Gravity.BOTTOM);
                getWindow().setAttributes(lp);
                TextView tvAccountBalance = (TextView) findViewById(R.id.tv_account_balance);
                TextView tvOrderAmount = (TextView) findViewById(R.id.tv_order_amount);
//                tvAccountBalance.setText(mAccountBalance);
//                tvOrderAmount.setText(mOrderAmount);
                break;
            case 5:
                break;
        }


    }

    private void initEvent() {
        switch (mType) {
            case 1:
                findViewById(R.id.dialog_dismiss).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                break;
            case 2:
                findViewById(R.id.dialog_dismiss).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                setPayOnclick();
                break;
            case 3:
                break;
            case 4:
                findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        // TODO: 2017/3/18
                    }
                });
                break;
            case 5:
                break;
        }

    }

    private void setPayOnclick() {
        findViewById(R.id.rl_zhifubao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/3/17
            }
        });
        findViewById(R.id.rl_vitural_coin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/3/17
            }
        });
    }

    /**
     * 初始化学习进度dialog中的信息
     * @return
     */
    public Dialog initText(String... text) {
        switch (mType) {
            case 1:
                //已完成计划，计划完成任务，课程有效期至
                this.mAlreadyPlan = text[0];
                this.mPlanComplete = text[1];
                this.mCourseDate = text[2];
                break;
            case 4:
                //账户余额和订单金额
                mAccountBalance = text[0];
                mOrderAmount = text[1];
                break;
        }
        return this;
    }

    public interface EventListener {
        void on
    }

}
