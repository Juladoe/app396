package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.FindCardItemAdapter;
import com.edusoho.kuozhi.v3.adapter.FindListAdapter;
import com.edusoho.kuozhi.v3.adapter.SchoolBannerAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.provider.SystemProvider;
import com.edusoho.kuozhi.v3.model.sys.FindCardEntity;
import com.edusoho.kuozhi.v3.model.sys.FindListEntity;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.SchoolBanner;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EdusohoViewPager;
import com.edusoho.kuozhi.v3.view.FindCardView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        int bannerHeight = AppUtil.dp2px(mContext, 300 * getViewScale());
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
        mListView.setAdapter(new FindListAdapter(mContext, getFindItemData()));
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

    private ArrayList getFindItemData() {
        ArrayList list = new ArrayList();
        FindListEntity listEntity = null;

        listEntity = new FindListEntity();
        listEntity.title = "推荐课程";
        listEntity.id = 1;
        listEntity.data = getFindCardData("course");
        list.add(listEntity);

        listEntity = new FindListEntity();
        listEntity.title = "推荐班级";
        listEntity.id = 3;
        listEntity.data = getFindCardData("classroom");
        list.add(listEntity);

        listEntity = new FindListEntity();
        listEntity.title = "推荐直播";
        listEntity.id = 3;
        listEntity.data = getFindCardData("live");
        list.add(listEntity);

        listEntity = new FindListEntity();
        listEntity.title = "推荐课程2";
        listEntity.id = 4;
        listEntity.data = getFindCardData("course");
        list.add(listEntity);

        listEntity = new FindListEntity();
        listEntity.title = "推荐班级3";
        listEntity.id = 5;
        listEntity.data = getFindCardData("classroom");
        list.add(listEntity);

        listEntity = new FindListEntity();
        listEntity.title = "推荐直播4";
        listEntity.id = 6;
        listEntity.data = getFindCardData("live");
        list.add(listEntity);

        return list;
    }

    protected ArrayList getFindCardData(String type) {
        ArrayList list = new ArrayList();
        FindCardEntity findCardEntity = null;
        for (int i = 0; i < 2; i++) {
            findCardEntity = new FindCardEntity();
            findCardEntity.title = "微信登录与微信支付";
            findCardEntity.picture = "http://demo.edusoho.com/files/default/2015/07-20/150221d08183090386.jpg?6.15.3";
            findCardEntity.price = 0;
            findCardEntity.studentNum = 20;
            findCardEntity.type = type;
            findCardEntity.startTime = "2016-02-07 22:13:50+08:00";
            findCardEntity.endTime = "2016-02-07 22:13:50+08:00";
            findCardEntity.nickname = "sujudz";
            findCardEntity.avatar = "http://demo.edusoho.com/files/user/2014/10-13/132605d4ca82252495.JPG?6.15.3";
            list.add(findCardEntity);
        }

        for (int i = 0; i < 1; i++) {
            findCardEntity = new FindCardEntity();
            findCardEntity.title = "微信登录与微信支付2";
            findCardEntity.picture = "http://demo.edusoho.com/files/default/2015/10-14/1614400dc36d466096.jpg?6.15.3";
            findCardEntity.price = 20.5f;
            findCardEntity.studentNum = 20;
            findCardEntity.type = type;
            findCardEntity.startTime = "2016-02-07 22:13:50+08:00";
            findCardEntity.endTime = "2016-03-07 22:13:50+08:00";
            findCardEntity.nickname = "admin";
            findCardEntity.avatar = "http://demo.edusoho.com/files/default/2015/07-30/1211131d9908593356.jpg?6.15.3";
            list.add(findCardEntity);
        }

        for (int i = 0; i < 2; i++) {
            findCardEntity = new FindCardEntity();
            findCardEntity.title = "微信登录与微信支付3";
            findCardEntity.picture = "http://demo.edusoho.com/files/default/2015/10-14/161517518061383672.jpg?6.15.3";
            findCardEntity.price = 20.5f;
            findCardEntity.studentNum = 20;
            findCardEntity.type = type;
            findCardEntity.startTime = "0";
            findCardEntity.endTime = "0";
            findCardEntity.startTime = "2016-06-07 22:13:50+08:00";
            findCardEntity.endTime = "2016-06-08 22:13:50+08:00";
            findCardEntity.nickname = "咯咯米";
            findCardEntity.avatar = "http://demo.edusoho.com/files/default/2015/08-31/091008008a96767512.jpg?6.15.3";
            list.add(findCardEntity);
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
