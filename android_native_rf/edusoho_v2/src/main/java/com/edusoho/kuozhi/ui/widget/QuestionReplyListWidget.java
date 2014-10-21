package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListAdapter;

import com.edusoho.kuozhi.adapter.QuestionReplyListAdapter;

/**
 * Created by hby on 14-9-18.
 * 单个问题回复列表
 */
public class QuestionReplyListWidget extends RefreshListWidget {
    private Context mContext;
    private QuestionReplyListAdapter mAdapter;
    private static final String TAG = "QuestionReplyListWidget";

    public QuestionReplyListWidget(Context context) {
        super(context);
        this.mContext = context;
    }

    public QuestionReplyListWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        try {
            if (adapter.isEmpty()) {
                adapter = getEmptyLayoutAdapter();
            } else {
                mAdapter = (QuestionReplyListAdapter) adapter;
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
