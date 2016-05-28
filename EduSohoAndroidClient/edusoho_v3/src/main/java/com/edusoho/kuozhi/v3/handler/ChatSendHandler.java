package com.edusoho.kuozhi.v3.handler;

import android.os.Bundle;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.push.BaseMsgEntity;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;

/**
 * Created by howzhi on 15/9/30.
 */
public class ChatSendHandler {

    public static final int REQUEST_SELECT_FRIEND = 0010;
    public static final int RESULT_SELECT_FRIEND_OK = 0020;

    //must Activity
    protected BaseActivity mActivity;
    protected EdusohoApp app;

    protected RedirectBody mRedirectBody;
    private ChatDataSource mChatDataSource;
    protected NormalCallback mFinishCallback;

    public ChatSendHandler(BaseActivity activity, RedirectBody redirectBody) {
        mActivity = activity;
        mRedirectBody = redirectBody;
        app = mActivity.app;
        mChatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mActivity.getBaseContext(), app.domain));
    }

    public void setFinishCallback(NormalCallback finishCallback) {
        mFinishCallback = finishCallback;
    }


    private Chat updateChatData(int toId, String content, int createdTime) {
        Chat chat = new Chat(app.loginUser.id, toId, app.loginUser.nickname, app.loginUser.mediumAvatar,
                content, PushUtil.ChatMsgType.MULTI.toLowerCase(), createdTime);
        chat.direct = Chat.Direct.SEND;
        chat.delivery = PushUtil.MsgDeliveryType.UPLOADING;
        chat.headImgUrl = app.loginUser.mediumAvatar;

        return chat;
    }


    private V2CustomContent getV2CustomContent(CustomContent customContent, BaseMsgEntity entity) {
        V2CustomContent v2CustomContent = new V2CustomContent();
        V2CustomContent.FromEntity fromEntity = new V2CustomContent.FromEntity();
        fromEntity.setType(customContent.getTypeBusiness());
        fromEntity.setId(app.loginUser.id);
        fromEntity.setImage(app.loginUser.mediumAvatar);

        v2CustomContent.setFrom(fromEntity);
        V2CustomContent.ToEntity toEntity = new V2CustomContent.ToEntity();
        toEntity.setId(customContent.getFromId());
        toEntity.setType(PushUtil.ChatUserType.USER);
        v2CustomContent.setTo(toEntity);
        V2CustomContent.BodyEntity bodyEntity = new V2CustomContent.BodyEntity();
        bodyEntity.setType(PushUtil.ChatMsgType.MULTI);
        bodyEntity.setContent(entity.content);
        v2CustomContent.setBody(bodyEntity);
        v2CustomContent.setV(2);
        v2CustomContent.setCreatedTime(customContent.getCreatedTime());

        return v2CustomContent;
    }

    private CustomContent createSendMsgCustomContent(int toId, String title, String avatar) {
        CustomContent customContent = new CustomContent();
        customContent.setFromId(toId);
        customContent.setNickname(title);
        customContent.setImgUrl(avatar);
        customContent.setTypeMsg(PushUtil.ChatMsgType.MULTI);
        customContent.setCreatedTime((int) (System.currentTimeMillis() / 1000));
        customContent.setTypeBusiness(TypeBusinessEnum.FRIEND.getName());

        return customContent;
    }

}
