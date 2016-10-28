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
import com.edusoho.kuozhi.v3.model.provider.LiveRoomProvider;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.view.LiveNoticeListDecoration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suju on 16/10/23.
 */
public class LiveNoticeListActivity extends ActionBarBaseActivity {

    public static final String TOKEN = "token";
    public static final String LIVE_HOST = "liveHost";
    public static final String ROOM_NO = "roomNo";

    private String mToken;
    private String mLiveHost;
    private String mRoomNo;
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
        mLiveHost = dataIntent.getStringExtra(LIVE_HOST);
    }

    private Promise getServerTime() {
        final Promise promise = new Promise();
        new LiveRoomProvider(mContext).getLiveServerTime(mLiveHost, mToken)
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
        new LiveRoomProvider(mContext).getLiveNoticeList(mLiveHost, mToken, mRoomNo
        ).success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap noticesMap) {
                mLoadView.setVisibility(View.GONE);
                mNoticeList = getNoticeListFromSignals(noticesMap);
                if (mNoticeList == null || mNoticeList.isEmpty()) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    return;
                }
                LiveNoticeListAdapter listAdapter = new LiveNoticeListAdapter(mContext, mNoticeList);
                mListView.setAdapter(listAdapter);
                mListView.scrollToPosition(mNoticeList.size() - 1);
            }
        });
    }

    private List<NoticeEntity> getNoticeListFromSignals(LinkedHashMap noticesMap) {
        List<NoticeEntity> noticeList = new ArrayList<>();
        if (noticesMap.get("history") instanceof ArrayList) {
            return noticeList;
        }
        LinkedHashMap<String, String> notices = (LinkedHashMap<String, String>) noticesMap.get("history");
        if (notices == null || notices.isEmpty()) {
            return noticeList;
        }

        for (Map.Entry<String, String> entity : notices.entrySet()) {
            NoticeEntity noticeEntity = new NoticeEntity();
            noticeEntity.setContent(entity.getValue());
            noticeEntity.setCreateTime(AppUtil.parseLong(entity.getKey()));
            noticeList.add(noticeEntity);
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
