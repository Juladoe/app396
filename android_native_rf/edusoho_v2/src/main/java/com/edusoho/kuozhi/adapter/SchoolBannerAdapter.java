package com.edusoho.kuozhi.adapter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.SchoolBanner;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.AboutFragment;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-8-10.
 */
public class SchoolBannerAdapter extends PagerAdapter {

    private ActionBarBaseActivity mActivity;
    private Bitmap cacheBitmap;
    private ArrayList<SchoolBanner> mSchoolBanners;

    public SchoolBannerAdapter(
            ActionBarBaseActivity activity, ArrayList<SchoolBanner> schoolBanners)
    {
        mActivity = activity;
        mSchoolBanners = schoolBanners;
        cacheBitmap = mActivity.app.query.getCachedImage(R.drawable.defaultpic);
    }

    @Override
    public int getCount() {
        return mSchoolBanners.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final SchoolBanner banner = mSchoolBanners.get(position);
        ImageView photoView = new ImageView(container.getContext());
        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if ("localRes".equals(banner.url)) {
            photoView.setImageBitmap(cacheBitmap);
        } else {
            mActivity.app.query.id(photoView).image(
                    mSchoolBanners.get(position).url, false, true, 0,
                    R.drawable.defaultpic, cacheBitmap, AQuery.FADE_IN);
        }

        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("webview".equals(banner.action)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AboutFragment.URL, banner.params);
                    bundle.putString(FragmentPageActivity.FRAGMENT, "AboutFragment");
                    mActivity.app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
                }
            }
        });
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
