package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-16.
 */
public class ExpandListEmptyAdapter<T> extends BaseExpandableListAdapter
{
    private int mEmptyIcon;
    private ArrayList<T> mList;
    private LayoutInflater inflater;
    private int mResource;

    public ExpandListEmptyAdapter(Context context, int resource, T[] array)
    {
        this.mResource = resource;
        mList = new ArrayList<T>();
        inflater = LayoutInflater.from(context);
        for (T text : array) {
            mList.add(text);
        }
    }

    public ExpandListEmptyAdapter(Context context, int resource, T[] array, int icon)
    {
        this(context, resource, array);
        this.mEmptyIcon = icon;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
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
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public View getGroupView(
            int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView  = inflater.inflate(mResource, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.list_empty_text);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_empty_icon);

        imageView.setImageResource(mEmptyIcon);
        textView.setText((String)mList.get(groupPosition));

        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        layoutParams.height = parent.getHeight();
        convertView.setLayoutParams(layoutParams);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }
}