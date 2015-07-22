package com.edusoho.kuozhi.v3.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.ChatAdapter;
import com.edusoho.kuozhi.v3.broadcast.AudioDownloadReceiver;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.result.PushResult;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.AudioRecord;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.EduSohoIconView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
public class ChatActivity extends ActionBarBaseActivity implements View.OnClickListener, ChatAdapter.ImageErrorClick {

    //region Field
    public static final String TAG = "ChatActivity";
    public static final String CHAT_DATA = "chat_data";
    public static final String FROM_ID = "from_id";
    public static final String TITLE = "title";
    private static final int IMAGE_SIZE = 1024 * 500;

    private static final int SEND_VOICE = 3;
    private static final int SEND_IMAGE = 1;
    private static final int SEND_CAMERA = 2;

    private Button btnVoice;
    private Button btnKeyBoard;
    private EditText etSend;
    private ListView lvMessage;
    private Button tvSend;
    private EduSohoIconView ivAddMedia;
    private EduSohoIconView ivPhoto;
    private EduSohoIconView ivCamera;
    private ChatAdapter mAdapter;
    private PtrClassicFrameLayout mPtrFrame;
    private View viewMediaLayout;
    private View viewPressToSpeak;
    private View viewMsgInput;
    private TextView tvSpeak;
    private TextView tvSpeakHint;
    private View mViewSpeakContainer;
    private ImageView ivRecordImage;

    private float mPressDownY;

    private LoadDialog mAudioLoadDialog;
    private Vibrator mVibrator;
    private AudioDownloadReceiver mAudioDownloadReceiver;
    /**
     * 根据手纸滑动距离是否保存
     */
    private boolean mIsSaveAudio;

    private ChatDataSource mChatDataSource;
    private int mSendTime;
    private int mStart = 0;
    private static final int LIMIT = 15;
    private File mCameraFile;

