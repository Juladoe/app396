package com.edusoho.kuozhi.v3.ui.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;

/**
 * Created by Zhang on 2016/12/8.
 */

public class CourseDetailFragment extends BaseDetailFragment {

    private String mCourseId;
    private CourseDetail mCourseDetail;

    public CourseDetailFragment() {
    }

    public CourseDetailFragment(String courseId) {
        this.mCourseId = courseId;
    }

    public void setCourseId(String courseId) {
        this.mCourseId = courseId;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }

    protected void initData() {
        CourseDetailModel.getCourseDetail(mCourseId, new ResponseCallbackListener<CourseDetail>() {
            @Override
            public void onSuccess(CourseDetail data) {
                mCourseDetail = data;
                refreshView();
            }

            @Override
            public void onFailure(String code, String message) {
                if (message.equals("课程不存在")) {
                    CommonUtil.shortToast(mContext, "课程不存在");
                }
            }
        });
    }

    @Override
    protected void refreshView() {
        mTvTitle.setText(mCourseDetail.getCourse().title);
        mTvTitleDesc.setText(mCourseDetail.getCourse().about);
//        m
//        if(mCourseDetail.get)
    }

    @Override
    protected void moreStudent() {

    }

    @Override
    protected void moreReview() {

    }

    @Override
    protected void vipInfo() {

    }
}
