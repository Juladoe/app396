package com.edusoho.kuozhi.imserver.ui;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.R;
import com.edusoho.kuozhi.imserver.SendEntity;
import com.edusoho.kuozhi.imserver.entity.IMUploadEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.entity.message.Source;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.imserver.ui.adapter.MessageListAdapter;
import com.edusoho.kuozhi.imserver.ui.broadcast.AudioDownloadReceiver;
import com.edusoho.kuozhi.imserver.ui.entity.PushUtil;
import com.edusoho.kuozhi.imserver.ui.entity.UpYunUploadResult;
import com.edusoho.kuozhi.imserver.ui.listener.MessageControllerListener;
import com.edusoho.kuozhi.imserver.ui.listener.MessageSendListener;
import com.edusoho.kuozhi.imserver.ui.util.ApiConst;
import com.edusoho.kuozhi.imserver.ui.view.MessageInputView;
import com.edusoho.kuozhi.imserver.util.MessageEntityBuildr;
import com.edusoho.kuozhi.imserver.util.MessageUtil;
import com.edusoho.kuozhi.imserver.util.SendEntityBuildr;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.body.MultipartFormDataBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by suju on 16/8/26.
 */
public class MessageListFragment extends Fragment {

    private static final String TAG = "MessageListFragment";
    public static final String CONV_NO = "convNo";
    public static final String CURRENT_ID = "currentId";
    public static final int SEND_IMAGE = 1;
    public static final int SEND_CAMERA = 2;

    protected int mStart = 0;
    private String mConversationNo;
    private int mCurrentId;
    private Role mTargetRole;
    private IMMessageReceiver mIMMessageReceiver;
    protected AudioDownloadReceiver mAudioDownloadReceiver;

    protected PtrClassicFrameLayout mPtrFrame;
    protected ListView mMessageListView;
    protected View mContainerView;
    protected MessageInputView mMessageInputView;
    protected MessageListAdapter mListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioDownloadReceiver = new AudioDownloadReceiver();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCurrentId = getArguments().getInt(CURRENT_ID);
        mConversationNo = getArguments().getString(CONV_NO);
        mListAdapter = new MessageListAdapter(getActivity().getBaseContext());
        mListAdapter.setCurrentId(mCurrentId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContainerView == null) {
            mContainerView = inflater.inflate(R.layout.fragment_message_list_layout, null);
            initView(mContainerView);
        }

