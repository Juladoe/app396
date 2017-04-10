package com.edusoho.kuozhi.clean.widget.ESRecyclerView;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by JesseHuang on 2017/4/8.
 */

public class SwipeRefreshLayoutOnRefresh implements SwipeRefreshLayout.OnRefreshListener {
    private ESPullAndLoadRecyclerView mPullLoadMoreRecyclerView;

    public SwipeRefreshLayoutOnRefresh(ESPullAndLoadRecyclerView pullLoadMoreRecyclerView) {
        this.mPullLoadMoreRecyclerView = pullLoadMoreRecyclerView;
    }

    @Override
    public void onRefresh() {
        if (!mPullLoadMoreRecyclerView.isRefresh()) {
            mPullLoadMoreRecyclerView.setIsRefresh(true);
            mPullLoadMoreRecyclerView.refresh();
        }
    }
}
