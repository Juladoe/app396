package com.edusoho.kuozhi.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.ui.course.FavoriteActivity;
import com.edusoho.kuozhi.ui.course.SchoolCourseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;

/**
 * 
 * @author howzhi
 *
*/
public class SettingActivity extends BaseActivity
{
    private CheckBox setting_check;
    private ViewGroup nav_courselist_btn;
    private ViewGroup nav_my_btn;
    private AQuery aq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_setting);
        app.addTask("SettingActivity", this);
		initView();
	}

    @Override
    protected void onStart() {
        super.onStart();
        setting_check.setChecked(app.config.startWithSchool);
    }

    /**
	 *
	*/
	private void initView()
    {
        setBackMode("个人中心", false, null);
        aq = new AQuery(this);

        setting_check = (CheckBox) findViewById(R.id.setting_check);
        nav_courselist_btn = (ViewGroup) findViewById(R.id.nav_courselist_btn);
        nav_my_btn = (ViewGroup) findViewById(R.id.nav_my_btn);

        enableBtn(nav_my_btn, false);
        bindClickListener();
	}

    private void bindClickListener()
    {
        setting_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                app.config.startWithSchool = b;
                app.saveConfig();
            }
        });

        aq.id(R.id.setting_user_layout).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (app.loginUser == null) {
                    LoginActivity.start(mActivity);
                    return;
                }
            }
        });

        aq.id(R.id.nav_courselist_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent schIntent = new Intent();
                schIntent.setClass(mContext, SchoolCourseActivity.class);
                startActivity(schIntent);
            }
        });

        aq.id(R.id.setting_favorite_layout).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteActivity.start(mActivity);
            }
        });

        //关于页面
        aq.id(R.id.setting_about_layout).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutActivity.start(mActivity);
            }
        });

        aq.id(R.id.setting_notification_layout).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationActivity.start(mActivity);
            }
        });

        //启动默认网校
        aq.id(R.id.nav_courselist_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!app.taskIsRun(SchoolCourseActivity.TAG)) {
                    SchoolCourseActivity.start(mActivity);
                }
                finish();
            }
        });

        aq.id(R.id.sel_sch_layout).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QrSchoolActivity.start(mActivity);
            }
        });

        aq.id(R.id.setting_clear_layout).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupDialog.createMuilt(
                        mContext,
                        "清除缓存",
                        "确定清除缓存?",
                        new PopupDialog.PopupClickListener() {
                            @Override
                            public void onClick(int button) {
                                if (button == PopupDialog.OK) {
                                    LoadDialog loadDialog = LoadDialog.create(mContext);
                                    loadDialog.showAutoHide("清除中....");
                                }
                            }
                        }).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
    }

    private void loadUser()
    {
        if (app.loginUser == null) {
            aq.id(R.id.setting_user_nickname).text("点击登录网校");
            aq.id(R.id.setting_user_info).text("暂无个人简介");
            return;
        }

        aq.id(R.id.setting_user_layout).enabled(false);
        aq.id(R.id.setting_user_avatar).image(app.loginUser.largeAvatar, false, true);
        aq.id(R.id.setting_user_nickname).text(app.loginUser.nickname);
        aq.id(R.id.setting_user_info).text(app.loginUser.title);
        aq.id(R.id.setting_logout_btn).visibility(View.VISIBLE).clicked(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupDialog.createMuilt(
                                mContext,
                                "退出提示",
                                "确定退出登录?",
                                new PopupDialog.PopupClickListener() {
                                    @Override
                                    public void onClick(int button) {
                                        if (button == PopupDialog.OK) {
                                            logout();
                                        }
                                    }
                                }).show();
                    }
        });
    }

    private void logout()
    {
        String url = app.bindToken2Url(Const.LOGOUT, true);
        ajaxGetString(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if ("true".equals(object)) {
                    app.removeToken();
                    aq.id(R.id.setting_user_avatar).image(R.drawable.course_teacher_avatar);
                    aq.id(R.id.setting_user_nickname).text("点击登录网校");
                    aq.id(R.id.setting_user_info).text("暂无个人简介");
                    aq.id(R.id.setting_logout_btn).visibility(View.GONE);
                    aq.id(R.id.setting_user_layout).enabled(true);
                    aq.id(R.id.setting_user_layout).clicked(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LoginActivity.start(mActivity);
                        }
                    });
                }
            }
        });
    }

    private void enableBtn(ViewGroup vg, boolean isEnable)
    {
        int count = vg.getChildCount();
        for (int i=0; i < count; i++) {
            vg.getChildAt(i).setEnabled(isEnable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}
