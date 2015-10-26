package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.ChatAdapter;
import com.edusoho.kuozhi.v3.adapter.ClassroomDiscussAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.model.bal.push.BaseMsgEntity;
import com.edusoho.kuozhi.v3.model.bal.push.ClassroomDiscussEntity;
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
import com.edusoho.kuozhi.v3.util.sql.ClassroomDiscussDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by JesseHuang on 15/10/16.
 */
public class ClassroomDiscussActivity extends BaseChatActivity implements ChatAdapter.ImageErrorClick {

    public static final String FROM_ID = "from_id";
    public static final String CLASSROOM_IMAGE = "classroom_image";
    public static int CurrentClassroomId = 0;

    private String mClassroomName;
    private String mClassroomImage;
    private int mFromClassroomId;
    /**
     * 自己的BusinessType
     */
    private String mMyType;

    private ClassroomDiscussDataSource mClassroomDiscussDataSource;
    private ClassroomDiscussAdapter<ClassroomDiscussEntity> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
        mAdapter.clear();
        mAdapter.addItems(getList(0));
        mStart = mAdapter.getCount();
        lvMessage.post(mListViewSelectRunnable);
        mAdapter.setSendImageClickListener(this);
        mHandler.postDelayed(mNewFragment2UpdateItemBadgeRunnable, 500);
    }

    @Override
    protected void initView() {
        super.initView();
        mAdapter = new ClassroomDiscussAdapter<>(getList(0), mContext);
        mAdapter.setSendImageClickListener(this);
        lvMessage.setAdapter(mAdapter);
        mAudioDownloadReceiver.setAdapter(mAdapter);
        mStart = mAdapter.getCount();
        lvMessage.post(mListViewSelectRunnable);
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mAdapter.addItems(getList(mStart));
                mStart = mAdapter.getCount();
                mPtrFrame.refreshComplete();
                lvMessage.postDelayed(mListViewSelectRunnable, 100);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canDoRefresh = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                int count = getList(mStart).size();
                return count > 0 && canDoRefresh;
            }
        });

        lvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewMediaLayout.setVisibility(View.GONE);
                return false;
            }
        });
        mHandler.postDelayed(mNewFragment2UpdateItemBadgeRunnable, 500);
    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "聊天记录读取错误");
            return;
        }
        mClassroomImage = intent.getStringExtra(CLASSROOM_IMAGE);
        mClassroomName = intent.getStringExtra(Const.ACTIONBAR_TITLE);
        setBackMode(BACK, mClassroomName);
        mFromClassroomId = intent.getIntExtra(FROM_ID, mFromClassroomId);
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
        CurrentClassroomId = mFromClassroomId;
        NotificationUtil.cancelById(mFromClassroomId);
        if (mClassroomDiscussDataSource == null) {
            mClassroomDiscussDataSource = new ClassroomDiscussDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        }

        super.initCacheFolder();
    }

    private Runnable mNewFragment2UpdateItemBadgeRunnable = new Runnable() {
        @Override
        public void run() {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.FROM_ID, mFromClassroomId);
            bundle.putString(Const.NEWS_TYPE, PushUtil.ChatUserType.CLASSROOM);
            app.sendMsgToTarget(NewsFragment.UPDATE_UNREAD_MSG, bundle, NewsFragment.class);
        }
    };

    private ArrayList<ClassroomDiscussEntity> getList(int start) {
        ArrayList<ClassroomDiscussEntity> list = mClassroomDiscussDataSource.getLists(mFromClassroomId, app.loginUser.id, start, Const.NEWS_LIMIT);
        Collections.reverse(list);
        return list;
    }

    @Override
    public void sendMsg(String content) {
        mSendTime = (int) (System.currentTimeMillis() / 1000);
        final ClassroomDiscussEntity model = new ClassroomDiscussEntity(0, mFromClassroomId, app.loginUser.id, app.loginUser.nickname, app.loginUser.mediumAvatar,
                etSend.getText().toString(), app.loginUser.id, PushUtil.ChatMsgType.TEXT, PushUtil.MsgDeliveryType.UPLOADING, mSendTime);

        addSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, model);

        etSend.setText("");
        etSend.requestFocus();

        WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
        message.setTitle(mClassroomName);
        message.setContent(model.content);
        V2CustomContent v2CustomContent = getV2CustomContent(PushUtil.ChatMsgType.TEXT, model.content);
        String v2CustomContentJson = gson.toJson(v2CustomContent);
        message.setCustomContentJson(v2CustomContentJson);
        message.isForeground = true;
        notifyNewFragmentListView2Update(message);

        RequestUrl requestUrl = app.bindPushUrl(Const.SEND);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", mClassroomName);
        params.put("content", content);
        params.put("custom", gson.toJson(v2CustomContent));

        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CloudResult result = parseJsonValue(response, new TypeToken<CloudResult>() {
                });
                if (result != null && result.getResult()) {
                    model.id = result.id;
                    updateSendMsgToListView(PushUtil.MsgDeliveryType.SUCCESS, model);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateSendMsgToListView(PushUtil.MsgDeliveryType.FAILED, model);
                CommonUtil.longToast(mActivity, "网络连接不可用请稍后再试");
            }
        });
    }

    public void sendMediaMsg(final ClassroomDiscussEntity model, String type) {
        RequestUrl requestUrl = app.bindPushUrl(Const.SEND);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", mClassroomName);
        params.put("content", model.upyunMediaGetUrl);
        params.put("custom", gson.toJson(getV2CustomContent(type, model.upyunMediaGetUrl)));
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CloudResult result = parseJsonValue(response, new TypeToken<CloudResult>() {
                });
                if (result != null && result.getResult()) {
                    model.id = result.id;
                    updateSendMsgToListView(PushUtil.MsgDeliveryType.SUCCESS, model);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "发送信息失败");
            }
        });
    }

    @Override
    public void uploadMediaAgain(final File file, final BaseMsgEntity model, final String type, String strType) {
        final ClassroomDiscussEntity discussModel = (ClassroomDiscussEntity) model;
        if (file == null || !file.exists()) {
            CommonUtil.shortToast(mContext, String.format("%s不存在", strType));
            return;
        }

        if (TextUtils.isEmpty(model.upyunMediaPutUrl)) {
            getUpYunUploadInfo(file, new NormalCallback<UpYunUploadResult>() {
                @Override
                public void success(final UpYunUploadResult result) {
                    if (result != null) {
                        model.upyunMediaPutUrl = result.putUrl;
                        model.upyunMediaGetUrl = result.getUrl;
                        model.headers = result.getHeaders();
                        uploadUnYunMedia(file, discussModel, type);
                        saveUploadResult(result.putUrl, result.getUrl, discussModel.fromId);
                    } else {
                        updateSendMsgToListView(PushUtil.MsgDeliveryType.FAILED, discussModel);
                    }
                }
            }, discussModel.fromId);
        } else {
            uploadUnYunMedia(file, discussModel, type);
        }
    }

    @Override
    public void sendMsgAgain(final BaseMsgEntity model) {
        RequestUrl requestUrl = app.bindPushUrl(Const.SEND);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", app.loginUser.nickname);
        params.put("content", model.content);
        params.put("custom", gson.toJson(getV2CustomContent(PushUtil.ChatMsgType.TEXT, model.content)));

        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CloudResult result = parseJsonValue(response, new TypeToken<CloudResult>() {
                });
                if (result != null && result.getResult()) {
                    model.id = result.id;
                    updateSendMsgToListView(PushUtil.MsgDeliveryType.SUCCESS, (ClassroomDiscussEntity) model);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "发送信息失败");
            }
        });
    }

    // region 多媒体资源上传

    public void uploadMedia(final File file, final String type, String strType) {
        if (file == null || !file.exists()) {
            CommonUtil.shortToast(mContext, String.format("%s不存在", strType));
            return;
        }
        try {
            mSendTime = (int) (System.currentTimeMillis() / 1000);
            final ClassroomDiscussEntity model = new ClassroomDiscussEntity(0, mFromClassroomId, app.loginUser.id, app.loginUser.nickname, app.loginUser.mediumAvatar,
                    file.getPath(), app.loginUser.id, type, PushUtil.MsgDeliveryType.UPLOADING, mSendTime);

            //生成New页面的消息并通知更改
            WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
            message.setTitle(mClassroomName);
            message.setContent(String.format("[%s]", strType));
            V2CustomContent v2CustomContent = getV2CustomContent(type, message.getContent());
            message.setCustomContentJson(gson.toJson(v2CustomContent));
            message.isForeground = true;
            notifyNewFragmentListView2Update(message);

            addSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, model);

            super.getUpYunUploadInfo(file, new NormalCallback<UpYunUploadResult>() {
                @Override
                public void success(final UpYunUploadResult result) {
                    if (result != null) {
                        model.upyunMediaPutUrl = result.putUrl;
                        model.upyunMediaGetUrl = result.getUrl;
                        model.headers = result.getHeaders();
                        uploadUnYunMedia(file, model, type);
                        ClassroomDiscussActivity.super.saveUploadResult(result.putUrl, result.getUrl, mFromClassroomId);
                    } else {
                        updateSendMsgToListView(PushUtil.MsgDeliveryType.FAILED, model);
                    }
                }
            }, mFromClassroomId);
            viewMediaLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void uploadUnYunMedia(final File file, final ClassroomDiscussEntity model, final String type) {
        RequestUrl putUrl = new RequestUrl(model.upyunMediaPutUrl);
        putUrl.setHeads(model.headers);
        putUrl.setMuiltParams(new Object[]{"file", file});
        ajaxPostMultiUrl(putUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "success");
                sendMediaMsg(model, type);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateSendMsgToListView(PushUtil.MsgDeliveryType.FAILED, model);
                CommonUtil.longToast(mActivity, getString(R.string.request_fail_text));
                Log.d(TAG, "upload media res to upyun failed");
            }
        }, Request.Method.PUT);
    }

    // endregion

    // region 数据库操作
    public void addSendMsgToListView(int delivery, ClassroomDiscussEntity model) {
        model.delivery = delivery;
        long discussId = mClassroomDiscussDataSource.create(model);
        model.discussId = (int) discussId;
        mAdapter.addItem(model);
    }

    @Override
    public void notifyNewFragmentListView2Update(WrapperXGPushTextMessage message) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.GET_PUSH_DATA, message);
        bundle.putInt(Const.ADD_CHAT_MSG_DESTINATION, NewsFragment.HANDLE_SEND_CLASSROOM_DISCUSS_MSG);
        app.sendMsgToTarget(Const.ADD_MSG, bundle, NewsFragment.class);
    }

    public void updateSendMsgToListView(int type, ClassroomDiscussEntity model) {
        model.delivery = type;
        mClassroomDiscussDataSource.update(model);
        mAdapter.updateItemByChatId(model);
    }

    // endregion

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
        V2CustomContent v2CustomContent = parseJsonValue(wrapperMessage.getCustomContentJson(), new TypeToken<V2CustomContent>() {
        });
        switch (messageType.code) {
            case Const.ADD_CLASSROOM_MSG:
                if (mFromClassroomId == v2CustomContent.getTo().getId()) {
                    ClassroomDiscussEntity model = new ClassroomDiscussEntity(wrapperMessage);
                    mAdapter.addItem(model);
                }
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{
                new MessageType(Const.ADD_CLASSROOM_MSG, source),
        };
    }

    /**
     * 存本地的Custom信息
     *
     * @return V2CustomContent
     */
    private V2CustomContent getV2CustomContent(String type, String content) {
        V2CustomContent v2CustomContent = new V2CustomContent();
        V2CustomContent.FromEntity fromEntity = new V2CustomContent.FromEntity();
        fromEntity.setId(app.loginUser.id);
        fromEntity.setImage(app.loginUser.mediumAvatar);
        fromEntity.setNickname(app.loginUser.nickname);
        fromEntity.setType(mMyType);
        v2CustomContent.setFrom(fromEntity);
        V2CustomContent.ToEntity toEntity = new V2CustomContent.ToEntity();
        toEntity.setId(mFromClassroomId);
        toEntity.setImage(mClassroomImage);

        toEntity.setType(PushUtil.ChatUserType.CLASSROOM);
        v2CustomContent.setTo(toEntity);
        V2CustomContent.BodyEntity bodyEntity = new V2CustomContent.BodyEntity();
        bodyEntity.setType(type);
        bodyEntity.setContent(content);
        v2CustomContent.setBody(bodyEntity);
        v2CustomContent.setV(Const.PUSH_VERSION);
        v2CustomContent.setCreatedTime(mSendTime);
        return v2CustomContent;
    }
}
