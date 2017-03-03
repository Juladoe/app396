package com.edusoho.kuozhi.v3.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;

import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by DF on 2017/3/1.
 */

public class TeachActivity extends ActionBarBaseActivity {

    private static int mCourseId;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach);
        hideActionBar();

        initView();
        initData();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText(getIntent().getStringExtra(Const.COURSE_TITLE));
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeachActivity.this.finish();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mCourseId = intent.getIntExtra(Const.COURSE_ID, 0);
        mContext = getApplication();
        if (mCourseId == 0) {
            ToastUtils.show(getApplicationContext(), "课程信息不存在!");
            return;
        }
        loadFragment();
    }

    private void loadFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = CoreEngine.create(this).runPluginWithFragment("TeachFragment", getApplicationContext(), mTeachPluginFragmentCallback);
        fragmentTransaction.replace(R.id.fl_fragment, fragment);
        fragmentTransaction.commit();
    }

    private static PluginFragmentCallback mTeachPluginFragmentCallback = new PluginFragmentCallback() {
        @Override
        public void setArguments(Bundle bundle) {
            School school = ((EdusohoApp) mContext).defaultSchool;
            String url = String.format(Const.MOBILE_APP_URL, school.url + "/", String.format(Const.TEACHER_MANAGERMENT, mCourseId));
            bundle.putString(Const.WEB_URL, url);
        }
    };
}
