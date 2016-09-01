package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.ui.listener.MessageControllerListener;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.provider.ClassRoomProvider;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 15/10/16.
 */
public class ClassroomDiscussActivity extends ImChatActivity {

    public static final int CLEAR = 0x10;

    private Classroom mClassRoom;

    @Override
    protected String getTargetType() {
        return Destination.CLASSROOM;
    }

    @Override
    protected Promise createChatConvNo() {
        final Promise promise = new Promise();
        User currentUser = getAppSettingProvider().getCurrentUser();
        if (currentUser == null || currentUser.id == 0) {
            ToastUtils.show(getBaseContext(), "用户未登录");
            promise.resolve(null);
            return promise;
        }

        new ClassRoomProvider(mContext).getClassRoom(mTargetId)
                .success(new NormalCallback<Classroom>() {
                    @Override
                    public void success(Classroom classroom) {
                        if (classroom == null || TextUtils.isEmpty(classroom.conversationId)) {
                            ToastUtils.show(getBaseContext(), "加入班级聊天失败!");
                            finish();
                            return;
                        }
                        mClassRoom = classroom;
                        promise.resolve(classroom.conversationId);
                    }
                });

        return promise;
    }

    @Override
    protected void createTargetRole(String type, int rid, final MessageControllerListener.RoleUpdateCallback callback) {
        if (Destination.CLASSROOM.equals(type)) {
            new IMProvider(mContext).createConvInfoByClassRoom(mConversationNo, mClassRoom)
                    .success(new NormalCallback<User>() {
                        @Override
                        public void success(User user) {
                            Role role = new Role();
                            role.setRid(user.id);
                            role.setAvatar(user.mediumAvatar);
                            role.setType(Destination.CLASSROOM);
                            role.setNickname(user.nickname);
                            callback.onCreateRole(role);
                        }
                    });
            return;
        }
        super.createTargetRole(type, rid, callback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.group_profile) {
            CoreEngine.create(mContext).runNormalPlugin("ClassroomDetailActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(Const.ACTIONBAR_TITLE, mTargetName);
                    startIntent.putExtra(ChatItemBaseDetail.CONV_NO, mConversationNo);
                    startIntent.putExtra(Const.FROM_ID, mTargetId);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{new MessageType(CLEAR)};
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        if (message.type.code == CLEAR) {
            mMessageListFragment.reload();
        }
    }
}
