package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;
import library.PullToRefreshBase;

/**
 * Created by howzhi on 14-8-25.
 */
public class RefreshListWidget extends BaseRefreshListWidget<ListView> {

    private UpdateListener mUpdateListener;
    public RefreshListWidget(Context context) {
        super(context);
    }

    public RefreshListWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public interface UpdateListener {
        public void update(PullToRefreshBase<ListView> refreshView);

        public void refresh(PullToRefreshBase<ListView> refreshView);
    }

    public void setUpdateListener(UpdateListener updateListener) {
        mUpdateListener = updateListener;
        setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mMode = REFRESH;
                Log.d(TAG, "refresh->");
                mUpdateListener.refresh(refreshView);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mMode = UPDATE;
                Log.d(TAG, "update->");
                mUpdateListener.update(refreshView);
            }
        });
    }
}
