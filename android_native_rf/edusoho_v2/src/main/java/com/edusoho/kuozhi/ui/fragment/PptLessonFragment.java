package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.course.LessonActivity;
import com.edusoho.plugin.photo.HackyViewPager;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by howzhi on 14-9-18.
 */
public class PptLessonFragment extends BaseFragment {

    private HackyViewPager pptViewPager;
    private ArrayList<String> ppts;
    private Bitmap cacheBitmap;
    private LayoutInflater mLayoutInflater;

    @Override
    public String getTitle() {
        return "ppt";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutInflater = LayoutInflater.from(mContext);
        cacheBitmap = app.query.getCachedImage(R.drawable.defaultpic);
        setContainerView(R.layout.ppt_lesson_layout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            ppts = bundle.getStringArrayList(LessonActivity.CONTENT);
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        pptViewPager = (HackyViewPager) view.findViewById(R.id.ppt_viewpager);
        PptPagerAdapter adapter = new PptPagerAdapter(ppts);
        pptViewPager.setAdapter(adapter);
    }

    public class PptPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener  {

        private ArrayList<String> mImages;

        public PptPagerAdapter(ArrayList<String> images)
        {
            mImages = images;
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final View view = mLayoutInflater.inflate(R.layout.ppt_lesson_item, null);
            PhotoView photoView = (PhotoView) view.findViewById(R.id.ppt_lesson_image);
            photoView.setEnabled(false);
            BitmapAjaxCallback bitmapAjaxCallback = new BitmapAjaxCallback(){
                @Override
                protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    if (bm == null) {
                        return;
                    }
                    view.findViewById(R.id.ppt_lesson_progress).setVisibility(View.GONE);
                    iv.setEnabled(true);
                    iv.setImageBitmap(bm);
                }
            };

            bitmapAjaxCallback.preset(cacheBitmap);
            app.query.id(photoView).image(
                    mImages.get(position), false, true, 0, R.drawable.defaultpic, bitmapAjaxCallback);
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
