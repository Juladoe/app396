package com.edusoho.kuozhi.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.TokenResult;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.fragment.LoginFragment;
import com.edusoho.kuozhi.ui.fragment.RegistFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class LoginActivity extends ActionBarBaseActivity {
    public static final int EXIT = 1002;
    public static final int LOGIN = 1001;
    public static final int OK = 1003;
    public static final int LOGIN_TYPE = 1010;
    public static final int REGIST_TYPE = 1011;

    public static final String FRAGMENT_TYPE = "fragment_type";
    private int mFramgmentType = LOGIN_TYPE;
    private Handler workHandler;
    private static boolean isRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initView();
    }

    public static void start(Activity context)
    {
        synchronized (context) {
            if (isRun) {
                return;
            }
            isRun = true;
            Intent intent = new Intent();
            intent.setClass(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }

    public static void startForResult(Activity context)
    {
        if (isRun) {
            return;
        }
        isRun = true;
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        context.startActivityForResult(intent, LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case QrSchoolActivity.REQUEST_QR:
                if (resultCode == QrSchoolActivity.RESULT_QR && data != null) {
                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");
                    showQrResultDlg(result);
                }
                break;
        }
    }

    private void showQrResultDlg(final String result)
    {
        if (!result.startsWith(app.host)) {
            longToast("请登录" + getString(R.string.app_name) + "－网校！");
            return;
        }

        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();
        app.query.ajax(result, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                int code = status.getCode();
                if (code != Const.OK) {
                    loading.dismiss();
                    longToast("二维码信息错误!");
                    return;
                }
                try {
                    final TokenResult schoolResult = app.gson.fromJson(
                            object, new TypeToken<TokenResult>() {
                    }.getType());
                    if (schoolResult == null) {
                        loading.dismiss();
                        longToast("二维码信息错误!");
                        return;
                    }

                    final School site = schoolResult.site;

                    if (schoolResult.token == null || "".equals(schoolResult.token)) {
                        loading.dismiss();
                        app.removeToken();
                        longToast("二维码登录信息已过期或失效!");
                    } else {
                        workHandler.postAtTime(new Runnable() {
                            @Override
                            public void run() {
                                loading.dismiss();
                                app.saveToken(schoolResult);
                                app.setCurrentSchool(site);
                                setResult(EXIT);
                                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                finish();
                            }
                        }, SystemClock.uptimeMillis() + 500);

                    }
                }catch (Exception e) {
                    loading.dismiss();
                    longToast("二维码信息错误!");
                }
            }
        });
    }

    private void initView() {
        Intent data = getIntent();
        if (data != null) {
            mFramgmentType = data.getIntExtra(FRAGMENT_TYPE, LOGIN_TYPE);
        }

        setBackMode(BACK, mFramgmentType == LOGIN_TYPE ? "登录" : "注册");

        String tag = null;
        switch (mFramgmentType) {
            case LOGIN_TYPE:
                tag = LoginFragment.TAG;
                break;
            case REGIST_TYPE:
                tag = RegistFragment.TAG;
                break;
        }
        showFragment(tag);
    }

    public void showFragment(String tag)
    {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        BaseFragment fragment = (BaseFragment) app.mEngine.runPluginWithFragment(tag, mActivity, null);
        fragmentTransaction.replace(R.id.login_container, fragment);
        List<Fragment> fragmentList =  mFragmentManager.getFragments();
        if (fragmentList != null && ! fragmentList.isEmpty()) {
            fragmentTransaction.addToBackStack(tag);
        }

        fragmentTransaction.commit();
        setTitle(fragment.getTitle());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
    }
}
