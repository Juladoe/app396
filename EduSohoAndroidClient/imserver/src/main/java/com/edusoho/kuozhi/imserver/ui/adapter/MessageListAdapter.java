package com.edusoho.kuozhi.imserver.ui.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.R;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.ui.entity.AudioBody;
import com.edusoho.kuozhi.imserver.ui.entity.Direct;
import com.edusoho.kuozhi.imserver.ui.entity.PushUtil;
import com.edusoho.kuozhi.imserver.ui.helper.MessageHelper;
import com.edusoho.kuozhi.imserver.ui.listener.AudioPlayStatusListener;
import com.edusoho.kuozhi.imserver.ui.listener.MessageListItemController;
import com.edusoho.kuozhi.imserver.ui.util.AudioUtil;
import com.edusoho.kuozhi.imserver.ui.util.ResourceDownloadTask;
import com.edusoho.kuozhi.imserver.ui.view.ChatImageView;
import com.edusoho.kuozhi.imserver.ui.view.MessageStatusView;
import com.edusoho.kuozhi.imserver.util.MessageUtil;
import com.edusoho.kuozhi.imserver.util.SystemUtil;
import com.edusoho.kuozhi.imserver.util.TimeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by suju on 16/8/26.
 */
public class MessageListAdapter extends BaseAdapter {

    private static final int SEND = 0X01;
    private static final int RECEIVE = 0X02;

    private static final int RECEIVE_TEXT = 0;
    private static final int SEND_TEXT = 1;
    private static final int RECEIVE_AUDIO = 2;
    private static final int SEND_AUDIO = 3;
    private static final int RECEIVE_IMAGE = 4;
    private static final int SEND_IMAGE = 5;
    private static final int RECEIVE_MULTI = 6;
    private static final int SEND_MULTI = 7;

    protected static long TIME_INTERVAL = 60 * 5 * 1000;
    protected static final String TAG = "MessageListAdapter";

    private int mCurrentId;
    private MessageHelper mMessageHelper;
    private MessageListItemController mMessageListItemController;

    protected Context mContext;
    protected List<MessageEntity> mMessageList;
    protected DisplayImageOptions mOptions;
    protected SparseArray<String> mImagePathArray;

    public MessageListAdapter(Context context) {
        this.mContext = context;
        this.mMessageList = new ArrayList<>();
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).
                showImageForEmptyUri(R.drawable.message_image_default).
                showImageOnFail(R.drawable.message_image_default).build();

