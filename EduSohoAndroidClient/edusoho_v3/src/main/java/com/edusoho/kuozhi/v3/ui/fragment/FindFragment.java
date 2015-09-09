package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.webview.ESWebViewRequestManager;
import java.io.File;

/**
 * Created by JesseHuang on 15/4/26.
 */
public class FindFragment extends ESWebViewFragment{

    private static final String TAG = "FindFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.setTitle(getString(R.string.title_find));
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mWebView.loadApp("main");
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{
                new MessageType(Const.CLEAR_APP_CACHE)
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        invokeUIMessage();
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (Const.CLEAR_APP_CACHE.equals(messageType.type)) {
            if (getRunStatus() == MSG_PAUSE) {
                saveMessage(message);
                return;
            }
            ESWebViewRequestManager.clear();
            checkLocalResourceStatus();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        setHasOptionsMenu(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mActivity.setTitle(getString(R.string.title_find));
        }
        super.onHiddenChanged(hidden);
    }

    private void checkLocalResourceStatus() {
        File schoolStorage = AppUtil.getSchoolStorage(app.domain);
        File appDir = new File(schoolStorage, "main");

        if (!appDir.exists()) {
            mWebView.loadApp("main");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.find_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
        if (mWebView != null) {
            mWebView.destroy();
            ((ViewGroup)getView()).removeView(mWebView);
            mWebView = null;
        }
    }

}
