package com.edusoho.kuozhi.imserver.command;

import android.util.Log;

import com.edusoho.kuozhi.imserver.ImServer;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.util.MessageEntityBuildr;

import org.json.JSONObject;

/**
 * Created by su on 2016/3/22.
 */
public class MessageCommand extends BaseCommand {

    public MessageCommand(ImServer imServer)
    {
        super(imServer);
    }

    /**
     * "convNo": "",
     "fromId": "",
     "fromName": "",
     "toId": "",
     "toName": "",
     "msg": "",
     "time": ""
     * @param params
     */
    @Override
    public void invoke(JSONObject params) {
        super.invoke(params);
        String toId = params.optString("toId");
        String fromId = params.optString("fromId");
        String toName = params.optString("toName");
        String fromName = params.optString("fromName");
        String msg = params.optString("msg");
        String convNo = params.optString("convNo");
        String time = params.optString("time");
        String msgNo = params.optString("msgNo");

        if (mImServer.getMsgDbHelper().hasMessageByNo(msgNo)) {
            Log.d("MessageCommand", "hasMessageByNo");
            return;
        }
        MessageEntity messageEntity = MessageEntityBuildr.getBuilder()
                .addToId(toId)
                .addToName(toName)
                .addFromId(fromId)
                .addFromName(fromName)
                .addMsg(msg)
                .addConvNo(convNo)
                .addTime(time)
                .addMsgNo(msgNo)
                .builder();
        mImServer.onReceiveMessage(messageEntity);
        mImServer.ack(params.optString("msgNo"));
    }
}
