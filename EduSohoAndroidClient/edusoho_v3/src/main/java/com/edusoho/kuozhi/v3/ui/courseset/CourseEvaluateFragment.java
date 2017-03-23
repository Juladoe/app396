package com.edusoho.kuozhi.v3.ui.courseset;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by DF on 2017/3/21.
 */

public class CourseEvaluateFragment extends BaseLazyFragment {

    private RecyclerView mRvContent;
    private TextView mTvEmpty;
    private View mLoadView;

    @Override
    protected int initContentView() {
        return R.layout.fragment_course_evaluate;
    }

    @Override
    protected void initView(View view) {
        mRvContent = (RecyclerView) view.findViewById(R.id.rv_content);
        mTvEmpty = (TextView) view.findViewById(R.id.ll_discuss_empty);
        mLoadView = view.findViewById(R.id.ll_frame_load);
    }

    @Override
    protected void initEvent() {}

    @Override
    protected void lazyLoad() {

    }
}
