package com.edusoho.kuozhi;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.core.CoreEngine;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.model.AppUpdateInfo;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.SqliteUtil;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.listener.NormalCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EdusohoApp extends Application{

    public AppConfig config;
    public AQuery query;
    public String host;
    public Gson gson;
    public SqliteUtil sqliteUtil;
    public School defaultSchool;
    public User loginUser;
    public String apiVersion;
    public String schoolHost = "";

    public CoreEngine mEngine;

    public String token;

    public static HashMap<String, Activity> runTask;

    public static int screenW;
    public static int screenH;

    public static EdusohoApp app;
    public static boolean debug = true;
    public static final String PLUGIN_CONFIG = "plugin_config";
    public static final String INSTALL_PLUGIN = "install_plugin";

    private android.os.Handler mWorkHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mWorkHandler = new android.os.Handler();
        init();
    }

    public static void log(String msg)
    {
        if (EdusohoApp.debug) {
            System.out.println(msg);
        }
    }

    public void exit()
    {
        for (Activity activity : runTask.values()) {
            activity.finish();
        }
        System.exit(0);
    }

    private void init()
    {
        Log.i(null, "init");
        app = this;
        gson = new Gson();
        apiVersion = "1.0.0";
        query = new AQuery(this);
        host = getString(R.string.host);
        sqliteUtil = new SqliteUtil(getApplicationContext(), null, null);
        initWorkSpace();
        loadConfig();

        mEngine = CoreEngine.create(this);
        installPlugin();
    }

    private void installPlugin()
    {
        final SharedPreferences sp = getSharedPreferences(PLUGIN_CONFIG, MODE_APPEND);
        if (sp.contains(INSTALL_PLUGIN)) {
            return;
        }
        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                mEngine.installApkPlugin();
                sp.edit().putBoolean(INSTALL_PLUGIN, true).commit();
            }
        });
    }

    private double getApkVersion()
    {
        double version = 0.0;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionStr = packageInfo.versionName;
            version = Double.parseDouble(versionStr);
        } catch (Exception e) {
            version = 0.0;
        }
        return version;
    }

    public void setCurrentSchool(School school)
    {
        app.defaultSchool = school;
        app.schoolHost = school.url + "/";

        SharedPreferences sp = getSharedPreferences("defaultSchool", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("name", school.name);
        edit.putString("url", school.url);
        edit.putString("logo", school.logo);
        edit.commit();
    }

    private void loadDefaultSchool()
    {
        SharedPreferences sp = getSharedPreferences("defaultSchool", MODE_PRIVATE);
        Map<String, String> map = (Map<String, String>) sp.getAll();
        if (!map.isEmpty()) {
            School item = new School();
            item.name = map.get("name");
            item.url = map.get("url");
            item.logo = map.get("logo");
            setCurrentSchool(item);
        }
    }

    private void loadConfig()
    {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        config = new AppConfig();
        config.showSplash = sp.getBoolean("showSplash", true);
        config.startWithSchool = sp.getBoolean("startWithSchool", true);
        if (config.startWithSchool) {
            loadDefaultSchool();
        }

        loadToken();
    }

    private void loadToken()
    {
        SharedPreferences sp = getSharedPreferences("token", MODE_PRIVATE);
        token = sp.getString("token", "");
    }

    public void saveToken(TokenResult result)
    {
        SharedPreferences sp = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor edit =  sp.edit();
        edit.putString("token", result.token);
        edit.commit();

        token = result.token;
        loginUser = result.user;
    }

    public void removeToken()
    {
        SharedPreferences sp = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor edit =  sp.edit();
        edit.putString("token", "");
        edit.commit();

        token = null;
        loginUser = null;
    }

    public boolean taskIsRun(String name)
    {
        Activity activity = runTask.get(name);
        return activity != null;
    }

    public void addTask(String name, Activity activity)
    {
        runTask.put(name, activity);
    }

    public void saveConfig()
    {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("showSplash", config.showSplash);
        edit.putBoolean("startWithSchool", config.startWithSchool);
        edit.commit();
    }

    private void initWorkSpace()
    {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File sdcard = Environment.getExternalStorageDirectory();
            File workSpace = new File(sdcard, "edusoho");
            if (! workSpace.exists()) {
                workSpace.mkdir();
            }
            AQUtility.setCacheDir(new File(workSpace, "cache"));
        } else {
            Toast.makeText(this, "设备没有内存卡,数据将保存在手机内存中！", Toast.LENGTH_LONG).show();
        }
        loadCustomBtn();
        runTask = new HashMap<String, Activity>();
    }

    public void setDisplay(Activity activity)
    {
        Display display = activity.getWindowManager().getDefaultDisplay();
        screenH = display.getHeight();
        screenW = display.getWidth();
    }

    public String bindToken2Url(String url, boolean addToken)
    {
        StringBuffer sb = new StringBuffer(app.schoolHost);
        sb.append(url);
        if (addToken) {
            sb.append("&token=").append(token);
        }
        return sb.toString();
    }

    public void checkToken()
    {
        String url = bindToken2Url(Const.CHECKTOKEN, true);
        query.ajax(url, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                TokenResult result = app.gson.fromJson(
                        object, new TypeToken<TokenResult>(){}.getType());
                if (result != null) {
                    saveToken(result);
                }
            }
        });
    }

    public static int tabLeftBtnSel;
    public static int tabRightBtnSel;

    public static int popLeftBtnSel;
    public static int popRightBtnSel;

    private void loadCustomBtn()
    {
        int version = Build.VERSION.SDK_INT;
        if ((version >= 8) && (version <= 10)) {
            popRightBtnSel = R.drawable.popup_right_10_btn;
            popLeftBtnSel = R.drawable.popup_left_10_btn;
            tabLeftBtnSel = R.drawable.course_tab_left_10_sel;
            tabRightBtnSel = R.drawable.course_tab_right_10_sel;
        } else {
            popRightBtnSel = R.drawable.popup_right_btn;
            popLeftBtnSel = R.drawable.popup_left_btn;
            tabRightBtnSel = R.drawable.course_tab_right_sel;
            tabLeftBtnSel = R.drawable.course_tab_left_sel;
        }
    }

    public void startUpdateWebView(String url)
    {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    private boolean mIsNotifyUpdate;

    public void updateApp(boolean isShowLoading, final NormalCallback callback)
    {
        if (mIsNotifyUpdate) {
            return;
        }
        mIsNotifyUpdate = true;

        final LoadDialog loadDialog = LoadDialog.create(this);
        if (isShowLoading) {
            loadDialog.show();
        }

        query.ajax(
                "http://open.edusoho.com/mobile/meta.php",
                String.class,
                new AjaxCallback<String>(){
                    @Override
                    public void callback(String url, String object, AjaxStatus status) {
                        loadDialog.dismiss();
                        super.callback(url, object, status);
                        final AppUpdateInfo appUpdateInfo = app.gson.fromJson(
                                object, new TypeToken<AppUpdateInfo>(){}.getType());

                        if (appUpdateInfo == null || appUpdateInfo.androidVersion == null) {
                            return;
                        }

                        callback.success(appUpdateInfo);
                    }
        });
    }
}
