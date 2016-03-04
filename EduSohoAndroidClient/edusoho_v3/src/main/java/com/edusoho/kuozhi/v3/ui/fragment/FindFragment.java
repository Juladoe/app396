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
import com.edusoho.kuozhi.v3.model.bal.Discovery.DiscoveryClassroomResult;
import com.edusoho.kuozhi.v3.model.bal.Discovery.DiscoveryColumn;
import com.edusoho.kuozhi.v3.model.bal.Discovery.DiscoveryColumnResult;
import com.edusoho.kuozhi.v3.model.bal.Discovery.DiscoveryCourseResult;
import com.edusoho.kuozhi.v3.model.bal.http.ModelDecor;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.provider.SystemProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.SchoolBanner;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EdusohoViewPager;
import com.google.gson.reflect.TypeToken;

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
                mFindListAdapter = new FindListAdapter(mContext, discoveryColumnList);
                for (final DiscoveryColumn discoveryColumn : discoveryColumnList) {
                    if ("course".equals(discoveryColumn.type) || "live".equals(discoveryColumn.type)) {
                        String url = String.format(Const.DISCOVERY_COURSES_COLUMNS, discoveryColumn.orderType, discoveryColumn.categoryId + "",
                                discoveryColumn.showCount + "", discoveryColumn.type);
                        RequestUrl requestUrl = app.bindNewApiUrl(url, true);
                        mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                DiscoveryCourseResult courseResult = ModelDecor.getInstance().decor(response, new TypeToken<DiscoveryCourseResult>() {
                                });
                                if (courseResult != null && courseResult.resources.length > 0) {
                                    discoveryColumn.setDiscoveryCardProperty(courseResult.resources);
                                    mListView.setAdapter(mFindListAdapter);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                    } else if ("classroom".equals(discoveryColumn.type)) {
                        String url = String.format(Const.DISCOVERY_CLASSROOMS_COLUMNS, discoveryColumn.orderType, discoveryColumn.categoryId + "",
                                discoveryColumn.showCount + "");
                        RequestUrl requestUrl = app.bindNewApiUrl(url, true);
                        mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                DiscoveryClassroomResult classroomResult = ModelDecor.getInstance().decor(response, new TypeToken<DiscoveryClassroomResult>() {
                                });
                                if (classroomResult != null && classroomResult.resources.length > 0) {
                                    discoveryColumn.setDiscoveryCardProperty(classroomResult.resources);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                    }
                }

                //mListView.setAdapter(mFindListAdapter);
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
                if (discoveryColumn != null && discoveryColumn.resources != null) {
                    normalCallback.success(discoveryColumn.resources);
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
