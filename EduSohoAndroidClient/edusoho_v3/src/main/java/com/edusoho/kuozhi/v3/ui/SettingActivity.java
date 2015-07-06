package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.shard.ThirdPartyLogin;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.FragmentNavigationDrawer;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;

import java.io.File;

/**
 * Created by JesseHuang on 15/5/6.
 */
public class SettingActivity extends ActionBarBaseActivity {
    private View viewScan;
    private View tvMsgNotify;
    private View tvOnlineDuration;
    private View tvAbout;
    private View viewClearCache;
    private TextView tvCache;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setBackMode(BACK, "设置");
        initView();
    }

    private void initView() {
        viewScan = findViewById(R.id.linear_scan);
        viewScan.setOnClickListener(scanClickListener);
        tvMsgNotify = findViewById(R.id.tvMsgNotify);
        tvMsgNotify.setOnClickListener(msgClickListener);
        tvOnlineDuration = findViewById(R.id.tvOnlineDuration);
        tvOnlineDuration.setOnClickListener(onlineDurationClickListener);
        tvAbout = findViewById(R.id.tvAbout);
        tvAbout.setOnClickListener(aboutClickListener);
        tvCache = (TextView) findViewById(R.id.tv_cache);
        viewClearCache = findViewById(R.id.rl_clear_cache);
        viewClearCache.setOnClickListener(clearCacheListener);
        float size = getCacheSize(app.getWorkSpace()) / 1024.0f / 1024.0f;
        if (size == 0) {
            tvCache.setText("0M");
        } else {
            tvCache.setText(String.format("%.1f%s", size, "M"));
        }
        btnLogout = (Button) findViewById(R.id.setting_logout_btn);
        btnLogout.setOnClickListener(logoutClickLister);
        if (app.loginUser != null) {
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            btnLogout.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener clearCacheListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearCache();
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

    private View.OnClickListener onlineDurationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.app.mEngine.runNormalPlugin("OnlineDurationActivity", mActivity, null);
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
                        app.sendMsgToTarget(Const.MAIN_MENU_CLOSE, null, FragmentNavigationDrawer.class);
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
                app.sendMsgToTarget(Const.MAIN_MENU_CLOSE, null, FragmentNavigationDrawer.class);
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
            if (file.getName().equals("videos")) {
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
        Log.d("setting--->", "finish");
        super.finish();
    }
}
