package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.MyTeachAdapter;
import com.edusoho.kuozhi.v3.entity.lesson.TeachLesson;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

/**
 * Created by DF on 2017/2/28.
 */

public class MyTeachFragment extends BaseFragment implements MineFragment.RefreshFragment {

    private SwipeRefreshLayout mSrlContent;
    private RecyclerView mRvContent;
    private MyTeachAdapter mMyTeachAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_mine_tab);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mSrlContent = (SwipeRefreshLayout) view.findViewById(R.id.srl_content);
        mSrlContent.setColorSchemeResources(R.color.primary_color);

        mRvContent = (RecyclerView) view.findViewById(R.id.rv_content);
        mRvContent.setLayoutManager(new LinearLayoutManager(getActivity()));

        View viewBreakline = view.findViewById(R.id.v_breakline);
        viewBreakline.setVisibility(View.GONE);

        initData();
        loadData();
        mSrlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
    }

    private void initData() {
        showLoadingView();
        mMyTeachAdapter = new MyTeachAdapter(getActivity());
        mRvContent.setAdapter(mMyTeachAdapter);
    }

    private void loadData(){
        CourseDetailModel.getTeach(new ResponseCallbackListener<TeachLesson>() {
            @Override
            public void onSuccess(TeachLesson data) {
                disabledLoadingView();
                mMyTeachAdapter.setData(data.getResources());
            }

            @Override
            public void onFailure(String code, String message) {
                disabledLoadingView();
            }
        });
    }

    private void showLoadingView() {
        mSrlContent.post(new Runnable() {
            @Override
            public void run() {
                mSrlContent.setRefreshing(true);
            }
        });
    }

    private void disabledLoadingView() {
        mSrlContent.setRefreshing(false);
    }

    @Override
    public void refreshData() {
        loadData();
    }

    @Override
    public void setSwipeEnabled(int i) {
        mSrlContent.setEnabled(i == 0);
    }


    public static class CourseTeachViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivPic;
        public View layoutLive;
        public TextView tvLiveIcon;
        public TextView tvLive;
        public TextView tvTitle;
        public TextView tvStudyState;
        public TextView tvMore;
        public View layoutClass;
        public View rLayoutItem;

        public CourseTeachViewHolder(View view) {
            super(view);
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            layoutLive = view.findViewById(R.id.layout_live);
            tvLiveIcon = (TextView) view.findViewById(R.id.tv_live_icon);
            tvLive = (TextView) view.findViewById(R.id.tv_live);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvStudyState = (TextView) view.findViewById(R.id.tv_study_state);
            tvMore = (TextView) view.findViewById(R.id.tv_more);
            layoutClass = view.findViewById(R.id.layout_class);
            rLayoutItem = view.findViewById(R.id.rlayout_item);
        }
    }
}
