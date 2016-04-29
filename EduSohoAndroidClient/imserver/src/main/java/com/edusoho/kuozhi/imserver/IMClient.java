package com.edusoho.kuozhi.imserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.edusoho.kuozhi.imserver.broadcast.IMBroadcastReceiver;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 菊 on 2016/4/23.
 */
public class IMClient {

    private static Object mLock = new Object();
    private static IMClient client = null;

    private Context mContext;
    private IImServerAidlInterface mImBinder;
    private IMMessageReceiver mLaterIMMessageReceiver;
    private List<IMMessageReceiver> mMessageReceiverList;

    private IMClient() {
        mMessageReceiverList = new LinkedList<>();
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public void start(LinkedHashMap<String, String> hostMap) {
        Intent intent = new Intent("com.edusoho.kuozhi.imserver.IImServerAidlInterface");
        intent.setPackage(mContext.getPackageName());

        ArrayList<String> hostList = new ArrayList<String>();
        hostList.addAll(hostMap.values());

        ArrayList<String> ignoreNosList = new ArrayList<String>();
        ignoreNosList.addAll(hostMap.keySet());

        intent.putStringArrayListExtra(ImService.HOST, hostList);
        intent.putStringArrayListExtra(ImService.IGNORE_NOS, ignoreNosList);
        intent.putExtra(ImService.CLIENT_NAME, getRandomClientName(mContext));
        intent.putExtra(ImService.ACTION, ImService.ACTION_INIT);
        mContext.startService(intent);

        connectService();
    }

    private void connectService() {
        ServiceConnection mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mImBinder = IImServerAidlInterface.Stub.asInterface(service);
                Log.d(getClass().getSimpleName(), "----" + service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(getClass().getSimpleName(), name.toString());
            }
        };
        boolean result = mContext.bindService(
                new Intent("com.edusoho.kuozhi.imserver.IImServerAidlInterface")
                        .setPackage(mContext.getPackageName()),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
        );
        Log.d(getClass().getSimpleName(), "bind:" + result);
    }

    public IMChatRoom getChatRoom(String convNo) {
        return new IMChatRoom(convNo, mImBinder);
    }

    public void addMessageReceiver(IMMessageReceiver receiver) {
        this.mMessageReceiverList.add(receiver);
    }

    public void removeReceiver(IMMessageReceiver receiver) {
        this.mMessageReceiverList.remove(receiver);
    }

    public void invokeReceiver(MessageEntity messageEntity) {
        int count = mMessageReceiverList.size();
        for (int i = count - 1; i >= 0; i--) {
            IMMessageReceiver receiver = mMessageReceiverList.get(i);
            if ("success".equals(messageEntity.getCmd())) {
                receiver.onSuccess(messageEntity.getMsg());
                continue;
            }
            receiver.getType().isProcessed = receiver.onReceiver(messageEntity);
            this.mLaterIMMessageReceiver = receiver;
        }

        this.mLaterIMMessageReceiver = null;
    }

    public boolean isHandleMessageInFront(String msgType, int msgId) {
        ReceiverInfo receiverInfo = null;
        if (mLaterIMMessageReceiver == null || (receiverInfo = mLaterIMMessageReceiver.getType()) == null) {
            return false;
        }

        return msgType.equals(receiverInfo.msgType) && msgId == receiverInfo.msgId;
    }

    private String getRandomClientName(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return String.format("android_%s", TelephonyMgr.getDeviceId());
    }

    public static IMClient getClient() {
        synchronized (mLock) {
            if (client == null) {
                client = new IMClient();
            }
        }

        return client;
    }
}
