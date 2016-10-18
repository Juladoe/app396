package com.edusoho.kuozhi.v3.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.IImServerAidlInterface;
import com.edusoho.kuozhi.imserver.ImService;
import com.edusoho.kuozhi.imserver.broadcast.IMBroadcastReceiver;
import com.edusoho.kuozhi.imserver.broadcast.IMServiceStartedBroadcastReceiver;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.listener.IMConnectStatusListener;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.imserver.managar.IMConvManager;
import com.edusoho.kuozhi.imserver.managar.IMRoleManager;
import com.edusoho.kuozhi.imserver.ui.IMessageListPresenter;
import com.edusoho.kuozhi.imserver.ui.IMessageListView;
import com.edusoho.kuozhi.imserver.ui.MessageListFragment;
import com.edusoho.kuozhi.imserver.ui.MessageListPresenterImpl;
import com.edusoho.kuozhi.imserver.ui.data.IMessageDataProvider;
import com.edusoho.kuozhi.imserver.ui.helper.MessageResourceHelper;
import com.edusoho.kuozhi.imserver.ui.listener.MessageControllerListener;
import com.edusoho.kuozhi.imserver.util.IMConnectStatus;
import com.edusoho.kuozhi.v3.adapter.LiveChatListAdapter;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.im.LiveMessageBody;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.model.provider.LiveChatDataProvider;
import com.edusoho.kuozhi.v3.model.provider.LiveRoomProvider;
import com.edusoho.kuozhi.v3.model.provider.UserProvider;
import com.edusoho.kuozhi.v3.ui.fragment.ViewPagerFragment;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.liveplayer.PLVideoViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by suju on 16/10/12.
 */
public class LessonLivePlayerActivity extends PLVideoViewActivity {

    private Context mContext;
    private int mLessonId;
    private String mConversationNo;
    private LiveImClient mLiveImClient;
    protected IMessageListPresenter mIMessageListPresenter;
    protected MessageListFragment mMessageListFragment;
    private LinkedHashMap mLiveData;

    private TextView mNoticeView;

    private void initParams() {
        mLessonId = AppUtil.parseInt(getIntent().getStringExtra(Const.LESSON_ID));
        mConversationNo = getIntent().getStringExtra("convNo");
    }

