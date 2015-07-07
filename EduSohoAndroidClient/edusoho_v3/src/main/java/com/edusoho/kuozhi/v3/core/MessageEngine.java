package com.edusoho.kuozhi.v3.core;

import android.os.Bundle;
import android.util.Log;

import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JesseHuang on 15/4/23.
 */
public class MessageEngine {

    private ConcurrentHashMap<String, MessageCallback> sourceMap;
    private ConcurrentHashMap<String, ArrayList<String>> pubMsgMap;

    private static Object synchronizedObj = new Object();

    private static MessageEngine messageEngine;

    private MessageEngine() {
        pubMsgMap = new ConcurrentHashMap<String, ArrayList<String>>();
        sourceMap = new ConcurrentHashMap<String, MessageCallback>();
    }

    public ConcurrentHashMap<String, MessageCallback> getSourceMap() {
        return sourceMap;
    }

    public static MessageEngine init() {
        synchronized (synchronizedObj) {
            if (messageEngine == null) {
                messageEngine = new MessageEngine();
            }
        }

        return messageEngine;
    }

    private String getMsgTargetType(Object target) {
        if (target instanceof Class) {
            return ((Class) target).getSimpleName();
        }

        return getObjectType(target);
    }

    private String getRegitstSourceType(MessageCallback source) {
        if (source.getMode() == MessageCallback.REGIST_CLASS) {
            return source.getClass().getSimpleName();
        }
        return getObjectType(source);
    }

    private String getObjectType(Object target) {
        return String.format(
                "%s@%s",
                target.getClass().getSimpleName(),
                Integer.toHexString(target.hashCode())
        );
    }

    public void sendMsgToTaget(int msgType, Bundle body, Object target) {
        String targetName = getMsgTargetType(target);
        MessageType messageType = new MessageType(msgType, targetName);

        MessageCallback messageCallback = sourceMap.get(targetName);
        if (messageCallback == null) {
            return;
        }
        messageCallback.invoke(new WidgetMessage(messageType, body));
    }

    public void sendMsgToTagetForCallback(
            int msgType, Bundle body, Class target, NormalCallback callback) {
        String targetName = target.getSimpleName();
        MessageType messageType = new MessageType(msgType, targetName);

        MessageCallback messageCallback = sourceMap.get(targetName);
        if (messageCallback == null) {
            return;
        }
        messageCallback.invoke(new WidgetMessage(messageType, body, callback));
    }

    public void sendMsg(String msgType, Bundle body) {
        ArrayList<String> msgList = pubMsgMap.get(msgType);
        if (msgList == null) {
            return;
        }
        MessageType messageType = new MessageType(msgType);
        int size = msgList.size();
        for (int i = 0; i < size; i++) {
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

    public void unRegistMessageSource(MessageCallback source) {
        if (source == null) {
            return;
        }
        String targetName = getRegitstSourceType(source);
        sourceMap.remove(targetName);
    }

    public void unRegistPubMessage(MessageType type, MessageCallback source) {
        if (source == null) {
            return;
        }
        ArrayList<String> list = pubMsgMap.get(type.toString());
        String targetName = getRegitstSourceType(source);
        list.remove(targetName);
    }

    public void registMessageSource(MessageCallback source) {
        if (source == null) {
            return;
        }
        String targetName = getRegitstSourceType(source);
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

            if (!msgList.contains(targetName)) {
                msgList.add(targetName);
            }
            pubMsgMap.put(msgType.toString(), msgList);
        }
    }

    public static interface MessageCallback {
        public static final int REGIST_CLASS = 0;
        public static final int REGIST_OBJECT = 1;

        public void invoke(WidgetMessage message);

        public MessageType[] getMsgTypes();

        public int getMode();
    }
}
