package com.edusoho.kuozhi.imserver.ui.helper;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by suju on 16/8/30.
 */
public class ViewCacheHelper {

    private SparseArray<WeakReference<View>> mViewArray;

    public ViewCacheHelper() {
        mViewArray = new SparseArray<>();
    }

    public void putView(int id, View view) {
        mViewArray.put(id, new WeakReference<View>(view));
    }

    public void remove(int id) {
        mViewArray.remove(id);
    }

    public View getView(int id) {
        if (mViewArray.indexOfKey(id) > 0) {
            View view = mViewArray.get(id).get();
            Log.d("getView:", "size:" + mViewArray.size() + " " + view);
            return view;
        }

        return null;
    }

    public void clear() {
        mViewArray.clear();
    }
}
