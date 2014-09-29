package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.edusoho.kuozhi.EdusohoApp;
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

    private TextView mTotalPageView;
    private TextView mStartPageView;
    private View mScreenView;

    private boolean isScreen;

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
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            ppts = bundle.getStringArrayList(LessonActivity.CONTENT);
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mScreenView = view.findViewById(R.id.ppt_page_screen);
        mStartPageView = (TextView) view.findViewById(R.id.ppt_page_start);
        mTotalPageView = (TextView) view.findViewById(R.id.ppt_page_total);
        pptViewPager = (HackyViewPager) view.findViewById(R.id.ppt_viewpager);

        PptPagerAdapter adapter = new PptPagerAdapter(ppts);
        mTotalPageView.setText("/ " + ppts.size());
        mStartPageView.setText("1");
        pptViewPager.setAdapter(adapter);
        pptViewPager.setOnPageChangeListener(adapter);

        mScreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isScreen) {
                    app.sendMsgToTarget(LessonActivity.SHOW_TOOLS, null, LessonActivity.class);
                    mActivity.showActionBar();
                } else {
                    app.sendMsgToTarget(LessonActivity.HIDE_TOOLS, null, LessonActivity.class);
                    mActivity.hideActionBar();
                }
                isScreen = ! isScreen;
            }
        });

        mStartPageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView listView = new ListView(mContext);
                ArrayList<String> array = new ArrayList<String>();
                for (int i = 1; i <= ppts.size(); i++) {
                    array.add(i + "");
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        mContext, R.layout.ppt_lesson_popwindow_list_item, array
                );
                listView.setAdapter(arrayAdapter);
                final PopupWindow popupWindow = new PopupWindow(listView, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setWidth((int)(mStartPageView.getWidth() * 1.5f));
                popupWindow.setHeight(EdusohoApp.screenH / 3);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_bg));
                popupWindow.setFocusable(true);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        pptViewPager.setCurrentItem(i);
                        popupWindow.dismiss();
                    }
                });
                int[] location = new int[2];
                mStartPageView.getLocationOnScreen(location);
                popupWindow.showAtLocation(
                        mStartPageView,
                        Gravity.NO_GRAVITY,
                        location[0],
                        location[1]
                );
            }
        });
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
            mStartPageView.setText((position + 1) + "");
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
