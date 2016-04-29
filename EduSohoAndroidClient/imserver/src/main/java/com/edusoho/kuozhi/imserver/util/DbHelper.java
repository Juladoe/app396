package com.edusoho.kuozhi.imserver.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.ArrayMap;

import java.util.ArrayList;

/**
 * Created by 菊 on 2016/4/29.
 */
public class DbHelper extends SQLiteOpenHelper {

    private String mInitSql;

    public DbHelper(Context context, String name, int dbVersion, String initSql) {
        super(context, name, null, dbVersion);
        this.mInitSql = initSql;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(mInitSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<ArrayMap> query(String table, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(table, null, selection, selectionArgs, null, null, null);
        ArrayList<ArrayMap> resultList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int columnCount = cursor.getColumnCount();
            ArrayMap arrayMap = new ArrayMap();
            for (int i = 0; i < columnCount; i++) {
                arrayMap.put(cursor.getColumnName(i), cursor.getString(i));
            }
            resultList.add(arrayMap);
        }

        cursor.close();
        return resultList;
    }

    public ArrayMap querySingle(String table, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(table, null, selection, selectionArgs, null, null, null);

        ArrayMap result = null;
        if (cursor.moveToNext()) {
            result = new ArrayMap();
            int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                result.put(cursor.getColumnName(i), cursor.getString(i));
            }
        }

        cursor.close();
        return result;
    }

    public long insert(String table, ContentValues cv) {
        SQLiteDatabase db = getReadableDatabase();
        return db.insert(table, null, cv);
    }
}
