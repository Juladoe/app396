package com.edusoho.kuozhi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.entity.SettingItem;
import com.edusoho.kuozhi.entity.SettingItem.ItemCheckListener;
import com.edusoho.kuozhi.util.Const;

public class SettingAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private int mResouce;
	private Context mContext;
	private ArrayList<SettingItem> mList;
	
	public SettingAdapter(
			Context context, ArrayList<SettingItem> list, int resource)
	{
		mList = list;
		mContext = context;
		mResouce = resource;
		inflater = LayoutInflater.from(context);
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
	public View getView(int index, View view, ViewGroup vg) {
		final ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(mResouce, null);
			holder = new ViewHolder();
			holder.set_title = (TextView) view.findViewById(R.id.setting_title);
			holder.set_logo = (ImageView) view.findViewById(R.id.setting_logo);
			holder.set_ck = (CheckBox) view.findViewById(R.id.setting_check);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		SettingItem rcItem = mList.get(index);
		holder.set_title.setText(rcItem.title);
		if (rcItem.logo != 0) {
			holder.set_logo.setImageResource(rcItem.logo);
		}
		
		if (rcItem.type == Const.CHECKBOX_ITEM) {
			holder.set_ck.setVisibility(View.VISIBLE);
			rcItem.listener = new ItemCheckListener() {
				@Override
				public void check() {
					holder.set_ck.setChecked(!holder.set_ck.isChecked()); 
				}
			};
		}
		
		return view;
	}
	
	private class ViewHolder
	{
		public TextView set_title;
		public ImageView set_logo;
		public CheckBox set_ck;
	}

}
