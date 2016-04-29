// IImServerAidlInterface.aidl
package com.edusoho.kuozhi.imserver;

import com.edusoho.kuozhi.imserver.SendEntity;

// Declare any non-default types here with import statements

interface IImServerAidlInterface {

    void send(in SendEntity sendEntity);

    void joinConversation(String clientId, String nickname, String convNo);
}
