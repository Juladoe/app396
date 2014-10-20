package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.view.EdusohoListView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14-8-10.
 */

public class CourseListWidget extends LinearLayout {

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

        if (isShowMoreBtn) {
            mShowMoreBtn = createShowMoreBtn();
            addView(mShowMoreBtn);
        }
    }

    private View initLoadView()
    {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout, null);
        loadView.findViewById(R.id.load_text).setVisibility(View.GONE);
        return loadView;
    }

    public void setItemClick(AdapterView.OnItemClickListener onItemClickListener)
    {
        mEdusohoListView.setOnItemClickListener(onItemClickListener);
    }

    private View createShowMoreBtn()
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_more_layout, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return view;
    }

    public void setShowMoreBtnClick(OnClickListener onClickListener)
    {
        if (mShowMoreBtn == null) {
            return;
        }
        mShowMoreBtn.setOnClickListener(onClickListener);
    }

    public void setFullHeight(boolean fullHeight)
    {
        this.isFullHeight = fullHeight;
    }

    public void initialise(
            final ActionBarBaseActivity mActivity, RequestUrl url)
    {
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                parseRequestData(mActivity, object);
            }

            @Override
            public void update(String url, String object, AjaxStatus ajaxStatus) {
                super.update(url, object, ajaxStatus);
                updateRequestData(mActivity, object);
            }
        });
    }

    private void updateRequestData(ActionBarBaseActivity mActivity, String object)
    {
        CourseResult courseResult = mActivity.gson.fromJson(
                object, new TypeToken<CourseResult>() {
        }.getType());

        if (courseResult == null) {
            return;
        }
        CourseListAdapter adapter = (CourseListAdapter) mEdusohoListView.getAdapter();
        adapter.setItems(courseResult.data);
    }

    private void parseRequestData(ActionBarBaseActivity mActivity, String object)
    {
        mLoadView.setVisibility(View.GONE);
        CourseResult courseResult = mActivity.gson.fromJson(
                object, new TypeToken<CourseResult>() {
        }.getType());

        if (courseResult == null) {
            return;
        }
        CourseListAdapter adapter = new CourseListAdapter(
                mActivity, R.layout.recommend_school_list_item);
        adapter.setItems(courseResult.data);
        mEdusohoListView.setAdapter(adapter);
        if (isFullHeight) {
            mEdusohoListView.initListHeight();
        }
    }
}
