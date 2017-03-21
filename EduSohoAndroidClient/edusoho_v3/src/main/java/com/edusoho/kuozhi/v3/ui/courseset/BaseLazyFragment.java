package com.edusoho.kuozhi.v3.ui.courseset;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by DF on 2017/3/21.
 */

public abstract class BaseLazyFragment extends Fragment {

    protected boolean mIsVisible;
    private View mContentView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView tv = new TextView(getContext());
//        Layoutpar
        tv.setText("测试中");

        if (mContentView == null) {
//            mContentView = inflater.inflate(initContentView(), null);
            mContentView = tv;
        } else {
            ViewGroup viewGroup = (ViewGroup) mContentView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mContentView);
            }
        }
        return mContentView;
    }

    protected abstract int initContentView();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            mIsVisible = true;
            lazyLoad();
        } else {
            mIsVisible = false;
        }
    }

    protected abstract void lazyLoad();
}
