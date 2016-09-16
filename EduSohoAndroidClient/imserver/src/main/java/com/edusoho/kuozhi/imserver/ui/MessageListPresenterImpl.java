package com.edusoho.kuozhi.imserver.ui;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.IMUploadEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.entity.message.Source;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.imserver.ui.entity.AudioBody;
import com.edusoho.kuozhi.imserver.ui.entity.PushUtil;
import com.edusoho.kuozhi.imserver.ui.helper.MessageHelper;
import com.edusoho.kuozhi.imserver.ui.helper.MessageResourceHelper;
import com.edusoho.kuozhi.imserver.ui.listener.IMessageDataProvider;
import com.edusoho.kuozhi.imserver.ui.util.AudioUtil;
import com.edusoho.kuozhi.imserver.ui.util.ResourceDownloadTask;
import com.edusoho.kuozhi.imserver.ui.util.UpYunUploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by suju on 16/9/16.
 */
public abstract class MessageListPresenterImpl implements IMessageListPresenter {

    public static final String TAG = "MessageListPresenter";

    private int mStart = 0;
    private String mConversationNo;
    private int mTargetId;
    private String mTargetType;
    private Role mTargetRole;

    private MessageResourceHelper mMessageResourceHelper;
    private IMMessageReceiver mIMMessageReceiver;
    private IMessageListView mIMessageListView;
    private IMessageDataProvider mIMessageDataProvider;

    public MessageListPresenterImpl(
            Bundle params,
            MessageResourceHelper messageResourceHelper,
            IMessageDataProvider mIMessageDataProvider,
            IMessageListView messageListView) {
        this.mMessageResourceHelper = messageResourceHelper;
        this.mIMessageDataProvider = mIMessageDataProvider;
        this.mIMessageListView = messageListView;
        initParams(params);
        checkConvNo();
        mIMessageListView.setPresenter(this);
    }

    @Override
    public void processResourceStatusChange(int resId, String resUri) {
        MessageEntity messageEntity = mIMessageDataProvider.getMessageManager().getMessage(resId);
        if (messageEntity == null) {
            return;
        }

        if (resUri == null || TextUtils.isEmpty(resUri)) {
            updateMessageReceiveStatus(messageEntity, MessageEntity.StatusType.FAILED);
            return;
        }

        MessageBody messageBody = new MessageBody(messageEntity);
        String body = resUri;
        if (PushUtil.ChatMsgType.AUDIO.equals(messageBody.getType())) {
            AudioBody audioBody = AudioUtil.getAudioBody(messageBody.getBody());
            audioBody.setFile(resUri);
            body = audioBody.toString();
        }
        messageBody.setBody(body);
        sendMessageToServer(messageBody);
    }

    @Override
    public void refresh() {
        mStart = 0;
        mIMessageListView.setMessageList(mIMessageDataProvider.getMessageList(mConversationNo, mStart));
    }

    @Override
    public void sendTextMessage(String content) {
        MessageBody messageBody = createSendMessageBody(content, PushUtil.ChatMsgType.TEXT);
        MessageEntity messageEntity = mIMessageDataProvider.createMessageEntity(messageBody);
        messageEntity.setStatus(MessageEntity.StatusType.UPLOADING);
        insertMessageToList(messageEntity);
        sendMessageToServer(messageBody);
    }

    @Override
    public void sendAudioMessage(File audioFile, int audioLength) {
        String content = wrapAudioMessageContent(audioFile.getAbsolutePath(), audioLength);
        MessageBody messageBody = createSendMessageBody(content, PushUtil.ChatMsgType.AUDIO);
        uploadMedia(audioFile, messageBody);
    }

    @Override
    public void sendImageMessage(File imageFile) {
        MessageBody messageBody = createSendMessageBody(imageFile.getAbsolutePath(), PushUtil.ChatMsgType.IMAGE);
        uploadMedia(imageFile, messageBody);
    }

    @Override
    public void removeReceiver() {
        IMClient.getClient().removeReceiver(mIMMessageReceiver);
    }

