package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Category;
import com.edusoho.kuozhi.ui.widget.CategoryListView;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class FoundCategoryListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<Category> mList;
    private int mMoveValue = 450;
    private HashMap<Integer, Boolean> mViewTagMap;
    private ExpandableListView mExpandableListView;

    private Stack<ArrayList<Category>> categoryStack;
    private Stack<Integer> indexStack;

    public FoundCategoryListAdapter(
            Context context,
            ArrayList<Category> list,
            int resource,
            ExpandableListView expandableListView)
    {
        mList = new ArrayList<Category>();
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
        mViewTagMap = new HashMap<Integer, Boolean>();
        mExpandableListView = expandableListView;

        indexStack = new Stack<Integer>();
        categoryStack = new Stack<ArrayList<Category>>();
        coverList(list);
    }

    //转换list
    private void coverList(ArrayList<Category> list)
    {
        ArrayList<Category> tempList = null;
        for (Category category : list) {
            if (0 == category.parentId) {
                mList.add(category);
            } else {
                //parentId != 0
                if (!indexStack.isEmpty()) {
                    if (indexStack.peek() != category.parentId) {
                        indexStack.pop();
                        categoryStack.pop();
                    }
                    if (indexStack.peek() == category.parentId) {
                        tempList = categoryStack.peek();
                        tempList.add(category);
                    }
                }
            }

            indexStack.push(category.id);
            tempList = new ArrayList<Category>();
            category.childs = tempList;
            categoryStack.push(tempList);
        }

        indexStack.clear();
        categoryStack.clear();
    }

    public void setItems(ArrayList<Category> categories)
    {
        mList.clear();
        coverList(categories);
        notifyDataSetChanged();
    }

    @Override
    public Object getGroup(int i) {
        return mList.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mList.get(groupPosition).childs.get(childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mList.get(groupPosition).childs.size();
    }

    @Override
    public View getChildView(int index, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        View currentView;
        Category category = mList.get(index).childs.get(childPosition);
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
        holder.mTitle.setPadding((category.depth - 1) * 30, 0, 0, 0);
        return currentView;
    }

    @Override
    public View getGroupView(int index, boolean isExpanded, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        View currentView;
        Category category = mList.get(index);
        if (view == null) {
            currentView = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.mTitle = (TextView) currentView.findViewById(R.id.category_list_item_title);
            holder.mExpandView = currentView.findViewById(R.id.category_list_item_expand);
            currentView.setTag(holder);
        } else {
            currentView = view;
            holder = (ViewHolder) currentView.getTag();
        }

        if (!category.childs.isEmpty()) {
            holder.mExpandView.setTag(index);
            holder.mExpandView.setVisibility(View.VISIBLE);
            holder.mExpandView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int groupId = (Integer) view.getTag();
                    if (mExpandableListView.isGroupExpanded(groupId)) {
                        mExpandableListView.collapseGroup(groupId);
                        rotation(view, -180, 0);
                    } else {
                        rotation(view, 0, -180);
                        mExpandableListView.expandGroup(groupId);
                    }
                }
            });
        } else {
            holder.mExpandView.setVisibility(View.GONE);
        }
        holder.mTitle.setText(category.name);
        //moveView(holder.mTitle, (category.depth - 1) * 30, category.id);
        return currentView;
    }

    private void rotation(View view, float start, float end)
    {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", start, end);
        objectAnimator.setDuration(180);
        objectAnimator.start();
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
        public View mExpandView;
    }

}
