package com.edusoho.kuozhi.core;

import android.os.Bundle;
import android.util.Log;

import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by howzhi on 14-8-18.
 */
public class MessageEngine {

    private ConcurrentHashMap<String, MessageCallback> sourceMap;
    private ConcurrentHashMap<String, ArrayList<String>> pubMsgMap;

    private static Object synchronizedObj = new Object();

    private static MessageEngine messageEngine;

    private MessageEngine()
    {
        pubMsgMap = new ConcurrentHashMap<String, ArrayList<String>>();
        sourceMap = new ConcurrentHashMap<String, MessageCallback>();
    }

    public ConcurrentHashMap<String, MessageCallback> getSourceMap()
    {
        return sourceMap;
    }

    public static MessageEngine init()
    {
        synchronized (synchronizedObj) {
            if (messageEngine == null) {
                messageEngine = new MessageEngine();
            }
        }

        return messageEngine;
    }

    public void sendMsgToTaget(int msgType, Bundle body, Class target)
    {
        String targetName = target.getSimpleName();
        MessageType messageType = new MessageType(msgType, targetName);

        MessageCallback messageCallback = sourceMap.get(targetName);
        messageCallback.invoke(new WidgetMessage(messageType, body));
    }

    public void sendMsg(String msgType, Bundle body)
    {
        ArrayList<String> msgList = pubMsgMap.get(msgType);
        if (msgList == null) {
            return;
        }
        MessageType messageType = new MessageType(msgType);
        int size = msgList.size();
        for (int i=0; i < size; i++) {
            String name = msgList.get(i);
            MessageCallback messageCallback = sourceMap.get(name);
            Log.d(null, "callback->" + messageCallback);
            if (messageCallback == null) {
                msgList.remove(i);
                continue;
            }
            messageCallback.invoke(new WidgetMessage(messageType, body));
        }
    }

    public void unRegistMessageSource(MessageCallback source)
    {
        if (source == null) {
            return;
        }
        String targetName = source.getClass().getSimpleName();
        sourceMap.remove(targetName);
    }

    public void registMessageSource(MessageCallback source)
    {
        if (source == null) {
            return;
        }
        String targetName = source.getClass().getSimpleName();
        sourceMap.put(targetName, source);
        MessageType[] msgTypes = source.getMsgTypes();
        if (msgTypes == null) {
            return;
        }

        for (MessageType msgType : msgTypes) {
            if (msgType.code != MessageType.NONE) {
                continue;
            }
            //all regist msgtype source device
            ArrayList<String> msgList = pubMsgMap.get(msgType.toString());
            if (msgList == null) {
                msgList = new ArrayList<String>();
            }

            Log.d(null, "regist_pub message-> " + msgType);
            if (!msgList.contains(targetName)) {
                msgList.add(targetName);
            }
            pubMsgMap.put(msgType.toString(), msgList);
        }
    }

    public static interface MessageCallback
    {
        public void invoke(WidgetMessage message);
        public MessageType[] getMsgTypes();
    }
}
