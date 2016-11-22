package com.edusoho.kuozhi.imserver.command;

import android.util.Log;

import com.edusoho.kuozhi.imserver.ImServer;

import org.json.JSONObject;

/**
 * Created by su on 2016/3/18.
 */
public class ErrorCommand extends BaseCommand {

    /*
        code 1406 超时
             1407 token过期
     */
    public ErrorCommand(ImServer imServer)
    {
        super(imServer);
    }

    /*
        {
            "cmd": "",
            "code": ,
            "msg": ""
        }
     */
    @Override
    public void invoke(JSONObject params) {
        Log.d("ErrorCommand", "invoke");
        mImServer.setServerInValid();
    }

}
