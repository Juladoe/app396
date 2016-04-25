package com.edusoho.kuozhi.imserver.command;

import com.edusoho.kuozhi.imserver.ImServer;
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
        String fromName = params.optString("fromName");
        String msg = params.optString("msg");

        mImServer.onReceiveMessage(msg);
    }
}
