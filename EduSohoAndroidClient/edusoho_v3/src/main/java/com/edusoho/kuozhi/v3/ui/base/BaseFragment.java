package com.edusoho.kuozhi.v3.ui.base;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.ViewUtil;
import com.edusoho.kuozhi.v3.view.EdusohoAnimWrap;

import java.lang.reflect.Field;

/**
 * Created by JesseHuang on 15/4/24.
 */
public abstract class BaseFragment extends Fragment {

    protected ActionBarBaseActivity mActivity;
    protected EdusohoApp app;
    protected int mViewId;
    protected View mContainerView;
    public String mTitle;
    protected Context mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActionBarBaseActivity) activity;
        mContext = mActivity.getBaseContext();
        app = mActivity.app;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (app == null) {
            app = EdusohoApp.app;
        }
    }

    protected void setContainerView(int viewId) {
        mViewId = viewId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        saveViewState(savedInstanceState);
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

    protected void viewBind(ViewGroup contentView) {
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


    protected void viewInject(View contentView) {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                ViewUtil viewUtil = field.getAnnotation(ViewUtil.class);
                if (viewUtil != null) {
                    int id = getResources().getIdentifier(
                            viewUtil.value(), "id", mContext.getPackageName());
                    field.set(this, contentView.findViewById(id));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showProgress(boolean isShow) {
        mActivity.setProgressBarIndeterminateVisibility(isShow);
    }

    protected void showBtnLayout(View view) {
        view.measure(0, 0);
        int height = view.getMeasuredHeight();
        CommonUtil.animForHeight(new EdusohoAnimWrap(view), 0, height, 240);
    }

    protected void startActivityWithBundle(String activityName, Bundle bundle) {
        app.mEngine.runNormalPluginWithBundle(activityName, mActivity, bundle);
    }

    protected void startActivityWithBundleAndResult(String activityName, int request, final Bundle bundle) {
        app.mEngine.runPluginFromFragmentFroResult(activityName, this, request, bundle);
    }

    protected void startActivity(String activityName, PluginRunCallback callback) {
        app.mEngine.runNormalPlugin(activityName, mActivity, callback);
    }

    protected void saveViewState(Bundle savedInstanceState) {
    }

    protected void initView(View view) {
    }
}
