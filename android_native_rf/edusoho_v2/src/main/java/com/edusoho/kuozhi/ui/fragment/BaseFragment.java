package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.Service.EdusohoMainService;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.annotations.ViewUtil;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.nineoldandroids.animation.ObjectAnimator;

import java.lang.reflect.Field;
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

    public static final int DATA_UPDATE = 0010;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registMsgSrc();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActionBarBaseActivity) activity;
        mContext = mActivity.getBaseContext();
        app = mActivity.app;
    }

    protected void viewBind(ViewGroup contentView)
    {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                ViewUtil viewUtil = field.getAnnotation(ViewUtil.class);
                if (viewUtil != null) {
                    int id = getResources().getIdentifier(
                            viewUtil.value(), "id", mContext.getPackageName());
                    Log.d(null, "viewUtil->id " + id);
                    field.set(this, contentView.findViewById(id));
                }
            }

        } catch (Exception e) {
            //nothing
        }
    }

    protected void viewInject(View contentView)
    {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                ViewUtil viewUtil = field.getAnnotation(ViewUtil.class);
                if (viewUtil != null) {
                    int id = getResources().getIdentifier(
                            viewUtil.value(), "id", mContext.getPackageName());
                    Log.d(null, "viewUtil->id " + id);
                    field.set(this, contentView.findViewById(id));
                }
            }
        } catch (Exception e) {
            //nothing
        }
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

    protected void showProgress(boolean isShow)
    {
        mActivity.setProgressBarIndeterminateVisibility(isShow);
    }

    protected void showBtnLayout(View view)
    {
        view.measure(0, 0);
        int height = view.getMeasuredHeight();
        AppUtil.animForHeight(new EdusohoAnimWrap(view), 0, height, 240);
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
        if (mContainerView == null) {
            mContainerView = inflater.inflate(mViewId, null);
            initView(mContainerView);
        }

        ViewGroup parent = (ViewGroup) mContainerView.getParent();
        if (parent != null) {
            parent.removeView(mContainerView);
        }
        return mContainerView;
    }

    protected void initView(View view){
    }
}
