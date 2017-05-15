package com.edusoho.kuozhi.clean.module.mine.favorite;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.module.mine.MineFragment;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

import java.util.List;

/**
 * Created by JesseHuang on 2017/2/7.
 */

public class MyFavoriteFragment extends BaseFragment implements MineFragment.RefreshFragment, MyFavoriteContract.View {

    private SwipeRefreshLayout srlContent;
    private RecyclerView rvContent;
    private MyFavoriteAdapter myFavoriteAdapter;

    private MyFavoriteContract.Presenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_mine_tab);
    }

    @Override
    protected void initView(View view) {
        srlContent = (SwipeRefreshLayout) view.findViewById(R.id.srl_content);
        srlContent.setColorSchemeResources(R.color.primary_color);

        rvContent = (RecyclerView) view.findViewById(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));

        view.findViewById(R.id.v_breakline).setVisibility(View.GONE);

        initData();
        srlContent.setRefreshing(true);
        mPresenter.subscribe();
        srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.subscribe();
            }
        });
    }

    private void initData() {
        myFavoriteAdapter = new MyFavoriteAdapter(getActivity());
        rvContent.setAdapter(myFavoriteAdapter);
        mPresenter = new MyFavoritePresenter(this);
    }

    @Override
    public void refreshData() {
        srlContent.setRefreshing(true);
        mPresenter.subscribe();
    }

    @Override
    public void showComplete(List<CourseSet> courseSets) {
        myFavoriteAdapter.setData(courseSets);
    }

    @Override
    public void setSwpFreshing(boolean isRefreshing) {
        srlContent.setRefreshing(isRefreshing);
    }

    @Override
    public void showToast(int resId) {

    }

    @Override
    public void showToast(String msg) {

    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        View recyclerViewItem;
        ImageView ivPic;
        TextView tvAddNum;
        TextView tvTitle;
        TextView tvMore;
        View layoutLive;
        View vLine;

        FavoriteViewHolder(View view) {
            super(view);
            recyclerViewItem = view.findViewById(R.id.llayout_favorite_content);
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            tvAddNum = (TextView) view.findViewById(R.id.tv_add_num);
            tvMore = (TextView) view.findViewById(R.id.tv_more);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            vLine = view.findViewById(R.id.v_line);
            layoutLive = view.findViewById(R.id.layout_live);
        }
    }
}
