package com.edusoho.kuozhi.v3.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.imserver.IImServerAidlInterface;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.ChatAdapter;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseChatActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by su on 2016/3/18.
 */
public class ImChatActivity extends BaseChatActivity{

    private ServiceConnection mServiceConnection;
    private IImServerAidlInterface mImBinder;

    public static final String TAG = "ChatActivity";
    public static final String FROM_ID = "from_id";
    public static final String MSG_DELIVERY = "msg_delivery";
    public static final String HEAD_IMAGE_URL = "head_image_url";
    public static int CurrentFromId = 0;

    private ChatAdapter<Chat> mAdapter;
    private ChatDataSource mChatDataSource;
    private int mSendTime;
    private User mFromUserInfo;
    private int mFromId;
    private int mToId;

    /**
     * 对方的BusinessType,这里是Role
     */
    private String mType;

    /**
     * 自己的BusinessType,这里是Role
     */
    private String mMyType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startIntent = getIntent();
        startIntent.putExtra(ChatActivity.FROM_ID, 0);
        startIntent.putExtra(Const.ACTIONBAR_TITLE, "test");
        startIntent.putExtra(Const.NEWS_TYPE, "chat");
        startIntent.putExtra(ChatActivity.HEAD_IMAGE_URL, "");
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "聊天记录读取错误");
            return;
        }
        if (TextUtils.isEmpty(mMyType)) {
            String[] roles = new String[app.loginUser.roles.length];
            for (int i = 0; i < app.loginUser.roles.length; i++) {
                roles[i] = app.loginUser.roles[i].toString();
            }
            if (CommonUtil.inArray(UserRole.ROLE_TEACHER.name(), roles)) {
                mMyType = PushUtil.ChatUserType.TEACHER;
            } else {
                mMyType = PushUtil.ChatUserType.FRIEND;
            }
        }

        mFromId = intent.getIntExtra(FROM_ID, mFromId);
        mType = intent.getStringExtra(Const.NEWS_TYPE);
        mToId = app.loginUser.id;
        mFromUserInfo = new User();
        mFromUserInfo.id = mFromId;
        mFromUserInfo.mediumAvatar = intent.getStringExtra(HEAD_IMAGE_URL);
        mFromUserInfo.nickname = intent.getStringExtra(Const.ACTIONBAR_TITLE);
        NotificationUtil.cancelById(mFromId);
        setBackMode(BACK, intent.getStringExtra(Const.ACTIONBAR_TITLE));
        CurrentFromId = mFromId;
        if (mChatDataSource == null) {
            mChatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        }
        initCacheFolder();
        getFriendUserInfo();

        mAdapter = new ChatAdapter<>(mContext, getChatList(0), mFromUserInfo);
        //mAdapter.setSendImageClickListener(this);
        lvMessage.setAdapter(mAdapter);
        mStart = mAdapter.getCount();
        lvMessage.postDelayed(mListViewSelectRunnable, 500);

        mAudioDownloadReceiver.setAdapter(mAdapter);

        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mAdapter.addItems(getChatList(mStart));
                mStart = mAdapter.getCount();
                mPtrFrame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canDoRefresh = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                int count = getChatList(mStart).size();
                return count > 0 && canDoRefresh;
            }
        });

        //mHandler.postDelayed(mNewFragment2UpdateItemBadgeRunnable, 500);
    }

    /**
     * 获取对方信息
     */
    private void getFriendUserInfo() {
        if (mFromUserInfo == null) {
            RequestUrl requestUrl = app.bindUrl(Const.USERINFO, false);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("userId", mFromId + "");
            ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mFromUserInfo = parseJsonValue(response, new TypeToken<User>() {
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "无法获取对方信息");
                }
            });
        }
    }

    private ArrayList<Chat> getChatList(int start) {
        String selectSql = String.format("(FROMID = %d AND TOID=%d) OR (TOID=%d AND FROMID=%d)", mFromId, mToId, mFromId, mToId);
        ArrayList<Chat> mList = mChatDataSource.getChats(start, Const.NEWS_LIMIT, selectSql);
        Collections.reverse(mList);
        return mList;
    }

    protected void initCacheFolder() {
        File imageFolder = new File(EdusohoApp.getChatCacheFile() + Const.UPLOAD_IMAGE_CACHE_FILE);
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        File imageThumbFolder = new File(EdusohoApp.getChatCacheFile() + Const.UPLOAD_IMAGE_CACHE_THUMB_FILE);
        if (!imageThumbFolder.exists()) {
            imageThumbFolder.mkdirs();
        }
        File audioFolder = new File(EdusohoApp.getChatCacheFile() + Const.UPLOAD_AUDIO_CACHE_FILE);
        if (!audioFolder.exists()) {
            audioFolder.mkdirs();
        }
    }

    public void onReceiver(String msg) {
        Log.d("onReceiver", msg);
        final Chat chat = new Chat();
        chat.fromId = mFromId;
        chat.toId = mToId;
        chat.nickname = "suju3";
        chat.content = msg;
        chat.headImgUrl = "";
        chat.type = "text";
        chat.createdTime = 0;
        chat.direct = Chat.Direct.getDirect(false);

        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mAdapter.addItem(chat);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mServiceConnection != null) {
            return;
        }
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mImBinder = IImServerAidlInterface.Stub.asInterface(service);
                try {
                    mImBinder.joinConversation("", "","b6565ecacef7fd0f3ea1fab66e7b3a49");
                } catch (Exception e) {
                }
                Log.d(getClass().getSimpleName(), "----" + service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(getClass().getSimpleName(), name.toString());
            }
        };
        boolean result = bindService(
                new Intent("com.edusoho.kuozhi.imserver.IImServerAidlInterface"),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
        );
        Log.d(getClass().getSimpleName(), "" + result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    public void sendMsg(String content) {
        super.sendMsg(content);
        try {
            mImBinder.send(content);
        } catch (Exception e) {
        }

        mSendTime = (int) (System.currentTimeMillis() / 1000);
        Chat chat = new Chat(mFromId, mToId, "suju3", "",
                etSend.getText().toString(), PushUtil.ChatMsgType.TEXT, mSendTime);
        addSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, chat);
        etSend.setText("");
        etSend.requestFocus();
    }

    private void addSendMsgToListView(int delivery, Chat chat) {
        chat.direct = Chat.Direct.SEND;
        chat.delivery = delivery;
        long chatId = mChatDataSource.create(chat);
        chat.chatId = (int) chatId;
        if (app.loginUser != null) {
            chat.headImgUrl = app.loginUser.mediumAvatar;
        }
        mAdapter.addItem(chat);
        mStart = mStart + 1;
    }

    /**
     * 上传资源:音频、图片
     * 子类重写
     *
     * @param file upload file
     */
    public void uploadMedia(final File file, final String type, String strType) {

    }
}
