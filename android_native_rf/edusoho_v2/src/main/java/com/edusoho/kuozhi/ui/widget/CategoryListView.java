package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CategoryListAdapter;
import com.edusoho.kuozhi.model.Category;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-8-14.
 */
public class CategoryListView extends FrameLayout {

    private Context mContext;
    private View mLoadView;
    private ListView mCategoryListView;

    public CategoryListView(Context context) {
        super(context);
        mContext = context;
    }

    public CategoryListView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private void initView(android.util.AttributeSet attrs)
    {
        mCategoryListView = new ListView(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mCategoryListView.setLayoutParams(layoutParams);

        mCategoryListView.setSelector(R.drawable.normal_list_select);
        mCategoryListView.setDivider(new ColorDrawable(mContext.getResources().getColor(R.color.found_list_divider)));
        mCategoryListView.setDividerHeight(1);
        addView(mCategoryListView);

        mLoadView = initLoadView();
        addView(mLoadView);
    }

    public void setItemClick(AdapterView.OnItemClickListener onItemClickListener)
    {
        mCategoryListView.setOnItemClickListener(onItemClickListener);
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

                ArrayList<Category> categories = mActivity.gson.fromJson(
                        object, new TypeToken<ArrayList<Category>>() {
                }.getType());

                if (categories == null || categories.isEmpty()) {
                    return;
                }

                CategoryListAdapter adapter = new CategoryListAdapter(
                        mActivity, categories, R.layout.category_list_item);
                mCategoryListView.setAdapter(adapter);
            }
        });
    }

}
