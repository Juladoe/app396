package com.edusoho.kuozhi.imserver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.edusoho.kuozhi.imserver.broadcast.IMServiceStartedBroadcastReceiver;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.entity.message.Source;
import com.edusoho.kuozhi.imserver.factory.DbManagerFactory;
import com.edusoho.kuozhi.imserver.listener.IConnectManagerListener;
import com.edusoho.kuozhi.imserver.listener.IMConnectStatusListener;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.imserver.managar.IMBlackListManager;
import com.edusoho.kuozhi.imserver.managar.IMChatRoom;
import com.edusoho.kuozhi.imserver.managar.IMConvManager;
import com.edusoho.kuozhi.imserver.managar.IMMessageManager;
import com.edusoho.kuozhi.imserver.managar.IMRoleManager;
import com.edusoho.kuozhi.imserver.ui.helper.MessageResourceHelper;
import com.edusoho.kuozhi.imserver.util.IMConnectStatus;
import com.edusoho.kuozhi.imserver.util.SystemUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 菊 on 2016/4/23.
 */
public class IMClient {

    public static final String TAG = "IMClient";

    private static Object mLock = new Object();
    private static IMClient client = null;

    private int mIMConnectStatus;
    private Context mContext;
    private int mClientId;
    private String mClientName;
    private MessageResourceHelper mMessageResourceHelper;

    private IImServerAidlInterface mImBinder;
    private ServiceConnection mServiceConnection;
    private IMMessageReceiver mLaterIMMessageReceiver;
    private List<IMMessageReceiver> mMessageReceiverList;
    private List<IMConnectStatusListener> mIMConnectStatusListenerList;
    private IMMessageReceiver mGlobalIMMessageReceiver;
    private BroadcastReceiver mIMServiceStatusBroadcastReceiver;
    private ConnectIMServiceRunnable mConnectIMServiceRunnable;

