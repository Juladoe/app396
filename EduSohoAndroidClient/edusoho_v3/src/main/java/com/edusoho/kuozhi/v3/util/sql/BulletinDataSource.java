package com.edusoho.kuozhi.v3.util.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.edusoho.kuozhi.v3.model.bal.push.Bulletin;

import java.util.ArrayList;

/**
 * Created by JesseHuang on 15/7/7.
 */
public class BulletinDataSource {
    private SqliteChatUtil mDbHelper;
    private SQLiteDatabase mDataBase;
    private static final String TABLE_NAME = "BULLETIN";

    public String[] allColumns = {"ID", "CONTENT", "CREATEDTIME"};

    public BulletinDataSource(SqliteChatUtil sqliteChatUtil) {
        mDbHelper = sqliteChatUtil;
    }

    public BulletinDataSource openWrite() throws SQLException {
        mDataBase = mDbHelper.getWritableDatabase();
        return this;
    }

    public BulletinDataSource openRead() throws SQLException {
        mDataBase = mDbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public ArrayList<Bulletin> getBulletins(int start, int limit, String sql) {
        ArrayList<Bulletin> list = null;
        try {
            list = new ArrayList<>();
            if (TextUtils.isEmpty(sql)) {
                sql = null;
            }
            Cursor cursor = mDataBase.query(TABLE_NAME, allColumns, sql, null, null, null, "ID DESC",
                    String.format("%d, %d", start, limit));
            while (cursor.moveToNext()) {
                list.add(cursorToBulletin(cursor));
            }
            cursor.close();
        } catch (Exception ex) {
            Log.d("-->", ex.getMessage());
        }
        return list;
    }

    public long create(Bulletin bulletin) {
        ContentValues cv = new ContentValues();
        cv.put(allColumns[0], bulletin.id);
        cv.put(allColumns[1], bulletin.content);
        cv.put(allColumns[2], bulletin.createdTime);
        long insertId = mDataBase.insert(TABLE_NAME, null, cv);
        return insertId;
    }

    private Bulletin cursorToBulletin(Cursor cursor) {
        Bulletin bulletin = new Bulletin();
        bulletin.id = cursor.getInt(0);
        bulletin.content = cursor.getString(1);
        bulletin.createdTime = cursor.getInt(0);
        return bulletin;

    }

}
