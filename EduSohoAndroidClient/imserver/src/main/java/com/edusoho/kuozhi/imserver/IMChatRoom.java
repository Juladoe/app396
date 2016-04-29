package com.edusoho.kuozhi.imserver;

import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by 菊 on 2016/4/23.
 */
public class IMChatRoom {

    protected String mConvNo;
    protected WeakReference<IImServerAidlInterface> mImBinderRef;

    public IMChatRoom(String convNo, IImServerAidlInterface imBinder)
    {
        this.mConvNo = convNo;
        this.mImBinderRef = new WeakReference<IImServerAidlInterface>(imBinder);
    }

    public void send(SendEntity sendEntity) {
        try {
            sendEntity.setConvNo(mConvNo);
            mImBinderRef.get().send(sendEntity);
        } catch (Exception e) {
        }
    }
}
