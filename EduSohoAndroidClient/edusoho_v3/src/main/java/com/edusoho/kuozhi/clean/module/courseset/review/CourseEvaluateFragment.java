package com.edusoho.kuozhi.clean.module.courseset.review;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.courseset.BaseLazyFragment;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.bal.course.CourseReviewDetail;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;

/**
 * Created by DF on 2017/3/21.
 */

public class CourseEvaluateFragment extends BaseLazyFragment {

    private RecyclerView mRvContent;
    private TextView mTvEmpty;
    private View mLoadView;
    private CourseEvaluateAdapter mCeAdapter;
    private int mCourseId;
    private int mStart = 0;
    private boolean mIsHave;
    private boolean mIsFirst = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCourseId = getArguments().getInt(Const.COURSE_ID);
    }

    @Override
    protected int initContentView() {
        return R.layout.fragment_course_evaluate;
    }

    @Override
    protected void initView(View view) {
        mRvContent = (RecyclerView) view.findViewById(R.id.rv_content);
        mTvEmpty = (TextView) view.findViewById(R.id.ll_discuss_empty);
        mLoadView = view.findViewById(R.id.ll_frame_load);
        mCeAdapter = new CourseEvaluateAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvContent.setAdapter(mCeAdapter);
    }

    @Override
    protected void initEvent() {
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == mCeAdapter.getItemCount() - 1) {
                    mCeAdapter.changeMoreStatus(CourseEvaluateAdapter.LOADING_MORE);
                    if (!mIsHave) {
                        if (mIsFirst) {
                            mIsFirst = false;
                            CommonUtil.shortCenterToast(getContext(), getString(R.string.discuss_load_data_finish));
                        }
                    }
                    CourseDetailModel.getCourseReviews(mCourseId, "10", mStart + "",
                            new ResponseCallbackListener<CourseReviewDetail>() {
                                @Override
                                public void onSuccess(CourseReviewDetail data) {
                                    if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
                                        return;
                                    }
                                    int length = data.getData().size();
                                    mStart += 10;
                                    mIsHave = length > 15;
                                    for (int i = 0; i < length; i++) {
                                        if (!data.getData().get(i).parentId.equals("0")) {
                                            data.getData().remove(i);
                                            i--;
                                            length--;
                                        }
                                    }
                                    mCeAdapter.setStatus(CourseEvaluateAdapter.NO_LOAD_MORE);
                                    mCeAdapter.addData(data.getData());
                                }

                                @Override
                                public void onFailure(String code, String message) {
                                    mCeAdapter.changeMoreStatus(CourseEvaluateAdapter.NO_LOAD_MORE);
                                }
                            });
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
        initData();
    }

    private void initData() {
        CourseDetailModel.getCourseReviews(mCourseId, "15", "0",
                new ResponseCallbackListener<CourseReviewDetail>() {
                    @Override
                    public void onSuccess(CourseReviewDetail data) {
                        if (getActivity() == null || getActivity().isFinishing() || !isAdded()) {
                            return;
                        }
                        int length = data.getData().size();
                        if(length < 15){
                            mIsHave = false;
                            mCeAdapter.setStatus(CourseEvaluateAdapter.NO_LOAD_MORE);
                        }
                        for (int i = 0; i < length; i++) {
                            if (!data.getData().get(i).parentId.equals("0")) {
                                data.getData().remove(i);
                                i--;
                                length--;
                            }
                        }
                        mStart += 10;
                        mLoadView.setVisibility(View.GONE);
                        if (data.getData().size() == 0) {
                            mTvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            mCeAdapter.reFreshData(data.getData());
                        }
                    }

                    @Override
                    public void onFailure(String code, String message) {
                        mLoadView.setVisibility(View.GONE);
                    }
                });
    }


}
