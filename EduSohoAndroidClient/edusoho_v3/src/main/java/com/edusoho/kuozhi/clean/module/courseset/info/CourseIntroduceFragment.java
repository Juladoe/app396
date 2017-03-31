package com.edusoho.kuozhi.clean.module.courseset.info;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.courseset.BaseLazyFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMember;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.view.EduHtmlHttpImageGetter;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by DF on 2017/3/21.
 */

public class CourseIntroduceFragment extends BaseLazyFragment
        implements View.OnClickListener {

    private TextView mTvPriceOld;
    private TextView mTvPriceNow;
    private TextView mTvTitle;
    private ReviewStarView mReviewStar;
    private TextView mTvTitleStudentNum;
    private TextView mTvTitleDesc;
    private View mVipLayout;
    private TextView mTvPeopleDesc;
    private TextView mTvStudentNum;
    private View mStudentMore;
    private LinearLayout mStudentIconLayout;
    private TextView mTvStudent1;
    private View mTvStudentNone;
    private View mLoadView;
    private View mPeopleLayout;

    private CourseDetail mCourseDetail;
    private int mCourseId;
    private TextView mTvVipDesc;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCourseId = getArguments().getInt(Const.COURSE_ID);
    }

    @Override
    protected int initContentView() {
        return R.layout.fragment_course_introduce;
    }

    protected void initView(View view) {
        mTvPriceOld = (TextView) view.findViewById(R.id.tv_price_old);
        mTvPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTvPriceNow = (TextView) view.findViewById(R.id.tv_price_now);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mReviewStar = (ReviewStarView) view.findViewById(R.id.review_star);
        mTvTitleStudentNum = (TextView) view.findViewById(R.id.tv_title_student_num);
        mTvTitleDesc = (TextView) view.findViewById(R.id.tv_title_desc);
        mVipLayout = view.findViewById(R.id.vip_rlayout);
        mTvPeopleDesc = (TextView) view.findViewById(R.id.tv_people_desc);
        mTvStudentNum = (TextView) view.findViewById(R.id.tv_student_num);
        mTvVipDesc = (TextView) view.findViewById(R.id.tv_vip_desc);
        mStudentMore = view.findViewById(R.id.tv_student_more);
        mStudentIconLayout = (LinearLayout) view.findViewById(R.id.student_icon_llayout);
        mTvStudent1 = (TextView) view.findViewById(R.id.tv_student1);
        mPeopleLayout = view.findViewById(R.id.people_rlayout);
        mTvStudentNone = view.findViewById(R.id.tv_student_none);
        mLoadView = view.findViewById(R.id.ll_detail_load);
    }

    protected void initEvent() {
        mStudentMore.setOnClickListener(this);
        mVipLayout.setOnClickListener(this);
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        mLoadView.setVisibility(View.VISIBLE);
        CourseDetailModel.getCourseDetail(mCourseId, new ResponseCallbackListener<CourseDetail>() {
            @Override
            public void onSuccess(CourseDetail data) {
                mCourseDetail = data;
                if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
                    return;
                }
                refreshView();
                mLoadView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String code, String message) {
                mLoadView.setVisibility(View.GONE);
            }
        });
        CourseDetailModel.getCourseMember(mCourseId,
                new ResponseCallbackListener<List<CourseMember>>() {
                    @Override
                    public void onSuccess(List<CourseMember> data) {
                        if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
                            return;
                        }
                        initStudent(data);
                    }

                    @Override
                    public void onFailure(String code, String message) {
                    }
                });
    }

    private void refreshView() {
        Course course = mCourseDetail.getCourse();
        mTvTitle.setText(course.title);
        mTvStudentNum.setText(String.format("(%s)", mCourseDetail.getCourse().studentNum));
        mTvTitleDesc.setText(Html.fromHtml(course.about, new EduHtmlHttpImageGetter(mTvTitleDesc, null, true), null));
        if (mCourseDetail.getCourse().vipLevelId == 0) {
            mVipLayout.setVisibility(View.GONE);
        } else {
            mVipLayout.setVisibility(View.VISIBLE);
            mTvVipDesc.setText(String.format("加入%s，免费学习更多课程",
                    mCourseDetail.getVipLevels().size() > course.vipLevelId - 1 ?
                            mCourseDetail.getVipLevels().get(course.vipLevelId - 1).name : ""));
        }
        if (course.price == 0) {
            mTvPriceNow.setText("免费");
            mTvPriceNow.setTextSize(18);
            mTvPriceNow.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_color));
        } else {
            mTvPriceNow.setText(String.valueOf(course.price));
            mTvPriceNow.setTextSize(24);
            mTvPriceNow.setTextColor(ContextCompat.getColor(getContext(), R.color.secondary_color));
        }
        if (course.originPrice == 0) {
            mTvPriceOld.setVisibility(View.GONE);
        } else {
            if (course.originPrice == course.price) {
                mTvPriceOld.setVisibility(View.GONE);
            } else {
                mTvPriceOld.setVisibility(View.VISIBLE);
                mTvPriceOld.setText("¥" + course.originPrice);
            }
        }
        mTvTitleStudentNum.setText(String.format("%s名学生",
                course.studentNum));
        mReviewStar.setRating((int) course.rating);
        StringBuilder sb = new StringBuilder();
        int length = course.audiences.length;
        if (length == 0) {
            mPeopleLayout.setVisibility(View.GONE);
        } else {
            mPeopleLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < length; i++) {
                sb.append(course.audiences[i]);
                if (i != length - 1) {
                    sb.append("；");
                }
            }
            mTvPeopleDesc.setText(sb.toString());
        }
    }

    private void initStudent(List<CourseMember> data) {
        View.OnClickListener onClickListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = String.valueOf(v.getTag());
                        jumpToMember(id);
                    }
                };
        if (data.size() == 0) {
            mTvStudentNone.setVisibility(View.VISIBLE);
        } else {
            mTvStudentNone.setVisibility(View.GONE);
        }
        for (int i = 0; i < 5; i++) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_detail_avatar, null);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, -1);
            params.weight = 1;
            view.setLayoutParams(params);
            ImageView image = (ImageView) view.findViewById(R.id.iv_avatar_icon);
            TextView txt = (TextView) view.findViewById(R.id.tv_avatar_name);
            if (data.size() > i && data.get(i).user != null) {
                image.setTag(data.get(i).user.id);
                image.setOnClickListener(onClickListener);
                txt.setText(data.get(i).user.nickname);
                ImageLoader.getInstance().displayImage(data.get(i).user.getAvatar(), image, EdusohoApp.app.mAvatarOptions);
            } else {
                txt.setText("");
                image.setImageAlpha(0);
            }
            mStudentIconLayout.addView(view);
        }
    }

    private void jumpToMember(String id) {
        final String url = String.format(
                Const.MOBILE_APP_URL,
                EdusohoApp.app.schoolHost,
                String.format("main#/userinfo/%s",
                        id)
        );
        CoreEngine.create(getContext()).runNormalPlugin("WebViewActivity"
                , getActivity(), new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_student_more) {
            moreStudent();
        } else if (id == R.id.vip_rlayout) {
            vipInfo();
        }
    }

    private void vipInfo() {
        MobclickAgent.onEvent(getActivity(), "courseDetailsPage_memberAdvertisements");
        if (EdusohoApp.app.loginUser == null) {
            CourseUtil.notLogin();
            return;
        }
        final String url = String.format(
                Const.MOBILE_APP_URL,
                EdusohoApp.app.schoolHost,
                "main#/viplist"
        );
        CoreEngine.create(getContext()).runNormalPlugin("WebViewActivity"
                , getContext(), new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
    }

    private void moreStudent() {
        MobclickAgent.onEvent(getActivity(), "courseDetailsPage_introduction_moreCoursesParticipants");
        final String url = String.format(
                Const.MOBILE_APP_URL,
                EdusohoApp.app.schoolHost,
                String.format("main#/studentlist/%s/%s",
                        "courses", mCourseId)
        );
        CoreEngine.create(getContext()).runNormalPlugin("WebViewActivity"
                , getContext(), new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
    }
}
