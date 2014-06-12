package com.edusoho.kowzhi.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteUtil extends SQLiteOpenHelper{

	public SqliteUtil(Context context, String name, CursorFactory factory) {
		super(context, Const.DB_NAME, factory, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Const.INIT_SQL);
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
