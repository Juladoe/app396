package com.edusoho.kuozhi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class EdusohoListView extends ListView{

	private Context mContext;
	public EdusohoListView(Context context) {
		super(context);
		mContext = context;
		initView();
	}

	public EdusohoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}
	
	private void initView(){	
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		initListHeight();
	}

    /**
     * #mark_update
     */
	public void initListHeight()
	{
		int totalHeight = 0;
		ListAdapter adapter = getAdapter();
		int count = adapter.getCount();
		for (int i=0; i < count; i++) {
			View child = adapter.getView(i, null, this);
			child.measure(0, 0);
			totalHeight += child.getMeasuredHeight() + getDividerHeight();
		}
		
		ViewGroup.LayoutParams lp = getLayoutParams();
		lp.height = totalHeight;
		setLayoutParams(lp);
	}
}
