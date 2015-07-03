package com.edusoho.kuozhi.v3.util.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.edusoho.kuozhi.v3.model.bal.push.New;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 15/7/2.
 */
public class NewDataSource {
    private SqliteChatUtil mDbHelper;
    private SQLiteDatabase mDataBase;
    private static final String TABLE_NAME = "NEW";
    private String[] allColumns = {"ID", "TOID", "TITLE", "CONTENT", "CREATEDTIME", "IMGURL", "UNREAD", "TYPE", "DETAILID", "BELONGID", "ISTOP"};

    public NewDataSource(SqliteChatUtil sqliteChatUtil) {
        mDbHelper = sqliteChatUtil;
    }

    public void open() throws SQLException {
        mDataBase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public List<New> getNews(String belongId) {
        List<New> news = new ArrayList<>();
        String sql;
        if (!TextUtils.isEmpty(belongId)) {
            sql = "BELONGID = " + belongId;
        } else {
            return null;
        }
        Cursor cursor = mDataBase.query(TABLE_NAME, allColumns, sql, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            news.add(cursorToNew(cursor));
        }
        return news;
    }

    public long createNew(New newModel) {
        ContentValues cv = new ContentValues();
//        cv.put(allColumns[0], newModel.id);
        cv.put(allColumns[1], newModel.toId);
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

    public New cursorToNew(Cursor cursor) {
        New newModel = new New();
        newModel.id = cursor.getInt(0);
        newModel.toId = cursor.getInt(1);
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
}
