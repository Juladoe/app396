package com.edusoho.kuozhi.v3.ui.live;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
    private TextView mNoticeView;

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

    private void getLiveRoom(String roomUrl) {
        new LessonProvider(mContext).getLiveRoom(roomUrl)
        .success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap data) {
                if (data == null) {
                    return;
                }
                mConversationNo = data.get("convNo").toString();
                initChatRoom();
                loadLiveRoomStatus();
            }
        });
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
    public void setNotice(String notice) {
        mNoticeView.setText(notice);
        mNoticeView.requestFocus();
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

                mILiveVideoPresenter = new LiveVideoPresenterImpl(mContext, getIntent().getExtras(), LessonLivePlayerActivity.this);
                mILiveVideoPresenter.handleHistorySignals();
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

    private void loadLiveRoomStatus() {
        new LiveRoomProvider(mContext).getLiveRoom(
                mRoomNo, mToken, mRole, mClientId
        ).success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap data) {
                String status = data.get("status").toString();
                if (NOT_START.equals(status)) {
                    setPlayStatus(NOT_START);
                    return;
                }
                startPlay(mPlayUrl);
            }
        });
    }

    private void valiteLessonInfo() {
        new LessonProvider(mContext).getLesson(mLessonId)
        .success(new NormalCallback<LessonItem>() {
            @Override
            public void success(LessonItem lessonItem) {
                if (lessonItem == null || lessonItem.id == 0) {
                    CommonUtil.longToast(mContext, "直播课时信息获取失败");
                    finish();
                    return;
                }

                setLiveTitle(lessonItem.title);
                setLiveDesc(lessonItem.summary);
                getLiveRoom(getIntent().getStringExtra("roomUrl"));
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                CommonUtil.longToast(mContext, "直播课时信息获取失败");
                finish();
            }
        });
    }

    @Override
    protected void initView() {
        mContext = getBaseContext();
        initParams();
        super.initView();
        setBottomView(LayoutInflater.from(getBaseContext()).inflate(R.layout.view_liveplayer_chatroom_layout, null));
        mNoticeView = (TextView) findViewById(R.id.tv_live_notice);

        setLiveTitle(mLiveTitle);
        setLiveDesc(null);
        initChatRoom();
        loadLiveRoomStatus();
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
