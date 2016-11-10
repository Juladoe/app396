package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.v3.adapter.NofityListAdapter;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.push.Notify;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.service.message.push.ESDbManager;
import com.edusoho.kuozhi.v3.service.message.push.NotifyDbHelper;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by suju on 16/11/10.
 */
public class NotifyActivity extends ActionBarBaseActivity {

    private RecyclerView mListView;
    private PtrClassicFrameLayout mPtrFrame;
    private NotifyDbHelper mNotifyDbHelper;
    private NofityListAdapter mListAdapter;
    private int mStart = 0;
    private boolean canLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK, "通知中心");
        setContentView(R.layout.activity_nofity_layout);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IMClient.getClient().getConvManager().clearReadCount(Destination.NOTIFY);
    }

    private void initView() {
        mListView = (RecyclerView) findViewById(R.id.listview);
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setReverseLayout(true);
        mListView.setLayoutManager(linearLayoutManager);
        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.refreshComplete();
                List<Notify> notifyList =  mNotifyDbHelper.getNofityList(mStart, 6);
                if (notifyList.isEmpty()) {
                    canLoad = false;
                    return;
                }

                mListAdapter.addDataList(notifyList);
                mStart += notifyList.size();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return canLoad && super.checkCanDoRefresh(frame, content, header);
            }
        });
    }

    private void initData() {
        School school = getAppSettingProvider().getCurrentSchool();
        mNotifyDbHelper= new NotifyDbHelper(mContext, new ESDbManager(mContext, school.getDomain()));
        List<Notify> notifyList =  mNotifyDbHelper.getNofityList(mStart, 6);
        mStart += notifyList.size();

        mListAdapter = new NofityListAdapter(mContext);
        mListView.setAdapter(mListAdapter);
        mListAdapter.addDataList(notifyList);
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
