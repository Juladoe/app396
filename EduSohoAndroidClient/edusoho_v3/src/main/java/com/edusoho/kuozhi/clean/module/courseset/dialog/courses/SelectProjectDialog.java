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
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.List;

/**
 * Created by DF on 2017/4/12.
 */

public class SelectProjectDialog extends ESBottomDialog implements
        ESBottomDialog.BottomDialogContentView, SelectProjectDialogContract.View {

    private final String IS_FREE = "1";
    private final String FREE_STATE = "freeMode";
    private static final String END_DATE_MODE = "end_date";
    private static final String DAYS_MODE = "days";
    private static final String DATE_MODE = "date";

    private RadioGroup mRg;
    private View mDiscount;
    private TextView mOriginalPrice;
    private TextView mDiscountPrice;
    private TextView mService;
    private TextView mWay;
    private TextView mValidity;
    private TextView mTask;
    private TextView mVip;
    private TextView mConfirm;
    private List<CourseProject> mCourseProjects;
    private CourseProject mCourseProject;
    private List<VipInfo> mVipInfos;
    private SelectProjectDialogContract.Presenter mPresenter;
    private LoadDialog mProcessDialog;

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
    public View getContentView(ViewGroup parentView) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_confirm_select, parentView, false);
        initView(view);
        return view;
    }

    @Override
    public void setButtonState(TextView btn) {
        mConfirm = btn;
        addButton();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.confirm(mCourseProject);
            }
        });
    }

    @Override
    public void showToastOrFinish(int content, boolean isFinish) {
        CommonUtil.shortToast(getContext(), getString(content));
        if (isFinish) {
            getActivity().finish();
        }
    }

    @Override
    public void goToConfirmOrderActivity() {
        ConfirmOrderActivity.launch(getContext(), mCourseProject.courseSetId, mCourseProject.id);
    }

    @Override
    public void goToCourseProjectActivity() {
        CourseProjectActivity.launch(getContext(), mCourseProject.id);
    }

    private void initView(View view) {
        mPresenter = new SelectProjectDialogPresenter(this);
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

    /**
     * 动态添加RadioButton到RadioGroup中
     */
    private void addButton() {
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

    private RadioGroup.OnCheckedChangeListener getOnCheckedChangeListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View view = group.findViewById(checkedId);
                int position = group.indexOfChild(view);
                mCourseProject = mCourseProjects.get(position);
                setPriceView();
                setServiceView();
                mWay.setText(FREE_STATE.equals(mCourseProject.learnMode) ?
                        getContext().getString(R.string.free_mode) : getContext().getString(R.string.locked_mode));
                setOtherView();
            }
        };
    }

    private void setPriceView() {
        if (IS_FREE.equals(mCourseProject.isFree)) {
            mDiscount.setVisibility(View.GONE);
            mOriginalPrice.setVisibility(View.GONE);
            mDiscountPrice.setText(R.string.free_course_project);
            mDiscountPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
        } else {
            mDiscountPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_color));
            if (mCourseProject.price == mCourseProject.originPrice) {
                mDiscount.setVisibility(View.GONE);
                mDiscountPrice.setText(String.format(getString(R.string.yuan_symbol), mCourseProject.price));
                return;
            }
            mDiscount.setVisibility(View.VISIBLE);
            mDiscountPrice.setText(String.format(getString(R.string.yuan_symbol), mCourseProject.price));
            mOriginalPrice.setVisibility(View.VISIBLE);
            mOriginalPrice.setText(String.format(getString(R.string.yuan_symbol), mCourseProject.originPrice));
            mOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void setServiceView() {
        mService.setVisibility(View.GONE);
        CourseProject.Service[] services = mCourseProject.services;
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

    private void setOtherView() {
        if (END_DATE_MODE.equals(mCourseProject.expiryMode)) {
            mValidity.setText(String.format(getContext().getString(R.string.validity), mCourseProject.expiryEndDate.substring(0, 10)));
        } else if (DATE_MODE.equals(mCourseProject.expiryMode)) {
            mValidity.setText(String.format(getContext().getString(R.string.validity_date),
                    mCourseProject.expiryStartDate.substring(0, 10), mCourseProject.expiryEndDate.substring(0, 10)));
        } else if (DAYS_MODE.equals(mCourseProject.expiryMode)) {
            mValidity.setText(String.format(getContext().getString(R.string.validity_day), mCourseProject.expiryDays));
        } else {
            mValidity.setText(R.string.validity_forever);
        }
        mTask.setText(String.format(getContext().getString(R.string.course_task_num), mCourseProject.taskNum));
        mVip.setVisibility(View.GONE);
        for (int i = 0; i < mVipInfos.size(); i++) {
            VipInfo vipInfo = mVipInfos.get(i);
            if (vipInfo.id == mCourseProject.vipLevelId) {
                mVip.setVisibility(View.VISIBLE);
                mVip.setText(String.format(getContext().getString(R.string.vip_free), vipInfo.name));
                break;
            }
        }
        if (EdusohoApp.app.loginUser.vip != null
                && EdusohoApp.app.loginUser.vip.seq >= mCourseProject.vipLevelId
                && mCourseProject.vipLevelId != 0 || IS_FREE.equals(mCourseProject.isFree)) {
            mConfirm.setText(R.string.txt_vip_free);
        } else {
            mConfirm.setText(R.string.confirm);
        }
    }

    @Override
    public boolean showConfirm() {
        return true;
    }
}
