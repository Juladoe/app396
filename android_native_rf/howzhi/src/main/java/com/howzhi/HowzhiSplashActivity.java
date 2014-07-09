package com.howzhi;

import android.view.View;
import android.widget.ImageView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.SplashActivity;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-7-9.
 */
public class HowzhiSplashActivity extends SplashActivity{

    @Override
    public ArrayList<View> setSplashList() {
        ArrayList<View> mViewList = new ArrayList<View>();
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(com.edusoho.kuozhi.R.drawable.sp2);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mViewList.add(imageView);

        imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.app_splash);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mViewList.add(imageView);
        return mViewList;
    }
}
