package com.edusoho.kuozhi.v3.ui.live;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.ui.IMessageListView;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.im.LiveMessageBody;
import com.edusoho.kuozhi.v3.model.provider.LiveRoomProvider;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedHashMap;

/**
 * Created by suju on 16/10/18.
 */
public class LiveChatPresenterImpl implements ILiveChatPresenter {

    private Bundle mLiveData;
    private Context mContext;
    private LiveImClient mLiveImClient;
    private IMessageListView mIMessageListView;

    public LiveChatPresenterImpl(Context context, Bundle liveData, LiveImClient liveImClient) {
        this.mContext = context;
        this.mLiveData = liveData;
        this.mLiveImClient = liveImClient;
    }

    @Override
    public void setView(IMessageListView view) {
        this.mIMessageListView = view;
    }

    @Override
    public void onHandleMessage(MessageEntity message) {
        String clientId = mLiveData.get("clientId").toString();
        String ClientName = mLiveData.get("clientName").toString();
        if (clientId.equals(message.getFromId()) && ClientName.equals(message.getFromName())) {
            return;
        }

        mIMessageListView.insertMessage(message);
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
                if (!clientId.equals(jsonObject.optInt("clientId"))) {
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
        mIMessageListView.onUserKicked();
    }

    @Override
    public void joinLiveChatRoom() {
        String joinToken = mLiveData.get("joinToken").toString();
        if (!TextUtils.isEmpty(joinToken)) {
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