        ViewGroup parent = (ViewGroup) mContainerView.getParent();
        if (parent != null) {
            parent.removeView(mContainerView);
        }
        return mContainerView;
    }

    protected void initView(View view) {
        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view_frame);
        mMessageListView = (ListView) view.findViewById(R.id.listview);
        mMessageInputView = (MessageInputView) view.findViewById(R.id.message_input_view);
        mMessageListView.setAdapter(mListAdapter);
        Log.d(TAG, "initView");

        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.refreshComplete();
                //lvMessage.postDelayed(mListViewSelectRunnable, 100);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canDoRefresh = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                int count = mListAdapter.getCount();
                return count > 0 && canDoRefresh;
            }
        });

        mMessageInputView.setMessageControllerListener(getMessageControllerListener());
        mMessageInputView.setMessageSendListener(getMessageSendListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIMMessageReceiver != null) {
            IMClient.getClient().removeReceiver(mIMMessageReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getBaseContext().unregisterReceiver(mAudioDownloadReceiver);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            /*mAdapter = new CourseDiscussAdapter<>(mContext, getChatList(0));
            mAdapter.setSendImageClickListener(this);
            lvMessage.setAdapter(mAdapter);
            mStart = mAdapter.getCount();
            lvMessage.postDelayed(mListViewSelectRunnable, 500);*/
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIMMessageReceiver == null) {
            mIMMessageReceiver = getIMMessageListener();
        }

        IMClient.getClient().addMessageReceiver(mIMMessageReceiver);
        IMClient.getClient().getConvManager().clearReadCount(mConversationNo);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getActivity().getBaseContext().registerReceiver(mAudioDownloadReceiver, intentFilter);
    }

    protected void handleMessage(MessageEntity messageEntity) {
        MessageBody messageBody = new MessageBody(messageEntity);
        Role role = IMClient.getClient().getRoleManager()
                .getRole(Destination.USER, MessageUtil.parseInt(messageEntity.getFromId()));
        mListAdapter.addItem(messageBody);
    }

    protected void handleOfflineMessage(List<MessageEntity> messageEntityList) {
        ArrayList<MessageBody> chatList = new ArrayList<>();
        for (MessageEntity messageEntity : messageEntityList) {
            MessageBody messageBody = new MessageBody(messageEntity);
            chatList.add(messageBody);
        }

        mListAdapter.addList(chatList);
    }

    protected void updateMessageSendStatus(MessageBody messageBody) {
        //updateSendMsgToListView(PushUtil.MsgDeliveryType.SUCCESS, chat);

        ContentValues cv = new ContentValues();
        cv.put("status", MessageEntity.StatusType.SUCCESS);
        IMClient.getClient().getMessageManager().updateMessageFieldByUid(messageBody.getMessageId(), cv);
    }

    protected IMMessageReceiver getIMMessageListener() {
        return new IMMessageReceiver() {
            @Override
            public boolean onReceiver(MessageEntity msg) {
                if (!mConversationNo.equals(msg.getConvNo())) {
                    return true;
                }
                handleMessage(msg);
                return true;
            }

            @Override
            public boolean onOfflineMsgReceiver(List<MessageEntity> messageEntities) {
                handleOfflineMessage(messageEntities);
                return false;
            }

            @Override
            public void onSuccess(String extr) {
                MessageBody messageBody = new MessageBody(extr);
                if (messageBody == null) {
                    return;
                }
                messageBody.setConvNo(mConversationNo);
                updateMessageSendStatus(messageBody);
            }

            @Override
            public ReceiverInfo getType() {
                return new ReceiverInfo(Destination.COURSE, mConversationNo);
            }
        };
    }

    protected MessageSendListener getMessageSendListener() {
        return new MessageSendListener() {

            @Override
            public void onSendMessage(String message) {
                sendMsg(message);
            }

            @Override
            public void onSendAudio(File audioFile) {
                uploadMedia(audioFile, PushUtil.ChatMsgType.AUDIO);
            }

            @Override
            public void onSendImage(File imageFile) {
                uploadMedia(imageFile, PushUtil.ChatMsgType.IMAGE);
            }
        };
    }

    protected MessageControllerListener getMessageControllerListener() {
        return new MessageControllerListener() {
            @Override
            public void onSelectPhoto() {
                openPictureFromLocal();
            }

            @Override
            public void onTakePhoto() {
                openPictureFromCamera();
            }
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        List<MessageEntity> messageEntityList = IMClient.getClient().getChatRoom(mConversationNo).getMessageList(mStart);

        List<MessageBody> messageBodies = new ArrayList<>();
        for (MessageEntity entity : messageEntityList) {
            messageBodies.add(new MessageBody(entity));
        }

        mListAdapter.addList(messageBodies);
    }

    /**
     * 从图库获取图片
     */
    protected void openPictureFromLocal() {
        Intent intent = new Intent(getActivity().getBaseContext(), MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 5);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        startActivityForResult(intent, SEND_IMAGE);
    }

    protected void openPictureFromCamera() {
        Intent intent = new Intent(getActivity().getBaseContext(), MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_TAKE_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, SEND_IMAGE);
    }

    /*
        send msg
     */

    private void sendMsg(String content) {
        MessageBody messageBody = saveMessageToLoacl(content, PushUtil.ChatMsgType.TEXT);
        sendMessageToServer(messageBody);
    }

    protected void sendMessageToServer(MessageBody messageBody) {
        try {
            String toId = "";
            switch (messageBody.getDestination().getType()) {
                case Destination.CLASSROOM:
                case Destination.COURSE:
                    toId = "all";
                    break;
                case Destination.USER:
                    toId = String.valueOf(messageBody.getDestination().getId());
            }
            SendEntity sendEntity = SendEntityBuildr.getBuilder()
                    .addToId(toId)
                    .addMsg(messageBody.toJson())
                    .builder();
            IMClient.getClient().getChatRoom(mConversationNo).send(sendEntity);
        } catch (Exception e) {
        }
    }

    private void uploadMedia(final File file, final String type) {
        if (file == null || !file.exists()) {
            //CommonUtil.shortToast(mContext, String.format("%s不存在", strType));
            return;
        }
        try {
            String content = file.getAbsolutePath();
            if (PushUtil.ChatMsgType.AUDIO.equals(type)) {
                content = wrapAudioMessageContent(file.getAbsolutePath(), getAudioDuration(file.getAbsolutePath()));
            }
            MessageBody messageBody = saveMessageToLoacl(content, type);
            IMClient.getClient().getMessageManager().saveUploadEntity(
                    messageBody.getMessageId(), messageBody.getType(), file.getPath()
            );
            getUpYunUploadInfo(messageBody, file, mTargetRole.getRid());
            //viewMediaLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void uploadUnYunMedia(String uploadUrl, final File file, HashMap<String, String> headers, final MessageBody messageBody) {

        AsyncHttpPost post = new AsyncHttpPost(uploadUrl);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.setHeader(entry.getKey(), entry.getValue());
        }
        MultipartFormDataBody body = new MultipartFormDataBody();
        body.addFilePart("file", file);
        post.setBody(body);
        post.setMethod("PUT");
        AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, String result) {
                if (ex != null) {
                    //CommonUtil.longToast(mContext, getString(R.string.request_fail_text));
                    Log.d(TAG, "upload media res to upyun failed");
                    return;
                }
                //sendMessageToServer(messageBody);
            }
        });
    }

    public void sendMsgAgain(MessageBody messageBody) {
        //updateSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, chat);
        sendMessageToServer(messageBody);
    }

    public void sendMediaMsg(MessageBody messageBody, String type) {
    }

    public void saveUploadResult(String putUrl, String getUrl, int fromId) {

        String path = String.format(ApiConst.SAVE_UPLOAD_INFO, fromId);
        AsyncHttpPost post = new AsyncHttpPost(path);
        JSONObject params = new JSONObject();
        try {
            params.put("putUrl", putUrl);
            params.put("getUrl", getUrl);
        } catch (JSONException e) {

        }

        JSONObjectBody body = new JSONObjectBody(params);
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, String result) {
                if (ex != null) {
                    Log.d(TAG, "save upload info error");
                    return;
                }
                try {
                    JSONObject resultJsonObject = new JSONObject(result);
                    if ("success".equals(resultJsonObject.getString("result"))) {
                        Log.d(TAG, "save upload result success");
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "convert json to obj error");
                }
            }
        });
    }

    public void getUpYunUploadInfo(final MessageBody messageBody, File file, int fromId) {

        String path = String.format(ApiConst.GET_UPLOAD_INFO, fromId, file.length(), file.getName());
        AsyncHttpRequest request = new AsyncHttpGet(path);
        AsyncHttpClient.getDefaultInstance().executeString(request, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                UpYunUploadCallback upYunUploadCallback = new UpYunUploadCallback(messageBody);
                if (e != null || TextUtils.isEmpty(result)) {
                    //CommonUtil.longToast(mActivity, getString(R.string.request_fail_text));
                    Log.d(TAG, "get upload info from upyun failed");
                    upYunUploadCallback.success(null);
                    return;
                }

                try {
                    UpYunUploadResult upYunUploadResult = new UpYunUploadResult();
                    JSONObject jsonObject = new JSONObject(result);
                    upYunUploadResult.setPutUrl(jsonObject.optString("putUrl"));
                    upYunUploadResult.setGetUrl(jsonObject.optString("getUrl"));

                    JSONArray jsonArray = jsonObject.optJSONArray("headers");
                    if (jsonArray == null || jsonArray.length() == 0) {
                        upYunUploadCallback.success(null);
                        return;
                    }
                    int length = jsonArray.length();
                    String[] headers = new String[length];
                    for (int i = 0; i < length; i++) {
                        headers[i] = jsonArray.optString(i);
                    }
                    upYunUploadResult.setHeaders(headers);
                    upYunUploadCallback.success(upYunUploadResult);
                } catch (JSONException je) {
                    upYunUploadCallback.success(null);
                }
            }
        });
    }

    private class UpYunUploadCallback {
        private MessageBody messageBody;

        public UpYunUploadCallback(MessageBody messageBody) {
            this.messageBody = messageBody;
        }

        public void success(UpYunUploadResult result) {
            if (result != null) {
                IMUploadEntity uploadEntity = IMClient.getClient().getMessageManager()
                        .getUploadEntity(messageBody.getMessageId());
                File file = new File(uploadEntity.getSource());
                String body = result.getUrl;
                if (PushUtil.ChatMsgType.AUDIO.equals(messageBody.getType())) {
                    body = wrapAudioMessageContent(result.getUrl, getAudioDuration(file.getAbsolutePath()));
                }
                messageBody.setBody(body);

                uploadUnYunMedia(result.putUrl, file, result.getHeaders(), messageBody);
                saveUploadResult(result.putUrl, result.getUrl, mTargetRole.getRid());
            } else {
                //updateSendMsgToListView(PushUtil.MsgDeliveryType.FAILED, chat);
            }
        }
    }

    private void updateConv(MessageBody messageBody) {
        ContentValues cv = new ContentValues();
        cv.put("laterMsg", messageBody.toJson());
        cv.put("updatedTime", System.currentTimeMillis());
        IMClient.getClient().getConvManager().updateConvField(mConversationNo, cv);
    }

    protected MessageBody saveMessageToLoacl(String content, String type) {
        MessageBody messageBody = createSendMessageBody(content, type);
        long sendTime = messageBody.getCreatedTime();

        MessageEntity messageEntity = createMessageEntityByBody(messageBody);
        IMClient.getClient().getMessageManager().createMessage(messageEntity);
        updateConv(messageBody);

        //addSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, chat);

        return messageBody;
    }

    private MessageEntity createMessageEntityByBody(MessageBody messageBody) {
        return new MessageEntityBuildr()
                .addUID(messageBody.getMessageId())
                .addConvNo(messageBody.getConvNo())
                .addToId(String.valueOf(messageBody.getDestination().getId()))
                .addToName(messageBody.getDestination().getNickname())
                .addFromId(String.valueOf(messageBody.getSource().getId()))
                .addFromName(messageBody.getSource().getNickname())
                .addCmd("message")
                .addMsg(messageBody.toJson())
                .addTime((int) (messageBody.getCreatedTime() / 1000))
                .builder();
    }

    /*
    todo nickname
     */
    protected MessageBody createSendMessageBody(String content, String type) {
        MessageBody messageBody = new MessageBody(1, type, content);
        messageBody.setCreatedTime(System.currentTimeMillis());
        messageBody.setDestination(new Destination(mTargetRole.getRid(), mTargetRole.getType()));
        messageBody.getDestination().setNickname(null);
        messageBody.setSource(new Source(mCurrentId, Destination.USER));
        messageBody.getSource().setNickname(mTargetRole.getNickname());
        messageBody.setConvNo(mConversationNo);
        messageBody.setMessageId(UUID.randomUUID().toString());

        return messageBody;
    }

    private int getAudioDuration(String audioFile) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), Uri.parse(audioFile));
        int duration = mediaPlayer.getDuration();
        mediaPlayer.release();
        return duration;
    }

    private String wrapAudioMessageContent(String audioFilePath, int audioTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("file", audioFilePath);
            jsonObject.put("duration", audioTime);
        } catch (JSONException e) {
        }

        return jsonObject.toString();
    }
}
