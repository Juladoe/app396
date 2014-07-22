package com.edusoho.kuozhi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;

import com.edusoho.kuozhi.core.listener.CoreEngineMsgCallback;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;

import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.ui.common.CourseColumnActivity;
import com.edusoho.kuozhi.ui.course.SchoolCourseActivity;
import com.edusoho.kuozhi.view.dialog.PopupDialog;

/**
 * Created by howzhi on 14-7-22.
 */
public class DefaultPageActivity extends BaseActivity {

    private ViewGroup mNavBtnLayout;
    private NavBtnClickListener mNavBtnClickListener;

    private ViewGroup viewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defalt_layout);
        initView();
    }

    private void initView() {
        viewContent = (ViewGroup) findViewById(R.id.view_content);
        mNavBtnClickListener = new NavBtnClickListener();
        bindNavOnClick();
        selectNavBtn(R.id.nav_courselist_btn);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            PopupDialog.createMuilt(
                    mContext, "退出应用", "确定退出应用?", new PopupDialog.PopupClickListener() {
                @Override
                public void onClick(int button) {
                    if (button == PopupDialog.OK) {
                        app.exit();
                        finish();
                    }
                }
            }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void selectNavBtn(int id) {
        View contentView = null;
        if (id == R.id.nav_my_btn) {
            contentView = app.mEngine.runNormalPluginInGroup("SettingActivity", mActivity, null);
        } else if (id == R.id.nav_column_btn) {
            app.addMessageListener(
                    CourseColumnActivity.LOAD_COURSE_BY_COLUMN, new CoreEngineMsgCallback() {
                        @Override
                        public void invoke(MessageModel obj) {
                            app.sendMessage(SchoolCourseActivity.REFRESH_COURSE, obj);
                            selectNavBtn(R.id.nav_courselist_btn);
                        }
            });

            contentView = app.mEngine.runNormalPluginInGroup(
                    "CourseColumnActivity", mActivity, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(CourseColumnActivity.COLUMN_PARENT, "");
                        }
            });
        } else if (id == R.id.nav_courselist_btn) {
            contentView = app.mEngine.runNormalPluginInGroup("SchoolCourseActivity", mActivity, null);
        }

        viewContent.removeAllViews();
        viewContent.addView(contentView);
        changeNavBtn(id);
    }

    private void changeNavBtn(int id)
    {
        mNavBtnLayout = (ViewGroup) findViewById(R.id.nav_bottom_layout);
        int count = mNavBtnLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mNavBtnLayout.getChildAt(i);
            if (child instanceof LinearLayout) {
                if (child.getId() == id) {
                    enableBtn((ViewGroup)child, false);
                } else {
                    enableBtn((ViewGroup)child, true);
                }
            }
        }
    }
    private class NavBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            selectNavBtn(id);
        }
    }

    private void bindNavOnClick() {
        mNavBtnLayout = (ViewGroup) findViewById(R.id.nav_bottom_layout);
        int count = mNavBtnLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mNavBtnLayout.getChildAt(i);
            if (child instanceof LinearLayout) {
                child.setOnClickListener(mNavBtnClickListener);
            }
        }
    }

}
