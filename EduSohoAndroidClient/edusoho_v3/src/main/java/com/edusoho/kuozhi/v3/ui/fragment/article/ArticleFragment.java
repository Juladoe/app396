package com.edusoho.kuozhi.v3.ui.fragment.article;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.article.MenuItem;
import com.edusoho.kuozhi.v3.model.provider.ArticleProvider;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by howzhi on 15/9/8.
 */
public class ArticleFragment extends BaseFragment {

    private ArticleProvider mArticleProvider;
    protected ListView mMessageListView;
    protected ViewGroup mMenuLayout;
    protected PtrClassicFrameLayout mMessageLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.article_layout);
        ModelProvider.init(mContext, this);
    }

    @Override
    public String getTitle() {
        return "资讯";
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mMenuLayout = (ViewGroup) view.findViewById(R.id.message_menu_layout);
        mMessageListView = (ListView) view.findViewById(R.id.message_list);
        mMessageLayout = (PtrClassicFrameLayout) view.findViewById(R.id.message_list_layout);
    }

    @Override
    public void onResume() {
        super.onResume();
        initMenu();
    }

    private void initMenu() {
        RequestUrl requestUrl = app.bindNewUrl(Const.ARTICEL_MENU, true);
        mArticleProvider.getMenus(requestUrl).success(new NormalCallback<List<LinkedHashMap>>() {
            @Override
            public void success(List<LinkedHashMap> menuItems) {
                initArticleMenuItem(coverMenuItem(menuItems));
            }
        });
    }

    private void filterInsertMenuItem(
            List<MenuItem> menuItems, List<LinkedHashMap> menuList, int start, int menuSize) {
        for (int i = start; i < menuSize; i++) {
            LinkedHashMap map = menuList.get(i);
            if (AppUtil.parseInt(map.get("depth").toString()) > 1) {
                continue;
            }
            MenuItem menuItem = new MenuItem();
            menuItem.id = map.get("id").toString();
            menuItem.title = map.get("name").toString();
            menuItem.type = MenuItem.DATA;
            menuItem.action = String.format(app.domain + Const.ARTICELS, menuItem.id);
            menuItems.add(menuItem);
        }
    }

    private List<MenuItem> coverMenuItem(List<LinkedHashMap> menuList) {
        List<MenuItem> menuItems = new ArrayList<>();

        int totalSize = menuList.size();
        int menuSize = totalSize > 3 ? 3 : totalSize;
        filterInsertMenuItem(menuItems, menuList, 0, menuSize);
        if (totalSize > 3) {
            int moreMenuSize = totalSize - 3;
            MenuItem moreMenuItem = MenuItem.createMoreMenu();
            menuItems.add(moreMenuItem);

            List<MenuItem> moreMenuItems = new ArrayList<>();
            filterInsertMenuItem(moreMenuItems, menuList, moreMenuSize, totalSize);
            moreMenuItem.setSubMenus(moreMenuItems);
        }
        return menuItems;
    }


    private void initArticleMenuItem(List<MenuItem> menuItems) {
        mMenuLayout.removeAllViews();
        for (MenuItem item : menuItems) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            TextView textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.base_large_size);
            textView.setPadding(padding, padding, padding, padding);
            textView.setTextColor(mContext.getResources().getColor(R.color.base_black_54));
            textView.setText(item.title);
            textView.setTag(item);
            textView.setLayoutParams(layoutParams);

            textView.setOnClickListener(mMenuClickListener);
            mMenuLayout.addView(textView);
        }
    }

    private View.OnClickListener mMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MenuItem menuItem = (MenuItem) v.getTag();
            if (menuItem == null) {
                return;
            }

            switch (menuItem.type) {
                case MenuItem.DATA:
                    break;
                case MenuItem.MENU:
                    Log.d("MenuItem:", menuItem.getSubMenus().toString());
                    break;
                case MenuItem.WEBVIEW:
                    break;
            }
        }
    };
}
