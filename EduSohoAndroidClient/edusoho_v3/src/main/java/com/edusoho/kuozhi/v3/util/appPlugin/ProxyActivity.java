package com.edusoho.kuozhi.v3.util.appPlugin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.io.File;

import androidx.pluginmgr.PlugInfo;
import androidx.pluginmgr.PluginManager;

/**
 * Created by howzhi on 15/10/13.
 */
public class ProxyActivity extends Activity {


    public static void start(String pluginName, Context context, Intent intent) {
        PluginManager pluginManager = PluginManager.getInstance(context);

        try {
            File pluginDir = new File(context.getFilesDir(), "plugin");
            PlugInfo plug = pluginManager.loadPluginWithId(
                    new File(pluginDir, pluginName + ".apk"), null);
            intent.setComponent(new ComponentName(plug.getPackageName(), plug.getMainActivity().activityInfo.name));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pluginManager.startActivity(context, intent);
        } catch (Exception e) {
        }
    }
}
