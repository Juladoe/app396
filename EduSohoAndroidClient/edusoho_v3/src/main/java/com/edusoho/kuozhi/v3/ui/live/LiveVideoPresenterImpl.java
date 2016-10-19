package com.edusoho.kuozhi.v3.ui.live;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.im.LiveMessageBody;
import com.edusoho.kuozhi.v3.model.provider.LiveRoomProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by suju on 16/10/18.
 */
public class LiveVideoPresenterImpl implements ILiveVideoPresenter {

    private Bundle mLiveData;
    private Context mContext;
    private ILiveVideoView mILiveVideoView;

    public LiveVideoPresenterImpl(Context context, Bundle liveData, ILiveVideoView videoView) {
        this.mContext = context;
        this.mLiveData = liveData;
        this.mILiveVideoView = videoView;
    }

    @Override
    public void handleHistorySignals() {
        String roomNo = mLiveData.get("roomNo").toString();
        String token = mLiveData.get("token").toString();
        String role = mLiveData.get("role").toString();
        String clientId = mLiveData.get("clientId").toString();

        int startTime = 0;
        int endTime = (int) (System.currentTimeMillis() / 1000);
        new LiveRoomProvider(mContext).getLiveSignals(
                roomNo, token, role, clientId, startTime, endTime
        ).success(new NormalCallback<ArrayList>() {
            @Override
            public void success(ArrayList arrayList) {
                Log.d("getHistorySignals:", "" + arrayList);
            }
        });
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
