package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.ChatAdapter;
import com.edusoho.kuozhi.v3.adapter.CourseDiscussAdapter;
import com.edusoho.kuozhi.v3.broadcast.AudioDownloadReceiver;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.UserRole;
import com.edusoho.kuozhi.v3.model.bal.push.BaseMsgEntity;
import com.edusoho.kuozhi.v3.model.bal.push.CourseDiscussEntity;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.UpYunUploadResult;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.result.CloudResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.ChatAudioRecord;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.CourseDiscussDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.EduSohoIconView;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by JesseHuang on 15/12/14.
 */
public class DiscussFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener, ChatAdapter.ImageErrorClick {
    private static final String TAG = "DiscussFragment";

    private String mCourseName;
    private String mCourseImage;
    private String mUserType;
    private int mCourseId;
    private New mNewItemInfo;

    private CourseDiscussDataSource mCourseDiscussDataSource;
    private CourseDiscussAdapter<CourseDiscussEntity> mAdapter;

    protected EduSohoIconView btnVoice;
    protected EduSohoIconView btnKeyBoard;
    protected EditText etSend;
    protected ListView lvMessage;
    protected Button tvSend;
    protected EduSohoIconView ivAddMedia;
    protected PtrClassicFrameLayout mPtrFrame;
    protected View viewMediaLayout;
    protected View viewPressToSpeak;
    protected View viewMsgInput;
    /**
     * 语音录制按钮
     */
    protected TextView tvSpeak;
    protected TextView tvSpeakHint;
    protected View mViewSpeakContainer;
    protected ImageView ivRecordImage;

    protected float mPressDownY;
    protected MediaRecorderTask mMediaRecorderTask;
    protected VolumeHandler mHandler;
    protected AudioDownloadReceiver mAudioDownloadReceiver;

    protected int[] mSpeakerAnimResId = new int[]{R.drawable.record_animate_1,
            R.drawable.record_animate_2,
            R.drawable.record_animate_3,
            R.drawable.record_animate_4};

    private static final int SEND_IMAGE = 1;
    private static final int SEND_CAMERA = 2;
    private static final int IMAGE_SIZE = 1024 * 500;

