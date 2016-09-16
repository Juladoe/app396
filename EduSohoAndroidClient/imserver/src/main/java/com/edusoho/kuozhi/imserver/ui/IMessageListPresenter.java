package com.edusoho.kuozhi.imserver.ui;

import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;

import java.io.File;

/**
 * Created by suju on 16/9/16.
 */
public interface IMessageListPresenter {

    String CONV_NO = "convNo";
    String TARGET_TYPE = "targetType";
    String TARGET_ID = "targetId";

    void start();

    void processResourceDownload(int resId, String resUri);

    void processResourceStatusChange(int resId, String resUri);

    void updateMessageReceiveStatus(MessageEntity messageEntity, int status);

    void insertMessageList();

    void uploadMedia(File file, MessageBody messageBody);

    void sendTextMessage(String content);

    void sendAudioMessage(File audioFile, int audioLength);

    void sendImageMessage(File imageFile);

    void onSendMessageAgain(MessageEntity messageEntity);

    void addMessageReceiver();

    void removeReceiver();

    void refresh();
}
