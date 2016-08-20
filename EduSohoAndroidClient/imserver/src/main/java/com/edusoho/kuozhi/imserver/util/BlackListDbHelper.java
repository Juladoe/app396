package com.edusoho.kuozhi.imserver.util;

import android.content.ContentValues;
import android.content.Context;

import com.edusoho.kuozhi.imserver.factory.DbManagerFactory;

import java.util.HashMap;

/**
 * Created by suju on 16/8/20.
 */
public class BlackListDbHelper {

    private static final String TABLE = "im_blacklist";
    private DbHelper mDbHelper;

    public BlackListDbHelper(Context context) {
        mDbHelper = new DbHelper(context, DbManagerFactory.getDefaultFactory().createIMDbManager(context));
    }

    public int getStatusByConvNo(String convNo) {
        HashMap<String, String> arrayMap = mDbHelper.querySingle(TABLE, "convNo=?", new String[]{convNo});
        return MessageUtil.parseInt(arrayMap.get("status"));
    }

    public long createBlackList(String convNo, int status) {
        ContentValues cv = new ContentValues();
        cv.put("convNo", convNo);
        cv.put("status", status);
        return mDbHelper.insert(TABLE, cv);
    }

    public long updateByConvNo(String convNo, int status) {
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        return mDbHelper.update(TABLE, cv, "convNo=?", new String[]{convNo});
    }

    public long deleteByConvNo(String convNo) {
        return mDbHelper.delete(TABLE, "convNo=?", new String[]{convNo});
    }
}
