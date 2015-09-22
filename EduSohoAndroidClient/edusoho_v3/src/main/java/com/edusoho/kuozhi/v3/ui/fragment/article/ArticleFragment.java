package com.edusoho.kuozhi.v3.ui.fragment.article;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.article.ArticleCardAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.article.ArticleChat;
import com.edusoho.kuozhi.v3.model.bal.article.ArticleList;
import com.edusoho.kuozhi.v3.model.bal.article.MenuItem;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.provider.ArticleProvider;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
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
    private ChatDataSource mChatDataSource;
    private View.OnClickListener mMenuClickListener = new MenuClickListener();

    private int mStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.article_layout);
        ModelProvider.init(mContext, this);
        mChatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
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
        mMessageLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                Log.d(TAG, "onRefreshBegin");
                mStart = mArticleAdapter.getGroupCount();
                mMessageListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                if (loadLocalArticles()) {
                    mMessageListView.setSelectedGroup(mStart - 1);
                }
                mMessageLayout.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        mMessageListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        initArticleList();
    }

    @Override
    public void onResume() {
        super.onResume();
        initMenu();
    }

    private void initArticleList() {
        mStart = 0;
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
        int childWidth = mMenuLayout.getWidth() / menuItems.size();
        for (MenuItem item : menuItems) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    childWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(mContext);
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

    private ArrayList<ArticleChat> getChatList(int start) {
        String selectSql = String.format("NICKNAME = '%s' ", app.domain);
        ArrayList<Chat> mList = mChatDataSource.getChats(start, 5, selectSql);
        Collections.reverse(mList);

        ArrayList<ArticleChat> articleChats = new ArrayList<>();
        for (Chat chat : mList) {
            articleChats.add(new ArticleChat(chat));
        }
        return articleChats;
    }

    private boolean loadLocalArticles() {
        ArrayList<ArticleChat> articleChats = getChatList(mStart);
        if (articleChats.isEmpty()) {
            return false;
        }
        mArticleAdapter.addArticleChats(articleChats);
        expandArticle();

        return true;
    }

    private void insertArticles(String categoryId) {

        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.show();
        String url = String.format("%s?categoryId=%s&limit=%s", Const.ARTICELS, categoryId, "3");
        RequestUrl requestUrl = app.bindNewUrl(url, true);
        mArticleProvider.getArticles(requestUrl).success(new NormalCallback<ArticleList>() {
            @Override
            public void success(ArticleList articleList) {
                loadDialog.dismiss();
                if (articleList == null || articleList.resources.isEmpty()) {
                    CommonUtil.longToast(mContext, "没有相关资讯!");
                    return;
                }
                ArticleChat articleChat = ArticleChat.create(articleList.resources);
                mArticleAdapter.addArticleChat(articleChat);
                expandArticle();
                new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).create(articleChat);
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
        switch (messageType.code) {
            case Const.ADD_ARTICLE_CREATE_MAG:
                WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
                ArticleChat articleChat = new ArticleChat(wrapperMessage);
                mMessageListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                mArticleAdapter.addArticleChat(articleChat);
                expandArticle();
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[] {
            new MessageType(Const.ADD_ARTICLE_CREATE_MAG, source)
        };
    }
}
