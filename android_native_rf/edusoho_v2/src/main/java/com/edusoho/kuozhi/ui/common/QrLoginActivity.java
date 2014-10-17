package com.edusoho.kuozhi.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.fragment.MyInfoFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.plugin.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14-10-17.
 */
public class QrLoginActivity extends ActionBarBaseActivity {

    public final static int REQUEST_QR = 0001;
    public final static int RESULT_QR = 0002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new View(mContext);
        setContentView(view);
        setBackMode(BACK, "扫描登录用户");

        startQrSearch();
    }

    private void startQrSearch()
    {
        Intent qrIntent = new Intent();
        qrIntent.putExtra(Const.ACTIONBAT_TITLE, "扫描登录用户");
        qrIntent.setClass(mContext, CaptureActivity.class);
        startActivityForResult(qrIntent, REQUEST_QR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_QR && resultCode == RESULT_QR) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                Log.d(null, "qr->" + result + "&version=2");
                showQrResultDlg(result + "&version=2");
            }
        } else {
            exit();
        }
    }

    private void showQrResultDlg(final String result)
    {
        if (!result.startsWith(app.host)) {
            longToast("请登录" + app.defaultSchool.name + "－网校！");
            exit();
            return;
        }

        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();
        app.query.ajax(result, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                loading.dismiss();
                int code = status.getCode();
                if (code != Const.OK) {
                    longToast("二维码信息错误!");
                    exit();
                    return;
                }
                try {
                    final TokenResult schoolResult = app.gson.fromJson(
                            object, new TypeToken<TokenResult>() {
                    }.getType());

                    if (schoolResult == null) {
                        longToast("二维码信息错误!");
                        exit();
                        return;
                    }

                    if (schoolResult.token == null || "".equals(schoolResult.token)) {
                        longToast("二维码登录信息已过期或失效!");
                    } else {
                        app.saveToken(schoolResult);
                        app.sendMessage(Const.LOGING_SUCCESS, null);
                        app.sendMsgToTarget(MyInfoFragment.REFRESH, null, MyInfoFragment.class);
                    }

                }catch (Exception e) {
                    longToast("二维码信息错误!");
                }
                exit();
            }
        });

    }

    private void exit()
    {
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }
}
