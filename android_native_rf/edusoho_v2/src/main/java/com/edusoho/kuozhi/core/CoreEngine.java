package com.edusoho.kuozhi.core;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.Window;


import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.CoreEngineMsgCallback;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.core.model.PluginModel;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by howzhi on 14-6-23.
 */
public class CoreEngine {

    private Context mContext;
    private static final String PLUGIN = "plugin";
    private static final String INSTALL = "install";
    private static CoreEngine engine;
    public AppCache appCache;
    private MessageEngine messageEngine;


    private ConcurrentHashMap<String, ArrayList<CoreEngineMsgCallback>> mMessageMap;
    private HashMap<String, PluginModel> mPluginModelHashMap;

    private CoreEngine(Context context)
    {
        mContext = context;
        init();
    }

    public void receiveMsg(String msgId, CoreEngineMsgCallback callback)
    {
        ArrayList<CoreEngineMsgCallback> callbackList = mMessageMap.get(msgId);
        if (callbackList == null) {
            callbackList = new ArrayList<CoreEngineMsgCallback>();
        }
        callbackList.add(callback);
        mMessageMap.put(msgId, callbackList);
    }

    public MessageEngine getMessageEngine()
    {
        return messageEngine;
    }

    public void removeMsg(String msgId)
    {
        mMessageMap.remove(msgId);
    }

    public void sendMsg(String msgId, MessageModel messageModel)
    {
        ArrayList<CoreEngineMsgCallback> callbackList = mMessageMap.get(msgId);
        if (callbackList != null) {
            for (CoreEngineMsgCallback callback : callbackList) {
                callback.invoke(messageModel);
            }
        }
    }

    public static CoreEngine create(Context context)
    {
        synchronized (CoreEngine.class) {
            if (engine == null) {
                engine = new CoreEngine(context);
            }
        }
        return engine;
    }

    public void runNormalPluginForResult(
            String pluginName, Activity serverActivity, int requestCode, PluginRunCallback callback)
    {
        PluginModel pluginModel = mPluginModelHashMap.get(pluginName);
        if (pluginModel != null) {
            Intent startIntent = new Intent();
            startIntent.setClassName(serverActivity, pluginModel.packAge);
            if (callback != null) {
                callback.setIntentDate(startIntent);
            }

            serverActivity.startActivityForResult(startIntent, requestCode);
        }
    }

    public void runService(
            String serviceName, Context serverActivity, PluginRunCallback callback)
    {
        Log.d(null, "name->" + serviceName + mPluginModelHashMap);
        PluginModel pluginModel = mPluginModelHashMap.get(serviceName);
        if (pluginModel != null) {
            Intent startIntent = new Intent();
            startIntent.setClassName(serverActivity, pluginModel.packAge);
            if (callback != null) {
                callback.setIntentDate(startIntent);
            }

            serverActivity.startService(startIntent);
        }
    }

    public BaseFragment runPluginWithFragmentByBundle(
            String pluginName, Activity activity, Bundle bundle)
    {
        BaseFragment fragment = null;
        PluginModel pluginModel = mPluginModelHashMap.get(pluginName);
        if (pluginModel != null) {
            fragment = (BaseFragment) Fragment.instantiate(activity, pluginModel.packAge);
            fragment.setArguments(bundle);

            return fragment;
        }
        return null;
    }

    public Fragment runPluginWithFragment(
            String pluginName, Activity activity, PluginFragmentCallback callback)
    {
        Fragment fragment = null;
        PluginModel pluginModel = mPluginModelHashMap.get(pluginName);
        if (pluginModel != null) {
            fragment = Fragment.instantiate(activity, pluginModel.packAge);
            if (callback != null) {
                Bundle bundle = new Bundle();
                fragment.setArguments(bundle);
                callback.setArguments(bundle);
            }

            return fragment;
        }
        return null;
    }

    public View runNormalPluginInGroup(
            String pluginName, ActivityGroup serverActivity, PluginRunCallback callback
    )
    {
        PluginModel pluginModel = mPluginModelHashMap.get(pluginName);
        if (pluginModel != null) {
            Intent startIntent = new Intent();
            startIntent.setClassName(serverActivity, pluginModel.packAge);
            if (callback != null) {
                callback.setIntentDate(startIntent);
            }
            Window window = serverActivity.getLocalActivityManager()
                    .startActivity(pluginName, startIntent);
            return window.getDecorView();
        }
        return new View(serverActivity);
    }

    public void runNormalPlugin(
            String pluginName, Activity serverActivity, PluginRunCallback callback)
    {
        PluginModel pluginModel = mPluginModelHashMap.get(pluginName);
        if (pluginModel != null) {
            Intent startIntent = new Intent();
            startIntent.setClassName(serverActivity, pluginModel.packAge);
            if (callback != null) {
                callback.setIntentDate(startIntent);
            }

            serverActivity.startActivity(startIntent);
        }
    }

    public void runNormalPluginWithBundle(
            String pluginName, Activity serverActivity, Bundle bundle)
    {
        PluginModel pluginModel = mPluginModelHashMap.get(pluginName);
        if (pluginModel != null) {
            Intent startIntent = new Intent();
            startIntent.setClassName(serverActivity, pluginModel.packAge);
            if (bundle != null) {
                startIntent.putExtras(bundle);
            }

            serverActivity.startActivity(startIntent);
        }
    }

