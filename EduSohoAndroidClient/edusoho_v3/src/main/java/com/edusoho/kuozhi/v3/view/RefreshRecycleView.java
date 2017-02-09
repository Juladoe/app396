package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.v3.adapter.discuss.CatalogueAdapter;

import java.util.ArrayList;

/**
 * Created by DF on 2017/2/9.
 */

public class RefreshRecycleView extends RecyclerView {

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;

    CatalogueAdapter myWrapAdapter;
    View headerView, footerView;

    private int mState = STATE_NORMAL;
    boolean isOnTouching;
    TextView status;
    boolean isRefresh;

    public MyRecyclerViewListener getMyRecyclerViewListener() {
        return myRecyclerViewListener;
    }

    public void setMyRecyclerViewListener(MyRecyclerViewListener myRecyclerViewListener) {
        this.myRecyclerViewListener = myRecyclerViewListener;
    }

    MyRecyclerViewListener myRecyclerViewListener;

    public RefreshRecycleView(Context context) {
        super(context);
    }

    public RefreshRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setLayoutManager(final LayoutManager layout) {
        super.setLayoutManager(layout);

        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isRefresh) {
                    return;
                }
                if (mState != STATE_NORMAL) {
                    return;
                }
                //判断是否最后一item个显示出来
                LayoutManager layoutManager = getLayoutManager();

                //可见的item个数
                int visibleChildCount = layoutManager.getChildCount();
                if (visibleChildCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && !isLoadMore) {
                    View lastVisibleView = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    int lastVisiblePosition = recyclerView.getChildLayoutPosition(lastVisibleView);
                    if (lastVisiblePosition >= layoutManager.getItemCount() - 1) {
                        footerView.setVisibility(VISIBLE);
                        isLoadMore = true;
                        if (myRecyclerViewListener != null) {
                            myRecyclerViewListener.onLoadMore();
                        }
                    } else {
                        footerView.setVisibility(GONE);
                    }
                }
            }
        });
    }

    boolean isLoadMore;

    @Override
    public void setAdapter(Adapter adapter) {
        ArrayList<View> footers = new ArrayList<>();
        LinearLayout footerLayout = new LinearLayout(getContext());
        footerLayout.setGravity(Gravity.CENTER);
        footerLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        footers.add(footerLayout);
        footerLayout.setPadding(0,15,0,15);
        footerLayout.addView(new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall));

        TextView text = new TextView(getContext());
        text.setText("正在加载...");
        footerLayout.addView(text);
        footerView=footerLayout;
        footerView.setVisibility(GONE);

        myWrapAdapter = new CatalogueAdapter(adapter, footers);
        super.setAdapter(myWrapAdapter);
    }

    public interface MyRecyclerViewListener {
        void onLoadMore();
    }

    public void setLoadMoreComplete() {
        footerView.setVisibility(GONE);
        isLoadMore = false;
    }
}
