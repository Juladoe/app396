package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.ChatAdapter;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class ChatActivity extends ActionBarBaseActivity {

    public static final int COURSE_CHAT = 0x01;
    public static final String CHAT_DATA = "chat_data";
    public static final String NEW_ID = "new_id";
    public New mNewsItem;

    private EditText etSend;
    private ListView lvMessage;
    private TextView tvSend;
    private ChatAdapter mAdapter;
    private List<Chat> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setBackMode(BACK, "suju");
        initView();
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
        int newId = intent.getIntExtra(NEW_ID, 0);
        ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        mList = chatDataSource.getChats(0, 15, "NEWID = " + newId);
        mAdapter = new ChatAdapter(mContext, mList);
        lvMessage.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            mNewsItem = (New) intent.getSerializableExtra(CHAT_DATA);
//            String courseId = intent.getStringExtra(COURSE_ID);
//            CommonUtil.longToast(mActivity, courseId);
        }
        if (mXGClick != null) {
            CommonUtil.longToast(this, "通知被点击:" + mXGClick.toString());
        }
    }

    View.OnClickListener mSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = "http://192.168.10.125/mapi_v2/User/sendPushMsg";
            RequestUrl requestUrl = new RequestUrl(url);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("toId", String.valueOf(mNewsItem.title));
            params.put("title", String.valueOf(mNewsItem.title));
            params.put("content", etSend.getText().toString());
            mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, null);
        }
    };

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (messageType.code == Const.CHAT_MSG) {
            Chat chat = (Chat) message.data.get(CHAT_DATA);
            mAdapter.addOneChat(chat);
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{new MessageType(Const.CHAT_MSG, source)};
        return messageTypes;
    }
}
