package com.edusoho.kuozhi.adapter.lesson;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.hb.views.PinnedSectionListView;

import java.util.ArrayList;


/**
 * Created by howzhi on 14-10-11.
 */
public class LessonEmptyAdapter extends BaseAdapter
        implements PinnedSectionListView.PinnedSectionListAdapter {

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private String[] mList;

    public LessonEmptyAdapter(
            Context context,
            String[] list,
            int resource) {
        mList = list;
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.length;
    }

    @Override
    public String getItem(int index) {
        return mList[index];
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 1;
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        if (view == null) {
            view = inflater.inflate(mResouce, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.list_empty_text);
        textView.setText(mList[index]);
        return view;
    }
}
