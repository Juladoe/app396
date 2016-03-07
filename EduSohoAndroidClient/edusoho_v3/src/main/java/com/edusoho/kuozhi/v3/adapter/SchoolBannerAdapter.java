package com.edusoho.kuozhi.v3.adapter;

/**
 * Created by su on 2016/2/19.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.edusoho.kuozhi.v3.model.sys.SchoolBanner;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by howzhi on 14-8-10.
 */
public class SchoolBannerAdapter extends PagerAdapter {

    private Context mContext;
    private Bitmap cacheBitmap;
    private List<SchoolBanner> mSchoolBanners;

    public SchoolBannerAdapter(Context context, List<SchoolBanner> schoolBanners) {
        mContext = context;
        mSchoolBanners = schoolBanners;
    }

    @Override
    public int getCount() {
        return mSchoolBanners.size();
    }

    public void setItems(List<SchoolBanner> schoolBanners) {
        this.mSchoolBanners.clear();
        this.mSchoolBanners = schoolBanners;
        notifyDataSetChanged();
    }

    public void wrapContent() {
//        SchoolBanner top = mSchoolBanners.get(0);
//        SchoolBanner last = mSchoolBanners.get(mSchoolBanners.size() - 1);
//        mSchoolBanners.add(mSchoolBanners.size(), top);
//        mSchoolBanners.add(0, last);
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        SchoolBanner banner = mSchoolBanners.get(position);
        ImageView photoView = new ImageView(container.getContext());
        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if ("localRes".equals(banner.url)) {
            //photoView.setImageBitmap(cacheBitmap);
        } else {
            ImageLoader.getInstance().displayImage(mSchoolBanners.get(position).url, photoView);
        }

        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SchoolBanner banner = mSchoolBanners.get(position);
                if ("webview".equals(banner.action)) {
                    Pattern CLASSROOM_PAT = Pattern.compile("/classroom/(\\d+)", Pattern.DOTALL);
                    Matcher matcher = CLASSROOM_PAT.matcher(banner.params);
                    if (matcher.find()) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Const.ACTIONBAR_TITLE, "班级标题");
                        return;
                    }
                } else if ("course".equals(banner.action)) {

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