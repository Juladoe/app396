package com.edusoho.kuozhi.imserver.factory;

import android.content.Context;
import android.text.TextUtils;

import com.edusoho.kuozhi.imserver.helper.IDbManager;
import com.edusoho.kuozhi.imserver.helper.IMDbManager;

/**
 * Created by suju on 16/8/17.
 */
public class DbManagerFactory {

    private String dbName;
    private static DbManagerFactory defaultDbManagerFactory = new DbManagerFactory();

    public static DbManagerFactory getDefaultFactory() {
        return defaultDbManagerFactory;
    }

    public void setDbName(String dbName) {
        this.dbName = String.format("im_db_%s", dbName);
    }

    public IDbManager createIMDbManager(Context context) {
        if (TextUtils.isEmpty(dbName)) {
            throw new RuntimeException("dbName no empty");
        }
        return new IMDbManager(context, dbName);
    }
}
