package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CategoryListAdapter;
import com.edusoho.kuozhi.adapter.FoundCategoryListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
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
    private ExpandableListView mCategoryListView;

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
        mCategoryListView = new ExpandableListView(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mCategoryListView.setLayoutParams(layoutParams);

        mCategoryListView.setGroupIndicator(null);
        mCategoryListView.setSelector(R.drawable.normal_list_select);
        mCategoryListView.setDivider(new ColorDrawable(mContext.getResources().getColor(R.color.found_list_divider)));
        mCategoryListView.setDividerHeight(1);
        addView(mCategoryListView);

        mLoadView = initLoadView();
        addView(mLoadView);
    }

    public void setItemClick(final ItemClickListener itemClick)
    {
        mCategoryListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                ExpandableListAdapter adapter = expandableListView.getExpandableListAdapter();
                Category category = (Category) adapter.getChild(groupPosition, childPosition);
                itemClick.click(category);
                return true;
            }
        });

        mCategoryListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                Category category = (Category) expandableListView.getItemAtPosition(groupPosition);
                itemClick.click(category);
                return true;
            }
        });
    }

    private View initLoadView()
    {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout, null);
        loadView.findViewById(R.id.load_text).setVisibility(View.GONE);
        return loadView;
    }
    
    public void initialise(
            final ActionBarBaseActivity mActivity, RequestUrl url)
    {
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mLoadView.setVisibility(View.GONE);
                parseRequeseData(mActivity, object);
            }

            @Override
            public void update(String url, String object, AjaxStatus ajaxStatus) {
                super.update(url, object, ajaxStatus);
                updateRequeseData(mActivity, object);
            }
        });
    }

    private void parseRequeseData(ActionBarBaseActivity mActivity, String object)
    {
        ArrayList<Category> categories = mActivity.gson.fromJson(
                object, new TypeToken<ArrayList<Category>>() {
        }.getType());

        if (categories == null || categories.isEmpty()) {
            return;
        }

        FoundCategoryListAdapter adapter = new FoundCategoryListAdapter(
                mActivity, categories, R.layout.category_list_item, mCategoryListView);
        mCategoryListView.setAdapter(adapter);
    }

    private void updateRequeseData(ActionBarBaseActivity mActivity, String object)
    {
        ArrayList<Category> categories = mActivity.gson.fromJson(
                object, new TypeToken<ArrayList<Category>>() {
        }.getType());

        if (categories == null || categories.isEmpty()) {
            return;
        }

        FoundCategoryListAdapter adapter = (FoundCategoryListAdapter)
                mCategoryListView.getAdapter();
        adapter.setItems(categories);
    }

    public static class ListExpandClickListener implements OnClickListener
    {
        @Override
        public void onClick(View view) {
        }
    }

    public static interface ItemClickListener
    {
        public void click(Category category);
    }
}
