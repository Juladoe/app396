package com.edusoho.kuozhi.imserver.managar;

import android.content.Context;

import com.edusoho.kuozhi.imserver.IImServerAidlInterface;
import com.edusoho.kuozhi.imserver.SendEntity;
import com.edusoho.kuozhi.imserver.entity.MessageEntity;
import com.edusoho.kuozhi.imserver.util.MsgDbHelper;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by 菊 on 2016/4/23.
 */
public class IMChatRoom {

    protected String mConvNo;
    protected Context mContext;
    protected WeakReference<IImServerAidlInterface> mImBinderRef;

    public IMChatRoom(Context context, String convNo, IImServerAidlInterface imBinder) {
        this.mConvNo = convNo;
        this.mContext = context;
        this.mImBinderRef = new WeakReference<IImServerAidlInterface>(imBinder);
    }

    public void send(SendEntity sendEntity) {
        try {
            sendEntity.setConvNo(mConvNo);
            mImBinderRef.get().send(sendEntity);
        } catch (Exception e) {
        }
    }

    public List<MessageEntity> getMessageList(int start) {
        return new MsgDbHelper(mContext).getMessageList(mConvNo, start);
    }
}
