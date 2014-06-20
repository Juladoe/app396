package com.edusoho.kuozhi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.entity.RecommendSchoolItem;

public class RecommendSchoolAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private int mResouce;
	private Context mContext;
	private ArrayList<RecommendSchoolItem> mList;
	
	public RecommendSchoolAdapter(Context context, ArrayList<RecommendSchoolItem> list, int resource)
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
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(mResouce, null);
			holder = new ViewHolder();
            holder.aq = new AQuery(view);
			holder.r_sch_name = (TextView) view.findViewById(R.id.recommend_sch_name);
			holder.r_sch_info = (TextView) view.findViewById(R.id.recommend_sch_info);
			holder.r_sch_logo = (ImageView) view.findViewById(R.id.recommend_sch_logo);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		RecommendSchoolItem rcItem = mList.get(index);
		holder.r_sch_name.setText(rcItem.title);
		holder.r_sch_info.setText(rcItem.info);
        holder.aq.id(R.id.recommend_sch_logo).image(rcItem.logo,false, true);
		return view;
	}
	
	private class ViewHolder
	{
		/** recommend_school_name */
        public AQuery aq;
		public TextView r_sch_name;
		public TextView r_sch_info;
		public ImageView r_sch_logo;
	}

}
