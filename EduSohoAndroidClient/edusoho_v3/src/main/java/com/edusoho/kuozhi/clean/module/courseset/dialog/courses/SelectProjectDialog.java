package com.edusoho.kuozhi.clean.module.courseset.dialog.courses;

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
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.order.confirm.ConfirmOrderActivity;
import com.edusoho.kuozhi.clean.widget.ESBottomDialog;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.List;

/**
 * Created by DF on 2017/4/12.
 * 课程-选择计划确定界面
 */

public class SelectProjectDialog extends ESBottomDialog<SelectProjectDialogContract.Presenter> implements
        ESBottomDialog.BottomDialogContentView, SelectProjectDialogContract.View {

    private final int FREE = 1;
    private final String FREE_STATE = "freeMode";

    private View mDiscount;
    private TextView mOriginalPrice;
    private TextView mDiscountPrice;
    private TextView mService;
    private TextView mWay;
    private TextView mValidity;
    private TextView mTask;
    private TextView mVip;
    private LoadDialog mProcessDialog;

    private List<CourseProject> mCourseProjects;
    private List<VipInfo> mVipInfos;
    private SelectProjectDialogContract.Presenter mPresenter;

    public void setData(List<CourseProject> courseStudyPlans, List<VipInfo> vipInfos) {
        this.mCourseProjects = courseStudyPlans;
        this.mVipInfos = vipInfos;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new SelectProjectDialogPresenter(this, mCourseProjects);
        initView(view);
    }

    @Override
    public View getContentView(ViewGroup parentView) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.dialog_confirm_select, parentView, false);
    }

    @Override
    public void setButtonState(TextView btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.confirm();
            }
        });
    }

    @Override
    public void showToastOrFinish(int content, boolean isFinish) {
        showToast(content);
        if (isFinish) {
            getActivity().finish();
        }
    }

    private void initView(View view) {
        RadioGroup mRg = (RadioGroup) view.findViewById(R.id.rg_type);
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
        addButton(mRg);
    }

    /**
     * 动态添加RadioButton到RadioGroup中
     */
    private void addButton(RadioGroup mRg) {
        int mostStudentNumPlan = getMostStudentNumPlan();
        for (int i = 0; i < mCourseProjects.size(); i++) {
            RadioButton mRb = new RadioButton(getContext());
            mRb.setGravity(Gravity.CENTER);
            RadioGroup.LayoutParams mp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mp.setMargins(0, 0, AppUtil.dp2px(getContext(), 10), AppUtil.dp2px(getContext(), 5));
            mRb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            mRb.setTextColor(getContext().getResources().getColorStateList(R.color.teach_type_text_selector));
            mRb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            mRb.setPadding(AppUtil.dp2px(getContext(), 7), AppUtil.dp2px(getContext(), 4)
                    , AppUtil.dp2px(getContext(), 7), AppUtil.dp2px(getContext(), 4));
            mRb.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.teach_type_rb_selector));
            mRb.setText(mCourseProjects.get(i).title);
            if (mostStudentNumPlan == i) {
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.hot);
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
        int num = 0;
        int maxIndex = 0;
        for (int i = 0; i < mCourseProjects.size(); i++) {
            if (mCourseProjects.get(i).studentNum > num) {
                num = mCourseProjects.get(i).studentNum;
                maxIndex = i;
            }
        }
        return maxIndex;
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
            mProcessDialog = LoadDialog.create(getContext());
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

    private RadioGroup.OnCheckedChangeListener getOnCheckedChangeListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View view = group.findViewById(checkedId);
                int position = group.indexOfChild(view);
                mPresenter.setData(position);
            }
        };
    }

    @Override
    public void showWayAndServiceView(CourseProject courseProject) {
        mWay.setText(FREE_STATE.equals(courseProject.learnMode) ?
                getContext().getString(R.string.free_mode) : getContext().getString(R.string.locked_mode));
        mService.setVisibility(View.GONE);
        CourseProject.Service[] services = courseProject.services;
        if (services != null && services.length != 0) {
            mService.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            sb.append(getContext().getString(R.string.promise_services));
            for (int i = 0; i < services.length; i++) {
                sb.append(services[i].fullName);
                if (i != services.length - 1) {
                    sb.append(" 、 ");
                }
            }
            mService.setText(sb);
        }
    }

    @Override
    public void showTaskView(int taskNum) {
        mTask.setText(String.format(getContext().getString(R.string.course_task_num), taskNum));
    }

    @Override
    public void showPriceView(CourseProject courseProject) {
        if (FREE == courseProject.isFree) {
            mDiscount.setVisibility(View.GONE);
            mOriginalPrice.setVisibility(View.GONE);
            mDiscountPrice.setText(R.string.free_course_project);
            mDiscountPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
        } else {
            mDiscountPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_color));
            if (courseProject.price == courseProject.originPrice) {
                mDiscount.setVisibility(View.GONE);
                mDiscountPrice.setText(String.format(getString(R.string.yuan_symbol), courseProject.price));
                return;
            }
            mDiscount.setVisibility(View.VISIBLE);
            mDiscountPrice.setText(String.format(getString(R.string.yuan_symbol), courseProject.price));
            mOriginalPrice.setVisibility(View.VISIBLE);
            mOriginalPrice.setText(String.format(getString(R.string.yuan_symbol), courseProject.originPrice));
            mOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public void showValiditView(int content) {
        mValidity.setText(content);
    }

    @Override
    public void showValidityView(int format, String content) {
        mValidity.setText(String.format(getContext().getString(format), content));
    }

    @Override
    public void showValidityView(int format, String textOne, String textTwo) {
        mValidity.setText(String.format(getContext().getString(R.string.validity_date), textOne, textTwo));
    }

    @Override
    public void showVipView(int vipLevelId) {
        mVip.setVisibility(View.GONE);
        for (int i = 0; i < mVipInfos.size(); i++) {
            VipInfo vipInfo = mVipInfos.get(i);
            if (vipInfo.id == vipLevelId) {
                mVip.setVisibility(View.VISIBLE);
                mVip.setText(String.format(getContext().getString(R.string.vip_free), vipInfo.name));
                break;
            }
        }
    }

    @Override
    public void goToConfirmOrderActivity(CourseProject courseProject) {
        ConfirmOrderActivity.launch(getContext(), courseProject.courseSet.id, courseProject.id);
    }

    @Override
    public void goToCourseProjectActivity(int courseProjectId) {
        CourseProjectActivity.launch(getContext(), courseProjectId);
    }

    @Override
    public boolean showConfirm() {
        return true;
    }

    @Override
    public void showToast(int resId) {
        CommonUtil.shortToast(getContext(), getString(resId));
    }
}
