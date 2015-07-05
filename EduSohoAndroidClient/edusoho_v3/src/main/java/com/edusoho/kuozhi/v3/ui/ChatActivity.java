package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.reflect.TypeToken;

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
    public static final int UPDATE_UNREAD = 0x02;

    private EditText etSend;
    private ListView lvMessage;
    private TextView tvSend;
    private ChatAdapter mAdapter;
    private List<Chat> mList;
    private ChatDataSource mChatDataSource;
    private int mSendTime;

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
    }

    private void initView() {
        etSend = (EditText) findViewById(R.id.et_send_content);
        tvSend = (TextView) findViewById(R.id.tv_send);
        tvSend.setOnClickListener(mSendClickListener);
        etSend.addTextChangedListener(msgTextWatcher);
        lvMessage = (ListView) findViewById(R.id.lv_messages);
        initData();
        mAdapter = new ChatAdapter(mContext, mList);
        lvMessage.setAdapter(mAdapter);
        sendNewFragment2UpdateItem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
        mAdapter.updateList(mList);
        sendNewFragment2UpdateItem();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "聊天记录读取错误");
            return;
        }
        mFromId = intent.getIntExtra(FROM_ID, mFromId);
        NotificationUtil.cancelById(mFromId);
        setBackMode(BACK, intent.getStringExtra(TITLE));
        int toId = app.loginUser.id;
        ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openRead();
        String selectSql = String.format("(FROMID = %d AND TOID=%d) OR (TOID=%d AND FROMID=%d)", mFromId, toId, mFromId, toId);
        mList = chatDataSource.getChats(0, 15, selectSql);
        Collections.reverse(mList);
        chatDataSource.close();
    }

    private void sendNewFragment2UpdateItem() {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.FROM_ID, mFromId);
        app.sendMsgToTarget(NewsFragment.UPDATE_UNREAD, bundle, NewsFragment.class);
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
                        etSend.setText("");
                        etSend.requestFocus();
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

    TextWatcher msgTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                etSend.setEnabled(false);
            } else {
                etSend.setEnabled(true);
            }
        }
    };

    private void sendMsg() {
        if (mChatDataSource == null) {
            mChatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openWrite();
        }
        final Chat chat = new Chat();
        mSendTime = (int) (System.currentTimeMillis() / 1000);
        chat.fromId = app.loginUser.id;
        chat.toId = mFromId;
        chat.nickName = app.loginUser.nickname;
        chat.headimgurl = app.loginUser.smallAvatar;
        chat.content = etSend.getText().toString();
        chat.type = ChatTypeEnum.TEXT.toString().toLowerCase();
        chat.createdTime = mSendTime;
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
                if (result.result.equals("success")) {
                    mChatDataSource.create(chat);
                    mAdapter.addOneChat(chat);
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
        customContent.createdTime = mSendTime;
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
