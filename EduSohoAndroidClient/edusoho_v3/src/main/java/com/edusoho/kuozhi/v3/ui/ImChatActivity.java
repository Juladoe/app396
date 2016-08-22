package com.edusoho.kuozhi.v3.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.SendEntity;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.IMUploadEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.entity.message.Source;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.imserver.managar.IMConvManager;
import com.edusoho.kuozhi.imserver.util.MessageEntityBuildr;
import com.edusoho.kuozhi.imserver.util.SendEntityBuildr;
import com.edusoho.kuozhi.v3.adapter.ChatAdapter;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.NotificationProvider;
import com.edusoho.kuozhi.v3.factory.UtilFactory;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.UpYunUploadResult;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.model.provider.UserProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseChatActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import cn.trinea.android.common.util.ToastUtils;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by su on 2016/3/18.
 */
public class ImChatActivity extends BaseChatActivity implements ChatAdapter.ImageErrorClick {


    public static final String TAG = "ChatActivity";
    public static final String FROM_ID = "from_id";
    public static final String CONV_NO = "conv_no";
    public static final String MSG_DELIVERY = "msg_delivery";
    public static final String HEAD_IMAGE_URL = "head_image_url";

    private ChatAdapter<Chat> mAdapter;
    private long mSendTime;
    private Role mTargetRole;
    private int mToId;
    private IMMessageReceiver mIMMessageReceiver;

    protected int mFromId;
    protected String mConversationNo;

    /**
     * 对方的BusinessType,这里是Role
     */
    private String mType;

    protected String getTargetType() {
        return Destination.USER;
    }

