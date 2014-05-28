package com.edusohoapp.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusohoapp.app.R;
import com.edusohoapp.app.entity.QRResult;
import com.edusohoapp.app.entity.RecommendSchoolItem;
import com.edusohoapp.app.entity.TokenResult;
import com.edusohoapp.app.util.AppUtil;
import com.edusohoapp.app.util.Const;
import com.edusohoapp.app.view.LoadDialog;
import com.edusohoapp.app.view.PopupDialog;
import com.edusohoapp.listener.ResultCallback;
import com.edusohoapp.plugin.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class QrSchoolActivity extends BaseActivity {

    private Button qr_search_btn;
    private View normal_login_btn;

    public final static int REQUEST_QR = 0001;
    public final static int RESULT_QR = 0002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrsch_layout);
        initView();
        app.addTask("QrSchoolActivity", this);
    }

    public static void start(Activity context) {
        Intent intent = new Intent();
        intent.setClass(context, QrSchoolActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        normal_login_btn = findViewById(R.id.normal_login_btn);
        qr_search_btn = (Button) findViewById(R.id.qr_search_btn);
        qr_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrIntent = new Intent();
                qrIntent.setClass(mContext, CaptureActivity.class);
                startActivityForResult(qrIntent, REQUEST_QR);
            }
        });

        normal_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetSchoolActivity.start(mActivity);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            app.exit();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_QR && resultCode == RESULT_QR) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                int status = bundle.getInt("status");
                String result = bundle.getString("result");
                System.out.println(result);
                showQrResultDlg(result);
            }
        }
    }

    private void showQrResultDlg(final String result)
    {
        PopupDialog.createMuilt(
                mContext,
                "扫描结果",
                "二维码信息:" + result,
                new PopupDialog.PopupClickListener() {
                @Override
                public void onClick(int button) {
                    if (button == PopupDialog.OK) {
                        ajaxGetString(result, new ResultCallback() {
                            @Override
                            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                                super.callback(url, object, ajaxStatus);
                                TokenResult result = app.gson.fromJson(
                                        object, new TypeToken<TokenResult>() {
                                }.getType());

                                if (result != null) {
                                    app.saveToken(result);
                                    app.setCurrentSchool(result.site);
                                    Intent intent = new Intent();
                                    intent.setClass(mContext, SchCourseActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }
        }).show();
    }
}