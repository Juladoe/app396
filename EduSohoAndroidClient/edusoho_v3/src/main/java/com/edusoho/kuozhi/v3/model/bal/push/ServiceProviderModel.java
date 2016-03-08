package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.Gson;

/**
 * Created by howzhi on 15/9/24.
 */
public class ServiceProviderModel {

    public int id;
    public int spId;
    public int toId;
    public String title;
    public String content;
    public String type;
    public String body;
    public int createdTime;

    public ServiceProviderModel(){
    }

    public ServiceProviderModel(WrapperXGPushTextMessage xgMessage)
    {
        V2CustomContent v2CustomContent = xgMessage.getV2CustomContent();
        V2CustomContent.BodyEntity bodyEntity = v2CustomContent.getBody();
        this.id = v2CustomContent.getMsgId();
        this.spId = v2CustomContent.getFrom().getId();
        this.toId = EdusohoApp.app.loginUser.id;
        this.title = xgMessage.getTitle();
        this.content = xgMessage.getContent();
        this.type = bodyEntity.getType();
        this.body = new Gson().toJson(bodyEntity);
        this.createdTime = v2CustomContent.getCreatedTime();
    }
}
