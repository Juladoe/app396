package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 14-9-16.
 */
public class EmptyAdapter<T> extends ListBaseAdapter<T>
{
    public static final int PARENT_HEIGHT = 0001;
    public static final int MATCH_PARENT = 0002;
    private int mEmptyIcon;
    private int mParentHeight;

    public EmptyAdapter(Context context, int resource)
    {
        super(context, resource);
    }

    public EmptyAdapter(Context context, int resource, T[] array)
    {
        super(context, resource);
        for (T text : array) {
            mList.add(text);
        }
    }

    public EmptyAdapter(Context context, int resource, T[] array, int icon)
    {
        this(context, resource, array);
        this.mEmptyIcon = icon;
    }

    public void setParentHeight(int parentHeight)
    {
        this.mParentHeight = parentHeight;
    }

    @Override
    public void addItems(ArrayList list) {

    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView  = inflater.inflate(mResource, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.list_empty_text);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_empty_icon);

        imageView.setImageResource(mEmptyIcon);
        textView.setText((String)mList.get(position));

        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        if (mParentHeight != MATCH_PARENT) {
            layoutParams.height = parent.getHeight();
        }
        convertView.setLayoutParams(layoutParams);
        return convertView;
    }
}