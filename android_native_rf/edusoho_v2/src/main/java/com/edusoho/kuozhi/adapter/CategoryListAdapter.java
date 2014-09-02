package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Category;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<Category> mList;
    private int mMoveValue = 450;
    private HashMap<Integer, Boolean> mViewTagMap;

    public CategoryListAdapter(
            Context context, ArrayList<Category> list, int resource)
    {
        mList = list;
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
        mViewTagMap = new HashMap<Integer, Boolean>();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int index) {
        return mList.get(index);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public boolean isEnabled(int position) {
        Category category = mList.get(position);
        return !Category.GROUP.equals(category.code);
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        ViewHolder holder;
        View currentView;
        Category category = mList.get(index);
        if (view == null) {
            currentView = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.mTitle = (TextView) currentView.findViewById(R.id.category_list_item_title);
            currentView.setTag(holder);
        } else {
            currentView = view;
            holder = (ViewHolder) currentView.getTag();
        }

        holder.mTitle.setText(category.name);
        moveView(holder.mTitle, (category.depth - 1) * 30, category.id);
        return currentView;
    }

    private void moveView(View view, int left, int id)
    {
        Boolean isViewTag = mViewTagMap.get(id);
        if (isViewTag != null) {
            view.setPadding(left, 0, 0, 0);
            return;
        }

        AnimWrap animWrap = new AnimWrap(view, left, id);
        startMoveAnim(animWrap);
    }

    private void startMoveAnim(final AnimWrap animWrap)
    {
        ObjectAnimator objectAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(
                mContext, R.anim.category_item_move);

        objectAnimator.setDuration(mMoveValue);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.setIntValues(0, animWrap.moveX);
        objectAnimator.setTarget(new EdusohoAnimWrap(animWrap.view));
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mViewTagMap.put(animWrap.id, true);
            }
        });
        objectAnimator.start();
        //mMoveValue += 30;
    }

    private class AnimWrap
    {
        public View view;
        public int moveX;
        public int id;

        public AnimWrap(View view, int moveX, int id)
        {
            this.id = id;
            this.view = view;
            this.moveX = moveX;
        }
    }

    private class ViewHolder
    {
        public TextView mTitle;
    }

}
