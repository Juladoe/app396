package com.edusoho.kuozhi.imserver.ui;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.R;
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
import com.edusoho.kuozhi.imserver.ui.entity.AudioBody;
import com.edusoho.kuozhi.imserver.ui.entity.PushUtil;
import com.edusoho.kuozhi.imserver.ui.helper.MessageHelper;
import com.edusoho.kuozhi.imserver.ui.helper.MessageResourceHelper;
import com.edusoho.kuozhi.imserver.ui.listener.AudioPlayStatusListener;
import com.edusoho.kuozhi.imserver.ui.listener.DefautlMessageDataProvider;
import com.edusoho.kuozhi.imserver.ui.listener.IMessageDataProvider;
import com.edusoho.kuozhi.imserver.ui.listener.InputViewControllerListener;
import com.edusoho.kuozhi.imserver.ui.listener.MessageControllerListener;
import com.edusoho.kuozhi.imserver.ui.listener.MessageListItemController;
import com.edusoho.kuozhi.imserver.ui.listener.MessageSendListener;
import com.edusoho.kuozhi.imserver.ui.util.AudioUtil;
import com.edusoho.kuozhi.imserver.ui.util.MessageAudioPlayer;
import com.edusoho.kuozhi.imserver.ui.util.ResourceDownloadTask;
import com.edusoho.kuozhi.imserver.ui.util.TaskFeature;
import com.edusoho.kuozhi.imserver.ui.util.UpYunUploadTask;
import com.edusoho.kuozhi.imserver.ui.util.UpdateRoleTask;
import com.edusoho.kuozhi.imserver.ui.view.MessageInputView;
import com.edusoho.kuozhi.imserver.util.SystemUtil;
import com.edusoho.kuozhi.imserver.util.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by suju on 16/8/26.
 */
public class MessageNormalListFragment extends Fragment implements ResourceStatusReceiver.StatusReceiverCallback {

    private static final String TAG = "MessageListFragment";

    public static final String CONV_NO = "convNo";
    public static final String TARGET_TYPE = "targetType";
    public static final String TARGET_ID = "targetId";
    public static final String CURRENT_ID = "currentId";

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
    private IMessageDataProvider mIMessageDataProvider;
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
        if (mIMessageDataProvider == null) {
            setIMessageDataProvider(new DefautlMessageDataProvider());
        }
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

    public void setIMessageDataProvider(IMessageDataProvider provider) {
        this.mIMessageDataProvider = provider;
    }

    @Override
    public void onResourceDownloadInvoke(int resId, String resUri) {
        MessageEntity messageEntity = mIMessageDataProvider.getMessageManager().getMessage(resId);
        if (messageEntity == null) {
            return;
        }
        if (TextUtils.isEmpty(resUri)) {
            updateMessageReceiveStatus(messageEntity, MessageEntity.StatusType.FAILED);
            return;
        }

        MessageBody messageBody = new MessageBody(messageEntity);
        if (PushUtil.ChatMsgType.AUDIO.equals(messageBody.getType())) {
            updateMessageReceiveStatus(messageEntity, MessageEntity.StatusType.UNREAD);
            return;
        }
        updateMessageReceiveStatus(messageEntity, MessageEntity.StatusType.SUCCESS);
    }

    @Override
    public void onResourceStatusInvoke(int resId, String resUri) {
        MessageEntity messageEntity = mIMessageDataProvider.getMessageManager().getMessage(resId);
        if (messageEntity == null) {
            return;
        }

        if (resUri == null || TextUtils.isEmpty(resUri)) {
            updateMessageReceiveStatus(messageEntity, MessageEntity.StatusType.FAILED);
            return;
        }

        MessageBody messageBody = new MessageBody(messageEntity);
        String body = resUri;
        if (PushUtil.ChatMsgType.AUDIO.equals(messageBody.getType())) {
            AudioBody audioBody = AudioUtil.getAudioBody(messageBody.getBody());
            audioBody.setFile(resUri);
            body = audioBody.toString();
        }
        messageBody.setBody(body);
        sendMessageToServer(messageBody);
    }

    public static final int MESSAGE_SELECT_LAST = 0;
    public static final int MESSAGE_SELECT_POSTION = 1;

