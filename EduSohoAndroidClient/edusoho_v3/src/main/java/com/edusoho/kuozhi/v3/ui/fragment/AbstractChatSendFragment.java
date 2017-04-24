package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.ContentValues;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.SendEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.entity.message.Source;
import com.edusoho.kuozhi.imserver.util.MessageEntityBuildr;
import com.edusoho.kuozhi.imserver.util.SendEntityBuildr;
import com.edusoho.kuozhi.v3.entity.error.Error;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.UtilFactory;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.PushUtil;

import java.util.LinkedHashMap;
import java.util.UUID;
import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by suju on 16/8/19.
 */
public abstract class AbstractChatSendFragment extends BaseFragment {

    protected RedirectBody mRedirectBody;

    protected MessageBody createSendMessageBody(int fromId, String convNo, String type, String title) {
        User currentUser = getAppSettingProvider().getCurrentUser();
        MessageBody messageBody = createMessageBody();
        messageBody.setCreatedTime(System.currentTimeMillis());
        messageBody.setDestination(new Destination(fromId, type));
        messageBody.getDestination().setNickname(title);
        messageBody.setSource(new Source(currentUser.id, Destination.USER));
        messageBody.getSource().setNickname(currentUser.nickname);
        messageBody.setConvNo(convNo);
        messageBody.setMessageId(UUID.randomUUID().toString());
        return messageBody;
    }

    private MessageBody createMessageBody() {
        switch (mRedirectBody.type) {
            case PushUtil.ChatMsgType.TEXT:
            case PushUtil.ChatMsgType.IMAGE:
            case PushUtil.ChatMsgType.AUDIO:
                return new MessageBody(MessageBody.VERSION, mRedirectBody.type, mRedirectBody.content);
        }
        return new MessageBody(MessageBody.VERSION, PushUtil.ChatMsgType.MULTI, getUtilFactory().getJsonParser().jsonToString(mRedirectBody));
    }

    private MessageEntity createMessageEntityByBody(MessageBody messageBody) {
        return new MessageEntityBuildr()
                .addUID(messageBody.getMessageId())
                .addConvNo(messageBody.getConvNo())
                .addToId(String.valueOf(messageBody.getDestination().getId()))
                .addToName(messageBody.getDestination().getNickname())
                .addFromId(String.valueOf(messageBody.getSource().getId()))
                .addFromName(messageBody.getSource().getNickname())
                .addCmd("message")
                .addMsg(messageBody.toJson())
                .addTime((int) (messageBody.getCreatedTime() / 1000))
                .builder();
    }

    protected void checkJoinedIM(String targetType, int targetId, final NormalCallback<String> callback) {
        new IMProvider(mContext).joinIMConvNo(targetId, targetType)
                .success(new NormalCallback<LinkedHashMap>() {
                    @Override
                    public void success(LinkedHashMap map) {
                        if (map == null) {
                            callback.success("加入会话失败");
                            return;
                        }
                        if (map.containsKey("error")) {
                            Error error = getUtilFactory().getJsonParser().fromJson(map.get("error").toString(), Error.class);
                            if (error != null) {
                                callback.success(error.message);
                            }
                            return;
                        }
                        callback.success(null);
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                callback.success("加入会话失败");
            }
        });
    }

    protected void sendMessageToServer(final String convNo, final MessageBody messageBody) {
        if (messageBody == null || messageBody.getDestination() == null) {
            ToastUtils.show(mContext, "发送失败");
            sendFailCallback();
            return;
        }
        checkJoinedIM(messageBody.getDestination().getType(), messageBody.getDestination().getId(), new NormalCallback<String>() {
            @Override
            public void success(String message) {
                if (TextUtils.isEmpty(message)) {
                    sendMessageBody(convNo, messageBody);
                    ToastUtils.show(mContext, "发送成功");
                    return;
                }
                ToastUtils.show(mContext, "发送失败");
                sendFailCallback();
            }
        });
    }

    private void sendMessageBody(String convNo, MessageBody messageBody) {
        try {
            String toId = "";
            switch (messageBody.getDestination().getType()) {
                case Destination.CLASSROOM:
                case Destination.COURSE:
                    toId = "all";
                    break;
                case Destination.USER:
                    toId = String.valueOf(messageBody.getDestination().getId());
            }

            SendEntity sendEntity = SendEntityBuildr.getBuilder()
                    .addToId(toId)
                    .addCmd("send")
                    .addMsg(messageBody.toJson())
                    .builder();
            IMClient.getClient().getChatRoom(convNo).send(sendEntity);
            sendSuccessCallback();

        } catch (Exception e) {
            ToastUtils.show(mContext, "发送失败");
            sendFailCallback();
        }
    }

    protected abstract void sendSuccessCallback();

    protected abstract void sendFailCallback();

    protected MessageBody saveMessageToLoacl(int fromId, String convNo, String type, String title) {
        MessageBody messageBody = createSendMessageBody(fromId, convNo, type, title);

        MessageEntity messageEntity = createMessageEntityByBody(messageBody);
        IMClient.getClient().getMessageManager().createMessage(messageEntity);
        updateConv(messageBody);
        return messageBody;
    }

    private void updateConv(MessageBody messageBody) {
        ContentValues cv = new ContentValues();
        cv.put("laterMsg", messageBody.toJson());
        cv.put("updatedTime", System.currentTimeMillis());
        IMClient.getClient().getConvManager().updateConvField(messageBody.getConvNo(), cv);
    }

    protected RedirectBody getShowRedirectBody(String title, String icon) {
        RedirectBody redirectBody = new RedirectBody();
        redirectBody.title = mRedirectBody.title;
        switch (mRedirectBody.fromType) {
            case Destination.USER:
                redirectBody.content = title;
                redirectBody.image = icon;
                break;
            default:
                redirectBody.content = mRedirectBody.content;
                redirectBody.image = mRedirectBody.image;
        }

        return redirectBody;
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }

    protected UtilFactory getUtilFactory() {
        return FactoryManager.getInstance().create(UtilFactory.class);
    }
}
