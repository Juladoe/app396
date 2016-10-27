package com.edusoho.kuozhi.v3.ui.live;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.broadcast.IMBroadcastReceiver;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.managar.IMRoleManager;
import com.edusoho.kuozhi.imserver.ui.IMessageListPresenter;
import com.edusoho.kuozhi.imserver.ui.MessageListFragment;
import com.edusoho.kuozhi.imserver.ui.adapter.MessageRecyclerListAdapter;
import com.edusoho.kuozhi.imserver.ui.helper.MessageResourceHelper;
import com.edusoho.kuozhi.imserver.ui.listener.MessageControllerListener;
import com.edusoho.kuozhi.v3.adapter.LiveChatListAdapter;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.model.provider.LiveChatDataProvider;
import com.edusoho.kuozhi.v3.model.provider.LiveRoomProvider;
import com.edusoho.kuozhi.v3.ui.fragment.ViewPagerFragment;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.liveplayer.PLVideoViewActivity;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by suju on 16/10/12.
 */
public class LessonLivePlayerActivity extends PLVideoViewActivity implements ILiveVideoView {

    private Context mContext;
    private int mLessonId;
    private String mConversationNo;
    private String mRole;
    private String mToken;
    private String mClientId;
    private String mClientName;
    private String mRoomNo;
    private String mPlayUrl;
    private String mLiveTitle;

