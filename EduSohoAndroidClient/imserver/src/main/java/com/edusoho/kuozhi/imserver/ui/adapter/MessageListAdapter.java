package com.edusoho.kuozhi.imserver.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
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
import com.edusoho.kuozhi.imserver.ui.util.ResourceDownloadTask;
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

/**
 * Created by suju on 16/8/26.
 */
public class MessageListAdapter extends BaseAdapter {

    private static final int SEND = 0X01;
    private static final int RECEIVE = 0X02;

    protected static long TIME_INTERVAL = 60 * 5 * 1000;

    private int mCurrentId;
    private MessageHelper mMessageHelper;
    private MessageListItemController mMessageListItemController;

    protected Context mContext;
    protected List<MessageEntity> mMessageList;
    protected DisplayImageOptions mOptions;

    public MessageListAdapter(Context context) {
        this.mContext = context;
        this.mMessageList = new ArrayList<>();
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).
                showImageForEmptyUri(R.drawable.message_image_default).
                showImageOnFail(R.drawable.message_image_default).build();

        this.mMessageHelper = new MessageHelper(mContext);
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
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        MessageBody messageBody = new MessageBody(mMessageList.get(position));
        return messageBody.getSource().getId() == mCurrentId ? SEND : RECEIVE;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        SendViewHolder viewHolder = null;
        MessageBody messageBody = new MessageBody(mMessageList.get(i));
        if (view == null) {
            int type = getItemViewType(i);
            view = getItemView(type);
            view.setTag(createItemViewHolder(view));
        }

        viewHolder = (SendViewHolder) view.getTag();
        viewHolder.setDirect(messageBody.getSource().getId() == mCurrentId ? Direct.SEND : Direct.RECEIVE);
        viewHolder.setContainerContent(messageBody);
        setMessageBody(viewHolder, messageBody, i);

        String avatarSrc = mMessageHelper.getRoleAvatar(messageBody.getSource().getType(), messageBody.getSource().getId());
        ImageLoader.getInstance().displayImage(avatarSrc, viewHolder.avatarView, mOptions);

