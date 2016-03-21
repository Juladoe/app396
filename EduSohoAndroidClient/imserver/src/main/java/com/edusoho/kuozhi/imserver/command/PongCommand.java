package com.edusoho.kuozhi.imserver.command;

import com.edusoho.kuozhi.imserver.ImServer;
import com.edusoho.kuozhi.imserver.PingManager;
import org.json.JSONObject;


/**
 * Created by su on 2016/3/18.
 */
public class PongCommand extends BaseCommand {

    public PongCommand(ImServer imServer)
    {
        super(imServer);
    }

    @Override
    public void invoke(JSONObject params) {
        super.invoke(params);
        String cmd = params.optString("cmd");
        if ("pong".equals(cmd)) {
            mImServer.getPingManager().setPongResult(PingManager.PONG_SUCCESS);
        } else {
            mImServer.getPingManager().setPongResult(PingManager.PONG_FAIL);
        }
    }
}
