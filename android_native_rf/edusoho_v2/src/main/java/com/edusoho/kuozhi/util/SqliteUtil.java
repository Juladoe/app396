package com.edusoho.kuozhi.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.FileHandler;

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
	
	public void insert(String table, ContentValues cv)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.insert(table, null, cv);
		db.close();
	}
	
	public static class QueryCallBack
	{
		public void query(Cursor cursor){};
	}
}
