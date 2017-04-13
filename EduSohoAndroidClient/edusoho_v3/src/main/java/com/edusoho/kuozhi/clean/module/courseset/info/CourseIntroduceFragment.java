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
import com.edusoho.kuozhi.clean.module.courseset.BaseLazyFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMember;
import com.edusoho.kuozhi.v3.util.Const;
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
    private TextView mPeopleDesc;
    private TextView mStudentNum;
    private View mStudentMore;
    private LinearLayout mStudentIconLayout;
    private View mStudentNone;
    private View mLoadView;
    private View mPeopleLayout;
    private View mInfoLayout;

    private int mCourseSetId;
    private CourseIntroduceContract.Presenter mPresenter;
    private CourseSet mCourseSet;
    private View mDiscount;
    private int studentNum;

    public CourseIntroduceFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCourseSetId = getArguments().getInt(Const.COURSE_ID);
        mCourseSetId = 1;
    }

    @Override
    protected int initContentView() {
        return R.layout.fragment_course_introduce;
    }

    protected void initView(View view) {
        mPriceOld = (TextView) view.findViewById(R.id.tv_price_old);
        mPriceNow = (TextView) view.findViewById(R.id.tv_price_now);
        mTitle = (TextView) view.findViewById(R.id.tv_title);
        mReviewStar = (ReviewStarView) view.findViewById(R.id.review_star);
        mTitleStudentNum = (TextView) view.findViewById(R.id.tv_title_student_num);
        mTitleDesc = (TextView) view.findViewById(R.id.tv_title_desc);
        mInfoLayout = view.findViewById(R.id.rl_info);
        mPeopleDesc = (TextView) view.findViewById(R.id.tv_people_desc);
        mStudentNum = (TextView) view.findViewById(R.id.tv_student_num);
        mStudentMore = view.findViewById(R.id.tv_student_more);
        mStudentIconLayout = (LinearLayout) view.findViewById(R.id.student_icon_llayout);
        mPeopleLayout = view.findViewById(R.id.people_rlayout);
        mStudentNone = view.findViewById(R.id.tv_student_none);
        mLoadView = view.findViewById(R.id.ll_detail_load);
        mDiscount = view.findViewById(R.id.tv_discount);

        mPresenter = new CourseIntroducePresenter(mCourseSetId + "", this);
    }

    protected void initEvent() {
        mStudentMore.setOnClickListener(this);
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
        mTitle.setText(mCourseSet.title);
        mReviewStar.setRating((int) mCourseSet.rating);
        int studentNum = mCourseSet.studentNum;
        mTitleStudentNum.setText(studentNum != 0 ?
                                    String.format(getContext().getString(R.string.course_student_count), studentNum) : "");
        showCoursePrice();
    }

    private void showCoursePrice() {
        if (mCourseSet.maxCoursePrice == 0) {
            mPriceNow.setText(getContext().getString(R.string.txt_free));
            mPriceNow.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
        } else {
            float discount = mCourseSet.discount;
            if (discount != 10) {
                mDiscount.setVisibility(View.VISIBLE);
                mPriceNow.setText(String.format("¥ %.2f-%.2f", (mCourseSet.minCoursePrice * discount / 10),
                        (mCourseSet.maxCoursePrice * discount / 10)));
                mPriceOld.setText(String.format("¥ %.2f-%.2f", mCourseSet.minCoursePrice, mCourseSet.maxCoursePrice));
                mPriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                mPriceNow.setText(String.format("¥ %.2f-%.2f",  mCourseSet.minCoursePrice,
                         mCourseSet.maxCoursePrice));
            }
        }
    }

    @Override
    public void showInfoAndPeople() {
        if (mCourseSet.summary.length() > 0) {
            mInfoLayout.setVisibility(View.VISIBLE);
            mTitleDesc.setText(Html.fromHtml(mCourseSet.summary, new EduHtmlHttpImageGetter(mTitleDesc, null, true), null));
        }
        StringBuilder sb = new StringBuilder();
        int length = mCourseSet.audiences.length;
        if (length > 0) {
            mPeopleLayout.setVisibility(View.VISIBLE);
            String[] audiences = mCourseSet.audiences;
            for (int i = 0; i < length; i++) {
                sb.append(audiences[i]);
                if (i != length - 1) {
                    sb.append("、");
                }
            }
            mPeopleDesc.setText(sb);
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
        studentNum = data.size();
        if (data.size() == 0) {
            mStudentNone.setVisibility(View.VISIBLE);
        } else {
            mStudentNum.setText(String.format("(%s)", data.size()));
            mStudentNone.setVisibility(View.GONE);
        }
        for (int i = 0; i < 5; i++) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_detail_avatar, mStudentIconLayout, false);
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
        }
    }

    private void moreStudent() {
        if(studentNum < 1){
            return;
        }
        MobclickAgent.onEvent(getActivity(), "courseDetailsPage_introduction_moreCoursesParticipants");
        final String url = String.format(
                Const.MOBILE_APP_URL,
                EdusohoApp.app.schoolHost,
                String.format("main#/studentlist/%s/%s",
                        "courses", mCourseSetId)
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