        this.mMessageHelper = new MessageHelper(mContext);
        this.mImagePathArray = new SparseArray<>();
    }

    public MessageListAdapter(Context context, int currentId) {
        this(context);
        this.mCurrentId = currentId;
    }

    public void updateItem(MessageEntity messageEntity) {
        int size = mMessageList.size();
        for (int i = 0; i < size; i++) {
            if (mMessageList.get(i).getId() == messageEntity.getId()) {
                mMessageList.remove(i);
                mMessageList.add(i, messageEntity);
                notifyDataSetChanged();
                return;
            }
        }
    }

    private void updateViewByPosition(int position, MessageEntity messageEntity) {

    }

    public void setCurrentId(int currentId) {
        this.mCurrentId = currentId;
    }

    @Override
    public int getViewTypeCount() {
        return 8;
    }

    @Override
    public int getItemViewType(int position) {
        MessageBody messageBody = new MessageBody(mMessageList.get(position));
        String type = messageBody.getType();
        boolean isSend = messageBody.getSource().getId() == mCurrentId;
        switch (type) {
            case PushUtil.ChatMsgType.TEXT:
                return isSend ? SEND_TEXT : RECEIVE_TEXT;
            case PushUtil.ChatMsgType.AUDIO:
                return isSend ? SEND_AUDIO : RECEIVE_AUDIO;
            case PushUtil.ChatMsgType.IMAGE:
                return isSend ? SEND_IMAGE : RECEIVE_IMAGE;
            case PushUtil.ChatMsgType.PUSH:
            case PushUtil.ChatMsgType.MULTI:
                return isSend ? SEND_MULTI: RECEIVE_MULTI;
        }
        return isSend ? SEND_TEXT : RECEIVE_TEXT;
    }

    public void destory() {
        mImagePathArray.clear();
        mMessageList.clear();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        SendViewHolder viewHolder = null;
        MessageBody messageBody = new MessageBody(mMessageList.get(i));
        if (view == null) {
            int type = getItemViewType(i);
            Log.d(TAG, "create view type:" + type);
            view = getItemView(type, messageBody.getSource().getId() == mCurrentId);
            view.setTag(createItemViewHolder(view));
        }

        viewHolder = (SendViewHolder) view.getTag();
        viewHolder.setDirect(messageBody.getSource().getId() == mCurrentId ? Direct.SEND : Direct.RECEIVE);
        viewHolder.setContainerContent(messageBody);
        setMessageBody(viewHolder, messageBody, i);

        String avatarSrc = mMessageHelper.getRoleAvatar(messageBody.getSource().getType(), messageBody.getSource().getId());
        mMessageListItemController.onUpdateRole(messageBody.getSource().getType(), messageBody.getSource().getId());
        ImageLoader.getInstance().displayImage(avatarSrc, viewHolder.avatarView, mOptions);

        switch (messageBody.getMsgStatus()) {
            case MessageEntity.StatusType.SUCCESS:
                viewHolder.errorStatusView.setVisibility(View.INVISIBLE);
                break;
            case MessageEntity.StatusType.UPLOADING:
                viewHolder.errorStatusView.setVisibility(View.VISIBLE);
                viewHolder.errorStatusView.setProgressStatus();
                viewHolder.lengthView.setVisibility(View.GONE);
                break;
            case MessageEntity.StatusType.FAILED:
                viewHolder.errorStatusView.setVisibility(View.VISIBLE);
                viewHolder.errorStatusView.setErrorStatus();
                viewHolder.lengthView.setVisibility(View.GONE);

        }
        initClickListener(viewHolder, i);
        return view;
    }

    private void initClickListener(SendViewHolder viewHolder, int position) {
        ViewItemClickListener itemClickListener = new ViewItemClickListener(position);
        viewHolder.containerView.setOnClickListener(itemClickListener);
        viewHolder.avatarView.setOnClickListener(itemClickListener);
        viewHolder.errorStatusView.setOnClickListener(itemClickListener);
    }

    public void setMessageBody(SendViewHolder viewHolder, MessageBody messageBody, int position) {
        viewHolder.timeView.setVisibility(View.GONE);
        if (position > 0) {
            long preTime = mMessageList.get(position - 1).getTime() * 1000L;
            if (messageBody.getCreatedTime() - preTime > TIME_INTERVAL) {
                viewHolder.timeView.setVisibility(View.VISIBLE);
                viewHolder.timeView.setText(TimeUtil.convertMills2Date(messageBody.getCreatedTime()));
            }
        } else {
            viewHolder.timeView.setVisibility(View.VISIBLE);
            viewHolder.timeView.setText(TimeUtil.convertMills2Date(messageBody.getCreatedTime()));
        }
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public MessageEntity getItem(int i) {
        return mMessageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addList(List<MessageEntity> messageBodyList) {
        mMessageList.addAll(0, messageBodyList);
        notifyDataSetChanged();
    }

    public void clear() {
        mMessageList.clear();
        notifyDataSetChanged();
    }

    public void insertList(List<MessageEntity> messageBodyList) {
        mMessageList.addAll(messageBodyList);
        notifyDataSetChanged();
    }

    public void addItem(MessageEntity messageBody) {
        mMessageList.add(messageBody);
        notifyDataSetChanged();
    }

    public void setmMessageListItemController(MessageListItemController listItemClickListener) {
        this.mMessageListItemController = listItemClickListener;
    }

    protected TextView createTextView() {
        return (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_message_list_send_text_content, null);
    }

    protected View createMultiView() {
        return LayoutInflater.from(mContext).inflate(R.layout.item_message_list_multi_content, null);
    }

    protected ImageView createImageView(boolean isSend) {
        ChatImageView imageView = (ChatImageView) LayoutInflater.from(mContext).inflate(R.layout.item_message_list_imageview_content, null);
        imageView.setBackgroudRes(isSend ? R.drawable.chat_send_to : R.drawable.chat_text_from);
        return imageView;
    }

    protected ImageView createAudioView(Direct direct) {
        if (direct == Direct.RECEIVE) {
            return (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_message_list_receive_audio_content, null);
        }
        return (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_message_list_send_audio_content, null);
    }

    protected View getItemView(int type, boolean isSend) {
        View contentView = null;
        View rootView = null;
        if (isSend) {
            rootView = LayoutInflater.from(mContext).inflate(R.layout.item_message_list_send_layout, null);
        } else {
            rootView = LayoutInflater.from(mContext).inflate(R.layout.item_message_list_receive_layout, null);
        }
        ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.tv_container);

        switch (type) {
            case SEND_TEXT:
                contentView = createTextView();
                containerView.setTag(new TextViewHolder(contentView));
                break;
            case RECEIVE_TEXT:
                contentView = createTextView();
                containerView.setTag(new TextViewHolder(contentView));
                break;
            case SEND_AUDIO:
                contentView = createAudioView(isSend ? Direct.SEND : Direct.RECEIVE);
                containerView.setTag(new AudioViewHolder(contentView));
                break;
            case RECEIVE_AUDIO:
                contentView = createAudioView(isSend ? Direct.SEND : Direct.RECEIVE);
                containerView.setTag(new AudioViewHolder(contentView));
                break;
            case SEND_IMAGE:
                contentView = createImageView(true);
                containerView.setBackground(new ColorDrawable(0));
                containerView.setTag(new ImageVewHolder(contentView));
                break;
            case RECEIVE_IMAGE:
                contentView = createImageView(false);
                containerView.setBackground(new ColorDrawable(0));
                containerView.setTag(new ImageVewHolder(contentView));
                break;
            case SEND_MULTI:
                contentView = createMultiView();
                containerView.setTag(new MultiViewHolder(contentView));
                break;
            case RECEIVE_MULTI:
                contentView = createMultiView();
                containerView.setTag(new MultiViewHolder(contentView));
                break;
        }

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerView.addView(contentView, lp);
        return rootView;
    }

    protected SendViewHolder createItemViewHolder(View view) {
        return new SendViewHolder(view);
    }

    /**
     * ViewItemClickListener
     */
    class ViewItemClickListener implements View.OnClickListener {

        private int mPosition;

        public ViewItemClickListener(int position) {
            this.mPosition = position;
        }

        private int[] getAudioAnimResArray(MessageBody messageBody, int currentId) {
            if (messageBody.getSource().getId() == currentId) {
                return new int[]{
                        R.drawable.chat_to_speak_voice,
                        R.drawable.chat_to_voice_play_anim
                };
            }
            return new int[]{
                    R.drawable.chat_from_speak_voice,
                    R.drawable.chat_from_voice_play_anim
            };
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.tv_container) {
                MessageBody messageBody = new MessageBody(getItem(mPosition));
                if (PushUtil.ChatMsgType.AUDIO.equals(messageBody.getType())) {
                    AudioBody audioBody = AudioUtil.getAudioBody(messageBody.getBody());

                    File audioFile = mMessageHelper.getRealAudioFile(audioBody.getFile());
                    if (audioFile == null || !audioFile.exists()) {
                        return;
                    }
                    int[] animResArray = getAudioAnimResArray(messageBody, mCurrentId);
                    mMessageListItemController.onAudioClick(
                            mPosition,
                            audioFile.getAbsolutePath(),
                            new AudioPlayStatusListenerImpl((ImageView) view.findViewById(R.id.iv_voice_play_anim), animResArray));
                } else if (PushUtil.ChatMsgType.IMAGE.equals(messageBody.getType())) {
                    mMessageListItemController.onImageClick(messageBody.getBody());
                } else {
                    mMessageListItemController.onContentClick(mPosition);
                }
            } else if (id == R.id.tv_error_status) {
                mMessageListItemController.onErrorClick(mPosition);
            } else if (id == R.id.tv_avatar) {
                mMessageListItemController.onAvatarClick(MessageUtil.parseInt(getItem(mPosition).getFromId()));
            }
        }

        /*
        AudioPlayStatusListenerImpl
         */
        class AudioPlayStatusListenerImpl implements AudioPlayStatusListener {

            private int[] mAnimRes;
            private ImageView mVoiceView;
            private AnimationDrawable mAnimDrawable;

            public AudioPlayStatusListenerImpl(ImageView voiceView, int[] animRes) {
                this.mAnimRes = animRes;
                this.mVoiceView = voiceView;
            }

            @Override
            public void onPlay() {
                if (mVoiceView.getDrawable() instanceof AnimationDrawable) {
                    mAnimDrawable = (AnimationDrawable) mVoiceView.getDrawable();
                    mAnimDrawable.stop();
                    mAnimDrawable.start();
                } else {
                    mVoiceView.setImageResource(mAnimRes[1]);
                    mAnimDrawable = (AnimationDrawable) mVoiceView.getDrawable();
                    mAnimDrawable.start();
                }
            }

            @Override
            public void onStop() {
                if (mAnimDrawable != null) {
                    mAnimDrawable.stop();
                    mVoiceView.setImageResource(mAnimRes[0]);
                }
            }
        }
    }

    /*
        content MultiViewHolder
     */

    class MultiViewHolder {

        public TextView multiTitleView;
        public TextView multiContentView;
        public ImageView mulitIconView;

        public MultiViewHolder(View view) {
            multiTitleView = (TextView) view.findViewById(R.id.chat_multi_title);
            multiContentView = (TextView) view.findViewById(R.id.chat_multi_content);
            mulitIconView = (ImageView) view.findViewById(R.id.chat_multi_icon);
        }
    }

    class TextViewHolder {

        public TextView mContentView;
        public TextViewHolder(View view) {
            mContentView = (TextView) view.findViewById(R.id.tv_send_content);
        }
    }

    class AudioViewHolder {

        public ImageView mAudioView;
        public AudioViewHolder(View view) {
            mAudioView = (ImageView) view.findViewById(R.id.iv_voice_play_anim);
        }
    }

    class ImageVewHolder {

        public ImageView mImageView;
        public ImageVewHolder(View view) {
            mImageView = (ImageView) view.findViewById(R.id.iv_msg_image);
        }
    }

    /*
        SendViewHolder
     */
    class SendViewHolder {

        protected Direct mDirect;

        public TextView timeView;
        public TextView lengthView;
        public ImageView avatarView;
        public FrameLayout containerView;
        //public ProgressBar statusProgressBar;
        public MessageStatusView errorStatusView;
        public ImageView unReadView;

        public SendViewHolder(View view) {
            lengthView = (TextView) view.findViewById(R.id.tv_audio_length);
            timeView = (TextView) view.findViewById(R.id.tv_time);
            avatarView = (ImageView) view.findViewById(R.id.tv_avatar);
            containerView = (FrameLayout) view.findViewById(R.id.tv_container);
            //statusProgressBar = (ProgressBar) view.findViewById(R.id.tv_status_pbar);
            errorStatusView = (MessageStatusView) view.findViewById(R.id.tv_error_status);
            unReadView = (ImageView) view.findViewById(R.id.tv_unread_view);
        }

        public void setDirect(Direct direct) {
            this.mDirect = direct;
        }

        public void setContainerContent(MessageBody messageBody) {
            lengthView.setVisibility(View.GONE);
            new MessageTypeProcessor(this).processorMessageContent(messageBody);
        }

        class MessageTypeProcessor {
            private SendViewHolder mViewHolder;

            public MessageTypeProcessor(SendViewHolder viewHolder) {
                this.mViewHolder = viewHolder;
            }

            public void processorMessageContent(MessageBody messageBody) {
                String contentType = messageBody.getType();
                switch (contentType) {
                    case "text":
                        processorText(messageBody);
                        break;
                    case "audio":
                        processorAudio(messageBody);
                        break;
                    case "push":
                    case "multi":
                        processorMulit(messageBody);
                        break;
                    case "image":
                        processorImage(messageBody);
                }
            }

            private void parseMultiMessageBody(MessageBody messageBody, MultiViewHolder viewHolder) {
                String body = messageBody.getBody();
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    String type = jsonObject.optString("type");
                    switch (type) {
                        case PushUtil.CourseType.QUESTION_CREATED:
                            viewHolder.multiTitleView.setText(jsonObject.optString("questionTitle"));
                            viewHolder.multiContentView.setText(jsonObject.optString("title"));
                            viewHolder.mulitIconView.setImageResource(R.drawable.default_course);
                            break;
                        default:
                            viewHolder.multiTitleView.setText(jsonObject.optString("title"));
                            viewHolder.multiContentView.setText(jsonObject.optString("content"));
                            ImageLoader.getInstance().displayImage(jsonObject.optString("image"), viewHolder.mulitIconView);
                    }
                } catch (JSONException e) {
                }
            }

            public void processorMulit(MessageBody messageBody) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                lp.width = (int) (SystemUtil.getScreenWidth(mContext) * 0.5f);
                MultiViewHolder viewHolder = (MultiViewHolder) mViewHolder.containerView.getTag();
                parseMultiMessageBody(messageBody, viewHolder);
                //view.setLayoutParams(lp);
            }

            public void processorText(MessageBody messageBody) {
                TextViewHolder textViewHolder = (TextViewHolder) mViewHolder.containerView.getTag();
                textViewHolder.mContentView.setText(messageBody.getBody());
            }

            public void processorImage(MessageBody messageBody) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;

                String body = messageBody.getBody();
                ImageVewHolder imageVewHolder = (ImageVewHolder) mViewHolder.containerView.getTag();
                ImageView imageView = imageVewHolder.mImageView;

                String imagePath = mImagePathArray.get(messageBody.getMid());
                if (TextUtils.isEmpty(imagePath)) {
                    imagePath = mMessageHelper.getThumbImagePath(body);
                    mImagePathArray.put(messageBody.getMid(), imagePath);
                }
                //local file
                if (imagePath.startsWith("file:")) {
                    ImageLoader.getInstance().displayImage(imagePath, imageView);
                } else {
                    try {
                        File realFile = mMessageHelper.createImageFile(body);
                        ResourceDownloadTask downloadTask = new ResourceDownloadTask(mContext, messageBody.getMid(), body, realFile);
                        IMClient.getClient().getResourceHelper().addTask(downloadTask);
                        messageBody.setMsgStatus(MessageEntity.StatusType.UPLOADING);
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                    imageView.setImageResource(mMessageHelper.getDefaultImageRes());
                }
                /*int[] size = mMessageHelper.getThumbImageSize(body);
                lp.width = size[0];
                lp.height = size[1];*/

                //imageView.setLayoutParams(lp);
            }

            public void processorAudio(MessageBody messageBody) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;

                AudioViewHolder audioViewHolder = (AudioViewHolder) mViewHolder.containerView.getTag();
                ImageView audioView = audioViewHolder.mAudioView;
                AudioBody audioBody = AudioUtil.getAudioBody(messageBody.getBody());
                lp.width = 48 + TimeUtil.getDuration(audioBody.getDuration()) * 8;
                lp.height = 32;
                //audioView.setLayoutParams(lp);

                if (mViewHolder.unReadView != null) {
                    mViewHolder.unReadView.setVisibility(View.GONE);
                }
                switch (messageBody.getMsgStatus()) {
                    case MessageEntity.StatusType.NONE:
                        try {
                            File realFile = mMessageHelper.createAudioFile(audioBody.getFile());
                            ResourceDownloadTask downloadTask = new ResourceDownloadTask(mContext, messageBody.getMid(), audioBody.getFile(), realFile);
                            IMClient.getClient().getResourceHelper().addTask(downloadTask);
                            messageBody.setMsgStatus(MessageEntity.StatusType.UPLOADING);
                        } catch (IOException ie) {
                            ie.printStackTrace();
                        }
                        break;
                    case MessageEntity.StatusType.UNREAD:
                        mViewHolder.errorStatusView.setVisibility(View.VISIBLE);
                        if (mViewHolder.unReadView != null) {
                            mViewHolder.unReadView.setVisibility(View.VISIBLE);
                        }
                    case MessageEntity.StatusType.SUCCESS:
                        mViewHolder.lengthView.setVisibility(View.VISIBLE);
                        mViewHolder.lengthView.setText(String.format("%d\"", TimeUtil.getDuration(audioBody.getDuration())));
                        break;
                }
            }
        }
    }
}
