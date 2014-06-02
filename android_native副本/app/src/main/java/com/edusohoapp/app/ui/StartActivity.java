package com.edusohoapp.app.ui;

import com.edusohoapp.app.EdusohoApp;
import com.edusohoapp.app.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        Intent startIntent;
        EdusohoApp app = (EdusohoApp) getApplication();
        if (app.config.startWithSchool && app.defaultSchool != null) {
            startIntent = new Intent(this, SchCourseActivity.class);
            startActivity(startIntent);
            finish();
            return;
        }

		startIntent = new Intent(this, QrSchoolActivity.class);
		startActivity(startIntent);

        finish();
    }
}
