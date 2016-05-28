package com.edusoho.kuozhi.v3.handler;

import android.os.Bundle;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.model.bal.push.BaseMsgEntity;
import com.edusoho.kuozhi.v3.model.bal.push.ClassroomDiscussEntity;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.ClassroomDiscussDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;

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

    private ClassroomDiscussEntity createSendMsgCustomContent(int toId, String content) {
        int sendTime = (int) (System.currentTimeMillis() / 1000);
        ClassroomDiscussEntity model = new ClassroomDiscussEntity(0, toId, app.loginUser.id, app.loginUser.nickname, app.loginUser.mediumAvatar,
                content, app.loginUser.id, PushUtil.ChatMsgType.MULTI, PushUtil.MsgDeliveryType.UPLOADING, sendTime);

        return model;
    }

}