        switch (messageBody.getMsgStatus()) {
            case MessageEntity.StatusType.SUCCESS:
                viewHolder.statusProgressBar.setVisibility(View.GONE);
                viewHolder.errorStatusView.setVisibility(View.GONE);
                break;
            case MessageEntity.StatusType.UPLOADING:
                viewHolder.statusProgressBar.setVisibility(View.VISIBLE);
                viewHolder.errorStatusView.setVisibility(View.GONE);
                viewHolder.lengthView.setVisibility(View.GONE);
                break;
            case MessageEntity.StatusType.FAILED:
                viewHolder.statusProgressBar.setVisibility(View.GONE);
                viewHolder.errorStatusView.setVisibility(View.VISIBLE);
                viewHolder.lengthView.setVisibility(View.VISIBLE);

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

    protected View getItemView(int type) {
        if (type == SEND) {
            return LayoutInflater.from(mContext).inflate(R.layout.item_message_list_send_layout, null);
        }
        return LayoutInflater.from(mContext).inflate(R.layout.item_message_list_receive_layout, null);
    }

    protected SendViewHolder createItemViewHolder(View view) {
        return new SendViewHolder(view);
    }

    private AudioBody getAudioBody(String body) {
        AudioBody audioBody = new AudioBody();
        try {
            JSONObject jsonObject = new JSONObject(body);
            audioBody.setDuration(jsonObject.optInt("duration"));
            audioBody.setFile(jsonObject.optString("file"));
        } catch (JSONException e) {
        }

        return audioBody;
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
                    AudioBody audioBody = getAudioBody(messageBody.getBody());

                    File audioFile = mMessageHelper.getRealAudioFile(audioBody.getFile());
                    if (audioFile == null || !audioFile.exists()) {
                        return;
                    }
                    int[] animResArray = getAudioAnimResArray(messageBody, mCurrentId);
                    mMessageListItemController.onAudioClick(
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
        SendViewHolder
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

    /*

     */
    class SendViewHolder {

        protected Direct mDirect;

        public TextView timeView;
        public TextView lengthView;
        public ImageView avatarView;
        public FrameLayout containerView;
        public ProgressBar statusProgressBar;
        public ImageView errorStatusView;

        public SendViewHolder(View view) {
            lengthView = (TextView) view.findViewById(R.id.tv_audio_length);
            timeView = (TextView) view.findViewById(R.id.tv_time);
            avatarView = (ImageView) view.findViewById(R.id.tv_avatar);
            containerView = (FrameLayout) view.findViewById(R.id.tv_container);
            statusProgressBar = (ProgressBar) view.findViewById(R.id.tv_status_pbar);
            errorStatusView = (ImageView) view.findViewById(R.id.tv_error_status);
        }

        public void setDirect(Direct direct) {
            this.mDirect = direct;
        }

        protected TextView createTextView() {
            return (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_message_list_send_text_content, null);
        }

        protected View createMultiView() {
            return LayoutInflater.from(mContext).inflate(R.layout.item_message_list_multi_content, null);
        }

        protected ImageView createImageView() {
            return (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_message_list_imageview_content, null);
        }

        protected ImageView createAudioView() {
            if (mDirect == Direct.RECEIVE) {
                return (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_message_list_receive_audio_content, null);
            }
            return (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_message_list_send_audio_content, null);
        }

        public void setContainerContent(MessageBody messageBody) {
            containerView.removeAllViews();
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
                    case "multi":
                        processorMulit(messageBody);
                        break;
                    case "image":
                        processorImage(messageBody);
                }
            }

            public void processorMulit(MessageBody messageBody) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                lp.width = (int) (SystemUtil.getScreenWidth(mContext) * 0.5f);

                View view = createMultiView();
                MultiViewHolder viewHolder = new MultiViewHolder(view);
                try {
                    JSONObject jsonObject = new JSONObject(messageBody.getBody());

                    viewHolder.multiTitleView.setText(jsonObject.optString("title"));
                    viewHolder.multiContentView.setText(jsonObject.optString("content"));
                    ImageLoader.getInstance().displayImage(jsonObject.optString("image"), viewHolder.mulitIconView);
                } catch (JSONException e) {
                }

                mViewHolder.containerView.addView(view, lp);
            }

            public void processorText(MessageBody messageBody) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                TextView textView = createTextView();
                textView.setText(messageBody.getBody());
                mViewHolder.containerView.addView(textView, lp);
            }

            public void processorImage(MessageBody messageBody) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;

                String body = messageBody.getBody();
                ImageView imageView = createImageView();
                String imagePath = mMessageHelper.getThumbImagePath(body);

                //local file
                if (imagePath.startsWith("file:")) {
                    ImageLoader.getInstance().displayImage(imagePath, imageView, mOptions);
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

                int[] size = mMessageHelper.getThumbImageSize(body);
                lp.width = size[0];
                lp.height = size[1];

                mViewHolder.containerView.addView(imageView, lp);
            }

            public void processorAudio(MessageBody messageBody) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;

                ImageView audioView = createAudioView();
                AudioBody audioBody = getAudioBody(messageBody.getBody());
                lp.width = 32 + TimeUtil.getDuration(audioBody.getDuration()) * 5;
                lp.height = 32;
                mViewHolder.containerView.addView(audioView, lp);

                switch (messageBody.getMsgStatus()) {
                    case PushUtil.MsgDeliveryType.NONE:
                        try {
                            File realFile = mMessageHelper.createAudioFile(audioBody.getFile());
                            ResourceDownloadTask downloadTask = new ResourceDownloadTask(mContext, messageBody.getMid(), audioBody.getFile(), realFile);
                            IMClient.getClient().getResourceHelper().addTask(downloadTask);
                            messageBody.setMsgStatus(MessageEntity.StatusType.UPLOADING);
                        } catch (IOException ie) {
                            ie.printStackTrace();
                        }
                        break;
                    case PushUtil.MsgDeliveryType.SUCCESS:
                        mViewHolder.lengthView.setVisibility(View.VISIBLE);
                        mViewHolder.lengthView.setText(String.format("%d\"", TimeUtil.getDuration(audioBody.getDuration())));
                        break;
                }
            }
        }
    }
}
