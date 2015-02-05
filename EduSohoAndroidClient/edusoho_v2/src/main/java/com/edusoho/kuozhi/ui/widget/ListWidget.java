package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import library.PullToRefreshBase;
import library.PullToRefreshListView;

/**
 * Created by howzhi on 14-9-17.
 */
public class ListWidget extends FrameLayout {

    protected Context mContext;
    protected View mLoadView;
    protected View mEmptyView;
    protected TextView mEmptyText;
    protected String mEmptyStr;
    private RefreshListWidget mResourceListView;
    private int mMode;
    private PullToRefreshBase.Mode mPullMode;

    public static final int BOTH = 0;
    public static final int PULL_FROM_START = 1;
    public static final int PULL_FROM_END = 2;
    public static final int DISABLED = 3;

    public ListWidget(Context context) {
        super(context);
        mContext = context;
        initView(null);
    }

    public ListWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    public ListWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs)
    {
        if (attrs != null) {
            TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.ListWidget);
            mEmptyStr = ta.getString(R.styleable.ListWidget_emptyStr);
            mMode = ta.getInt(R.styleable.ListWidget_pullMode, BOTH);
            switch (mMode) {
                case 0:
                    mPullMode = PullToRefreshBase.Mode.BOTH;
                    break;
                case 1:
                    mPullMode = PullToRefreshBase.Mode.PULL_FROM_START;
                    break;
                case 2:
                    mPullMode = PullToRefreshBase.Mode.PULL_FROM_END;
                    break;
                case 3:
                    mPullMode = PullToRefreshBase.Mode.DISABLED;
                    break;
            }
            ta.recycle();
        }
        mLoadView = initLoadView();
        mEmptyView = initEmptyView();
        mResourceListView = new RefreshListWidget(mContext);
        mResourceListView.setMode(mPullMode);
        mResourceListView.getRefreshableView().setDivider(new ColorDrawable(Color.LTGRAY));
        mResourceListView.getRefreshableView().setDividerHeight(1);

        addView(mResourceListView);
        addView(mLoadView);
    }

    public void setOnItemClick(AdapterView.OnItemClickListener itemClick)
    {
        mResourceListView.setOnItemClickListener(itemClick);
    }

    private View initLoadView()
    {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout, null);
        loadView.findViewById(R.id.load_text).setVisibility(View.GONE);
        return loadView;
    }

    public void setEmpytText(String empytText)
    {
        mEmptyText.setText(empytText);
    }

    private View initEmptyView()
    {
        View emptyLayout = LayoutInflater.from(mContext).inflate(
                R.layout.course_empty_layout, null);
        emptyLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mEmptyText = (TextView) emptyLayout.findViewById(R.id.list_empty_text);
        mEmptyText.setText(mEmptyStr);

        return emptyLayout;
    }

    public ListAdapter getAdapter()
    {
        return mResourceListView.getRefreshableView().getAdapter();
    }

    public void setAdapter(ListAdapter adapter)
    {
        mLoadView.setVisibility(GONE);
        mResourceListView.setAdapter(adapter);
    }
}
