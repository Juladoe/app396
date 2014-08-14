package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

/**
 * Created by howzhi on 14-8-7.
 */
public class BaseFragment extends Fragment {

    protected ActionBarBaseActivity mActivity;
    protected EdusohoApp app;
    protected int mViewId;
    protected View mContainerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (ActionBarBaseActivity) getActivity();
        app = mActivity.app;
    }

    protected void setContainerView(int viewId)
    {
        mViewId = viewId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainerView = inflater.inflate(mViewId, container, false);
        initView(mContainerView);
        return mContainerView;
    }

    protected void initView(View view){
    }
}
