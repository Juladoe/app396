package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CategoryListAdapter;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.Category;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.course.CourseListActivity;
import com.edusoho.kuozhi.ui.widget.CategoryListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-8-14.
 */

public class FoundFragment extends BaseFragment {

    private CategoryListView mCategoryListView;
    private View mFoundSearchLayout;
    public String mTitle = "发现";

    public static final int HIDE_ACTION_BAR_CODE = 0001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.found_layout);
        Log.d(null, "onCreate");
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        int type = message.type.code;
        switch (type) {
            case HIDE_ACTION_BAR_CODE:
                showSearchLayout();
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(HIDE_ACTION_BAR_CODE, source)
        };
        return messageTypes;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(null, "onResume");
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    protected void initView(View view) {
        Log.d(null, "FoundFragment->init");
        mFoundSearchLayout = view.findViewById(R.id.found_search_layout);
        mCategoryListView = (CategoryListView) view.findViewById(R.id.found_category_list);

        mFoundSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(null, "show popwindow search");
                hideSearchLayout();
                SearchDialogFragment searchDialogFragment = new SearchDialogFragment();
                searchDialogFragment.show(getChildFragmentManager(), "dialog");
            }
        });

        String url = app.bindUrl(Const.CATEGORYS);
        HashMap<String, String> params = app.createParams(true, null);

        mCategoryListView.initialise(mActivity, url, params);
        mCategoryListView.setItemClick(new CategoryListView.ItemClickListener() {
            @Override
            public void click(final Category category) {
                app.mEngine.runNormalPlugin("CourseListActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(CourseListActivity.TITLE, category.name);
                        startIntent.putExtra(CourseListActivity.CATEGORY_ID, category.id);
                    }
                });
            }
        });
    }

    private void hideSearchLayout()
    {
        EdusohoAnimWrap wrap = new EdusohoAnimWrap(mFoundSearchLayout);
        mFoundSearchLayout.setTag(mFoundSearchLayout.getHeight());
        ObjectAnimator animator = ObjectAnimator.ofInt(
                wrap, "height", wrap.getHeight(), 0);
        animator.setDuration(300);
        animator.start();
    }

    private void showSearchLayout()
    {
        EdusohoAnimWrap wrap = new EdusohoAnimWrap(mFoundSearchLayout);
        int height = (Integer)mFoundSearchLayout.getTag();
        ObjectAnimator animator = ObjectAnimator.ofInt(
                wrap, "height", 0, height);
        animator.setDuration(10);
        animator.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(null, "onDestroyView");
    }
}
