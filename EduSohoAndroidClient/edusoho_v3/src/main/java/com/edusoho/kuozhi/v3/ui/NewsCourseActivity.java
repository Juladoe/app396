package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;

/**
 * Created by JesseHuang on 15/9/16.
 */
public class NewsCourseActivity extends ActionBarBaseActivity {
    public static int CurrentCourseId = 0;
    public static final String COURSE_ID = "course_id";
    private static final String mFragmentTags[] = {"CourseStudyFragment", "CourseDiscussFragment"};
    private int mCourseId;
    private String mCourseTitle;
    private String mCurrentFragmentTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_course);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mCourseTitle = intent.getStringExtra(Const.ACTIONBAR_TITLE);
        //setBackMode(BACK, mCourseTitle);
        initSwitchButton(BACK, mOnCheckedChangeListener);
        mCourseId = intent.getIntExtra(COURSE_ID, 0);
        CurrentCourseId = mCourseId;
        if (mCourseId == 0) {
            CommonUtil.longToast(getApplicationContext(), getString(R.string.course_params_error));
            return;
        }
        showFragment(mFragmentTags[0]);
    }


    private void showFragment(String tag) {
        Fragment fragment;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment currentFragment = mFragmentManager.findFragmentByTag(mCurrentFragmentTag);
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentTransaction.show(fragment);
        } else {
            fragment = app.mEngine.runPluginWithFragment(tag, mActivity, null);
            fragmentTransaction.add(R.id.fragment_container, fragment, tag);
        }
        fragmentTransaction.commit();
        mCurrentFragmentTag = tag;
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_study) {
                showFragment(mFragmentTags[0]);
            } else if (checkedId == R.id.rb_discuss) {
                showFragment(mFragmentTags[1]);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.news_course_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.news_course_profile) {
            app.mEngine.runNormalPlugin("CourseDetailActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(Const.FROM_ID, mCourseId);
                    startIntent.putExtra(Const.ACTIONBAR_TITLE, mCourseTitle);
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{new MessageType(Const.ADD_COURSE_MSG, source)};
    }

    @Override
    public void invoke(WidgetMessage message) {

    }
}