    private void getLiveRoom(String roomUrl) {
        new LessonProvider(mContext).getLiveRoom(roomUrl)
        .success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap data) {
                if (data == null) {
                    startPlay("http://demo.edusoho.com/mapi_v2/Lesson/getLocalVideo?targetId=2887&token=kp831f26ba8gcg0ks4oco4g4kwgckow");
                    return;
                }
                mConversationNo = data.get("convNo").toString();
                LinkedHashMap<String, String> playData = (LinkedHashMap<String, String>) data.get("play");
                String streamUrl = playData.get("url").toString();
                String streamId = playData.get("stream").toString();
                startPlay(streamUrl + "/" + streamId);
                mLiveData = data;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.live_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initChatRoom() {
        String host = mLiveData.get("url").toString();
        String roomNo = mLiveData.get("roomNo").toString();
        String userToken = mLiveData.get("token").toString();
        String role = mLiveData.get("role").toString();
        final String clientId = mLiveData.get("clientId").toString();
        final String clientName = mLiveData.get("clientName").toString();

        new IMProvider(mContext).getLiveChatServer(
                host, roomNo, userToken, role, clientId
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

                mLiveImClient = new LiveImClient();
                mLiveImClient.start(
                        AppUtil.parseInt(clientId), clientName, new ArrayList<String>(), hostList);
            }
        });
    }

    private void loadLiveRoomStatus() {
        String host = mLiveData.get("url").toString();
        String roomNo = mLiveData.get("roomNo").toString();
        String token = mLiveData.get("token").toString();
        String role = mLiveData.get("role").toString();
        new LiveRoomProvider(mContext).getLiveRoom(
                host, roomNo, token, role
        ).success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap data) {
                Log.d("loadLiveRoomStatus", data.get("status").toString());
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
        valiteLessonInfo();
    }

    protected IMessageListPresenter createProsenter() {
        Bundle bundle = new Bundle();
        bundle.putString(MessageListFragment.CONV_NO, mConversationNo);
        bundle.putInt(MessageListFragment.TARGET_ID, 0);
        bundle.putString(MessageListFragment.TARGET_TYPE, "live_chatroom");

        return new LiveChatMessageListPresenterImpl(
                bundle,
                new LiveChatDataProvider.MockConvManager(mContext),
                new IMRoleManager(mContext),
                new MessageResourceHelper(mContext),
                new LiveChatDataProvider(mLiveImClient.getImBinder()),
                mMessageListFragment
        );
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
        messageListFragment.setAdapter(new LiveChatListAdapter(mContext));
        return messageListFragment;
    }

    protected class LiveChatMessageListPresenterImpl extends MessageListPresenterImpl {

        private IMBroadcastReceiver mReceiver;
        String[] filterArray = {
                "100001",
                "100002",
                "101001",
                "101002",
                "101003",
                "103004",
                "103005",
                "103007",
                "103008",
                "103009",
                "103010",
                "102002"
        };

        public LiveChatMessageListPresenterImpl(Bundle params,
                                            IMConvManager convManager,
                                            IMRoleManager roleManager,
                                            MessageResourceHelper messageResourceHelper,
                                            IMessageDataProvider mIMessageDataProvider,
                                            IMessageListView messageListView) {
            super(params, convManager, roleManager, messageResourceHelper, mIMessageDataProvider, messageListView);

            String clientId = mLiveData.get("clientId").toString();
            String clientName = mLiveData.get("clientName").toString();
            setClientInfo(AppUtil.parseInt(clientId), clientName);
        }

        @Override
        protected Map<String, String> getRequestHeaders() {
            HashMap<String, String> map = new HashMap();
            String token = ApiTokenUtil.getApiToken(mContext);
            map.put("Auth-Token", TextUtils.isEmpty(token) ? "" : token);
            return map;
        }

        @Override
        protected void createRole(String type, int rid, MessageListPresenterImpl.RoleUpdateCallback callback) {
            createTargetRole(type, rid, callback);
        }

        @Override
        protected void createConvNo(MessageListPresenterImpl.ConvNoCreateCallback convNoCreateCallback) {
            convNoCreateCallback.onCreateConvNo(mConversationNo);
        }

        private boolean messageIsSignal(String type) {
            for (String filter : filterArray) {
                if (type.equals(filter)) {
                    return true;
                }
            }
            return false;
        }

        protected void createTargetRole(String type, int rid, final MessageListPresenterImpl.RoleUpdateCallback callback) {
            new UserProvider(mContext).getUserInfo(rid)
                    .success(new NormalCallback<User>() {
                        @Override
                        public void success(User user) {
                            Role role = new Role();
                            if (user == null) {
                                callback.onCreateRole(role);
                                return;
                            }
                            role.setRid(user.id);
                            role.setAvatar(user.mediumAvatar);
                            role.setType(Destination.USER);
                            role.setNickname(user.nickname);
                            callback.onCreateRole(role);
                        }
                    });
        }

        private void joinLiveChatRoom() {
            String host = mLiveData.get("url").toString();
            String roomNo = mLiveData.get("roomNo").toString();
            String token = mLiveData.get("token").toString();
            String role = mLiveData.get("role").toString();
            String clientId = mLiveData.get("clientId").toString();
            new LiveRoomProvider(mContext).joinLiveChatRoom(
                    host, roomNo, token, role, clientId
            ).success(new NormalCallback<LinkedHashMap>() {
                @Override
                public void success(LinkedHashMap data) {
                    String token = null;
                    if (data == null || TextUtils.isEmpty((token = data.get("token").toString()))) {
                        return;
                    }

                    try {
                        mLiveImClient.getImBinder().joinConversation(token, mConversationNo);
                    } catch (RemoteException e) {
                        Log.i("joinLiveChatRoom", "join error");
                    }
                }
            });
        }

        private void checkLivePlayStatus(MessageEntity messageEntity) {
            LiveMessageBody liveMessageBody = new LiveMessageBody(messageEntity.getMsg());
            try {
                JSONObject jsonObject = new JSONObject(liveMessageBody.getData());
                if (jsonObject.optBoolean("isResting")) {
                    pauseLive();
                } else {
                    resumeLive();
                }
            } catch (JSONException e) {
            }
        }

        private void updateNoticeFromMessage(MessageEntity message) {
            LiveMessageBody liveMessageBody = new LiveMessageBody(message.getMsg());
            try {
                JSONObject jsonObject = new JSONObject(liveMessageBody.getData());
                mNoticeView.setText(jsonObject.optString("info"));
            } catch (JSONException e) {
            }
        }

        @Override
        public void addMessageReceiver() {
            mReceiver = new IMBroadcastReceiver() {

                @Override
                protected void invokeReceiverSignal(MessageEntity message) {
                    String cmd = message.getCmd();
                    switch (cmd) {
                        case "connected":
                            joinLiveChatRoom();
                            break;
                        case "102002":
                            updateNoticeFromMessage(message);
                            break;
                        case "103004":
                        case "103005":
                            LiveMessageBody liveMessageBody = new LiveMessageBody(message.getMsg());
                            try {
                                JSONObject jsonObject = new JSONObject(liveMessageBody.getData());
                                if (jsonObject.optBoolean("isCanChat") || jsonObject.optBoolean("isAllCanChat")) {
                                    enableChatView();
                                } else {
                                    unEnableChatView();
                                }
                            } catch (JSONException e) {
                            }
                            mIMessageListView.insertMessage(message);
                            break;
                        case "101002":
                            checkLivePlayStatus(message);
                    }
                }

                @Override
                protected void invokeReceiver(MessageEntity message) {
                    LiveMessageBody liveMessageBody = new LiveMessageBody(message.getMsg());
                    if (liveMessageBody != null && messageIsSignal(liveMessageBody.getType())) {
                        message.setCmd(liveMessageBody.getType());
                        invokeReceiverSignal(message);
                        return;
                    }

                    if (mClientId == AppUtil.parseInt(message.getFromId()) && mClientName.equals(message.getFromName())) {
                        return;
                    }
                    mIMessageListView.insertMessage(message);
                }

                @Override
                protected void invokeOfflineMsgReceiver(List<MessageEntity> messageEntityList) {
                    Iterator<MessageEntity> iterator = messageEntityList.iterator();
                    while (iterator.hasNext()) {
                        if (messageEntityInFilter(iterator.next())) {
                            iterator.remove();
                        }
                    }
                    coverMessageEntityStatus(messageEntityList);
                    mIMessageListView.insertMessageList(messageEntityList);
                }
            };
            registerReceiver(mReceiver, new IntentFilter(IMBroadcastReceiver.ACTION_NAME));
        }

        @Override
        public void removeReceiver() {
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
            }
        }
    }

    private class LiveImClient {

        String TAG = "LiveImClient";

        private int mIMConnectStatus;
        private List<IMMessageReceiver> mMessageReceiverList;
        private List<IMConnectStatusListener> mIMConnectStatusListenerList;
        private BroadcastReceiver mIMServiceStatusBroadcastReceiver;
        private IImServerAidlInterface mImBinder;
        private ServiceConnection mServiceConnection;
        private ConnectIMServiceRunnable mConnectIMServiceRunnable;

        public LiveImClient() {
            this.mIMConnectStatus = IMConnectStatus.NO_READY;
            mMessageReceiverList = new LinkedList<>();
            mIMConnectStatusListenerList = new LinkedList<>();
            registIMServiceStatusBroadcastReceiver();
        }

        public IImServerAidlInterface getImBinder() {
            return mImBinder;
        }

        public void destory() {
            if (mImBinder != null) {
                try {
                    mImBinder.closeIMServer();
                } catch (RemoteException e) {
                    Log.e(TAG, "closeIMServer error");
                }
            }
            if (mServiceConnection != null) {
                mContext.unbindService(mServiceConnection);
                mServiceConnection = null;
            }

            unRegistIMServiceStatusBroadcastReceiver();
            mContext.stopService(getIMServiceIntent());
            mImBinder = null;

            mIMConnectStatusListenerList.clear();
            mMessageReceiverList.clear();
        }

        private void unRegistIMServiceStatusBroadcastReceiver() {
            if (mIMServiceStatusBroadcastReceiver != null) {
                mContext.unregisterReceiver(mIMServiceStatusBroadcastReceiver);
            }
        }

        private void registIMServiceStatusBroadcastReceiver() {
            mIMServiceStatusBroadcastReceiver = new IMServiceStartedBroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mConnectIMServiceRunnable == null) {
                        return;
                    }
                    new Handler(Looper.getMainLooper()).post(mConnectIMServiceRunnable);
                }
            };

            mContext.registerReceiver(mIMServiceStatusBroadcastReceiver, new IntentFilter(IMServiceStartedBroadcastReceiver.ACTION_NAME));
        }

        public void start(
                int clientId, String clientName, ArrayList<String> ignoreNosList, ArrayList<String> hostList) {
            this.mConnectIMServiceRunnable = new ConnectIMServiceRunnable(clientId, clientName, ignoreNosList, hostList);
            startImService();
        }

        private void startImService() {
            Intent intent = getIMServiceIntent();
            intent.putExtra(ImService.ACTION, ImService.ACTION_INIT);
            mContext.startService(intent);
        }

        private Intent getIMServiceIntent() {
            Intent intent = new Intent("com.edusoho.kuozhi.liveimserver.IImServerAidlInterface");
            intent.setPackage(mContext.getPackageName());
            return intent;
        }

        private void connectService(
                final int clientId, final String clientName, final String[] ignoreNosList, final String[] hostList) {
            if (mServiceConnection != null) {
                mContext.unbindService(mServiceConnection);
            }
            mServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mImBinder = IImServerAidlInterface.Stub.asInterface(service);
                    try {
                        Log.d(TAG, "mImBinder:" + mImBinder);
                        attachMessageListFragment();
                        mImBinder.start(clientId, clientName, ignoreNosList, hostList);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Log.d(TAG, name.toString());
                }
            };
            boolean result = mContext.bindService(
                    new Intent("com.edusoho.kuozhi.imserver.IImServerAidlInterface")
                            .setPackage(mContext.getPackageName()),
                    mServiceConnection,
                    Context.BIND_AUTO_CREATE
            );
            Log.d(TAG, "bind:" + result);
        }

        private class ConnectIMServiceRunnable implements Runnable {

            private int mClientId;
            private String mClientName;
            private String[] mHostList;
            private String[] mIgnoreNosList;

            public ConnectIMServiceRunnable(
                    int clientId, String clientName, ArrayList<String> ignoreNosList, ArrayList<String> hostList) {
                this.mClientId = clientId;
                this.mClientName = clientName;
                this.mHostList = new String[hostList.size()];
                this.mIgnoreNosList = new String[ignoreNosList.size()];
                ignoreNosList.toArray(mIgnoreNosList);
                hostList.toArray(mHostList);
            }

            @Override
            public void run() {
                connectService(mClientId, mClientName, mIgnoreNosList, mHostList);
            }
        }
    }
}
