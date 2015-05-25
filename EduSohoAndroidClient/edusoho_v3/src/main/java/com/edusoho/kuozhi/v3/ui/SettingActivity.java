package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.sys.MetaResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

/**
 * Created by JesseHuang on 15/5/6.
 */
public class SettingActivity extends ActionBarBaseActivity {
    private View viewScan;
    private View tvMsgNotify;
    private View tvOnlineDuration;
    private View tvAbout;
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

        btnLogout = (Button) findViewById(R.id.setting_logout_btn);
        btnLogout.setOnClickListener(logoutClickLister);
        if (app.loginUser != null) {
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            btnLogout.setVisibility(View.INVISIBLE);
        }
    }

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
            RequestUrl requestUrl = app.bindUrl(Const.LOGOUT, true);
            mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    MetaResult metaResult = mActivity.parseJsonValue(response.toString(), new TypeToken<MetaResult>() {
                    });
                    if (metaResult.meta.code == Const.RESULT_CODE_SUCCESS) {
                        app.removeToken();
                        btnLogout.setVisibility(View.INVISIBLE);
                        app.sendMessage(Const.LOGOUT_SUCCESS, null);
                        CommonUtil.longToast(mContext, metaResult.meta.message);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

//            M3U8DownService service = M3U8DownService.getService();
//            if (service != null) {
//                service.cancelAllDownloadTask();
//            }
        }
    };
}
