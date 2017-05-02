package com.edusoho.kuozhi.clean.module.courseset.review;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseReview;
import com.edusoho.kuozhi.clean.module.courseset.BaseLazyFragment;
import com.edusoho.kuozhi.clean.module.courseset.CourseUnLearnActivity;

import java.util.List;

/**
 * Created by DF on 2017/3/21.
 * 课程-评价界面
 */

public class CourseEvaluateFragment extends BaseLazyFragment<CourseEvaluateContract.Presenter> implements CourseEvaluateContract.View {

    private RecyclerView mContent;
    private TextView mEmpty;
    private View mLoadView;
    private CourseEvaluateAdapter mCeAdapter;
    private CourseEvaluateContract.Presenter mPresenter;
    private int mCourseSetId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCourseSetId = getArguments().getInt(CourseUnLearnActivity.COURSE_SET_ID);
    }

    @Override
    protected int initContentView() {
        return R.layout.fragment_course_evaluate;
    }

    @Override
    protected void initView(View view) {
        mContent = (RecyclerView) view.findViewById(R.id.rv_content);
        mEmpty = (TextView) view.findViewById(R.id.ll_discuss_empty);
        mLoadView = view.findViewById(R.id.ll_frame_load);
        mCeAdapter = new CourseEvaluateAdapter(getContext());
        mContent.setLayoutManager(new LinearLayoutManager(getContext()));
        mContent.setAdapter(mCeAdapter);

        mPresenter = new CourseEvaluatePresenter(this, mCourseSetId);
    }

    @Override
    protected void initEvent() {
        mContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == mCeAdapter.getItemCount() - 1) {
                    mCeAdapter.changeMoreStatus(CourseEvaluateAdapter.LOADING_MORE);
                    mPresenter.loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        mPresenter.subscribe();
    }

    @Override
    public void showCompanies(List<CourseReview.DataBean> list) {
        if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
            return;
        }
        mCeAdapter.reFreshData(list);
    }

    @Override
    public void setRecyclerViewStatus(int status) {
        if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
            return;
        }
        mCeAdapter.setStatus(status);
    }

    @Override
    public void addData(List<CourseReview.DataBean> list) {
        mCeAdapter.setStatus(CourseEvaluateAdapter.NO_LOAD_MORE);
        mCeAdapter.addData(list);
    }

    @Override
    public void changeMoreStatus(int status) {
        if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
            return;
        }
        mCeAdapter.changeMoreStatus(status);
    }

    @Override
    public void setLoadViewVis(boolean isVis) {
        if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
            return;
        }
        mLoadView.setVisibility(isVis ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEmptyViewVis(boolean isVis) {
        if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
            return;
        }
        mEmpty.setVisibility(isVis ? View.VISIBLE : View.GONE);
    }
}