    private void initConvNoInfo() {
        IMConvManager imConvManager = IMClient.getClient().getConvManager();
        ConvEntity convEntity = imConvManager.getConvByTypeAndId(getTargetType(), mFromId);
        if (convEntity != null) {
            if (convNoIsEmpty(mConversationNo)) {
                mConversationNo = convEntity.getConvNo();
            }
            setBackMode(BACK, convEntity.getTargetName());
        }
        mTargetRole = IMClient.getClient().getRoleManager().getRole(getTargetType(), mFromId);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "聊天记录读取错误");
            return;
        }
        User user = getAppSettingProvider().getCurrentUser();
        if (user == null) {
            CommonUtil.longToast(mContext, "尚未登陆登录!");
            finish();
            return;
        }
        mToId = user.id;
        mFromId = intent.getIntExtra(FROM_ID, mFromId);
        mType = intent.getStringExtra(Const.NEWS_TYPE);
        mConversationNo = intent.getStringExtra(CONV_NO);

        initConvNoInfo();
        initCacheFolder();

        if (convNoIsEmpty(mConversationNo)) {
            createChatConvNo();
            return;
        }

        initAdapter();
    }

    protected boolean convNoIsEmpty(String convNo) {
        return TextUtils.isEmpty(convNo) || "0".equals(convNo);
    }

    protected void initAdapter() {
        mAdapter = new ChatAdapter(mContext, getChatList(0));
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
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        mHandler.postDelayed(mNewFragment2UpdateItemBadgeRunnable, 500);
        mAdapter.setSendImageClickListener(this);
        registerIMMessageReceiver();
    }

    @Override
    public void uploadMediaAgain(File file, Chat chat, String type, String strType) {
        MessageBody messageBody = new MessageBody(1, chat.type, chat.content);
        messageBody.setConvNo(mConversationNo);
        messageBody.setMessageId(chat.mid);
        messageBody.setCreatedTime(chat.createdTime);
        messageBody.setDestination(new Destination(chat.fromId, Destination.USER));
        messageBody.setSource(new Source(chat.toId, Destination.USER));

        updateSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, chat);
        getUpYunUploadInfo(file, mFromId, new UpYunUploadCallback(messageBody));
    }

    @Override
    public void sendMsgAgain(Chat chat) {
        MessageBody messageBody = new MessageBody(1, chat.type, chat.content);
        messageBody.setConvNo(mConversationNo);
        messageBody.setMessageId(chat.mid);
        messageBody.setCreatedTime(chat.createdTime);
        messageBody.setDestination(new Destination(chat.fromId, Destination.USER));
        messageBody.setSource(new Source(chat.toId, Destination.USER));

        updateSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, chat);
        sendMessageToServer(messageBody);
    }

    /**
     * 逆序显示
     *
     * @param start
     * @return
     */
    protected ArrayList<Chat> getChatList(int start) {
        List<MessageEntity> messageEntityList = IMClient.getClient().getChatRoom(mConversationNo).getMessageList(start);
        ArrayList<Chat> chats = new ArrayList<>();

        User currentUser = getAppSettingProvider().getCurrentUser();
        MessageEntity messageEntity;
        for (int i = messageEntityList.size() - 1; i >= 0; i--) {
            messageEntity = messageEntityList.get(i);
            MessageBody messageBody = new MessageBody(messageEntity);
            Chat chat = new Chat(messageBody);
            chat.id = messageEntity.getId();
            Role role = IMClient.getClient().getRoleManager().getRole(messageBody.getSource().getType(), chat.fromId);
            chat.setDirect(chat.fromId == currentUser.id ? Chat.Direct.SEND : Chat.Direct.RECEIVE);
            chat.headImgUrl = role.getAvatar();
            if (messageEntity.getStatus() != PushUtil.MsgDeliveryType.NONE) {
                chat.delivery = messageEntity.getStatus();
            }
            chats.add(chat);
        }
        return chats;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!convNoIsEmpty(mConversationNo)) {
            registerIMMessageReceiver();
            IMClient.getClient().getConvManager().clearReadCount(mConversationNo);
            getNotificationProvider().cancelNotification(mConversationNo.hashCode());
        }
    }

    private void registerIMMessageReceiver() {
        if (mIMMessageReceiver != null) {
            return;
        }
        mIMMessageReceiver = getIMMessageListener();
        IMClient.getClient().addMessageReceiver(mIMMessageReceiver);
    }

    protected IMMessageReceiver getIMMessageListener() {
        return new IMMessageReceiver() {
            @Override
            public boolean onReceiver(MessageEntity msg) {
                if (!mConversationNo.equals(msg.getConvNo())) {
                    return true;
                }
                handleMessage(msg);
                IMClient.getClient().getConvManager().clearReadCount(mConversationNo);
                return true;
            }

            @Override
            public boolean onOfflineMsgReceiver(List<MessageEntity> messageEntities) {
                handleOfflineMessage(messageEntities);
                return false;
            }

            @Override
            public void onSuccess(String extr) {
                MessageBody messageBody = new MessageBody(extr);
                if (messageBody == null) {
                    return;
                }
                messageBody.setConvNo(mConversationNo);
                updateMessageSendStatus(messageBody);
            }

            @Override
            public ReceiverInfo getType() {
                return new ReceiverInfo(getTargetType(), mConversationNo);
            }
        };
    }

    protected void updateMessageSendStatus(MessageBody messageBody) {
        Chat chat = new Chat(messageBody);
        updateSendMsgToListView(PushUtil.MsgDeliveryType.SUCCESS, chat);

        ContentValues cv = new ContentValues();
        cv.put("status", MessageEntity.StatusType.SUCCESS);
        IMClient.getClient().getMessageManager().updateMessageFieldByUid(messageBody.getMessageId(), cv);
    }

    protected void handleOfflineMessage(List<MessageEntity> messageEntityList) {
        ArrayList<Chat> chatList = new ArrayList<>();
        for (MessageEntity messageEntity : messageEntityList) {
            MessageBody messageBody = getUtilFactory().getJsonParser().fromJson(messageEntity.getMsg(), MessageBody.class);
            Chat chat = new Chat(messageBody);
            if (mTargetRole != null) {
                chat.headImgUrl = mTargetRole.getAvatar();
            }
            chatList.add(chat);
        }

        mAdapter.addItems(chatList);
    }

    protected void handleMessage(MessageEntity messageEntity) {
        MessageBody messageBody = new MessageBody(messageEntity);
        Chat chat = new Chat(messageBody);
        Role role = IMClient.getClient().getRoleManager().getRole(Destination.USER, chat.fromId);
        chat.headImgUrl = role.getAvatar();
        mAdapter.addItem(chat);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIMMessageReceiver != null) {
            IMClient.getClient().removeReceiver(mIMMessageReceiver);
            mIMMessageReceiver = null;
        }
    }

    @Override
    public void sendMsg(String content) {
        super.sendMsg(content);

        MessageBody messageBody = saveMessageToLoacl(content, PushUtil.ChatMsgType.TEXT);
        etSend.setText("");
        etSend.requestFocus();
        sendMessageToServer(messageBody);
    }

    private MessageEntity createMessageEntityByBody(MessageBody messageBody) {
        return new MessageEntityBuildr()
                .addUID(messageBody.getMessageId())
                .addConvNo(messageBody.getConvNo())
                .addToId(String.valueOf(messageBody.getDestination().getId()))
                .addToName(messageBody.getDestination().getNickname())
                .addFromId(String.valueOf(messageBody.getSource().getId()))
                .addFromName(messageBody.getSource().getNickname())
                .addCmd("message")
                .addMsg(messageBody.toJson())
                .addTime((int) (messageBody.getCreatedTime() / 1000))
                .builder();
    }

    private void updateConv(MessageBody messageBody) {
        ContentValues cv = new ContentValues();
        cv.put("laterMsg", messageBody.toJson());
        cv.put("updatedTime", System.currentTimeMillis());
        IMClient.getClient().getConvManager().updateConvField(mConversationNo, cv);
    }

    protected MessageBody saveMessageToLoacl(String content, String type) {
        MessageBody messageBody = createSendMessageBody(content, type);
        mSendTime = messageBody.getCreatedTime();

        MessageEntity messageEntity = createMessageEntityByBody(messageBody);
        IMClient.getClient().getMessageManager().createMessage(messageEntity);
        updateConv(messageBody);

        User currentUser = getAppSettingProvider().getCurrentUser();
        Chat chat = new Chat.Builder()
                .addToId(mToId)
                .addFromId(mFromId)
                .addAvatar(currentUser.mediumAvatar)
                .addContent(messageBody.getBody())
                .addNickname(currentUser.nickname)
                .addType(messageBody.getType())
                .addMessageId(messageBody.getMessageId())
                .addCreatedTime(mSendTime).builder();
        addSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, chat);

        return messageBody;
    }

    protected MessageBody createSendMessageBody(String content, String type) {
        User currentUser = getAppSettingProvider().getCurrentUser();
        MessageBody messageBody = new MessageBody(1, type, content);
        messageBody.setCreatedTime(System.currentTimeMillis());
        messageBody.setDestination(new Destination(mFromId, getTargetType()));
        messageBody.getDestination().setNickname(currentUser.nickname);
        messageBody.setSource(new Source(mToId, Destination.USER));
        messageBody.getSource().setNickname(mTargetRole.getNickname());
        messageBody.setConvNo(mConversationNo);
        messageBody.setMessageId(UUID.randomUUID().toString());
        return messageBody;
    }

    protected void sendMessageToServer(MessageBody messageBody) {
        try {
            String toId = "";
            switch (messageBody.getDestination().getType()) {
                case Destination.CLASSROOM:
                case Destination.COURSE:
                    toId = "all";
                    break;
                case Destination.USER:
                    toId = String.valueOf(messageBody.getDestination().getId());
            }
            SendEntity sendEntity = SendEntityBuildr.getBuilder()
                    .addToId(toId)
                    .addMsg(messageBody.toJson())
                    .builder();
            IMClient.getClient().getChatRoom(mConversationNo).send(sendEntity);
        } catch (Exception e) {
        }
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


    protected void createChatConvNo() {
        final LoadDialog loadDialog = LoadDialog.create(this);
        loadDialog.show();

        User currentUser = getAppSettingProvider().getCurrentUser();
        new UserProvider(mContext).createConvNo(new int[]{currentUser.id, mFromId})
                .success(new NormalCallback<LinkedHashMap>() {
                    @Override
                    public void success(LinkedHashMap linkedHashMap) {
                        if (linkedHashMap == null || !linkedHashMap.containsKey("no")) {
                            ToastUtils.show(getBaseContext(), "创建聊天失败!");
                            return;
                        }
                        String convNo = linkedHashMap.get("no").toString();
                        if (convNo == null || convNo.endsWith(mConversationNo)) {
                            return;
                        }
                        new IMProvider(mContext).createConvInfoByUser(mConversationNo, mFromId)
                                .success(new NormalCallback<ConvEntity>() {
                                    @Override
                                    public void success(ConvEntity convEntity) {
                                        loadDialog.dismiss();
                                        setTitle(convEntity.getTargetName());
                                        initAdapter();
                                    }
                                });
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

        User currentUser = getAppSettingProvider().getCurrentUser();
        chat.headImgUrl = currentUser.mediumAvatar;
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
        if (file == null || !file.exists()) {
            CommonUtil.shortToast(mContext, String.format("%s不存在", strType));
            return;
        }
        try {
            mSendTime = System.currentTimeMillis();
            String content = file.getAbsolutePath();
            if (PushUtil.ChatMsgType.AUDIO.equals(type)) {
                content = wrapAudioMessageContent(file.getAbsolutePath(), getAudioDuration(file.getAbsolutePath()));
            }
            MessageBody messageBody = saveMessageToLoacl(content, type);
            IMClient.getClient().getMessageManager().saveUploadEntity(
                    messageBody.getMessageId(), messageBody.getType(), file.getPath()
            );
            getUpYunUploadInfo(file, mFromId, new UpYunUploadCallback(messageBody));
            viewMediaLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private int getAudioDuration(String audioFile) {
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, Uri.parse(audioFile));
        int duration = mediaPlayer.getDuration();
        mediaPlayer.release();
        return duration;
    }

    private String wrapAudioMessageContent(String audioFilePath, int audioTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("file", audioFilePath);
            jsonObject.put("duration", audioTime);
        } catch (JSONException e) {
        }

        return jsonObject.toString();
    }

    /**
     * 更新一行聊天记录，并更新对应的Item
     *
     * @param delivery 是否送达
     * @param chat     一行聊天记录
     */
    private void updateSendMsgToListView(int delivery, Chat chat) {
        chat.delivery = delivery;
        mAdapter.updateItemByMsgId(chat);
    }

    private void uploadUnYunMedia(String uploadUrl, final File file, HashMap<String, String> headers, final MessageBody messageBody) {
        RequestUrl putUrl = new RequestUrl(uploadUrl);
        putUrl.setHeads(headers);
        putUrl.setMuiltParams(new Object[]{"file", file});
        ajaxPostMultiUrl(putUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "success");
                sendMessageToServer(messageBody);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtil.longToast(getBaseContext(), getString(R.string.request_fail_text));
                Log.d(TAG, "upload media res to upyun failed");
            }
        }, Request.Method.PUT);
    }

    private class UpYunUploadCallback implements NormalCallback<UpYunUploadResult> {
        private MessageBody messageBody;

        public UpYunUploadCallback(MessageBody messageBody) {
            this.messageBody = messageBody;
        }

        @Override
        public void success(UpYunUploadResult result) {
            final Chat chat = new Chat(messageBody);
            if (result != null) {
                IMUploadEntity uploadEntity = IMClient.getClient().getMessageManager().getUploadEntity(messageBody.getMessageId());
                File file = new File(uploadEntity.getSource());
                String body = result.getUrl;
                if (PushUtil.ChatMsgType.AUDIO.equals(messageBody.getType())) {
                    body = wrapAudioMessageContent(result.getUrl, getAudioDuration(file.getAbsolutePath()));
                }
                messageBody.setBody(body);

                uploadUnYunMedia(result.putUrl, file, result.getHeaders(), messageBody);
                saveUploadResult(result.putUrl, result.getUrl, mFromId);
            } else {
                updateSendMsgToListView(PushUtil.MsgDeliveryType.FAILED, chat);
            }
        }
    }

    protected NotificationProvider getNotificationProvider() {
        return FactoryManager.getInstance().create(NotificationProvider.class);
    }
}
