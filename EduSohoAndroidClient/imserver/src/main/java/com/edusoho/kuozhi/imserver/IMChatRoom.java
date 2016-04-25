package com.edusoho.kuozhi.imserver;

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

    public void send(String message) {
        try {
            mImBinderRef.get().send(mConvNo, message);
        } catch (Exception e) {
        }
    }
}
