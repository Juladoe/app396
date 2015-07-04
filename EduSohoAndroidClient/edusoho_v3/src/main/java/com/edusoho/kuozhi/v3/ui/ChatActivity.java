package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.ChatAdapter;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.ChatTypeEnum;
import com.edusoho.kuozhi.v3.model.bal.push.CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.result.PushResult;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class ChatActivity extends ActionBarBaseActivity {
    public static final String TAG = "ChatActivity";
    public static final int COURSE_CHAT = 0x01;
    public static final String CHAT_DATA = "chat_data";
    public static final String FROM_ID = "from_id";
    public static final String TITLE = "title";

    private EditText etSend;
    private ListView lvMessage;
    private TextView tvSend;
    private ChatAdapter mAdapter;
    private List<Chat> mList;
    /**
     * 对方的userInfo信息;
     */
    private User mFromUserInfo;
    private int mFromId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        Log.d(TAG, this.getTaskId() + "");
    }

    private void initView() {
        etSend = (EditText) findViewById(R.id.et_send_content);
        tvSend = (TextView) findViewById(R.id.tv_send);
        tvSend.setOnClickListener(mSendClickListener);
        lvMessage = (ListView) findViewById(R.id.lv_messages);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "聊天记录读取错误");
            return;
        }
        setBackMode(BACK, intent.getStringExtra(TITLE));
        mFromId = intent.getIntExtra(FROM_ID, 0);
        NotificationUtil.cancelById(mFromId);
        int toId = app.loginUser.id;
        ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openRead();
        String selectSql = String.format("(FROMID = %d AND TOID=%d) OR (TOID=%d AND FROMID=%d)", mFromId, toId, mFromId, toId);
        mList = chatDataSource.getChats(0, 15, selectSql);
        Collections.reverse(mList);
        mAdapter = new ChatAdapter(mContext, mList);
        lvMessage.setAdapter(mAdapter);
    }

    View.OnClickListener mSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mFromUserInfo != null) {
                sendMsg();

            } else {
                RequestUrl requestUrl = app.bindUrl(Const.USERINFO, false);
                HashMap<String, String> params = requestUrl.getParams();
                params.put("userId", mFromId + "");
                ajaxPost(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mFromUserInfo = parseJsonValue(response, new TypeToken<User>() {
                        });
                        sendMsg();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonUtil.longToast(mContext, "无法获取对方信息");
                    }
                });
            }
        }
    };

    private void sendMsg() {
        RequestUrl requestUrl = app.bindPushUrl(String.format(Const.SEND, app.loginUser.id, mFromId));
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", app.loginUser.nickname);
        params.put("type", "text");
        params.put("content", etSend.getText().toString());
        params.put("custom", gson.toJson(getCustomContent()));
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                PushResult result = parseJsonValue(response, new TypeToken<PushResult>() {
                });
                if (result.equals("success")) {

                }
            }
        }, null);
    }

    private CustomContent getCustomContent() {
        CustomContent customContent = new CustomContent();
        customContent.fromId = app.loginUser.id;
        customContent.nickname = app.loginUser.nickname;
        customContent.imgUrl = app.loginUser.smallAvatar;
        customContent.typeMsg = ChatTypeEnum.TEXT.toString().toLowerCase();
        customContent.typeObject = ChatTypeEnum.FRIEND.toString().toLowerCase();
        customContent.createdTime = (int) Calendar.getInstance().getTimeInMillis();
        return customContent;
    }

    @Override
    public void invoke(WidgetMessage message) {
        try {
            MessageType messageType = message.type;
            if (messageType.code == Const.ADD_CHAT_MSG) {
                WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(CHAT_DATA);
                Chat chat = new Chat(wrapperMessage);
                mAdapter.addOneChat(chat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{new MessageType(Const.ADD_CHAT_MSG, source)};
        return messageTypes;
    }
}
