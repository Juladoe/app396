package com.edusoho.kuozhi.v3.ui.live;

import android.text.TextUtils;

import com.baidu.cyberplayer.utils.P;
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

    private String mConvNo;
    private ILiveChatPresenter mILiveChatMessgeListPresenter;
    private ILiveVideoPresenter mILiveVideoPresenter;

    String[] filterArray = {
            "100001",
            "102001",
            "101003",
            "102002",
            "103004",
            "103005",
            "103007",
            "memberJoined",
            "success"
    };

    String[] signalArray = {
            "100001",
            "101003",
            "102002",
            "103004",
            "103005",
            "103007"
    };

    public LiveIMBroadcastReceiver(
            String convNo,
            ILiveVideoPresenter presenter,
            ILiveChatPresenter liveChatMessgeListPresenter) {
        this.mConvNo = convNo;
        this.mILiveVideoPresenter = presenter;
        this.mILiveChatMessgeListPresenter = liveChatMessgeListPresenter;
    }

    private boolean messageIsFilter(String type) {
        if (type == null) {
            return false;
        }
        for (String filter : filterArray) {
            if (type.equals(filter)) {
                return true;
            }
        }
        return false;
    }

    private boolean messageIsSignal(String type) {
        if (type == null) {
            return false;
        }
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
        if ("connected".equals(cmd)) {
            mILiveChatMessgeListPresenter.joinLiveChatRoom();
            return;
        }
        if (TextUtils.isEmpty(mConvNo) || !mConvNo.equals(message.getConvNo())) {
            return;
        }
        switch (cmd) {
            case "replace":
                mILiveChatMessgeListPresenter.onReplace();
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
        LiveMessageBody liveMessageBody = new LiveMessageBody(message);
        if (liveMessageBody != null && !messageIsFilter(liveMessageBody.getType())) {
            return;
        }
        if (liveMessageBody != null && messageIsSignal(liveMessageBody.getType())) {
            message.setCmd(liveMessageBody.getType());
            invokeReceiverSignal(message);
            return;
        }

        if (!TextUtils.isEmpty(message.getConvNo())
                && (TextUtils.isEmpty(mConvNo) || !mConvNo.equals(message.getConvNo()))) {
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
            case "success":
                mILiveChatMessgeListPresenter.onSuccess(message);
                break;
            case "memberJoined":
            case "flashMessage":
            case "message":
                mILiveChatMessgeListPresenter.onHandleMessage(message);
                break;
        }
    }

    @Override
    protected void invokeOfflineMsgReceiver(List<MessageEntity> messageEntityList) {
    }
}
