package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.HorizontalCourseListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14-8-10.
 */
public class HorizontalListWidget extends HorizontalScrollView {

    private Context mContext;
    private GridView mGridView;
    private GestureDetector mGestureDetector;
    private FrameLayout mContainer;
    private View mLoadView;
    private ActionBarBaseActivity mActivity;
    private View mLabel;

    public HorizontalListWidget(Context context) {
        super(context);
        mContext = context;
    }

    public HorizontalListWidget(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private GridView initGridView()
    {
        GridView gridView = new GridView(mContext);

        gridView.setColumnWidth(285);
        gridView.setHorizontalSpacing(2);
        gridView.setNumColumns(GridView.AUTO_FIT);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setGravity(Gravity.CENTER);

        return gridView;
    }

    public void setOnItemClick(AdapterView.OnItemClickListener onItemClick)
    {
        mGridView.setOnItemClickListener(onItemClick);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(event) && mGestureDetector.onTouchEvent(event);
    }

    private class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

            if (Math.abs(distanceY) >= Math.abs(distanceX)) {
                return true;
            }
            return false;
        }
    }

    private void setGridViewWidth(int count)
    {
        mGridView.setNumColumns(count);
        FrameLayout.LayoutParams layoutParams =  new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        int width = (int)(EdusohoApp.screenW * 0.5f);
        layoutParams.width = (width + 2 + 16) * count;
        mGridView.setLayoutParams(layoutParams);
    }

    private void initView(android.util.AttributeSet attrs)
    {
        mGestureDetector = new GestureDetector(mContext, new YScrollDetector());
        mContainer = new FrameLayout(mContext);
        mContainer.setLayoutParams(new HorizontalScrollView.LayoutParams(
                HorizontalScrollView.LayoutParams.MATCH_PARENT, HorizontalScrollView.LayoutParams.MATCH_PARENT));

        mGridView = initGridView();
        mContainer.addView(mGridView);

        mLoadView = initLoadView();
        mContainer.addView(mLoadView);

        addView(mContainer);
    }

    private View initLoadView()
    {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout, null);
        loadView.findViewById(R.id.load_text).setVisibility(View.GONE);
        return loadView;
    }

    private void parseRequestData(String object)
    {
        mLoadView.setVisibility(View.GONE);
        CourseResult courseResult = mActivity.gson.fromJson(
                object, new TypeToken<CourseResult>() {
        }.getType());

        if (courseResult == null || courseResult.data.isEmpty()) {
            setVisibility(GONE);
            if (mLabel != null) {
                mLabel.setVisibility(GONE);
            }
            return;
        }

        HorizontalCourseListAdapter adapter = new HorizontalCourseListAdapter(
                mActivity, courseResult, R.layout.horizontal_course_item);

        setGridViewWidth(adapter.getCount());
        mGridView.setAdapter(adapter);
    }

    private void updateRequestData(String object)
    {
        CourseResult courseResult = mActivity.gson.fromJson(
                object, new TypeToken<CourseResult>() {
        }.getType());

        if (courseResult == null) {
            return;
        }

        HorizontalCourseListAdapter adapter = (HorizontalCourseListAdapter)
                mGridView.getAdapter();
        adapter.setItems(courseResult);
    }

    public void setLabel(View label)
    {
        mLabel = label;
    }

    public void initialise(ActionBarBaseActivity activity, RequestUrl requestUrl)
    {
        mActivity = activity;
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                Log.d(null, "HorizontalListWidget->callback");
                parseRequestData(object);
            }

            @Override
            public void update(String url, String object, AjaxStatus ajaxStatus) {
                super.update(url, object, ajaxStatus);
                Log.d(null, "HorizontalListWidget->update");
                updateRequestData(object);
            }
        });
    }
}
