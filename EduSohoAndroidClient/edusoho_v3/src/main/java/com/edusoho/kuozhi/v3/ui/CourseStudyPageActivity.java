package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.Const;

/**
 * Created by melomelon on 15/12/9.
 */
public class CourseStudyPageActivity extends BaseActivity {

    private FragmentManager mFragmentManager;

    private Toolbar mToolbar;
    private RadioButton studyButton;
    private RadioButton discussButton;
    private RadioGroup mTabRadioGroup;

    private Bundle mBundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_study_page_layout);
        setBundleMessage();
        initView();
        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_group_study_process) {
                    switchFragment("CourseStudyProcessFragment");
                }
                if (i == R.id.radio_group_study_discuss) {
                    switchFragment("CourseStudyDiscussFragment");
                }
            }
        });

    }

    protected void initView() {
        mFragmentManager = getSupportFragmentManager();
        initToolbar();
        loadFragment();
    }

    protected void setBundleMessage() {
        Intent intent = getIntent();
        if (intent == null){
            return ;
        }
        String courseTitile = intent.getStringExtra(Const.ACTIONBAR_TITLE);
        int courseId = intent.getIntExtra("course_id", 0);
        mBundle = new Bundle();
        mBundle.putString(Const.ACTIONBAR_TITLE,courseTitile);
        mBundle.putInt("course_id",courseId);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.course_study_page_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.icon_up_button);

            mTabRadioGroup = (RadioGroup) findViewById(R.id.course_process_radio_group);
            studyButton = (RadioButton) findViewById(R.id.radio_group_study_process);
            discussButton = (RadioButton) findViewById(R.id.radio_group_study_discuss);
            studyButton.setChecked(true);

        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    protected void loadFragment() {
        if (studyButton.isChecked()) {
            switchFragment("CourseStudyProcessFragment");
        }
        if (discussButton.isChecked()) {
            switchFragment("CourseStudyDiscussFragment");
        }
    }

    protected void switchFragment(String fragmentName) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment fragment = app.mEngine.runPluginWithFragment(fragmentName, this, null);
        fragment.setArguments(mBundle);
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_study_page_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //TODO 是否两个fragment都需要这个icon
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.course_study_page_menu_student) {

        }
        return super.onOptionsItemSelected(item);
    }


}
