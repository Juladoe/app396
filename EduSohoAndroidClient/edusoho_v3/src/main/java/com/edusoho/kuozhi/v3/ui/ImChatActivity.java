package com.edusoho.kuozhi.v3.ui;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.edusoho.kuozhi.v3.adapter.ChatAdapter;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.service.ImService;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by su on 2016/3/18.
 */
public class ImChatActivity extends ChatActivity {

    private ServiceConnection mServiceConnection;
    private ImService.ImBinder mImBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startIntent = getIntent();
        startIntent.putExtra(ChatActivity.FROM_ID, 0);
        startIntent.putExtra(Const.ACTIONBAR_TITLE, "test");
        startIntent.putExtra(Const.NEWS_TYPE, "chat");
        startIntent.putExtra(ChatActivity.HEAD_IMAGE_URL, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mServiceConnection != null) {
            return;
        }
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mImBinder = (ImService.ImBinder) service;
                Log.d(getClass().getSimpleName(), "----" + service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(getClass().getSimpleName(), name.toString());
            }
        };
        boolean result =  bindService(new Intent(this, ImService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(getClass().getSimpleName(), "" + result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    public void sendMsg(String content) {
        super.sendMsg(content);
        mImBinder.send(content);
    }

    public void addMsg(String msg) {
        Chat chat = new Chat();
    }
}
