package com.edusoho.kuozhi.v3.service.message;

import android.content.Context;
import android.os.Bundle;

import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;

/**
 * Created by 菊 on 2016/4/25.
 */
public class MessageCommand extends AbstractCommand {

    public MessageCommand(Context context, IMMessageReceiver receiver, V2CustomContent v2CustomContent)
    {
        super(context, receiver, v2CustomContent);
    }

    @Override
    public void invoke() {
        if (! IMClient.getClient().isHandleMessageInFront("chat", mV2CustomContent.getFrom().getId())) {
            NotificationUtil.showMsgNotification(mContext, mV2CustomContent);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(Const.ADD_CHAT_MSG_DESTINATION, NewsFragment.HANDLE_RECEIVE_CHAT_MSG);
        bundle.putSerializable(Const.GET_PUSH_DATA, mV2CustomContent);
        MessageEngine.getInstance().sendMsgToTaget(Const.ADD_MSG, bundle, NewsFragment.class);
        Chat chatModel = new Chat(mV2CustomContent);
        ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, EdusohoApp.app.domain));
        chatDataSource.create(chatModel);
    }
}