    @Override
    public void onSendMessageAgain(MessageEntity messageEntity) {
        MessageBody messageBody = new MessageBody(messageEntity);
        switch (messageBody.getType()) {
            case PushUtil.ChatMsgType.TEXT:
            case PushUtil.ChatMsgType.MULTI:
                sendMessageToServer(messageBody);
                break;
            case PushUtil.ChatMsgType.AUDIO:
                messageEntity.setUid(messageBody.getMessageId());
                if (IMClient.getClient().getClientId() == messageBody.getSource().getId()) {
                    sendAudioMessageAgain(messageEntity);
                    return;
                }
                //receive
                receiveAudioMessageAgain(messageBody);
                break;
            case PushUtil.ChatMsgType.IMAGE:
                messageEntity.setUid(messageBody.getMessageId());
                if (IMClient.getClient().getClientId() == messageBody.getSource().getId()) {
                    sendImageMediaMessageAgain(messageEntity);
                    return;
                }
                receiveImageMessageAgain(messageBody);
        }
    }

    protected void handleOfflineMessage(List<MessageEntity> messageEntityList) {
        coverMessageEntityStatus(messageEntityList);
        mIMessageListView.insertMessageList(messageEntityList);
    }

    private void coverMessageEntityStatus(List<MessageEntity> messageEntityList) {
        MessageResourceHelper messageResourceHelper = IMClient.getClient().getResourceHelper();
        for (MessageEntity messageEntity : messageEntityList) {
            if (messageEntity.getStatus() != PushUtil.MsgDeliveryType.SUCCESS
                    && messageResourceHelper.hasTask(messageEntity.getId())) {
                messageEntity.setStatus(PushUtil.MsgDeliveryType.UPLOADING);
            }
        }
    }

    private void receiveImageMessageAgain(MessageBody messageBody) {
        try {
            mMessageResourceHelper.addImageDownloadTask(messageBody.getMid(), messageBody.getBody());
        } catch (IOException e) {
            mIMessageListView.notifiy("图片文件不存在,语音消息接受失败");
        }
    }

    private void receiveAudioMessageAgain(MessageBody messageBody) {
        AudioBody audioBody = AudioUtil.getAudioBody(messageBody.getBody());
        try {
            if (TextUtils.isEmpty(audioBody.getFile())) {
                mIMessageListView.notifiy("音频文件不存在,语音消息接受失败");
                return;
            }
            mMessageResourceHelper.addAudioDownloadTask(messageBody.getMid(), audioBody.getFile());
        } catch (IOException e) {
            mIMessageListView.notifiy("音频文件不存在,语音消息接受失败");
        }
    }

