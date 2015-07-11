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
    private static final String TABLE_NAME = "CHAT";
    public String[] allColumns = {"CHATID", "ID", "FROMID", "TOID", "NICKNAME", "HEADIMGURL", "CONTENT", "TYPE", "DELIVERY", "CREATEDTIME"};
    private SqliteChatUtil mDbHelper;
    private SQLiteDatabase mDataBase;

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
        if (mDataBase.isOpen()) {
            mDataBase.close();
        }
        mDbHelper.close();
    }

    public ArrayList<Chat> getChats(int start, int limit, String sql) {
        this.openRead();
        ArrayList<Chat> list = null;
        try {
            list = new ArrayList<>();
            if (TextUtils.isEmpty(sql)) {
                sql = null;
            }
            Cursor cursor = mDataBase.query(TABLE_NAME, allColumns, sql, null, null, null, "CHATID DESC",
                    String.format("%d, %d", start, limit));
            while (cursor.moveToNext()) {
                list.add(cursorToComment(cursor));
            }
            cursor.close();
        } catch (Exception ex) {
            Log.d("-->", ex.getMessage());
        }
        this.close();
        return list;
    }

    public long create(Chat chat) {
        this.openWrite();
        ContentValues cv = new ContentValues();
        cv.put(allColumns[1], chat.id);
        cv.put(allColumns[2], chat.fromId);
        cv.put(allColumns[3], chat.toId);
        cv.put(allColumns[4], chat.nickName);
        cv.put(allColumns[5], chat.headimgurl);
        cv.put(allColumns[6], chat.content);
        cv.put(allColumns[7], chat.type);
        cv.put(allColumns[8], chat.delivery);
        cv.put(allColumns[9], chat.createdTime);
        long effectRow = mDataBase.insert(TABLE_NAME, null, cv);
        this.close();
        return effectRow;
    }

    public int update(Chat chat) {
        this.openWrite();
        ContentValues cv = new ContentValues();
        cv.put(allColumns[1], chat.id);
        cv.put(allColumns[2], chat.fromId);
        cv.put(allColumns[3], chat.toId);
        cv.put(allColumns[4], chat.nickName);
        cv.put(allColumns[5], chat.headimgurl);
        cv.put(allColumns[6], chat.content);
        cv.put(allColumns[7], chat.type);
        cv.put(allColumns[8], chat.delivery);
        cv.put(allColumns[9], chat.createdTime);
        int effectRow = mDataBase.update(TABLE_NAME, cv, "CHATID = ?", new String[]{chat.chatId + ""});
        this.close();
        return effectRow;
    }

    public Chat cursorToComment(Cursor cursor) {
        Chat chat = new Chat(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
                cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getInt(8), cursor.getInt(9));
        return chat;
    }

    public long delete(int fromId, int toId) {
        this.openWrite();
        long effectRow = mDataBase.delete(TABLE_NAME, "(FROMID = ? AND TOID = ?) OR (TOID = ? AND FROMID = ?)",
                new String[]{fromId + "", toId + "", fromId + "", toId + ""});
        this.close();
        return effectRow;
    }
}
