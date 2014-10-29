package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by howzhi on 14-8-27.
 */
public class CourseDetailsLabelWidget extends LinearLayout {

    protected Context mContext;
    protected View mLoadView;
    protected View mMoreBtn;
    protected boolean isLearn;
    protected boolean isShowTitle;

    private TextView mTitleView;
    private View mContentView;
    private View mEmptyLayout;
    protected ViewGroup mContainer;

    public CourseDetailsLabelWidget(Context context) {
        super(context);
        mContext = context;
    }

    public CourseDetailsLabelWidget(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    protected View initEmptyLayout()
    {
        TextView textView = new TextView(mContext);
        Resources resources = getResources();
        textView.setTextColor(resources.getColor(R.color.system_normal_text));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.sys_normal));
        return textView;
    }

    protected void showEmptyLayout()
    {
        removeContentView();
        if (mEmptyLayout == null) {
            mEmptyLayout = initEmptyLayout();
        }
        mLoadView.setVisibility(View.GONE);
        mContainer.addView(mEmptyLayout);
    }

    public void onShow(){
    }

    protected View initLoadView()
    {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout, null);
        loadView.findViewById(R.id.load_text).setVisibility(View.GONE);
        return loadView;
    }

    protected void initView(android.util.AttributeSet attrs)
    {
        setOrientation(LinearLayout.VERTICAL);
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CourseDetailsLabelWidget);
        String title = ta.getString(R.styleable.CourseDetailsLabelWidget_labelTitle);
        isShowTitle = ta.getBoolean(R.styleable.CourseDetailsLabelWidget_showTitle, true);

        mTitleView = initTitleView(title);
        addView(mTitleView);

        mContainer = new FrameLayout(mContext);
        mContainer.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        addView(mContainer);
    }

    public void hideTitle()
    {
        mTitleView.setVisibility(View.GONE);
    }

    public boolean isMoveToTop(int top)
    {
        return getTop() <= top;
    }

    public boolean isLeaveTop(int top)
    {
        return getTop() > top;
    }

    protected void setLoadView()
    {
        if (mLoadView == null) {
            mLoadView = initLoadView();
        }
        mContainer.addView(mLoadView);
    }

    public void show() {
        setVisibility(VISIBLE);
        int count = getChildCount();
        for (int i=0; i < count; i++) {
            View view = getChildAt(i);
            view.setVisibility(VISIBLE);
        }
    }

    public void setShowMoreBtn(OnClickListener clickListener)
    {
        mMoreBtn = LayoutInflater.from(mContext).inflate(R.layout.view_more_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mMoreBtn.setLayoutParams(layoutParams);

        mMoreBtn.setOnClickListener(clickListener);

        addView(mMoreBtn);
    }

    protected void setContentView(int layoutId)
    {
        mContentView = LayoutInflater.from(mContext).inflate(layoutId, null);
        mContainer.addView(mContentView);
    }

    protected void removeContentView()
    {
        mContainer.removeView(mContentView);
    }

    protected void setContentView(View view)
    {
        mContentView = view;
        mContainer.addView(mContentView);
    }

    public String getTitle()
    {
        return mTitleView.getText().toString();
    }

    private TextView initTitleView(String title)
    {
        TextView textView = (TextView) LayoutInflater.from(mContext).inflate(
                R.layout.course_details_label, null);
        textView.setText(title);

        if (!isShowTitle) {
            textView.setVisibility(GONE);
        }
        return textView;
    }
}
