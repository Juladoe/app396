package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.ArrayList;

/**
 * Created by howzhi on 14/11/22.
 */
public class ListLoadAdapter extends ListBaseAdapter<String> {

    public ListLoadAdapter(Context context, int resource)
    {
        super(context, resource);
        mList.add("loading");
    }

    @Override
    public void addItem(String item) {
        super.addItem(item);
    }

    @Override
    public void addItems(ArrayList<String> list) {

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(mResource, null);
        }
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        layoutParams.height = viewGroup.getHeight();
        view.setLayoutParams(layoutParams);
        return view;
    }
}
