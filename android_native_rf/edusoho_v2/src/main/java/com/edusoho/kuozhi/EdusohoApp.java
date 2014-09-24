package com.edusoho.kuozhi;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.Service.EdusohoMainService;
import com.edusoho.kuozhi.core.CacheAjaxCallback;
import com.edusoho.kuozhi.core.CoreEngine;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.listener.CoreEngineMsgCallback;
import com.edusoho.kuozhi.core.model.Cache;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.model.AppUpdateInfo;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.SqliteUtil;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.AjaxResultCallback;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.RequestParamsCallback;
import com.edusoho.listener.ResultCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private HashMap<String, Object> paramsMap;
    public static EdusohoApp app;
    public static boolean debug = true;
    public static final String PLUGIN_CONFIG = "plugin_config";
    public static final String INSTALL_PLUGIN = "install_plugin";

    private android.os.Handler mWorkHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(null, "create application");
        mWorkHandler = new android.os.Handler();
        init();
    }

    public static void log(String msg)
    {
        if (EdusohoApp.debug) {
            System.out.println(msg);
        }
    }

    public EdusohoMainService getService()
    {
        return EdusohoMainService.getService();
    }

    public AjaxCallback postUrl(
            final RequestUrl requestUrl, final AjaxResultCallback ajaxResultCallback)
    {
        Cache cache = mEngine.appCache.getCache(requestUrl);
        CacheAjaxCallback<String> ajaxCallback = new CacheAjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                super.callback(url, object, status);
                if (this.isCacheRequest()) {
                    mEngine.appCache.updateCache(requestUrl, object);
                    ajaxResultCallback.update(url, object, status);
                    return;
                }
                mEngine.appCache.setCache(requestUrl, object);
                ajaxResultCallback.callback(url, object, status);
            }
        };

        ajaxCallback.headers(requestUrl.heads);
        ajaxCallback.method(AQuery.METHOD_POST);

        if (cache != null) {
            Log.d(null, "get to cache->" + requestUrl.url);
            mEngine.appCache.cacheCallback(requestUrl.url, cache, ajaxCallback);
            ajaxCallback.setCacheRequest(true);
        }

        query.ajax(requestUrl.url, requestUrl.getAllParams(), String.class, ajaxCallback);
        return ajaxCallback;
    }

    public <T> void queryUrl(String url, Class<T> tClass, final AjaxCallback<T> ajaxCallback)
    {
        query.ajax(url, tClass, new AjaxCallback<T>(){
            @Override
            public void callback(String url, T object, AjaxStatus status) {
                ajaxCallback.callback(url, object, status);
            }
        });
    }

    public void setParame(String key, Object obj)
    {
        paramsMap.put(key, obj);
    }

    public Object getParame(String key)
    {
        return paramsMap.get(key);
    }

    public void addMessageListener(String msgId, CoreEngineMsgCallback callback)
    {
        mEngine.receiveMsg(msgId, callback);
    }

    public void registMsgSource(MessageEngine.MessageCallback messageCallback)
    {
        mEngine.registMsgSrc(messageCallback);
    }

    public void unRegistMsgSource(MessageEngine.MessageCallback messageCallback)
    {
        mEngine.unRegistMessageSource(messageCallback);
    }

    public ConcurrentHashMap<String, MessageEngine.MessageCallback> getSourceMap()
    {
        return mEngine.getMessageEngine().getSourceMap();
    }

    public void delMessageListener(String msgId)
    {
        mEngine.removeMsg(msgId);
    }

    public void sendMessage(String msgId, Bundle bundle)
    {
        mEngine.getMessageEngine().sendMsg(msgId, bundle);
    }

    public void sendMsgToTarget(int msgType, Bundle body, Class target)
    {
        mEngine.getMessageEngine().sendMsgToTaget(msgType, body, target);
    }

    public void exit()
    {
        for (Activity activity : runTask.values()) {
            activity.finish();
        }

        query.clear();
        paramsMap.clear();
        runTask.clear();
        System.exit(0);
    }

    private void init()
    {
        Log.i(null, "init");
        app = this;
        gson = new Gson();
        apiVersion = "2.0.0";
        query = new AQuery(this);
        host = getString(R.string.app_host);
        paramsMap = new HashMap<String, Object>();
        initWorkSpace();
        loadConfig();

        mEngine = CoreEngine.create(this);
        installPlugin();

        registDevice();
        startMainService();
    }

    public void startMainService()
    {
        app.mEngine.runService(EdusohoMainService.TAG, this, null);
    }

    public Map<String, String> getPlatformInfo()
    {
        Map<String, String> params = new HashMap<String, String>();
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        params.put("imei", telephonyManager.getDeviceId());
        params.put("platform", "Android " + Build.MODEL);
        params.put("version", Build.VERSION.SDK);
        params.put("screenresolution", displayMetrics.widthPixels + "x" + displayMetrics.heightPixels);
        params.put("kernel", Build.VERSION.RELEASE);

        return params;
    }

    public void logToServer(
            String url, Map<String, String> params, AjaxCallback<String> ajaxCallback)
    {
        if (ajaxCallback == null) {
            ajaxCallback = new AjaxCallback<String>();
        }
        app.query.ajax(url, params, String.class, ajaxCallback);
    }

    public void registDevice()
    {
        if (app.config.isRegistDevice) {
            return;
        }
        Map<String, String> params = getPlatformInfo();

        logToServer("http://open.edusoho.com/mobile/mobile_install_stat.php", params, null);
        logToServer(app.schoolHost + Const.REGIST_DEVICE, params, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                super.callback(url, object, status);
                Log.d(null, "regist device->" + object);
                try {
                    Boolean result = app.gson.fromJson(
                            object, new TypeToken<Boolean>() {
                    }.getType());

                    if (true == result) {
                        app.config.isRegistDevice = true;
                        app.saveConfig();
                    }
                } catch (Exception e) {
                    //none
                }
            }
        });
    }

    public boolean getNetStatus()
    {
        ConnectivityManager connManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    public String getPluginFile(String pluginName)
    {
        File file = mEngine.getPluginFile(pluginName);
        return file.getAbsolutePath();
    }

    public void installApk(String file)
    {
        if (file == null || "".equals(file)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);

        intent.setDataAndType(Uri.parse("file://" + file),
                "application/vnd.android.package-archive");
        this.startActivity(intent);
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

    public String getApkVersion()
    {
        String version = "0.0.0";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (Exception e) {
            version = "0.0.0";
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
        edit.putString("host", school.host);
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
            item.host = map.get("host");
            item.logo = map.get("logo");
            host = item.host;
            setCurrentSchool(item);
        }
    }

    private void loadConfig()
    {
        SharedPreferences sp = getSharedPreferences("config", MODE_APPEND);
        config = new AppConfig();
        config.showSplash = sp.getBoolean("showSplash", true);
        config.isRegistDevice = sp.getBoolean("registDevice", false);
        config.startWithSchool = sp.getBoolean("startWithSchool", true);
        if (config.startWithSchool) {
            loadDefaultSchool();
        }

        loadToken();
    }

    private void loadToken()
    {
        SharedPreferences sp = getSharedPreferences("token", MODE_APPEND);
        token = sp.getString("token", "");
    }

    public void saveToken(TokenResult result)
    {
        SharedPreferences sp = getSharedPreferences("token", MODE_APPEND);
        SharedPreferences.Editor edit =  sp.edit();
        edit.putString("token", result.token);
        edit.commit();

        token = result.token == null || "".equals(result.token) ? "" : result.token;
        loginUser = "".equals(token) ? null : result.user;
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

    public void removeTask(String name)
    {
        runTask.remove(name);
    }

    public void addTask(String name, Activity activity)
    {
        runTask.put(name, activity);
    }

    public void saveConfig()
    {
        SharedPreferences sp = getSharedPreferences("config", MODE_APPEND);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("showSplash", config.showSplash);
        edit.putBoolean("registDevice", config.isRegistDevice);
        edit.putBoolean("startWithSchool", config.startWithSchool);
        edit.commit();
    }

    public void query(String url, final ResultCallback callback, Activity mActivity)
    {
        if (!getNetStatus()) {
            PopupDialog.createNormal(
                    mActivity, "提示信息", "无网络,请检查网络和手机设置!").show();
            mActivity.finish();
            return;
        }

        query.ajax(url, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                super.callback(url, object, status);
                callback.callback(url, object, status);
            }
        });
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
        loadCustomBtnStyle();
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

    public HashMap<String, String> initParams(String[] arges)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        for (int i=0; i < arges.length; i = i + 2) {
            params.put(arges[i], arges[i+1]);
        }

        return params;
    }

    public HashMap<String, String> createParams(
            boolean addToken, RequestParamsCallback callback)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        if (callback != null) {
            callback.addParams(params);
        }

        if (addToken) {
            params.put("token", token);
        }
        return params;
    }

    public RequestUrl bindUrl(String url, boolean addToken)
    {
        StringBuffer sb = new StringBuffer(app.schoolHost);
        sb.append(url);
        RequestUrl requestUrl = new RequestUrl(sb.toString());

        if (addToken) {
            requestUrl.heads.put("token", token);
        }
        return requestUrl;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        AQUtility.cleanCacheAsync(this);
        BitmapAjaxCallback.clearCache();
    }

    public void checkToken()
    {
        synchronized (this) {
            if (loginUser != null) {
                return;
            }
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
    }

    public static int tabLeftBtnSel;
    public static int tabRightBtnSel;

    public static int popLeftBtnSel;
    public static int popRightBtnSel;

    private void loadCustomBtnStyle()
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

    public void updateApp(String url, boolean isShowLoading, final NormalCallback callback)
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
                url,
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
