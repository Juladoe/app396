package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by howzhi on 14-9-17.
 */
public class ListWidget extends FrameLayout {

    protected Context mContext;
    protected View mLoadView;
    protected View mEmptyView;
    protected TextView mEmptyText;
    protected String mEmptyStr;
    private PullToRefreshListView mResourceListView;
    private int mMode;

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
        mLoadView = initLoadView();
        mEmptyView = initEmptyView();
        mResourceListView = new PullToRefreshListView(mContext);

        if (attrs != null) {
            TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.ListWidget);
            mEmptyStr = ta.getString(R.styleable.ListWidget_emptyStr);
            mMode = ta.getInt(R.styleable.ListWidget_pullMode, BOTH);
            ta.recycle();
        }

        addView(mResourceListView);
        addView(mLoadView);
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

    public void setAdapter(ListAdapter adapter)
    {
        mLoadView.setVisibility(GONE);
        mResourceListView.setAdapter(adapter);
    }
}
