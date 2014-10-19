package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.Member;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.ui.widget.CourseDetailsGoalsWidget;
import com.edusoho.kuozhi.util.AppUtil;

import cn.trinea.android.common.util.ResourceUtils;

/**
 * Created by howzhi on 14-8-31.
 */
public class CourseInfoFragment extends BaseFragment {

    public static final String COURSE = "course";
    public static final String MEMBER = "merber";

    private Course mCourse;
    private Member mMember;
    private AQuery aQuery;

    private CourseDetailsGoalsWidget mCourseGoalsView;
    private CourseDetailsGoalsWidget mCourseAudiencesView;
    private CourseDetailsGoalsWidget mCourseAboutView;

    private TextView mExpiryText;

    @Override
    public String getTitle() {
        return "课程";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_info_fragment);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        Bundle bundle = getArguments();
        if (bundle == null) {
            mActivity.longToast("无效课程信息！");
            return;
        }

        mCourse = (Course) bundle.getSerializable(COURSE);
        mMember = (Member) bundle.getSerializable(MEMBER);

        mExpiryText = (TextView) view.findViewById(R.id.course_details_info_expiry);
        mCourseAudiencesView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_audiences);
        mCourseGoalsView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_goals);
        mCourseAboutView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_about);

        aQuery = new AQuery(view);
        aQuery.id(R.id.course_details_favorite).visibility(View.INVISIBLE);
        aQuery.id(R.id.course_details_info_title).text(mCourse.title);
        aQuery.id(R.id.course_details_info_subtitle).text(mCourse.subtitle);

        Teacher teacher = null;
        if (mCourse.teachers != null && mCourse.teachers.length > 0) {
            teacher = mCourse.teachers[0];
            aQuery.id(R.id.course_details_info_teacher).text("教师:" + teacher.nickname);
        }

        aQuery.id(R.id.course_details_rating).rating((float)mCourse.rating);
        String price = mCourse.price <= 0 ? "免费" : "￥" + mCourse.price;
        aQuery.id(R.id.course_details_price).text(price);
        if (mCourse.expiryDay > 0) {
            aQuery.id(R.id.course_details_info_expiry).text("有效期:" + mCourse.expiryDay + "天");
        }
        aQuery.id(R.id.course_details_studentNum).text(mCourse.studentNum + "学员");

        String goals = AppUtil.goalsToStr(mCourse.goals);
        if (TextUtils.isEmpty(goals)) {
            mCourseGoalsView.setVisibility(View.GONE);
        }
        mCourseGoalsView.setText(goals);

        String audiences = AppUtil.audiencesToStr(mCourse.audiences);
        if (TextUtils.isEmpty(audiences)) {
            mCourseAudiencesView.setVisibility(View.GONE);
        }
        mCourseAudiencesView.setText(audiences);

        String template = ResourceUtils.geFileFromAssets(mContext, "template.html");
        String about = template.replace("%content%", mCourse.about);
        if (TextUtils.isEmpty(about)) {
            mCourseAudiencesView.setVisibility(View.GONE);
        }

        mCourseAboutView.setHtml(about);
        checkMember();
    }

    private void checkMember()
    {
        if (mMember != null) {
            if (mMember.deadline == -1) {
                mExpiryText.setText("已过期");
            } else {
                String format = "有效期:%s";
                mExpiryText.setText(String.format(format, getExpiryDay(mMember.deadline)));
            }
        } else {
            if (mCourse.expiryDay == 0) {
                mExpiryText.setVisibility(View.GONE);
            } else {
                mExpiryText.setText("有效期" + mCourse.expiryDay + "天");
            }
        }
    }

    private String getExpiryDay(long remain)
    {
        if (remain <= 3600) {
            return Math.round(remain / 60) + "分钟";
        }
        if (remain < 86400){
            return  Math.round(remain / 3600) + "小时";
        }

        return  Math.round(remain / 86400) + "天";
    }
}
