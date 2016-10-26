package com.edusoho.kuozhi.v3.ui.live;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.LiveNoticeListAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.live.Signal;
import com.edusoho.kuozhi.v3.model.provider.LiveRoomProvider;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.view.LiveNoticeListDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suju on 16/10/23.
 */
public class LiveNoticeListActivity extends ActionBarBaseActivity {

    public static final String TOKEN = "token";
    public static final String ROLE = "role";
    public static final String ROOM_NO = "roomNo";
    public static final String CLIENT_ID = "clientId";

    private String mToken;
    private String mRole;
    private String mRoomNo;
    private String mClientId;
    private List<NoticeEntity> mNoticeList;

    private RecyclerView mListView;
    private TextView mEmptyView;
    private ProgressBar mLoadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_notices_layout);
        setBackMode(BACK, "公告");

        initView();
        checkParams();
        loadNoticeList();
    }

    private void initView() {
        mListView = (RecyclerView) findViewById(R.id.listview);
        mEmptyView = (TextView) findViewById(R.id.tv_live_empty);
        mLoadView = (ProgressBar) findViewById(R.id.pb_live_load);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mListView.addItemDecoration(new LiveNoticeListDecoration(
                mContext, LinearLayoutManager.VERTICAL, R.drawable.live_notice_list_decoration_line));
        mListView.setLayoutManager(layoutManager);
    }

    private void checkParams() {
        Intent dataIntent = getIntent();
        mToken = dataIntent.getStringExtra(TOKEN);
        mRoomNo = dataIntent.getStringExtra(ROOM_NO);
        mRole = dataIntent.getStringExtra(ROLE);
        mClientId = dataIntent.getStringExtra(CLIENT_ID);
    }

    private Promise getServerTime() {
        final Promise promise = new Promise();
        new LiveRoomProvider(mContext).getLiveServerTime()
                .success(new NormalCallback<LinkedHashMap>() {
                    @Override
                    public void success(LinkedHashMap data) {
                        Double time = (Double) data.get("time");
                        promise.resolve(time.longValue());
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                promise.resolve((long)0);
            }
        });

        return promise;
    }

    private void loadNoticeList() {
        getServerTime().then(new PromiseCallback<Long>() {
            @Override
            public Promise invoke(Long time) {
                if (time == 0) {
                    time = System.currentTimeMillis();
                }
                long startTime = 0;
                new LiveRoomProvider(mContext).getLiveSignals(
                        mRoomNo, mToken, mRole, mClientId, startTime, time
                ).success(new NormalCallback<LinkedHashMap<String, Signal>>() {
                    @Override
                    public void success(LinkedHashMap<String, Signal> signalList) {
                        mLoadView.setVisibility(View.GONE);
                        mNoticeList = getNoticeListFromSignals(signalList);
                        if (mNoticeList == null || mNoticeList.isEmpty()) {
                            mEmptyView.setVisibility(View.VISIBLE);
                            return;
                        }
                        LiveNoticeListAdapter listAdapter = new LiveNoticeListAdapter(mContext, mNoticeList);
                        mListView.setAdapter(listAdapter);
                        mListView.scrollToPosition(mNoticeList.size() - 1);
                    }
                });
                return null;
            }
        });
    }

    private List<NoticeEntity> getNoticeListFromSignals(LinkedHashMap<String, Signal> signalMap) {
        List<String> keyArray = new ArrayList<>(signalMap.keySet());
        List<NoticeEntity> noticeList = new ArrayList<>();
        for (String key : keyArray) {
            Signal signal = signalMap.get(key);
            if ("102002".equals(signal.getType())) {
                Map noticeData = signal.getData();
                NoticeEntity noticeEntity = new NoticeEntity();
                noticeEntity.setContent(noticeData.get("info").toString());
                noticeEntity.setCreateTime(signal.getTime());
                noticeList.add(noticeEntity);
            }
        }

        return noticeList;
    }

    public static class NoticeEntity {

        private String content;
        private long createTime;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
