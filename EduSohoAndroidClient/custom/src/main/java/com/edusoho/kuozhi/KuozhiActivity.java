package com.edusoho.kuozhi;


import android.os.Bundle;
import com.androidquery.callback.AjaxStatus;
import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.SchoolResult;
import com.edusoho.kuozhi.model.SystemInfo;
import com.edusoho.kuozhi.ui.StartActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

public class KuozhiActivity extends StartActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initChannel();
    }

    @Override
    protected void initApp() {
        if (!app.getNetIsConnect()) {
            longToast("没有网络服务！请检查网络设置。");
            return;
        }
        String host = getResources().getString(R.string.app_host);
        checkSchoolApiVersion(host);
    }

    @Override
    protected void checkSchoolVersion(SystemInfo info) {
        ajaxNormalGet(info.mobileApiUrl + Const.VERIFYSCHOOL, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                SchoolResult schoolResult = app.gson.fromJson(
                        object, new TypeToken<SchoolResult>() {
                        }.getType());

                if (schoolResult == null) {
                    showSchoolErrorDlg();
                    return;
                }
                School site = schoolResult.site;
                CustomClientVersionHandler handler = new CustomClientVersionHandler(mActivity);
                if (!checkMobileVersion(site.apiVersionRange, handler)) {
                    return;
                }

                app.setCurrentSchool(site);
                startApp();
            }
        });
    }

    @Override
    protected void showSchoolErrorDlg() {
        PopupDialog.createNormal(
                mContext,
                "提示信息",
                "网校客户端已关闭或网校服务器出现异常"
        ).show();
    }

    @Override
    protected void startApp() {
        if (app.config.startWithSchool && app.defaultSchool != null) {
            app.mEngine.runNormalPlugin("DefaultPageActivity", this, null);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            return;
        }
    }

    private void initChannel() {
        //AppKey不用改
        StatService.setAppKey("8f1996ac26");
        //定制项目，app_name就是渠道号
        StatService.setAppChannel(this, getString(R.string.channel_name), true);
        StatService.setSessionTimeOut(30);
        StatService.setOn(this, StatService.EXCEPTION_LOG);
        StatService.setLogSenderDelayed(0);
        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 0);
        StatService.setDebugOn(false);
    }
}