    private void uploadMediaAgain(File file, MessageEntity messageEntity) {
        try {
            messageEntity.setStatus(MessageEntity.StatusType.UPLOADING);
            mIMessageListView.updateMessageEntity(messageEntity);

            UpYunUploadTask upYunUploadTask = new UpYunUploadTask(messageEntity.getId(), mTargetId, file, getRequestHeaders());
            IMClient.getClient().getResourceHelper().addTask(upYunUploadTask);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void sendImageMediaMessageAgain(MessageEntity messageEntity) {
        IMUploadEntity uploadEntity = mIMessageDataProvider.getMessageManager()
                .getUploadEntity(messageEntity.getUid());
        if (uploadEntity == null) {
            mIMessageListView.notifiy("媒体文件不存在,请重新发送消息");
            return;
        }
        File audioFile = new File(uploadEntity.getSource());
        uploadMediaAgain(audioFile, messageEntity);
    }

    private void sendAudioMessageAgain(MessageEntity messageEntity) {
        IMUploadEntity uploadEntity = mIMessageDataProvider.getMessageManager()
                .getUploadEntity(messageEntity.getUid());
        if (uploadEntity == null) {
            mIMessageListView.notifiy("媒体文件不存在,请重新发送消息");
            return;
        }
        File audioFile = new File(uploadEntity.getSource());
        uploadMediaAgain(audioFile, messageEntity);
    }

    public void sendMessageToServer(MessageBody messageBody) {
        mIMessageDataProvider.sendMessage(mConversationNo, messageBody);
    }

    @Override
    public void processResourceDownload(int resId, String resUri) {
        MessageEntity messageEntity = mIMessageDataProvider.getMessageManager().getMessage(resId);
        if (messageEntity == null) {
            return;
        }
        if (TextUtils.isEmpty(resUri)) {
            updateMessageReceiveStatus(messageEntity, MessageEntity.StatusType.FAILED);
            return;
        }

        MessageBody messageBody = new MessageBody(messageEntity);
        if (PushUtil.ChatMsgType.AUDIO.equals(messageBody.getType())) {
            updateMessageReceiveStatus(messageEntity, MessageEntity.StatusType.UNREAD);
            return;
        }
        updateMessageReceiveStatus(messageEntity, MessageEntity.StatusType.SUCCESS);
    }

    @Override
    public void uploadMedia(File file, MessageBody messageBody) {
        try {
            MessageEntity messageEntity = mIMessageDataProvider.createMessageEntity(messageBody);
            messageEntity.setStatus(MessageEntity.StatusType.UPLOADING);

            MessageResourceHelper messageResourceHelper = IMClient.getClient().getResourceHelper();
            if (messageEntity.getStatus() != PushUtil.MsgDeliveryType.SUCCESS
                    && messageResourceHelper.hasTask(messageEntity.getId())) {
                messageEntity.setStatus(PushUtil.MsgDeliveryType.UPLOADING);
            }
            mIMessageListView.insertMessage(messageEntity);
            mIMessageDataProvider.getMessageManager().saveUploadEntity(
                    messageBody.getMessageId(), messageBody.getType(), file.getPath()
            );

            UpYunUploadTask upYunUploadTask = new UpYunUploadTask(messageEntity.getId(), mTargetId, file, getRequestHeaders());
            IMClient.getClient().getResourceHelper().addTask(upYunUploadTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertMessageList() {
        List<MessageEntity> messageEntityList  = IMClient.getClient().getChatRoom(mConversationNo).getMessageList(mStart);
        mIMessageListView.insertMessageList(messageEntityList);
    }

    @Override
    public void addMessageReceiver() {
        if (mIMMessageReceiver == null) {
            mIMMessageReceiver = getIMMessageListener();
        }

        IMClient.getClient().addMessageReceiver(mIMMessageReceiver);
        IMClient.getClient().getConvManager().clearReadCount(mConversationNo);
    }

    @Override
    public void updateMessageReceiveStatus(MessageEntity messageEntity, int status) {
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        mIMessageDataProvider.getMessageManager().updateMessageFieldByMsgNo(messageEntity.getMsgNo(), cv);
        messageEntity.setStatus(status);
        mIMessageListView.updateMessageEntity(messageEntity);
    }

    @Override
    public void start() {
        if (convNoIsEmpty(mConversationNo)) {
            createConvNo(new ConvNoCreateCallback() {
                @Override
                public void onCreateConvNo(String convNo) {
                    if (convNoIsEmpty(convNo)) {
                        Log.d(TAG, "mConversationNo is null");
                        return;
                    }
                    Log.d(TAG, "onCreateConvNo " + convNo);
                    mConversationNo = convNo;
                    checkTargetRole();
                }
            });
            return;
        }

        checkTargetRole();
    }

    protected void insertMessageToList(MessageEntity messageEntity) {
        MessageResourceHelper messageResourceHelper = IMClient.getClient().getResourceHelper();
        if (messageEntity.getStatus() != PushUtil.MsgDeliveryType.SUCCESS
                && messageResourceHelper.hasTask(messageEntity.getId())) {
            messageEntity.setStatus(PushUtil.MsgDeliveryType.UPLOADING);
        }
        mIMessageListView.insertMessage(messageEntity);
    }

    private String wrapAudioMessageContent(String audioFilePath, int audioTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("f", audioFilePath);
            jsonObject.put("d", audioTime);
        } catch (JSONException e) {
        }

        return jsonObject.toString();
    }

    protected MessageBody createSendMessageBody(String content, String type) {
        MessageBody messageBody = new MessageBody(1, type, content);
        messageBody.setCreatedTime(System.currentTimeMillis());
        messageBody.setDestination(new Destination(mTargetRole.getRid(), mTargetRole.getType()));
        messageBody.getDestination().setNickname(mTargetRole.getNickname());
        messageBody.setSource(new Source(IMClient.getClient().getClientId(), Destination.USER));
        messageBody.getSource().setNickname(IMClient.getClient().getClientName());
        messageBody.setConvNo(mConversationNo);
        messageBody.setMessageId(UUID.randomUUID().toString());

        return messageBody;
    }

    private void checkConvEntity(Role role) {
        mIMessageDataProvider.updateConvEntity(mConversationNo, role);
    }

    private void checkTargetRole() {
        if (mTargetRole.getRid() != 0) {
            checkConvEntity(mTargetRole);
            mIMessageListView.setMessageList(mIMessageDataProvider.getMessageList(mConversationNo, mStart));
            return;
        }
        createRole(mTargetType, mTargetId, new RoleUpdateCallback() {
            @Override
            public void onCreateRole(Role role) {
                if (role.getRid() == 0) {
                    Log.d(TAG, "mTargetRole is null");
                    return;
                }
                Log.d(TAG, "mTargetRole " + role.getRid());
                mTargetRole = role;
                IMClient.getClient().getRoleManager().createRole(role);
                checkConvEntity(role);
                mIMessageListView.setMessageList(mIMessageDataProvider.getMessageList(mConversationNo, mStart));
            }
        });
    }

    protected abstract Map<String, String> getRequestHeaders();

    protected abstract void createRole(String type, int rid, RoleUpdateCallback callback);

    protected abstract void createConvNo(ConvNoCreateCallback convNoCreateCallback);

    private void initParams(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        mConversationNo = bundle.getString(CONV_NO);
        mTargetId = bundle.getInt(TARGET_ID, 0);
        mTargetType = bundle.getString(TARGET_TYPE);

        mTargetRole = IMClient.getClient().getRoleManager().getRole(mTargetType, mTargetId);
    }

    /**
     * 检查是否有convNo
     */
    private void checkConvNo() {
        ConvEntity convEntity = IMClient.getClient().getConvManager()
                .getConvByTypeAndId(mTargetType, mTargetId);
        if (convNoIsEmpty(mConversationNo) && convEntity != null) {
            mConversationNo = convEntity.getConvNo();
        }
    }

    protected void updateMessageSendStatus(MessageBody messageBody) {
        ContentValues cv = new ContentValues();
        cv.put("status", MessageEntity.StatusType.SUCCESS);
        mIMessageDataProvider.getMessageManager().updateMessageFieldByUid(messageBody.getMessageId(), cv);

        MessageEntity messageEntity = mIMessageDataProvider.getMessageManager().getMessageByUID(messageBody.getMessageId());
        mIMessageListView.updateMessageEntity(messageEntity);
    }

    protected boolean convNoIsEmpty(String convNo) {
        return TextUtils.isEmpty(convNo) || "0".equals(convNo);
    }

    protected IMMessageReceiver getIMMessageListener() {
        return new IMMessageReceiver() {
            @Override
            public boolean onReceiver(MessageEntity msg) {
                if (!mConversationNo.equals(msg.getConvNo())) {
                    return true;
                }

                mIMessageListView.insertMessage(msg);
                IMClient.getClient().getConvManager().clearReadCount(mConversationNo);
                return true;
            }

            @Override
            public boolean onOfflineMsgReceiver(List<MessageEntity> messageEntities) {
                coverMessageEntityStatus(messageEntities);
                mIMessageListView.insertMessageList(messageEntities);
                IMClient.getClient().getConvManager().clearReadCount(mConversationNo);
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
                return new ReceiverInfo(mTargetType, mConversationNo);
            }
        };
    }

    public interface RoleUpdateCallback {
        void onCreateRole(Role role);
    }

    public interface ConvNoCreateCallback {
        void onCreateConvNo(String convNo);
    }
}
