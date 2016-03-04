package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.FindListAdapter;
import com.edusoho.kuozhi.v3.adapter.SchoolBannerAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.Discovery.DiscoveryCardEntity;
import com.edusoho.kuozhi.v3.model.bal.Discovery.DiscoveryColumn;
import com.edusoho.kuozhi.v3.model.bal.Discovery.DiscoveryColumnResult;
import com.edusoho.kuozhi.v3.model.bal.http.ModelDecor;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.provider.SystemProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.SchoolBanner;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EdusohoViewPager;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by JesseHuang on 15/4/26.
 */
public class FindFragment extends BaseFragment {

    private static final String TAG = "FindFragment";

    private SystemProvider mSystemProvider;

    private ListView mListView;
    private PtrClassicFrameLayout mFindContentLayout;
    private EdusohoViewPager mFindBannerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_find_layout);
        mSystemProvider = ModelProvider.initProvider(mContext, SystemProvider.class);
    }

    private float getViewScale() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        return wm.getDefaultDisplay().getWidth() / 750f;
    }

    private void addBannerView() {
        mFindBannerView = (EdusohoViewPager) LayoutInflater.from(mContext).inflate(R.layout.find_listview_head_layout, null);
        int bannerHeight = (int) (300 * getViewScale());
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, bannerHeight);
        mFindBannerView.setLayoutParams(lp);
        mListView.addHeaderView(mFindBannerView);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mFindContentLayout = (PtrClassicFrameLayout) view.findViewById(R.id.find_content);
        mListView = (ListView) view.findViewById(R.id.listview);

        addBannerView();

        getDiscoveryColumns(new NormalCallback<List<DiscoveryColumn>>() {
            @Override
            public void success(List<DiscoveryColumn> discoveryColumnList) {
                mListView.setAdapter(new FindListAdapter(mContext, discoveryColumnList));
            }
        });

        initSchoolBanner(false);

        mFindContentLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mFindContentLayout.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, content, header);
            }
        });
    }

    private void getDiscoveryColumns(final NormalCallback<List<DiscoveryColumn>> normalCallback) {
        RequestUrl requestUrl = app.bindNewApiUrl(Const.DISCOVERY_COLUMNS, true);
        mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DiscoveryColumnResult discoveryColumn = ModelDecor.getInstance().decor(response, new TypeToken<DiscoveryColumnResult>() {
                });
                if (discoveryColumn != null) {
                    normalCallback.success(discoveryColumn.datas);
                } else {
                    normalCallback.success(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private ArrayList getDicoveryDataFromCategoryId(String type) {
        ArrayList list = new ArrayList();
        DiscoveryCardEntity discoveryCardEntity = null;
        for (int i = 0; i < 2; i++) {
            discoveryCardEntity = new DiscoveryCardEntity();
            discoveryCardEntity.title = "微信登录与微信支付";
            discoveryCardEntity.picture = "http://demo.edusoho.com/files/default/2015/07-20/150221d08183090386.jpg?6.15.3";
            discoveryCardEntity.price = 0;
            discoveryCardEntity.studentNum = 20;
            discoveryCardEntity.type = type;
            discoveryCardEntity.startTime = "2016-02-07 22:13:50+08:00";
            discoveryCardEntity.endTime = "2016-02-07 22:13:50+08:00";
            discoveryCardEntity.nickname = "sujudz";
            discoveryCardEntity.avatar = "http://demo.edusoho.com/files/user/2014/10-13/132605d4ca82252495.JPG?6.15.3";
            list.add(discoveryCardEntity);
        }

        for (int i = 0; i < 1; i++) {
            discoveryCardEntity = new DiscoveryCardEntity();
            discoveryCardEntity.title = "微信登录与微信支付2";
            discoveryCardEntity.picture = "http://demo.edusoho.com/files/default/2015/10-14/1614400dc36d466096.jpg?6.15.3";
            discoveryCardEntity.price = 20.5f;
            discoveryCardEntity.studentNum = 20;
            discoveryCardEntity.type = type;
            discoveryCardEntity.startTime = "2016-02-07 22:13:50+08:00";
            discoveryCardEntity.endTime = "2016-03-07 22:13:50+08:00";
            discoveryCardEntity.nickname = "admin";
            discoveryCardEntity.avatar = "http://demo.edusoho.com/files/default/2015/07-30/1211131d9908593356.jpg?6.15.3";
            list.add(discoveryCardEntity);
        }

        for (int i = 0; i < 2; i++) {
            discoveryCardEntity = new DiscoveryCardEntity();
            discoveryCardEntity.title = "微信登录与微信支付3";
            discoveryCardEntity.picture = "http://demo.edusoho.com/files/default/2015/10-14/161517518061383672.jpg?6.15.3";
            discoveryCardEntity.price = 20.5f;
            discoveryCardEntity.studentNum = 20;
            discoveryCardEntity.type = type;
            discoveryCardEntity.startTime = "0";
            discoveryCardEntity.endTime = "0";
            discoveryCardEntity.startTime = "2016-06-07 22:13:50+08:00";
            discoveryCardEntity.endTime = "2016-06-08 22:13:50+08:00";
            discoveryCardEntity.nickname = "咯咯米";
            discoveryCardEntity.avatar = "http://demo.edusoho.com/files/default/2015/08-31/091008008a96767512.jpg?6.15.3";
            list.add(discoveryCardEntity);
        }

        return list;
    }

    protected void initSchoolBanner(final boolean isUpdate) {
        RequestUrl requestUrl = app.bindUrl(Const.SCHOOL_BANNER, false);
        mSystemProvider.getSchoolBanners(requestUrl).success(new NormalCallback<List<SchoolBanner>>() {
            @Override
            public void success(List<SchoolBanner> schoolBanners) {
                SchoolBannerAdapter adapter;
                if (isUpdate) {
                    mFindBannerView.update(schoolBanners);
                } else {
                    adapter = new SchoolBannerAdapter(
                            mActivity, schoolBanners);
                    mFindBannerView.setAdapter(adapter);
                    mFindBannerView.setCurrentItem(1, false);
                    mFindBannerView.setupAutoPlay();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.find_menu, menu);
    }
}
