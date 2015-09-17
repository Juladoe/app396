package com.edusoho.kuozhi.v3.model.bal.push;

import com.google.gson.Gson;
import com.tencent.android.tpush.XGPushTextMessage;

import java.io.Serializable;

/**
 * Created by JesseHuang on 15/7/3.
 */
public class WrapperXGPushTextMessage extends XGPushTextMessage implements Serializable {
    public String title;
    public String content;
    public String customContentJson;
    public boolean isForeground = false;

    public WrapperXGPushTextMessage() {

    }

    public WrapperXGPushTextMessage(XGPushTextMessage xgPushTextMessage) {
        this.title = xgPushTextMessage.getTitle();
        this.content = xgPushTextMessage.getContent();
        this.customContentJson = xgPushTextMessage.getCustomContent();
    }

    public boolean isForeground() {
        return isForeground;
    }

    public void setForeground(boolean isForeground) {
        this.isForeground = isForeground;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCustomContentJson() {
        return customContentJson;
    }

    public void setCustomContentJson(String customContent) {
        this.customContentJson = customContent;
    }

    public V2CustomContent getV2CustomContent() {
        Gson gson = new Gson();
        return gson.fromJson(this.customContentJson, V2CustomContent.class);
    }
}
