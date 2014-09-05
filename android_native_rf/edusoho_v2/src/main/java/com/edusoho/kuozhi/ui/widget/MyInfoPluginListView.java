package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CategoryListAdapter;
import com.edusoho.kuozhi.adapter.MyInfoPluginListAdapter;
import com.edusoho.kuozhi.model.Category;
import com.edusoho.kuozhi.model.MyInfoPlugin;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-8-14.
 */
public class MyInfoPluginListView extends LinearLayout {

    private Context mContext;
    private View mLoadView;
    private ListView mPluginListView;
    private PluginItemClick mPluginItemClick;

    public MyInfoPluginListView(Context context) {
        super(context);
        mContext = context;
    }

    public MyInfoPluginListView(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private void initView(android.util.AttributeSet attrs)
    {
        setOrientation(LinearLayout.VERTICAL);
        mLoadView = initLoadView();
        addView(mLoadView);

        mPluginListView = new ListView(mContext);
        mPluginListView.setSelector(R.drawable.normal_list_select);
        mPluginListView.setDivider(new ColorDrawable(mContext.getResources().getColor(R.color.found_list_divider)));
        mPluginListView.setDividerHeight(1);

        mPluginListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(null, "onItemClick->");
                if (mPluginItemClick == null) {
                    return;
                }
                MyInfoPlugin plugin = (MyInfoPlugin) adapterView.getItemAtPosition(i);
                mPluginItemClick.onClick(plugin);
            }
        });
        addView(mPluginListView);
    }

    public void setItemOnClick(PluginItemClick pluginItemClick)
    {
        mPluginItemClick = pluginItemClick;
    }

    private View initLoadView()
    {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout, null);
        loadView.findViewById(R.id.load_text).setVisibility(View.GONE);
        return loadView;
    }

    public void initFromLocal(ActionBarBaseActivity mActivity)
    {
        mLoadView.setVisibility(View.GONE);
        ArrayList<MyInfoPlugin> list = MyInfoPlugin.createNormalList(mActivity);

        MyInfoPluginListAdapter adapter = new MyInfoPluginListAdapter(
                mActivity, list, R.layout.myinfo_plugin_list_item);
        mPluginListView.setAdapter(adapter);
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
                mPluginListView.setAdapter(adapter);
            }
        });
    }

    public interface PluginItemClick
    {
        public void onClick(MyInfoPlugin plugin);
    }
}