    protected int mSendTime;
    protected int mStart = 0;
    protected File mCameraFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.activity_chat);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mActivity.registerReceiver(mAudioDownloadReceiver, intentFilter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    protected void initView(View view) {
        mHandler = new VolumeHandler(this);
        mAudioDownloadReceiver = new AudioDownloadReceiver();
        etSend = (EditText) view.findViewById(R.id.et_send_content);
        etSend.addTextChangedListener(msgTextWatcher);
        tvSend = (Button) view.findViewById(R.id.tv_send);
        tvSend.setOnClickListener(this);
        lvMessage = (ListView) view.findViewById(R.id.lv_messages);
        ivAddMedia = (EduSohoIconView) view.findViewById(R.id.iv_show_media_layout);
        ivAddMedia.setOnClickListener(this);
        viewMediaLayout = view.findViewById(R.id.ll_media_layout);
        btnVoice = (EduSohoIconView) view.findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(this);
        btnKeyBoard = (EduSohoIconView) view.findViewById(R.id.btn_set_mode_keyboard);
        btnKeyBoard.setOnClickListener(this);
        viewPressToSpeak = view.findViewById(R.id.rl_btn_press_to_speak);
        viewPressToSpeak.setOnClickListener(this);
        viewMsgInput = view.findViewById(R.id.rl_msg_input);
        EduSohoIconView ivPhoto = (EduSohoIconView) view.findViewById(R.id.iv_image);
        ivPhoto.setOnClickListener(this);
        EduSohoIconView ivCamera = (EduSohoIconView) view.findViewById(R.id.iv_camera);
        ivCamera.setOnClickListener(this);
        viewPressToSpeak.setOnTouchListener(this);
        tvSpeak = (TextView) view.findViewById(R.id.tv_speak);
        tvSpeakHint = (TextView) view.findViewById(R.id.tv_speak_hint);
        ivRecordImage = (ImageView) view.findViewById(R.id.iv_voice_volume);
        mViewSpeakContainer = view.findViewById(R.id.recording_container);
        mViewSpeakContainer.bringToFront();
        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view_frame);
    }

    protected void initData() {
        Intent intent = mActivity.getIntent();
        if (intent == null) {
            CommonUtil.longToast(mContext, "聊天记录读取错误");
            return;
        }
        mNewItemInfo = (New) intent.getSerializableExtra(Const.NEW_ITEM_INFO);
        mCourseImage = mNewItemInfo.imgUrl;
        mCourseName = mNewItemInfo.title;
        mCourseId = mNewItemInfo.fromId;
        if (TextUtils.isEmpty(mUserType)) {
            String[] roles = new String[app.loginUser.roles.length];
            for (int i = 0; i < app.loginUser.roles.length; i++) {
                roles[i] = app.loginUser.roles[i].toString();
            }
            if (CommonUtil.inArray(UserRole.ROLE_TEACHER.name(), roles)) {
                mUserType = PushUtil.ChatUserType.TEACHER;
            } else {
                mUserType = PushUtil.ChatUserType.FRIEND;
            }
        }
        NotificationUtil.cancelById(mCourseId);
        if (mCourseDiscussDataSource == null) {
            mCourseDiscussDataSource = new CourseDiscussDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        }
        initCacheFolder();

        try {
            mAdapter = new CourseDiscussAdapter<>(getList(0), mContext);
            mAdapter.setSendImageClickListener(this);
            lvMessage.setAdapter(mAdapter);
            mAudioDownloadReceiver.setAdapter(mAdapter);
            mStart = mAdapter.getCount();
            lvMessage.post(mListViewSelectRunnable);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Runnable mNewFragment2UpdateItemBadgeRunnable = new Runnable() {
        @Override
        public void run() {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.FROM_ID, mCourseId);
            bundle.putString(Const.NEWS_TYPE, PushUtil.ChatUserType.CLASSROOM);
            app.sendMsgToTarget(NewsFragment.UPDATE_UNREAD_MSG, bundle, NewsFragment.class);
        }
    };

    protected Runnable mListViewSelectRunnable = new Runnable() {
        @Override
        public void run() {
            lvMessage.setSelection(mStart);
        }
    };

    private ArrayList<CourseDiscussEntity> getList(int start) {
        ArrayList<CourseDiscussEntity> list = mCourseDiscussDataSource.getLists(mCourseId, app.loginUser.id, start, Const.NEWS_LIMIT);
        Collections.reverse(list);
        return list;
    }

    protected TextWatcher msgTextWatcher = new TextWatcher() {
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

    private void sendMsg(String content) {
        mSendTime = (int) (System.currentTimeMillis() / 1000);
        final CourseDiscussEntity model = new CourseDiscussEntity(0, mCourseId, app.loginUser.id, app.loginUser.nickname, app.loginUser.mediumAvatar,
                etSend.getText().toString(), app.loginUser.id, PushUtil.ChatMsgType.TEXT, PushUtil.MsgDeliveryType.UPLOADING, mSendTime);

        addSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, model);

        etSend.setText("");
        etSend.requestFocus();

        WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
        message.setTitle(mCourseName);
        message.setContent(model.content);
        V2CustomContent v2CustomContent = getV2CustomContent(PushUtil.ChatMsgType.TEXT, model.content);
        String v2CustomContentJson = mActivity.gson.toJson(v2CustomContent);
        message.setCustomContentJson(v2CustomContentJson);
        message.isForeground = true;
        notifyNewFragmentListView2Update(message);

        RequestUrl requestUrl = app.bindPushUrl(Const.SEND);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", mCourseName);
        params.put("content", content);
        params.put("custom", mActivity.gson.toJson(v2CustomContent));

        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CloudResult result = mActivity.parseJsonValue(response, new TypeToken<CloudResult>() {
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

    public void sendMediaMsg(final CourseDiscussEntity model, String type) {
        RequestUrl requestUrl = app.bindPushUrl(Const.SEND);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", mCourseName);
        params.put("content", model.upyunMediaGetUrl);
        params.put("custom", mActivity.gson.toJson(getV2CustomContent(type, model.upyunMediaGetUrl)));
        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CloudResult result = mActivity.parseJsonValue(response, new TypeToken<CloudResult>() {
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

    // region 数据库操作
    public void addSendMsgToListView(int delivery, CourseDiscussEntity model) {
        model.delivery = delivery;
        long discussId = mCourseDiscussDataSource.create(model);
        model.discussId = (int) discussId;
        mAdapter.addItem(model);
    }

    public void notifyNewFragmentListView2Update(WrapperXGPushTextMessage message) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.GET_PUSH_DATA, message);
        bundle.putInt(Const.ADD_CHAT_MSG_DESTINATION, NewsFragment.HANDLE_SEND_COURSE_DISCUSS_MSG);
        app.sendMsgToTarget(Const.ADD_MSG, bundle, NewsFragment.class);
    }

    public void updateSendMsgToListView(int type, CourseDiscussEntity model) {
        model.delivery = type;
        mCourseDiscussDataSource.update(model);
        mAdapter.updateItemByChatId(model);
    }

    private void uploadMedia(final File file, final String type, String strType) {
        if (file == null || !file.exists()) {
            CommonUtil.shortToast(mContext, String.format("%s不存在", strType));
            return;
        }
        try {
            mSendTime = (int) (System.currentTimeMillis() / 1000);
            final CourseDiscussEntity model = new CourseDiscussEntity(0, mCourseId, app.loginUser.id, app.loginUser.nickname, app.loginUser.mediumAvatar,
                    file.getPath(), app.loginUser.id, type, PushUtil.MsgDeliveryType.UPLOADING, mSendTime);

            //生成New页面的消息并通知更改
            WrapperXGPushTextMessage message = new WrapperXGPushTextMessage();
            message.setTitle(mCourseName);
            message.setContent(String.format("[%s]", strType));
            V2CustomContent v2CustomContent = getV2CustomContent(type, message.getContent());
            message.setCustomContentJson(mActivity.gson.toJson(v2CustomContent));
            message.isForeground = true;
            notifyNewFragmentListView2Update(message);

            addSendMsgToListView(PushUtil.MsgDeliveryType.UPLOADING, model);

            getUpYunUploadInfo(file, new NormalCallback<UpYunUploadResult>() {
                @Override
                public void success(final UpYunUploadResult result) {
                    if (result != null) {
                        model.upyunMediaPutUrl = result.putUrl;
                        model.upyunMediaGetUrl = result.getUrl;
                        model.headers = result.getHeaders();
                        uploadUnYunMedia(file, model, type);
                        saveUploadResult(result.putUrl, result.getUrl, mCourseId);
                    } else {
                        updateSendMsgToListView(PushUtil.MsgDeliveryType.FAILED, model);
                    }
                }
            }, mCourseId);
            viewMediaLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    // endregion

    @Override
    public void uploadMediaAgain(File file, BaseMsgEntity model, String type, String strType) {

    }

    /**
     * get upyun upload info from push server
     *
     * @param file     upload file
     * @param callback callback
     */
    public void getUpYunUploadInfo(File file, final NormalCallback<UpYunUploadResult> callback, int fromId) {
        String path = String.format(Const.GET_UPLOAD_INFO, fromId, file.length(), file.getName());
        RequestUrl url = app.bindPushUrl(path);
        mActivity.ajaxGet(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                UpYunUploadResult result = mActivity.parseJsonValue(response, new TypeToken<UpYunUploadResult>() {
                });
                callback.success(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.success(null);
                CommonUtil.longToast(mActivity, getString(R.string.request_fail_text));
                Log.d(TAG, "get upload info from upyun failed");
            }
        });
    }

    public void saveUploadResult(String putUrl, String getUrl, int fromId) {
        String path = String.format(Const.SAVE_UPLOAD_INFO, fromId);
        RequestUrl url = app.bindPushUrl(path);
        HashMap<String, String> hashMap = url.getParams();
        hashMap.put("putUrl", putUrl);
        hashMap.put("getUrl", getUrl);
        mActivity.ajaxPost(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    if ("success".equals(result.getString("result"))) {
                        Log.d(TAG, "save upload result success");
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "convert json to obj error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "save upload info error");
            }
        });
    }

    public void uploadUnYunMedia(final File file, final CourseDiscussEntity model, final String type) {
        RequestUrl putUrl = new RequestUrl(model.upyunMediaPutUrl);
        putUrl.setHeads(model.headers);
        putUrl.setMuiltParams(new Object[]{"file", file});
        mActivity.ajaxPostMultiUrl(putUrl, new Response.Listener<String>() {
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

    @Override
    public void sendMsgAgain(final BaseMsgEntity model) {
        RequestUrl requestUrl = app.bindPushUrl(Const.SEND);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("title", app.loginUser.nickname);
        params.put("content", model.content);
        params.put("custom", mActivity.gson.toJson(getV2CustomContent(PushUtil.ChatMsgType.TEXT, model.content)));

        mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CloudResult result = mActivity.parseJsonValue(response, new TypeToken<CloudResult>() {
                });
                if (result != null && result.getResult()) {
                    model.id = result.id;
                    updateSendMsgToListView(PushUtil.MsgDeliveryType.SUCCESS, (CourseDiscussEntity) model);
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
     * 从图库获取图片
     */
    protected void openPictureFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, SEND_IMAGE);
    }

    protected void initCacheFolder() {
        File imageFolder = new File(EdusohoApp.getChatCacheFile() + Const.UPLOAD_IMAGE_CACHE_FILE);
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        File imageThumbFolder = new File(EdusohoApp.getChatCacheFile() + Const.UPLOAD_IMAGE_CACHE_THUMB_FILE);
        if (!imageThumbFolder.exists()) {
            imageThumbFolder.mkdirs();
        }
        File audioFolder = new File(EdusohoApp.getChatCacheFile() + Const.UPLOAD_AUDIO_CACHE_FILE);
        if (!audioFolder.exists()) {
            audioFolder.mkdirs();
        }
    }

    // region events

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /**
         * 根据滑动距离是否保存
         */
        boolean mHandUpAndCancel;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                try {
                    if (!CommonUtil.isExitsSdcard()) {
                        CommonUtil.longToast(mContext, "发送语音需要sdcard");
                        return false;
                    }
                    mPressDownY = event.getY();
                    mMediaRecorderTask = new MediaRecorderTask();
                    mMediaRecorderTask.execute();
                } catch (Exception e) {
                    mMediaRecorderTask.getAudioRecord().clear();
                    Log.d(TAG, e.getMessage());
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float mPressMoveY = event.getY();
                if (Math.abs(mPressDownY - mPressMoveY) > EdusohoApp.screenH * 0.1) {
                    tvSpeak.setText(getString(R.string.hand_up_and_exit));
                    tvSpeakHint.setText(getString(R.string.hand_up_and_exit));
                    tvSpeakHint.setBackgroundResource(R.drawable.speak_hint_bg);
                    ivRecordImage.setImageResource(R.drawable.record_cancel);
                    mHandUpAndCancel = true;
                } else {
                    tvSpeakHint.setText(getString(R.string.hand_move_up_and_send_cancel));
                    tvSpeakHint.setBackgroundResource(R.drawable.speak_hint_transparent_bg);
                    tvSpeak.setText(getString(R.string.hand_up_and_end));
                    ivRecordImage.setImageResource(R.drawable.record_animate_1);
                    mHandUpAndCancel = false;
                }
                mMediaRecorderTask.setCancel(mHandUpAndCancel);
                return true;
            case MotionEvent.ACTION_UP:
                mMediaRecorderTask.setAudioStop(true);
                return true;
        }
        return false;
    }

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
                mCameraFile = new File(EdusohoApp.getChatCacheFile() + Const.UPLOAD_IMAGE_CACHE_FILE + "/" + System.currentTimeMillis());
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

    // endregion

    //region InnerClass

    public class MediaRecorderTask extends AsyncTask<Void, Integer, Boolean> {
        private ChatAudioRecord mAudioRecord;
        private boolean mCancelSave = false;
        private boolean mStopRecord = false;
        private File mUploadAudio;

        @Override
        protected void onPreExecute() {
            if (mAudioRecord == null) mAudioRecord = new ChatAudioRecord(mContext);
            mViewSpeakContainer.setVisibility(View.VISIBLE);
            tvSpeak.setText(getString(R.string.hand_up_and_end));
            tvSpeakHint.setText(getResources().getString(R.string.hand_move_up_and_send_cancel));
            tvSpeakHint.setBackgroundResource(R.drawable.speak_hint_transparent_bg);
            ivRecordImage.setImageResource(R.drawable.record_animate_1);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mAudioRecord.ready();
            mAudioRecord.start();
            while (true) {
                if (mStopRecord) {
                    //结束录音
                    mUploadAudio = mAudioRecord.stop(mCancelSave);
                    int audioLength = mAudioRecord.getAudioLength();
                    if (audioLength >= 1) {
                        Log.d(TAG, "上传成功");
                    } else {
                        return false;
                    }
                    mAudioRecord.clear();
                    break;
                } else {
                    //录音中动画
                    double ratio = 0;
                    if (mAudioRecord.getMediaRecorder() != null) {
                        ratio = (double) mAudioRecord.getMediaRecorder().getMaxAmplitude();
                    }
                    double db = 0;
                    if (ratio > 1) {
                        db = 20 * Math.log10(ratio);
                    }
                    if (!mCancelSave) {
                        if (db < 60) {
                            mHandler.sendEmptyMessage(0);
                        } else if (db < 70) {
                            mHandler.sendEmptyMessage(1);
                        } else if (db < 80) {
                            mHandler.sendEmptyMessage(2);
                        } else if (db < 90) {
                            mHandler.sendEmptyMessage(3);
                        }
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSave) {
            if (mCancelSave) {
                mViewSpeakContainer.setVisibility(View.GONE);
                Log.d(TAG, "手指松开取消保存");
            } else {
                if (isSave) {
                    Log.d(TAG, "正常保存上传");
                    uploadMedia(mUploadAudio, PushUtil.ChatMsgType.AUDIO, Const.MEDIA_AUDIO);
                    mViewSpeakContainer.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "录制时间太短");
                    tvSpeakHint.setText(getString(R.string.audio_length_too_short));
                    tvSpeakHint.setBackgroundResource(R.drawable.speak_hint_transparent_bg);
                    ivRecordImage.setImageResource(R.drawable.record_duration_short);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mViewSpeakContainer.setVisibility(View.GONE);
                        }
                    }, 200);
                    mAudioRecord.delete();
                }
            }
            tvSpeak.setText(getString(R.string.hand_press_and_speak));
            viewPressToSpeak.setPressed(false);
            super.onPostExecute(isSave);
        }

        public void setCancel(boolean cancel) {
            mCancelSave = cancel;
        }

        public void setAudioStop(boolean stop) {
            mStopRecord = stop;
        }

        public ChatAudioRecord getAudioRecord() {
            return mAudioRecord;
        }
    }

    public static class VolumeHandler extends Handler {
        private WeakReference<DiscussFragment> mWeakReference;

        private VolumeHandler(DiscussFragment fragment) {
            if (this.mWeakReference == null) {
                this.mWeakReference = new WeakReference<>(fragment);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            DiscussFragment fragment = this.mWeakReference.get();
            if (fragment != null) {
                fragment.ivRecordImage.setImageResource(fragment.mSpeakerAnimResId[msg.what]);
            }
        }
    }

    //endregion

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
        fromEntity.setType(mUserType);
        v2CustomContent.setFrom(fromEntity);
        V2CustomContent.ToEntity toEntity = new V2CustomContent.ToEntity();
        toEntity.setId(mCourseId);
        toEntity.setImage(mCourseImage);

        toEntity.setType(PushUtil.ChatUserType.COURSE);
        v2CustomContent.setTo(toEntity);
        V2CustomContent.BodyEntity bodyEntity = new V2CustomContent.BodyEntity();
        bodyEntity.setType(type);
        bodyEntity.setContent(content);
        v2CustomContent.setBody(bodyEntity);
        v2CustomContent.setV(Const.PUSH_VERSION);
        v2CustomContent.setCreatedTime(mSendTime);
        return v2CustomContent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEND_IMAGE:
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        File file = selectPicture(selectedImage);
                        uploadMedia(file, PushUtil.ChatMsgType.IMAGE, Const.MEDIA_IMAGE);
                    }
                }
                break;
            case SEND_CAMERA:
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(mCameraFile.getPath(), options);
                if (bitmap != null) {
                    File compressedCameraFile = compressImage(bitmap, mCameraFile);
                    uploadMedia(compressedCameraFile, PushUtil.ChatMsgType.IMAGE, Const.MEDIA_IMAGE);
                }
                break;
        }
    }

    /**
     * 选择图片并压缩
     *
     * @param selectedImage 原图
     * @return file
     */
    protected File selectPicture(Uri selectedImage) {
        Cursor cursor = mActivity.getContentResolver().query(selectedImage, null, null, null, null);
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
        if (TextUtils.isEmpty(picturePath)) {
            CommonUtil.shortToast(mContext, "图片不存在");
            return null;
        }
        File file = new File(picturePath);
        Bitmap bitmap = AppUtil.getBitmapFromFile(file);
        if (bitmap == null) {
            return null;
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
    protected File compressImage(Bitmap bitmap, File file) {
        File compressedFile;
        try {
            //分辨率压缩到屏幕的0.4
            Bitmap compressWidthBitmap = null;
            if (bitmap.getWidth() > EdusohoApp.screenW * 0.4f) {
                compressWidthBitmap = AppUtil.scaleImage(bitmap, EdusohoApp.screenW * 0.4f, AppUtil.getImageDegree(file.getPath()));
                if (AppUtil.getImageSize(compressWidthBitmap) > IMAGE_SIZE) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap = AppUtil.compressImage(compressWidthBitmap, baos);
                }
            }
            //大于500K质量压缩
            if (AppUtil.getImageSize(bitmap) > IMAGE_SIZE) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap = AppUtil.compressImage(bitmap, baos);
            }
            compressedFile = AppUtil.convertBitmap2File(bitmap, EdusohoApp.getChatCacheFile() + Const.UPLOAD_IMAGE_CACHE_FILE + "/" + System.currentTimeMillis());


            AppUtil.convertBitmap2File(compressWidthBitmap != null ? compressWidthBitmap : bitmap, EdusohoApp.getChatCacheFile() +
                    Const.UPLOAD_IMAGE_CACHE_THUMB_FILE + "/" + compressedFile.getName());

        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
            return null;
        }
        return compressedFile;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(mAudioDownloadReceiver);
    }
}
