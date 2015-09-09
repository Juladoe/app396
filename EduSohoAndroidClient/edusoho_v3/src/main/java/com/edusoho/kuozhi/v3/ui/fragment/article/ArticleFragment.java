package com.edusoho.kuozhi.v3.ui.fragment.article;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by howzhi on 15/9/8.
 */
public class ArticleFragment extends BaseFragment {

    private ListView mMessageListView;
    private ViewGroup mMenuLayout;
    private PtrClassicFrameLayout mMessageLayout;

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
}
