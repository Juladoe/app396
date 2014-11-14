package com.edusoho.kuozhi.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.handler.ClientVersionHandler;
import com.edusoho.kuozhi.AppConfig;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.Service.EdusohoMainService;
import com.edusoho.kuozhi.core.CoreEngine;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.*;
import com.edusoho.kuozhi.ui.common.QrSchoolActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.edusoho.listener.StatusCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by howzhi on 14-8-6.
 */
public class ActionBarBaseActivity extends ActionBarActivity {

    private static final String TAG = "ActionBarBaseActivity";
    public static final String BACK = "返回";
    protected ActionBarBaseActivity mActivity;
    protected Context mContext;
    public EdusohoApp app;
    public ActionBar mActionBar;
    protected CoreEngine mCoreEngine;
    public Gson gson;
    protected FragmentManager mFragmentManager;
    private TextView mTitleTextView;

    protected EdusohoMainService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
        initActivity();
    }

    private void initActivity()
    {
        app = (EdusohoApp) getApplication();
        app.setDisplay(this);
        gson = app.gson;
        mCoreEngine = app.mEngine;
        mService = app.getService();
        mActionBar = getSupportActionBar();
        mFragmentManager = getSupportFragmentManager();
        setProgressBarIndeterminateVisibility(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public EdusohoMainService getService()
    {
        return mService;
    }

    public <T> T parseJsonValue(String json, TypeToken<T> typeToken)
    {
        T value = null;
        try {
            value = mActivity.gson.fromJson(
                    json, typeToken.getType());
        }catch (Exception e) {
            e.printStackTrace();
            return value;
        }

        return value;
    }

    public CoreEngine getCoreEngine()
    {
        return mCoreEngine;
    }

    public Bitmap getBitmap(int redId)
    {
        return app.query.getCachedImage(redId);
    }

    public void runService(String serviceName)
    {
        app.mEngine.runService(serviceName, mActivity, null);
    }

    public void log(String format, String... strs)
    {
        if (EdusohoApp.debug) {
            System.out.println(String.format(format, strs));
        }
    }

    public void enableBtn(ViewGroup vg, boolean isEnable)
    {
        vg.setEnabled(isEnable);
        int count = vg.getChildCount();
        for (int i=0; i < count; i++) {
            vg.getChildAt(i).setEnabled(isEnable);
        }
    }

    public void hideActionBar()
    {
        mActionBar.hide();
    }

    public void showActionBar()
    {
        mActionBar.show();
    }

    public void setTitle(String title)
    {
        mTitleTextView.setText(title == null ? "" : title);
    }

    public void setNormalActionBack(String title)
    {
        mActionBar.setDisplayShowCustomEnabled(false);
        mActionBar.setTitle(title);
    }

    public void setBackMode(String backTitle, String title)
    {
        mTitleTextView = (TextView) getLayoutInflater().inflate(R.layout.actionbar_custom_title, null);
        mTitleTextView.setText(title);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        layoutParams.width = (int) (EdusohoApp.screenW * 0.6);
        layoutParams.gravity = Gravity.CENTER;

        mActionBar.setCustomView(mTitleTextView, layoutParams);

        if (backTitle != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            mActionBar.setIcon(R.drawable.action_bar_back);
            mActionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(null, "menu item->" + item.getItemId());
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public String wrapUrl(String url, String... params)
    {
        if (params.length > 0) {
            for (String param : params) {
                url = url.replaceFirst("\\{[\\w\\W^\\/]+\\}", param);
            }
        }
        return url;
    }

    public Gson getGson()
    {
        return app.gson;
    }

    public void saveCurrentSchool(School school) {
        app.setCurrentSchool(school);
    }

    private String splitTitle(String title)
    {
        int length = title.length();
        if (length > 10) {
            return title.substring(0, 10) + "...";
        }
        return title;
    }

    public boolean checkMobileVersion(
            HashMap<String, String> versionRange, ClientVersionHandler handler)
    {
        String min = versionRange.get("min");
        String max = versionRange.get("max");

        System.out.println("version->" + app.apiVersion);
        int result = AppUtil.compareVersion(app.apiVersion, min);
        if (handler != null) {
            return handler.execute(min, max, app.apiVersion);
        }

        if (result == Const.LOW_VERSIO) {
            PopupDialog popupDialog = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "您的客户端版本过低，无法登录该网校，请立即更新至最新版本。\n或选择其他网校",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                String code = getResources().getString(R.string.app_code);
                                String updateUrl = String.format(
                                        "%s%s?code=%s",
                                        app.schoolHost,
                                        Const.DOWNLOAD_URL,
                                        code
                                );
                                app.startUpdateWebView(updateUrl);
                            } else {
                                QrSchoolActivity.start(mActivity);
                                finish();
                            }
                        }
                    });
            popupDialog.setCancelText("选择新网校");
            popupDialog.setOkText("立即下载");
            popupDialog.show();
            return false;
        }

        result = AppUtil.compareVersion(app.apiVersion, max);
        if (result == Const.HEIGHT_VERSIO) {
            PopupDialog popupDialog = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "网校服务器版本过低，无法继续登录！请重新尝试。\n或选择其他网校",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                QrSchoolActivity.start(mActivity);
                                finish();
                            }
                        }
                    });

            popupDialog.setOkText("选择新网校");
            popupDialog.show();
            return false;
        }

        return true;
    }

    protected void showEmptyLayout(final String text)
    {
        ViewStub emptyLayout = (ViewStub) findViewById(R.id.list_empty_stub);
        if (emptyLayout == null) {
            return;
        }
        emptyLayout.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub viewStub, View view) {
                TextView emptyText = (TextView) view.findViewById(R.id.list_empty_text);
                emptyText.setText(text);
            }
        });
        emptyLayout.inflate();
    }

    protected void showErrorLayout(final String text, final ListErrorListener listener)
    {
        ViewStub errorLayout = (ViewStub) findViewById(R.id.list_error_layout);
        errorLayout.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub viewStub, View view) {
                TextView errorText = (TextView) view.findViewById(R.id.list_error_text);
                errorText.setText(text);
                View errorBtn = view.findViewById(R.id.list_error_btn);
                errorBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.error(view);
                    }
                });
            }
        });
        errorLayout.inflate();
        return;
    }

    public void setMenu(int menuViewRes, MenuListener listener)
    {
        LinearLayout menuLayout = (LinearLayout) findViewById(R.id.actionbar_menu);
        View menuView = getLayoutInflater().inflate(menuViewRes, null);
        menuLayout.addView(menuView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        menuView.setLayoutParams(lp);
        listener.bind(menuView);
    }

    public static interface MenuListener {
        public void bind(View menuView);
    }

    public void longToast(String title)
    {
        Toast.makeText(mContext, title, Toast.LENGTH_SHORT).show();
    }

    public void ajax(String url, ResultCallback rcl, boolean showLoading)
    {
        if (showLoading) {
            ajaxGetString(url, rcl);
        } else {
            ajaxNormalGet(url, rcl);
        }
    }

    public void ajaxGetString(
            String url, final ResultCallback rcl)
    {
        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();
        app.queryUrl(url, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                if (loading != null) {
                    loading.dismiss();
                }
                int code = status.getCode();
                if (handlerError(object)) {
                    return;
                }

                if (code != Const.OK) {
                    longToast("服务器访问异常！");
                    rcl.error(url, status);
                    return;
                }
                try {
                    rcl.callback(url,object,status);
                }catch (Exception e) {
                    e.printStackTrace();
                    rcl.error(url, status);
                }
            }
        });
    }

    public void ajaxPostByLoding(
            RequestUrl url, final ResultCallback rcl)
    {
        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();
        app.postUrl(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                if (loading != null) {
                    loading.dismiss();
                }
                if (handleRequest(url, object, status, rcl)){
                    return;
                }
                try {
                    rcl.callback(url,object,status);
                }catch (Exception e) {
                    rcl.error(url, status);
                }
            }
        });
    }

    private boolean handleRequest(
            String url, String object, AjaxStatus status, ResultCallback rcl)
    {
        int code = status.getCode();
        if (code == Const.CACHE_CODE) {
            rcl.callback(url, object, status);
            return true;
        }

        if (!app.getNetStatus()) {
            longToast("没有网络服务！请检查网络设置。");
            rcl.error(url, status);
            return true;
        }

        if (code != Const.OK) {
            longToast("服务器访问异常!");
            rcl.error(url, status);
            return true;
        }

        if (handlerError(object)) {
            rcl.error(url, status);
            return true;
        }

        return false;
    }

    public void ajaxPostMuiltKeys(RequestUrl url, final ResultCallback rcl)
    {
        app.postByMuiltKeys(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                if (handleRequest(url, object, status, rcl)) {
                    return;
                }
                try {
                    rcl.callback(url, object, status);
                } catch (Exception e) {
                    rcl.error(url, status);
                }
            }

            @Override
            public void update(String url, String object, AjaxStatus status) {
                handleRequest(url, object, status, rcl);
                try {
                    rcl.update(url, object, status);
                } catch (Exception e) {
                    rcl.error(url, status);
                }
            }
        });
    }

    public void ajaxGet(RequestUrl url, final ResultCallback rcl)
    {
        app.getUrl(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                if (handleRequest(url, object, status, rcl)){
                    return;
                }
                try {
                    rcl.callback(url,object,status);
                }catch (Exception e) {
                    rcl.error(url, status);
                }
            }

            @Override
            public void update(String url, String object, AjaxStatus status) {
                handleRequest(url, object, status, rcl);
                try {
                    rcl.update(url,object,status);
                }catch (Exception e) {
                    rcl.error(url, status);
                }
            }
        });
    }

    public void ajaxPost(
            RequestUrl url, final ResultCallback rcl)
    {
        app.postUrl(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                if (handleRequest(url, object, status, rcl)){
                    return;
                }
                try {
                    rcl.callback(url,object,status);
                }catch (Exception e) {
                    rcl.error(url, status);
                }
            }

            @Override
            public void update(String url, String object, AjaxStatus status) {
                handleRequest(url, object, status, rcl);
                try {
                    rcl.update(url,object,status);
                }catch (Exception e) {
                    rcl.error(url, status);
                }
            }
        });
    }

    public void ajaxNormalGet(String url, final ResultCallback rcl)
    {
        app.queryUrl(url, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                int code = status.getCode();
                if (handlerError(object)) {
                    return;
                }
                if (code != Const.OK) {
                    longToast("服务器访问异常！");
                    rcl.error(url, status);
                    return;
                }
                try {
                    rcl.callback(url,object,status);
                }catch (Exception e) {
                    rcl.error(url, status);
                }
            }
        });
    }

    private boolean handlerError(String errorStr)
    {
        try {
            ErrorResult result = app.gson.fromJson(
                    errorStr, new TypeToken<ErrorResult>() {
            }.getType());
            if (result != null) {
                com.edusoho.kuozhi.model.Error error = result.error;
                if (Const.CLIENT_CLOSE.equals(error.name)) {
                    PopupDialog popupDialog = PopupDialog.createMuilt(
                            mContext,
                            "系统提示",
                            error.message,
                            new PopupDialog.PopupClickListener() {
                                @Override
                                public void onClick(int button) {
                                    if (button == PopupDialog.OK) {
                                        QrSchoolActivity.start(mActivity);
                                        finish();
                                    }
                                }
                            });
                    popupDialog.setOkText("选择新网校");
                    popupDialog.show();
                    return true;
                }
                longToast(result.error.message);
                return true;
            }
        } catch (Exception e) {
            //result error
        }
        return false;
    }

    public void removeSchoolItem()
    {
        SharedPreferences sp = getSharedPreferences(Const.DEFAULT_SCHOOL, MODE_PRIVATE);
        sp.edit().clear().commit();
    }

    /**
     * @suju
     */
    public interface ListErrorListener
    {
        public void error(View errorBtn);
    }

    protected void updateApp()
    {
        app.updateApp("http://open.edusoho.com/mobile/meta.php", false, new NormalCallback() {
            @Override
            public void success(Object obj) {
                final AppUpdateInfo appUpdateInfo = (AppUpdateInfo) obj;
                String newVersion = appUpdateInfo.androidVersion;
                int result = AppUtil.compareVersion(app.getApkVersion(), newVersion);
                if (result == Const.LOW_VERSIO) {
                    PopupDialog.createMuilt(
                            mActivity,
                            "版本更新",
                            "当前有新版本，是否更新?", new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                app.startUpdateWebView(appUpdateInfo.updateUrl);
                            }
                        }
                    }).show();
                }
            }
        });
    }
}
