package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;

import com.edusoho.kuozhi.adapter.QuestionListAdapter;

/**
 * Created by hby on 14-9-16.
 * 问题列表List
 */
public class QuestionRefreshListWidget extends RefreshListWidget {
    private Context mContext;
    private QuestionListAdapter mAdapter;

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
        if (adapter.isEmpty()) {
            adapter = getEmptyLayoutAdapter();
        }
        mAdapter = (QuestionListAdapter) adapter;
        super.setAdapter(adapter);
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }
}
