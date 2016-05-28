package com.edusoho.kuozhi.imserver.helper;

import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 菊 on 2016/5/14.
 */
public class IMDbManager implements IDbManager {

    private static int dbVersion = 1;
    private static final String DB_NAME = "im_db";

    private static final String INIT_SQL = "db_im_%d.sql";

    private Context mContext;

    public IMDbManager(Context context)
    {
        this.mContext = context;
    }

    @Override
    public int getVersion() {
        return dbVersion;
    }

    @Override
    public String getName() {
        return DB_NAME;
    }

    @Override
    public List<String> getInitSql() {
        return getIncrementSql(0);
    }

    @Override
    public List<String> getIncrementSql(int oldVersion) {
        List<String> sqlList = new ArrayList<>();
        if (oldVersion >= dbVersion) {
            return sqlList;
        }
        int version = dbVersion;
        while (version > oldVersion) {
            sqlList.addAll(getSqlByVersion(version));
            version --;
        }
        return sqlList;
    }

    private List<String> getSqlByVersion(int version) {
        InputStream inputStream = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        List<String> sqlList = new ArrayList<>();
        try {
            inputStream = mContext.getAssets().open(String.format(INIT_SQL, version));
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
}
