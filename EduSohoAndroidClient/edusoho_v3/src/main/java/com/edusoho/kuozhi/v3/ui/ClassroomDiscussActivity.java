package com.edusoho.kuozhi.v3.ui;

import android.text.TextUtils;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.provider.ClassRoomProvider;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import java.util.ArrayList;
import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 15/10/16.
 */
public class ClassroomDiscussActivity extends ImChatActivity {

    @Override
    protected String getTargetType() {
        return Destination.CLASSROOM;
    }

    @Override
    protected void createChatConvNo() {
        final LoadDialog loadDialog = LoadDialog.create(this);
        loadDialog.show();
        new ClassRoomProvider(mContext).getClassRoom(mFromId)
        .success(new NormalCallback<Classroom>() {
            @Override
            public void success(Classroom classroom) {
                if (classroom == null || TextUtils.isEmpty(classroom.conversationId)) {
                    ToastUtils.show(getBaseContext(), "加入班级聊天失败!");
                    return;
                }

                mConversationNo = classroom.conversationId;
                new IMProvider(mContext).createConvInfoByClassRoom(mConversationNo, classroom)
                .success(new NormalCallback<ConvEntity>() {
                    @Override
                    public void success(ConvEntity convEntity) {
                        loadDialog.dismiss();
                        setTitle(convEntity.getTargetName());
                        initAdapter();
                    }
                });
            }
        });
    }

    @Override
    protected ArrayList<Chat> getChatList(int start) {
        return super.getChatList(start);
    }
}
