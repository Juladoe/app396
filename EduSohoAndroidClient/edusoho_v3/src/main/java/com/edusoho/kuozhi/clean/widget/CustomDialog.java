package com.edusoho.kuozhi.clean.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.courseset.GuaranteServiceAdapter;

import java.util.List;

/**
 * Created by DF on 2017/3/17.
 */

public class CustomDialog extends Dialog {

    private int mType;

    private List<CourseProject> mCourseStudyPlans;
    private List<VipInfo> mVipInfos;
    private CourseProject mCourseStudyPlan;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }


    /**
     * 初始化dialog的类型1:学习进度 2:支付方式 3:优惠券 4:虚拟币 7:承诺服务
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
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                TextView tvAccountBalance = (TextView) findViewById(R.id.tv_account_balance);
                TextView tvOrderAmount = (TextView) findViewById(R.id.tv_order_amount);
//                tvAccountBalance.setText(mAccountBalance);
//                tvOrderAmount.setText(mOrderAmount);
                break;
            case 7:
                RecyclerView rv = (RecyclerView) findViewById(R.id.rv_content);
                GuaranteServiceAdapter guaranteServiceAdapter = new GuaranteServiceAdapter();
                rv.setAdapter(guaranteServiceAdapter);
                break;
        }
    }

}
