package com.edusoho.kuozhi.v3.ui.fragment.article;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.article.MenuItem;
import com.edusoho.kuozhi.v3.model.provider.ArticleProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

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

    private void initMenu() {
        RequestUrl requestUrl = new RequestUrl();
        mArticleProvider.getMenus(requestUrl).success(new NormalCallback<List<MenuItem>>() {
            @Override
            public void success(List<MenuItem> menuItems) {

            }
        });
    }
}
