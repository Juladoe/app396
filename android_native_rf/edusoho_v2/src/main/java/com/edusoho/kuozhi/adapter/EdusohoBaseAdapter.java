package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.androidquery.util.AQUtility;

import java.io.File;

/**
 * Created by howzhi on 14-9-12.
 */
public abstract class EdusohoBaseAdapter extends BaseAdapter {

    public static final int UPDATE = 0001;
    public static final int NORMAL = 0002;

    protected int mMode;

    public EdusohoBaseAdapter(){}

    protected void setMode(int mode)
    {
        this.mMode = mode;
    }

    protected boolean urlCacheExistsed(Context context, String url)
    {
        File cacheDir = AQUtility.getCacheDir(context);
        File cacheFile = AQUtility.getExistedCacheByUrl(cacheDir, url);
        return cacheFile != null;
    }
}
