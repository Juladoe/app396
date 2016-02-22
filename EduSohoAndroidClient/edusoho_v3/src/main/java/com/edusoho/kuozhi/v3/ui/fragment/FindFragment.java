package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.FindCardItemAdapter;
import com.edusoho.kuozhi.v3.adapter.SchoolBannerAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.provider.SystemProvider;
import com.edusoho.kuozhi.v3.model.sys.FindCardEntity;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.SchoolBanner;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EdusohoViewPager;
import com.edusoho.kuozhi.v3.view.FindCardView;

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

    private ViewGroup mCardContent;
    private PtrClassicFrameLayout mFindContentLayout;
    private EdusohoViewPager mFindBannerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_find_layout);
        mSystemProvider = ModelProvider.initProvider(mContext, SystemProvider.class);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mFindContentLayout = (PtrClassicFrameLayout) view.findViewById(R.id.find_content);
        mFindBannerView = (EdusohoViewPager) view.findViewById(R.id.find_banner);
        mCardContent = (ViewGroup) view.findViewById(R.id.card_content);

        initSchoolBanner(false);
        initCard();

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

    protected ArrayList getFindCardData() {
        ArrayList list = new ArrayList();
        FindCardEntity findCardEntity = new FindCardEntity();
        findCardEntity.title = "微信登录与微信支付";
        findCardEntity.picture = "http://demo.edusoho.com/files/default/2015/07-20/150221d08183090386.jpg?6.15.3";
        findCardEntity.price = 20.5f;
        findCardEntity.studentNum = 20;
        list.add(findCardEntity);
        list.add(findCardEntity);
        list.add(findCardEntity);

        return list;
    }

    protected void initCard() {
        FindCardView findCardView = new FindCardView(mContext);
        findCardView.setTitle("推荐班级");
        findCardView.setAdapter(new FindCardItemAdapter(mContext, getFindCardData()));
        mCardContent.addView(findCardView);

        findCardView = new FindCardView(mContext);
        findCardView.setTitle("推荐班级");
        findCardView.setAdapter(new FindCardItemAdapter(mContext, getFindCardData()));
        mCardContent.addView(findCardView);
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
                    mFindBannerView.setCurrentItem(1);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.find_menu, menu);
    }
}
