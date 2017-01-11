package com.edusoho.kuozhi.v3.entity.lesson;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.ui.adapter.MessageRecyclerListAdapter;
import com.edusoho.kuozhi.imserver.ui.entity.PushUtil;
import com.edusoho.kuozhi.imserver.util.TimeUtil;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.ui.DiscussDetailActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by DF on 2017/1/5.
 */

public class QuestionAnswerAdapter extends MessageRecyclerListAdapter {

    private View VIEW_HEADER;
    private Bundle info;
    //Type
    private static final int TYPE_HEADER = 1001;
    private Dialog dialog;
    private ImageView ivBig;


    public QuestionAnswerAdapter(Context context) {
        super(context);
    }

    @Override
    public void setList(List<MessageEntity> messageBodyList) {
        mMessageList.clear();
        mMessageList.add(new QuestionHeaderMessageEntity());
        mMessageList.addAll(messageBodyList);
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        mMessageList.clear();
        mMessageList.add(new QuestionHeaderMessageEntity());
        notifyDataSetChanged();
    }

    @Override
    public void addItem(MessageEntity messageBody) {
        mMessageList.add(1, messageBody);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else {
            MessageBody messageBody = new MessageBody(mMessageList.get(position));
            String type = messageBody.getType();
            switch (type) {
                case PushUtil.ChatMsgType.TEXT:
                    return RECEIVE_TEXT;
                case PushUtil.ChatMsgType.AUDIO:
                    return RECEIVE_AUDIO;
                case PushUtil.ChatMsgType.IMAGE:
                    return RECEIVE_IMAGE;
                case PushUtil.ChatMsgType.PUSH:
                case PushUtil.ChatMsgType.MULTI:
                    return RECEIVE_MULTI;
                case PushUtil.ChatMsgType.LABEL:
                    return LABEL;
            }
            return RECEIVE_TEXT;
        }
    }

    @Override
    protected View getItemView(int type) {
        if (type == TYPE_HEADER) {
            return createHeadView();
        }
        return super.getItemView(type);
    }

