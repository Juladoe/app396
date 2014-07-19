package com.howzhi;

import android.view.View;
import android.widget.ImageView;

import com.edusoho.kuozhi.ui.SplashActivity;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-7-9.
 */
public class HowzhiSplashActivity extends SplashActivity{

    @Override
    protected void loadConfig() {
        mSplashMode = JazzyViewPager.TransitionEffect.Standard;
    }

    @Override
    public ArrayList<View> initSplashList() {
        int[] imageIds = new int[]{
                R.drawable.splash_1,
                R.drawable.splash_2,
                R.drawable.splash_3
        };
        return createSplashList(imageIds);
    }
}
