package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.edusoho.kuozhi.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by howzhi on 14-8-25.
 */
public class CourseRefreshListWidget extends PullToRefreshListView {

    private Context mContext;

    public CourseRefreshListWidget(Context context) {
        super(context);
        mContext = context;
    }

    public CourseRefreshListWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (adapter.isEmpty()) {
            adapter = getEmptyLayoutAdapter();
        }
        super.setAdapter(adapter);
    }

    protected ListAdapter getEmptyLayoutAdapter()
    {
        String[] emptyText = new String[]{ "没有搜到相关课程，请换个关键词试试！" };
        EmptyAdapter arrayAdapter = new EmptyAdapter(
                mContext, R.layout.course_empty_layout, R.id.list_empty_text, emptyText);

        return arrayAdapter;
    }

    protected void setErrorLayout()
    {

    }

    private class EmptyAdapter extends ArrayAdapter<String>
    {
        public EmptyAdapter(Context context, int resource, int textViewResourceId, String[] objects)
        {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = getHeight();
            view.setLayoutParams(layoutParams);
            return view;
        }
    }

    public ListAdapter getAdapter()
    {
        return getRefreshableView().getAdapter();
    }
}
