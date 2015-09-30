package com.edusoho.kuozhi.v3.handler;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.result.CloudResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.dialog.RedirectPreViewDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by howzhi on 15/9/30.
 */
public class ChatSendHandler {

    public static final int REQUEST_SELECT_FRIEND = 0010;
    public static final int RESULT_SELECT_FRIEND_OK = 0020;

    //must Activity
    private BaseActivity mActivity;
    private EdusohoApp app;

    private RedirectBody mRedirectBody;
    private ChatDataSource mChatDataSource;
    private NormalCallback mFinishCallback;

    public ChatSendHandler(BaseActivity activity, RedirectBody redirectBody)
    {
        mActivity = activity;
        mRedirectBody = redirectBody;
        app = mActivity.app;
        mChatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mActivity.getBaseContext(), app.domain));
    }

    public void setFinishCallback(NormalCallback finishCallback) {
        mFinishCallback = finishCallback;
    }

    public void handleClick(final int toId, final String title, final String avatar) {
        RedirectPreViewDialog dialog = RedirectPreViewDialog.getBuilder(mActivity)
                .setLayout(R.layout.redirect_preview_layout)
                .setTitle(mRedirectBody.title)
                .setBody(mRedirectBody.content)
                .setIconByUri(mRedirectBody.image)
                .build();
        dialog.show();
        dialog.setButtonClickListener(new PopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button) {
                if (button == PopupDialog.OK) {
                    sendMessage(toId, title, avatar, mRedirectBody);
                }
            }
        });
    }

    private Chat updateChatData(int toId, String content, int createdTime) {
        Chat chat = new Chat(app.loginUser.id, toId, app.loginUser.nickname, app.loginUser.mediumAvatar,
                content, Chat.FileType.MULTI.toString().toLowerCase(), createdTime);
        chat.direct = Chat.Direct.SEND;
        chat.setDelivery(Chat.Delivery.UPLOADING);
        chat.headimgurl = app.loginUser.mediumAvatar;
        chat.chatId = (int) mChatDataSource.create(chat);

        return chat;
    }

    private void sendMessage(int toId, String title, String avatar, RedirectBody body) {

        CustomContent customContent = createSendMsgCustomContent(toId, title, avatar);
        Gson gson = new Gson();
        String content = gson.toJson(body);

        Chat chat = updateChatData(toId, content, customContent.getCreatedTime());
        WrapperXGPushTextMessage message = updateNewsList(customContent, content);
        redirectMessageToUser(customContent, chat, message);
    }

    private WrapperXGPushTextMessage updateNewsList(CustomContent customContent, String content) {
        WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
        message.setTitle(customContent.getNickname());
        message.setContent(content);
        message.setCustomContentJson(new Gson().toJson(customContent));
        message.isForeground = true;

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.GET_PUSH_DATA, message);
        bundle.putInt(Const.ADD_CHAT_MSG_TYPE, NewsFragment.HANDLE_SEND_MSG);
        app.sendMsgToTarget(Const.ADD_CHAT_MSG, bundle, NewsFragment.class);

        return message;
    }

    private void redirectMessageToUser(CustomContent customContent, final Chat chat, WrapperXGPushTextMessage message) {
        int toId = customContent.getFromId();
        customContent.setFromId(app.loginUser.id);
        customContent.setNickname(app.loginUser.nickname);
        customContent.setImgUrl(app.loginUser.mediumAvatar);

        RequestUrl requestUrl = app.bindPushUrl(String.format(Const.SEND, app.loginUser.id, toId));
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", app.loginUser.nickname);
        params.put("type", Chat.FileType.MULTI.getName());
        params.put("content", chat.content);
        params.put("custom", new Gson().toJson(customContent));

        final Bundle bundle = new Bundle();
        bundle.putSerializable(Const.GET_PUSH_DATA, message);
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sendMessage", response);
                CloudResult result = app.parseJsonValue(response, new TypeToken<CloudResult>() {
                });

                Chat.Delivery status = Chat.Delivery.FAILED;
                if (result != null && result.getResult()) {
                    chat.id = result.id;
                    status = Chat.Delivery.SUCCESS;
                }
                updateChatStatus(chat, status, bundle);
                if (mFinishCallback != null) {
                    mFinishCallback.success(null);
                }
                mActivity.finish();
                CommonUtil.longToast(mActivity.getBaseContext(), "分享成功");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtil.longToast(mActivity.getBaseContext(), "网络连接不可用请稍后再试");
                updateChatStatus(chat, Chat.Delivery.FAILED, bundle);
            }
        });
    }

    private CustomContent createSendMsgCustomContent(int toId, String title, String avatar) {
        CustomContent customContent = new CustomContent();
        customContent.setFromId(toId);
        customContent.setNickname(title);
        customContent.setImgUrl(avatar);
        customContent.setTypeMsg(Chat.FileType.MULTI.getName());
        customContent.setCreatedTime((int) (System.currentTimeMillis() / 1000));
        customContent.setTypeBusiness(TypeBusinessEnum.FRIEND.getName());

        return customContent;
    }

    private void updateChatStatus(Chat chat, Chat.Delivery status, Bundle bundle) {
        chat.setDelivery(status);
        mChatDataSource.update(chat);
        bundle.putInt(ChatActivity.MSG_DELIVERY, status.getIndex());
        app.sendMsgToTarget(Const.UPDATE_CHAT_MSG, bundle, ChatActivity.class);
    }
}
