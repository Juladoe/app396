package com.edusoho.kuozhi.imserver.managar;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.util.ConvDbHelper;

import java.util.List;

/**
 * Created by 菊 on 2016/5/15.
 */
public class IMConvManager {

    private Context mContext;

    public IMConvManager(Context context) {
        this.mContext = context;
    }

    public List<ConvEntity> getConvList() {
        return new ConvDbHelper(mContext).getConvList();
    }

    public List<ConvEntity> getConvListByType(String type) {
        return new ConvDbHelper(mContext).getConvListByType(type);
    }

    public List<ConvEntity> getConvListByUid(int uid) {
        return new ConvDbHelper(mContext).getConvListByUid(uid);
    }

    public ConvEntity getSingleConv(String convNo) {
        return new ConvDbHelper(mContext).getConvByConNo(convNo);
    }

    public ConvEntity getConvByTypeAndId(String type, int targetId, int uid) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        return new ConvDbHelper(mContext).getConvByTypeAndId(type, targetId, uid);
    }

    public int updateConv(ConvEntity convEntity) {
        return new ConvDbHelper(mContext).update(convEntity);
    }

    public int updateConvField(String convNo, ContentValues cv) {
        return new ConvDbHelper(mContext).updateField(convNo, cv);
    }

    public int clearReadCount(String convNo) {
        return new ConvDbHelper(mContext).updateUnRead(convNo, 0);
    }

    public int deleteConv(String convNo) {
        return new ConvDbHelper(mContext).deleteByConvNo(convNo);
    }

    public int deleteConv(int id) {
        return new ConvDbHelper(mContext).deleteById(id);
    }

    public long createConv(ConvEntity convEntity) {
        return new ConvDbHelper(mContext).save(convEntity);
    }
}
