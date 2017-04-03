package com.edusoho.kuozhi.clean.module.course.info;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.view.circleImageView.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wefika.flowlayout.FlowLayout;

import java.util.Locale;

import cn.trinea.android.common.util.StringUtils;

/**
 * Created by JesseHuang on 2017/3/26.
 * 教学计划简介
 */

public class CourseProjectInfoFragment extends Fragment implements CourseProjectInfoContract.View, CourseProjectFragmentListener {

    private static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    private CourseProjectInfoContract.Presenter mPresenter;

    private FlowLayout mPromise;
    private TextView mTitle;
    private TextView mStudentNum;
    private RatingBar mCourseRate;
    private TextView mSalePrice;
    private TextView mOriginalPrice;
    private TextView mSaleWord;
    private View mVipLine;
    private View mVipLayout;
    private TextView mVipText;
    private View mServicesLayout;
    private TextView mIntroduce;
    private View mIntroduceLayout;
    private View mAudiencesLayout;
    private TextView mAudiences;
    private CircularImageView mTeacherAvatar;
    private TextView mTeacherName;
    private TextView mTeacherTitle;
    private TextView mCourseMemberCount;
    private LinearLayout mCourseMembers;
    private View mCourseMembersLine;
    private CourseProject courseProject;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_project_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPromise = (FlowLayout) view.findViewById(R.id.fl_promise_layout);
        mTitle = (TextView) view.findViewById(R.id.tv_course_project_title);
        mStudentNum = (TextView) view.findViewById(R.id.tv_student_num);
        mCourseRate = (RatingBar) view.findViewById(R.id.rb_course_rate);
        mSalePrice = (TextView) view.findViewById(R.id.tv_sale_price);
        mOriginalPrice = (TextView) view.findViewById(R.id.tv_original_price);
        mSaleWord = (TextView) view.findViewById(R.id.tv_sale_word);
        mVipLine = view.findViewById(R.id.v_vip_line);
        mVipLayout = view.findViewById(R.id.rl_vip_layout);
        mVipText = (TextView) view.findViewById(R.id.tv_vip_text);
        mServicesLayout = view.findViewById(R.id.ll_services_layout);
        mIntroduce = (TextView) view.findViewById(R.id.tv_course_project_info);
        mIntroduceLayout = view.findViewById(R.id.ll_course_project_introduce);
        mAudiencesLayout = view.findViewById(R.id.ll_audiences_layout);
        mAudiences = (TextView) view.findViewById(R.id.tv_audiences);
        mTeacherAvatar = (CircularImageView) view.findViewById(R.id.civ_teacher_avatar);
        mTeacherName = (TextView) view.findViewById(R.id.tv_teacher_name);
        mTeacherTitle = (TextView) view.findViewById(R.id.tv_teacher_title);
        mCourseMemberCount = (TextView) view.findViewById(R.id.tv_course_member_count);
        mCourseMembers = (LinearLayout) view.findViewById(R.id.ll_course_members);
        mCourseMembersLine = view.findViewById(R.id.v_course_members_line);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        CourseProject courseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
        mPresenter = new CourseProjectInfoPresenter(courseProject, this);
        mPresenter.subscribe();
    }

    @Override
    public void showCourseProjectInfo(CourseProject courseProject) {
        mTitle.setText(courseProject.title);
        mStudentNum.setText(String.format(getString(R.string.course_student_count), courseProject.studentNum));
        mCourseRate.setRating(Float.valueOf(courseProject.rating));
    }

    @Override
    public void showPrice(CourseProjectPriceEnum type, String price, String originPrice) {
        switch (type) {
            case FREE:
                mOriginalPrice.setText(R.string.free_course_project);
                mOriginalPrice.setTextColor(getResources().getColor(R.color.primary_color));
                mSalePrice.setVisibility(View.GONE);
                break;
            case ORIGINAL:
                mOriginalPrice.setText(String.format(Locale.CHINA, "￥%s", originPrice));
                mOriginalPrice.setTextColor(getResources().getColor(R.color.secondary_color));
                mSalePrice.setVisibility(View.GONE);
                break;
            case SALE:
                mSalePrice.setText(String.format(Locale.CHINA, "￥%s", price));
                mOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                mOriginalPrice.setText(String.format(Locale.CHINA, "￥%s", originPrice));
                mOriginalPrice.setText(originPrice);
                mSaleWord.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void showVipAdvertising(String vipName) {
        mVipLine.setVisibility(View.VISIBLE);
        mVipLayout.setVisibility(View.VISIBLE);
        mVipText.setText(String.format(getString(R.string.join_vip), vipName));
    }

    @Override
    public void showServices(CourseProject.Service[] services) {
        if (services == null || services.length == 0) {
            return;
        }
        mServicesLayout.setVisibility(View.VISIBLE);
        for (CourseProject.Service service : services) {
            TextView tv = new TextView(getActivity());
            tv.setTextColor(Color.BLACK);
            tv.setText(service.full_name);
            tv.setTextSize(AppUtil.px2sp(getActivity(), getResources().getDimension(R.dimen.font_s)));
            FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = 20;
            tv.setLayoutParams(lp);
            mPromise.addView(tv);
        }
    }

    @Override
    public void showIntroduce(String content) {
        if (!StringUtils.isEmpty(content)) {
            mIntroduceLayout.setVisibility(View.VISIBLE);
            mIntroduce.setText(content);
        }
    }

    @Override
    public void showAudiences(String[] audiences) {
        if (audiences != null && audiences.length > 0) {
            mAudiencesLayout.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (String audience : audiences) {
                sb.append(audience).append("、");
            }
            mAudiences.setText(sb.replace(sb.length() - 1, sb.length(), "").toString());
        }
    }

    @Override
    public void showTeacher(CourseProject.Teacher teacher) {
        mTeacherName.setText(teacher.nickname);
        mTeacherTitle.setText(teacher.title);
        ImageLoader.getInstance().displayImage(teacher.avatar, mTeacherAvatar, EdusohoApp.app.mAvatarOptions);
    }

    @Override
    public void showMemberNum(int count) {
        mCourseMemberCount.setText(String.format(getString(R.string.course_member_count), count));
    }

    @Override
    public void showMembers(CourseMember[] courseMembers) {
        if (courseMembers != null && courseMembers.length > 0) {
            mCourseMembers.setVisibility(View.VISIBLE);
            mCourseMembersLine.setVisibility(View.VISIBLE);
            mCourseMemberCount.setVisibility(View.VISIBLE);
            int screenWidth = EdusohoApp.screenW;
            int memberAvatarWidth = CommonUtil.dip2px(getActivity(), 50);
            int avatarMargin = CommonUtil.dip2px(getActivity(), 24);
            int viewMargin = CommonUtil.dip2px(getActivity(), 15);
            int showMemberCount;
            showMemberCount = (screenWidth + avatarMargin - 2 * viewMargin) / (memberAvatarWidth + avatarMargin);
            int size = (showMemberCount < courseMembers.length ? showMemberCount : courseMembers.length);
            for (int i = 0; i < size; i++) {
                CircularImageView memberAvatar = new CircularImageView(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(CommonUtil.dip2px(getActivity(), 50), CommonUtil.dip2px(getActivity(), 50));
                if (i != size - 1) {
                    lp.rightMargin = CommonUtil.dip2px(getActivity(), 24);
                }
                memberAvatar.setLayoutParams(lp);
                ImageLoader.getInstance().displayImage(courseMembers[i].user.getMediumAvatar(), memberAvatar, EdusohoApp.app.mAvatarOptions);
                mCourseMembers.addView(memberAvatar);
            }
        }
    }

    public CourseProjectFragmentListener newInstance(CourseProject courseProject) {
        CourseProjectInfoFragment fragment = new CourseProjectInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COURSE_PROJECT_MODEL, courseProject);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }


}
