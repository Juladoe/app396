package com.edusoho.kuozhi.imserver.ui.listener;

import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.managar.IMMessageManager;

import java.util.List;

/**
 * Created by suju on 16/9/6.
 */
public interface IMessageDataProvider {

    IMMessageManager getMessageManager();

    MessageEntity createMessageEntity(MessageBody messageBody);

    void updateConvEntity(String convNo, Role role);

    void sendMessage(String convNo, MessageBody messageBody);

    List<MessageEntity> getMessageList(String convNo, int start);
}
