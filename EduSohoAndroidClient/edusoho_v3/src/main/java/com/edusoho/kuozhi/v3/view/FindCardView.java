package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.FindCardItemAdapter;
import com.edusoho.kuozhi.v3.model.sys.FindCardEntity;
import com.edusoho.kuozhi.v3.model.sys.FindListEntity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import java.util.List;

/**
 * Created by su on 2016/2/19.
 */
public class FindCardView extends LinearLayout {

    private GridView mGridView;
    private TextView mTitleView;
    private FindCardItemAdapter mAdapter;
    private SparseArray<Integer> mChildHeightArray;
    private int mChildId;

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
        int padding = AppUtil.dp2px(getContext(), 16);
        setPadding(padding, 0, padding, 0);
        setBackgroundColor(Color.WHITE);
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.view_find_card_head_layout, null);
        mTitleView = (TextView) headView.findViewById(R.id.card_title);
        addView(headView);

        mGridView = createGridView();
        addView(mGridView);
        mChildHeightArray = new SparseArray<>();
    }

    public void setFindListEntity(FindListEntity findListEntity) {
        this.mChildId = findListEntity.id;
        setTitle(findListEntity.title);
        setData(findListEntity.data);
    }

    protected void setTitle(String title) {
        mTitleView.setText(title);
    }

    protected GridView createGridView() {
        GridView gridView = new GridView(getContext());
        gridView.setBackgroundResource(R.color.background);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();

        gridView.setColumnWidth(width / 2);
        gridView.setNumColumns(2);
        gridView.setVerticalSpacing(2);
        gridView.setVerticalScrollBarEnabled(false);
        gridView.setHorizontalScrollBarEnabled(false);
        return gridView;
    }

    public void setData(List data) {
        mAdapter.clear();
        if (data.size() % 2 != 0) {
            data.add(new FindCardEntity(true));
        }
        addData(data);
    }

    private void addData(List data) {
        mAdapter.addList(data);

        int totalHeight = 0, childHeight = 0;
        totalHeight = mChildHeightArray.get(mChildId, 0);
        if (totalHeight == 0) {
            int count = mAdapter.getCount();
            count = count % 2 == 0 ? count / 2 : count / 2 + 1;

            View child = mAdapter.getView(0, null, mGridView);
            child.measure(0, 0);
            childHeight = child.getMeasuredHeight();
            totalHeight = childHeight * count;
            mChildHeightArray.put(mChildId, totalHeight);
        }

        ViewGroup.LayoutParams lp = mGridView.getLayoutParams();
        lp.height = totalHeight;
        mGridView.setLayoutParams(lp);
    }

    public void setAdapter(ListAdapter adapter) {
        mAdapter = (FindCardItemAdapter) adapter;
        if (mAdapter.getCount() % 2 != 0) {
            mAdapter.addData(new FindCardEntity(true));
        }
        mGridView.setAdapter(adapter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mChildHeightArray.clear();
    }
}
