package com.edusoho.kuozhi.v3.ui;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.ChatAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.model.bal.push.BaseMsgEntity;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.UpYunUploadResult;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.result.CloudResult;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseChatActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class ChatActivity extends BaseChatActivity implements ChatAdapter.ImageErrorClick {

    public static final String TAG = "ChatActivity";
    public static final String FROM_ID = "from_id";
    public static final String MSG_DELIVERY = "msg_delivery";
    public static final String HEAD_IMAGE_URL = "head_image_url";

    public static int CurrentFromId = 0;

    private ChatAdapter<Chat> mAdapter;
    private ChatDataSource mChatDataSource;
    private int mSendTime;
    private User mFromUserInfo;
    private int mFromId;
    private int mToId;

    /**
     * 对方的BusinessType,这里是Role
     */
    private String mType;

    /**
     * 自己的BusinessType,这里是Role
     */
    private String mMyType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mAudioDownloadReceiver, intentFilter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
        mAdapter.clear();
        mAdapter.addItems(getChatList(0));
        mStart = mAdapter.getCount();
        lvMessage.postDelayed(mListViewSelectRunnable, 500);
        mAdapter.setSendImageClickListener(this);
        mHandler.postDelayed(mNewFragment2UpdateItemBadgeRunnable, 500);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "聊天记录读取错误");
            return;
        }
        if (TextUtils.isEmpty(mMyType)) {
            String[] roles = new String[app.loginUser.roles.length];
            for (int i = 0; i < app.loginUser.roles.length; i++) {
                roles[i] = app.loginUser.roles[i].toString();
            }
            if (CommonUtil.inArray(UserRole.ROLE_TEACHER.name(), roles)) {
                mMyType = PushUtil.ChatUserType.TEACHER;
            } else {
                mMyType = PushUtil.ChatUserType.FRIEND;
            }
        }

        mFromId = intent.getIntExtra(FROM_ID, mFromId);
        mType = intent.getStringExtra(Const.NEWS_TYPE);
        mToId = app.loginUser.id;
        mFromUserInfo = new User();
        mFromUserInfo.id = mFromId;
        mFromUserInfo.mediumAvatar = intent.getStringExtra(HEAD_IMAGE_URL);
        mFromUserInfo.nickname = intent.getStringExtra(Const.ACTIONBAR_TITLE);
        NotificationUtil.cancelById(mFromId);
        setBackMode(BACK, intent.getStringExtra(Const.ACTIONBAR_TITLE));
        CurrentFromId = mFromId;
        if (mChatDataSource == null) {
            mChatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        }
        initCacheFolder();
        getFriendUserInfo();

        mAdapter = new ChatAdapter<>(mContext, getChatList(0), mFromUserInfo);
        mAdapter.setSendImageClickListener(this);
        lvMessage.setAdapter(mAdapter);
        mStart = mAdapter.getCount();
        lvMessage.postDelayed(mListViewSelectRunnable, 500);

        mAudioDownloadReceiver.setAdapter(mAdapter);

        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mAdapter.addItems(getChatList(mStart));
                mStart = mAdapter.getCount();
                mPtrFrame.refreshComplete();
                lvMessage.postDelayed(mListViewSelectRunnable, 500);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canDoRefresh = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                int count = getChatList(mStart).size();
                return count > 0 && canDoRefresh;
            }
        });

        mHandler.postDelayed(mNewFragment2UpdateItemBadgeRunnable, 500);
    }

    private Runnable mListViewSelectRunnable = new Runnable() {
        @Override
        public void run() {
            lvMessage.setSelection(mStart);
            Log.d("onLayoutChange", "bottom-->");
        }
    };

    private Runnable mNewFragment2UpdateItemBadgeRunnable = new Runnable() {
        @Override
        public void run() {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.FROM_ID, mFromId);
            bundle.putString(Const.NEWS_TYPE, mType);
            app.sendMsgToTarget(NewsFragment.UPDATE_UNREAD_MSG, bundle, NewsFragment.class);
        }
    };

    private ArrayList<Chat> getChatList(int start) {
        String selectSql = String.format("(FROMID = %d AND TOID=%d) OR (TOID=%d AND FROMID=%d)", mFromId, mToId, mFromId, mToId);
        ArrayList<Chat> mList = mChatDataSource.getChats(start, Const.NEWS_LIMIT, selectSql);
        Collections.reverse(mList);
        return mList;
    }

    @Override
    public void sendMsg(String content) {
        mSendTime = (int) (System.currentTimeMillis() / 1000);
        final Chat chat = new Chat(app.loginUser.id, mFromId, app.loginUser.nickname, app.loginUser.mediumAvatar,
                etSend.getText().toString(), PushUtil.ChatMsgType.TEXT, mSendTime);

        addSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, chat);

        etSend.setText("");
        etSend.requestFocus();

        WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
        message.setTitle(mFromUserInfo.nickname);
        message.setContent(chat.content);
        V2CustomContent v2CustomContent = getV2CustomContent(PushUtil.ChatMsgType.TEXT, chat.content);
        String v2CustomContentJson = gson.toJson(v2CustomContent);
        message.setCustomContentJson(v2CustomContentJson);
        message.isForeground = true;
        notifyNewFragmentListView2Update(message);

        RequestUrl requestUrl = app.bindPushUrl(Const.SEND);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", app.loginUser.nickname);
        params.put("content", content);
        v2CustomContent.getFrom().setId(app.loginUser.id);
        v2CustomContent.getFrom().setImage(app.loginUser.mediumAvatar);
        v2CustomContent.getFrom().setType(mMyType);
        params.put("custom", gson.toJson(v2CustomContent));
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CloudResult result = parseJsonValue(response, new TypeToken<CloudResult>() {
                });
                if (result != null && result.getResult()) {
                    chat.id = result.id;
                    updateSendMsgToListView(PushUtil.MsgDeliveryType.SUCCESS, chat);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateSendMsgToListView(PushUtil.MsgDeliveryType.FAILED, chat);
                CommonUtil.longToast(mActivity, "网络连接不可用请稍后再试");
            }
        });
    }

    @Override
    public void sendMsgAgain(final BaseMsgEntity model) {
        RequestUrl requestUrl = app.bindPushUrl(Const.SEND);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", app.loginUser.nickname);
        params.put("content", model.content);
        V2CustomContent v2CustomContent = getV2CustomContent(PushUtil.ChatMsgType.TEXT, model.content);
        v2CustomContent.getFrom().setId(app.loginUser.id);
        v2CustomContent.getFrom().setImage(app.loginUser.mediumAvatar);
        v2CustomContent.getFrom().setType(mMyType);
        params.put("custom", gson.toJson(v2CustomContent));

        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CloudResult result = parseJsonValue(response, new TypeToken<CloudResult>() {
                });
                if (result != null && result.getResult()) {
                    model.id = result.id;
                    updateSendMsgToListView(PushUtil.MsgDeliveryType.SUCCESS, (Chat) model);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "发送信息失败");
            }
        });
    }

    private void sendMediaMsg(final Chat chat, String type) {
        RequestUrl requestUrl = app.bindPushUrl(Const.SEND);
        V2CustomContent v2CustomContent = getV2CustomContent(type, chat.upyunMediaGetUrl);
        v2CustomContent.getFrom().setId(app.loginUser.id);
        v2CustomContent.getFrom().setImage(app.loginUser.mediumAvatar);
        v2CustomContent.getFrom().setType(mMyType);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", app.loginUser.nickname);
        params.put("content", PushUtil.getNotificationContent(type));
        params.put("custom", gson.toJson(v2CustomContent));
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CloudResult result = parseJsonValue(response, new TypeToken<CloudResult>() {
                });
                if (result != null && result.getResult()) {
                    chat.id = result.id;
                    updateSendMsgToListView(PushUtil.MsgDeliveryType.SUCCESS, chat);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "发送信息失败");
            }
        });
    }

    /**
     * 上传资源
     *
     * @param file upload file
     */
    @Override
    public void uploadMedia(final File file, final String type, String strType) {
        if (file == null || !file.exists()) {
            CommonUtil.shortToast(mContext, String.format("%s不存在", strType));
            return;
        }
        try {
            mSendTime = (int) (System.currentTimeMillis() / 1000);
            final Chat chat = new Chat(app.loginUser.id, mFromId, app.loginUser.nickname, app.loginUser.mediumAvatar,
                    file.getPath(), type, mSendTime);

            //生成New页面的消息并通知更改
            WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
            message.setTitle(mFromUserInfo.nickname);
            message.setContent(String.format("[%s]", strType));
            V2CustomContent v2CustomContent = getV2CustomContent(type, message.getContent());
            message.setCustomContentJson(gson.toJson(v2CustomContent));
            message.isForeground = true;
            notifyNewFragmentListView2Update(message);

            addSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, chat);

            getUpYunUploadInfo(file, mFromId, new NormalCallback<UpYunUploadResult>() {
                @Override
                public void success(final UpYunUploadResult result) {
                    if (result != null) {
                        chat.upyunMediaPutUrl = result.putUrl;
                        chat.upyunMediaGetUrl = result.getUrl;
                        chat.headers = result.getHeaders();
                        uploadUnYunMedia(file, chat, type);
                        saveUploadResult(result.putUrl, result.getUrl, mFromId);
                    } else {
                        updateSendMsgToListView(PushUtil.MsgDeliveryType.FAILED, chat);
                    }
                }
            });
            viewMediaLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void uploadUnYunMedia(final File file, final Chat chat, final String type) {
        RequestUrl putUrl = new RequestUrl(chat.upyunMediaPutUrl);
        putUrl.setHeads(chat.headers);
        putUrl.setMuiltParams(new Object[]{"file", file});
        ajaxPostMultiUrl(putUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "success");
                sendMediaMsg(chat, type);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateSendMsgToListView(PushUtil.MsgDeliveryType.FAILED, chat);
                CommonUtil.longToast(mActivity, getString(R.string.request_fail_text));
                Log.d(TAG, "upload media res to upyun failed");
            }
        }, Request.Method.PUT);
    }

    @Override
    public void uploadMediaAgain(final File file, final BaseMsgEntity model, final String type, String strType) {
        final Chat chat = (Chat) model;
        if (file == null || !file.exists()) {
            CommonUtil.shortToast(mContext, String.format("%s不存在", strType));
            return;
        }

        if (TextUtils.isEmpty(model.upyunMediaPutUrl)) {
            getUpYunUploadInfo(file, mFromId, new NormalCallback<UpYunUploadResult>() {
                @Override
                public void success(final UpYunUploadResult result) {
                    if (result != null) {
                        model.upyunMediaPutUrl = result.putUrl;
                        model.upyunMediaGetUrl = result.getUrl;
                        model.headers = result.getHeaders();
                        uploadUnYunMedia(file, chat, type);
                        saveUploadResult(result.putUrl, result.getUrl, mFromId);
                    } else {
                        updateSendMsgToListView(PushUtil.MsgDeliveryType.FAILED, chat);
                    }
                }
            });
        } else {
            uploadUnYunMedia(file, chat, type);
        }
    }

    /**
     * update badge the ListView of NewsFragment
     *
     * @param message xg message
     */
    public void notifyNewFragmentListView2Update(WrapperXGPushTextMessage message) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.GET_PUSH_DATA, message);
        bundle.putInt(Const.ADD_CHAT_MSG_DESTINATION, NewsFragment.HANDLE_SEND_CHAT_MSG);
        app.sendMsgToTarget(Const.ADD_MSG, bundle, NewsFragment.class);
    }

    /**
     * 保持一条聊天记录到数据库，并添加到ListView
     *
     * @param delivery 是否送达
     * @param chat     一行聊天记录
     */
    private void addSendMsgToListView(int delivery, Chat chat) {
        chat.direct = Chat.Direct.SEND;
        chat.delivery = delivery;
        long chatId = mChatDataSource.create(chat);
        chat.chatId = (int) chatId;
        if (app.loginUser != null) {
            chat.headImgUrl = app.loginUser.mediumAvatar;
        }
        mAdapter.addItem(chat);
        mStart = mStart + 1;
    }

    /**
     * 更新一行聊天记录，并更新对应的Item
     *
     * @param delivery 是否送达
     * @param chat     一行聊天记录
     */
    private void updateSendMsgToListView(int delivery, Chat chat) {
        chat.delivery = delivery;
        mChatDataSource.update(chat);
        mAdapter.updateItemByChatId(chat);
    }

    /**
     * 获取对方信息
     */
    private void getFriendUserInfo() {
        if (mFromUserInfo == null) {
            RequestUrl requestUrl = app.bindUrl(Const.USERINFO, false);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("userId", mFromId + "");
            ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mFromUserInfo = parseJsonValue(response, new TypeToken<User>() {
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "无法获取对方信息");
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.user_profile) {
            mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.USER_PROFILE, mFromId));
                    startIntent.putExtra(Const.WEB_URL, url);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private V2CustomContent getV2CustomContent(String type, String content) {
        V2CustomContent v2CustomContent = new V2CustomContent();
        V2CustomContent.FromEntity fromEntity = new V2CustomContent.FromEntity();
        fromEntity.setNickname(app.loginUser.nickname);
        fromEntity.setId(mFromId);
        fromEntity.setType(mType);
        fromEntity.setImage(mFromUserInfo.mediumAvatar);
        v2CustomContent.setFrom(fromEntity);
        V2CustomContent.ToEntity toEntity = new V2CustomContent.ToEntity();
        toEntity.setId(mFromId);
        toEntity.setType(PushUtil.ChatUserType.USER);
        v2CustomContent.setTo(toEntity);
        V2CustomContent.BodyEntity bodyEntity = new V2CustomContent.BodyEntity();
        bodyEntity.setType(type);
        bodyEntity.setContent(content);
        v2CustomContent.setBody(bodyEntity);
        v2CustomContent.setV(2);
        v2CustomContent.setCreatedTime(mSendTime);
        return v2CustomContent;
    }

    @Override
    public void invoke(WidgetMessage message) {
        try {
            MessageType messageType = message.type;
            WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
            V2CustomContent v2CustomContent = parseJsonValue(wrapperMessage.getCustomContentJson(), new TypeToken<V2CustomContent>() {
            });
            switch (messageType.code) {
                case Const.ADD_MSG:
                    if (mFromId == v2CustomContent.getFrom().getId()) {
                        Chat chat = new Chat(wrapperMessage);
                        if (mFromUserInfo != null) {
                            chat.headImgUrl = mFromUserInfo.mediumAvatar;
                        }
                        mAdapter.addItem(chat);
                    }
                    break;
                case Const.ADD_CHAT_MSGS:
                    ArrayList<Chat> chats = (ArrayList<Chat>) message.data.get(Const.GET_PUSH_DATA);
                    mAdapter.addItems(chats);
                    break;
                case Const.UPDATE_CHAT_MSG:
                    if (mFromId == v2CustomContent.getFrom().getId()) {
                        Chat chat = new Chat(wrapperMessage);
                        updateSendMsgToListView(message.data.getInt(MSG_DELIVERY), chat);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{
                new MessageType(Const.ADD_MSG, source),
                new MessageType(Const.ADD_CHAT_MSGS, source),
                new MessageType(Const.UPDATE_CHAT_MSG, source)
        };
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatDataSource != null) {
            mChatDataSource.close();
        }
    }
}
