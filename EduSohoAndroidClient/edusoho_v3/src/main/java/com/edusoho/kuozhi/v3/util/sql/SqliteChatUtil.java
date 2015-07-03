package com.edusoho.kuozhi.v3.util.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.edusoho.kuozhi.v3.EdusohoApp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 15/7/1.
 */
public class SqliteChatUtil extends SQLiteOpenHelper {

    private static String mCurDbName;
    private Context mContext;
    private static SqliteChatUtil instance;
    private static final int oldVersion = 1;
    private static final int newVersion = 2;

    public SqliteChatUtil(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, null, oldVersion);
        mContext = context;
        mCurDbName = name;
    }

    public static SqliteChatUtil getSqliteChatUtil(Context context, String dbName) {
        if (TextUtils.isEmpty(mCurDbName)) {
            mCurDbName = EdusohoApp.app.domain;
        }
        if (!mCurDbName.equals(dbName)) {
            instance = new SqliteChatUtil(context, dbName, null);
            return instance;
        }
        if (instance == null) {
            instance = new SqliteChatUtil(context, dbName, null);
        }
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> sqlList = getInitSql("db_init_chat.sql");
        for (String sql : sqlList) {
            db.execSQL(sql);
        }
        //initTypeTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private ArrayList<String> getInitSql(String name) {
        ArrayList<String> sqlList = new ArrayList<>();
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
            Log.d("sqlchat", e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                Log.d("sqlchat", e.getMessage());
            }
        }

        return sqlList;
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

    public void close() {
        getReadableDatabase().close();
        getWritableDatabase().close();
    }
}
