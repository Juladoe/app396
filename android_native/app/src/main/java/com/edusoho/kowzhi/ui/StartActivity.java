package com.edusoho.kowzhi.ui;

import com.edusoho.kowzhi.EdusohoApp;
import com.edusoho.kowzhi.R;

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
        Intent startIntent;
        EdusohoApp app = (EdusohoApp) getApplication();
        if (app.config.startWithSchool && app.defaultSchool != null) {
            startIntent = new Intent(this, SchCourseActivity.class);
            startActivity(startIntent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            finish();
            return;
        }

        startIntent = new Intent(this, QrSchoolActivity.class);
        startActivity(startIntent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

        finish();
    }
}
