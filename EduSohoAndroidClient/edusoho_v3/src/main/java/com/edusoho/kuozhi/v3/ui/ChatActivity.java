package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class ChatActivity extends ActionBarBaseActivity {
    public static final String TAG = "ChatActivity";
    public static final String CHAT_DATA = "chat_data";
    public static final String FROM_ID = "from_id";
    public static final String TITLE = "title";

    private EditText etSend;
    private ListView lvMessage;
    private Button tvSend;
    private ChatAdapter mAdapter;
    private PtrClassicFrameLayout mPtrFrame;
    private ArrayList<Chat> mList;
    private ChatDataSource mChatDataSource;
    private int mSendTime;
    private int mStart = 0;
    private static final int LIMIT = 15;

    /**
     * 对方的userInfo信息;
     */
    private User mFromUserInfo;
    private int mFromId;
    private int mToId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
    }

    private void initView() {
        etSend = (EditText) findViewById(R.id.et_send_content);
        tvSend = (Button) findViewById(R.id.tv_send);
        tvSend.setOnClickListener(mSendClickListener);
        etSend.addTextChangedListener(msgTextWatcher);
        lvMessage = (ListView) findViewById(R.id.lv_messages);
        initData();
        mAdapter = new ChatAdapter(mContext, getChatList(0));
        lvMessage.setAdapter(mAdapter);
        mStart = mAdapter.getCount();
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mAdapter.addItems(getChatList(mStart));
                mStart = mAdapter.getCount();
                mPtrFrame.refreshComplete();
                lvMessage.post(new Runnable() {
                    @Override
                    public void run() {
                        lvMessage.setSelection(0);
                    }
                });
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        sendNewFragment2UpdateItem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
        mAdapter.clear();
        mAdapter.addItems(getChatList(0));
        mStart = mAdapter.getCount();
        sendNewFragment2UpdateItem();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "聊天记录读取错误");
            return;
        }
        mFromId = intent.getIntExtra(FROM_ID, mFromId);
        mToId = app.loginUser.id;
        NotificationUtil.cancelById(mFromId);
        setBackMode(BACK, intent.getStringExtra(TITLE));
    }

    private ArrayList<Chat> getChatList(int start) {
        ChatDataSource chatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain)).openRead();
        String selectSql = String.format("(FROMID = %d AND TOID=%d) OR (TOID=%d AND FROMID=%d)", mFromId, mToId, mFromId, mToId);
        mList = chatDataSource.getChats(start, LIMIT, selectSql);
        Collections.reverse(mList);
        chatDataSource.close();
        return mList;
    }

    private void sendNewFragment2UpdateItem() {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.FROM_ID, mFromId);
        app.sendMsgToTarget(NewsFragment.UPDATE_UNREAD, bundle, NewsFragment.class);
    }

    View.OnClickListener mSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (etSend.getText().length() == 0) {
                return;
            }
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

    TextWatcher msgTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!"".equals(s.toString().trim())) {
                tvSend.setBackground(getResources().getDrawable(R.drawable.send_btn_click));
                tvSend.setTextColor(getResources().getColor(android.R.color.white));
                tvSend.setEnabled(true);
            } else {
                tvSend.setBackground(getResources().getDrawable(R.drawable.send_btn_unclick));
                tvSend.setTextColor(getResources().getColor(R.color.grey_alpha));
                tvSend.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void sendMsg() {
        if (mChatDataSource == null) {
            mChatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
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
                    mChatDataSource.openWrite();
                    chat.id = result.id;
                    mChatDataSource.create(chat);
                    mChatDataSource.close();
                    mAdapter.addOneChat(chat);
                    etSend.setText("");
                    etSend.requestFocus();

                    WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
                    message.setTitle(chat.nickName);
                    message.setContent(chat.content);
                    CustomContent cc = getCustomContent();
                    cc.fromId = mFromId;
                    cc.imgUrl = mFromUserInfo.mediumAvatar;
                    message.setCustomContent(gson.toJson(cc));
                    message.isForeground = true;
                    notifyNewList2Update(message);
                }
            }
        }, null);
    }

    private void notifyNewList2Update(WrapperXGPushTextMessage message) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.CHAT_DATA, message);
        app.sendMsgToTarget(Const.ADD_CHAT_MSG, bundle, NewsFragment.class);

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
