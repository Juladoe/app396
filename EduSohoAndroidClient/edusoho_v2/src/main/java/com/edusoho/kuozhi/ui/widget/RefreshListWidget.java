package com.edusoho.kuozhi.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.EmptyAdapter;
import com.edusoho.kuozhi.adapter.EmptyPageAdapter;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

import library.PullToRefreshBase;

/**
 * Created by howzhi on 14-8-25.
 */
public class RefreshListWidget extends BaseRefreshListWidget<ListView> {

    private UpdateListener mUpdateListener;

    private int mLogoutIcon;
    private int mNoDataIcon;
    private String[] mLoginText;
    private String[] mLogoutText;
    private int mRecourseId;
    protected boolean mIsLogin;
    private ActionBarBaseActivity mActivity;

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

    /**
     * 空数据显示
     * @param activity
     * @param layoutId
     * @param logoutText
     * @param loginText
     * @param logoutIcon
     * @param noDataIcon
     */
    public void setEmptyText(ActionBarBaseActivity activity, int layoutId, String[] logoutText, String[] loginText, int logoutIcon, int noDataIcon) {
        mActivity = activity;
        mRecourseId = layoutId;
        mLogoutIcon = logoutIcon;
        mNoDataIcon = noDataIcon;
        mLogoutText = logoutText;
        mLoginText = loginText;
    }

    @Override
    public EmptyAdapter getEmptyLayoutAdapter() {
        if (mRecourseId != 0) {
            if (mEmptyAdapter == null) {
                mEmptyAdapter = new EmptyPageAdapter<String>(mContext, mActivity, mRecourseId, mLogoutText, mLoginText, mLogoutIcon, mNoDataIcon, mIsLogin);
            }
            return mEmptyAdapter;
        } else {
            return super.getEmptyLayoutAdapter();
        }
    }

    public void setLoginStatus(boolean isLogin) {
        mIsLogin = isLogin;
        if (mEmptyAdapter != null) {
            mEmptyAdapter.setLoginStatus(isLogin);
        }
    }

}
