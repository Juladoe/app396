package com.edusoho.kuozhi.v3.util.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.edusoho.kuozhi.v3.model.bal.push.New;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 15/7/2.
 */
public class NewDataSource {
    private static final String TABLE_NAME = "NEW";
    private SqliteChatUtil mDbHelper;
    private SQLiteDatabase mDataBase;
    private String[] allColumns = {"ID", "FROMID", "TITLE", "CONTENT", "CREATEDTIME", "IMGURL", "UNREAD", "TYPE", "BELONGID", "ISTOP"};

    public NewDataSource(SqliteChatUtil sqliteChatUtil) {
        mDbHelper = sqliteChatUtil;
    }

    public NewDataSource openWrite() throws SQLException {
        mDataBase = mDbHelper.getWritableDatabase();
        return this;
    }

    public NewDataSource openRead() throws SQLException {
        mDataBase = mDbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        if (mDataBase.isOpen()) {
            mDataBase.close();
        }
        mDbHelper.close();
    }

    /**
     * 查询new表数据
     *
     * @param whereSql  "WHERE FROMID = ?"
     * @param whereArgs
     * @return
     */
    public List<New> getNews(String whereSql, String... whereArgs) {
        List<New> news = new ArrayList<>();
        Cursor cursor = mDataBase.rawQuery("SELECT * FROM NEW " + whereSql, whereArgs);
        while (cursor.moveToNext()) {
            news.add(convertCursor2New(cursor));
        }
        cursor.close();
        return news;
    }

    public long create(New newModel) {
//        List<New> newList = getNews("WHERE FROMID = ?", new String[]{newModel.fromId + ""});
//        if (newList == null || newList.size() == 0) {
//
//        }
        ContentValues cv = new ContentValues();
//        cv.put(allColumns[0], newModel.id);
        cv.put(allColumns[1], newModel.fromId);
        cv.put(allColumns[2], newModel.title);
        cv.put(allColumns[3], newModel.content);
        cv.put(allColumns[4], newModel.createdTime);
        cv.put(allColumns[5], newModel.imgUrl);
        cv.put(allColumns[6], newModel.unread);
        cv.put(allColumns[7], newModel.type);
        cv.put(allColumns[8], newModel.belongId);
        cv.put(allColumns[9], newModel.isTop);
        long insertId = mDataBase.insert(TABLE_NAME, null, cv);
        return insertId;
    }

    public long update(New newModel) {
        ContentValues cv = new ContentValues();
        cv.put(allColumns[1], newModel.fromId);
        cv.put(allColumns[2], newModel.title);
        cv.put(allColumns[3], newModel.content);
        cv.put(allColumns[4], newModel.createdTime);
        cv.put(allColumns[5], newModel.imgUrl);
        cv.put(allColumns[6], newModel.unread);
        cv.put(allColumns[7], newModel.type);
        cv.put(allColumns[8], newModel.belongId);
        cv.put(allColumns[9], newModel.isTop);
        return mDataBase.update(TABLE_NAME, cv, "FROMID = ? ", new String[]{newModel.getFromId() + ""});
    }

    public long delete(String whereSql, String... whereArgs) {
        return mDataBase.delete(TABLE_NAME, whereSql, whereArgs);
    }


    public New convertCursor2New(Cursor cursor) {
        New newModel = new New();
        newModel.id = cursor.getInt(0);
        newModel.fromId = cursor.getInt(1);
        newModel.title = cursor.getString(2);
        newModel.content = cursor.getString(3);
        newModel.createdTime = cursor.getInt(4);
        newModel.imgUrl = cursor.getString(5);
        newModel.unread = cursor.getInt(6);
        newModel.type = cursor.getString(7);
        newModel.belongId = cursor.getInt(8);
        newModel.isTop = cursor.getInt(9);
        return newModel;
    }

    public long delete(int id) {
        return mDataBase.delete(TABLE_NAME, "ID = ?", new String[]{id + ""});
    }
}
