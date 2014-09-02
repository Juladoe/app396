package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import org.w3c.dom.Text;

/**
 * Created by howzhi on 14-8-27.
 */
public class CourseDetailsLabelWidget extends LinearLayout {

    protected Context mContext;
    protected View mLoadView;

    private TextView mTitleView;
    private View mContentView;
    private ViewGroup mContainer;

    public CourseDetailsLabelWidget(Context context) {
        super(context);
        mContext = context;
    }

    public CourseDetailsLabelWidget(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
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
        mTitleView = initTitleView(title);
        addView(mTitleView);

        mContainer = new FrameLayout(mContext);
        mContainer.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addView(mContainer);
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

    protected View setShowMoreBtn(OnClickListener clickListener)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_more_layout, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        view.setLayoutParams(layoutParams);

        view.setOnClickListener(clickListener);

        addView(view);
        return view;
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
        TextView textView = new TextView(mContext);
        Resources resources = mContext.getResources();

        textView.setText(title);
        textView.setTextColor(resources.getColor(R.color.system_light_text));
        textView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimensionPixelSize(R.dimen.course_details_widget));

        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setSingleLine();
        int padding = resources.getDimensionPixelSize(R.dimen.course_details_widget_label_padding);
        textView.setPadding(padding, padding, padding, padding);
        textView.setBackgroundResource(R.drawable.course_details_widget_bg);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        return textView;
    }
}
