package com.edusoho.kuozhi.clean.module.course.info;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.Member;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;
import com.edusoho.kuozhi.clean.module.course.dialog.ServicesDialog;
import com.edusoho.kuozhi.clean.utils.ItemClickSupport;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduHtmlHttpImageGetter;
import com.edusoho.kuozhi.v3.view.circleImageView.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wefika.flowlayout.FlowLayout;

import java.util.List;
import java.util.zip.Inflater;

import cn.trinea.android.common.util.StringUtils;

/**
 * Created by JesseHuang on 2017/3/26.
 * 教学计划简介
 */

public class CourseProjectInfoFragment extends BaseFragment<CourseProjectInfoContract.Presenter>
        implements CourseProjectInfoContract.View, CourseProjectFragmentListener {

    public static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    protected CourseProjectInfoContract.Presenter mPresenter;

    private FlowLayout mPromise;
    private TextView mTitle;
    private TextView mStudentNum;
    private RatingBar mCourseRate;
    private View mPriceLayout;
    private View mRateLayout;
    private TextView mSalePrice;
    private TextView mOriginalPrice;
    private TextView mSaleWord;
    private View mVipLine;
    private View mCourseInfoLine;
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
    private View mCourseMemberCountLayout;
    private TextView mCourseMemberCount;
    private LinearLayout mCourseMembers;
    private View mCourseMembersLine;
    private RecyclerView mRelativeCourses;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        CourseProject courseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
        mPresenter = new CourseProjectInfoPresenter(courseProject, this);
    }

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
        mPriceLayout = view.findViewById(R.id.price_layout);
        mRateLayout = view.findViewById(R.id.rate_layout);
        mSalePrice = (TextView) view.findViewById(R.id.tv_sale_price);
        mOriginalPrice = (TextView) view.findViewById(R.id.tv_original_price);
        mSaleWord = (TextView) view.findViewById(R.id.tv_sale_word);
        mVipLine = view.findViewById(R.id.v_vip_line);
        mCourseInfoLine = view.findViewById(R.id.course_info_line);
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
        mCourseMemberCountLayout = view.findViewById(R.id.rl_course_member_num);
        mCourseMemberCount = (TextView) view.findViewById(R.id.tv_course_member_count);
        mCourseMembers = (LinearLayout) view.findViewById(R.id.ll_course_members);
        mCourseMembersLine = view.findViewById(R.id.v_course_members_line);
        mRelativeCourses = (RecyclerView) view.findViewById(R.id.rv_relative_courses);

        mVipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        Const.VIP_LIST
                );
                CoreEngine.create(getContext()).runNormalPlugin("WebViewActivity"
                        , getContext(), new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.subscribe();
    }

    @Override
    public void initCourseProjectInfo(CourseProject course) {
        mTitle.setText(course.title);
        mStudentNum.setText(String.format(getString(R.string.course_student_count), course.studentNum));
        mCourseRate.setRating(Float.valueOf(course.rating));
    }

    @Override
    public void showCourseProjectInfo(boolean show) {
        mTitle.setVisibility(show ? View.VISIBLE : View.GONE);
        mPriceLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mRateLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mCourseInfoLine.setVisibility(show ? View.VISIBLE : View.GONE);
        mVipLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showPrice(CourseProjectPriceEnum type, float price, float originPrice) {
        switch (type) {
            case FREE:
                mOriginalPrice.setText(R.string.free_course_project);
                mOriginalPrice.setTextColor(getResources().getColor(R.color.primary_color));
                mSalePrice.setVisibility(View.GONE);
                break;
            case ORIGINAL:
                mOriginalPrice.setText(String.format(getString(R.string.price_format), originPrice));
                mOriginalPrice.setTextColor(getResources().getColor(R.color.secondary_color));
                mSalePrice.setVisibility(View.GONE);
                break;
            case SALE:
                mSalePrice.setText(String.format(getString(R.string.price_format), price));
                mOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                mOriginalPrice.setText(String.format(getString(R.string.price_format), originPrice));
                mOriginalPrice.setText(originPrice + "");
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
    public void showServices(final CourseProject.Service[] services) {
        if (services == null || services.length == 0) {
            return;
        }
        mServicesLayout.setVisibility(View.VISIBLE);
        for (CourseProject.Service service : services) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_course_project_promise, null);
            ((TextView) view.findViewById(R.id.tv_promise)).setText(service.fullName);
            FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            mPromise.addView(view);
        }

        mServicesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServicesDialog.newInstance(services).show(getFragmentManager(), "ServicesDialog");
            }
        });
    }

    @Override
    public void showIntroduce(String content) {
        if (!StringUtils.isEmpty(content)) {
            mIntroduceLayout.setVisibility(View.VISIBLE);
            mIntroduce.setText(Html.fromHtml(content, new EduHtmlHttpImageGetter(mIntroduce, null, true), null));
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
    public void showTeacher(Teacher teacher) {
        mTeacherName.setText(teacher.nickname);
        mTeacherTitle.setText(teacher.title);
        ImageLoader.getInstance().displayImage(teacher.avatar.middle, mTeacherAvatar, EdusohoApp.app.mAvatarOptions);
    }

    @Override
    public void showMemberNum(int count) {
        mCourseMemberCount.setText(String.format(getString(R.string.course_member_count), count));
    }

    @Override
    public void showMembers(List<Member> members) {
        if (members != null && members.size() > 0) {
            mCourseMembers.setVisibility(View.VISIBLE);
            mCourseMemberCountLayout.setVisibility(View.VISIBLE);
            mCourseMembersLine.setVisibility(View.VISIBLE);
            int screenWidth = EdusohoApp.screenW;
            int memberAvatarWidth = CommonUtil.dip2px(getActivity(), 50);
            int avatarMargin = CommonUtil.dip2px(getActivity(), 24);
            int viewMargin = CommonUtil.dip2px(getActivity(), 15);
            int showMemberCount;
            showMemberCount = (screenWidth + avatarMargin - 2 * viewMargin) / (memberAvatarWidth + avatarMargin);
            int size = (showMemberCount < members.size() ? showMemberCount : members.size());
            for (int i = 0; i < size; i++) {
                LinearLayout memberView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_course_member, null);
                ImageLoader.getInstance().displayImage(members.get(i).user.getMediumAvatar(), (CircularImageView) memberView.findViewById(R.id.avatar_course_member), EdusohoApp.app.mAvatarOptions);
                ((TextView) memberView.findViewById(R.id.tv_member_name)).setText(members.get(i).user.nickname);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                if (i != size - 1) {
                    lp.rightMargin = CommonUtil.dip2px(getActivity(), 24);
                }
                memberView.setLayoutParams(lp);
                mCourseMembers.addView(memberView);

            }
        }
    }

    @Override
    public void showRelativeCourseProjects(List<CourseProject> courseList, List<VipInfo> vipInfos) {
        mRelativeCourses.setHasFixedSize(true);
        mRelativeCourses.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRelativeCourses.setItemAnimator(new DefaultItemAnimator());
        mRelativeCourses.setNestedScrollingEnabled(false);
        final RelativeCourseAdapter relativeCourseAdapter = new RelativeCourseAdapter(getActivity(), courseList, vipInfos);
        mRelativeCourses.setAdapter(relativeCourseAdapter);
        ItemClickSupport.addTo(mRelativeCourses).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                CourseProjectActivity.launch(getActivity(), relativeCourseAdapter.getItem(position).id);
            }
        });
    }

    @Override
    public void launchCourseProject(int courseId) {
        CourseProjectActivity.launch(getActivity(), courseId);
    }

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }
}
