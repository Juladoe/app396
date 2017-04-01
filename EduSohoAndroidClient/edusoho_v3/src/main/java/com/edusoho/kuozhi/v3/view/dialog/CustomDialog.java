package com.edusoho.kuozhi.v3.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.courseset.GuaranteServiceAdapter;
import com.edusoho.kuozhi.v3.util.AppUtil;

/**
 * Created by DF on 2017/3/17.
 */

public class CustomDialog extends Dialog {

    private Context mContext;
    private String mAlreadyPlan;
    private String mPlanComplete;
    private String mCourseDate;
    private int mType;
    private String mAccountBalance;
    private String mOrderAmount;

    private RadioButton mRb;

    public CustomDialog(@NonNull Context context) {
        super(context, R.style.dialog_custom);
        this.mContext = context;
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
     * 初始化dialog的类型1:学习进度 2:支付方式 3:优惠券 4:虚拟币 5:输入支付密码 6:确认加入课程 7:承诺服务
     *
     * @param type
     * @return
     */
    public CustomDialog initType(int type) {
        mType = type;
        return this;
    }

    private int getContentView(int mType) {
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
            case 6:
                return R.layout.dialog_confirm_buy;
            case 7:
                return R.layout.dialog_guaranteed_service;
        }
        return 0;
    }

    private void initView() {
        switch (mType) {
            case 1:
                TextView tvAlreadyPlan = (TextView) findViewById(R.id.already_plan);
                TextView tvPlanCom = (TextView) findViewById(R.id.plan_complete);
                TextView tvCourseDate = (TextView) findViewById(R.id.course_date);
                tvAlreadyPlan.setText(String.format("%s%s", "已经完成计划: ", mAlreadyPlan));
                tvPlanCom.setText(String.format("%s%s", "计划完成任务: ", mPlanComplete));
                tvCourseDate.setText(String.format("%s%s", "课程有效期至: ", mCourseDate));
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                setPositionBottom();
                TextView tvAccountBalance = (TextView) findViewById(R.id.tv_account_balance);
                TextView tvOrderAmount = (TextView) findViewById(R.id.tv_order_amount);
//                tvAccountBalance.setText(mAccountBalance);
//                tvOrderAmount.setText(mOrderAmount);
                break;
            case 5:
                break;
            case 6:
                setPositionBottom();
//                ((TextView) findViewById(R.id.tv_discount_price)).setText("");
//                ((TextView) findViewById(R.id.tv_original_price)).setText("");
//                ((TextView) findViewById(R.id.tv_service)).setText("");
//                ((TextView) findViewById(R.id.tv_way)).setText("");
//                ((TextView) findViewById(R.id.tv_validity)).setText("");
//                ((TextView) findViewById(R.id.tv_task)).setText("");
                findViewById(R.id.discount).setVisibility(View.VISIBLE);
                RadioGroup rg = (RadioGroup) findViewById(R.id.rg_type);
                addButton(rg);
                break;
            case 7:
                setPositionBottom();
                RecyclerView rv = (RecyclerView) findViewById(R.id.rv_content);
                GuaranteServiceAdapter guaranteServiceAdapter = new GuaranteServiceAdapter();
                rv.setLayoutManager(new LinearLayoutManager(getContext()));
                rv.setAdapter(guaranteServiceAdapter);
                break;
        }
    }

    /**
     * 动态添加RadioButton到RadioGroup中
     * @param rg
     */
    private void addButton(RadioGroup rg) {
        for (int i = 0; i < 3; i++) {
            mRb = new RadioButton(mContext);
            mRb.setGravity(Gravity.CENTER);
            RadioGroup.LayoutParams mp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mp.setMargins(0, 0, AppUtil.dp2px(mContext, 10), AppUtil.dp2px(mContext, 5));
            mRb.setTextSize(AppUtil.sp2px(mContext, 3));
            mRb.setTextColor(mContext.getResources().getColorStateList(R.color.teach_type_text_selector));
            mRb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            mRb.setPadding(AppUtil.dp2px(mContext, 7), AppUtil.dp2px(mContext, 4)
                    , AppUtil.dp2px(mContext, 7), AppUtil.dp2px(mContext, 4));
            mRb.setBackground(ContextCompat.getDrawable(mContext, R.drawable.teach_type_rb_selector));
            if (i % 2 == 0) {
                mRb.setText("第" + i + "个");
            } else {
                mRb.setText("下车好啊记得啦时间");
            }
            rg.addView(mRb, mp);
        }
    }

    private void setPositionBottom() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = getContext().getResources().getDisplayMetrics().widthPixels;
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setAttributes(lp);
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
//                setPayOnclick();
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
            case 6:
                findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
        }

    }

    private void setPayOnClick() {
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
     *
     * @return
     */
    public Dialog initData(String... text) {
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

    }

}
