package com.edusoho.kuozhi.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.edusoho.kuozhi.R;
import com.edusoho.listener.MoveListener;
import java.util.HashMap;

/**
 * Created by howzhi on 14-5-15.
 */
public class EduSohoList extends LinearLayout {

    private Context mContext;
    private HashMap<Integer, View> childs;
    private Adapter mAdapter;
    private InnerScrollView mContainer;
    private LinearLayout mLinearLayout;
    private MoveListener mMoveListener;
    private View mCourseMoreBtn;
    private boolean isShowMoreBtn;

    private EduSohoItemClickListener itemClickListener;

    public EduSohoList(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public EduSohoList(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public Adapter getAdapter()
    {
        return mAdapter;
    }

    public void setAdapter(Adapter adapter)
    {
        DataSetObserver mDataSetObserver = new DataSetObserver() {
            @Override
            public void onInvalidated() {
                super.onInvalidated();
                refresh();
            }

            @Override
            public void onChanged() {
                super.onChanged();
                dataSetChanged();
            }
        };
        this.mAdapter = adapter;
        adapter.registerDataSetObserver(mDataSetObserver);
        refresh();
    }

    private void dataSetChanged()
    {
        int count = mAdapter.getCount();
        for (int i=0; i < count; i++) {
            View view = mAdapter.getView(i, null, this);
            if (childs.containsKey(i)) {;
                continue;
            }
            childs.put(i, view);
            mLinearLayout.addView(view);
            mLinearLayout.addView(getLineView());
        }
    }

    public boolean isShowMoreBtn()
    {
        return isShowMoreBtn;
    }

    public void hideMoreBtn()
    {
        isShowMoreBtn = false;
        mLinearLayout.removeView(mCourseMoreBtn);
        mCourseMoreBtn.setVisibility(View.GONE);
    }

    public void showMoreBtn()
    {
        isShowMoreBtn = true;
        mLinearLayout.addView(mCourseMoreBtn);
        mCourseMoreBtn.setVisibility(View.VISIBLE);
        mCourseMoreBtn.findViewById(R.id.more_btn_loadbar).setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                mContainer.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void refresh()
    {
        childs.clear();
        mLinearLayout.removeAllViews();
        int count = mAdapter.getCount();
        for (int i=0; i < count; i++) {
            View view = mAdapter.getView(i, null, this);
            childs.put(i, view);
            mLinearLayout.addView(view);
            mLinearLayout.addView(getLineView());
        }
    }

    private View getLineView()
    {
        View lineView = new View(mContext);
        lineView.setBackgroundColor(mContext.getResources().getColor(R.color.title_bar));
        lineView.setMinimumHeight(1);
        return lineView;
    }

    public void setMoveListener(MoveListener moveListener)
    {
        this.mMoveListener = moveListener;
    }

    private LinearLayout initLinearLayout()
    {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }

    private InnerScrollView initContainer()
    {
        InnerScrollView scrollView = new InnerScrollView(mContext);
        scrollView.setLayoutParams(new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,ScrollView.LayoutParams.MATCH_PARENT));
        return scrollView;
    }

    private void initView()
    {
        mContainer = initContainer();
        mLinearLayout = initLinearLayout();

        mContainer.addView(mLinearLayout);
        mCourseMoreBtn = LayoutInflater.from(mContext).inflate(R.layout.course_more_btn, null);
        addView(mContainer);
        childs = new HashMap<Integer, View>();
        initListener();
    }

    public void setOnItemClickListener(EduSohoItemClickListener listener)
    {
        this.itemClickListener = listener;
    }

    private int currentIndex = -1;

    private void initListener()
    {
        mLinearLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_UP) {
                    int index = containPoint(motionEvent);
                    if (currentIndex == index && itemClickListener != null) {
                        itemClickListener.onItemClick(
                                mAdapter.getItem(currentIndex), currentIndex, childs.get(currentIndex));
                        return true;
                    }

                } else if (action == MotionEvent.ACTION_DOWN) {
                    currentIndex = containPoint(motionEvent);
                    if (currentIndex != -1) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private int containPoint(MotionEvent motionEvent)
    {
        Rect rect = new Rect();
        int size = childs.size();
        for (int i=0; i < size; i++) {
            View childView =  childs.get(i);
            childView.getHitRect(rect);
            if (rect.contains((int)motionEvent.getX(), (int)motionEvent.getY())){
                return i;
            }
        }
        return -1;
    }

    public static interface EduSohoItemClickListener{
        public void onItemClick(Object item, int index, View view);
    }

    private class InnerScrollView extends ScrollView
    {
        public InnerScrollView(Context context) {
            super(context);
        }

        public InnerScrollView(android.content.Context context, android.util.AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onScrollChanged(
                int leftOfVisibleView, int topOfVisibleView, int oldLeftOfVisibleView, int oldTopOfVisibleView) {
            int height = getHeight();
            int childHeight = mLinearLayout.getHeight();
            if (childHeight < height) {
                return;
            }

            if (topOfVisibleView >= (childHeight - height)) {
                if (mMoveListener != null) {
                    mMoveListener.moveToBottom();
                }
            }
            super.onScrollChanged(leftOfVisibleView, topOfVisibleView, oldLeftOfVisibleView, oldTopOfVisibleView);
        }
    }
}
