package com.edusoho.kuozhi.imserver.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
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
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.IMUploadEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.ReceiverInfo;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.entity.message.Source;
import com.edusoho.kuozhi.imserver.listener.IMMessageReceiver;
import com.edusoho.kuozhi.imserver.ui.adapter.MessageListAdapter;
import com.edusoho.kuozhi.imserver.ui.broadcast.ResourceStatusReceiver;
import com.edusoho.kuozhi.imserver.ui.entity.PushUtil;
import com.edusoho.kuozhi.imserver.ui.entity.UpYunUploadResult;
import com.edusoho.kuozhi.imserver.ui.helper.MessageHelper;
import com.edusoho.kuozhi.imserver.ui.helper.MessageResourceHelper;
import com.edusoho.kuozhi.imserver.ui.listener.AudioPlayStatusListener;
import com.edusoho.kuozhi.imserver.ui.listener.InputViewControllerListener;
import com.edusoho.kuozhi.imserver.ui.listener.MessageControllerListener;
import com.edusoho.kuozhi.imserver.ui.listener.MessageListItemController;
import com.edusoho.kuozhi.imserver.ui.listener.MessageSendListener;
import com.edusoho.kuozhi.imserver.ui.util.ApiConst;
import com.edusoho.kuozhi.imserver.ui.util.MessageAudioPlayer;
import com.edusoho.kuozhi.imserver.ui.util.UpYunUploadTask;
import com.edusoho.kuozhi.imserver.ui.view.MessageInputView;
import com.edusoho.kuozhi.imserver.util.MessageEntityBuildr;
import com.edusoho.kuozhi.imserver.util.SendEntityBuildr;
import com.edusoho.kuozhi.imserver.util.SystemUtil;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.nostra13.universalimageloader.core.ImageLoader;

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
public class MessageListFragment extends Fragment implements ResourceStatusReceiver.StatusReceiverCallback {

    private static final String TAG = "MessageListFragment";
    private static final int EXPAID_TIME = 3600 * 1 * 1000;

    public static final String CONV_NO = "convNo";
    public static final String TARGET_TYPE = "targetType";
    public static final String TARGET_ID = "targetId";
    public static final String CURRENT_ID = "currentId";
    public static final int SEND_IMAGE = 1;
    public static final int SEND_CAMERA = 2;

    private int mStart = 0;
    private boolean canLoadData = true;
    private String mConversationNo;
    private int mTargetId;
    private String mTargetType;
    private Role mTargetRole;
    private Context mContext;
    private int mCurrentSelectedIndex;
    private MessageAudioPlayer mAudioPlayer;
    private IMMessageReceiver mIMMessageReceiver;
    private MessageSendListener mMessageSendListener;
    private MessageControllerListener mMessageControllerListener;

    protected ResourceStatusReceiver mResourceStatusReceiver;
    protected PtrClassicFrameLayout mPtrFrame;
    protected ListView mMessageListView;
    protected View mContainerView;
    protected MessageInputView mMessageInputView;
    protected MessageListAdapter mListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResourceStatusReceiver = new ResourceStatusReceiver(this);
        mContext.registerReceiver(mResourceStatusReceiver, new IntentFilter(ResourceStatusReceiver.ACTION));
        initParams(getArguments());
        checkConvNo();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getBaseContext();
        mListAdapter = new MessageListAdapter(getActivity().getBaseContext());
        mListAdapter.setCurrentId(IMClient.getClient().getClientId());
        mListAdapter.setmMessageListItemController(getMessageListItemClickListener());
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

    @Override
    public void onResourceDownloadInvoke(int resId, String resUri) {
        MessageEntity messageEntity = IMClient.getClient().getMessageManager().getMessage(resId);
        if (messageEntity == null) {
            return;
        }
        messageEntity.setStatus(PushUtil.MsgDeliveryType.SUCCESS);
        updateMessageReceiveStatus(messageEntity);
    }

