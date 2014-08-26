package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CategoryListAdapter;
import com.edusoho.kuozhi.model.Category;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-8-14.
 */
public class LearnStatusWidget extends FrameLayout {

    private Context mContext;
    private View mLoadView;
    private View mContainer;

    public LearnStatusWidget(Context context) {
        super(context);
        mContext = context;
    }

    public LearnStatusWidget(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private void initView(android.util.AttributeSet attrs)
    {
        mLoadView = initLoadView();
        addView(mLoadView);

        mContainer = initLoadContainer();
        addView(mContainer);

        setVisibility(View.GONE);
    }

    private View initLoadContainer()
    {
        return LayoutInflater.from(mContext).inflate(R.layout.learn_status_widget_layout, null);
    }

    private View initLoadView()
    {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout, null);
        loadView.findViewById(R.id.load_text).setVisibility(View.GONE);
        return loadView;
    }

    public void initialise(
            final ActionBarBaseActivity mActivity, String url, HashMap<String, String> params)
    {
        mActivity.ajaxPost(url, params, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mLoadView.setVisibility(View.GONE);
                setVisibility(View.VISIBLE);
            }
        });
    }
}
