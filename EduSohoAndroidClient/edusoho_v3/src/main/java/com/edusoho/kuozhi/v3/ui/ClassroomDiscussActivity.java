package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.provider.ClassRoomProvider;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.ArrayList;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by JesseHuang on 15/10/16.
 */
public class ClassroomDiscussActivity extends ImChatActivity {

    private String mClassroomName;

    @Override
    public void initData() {
        mClassroomName = getIntent().getStringExtra(Const.ACTIONBAR_TITLE);
        super.initData();
    }

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
                            loadDialog.dismiss();
                            finish();
                            return;
                        }

                        String convNo = classroom.conversationId;
                        if (convNoIsEmpty(convNo) || convNo.equals(mConversationNo)) {
                            ToastUtils.show(getBaseContext(), "该班级不支持聊天!");
                            loadDialog.dismiss();
                            finish();
                            return;
                        }
                        mConversationNo = convNo;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.group_profile) {
            mActivity.app.mEngine.runNormalPlugin("ClassroomDetailActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(Const.ACTIONBAR_TITLE, mClassroomName);
                    startIntent.putExtra(ChatItemBaseDetail.CONV_NO, mConversationNo);
                    startIntent.putExtra(Const.FROM_ID, mFromId);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected ArrayList<Chat> getChatList(int start) {
        return super.getChatList(start);
    }
}
