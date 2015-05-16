package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.TokenResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by JesseHuang on 15/5/6.
 * 扫描网校界面
 */
public class QrSchoolActivity extends ActionBarBaseActivity {
    private Button mQrSearchBtn;
    public final static int REQUEST_QR = 0001;
    public final static int RESULT_QR = 0002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_school);
        setBackMode(BACK, "进入网校");
        initView();
    }

    private void initView() {
        mQrSearchBtn = (Button) findViewById(R.id.qr_search_btn);
        mQrSearchBtn.setOnClickListener(mSearchClickListener);
    }

    private View.OnClickListener mSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent qrIntent = new Intent();
            qrIntent.setClass(QrSchoolActivity.this, CaptureActivity.class);
            startActivityForResult(qrIntent, REQUEST_QR);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_QR && resultCode == RESULT_QR) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                Log.d("qr:", "qr->" + result + "&version=2");
                showQrResultDlg(result + "&version=2");
            }
        }
    }

    private void showQrResultDlg(final String result) {
        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();

        RequestUrl requestUrl = new RequestUrl(result);
        ajaxGet(requestUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
                try {
                    final TokenResult schoolResult = app.gson.fromJson(
                            response.toString(), new TypeToken<TokenResult>() {
                            }.getType());

                    if (schoolResult == null) {
                        CommonUtil.longToast(mActivity, "二维码信息错误!");
                        return;
                    }

                    Log.d(null, "token---->" + schoolResult.token);
                    School site = schoolResult.site;
                    if (!checkMobileVersion(site, site.apiVersionRange)) {
                        return;
                    }

                    if (schoolResult.token == null || "".equals(schoolResult.token)) {
                        app.removeToken();
                    } else {
                        app.saveToken(schoolResult);
                    }
                    app.setCurrentSchool(site);
                    app.sendMessage(Const.LOGING_SUCCESS, null);


                    //showSchSplash(site.name, site.splashs);
                    Log.d("QrCode-->", result);
                    CommonUtil.longToast(mActivity, result);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                    CommonUtil.longToast(mActivity, "二维码信息错误!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                CommonUtil.longToast(mActivity, "二维码信息错误!");
            }
        });
    }

    public boolean checkMobileVersion(final School site, HashMap<String, String> versionRange) {
        String min = versionRange.get("min");
        String max = versionRange.get("max");

        int result = AppUtil.compareVersion(app.apiVersion, min);
        if (result == Const.LOW_VERSIO) {
            PopupDialog dlg = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "您的客户端版本过低，无法登录该网校，请立即更新至最新版本。",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                String code = getResources().getString(R.string.app_code);
                                String updateUrl = String.format(
                                        "%s/%s?code=%s",
                                        site.url,
                                        Const.DOWNLOAD_URL,
                                        code
                                );
                                app.startUpdateWebView(updateUrl);
                            }
                        }
                    });

            dlg.setOkText("立即下载");
            dlg.show();
            return false;
        }

        result = AppUtil.compareVersion(app.apiVersion, max);
        if (result == Const.HEIGHT_VERSIO) {
            PopupDialog.createNormal(
                    mContext,
                    "网校提示",
                    "网校服务器版本过低，无法继续登录！请重新尝试。"
            ).show();
            return false;
        }

        return true;
    }
}