    private LiveImClient mLiveImClient;
    private ILiveVideoPresenter mILiveVideoPresenter;
    private ILiveChatPresenter mILiveChatPresenter;
    protected IMessageListPresenter mIMessageListPresenter;
    protected MessageListFragment mMessageListFragment;
    private IMBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.setStatusBarTranslucent(this);
    }

    private void initParams() {
        mConversationNo = getIntent().getStringExtra("convNo");
        mRole = getIntent().getStringExtra("role");
        mToken = getIntent().getStringExtra("token");
        mRoomNo = getIntent().getStringExtra("roomNo");
        mPlayUrl = getIntent().getStringExtra("playUrl");
        mClientId = getIntent().getStringExtra("clientId");
        mClientName = getIntent().getStringExtra("clientName");
        mLiveTitle = getIntent().getStringExtra("title");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLiveImClient != null) {
            mLiveImClient.destory();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMessageListFragment == null || mLiveImClient == null) {
            return;
        }
        if (mILiveVideoPresenter != null) {
            mILiveVideoPresenter.handleHistorySignals();
        }
        registIMReceiver();
    }

    private void registIMReceiver() {
        mILiveChatPresenter = new LiveChatPresenterImpl(mContext, getIntent().getExtras(), mLiveImClient);
        mILiveChatPresenter.setView(mMessageListFragment);
        mReceiver = new LiveIMBroadcastReceiver(mILiveVideoPresenter, mILiveChatPresenter);
        mContext.registerReceiver(mReceiver, new IntentFilter(IMBroadcastReceiver.ACTION_NAME));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
        mReceiver = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.live_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem noticeItem = menu.findItem(R.id.menu_notice);
        if (noticeItem != null) {
            int currentOrientation = getResources().getConfiguration().orientation;
            noticeItem.setVisible(currentOrientation == Configuration.ORIENTATION_PORTRAIT);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_notice) {
            Intent intent = new Intent(mContext, LiveNoticeListActivity.class);
            intent.putExtra(LiveNoticeListActivity.ROLE, mRole);
            intent.putExtra(LiveNoticeListActivity.TOKEN, mToken);
            intent.putExtra(LiveNoticeListActivity.CLIENT_ID, mClientId);
            intent.putExtra(LiveNoticeListActivity.ROOM_NO, mRoomNo);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        invalidateOptionsMenu();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void setNotice(String notice) {
        mNoticeView.setText(notice);
        mNoticeView.requestFocus();
    }

    @Override
    public void hideNoticeView() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mNoticeView, "alpha",  1.0f, 0.0f);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }

    @Override
    public synchronized void showNoticeView() {
        if (mNoticeView.getTag() != null) {
            return;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mNoticeView, "alpha",  0.0f, 1.0f);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(360);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mNoticeView.setTag(null);
            }
        });
        mNoticeView.setTag(objectAnimator);
        objectAnimator.start();
    }

    @Override
    public void setLivePlayStatus(boolean isResting) {
        if (isResting) {
            pauseLive();
        } else {
            resumeLive();
        }
    }

    private void initChatRoom() {
        new IMProvider(mContext).getLiveChatServer(
                mRoomNo, mToken, mRole, mClientId
        ).success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap data) {
                if (data == null) {
                    return;
                }

                String token = data.get("token").toString();
                LinkedHashMap<String, String> servers = (LinkedHashMap<String, String>) data.get("servers");
                ArrayList<String> hostList = new ArrayList<>();
                for (String host : servers.values()) {
                    hostList.add(host + "?token=" + token);
                }

                mLiveImClient = new LiveImClient(mContext);
                mLiveImClient.setOnConnectedCallback(new LiveImClient.OnConnectedCallback() {
                    @Override
                    public void onConnected() {
                        attachMessageListFragment();
                    }
                });
                mLiveImClient.start(
                        AppUtil.parseInt(mClientId), mClientName, new ArrayList<String>(), hostList);
            }
        });
    }

    @Override
    public void checkLivePlayStatus() {
        new LiveRoomProvider(mContext).getLiveRoom(
                mRoomNo, mToken, mRole, mClientId
        ).success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap data) {
                String status = data.get("status").toString();
                Log.d("status:", status);
                if (CLOSE.equals(status)) {
                    setPlayStatus(CLOSE);
                    return;
                }
                if (NOT_START.equals(status)) {
                    setPlayStatus(NOT_START);
                    return;
                }
                startPlay(mPlayUrl);
            }
        });
    }

    @Override
    protected void initView() {
        mContext = getBaseContext();
        initParams();
        super.initView();
        setLiveTitle(mLiveTitle);
        setLiveDesc(null);
        initChatRoom();
        checkLivePlayStatus();
    }

    protected IMessageListPresenter createProsenter() {
        Bundle bundle = new Bundle();
        bundle.putString(MessageListFragment.CONV_NO, mConversationNo);
        bundle.putInt(MessageListFragment.TARGET_ID, mLessonId);
        bundle.putString(MessageListFragment.TARGET_TYPE, "live_chatroom");

        LiveChatMessageListPresenterImpl presenter = new LiveChatMessageListPresenterImpl(
                mContext,
                bundle,
                new LiveChatDataProvider.MockConvManager(mContext),
                new IMRoleManager(mContext),
                new MessageResourceHelper(mContext),
                new LiveChatDataProvider(mLiveImClient.getImBinder()),
                mMessageListFragment
        );
        presenter.setLiveData(getIntent().getExtras());

        return presenter;
    }

    protected void attachMessageListFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("im_container");

        if (fragment != null) {
            mMessageListFragment = (MessageListFragment) fragment;
        } else {
            mMessageListFragment = createFragment();
            fragmentTransaction.add(R.id.chat_content, mMessageListFragment, "im_container");
            fragmentTransaction.commitAllowingStateLoss();
        }
        mIMessageListPresenter = createProsenter();
        mIMessageListPresenter.addMessageControllerListener(getMessageControllerListener());

        mILiveVideoPresenter = new LiveVideoPresenterImpl(
                mContext, getIntent().getExtras(), LessonLivePlayerActivity.this, mMessageListFragment);
        mILiveVideoPresenter.handleHistorySignals();
        registIMReceiver();
    }

    protected MessageControllerListener getMessageControllerListener() {
        return new MessageControllerListener() {

            @Override
            public void onShowImage(int index, ArrayList<String> imageList) {
                Bundle bundle = new Bundle();
                bundle.putInt("index", index);
                bundle.putStringArrayList("imageList", imageList);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
                viewPagerFragment.setArguments(bundle);
                fragmentTransaction.setCustomAnimations(R.anim.window_zoom_open, R.anim.window_zoom_exit);
                viewPagerFragment.show(fragmentTransaction, "viewpager");
            }

            @Override
            public void onShowUser(Role role) {
            }

            @Override
            public void onShowWebPage(String url) {
                Bundle bundle = new Bundle();
                bundle.putString(Const.WEB_URL, url);
                CoreEngine.create(mContext).runNormalPluginWithBundle("WebViewActivity", mContext, bundle);
            }

            @Override
            public void selectPhoto() {
            }

            @Override
            public void takePhoto() {
            }

            @Override
            public void onShowActivity(Bundle bundle) {
            }
        };
    }

    protected MessageListFragment createFragment() {
        MessageListFragment messageListFragment = (MessageListFragment) Fragment.instantiate(
                getBaseContext(), MessageListFragment.class.getName());
        MessageRecyclerListAdapter messageRecyclerListAdapter = new LiveChatListAdapter(mContext);
        messageRecyclerListAdapter.setCurrentId(AppUtil.parseInt(mClientId));
        messageListFragment.setAdapter(messageRecyclerListAdapter);
        return messageListFragment;
    }
}
