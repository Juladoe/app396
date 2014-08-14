package com.edusoho.kuozhi.adapter;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.SchoolBanner;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by howzhi on 14-8-10.
 */
public class SchoolBannerAdapter extends PagerAdapter {

    private EdusohoApp app;
    private Bitmap cacheBitmap;
    private ArrayList<SchoolBanner> mSchoolBanners;

    public SchoolBannerAdapter(EdusohoApp app, ArrayList<SchoolBanner> schoolBanners)
    {
        this.app = app;
        mSchoolBanners = schoolBanners;
        cacheBitmap = app.query.getCachedImage(R.drawable.defaultpic);
    }

    @Override
    public int getCount() {
        return mSchoolBanners.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        ImageView photoView = new ImageView(container.getContext());
        photoView.setScaleType(ImageView.ScaleType.FIT_XY);
        app.query.id(photoView).image(
                mSchoolBanners.get(position).url, false, true, 0, R.drawable.defaultpic, cacheBitmap, AQuery.FADE_IN);
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
