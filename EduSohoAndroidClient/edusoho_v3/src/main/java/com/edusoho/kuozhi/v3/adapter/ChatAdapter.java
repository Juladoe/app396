package com.edusoho.kuozhi.v3.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.ChatDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class ChatAdapter extends BaseAdapter {

    private Context mContext;
    private List<Chat> mList;
    private HashMap<Long, Integer> mDownloadList;
    private MediaPlayer mMediaPlayer;
    private ChatDataSource mChatDataSource;

    private int mDurationMax = EdusohoApp.screenW / 2;
    private int mDurationUnit = EdusohoApp.screenW / 40;
    private static long TIME_INTERVAL = 60 * 5;
    private static final int TYPE_COUNT = 6;
    private static final int MSG_SEND_TEXT = 0;
    private static final int MSG_RECEIVE_TEXT = 1;
    private static final int MSG_SEND_IMAGE = 2;
    private static final int MSG_RECEIVE_IMAGE = 3;
    private static final int MSG_SEND_AUDIO = 4;
    private static final int MSG_RECEIVE_AUDIO = 5;
    private DisplayImageOptions mOptions;

    ImageErrorClick mImageErrorClick;

    public void setSendImageClickListener(ImageErrorClick imageErrorClick) {
        mImageErrorClick = imageErrorClick;
    }

    public void addItems(ArrayList<Chat> list) {
        mList.addAll(0, list);
        notifyDataSetChanged();
    }

    public ChatAdapter(Context ctx, List<Chat> list) {
        mContext = ctx;
        mList = list;
        mDownloadList = new HashMap<>();
        mChatDataSource = new ChatDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, EdusohoApp.app.domain));
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).
                showImageForEmptyUri(R.drawable.defaultpic).
                showImageOnLoading(R.drawable.defaultpic).
                showImageOnFail(R.drawable.defaultpic).build();
    }

    public void addOneChat(Chat chat) {
        mList.add(chat);
        notifyDataSetChanged();
    }

    public void updateItemByChatId(Chat chat) {
        try {
            for (Chat c : mList) {
                if (c.chatId == chat.chatId) {
                    c.setDelivery(chat.getDelivery());
                    notifyDataSetChanged();
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("updateItemByChatId", e.getMessage());
        }
    }

    public void clear() {
        if (mList.size() > 0) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Chat msg = mList.get(position);
        int type = -1;
        if (msg.direct == Chat.Direct.SEND) {
            switch (msg.fileType) {
                case TEXT:
                    type = MSG_SEND_TEXT;
                    break;
                case IMAGE:
                    type = MSG_SEND_IMAGE;
                    break;
                case AUDIO:
                    type = MSG_SEND_AUDIO;
                    break;
            }
        } else {
            switch (msg.fileType) {
                case TEXT:
                    type = MSG_RECEIVE_TEXT;
                    break;
                case IMAGE:
                    type = MSG_RECEIVE_IMAGE;
                    break;
                case AUDIO:
                    type = MSG_RECEIVE_AUDIO;
                    break;
            }
        }
        return type;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int type = getItemViewType(position);
        if (convertView == null) {
            convertView = createViewByType(type);
            holder = new ViewHolder(convertView, type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (type) {
            case MSG_SEND_TEXT:
                handleMsgText(holder, position);
                break;
            case MSG_RECEIVE_TEXT:
                handleMsgText(holder, position);
                break;
            case MSG_SEND_IMAGE:
                handlerSendImage(holder, position);
                break;
            case MSG_RECEIVE_IMAGE:
                handlerReceiveImage(holder, position);
                break;
            case MSG_SEND_AUDIO:
                handlerSendAudio(holder, position);
                break;
            case MSG_RECEIVE_AUDIO:
                handlerReceiveAudio(holder, position);
                break;
        }
        return convertView;
    }

    private void handleMsgText(ViewHolder holder, int position) {
        Chat model = mList.get(position);
        holder.tvSendTime.setVisibility(View.GONE);
        if (position > 0) {
            if (model.createdTime - mList.get(position - 1).createdTime > TIME_INTERVAL) {
                holder.tvSendTime.setVisibility(View.VISIBLE);
                holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
            }
        } else {
            holder.tvSendTime.setVisibility(View.VISIBLE);
            holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
        }
        holder.tvSendContent.setText(model.content);
        ImageLoader.getInstance().displayImage(model.headimgurl, holder.ciPic, EdusohoApp.app.mOptions);
    }


    private void handlerSendImage(final ViewHolder holder, int position) {
        final Chat model = mList.get(position);
        if (position > 0) {
            if (model.createdTime - mList.get(position - 1).createdTime > TIME_INTERVAL) {
                holder.tvSendTime.setVisibility(View.VISIBLE);
                holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
            } else {
                holder.tvSendTime.setVisibility(View.GONE);
            }
        } else {
            holder.tvSendTime.setVisibility(View.VISIBLE);
            holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
        }
        switch (model.getDelivery()) {
            case SUCCESS:
                holder.pbLoading.setVisibility(View.GONE);
                holder.ivStateError.setVisibility(View.GONE);
                break;
            case UPLOADING:
                holder.pbLoading.setVisibility(View.VISIBLE);
                holder.ivStateError.setVisibility(View.GONE);
                break;
            case FAILED:
                holder.pbLoading.setVisibility(View.GONE);
                holder.ivStateError.setVisibility(View.VISIBLE);
                holder.ivStateError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mImageErrorClick != null) {
                            File file = new File(model.content);
                            if (file.exists()) {
                                model.setDelivery(Chat.Delivery.UPLOADING);
                                holder.pbLoading.setVisibility(View.VISIBLE);
                                holder.ivStateError.setVisibility(View.GONE);
                                mImageErrorClick.uploadMediaAgain(file, model, Const.MEDIA_IMAGE);
                            } else {
                                CommonUtil.longToast(mContext, "图片不存在，无法上传");
                            }
                        }
                    }
                });
                break;
        }
        holder.ivMsgImage.setOnClickListener(new ImageMsgClick("file://" + model.content));
        ImageLoader.getInstance().displayImage("file://" + getThumbFromOriginalImagePath(model.content), holder.ivMsgImage, EdusohoApp.app.mOptions);
        ImageLoader.getInstance().displayImage(model.headimgurl, holder.ciPic, EdusohoApp.app.mOptions);
    }

    private void handlerReceiveImage(final ViewHolder holder, int position) {
        final Chat model = mList.get(position);
        final MyImageLoadingListener mMyImageLoadingListener = new MyImageLoadingListener(holder);
        if (position > 0) {
            if (model.createdTime - mList.get(position - 1).createdTime > TIME_INTERVAL) {
                holder.tvSendTime.setVisibility(View.VISIBLE);
                holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
            } else {
                holder.tvSendTime.setVisibility(View.GONE);
            }
        } else {
            holder.tvSendTime.setVisibility(View.VISIBLE);
            holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
        }
        holder.ivStateError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader.getInstance().displayImage(model.content, holder.ivMsgImage, EdusohoApp.app.mOptions, mMyImageLoadingListener);
            }
        });
        ImageLoader.getInstance().displayImage(model.headimgurl, holder.ciPic, EdusohoApp.app.mOptions);

        File receiveImage = ImageLoader.getInstance().getDiskCache().get(model.content);
        holder.ivMsgImage.setOnClickListener(new ImageMsgClick(model.content));
        if (receiveImage.exists()) {
            String thumbImagePath = getThumbFromImageName(receiveImage.getName());
            File thumbImage = new File(thumbImagePath);
            if (thumbImage.exists()) {
                holder.pbLoading.setVisibility(View.GONE);
                holder.ivStateError.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage("file://" + thumbImagePath, holder.ivMsgImage);
                return;
            }
        }

        ImageLoader.getInstance().displayImage(model.content, holder.ivMsgImage, mOptions, mMyImageLoadingListener);
    }

    private void handlerSendAudio(final ViewHolder holder, int position) {
        final Chat model = mList.get(position);
        if (position > 0) {
            if (model.createdTime - mList.get(position - 1).createdTime > TIME_INTERVAL) {
                holder.tvSendTime.setVisibility(View.VISIBLE);
                holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
            } else {
                holder.tvSendTime.setVisibility(View.GONE);
            }
        } else {
            holder.tvSendTime.setVisibility(View.VISIBLE);
            holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
        }
        switch (model.getDelivery()) {
            case SUCCESS:
                holder.ivStateError.setVisibility(View.GONE);
                holder.pbLoading.setVisibility(View.GONE);
                holder.tvAudioLength.setVisibility(View.VISIBLE);
                try {
                    int duration = getAmrDuration(model.content);
                    holder.tvAudioLength.setText(duration + "\"");

                    holder.ivMsgImage.getLayoutParams().width = 50 + mDurationUnit * duration < mDurationMax ? 50 + mDurationUnit * duration : mDurationMax;
                    holder.ivMsgImage.requestLayout();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.ivMsgImage.setOnClickListener(new AudioMsgClick(model.content, holder, R.drawable.chat_to_speak_voice, R.drawable.chat_to_voice_play_anim));
                break;
            case UPLOADING:
                holder.pbLoading.setVisibility(View.VISIBLE);
                holder.ivStateError.setVisibility(View.GONE);
                holder.tvAudioLength.setVisibility(View.GONE);
                break;
            case FAILED:
                holder.pbLoading.setVisibility(View.GONE);
                holder.ivStateError.setVisibility(View.VISIBLE);
                holder.tvAudioLength.setVisibility(View.GONE);
                holder.ivStateError.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mImageErrorClick != null) {
                            File file = new File(model.content);
                            if (file.exists()) {
                                model.setDelivery(Chat.Delivery.UPLOADING);
                                holder.pbLoading.setVisibility(View.VISIBLE);
                                holder.ivStateError.setVisibility(View.GONE);
                                mChatDataSource.update(model);
                                mImageErrorClick.uploadMediaAgain(file, model, Const.MEDIA_IMAGE);
                                notifyDataSetChanged();
                            } else {
                                CommonUtil.longToast(mContext, "音频不存在，无法上传");
                            }
                        }
                    }
                });
                break;
        }
        //getAmrDuration();
    }

    private void handlerReceiveAudio(final ViewHolder holder, int position) {
        final Chat model = mList.get(position);
        if (position > 0) {
            if (model.createdTime - mList.get(position - 1).createdTime > TIME_INTERVAL) {
                holder.tvSendTime.setVisibility(View.VISIBLE);
                holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
            } else {
                holder.tvSendTime.setVisibility(View.GONE);
            }
        } else {
            holder.tvSendTime.setVisibility(View.VISIBLE);
            holder.tvSendTime.setText(AppUtil.convertMills2Date(((long) model.createdTime) * 1000));
        }
        switch (model.getDelivery()) {
            case SUCCESS:
                holder.ivStateError.setVisibility(View.GONE);
                holder.pbLoading.setVisibility(View.GONE);
                holder.tvAudioLength.setVisibility(View.VISIBLE);
                String audioFileName = EdusohoApp.getWorkSpace() + Const.UPLOAD_AUDIO_CACHE_FILE + "/" +
                        model.getContent().substring(model.getContent().lastIndexOf('/') + 1);
                try {
                    Log.e("handlerReceiveAudio3", System.currentTimeMillis() + "");
                    int duration = getAmrDuration(audioFileName);
                    Log.e("handlerReceiveAudio4", System.currentTimeMillis() + "");
                    holder.tvAudioLength.setText(duration + "\"");
                    holder.ivMsgImage.getLayoutParams().width = 50 + mDurationUnit * duration < mDurationMax ? 50 + mDurationUnit * duration : mDurationMax;
                    holder.ivMsgImage.requestLayout();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                holder.ivMsgImage.setOnClickListener(new AudioMsgClick(audioFileName, holder,
                        R.drawable.chat_from_speak_voice,
                        R.drawable.chat_from_voice_play_anim));
                break;
            case UPLOADING:
                holder.pbLoading.setVisibility(View.VISIBLE);
                holder.ivStateError.setVisibility(View.GONE);
                holder.tvAudioLength.setVisibility(View.GONE);
                downloadAudio(model.content, model.chatId);
                break;
            case FAILED:
                holder.pbLoading.setVisibility(View.GONE);
                holder.ivStateError.setVisibility(View.VISIBLE);
                holder.tvAudioLength.setVisibility(View.GONE);
                break;
        }
    }

    private DownloadManager mDownloadManager;

    private void downloadAudio(String url, int chatId) {
        if (mDownloadManager == null) {
            mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        }
        Uri uri = Uri.parse(url);
        String filename = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationInExternalPublicDir("/edusoho" + Const.UPLOAD_AUDIO_CACHE_FILE + "/", filename);
        long downloadId = mDownloadManager.enqueue(request);
        mDownloadList.put(downloadId, chatId);
    }

    /**
     * 获取amr播放长度
     *
     * @param filePath 文件路径
     * @return 音频长度
     */
    private int getAmrDuration(String filePath) {
        long prev = System.currentTimeMillis();
        Log.e("getAmrDuration1", filePath + "");
        mMediaPlayer = MediaPlayer.create(mContext, Uri.parse(filePath));
        Log.e("getAmrDuration2", (System.currentTimeMillis() - prev) + "");
        prev = System.currentTimeMillis();
        int duration = mMediaPlayer.getDuration();
        Log.e("getAmrDuration3", (System.currentTimeMillis() - prev) + "");
        prev = System.currentTimeMillis();
        int i = (int) Math.ceil(Float.valueOf(duration) / 1000);
        Log.e("getAmrDuration4", (System.currentTimeMillis() - prev) + "");
        return i;
    }

    /**
     * 获取缩略图文件路径
     *
     * @param imagePath 文件路径
     * @return 文件路径
     */
    private String getThumbFromOriginalImagePath(String imagePath) {
        return imagePath.replace(Const.UPLOAD_IMAGE_CACHE_FILE, Const.UPLOAD_IMAGE_CACHE_THUMB_FILE);
    }

    private String getThumbFromImageName(String imageName) {
        return EdusohoApp.getWorkSpace() + Const.UPLOAD_IMAGE_CACHE_THUMB_FILE + "/" + imageName;
    }

    private class ImageMsgClick implements View.OnClickListener {
        private String mImageUrl;

        public ImageMsgClick(String url) {
            this.mImageUrl = url;
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", 1);
            bundle.putStringArray("images", new String[]{mImageUrl});
            EdusohoApp.app.mEngine.runNormalPluginWithBundle("ViewPagerActivity", mContext, bundle);
        }
    }

    private String mCurrentAudioPath;
    private AnimationDrawable mAnimDrawable;
    private ImageView mPrev;

    private class AudioMsgClick implements View.OnClickListener {
        private File mAudioFile;
        private ViewHolder holder;
        private int mChatSpeakResId;
        private int mChatSpeakAnimResId;

        public AudioMsgClick(String filePath, ViewHolder h, int resId, int animResId) {
            mAudioFile = new File(filePath);
            holder = h;
            mChatSpeakResId = resId;
            mChatSpeakAnimResId = animResId;
        }

        @Override
        public void onClick(View v) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                if (mCurrentAudioPath.equals(mAudioFile.getPath())) {
                    mMediaPlayer.stop();
                    stopVoiceAnim(holder, mChatSpeakResId);
                    return;
                } else {
                    mMediaPlayer.stop();
                }
            }
            if (mAudioFile != null && mAudioFile.exists()) {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(mContext, Uri.parse(mAudioFile.getPath()));
                    mMediaPlayer.getDuration();
                } else {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mMediaPlayer.reset();
                        mMediaPlayer.release();
                    }
                    mMediaPlayer = null;
                    mMediaPlayer = MediaPlayer.create(mContext, Uri.parse(mAudioFile.getPath()));
                }
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopVoiceAnim(holder, mChatSpeakResId);
                        mPrev = null;
                    }
                });
                if (mPrev != null && mPrev.getBackground() instanceof AnimationDrawable) {
                    ((AnimationDrawable) mPrev.getBackground()).stop();
                    mPrev.setBackgroundResource(mChatSpeakResId);
                }
                mMediaPlayer.start();
                mPrev = holder.ivVoiceAnim;
                startVoiceAnim(holder, mChatSpeakAnimResId);
                mCurrentAudioPath = mAudioFile.getPath();
            }
        }
    }

    private void startVoiceAnim(ViewHolder holder, int resId) {
        if (holder.ivVoiceAnim.getBackground() instanceof AnimationDrawable) {
            mAnimDrawable = (AnimationDrawable) holder.ivVoiceAnim.getBackground();
            mAnimDrawable.stop();
            mAnimDrawable.start();
        } else {
            holder.ivVoiceAnim.setBackgroundResource(resId);
            mAnimDrawable = (AnimationDrawable) holder.ivVoiceAnim.getBackground();
            mAnimDrawable.start();
        }
    }

    private void stopVoiceAnim(ViewHolder holder, int resId) {
        if (mAnimDrawable != null) {
            mAnimDrawable.stop();
            holder.ivVoiceAnim.setBackgroundResource(resId);
        }
    }

    private class MyImageLoadingListener implements ImageLoadingListener {
        private ViewHolder holder;

        public MyImageLoadingListener(ViewHolder h) {
            this.holder = h;
        }

        @Override
        public void onLoadingStarted(String s, View view) {
            holder.pbLoading.setVisibility(View.VISIBLE);
            holder.ivStateError.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingFailed(String s, View view, FailReason failReason) {
            holder.pbLoading.setVisibility(View.GONE);
            holder.ivStateError.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            //分辨率压缩并缓存
            if (bitmap.getWidth() > EdusohoApp.screenW * 0.4f) {
                bitmap = AppUtil.scaleImage(bitmap, EdusohoApp.screenW * 0.4f, 0);
            }
            File receiveFile = ImageLoader.getInstance().getDiskCache().get(s);
            try {
                AppUtil.convertBitmap2File(bitmap, EdusohoApp.getWorkSpace() + Const.UPLOAD_IMAGE_CACHE_THUMB_FILE + "/" + receiveFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }

            holder.pbLoading.setVisibility(View.GONE);
            holder.ivStateError.setVisibility(View.GONE);
            ((ImageView) view).setImageBitmap(bitmap);
        }

        @Override
        public void onLoadingCancelled(String s, View view) {

        }
    }

    private View createViewByType(int type) {
        View convertView = null;
        switch (type) {
            case MSG_SEND_TEXT:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_msg_send_text, null);
                break;
            case MSG_RECEIVE_TEXT:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_msg_receive_text, null);
                break;
            case MSG_SEND_IMAGE:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_msg_send_image, null);
                break;
            case MSG_RECEIVE_IMAGE:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_msg_receive_image, null);
                break;
            case MSG_SEND_AUDIO:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_msg_send_audio, null);
                break;
            case MSG_RECEIVE_AUDIO:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_msg_receive_audio, null);
                break;
        }
        return convertView;
    }

    private static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvSendContent;
        public CircleImageView ciPic;
        public ImageView ivMsgImage;
        public ProgressBar pbLoading;
        public ImageView ivStateError;
        public TextView tvAudioLength;
        public ImageView ivVoiceAnim;

        public ViewHolder(View view, int type) {
            switch (type) {
                case MSG_SEND_TEXT:
                case MSG_RECEIVE_TEXT:
                    tvSendTime = (TextView) view.findViewById(R.id.tv_send_time);
                    tvSendContent = (TextView) view.findViewById(R.id.tv_send_content);
                    ciPic = (CircleImageView) view.findViewById(R.id.ci_send_pic);
                    break;
                case MSG_SEND_IMAGE:
                case MSG_RECEIVE_IMAGE:
                    tvSendTime = (TextView) view.findViewById(R.id.tv_send_time);
                    tvSendContent = (TextView) view.findViewById(R.id.tv_send_content);
                    ciPic = (CircleImageView) view.findViewById(R.id.ci_send_pic);
                    ivMsgImage = (ImageView) view.findViewById(R.id.iv_msg_image);
                    pbLoading = (ProgressBar) view.findViewById(R.id.sendProgressPar);
                    ivStateError = (ImageView) view.findViewById(R.id.msg_status);
                    break;
                case MSG_SEND_AUDIO:
                case MSG_RECEIVE_AUDIO:
                    tvSendTime = (TextView) view.findViewById(R.id.tv_send_time);
                    ciPic = (CircleImageView) view.findViewById(R.id.ci_send_pic);
                    ivMsgImage = (ImageView) view.findViewById(R.id.iv_msg_image);
                    pbLoading = (ProgressBar) view.findViewById(R.id.sendProgressPar);
                    ivStateError = (ImageView) view.findViewById(R.id.msg_status);
                    tvAudioLength = (TextView) view.findViewById(R.id.tv_audio_length);
                    ivVoiceAnim = (ImageView) view.findViewById(R.id.iv_voice_play_anim);
                    break;
            }
        }
    }

    public interface ImageErrorClick {
        public void uploadMediaAgain(File file, Chat chat, String strType);
    }

    public HashMap<Long, Integer> getDownloadList() {
        return mDownloadList;
    }

    public void updateVoiceDownloadStatus(long downId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downId);
        Cursor c = mDownloadManager.query(query);
        if (c != null && c.moveToFirst()) {
            String fileUri = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
            for (Chat chat : mList) {
                if (chat.chatId == mDownloadList.get(downId)) {
                    chat.setDelivery(TextUtils.isEmpty(fileUri) ? Chat.Delivery.FAILED : Chat.Delivery.SUCCESS);
                    mChatDataSource.update(chat);
                    mDownloadList.remove(downId);
                    notifyDataSetChanged();
                    break;
                }
            }
            c.close();
        }
    }
}
