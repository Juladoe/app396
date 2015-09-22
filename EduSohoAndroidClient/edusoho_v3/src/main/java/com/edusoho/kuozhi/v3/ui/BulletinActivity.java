package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.bal.push.Bulletin;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.sql.BulletinDataSource;
import com.edusoho.kuozhi.v3.util.sql.NewDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collections;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by JesseHuang on 15/7/14.
 * 公告
 */
public class BulletinActivity extends ActionBarBaseActivity {

    private ListView mListView;
    private PtrClassicFrameLayout mPtrFrame;
    private View mEmptyView;
    private TextView tvEmpty;
    private BulletinDataSource mBulletinDataSource;
    private BulletinAdapter mBulletinAdapter;
    private String mHeadImageUrl;
    private static final int LIMIT = 15;
    private int mStart = 0;

    private static long TIME_INTERVAL = 60 * 5;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin);
        initView();
        initData();
    }

    private void initView() {
        mHandler = new Handler();
        mListView = (ListView) findViewById(R.id.lv_bulletin);
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        mEmptyView = findViewById(R.id.view_empty);
        tvEmpty = (TextView) findViewById(R.id.tv_empty_text);
        tvEmpty.setText(getResources().getString(R.string.announcement_empty_text));
    }

    private void initData() {
        setBackMode(BACK, "网校公告");
        if (TextUtils.isEmpty(mHeadImageUrl)) {
            NewDataSource newDataSource = new NewDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
            List<New> bulletins = newDataSource.getNews("WHERE TYPE = ? ORDER BY CREATEDTIME DESC", TypeBusinessEnum.BULLETIN.getName());
            if (bulletins.size() > 0) {
                mHeadImageUrl = bulletins.get(0).imgUrl;
            }
            if (TextUtils.isEmpty(mHeadImageUrl)) {
                RequestUrl requestUrl = app.bindNewUrl(Const.SCHOOL_APPS, true);
                mActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SchoolApp[] schoolAppResult = mActivity.parseJsonValue(response, new TypeToken<SchoolApp[]>() {
                        });
                        if (schoolAppResult.length != 0) {
                            mHeadImageUrl = app.schoolHost + schoolAppResult[0].avatar;
                        }
                    }
                }, null);
            }
        }
        if (mBulletinDataSource == null) {
            mBulletinDataSource = new BulletinDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        }
        List<Bulletin> bulletinList = getBulletins(mStart);
        NotificationUtil.cancelById(bulletinList.size() == 0 ? 0 : bulletinList.get(bulletinList.size() - 1).id);
        mBulletinAdapter = new BulletinAdapter(bulletinList);
        mListView.setAdapter(mBulletinAdapter);
        mListView.post(mRunnable);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                mBulletinAdapter.addItems(getBulletins(mStart));
                mPtrFrame.refreshComplete();
                mListView.postDelayed(mRunnable, 100);
            }
        });
        mHandler.postDelayed(mNotifyNewFragment2UpdateItemBadgeRunnable, 500);
        setListVisibility(mBulletinAdapter.getCount() == 0);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mListView.setSelection(mStart);
        }
    };

    private List<Bulletin> getBulletins(int start) {
        List<Bulletin> bulletinList = mBulletinDataSource.getBulletins(start, LIMIT, String.format("SCHOOLDOMAIN = '%s' ", app.domain), "CREATEDTIME DESC");
        mStart = start + bulletinList.size();
        Collections.reverse(bulletinList);
        return bulletinList;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mBulletinAdapter.clear();
        mStart = 0;
        mBulletinAdapter.addItems(getBulletins(mStart));
        mListView.post(mRunnable);
        mHandler.postDelayed(mNotifyNewFragment2UpdateItemBadgeRunnable, 500);
    }

    private Runnable mNotifyNewFragment2UpdateItemBadgeRunnable = new Runnable() {
        @Override
        public void run() {
            app.sendMsgToTarget(NewsFragment.UPDATE_UNREAD_BULLETIN, null, NewsFragment.class);
        }
    };

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        switch (messageType.code) {
            case Const.ADD_BULLETIT_MSG:
                WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
                Bulletin bulletin = new Bulletin(wrapperMessage);
                mBulletinAdapter.addItem(bulletin);
                break;
        }
        setListVisibility(mBulletinAdapter.getCount() == 0);
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{new MessageType(Const.ADD_BULLETIT_MSG, source)};
    }

    public class BulletinAdapter extends BaseAdapter {
        private List<Bulletin> mList;
        private DisplayImageOptions mOptions;

        public BulletinAdapter(List<Bulletin> list) {
            mList = list;
            mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).showImageForEmptyUri(R.drawable.default_avatar).
                    showImageOnFail(R.drawable.default_avatar).build();
        }

        public void addItems(List<Bulletin> list) {
            mList.addAll(0, list);
            notifyDataSetChanged();
        }

        public void addItem(Bulletin bulletin) {
            mList.add(bulletin);
            notifyDataSetChanged();
        }

        public void clear() {
            if (mList.size() > 0) {
                mList.clear();
                notifyDataSetChanged();
            }
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            BulletinActivity.this.setListVisibility(getCount() == 0);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Bulletin getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_bulletin, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Bulletin bulletin = mList.get(position);
            holder.tvCreatedTime.setVisibility(View.GONE);
            if (position > 0) {
                if (bulletin.createdTime - mList.get(position - 1).createdTime > TIME_INTERVAL) {
                    holder.tvCreatedTime.setVisibility(View.VISIBLE);
                    holder.tvCreatedTime.setText(AppUtil.convertMills2Date(((long) bulletin.createdTime) * 1000));
                }
            } else {
                holder.tvCreatedTime.setVisibility(View.VISIBLE);
                holder.tvCreatedTime.setText(AppUtil.convertMills2Date(((long) bulletin.createdTime) * 1000));
            }
            holder.tvContent.setText(bulletin.content);
            ImageLoader.getInstance().displayImage(mHeadImageUrl, holder.ivHeadImageUrl, mOptions);
            return convertView;
        }
    }

    public static class ViewHolder {
        public ImageView ivHeadImageUrl;
        public TextView tvContent;
        public TextView tvCreatedTime;

        public ViewHolder(View view) {
            ivHeadImageUrl = (ImageView) view.findViewById(R.id.ci_send_pic);
            tvContent = (TextView) view.findViewById(R.id.tv_send_content);
            tvCreatedTime = (TextView) view.findViewById(R.id.tv_send_time);
        }
    }

    /**
     * 设置空数据背景ICON
     *
     * @param visibility 是否空数据
     */
    private void setListVisibility(boolean visibility) {
        mListView.setVisibility(visibility ? View.GONE : View.VISIBLE);
        mEmptyView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
}
