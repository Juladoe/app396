package com.edusoho.kuozhi.v3.ui.live;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.ui.IMessageListView;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.im.LiveMessageBody;
import com.edusoho.kuozhi.v3.model.live.Signal;
import com.edusoho.kuozhi.v3.model.provider.LiveRoomProvider;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Promise;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suju on 16/10/18.
 */
public class LiveVideoPresenterImpl implements ILiveVideoPresenter {

    private static final int FADE_OUT = 0x02;

    private Bundle mLiveData;
    private Context mContext;
    private ILiveVideoView mILiveVideoView;
    private IMessageListView mIMessageListView;

    public LiveVideoPresenterImpl(Context context, Bundle liveData, ILiveVideoView videoView, IMessageListView messageListView) {
        this.mContext = context;
        this.mLiveData = liveData;
        this.mILiveVideoView = videoView;
        this.mIMessageListView = messageListView;
    }

    private Promise getServerTime() {
        final Promise promise = new Promise();
        new LiveRoomProvider(mContext).getLiveServerTime()
        .success(new NormalCallback<LinkedHashMap>() {
            @Override
            public void success(LinkedHashMap data) {
                Double time = (Double) data.get("time");
                promise.resolve(time.longValue());
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                promise.resolve(0);
            }
        });

        return promise;
    }

    @Override
    public void handleHistorySignals() {
        getServerTime().then(new PromiseCallback<Long>() {
            @Override
            public Promise invoke(Long time) {
                String roomNo = mLiveData.get("roomNo").toString();
                String token = mLiveData.get("token").toString();
                String role = mLiveData.get("role").toString();
                String clientId = mLiveData.get("clientId").toString();

                long startTime = 0;
                new LiveRoomProvider(mContext).getLiveSignals(
                        roomNo, token, role, clientId, startTime, time
                ).success(new NormalCallback<LinkedHashMap<String, Signal>>() {
                    @Override
                    public void success(LinkedHashMap<String, Signal> signalList) {
                        invokeSignals(signalList);
                    }
                });
                return null;
            }
        });
    }

    private void invokeSignals(LinkedHashMap<String, Signal> signalMap) {
        Map<String, Boolean> typeFilterMap = new HashMap<>();
        List<String> keyArray = new ArrayList<>(signalMap.keySet());
        Collections.sort(keyArray, new Comparator<String>() {
            @Override
            public int compare(String s, String s2) {
                return (int) (AppUtil.parseLong(s2) - AppUtil.parseLong(s));
            }
        });
        for (String key : keyArray) {
            Signal signal = signalMap.get(key);
            if (typeFilterMap.containsKey(signal.getType())) {
                continue;
            }
            typeFilterMap.put(signal.getType(), true);
            switch (signal.getType()) {
                case "101002":
                    Map data = signal.getData();
                    mILiveVideoView.setLivePlayStatus(Boolean.TRUE.equals(data.get("isResting")));
                    break;
                case "102002":
                    Map noticeData = signal.getData();
                    mILiveVideoView.setNotice(noticeData.get("info").toString());
                    break;
                case "103005":
                    Map allCanChatData = signal.getData();
                    mIMessageListView.setEnable(Boolean.TRUE.equals(allCanChatData.get("isAllCanChat")));
                    break;
                case "103004":
                    Map canChatData = signal.getData();
                    mIMessageListView.setEnable(Boolean.TRUE.equals(canChatData.get("isCanChat")));
                    break;
            }
        }
    }

    @Override
    public void updateLivePlayStatus(MessageEntity messageEntity) {
        if ("103010".equals(messageEntity.getCmd())) {
            mILiveVideoView.checkLivePlayStatus();
            return;
        }
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
            mILiveVideoView.showNoticeView();
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), 5000);
        } catch (JSONException e) {
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FADE_OUT:
                    mILiveVideoView.hideNoticeView();
            }
        }
    };
}
