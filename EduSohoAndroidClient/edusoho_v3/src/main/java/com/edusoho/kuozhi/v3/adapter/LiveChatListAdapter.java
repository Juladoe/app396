package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.entity.message.MessageBody;
import com.edusoho.kuozhi.imserver.ui.adapter.MessageRecyclerListAdapter;
import com.edusoho.kuozhi.imserver.util.TimeUtil;
import com.edusoho.kuozhi.v3.model.im.LiveMessageBody;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by suju on 16/10/13.
 */
public class LiveChatListAdapter extends MessageRecyclerListAdapter {

    public LiveChatListAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(MessageRecyclerListAdapter.MessageViewHolder viewHolder, int position) {
        LiveMessageBody messageBody = new LiveMessageBody(mMessageList.get(position).getMsg());
        if (viewHolder instanceof LiveTextViewHolder) {
            ((LiveTextViewHolder) viewHolder).setLiveMessageBody(messageBody, position);
            return;
        }
        super.onBindViewHolder(viewHolder, position);
    }

    @Override
    public int getItemViewType(int position) {
        MessageEntity messageEntity = mMessageList.get(position);
        if (TextUtils.isEmpty(messageEntity.getCmd())) {
            return super.getItemViewType(position);
        }
        switch (messageEntity.getCmd()) {
            case "103004":
            case "103005":
                return LABEL;
        }
        return super.getItemViewType(position);
    }

    protected MessageViewHolder createViewHolder(int viewType, View contentView) {
        if (viewType == RECEIVE_TEXT || viewType == SEND_TEXT) {
            return new LiveTextViewHolder(contentView);
        }
        if (viewType == LABEL) {
            return new LiveLabelViewHolder(contentView);
        }
        return super.createViewHolder(viewType, contentView);
    }

    protected class LiveLabelViewHolder extends LabelViewHolder {

        public LiveLabelViewHolder(View view) {
            super(view);
        }

        @Override
        public void setMessageBody(MessageBody messageBody, int position) {
            try {
                LiveMessageBody liveMessageBody = new LiveMessageBody(mMessageList.get(position).getMsg());
                JSONObject jsonObject = new JSONObject(liveMessageBody.getData());
                switch (liveMessageBody.getType()) {
                    case "103004":
                        mContentView.setVisibility(View.VISIBLE);
                        if (jsonObject.optBoolean("isCanChat")) {
                            mContentView.setText(String.format("管理员将 %s 解除禁言", liveMessageBody.getClientName()));
                        } else {
                            mContentView.setText(String.format("管理员将 %s 禁言", liveMessageBody.getClientName()));
                        }
                        break;
                    case "103005":
                        mContentView.setVisibility(View.VISIBLE);
                        if (jsonObject.optBoolean("isAllCanChat")) {
                            mContentView.setText("管理员将全体解除禁言");
                        } else {
                            mContentView.setText("管理员将全体禁言");
                        }
                        break;
                }
            } catch (JSONException e) {
            }
        }
    }

    protected class LiveTextViewHolder extends TextViewHolder {

        public LiveTextViewHolder(View view) {
            super(view);
        }

        public void setLiveMessageBody(LiveMessageBody messageBody, int position) {
            String body = "";
            try {
                JSONObject jsonObject = new JSONObject(messageBody.getData());
                body = jsonObject.optString("info");
            } catch (JSONException je) {
            }
            mContentView.setText(body);

            timeView.setVisibility(View.GONE);
            if (position < (getItemCount() - 1)) {
                long preTime = mMessageList.get(position + 1).getTime() * 1000L;
                if (messageBody.getTime() - preTime > TIME_INTERVAL) {
                    timeView.setVisibility(View.VISIBLE);
                    timeView.setText(TimeUtil.convertMills2Date(messageBody.getTime()));
                }
                return;
            }
            timeView.setVisibility(View.VISIBLE);
            timeView.setText(TimeUtil.convertMills2Date(messageBody.getTime()));
        }
    }
}
