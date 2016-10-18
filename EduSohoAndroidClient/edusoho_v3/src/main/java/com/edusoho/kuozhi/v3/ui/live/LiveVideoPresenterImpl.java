package com.edusoho.kuozhi.v3.ui.live;

import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.v3.model.im.LiveMessageBody;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by suju on 16/10/18.
 */
public class LiveVideoPresenterImpl implements ILiveVideoPresenter {

    private ILiveVideoView mILiveVideoView;

    public LiveVideoPresenterImpl(ILiveVideoView videoView) {
        this.mILiveVideoView = videoView;
    }

    @Override
    public void updateLivePlayStatus(MessageEntity messageEntity) {
        LiveMessageBody liveMessageBody = new LiveMessageBody(messageEntity.getMsg());
        try {
            JSONObject jsonObject = new JSONObject(liveMessageBody.getData());
            mILiveVideoView.setLivePlayStatus(jsonObject.optBoolean("isResting"));
        } catch (JSONException e) {
        }
    }

    @Override
    public void updateNotice(MessageEntity message) {
        LiveMessageBody liveMessageBody = new LiveMessageBody(message.getMsg());
        try {
            JSONObject jsonObject = new JSONObject(liveMessageBody.getData());
            mILiveVideoView.setNotice(jsonObject.optString("info"));
        } catch (JSONException e) {
        }
    }
}
