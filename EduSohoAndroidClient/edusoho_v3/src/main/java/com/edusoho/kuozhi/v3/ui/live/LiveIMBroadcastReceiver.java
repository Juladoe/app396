package com.edusoho.kuozhi.v3.ui.live;

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
            "100002",
            "101001",
            "101002",
            "101003",
            "103004",
            "103005",
            "103007",
            "103008",
            "103009",
            "103010",
            "102002"
    };

    public LiveIMBroadcastReceiver(ILiveVideoPresenter presenter, ILiveChatPresenter liveChatMessgeListPresenter) {
        this.mILiveVideoPresenter = presenter;
        this.mILiveChatMessgeListPresenter = liveChatMessgeListPresenter;
    }

    private boolean messageIsSignal(String type) {
        for (String filter : filterArray) {
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
            case "connected":
                mILiveChatMessgeListPresenter.joinLiveChatRoom();
                break;
            case "102002":
                mILiveVideoPresenter.updateNotice(message);
                break;
            case "103004":
            case "103005":
                mILiveChatMessgeListPresenter.setUserCanChatStatus(message);
                mILiveChatMessgeListPresenter.onHandleMessage(message);
                break;
            case "103010":
                mILiveVideoPresenter.updateLivePlayStatus(message);
                break;
            case "101002":
                mILiveVideoPresenter.updateLivePlayStatus(message);
        }
    }

    @Override
    protected void invokeReceiver(MessageEntity message) {
        LiveMessageBody liveMessageBody = new LiveMessageBody(message.getMsg());
        if (liveMessageBody != null && messageIsSignal(liveMessageBody.getType())) {
            message.setCmd(liveMessageBody.getType());
            invokeReceiverSignal(message);
            return;
        }
        mILiveChatMessgeListPresenter.onHandleMessage(message);
    }

    @Override
    protected void invokeOfflineMsgReceiver(List<MessageEntity> messageEntityList) {
    }
}
