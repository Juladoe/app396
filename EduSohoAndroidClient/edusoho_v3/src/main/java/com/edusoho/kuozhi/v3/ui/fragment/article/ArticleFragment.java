package com.edusoho.kuozhi.v3.ui.fragment.article;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.article.ArticleCardAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.article.Article;
import com.edusoho.kuozhi.v3.model.bal.article.ArticleModel;
import com.edusoho.kuozhi.v3.model.bal.article.ArticleList;
import com.edusoho.kuozhi.v3.model.bal.article.MenuItem;
import com.edusoho.kuozhi.v3.model.bal.push.ServiceProviderModel;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.provider.ArticleProvider;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.ServiceProviderActivity;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.ui.fragment.ServiceProfileFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.ServiceProviderDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by howzhi on 15/9/8.
 */
public class ArticleFragment extends BaseFragment {

    private static final String TAG = "ArticleFragment";

    private ArticleProvider mArticleProvider;
    protected ExpandableListView mMessageListView;
    protected ViewGroup mMenuLayout;
    private PopupWindow mMenuPopupWindow;
    protected PtrClassicFrameLayout mMessageLayout;

    private ArticleCardAdapter mArticleAdapter;
    private ServiceProviderDataSource mSPDataSource;
    private View.OnClickListener mMenuClickListener = new MenuClickListener();

    private int mStart;
    private int mServiceProvierId;
    private SparseArray<Integer> mCategoryCacheArray;