    @Override
    public void onResourceStatusInvoke(int resId, String resUri) {
        MessageEntity messageEntity = IMClient.getClient().getMessageManager().getMessage(resId);
        Log.d(TAG, "onResourceStatusInvoke:" + messageEntity.getId());
        if (messageEntity == null) {
            return;
        }

        MessageBody messageBody = new MessageBody(messageEntity);
        IMUploadEntity uploadEntity = IMClient.getClient().getMessageManager()
                .getUploadEntity(messageBody.getMessageId());
        File file = new File(uploadEntity.getSource());
        String body = resUri;
        if (PushUtil.ChatMsgType.AUDIO.equals(messageBody.getType())) {
            body = wrapAudioMessageContent(resUri, getAudioDuration(file.getAbsolutePath()));
        }
        messageBody.setBody(body);
        sendMessageToServer(messageBody);
    }

    private void initParams(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        mConversationNo = bundle.getString(CONV_NO);
        mTargetId = bundle.getInt(TARGET_ID, 0);
        mTargetType = bundle.getString(TARGET_TYPE);

        mTargetRole = IMClient.getClient().getRoleManager().getRole(mTargetType, mTargetId);
        if (mTargetRole.getRid() == 0) {
            Role role = new Role();
            role.setType(mTargetType);
            role.setRid(mTargetId);
            if (IMClient.getClient().getRoleManager().createRole(role) > 0) {
                mTargetRole = role;
            }
        }
    }

    public void setMessageControllerListener(MessageControllerListener listener) {
        this.mMessageControllerListener = listener;
    }

    /**
     * 检查是否有convNo
     */
    private void checkConvNo() {
        ConvEntity convEntity = IMClient.getClient().getConvManager()
                .getConvByTypeAndId(mTargetType, mTargetId, IMClient.getClient().getClientId());
        if (convNoIsEmpty(mConversationNo) && convEntity != null) {
            mConversationNo = convEntity.getConvNo();
        }
    }

