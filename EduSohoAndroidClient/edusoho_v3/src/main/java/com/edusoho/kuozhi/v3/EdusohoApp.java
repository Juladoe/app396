package com.edusoho.kuozhi.v3;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.CoreEngineMsgCallback;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.RequestParamsCallback;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.result.CloudResult;
import com.edusoho.kuozhi.v3.model.result.UserResult;
import com.edusoho.kuozhi.v3.model.sys.AppConfig;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.Token;
import com.edusoho.kuozhi.v3.service.DownLoadService;
import com.edusoho.kuozhi.v3.service.EdusohoMainService;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.MultipartRequest;
import com.edusoho.kuozhi.v3.util.VolleySingleton;
import com.edusoho.kuozhi.v3.util.server.CacheServer;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.kuozhi.v3.util.volley.StringVolleyRequest;
import com.edusoho.kuozhi.v3.view.webview.ESCordovaWebViewFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EdusohoApp extends Application {

    public AppConfig config;
    public String host;
    public String domain;
    public Gson gson;
    public SqliteUtil sqliteUtil;
    public School defaultSchool;
    public User loginUser;
    public String apiVersion;
    public String schoolHost = "";
    public CoreEngine mEngine;

    public Activity mActivity;
    public Context mContext;

    public String token;
    /**
     * school token
     */
    public String apiToken;

    private HashMap<String, Bundle> notifyMap;
    public static HashMap<String, Activity> runTask;
    private static final String TAG = "EdusohoApp";

    public static int screenW;
    public static int screenH;

    public static EdusohoApp app;
    public static boolean debug = true;
    public static final String PLUGIN_CONFIG = "plugin_config";
    public static final String INSTALL_PLUGIN = "install_plugin";

    private ImageLoaderConfiguration mImageLoaderConfiguration;
    public DisplayImageOptions mOptions;
    public VolleySingleton mVolley;

    //cache 缓存服务器
    private CacheServer mResouceCacheServer;
    private CacheServer mPlayCacheServer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "create application");
        //EduSohoUncaughtExceptionHandler.initCaughtHandler(this);
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

    /**
     * @param requestUrl
     * @param responseListener
     * @param errorListener
     * @return
     */
    public Request<String> postMultiUrl(final RequestUrl requestUrl, Response.Listener<String> responseListener, Response.ErrorListener errorListener, int method) {
        mVolley.getRequestQueue();
        MultipartRequest multipartRequest = new MultipartRequest(method, requestUrl, responseListener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return requestUrl.getHeads();
            }
        };
        multipartRequest.setTag(requestUrl.url);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(Const.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return mVolley.addToRequestQueue(multipartRequest);
    }

    public Request<String> postUrl(final RequestUrl requestUrl, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        mVolley.getRequestQueue();
        StringVolleyRequest request = new StringVolleyRequest(Request.Method.POST, requestUrl, responseListener, errorListener);
        request.setTag(requestUrl.url);
        return mVolley.addToRequestQueue(request);
    }


    /**
     * volley get 请求
     *
     * @param requestUrl       url、参数、header等信息
     * @param responseListener 返回response信息
     * @param errorListener    错误信息
     */
    public void getUrl(final RequestUrl requestUrl, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        mVolley.getRequestQueue();
        StringVolleyRequest request = new StringVolleyRequest(Request.Method.GET, requestUrl, responseListener, errorListener);
        request.setTag(requestUrl.url);
        mVolley.addToRequestQueue(request);
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

    public void sendMsgToTarget(int msgType, Bundle body, Object target) {
        mEngine.getMessageEngine().sendMsgToTaget(msgType, body, target);
    }

    public void sendMsgToTargetForCallback(
            int msgType, Bundle body, Class target, NormalCallback callback) {
        mEngine.getMessageEngine().sendMsgToTagetForCallback(msgType, body, target, callback);
    }

    public void exit() {
        stopService(DownLoadService.getIntent(this));
        notifyMap.clear();
        if (mResouceCacheServer != null) {
            mResouceCacheServer.close();
        }

        if (mPlayCacheServer != null) {
            mPlayCacheServer.close();
        }

        M3U8DownService m3U8DownService = M3U8DownService.getService();
        if (m3U8DownService != null) {
            m3U8DownService.cancelAllDownloadTask();
        }

        SqliteUtil.getUtil(this).close();
        ESCordovaWebViewFactory.getFactory().destory();
    }

    private void init() {
        app = this;
        gson = new Gson();
        mVolley = VolleySingleton.getInstance(getApplicationContext());
        apiVersion = getString(R.string.api_version);
        setHost(getString(R.string.app_host));
        notifyMap = new HashMap<>();
        initApp();
    }

    private String getDomain() {
        Uri hostUri = Uri.parse(app.host);
        if (hostUri != null) {
            return hostUri.getHost();
        }
        return "";
    }

    public void initApp() {
        runTask = new HashMap<>();
        File workFile = initWorkSpace();
        initImageLoaderConfig(workFile);
        loadConfig();

        mEngine = CoreEngine.create(this);
        startMainService();
    }

    protected void initImageLoaderConfig(File file) {
        if (file == null || !file.exists()) {
            file = new File(getCacheDir(), getResources().getString(R.string.image_cache_path));
        } else {
            file = new File(file, getResources().getString(R.string.image_cache_path));
        }
        mImageLoaderConfiguration = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCacheExtraOptions((int) (screenW * 0.8f), (int) (screenH * 0.8f))
                .diskCache(new UnlimitedDiscCache(file)).imageDownloader(new BaseImageDownloader(this, Const.TIMEOUT, Const.TIMEOUT))
                .build();
        ImageLoader.getInstance().init(mImageLoaderConfiguration);
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).showImageForEmptyUri(R.drawable.defaultpic).
                showImageOnFail(R.drawable.defaultpic).build();
    }

    public void startMainService() {
        app.mEngine.runService(EdusohoMainService.TAG, this, null);
    }

    public HashMap<String, String> getPlatformInfo() {
        HashMap<String, String> params = new HashMap<String, String>();
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

    public void registDevice(final NormalCallback normalCallback) {
        HashMap<String, String> params = getPlatformInfo();
        RequestUrl requestUrl = new RequestUrl(app.schoolHost + Const.REGIST_DEVICE);
        requestUrl.setParams(params);
        app.postUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(null, "regist device to school");
                try {
                    Boolean result = app.gson.fromJson(
                            response, new TypeToken<Boolean>() {
                            }.getType()
                    );

                    if (result) {
                        app.saveConfig();
                    }

                    normalCallback.success(null);
                } catch (Exception e) {
                    Log.e(null, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(null, "regist failed");
            }
        });
    }

    public boolean getNetIsConnect() {
        ConnectivityManager connManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    public boolean getNetIsWiFi() {
        ConnectivityManager connManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
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
        intent.setAction(Intent.ACTION_VIEW);

        intent.setDataAndType(Uri.parse("file://" + file),
                "application/vnd.android.package-archive");
        this.startActivity(intent);
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
        setHost(school.host);
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
            setHost(item.host);
            item.url = checkSchoolUrl(item.url);
            setCurrentSchool(item);
        }
    }

    private void setHost(String host) {
        this.host = host;
        this.domain = getDomain();
    }

    private String checkSchoolUrl(String url) {
        if (url.endsWith("mapi_v1")) {
            String newUrl = url.substring(0, url.length() - 1);
            return newUrl + "2";
        }
        return url;
    }

    public void SaveUser2Local(User user) {
        SharedPreferences sp = getSharedPreferences("token", MODE_APPEND);
        SharedPreferences.Editor edit = sp.edit();
        if (user != null) {
            edit.putString("userInfo", gson.toJson(user));
        } else {
            edit.putString("userInfo", "");
        }

        edit.apply();
    }

    public User loadUserInfo() {
        SharedPreferences sp = getSharedPreferences("token", MODE_APPEND);
        String strUser = sp.getString("userInfo", "");
        User user = null;
        if (!TextUtils.isEmpty(strUser)) {
            user = parseJsonValue(AppUtil.encode2(strUser), new TypeToken<User>() {
            });
        }
        return user;
    }

    private void loadToken() {
        SharedPreferences sp = getSharedPreferences("token", MODE_APPEND);
        token = sp.getString("token", "");
        apiToken = sp.getString("apiToken", "");
    }

    public void saveApiToken(String apiToken) {
        SharedPreferences sp = getSharedPreferences("token", MODE_APPEND);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("apiToken", apiToken);
        edit.apply();
        this.apiToken = apiToken;
    }

    public void saveToken(UserResult userResult) {
        SharedPreferences sp = getSharedPreferences("token", MODE_APPEND);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("token", userResult.token);
        edit.putString("userInfo", AppUtil.encode2(gson.toJson(userResult.user)));
        edit.apply();

        token = userResult.token == null || "".equals(userResult.token) ? "" : userResult.token;
        if (TextUtils.isEmpty(token)) {
            loginUser = null;
        } else {
            loginUser = userResult.user;
            SqliteUtil.saveUser(loginUser);
        }
    }

    public void removeToken() {
        SharedPreferences sp = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("token", "");
        edit.putString("userInfo", "");
        edit.apply();

        SqliteUtil.clearUser(loginUser == null ? 0 : loginUser.id);
        token = null;
        loginUser = null;

        EdusohoMainService mService = getService();
        if (mService == null) {
            return;
        }
        mService.sendMessage(EdusohoMainService.EXIT_USER, null);
        Log.d(null, "remove->token data->" + loginUser);
    }

    public boolean taskIsRun(String name) {
        Activity activity = runTask.get(name);
        return activity != null;
    }

    public void addTask(String name, Activity activity) {
        Activity oldActivity = runTask.get(name);
        runTask.put(name, activity);
        if (oldActivity != null) {
            Log.d(null, "remove activity->" + name);
            oldActivity.finish();
            runTask.remove(name);
        }
    }

    private void loadConfig() {
        SharedPreferences sp = getSharedPreferences("config", MODE_APPEND);
        config = new AppConfig();
        config.showSplash = sp.getBoolean("showSplash", true);
        config.isPublicRegistDevice = sp.getBoolean("registPublicDevice", false);
        config.startWithSchool = sp.getBoolean("startWithSchool", true);
        config.offlineType = sp.getInt("offlineType", 0);
        config.msgSound = sp.getInt("msgSound", 0);
        config.msgVibrate = sp.getInt("msgVibrate", 0);
        if (config.startWithSchool) {
            loadDefaultSchool();
        }

        loadToken();
    }

    public void saveConfig() {
        SharedPreferences sp = getSharedPreferences("config", MODE_APPEND);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("showSplash", config.showSplash);
        edit.putBoolean("registPublicDevice", config.isPublicRegistDevice);
        edit.putBoolean("startWithSchool", config.startWithSchool);
        edit.putInt("offlineType", config.offlineType);
        edit.putInt("msgSound", config.msgSound);
        edit.putInt("msgVibrate", config.msgVibrate);
        edit.apply();
    }

    private File initWorkSpace() {
        File workSpace = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            workSpace = new File(Environment.getExternalStorageDirectory(), "edusoho");
            if (!workSpace.exists()) {
                workSpace.mkdir();
            }
        } else {
            CommonUtil.longToast(getApplicationContext(), "设备没有内存卡,数据将保存在手机内存中！");
        }
        return workSpace;
    }

    public static File getWorkSpace() {
        File file = new File(Environment.getExternalStorageDirectory() + "/edusoho");
        return file != null ? file : null;
    }

    public static File getChatCacheFile() {
        return app.getExternalCacheDir();
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

    public RequestUrl bindNewUrl(String url, boolean addToken) {
        StringBuffer sb = new StringBuffer(app.host);
        sb.append(url);
        RequestUrl requestUrl = new RequestUrl(sb.toString());

        if (addToken) {
            requestUrl.heads.put("Auth-Token", token);
        }
        return requestUrl;
    }

    public RequestUrl bindPushUrl(String url) {
        StringBuffer sb = new StringBuffer(Const.PUSH_HOST);
        sb.append(url);
        RequestUrl requestUrl = new RequestUrl(sb.toString());
        requestUrl.heads.put("Auth-Token", app.apiToken);
        return requestUrl;
    }

    private void bindPushUrl(String url, final NormalCallback<RequestUrl> normalCallback) {
        final StringBuffer sb = new StringBuffer(Const.PUSH_HOST);
        sb.append(url);
        if (TextUtils.isEmpty(app.apiToken)) {
            final RequestUrl requestUrl = app.bindNewUrl(Const.GET_API_TOKEN, false);
            app.getUrl(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Token token = parseJsonValue(response, new TypeToken<Token>() {
                    });
                    if (token != null) {
                        RequestUrl requestUrl = new RequestUrl(sb.toString());
                        requestUrl.heads.put("Auth-Token", app.apiToken);
                        app.saveApiToken(token.token);
                        normalCallback.success(requestUrl);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "无法获取网校Token");
                }
            });
        } else {
            RequestUrl requestUrl = new RequestUrl(sb.toString());
            requestUrl.heads.put("Auth-Token", app.apiToken);
            normalCallback.success(requestUrl);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
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

    public CacheServer getResouceCacheServer(ActionBarBaseActivity activity) {
        if (mResouceCacheServer == null) {
            mResouceCacheServer = new CacheServer(activity, Const.WEB_RES_PROT);
        }

        return mResouceCacheServer;
    }

    /**
     * 启动播放器缓存server
     *
     * @param activity
     * @return
     */
    public CacheServer startPlayCacheServer(ActionBarBaseActivity activity) {
        if (mPlayCacheServer == null) {
            mPlayCacheServer = new CacheServer(activity, Const.CACHE_PROT);
            mPlayCacheServer.start();
        }

        return mPlayCacheServer;
    }

    /**
     * 注册到xg、教育云
     *
     * @param bundle
     */
    public void pushRegister(final Bundle bundle) {
        XGPushConfig.enableDebug(this, true);
        XGPushManager.registerPush(mContext, new XGIOperateCallback() {
            @Override
            public void onSuccess(final Object data, int flag) {
                Log.w(Constants.LogTag, "+++ register push success. token:" + data);
                NormalCallback<RequestUrl> normalCallback = new NormalCallback<RequestUrl>() {
                    @Override
                    public void success(RequestUrl requestUrl) {
                        HashMap<String, String> params = requestUrl.getParams();
                        params.put("appToken", data.toString());
                        if (bundle != null) {
                            params.put("studentId", bundle.getString(Const.BIND_USER_ID));
                        }
                        params.put("euqip", Const.EQUIP_TYPE);
                        app.postUrl(requestUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject resultObject = new JSONObject(response);
                                    if (bundle != null) {
                                        getService().getOfflineMsgs();
                                    }
                                    String result = resultObject.getString("result");
                                    if (result.equals("success")) {
                                        Log.d(TAG, "cloud register success");
                                    }
                                } catch (JSONException e) {
                                    Log.d(TAG, "cloud register failed");
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, error.toString());
                            }
                        });
                    }
                };
                app.bindPushUrl(bundle == null ? Const.ANONYMOUS_BIND : Const.BIND, normalCallback);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.w(Constants.LogTag, "+++ register push fail. token:" + data + ", errCode:" + errCode + ",msg:" + msg);
            }
        });
    }

    /**
     * 注销到xg、教育云
     *
     * @param bundle
     */
    public void pushUnregister(final Bundle bundle) {
        XGPushManager.unregisterPush(mContext, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int i) {
                RequestUrl requestUrl = bindPushUrl(Const.UNBIND);
                HashMap<String, String> hashMap = requestUrl.getParams();
                hashMap.put("studentId", bundle.getString(Const.BIND_USER_ID));
                postUrl(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CloudResult pushResult = app.parseJsonValue(response, new TypeToken<CloudResult>() {
                        });
                        if (pushResult != null && pushResult.result.equals("success")) {
                            Log.d(TAG, "cloud logout success");
                        } else {
                            Log.d(TAG, "cloud logout failed");
                        }
                    }
                }, null);
            }

            @Override
            public void onFail(Object data, int i, String s) {
                Log.w(Constants.LogTag, "+++ unregister push fail. token:" + data + ", errCode:" + i + ",msg:" + s);
            }
        });
    }


    public <T> T parseJsonValue(String json, TypeToken<T> typeToken) {
        T value;
        try {
            value = gson.fromJson(
                    json, typeToken.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return value;
    }

    /**
     * 判断是否为当前Activity
     *
     * @param activityName ActivityName
     * @return
     */
    public boolean isForeground(String activityName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);
        ComponentName componentInfo = task.get(0).topActivity;
        if (componentInfo.getClassName().equals(activityName)) {
            return true;
        }
        return false;
    }
}
