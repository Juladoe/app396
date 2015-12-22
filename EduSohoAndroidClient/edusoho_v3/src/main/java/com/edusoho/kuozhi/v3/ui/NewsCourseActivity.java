package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.push.New;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.DiscussFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.google.gson.reflect.TypeToken;

/**
 * Created by JesseHuang on 15/9/16.
 */
public class NewsCourseActivity extends ActionBarBaseActivity {
    public static int CurrentCourseId = 0;
    public static final String COURSE_ID = "course_id";
    private static final String mFragmentTags[] = {"DiscussFragment", "CourseStudyFragment", "TeachFragment"};
    private static final String mRadioButtonTitle[] = {"学习", "教学"};
    private int mCourseId;
    private String mCourseTitle;
    private String mCourseType;
    private String mCurrentFragmentTag;
    private New mNewItemInfo;
    private String mUserType;
    private PluginFragmentCallback mPluginFragmentCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_course);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mNewItemInfo = (New) intent.getSerializableExtra(Const.NEW_ITEM_INFO);
        if (mNewItemInfo == null || mNewItemInfo.fromId == 0) {
            CommonUtil.longToast(getApplicationContext(), getString(R.string.course_params_error));
            return;
        }
        mCourseTitle = mNewItemInfo.title;
        mCourseId = mNewItemInfo.fromId;
        mCourseType = mNewItemInfo.type;
        CurrentCourseId = mCourseId;
        mUserType = app.getCurrentUserRole();
        if (PushUtil.ChatUserType.COURSE.equals(mCourseType) && PushUtil.ChatUserType.FRIEND.equals(mUserType)) {
            //用户，讨论
            initSwitchButton(BACK, mRadioButtonTitle[0], mOnCheckedChangeListener);
            setRadioButtonChecked(R.id.rb_discuss);
        } else if (!PushUtil.ChatUserType.COURSE.equals(mCourseType) && PushUtil.ChatUserType.FRIEND.equals(mUserType)) {
            //用户，学习
            initSwitchButton(BACK, mRadioButtonTitle[0], mOnCheckedChangeListener);
            setRadioButtonChecked(R.id.rb_study);
        } else if (PushUtil.ChatUserType.COURSE.equals(mCourseType) && PushUtil.ChatUserType.TEACHER.equals(mUserType)) {
            //教师，讨论
            initSwitchButton(BACK, mRadioButtonTitle[1], mOnCheckedChangeListener);
            setRadioButtonChecked(R.id.rb_discuss);
        } else if (!PushUtil.ChatUserType.COURSE.equals(mCourseType) && PushUtil.ChatUserType.TEACHER.equals(mUserType)) {
            //教师，教学
            initSwitchButton(BACK, mRadioButtonTitle[1], mOnCheckedChangeListener);
            setRadioButtonChecked(R.id.rb_study);
        }
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
            if (tag.equals(mFragmentTags[0])) {
                fragment = app.mEngine.runPluginWithFragment(tag, mActivity, null);
            } else if (tag.equals(mFragmentTags[1])) {
                fragment = app.mEngine.runPluginWithFragment(tag, mActivity, mStudyPluginFragmentCallback);
            } else if (tag.equals(mFragmentTags[2])) {
                fragment = app.mEngine.runPluginWithFragment(tag, mActivity, mTeachPluginFragmentCallback);
            }
            fragmentTransaction.add(R.id.fragment_container, fragment, tag);
        }
        fragmentTransaction.commit();
        mCurrentFragmentTag = tag;
        if (mCurrentFragmentTag.equals(mFragmentTags[0])) {
            setSwitchBadgeViewVisible(View.INVISIBLE);
        }
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_study) {
                if (PushUtil.ChatUserType.FRIEND.equals(mUserType)) {
                    showFragment(mFragmentTags[1]);
                } else {
                    showFragment(mFragmentTags[2]);
                }
            } else if (checkedId == R.id.rb_discuss) {
                showFragment(mFragmentTags[0]);
            }
        }
    };

    private PluginFragmentCallback mTeachPluginFragmentCallback = new PluginFragmentCallback() {
        @Override
        public void setArguments(Bundle bundle) {
            String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.TEACHER_MANAGERMENT, app.loginUser.id));
            bundle.putString(Const.WEB_URL, url);
        }
    };

    private PluginFragmentCallback mStudyPluginFragmentCallback = new PluginFragmentCallback() {
        @Override
        public void setArguments(Bundle bundle) {
            bundle.putSerializable(Const.NEW_ITEM_INFO, mNewItemInfo);
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
        return new MessageType[]{new MessageType(Const.ADD_COURSE_DISCUSS_MSG, source)};
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        switch (messageType.code) {
            case Const.ADD_COURSE_DISCUSS_MSG:
                WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
                V2CustomContent v2CustomContent = parseJsonValue(wrapperMessage.getCustomContentJson(), new TypeToken<V2CustomContent>() {
                });
                if (mCurrentFragmentTag.equals(mFragmentTags[0]) && v2CustomContent.getTo().getId() == mNewItemInfo.fromId) {
                    app.sendMsgToTarget(Const.ADD_COURSE_DISCUSS_MSG, message.data, DiscussFragment.class);
                }
                if (!mCurrentFragmentTag.equals(mFragmentTags[0])) {
                    setSwitchBadgeViewVisible(View.VISIBLE);
                }
                break;
            default:
        }
    }
}
