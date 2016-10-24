package com.edusoho.kuozhi.v3.ui.live;

import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.ui.IMessageListView;

/**
 * Created by suju on 16/10/18.
 */
public interface ILiveChatPresenter {

    void setUserCanChatStatus(MessageEntity message);

    void onHandleMessage(MessageEntity messageEntity);

    void joinLiveChatRoom();

    void setView(IMessageListView view);
}
