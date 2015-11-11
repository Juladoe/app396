package com.edusoho.kuozhi.v3.listener;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.view.dialog.ExerciseOptionDialog.GridViewItem;

/**
 * Created by howzhi on 15/11/2.
 */

public abstract class BaseLessonPluginCallback implements LessonPluginCallback, NormalCallback<VolleyError>
{
    protected Context mContext;
    private int mPosition;
    protected BaseAdapter mAdapter;
    private Object mLock = new Object();

    public BaseLessonPluginCallback(Context context)
    {
        this.mContext = context;
    }

    @Override
    public boolean click(AdapterView<?> parent, View view, int position) {
        return false;
    }

    @Override
    public void initPlugin(BaseAdapter adapter, int postion) {
        synchronized (mLock) {
            if (this.mAdapter != null) {
                return;
            }
        }
        this.mAdapter = adapter;
        this.mPosition = postion;

        GridViewItem item = (GridViewItem) mAdapter.getItem(postion);
        item.status = GridViewItem.LOAD;
        mAdapter.notifyDataSetInvalidated();
        loadPlugin(item.bundle);
    }

    protected abstract RequestUrl getRequestUrl(int lessonId);

    protected abstract void loadPlugin(Bundle bundle);

    protected void setViewStatus(boolean status) {
        GridViewItem item = (GridViewItem) mAdapter.getItem(mPosition);
        item.status = status ? GridViewItem.ENABLE : GridViewItem.UNENABLE;
        mAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void success(VolleyError obj) {
        setViewStatus(false);
    }
}