package com.edusoho.kuozhi.clean.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by JesseHuang on 2017/5/9.
 */

public class SharedPreferencesHelper {
    private Context mContext;
    private SharedPreferences preference;
    private static SharedPreferencesHelper instance;

    private SharedPreferencesHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public static SharedPreferencesHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedPreferencesHelper.class) {
                if (instance == null) {
                    instance = new SharedPreferencesHelper(context);
                }
            }
        }
        return instance;
    }

    public SharedPreferencesHelper open(String name) {
        this.preference = mContext.getSharedPreferences(name, 0);
        return instance;
    }

    public String getString(String key) {
        return this.preference.getString(key, "");
    }

    public void putString(String key, String value) {
        this.preference.edit().putString(key, value).apply();
    }

    public void getInt(String key) {
        this.preference.getInt(key, 0);
    }

    public void putInt(String key, int value) {
        this.preference.edit().putInt(key, value).apply();
    }
}
