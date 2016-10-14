package com.edusoho.kuozhi.imserver.command;

import com.edusoho.kuozhi.imserver.ImServer;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.util.MessageEntityBuildr;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by su on 2016/3/18.
 */
public class MemberJoinedCommand extends BaseCommand {

    public MemberJoinedCommand(ImServer imServer) {
        super(imServer);
    }

    @Override
    public void invoke(JSONObject params) {
        super.invoke(params);
        String clientId = params.optString("clientId");
        String clientName = params.optString("clientName");
        String msg = params.optString("msg");
        String convNo = params.optString("convNo");
        int time = params.optInt("time");
        String msgNo = params.optString("msgNo");
        String cmd = params.optString("cmd");

        MessageEntity messageEntity =
                MessageEntityBuildr.getBuilder()
                        .addExtend(wrapExtend(clientId, clientName))
                        .addMsg(msg)
                        .addConvNo(convNo)
                        .addTime(time)
                        .addMsgNo(msgNo)
                        .addCmd(cmd)
                        .addStatus(MessageEntity.StatusType.NONE)
                        .builder();
        mImServer.onReceiveMessage(messageEntity);
    }

    private String wrapExtend(String clientId, String clientName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clientId", clientId);
            jsonObject.put("clientName", clientName);
        } catch (JSONException e) {
        }

        return jsonObject.toString();
    }
}
