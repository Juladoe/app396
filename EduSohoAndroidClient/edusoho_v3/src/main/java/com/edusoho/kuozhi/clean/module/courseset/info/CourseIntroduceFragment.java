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
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.module.base.BaseLazyFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
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
        implements View.OnClickListener, CourseIntroduceContract.View{

    private TextView mPriceOld;
    private TextView mPriceNow;
    private TextView mTitle;
    private ReviewStarView mReviewStar;
    private TextView mTitleStudentNum;
    private TextView mTitleDesc;
    private View mVipLayout;
    private TextView mPeopleDesc;
    private TextView mStudentNum;
    private TextView mVipDesc;
    private View mStudentMore;
    private LinearLayout mStudentIconLayout;
    private TextView mStudent;
    private View mStudentNone;
    private View mLoadView;
    private View mPeopleLayout;
    private View mInfoLayout;

    private CourseDetail mCourseDetail;
    private int mCourseId = 1;
    private CourseIntroduceContract.Presenter mPresenter;
    private CourseSet mCourseSet;
    private View mDiscount;

    public CourseIntroduceFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mCourseId = getArguments().getInt(Const.COURSE_ID);
    }

    @Override
    protected int initContentView() {
        return R.layout.fragment_course_introduce;
    }

    protected void initView(View view) {
        mPriceOld = (TextView) view.findViewById(R.id.tv_price_old);
        mPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mPriceNow = (TextView) view.findViewById(R.id.tv_price_now);
        mTitle = (TextView) view.findViewById(R.id.tv_title);
        mReviewStar = (ReviewStarView) view.findViewById(R.id.review_star);
        mTitleStudentNum = (TextView) view.findViewById(R.id.tv_title_student_num);
        mTitleDesc = (TextView) view.findViewById(R.id.tv_title_desc);
        mVipLayout = view.findViewById(R.id.vip_rlayout);
        mInfoLayout = view.findViewById(R.id.rl_info);
        mPeopleDesc = (TextView) view.findViewById(R.id.tv_people_desc);
        mStudentNum = (TextView) view.findViewById(R.id.tv_student_num);
        mVipDesc = (TextView) view.findViewById(R.id.tv_vip_desc);
        mStudentMore = view.findViewById(R.id.tv_student_more);
        mStudentIconLayout = (LinearLayout) view.findViewById(R.id.student_icon_llayout);
        mStudent = (TextView) view.findViewById(R.id.tv_student);
        mPeopleLayout = view.findViewById(R.id.people_rlayout);
        mStudentNone = view.findViewById(R.id.tv_student_none);
        mLoadView = view.findViewById(R.id.ll_detail_load);
        mDiscount = view.findViewById(R.id.tv_discount);

        mPresenter = new CourseIntroducePresenter(mCourseId + "", this);
    }

    protected void initEvent() {
        mStudentMore.setOnClickListener(this);
        mVipLayout.setOnClickListener(this);
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        mPresenter.subscribe();
    }

    @Override
    public void setData(CourseSet courseSet) {
        this.mCourseSet = courseSet;
    }

    @Override
    public void setLoadViewVis(boolean isVis) {
        mLoadView.setVisibility(isVis ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showHead() {
        mTitle.setText(mCourseSet.getTitle());
        mReviewStar.setRating((int) mCourseSet.getRating());
        int studentNum = mCourseSet.getStudentNum();
        mTitleStudentNum.setText(studentNum != 0 ?
                                    String.format("%s名学生", studentNum) : "");
        showCoursePrice();
    }

    private void showCoursePrice() {
        if (mCourseSet.getMaxCoursePrice() == 0) {
            mPriceNow.setText("免费");
            mPriceNow.setText(ContextCompat.getColor(getContext(), R.color.primary));
        } else {
            float discount = mCourseSet.getDiscount();
            if (discount != 10) {
                mDiscount.setVisibility(View.VISIBLE);
                mPriceNow.setText(String.format("¥ %s-%s", (int)(mCourseSet.getMinCoursePrice() * discount),
                        (int)(mCourseSet.getMaxCoursePrice() * discount)));
                mPriceOld.setText(((int) mCourseSet.getMaxCoursePrice()));
                mPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                mPriceNow.setText(String.format("¥ %s-%s", ((int) mCourseSet.getMinCoursePrice()),
                        ((int) mCourseSet.getMaxCoursePrice())));
            }
        }
    }


    @Override
    public void showInfoAndPeople() {
        mInfoLayout.setVisibility(View.VISIBLE);
        mTitleDesc.setText(Html.fromHtml(mCourseSet.getSummary(), new EduHtmlHttpImageGetter(mTitleDesc, null, true), null));
        StringBuilder sb = new StringBuilder();
        int length = mCourseSet.getAudiences().length;
        if (length == 0) {
            mPeopleLayout.setVisibility(View.GONE);
        } else {
            String[] audiences = mCourseSet.getAudiences();
            for (int i = 0; i < length; i++) {
                sb.append(audiences[i]);
                if (i != length - 1) {
                    sb.append("\n");
                }
            }
            mPeopleDesc.setText(sb);
        }
    }

    private void refreshView() {
        Course course = mCourseDetail.getCourse();
        if (mCourseDetail.getCourse().vipLevelId == 0) {
            mVipLayout.setVisibility(View.GONE);
        } else {
            mVipLayout.setVisibility(View.VISIBLE);
            mVipDesc.setText(String.format("加入%s，免费学习更多课程",
                    mCourseDetail.getVipLevels().size() > course.vipLevelId - 1 ?
                            mCourseDetail.getVipLevels().get(course.vipLevelId - 1).name : ""));
        }
    }

    @Override
    public void showStudent(List<CourseMember> data) {
        View.OnClickListener onClickListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = String.valueOf(v.getTag());
                        jumpToMember(id);
                    }
                };
        if (data.size() == 0) {
            mStudentNone.setVisibility(View.VISIBLE);
        } else {
            mStudentNone.setVisibility(View.GONE);
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
                ImageLoader.getInstance().displayImage(data.get(i).user.smallAvatar, image, EdusohoApp.app.mAvatarOptions);
            } else {
                txt.setText("");
                image.setImageAlpha(0);
            }
            mStudentIconLayout.addView(view);
        }
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

}
