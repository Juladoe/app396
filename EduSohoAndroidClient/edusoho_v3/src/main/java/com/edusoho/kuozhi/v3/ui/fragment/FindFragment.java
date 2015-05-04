package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

/**
 * Created by JesseHuang on 15/4/26.
 */
public class FindFragment extends BaseFragment {
    @Override
    public void onAttach(Activity activity) {
        Log.d("FindFragment-->", "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_webview);
        Log.d("FindFragment-->", "onCreate");
        mActivity.setTitle(getString(R.string.title_find));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FindFragment-->", "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("FindFragment-->", "onActivityCreated");
        setHasOptionsMenu(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mActivity.setTitle(getString(R.string.title_find));
        }
        super.onHiddenChanged(hidden);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.find_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.find_search) {

            //TODO 跳转到搜索页面
            app.mEngine.runNormalPlugin("", mActivity, null);
        }
        return super.onOptionsItemSelected(item);
    }
}
