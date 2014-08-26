package com.edusoho.kuozhi.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

import java.util.WeakHashMap;

/**
 * Created by howzhi on 14-8-7.
 */
public abstract class BaseFragment extends Fragment implements MessageEngine.MessageCallback {

    protected ActionBarBaseActivity mActivity;
    protected EdusohoApp app;
    protected int mViewId;
    protected View mContainerView;
    public String mTitle;
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (ActionBarBaseActivity) getActivity();
        mContext = mActivity.getBaseContext();
        app = mActivity.app;
        registMsgSrc();
    }

    protected void registMsgSrc(){
        app.registMsgSource(this);
    }

    @Override
    public MessageType[] getMsgTypes() {
        return null;
    }

    @Override
    public void onDestroy() {
        app.unRegistMsgSource(this);
        super.onDestroy();
    }

    @Override
    public void invoke(WidgetMessage message) {

    }

    public abstract String getTitle();

    public void setTitle(String title)
    {
        this.mTitle = title;
    }

    protected void setContainerView(int viewId)
    {
        mViewId = viewId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainerView = inflater.inflate(mViewId, null);
        initView(mContainerView);

        return mContainerView;
    }

    protected void initView(View view){
    }
}
