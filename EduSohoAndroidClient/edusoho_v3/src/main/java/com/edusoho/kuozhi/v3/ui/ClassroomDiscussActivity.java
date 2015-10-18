package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.ClassroomDiscussAdapter;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.ClassroomDiscussEntity;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.result.CloudResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseChatActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
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
public class ClassroomDiscussActivity extends BaseChatActivity implements ClassroomDiscussAdapter.ErrorClick {

    public static final String FROM_ID = "from_id";
    public static final String CLASSROOM_NAME = "classroom_name";
    public static int CurrentClassroomId = 0;

    public String mClassroomName;
    private int mFromClassroomId;
    private ClassroomDiscussDataSource mClassroomDiscussDataSource;
    private ClassroomDiscussAdapter mAdapter;

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
        mAdapter.setErrorClick(this);
        mHandler.postDelayed(mNewFragment2UpdateItemBadgeRunnable, 500);
    }

    @Override
    protected void initView() {
        super.initView();
        mAdapter = new ClassroomDiscussAdapter(getList(0), mContext);
        mAdapter.setErrorClick(this);
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
        setBackMode(BACK, intent.getStringExtra(Const.ACTIONBAR_TITLE));

        mFromClassroomId = intent.getIntExtra(FROM_ID, 0);
        mClassroomName = intent.getStringExtra(CLASSROOM_NAME);
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
            // TODO 更新newsfragment的unread
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
//        final Chat chat = new Chat(app.loginUser.id, mFromId, app.loginUser.nickname, app.loginUser.mediumAvatar,
//                etSend.getText().toString(), Chat.FileType.TEXT.toString().toLowerCase(), mSendTime);
        final ClassroomDiscussEntity model = new ClassroomDiscussEntity(0, mFromClassroomId, app.loginUser.id, app.loginUser.nickname, app.loginUser.mediumAvatar,
                etSend.getText().toString(), app.loginUser.id, Chat.FileType.TEXT.toString().toLowerCase(), Chat.Delivery.UPLOADING.getIndex(), mSendTime);


        addSendMsgToListView(Chat.Delivery.UPLOADING, chat);

        etSend.setText("");
        etSend.requestFocus();

        WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
        message.setTitle(mClassroomName);
        message.setContent(model.getContent());
        V2CustomContent v2CustomContent = getV2CustomContent(Chat.FileType.TEXT, TypeBusinessEnum.FRIEND, model.getContent());
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
        params.put("custom", gson.toJson(v2CustomContent));

        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CloudResult result = parseJsonValue(response, new TypeToken<CloudResult>() {
                });
                if (result != null && result.getResult()) {
                    chat.id = result.id;
                    updateSendMsgToListView(Chat.Delivery.SUCCESS, chat);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateSendMsgToListView(Chat.Delivery.FAILED, chat);
                CommonUtil.longToast(mActivity, "网络连接不可用请稍后再试");
            }
        });
    }

    public void addSendMsgToListView(Chat.Delivery delivery, Chat chat) {
        super.addSendMsgToListView(delivery, chat);
    }

    @Override
    public void uploadMediaAgain(File file, ClassroomDiscussEntity model, Chat.FileType type, String strType) {

    }

    @Override
    public void sendMsgAgain(ClassroomDiscussEntity model) {

    }
}
