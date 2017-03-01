package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by DF on 2017/3/1.
 */

public class TeachActivity extends ActionBarBaseActivity {


    private int mCourseId;
    private TextView mSummaryCourseTitle;
    private ImageView mSummaryCourseImage;
    private TextView mSummaryCourseIntroduction;
    private TextView mSummaryCourseTeacher;
    private TextView mSummaryTeacherTag;
    private RelativeLayout mSummaryFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK, "教学");
        setContentView(R.layout.activity_teach);

        initView();
        initData();
    }

    private void initView() {
        mSummaryFrame = (RelativeLayout) findViewById(R.id.process_lesson_summary_frame);
        mSummaryCourseTitle = (TextView) findViewById(R.id.study_process_lesson_summary_title);
        mSummaryCourseImage = (ImageView) findViewById(R.id.study_process_lesson_summary_image);
        mSummaryCourseIntroduction = (TextView) findViewById(R.id.study_process_lesson_summary_introduction);
        mSummaryTeacherTag = (TextView) findViewById(R.id.study_process_lesson_teacher_tag);
        mSummaryCourseTeacher = (TextView) findViewById(R.id.study_process_lesson_summary_teacher);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mCourseId = intent.getIntExtra(Const.COURSE_ID, 0);
        if (mCourseId == 0) {
            ToastUtils.show(mContext, "课程信息不存在!");
            return;
        }
        loadData();
    }

    private void loadData() {
        CourseDetailModel.getCourseDetail(mCourseId, new ResponseCallbackListener<CourseDetail>() {
            @Override
            public void onSuccess(CourseDetail data) {
                setData(data);
                loadFragment();
            }

            @Override
            public void onFailure(String code, String message) {

            }
        });
    }

    private void setData(CourseDetail data) {
        mSummaryCourseIntroduction.setText("课程简介：" + AppUtil.removeHtmlSpace(AppUtil.removeHtmlSpan(data.getCourse().about)));
        ImageLoader.getInstance().displayImage(data.getCourse().smallPicture, mSummaryCourseImage);
        mSummaryCourseTitle.setText(data.getCourse().title);
        if (data.getCourse().teachers.length == 0) {
            mSummaryTeacherTag.setVisibility(View.GONE);
        } else {
            mSummaryTeacherTag.setVisibility(View.VISIBLE);
            mSummaryCourseTeacher.setText(data.getCourse().teachers[0].nickname);
        }
        mSummaryFrame.setOnClickListener(getOnItemClickLestioner());
    }

    private View.OnClickListener getOnItemClickLestioner() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Const.COURSE_ID, mCourseId);
                app.mEngine.runNormalPluginWithBundle("CourseActivity", mContext, bundle);
            }
        };
    }

    private void loadFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = CoreEngine.create(mContext).runPluginWithFragment("TeachFragment", mContext, mTeachPluginFragmentCallback);
        fragmentTransaction.replace(R.id.fl_fragment, fragment);
        fragmentTransaction.commit();
    }

    private PluginFragmentCallback mTeachPluginFragmentCallback = new PluginFragmentCallback() {
        @Override
        public void setArguments(Bundle bundle) {
            School school = getAppSettingProvider().getCurrentSchool();
            String url = String.format(Const.MOBILE_APP_URL, school.url + "/", String.format(Const.TEACHER_MANAGERMENT, mCourseId));
            bundle.putString(Const.WEB_URL, url);
        }
    };
}
