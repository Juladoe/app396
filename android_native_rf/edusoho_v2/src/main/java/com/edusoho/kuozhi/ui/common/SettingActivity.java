package com.edusoho.kuozhi.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.ui.course.FavoriteActivity;
import com.edusoho.kuozhi.ui.course.LearningActivity;
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
    private AQuery aq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setSettingLayout();
        app.addTask("SettingActivity", this);
		initView();
	}

    @Override
    protected void onStart() {
        super.onStart();
        if (setting_check != null) {
            setting_check.setChecked(app.config.startWithSchool);
        }
    }

    public void setSettingLayout()
    {
        setContentView(R.layout.my_setting);
    }

    /**
	 *
	*/
	private void initView()
    {
        setBackMode("个人中心", false, null);
        aq = new AQuery(this);

        setting_check = (CheckBox) findViewById(R.id.setting_check);
        bindClickListener();
	}

    private void bindClickListener()
    {
        if (setting_check != null) {
            setting_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    app.config.startWithSchool = b;
                    app.saveConfig();
                }
            });
        }

        aq.id(R.id.setting_user_layout).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (app.loginUser == null) {
                    LoginActivity.start(mActivity);
                    return;
                }
            }
        });

        aq.id(R.id.setting_favorite_layout).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteActivity.start(mActivity);
            }
        });

        aq.id(R.id.setting_learn_layout).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LearningActivity.start(mActivity);
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
        aq.id(R.id.setting_user_avatar).image(app.loginUser.largeAvatar, false, true, 0, R.drawable.course_teacher_avatar);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
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
