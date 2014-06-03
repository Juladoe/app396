package com.edusohoapp.app.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.edusohoapp.app.R;
import com.edusohoapp.app.entity.CourseCommentItem;
import com.edusohoapp.app.entity.CourseInfoItem;
import com.edusohoapp.app.entity.UserItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by howzhi on 14-5-15.
 */
public class EduSohoList extends LinearLayout {

    private Context mContext;
    private ArrayList<View> childs;
    private Adapter mAdapter;

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

    public void setAdapter(Adapter adapter)
    {
        DataSetObserver mDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                refresh();
            }
        };
        this.mAdapter = adapter;
        adapter.registerDataSetObserver(mDataSetObserver);
        refresh();
    }

    public void refresh()
    {
        childs.clear();
        removeAllViews();
        int count = mAdapter.getCount();
        for (int i=0; i < count; i++) {
            View view = mAdapter.getView(i, null, this);
            childs.add(view);
            addView(view);
            addView(getLineView());
        }
    }

    private View getLineView()
    {
        View lineView = new View(mContext);
        lineView.setBackgroundColor(mContext.getResources().getColor(R.color.title_bar));
        lineView.setMinimumHeight(1);
        return lineView;
    }


    private void initView()
    {
        childs = new ArrayList<View>();
        initListener();
    }

    public void setOnItemClickListener(EduSohoItemClickListener listener)
    {
        this.itemClickListener = listener;
    }

    private int currentIndex = -1;

    private void initListener()
    {
        setOnTouchListener(new OnTouchListener() {
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

                } else if(action == MotionEvent.ACTION_DOWN) {
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
}
