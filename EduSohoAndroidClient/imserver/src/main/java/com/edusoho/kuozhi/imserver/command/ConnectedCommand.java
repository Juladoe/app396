package com.edusoho.kuozhi.imserver.command;

import com.edusoho.kuozhi.imserver.ImServer;

import org.json.JSONObject;

/**
 * Created by su on 2016/3/18.
 */
public class ConnectedCommand extends BaseCommand {

    public ConnectedCommand(ImServer imServer)
    {
        super(imServer);
    }

    @Override
    public void invoke(JSONObject params) {
        mImServer.requestOfflineMsg();
    }

}
