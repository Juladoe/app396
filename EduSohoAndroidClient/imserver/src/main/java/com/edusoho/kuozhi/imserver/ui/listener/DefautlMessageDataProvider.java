package com.edusoho.kuozhi.imserver.ui.listener;

import android.content.ContentValues;
import android.util.Log;

import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.SendEntity;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.managar.IMMessageManager;
import com.edusoho.kuozhi.imserver.util.MessageEntityBuildr;
import com.edusoho.kuozhi.imserver.util.SendEntityBuildr;
import java.util.List;

/**
 * Created by suju on 16/9/6.
 */
public class DefautlMessageDataProvider implements IMessageDataProvider {

    private static final int EXPAID_TIME = 3600 * 1 * 1000;

    private ConvEntity createConvNo(String convNo, Role role) {
        ConvEntity convEntity = new ConvEntity();
        convEntity.setTargetId(role.getRid());
        convEntity.setTargetName(role.getNickname());
        convEntity.setConvNo(convNo);
        convEntity.setType(role.getType());
        convEntity.setAvatar(role.getAvatar());
        convEntity.setCreatedTime(System.currentTimeMillis());
        convEntity.setUpdatedTime(0);
        IMClient.getClient().getConvManager().createConv(convEntity);

        return convEntity;
    }

    @Override
    public void updateConvEntity(String convNo, Role role) {
        ConvEntity convEntity = IMClient.getClient().getConvManager().getConvByConvNo(convNo);
        if (convEntity == null) {
            Log.d("DefautlMessageProvider", "create ConvNo");
            convEntity = createConvNo(convNo, role);
        }

        if ((System.currentTimeMillis() - convEntity.getUpdatedTime()) > EXPAID_TIME) {
            Log.d("DefautlMessageProvider", "update ConvNo");
            convEntity.setAvatar(role.getAvatar());
            convEntity.setTargetName(role.getNickname());
            convEntity.setUpdatedTime(System.currentTimeMillis());
            IMClient.getClient().getConvManager().updateConvByConvNo(convEntity);
        }
    }

    @Override
    public List<MessageEntity> getMessageList(String convNo, int start) {
        return IMClient.getClient().getChatRoom(convNo).getMessageList(start);
    }

    @Override
    public void sendMessage(String convNo, MessageBody messageBody) {
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
            messageBody.setMsgStatus(MessageEntity.StatusType.NONE);
            SendEntity sendEntity = SendEntityBuildr.getBuilder()
                    .addToId(toId)
                    .addMsg(messageBody.toJson())
                    .builder();
            IMClient.getClient().getChatRoom(convNo).send(sendEntity);
        } catch (Exception e) {
        }
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
                .addStatus(MessageEntity.StatusType.FAILED)
                .addMsg(messageBody.toJson())
                .addTime((int) (messageBody.getCreatedTime() / 1000))
                .builder();
    }

    @Override
    public IMMessageManager getMessageManager() {
        return IMClient.getClient().getMessageManager();
    }

    @Override
    public MessageEntity createMessageEntity(MessageBody messageBody) {
        MessageEntity messageEntity = createMessageEntityByBody(messageBody);
        messageEntity = IMClient.getClient().getMessageManager().createMessage(messageEntity);
        updateConv(messageBody);
        return messageEntity;
    }

    private void updateConv(MessageBody messageBody) {
        ContentValues cv = new ContentValues();
        cv.put("laterMsg", messageBody.toJson());
        cv.put("updatedTime", System.currentTimeMillis());
        IMClient.getClient().getConvManager().updateConvField(messageBody.getConvNo(), cv);
    }
}
