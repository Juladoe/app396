package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.shard.ThirdPartyLogin;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;

import java.io.File;

/**
 * Created by JesseHuang on 15/5/6.
 */
public class SettingActivity extends ActionBarBaseActivity {
    private View viewScan;
    private View tvMsgNotify;
    private View tvAbout;
    private View viewClearCache;
    private TextView tvCache;
    private Button btnLogout;
    private CheckBox cbOfflineType;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setBackMode(BACK, "设置");
        initView();
        initData();
    }

    private void initView() {
        viewScan = findViewById(R.id.linear_scan);
        viewScan.setOnClickListener(scanClickListener);
        tvMsgNotify = findViewById(R.id.rl_msg_notify);
        tvMsgNotify.setOnClickListener(msgClickListener);
        tvAbout = findViewById(R.id.rl_about);
        tvAbout.setOnClickListener(aboutClickListener);
        cbOfflineType = (CheckBox) findViewById(R.id.cb_offline_type);
        cbOfflineType.setOnClickListener(setOfflineTypeListener);
        tvCache = (TextView) findViewById(R.id.tv_cache);
        viewClearCache = findViewById(R.id.rl_clear_cache);
        viewClearCache.setOnClickListener(cleanCacheListener);

        btnLogout = (Button) findViewById(R.id.setting_logout_btn);
        btnLogout.setOnClickListener(logoutClickLister);
        if (app.loginUser != null) {
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            btnLogout.setVisibility(View.INVISIBLE);
        }
    }

    private void initData() {
        float size = getCacheSize(app.getWorkSpace()) / 1024.0f / 1024.0f;
        if (size == 0) {
            tvCache.setText("0M");
        } else {
            tvCache.setText(String.format("%.1f%s", size, "M"));
        }

        cbOfflineType.setChecked(app.config.offlineType == 1);
    }

    private View.OnClickListener setOfflineTypeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            app.config.offlineType = cbOfflineType.isChecked() ? 1 : 0;
            app.saveConfig();
        }
    };

    private View.OnClickListener cleanCacheListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupDialog.createMuilt(
                    mActivity,
                    "清理缓存",
                    "是否清理文件缓存",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                clearCache();
                            }
                        }
                    }).show();
        }
    };

    private View.OnClickListener scanClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.app.mEngine.runNormalPlugin("QrSchoolActivity", mActivity, null);
        }
    };

    private View.OnClickListener msgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.app.mEngine.runNormalPlugin("MsgReminderActivity", mActivity, null);
        }
    };

    private View.OnClickListener aboutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.app.mEngine.runNormalPlugin("AboutActivity", mActivity, null);
        }
    };

    private View.OnClickListener logoutClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(app.loginUser.thirdParty)) {
                RequestUrl requestUrl = app.bindUrl(Const.LOGOUT, true);
                mActivity.ajaxPostWithLoading(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Const.BIND_USER_ID, app.loginUser.id + "");
                        app.pushUnregister(bundle);
                        app.removeToken();
                        btnLogout.setVisibility(View.INVISIBLE);
                        app.sendMessage(Const.LOGOUT_SUCCESS, null);
                        app.sendMsgToTarget(Const.SWITCH_TAB, null, DefaultPageActivity.class);

                        NotificationUtil.cancelAll();
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }, "");

            } else {
                ThirdPartyLogin.getInstance(mContext).loginOut(app.loginUser.thirdParty);
                app.removeToken();
                btnLogout.setVisibility(View.INVISIBLE);
                app.sendMessage(Const.LOGOUT_SUCCESS, null);
                app.sendMsgToTarget(Const.SWITCH_TAB, null, DefaultPageActivity.class);
                NotificationUtil.cancelAll();
                finish();
            }

            M3U8DownService service = M3U8DownService.getService();
            if (service != null) {
                service.cancelAllDownloadTask();
            }
        }
    };

    private long getCacheSize(File workSpace) {
        long totalSize = 0;
        for (File file : workSpace.listFiles()) {
            if (CommonUtil.inArray(file.getName(), new String[]{"videos", "appZip"})) {
                continue;
            }

            if (!file.isDirectory()) {
                totalSize = totalSize + file.length();
            } else {
                totalSize = totalSize + getCacheSize(file);
            }
        }
        return totalSize;
    }

    private void clearCache() {
        deleteFile(app.getWorkSpace());
        mContext.deleteDatabase("webview.db");
        mContext.deleteDatabase("webviewCache.db");

        SqliteUtil.getUtil(mContext).delete("lesson_resource", "", null);

        float size = getCacheSize(app.getWorkSpace()) / 1024.0f / 1024.0f;
        if (size == 0) {
            tvCache.setText("0M");
        } else {
            tvCache.setText(String.format("%.1f%s", size, "M"));
        }

        app.sendMessage(Const.CLEAR_APP_CACHE, null);
    }

    private void deleteFile(File workSpace) {
        for (File file : workSpace.listFiles()) {
            if (file.getName().equals("videos")) {
                continue;
            }
            if (file.isDirectory()) {
                deleteFile(file);
                file.delete();
            } else {
                file.delete();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
