package com.edusoho.kuozhi.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.edusoho.kuozhi.core.model.Cache;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SqliteUtil extends SQLiteOpenHelper{

    private Context mContext;
	public SqliteUtil(Context context, String name, CursorFactory factory) {
		super(context, Const.DB_NAME, factory, 1);
        mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
        Log.d(null, "create cache db->");
		db.execSQL(getInitSql());
	}

    private String getInitSql()
    {
        InputStream inputStream = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try{
            inputStream = mContext.getAssets().open("db_init.sql");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            //nothing
        } finally {
            try {
                reader.close();
                inputStream.close();
            } catch (Exception e){
                //nothing
            }
        }

        return stringBuilder.toString();
    }
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

    public Cache query(String selection, String... selectionArgs)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selection, selectionArgs);

        Cache cache = null;
        if (cursor.moveToNext()) {
            String key = cursor.getString(cursor.getColumnIndex("key"));
            String value = cursor.getString(cursor.getColumnIndex("value"));
            cache = new Cache(key, value);
        }
        cursor.close();
        db.close();

        return cache;
    }

	public void query(QueryCallBack callback, String selection, String... selectionArgs)
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(selection, selectionArgs);
		while (cursor.moveToNext()) {
			callback.query(cursor);
		}
		cursor.close();
		db.close();
	}
	
	public void execSQL(String sql)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql);
		db.close();
	}

    public int update(String table, ContentValues cv, String where, String[] args)
    {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.update(table, cv, where, args);
        Log.d(null, "upate cache->" + result);
        db.close();
        return result;
    }

	public long insert(String table, ContentValues cv)
	{
		SQLiteDatabase db = getWritableDatabase();
		long lastId = db.insert(table, null, cv);
		db.close();
        return lastId;
	}
	
	public static class QueryCallBack
	{
		public void query(Cursor cursor){};
	}
}
