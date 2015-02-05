package com.edusoho.kuozhi;

import android.app.Activity;
import android.os.Bundle;

import com.soooner.EplayerPluginLibary.R;
import com.soooner.EplayerPluginLibary.util.ActivityUtil;

/**
 * Created by howzhi on 15/2/5.
 */
public class LiveTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_layout);
        ActivityUtil.initLiveRoom(this, "edusoho", "54d31bafa68d513f2d0007d1", "bbbOfIDjRl2H0WyuD3edDEuwE6bBnTVc|live-53@edusoho.net|xiaoping|学员");
    }
}