    public File getPluginFile(String pluginName)
    {
        return new File(mContext.getFilesDir(), pluginName);
    }

    public void runApkPlugin(String pluginName, Activity proxyActivity)
    {
        File pluginDir = new File(mContext.getFilesDir(), PLUGIN);
        File pluginFile = new File(pluginDir, pluginName + ".apk");
        if (!pluginFile.exists()) {
            return;
        }
        DexClassLoader dexClassLoader = new DexClassLoader(
                pluginFile.getAbsolutePath(), pluginDir.getAbsolutePath(), null, ClassLoader.getSystemClassLoader());
        PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(
                pluginFile.getAbsolutePath(), 1);
        ActivityInfo[] activities = packageInfo.activities;
        if (activities == null || activities.length <= 0) {
            //no find
            return;
        }

        try {
            Class pluginClass = dexClassLoader.loadClass(activities[0].name);
            Constructor pluginConstructor = pluginClass.getConstructor(new Class[]{});
            Object pluginInstance = pluginConstructor.newInstance(new Object[]{});

            invokeMethod(
                    pluginInstance, pluginClass, "setActivity", new Class[]{ Activity.class }, new Object[]{ proxyActivity });

            Bundle bundle = new Bundle();
            bundle.putBoolean("isProxy", true);
            invokeMethod(
                    pluginInstance, pluginClass, "onCreate", new Class[]{ Bundle.class }, new Object[]{ bundle });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void invokeMethod(
            Object instance, Class pluginClass, String methodName, Class[] methodParams, Object[] params)
            throws Exception
    {
        Method method = pluginClass.getDeclaredMethod(methodName, methodParams);
        method.setAccessible(true);
        method.invoke(instance, params);
    }

    public void registMsgSrc(MessageEngine.MessageCallback source)
    {
        messageEngine.registMessageSource(source);
    }

    public void unRegistMessageSource(MessageEngine.MessageCallback source)
    {
        messageEngine.unRegistMessageSource(source);
    }

    private void init()
    {
        appCache = AppDbCache.getInstance(mContext);
        messageEngine = MessageEngine.init();
        mMessageMap = new ConcurrentHashMap<String, ArrayList<CoreEngineMsgCallback>>();
        initPluginFromXml();

        try{
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 1);
            ActivityInfo[] activities = packageInfo.activities;
            Resources resources = packageManager.getResourcesForActivity(
                    new ComponentName(mContext.getPackageName(), activities[0].name));
            XmlResourceParser xmlResourceParser = resources.getXml(R.xml.plugins);
            HashMap<String, PluginModel> otherPluginMap = parsePluginXml(xmlResourceParser);
            mPluginModelHashMap.putAll(otherPluginMap);
        } catch (Exception e) {
            Log.i(null, "no app plugin");
        }
    }

    public void installApkPlugin()
    {
        try {
            copyPluginFromAsset(getAssetPlugins(PLUGIN));
            copyInstallApkFromAsset(getAssetPlugins(INSTALL));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, PluginModel> parsePluginXml(XmlResourceParser parser)
    {
        PluginModel pluginModel = null;
        HashMap<String, PluginModel> pluginModels = null;
        try {
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_DOCUMENT:
                        pluginModels = new HashMap<String, PluginModel>();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("plugin".equals(parser.getName())) {
                            String name = parser.getAttributeValue(null, "name");
                            String version = parser.getAttributeValue(null, "version");
                            String packAge = parser.getAttributeValue(null, "package");
                            pluginModel = new PluginModel();
                            pluginModel.packAge = packAge;
                            pluginModel.version = version;
                            pluginModel.name = name;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("plugin".equals(parser.getName())) {
                            pluginModels.put(pluginModel.name, pluginModel);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pluginModels;
    }

    private void initPluginFromXml()
    {
        XmlResourceParser parser = mContext.getResources().getXml(R.xml.core_plugins);
        System.out.println("paset->" + parser);
        mPluginModelHashMap = parsePluginXml(parser);
        System.out.println("mPluginModelHashMap->" + mPluginModelHashMap);
    }

    public String[] getAssetPlugins(String dirName) throws IOException
    {
        AssetManager assetManager = mContext.getAssets();
        return assetManager.list(dirName);
    }

    public void copyPluginFromAsset(String[] dirPath) throws Exception
    {
        AssetManager assetManager = mContext.getAssets();
        File pluginDir = getPluginDir();
        for (String path : dirPath) {
            OutputStream target = new FileOutputStream(new File(pluginDir, path));
            copyFile(assetManager.open(PLUGIN + "/" +path), target);
        }
    }

    private void copyInstallApkFromAsset(String[] dirPath) throws Exception
    {
        AssetManager assetManager = mContext.getAssets();
        for (String path : dirPath) {
            OutputStream target = mContext.openFileOutput(path, mContext.MODE_WORLD_READABLE);
            copyFile(assetManager.open(INSTALL + "/" +path), target);
        }
    }

    private File getPluginDir()
    {
        File pluginDir = new File(mContext.getFilesDir(), PLUGIN);
        if (!pluginDir.exists()) {
            pluginDir.mkdir();
        }
        return pluginDir;
    }

    private void copyFile(InputStream src, OutputStream target)
    {
        int len = -1;
        byte[] buffer = new byte[1024];
        try {
            while ( (len = src.read(buffer)) != -1) {
                target.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
