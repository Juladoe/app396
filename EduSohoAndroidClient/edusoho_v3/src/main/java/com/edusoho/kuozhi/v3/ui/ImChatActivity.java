package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.imserver.IImServerAidlInterface;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.ChatAdapter;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.UtilFactory;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.provider.UserProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseChatActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by su on 2016/3/18.
 */
public class ImChatActivity extends BaseChatActivity{

    private IMMessageReceiver mIMMessageReceiver;

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
    private String mConversationNo;

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

        User user = getAppSettingProvider().getCurrentUser();
        if (TextUtils.isEmpty(mMyType)) {
            String[] roles = new String[user.roles == null ? 0 :user.roles.length];
            Log.d("roles:", Arrays.toString(roles));
            for (int i = 0; i < roles.length; i++) {
                roles[i] = user.roles[i].toString();
            }
            if (CommonUtil.inArray(UserRole.ROLE_TEACHER.name(), roles)) {
                mMyType = PushUtil.ChatUserType.TEACHER;
            } else {
                mMyType = PushUtil.ChatUserType.FRIEND;
            }
        }

        mFromId = intent.getIntExtra(FROM_ID, mFromId);
        mType = intent.getStringExtra(Const.NEWS_TYPE);
        mToId = user.id;
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
        mHandler.postDelayed(mNewFragment2UpdateItemBadgeRunnable, 500);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (mIMMessageReceiver == null) {
            createChatConvNo();
            mIMMessageReceiver = getIMMessageListener();
        }

        IMClient.getClient().addMessageReceiver(mIMMessageReceiver);
    }

    protected IMMessageReceiver getIMMessageListener() {
        return new IMMessageReceiver() {
            @Override
            public boolean onReceiver(String msg) {
                handleMessage(this, msg);
                return true;
            }

            @Override
            public ReceiverInfo getType() {
                return new ReceiverInfo("chat", mFromId);
            }
        };
    }

    protected void handleMessage(IMMessageReceiver receiver, String msg) {
        V2CustomContent v2CustomContent = getUtilFactory().getJsonParser().fromJson(msg, V2CustomContent.class);
        if (v2CustomContent.getFrom().getId() != receiver.getType().msgId) {
            return;
        }
        Chat chat = new Chat(v2CustomContent);
        if (mFromUserInfo != null) {
            chat.headImgUrl = mFromUserInfo.mediumAvatar;
        }
        mAdapter.addItem(chat);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIMMessageReceiver != null) {
            IMClient.getClient().removeReceiver(mIMMessageReceiver);
        }
    }

    @Override
    public void sendMsg(String content) {
        super.sendMsg(content);

        mSendTime = (int) (System.currentTimeMillis() / 1000);
        Chat chat = new Chat(mToId, mFromId, app.loginUser.nickname,app.loginUser.mediumAvatar,
                etSend.getText().toString(), PushUtil.ChatMsgType.TEXT, mSendTime);

        addSendMsgToListView(PushUtil.MsgDeliveryType.SUCCESS, chat);
        etSend.setText("");
        etSend.requestFocus();

        V2CustomContent v2CustomContent = getV2CustomContent(PushUtil.ChatMsgType.TEXT, chat.content);
        try {
            String message = getUtilFactory().getJsonParser().jsonToString(v2CustomContent);
            IMClient.getClient().getChatRoom(mConversationNo).send(message);
        } catch (Exception e) {
        }

        notifyNewListView2Update(getNotifyV2CustomContent(PushUtil.ChatMsgType.TEXT, chat.content));
    }

    private Runnable mNewFragment2UpdateItemBadgeRunnable = new Runnable() {
        @Override
        public void run() {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.FROM_ID, mFromId);
            bundle.putString(Const.NEWS_TYPE, mType);
            app.sendMsgToTarget(NewsFragment.UPDATE_UNREAD_MSG, bundle, NewsFragment.class);
        }
    };

    public void notifyNewListView2Update(V2CustomContent v2CustomContent) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.GET_PUSH_DATA, v2CustomContent);
        bundle.putInt(Const.ADD_CHAT_MSG_DESTINATION, NewsFragment.HANDLE_SEND_CHAT_MSG);
        app.sendMsgToTarget(Const.ADD_MSG, bundle, NewsFragment.class);
    }

    private V2CustomContent getNotifyV2CustomContent(String type, String content) {
        V2CustomContent v2CustomContent = new V2CustomContent();
        V2CustomContent.FromEntity fromEntity = new V2CustomContent.FromEntity();
        fromEntity.setNickname(mFromUserInfo.nickname);
        fromEntity.setId(mFromId);
        fromEntity.setType(mType);
        fromEntity.setImage(mFromUserInfo.mediumAvatar);
        v2CustomContent.setFrom(fromEntity);
        V2CustomContent.ToEntity toEntity = new V2CustomContent.ToEntity();
        toEntity.setId(mToId);
        toEntity.setType(PushUtil.ChatUserType.USER);
        v2CustomContent.setTo(toEntity);
        V2CustomContent.BodyEntity bodyEntity = new V2CustomContent.BodyEntity();
        bodyEntity.setType(type);
        bodyEntity.setContent(content);
        v2CustomContent.setBody(bodyEntity);
        v2CustomContent.setV(2);
        v2CustomContent.setCreatedTime(mSendTime);
        return v2CustomContent;
    }

    private V2CustomContent getV2CustomContent(String type, String content) {
        V2CustomContent v2CustomContent = new V2CustomContent();
        V2CustomContent.FromEntity fromEntity = new V2CustomContent.FromEntity();
        fromEntity.setNickname(app.loginUser.nickname);
        fromEntity.setId(mToId);
        fromEntity.setType(mType);
        fromEntity.setImage(app.loginUser.mediumAvatar);
        v2CustomContent.setFrom(fromEntity);
        V2CustomContent.ToEntity toEntity = new V2CustomContent.ToEntity();
        toEntity.setId(mFromId);
        toEntity.setType(PushUtil.ChatUserType.USER);
        v2CustomContent.setTo(toEntity);
        V2CustomContent.BodyEntity bodyEntity = new V2CustomContent.BodyEntity();
        bodyEntity.setType(type);
        bodyEntity.setContent(content);
        v2CustomContent.setBody(bodyEntity);
        v2CustomContent.setV(2);
        v2CustomContent.setCreatedTime(mSendTime);
        return v2CustomContent;
    }

    protected void createChatConvNo() {
        new UserProvider(mContext).createConvNo(mFromId)
        .success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap linkedHashMap) {
                String conversationNo = null;
                if (linkedHashMap == null
                        || (conversationNo = linkedHashMap.get("conversationNo").toString()) == null) {
                    return;
                }

                mConversationNo = conversationNo;
                Log.d(TAG, conversationNo);
            }
        });
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }

    protected UtilFactory getUtilFactory() {
        return FactoryManager.getInstance().create(UtilFactory.class);
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
