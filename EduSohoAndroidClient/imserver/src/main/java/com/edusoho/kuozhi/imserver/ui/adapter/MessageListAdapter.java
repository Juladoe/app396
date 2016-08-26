package com.edusoho.kuozhi.imserver.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.imserver.R;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.ui.entity.Direct;
import com.edusoho.kuozhi.imserver.ui.helper.MessageHelper;
import com.edusoho.kuozhi.imserver.util.TimeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suju on 16/8/26.
 */
public class MessageListAdapter extends BaseAdapter {

    protected static long TIME_INTERVAL = 60 * 5 * 1000;

    private int mCurrentId;
    private MessageHelper mMessageHelper;
    protected Context mContext;
    protected List<MessageBody> mMessageList;
    protected DisplayImageOptions mOptions;

    public MessageListAdapter(Context context) {
        this.mContext = context;
        this.mMessageList = new ArrayList<>();
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).
                showImageOnLoading(R.drawable.message_image_default).
                showImageForEmptyUri(R.drawable.message_image_default).
                showImageOnFail(R.drawable.message_image_default).build();

        this.mMessageHelper = new MessageHelper(mContext);
    }

    public MessageListAdapter(Context context, int currentId) {
        this(context);
        this.mCurrentId = currentId;
    }

    public void setCurrentId(int currentId) {
        this.mCurrentId = currentId;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        SendViewHolder viewHolder = null;
        MessageBody messageBody = mMessageList.get(i);
        if (view == null) {
            view = getItemView(messageBody.getSource().getId() == mCurrentId);
            view.setTag(createItemViewHolder(view));
        }

        viewHolder = (SendViewHolder) view.getTag();
        viewHolder.setDirect(messageBody.getSource().getId() == mCurrentId ? Direct.SEND : Direct.RECEIVE);
        viewHolder.setContainerContent(messageBody.getType(), messageBody.getBody());
        setMessageBody(viewHolder, messageBody, i);
        return view;
    }

    public void setMessageBody(SendViewHolder viewHolder, MessageBody messageBody, int position) {
        viewHolder.timeView.setVisibility(View.GONE);
        if (position > 0) {
            if (messageBody.getCreatedTime() - mMessageList.get(position - 1).getCreatedTime() > TIME_INTERVAL) {
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
    public MessageBody getItem(int i) {
        return mMessageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addList(List<MessageBody> messageBodyList) {
        mMessageList.addAll(messageBodyList);
        notifyDataSetChanged();
    }

    public void addItem(MessageBody messageBody) {
        mMessageList.add(messageBody);
        notifyDataSetChanged();
    }

    protected View getItemView(boolean isSend) {
        if (isSend) {
            return LayoutInflater.from(mContext).inflate(R.layout.item_message_list_send_layout, null);
        }
        return LayoutInflater.from(mContext).inflate(R.layout.item_message_list_receive_layout, null);
    }

    protected SendViewHolder createItemViewHolder(View view) {
        return new SendViewHolder(view);
    }

    class SendViewHolder {

        protected Direct mDirect;

        public TextView timeView;
        public ImageView avatarView;
        public FrameLayout containerView;
        public ProgressBar statusProgressBar;
        public ImageView errorStatusView;

        public SendViewHolder(View view) {
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

        protected ImageView createImageView() {
            return (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_message_list_imageview_content, null);
        }

        protected ImageView createAudioView() {
            if (mDirect == Direct.RECEIVE) {
                return (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_message_list_receive_audio_content, null);
            }
            return (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_message_list_send_audio_content, null);
        }

        public void setContainerContent(String contentType, String body) {
            containerView.removeAllViews();
            switch (contentType) {
                case "text":
                    TextView textView = createTextView();
                    textView.setText(body);
                    containerView.addView(textView);
                    break;
                case "audio":
                    ImageView audioView = createAudioView();
                    containerView.addView(audioView);
                    break;
                case "image":
                    ImageView imageView = createImageView();
                    ImageLoader.getInstance().displayImage(mMessageHelper.getThumbImagePath(body), imageView);
                    containerView.addView(imageView);
            }
        }
    }

}
