package com.edusoho.kuozhi.v3.util.appplugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.morgoo.droidplugin.pm.PluginManager;
import java.io.File;

/**
 * Created by howzhi on 15/10/14.
 */
public class PluginUtil {

    public static void start(String pluginName, Context context, Intent intent) {

        try {
            File pluginDir = new File(context.getFilesDir(), "plugin");
            if (!PluginManager.getInstance().isPluginPackage(pluginName)) {
                PluginManager.getInstance().installPackage(new File(pluginDir, pluginName + ".apk").getAbsolutePath(), 0);
            }

            Intent startIntent = context.getPackageManager().getLaunchIntentForPackage(pluginName);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent != null) {
                startIntent.putExtras(intent);
            }
            context.startActivity(startIntent);
        } catch (Exception e) {

        }
    }

}