    private PtrHandler mMessageListPtrHandler = new PtrHandler() {
        @Override
        public void onRefreshBegin(PtrFrameLayout frame) {
            mStart = mArticleAdapter.getGroupCount();
            mMessageListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
            if (loadLocalArticles()) {
                int total = mArticleAdapter.getGroupCount();
                mMessageListView.setSelectedGroup(total - mStart > 4 ? 4 : total - mStart);
            }
            mMessageLayout.refreshComplete();
        }

        @Override
        public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
            return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
        }
    };

    private void showArticleProfile() {
        app.mEngine.runNormalPlugin("FragmentPageActivity", mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ServiceProfileFragment.SERVICE_ID, mServiceProvierId);
                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "ServiceProfileFragment");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_course_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (R.id.news_course_profile == item.getItemId()) {
            showArticleProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoryCacheArray = new SparseArray<>();
        setHasOptionsMenu(true);
        setContainerView(R.layout.article_layout);
        ModelProvider.init(mContext, this);
        mSPDataSource = new ServiceProviderDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
    }

    @Override
    public String getTitle() {
        return "资讯";
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mMenuLayout = (ViewGroup) view.findViewById(R.id.message_menu_layout);
        mMessageListView = (ExpandableListView) view.findViewById(R.id.message_list);
        mMessageLayout = (PtrClassicFrameLayout) view.findViewById(R.id.message_list_layout);

        mMessageLayout.setLastUpdateTimeRelateObject(this);
        mMessageLayout.setPtrHandler(mMessageListPtrHandler);
        mMessageListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        mMessageListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Article article = mArticleAdapter.getChild(groupPosition, childPosition);
                showArticle(article.id);
                return false;
            }
        });
        initData();
        sendNewFragment2UpdateItemBadge();
    }

    @Override
    public void onResume() {
        super.onResume();
        initMenu();
    }

    private void showArticle(int id) {
        final String url = String.format(Const.ARTICLE_CONTENT, app.schoolHost, id);
        app.mEngine.runNormalPlugin("WebViewActivity", mActivity, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(Const.WEB_URL, url);
            }
        });
    }

    private void initData() {
        mStart = 0;
        mServiceProvierId = getArguments().getInt(ServiceProviderActivity.SERVICE_ID);
        mArticleAdapter = new ArticleCardAdapter(mContext);
        mMessageListView.setAdapter(mArticleAdapter);
        loadLocalArticles();
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

    private void sendNewFragment2UpdateItemBadge() {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.FROM_ID, mServiceProvierId);
        app.sendMsgToTarget(NewsFragment.UPDATE_UNREAD_ARTICLE_CREATE, bundle, NewsFragment.class);
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
        if (menuItems.isEmpty()) {
            return;
        }
        int childWidth = mMenuLayout.getWidth() / menuItems.size();
        for (MenuItem item : menuItems) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    childWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(mContext);
            textView.setSingleLine();
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.base_size));
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setBackgroundResource(R.drawable.article_menu_btn_bg);
            textView.setGravity(Gravity.CENTER);
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.base_large_size);
            textView.setPadding(padding, padding, padding, padding);
            textView.setTextColor(mContext.getResources().getColor(R.color.base_black_54));
            textView.setText(item.title);
            textView.setTag(item);
            textView.setLayoutParams(layoutParams);

            textView.setOnClickListener(mMenuClickListener);
            mMenuLayout.addView(textView, layoutParams);
        }
    }

    private void handleClick(View v, MenuItem menuItem) {
        if (menuItem == null) {
            return;
        }

        switch (menuItem.type) {
            case MenuItem.DATA:
                insertArticles(menuItem.id);
                break;
            case MenuItem.MENU:
                showMenuPop(v, menuItem);
                break;
            case MenuItem.WEBVIEW:
                break;
        }
    }

    private class MenuClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            MenuItem menuItem = (MenuItem) v.getTag();
            handleClick(v, menuItem);
        }
    }

    private ArrayList<ArticleModel> getChatList(int start) {
        ArrayList<ServiceProviderModel> mList = mSPDataSource.getServiceProviderMsgs(app.loginUser.id, start, 5);
        Collections.reverse(mList);

        ArrayList<ArticleModel> articleModels = new ArrayList<>();
        for (ServiceProviderModel model : mList) {
            articleModels.add(new ArticleModel(model));
        }
        return articleModels;
    }

    private void clearArticleList() {
        mStart = 0;
        mArticleAdapter.clear();
        mCategoryCacheArray.clear();
    }

    private boolean loadLocalArticles() {
        ArrayList<ArticleModel> articleModels = getChatList(mStart);
        if (articleModels.isEmpty()) {
            return false;
        }
        mArticleAdapter.addArticleChats(articleModels);
        expandArticle();

        return true;
    }

    private void insertArticles(final String categoryId) {

        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.show();
        final int start = mCategoryCacheArray.get(AppUtil.parseInt(categoryId), 0);
        String url = String.format("%s?categoryId=%s&limit=%s&start=%d", Const.ARTICELS, categoryId, "3", start);
        RequestUrl requestUrl = app.bindNewUrl(url, true);
        mArticleProvider.getArticles(requestUrl).success(new NormalCallback<ArticleList>() {
            @Override
            public void success(ArticleList articleList) {
                loadDialog.dismiss();
                if (articleList == null || articleList.resources.isEmpty()) {
                    CommonUtil.longToast(mContext, "没有相关资讯!");
                    return;
                }

                mCategoryCacheArray.put(AppUtil.parseInt(categoryId), start + 3);
                ArticleModel articleModel = ArticleModel.create(app.loginUser.id, articleList.resources);
                mArticleAdapter.addArticleChat(articleModel);
                expandArticle();
                new ServiceProviderDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).create(articleModel);
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                loadDialog.dismiss();
            }
        });
    }

    private void expandArticle() {
        int count = mArticleAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            mMessageListView.expandGroup(i);
        }
    }

    private void hideMenuPop() {
        if (mMenuPopupWindow != null) {
            mMenuPopupWindow.dismiss();
        }
    }

    private void showMenuPop(View target, MenuItem menuItem) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.article_more_menu, null);
        ListView listView = (ListView) contentView.findViewById(R.id.article_listview);
        MenuAdapter adapter = new MenuAdapter(mContext, menuItem.getSubMenus());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuItem menuItem = (MenuItem) parent.getItemAtPosition(position);
                handleClick(view, menuItem);
                hideMenuPop();
            }
        });

        mMenuPopupWindow = new PopupWindow(
                contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mMenuPopupWindow.setWidth(target.getWidth());
        mMenuPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.article_more_menu_bg));
        mMenuPopupWindow.setFocusable(true);
        mMenuPopupWindow.setOutsideTouchable(true);

        int[] location = new int[2];
        target.getLocationOnScreen(location);
        mMenuPopupWindow.showAtLocation(
                target, Gravity.BOTTOM | Gravity.LEFT, location[0] - 10, target.getHeight() + 10);
    }

    private class MenuAdapter extends BaseAdapter {

        private Context mContext;
        private List<MenuItem> mMenuItems;

        public MenuAdapter(Context context, List<MenuItem> menuItems) {
            this.mContext = context;
            this.mMenuItems = menuItems;
        }

        @Override
        public MenuItem getItem(int position) {
            return mMenuItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.article_menu_list_item, null);
            }

            TextView textView = (TextView)convertView;
            textView.setText(mMenuItems.get(position).title);
            return convertView;
        }

        @Override
        public int getCount() {
            return mMenuItems.size();
        }
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        MessageType messageType = message.type;
        if (Const.CLEAR_HISTORY.equals(messageType.type)) {
            Bundle bundle = message.data;
            int serviceId = bundle.getInt(ServiceProviderActivity.SERVICE_ID, 0);
            if (serviceId == mServiceProvierId) {
                clearArticleList();
            }

            return;
        }
        switch (messageType.code) {
            case Const.ADD_ARTICLE_CREATE_MAG:
                WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
                ArticleModel articleModel = new ArticleModel(wrapperMessage);
                mMessageListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                mArticleAdapter.addArticleChat(articleModel);
                expandArticle();
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[] {
            new MessageType(Const.ADD_ARTICLE_CREATE_MAG, source),
            new MessageType(Const.CLEAR_HISTORY)
        };
    }
}
