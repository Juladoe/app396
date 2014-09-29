package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.ui.widget.CourseDetailsGoalsWidget;
import com.edusoho.kuozhi.util.AppUtil;

import cn.trinea.android.common.util.FileUtils;
import cn.trinea.android.common.util.ResourceUtils;

/**
 * Created by howzhi on 14-8-31.
 */
public class CourseInfoFragment extends BaseFragment {

    public static final String COURSE = "course";

    private Course mCourse;
    private AQuery aQuery;

    private CourseDetailsGoalsWidget mCourseGoalsView;
    private CourseDetailsGoalsWidget mCourseAudiencesView;
    private CourseDetailsGoalsWidget mCourseAboutView;

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

        mCourseAudiencesView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_audiences);
        mCourseGoalsView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_goals);
        mCourseAboutView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_about);

        aQuery = new AQuery(view);
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

        mCourseGoalsView.setText(AppUtil.goalsToStr(mCourse.goals));
        mCourseAudiencesView.setText(AppUtil.audiencesToStr(mCourse.audiences));

        String template = ResourceUtils.geFileFromAssets(mContext, "template.html");
        mCourseAboutView.setHtml(template.replace("%content%", mCourse.about));
    }
}
