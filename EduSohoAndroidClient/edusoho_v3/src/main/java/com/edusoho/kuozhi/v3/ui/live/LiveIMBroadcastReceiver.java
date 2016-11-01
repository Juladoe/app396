package com.edusoho.kuozhi.v3.ui.live;

import android.text.TextUtils;

import com.edusoho.kuozhi.imserver.broadcast.IMBroadcastReceiver;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.v3.model.im.LiveMessageBody;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

/**
 * Created by suju on 16/10/18.
 */
public class LiveIMBroadcastReceiver extends IMBroadcastReceiver {

    private ILiveChatPresenter mILiveChatMessgeListPresenter;
    private ILiveVideoPresenter mILiveVideoPresenter;

    String[] filterArray = {
            "100001",
            "102001",
            "101003",
            "102002",
            "103004",
            "103005",
            "103007"
    };

    String[] signalArray = {
            "100001",
            "101003",
            "102002",
            "103004",
            "103005",
            "103007"
    };

    public LiveIMBroadcastReceiver(ILiveVideoPresenter presenter, ILiveChatPresenter liveChatMessgeListPresenter) {
        this.mILiveVideoPresenter = presenter;
        this.mILiveChatMessgeListPresenter = liveChatMessgeListPresenter;
    }

    private boolean messageIsFilter(String type) {
        for (String filter : filterArray) {
            if (type.equals(filter)) {
                return true;
            }
        }
        return false;
    }

    private boolean messageIsSignal(String type) {
        for (String filter : signalArray) {
            if (type.equals(filter)) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void invokeReceiverSignal(MessageEntity message) {
        String cmd = message.getCmd();
        switch (cmd) {
            case "replace":
                mILiveChatMessgeListPresenter.onReplace();
                break;
            case "connected":
                mILiveChatMessgeListPresenter.joinLiveChatRoom();
                break;
            case "102002":
                mILiveVideoPresenter.updateNotice(message);
                break;
            case "103004":
            case "103005":
                mILiveChatMessgeListPresenter.setUserCanChatStatus(message);
                break;
            case "103007":
                mILiveVideoPresenter.onKill(message);
                break;
            case "100001":
                mILiveVideoPresenter.updateLivePlayStatus(message);
        }
    }

    @Override
    protected void invokeReceiver(MessageEntity message) {
        LiveMessageBody liveMessageBody = new LiveMessageBody(message.getMsg());
        if (liveMessageBody != null && !messageIsFilter(liveMessageBody.getType())) {
            return;
        }
        if (liveMessageBody != null && messageIsSignal(liveMessageBody.getType())) {
            message.setCmd(liveMessageBody.getType());
            invokeReceiverSignal(message);
            return;
        }

        if (TextUtils.isEmpty(message.getCmd())) {
            mILiveChatMessgeListPresenter.onHandleMessage(message);
            return;
        }
        switch (message.getCmd()) {
            case "102001":
            case "103004":
            case "103005":
            case "memberJoined":
                mILiveChatMessgeListPresenter.onHandleMessage(message);
                break;
        }
    }

    @Override
    protected void invokeOfflineMsgReceiver(List<MessageEntity> messageEntityList) {
    }
}
