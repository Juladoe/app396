package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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

    protected MessageViewHolder createViewHolder(int viewType, View contentView) {
        if (viewType == RECEIVE_TEXT || viewType == SEND_TEXT) {
            return new LiveTextViewHolder(contentView);
        }
        return super.createViewHolder(viewType, contentView);
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
