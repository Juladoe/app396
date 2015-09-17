package com.edusoho.kuozhi.v3.util.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.edusoho.kuozhi.v3.model.bal.push.NewsCourseEntity;

import java.util.ArrayList;

/**
 * Created by JesseHuang on 15/9/16.
 */
public class NewsCourseDataSource {
    public static final String TABLE_NAME = "NEWS_COURSE";
    public String[] allColumns = {"ID", "COURSEID", "LESSONID", "TITLE", "CONTENT", "FROMTYPE", "BODYTYPE", "USERID", "CREATEDTIME"};
    private SqliteChatUtil mDbHelper;
    private SQLiteDatabase mDataBase;

    public NewsCourseDataSource(SqliteChatUtil sqliteChatUtil) {
        mDbHelper = sqliteChatUtil;
    }

    public NewsCourseDataSource openWrite() throws SQLException {
        mDataBase = mDbHelper.getWritableDatabase();
        return this;
    }

    public NewsCourseDataSource openRead() throws SQLException {
        mDataBase = mDbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        if (mDataBase.isOpen()) {
            mDataBase.close();
        }
        mDbHelper.close();
    }

    public ArrayList<NewsCourseEntity> getNewsCourse(int start, int limit, int courseId, int userId) {
        this.openRead();
        ArrayList<NewsCourseEntity> list = null;
        try {
            list = new ArrayList<>();
            String sql = String.format("COURSEID = %d AND USERID = %d", courseId, userId);
            Cursor cursor = mDataBase.query(TABLE_NAME, allColumns, sql, null, null, null, "CREATEDTIME DESC",
                    String.format("%d, %d", start, limit));
            while (cursor.moveToNext()) {
                list.add(cursorToEntity(cursor));
            }
            cursor.close();
        } catch (Exception ex) {
            Log.d("-->", ex.getMessage());
        }
        this.close();
        return list;
    }

    public NewsCourseEntity cursorToEntity(Cursor cursor) {
        NewsCourseEntity newsCourseEntity = new NewsCourseEntity();
        newsCourseEntity.setId(cursor.getInt(0));
        newsCourseEntity.setCourseId(cursor.getInt(1));
        newsCourseEntity.setLessonId(cursor.getInt(2));
        newsCourseEntity.setTitle(cursor.getString(3));
        newsCourseEntity.setContent(cursor.getString(4));
        newsCourseEntity.setFromType(cursor.getString(5));
        newsCourseEntity.setBodyType(cursor.getString(6));
        newsCourseEntity.setUserId(cursor.getInt(7));
        newsCourseEntity.setCreatedTime(cursor.getInt(7));
        return newsCourseEntity;
    }

    public long create(NewsCourseEntity newsCourseEntity) {
        this.openWrite();
        ContentValues cv = new ContentValues();
        cv.put(allColumns[0], newsCourseEntity.getId());
        cv.put(allColumns[1], newsCourseEntity.getCourseId());
        cv.put(allColumns[2], newsCourseEntity.getLessonId());
        cv.put(allColumns[3], newsCourseEntity.getTitle());
        cv.put(allColumns[4], newsCourseEntity.getContent());
        cv.put(allColumns[5], newsCourseEntity.getFromType());
        cv.put(allColumns[6], newsCourseEntity.getBodyType());
        cv.put(allColumns[7], newsCourseEntity.getUserId());
        cv.put(allColumns[8], newsCourseEntity.getCreatedTime());
        long effectRow = mDataBase.insert(TABLE_NAME, null, cv);
        this.close();
        return effectRow;
    }

    public int update(NewsCourseEntity newsCourseEntity) {
        this.openWrite();
        ContentValues cv = new ContentValues();
        cv.put(allColumns[1], newsCourseEntity.getCourseId());
        cv.put(allColumns[2], newsCourseEntity.getLessonId());
        cv.put(allColumns[3], newsCourseEntity.getTitle());
        cv.put(allColumns[4], newsCourseEntity.getContent());
        cv.put(allColumns[5], newsCourseEntity.getFromType());
        cv.put(allColumns[7], newsCourseEntity.getUserId());
        cv.put(allColumns[8], newsCourseEntity.getCreatedTime());
        int effectRow = mDataBase.update(TABLE_NAME, cv, "ID = ?", new String[]{newsCourseEntity.getId() + ""});
        this.close();
        return effectRow;
    }

    public long delete(int courseId, int userId) {
        this.openWrite();
        long effectRow = mDataBase.delete(TABLE_NAME, "COURSEID = ? AND USERID = ?",
                new String[]{courseId + "", userId + ""});
        this.close();
        return effectRow;
    }
}
