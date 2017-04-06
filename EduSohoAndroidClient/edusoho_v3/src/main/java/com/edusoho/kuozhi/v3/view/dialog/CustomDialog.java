package com.edusoho.kuozhi.v3.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.courseset.GuaranteServiceAdapter;
import com.edusoho.kuozhi.v3.util.AppUtil;

import java.util.List;

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
    private List<CourseStudyPlan> mCourseStudyPlans;
    private List<VipInfo> mVipInfos;

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
                RadioGroup rg = (RadioGroup) findViewById(R.id.rg_type);
                rg.setOnCheckedChangeListener(getOnCheckedChangeListener());
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
        for (int i = 0; i < mCourseStudyPlans.size(); i++) {
            mRb = new RadioButton(mContext);
            mRb.setGravity(Gravity.CENTER);
            RadioGroup.LayoutParams mp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mp.setMargins(0, 0, AppUtil.dp2px(mContext, 10), AppUtil.dp2px(mContext, 5));
            mRb.setTextSize(AppUtil.sp2px(mContext, 4));
            mRb.setTextColor(mContext.getResources().getColorStateList(R.color.teach_type_text_selector));
            mRb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            mRb.setPadding(AppUtil.dp2px(mContext, 7), AppUtil.dp2px(mContext, 4)
                    , AppUtil.dp2px(mContext, 7), AppUtil.dp2px(mContext, 4));
            mRb.setBackground(ContextCompat.getDrawable(mContext, R.drawable.teach_type_rb_selector));
            mRb.setText(mCourseStudyPlans.get(i).getTitle());
            rg.addView(mRb, mp);
            if (i == 0) {
                rg.check(mRb.getId());
            }
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

    public Dialog initPlanData(List<CourseStudyPlan> list, List<VipInfo> vipInfo){
        mCourseStudyPlans = list;
        mVipInfos = vipInfo;
        return this;
    }

    private RadioGroup.OnCheckedChangeListener getOnCheckedChangeListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View view = group.findViewById(checkedId);
                int position = group.indexOfChild(view);
                CourseStudyPlan courseStudyPlan = mCourseStudyPlans.get(position);
                if ("1".equals(mCourseStudyPlans.get(position).getIsFree())) {
                    findViewById(R.id.discount).setVisibility(View.GONE);
                    findViewById(R.id.tv_original_price).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.tv_discount_price)).setText("免费");
                    ((TextView) findViewById(R.id.tv_discount_price)).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                } else {
                    findViewById(R.id.discount).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.tv_discount_price)).setText(String.format("%s%s", "¥ ", courseStudyPlan.getPrice()));
                    ((TextView) findViewById(R.id.tv_original_price)).setText(String.format("%s%s", "¥ ", courseStudyPlan.getOriginPrice()));
                    ((TextView) findViewById(R.id.tv_original_price)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);;
                    ((TextView) findViewById(R.id.tv_discount_price)).setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_color));
                }
                findViewById(R.id.tv_service).setVisibility(View.GONE);
                List<CourseStudyPlan.ServicesBean> servicesList = courseStudyPlan.getServices();
                if (servicesList != null && servicesList.size() != 0) {
                    findViewById(R.id.tv_service).setVisibility(View.VISIBLE);
                    StringBuilder sb = new StringBuilder();
                    sb.append("承诺服务: ");
                    for (int i = 0; i < servicesList.size(); i++) {
                        sb.append(servicesList.get(i).getFull_name());
                        if (i != servicesList.size() - 1) {
                            sb.append(" 、 ");
                        }
                    }
                    ((TextView) findViewById(R.id.tv_service)).setText(sb);
                }
//                ((TextView) findViewById(R.id.tv_way)).setText("");
                if ("days".equals(courseStudyPlan.getExpiryMode())) {
                    ((TextView) findViewById(R.id.tv_validity)).setText(String.format("%s%s", "有效期:  ", courseStudyPlan.getExpiryDays() + "天"));
                } else {
                    ((TextView) findViewById(R.id.tv_validity)).setText("有效期:  永久");
                }
                ((TextView) findViewById(R.id.tv_task)).setText(String.format("%s%s", "学习任务:  ", courseStudyPlan.getTaskNum() + "个"));
                findViewById(R.id.tv_vip).setVisibility(View.GONE);
                for (int i = 0; i < mVipInfos.size(); i++) {
                    VipInfo vipInfo = mVipInfos.get(i);
                    if (vipInfo.getId().equals(courseStudyPlan.getId())) {
                        findViewById(R.id.tv_vip).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.tv_vip)).setText(String.format("%s%s", vipInfo.getName(), "会员免费加入学习"));
                        break;
                    }
                }
            }
        };
    }

    public interface EventListener {

    }

}
