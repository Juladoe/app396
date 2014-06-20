package com.edusoho.kuozhi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.edusoho.kuozhi.R;

public class IndexPagerAdapter extends PagerAdapter implements
		OnPageChangeListener, View.OnClickListener {
	private Context mContext;
	private ArrayList<View> mViewList;
	private ArrayList<View> mIndexViewList;

	public IndexPagerAdapter(Context context, ArrayList<View> viewList, LayoutInflater inflater,
			LinearLayout indexLayout) {
		mContext = context;
		mViewList = viewList;
		mIndexViewList = new ArrayList<View>();
		indexLayout.removeAllViews();
		for (int i = 0; i < mViewList.size(); i++) {
			ImageView indexView = new ImageView(mContext);
			indexView.setLayoutParams(new LayoutParams(1, 1));
			indexView.setPadding(1, 1, 1, 1);
			if (i == 0) {
				indexView.setImageResource(R.drawable.viewpager_index_bg_sel);
			} else {
				indexView
						.setImageResource(R.drawable.viewpager_index_bg_normal);
			}
			View pagerView = mViewList.get(i);
			pagerView.setTag(i);
			pagerView.setOnClickListener(this);

			indexLayout.addView(indexView);
			mIndexViewList.add(indexView);
		}
	}

	@Override
	public int getCount() {
		return mViewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mViewList.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mViewList.get(position), 0);
		return mViewList.get(position);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int index) {
		ImageView selView = null;
		for (int i = 0; i < mIndexViewList.size(); i++) {
			selView = (ImageView) mIndexViewList.get(i);
			if (i == index) {
				selView.setImageResource(R.drawable.viewpager_index_bg_sel);
			} else {
				selView.setImageResource(R.drawable.viewpager_index_bg_normal);
			}
		}
	}

	@Override
	public void onClick(View v) {
	}
}
