package com.edusoho.kuozhi.v3.ui.live;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.managar.IMConvManager;
import com.edusoho.kuozhi.imserver.managar.IMRoleManager;
import com.edusoho.kuozhi.imserver.ui.IMessageListView;
import com.edusoho.kuozhi.imserver.ui.MessageListPresenterImpl;
import com.edusoho.kuozhi.imserver.ui.data.IMessageDataProvider;
import com.edusoho.kuozhi.imserver.ui.helper.MessageResourceHelper;
import com.edusoho.kuozhi.imserver.ui.view.MessageInputView;
import com.edusoho.kuozhi.imserver.util.MessageEntityBuildr;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.im.LiveMessageBody;
import com.edusoho.kuozhi.v3.model.provider.LiveRoomProvider;
import com.edusoho.kuozhi.v3.model.provider.UserProvider;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by suju on 16/10/18.
 */
public class LiveChatMessageListPresenterImpl extends MessageListPresenterImpl {

    private Bundle mLiveData;
    private Context mContext;
    private String mConversationNo;
    private Map<Long, Boolean> mMessageFilterMap;
    private Map<String, MessageBody> mNotSendMessageMap;

    public LiveChatMessageListPresenterImpl(
                                            Context context,
                                            Bundle params,
                                            IMConvManager convManager,
                                            IMRoleManager roleManager,
                                            MessageResourceHelper messageResourceHelper,
                                            IMessageDataProvider mIMessageDataProvider,
                                            IMessageListView messageListView) {
        super(params, convManager, roleManager, messageResourceHelper, mIMessageDataProvider, messageListView);
        this.mContext = context;
        mMessageFilterMap = new HashMap<>();
        mNotSendMessageMap = new ConcurrentHashMap<>();
        messageListView.setInputTextMode(MessageInputView.INPUT_TEXT);
    }

    public void setLiveData(Bundle liveData) {
        this.mLiveData = liveData;
        mConversationNo = liveData.get("convNo").toString();
        String clientId = mLiveData.get("clientId").toString();
        String clientName = mLiveData.get("clientName").toString();
        setClientInfo(AppUtil.parseInt(clientId), clientName);
    }

    @Override
    protected Map<String, String> getRequestHeaders() {
        HashMap<String, String> map = new HashMap();
        String token = ApiTokenUtil.getApiToken(mContext);
        map.put("Auth-Token", TextUtils.isEmpty(token) ? "" : token);
        return map;
    }

    @Override
    protected void createRole(String type, int rid, MessageListPresenterImpl.RoleUpdateCallback callback) {
        createTargetRole(type, rid, callback);
    }

    @Override
    protected void createConvNo(MessageListPresenterImpl.ConvNoCreateCallback convNoCreateCallback) {
        convNoCreateCallback.onCreateConvNo(mConversationNo);
    }

    protected void createTargetRole(String type, int rid, final MessageListPresenterImpl.RoleUpdateCallback callback) {
        new UserProvider(mContext).getUserInfo(rid)
                .success(new NormalCallback<User>() {
                    @Override
                    public void success(User user) {
                        Role role = new Role();
                        if (user == null) {
                            callback.onCreateRole(role);
                            return;
                        }
                        role.setRid(user.id);
                        role.setAvatar(user.mediumAvatar);
                        role.setType(Destination.USER);
                        role.setNickname(user.nickname);
                        callback.onCreateRole(role);
                    }
                });
    }

    @Override
    public void addMessageReceiver() {
    }

    @Override
    public void removeReceiver() {
    }

    @Override
    public boolean canRefresh() {
        return false;
    }

    @Override
    public void sendMessageToServer(MessageBody messageBody) {
        mNotSendMessageMap.put(String.valueOf(messageBody.getMid()), messageBody);
        super.sendMessageToServer(messageBody);
    }

    @Override
    public void reSendMessageList() {
        if (mNotSendMessageMap.isEmpty()) {
            return;
        }
        for (MessageBody messageBody : mNotSendMessageMap.values()) {
            Log.d("MessageListPresenter", "resend:" + messageBody.getMessageId());
            sendMessageToServer(messageBody);
        }
    }

    @Override
    public boolean onReceiverMessageEntity(MessageEntity message) {
        String clientId = mLiveData.get("clientId").toString();
        String clientName = mLiveData.get("clientName").toString();

        if (clientId.equals(message.getFromId()) && clientName.equals(message.getFromName())) {
            return true;
        }
        LiveMessageBody messageBody = new LiveMessageBody(message);
        long messageTime = messageBody.getTime();
        if (mMessageFilterMap.containsKey(messageTime)) {
            return true;
        }
        mMessageFilterMap.put(messageTime, true);
        mIMessageListView.insertMessage(message);
        return true;
    }

    @Override
    public void onMessageSuccess(MessageEntity successEntity) {
        int index = AppUtil.parseInt(successEntity.getUid());
        MessageEntity messageEntity = mIMessageDataProvider.getMessageByUID(String.valueOf(index));
        if (messageEntity == null) {
            return;
        }
        mNotSendMessageMap.remove(String.valueOf(index));
        messageEntity.setStatus(MessageEntity.StatusType.SUCCESS);
        mIMessageListView.updateMessageEntity(messageEntity);
    }
}
