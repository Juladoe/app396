package com.edusoho.kuozhi.ui;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.common.QrSchoolActivity;
import com.edusoho.kuozhi.ui.course.SchoolCourseActivity;
import com.edusoho.plugin.video.EduSohoVideoActivity;
import com.ffplay.ffplayActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.Window;

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


        Intent intent = new Intent(this, EduSohoVideoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("url", "http://bcs.duapp.com/bimbucket/ios_rtencode_stream.ts");
        startActivity(intent);
        //app.mEngine.runNormalPlugin("QrSchoolActivity", this, null);
        //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }
}
