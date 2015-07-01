package com.edusoho.kuozhi.v3.util.sql;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.util.SparseArray;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.sys.Cache;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class SqliteUtil extends SQLiteOpenHelper {

    private Context mContext;
    private static final int dbVersion = 10;
    private static final int oldVersion = 9;

    private static SqliteUtil instance;

    private static String[] INIT_SQLS = {"db_init_m3u8.sql", "db_init_lesson_resource.sql", "db_init_chat.sql"};

    public SqliteUtil(Context context, String name, CursorFactory factory) {
        super(context, Const.DB_NAME, null, dbVersion);
        Log.d("SqliteUtil", "dbVersion " + dbVersion);
        mContext = context;

        //更新
        onUpgrade(getWritableDatabase(), oldVersion, dbVersion);
    }

    public static SqliteUtil getUtil(Context context) {
        if (instance == null) {
            instance = new SqliteUtil(context, null, null);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(null, "create cache db->");
        ArrayList<String> sqlList = getInitSql("db_init.sql");
        for (String sql : sqlList) {
            db.execSQL(sql);
        }
    }

    private ArrayList<String> getInitSql(String name) {
        ArrayList<String> sqlList = new ArrayList<String>();
        InputStream inputStream = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = mContext.getAssets().open(name);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                if (line.endsWith(";")) {
                    sqlList.add(stringBuilder.toString());
                    stringBuilder.delete(0, stringBuilder.length());
                }
            }
        } catch (Exception e) {
            //nothing
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                //nothing
            }
        }

        return sqlList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(null, String.format("create db_init_m3u8 db newVersion %d ov %d", newVersion, oldVersion));
        if (oldVersion < newVersion) {
            SharedPreferences sp = mContext.getSharedPreferences("db_preference", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            for (String initSql : INIT_SQLS) {
                if (!sp.contains(initSql)) {
                    initDbSql(initSql, db);
                    if ("db_init_chat.sql".equals(initSql)) {
                        initTypeTable(db);
                    }
                    editor.putBoolean(initSql, true);
                }
            }
            editor.commit();
        }
    }

    private void initDbSql(String name, SQLiteDatabase db) {
        Log.d(null, "initDbSql->" + name);
        ArrayList<String> sqlList = getInitSql(name);
        for (String sql : sqlList) {
            db.execSQL(sql);
        }
    }

    private void initTypeTable(SQLiteDatabase db) {
        try {
            String sql = "INSERT INTO TYPE VALUES(?,?)";
            SQLiteStatement sqLiteStatement = db.compileStatement(sql);
            int size = getTypeDatas().size();
            db.beginTransaction();
            for (int i = 1; i < size + 1; i++) {
                sqLiteStatement.bindLong(1, i);
                sqLiteStatement.bindString(2, getTypeDatas().get(i));
                sqLiteStatement.execute();
                sqLiteStatement.clearBindings();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Log.d("init", ex.getMessage());
        }
    }

    private SparseArray<String> getTypeDatas() {
        SparseArray<String> typeDatas = new SparseArray<>(6);
        typeDatas.put(1, "friend");
        typeDatas.put(2, "teacher");
        typeDatas.put(3, "course");
        typeDatas.put(4, "text");
        typeDatas.put(5, "sound");
        typeDatas.put(6, "image");
        return typeDatas;
    }

    public Cache query(String selection, String... selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selection, selectionArgs);

        Cache cache = null;
        if (cursor.moveToNext()) {
            String key = cursor.getString(cursor.getColumnIndex("key"));
            String value = cursor.getString(cursor.getColumnIndex("value"));
            cache = new Cache(key, value);
        }
        cursor.close();
        return cache;
    }

    public void query(QueryCallBack callback, String selection, String... selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selection, selectionArgs);
        while (cursor.moveToNext()) {
            callback.query(cursor);
        }
        cursor.close();
    }

    public <T> T query(Class<T> type, String attrName, String selection, String... selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selection, selectionArgs);
        if (cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex(attrName);
            if (type == String.class) {
                return (T) cursor.getString(columnIndex);
            } else if (type == Integer.class) {
                return (T) new Integer(cursor.getInt(columnIndex));
            } else if (type == Float.class) {
                return (T) new Float(cursor.getFloat(columnIndex));
            } else if (type == Double.class) {
                return (T) new Double(cursor.getDouble(columnIndex));
            }
        }

        return null;
    }

    public <T> T query(QueryPaser<T> queryPaser, String selection, String... selectionArgs) {
        T obj = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selection, selectionArgs);
        while (cursor.moveToNext()) {
            obj = queryPaser.parse(cursor);
            if (queryPaser.isSignle()) {
                break;
            }
        }
        cursor.close();

        return obj;
    }

    public void execSQL(String sql) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }

    public int delete(String table, String where, String[] args) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(table, where, args);
        return result;
    }

    public int update(String table, ContentValues cv, String where, String[] args) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.update(table, cv, where, args);
        Log.d(null, "upate sqlite ->" + result);
        return result;
    }

    public long insert(String table, ContentValues cv) {
        SQLiteDatabase db = getWritableDatabase();
        long lastId = db.insert(table, null, cv);
        return lastId;
    }

    public static class QueryCallBack {
        public void query(Cursor cursor) {
        }
    }

    public static class QueryPaser<T> {
        public T parse(Cursor cursor) {
            return null;
        }

        public boolean isSignle() {
            return false;
        }
    }

    public void close() {
        getReadableDatabase().close();
        getWritableDatabase().close();
    }

    public void saveLocalCache(String type, String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("key", key);
        cv.put("value", value);
        long result = insert("data_cache", cv);
        Log.d(null, "insert to cache->" + result);
    }

    public <T> T queryForObj(
            TypeToken<T> typeToken, String selection, String... selectionArgs) {
        T obj = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from data_cache " + selection, selectionArgs);
        if (cursor.moveToNext()) {
            String value = cursor.getString(cursor.getColumnIndex("value"));
            try {
                obj = EdusohoApp.app.gson.fromJson(
                        value, typeToken.getType());
            } catch (Exception e) {
                e.printStackTrace();
                return obj;
            }

        }
        cursor.close();

        return obj;
    }

    public static void saveUser(User user) {
        //保存用户
        EdusohoApp app = EdusohoApp.app;
        ContentValues cv = new ContentValues();
        cv.put("key", "data-" + user.id);
        cv.put("value", app.gson.toJson(user));
        cv.put("type", Const.CACHE_USER_TYPE);
        SqliteUtil.getUtil(app).insert("data_cache", cv);
    }

    public static void clearUser(int userId) {
        //保存用户
        EdusohoApp app = EdusohoApp.app;
        SqliteUtil.getUtil(app).delete(
                "data_cache",
                "key=?",
                new String[]{"data-" + userId}
        );
    }
}
