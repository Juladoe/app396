package com.edusoho.kuozhi.v3.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by DF on 2017/3/17.
 */

public class StudyProgressDialog extends Dialog {

    private String mAlreadyPlan;
    private String mPlanComplete;
    private String mCourseDate;
    private TextView mTvAlreadyPlan;
    private TextView mTvPlanCom;
    private TextView mTvCourseDate;
    private int mType;

    public StudyProgressDialog(@NonNull Context context) {
        super(context, R.style.dialog_custom);
    }

    public StudyProgressDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView(mType));
        initView();
        initEvent();
    }

    public StudyProgressDialog initType(int type) {
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
                if (mTvAlreadyPlan == null) {
                    mTvAlreadyPlan = (TextView) findViewById(R.id.already_plan);
                    mTvPlanCom = (TextView) findViewById(R.id.plan_complete);
                    mTvCourseDate = (TextView) findViewById(R.id.course_date);
                }
                mTvAlreadyPlan.setText(String.format("%s%s","已经完成计划: ", mAlreadyPlan));
                mTvPlanCom.setText(String.format("%s%s", "计划完成任务: ", mPlanComplete));
                mTvCourseDate.setText(String.format("%s%s", "课程有效期至: ", mCourseDate));
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
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
     * @param alreadyPlan  已经完成计划
     * @param planComplete   计划完成任务
     * @param courseDate   课程有效期至
     * @return
     */
    public Dialog initText(String alreadyPlan, String planComplete, String courseDate) {
        this.mAlreadyPlan = alreadyPlan;
        this.mPlanComplete = planComplete;
        this.mCourseDate = courseDate;
        return this;
    }


}
