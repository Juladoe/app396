package com.edusoho.kuozhi.v3.model.provider;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.edusoho.kuozhi.imserver.IImServerAidlInterface;
import com.edusoho.kuozhi.imserver.SendEntity;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.IMUploadEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.managar.IMConvManager;
import com.edusoho.kuozhi.imserver.ui.data.IMessageDataProvider;
import com.edusoho.kuozhi.imserver.util.MessageEntityBuildr;
import com.edusoho.kuozhi.imserver.util.SendEntityBuildr;
import com.edusoho.kuozhi.v3.util.AppUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suju on 16/10/13.
 */
public class LiveChatDataProvider implements IMessageDataProvider {

    private String mRole;
    private WeakReference<IImServerAidlInterface> mImBinderRef;
    private List<MessageEntity> mMessageEntityList;

    public LiveChatDataProvider(IImServerAidlInterface imBinder) {
        mMessageEntityList = new ArrayList<>();
        mImBinderRef = new WeakReference<IImServerAidlInterface>(imBinder);
    }

    @Override
    public MessageEntity createMessageEntity(MessageBody messageBody) {
        MessageEntity messageEntity = new MessageEntityBuildr()
                .addUID(messageBody.getMessageId())
                .addConvNo(messageBody.getConvNo())
                .addToId("all")
                .addToName("all")
                .addFromId(String.valueOf(messageBody.getSource().getId()))
                .addFromName(messageBody.getSource().getNickname())
                .addCmd("message")
                .addStatus(MessageEntity.StatusType.FAILED)
                .addMsg(wrapLiveMessageBody(messageBody))
                .addTime((int) (messageBody.getCreatedTime() / 1000))
                .builder();

        messageEntity.setId(mMessageEntityList.size());
        messageBody.setMid(mMessageEntityList.size());
        mMessageEntityList.add(messageEntity);
        return messageEntity;
    }

    @Override
    public List<MessageEntity> getMessageList(String convNo, int start) {
        return new ArrayList<>();
    }

    @Override
    public void sendMessage(String convNo, MessageBody messageBody) {
        try {
            String toId = "all";
            messageBody.setMsgStatus(MessageEntity.StatusType.NONE);
            SendEntity sendEntity = SendEntityBuildr.getBuilder()
                    .addToId(toId)
                    .addToName("all")
                    .addK(String.valueOf(messageBody.getMid()))
                    .addCmd("flashSend")
                    .addMsg(wrapLiveMessageBody(messageBody))
                    .builder();
            sendToServer(convNo, sendEntity);
        } catch (Exception e) {
        }
    }

    private String wrapLiveMessageBody(MessageBody messageBody) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("convNo", messageBody.getConvNo());
            jsonObject.put("type", "102001");
            jsonObject.put("clientId", messageBody.getSource().getId());
            jsonObject.put("clientName", messageBody.getSource().getNickname());
            jsonObject.put("role", mRole);

            JSONObject dataJsonObj = new JSONObject();
            dataJsonObj.put("info", messageBody.getBody());
            jsonObject.put("data", dataJsonObj);
            jsonObject.put("time", messageBody.getCreatedTime());
        } catch (JSONException e) {
        }

        return jsonObject.toString();
    }

    private void sendToServer(String convNo, SendEntity sendEntity) {
        try {
            sendEntity.setConvNo(convNo);
            mImBinderRef.get().send(sendEntity);
        } catch (Exception e) {
        }
    }

    @Override
    public int deleteMessageById(int msgId) {
        return 0;
    }

    @Override
    public IMUploadEntity getUploadEntity(String muid) {
        return null;
    }

    @Override
    public long saveUploadEntity(String muid, String type, String source) {
        return 0;
    }

    @Override
    public MessageEntity getMessageByUID(String uid) {
        if (TextUtils.isEmpty(uid)) {
            return null;
        }
        return mMessageEntityList.get(AppUtil.parseInt(uid));
    }

    @Override
    public MessageEntity getMessage(int msgId) {
        return mMessageEntityList.get(msgId);
    }

    @Override
    public int updateMessageFieldByMsgNo(String msgNo, ContentValues cv) {
        return 0;
    }

    @Override
    public int updateMessageFieldByUid(String uid, ContentValues cv) {
        return 0;
    }

    public static class MockConvManager extends IMConvManager {

        public MockConvManager(Context context) {
            super(context);
        }

        @Override
        public long createConv(ConvEntity convEntity) {
            return 0;
        }

        @Override
        public ConvEntity getConvByConvNo(String convNo) {
            return null;
        }

        @Override
        public ConvEntity getConvByTypeAndId(String type, int targetId) {
            return null;
        }

        @Override
        public int updateConvByConvNo(ConvEntity convEntity) {
            return 0;
        }

        @Override
        public int clearReadCount(String convNo) {
            return 0;
        }
    }
}
