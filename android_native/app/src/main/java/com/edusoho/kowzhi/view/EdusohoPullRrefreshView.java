package com.edusoho.kowzhi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kowzhi.R;
import com.edusoho.listener.MoveListener;

public class EdusohoPullRrefreshView extends LinearLayout{

    private Context mContext;
    private TextView up_refresh_btn;
    private ListView listView;
    private TextView bottom_refresh_btn;

    public EdusohoPullRrefreshView(Context context) {
        super(context);
        mContext = context;
    }

    public EdusohoPullRrefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public RefreshCallback refreshCallback;
    private OverScrollView overScrollView;

    public void setOverScrollView(OverScrollView osv)
    {
        overScrollView = osv;
        overScrollView.setMoveListener(moveListener);
        initView();
    }

    public void setRefreshCallBack(RefreshCallback callback)
    {
        refreshCallback = callback;
        moveListener.isRefresh = true;
    }

    /**
     * move listener
     */
    public MoveListener moveListener = new MoveListener() {
        @Override
        public void invoke(int scroll) {
            if (scroll > 50 && !isShowRefresh) {
                up_refresh_btn.setText("释放刷新");
                isShowRefresh = true;
            } else if (scroll < 50) {
                isShowRefresh = false;
                up_refresh_btn.setText("下拉刷新");
            }
        }

        @Override
        public void refresh() {
            if (isShowRefresh
                    && isRefresh
                    && !isRefreshing) {
                isRefreshing = true;

                refreshCallback.refresh();
                isRefreshing = false;
            }
        }
    };

    public boolean isShowRefresh = false;

    private void initView()
    {
        bottom_refresh_btn = (TextView) findViewById(R.id.bottom_refresh_btn);
        up_refresh_btn = (TextView) findViewById(R.id.up_refresh_btn);
        listView = (ListView) findViewById(R.id.refresh_listview);
    }

    public void setAdapter(ListAdapter adapter)
    {
        listView.setAdapter(adapter);
        initListHeight(listView);
        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    bottom_refresh_btn.setVisibility(View.GONE);
                } else{
                    bottom_refresh_btn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initListHeight(ListView lv)
    {
        int totalHeight = 0;
        ListAdapter adapter = lv.getAdapter();
        int count = adapter.getCount();
        for (int i=0; i < count; i++) {
            View child = adapter.getView(i, null, this);
            child.measure(0, 0);
            totalHeight += child.getMeasuredHeight() + lv.getDividerHeight();
        }

        ViewGroup.LayoutParams lp =  lv.getLayoutParams();
        lp.height = totalHeight;

        int viewH = overScrollView.getHeight();
        if (totalHeight < viewH) {
            lp.height = viewH;
        }
        lv.setLayoutParams(lp);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        listView.setOnItemClickListener(listener);
    }

    public static abstract class RefreshCallback
    {
        public abstract void refresh();
    }
}