    protected boolean convNoIsEmpty(String convNo) {
        return TextUtils.isEmpty(convNo) || "0".equals(convNo);
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
                addMessageList(mStart);
                mPtrFrame.refreshComplete();
                //lvMessage.postDelayed(mListViewSelectRunnable, 100);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canDoRefresh = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                return canLoadData && canDoRefresh;
            }
        });

        mMessageSendListener = getMessageSendListener();
        mMessageInputView.setMessageControllerListener(getMessageControllerListener());
        mMessageInputView.setMessageSendListener(mMessageSendListener);
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
        if (mAudioPlayer != null) {
            mAudioPlayer.stop();
        }
        if (mResourceStatusReceiver != null) {
            mContext.unregisterReceiver(mResourceStatusReceiver);
        }
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
    }

    protected void handleMessage(MessageEntity messageEntity) {
        insertDataToList(messageEntity);
    }

    protected void handleOfflineMessage(List<MessageEntity> messageEntityList) {
        coverMessageEntityStatus(messageEntityList);
        mListAdapter.addList(messageEntityList);
    }

    protected void updateMessageSendStatus(MessageBody messageBody) {
        ContentValues cv = new ContentValues();
        cv.put("status", MessageEntity.StatusType.SUCCESS);
        IMClient.getClient().getMessageManager().updateMessageFieldByUid(messageBody.getMessageId(), cv);

        MessageEntity messageEntity = IMClient.getClient().getMessageManager().getMessageByUID(messageBody.getMessageId());
        mListAdapter.updateItem(messageEntity);
    }

    private void updateMessageReceiveStatus(MessageEntity messageEntity) {
        ContentValues cv = new ContentValues();
        cv.put("status", MessageEntity.StatusType.SUCCESS);
        IMClient.getClient().getMessageManager().updateMessageField(messageEntity.getMsgNo(), cv);
        mListAdapter.updateItem(messageEntity);
    }

    /*
        MessageListItemClickListener
     */
    protected MessageListItemController getMessageListItemClickListener() {
        return new MessageListItemController() {

            @Override
            public void onAudioClick(String audioFile, AudioPlayStatusListener listener) {
                if (mAudioPlayer != null) {
                    mAudioPlayer.stop();
                }
                mAudioPlayer = new MessageAudioPlayer(mContext, audioFile, listener);
                mAudioPlayer.play();
            }

            @Override
            public void onImageClick(String imageUrl) {
                showImageWithFullScreen(imageUrl);
            }

            @Override
            public void onErrorClick(int position) {
            }

            @Override
            public void onAvatarClick(int userId) {
                Role role = IMClient.getClient().getRoleManager().getRole(Destination.USER, userId);
                mMessageControllerListener.onShowUser(role);
            }

            @Override
            public void onContentClick(int position) {
                MessageBody messageBody = new MessageBody(mListAdapter.getItem(position));
                if (PushUtil.ChatMsgType.MULTI.equals(messageBody.getType())) {
                    try {
                        JSONObject jsonObject = new JSONObject(messageBody.getBody());
                        mMessageControllerListener.onShowWebPage(jsonObject.optString("url"));
                    } catch (JSONException e) {
                    }
                }
            }
        };
    }

    private void showImageWithFullScreen(String imageUrl) {
        ArrayList<String> imageUrls = getAllMessageImageUrls();
        int index = 0;
        int size = imageUrls.size();
        for (int i = 0; i < size; i++) {
            if ((imageUrls.get(i).equals(imageUrl))) {
                index = i;
                break;
            }
        }
        mMessageControllerListener.onShowImage(index, imageUrls);
    }

    protected ArrayList<String> getAllMessageImageUrls() {
        ArrayList<String> imagesUrls = new ArrayList<>();
        int size = mListAdapter.getCount();
        for (int i = 0; i < size; i++) {
            MessageBody messageBody = new MessageBody(mListAdapter.getItem(i));
            if (PushUtil.ChatMsgType.IMAGE.equals(messageBody.getType())) {
                imagesUrls.add(messageBody.getBody());
            }
        }
        return imagesUrls;
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
                return new ReceiverInfo(mTargetType, mConversationNo);
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

    protected InputViewControllerListener getMessageControllerListener() {
        return new InputViewControllerListener() {
            @Override
            public void onSelectPhoto() {
                openPictureFromLocal();
            }

            @Override
            public void onTakePhoto() {
                openPictureFromCamera();
            }

            @Override
            public void onInputViewFocus(boolean isFocus) {
                if (isFocus) {
                    mMessageListView.postDelayed(mListViewScrollToBottomRunnable, 50);
                }
            }
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");

        if (convNoIsEmpty(mConversationNo)) {
            mMessageControllerListener.createConvNo(new MessageControllerListener.ConvNoCreateCallback() {
                @Override
                public void onCreateConvNo(String convNo) {
                    if (convNoIsEmpty(convNo)) {
                        Log.d(TAG, "mConversationNo is null");
                        return;
                    }
                    Log.d(TAG, "onCreateConvNo " + convNo);
                    mConversationNo = convNo;
                    checkTargetRole();
                }
            });
            return;
        }

        checkTargetRole();
    }

    private void checkTargetRole() {
        if (mTargetRole.getRid() != 0) {
            checkConvEntity(mTargetRole);
            addMessageList(mStart);
            return;
        }
        mMessageControllerListener.createRole(new MessageControllerListener.RoleUpdateCallback() {
            @Override
            public void onCreateRole(Role role) {
                if (mTargetRole.getRid() == 0) {
                    Log.d(TAG, "mTargetRole is null");
                    return;
                }
                Log.d(TAG, "mTargetRole " + role.getRid());
                mTargetRole = role;
                checkConvEntity(role);
                addMessageList(mStart);
            }
        });
    }

    private void checkConvEntity(Role role) {
        ConvEntity convEntity = IMClient.getClient().getConvManager().getSingleConv(mConversationNo);
        if (convEntity == null) {
            Log.d(TAG, "create ConvNo");
            convEntity = createConvNo(IMClient.getClient().getClientId(), mConversationNo, role);
        }

        if ((System.currentTimeMillis() - convEntity.getUpdatedTime()) > EXPAID_TIME) {
            Log.d(TAG, "update ConvNo");
            convEntity.setAvatar(role.getAvatar());
            convEntity.setTargetName(role.getNickname());
            convEntity.setUpdatedTime(System.currentTimeMillis());
            IMClient.getClient().getConvManager().updateConv(convEntity);
        }
    }

    private ConvEntity createConvNo(int uid, String convNo, Role role) {
        ConvEntity convEntity = new ConvEntity();
        convEntity.setTargetId(role.getRid());
        convEntity.setTargetName(role.getNickname());
        convEntity.setConvNo(convNo);
        convEntity.setType(role.getType());
        convEntity.setAvatar(role.getAvatar());
        convEntity.setUid(uid);
        convEntity.setCreatedTime(System.currentTimeMillis());
        convEntity.setUpdatedTime(0);
        IMClient.getClient().getConvManager().createConv(convEntity);

        return convEntity;
    }

    private void coverMessageEntityStatus(List<MessageEntity> messageEntityList) {
        MessageResourceHelper messageResourceHelper = IMClient.getClient().getResourceHelper();
        for (MessageEntity messageEntity : messageEntityList) {
            if (messageEntity.getStatus() != PushUtil.MsgDeliveryType.SUCCESS
                    && messageResourceHelper.hasTask(messageEntity.getId())) {
                messageEntity.setStatus(PushUtil.MsgDeliveryType.UPLOADING);
            }
        }
    }

    public void reload() {
        mListAdapter.clear();
        mStart = 0;
        addMessageList(mStart);
    }

    protected void addMessageList(int start) {
        List<MessageEntity> messageEntityList = IMClient.getClient().getChatRoom(mConversationNo).getMessageList(start);
        if (messageEntityList == null || messageEntityList.isEmpty()) {
            canLoadData = false;
            return;
        }
        coverMessageEntityStatus(messageEntityList);
        mListAdapter.addList(messageEntityList);
        mStart += messageEntityList.size();
        mMessageListView.postDelayed(mListViewSelectRunnable, 100);
    }

    private void insertDataToList(MessageEntity messageEntity) {
        MessageResourceHelper messageResourceHelper = IMClient.getClient().getResourceHelper();
        if (messageEntity.getStatus() != PushUtil.MsgDeliveryType.SUCCESS
                && messageResourceHelper.hasTask(messageEntity.getId())) {
            messageEntity.setStatus(PushUtil.MsgDeliveryType.UPLOADING);
        }
        mListAdapter.addItem(messageEntity);
        mMessageListView.postDelayed(mListViewScrollToBottomRunnable, 50);
    }

    protected Runnable mListViewScrollToBottomRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMessageListView != null && mMessageListView.getAdapter() != null) {
                mMessageListView.smoothScrollToPosition(mListAdapter.getCount());
            }
        }
    };
    protected Runnable mListViewSelectRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMessageListView != null && mMessageListView.getAdapter() != null) {
                if (mStart <= 10) {
                    mMessageListView.setSelection(mStart);
                    return;
                }
                mMessageListView.setSelectionFromTop(mStart - 10, 50);
            }
        }
    };

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if(requestCode == SEND_IMAGE){
            List<String> pathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (pathList == null || pathList.isEmpty()) {
                return;
            }

            for (String path : pathList) {
                MessageHelper messageHelper = new MessageHelper(mContext);
                File file = messageHelper.compressImageByFile(path);
                messageHelper.compressTumbImageByFile(file.getAbsolutePath(), SystemUtil.getScreenWidth(mContext));
                mMessageSendListener.onSendImage(file);
            }
        }
    }

    /*
        send msg
     */
    private void sendMsg(String content) {
        MessageBody messageBody = createSendMessageBody(content, PushUtil.ChatMsgType.TEXT);
        MessageEntity messageEntity = saveMessageToLoacl(messageBody);
        insertDataToList(messageEntity);
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

    private void uploadMedia(File file, String type) {
        if (file == null || !file.exists()) {
            //CommonUtil.shortToast(mContext, String.format("%s不存在", strType));
            return;
        }
        try {
            String content = file.getAbsolutePath();
            if (PushUtil.ChatMsgType.AUDIO.equals(type)) {
                content = wrapAudioMessageContent(file.getAbsolutePath(), getAudioDuration(file.getAbsolutePath()));
            }

            MessageBody messageBody = createSendMessageBody(content, type);
            MessageEntity messageEntity = saveMessageToLoacl(messageBody);
            insertDataToList(messageEntity);
            IMClient.getClient().getMessageManager().saveUploadEntity(
                    messageBody.getMessageId(), messageBody.getType(), file.getPath()
            );

            UpYunUploadTask upYunUploadTask = new UpYunUploadTask(messageEntity.getId(), mTargetId, file, mMessageControllerListener.getRequestHeaders());
            IMClient.getClient().getResourceHelper().addTask(upYunUploadTask);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void uploadUnYunMedia(String uploadUrl, final File file, HashMap<String, String> headers, final MessageBody messageBody) {

        AsyncHttpRequest post = new AsyncHttpRequest(Uri.parse(uploadUrl), "PUT");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.setHeader(entry.getKey(), entry.getValue());
        }
        MultipartFormDataBody body = new MultipartFormDataBody();
        body.addFilePart("file", file);
        post.setBody(body);
        AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, String result) {
                if (ex != null) {
                    //CommonUtil.longToast(mContext, getString(R.string.request_fail_text));
                    Log.d(TAG, "upload media res to upyun failed");
                    return;
                }
                sendMessageToServer(messageBody);
            }
        });
    }

    public void sendMsgAgain(MessageBody messageBody) {
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

        String path = String.format(ApiConst.PUSH_HOST + ApiConst.GET_UPLOAD_INFO, fromId, file.length(), file.getName());
        AsyncHttpRequest request = new AsyncHttpGet(path);

        for (Map.Entry<String, String> entry : mMessageControllerListener.getRequestHeaders().entrySet()) {
            request.setHeader(entry.getKey(), entry.getValue());
        }
        AsyncHttpClient.getDefaultInstance().executeString(request, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                UpYunUploadCallback upYunUploadCallback = new UpYunUploadCallback(messageBody);
                if (e != null || TextUtils.isEmpty(result)) {
                    //CommonUtil.longToast(mActivity, getString(R.string.request_fail_text));
                    e.printStackTrace();
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

    protected MessageEntity saveMessageToLoacl(MessageBody messageBody) {
        MessageEntity messageEntity = createMessageEntityByBody(messageBody);
        messageEntity = IMClient.getClient().getMessageManager().createMessage(messageEntity);
        updateConv(messageBody);
        return messageEntity;
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
                .addStatus(MessageEntity.StatusType.NONE)
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
        messageBody.getDestination().setNickname(mTargetRole.getNickname());
        messageBody.setSource(new Source(IMClient.getClient().getClientId(), Destination.USER));
        messageBody.getSource().setNickname(IMClient.getClient().getClientName());
        messageBody.setConvNo(mConversationNo);
        messageBody.setMessageId(UUID.randomUUID().toString());

        return messageBody;
    }

    private int getAudioDuration(String audioFile) {
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, Uri.parse(audioFile));
        if (mediaPlayer == null) {
            return 0;
        }
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
