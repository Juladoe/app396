package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by howzhi on 14-10-20.
 */
public abstract class ListBaseAdapter<T> extends BaseAdapter {

    protected LayoutInflater inflater;
    protected int mResource;
    protected Context mContext;
    protected ArrayList<T> mList;

    protected SparseArray<Boolean> animArray;
    protected SparseArray<View> cacheArray;
    protected Queue<View> animQueue;
    private int animCount;
    private boolean mIsCache;

    public ListBaseAdapter(Context context, int resource)
    {
        mResource = resource;
        mContext = context;
        mList = new ArrayList<T>();
        inflater = LayoutInflater.from(mContext);

        animArray = new SparseArray<Boolean>();
        animQueue = new LinkedList<View>();
    }

    public ListBaseAdapter(Context context, int resouce, boolean isCache)
    {
        this(context, resouce);
        this.mIsCache = isCache;
        if (isCache) {
            cacheArray = new SparseArray<View>();
        }
    }

    protected View getCacheView(int index)
    {
        if (!mIsCache) {
            return null;
        }
        return cacheArray.get(index);
    }

    protected void setCacheView(int index, View view)
    {
        if (mIsCache) {
            cacheArray.put(index, view);
        }
    }

    /**
     * 同步动画
     */
    protected void startAnim()
    {
        Log.d(null, "animCount->" + animCount);
        synchronized (mContext) {
            if (animCount > 0) {
                return;
            }
        }
        if (animQueue.isEmpty()) {
            return;
        }
        View view = animQueue.poll();
        if (view == null) {
            return;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.list_item_l_to_r);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animCount ++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animCount --;
                startAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(animation);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    public void clear()
    {
        mList.clear();
        if (mIsCache) {
            cacheArray.clear();
        }
    }

    public void addItem(T item){}

    public abstract void addItems(ArrayList<T> list);
}