    public View createHeadView(){
        return VIEW_HEADER;
    }


    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, int position) {
        if (isHeaderView(position)) {
            initHeadInfo(info);
            return;
        }
        super.onBindViewHolder(viewHolder, position);
    }

    @Override
    protected MessageViewHolder createViewHolder(int viewType, View contentView) {
        if (viewType == TYPE_HEADER) {
            new HeadViewHolder(contentView);
        } else {
            switch (viewType) {
                case SEND_AUDIO:
                case RECEIVE_AUDIO:
                    return new NewAudioViewHolder(contentView);
                case SEND_IMAGE:
                case RECEIVE_IMAGE:
                    return new NewImageViewHolder(contentView);
            }
        }
        return new NewTextViewHolder(contentView);
    }

    @Override
    protected View createTextView(boolean isSend) {
        return LayoutInflater.from(mContext).inflate(R.layout.item_new_message_list_receive_text_layout, null);
    }

    @Override
    protected View createImageView(boolean isSend) {
        return LayoutInflater.from(mContext).inflate(R.layout.item_new_message_lis_receive_image_content, null);
    }

    @Override
    protected View createAudioView(boolean isSend) {
        return LayoutInflater.from(mContext).inflate(R.layout.item_new_message_list_receive_audio_content, null);
    }

    protected class NewMessageViewHolder extends MessageViewHolder {

        public NewMessageViewHolder(View view) {
            super(view);
        }

        @Override
        public void setMessageBody(MessageBody messageBody, int position) {
            nicknameView.setText(messageBody.getSource().getNickname());
        }
    }

    protected class NewTextViewHolder extends TextViewHolder {

        public NewTextViewHolder(View view) {
            super(view);
        }

        @Override
        public void setMessageBody(MessageBody messageBody, int position) {
            nicknameView.setText(messageBody.getSource().getNickname());
            timeView.setText(TimeUtil.convertMills2Date(messageBody.getCreatedTime()));
        }
    }

    protected class NewImageViewHolder extends ImageVewHolder {

        public NewImageViewHolder(View view) {
            super(view);
        }

        @Override
        public void setMessageBody(MessageBody messageBody, int position) {
            nicknameView.setText(messageBody.getSource().getNickname());
            timeView.setText(TimeUtil.convertMills2Date(messageBody.getCreatedTime()));
        }
    }

    protected class NewAudioViewHolder extends AudioViewHolder {

        public NewAudioViewHolder(View view) {
            super(view);
        }

        @Override
        public void setMessageBody(MessageBody messageBody, int position) {
            timeView.setText(TimeUtil.convertMills2Date(messageBody.getCreatedTime()));
        }
    }

    class HeadViewHolder extends NewMessageViewHolder {

        public HeadViewHolder(View view) {
            super(view);
        }
    }

    private void initHeadInfo(Bundle info) {
        DiscussDetail.ResourcesBean resourcesBean = (DiscussDetail.ResourcesBean) info.getSerializable("coursebean");
        ((TextView) VIEW_HEADER.findViewById(R.id.tdh_time)).setText(resourcesBean.getCreatedTime().split("T")[0]);
        ((TextView) VIEW_HEADER.findViewById(R.id.tdh_title)).setText(resourcesBean.getTitle());
        ((TextView) VIEW_HEADER.findViewById(R.id.tdh_content)).setText(resourcesBean.getContent());
        ImageLoader.getInstance().displayImage(resourcesBean.getUser().getAvatar(), (RoundedImageView) VIEW_HEADER.findViewById(R.id.tdh_avatar));
        ((TextView) VIEW_HEADER.findViewById(R.id.tdh_nickname)).setText(resourcesBean.getUser().getNickname());
        if ("question".equals(resourcesBean.getType())) {
            ((TextView) VIEW_HEADER.findViewById(R.id.tdh_label)).setText("问题");
        } else {
            ((TextView) VIEW_HEADER.findViewById(R.id.tdh_label)).setText("话题");
        }
        VIEW_HEADER.findViewById(R.id.tdh_label).setBackgroundResource(R.drawable.shape_question_answer);
        if ("course".equals(info.getString(DiscussDetailActivity.THREAD_TARGET_TYPE))) {
            ((TextView) VIEW_HEADER.findViewById(R.id.tdh_from_course)).setText(String.format("来自课程《%s》", info.getString("title")));
        } else {
            ((TextView) VIEW_HEADER.findViewById(R.id.tdh_from_course)).setText(String.format("来自班级《%s》", info.getString("title")));
        }
    }

    public void addHeaderView(View headerView, Bundle info) {
        if (haveHeaderView()) {
            //throw new IllegalStateException("hearview has already exists!");
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(params);
            VIEW_HEADER = headerView;
            this.info = info;
            notifyItemInserted(0);
        }
        mMessageList.add(new QuestionHeaderMessageEntity());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    private boolean haveHeaderView() {
        return VIEW_HEADER != null;
    }

    private boolean isHeaderView(int position) {
        MessageEntity messageEntity = mMessageList.get(position);
        return messageEntity instanceof QuestionHeaderMessageEntity;
    }

    public boolean isAdd;
    private void showBigImage(String url) {
        if (!isAdd) {
            isAdd = true;
            dialog = new Dialog(mContext, R.style.dialog_big_image);
            View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_image, null);
            ivBig = (ImageView) dialogView.findViewById(R.id.iv_big);
            dialogView.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    ivBig.setImageResource(R.drawable.oval_white_bg);
                }
            });
            dialog.setContentView(dialogView);
            dialog.setCancelable(false);
        }
        ImageLoader.getInstance().displayImage(url, ivBig);
        dialog.show();
    }

    class QuestionHeaderMessageEntity extends MessageEntity {

    }
}
