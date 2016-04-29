package com.edusoho.kuozhi.imserver.command;

import com.edusoho.kuozhi.imserver.ImServer;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.util.MessageEntityBuildr;

import org.json.JSONObject;

/**
 * Created by 菊 on 2016/4/27.
 */
public class SuccessCommand extends BaseCommand {

    public SuccessCommand(ImServer imServer)
    {
        super(imServer);
    }

    @Override
    public void invoke(JSONObject params) {
        super.invoke(params);
        String msg = params.optString("msg");
        MessageEntity messageEntity = MessageEntityBuildr.getBuilder()
                .addCmd("success")
                .addMsg(msg)
                .builder();
        mImServer.onReceiveMessage(messageEntity);
    }
}
