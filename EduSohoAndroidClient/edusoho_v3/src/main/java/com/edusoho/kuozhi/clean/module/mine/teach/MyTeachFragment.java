package com.edusoho.kuozhi.clean.module.mine.teach;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.mine.MineFragment;
import com.edusoho.kuozhi.v3.entity.lesson.TeachLesson;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

/**
 * Created by DF on 2017/2/28.
 */

public class MyTeachFragment extends BaseFragment implements MineFragment.RefreshFragment,MyTeachContract.View{

    private SwipeRefreshLayout mSrlContent;
    private RecyclerView mRvContent;
    private MyTeachAdapter mMyTeachAdapter;

    private MyTeachContract.Presenter mPresenter;

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
        mMyTeachAdapter = new MyTeachAdapter(getActivity());
        mRvContent.setAdapter(mMyTeachAdapter);
        mPresenter = new MyTeachPresenter(this);
    }

    private void loadData() {
        mSrlContent.setRefreshing(true);
        mPresenter.subscribe();
    }

    @Override
    public void refreshData() {
        loadData();
    }

    @Override
    public void hideSwpView() {
        mSrlContent.setRefreshing(false);
    }

    @Override
    public void showRequestComplete(TeachLesson teachLesson) {
        mMyTeachAdapter.setData(teachLesson.getResources());
    }

    @Override
    public void showToast(int resId) {

    }

    @Override
    public void showToast(String msg) {

    }

    public static class CourseTeachViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPic;
        View layoutLive;
        TextView tvLiveIcon;
        TextView tvLive;
        TextView tvTitle;
        TextView tvStudyState;
        TextView tvMore;
        View rLayoutItem;

        CourseTeachViewHolder(View view) {
            super(view);
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            layoutLive = view.findViewById(R.id.layout_live);
            tvLiveIcon = (TextView) view.findViewById(R.id.tv_live_icon);
            tvLive = (TextView) view.findViewById(R.id.tv_live);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvStudyState = (TextView) view.findViewById(R.id.tv_study_state);
            tvMore = (TextView) view.findViewById(R.id.tv_more);
            rLayoutItem = view.findViewById(R.id.rlayout_item);
        }
    }
}
