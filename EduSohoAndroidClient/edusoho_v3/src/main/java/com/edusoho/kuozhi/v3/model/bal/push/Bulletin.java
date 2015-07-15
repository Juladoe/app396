package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.reflect.TypeToken;

/**
 * Created by JesseHuang on 15/7/7.
 */
public class Bulletin {
    public int id;
    public String content;
    public int createdTime;
    public String schoolDomain;

    public Bulletin() {

    }

    public Bulletin(WrapperXGPushTextMessage message) {
        CustomContent customContent = EdusohoApp.app.parseJsonValue(message.getCustomContent(), new TypeToken<CustomContent>() {
        });
        id = customContent.getId();
        content = message.getContent();
        createdTime = customContent.getCreatedTime();
        schoolDomain = EdusohoApp.app.domain;
    }
}
