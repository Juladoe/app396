package com.edusoho.kuozhi.v3.handler;

import android.os.Bundle;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.model.bal.push.BaseMsgEntity;
import com.edusoho.kuozhi.v3.model.bal.push.ClassroomDiscussEntity;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.ClassroomDiscussDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.Gson;

/**
 * Created by howzhi on 15/11/2.
 */
public class ClassRoomChatSendHandler extends ChatSendHandler {

    private ClassroomDiscussDataSource mClassroomDiscussDataSource;

    public ClassRoomChatSendHandler(BaseActivity activity, RedirectBody redirectBody) {
        super(activity, redirectBody);
        mClassroomDiscussDataSource = new ClassroomDiscussDataSource(
                SqliteChatUtil.getSqliteChatUtil(mActivity.getBaseContext(), app.domain));
    }

    private ClassroomDiscussEntity updateChatData(int toId, String content) {
        ClassroomDiscussEntity classroomDiscussEntity = createSendMsgCustomContent(toId, content);
        int discussId = (int) mClassroomDiscussDataSource.create(classroomDiscussEntity);
        classroomDiscussEntity.discussId = discussId;

        return classroomDiscussEntity;
    }

    @Override
    protected void sendMessage(int toId, String title, String avatar, RedirectBody body) {
        ClassroomDiscussEntity classroomDiscussEntity = updateChatData(toId, new Gson().toJson(body));
        WrapperXGPushTextMessage message = updateNewsList(title, avatar, classroomDiscussEntity);
        redirectMessageToUser(classroomDiscussEntity, message);
    }

    private WrapperXGPushTextMessage updateNewsList(String classRoomTitle, String classRoomAvatar, ClassroomDiscussEntity model) {

        WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
        message.setTitle(classRoomTitle);
        message.setContent(model.content);
        V2CustomContent v2CustomContent = getV2CustomContent(classRoomAvatar, PushUtil.ChatMsgType.MULTI, model);
        String v2CustomContentJson = new Gson().toJson(v2CustomContent);
        message.setCustomContentJson(v2CustomContentJson);
        message.isForeground = true;

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.GET_PUSH_DATA, message);
        bundle.putInt(Const.ADD_CHAT_MSG_DESTINATION, NewsFragment.HANDLE_SEND_CLASSROOM_DISCUSS_MSG);
        app.sendMsgToTarget(Const.ADD_MSG, bundle, NewsFragment.class);

        return message;
    }

    private String getBusinessType() {
        String[] roles = new String[app.loginUser.roles.length];
        for (int i = 0; i < app.loginUser.roles.length; i++) {
            roles[i] = app.loginUser.roles[i].toString();
        }
        if (CommonUtil.inArray(UserRole.ROLE_TEACHER.name(), roles)) {
            return PushUtil.ChatUserType.TEACHER;
        } else {
            return PushUtil.ChatUserType.FRIEND;
        }
    }

    private V2CustomContent getV2CustomContent(String classRoomAvatar, String type, ClassroomDiscussEntity modle) {
        V2CustomContent v2CustomContent = new V2CustomContent();
        V2CustomContent.FromEntity fromEntity = new V2CustomContent.FromEntity();
        fromEntity.setId(app.loginUser.id);
        fromEntity.setImage(app.loginUser.mediumAvatar);
        fromEntity.setNickname(app.loginUser.nickname);
        fromEntity.setType(getBusinessType());
        v2CustomContent.setFrom(fromEntity);
        V2CustomContent.ToEntity toEntity = new V2CustomContent.ToEntity();
        toEntity.setId(modle.classroomId);
        toEntity.setImage(classRoomAvatar);

        toEntity.setType(PushUtil.ChatUserType.CLASSROOM);
        v2CustomContent.setTo(toEntity);
        V2CustomContent.BodyEntity bodyEntity = new V2CustomContent.BodyEntity();
        bodyEntity.setType(type);
        bodyEntity.setContent(modle.content);
        v2CustomContent.setBody(bodyEntity);
        v2CustomContent.setV(Const.PUSH_VERSION);
        v2CustomContent.setCreatedTime(modle.createdTime);
        return v2CustomContent;
    }

    private ClassroomDiscussEntity createSendMsgCustomContent(int toId, String content) {
        int sendTime = (int) (System.currentTimeMillis() / 1000);
        ClassroomDiscussEntity model = new ClassroomDiscussEntity(0, toId, app.loginUser.id, app.loginUser.nickname, app.loginUser.mediumAvatar,
                content, app.loginUser.id, PushUtil.ChatMsgType.MULTI, PushUtil.MsgDeliveryType.UPLOADING, sendTime);

        return model;
    }

    @Override
    protected void updateChatStatus(BaseMsgEntity entity, int status, Bundle bundle) {
        ClassroomDiscussEntity model = (ClassroomDiscussEntity) entity;
        model.delivery = status;
        mClassroomDiscussDataSource.update(model);
        bundle.putInt(ChatActivity.MSG_DELIVERY, status);
        app.sendMsgToTarget(Const.ADD_CLASSROOM_MSG, bundle, ClassroomDiscussEntity.class);
    }
}
