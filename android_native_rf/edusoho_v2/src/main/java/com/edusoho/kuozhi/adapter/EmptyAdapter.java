package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-16.
 */
public class EmptyAdapter<T> extends ListBaseAdapter<T>
{
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
            convertView  = inflater.inflate(mRecourse, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.list_empty_text);
        textView.setText((String)mList.get(position));

        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        layoutParams.height = parent.getHeight();
        convertView.setLayoutParams(layoutParams);
        return convertView;
    }
}