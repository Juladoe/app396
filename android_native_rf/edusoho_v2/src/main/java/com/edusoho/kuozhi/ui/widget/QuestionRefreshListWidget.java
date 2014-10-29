package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListAdapter;

import com.edusoho.kuozhi.adapter.QuestionListAdapter;

/**
 * Created by hby on 14-9-16.
 * 问题列表List
 */
public class QuestionRefreshListWidget extends RefreshListWidget {
    private Context mContext;
    private QuestionListAdapter mAdapter;
    private static final String TAG = "QuestionRefreshListWidget";

    public QuestionRefreshListWidget(Context context) {
        super(context);
        this.mContext = context;
    }

    public QuestionRefreshListWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        try {
            if (adapter.isEmpty()) {
                adapter = getEmptyLayoutAdapter();
            } else {
                mAdapter = (QuestionListAdapter) adapter;
            }
            super.setAdapter(adapter);
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }
}
