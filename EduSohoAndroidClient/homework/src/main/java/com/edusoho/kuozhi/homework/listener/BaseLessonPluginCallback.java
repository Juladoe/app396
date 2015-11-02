package com.edusoho.kuozhi.homework.listener;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.homework.R;
import com.edusoho.kuozhi.homework.model.HomeWorkModel;
import com.edusoho.kuozhi.homework.model.HomeworkProvider;
import com.edusoho.kuozhi.v3.listener.LessonPluginCallback;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.Const;

/**
 * Created by howzhi on 15/11/2.
 */

public abstract class BaseLessonPluginCallback implements LessonPluginCallback, NormalCallback<VolleyError>
{
    protected Context mContext;
    protected View mView;
    protected View mLoadView;
    private Object mLock = new Object();

    public BaseLessonPluginCallback(Context context)
    {
        this.mContext = context;
    }

    protected void addLoadView() {
        mLoadView = LayoutInflater.from(mContext).inflate(R.layout.plugin_loading_layout, null);
        ((ViewGroup)mView).addView(mLoadView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    protected void removeLoadView() {
        ((ViewGroup)mView).removeView(mLoadView);
    }

    @Override
    public boolean click(AdapterView<?> parent, View view, int position) {
        return false;
    }

    @Override
    public void initPlugin(View view, Bundle bundle) {
        synchronized (mLock) {
            if (mView != null) {
                return;
            }
        }

        this.mView = view;
        addLoadView();
        int lessonId = bundle.getInt(Const.LESSON_ID, 0);
        checkHasHomeWork(lessonId);
    }

    protected abstract RequestUrl getRequestUrl(int lessonId);

    protected void checkHasHomeWork(int lessonId) {
        setViewEnabled(false);
        RequestUrl requestUrl = getRequestUrl(lessonId);
        HomeworkProvider provider = ModelProvider.initProvider(mContext, HomeworkProvider.class);
        provider.getHomeWork(requestUrl).success(new NormalCallback<HomeWorkModel>() {
            @Override
            public void success(HomeWorkModel homeWorkModel) {
                removeLoadView();
                if (homeWorkModel != null){
                    setViewEnabled(true);
                }
            }
        }).fail(this);
    }

    private void setViewEnabled(boolean status) {
        mView.setEnabled(status);
        ViewGroup viewGroup = ((ViewGroup)mView);
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            viewGroup.getChildAt(i).setEnabled(status);
        }
    }

    @Override
    public void success(VolleyError obj) {
    }
}