    private IMClient() {
        this.mIMConnectStatus = IMConnectStatus.NO_READY;
        mMessageReceiverList = new LinkedList<>();
        mIMConnectStatusListenerList = new LinkedList<>();
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public void setClientInfo(int clientId, String clientName) {
        this.mClientId = clientId;
        this.mClientName = clientName;
        this.mMessageResourceHelper = new MessageResourceHelper(mContext);
        registIMServiceStatusBroadcastReceiver();
    }

    public void setIMDataBase(String dbName) {
        Log.d(TAG, "setIMDataBase : " + dbName);
        DbManagerFactory.getDefaultFactory().setDbName(mContext, dbName);
    }

    private void registIMServiceStatusBroadcastReceiver() {
        mIMServiceStatusBroadcastReceiver = new IMServiceStartedBroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mConnectIMServiceRunnable == null) {
                    return;
                }
                new Handler(Looper.getMainLooper()).post(mConnectIMServiceRunnable);
            }
        };

        mContext.registerReceiver(mIMServiceStatusBroadcastReceiver, new IntentFilter(IMServiceStartedBroadcastReceiver.ACTION_NAME));
    }

    private void unRegistIMServiceStatusBroadcastReceiver() {
        if (mIMServiceStatusBroadcastReceiver != null) {
            mContext.unregisterReceiver(mIMServiceStatusBroadcastReceiver);
        }
    }

    private void startImService() {
        Intent intent = getIMServiceIntent();
        intent.putExtra(ImService.ACTION, ImService.ACTION_INIT);
        mContext.startService(intent);
    }

    public void start(ArrayList<String> ignoreNosList, ArrayList<String> hostList) {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        if (processAppName == null || !processAppName.equalsIgnoreCase(mContext.getPackageName())) {
            Log.e(TAG, "enter the service process!");
            return;
        }
        this.mConnectIMServiceRunnable = new ConnectIMServiceRunnable(mClientId, mClientName, ignoreNosList, hostList);
        startImService();
    }

    public MessageResourceHelper getResourceHelper() {
        return mMessageResourceHelper;
    }

    public void destory() {
        if (mImBinder != null) {
            try {
                mImBinder.closeIMServer();
            } catch (RemoteException e) {
                Log.e(TAG, "closeIMServer error");
            }
        }
        if (mServiceConnection != null) {
            mContext.unbindService(mServiceConnection);
            mServiceConnection = null;
        }

        if (mMessageResourceHelper != null) {
            mMessageResourceHelper.clear();
            mMessageResourceHelper = null;
        }
        unRegistIMServiceStatusBroadcastReceiver();
        mContext.stopService(getIMServiceIntent());
        mImBinder = null;

        setClientInfo(0, null);
    }

    private Intent getIMServiceIntent() {
        Intent intent = new Intent("com.edusoho.kuozhi.imserver.IImServerAidlInterface");
        intent.setPackage(mContext.getPackageName());
        return intent;
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    private void connectService(final int clientId, final String clientName, final String[] ignoreNosList, final String[] hostList) {
        if (mServiceConnection != null) {
            mContext.unbindService(mServiceConnection);
        }
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mImBinder = IImServerAidlInterface.Stub.asInterface(service);
                try {
                    Log.d(TAG, "mImBinder:" + mImBinder);
                    mImBinder.start(clientId, clientName, ignoreNosList, hostList);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, name.toString());
            }
        };
        boolean result = mContext.bindService(
                new Intent("com.edusoho.kuozhi.imserver.IImServerAidlInterface")
                        .setPackage(mContext.getPackageName()),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
        );
        Log.d(TAG, "bind:" + result);
    }

    public IMChatRoom getChatRoom(String convNo) {
        return new IMChatRoom(mContext, convNo, mImBinder);
    }

    public IMBlackListManager getIMBlackListManager() {
        return new IMBlackListManager(mContext);
    }

    public void addConnectStatusListener(IMConnectStatusListener listener) {
        this.mIMConnectStatusListenerList.add(listener);
    }

    public void addMessageReceiver(IMMessageReceiver receiver) {
        this.mMessageReceiverList.add(receiver);
    }

    public void removeGlobalIMMessageReceiver() {
        removeReceiver(mGlobalIMMessageReceiver);
        this.mGlobalIMMessageReceiver = null;
    }

    public void addGlobalIMMessageReceiver(IMMessageReceiver receiver) {
        this.mGlobalIMMessageReceiver = receiver;
        addMessageReceiver(receiver);
    }

    public void removeReceiver(IMMessageReceiver receiver) {
        this.mMessageReceiverList.remove(receiver);
    }

    public int getClientId() {
        return mClientId;
    }

    public String getClientName() {
        return mClientName;
    }

    public void sendCmd(String cmd) {
        try {
            switch (cmd) {
                case "requestOfflineMsg":
                    if (mImBinder != null) {
                        mImBinder.requestOfflineMsg();
                    }
            }
        } catch (RemoteException e) {
        }
    }

    public void setIMConnectStatus(int status) {
        this.mIMConnectStatus = status;
        invokeConnectReceiver(status, false);
    }

    public int getIMConnectStatus() {
        if (!SystemUtil.isServiceRunning(mContext, ImService.class)) {
            return IMConnectStatus.NO_READY;
        }

        try {
            return mImBinder == null ? IMConnectStatus.NO_READY : mImBinder.getIMStatus();
        } catch (RemoteException e) {
            return IMConnectStatus.ERROR;
        }
    }

    public void invokeConnectReceiver(int status, boolean isConnected) {
        Log.d("IMClient", "invokeConnectReceiver:" + status);
        int count = mIMConnectStatusListenerList.size();
        for (int i = count - 1; i >= 0; i--) {
            IMConnectStatusListener receiver = mIMConnectStatusListenerList.get(i);
            switch (status) {
                case IConnectManagerListener.OPEN:
                    receiver.onOpen();
                    break;
                case IConnectManagerListener.CLOSE:
                case IConnectManagerListener.END:
                    receiver.onClose();
                    break;
                case IConnectManagerListener.CONNECTING:
                    receiver.onConnect();
                    break;
                case IConnectManagerListener.ERROR:
                    receiver.onError();
            }
        }
    }

    public void invokeOfflineMsgReceiver(List<MessageEntity> messageEntities) {
        int count = mMessageReceiverList.size();
        for (int i = count - 1; i >= 0; i--) {
            IMMessageReceiver receiver = mMessageReceiverList.get(i);
            receiver.getType().isProcessed = receiver.onOfflineMsgReceiver(messageEntities);
            this.mLaterIMMessageReceiver = receiver;
        }

        this.mLaterIMMessageReceiver = null;
    }

    private boolean filterMessageBody(MessageBody messageBody) {
        if (messageBody == null) {
            return true;
        }
        if (com.edusoho.kuozhi.imserver.ui.entity.PushUtil.ChatMsgType.PUSH.equals(messageBody.getType())) {
            Source source = messageBody.getSource();
            if (Destination.CLASSROOM.equals(source.getType()) || Destination.COURSE.equals(source.getType())) {
                return true;
            }
        }

        return false;
    }

    public void invokeReceiver(MessageEntity messageEntity) {
        int count = mMessageReceiverList.size();
        for (int i = count - 1; i >= 0; i--) {
            IMMessageReceiver receiver = mMessageReceiverList.get(i);
            if ("success".equals(messageEntity.getCmd())) {
                receiver.onSuccess(messageEntity.getMsg());
                continue;
            }
            if (!Destination.GLOBAL.equals(receiver.getType().msgType)
                    && filterMessageBody(new MessageBody(messageEntity))) {
                continue;
            }
            receiver.getType().isProcessed = receiver.onReceiver(messageEntity);
            this.mLaterIMMessageReceiver = receiver;
        }

        this.mLaterIMMessageReceiver = null;
    }

    public IMConvManager getConvManager() {
        return new IMConvManager(mContext);
    }

    public IMMessageManager getMessageManager() {
        return new IMMessageManager(mContext);
    }

    public IMRoleManager getRoleManager() {
        return new IMRoleManager(mContext);
    }

    public boolean isHandleMessageInFront(String msgType, String convNo) {
        ReceiverInfo receiverInfo = null;
        if (mLaterIMMessageReceiver == null || (receiverInfo = mLaterIMMessageReceiver.getType()) == null) {
            return false;
        }

        return msgType.equals(receiverInfo.msgType) && convNo.equals(receiverInfo.convNo);
    }

    public static IMClient getClient() {
        synchronized (mLock) {
            if (client == null) {
                client = new IMClient();
            }
        }

        return client;
    }

    private class ConnectIMServiceRunnable implements Runnable {

        private int mClientId;
        private String mClientName;
        private String[] mHostList;
        private String[] mIgnoreNosList;

        public ConnectIMServiceRunnable(int clientId, String clientName, ArrayList<String> ignoreNosList, ArrayList<String> hostList) {
            this.mClientId = clientId;
            this.mClientName = clientName;
            this.mHostList = new String[hostList.size()];
            this.mIgnoreNosList = new String[ignoreNosList.size()];
            ignoreNosList.toArray(mIgnoreNosList);
            hostList.toArray(mHostList);
        }

        @Override
        public void run() {
            connectService(mClientId, mClientName, mIgnoreNosList, mHostList);
        }
    }
}
