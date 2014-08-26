package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseListAdapter;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.view.EdusohoListView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by howzhi on 14-8-10.
 */

public class CourseListWidget extends LinearLayout {

    private int showNum;
    private boolean isShowMoreBtn;
    private boolean isFullHeight;
    private Context mContext;
    private View mLoadView;
    private EdusohoListView mEdusohoListView;
    private View mShowMoreBtn;

    public CourseListWidget(Context context) {
        super(context);
        mContext = context;
    }

    public CourseListWidget(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private void initView(android.util.AttributeSet attrs)
    {
        setOrientation(LinearLayout.VERTICAL);
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CourseListWidget);
        isShowMoreBtn = ta.getBoolean(R.styleable.CourseListWidget_showMoreBtn, false);

        mLoadView = initLoadView();
        addView(mLoadView);

        mEdusohoListView = new EdusohoListView(mContext);
        addView(mEdusohoListView);
    }

    private View initLoadView()
    {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout, null);
        loadView.findViewById(R.id.load_text).setVisibility(View.GONE);
        return loadView;
    }

    private View createShowMoreBtn()
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_more_layout, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return view;
    }

    public void setShowMoreBtnClick(OnClickListener onClickListener)
    {
        if (mShowMoreBtn == null) {
            return;
        }
        mShowMoreBtn.setOnClickListener(onClickListener);
    }

    public void setShowListNum(int num)
    {

    }

    public void setFullHeight(boolean fullHeight)
    {
        this.isFullHeight = fullHeight;
    }

    public void initialise(
            final ActionBarBaseActivity mActivity, String url, HashMap<String, String> params)
    {
        mActivity.ajaxPost(url, params, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mLoadView.setVisibility(View.GONE);
                CourseResult courseResult = mActivity.gson.fromJson(
                        object, new TypeToken<CourseResult>() {
                }.getType());

                if (courseResult == null) {
                    return;
                }
                CourseListAdapter adapter = new CourseListAdapter(
                        mActivity, courseResult, R.layout.recommend_school_list_item);
                mEdusohoListView.setAdapter(adapter);
                if (isFullHeight) {
                    mEdusohoListView.initListHeight();
                }
                if (isShowMoreBtn) {
                    mShowMoreBtn = createShowMoreBtn();
                    addView(mShowMoreBtn);
                }
            }
        });
    }
}
