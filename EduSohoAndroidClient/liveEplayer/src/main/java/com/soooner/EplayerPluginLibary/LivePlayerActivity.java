package com.soooner.EplayerPluginLibary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
/**
 * Created by howzhi on 15/10/12.
 */
public class LivePlayerActivity extends Activity {

    public static final String CLASSROOM_ID = "liveClassroomId";
    public static final String EXSTR = "exStr";
    public static final String STATUS = "replayState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        if (data != null) {
            String liveClassroomId = data.getStringExtra(CLASSROOM_ID);
            String exStr = data.getStringExtra(EXSTR);
            boolean replayState = data.getBooleanExtra(STATUS, false);
            new LiveUtil(this).startLiveActivity(liveClassroomId, exStr, replayState);
        }

        finish();
    }
}