    public static int CurrentFromId = 0;
    /**
     * 对方的userInfo信息;
     */
    private User mFromUserInfo;
    private int mFromId;
    private int mToId;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mAudioDownloadReceiver, intentFilter);
    }

    private void initView() {
        mAudioDownloadReceiver = new AudioDownloadReceiver();
        etSend = (EditText) findViewById(R.id.et_send_content);
        etSend.addTextChangedListener(msgTextWatcher);
        tvSend = (Button) findViewById(R.id.tv_send);
        tvSend.setOnClickListener(this);
        lvMessage = (ListView) findViewById(R.id.lv_messages);
        ivAddMedia = (EduSohoIconView) findViewById(R.id.iv_show_media_layout);
        ivAddMedia.setOnClickListener(this);
        viewMediaLayout = findViewById(R.id.ll_media_layout);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(this);
        btnKeyBoard = (Button) findViewById(R.id.btn_set_mode_keyboard);
        btnKeyBoard.setOnClickListener(this);
        viewPressToSpeak = findViewById(R.id.rl_btn_press_to_speak);
        viewPressToSpeak.setOnClickListener(this);
        viewMsgInput = findViewById(R.id.rl_msg_input);
        ivPhoto = (EduSohoIconView) findViewById(R.id.iv_image);
        ivPhoto.setOnClickListener(this);
        ivCamera = (EduSohoIconView) findViewById(R.id.iv_camera);
        ivCamera.setOnClickListener(this);
        viewPressToSpeak.setOnTouchListener(mVoiceRecordingTouchListener);
        tvSpeak = (TextView) findViewById(R.id.tv_speak);
        tvSpeakHint = (TextView) findViewById(R.id.tv_speak_hint);
        ivRecordImage = (ImageView) findViewById(R.id.iv_voice_volume);
        mViewSpeakContainer = findViewById(R.id.recording_container);
        mViewSpeakContainer.bringToFront();
        initData();
        mAdapter = new ChatAdapter(mContext, getChatList(0));
        mAdapter.setSendImageClickListener(this);
        lvMessage.setAdapter(mAdapter);
        mAudioDownloadReceiver.setChatAdapter(mAdapter);
        mStart = mAdapter.getCount();
        lvMessage.post(mListViewSelectRunnable);
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mStart = mAdapter.getCount();
                mAdapter.addItems(getChatList(mStart));
                mPtrFrame.refreshComplete();
                lvMessage.postDelayed(mListViewSelectRunnable, 100);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        lvMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewMediaLayout.setVisibility(View.GONE);
                return false;
            }
        });
        sendNewFragment2UpdateItem();
        mAudioLoadDialog = LoadDialog.create(mContext);
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private Runnable mListViewSelectRunnable = new Runnable() {
        @Override
        public void run() {
            lvMessage.setSelection(mStart);
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
        mAdapter.clear();
        mAdapter.addItems(getChatList(0));
        mStart = mAdapter.getCount();
        lvMessage.post(mListViewSelectRunnable);
        mAdapter.setSendImageClickListener(this);
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
        CurrentFromId = mFromId;
        if (mChatDataSource == null) {
            mChatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        }
        initCacheFolder();
        getFriendUserInfo();
    }

    private ArrayList<Chat> getChatList(int start) {
        String selectSql = String.format("(FROMID = %d AND TOID=%d) OR (TOID=%d AND FROMID=%d)", mFromId, mToId, mFromId, mToId);
        ArrayList<Chat> mList = mChatDataSource.getChats(start, LIMIT, selectSql);
        Collections.reverse(mList);
        return mList;
    }

    private void sendNewFragment2UpdateItem() {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.FROM_ID, mFromId);
        app.sendMsgToTarget(NewsFragment.UPDATE_UNREAD_MSG, bundle, NewsFragment.class);
    }

    private void sendMsg(String content) {
        mSendTime = (int) (System.currentTimeMillis() / 1000);
        final Chat chat = new Chat(app.loginUser.id, mFromId, app.loginUser.nickname, app.loginUser.mediumAvatar,
                etSend.getText().toString(), Chat.FileType.TEXT.toString().toLowerCase(), mSendTime);

        RequestUrl requestUrl = app.bindPushUrl(String.format(Const.SEND, app.loginUser.id, mFromId));
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", app.loginUser.nickname);
        params.put("type", "text");
        params.put("content", content);
        params.put("custom", gson.toJson(getCustomContent(Chat.FileType.TEXT, TypeBusinessEnum.FRIEND)));
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                PushResult result = parseJsonValue(response, new TypeToken<PushResult>() {
                });
                if (result.result.equals("success")) {
                    chat.id = result.id;
                    mChatDataSource.create(chat);
                    mAdapter.addOneChat(chat);
                    etSend.setText("");
                    etSend.requestFocus();

                    WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
                    message.setTitle(mFromUserInfo.nickname);
                    message.setContent(chat.content);
                    CustomContent cc = getCustomContent(Chat.FileType.TEXT, TypeBusinessEnum.FRIEND);
                    cc.setFromId(mFromId);
                    cc.setImgUrl(mFromUserInfo.mediumAvatar);
                    message.setCustomContent(gson.toJson(cc));
                    message.isForeground = true;
                    notifyNewList2Update(message);
                }
            }
        }, null);
    }

    private void sendMediaMsg(String url, final Chat chat, Chat.FileType type) {
        RequestUrl requestUrl = app.bindPushUrl(String.format(Const.SEND, app.loginUser.id, mFromId));
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", app.loginUser.nickname);
        params.put("type", type.getName());
        params.put("content", url);
        params.put("custom", gson.toJson(getCustomContent(type, TypeBusinessEnum.FRIEND)));
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                PushResult result = parseJsonValue(response, new TypeToken<PushResult>() {
                });
                if (result.result.equals("success")) {
                    chat.id = result.id;
                    updateSendMsgToListView(Chat.Delivery.SUCCESS, chat);
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
     * 通知NewsFragment
     *
     * @param message xg消息
     */
    private void notifyNewList2Update(WrapperXGPushTextMessage message) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.CHAT_DATA, message);
        bundle.putInt(Const.ADD_CHAT_MSG_TYPE, NewsFragment.HANDLE_SEND_MSG);
        app.sendMsgToTarget(Const.ADD_CHAT_MSG, bundle, NewsFragment.class);
    }

    private CustomContent getCustomContent(Chat.FileType fileType, TypeBusinessEnum typeBusiness) {
        CustomContent customContent = new CustomContent();
        customContent.setFromId(app.loginUser.id);
        customContent.setNickname(app.loginUser.nickname);
        customContent.setImgUrl(app.loginUser.mediumAvatar);
        customContent.setTypeMsg(fileType.getName());
        customContent.setCreatedTime(mSendTime);
        customContent.setTypeBusiness(typeBusiness.getName());
        return customContent;
    }

    @Override
    public void uploadMediaAgain(File file, Chat chat, String strType) {
        uploadMediaAgain(file, chat, Chat.FileType.IMAGE, strType);
    }

    //region Touch, Click Listener etc.
    private View.OnTouchListener mVoiceRecordingTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    try {
                        if (!CommonUtil.isExitsSdcard()) {
                            CommonUtil.longToast(mContext, "发送语音需要sdcard");
                            v.setPressed(false);
                            return false;
                        }
                        mPressDownY = event.getY();
                        mAudioLoadDialog.show();

                        //录音
                        AudioRecord.getInstance().ready(new NormalCallback() {
                            @Override
                            public void success(Object obj) {
                                mViewSpeakContainer.setVisibility(View.VISIBLE);
                                tvSpeak.setText(getString(R.string.hand_up_and_end));
                                tvSpeakHint.setBackgroundResource(R.drawable.speak_hint_transparent_bg);
                                ivRecordImage.setImageResource(R.drawable.record_animate_1);
                                mAudioLoadDialog.dismiss();
                                mVibrator.vibrate(50);
                                AudioRecord.getInstance().setSpeakerImageView(ivRecordImage).start();
                                //mThread.start();
                            }
                        });
                    } catch (Exception e) {
                        mAudioLoadDialog.dismiss();
                        AudioRecord.getInstance().clear();
                        e.printStackTrace();
                        v.setPressed(false);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float mPressMoveY = event.getY();
                    if (Math.abs(mPressDownY - mPressMoveY) > EdusohoApp.screenH * 0.15) {
                        tvSpeak.setText(getString(R.string.hand_up_and_exit));
                        tvSpeakHint.setText(getString(R.string.hand_up_and_exit));
                        tvSpeakHint.setBackgroundResource(R.drawable.speak_hint_bg);
                        ivRecordImage.setImageResource(R.drawable.record_cancel);
                        mIsSaveAudio = false;
                    } else {
                        tvSpeakHint.setText(getString(R.string.hand_move_up_and_send_cancel));
                        tvSpeakHint.setBackgroundResource(R.drawable.speak_hint_transparent_bg);
                        tvSpeak.setText(getString(R.string.hand_up_and_end));
                        ivRecordImage.setImageResource(R.drawable.record_animate_1);
                        mIsSaveAudio = true;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    try {
                        tvSpeak.setText(getString(R.string.hand_press_and_speak));
                        File mAudioRecord = AudioRecord.getInstance().stop(mIsSaveAudio);
                        int audioLength = AudioRecord.getInstance().getAudioLength();
                        if (mIsSaveAudio) {
                            if (audioLength >= 1) {
                                mViewSpeakContainer.setVisibility(View.GONE);
                                //uploadMedia(mAudioRecord, Chat.FileType.AUDIO, Const.MEDIA_AUDIO);
                                Log.d(TAG, "上传成功");
                            } else if (audioLength < 1) {
                                ivRecordImage.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvSpeakHint.setText(getString(R.string.audio_length_too_short));
                                        tvSpeakHint.setBackgroundResource(R.drawable.speak_hint_transparent_bg);
                                        ivRecordImage.setImageResource(R.drawable.record_duration_short);
                                    }
                                }, 300);
                                v.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mViewSpeakContainer.setVisibility(View.GONE);
                                    }
                                }, 500);
                                Log.d(TAG, "录音时间过短");
                            }
                        } else {
                            mViewSpeakContainer.setVisibility(View.GONE);
                        }
                        mIsSaveAudio = true;
                        AudioRecord.getInstance().clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    };


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_show_media_layout) {
            //加号，显示多媒体框
            if (viewMediaLayout.getVisibility() == View.GONE) {
                viewMediaLayout.setVisibility(View.VISIBLE);
            } else {
                viewMediaLayout.setVisibility(View.GONE);
            }
        } else if (v.getId() == R.id.tv_send) {
            //发送消息
            if (etSend.getText().length() == 0) {
                return;
            }
            sendMsg(etSend.getText().toString());

        } else if (v.getId() == R.id.btn_voice) {
            //语音
            viewMediaLayout.setVisibility(View.GONE);
            btnKeyBoard.setVisibility(View.VISIBLE);
            btnVoice.setVisibility(View.GONE);
            viewMsgInput.setVisibility(View.GONE);
            viewPressToSpeak.setVisibility(View.VISIBLE);

        } else if (v.getId() == R.id.btn_set_mode_keyboard) {
            //键盘
            viewMediaLayout.setVisibility(View.GONE);
            btnVoice.setVisibility(View.VISIBLE);
            viewPressToSpeak.setVisibility(View.GONE);
            viewMsgInput.setVisibility(View.VISIBLE);
            btnKeyBoard.setVisibility(View.GONE);
        } else if (v.getId() == R.id.rl_btn_press_to_speak) {
            //长按发送语音
            viewMediaLayout.setVisibility(View.GONE);
        } else if (v.getId() == R.id.iv_image) {
            //选择图片
            openPictureFromLocal();
        } else if (v.getId() == R.id.iv_camera) {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mCameraFile = new File(EdusohoApp.getWorkSpace().getPath() + Const.UPLOAD_IMAGE_CACHE_FILE + "/" + System.currentTimeMillis());
                if (mCameraFile.createNewFile()) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
                    startActivityForResult(intent, SEND_CAMERA);
                } else {
                    CommonUtil.shortToast(mContext, "照片生成失败");
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

    TextWatcher msgTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                tvSend.setVisibility(View.VISIBLE);
                ivAddMedia.setVisibility(View.GONE);
            } else {
                ivAddMedia.setVisibility(View.VISIBLE);
                tvSend.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    //endregion

    /**
     * 从图库获取图片
     */
    private void openPictureFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, SEND_IMAGE);
    }

    /**
     * 选择图片并压缩
     *
     * @param selectedImage 原图
     * @return
     */
    private File selectPicture(Uri selectedImage) {
        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
        String picturePath = null;
        if (cursor != null) {
            cursor.moveToFirst();
            //int pictureIndex = cursor.getColumnIndex("_data");
            picturePath = cursor.getString(1);
            cursor.close();

            if (TextUtils.isEmpty(picturePath)) {
                CommonUtil.shortToast(mContext, "找不到图片");
                return null;
            }
        }
        File file = new File(picturePath);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
        } catch (FileNotFoundException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return compressImage(bitmap, file);
    }

    /**
     * 图片压缩
     *
     * @param bitmap original bitmap
     * @param file   original image file
     * @return compressed image file
     */
    private File compressImage(Bitmap bitmap, File file) {
        File compressedFile;
        try {
            if (bitmap.getWidth() > EdusohoApp.screenW * 0.4f) {
                bitmap = AppUtil.scaleImage(bitmap, EdusohoApp.screenW * 0.4f, AppUtil.getImageDegree(file.getPath()));
            }

            if (AppUtil.getImageSize(bitmap) > IMAGE_SIZE) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap = AppUtil.compressImage(bitmap, baos);
                compressedFile = AppUtil.convertBitmap2File(bitmap, EdusohoApp.getWorkSpace() + Const.UPLOAD_IMAGE_CACHE_FILE + "/" + System.currentTimeMillis());
            } else {
                compressedFile = copyImageFileToCache(file);
            }

            AppUtil.convertBitmap2File(bitmap, EdusohoApp.getWorkSpace().getPath() + Const.UPLOAD_IMAGE_CACHE_THUMB_FILE + "/" + compressedFile.getName());
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
            return null;
        }
        return compressedFile;
    }

    /**
     * 上传图片
     *
     * @param file upload file
     */
    private void uploadMedia(final File file, final Chat.FileType type, String strType) {
        try {
            if (file == null || !file.exists()) {
                CommonUtil.shortToast(mContext, String.format("%s不存在", strType));
                return;
            }
            mSendTime = (int) (System.currentTimeMillis() / 1000);
            final Chat chat = new Chat(app.loginUser.id, mFromId, app.loginUser.nickname, app.loginUser.mediumAvatar,
                    file.getPath(), type.getName(), mSendTime);
            chat.content = file.getPath();

            //生成New页面的消息并通知更改
            WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
            message.setTitle(mFromUserInfo.nickname);
            message.setContent(String.format("[%s]", strType));
            CustomContent cc = getCustomContent(type, TypeBusinessEnum.FRIEND);
            cc.setFromId(mFromId);
            cc.setImgUrl(mFromUserInfo.mediumAvatar);
            message.setCustomContent(gson.toJson(cc));
            message.isForeground = true;
            notifyNewList2Update(message);

            addSendMsgToListView(Chat.Delivery.UPLOADING, chat);

            RequestUrl url = app.bindNewApiUrl(Const.UPLOAD_MEDIA, true);
            url.setMuiltParams(new Object[]{
                    "file", file
            });
            String contentType = type == Chat.FileType.IMAGE ? Const.IMAGE_CONTENT_TYPE : Const.AUDIO_CONTENT_TYPE;

            ajaxPostMultiUrl(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String imageUrl = app.host + "/" + jsonObject.getString("uri");
                        sendMediaMsg(imageUrl, chat, type);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    updateSendMsgToListView(Chat.Delivery.FAILED, chat);
                    CommonUtil.shortToast(mContext, "发送失败");
                }
            }, contentType);
            viewMediaLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void uploadMediaAgain(File file, final Chat chat, final Chat.FileType type, final String strType) {
        if (file == null || !file.exists()) {
            CommonUtil.shortToast(mContext, String.format("%s不存在", strType));
            return;
        }

        RequestUrl url = app.bindNewApiUrl(Const.UPLOAD_MEDIA, true);
        url.setMuiltParams(new Object[]{
                "file", file
        });
        String contentType = type == Chat.FileType.IMAGE ? Const.IMAGE_CONTENT_TYPE : Const.AUDIO_CONTENT_TYPE;
        ajaxPostMultiUrl(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String imageUrl = app.host + "/" + jsonObject.getString("uri");
                    //String createdTime = jsonObject.getString("createdTime");
                    sendMediaMsg(imageUrl, chat, type);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateSendMsgToListView(Chat.Delivery.FAILED, chat);
                CommonUtil.shortToast(mContext, String.format("%s上传失败", strType));
            }
        }, contentType);
    }

    /**
     * 保持一条聊天记录并添加到ListView
     *
     * @param delivery 是否送达
     * @param chat     一行聊天记录
     */
    private void addSendMsgToListView(Chat.Delivery delivery, Chat chat) {
        chat.direct = Chat.Direct.SEND;
        chat.setDelivery(delivery);
        long chatId = mChatDataSource.create(chat);
        chat.chatId = (int) chatId;
        mAdapter.addOneChat(chat);
    }

    /**
     * 更新一行聊天记录，并更新对应的Item
     *
     * @param delivery 是否送达
     * @param chat     一行聊天记录
     */
    private void updateSendMsgToListView(Chat.Delivery delivery, Chat chat) {
        chat.setDelivery(delivery);
        mChatDataSource.update(chat);
        mAdapter.updateItemByChatId(chat);
    }

    /**
     * 上传的Image复制到本地缓存
     *
     * @param originFile 小于500k的原图
     */
    private File copyImageFileToCache(File originFile) {
        String targetPath;
        String targetFileName = System.currentTimeMillis() + "";

        targetPath = EdusohoApp.getWorkSpace().getPath() + Const.UPLOAD_IMAGE_CACHE_FILE;
        File targetFile = new File(targetPath + "/" + targetFileName);
        if (targetFile.exists()) {
            targetFile.delete();
        }
        FileInputStream fis;
        FileOutputStream fos;
        FileChannel in;
        FileChannel out;
        try {
            fis = new FileInputStream(originFile);
            fos = new FileOutputStream(targetFile);
            in = fis.getChannel();
            out = fos.getChannel();
            in.transferTo(0, in.size(), out);
            fis.close();
            fos.close();
            in.close();
            out.close();
            return targetFile;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private void initCacheFolder() {
        File imageFolder = new File(EdusohoApp.getWorkSpace().getPath() + Const.UPLOAD_IMAGE_CACHE_FILE);
        if (!imageFolder.exists()) {
            imageFolder.mkdir();
        }
        File imageThumbFolder = new File(EdusohoApp.getWorkSpace().getPath() + Const.UPLOAD_IMAGE_CACHE_THUMB_FILE);
        if (!imageThumbFolder.exists()) {
            imageThumbFolder.mkdir();
        }
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
                    CommonUtil.longToast(mContext, "无法获取对方信息");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEND_IMAGE:
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        File file = selectPicture(selectedImage);
                        uploadMedia(file, Chat.FileType.IMAGE, Const.MEDIA_IMAGE);
                    }
                }
                break;
            case SEND_CAMERA:
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(mCameraFile.getPath(), options);
                if (bitmap != null) {
                    File compressedCameraFile = compressImage(bitmap, mCameraFile);
                    uploadMedia(compressedCameraFile, Chat.FileType.IMAGE, Const.MEDIA_IMAGE);
                }
                break;
            case SEND_VOICE:
                break;
        }
    }

    @Override
    public void invoke(WidgetMessage message) {
        try {
            MessageType messageType = message.type;
            WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(CHAT_DATA);
            CustomContent customContent = parseJsonValue(wrapperMessage.getCustomContent(), new TypeToken<CustomContent>() {
            });
            if (customContent.getTypeBusiness().equals(TypeBusinessEnum.FRIEND.getName()) ||
                    customContent.getTypeBusiness().equals(TypeBusinessEnum.TEACHER.getName())) {
                if (messageType.code == Const.ADD_CHAT_MSG && mFromId == customContent.getFromId()) {
                    Chat chat = new Chat(wrapperMessage);
                    mAdapter.addOneChat(chat);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{new MessageType(Const.ADD_CHAT_MSG, source)};
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatDataSource != null) {
            mChatDataSource.close();
        }
        unregisterReceiver(mAudioDownloadReceiver);
    }
}
