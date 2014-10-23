package com.edusoho.kuozhi.util;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by MyPC on 14-10-22.
 */
public class ListViewCache {
    private static SparseArray<View> mCacheList = new SparseArray<View>();

    public static void addCache(int key, View view) {
        if (mCacheList.get(key) == null) {
            Log.d("pos", key + "");
            mCacheList.put(key, view);
        }
    }

//    public static void addCache(int key, View view) {
//        for (int i = 0; i < mCacheList.size(); i++) {
//            if (mCacheList.valueAt(i) == view) {
//                return;
//            }
//        }
//        mCacheList.put(key, view);
//    }

    public static View getOneCacheView(int key) {
        if (mCacheList.get(key) != null) {
            return mCacheList.get(key);
        }
        return null;
    }

}
