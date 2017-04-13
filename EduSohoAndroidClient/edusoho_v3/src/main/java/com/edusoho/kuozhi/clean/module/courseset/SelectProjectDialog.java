package com.edusoho.kuozhi.clean.module.courseset;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.courseset.order.ConfirmOrderActivity;
import com.edusoho.kuozhi.clean.widget.ESBottomDialog;
import com.edusoho.kuozhi.v3.util.AppUtil;

import java.util.List;

/**
 * Created by DF on 2017/4/12.
 */

public class SelectProjectDialog extends ESBottomDialog implements ESBottomDialog.BottomDialogContentView {

    public static final String COURSE_SET = "1";
    public static final String STUDY_PLANS = "2";
    public static final String VIP_INFOS = "3";
    private RadioButton mRb;
    private RadioGroup mRg;
    private View mDiscount;
    private TextView mOriginalPrice;
    private TextView mDiscountPrice;
    private TextView mService;
    private TextView mWay;
    private TextView mValidity;
    private TextView mTask;
    private TextView mVip;

    private List<CourseProject> mCourseStudyPlans;
    private CourseProject mCourseStudyPlan;
    private List<VipInfo> mVipInfos;
    private CourseSet mCourseSet;

    public void setData(CourseSet mCourseSet, List<CourseProject> mCourseStudyPlans, List<VipInfo> mVipInfos) {
        this.mCourseSet = mCourseSet;
        this.mCourseStudyPlans = mCourseStudyPlans;
        this.mVipInfos = mVipInfos;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(this);
    }

    @Override
    public View getContentView(ViewGroup parentView) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_confirm_select, parentView, false);
        initView(view);
        addButton();
        return view;
    }

    private void initView(View view) {
        mRg = (RadioGroup) view.findViewById(R.id.rg_type);
        mRg.setOnCheckedChangeListener(getOnCheckedChangeListener());
        mDiscount = view.findViewById(R.id.discount);
        mService = (TextView) view.findViewById(R.id.tv_service);
        mOriginalPrice = (TextView) view.findViewById(R.id.tv_original_price);
        mDiscountPrice = (TextView) view.findViewById(R.id.tv_discount_price);
        mValidity = (TextView) view.findViewById(R.id.tv_validity);
        mWay = (TextView) view.findViewById(R.id.tv_way);
        mTask = (TextView) view.findViewById(R.id.tv_task);
        mVip = (TextView) view.findViewById(R.id.tv_vip);
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 动态添加RadioButton到RadioGroup中
     */
    private void addButton() {
        int mostStudentNumPlan = getMostStudentNumPlan();
        for (int i = 0; i < mCourseStudyPlans.size(); i++) {
            mRb = new RadioButton(getContext());
            mRb.setGravity(Gravity.CENTER);
            RadioGroup.LayoutParams mp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mp.setMargins(0, 0, AppUtil.dp2px(getContext(), 10), AppUtil.dp2px(getContext(), 5));
            mRb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            mRb.setTextColor(getContext().getResources().getColorStateList(R.color.teach_type_text_selector));
            mRb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            mRb.setPadding(AppUtil.dp2px(getContext(), 7), AppUtil.dp2px(getContext(), 4)
                    , AppUtil.dp2px(getContext(), 7), AppUtil.dp2px(getContext(), 4));
            mRb.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.teach_type_rb_selector));
            mRb.setText(mCourseStudyPlans.get(i).title);
            if (mostStudentNumPlan == i) {
                Drawable drawable = getContext().getResources().getDrawable(R.drawable.hot);
                drawable.setBounds(0, 0, AppUtil.dp2px(getContext(), 10), AppUtil.dp2px(getContext(), 13));
                mRb.setCompoundDrawablePadding(AppUtil.dp2px(getContext(), 5));
                mRb.setCompoundDrawables(null, null, drawable, null);
            }
            mRg.addView(mRb, mp);
            if (i == 0) {
                mRg.check(mRb.getId());
            }
        }
    }

    private int getMostStudentNumPlan() {
        int index = 0;
        for (int i = 0; i < mCourseStudyPlans.size(); i++) {
            if (i > 0) {
                if (mCourseStudyPlans.get(i - 1).studentNum < mCourseStudyPlans.get(i).studentNum) {
                    index = i;
                }
            }
        }
        return index;
    }

    private RadioGroup.OnCheckedChangeListener getOnCheckedChangeListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View view = group.findViewById(checkedId);
                int position = group.indexOfChild(view);
                mCourseStudyPlan = mCourseStudyPlans.get(position);
                setPriceView(position);
                setServiceView();
                mWay.setText("freeMode".equals(mCourseStudyPlan.learnMode) ?
                        getContext().getString(R.string.free_mode) : getContext().getString(R.string.locked_mode));
                setOtherView();
//                if (EdusohoApp.app.loginUser != null && EdusohoApp.app.loginUser.vip.levelId > mCourseStudyPlan.vipLevelId) {
//                    ((TextView) findViewById(R.id.tv_confirm)).setText(getContext().getString(R.string.txt_vip_free));
//                }
            }
        };
    }

    private void setPriceView(int position) {
        if ("1".equals(mCourseStudyPlans.get(position).isFree)) {
            mDiscount.setVisibility(View.GONE);
            mOriginalPrice.setVisibility(View.GONE);
            mDiscountPrice.setText(R.string.free_course_project);
            mDiscountPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
        } else {
            mDiscountPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_color));
            if (mCourseStudyPlan.price == mCourseStudyPlan.price) {
                mDiscount.setVisibility(View.GONE);
                mDiscountPrice.setText(String.format("%s%.2f", "¥ ", mCourseStudyPlan.price));
                return;
            }
            mDiscount.setVisibility(View.VISIBLE);
            mDiscountPrice.setText(String.format("%s%.2f", "¥ ", mCourseStudyPlan.price));
            mOriginalPrice.setText(String.format("%s%.2f", "¥ ", mCourseStudyPlan.originPrice));
            mOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            ;
        }
    }

    private void setServiceView() {
        mService.setVisibility(View.GONE);
        CourseProject.Service[] services = mCourseStudyPlan.services;
        if (services != null && services.length != 0) {
            mService.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            sb.append(getContext().getString(R.string.promise_services));
            for (int i = 0; i < services.length; i++) {
                sb.append(services[i].full_name);
                if (i != services.length - 1) {
                    sb.append(" 、 ");
                }
            }
            mService.setText(sb);
        }
    }

    private void setOtherView() {
        if ("days".equals(mCourseStudyPlan.expiryMode)) {
            mValidity.setText(String.format(getContext().getString(R.string.validity_day), mCourseStudyPlan.expiryDays));
        } else {
            mValidity.setText(R.string.validity_forever);
        }
        mTask.setText(String.format(getContext().getString(R.string.course_task_num), mCourseStudyPlan.taskNum));
        mVip.setVisibility(View.GONE);
        for (int i = 0; i < mVipInfos.size(); i++) {
            VipInfo vipInfo = mVipInfos.get(i);
            if (vipInfo.id == mCourseStudyPlan.vipLevelId) {
                mVip.setVisibility(View.VISIBLE);
                mVip.setText(String.format(getContext().getString(R.string.vip_free), vipInfo.name));
                break;
            }
        }
    }

    @Override
    public void setButtonState(TextView btn) {

    }

    @Override
    public boolean showConfirm() {
        return true;
    }
}
