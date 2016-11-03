package com.edusoho.kuozhi.v3.ui.live;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.ui.IMessageListView;
import com.edusoho.kuozhi.imserver.ui.data.IMessageDataProvider;
import com.edusoho.kuozhi.imserver.ui.entity.PushUtil;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.im.LiveMessageBody;
import com.edusoho.kuozhi.v3.model.provider.LiveRoomProvider;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by suju on 16/10/18.
 */
public class LiveChatPresenterImpl implements ILiveChatPresenter {

    private Bundle mLiveData;
    private Context mContext;
    private LiveImClient mLiveImClient;
    private IMessageDataProvider mIMessageDataProvider;
    private IMessageListView mIMessageListView;
    private Map<Long, Boolean> mMessageFilterMap;

    public LiveChatPresenterImpl(Context context, Bundle liveData, LiveImClient liveImClient, IMessageDataProvider dataProvider) {
        this.mContext = context;
        this.mLiveData = liveData;
        this.mLiveImClient = liveImClient;
        this.mIMessageDataProvider = dataProvider;
        mMessageFilterMap = new HashMap<>();
    }

    @Override
    public void setView(IMessageListView view) {
        this.mIMessageListView = view;
    }

    @Override
    public void onHandleMessage(MessageEntity message) {
        String clientId = mLiveData.get("clientId").toString();
        String clientName = mLiveData.get("clientName").toString();

        if (clientId.equals(message.getFromId()) && clientName.equals(message.getFromName())) {
            return;
        }
        LiveMessageBody messageBody = new LiveMessageBody(message);
        long messageTime = messageBody.getTime();
        if (mMessageFilterMap.containsKey(messageTime)) {
            return;
        }
        mMessageFilterMap.put(messageTime, true);
        mIMessageListView.insertMessage(message);
    }

    public void onSuccess(MessageEntity successEntity) {
        int index = AppUtil.parseInt(successEntity.getUid());
        MessageEntity messageEntity = mIMessageDataProvider.getMessageByUID(String.valueOf(index));
        if (messageEntity == null) {
            return;
        }
        messageEntity.setStatus(MessageEntity.StatusType.SUCCESS);
        mIMessageListView.updateMessageEntity(messageEntity);
    }

    @Override
    public void setUserCanChatStatus(MessageEntity message) {
        LiveMessageBody liveMessageBody = new LiveMessageBody(message.getMsg());
        try {
            JSONObject jsonObject = new JSONObject(liveMessageBody.getData());
            if (jsonObject.has("isAllCanChat")) {
                mIMessageListView.setEnable(jsonObject.optBoolean("isAllCanChat"));
                return;
            }
            if (jsonObject.has("isCanChat")) {
                String clientId = mLiveData.get("clientId").toString();
                if (AppUtil.parseInt(clientId) != jsonObject.optInt("clientId")) {
                    return;
                }
                mIMessageListView.setEnable(jsonObject.optBoolean("isCanChat"));
                return;
            }
        } catch (JSONException e) {
        }
    }

    @Override
    public void onReplace() {
        MessageEngine.getInstance().sendMsg(Const.TOKEN_LOSE, null);
        mIMessageListView.onUserKicked();
    }

    public void checkClientIsBan(final String clientId) {
        String roomNo = mLiveData.get("roomNo").toString();
        String token = mLiveData.get("token").toString();
        String liveHost = mLiveData.get("liveHost").toString();
        new LiveRoomProvider(mContext).getLiveChatBannedList(liveHost, token, roomNo)
        .success(new NormalCallback<ArrayList>() {
            @Override
            public void success(ArrayList bannedList) {
                mIMessageListView.setEnable(!findClientIdInArray(clientId, bannedList));
            }
        });
    }

    private boolean findClientIdInArray(String clientId, ArrayList<LinkedHashMap> bannedList) {
        for (LinkedHashMap bannedMap : bannedList) {
            if (clientId.equals(bannedMap.get("clientId").toString())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void joinLiveChatRoom() {
        String joinToken = mLiveData.get("joinToken").toString();
        final String clientId = mLiveData.get("clientId").toString();
        if (!TextUtils.isEmpty(joinToken)) {
            checkClientIsBan(clientId);
            joinConversation(joinToken);
            return;
        }
        String roomNo = mLiveData.get("roomNo").toString();
        String token = mLiveData.get("token").toString();
        String liveHost = mLiveData.get("liveHost").toString();
        new LiveRoomProvider(mContext).getLiveChatServer(liveHost, token, roomNo).success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap data) {
                String joinToken = null;
                if (data == null || TextUtils.isEmpty((joinToken = data.get("joinToken").toString()))) {
                    return;
                }
                checkClientIsBan(clientId);
                joinConversation(joinToken);
            }
        });
    }

    private void joinConversation(String joinToken) {
        try {
            String conversationNo = mLiveData.get("convNo").toString();
            mLiveImClient.getImBinder().joinConversation(joinToken, conversationNo);
        } catch (RemoteException e) {
            Log.i("joinLiveChatRoom", "join error");
        }
    }
}
