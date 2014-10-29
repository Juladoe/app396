package com.edusoho.kuozhi;

import android.view.View;

import com.edusoho.kuozhi.ui.SplashActivity;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-8-6.
 */
public class KuozhiSplashActivity extends SplashActivity {

    @Override
    protected void loadConfig() {
        mSplashMode = JazzyViewPager.TransitionEffect.Standard;
    }

    @Override
    public ArrayList<View> initSplashList() {
        int[] imageIds = new int[]{
        };
        return createSplashList(imageIds);
    }
}
