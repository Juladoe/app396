package com.edusoho.kuozhi.v3.util.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.edusoho.kuozhi.v3.model.bal.push.ClassroomDiscussEntity;

import java.util.ArrayList;

/**
 * Created by JesseHuang on 15/10/15.
 */
public class ClassroomDiscussDataSource {
    private static final String TABLE_NAME = "CLASSROOM_DISCUSS";
    private SqliteChatUtil mDbHelper;
    private SQLiteDatabase mDataBase;
    private String[] allColumns = {"ID", "CLASSROOMID", "FROMID", "NICKNAME", "HEADIMGURL", "CONTENT", "BELONGID", "TYPE", "DELIVERY", "CREATEDTIME"};

    public ClassroomDiscussDataSource(SqliteChatUtil sqliteChatUtil) {
        mDbHelper = sqliteChatUtil;
    }

    public ClassroomDiscussDataSource openWrite() throws SQLException {
        mDataBase = mDbHelper.getWritableDatabase();
        return this;
    }

    public ClassroomDiscussDataSource openRead() throws SQLException {
        mDataBase = mDbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        if (mDataBase.isOpen()) {
            mDataBase.close();
        }
        mDbHelper.close();
    }

    public ArrayList<ClassroomDiscussEntity> getLists(int classroomId, int belongId, int start, int limit) {
        openRead();
        ArrayList<ClassroomDiscussEntity> news = new ArrayList<>();
        Cursor cursor = mDataBase.rawQuery("SELECT * FROM ? WHERE CLASSROOMID = ? AND BELONGID = ? ORDER BY ID DESC LIMIT ?, ? ",
                new String[]{classroomId + "", belongId + "", start + "", limit + ""});
        while (cursor.moveToNext()) {
            news.add(convertCursor2ClassroomDiscussEntity(cursor));
        }
        cursor.close();
        close();
        return news;
    }

    public long create(ClassroomDiscussEntity model) {
        this.openWrite();
        ContentValues cv = new ContentValues();
        cv.put(allColumns[1], model.getId());
        cv.put(allColumns[2], model.getClassroomId());
        cv.put(allColumns[3], model.getFromId());
        cv.put(allColumns[4], model.getNickname());
        cv.put(allColumns[5], model.getHeadImgUrl());
        cv.put(allColumns[6], model.getContent());
        cv.put(allColumns[7], model.getBelongId());
        cv.put(allColumns[8], model.getType());
        cv.put(allColumns[9], model.getDelivery());
        cv.put(allColumns[10], model.getCreatedTime());
        long effectRow = mDataBase.insert(TABLE_NAME, null, cv);
        this.close();
        return effectRow;
    }

    public void delete(int classroomId, int belongId) {
        this.openWrite();
        mDataBase.delete(TABLE_NAME, "CLASSROOMID = ? AND BELONGID = ?",
                new String[]{classroomId + "", belongId + ""});
        this.close();
    }

    public ClassroomDiscussEntity convertCursor2ClassroomDiscussEntity(Cursor cursor) {
        ClassroomDiscussEntity model = new ClassroomDiscussEntity();
        model.setId(cursor.getInt(0));
        model.setClassroomId(cursor.getInt(1));
        model.setFromId(cursor.getInt(2));
        model.setNickname(cursor.getString(3));
        model.setHeadImgUrl(cursor.getString(4));
        model.setContent(cursor.getString(5));
        model.setBelongId(cursor.getInt(6));
        model.setType(cursor.getString(7));
        model.setDelivery(cursor.getInt(8));
        model.setCreatedTime(cursor.getInt(9));
        return model;
    }
}
