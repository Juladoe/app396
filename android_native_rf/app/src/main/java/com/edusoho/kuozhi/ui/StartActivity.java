package com.edusoho.kuozhi.ui;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

public class StartActivity extends Activity {

    private Handler mWorkHandler;
    private EdusohoApp app;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        mActivity = this;
        app = (EdusohoApp) getApplication();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        mWorkHandler = new Handler();

        mWorkHandler.postAtTime(new Runnable() {
            @Override
            public void run() {
                startApp();
            }
        }, SystemClock.uptimeMillis() + 1200);
    }


    private void startApp()
    {
        EdusohoApp app = (EdusohoApp) getApplication();
        if (app.config.startWithSchool && app.defaultSchool != null) {
            app.mEngine.runNormalPlugin("SchoolCourseActivity", this, null);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            finish();
            return;
        }


        Intent intent = new Intent(this, com.edusoho.plugin.videoplayer.Videoplayer.class);
        startActivity(intent);

        //app.mEngine.runNormalPlugin("QrSchoolActivity", this, null);
        //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }
}
