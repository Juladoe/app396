package com.edusoho.kuozhi.v3.ui.live;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.managar.IMConvManager;
import com.edusoho.kuozhi.imserver.managar.IMRoleManager;
import com.edusoho.kuozhi.imserver.ui.IMessageListView;
import com.edusoho.kuozhi.imserver.ui.MessageListPresenterImpl;
import com.edusoho.kuozhi.imserver.ui.data.IMessageDataProvider;
import com.edusoho.kuozhi.imserver.ui.helper.MessageResourceHelper;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.provider.UserProvider;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by suju on 16/10/18.
 */
public class LiveChatMessageListPresenterImpl extends MessageListPresenterImpl {

    private Map mLiveData;
    private Context mContext;
    private String mConversationNo;

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
    }

    public void setLiveData(Map liveData) {
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
}
