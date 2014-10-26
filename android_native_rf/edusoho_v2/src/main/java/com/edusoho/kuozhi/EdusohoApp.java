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
import com.edusoho.handler.EduSohoUncaughtExceptionHandler;
import com.edusoho.kuozhi.Service.DownLoadService;
import com.edusoho.kuozhi.Service.EdusohoMainService;
import com.edusoho.kuozhi.core.CacheAjaxCallback;
import com.edusoho.kuozhi.core.CoreEngine;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.listener.CoreEngineMsgCallback;
import com.edusoho.kuozhi.core.model.Cache;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.TokenResult;
import com.edusoho.kuozhi.model.AppUpdateInfo;
import com.edusoho.kuozhi.model.MessageType;
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
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EdusohoApp extends Application {

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

    private HashMap<String, Bundle> notifyMap;

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
        EduSohoUncaughtExceptionHandler.initCaughtHandler(this);
        init();
    }

    public static void log(String msg) {
        if (EdusohoApp.debug) {
            System.out.println(msg);
        }
    }

    public EdusohoMainService getService() {
        return EdusohoMainService.getService();
    }

    public void postByMuiltKeys(
            final RequestUrl requestUrl, final AjaxResultCallback ajaxResultCallback
    ) {
        AjaxCallback<String> ajaxCallback = new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                super.callback(url, object, status);
                ajaxResultCallback.callback(url, object, status);
            }
        };

        ajaxCallback.headers(requestUrl.heads);
        ajaxCallback.method(AQuery.METHOD_POST);

        query.ajax(requestUrl.url, requestUrl.getKeysMap(), String.class, ajaxCallback);
    }

    public AjaxCallback postUrl(
            final RequestUrl requestUrl, final AjaxResultCallback ajaxResultCallback) {
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
        ajaxCallback.timeout(1000 * 10);
        ajaxCallback.method(AQuery.METHOD_POST);

        if (cache != null) {
            Log.d(null, "get to cache->" + requestUrl.url);
            mEngine.appCache.cacheCallback(requestUrl.url, cache, ajaxCallback);
            ajaxCallback.setCacheRequest(true);
        }

        query.ajax(requestUrl.url, requestUrl.getAllParams(), String.class, ajaxCallback);
        return ajaxCallback;
    }

    public <T> void queryUrl(String url, Class<T> tClass, final AjaxCallback<T> ajaxCallback) {
        query.ajax(url, tClass, new AjaxCallback<T>() {
            @Override
            public void callback(String url, T object, AjaxStatus status) {
                ajaxCallback.callback(url, object, status);
            }
        });
    }

    public void addMessageListener(String msgId, CoreEngineMsgCallback callback) {
        mEngine.receiveMsg(msgId, callback);
    }

    public void registMsgSource(MessageEngine.MessageCallback messageCallback) {
        mEngine.registMsgSrc(messageCallback);
    }

    public void unRegistMsgSource(MessageEngine.MessageCallback messageCallback) {
        mEngine.unRegistMessageSource(messageCallback);
    }

    public void unRegistPubMsg(MessageType messageType, MessageEngine.MessageCallback messageCallback) {
        mEngine.unRegistPubMessage(messageType, messageCallback);
    }

    public ConcurrentHashMap<String, MessageEngine.MessageCallback> getSourceMap() {
        return mEngine.getMessageEngine().getSourceMap();
    }

    public void delMessageListener(String msgId) {
        mEngine.removeMsg(msgId);
    }

    public void sendMessage(String msgId, Bundle bundle) {
        mEngine.getMessageEngine().sendMsg(msgId, bundle);
    }

    public void sendMsgToTarget(int msgType, Bundle body, Class target) {
        mEngine.getMessageEngine().sendMsgToTaget(msgType, body, target);
    }

    public void sendMsgToTargetForCallback(
            int msgType, Bundle body, Class target, NormalCallback callback) {
        mEngine.getMessageEngine().sendMsgToTagetForCallback(msgType, body, target, callback);
    }

    public void appFinish() {
        for (Activity activity : runTask.values()) {
            activity.finish();
        }
    }

    public void exit() {
        notifyMap.clear();
        runTask.clear();
        stopService(DownLoadService.getIntent(this));
        System.exit(0);
    }

    private void init() {
        Log.i(null, "init");
        app = this;
        gson = new Gson();
        apiVersion = "2.0.1";
        query = new AQuery(this);
        host = getString(R.string.app_host);

        notifyMap = new HashMap<String, Bundle>();

        initImageLoaderConfig();
        initWorkSpace();
        loadConfig();

        mEngine = CoreEngine.create(this);
        installPlugin();

        registDevice();
        startMainService();
    }

    private void initImageLoaderConfig() {
        ImageLoaderConfiguration mConfig =
                new ImageLoaderConfiguration.Builder(this).diskCache(new UnlimitedDiscCache(AQUtility.getCacheDir(this))).build();
        ImageLoader.getInstance().init(mConfig);
    }

    public void startMainService() {
        app.mEngine.runService(EdusohoMainService.TAG, this, null);
    }

    public Map<String, String> getPlatformInfo() {
        Map<String, String> params = new HashMap<String, String>();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        params.put("deviceSn", telephonyManager.getDeviceId());
        params.put("platform", "Android " + Build.MODEL);
        params.put("version", Build.VERSION.SDK);
        params.put("screenresolution", displayMetrics.widthPixels + "x" + displayMetrics.heightPixels);
        params.put("kernel", Build.VERSION.RELEASE);
        params.put("edusohoVersion", apiVersion);

        return params;
    }

    public void logToServer(
            String url, Map<String, String> params, AjaxCallback<String> ajaxCallback) {
        if (ajaxCallback == null) {
            ajaxCallback = new AjaxCallback<String>();
        }
        ajaxCallback.method(AQuery.METHOD_POST);
        app.query.ajax(url, params, String.class, ajaxCallback);
    }

    public void registDevice() {
        Log.d(null, "registDevice->");
        AppConfig config = app.config;
        Log.d(null, "isPublicRegistDevice->" + config.isPublicRegistDevice);
        Log.d(null, "isRegistDevice->" + config.isRegistDevice);
        if (config.isPublicRegistDevice && config.isRegistDevice) {
            return;
        }

        Map<String, String> params = getPlatformInfo();

        if (!config.isPublicRegistDevice) {
            logToServer(Const.MOBILE_REGIST, params, new AjaxCallback<String>() {
                @Override
                public void callback(String url, String object, AjaxStatus status) {
                    super.callback(url, object, status);
                    Log.d(null, "regist device to public->" + object);
                    try {
                        Boolean result = app.gson.fromJson(
                                object, new TypeToken<Boolean>() {
                        }.getType());

                        if (true == result) {
                            app.config.isPublicRegistDevice = true;
                            app.saveConfig();
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }

        if (!config.isRegistDevice) {
            logToServer(app.schoolHost + Const.REGIST_DEVICE, params, new AjaxCallback<String>() {
                @Override
                public void callback(String url, String object, AjaxStatus status) {
                    super.callback(url, object, status);
                    Log.d(null, "regist device to school->" + object);
                    try {
                        Boolean result = app.gson.fromJson(
                                object, new TypeToken<Boolean>() {
                        }.getType());

                        if (true == result) {
                            app.config.isRegistDevice = true;
                            app.saveConfig();
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }
    }

    public boolean getNetStatus() {
        ConnectivityManager connManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    public String getPluginFile(String pluginName) {
        File file = mEngine.getPluginFile(pluginName);
        return file.getAbsolutePath();
    }

    public void installApk(String file) {
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

    private void installPlugin() {
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

    public String getApkVersion() {
        String version = "0.0.0";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (Exception e) {
            version = getResources().getString(R.string.apk_version);
        }
        return version;
    }

    public void setCurrentSchool(School school) {
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

    private void loadDefaultSchool() {
        SharedPreferences sp = getSharedPreferences("defaultSchool", MODE_PRIVATE);
        Map<String, String> map = (Map<String, String>) sp.getAll();
        if (!map.isEmpty()) {
            School item = new School();
            item.name = map.get("name");
            item.url = map.get("url");
            item.host = map.get("host");
            item.logo = map.get("logo");
            host = item.host;
            item.url = checkSchoolUrl(item.url);
            setCurrentSchool(item);
        }
    }

    private String checkSchoolUrl(String url) {
        if (url.endsWith("mapi_v1")) {
            String newUrl = url.substring(0, url.length() - 1);
            return newUrl + "2";
        }
        return url;
    }

    private void loadConfig() {
        SharedPreferences sp = getSharedPreferences("config", MODE_APPEND);
        config = new AppConfig();
        config.showSplash = sp.getBoolean("showSplash", true);
        config.isRegistDevice = sp.getBoolean("registDevice", false);
        config.isPublicRegistDevice = sp.getBoolean("registPublicDevice", false);
        config.startWithSchool = sp.getBoolean("startWithSchool", true);
        if (config.startWithSchool) {
            loadDefaultSchool();
        }

        loadToken();
    }

    private void loadToken() {
        SharedPreferences sp = getSharedPreferences("token", MODE_APPEND);
        token = sp.getString("token", "");
    }

    public void saveToken(TokenResult result) {
        SharedPreferences sp = getSharedPreferences("token", MODE_APPEND);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("token", result.token);
        edit.commit();

        token = result.token == null || "".equals(result.token) ? "" : result.token;
        loginUser = "".equals(token) ? null : result.user;
    }

    public void removeToken() {
        SharedPreferences sp = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("token", "");
        edit.commit();

        token = null;
        loginUser = null;

        EdusohoMainService mService = getService();
        if (mService == null) {
            return;
        }
        mService.sendMessage(EdusohoMainService.EXIT_USER, null);
        Log.d(null, "remove->token user->" + loginUser);
    }


    public boolean taskIsRun(String name) {
        Activity activity = runTask.get(name);
        return activity != null;
    }

    public void removeTask(String name) {
        runTask.remove(name);
    }

    public void addTask(String name, Activity activity) {
        runTask.put(name, activity);
    }

    public void saveConfig() {
        SharedPreferences sp = getSharedPreferences("config", MODE_APPEND);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("showSplash", config.showSplash);
        edit.putBoolean("registDevice", config.isRegistDevice);
        edit.putBoolean("registPublicDevice", config.isPublicRegistDevice);
        edit.putBoolean("startWithSchool", config.startWithSchool);
        edit.commit();
    }

    public void query(String url, final ResultCallback callback, Activity mActivity) {
        if (!getNetStatus()) {
            PopupDialog.createNormal(
                    mActivity, "提示信息", "无网络,请检查网络和手机设置!").show();
            mActivity.finish();
            return;
        }

        query.ajax(url, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                super.callback(url, object, status);
                callback.callback(url, object, status);
            }
        });
    }

    private void initWorkSpace() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File sdcard = Environment.getExternalStorageDirectory();
            File workSpace = new File(sdcard, "edusoho");
            if (!workSpace.exists()) {
                workSpace.mkdir();
            }
            AQUtility.setCacheDir(new File(workSpace, "cache"));
        } else {
            Toast.makeText(this, "设备没有内存卡,数据将保存在手机内存中！", Toast.LENGTH_LONG).show();
        }
        loadCustomBtnStyle();
        runTask = new HashMap<String, Activity>();
    }

    public void setDisplay(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        screenH = display.getHeight();
        screenW = display.getWidth();
    }

    public String bindToken2Url(String url, boolean addToken) {
        StringBuffer sb = new StringBuffer(app.schoolHost);
        sb.append(url);
        if (addToken) {
            sb.append("&token=").append(token);
        }
        return sb.toString();
    }

    public HashMap<String, String> initParams(String[] arges) {
        HashMap<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < arges.length; i = i + 2) {
            params.put(arges[i], arges[i + 1]);
        }

        return params;
    }

    public HashMap<String, String> createParams(
            boolean addToken, RequestParamsCallback callback) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (callback != null) {
            callback.addParams(params);
        }

        if (addToken) {
            params.put("token", token);
        }
        return params;
    }

    public RequestUrl bindUrl(String url, boolean addToken) {
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

    public void checkToken() {
        synchronized (this) {
            if (loginUser != null) {
                return;
            }
            String url = bindToken2Url(Const.CHECKTOKEN, true);
            query.ajax(url, String.class, new AjaxCallback<String>() {
                @Override
                public void callback(String url, String object, AjaxStatus status) {
                    TokenResult result = app.gson.fromJson(
                            object, new TypeToken<TokenResult>() {
                    }.getType());
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

    private void loadCustomBtnStyle() {
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

    public void startUpdateWebView(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    private boolean mIsNotifyUpdate;

    public void updateApp(String url, boolean isShowLoading, final NormalCallback callback) {
        if (mIsNotifyUpdate) {
            return;
        }
        mIsNotifyUpdate = true;

        final LoadDialog loadDialog = LoadDialog.create(this);
        if (isShowLoading) {
            loadDialog.setMessage("正在检查版本更新");
            loadDialog.show();
        }

        query.ajax(url, String.class,
                new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String object, AjaxStatus status) {
                        loadDialog.dismiss();
                        super.callback(url, object, status);
                        final AppUpdateInfo appUpdateInfo = app.gson.fromJson(
                                object, new TypeToken<AppUpdateInfo>() {
                        }.getType());

                        if (appUpdateInfo == null || appUpdateInfo.androidVersion == null) {
                            return;
                        }

                        callback.success(appUpdateInfo);
                    }
                });
    }

    public void addNotify(String type, Bundle bundle) {
        notifyMap.put(type, bundle);
    }

    public Bundle getNotify(String type) {
        return notifyMap.get(type);
    }

    public Set<String> getNotifys() {
        return notifyMap.keySet();
    }

    public void removeNotify(String type) {
        notifyMap.remove(type);
    }
}
