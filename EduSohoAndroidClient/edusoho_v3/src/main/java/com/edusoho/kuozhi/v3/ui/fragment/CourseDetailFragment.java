package com.edusoho.kuozhi.v3.ui.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;

/**
 * Created by Zhang on 2016/12/8.
 */

public class CourseDetailFragment extends BaseDetailFragment {

    private String mCourseId;

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

    }

    protected void initEvent() {

    }
}
