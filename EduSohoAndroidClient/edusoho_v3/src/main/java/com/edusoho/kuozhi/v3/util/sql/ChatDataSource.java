package com.edusoho.kuozhi.v3.util.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.edusoho.kuozhi.v3.model.bal.push.Chat;

import java.util.ArrayList;

/**
 * Created by JesseHuang on 15/7/2.
 */
public class ChatDataSource {
    private SqliteChatUtil mDbHelper;
    private SQLiteDatabase mDataBase;
    private static final String TABLE_NAME = "CHAT";

    public String[] allColumns = {"ID", "FROMID", "TOID", "NICKNAME", "HEADIMGURL", "CONTENT", "TYPE", "CREATEDTIME"};

    public ChatDataSource(SqliteChatUtil sqliteChatUtil) {
        mDbHelper = sqliteChatUtil;
    }

    public ChatDataSource openWrite() throws SQLException {
        mDataBase = mDbHelper.getWritableDatabase();
        return this;
    }

    public ChatDataSource openRead() throws SQLException {
        mDataBase = mDbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public ArrayList<Chat> getChats(int start, int limit, String sql) {
        ArrayList<Chat> list = null;
        try {
            list = new ArrayList<>();
            if (TextUtils.isEmpty(sql)) {
                sql = null;
            }
            Cursor cursor = mDataBase.query(TABLE_NAME, allColumns, sql, null, null, null, "ID DESC",
                    String.format("%d, %d", start, limit));
            while (cursor.moveToNext()) {
                list.add(cursorToComment(cursor));
            }
            cursor.close();
        } catch (Exception ex) {
            Log.d("-->", ex.getMessage());
        }
        return list;
    }

    public long create(Chat chat) {
        ContentValues cv = new ContentValues();
        cv.put(allColumns[0], chat.id);
        cv.put(allColumns[1], chat.fromId);
        cv.put(allColumns[2], chat.toId);
        cv.put(allColumns[3], chat.nickName);
        cv.put(allColumns[4], chat.headimgurl);
        cv.put(allColumns[5], chat.content);
        cv.put(allColumns[6], chat.type);
        cv.put(allColumns[7], chat.createdTime);
        long insertId = mDataBase.insert(TABLE_NAME, null, cv);
        return insertId;
    }

    public Chat cursorToComment(Cursor cursor) {
        Chat chat = new Chat();
        chat.id = cursor.getInt(0);
        chat.fromId = cursor.getInt(1);
        chat.toId = cursor.getInt(2);
        chat.nickName = cursor.getString(3);
        chat.headimgurl = cursor.getString(4);
        chat.content = cursor.getString(5);
        chat.type = cursor.getString(6);
        chat.createdTime = cursor.getInt(7);
        return chat;
    }

    public long delete(int fromId, int toId) {
        return mDataBase.delete(TABLE_NAME, "(FROMID = ? AND TOID = ?) OR (TOID = ? AND FROMID = ?)",
                new String[]{fromId + "", toId + "", fromId + "", toId + ""});
    }
}
