package com.edusoho.kuozhi.imserver.util;

import android.content.ContentValues;
import android.content.Context;
import android.util.ArrayMap;

import com.edusoho.kuozhi.imserver.entity.MessageEntity;

/**
 * Created by 菊 on 2016/4/29.
 */
public class MsgDbHelper {

    private static final int dbVersion = 1;
    private static final String DB_NAME = "im_db";
    private static final String INIT_SQL = "create table if not exists im_message(\n" +
            "    id integer PRIMARY KEY AUTOINCREMENT,\n" +
            "    convNo varchar(64),\n" +
            "    fromId varchar(100),\n" +
            "    fromName varchar(100),\n" +
            "    toId  varchar(100),\n" +
            "    toName varchar(100),\n" +
            "    msg  text,\n" +
            "    msgNo  varchar(64),\n" +
            "    time  integer\n" +
            ");";

    private DbHelper mDbHelper;

    public MsgDbHelper(Context context) {
        mDbHelper = new DbHelper(context, DB_NAME, dbVersion, INIT_SQL);
    }

    public void getListByNo(String msgNo) {
        mDbHelper.query("im_message", "msgNo=?", new String[] { msgNo });
    }

    public String getLaterNo() {
        ArrayMap arrayMap = mDbHelper.querySingleBySort("im_message", null, null, "time desc");
        return arrayMap == null ? "" : arrayMap.get("msgNo").toString();
    }

    public boolean hasMessageByNo(String msgNo) {
        return mDbHelper.querySingle("im_message", "msgNo=?", new String[] { msgNo }) != null;
    }

    public void save(MessageEntity messageEntity) {
        ContentValues cv = new ContentValues();
        cv.put("convNo", messageEntity.getConvNo());
        cv.put("fromId", messageEntity.getFromId());
        cv.put("fromName", messageEntity.getFromName());
        cv.put("toId", messageEntity.getToId());
        cv.put("toName", messageEntity.getToName());
        cv.put("msg", messageEntity.getMsg());
        cv.put("msgNo", messageEntity.getMsgNo());
        cv.put("time", messageEntity.getTime());
        mDbHelper.insert("im_message", cv);
    }
}