    protected Handler mUpdateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_SELECT_LAST:
                    mMessageListView.setSelection(mListAdapter.getCount() - 1);
                    break;
                case MESSAGE_SELECT_POSTION:
                    mMessageListView.setSelectionFromTop(mListAdapter.getCount() - msg.arg1, 100);
                    break;
            }
        }
    };

    private void initParams(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        mConversationNo = bundle.getString(CONV_NO);
        mTargetId = bundle.getInt(TARGET_ID, 0);
        mTargetType = bundle.getString(TARGET_TYPE);

        mTargetRole = IMClient.getClient().getRoleManager().getRole(mTargetType, mTargetId);
    }

    public void setMessageControllerListener(MessageControllerListener listener) {
        this.mMessageControllerListener = listener;
    }

    /**
     * 检查是否有convNo
     */
    private void checkConvNo() {
        ConvEntity convEntity = IMClient.getClient().getConvManager()
                .getConvByTypeAndId(mTargetType, mTargetId);
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
                mPtrFrame.refreshComplete();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        insertDataToMessageList(mStart);
                    }
                }, 300);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canDoRefresh = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                return canLoadData && canDoRefresh;
            }
        });

        mMessageSendListener = getMessageSendListener();
        mMessageInputView.setMessageSendListener(mMessageSendListener);
        mMessageInputView.setMessageControllerListener(getMessageControllerListener());
        initListViewListener();
    }

    protected void initListViewListener() {
        mMessageListView.setOnItemLongClickListener(getOnItemLongClickListener());
        mMessageListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
    }

    protected AdapterView.OnItemLongClickListener getOnItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view.getTag() != null) {
                    mCurrentSelectedIndex = i;
                    showItemMenuDialog();
                    return false;
                }
                return false;
            }
        };
    }

    protected void showItemMenuDialog() {
        registerForContextMenu(mMessageListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        new MenuInflater(mContext).inflate(R.menu.message_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_copy);
        MessageBody messageBody = getSelectedMessageBody();
        if (menuItem == null || messageBody == null) {
            return;
        }
        menuItem.setVisible(PushUtil.ChatMsgType.TEXT.equals(messageBody.getType()));
    }

    private void copyDataToClipboard(String text) {
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(text);
        SystemUtil.toast(mContext, "已复制");
    }

    private MessageBody getSelectedMessageBody() {
        MessageEntity messageEntity = mListAdapter.getItem(mCurrentSelectedIndex);
        if (messageEntity == null) {
            return null;
        }
        return new MessageBody(messageEntity);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_copy) {
            MessageBody messageBody = getSelectedMessageBody();
            if (messageBody != null) {
                copyDataToClipboard(messageBody.getBody());
            }
        } else if (id == R.id.menu_replay) {
            MessageBody messageBody = getSelectedMessageBody();
            if (messageBody == null) {
                return true;
            }
            JSONObject data = new JSONObject();
            try {
                data.put("type", messageBody.getType());
                data.put("fromType", Destination.USER);
                data.put("title", "确定转发给:");
                data.put("content", messageBody.getBody());
                data.put("source", "self");
                data.put("id", messageBody.getDestination().getId());
            } catch (JSONException e) {
            }

            Bundle bundle = new Bundle();
            bundle.putString("data", data.toString());
            bundle.putString("activityName", "ChatSelectFragment");
            mMessageControllerListener.onShowActivity(bundle);
        } else if (id == R.id.menu_delete) {
            MessageBody messageBody = getSelectedMessageBody();
            if (messageBody == null) {
                return true;
            }
            mListAdapter.removeItem(messageBody.getMid());
            mIMessageDataProvider.getMessageManager().deleteById(messageBody.getMid());
        }
        return super.onContextItemSelected(item);
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
        mListAdapter.destory();
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
        mIMessageDataProvider.getMessageManager().updateMessageFieldByUid(messageBody.getMessageId(), cv);

        MessageEntity messageEntity = mIMessageDataProvider.getMessageManager().getMessageByUID(messageBody.getMessageId());
        mListAdapter.updateItem(messageEntity);
    }

    private void updateMessageReceiveStatus(MessageEntity messageEntity, int status) {
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        mIMessageDataProvider.getMessageManager().updateMessageFieldByMsgNo(messageEntity.getMsgNo(), cv);
        messageEntity.setStatus(status);
        mListAdapter.updateItem(messageEntity);
    }

    /*
        MessageListItemClickListener
     */
    protected MessageListItemController getMessageListItemClickListener() {
        return new MessageListItemController() {

            @Override
            public void onAudioClick(int position, String audioFile, AudioPlayStatusListener listener) {
                if (mAudioPlayer != null) {
                    mAudioPlayer.stop();
                }
                mAudioPlayer = new MessageAudioPlayer(mContext, audioFile, listener);
                mAudioPlayer.play();

                MessageEntity messageEntity = mListAdapter.getItem(position);
                if (messageEntity.getStatus() == MessageEntity.StatusType.SUCCESS) {
                    return;
                }
                updateMessageReceiveStatus(messageEntity, MessageEntity.StatusType.SUCCESS);
                mListAdapter.updateItem(messageEntity);
            }

            @Override
            public void onImageClick(String imageUrl) {
                showImageWithFullScreen(imageUrl);
            }

            @Override
            public void onErrorClick(int position) {
                handleErrorMessage(mListAdapter.getItem(position));
            }

            @Override
            public void onAvatarClick(int userId) {
                Role role = IMClient.getClient().getRoleManager().getRole(Destination.USER, userId);
                role.setRid(userId);
                mMessageControllerListener.onShowUser(role);
            }

            @Override
            public void onUpdateRole(final String type, final int rid) {
                Role role = IMClient.getClient().getRoleManager().getRole(type, rid);
                if (role.getRid() != 0) {
                    return;
                }
                new Handler().postDelayed(new UpdateRoleRunnable(type, rid), 200);
            }

            @Override
            public void onContentClick(int position) {
                MessageBody messageBody = new MessageBody(mListAdapter.getItem(position));
                switch (messageBody.getType()) {
                    case PushUtil.ChatMsgType.MULTI:
                        try {
                            JSONObject jsonObject = new JSONObject(messageBody.getBody());
                            mMessageControllerListener.onShowWebPage(jsonObject.optString("url"));
                        } catch (JSONException e) {
                        }
                        break;
                    case PushUtil.ChatMsgType.PUSH:
                        try {
                            JSONObject jsonObject = new JSONObject(messageBody.getBody());
                            String type = jsonObject.optString("type");
                            if (PushUtil.CourseType.QUESTION_CREATED.equals(type)) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("target_id", jsonObject.optInt("course"));
                                bundle.putString("target_type", "course");
                                bundle.putInt("thread_id", jsonObject.optInt("threadId"));
                                bundle.putString("activity_type", "thread.post");
                                mMessageControllerListener.onShowActivity(bundle);
                            }
                        } catch (JSONException e) {
                        }
                }
            }
        };
    }

    private void handleErrorMessage(MessageEntity messageEntity) {
        MessageBody messageBody = new MessageBody(messageEntity);
        switch (messageBody.getType()) {
            case PushUtil.ChatMsgType.TEXT:
            case PushUtil.ChatMsgType.MULTI:
                sendMessageToServer(messageBody);
                break;
            case PushUtil.ChatMsgType.AUDIO:
                messageEntity.setUid(messageBody.getMessageId());
                if (IMClient.getClient().getClientId() == messageBody.getSource().getId()) {
                    sendMediaMessageAgain(messageEntity);
                    return;
                }
                //receive
                receiveAudioMessageAgain(messageBody);
                break;
            case PushUtil.ChatMsgType.IMAGE:
                messageEntity.setUid(messageBody.getMessageId());
                if (IMClient.getClient().getClientId() == messageBody.getSource().getId()) {
                    sendImageMediaMessageAgain(messageEntity);
                    return;
                }
                receiveImageMessageAgain(messageBody);
        }
    }

    private void sendImageMediaMessageAgain(MessageEntity messageEntity) {
        IMUploadEntity uploadEntity = mIMessageDataProvider.getMessageManager()
                .getUploadEntity(messageEntity.getUid());
        if (uploadEntity == null) {
            SystemUtil.toast(mContext, "媒体文件不存在,请重新发送消息");
            return;
        }
        File audioFile = new File(uploadEntity.getSource());
        uploadImageAgain(audioFile, messageEntity);
    }

    private void sendMediaMessageAgain(MessageEntity messageEntity) {
        IMUploadEntity uploadEntity = mIMessageDataProvider.getMessageManager()
                .getUploadEntity(messageEntity.getUid());
        if (uploadEntity == null) {
            SystemUtil.toast(mContext, "媒体文件不存在,请重新发送消息");
            return;
        }
        File audioFile = new File(uploadEntity.getSource());
        uploadAudioAgain(audioFile, TimeUtil.getAudioDuration(mContext, uploadEntity.getSource()), messageEntity);
    }

    private void uploadMediaAgain(File file, MessageEntity messageEntity) {
        try {
            messageEntity.setStatus(MessageEntity.StatusType.UPLOADING);
            mListAdapter.updateItem(messageEntity);

            UpYunUploadTask upYunUploadTask = new UpYunUploadTask(messageEntity.getId(), mTargetId, file, mMessageControllerListener.getRequestHeaders());
            IMClient.getClient().getResourceHelper().addTask(upYunUploadTask);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void receiveAudioMessageAgain(MessageBody messageBody) {
        AudioBody audioBody = AudioUtil.getAudioBody(messageBody.getBody());
        try {
            File realFile = new MessageHelper(mContext).createAudioFile(audioBody.getFile());
            ResourceDownloadTask downloadTask = new ResourceDownloadTask(mContext, messageBody.getMid(), audioBody.getFile(), realFile);
            IMClient.getClient().getResourceHelper().addTask(downloadTask);
        } catch (IOException e) {
            SystemUtil.toast(mContext, "音频文件不存在,请重新发送语音消息");
        }
    }

    private void receiveImageMessageAgain(MessageBody messageBody) {
        try {
            File realFile = new MessageHelper(mContext).createImageFile(messageBody.getBody());
            ResourceDownloadTask downloadTask = new ResourceDownloadTask(mContext, messageBody.getMid(), messageBody.getBody(), realFile);
            IMClient.getClient().getResourceHelper().addTask(downloadTask);
        } catch (IOException e) {
            SystemUtil.toast(mContext, "图片文件不存在,请重新发送语音消息");
        }
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
                IMClient.getClient().getConvManager().clearReadCount(mConversationNo);
                return true;
            }

            @Override
            public boolean onOfflineMsgReceiver(List<MessageEntity> messageEntities) {
                handleOfflineMessage(messageEntities);
                IMClient.getClient().getConvManager().clearReadCount(mConversationNo);
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
            public void onSendAudio(File audioFile, int audioLength) {
                if (audioFile == null || !audioFile.exists()) {
                    SystemUtil.toast(mContext, "音频文件不存在,请重新录制");
                    return;
                }
                uploadAudio(audioFile, audioLength);
            }

            @Override
            public void onStartRecordAudio() {
            }

            @Override
            public void onStopRecordAudio() {
            }

            @Override
            public void onSendImage(File imageFile) {
                uploadImage(imageFile);
            }
        };
    }

    private void handleSelectPhotoResult(List<String> pathList) {
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

    protected InputViewControllerListener getMessageControllerListener() {
        return new InputViewControllerListener() {
            @Override
            public void onSelectPhoto() {
                mMessageControllerListener.selectPhoto(new MessageControllerListener.PhotoSelectCallback() {
                    @Override
                    public void onSelected(List<String> pathList) {
                        handleSelectPhotoResult(pathList);
                    }
                });
            }

            @Override
            public void onTakePhoto() {
                mMessageControllerListener.takePhoto(new MessageControllerListener.PhotoSelectCallback() {
                    @Override
                    public void onSelected(List<String> pathList) {
                        handleSelectPhotoResult(pathList);

                    }
                });
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
        mMessageControllerListener.createRole(mTargetType, mTargetId, new MessageControllerListener.RoleUpdateCallback() {
            @Override
            public void onCreateRole(Role role) {
                if (role.getRid() == 0) {
                    Log.d(TAG, "mTargetRole is null");
                    return;
                }
                Log.d(TAG, "mTargetRole " + role.getRid());
                mTargetRole = role;
                IMClient.getClient().getRoleManager().createRole(role);
                checkConvEntity(role);
                addMessageList(mStart);
            }
        });
    }

    private void checkConvEntity(Role role) {
        mIMessageDataProvider.updateConvEntity(mConversationNo, role);
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

    public void updateListByEntity(MessageEntity messageEntity) {
        mListAdapter.updateItem(messageEntity);
    }

    protected void insertDataToMessageList(int start) {
        List<MessageEntity> messageEntityList = IMClient.getClient().getChatRoom(mConversationNo).getMessageList(start);
        if (messageEntityList == null || messageEntityList.isEmpty()) {
            canLoadData = false;
            return;
        }
        coverMessageEntityStatus(messageEntityList);
        mListAdapter.insertList(messageEntityList);
        Message msg = mUpdateHandler.obtainMessage(MESSAGE_SELECT_POSTION);
        msg.arg1 = mStart;
        mUpdateHandler.sendMessage(msg);
        mStart += messageEntityList.size();
    }

    protected void addMessageList(int start) {
        List<MessageEntity> messageEntityList = mIMessageDataProvider.getMessageList(mConversationNo, start);
        if (messageEntityList == null || messageEntityList.isEmpty()) {
            canLoadData = false;
            return;
        }
        coverMessageEntityStatus(messageEntityList);
        mListAdapter.addList(messageEntityList);
        if (start == 0) {
            mUpdateHandler.obtainMessage(MESSAGE_SELECT_LAST).sendToTarget();
        } else {
            Message msg = mUpdateHandler.obtainMessage(MESSAGE_SELECT_POSTION);
            msg.arg1 = mStart;
            mUpdateHandler.sendMessage(msg);
        }
        mStart += messageEntityList.size();
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

    /*
        send msg
     */
    private void sendMsg(String content) {
        MessageBody messageBody = createSendMessageBody(content, PushUtil.ChatMsgType.TEXT);
        MessageEntity messageEntity = saveMessageToLocal(messageBody);
        messageEntity.setStatus(MessageEntity.StatusType.UPLOADING);
        insertDataToList(messageEntity);
        sendMessageToServer(messageBody);
    }

    protected void sendMessageToServer(MessageBody messageBody) {
        mIMessageDataProvider.sendMessage(mConversationNo, messageBody);
    }

    private void uploadAudio(File file, int audioLength) {
        String content = wrapAudioMessageContent(file.getAbsolutePath(), audioLength);
        MessageBody messageBody = createSendMessageBody(content, PushUtil.ChatMsgType.AUDIO);
        uploadMedia(file, messageBody);
    }

    private void uploadAudioAgain(File file, int audioLength, MessageEntity messageEntity) {
        String content = wrapAudioMessageContent(file.getAbsolutePath(), audioLength);
        uploadMediaAgain(file, messageEntity);
    }

    private void uploadImage(File file) {
        MessageBody messageBody = createSendMessageBody(file.getAbsolutePath(), PushUtil.ChatMsgType.IMAGE);
        uploadMedia(file, messageBody);
    }

    private void uploadImageAgain(File file, MessageEntity messageEntity) {
        uploadMediaAgain(file, messageEntity);
    }

    private void uploadMedia(File file, MessageBody messageBody) {
        try {
            MessageEntity messageEntity = saveMessageToLocal(messageBody);
            messageEntity.setStatus(MessageEntity.StatusType.UPLOADING);
            insertDataToList(messageEntity);
            mIMessageDataProvider.getMessageManager().saveUploadEntity(
                    messageBody.getMessageId(), messageBody.getType(), file.getPath()
            );

            UpYunUploadTask upYunUploadTask = new UpYunUploadTask(messageEntity.getId(), mTargetId, file, mMessageControllerListener.getRequestHeaders());
            IMClient.getClient().getResourceHelper().addTask(upYunUploadTask);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected MessageEntity saveMessageToLocal(MessageBody messageBody) {
        return mIMessageDataProvider.createMessageEntity(messageBody);
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

    private String wrapAudioMessageContent(String audioFilePath, int audioTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("f", audioFilePath);
            jsonObject.put("d", audioTime);
        } catch (JSONException e) {
        }

        return jsonObject.toString();
    }

    private class UpdateRoleRunnable implements Runnable {

        private String type;
        private int rid;

        public UpdateRoleRunnable(String type, int rid) {
            this.type = type;
            this.rid = rid;
        }

        @Override
        public void run() {
            UpdateRoleTask task = new UpdateRoleTask(type, rid, new UpdateRoleTask.TaskCallback() {
                @Override
                public void run(final TaskFeature taskFeature) {
                    mMessageControllerListener.createRole(type, rid, new MessageControllerListener.RoleUpdateCallback() {
                        @Override
                        public void onCreateRole(Role role) {
                            if (role.getRid() != 0) {
                                IMClient.getClient().getRoleManager().createRole(role);
                                Log.d(TAG, "create role:" + rid);
                                mListAdapter.notifyDataSetInvalidated();
                                taskFeature.success(null);
                                return;
                            }
                            taskFeature.fail();
                        }
                    });
                }
            });
            IMClient.getClient().getResourceHelper().addTask(task);
        }
    }
}
