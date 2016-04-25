// IImServerAidlInterface.aidl
package com.edusoho.kuozhi.imserver;

// Declare any non-default types here with import statements

interface IImServerAidlInterface {

    void send(String convNo, String message);

    void joinConversation(String clientId, String nickname, String convNo);
}
