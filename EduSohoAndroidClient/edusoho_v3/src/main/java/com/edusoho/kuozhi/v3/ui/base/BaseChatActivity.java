package com.edusoho.kuozhi.v3.ui.base;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.broadcast.AudioDownloadReceiver;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.TypeBusinessEnum;
import com.edusoho.kuozhi.v3.model.bal.push.UpYunUploadResult;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.ChatAudioRecord;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoIconView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by JesseHuang on 15/10/16.
 */
public class BaseChatActivity extends ActionBarBaseActivity {

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
    protected BaseAdapter mAdapter;
    protected MediaRecorderTask mMediaRecorderTask;
    protected VolumeHandler mHandler;
    protected AudioDownloadReceiver mAudioDownloadReceiver;

    protected int[] mSpeakerAnimResId = new int[]{R.drawable.record_animate_1,
            R.drawable.record_animate_2,
            R.drawable.record_animate_3,
            R.drawable.record_animate_4};

    private static final int IMAGE_SIZE = 1024 * 500;

    private static final int SEND_IMAGE = 1;
    private static final int SEND_CAMERA = 2;

    protected int mSendTime;
    protected int mStart = 0;
    protected File mCameraFile;

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
        initView();
    }

    protected void initView() {
        mHandler = new VolumeHandler(this);
        mAudioDownloadReceiver = new AudioDownloadReceiver();
        etSend = (EditText) findViewById(R.id.et_send_content);
        etSend.addTextChangedListener(msgTextWatcher);
        tvSend = (Button) findViewById(R.id.tv_send);
        tvSend.setOnClickListener(mClickListener);
        lvMessage = (ListView) findViewById(R.id.lv_messages);
        ivAddMedia = (EduSohoIconView) findViewById(R.id.iv_show_media_layout);
        ivAddMedia.setOnClickListener(mClickListener);
        viewMediaLayout = findViewById(R.id.ll_media_layout);
        btnVoice = (EduSohoIconView) findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(mClickListener);
        btnKeyBoard = (EduSohoIconView) findViewById(R.id.btn_set_mode_keyboard);
        btnKeyBoard.setOnClickListener(mClickListener);
        viewPressToSpeak = findViewById(R.id.rl_btn_press_to_speak);
        viewPressToSpeak.setOnClickListener(mClickListener);
        viewMsgInput = findViewById(R.id.rl_msg_input);
        EduSohoIconView ivPhoto = (EduSohoIconView) findViewById(R.id.iv_image);
        ivPhoto.setOnClickListener(mClickListener);
        EduSohoIconView ivCamera = (EduSohoIconView) findViewById(R.id.iv_camera);
        ivCamera.setOnClickListener(mClickListener);
        viewPressToSpeak.setOnTouchListener(mVoiceRecordingTouchListener);
        tvSpeak = (TextView) findViewById(R.id.tv_speak);
        tvSpeakHint = (TextView) findViewById(R.id.tv_speak_hint);
        ivRecordImage = (ImageView) findViewById(R.id.iv_voice_volume);
        mViewSpeakContainer = findViewById(R.id.recording_container);
        mViewSpeakContainer.bringToFront();
        initData();
    }

    /**
     * 获取本地聊天记录
     */
    public void initData() {

    }

    protected Runnable mListViewSelectRunnable = new Runnable() {
        @Override
        public void run() {
            lvMessage.setSelection(mStart);
        }
    };

    /**
     * 初始化Cache文件夹
     */
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

    protected View.OnClickListener mClickListener = new View.OnClickListener() {
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
    };

    protected View.OnTouchListener mVoiceRecordingTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
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
    };

    /**
     * 发送普通文本
     * 子类重写
     *
     * @param content
     */
    public void sendMsg(String content) {

    }

    /**
     * 发送图片、声音等
     *
     * @param chat
     */
    public void sendMediaMsg(final Chat chat, Chat.FileType type) {

    }

    // region 发送信息

    /**
     * 上传资源:音频、图片
     * 子类重写
     *
     * @param file upload file
     */
    public void uploadMedia(final File file, final Chat.FileType type, String strType) {

    }

    /**
     * Upload media resource to upyun
     *
     * @param file Upload file
     * @param chat chatInfo
     * @param type Media Type
     */
    public void uploadUnYunMedia(final File file, final Chat chat, final Chat.FileType type) {

    }

    /**
     * get upyun upload info from push server
     *
     * @param file     upload file
     * @param callback callback
     */
    public void getUpYunUploadInfo(File file, final NormalCallback<UpYunUploadResult> callback) {

    }

    public void saveUploadResult(String putUrl, String getUrl) {

    }

    // endregion

    /**
     * update badge the ListView of NewsFragment
     *
     * @param message xg message
     */
    public void notifyNewFragmentListView2Update(WrapperXGPushTextMessage message) {

    }

    /**
     * 保持一条聊天记录到数据库，并添加到ListView
     *
     * @param delivery 是否送达
     * @param chat     一行聊天记录
     */
    public void addSendMsgToListView(Chat.Delivery delivery, Chat chat) {

    }

    /**
     * 更新一行聊天记录，并更新对应的Item
     *
     * @param delivery 是否送达
     * @param chat     一行聊天记录
     */
    public void updateSendMsgToListView(Chat.Delivery delivery, Chat chat) {

    }

    // region 图片处理

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
     * @return file
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
    private File compressImage(Bitmap bitmap, File file) {
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

    // endregion

    /**
     * 存本地的Custom信息
     *
     * @param fileType
     * @param typeBusiness
     * @param content
     * @return
     */
    private V2CustomContent getV2CustomContent(Chat.FileType fileType, TypeBusinessEnum typeBusiness, String content) {
        V2CustomContent v2CustomContent = new V2CustomContent();

        return v2CustomContent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAudioDownloadReceiver);
    }


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
                    uploadMedia(mUploadAudio, Chat.FileType.AUDIO, Const.MEDIA_AUDIO);
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
        private WeakReference<BaseChatActivity> mWeakReference;

        private VolumeHandler(BaseChatActivity activity) {
            if (this.mWeakReference == null) {
                this.mWeakReference = new WeakReference<>(activity);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            BaseChatActivity activity = this.mWeakReference.get();
            activity.ivRecordImage.setImageResource(activity.mSpeakerAnimResId[msg.what]);
        }
    }

    //endregion
}
