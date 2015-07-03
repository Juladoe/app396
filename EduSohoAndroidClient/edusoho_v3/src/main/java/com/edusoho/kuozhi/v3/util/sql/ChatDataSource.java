package com.edusoho.kuozhi.v3.util.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.edusoho.kuozhi.v3.model.bal.push.Chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 15/7/2.
 */
public class ChatDataSource {
    private SqliteChatUtil mDbHelper;
    private SQLiteDatabase mDataBase;
    private static final String TABLE_NAME = "CHAT";

    public String[] allColumns = {"ID", "NEWID", "FROMID", "TOID", "NICKNAME", "HEADIMGURL", "CONTENT", "TYPE", "CREATEDTIME"};

    public ChatDataSource(SqliteChatUtil sqliteChatUtil) {
        mDbHelper = sqliteChatUtil;
    }

    public void open() throws SQLException {
        mDataBase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public List<Chat> getChats(int start, int limit, String sql) {
        List<Chat> list = new ArrayList<>();
        if (TextUtils.isEmpty(sql)) {
            sql = null;
        }
        Cursor cursor = mDataBase.query(TABLE_NAME, allColumns, sql, null, null, null, null, String.format("limit %d, %d", start, limit));
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursorToComment(cursor));
        }
        cursor.close();
        return list;
    }

    public long createChat(Chat chat) {
        ContentValues cv = new ContentValues();
//        cv.put(allColumns[0], chat.id);
        cv.put(allColumns[1], chat.newId);
        cv.put(allColumns[2], chat.fromId);
        cv.put(allColumns[3], chat.toId);
        cv.put(allColumns[4], chat.nickName);
        cv.put(allColumns[5], chat.headimgurl);
        cv.put(allColumns[6], chat.content);
        cv.put(allColumns[7], chat.type);
        cv.put(allColumns[8], chat.createdTime);
        long insertId = mDataBase.insert(TABLE_NAME, null, cv);
        return insertId;
    }

    public Chat cursorToComment(Cursor cursor) {
        Chat chat = new Chat();
        chat.id = cursor.getInt(0);
        chat.newId = cursor.getInt(1);
        chat.fromId = cursor.getInt(2);
        chat.toId = cursor.getInt(3);
        chat.nickName = cursor.getString(4);
        chat.headimgurl = cursor.getString(5);
        chat.content = cursor.getString(6);
        chat.type = cursor.getString(7);
        chat.createdTime = cursor.getInt(8);
        return chat;
    }
}
