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

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.FindListAdapter;
import com.edusoho.kuozhi.v3.adapter.SchoolBannerAdapter;
import com.edusoho.kuozhi.v3.entity.discovery.DiscoveryClassroom;
import com.edusoho.kuozhi.v3.entity.discovery.DiscoveryColumn;
import com.edusoho.kuozhi.v3.entity.discovery.DiscoveryCourse;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.discovery.DiscoveryModel;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.provider.SystemProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.SchoolBanner;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EdusohoViewPager;

import java.util.Iterator;
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
    private FindListAdapter mFindListAdapter;

    private DiscoveryModel discoveryModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_find_layout);
        mSystemProvider = ModelProvider.initProvider(mContext, SystemProvider.class);
        discoveryModel = new DiscoveryModel();
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
        getDiscoveryData();
        initSchoolBanner(false);
        mFindContentLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getDiscoveryData();
                mFindContentLayout.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, content, header);
            }
        });
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

    private void getDiscoveryData() {
        discoveryModel.getDiscoveryColumns(new ResponseCallbackListener<List<DiscoveryColumn>>() {
            @Override
            public void onSuccess(List<DiscoveryColumn> discoveryColumnList) {
                mFindListAdapter = new FindListAdapter(mContext);
                for (final DiscoveryColumn discoveryColumn : discoveryColumnList) {
                    if ("course".equals(discoveryColumn.type) || "live".equals(discoveryColumn.type)) {
                        discoveryModel.getDiscoveryCourseByColumn(discoveryColumn, new ResponseCallbackListener<List<DiscoveryCourse>>() {
                            @Override
                            public void onSuccess(List<DiscoveryCourse> discoveryCourseList) {
                                if (discoveryCourseList != null && discoveryCourseList.size() > 0) {
                                    filterCoursesInClassroom(discoveryCourseList);
                                    if (discoveryCourseList.size() % 2 != 0) {
                                        discoveryCourseList.add(new DiscoveryCourse(true));
                                    }
                                    discoveryColumn.data = discoveryCourseList;
                                    mFindListAdapter.addData(discoveryColumn);
                                }
                            }

                            @Override
                            public void onFailure(String code, String message) {

                            }
                        });
                    } else if ("classroom".equals(discoveryColumn.type)) {
                        discoveryModel.getDiscoveryClassroomByColumn(discoveryColumn, new ResponseCallbackListener<List<DiscoveryClassroom>>() {
                            @Override
                            public void onSuccess(List<DiscoveryClassroom> discoveryClassroomList) {
                                if (discoveryClassroomList != null && discoveryClassroomList.size() > 0) {
                                    if (discoveryClassroomList.size() % 2 != 0) {
                                        discoveryClassroomList.add(new DiscoveryClassroom(true));
                                    }
                                    discoveryColumn.data = discoveryClassroomList;
                                    mFindListAdapter.addData(discoveryColumn);
                                }
                            }

                            @Override
                            public void onFailure(String code, String message) {

                            }
                        });
                    }
                }
                mListView.setAdapter(mFindListAdapter);
            }

            @Override
            public void onFailure(String code, String message) {

            }
        });
    }

    private void filterCoursesInClassroom(List<DiscoveryCourse> list) {
        Iterator<DiscoveryCourse> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().parentId != 0) {
                iterator.remove();
            }
        }
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
