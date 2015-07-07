package com.edusoho.kuozhi.v3.model.bal.push;

import com.tencent.android.tpush.XGPushTextMessage;

import java.io.Serializable;

/**
 * Created by JesseHuang on 15/7/3.
 */
public class WrapperXGPushTextMessage extends XGPushTextMessage implements Serializable {
    public String title;
    public String content;
    public boolean isForeground = false;

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

    @Override
    public String getCustomContent() {
        return customContent;
    }

    public void setCustomContent(String customContent) {
        this.customContent = customContent;
    }

    public String customContent;

    public WrapperXGPushTextMessage() {

    }

    public WrapperXGPushTextMessage(XGPushTextMessage xgPushTextMessage) {
        this.title = xgPushTextMessage.getTitle();
        this.content = xgPushTextMessage.getContent();
        this.customContent = xgPushTextMessage.getCustomContent();
    }

}
