package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.edusoho.kuozhi.R;

/**
 * Created by su on 2016/2/19.
 */
public class FindCardView extends LinearLayout {

    private GridView mGridView;
    private TextView mTitleView;

    public FindCardView(Context context) {
        super(context, null);
        initView();
    }

    public FindCardView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initView();
    }

    protected void initView() {
        setOrientation(LinearLayout.VERTICAL);
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.view_find_card_head_layout, null);
        mTitleView = (TextView) headView.findViewById(R.id.card_title);
        addView(headView);

        mGridView = createGridView();
        addView(mGridView);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    protected GridView createGridView() {
        GridView gridView = new GridView(getContext());

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();

        gridView.setColumnWidth(width/ 2);
        gridView.setNumColumns(2);
        return gridView;
    }

    public void setAdapter(ListAdapter adapter) {
        mGridView.setAdapter(adapter);
        int count = adapter.getCount();
        count = count % 2 == 0 ? count / 2 : count / 2 + 1;

        int totalHeight = 0;
        for (int i = 0; i < count; i++) {
            View child = adapter.getView(i, null, mGridView);
            child.measure(0, 0);
            totalHeight += child.getMeasuredHeight();
        }

        ViewGroup.LayoutParams lp = mGridView.getLayoutParams();
        lp.height = totalHeight;
        mGridView.setLayoutParams(lp);
    }
}
