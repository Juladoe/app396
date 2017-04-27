package com.edusoho.kuozhi.clean.module.course.task.menu.rate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.course.rate.CourseProjectRatesFragment;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.ReviewActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;

/**
 * Created by DF on 2017/4/24.e
 */

public class RatesActivity extends BaseActivity {

    public static final String COURSE_PROJECT_MODEL = "CourseProjectModel";

    private View mBack;
    private View mPublish;
    private CourseProjectRatesFragment mCourseProjectRatesFragment;

    public static void launch(Context context, CourseProject courseProject) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COURSE_PROJECT_MODEL, courseProject);
        intent.setClass(context, RatesActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);

        initView();
        initFragment(getIntent().getExtras());
        initEvent();
    }

    private void initView() {
        mBack = findViewById(R.id.es_back);
        mPublish = findViewById(R.id.es_publish);
    }

    private void initFragment(Bundle bundle) {
        mCourseProjectRatesFragment = new CourseProjectRatesFragment();
        mCourseProjectRatesFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_content, mCourseProjectRatesFragment)
                .commit();
    }

    private void initEvent() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoreEngine.create(getBaseContext()).runNormalPluginForResult("ReviewActivity", RatesActivity.this, ReviewActivity.REVIEW_RESULT
                        , new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(ReviewActivity.TYPE, ReviewActivity.TYPE_COURSE);
                                startIntent.putExtra(ReviewActivity.ID, ((CourseProject) getIntent().getExtras().getSerializable(COURSE_PROJECT_MODEL)).id);
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ReviewActivity.REVIEW_RESULT) {
            mCourseProjectRatesFragment.reFreshView();
        }
    }
}
