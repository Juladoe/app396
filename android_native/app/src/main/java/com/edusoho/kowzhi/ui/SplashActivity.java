package com.edusoho.kowzhi.ui;

import java.util.ArrayList;

import com.androidquery.AQuery;
import com.edusoho.kowzhi.EdusohoApp;
import com.edusoho.kowzhi.R;
import com.edusoho.kowzhi.adapter.IndexPagerAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class SplashActivity extends Activity {

	private int[] splashImages = {
			R.drawable.img1,
			R.drawable.img1,
			R.drawable.img1
	};

    private Context mContext;
	private ViewPager image_viewpager;
    protected EdusohoApp app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        mContext = this;
        app = (EdusohoApp) getApplication();
		init();
	}
	
	private void init()
	{
		LayoutInflater inflater = getLayoutInflater();
		ArrayList<View> viewList = new ArrayList<View>();
        image_viewpager = (ViewPager)findViewById(R.id.splash_image_viewpager);
		LinearLayout indexLayout = (LinearLayout) findViewById(R.id.splash_viewpager_index_layout);
		
		View pager = null;
		for (int i=0; i < splashImages.length; i++) {
			pager = inflater.inflate(R.layout.splash_view, null);
			AQuery query = new AQuery(pager);
			query.id(R.id.pager_image).image(splashImages[i]);
			if (i == splashImages.length -1) {
				query.id(R.id.splash_notice_btn).visibility(View.VISIBLE).clicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent startIntent = new Intent(mContext, IndexPageActivity.class);
                        startActivity(startIntent);
                        app.config.showSplash = false;
                        app.saveConfig();
                        finish();
                    }
                });
			}
			viewList.add(pager);
		}

        IndexPagerAdapter ipAdapter = new IndexPagerAdapter(mContext, viewList, inflater, indexLayout);

		image_viewpager.setAdapter(ipAdapter);
		image_viewpager.setOnPageChangeListener(ipAdapter);
	}
}
